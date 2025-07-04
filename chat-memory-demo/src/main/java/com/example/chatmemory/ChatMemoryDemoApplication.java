package com.example.chatmemory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatMemoryDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMemoryDemoApplication.class, args);
    }
} 