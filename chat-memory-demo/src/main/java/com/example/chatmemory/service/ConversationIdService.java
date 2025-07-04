package com.example.chatmemory.service;

import org.springframework.stereotype.Service;

/**
 * 对话ID管理服务
 */
@Service
public class ConversationIdService {

    /**
     * 为用户生成唯一的对话ID
     */
    public String generateConversationId(String userId, String sessionType) {
        return String.format("%s_%s_%s", 
                sessionType, userId, System.currentTimeMillis());
    }

    /**
     * 从对话ID中提取用户ID
     */
    public String extractUserId(String conversationId) {
        String[] parts = conversationId.split("_");
        return parts.length > 1 ? parts[1] : null;
    }
} 