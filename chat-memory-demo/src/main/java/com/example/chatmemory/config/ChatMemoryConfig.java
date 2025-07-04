package com.example.chatmemory.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }

    /**
     * 可选：自定义 ChatMemory 配置
     * 如果不配置，Spring AI 会使用默认的自动配置
     */
    @Bean
    public ChatMemory customChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(30)  // 设置最大消息数为30
                .build();
    }
} 