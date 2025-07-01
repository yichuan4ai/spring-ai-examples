package com.example.promptengineering.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class WeatherPromptService {

    private final ChatClient chatClient;

    public WeatherPromptService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String getWeatherInfo(String city, String date) {
        // 定义 Prompt 模板
        String promptTemplate = """
            你是一位专业的天气预报员。请根据以下信息为用户提供天气建议：
            
            城市：{city}
            日期：{date}
            
            请提供以下信息：
            1. 天气状况描述
            2. 温度范围
            3. 出行建议
            4. 着装建议
            
            请用友好的语气回答，就像在和朋友聊天一样。
            """;

        // 创建 PromptTemplate 实例
        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        // 填充模板参数
        String filledPrompt = template.render(Map.of(
            "city", city,
            "date", date
        ));

        // 调用 AI 模型
        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }
} 