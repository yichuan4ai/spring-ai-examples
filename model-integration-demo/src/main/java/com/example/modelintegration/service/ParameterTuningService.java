package com.example.modelintegration.service;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParameterTuningService {

    private final ChatModel chatModel;

    public ParameterTuningService(ChatClientFactory factory) {
        this.chatModel = factory.getChatModel();
    }

    /**
     * 测试不同 Temperature 值的效果
     */
    public List<Map<String, Object>> testTemperatureEffects(String prompt) {
        double[] temperatures = {0.0, 0.3, 0.7, 1.0, 1.5};
        List<Map<String, Object>> results = new ArrayList<>();

        for (double temperature : temperatures) {
            try {
                ChatClient client = ChatClient.builder(chatModel)
                        .defaultOptions(ChatOptions.builder()
                                .temperature(temperature)
                                .maxTokens(200)
                                .build())
                        .build();

                long startTime = System.currentTimeMillis();
                String response = client.prompt()
                        .user(prompt)
                        .call()
                        .content();
                long duration = System.currentTimeMillis() - startTime;

                results.add(Map.of(
                        "temperature", temperature,
                        "response", response,
                        "responseLength", response.length(),
                        "durationMs", duration
                ));
            } catch (Exception e) {
                results.add(Map.of(
                        "temperature", temperature,
                        "error", e.getMessage(),
                        "status", "failed"
                ));
            }
        }

        return results;
    }

    /**
     * 对比不同参数组合的效果
     */
    public Map<String, Object> compareParameterCombinations(String prompt) {
        Map<String, Object> results = new HashMap<>();

        // 保守配置：适合需要准确性的场景
        results.put("conservative", testConfiguration(prompt, 0.0f, 0.1f, 200, "保守配置"));

        // 平衡配置：适合一般对话场景
        results.put("balanced", testConfiguration(prompt, 0.7f, 0.9f, 500, "平衡配置"));

        // 创造性配置：适合内容创作场景
        results.put("creative", testConfiguration(prompt, 1.2f, 0.95f, 800, "创造性配置"));

        return results;
    }

    /**
     * 测试特定参数配置
     */
    private Map<String, Object> testConfiguration(String prompt, double temperature,
                                                  double topP, int maxTokens, String description) {
        try {
            ChatClient client = ChatClient.builder(chatModel)
                    .defaultOptions(ChatOptions.builder()
                            .temperature(temperature)
                            .topP(topP)
                            .maxTokens(maxTokens)
                            .build())
                    .build();

            long startTime = System.currentTimeMillis();
            String response = client.prompt()
                    .user(prompt)
                    .call()
                    .content();
            long duration = System.currentTimeMillis() - startTime;

            return Map.of(
                    "description", description,
                    "parameters", Map.of(
                            "temperature", temperature,
                            "topP", topP,
                            "maxTokens", maxTokens
                    ),
                    "response", response,
                    "responseLength", response.length(),
                    "durationMs", duration,
                    "status", "success"
            );
        } catch (Exception e) {
            return Map.of(
                    "description", description,
                    "parameters", Map.of(
                            "temperature", temperature,
                            "topP", topP,
                            "maxTokens", maxTokens
                    ),
                    "error", e.getMessage(),
                    "status", "failed"
            );
        }
    }

    /**
     * 参数建议服务
     */
    public Map<String, Object> getParameterRecommendations(String useCase) {
        Map<String, Map<String, Object>> recommendations = new HashMap<>();

        recommendations.put("qa", Map.of(
                "name", "问答系统",
                "temperature", 0.1,
                "topP", 0.1,
                "maxTokens", 300,
                "reason", "需要准确、一致的答案"
        ));

        recommendations.put("creative", Map.of(
                "name", "创意写作",
                "temperature", 1.0,
                "topP", 0.9,
                "maxTokens", 1000,
                "reason", "需要创造性和多样性"
        ));

        recommendations.put("chat", Map.of(
                "name", "日常对话",
                "temperature", 0.7,
                "topP", 0.9,
                "maxTokens", 500,
                "reason", "平衡准确性和自然性"
        ));

        recommendations.put("code", Map.of(
                "name", "代码生成",
                "temperature", 0.0,
                "topP", 0.1,
                "maxTokens", 800,
                "reason", "需要精确的语法和逻辑"
        ));

        Map<String, Object> recommendation = recommendations.getOrDefault(
                useCase.toLowerCase(),
                recommendations.get("chat")
        );

        return Map.of(
                "useCase", useCase,
                "recommendation", recommendation,
                "allOptions", recommendations.keySet(),
                "timestamp", System.currentTimeMillis()
        );
    }
} 