package com.example.chatmemory.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

/**
 * 智能客服机器人服务
 */
@Service
public class IntelligentCustomerServiceBot {

    private final ChatClient customerServiceClient;

    public IntelligentCustomerServiceBot(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.customerServiceClient = chatClientBuilder
                .defaultSystem("""
                        你是一个专业的客服代表，具备以下能力：
                        
                        1. 记住客户的基本信息（姓名、问题类型等）
                        2. 能够关联之前的对话内容，提供连贯的服务
                        3. 根据客户的历史咨询记录，提供个性化的解决方案
                        4. 对于复杂问题，能够逐步引导客户解决
                        5. 始终保持礼貌、专业、耐心的态度
                        
                        服务原则：
                        - 首先理解客户的问题
                        - 基于对话历史提供相关建议
                        - 如果无法解决，及时提供人工客服联系方式
                        - 记录重要信息，便于后续服务
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 处理客户咨询
     */
    public Map<String, Object> handleCustomerInquiry(String customerId, String customerName, String inquiry) {
        String conversationId = "customer_" + customerId;
        
        // 构建包含客户信息的消息
        String messageWithContext = String.format("""
                客户信息：
                - 客户ID: %s
                - 姓名: %s
                
                客户咨询：%s
                """, customerId, customerName, inquiry);
        
        long startTime = System.currentTimeMillis();
        
        String response = customerServiceClient.prompt()
                .user(messageWithContext)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        
        long duration = System.currentTimeMillis() - startTime;
        
        return Map.of(
                "customerId", customerId,
                "customerName", customerName,
                "inquiry", inquiry,
                "response", response,
                "conversationId", conversationId,
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 后续对话（不需要重复客户信息）
     */
    public Map<String, Object> continueConversation(String customerId, String message) {
        String conversationId = "customer_" + customerId;
        
        long startTime = System.currentTimeMillis();
        
        String response = customerServiceClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        
        long duration = System.currentTimeMillis() - startTime;
        
        return Map.of(
                "customerId", customerId,
                "message", message,
                "response", response,
                "conversationId", conversationId,
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }
} 