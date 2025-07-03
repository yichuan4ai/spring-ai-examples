package com.example.modelintegration.controller;

import java.util.List;
import java.util.Map;

import com.example.modelintegration.config.EmbeddingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embedding")
@ConditionalOnBean(EmbeddingService.class)
public class EmbeddingModelController {

    private final EmbeddingService embeddingService;

    public EmbeddingModelController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    /**
     * 单个文本嵌入
     */
    @PostMapping("/embed")
    public Map<String, Object> embedText(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        float[] embedding = embeddingService.embed(text);

        return Map.of(
                "text", text,
                "embedding", embedding,
                "dimensions", embedding.length,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 批量文本嵌入
     */
    @PostMapping("/embed-batch")
    public Map<String, Object> embedBatch(@RequestBody Map<String, List<String>> request) {
        List<String> texts = request.get("texts");

        Map<String, float[]> embeddings = embeddingService.embedBatch(texts);

        return Map.of(
                "texts", texts,
                "embeddings", embeddings,
                "count", texts.size(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 文本相似度计算
     */
    @PostMapping("/similarity")
    public Map<String, Object> calculateSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");

        double similarity = embeddingService.calculateSimilarity(text1, text2);

        return Map.of(
                "text1", text1,
                "text2", text2,
                "similarity", similarity,
                "similarityPercentage", String.format("%.2f%%", similarity * 100),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Embedding Model Controller",
                "timestamp", System.currentTimeMillis()
        );
    }
} 