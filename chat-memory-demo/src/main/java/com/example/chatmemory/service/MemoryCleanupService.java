package com.example.chatmemory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 记忆清理服务
 */
@Component
public class MemoryCleanupService {

    private static final Logger log = LoggerFactory.getLogger(MemoryCleanupService.class);
    private final ChatMemory chatMemory;

    public MemoryCleanupService(ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
    }

    /**
     * 清理过期对话
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行
    public void cleanupExpiredConversations() {
        // 在实际应用中，你可能需要跟踪对话的最后活动时间
        // 这里只是一个示例
        log.info("执行对话清理任务...");
    }

    /**
     * 手动清理指定用户的所有对话
     */
    public void clearUserConversations(String userId) {
        // 实际实现中，你需要维护用户和对话ID的映射关系
        log.info("清理用户 {} 的所有对话", userId);
    }
} 