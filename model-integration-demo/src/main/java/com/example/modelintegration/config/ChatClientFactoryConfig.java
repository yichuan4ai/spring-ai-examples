package com.example.modelintegration.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek 模型配置类 - 使用工厂模式重构
 * 减少重复代码，提高可维护性
 */
@Configuration
public class ChatClientFactoryConfig {
    @Value("${info.model.provider:Unknown}")
    private String provider;

    @Bean
    public ChatClientFactory deepSeekChatClientFactory(@Qualifier("deepSeekChatModel") ChatModel chatModel) {
        return new ChatClientFactory("provider", chatModel);
    }
} 