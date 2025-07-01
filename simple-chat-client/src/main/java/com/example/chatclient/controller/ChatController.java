package com.example.chatclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @GetMapping("/ok")
    public String ok() {
        return "OK";
    }

    private final ChatClient customChatClient; // 注入我们自定义的 ChatClient

    public ChatController(ChatClient customChatClient) {
        this.customChatClient = customChatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你好，Spring AI！") String message) {
        // 使用 ChatClient 发送消息并获取回复
        String response = customChatClient.prompt()
                .user(message) // 设置用户输入
                .call()        // 调用 AI 模型
                .content();    // 获取回复内容
        return response;
    }
}
