package com.lkr.project2.controller;

import com.lkr.project2.common.Result;
import com.lkr.project2.dto.ChatRequestDTO;
import com.lkr.project2.service.ChatService;
import com.lkr.project2.vo.ChatResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        ChatResponseVO responseVO = chatService.chat(requestDTO);
        return Result.success(responseVO);
    }
}