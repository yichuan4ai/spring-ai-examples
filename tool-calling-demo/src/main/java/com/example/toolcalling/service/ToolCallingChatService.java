package com.example.toolcalling.service;

import com.example.toolcalling.tools.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ToolCallingChatService {

    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    private final CalculatorTools calculatorTools;
    private final WeatherTools weatherTools;
    private final FileOperationTools fileOperationTools;
    private final DatabaseQueryTools databaseQueryTools;

    public ToolCallingChatService(ChatModel chatModel,
                                 DateTimeTools dateTimeTools,
                                 CalculatorTools calculatorTools,
                                 WeatherTools weatherTools,
                                 FileOperationTools fileOperationTools,
                                 DatabaseQueryTools databaseQueryTools) {
        this.dateTimeTools = dateTimeTools;
        this.calculatorTools = calculatorTools;
        this.weatherTools = weatherTools;
        this.fileOperationTools = fileOperationTools;
        this.databaseQueryTools = databaseQueryTools;

        // 创建支持工具调用的 ChatClient
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                    你是一个智能助手，可以使用多种工具来帮助用户。
                    你有以下工具可以使用：
                    1. 时间工具：获取当前时间和不同时区的时间
                    2. 计算器工具：执行数学运算和计算
                    3. 天气工具：查询城市天气信息和穿衣建议
                    4. 文件操作工具：创建、读取、删除文件等
                    5. 数据库查询工具：查询用户信息和统计数据
                    
                    请根据用户的问题，智能选择合适的工具来提供准确的信息。
                    如果需要使用多个工具，请合理安排调用顺序。
                    """)
                .build();
    }

    public String chatWithTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(dateTimeTools, calculatorTools, weatherTools, fileOperationTools, databaseQueryTools)
                .call()
                .content();
    }

    // 只使用特定工具的方法
    public String chatWithTimeTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(dateTimeTools)
                .call()
                .content();
    }

    public String chatWithCalculator(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(calculatorTools)
                .call()
                .content();
    }

    public String chatWithWeatherTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(weatherTools)
                .call()
                .content();
    }

    public String chatWithFileTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(fileOperationTools)
                .call()
                .content();
    }

    public String chatWithDatabaseTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(databaseQueryTools)
                .call()
                .content();
    }

    // 组合工具的方法
    public String chatWithBasicTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(dateTimeTools, calculatorTools, weatherTools)
                .call()
                .content();
    }

    public String chatWithDataTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(fileOperationTools, databaseQueryTools)
                .call()
                .content();
    }
} 