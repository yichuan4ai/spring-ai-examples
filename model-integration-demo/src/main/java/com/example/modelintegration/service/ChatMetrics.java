package com.example.modelintegration.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ChatMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Timer responseTimer;
    private final Counter errorCounter;

    public ChatMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.requestCounter = Counter.builder("ai.chat.requests")
                .description("Total chat requests")
                .register(meterRegistry);

        this.responseTimer = Timer.builder("ai.chat.response.time")
                .description("Chat response time")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("ai.chat.errors")
                .description("Chat request errors")
                .register(meterRegistry);
    }

    /**
     * 记录聊天请求的执行时间和结果
     */
    public String timedChat(ChatClient client, String message) {
        requestCounter.increment();

        try {
            return responseTimer.recordCallable(() ->
                    client.prompt().user(message).call().content()
            );
        } catch (Exception e) {
            errorCounter.increment();
            throw new RuntimeException("Chat request failed", e);
        }
    }

    /**
     * 手动记录请求
     */
    public void recordRequest() {
        requestCounter.increment();
    }

    /**
     * 手动记录错误
     */
    public void recordError() {
        errorCounter.increment();
    }

    /**
     * 获取当前指标统计
     */
    public io.micrometer.core.instrument.Metrics getMetrics() {
        return new io.micrometer.core.instrument.Metrics() {
            public double getRequestCount() {
                return requestCounter.count();
            }

            public double getErrorCount() {
                return errorCounter.count();
            }

            public double getAverageResponseTime() {
                return responseTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        };
    }
} 