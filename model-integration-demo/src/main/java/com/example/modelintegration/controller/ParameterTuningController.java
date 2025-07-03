package com.example.modelintegration.controller;

import java.util.List;
import java.util.Map;

import com.example.modelintegration.service.ParameterTuningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tuning")
public class ParameterTuningController {

    private final ParameterTuningService tuningService;

    public ParameterTuningController(ParameterTuningService tuningService) {
        this.tuningService = tuningService;
    }

    /**
     * 测试不同 Temperature 的效果
     */
    @PostMapping("/temperature-test")
    public Map<String, Object> testTemperature(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Map.of(
                "error", "Prompt cannot be empty",
                "status", "failed"
            );
        }
        
        List<Map<String, Object>> results = tuningService.testTemperatureEffects(prompt);
        
        return Map.of(
            "prompt", prompt,
            "results", results,
            "totalTests", results.size(),
            "description", "不同temperature值对输出随机性和创造性的影响测试",
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 对比不同参数组合
     */
    @PostMapping("/parameter-comparison")
    public Map<String, Object> compareParameters(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Map.of(
                "error", "Prompt cannot be empty",
                "status", "failed"
            );
        }
        
        Map<String, Object> results = tuningService.compareParameterCombinations(prompt);
        
        return Map.of(
            "prompt", prompt,
            "configurations", results,
            "description", "保守、平衡、创造性三种参数配置的对比测试",
            "recommendations", Map.of(
                "conservative", "适合需要准确答案的场景，如问答系统、事实查询",
                "balanced", "适合日常对话场景，平衡准确性和自然性",
                "creative", "适合创意写作、头脑风暴等需要发散思维的场景"
            ),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取参数调优建议
     */
    @GetMapping("/recommendations/{useCase}")
    public Map<String, Object> getRecommendations(@PathVariable String useCase) {
        return tuningService.getParameterRecommendations(useCase);
    }

    /**
     * 获取所有支持的用例类型
     */
    @GetMapping("/use-cases")
    public Map<String, Object> getUseCases() {
        return Map.of(
            "useCases", Map.of(
                "qa", "问答系统 - 需要准确、一致的答案",
                "creative", "创意写作 - 需要创造性和多样性",
                "chat", "日常对话 - 平衡准确性和自然性",
                "code", "代码生成 - 需要精确的语法和逻辑"
            ),
            "parameterGuide", Map.of(
                "temperature", "控制输出随机性，0.0最确定，2.0最随机",
                "topP", "核采样，控制候选词汇范围，0.1最集中，1.0最多样",
                "maxTokens", "最大输出长度，根据需求设置"
            ),
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
            "service", "Parameter Tuning Controller",
            "timestamp", System.currentTimeMillis()
        );
    }
} 