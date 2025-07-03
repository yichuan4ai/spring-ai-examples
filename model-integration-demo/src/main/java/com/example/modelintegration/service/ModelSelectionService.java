package com.example.modelintegration.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ModelSelectionService {

    private final Map<String, ChatClient> chatClients;
    private final Map<String, String> modelDescriptions;

    public ModelSelectionService(ChatClientFactory clientFactory) {
        // 创建不同配置的客户端
        this.chatClients = new HashMap<>();
        this.modelDescriptions = new HashMap<>();

        // 基础模型
        this.chatClients.put("default", clientFactory.getPrimaryChatClient());
        this.modelDescriptions.put("default", "通用助手，适合日常问答");

        // 代码分析模型
        this.chatClients.put("technical", clientFactory.getCodeChatClient());
        this.modelDescriptions.put("technical", "技术专家，专注代码分析和技术解答");

        // 创意写作模型
        this.chatClients.put("creative", clientFactory.getCreativeChatClient());
        this.modelDescriptions.put("creative", "创意作家，擅长文学创作和内容创意");

        // 业务咨询模型
        this.chatClients.put("business", ChatClient.builder(clientFactory.getChatModel())
                .defaultSystem("""
                        你是一个资深的商业顾问，擅长商业分析、市场策略和企业管理。
                        请用专业且易懂的语言回答商业相关问题。
                        """)
                .build());
        this.modelDescriptions.put("business", "商业顾问，专注商业分析和策略规划");
    }

    /**
     * 根据任务类型选择合适的模型配置
     */
    public String processWithBestModel(String input, String taskType) {
        ChatClient selectedClient = selectModelForTask(taskType);

        return selectedClient.prompt()
                .user(input)
                .call()
                .content();
    }

    /**
     * 对比不同模型提供商的响应
     */
    public Map<String, Object> compareProviders(String prompt) {
        Map<String, Object> results = new ConcurrentHashMap<>();
        Map<String, CompletableFuture<Map<String, Object>>> futures = new HashMap<>();

        // 并行执行多个模型的请求
        for (Map.Entry<String, ChatClient> entry : chatClients.entrySet()) {
            String modelType = entry.getKey();
            ChatClient client = entry.getValue();

            CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    String response = client.prompt()
                            .user(prompt)
                            .call()
                            .content();
                    long duration = System.currentTimeMillis() - startTime;

                    return Map.of(
                            "response", response,
                            "duration", duration,
                            "responseLength", response.length(),
                            "status", "success",
                            "description", modelDescriptions.get(modelType)
                    );
                } catch (Exception e) {
                    return Map.of(
                            "error", e.getMessage(),
                            "status", "failed",
                            "description", modelDescriptions.get(modelType)
                    );
                }
            });

            futures.put(modelType, future);
        }

        // 等待所有请求完成
        futures.forEach((modelType, future) -> {
            try {
                results.put(modelType, future.get());
            } catch (Exception e) {
                results.put(modelType, Map.of(
                        "error", "Request timeout or failed: " + e.getMessage(),
                        "status", "failed"
                ));
            }
        });

        return results;
    }

    /**
     * 智能任务路由 - 根据输入内容自动选择最合适的模型
     */
    public Map<String, Object> smartTaskRouting(String input) {
        String taskType = detectTaskType(input);
        ChatClient selectedClient = selectModelForTask(taskType);

        long startTime = System.currentTimeMillis();
        String response = selectedClient.prompt()
                .user(input)
                .call()
                .content();
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "input", input,
                "detectedTaskType", taskType,
                "selectedModel", getModelNameForTask(taskType),
                "response", response,
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取所有可用的模型信息
     */
    public Map<String, Object> getAvailableModels() {
        Map<String, Object> models = new HashMap<>();

        chatClients.keySet().forEach(modelType -> {
            models.put(modelType, Map.of(
                    "name", getModelNameForTask(modelType),
                    "description", modelDescriptions.get(modelType),
                    "suitableFor", getSuitableTasksForModel(modelType)
            ));
        });

        return Map.of(
                "models", models,
                "totalCount", models.size(),
                "timestamp", System.currentTimeMillis()
        );
    }

    private ChatClient selectModelForTask(String taskType) {
        return switch (taskType.toLowerCase()) {
            case "creative", "writing", "story" -> chatClients.get("creative");
            case "technical", "code", "programming" -> chatClients.get("technical");
            case "business", "strategy", "analysis" -> chatClients.get("business");
            default -> chatClients.get("default");
        };
    }

    private String detectTaskType(String input) {
        String lowerInput = input.toLowerCase();

        // 代码相关关键词
        if (lowerInput.contains("代码") || lowerInput.contains("程序") ||
                lowerInput.contains("函数") || lowerInput.contains("bug") ||
                lowerInput.contains("编程") || lowerInput.contains("算法")) {
            return "technical";
        }

        // 创意写作相关关键词
        if (lowerInput.contains("写作") || lowerInput.contains("故事") ||
                lowerInput.contains("诗歌") || lowerInput.contains("创意") ||
                lowerInput.contains("小说") || lowerInput.contains("文章")) {
            return "creative";
        }

        // 商业相关关键词
        if (lowerInput.contains("商业") || lowerInput.contains("市场") ||
                lowerInput.contains("策略") || lowerInput.contains("管理") ||
                lowerInput.contains("营销") || lowerInput.contains("分析")) {
            return "business";
        }

        return "default";
    }

    private String getModelNameForTask(String taskType) {
        return switch (taskType.toLowerCase()) {
            case "creative" -> "Creative Writing Assistant";
            case "technical" -> "Technical Expert";
            case "business" -> "Business Consultant";
            default -> "General Assistant";
        };
    }

    private String[] getSuitableTasksForModel(String modelType) {
        return switch (modelType.toLowerCase()) {
            case "creative" -> new String[]{"创意写作", "内容创作", "故事编写", "诗歌创作"};
            case "technical" -> new String[]{"代码分析", "技术咨询", "程序调试", "架构设计"};
            case "business" -> new String[]{"商业分析", "市场策略", "管理咨询", "数据分析"};
            default -> new String[]{"日常问答", "通用咨询", "信息查询", "学习辅导"};
        };
    }
} 