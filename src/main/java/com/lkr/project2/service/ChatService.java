package com.lkr.project2.service;

public interface ChatService {
    String chat(String message);
    String chat(String sessionId, String message);
}