package com.example.chatclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // 假设你已经配置了 ChatModel，Spring AI 会自动为你注入
    // 例如，如果你引入了 spring-ai-starter-model-deepseek 依赖，它会自动配置 DeepSeek ChatModel
    @Bean
    public ChatClient customChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                // 你可以在这里进行更多自定义配置，例如：
                // .defaultSystem("你是一个乐于助人的AI助手。")
                // .defaultOptions(DeepSeekChatOptions.builder().withTemperature(0.7f).build())
                .build();
    }
}