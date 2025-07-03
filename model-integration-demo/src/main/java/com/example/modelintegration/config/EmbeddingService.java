package com.example.modelintegration.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;
import java.util.Map;

public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 将文本转换为向量
     */
    public float[] embed(String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResults().get(0).getOutput();
    }

    /**
     * 批量嵌入文本
     */
    public Map<String, float[]> embedBatch(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);

        Map<String, float[]> result = new java.util.HashMap<>();
        for (int i = 0; i < texts.size(); i++) {
            result.put(texts.get(i), response.getResults().get(i).getOutput());
        }

        return result;
    }

    /**
     * 计算两个文本的相似度
     */
    public double calculateSimilarity(String text1, String text2) {
        float[] embedding1 = embed(text1);
        float[] embedding2 = embed(text2);

        return cosineSimilarity(embedding1, embedding2);
    }

    /**
     * 计算余弦相似度
     */
    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
} 