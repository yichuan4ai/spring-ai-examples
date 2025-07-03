package com.example.modelintegration.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

/**
 * ChatClient 工厂接口
 * 用于创建不同类型的 ChatClient 实例
 */
public class ChatClientFactory {

    private final String provider;
    private final ChatModel chatModel;

    private final ChatClient primaryChatClient;
    private final ChatClient codeChatClient;
    private final ChatClient creativeChatClient;

    public ChatClientFactory(String provider, ChatModel chatModel) {
        this.provider = provider;
        this.chatModel = chatModel;

        primaryChatClient = createChatClient(SystemPrompts.PRIMARY);
        codeChatClient = createChatClient(SystemPrompts.CODE);
        creativeChatClient = createChatClient(SystemPrompts.CREATIVE);
    }

    public ChatClient getPrimaryChatClient() {
        return primaryChatClient;
    }

    public ChatClient getCodeChatClient() {
        return codeChatClient;
    }

    public ChatClient getCreativeChatClient() {
        return creativeChatClient;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }


    private ChatClient createChatClient(String prompt) {
        return ChatClient.builder(chatModel)
                .defaultSystem(prompt)
                .build();
    }

    public String getProviderName() {
        return provider;
    }
} 