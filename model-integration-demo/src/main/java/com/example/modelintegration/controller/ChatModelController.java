package com.example.modelintegration.controller;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatModelController {

    private final ChatClient primaryChatClient;
    private final ChatClient codeChatClient;
    private final ChatClient creativeChatClient;

    public ChatModelController(ChatClientFactory factory) {
        this.primaryChatClient = factory.getPrimaryChatClient();
        this.codeChatClient = factory.getCodeChatClient();
        this.creativeChatClient = factory.getCreativeChatClient();
    }

    /**
     * 基础对话接口
     */
    @PostMapping("/basic")
    public Map<String, Object> basicChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        ChatResponse response = primaryChatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        return Map.of(
                "response", response.getResult().getOutput().getText(),
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 代码分析专用接口
     */
    @PostMapping("/code-analysis")
    public Map<String, Object> analyzeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String question = request.get("question");

        String prompt = String.format("""
                请分析以下代码：
                
                ```
                %s
                ```
                
                问题：%s
                
                请提供详细的分析和建议。
                """, code, question);

        ChatResponse response = codeChatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "analysis", response.getResult().getOutput().getText(),
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "codeLength", code.length(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 创意写作接口
     */
    @PostMapping("/creative-writing")
    public Map<String, Object> creativeWriting(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String style = request.getOrDefault("style", "随意");

        String prompt = String.format("""
                请以'%s'风格，围绕'%s'这个主题进行创作。
                可以是诗歌、小故事、散文或其他创意形式。
                """, style, topic);

        ChatResponse response = creativeChatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "content", response.getResult().getOutput().getText(),
                "topic", topic,
                "style", style,
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 简单的健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Chat Model Controller",
                "timestamp", System.currentTimeMillis()
        );
    }
} 