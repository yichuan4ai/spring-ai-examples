package com.example.chatmemory.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.chatmemory.service.DirectMemoryChatService;
import com.example.chatmemory.service.IntelligentCustomerServiceBot;
import com.example.chatmemory.service.MemorizedChatService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat Memory API 控制器
 */
@RestController
@RequestMapping("/api/chat")
public class ChatMemoryController {

    private final MemorizedChatService memorizedChatService;
    private final DirectMemoryChatService directMemoryChatService;
    private final IntelligentCustomerServiceBot customerServiceBot;

    public ChatMemoryController(MemorizedChatService memorizedChatService,
                               DirectMemoryChatService directMemoryChatService,
                               IntelligentCustomerServiceBot customerServiceBot) {
        this.memorizedChatService = memorizedChatService;
        this.directMemoryChatService = directMemoryChatService;
        this.customerServiceBot = customerServiceBot;
    }

    /**
     * 开始新对话
     */
    @PostMapping("/new")
    public Map<String, Object> startNewConversation() {
        String conversationId = UUID.randomUUID().toString();
        
        return Map.of(
                "conversationId", conversationId,
                "message", "新对话已创建，你可以开始聊天了！",
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 使用 Advisor 方式的对话
     */
    @PostMapping("/{conversationId}/advisor")
    public Map<String, Object> chatWithAdvisor(@PathVariable String conversationId,
                                              @RequestBody Map<String, String> request) {
        String message = request.get("message");
        return memorizedChatService.chat(conversationId, message);
    }

    /**
     * 直接使用 ChatMemory 的对话
     */
    @PostMapping("/{conversationId}/direct")
    public Map<String, Object> chatWithDirectMemory(@PathVariable String conversationId,
                                                   @RequestBody Map<String, String> request) {
        String message = request.get("message");
        return directMemoryChatService.chatWithManualMemory(conversationId, message);
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/{conversationId}/history")
    public List<Map<String, Object>> getHistory(@PathVariable String conversationId,
                                               @RequestParam(defaultValue = "10") int count) {
        return directMemoryChatService.getConversationHistory(conversationId, count);
    }

    /**
     * 清除对话历史
     */
    @DeleteMapping("/{conversationId}")
    public Map<String, Object> clearHistory(@PathVariable String conversationId) {
        directMemoryChatService.clearConversation(conversationId);
        return Map.of(
                "conversationId", conversationId,
                "action", "cleared",
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 客服场景 - 首次咨询
     */
    @PostMapping("/customer-service/new")
    public Map<String, Object> newCustomerInquiry(@RequestBody Map<String, String> request) {
        String customerId = request.get("customerId");
        String customerName = request.get("customerName");
        String inquiry = request.get("inquiry");
        
        return customerServiceBot.handleCustomerInquiry(customerId, customerName, inquiry);
    }

    /**
     * 客服场景 - 继续对话
     */
    @PostMapping("/customer-service/{customerId}/continue")
    public Map<String, Object> continueCustomerService(@PathVariable String customerId,
                                                      @RequestBody Map<String, String> request) {
        String message = request.get("message");
        return customerServiceBot.continueConversation(customerId, message);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Chat Memory Demo",
                "features", List.of(
                        "MessageChatMemoryAdvisor",
                        "Direct ChatMemory Access",
                        "Customer Service Bot"
                ),
                "timestamp", System.currentTimeMillis()
        );
    }
} 