package com.lkr.project2.service;

import com.lkr.project2.dto.ChatRequestDTO;
import com.lkr.project2.vo.ChatResponseVO;

public interface ChatService {
    ChatResponseVO chat(ChatRequestDTO requestDTO);
}