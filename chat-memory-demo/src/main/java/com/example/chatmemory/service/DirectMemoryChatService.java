package com.example.chatmemory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

/**
 * 直接使用 ChatMemory 的服务
 */
@Service
public class DirectMemoryChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public DirectMemoryChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        你是一个专业的AI助手，能够基于对话历史提供连贯的回答。
                        请根据之前的对话内容，为用户提供相关和有帮助的回应。
                        """)
                .build();
        this.chatMemory = chatMemory;
    }

    /**
     * 手动管理记忆的对话
     */
    public Map<String, Object> chatWithManualMemory(String conversationId, String userMessage) {
        // 1. 创建用户消息
        UserMessage currentUserMessage = new UserMessage(userMessage);
        
        // 2. 获取对话历史
        List<Message> history = chatMemory.get(conversationId);
        
        // 3. 构建完整的消息列表
        List<Message> messages = new ArrayList<>(history);
        messages.add(currentUserMessage);
        
        // 4. 调用 ChatClient
        long startTime = System.currentTimeMillis();
        ChatResponse response = chatClient.prompt()
                .messages(messages)
                .call()
                .chatResponse();
        long duration = System.currentTimeMillis() - startTime;
        
        String assistantResponse = response.getResult().getOutput().getText();
        
        // 5. 将新消息添加到记忆中
        chatMemory.add(conversationId, List.of(
                currentUserMessage,
                new AssistantMessage(assistantResponse)
        ));
        
        return Map.of(
                "conversationId", conversationId,
                "userMessage", userMessage,
                "assistantResponse", assistantResponse,
                "historyCount", history.size(),
                "duration", duration,
                "usage", response.getMetadata().getUsage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取对话历史
     */
    public List<Map<String, Object>> getConversationHistory(String conversationId, int count) {
        List<Message> history = chatMemory.get(conversationId);
        
        return history.stream()
                .map(message -> Map.<String, Object>of(
                        "role", message.getMessageType().name().toLowerCase(),
                        "content", message.getText()
                ))
                .toList();
    }

    /**
     * 清除对话历史
     */
    public void clearConversation(String conversationId) {
        chatMemory.clear(conversationId);
    }
} 