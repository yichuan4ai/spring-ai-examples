package com.example.modelintegration.controller;

import java.util.Map;

import com.example.modelintegration.service.ModelSelectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/model-switching")
public class ModelComparisonController {

    private final ModelSelectionService modelService;

    public ModelComparisonController(ModelSelectionService modelService) {
        this.modelService = modelService;
    }

    /**
     * 智能模型选择
     */
    @PostMapping("/smart-selection")
    public Map<String, Object> smartSelection(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String taskType = request.get("taskType");

        if (taskType != null && !taskType.trim().isEmpty()) {
            // 手动指定任务类型
            String response = modelService.processWithBestModel(input, taskType);
            return Map.of(
                "input", input,
                "taskType", taskType,
                "response", response,
                "selectedModel", getModelNameForTask(taskType),
                "mode", "manual"
            );
        } else {
            // 自动检测任务类型
            return modelService.smartTaskRouting(input);
        }
    }

    /**
     * 对比不同模型的回答
     */
    @PostMapping("/compare-models")
    public Map<String, Object> compareModels(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Map.of(
                "error", "Prompt cannot be empty",
                "status", "failed"
            );
        }
        
        Map<String, Object> responses = modelService.compareProviders(prompt);

        return Map.of(
            "prompt", prompt,
            "responses", responses,
            "note", "不同配置可能产生不同的回答风格和质量",
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取可用模型信息
     */
    @GetMapping("/available-models")
    public Map<String, Object> getAvailableModels() {
        return modelService.getAvailableModels();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "OK",
            "service", "Model Comparison Controller",
            "timestamp", System.currentTimeMillis()
        );
    }

    private String getModelNameForTask(String taskType) {
        if (taskType == null) return "General Assistant";
        
        switch (taskType.toLowerCase()) {
            case "creative":
            case "writing":
                return "Creative Writing Assistant";
            case "technical":
            case "code":
                return "Technical Expert";
            case "business":
                return "Business Consultant";
            default:
                return "General Assistant";
        }
    }
} 