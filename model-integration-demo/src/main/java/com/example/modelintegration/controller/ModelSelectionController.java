package com.example.modelintegration.controller;

import com.example.modelintegration.service.ModelSelectionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/model-selecting")
public class ModelSelectionController {

    private final ModelSelectionService modelService;

    public ModelSelectionController(ModelSelectionService modelService) {
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