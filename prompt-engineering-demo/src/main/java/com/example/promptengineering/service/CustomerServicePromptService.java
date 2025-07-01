package com.example.promptengineering.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerServicePromptService {

    private final ChatClient chatClient;

    public CustomerServicePromptService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String handleCustomerInquiry(String customerMessage, String customerName, String orderHistory) {
        // 定义客服 Prompt 模板
        String promptTemplate = "你是一位专业的客服代表，名字叫小明。请根据以下信息为客户提供帮助：\n\n" +
                "客户姓名：{customerName}\n" +
                "客户消息：{customerMessage}\n" +
                "历史订单：{orderHistory}\n\n" +
                "请遵循以下原则：\n" +
                "1. 保持友好、耐心的态度\n" +
                "2. 如果客户询问订单相关的问题，请参考历史订单信息\n" +
                "3. 如果不确定的信息，请建议客户联系人工客服\n" +
                "4. 回答要简洁明了，避免过于冗长\n" +
                "5. 如果客户表达不满，要表示理解和歉意\n\n" +
                "请以\"您好，{customerName}！\"开头，以\"还有什么可以帮您的吗？\"结尾。";

        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        String filledPrompt = template.render(Map.of(
            "customerName", customerName,
            "customerMessage", customerMessage,
            "orderHistory", orderHistory
        ));

        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }

    public String generateOrderStatusUpdate(String orderNumber, String status, String estimatedDelivery) {
        String promptTemplate = "请生成一条订单状态更新的通知消息：\n\n" +
                "订单号：{orderNumber}\n" +
                "当前状态：{status}\n" +
                "预计送达时间：{estimatedDelivery}\n\n" +
                "要求：\n" +
                "1. 语气要专业但友好\n" +
                "2. 包含所有必要信息\n" +
                "3. 如果状态是\"已发货\"，要提醒客户注意查收\n" +
                "4. 如果状态是\"配送中\"，要提醒客户保持电话畅通\n" +
                "5. 字数控制在 100 字以内";

        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        String filledPrompt = template.render(Map.of(
            "orderNumber", orderNumber,
            "status", status,
            "estimatedDelivery", estimatedDelivery
        ));

        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }
} 