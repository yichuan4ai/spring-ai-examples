package com.example.modelintegration.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EmbeddingConfig {

    @Bean
    @Profile("openai")
    public EmbeddingService openAiEmbeddingService(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
        return new EmbeddingService(embeddingModel);
    }

    @Bean
    @Profile("ollama")
    public EmbeddingService ollamaEmbeddingService(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return new EmbeddingService(embeddingModel);
    }
} 