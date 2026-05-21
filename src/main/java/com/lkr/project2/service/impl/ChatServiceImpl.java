package com.lkr.project2.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.lkr.project2.entity.ChatRecord;
import com.lkr.project2.service.ChatService;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final StringRedisTemplate redisTemplate;
    
    private static final String CHAT_SESSION_PREFIX = "chat:session:";
    private static final int MAX_HISTORY_ROUNDS = 3;

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, StringRedisTemplate redisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请根据用户的问题提供准确、有用的回答。")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String chat(String message) {
        return chatClient.prompt(message)
                .call()
                .content();
    }

    @Override
    public String chat(String sessionId, String message) {
        String sessionKey = CHAT_SESSION_PREFIX + sessionId;
        
        List<ChatRecord> historyRecords = getHistoryRecords(sessionKey);
        
        String context = buildContext(historyRecords);
        
        String fullPrompt = context + "\n用户问：" + message;
        
        String answer = chatClient.prompt(fullPrompt)
                .call()
                .content();
        
        ChatRecord newRecord = new ChatRecord();
        newRecord.setSessionId(sessionId);
        newRecord.setUserMessage(message);
        newRecord.setAssistantMessage(answer);
        newRecord.setCreateTime(LocalDateTime.now());
        
        saveChatRecord(sessionKey, newRecord);
        
        return answer;
    }

    private List<ChatRecord> getHistoryRecords(String sessionKey) {
        String json = redisTemplate.opsForValue().get(sessionKey);
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.toList(json, ChatRecord.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String buildContext(List<ChatRecord> records) {
        if (records.isEmpty()) {
            return "";
        }
        
        int startIndex = Math.max(0, records.size() - MAX_HISTORY_ROUNDS);
        StringBuilder context = new StringBuilder();
        
        for (int i = startIndex; i < records.size(); i++) {
            ChatRecord record = records.get(i);
            context.append("用户：").append(record.getUserMessage()).append("\n");
            context.append("助手：").append(record.getAssistantMessage()).append("\n");
        }
        
        return context.toString().trim();
    }

    private void saveChatRecord(String sessionKey, ChatRecord record) {
        List<ChatRecord> records = getHistoryRecords(sessionKey);
        records.add(record);
        
        if (records.size() > MAX_HISTORY_ROUNDS) {
            records = records.subList(records.size() - MAX_HISTORY_ROUNDS, records.size());
        }
        
        redisTemplate.opsForValue().set(sessionKey, JSONUtil.toJsonStr(records), 24, TimeUnit.HOURS);
    }
}