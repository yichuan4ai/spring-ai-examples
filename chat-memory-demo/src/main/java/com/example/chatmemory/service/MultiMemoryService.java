package com.example.chatmemory.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

/**
 * 多记忆策略服务
 */
@Service
public class MultiMemoryService {

    private final ChatClient shortMemoryClient;
    private final ChatClient longMemoryClient;

    public MultiMemoryService(ChatClient.Builder chatClientBuilder) {
        // 短期记忆客户端（最近5条消息）
        ChatMemory shortMemory = MessageWindowChatMemory.builder()
                .maxMessages(5)
                .build();
        
        this.shortMemoryClient = chatClientBuilder
                .defaultSystem("你是一个使用短期记忆的AI助手，只记住最近的5条消息。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(shortMemory).build())
                .build();

        // 长期记忆客户端（最近30条消息）
        ChatMemory longMemory = MessageWindowChatMemory.builder()
                .maxMessages(30)
                .build();
        
        this.longMemoryClient = chatClientBuilder
                .defaultSystem("你是一个使用长期记忆的AI助手，记住最近的30条消息，能够进行更深入的对话。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(longMemory).build())
                .build();
    }

    public String chatWithShortMemory(String conversationId, String message) {
        return shortMemoryClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    public String chatWithLongMemory(String conversationId, String message) {
        return longMemoryClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
} 