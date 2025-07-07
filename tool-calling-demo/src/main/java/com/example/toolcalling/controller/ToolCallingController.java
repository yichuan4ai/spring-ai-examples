package com.example.toolcalling.controller;

import java.util.Map;

import com.example.toolcalling.service.ToolCallingChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.toolcalling.tools.DatabaseQueryTools;
import com.example.toolcalling.tools.FileOperationTools;
import com.example.toolcalling.tools.WeatherTools;

@RestController
@RequestMapping("/api/tools")
public class ToolCallingController {

    private final ToolCallingChatService chatService;
    private final WeatherTools weatherTools;
    private final FileOperationTools fileOperationTools;
    private final DatabaseQueryTools databaseQueryTools;

    public ToolCallingController(ToolCallingChatService chatService,
                                WeatherTools weatherTools,
                                FileOperationTools fileOperationTools,
                                DatabaseQueryTools databaseQueryTools) {
        this.chatService = chatService;
        this.weatherTools = weatherTools;
        this.fileOperationTools = fileOperationTools;
        this.databaseQueryTools = databaseQueryTools;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "Tool Calling Demo",
                "timestamp", System.currentTimeMillis(),
                "availableTools", "DateTime, Calculator, Weather, File, Database"
        );
    }

    @PostMapping("/chat")
    public Map<String, Object> chatWithTools(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithTools(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "availableTools", "DateTime, Calculator, Weather, File, Database",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/calculator")
    public Map<String, Object> useCalculator(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithCalculator(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolType", "Calculator",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/weather")
    public Map<String, Object> checkWeather(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        // 尝试提取城市名
        String city = extractCityFromMessage(message);

        if (city != null) {
            long startTime = System.currentTimeMillis();
            String weatherInfo = weatherTools.getWeather(city);
            long duration = System.currentTimeMillis() - startTime;
            
            return Map.of(
                    "userMessage", message,
                    "city", city,
                    "weatherInfo", weatherInfo,
                    "method", "Direct Tool Call",
                    "duration", duration,
                    "timestamp", System.currentTimeMillis()
            );
        } else {
            long startTime = System.currentTimeMillis();
            String response = chatService.chatWithWeatherTools(message);
            long duration = System.currentTimeMillis() - startTime;
            
            return Map.of(
                    "userMessage", message,
                    "aiResponse", response,
                    "method", "AI-Guided Tool Call",
                    "duration", duration,
                    "timestamp", System.currentTimeMillis()
            );
        }
    }

    @PostMapping("/files")
    public Map<String, Object> manageFiles(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithFileTools(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolType", "File Operations",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/database")
    public Map<String, Object> queryDatabase(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithDatabaseTools(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolType", "Database Query",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/basic")
    public Map<String, Object> useBasicTools(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithBasicTools(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolTypes", "DateTime, Calculator, Weather",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/data")
    public Map<String, Object> useDataTools(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithDataTools(message);
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolTypes", "File, Database",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/list-files")
    public Map<String, Object> listFiles() {
        long startTime = System.currentTimeMillis();
        String files = fileOperationTools.listFiles();
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "files", files,
                "method", "Direct Tool Call",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/user-count")
    public Map<String, Object> getUserCount() {
        long startTime = System.currentTimeMillis();
        String count = databaseQueryTools.getUserCount();
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "userCount", count,
                "method", "Direct Tool Call",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    // 简单的城市名提取逻辑
    private String extractCityFromMessage(String message) {
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "武汉", "成都", "西安", "重庆"};
        for (String city : cities) {
            if (message.contains(city)) {
                return city;
            }
        }
        return null;
    }
} 