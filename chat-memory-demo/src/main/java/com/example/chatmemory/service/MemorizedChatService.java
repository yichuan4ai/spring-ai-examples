package com.example.chatmemory.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * 使用 ChatClient Advisor 的记忆对话服务
 */
@Service
public class MemorizedChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ChatModel chatModel;

    public MemorizedChatService(ChatModel chatModel, ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        你是一个友好的AI助手，名字叫小智。你有以下特点：
                        1. 能够记住对话历史，并基于上下文进行回答
                        2. 当用户提到之前的内容时，你会主动回忆并引用
                        3. 对于初次见面的用户，你会主动介绍自己
                        4. 保持友好、耐心、专业的态度
                        5. 如果用户询问个人信息，你会根据之前的对话内容来回答
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 进行有记忆的对话
     */
    public Map<String, Object> chat(String conversationId, String userMessage) {
        long startTime = System.currentTimeMillis();

        String response = chatClient.prompt()
                .user(userMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "conversationId", conversationId,
                "userMessage", userMessage,
                "assistantResponse", response,
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 使用 PromptChatMemoryAdvisor 的示例
     * 将记忆内容作为文本附加到系统提示中
     */
    public String chatWithPromptMemory(String conversationId, String userMessage) {
        ChatClient promptMemoryClient = ChatClient.builder(chatModel)
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        return promptMemoryClient.prompt()
                .user(userMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
} 