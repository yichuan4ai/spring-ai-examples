package com.example.promptengineering.controller;

import com.example.promptengineering.service.CustomerServicePromptService;
import com.example.promptengineering.service.WeatherPromptService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
public class PromptDemoController {

    private final WeatherPromptService weatherPromptService;
    private final CustomerServicePromptService customerServicePromptService;

    public PromptDemoController(WeatherPromptService weatherPromptService,
                               CustomerServicePromptService customerServicePromptService) {
        this.weatherPromptService = weatherPromptService;
        this.customerServicePromptService = customerServicePromptService;
    }

    @GetMapping("/weather")
    public String getWeatherInfo(@RequestParam String city, @RequestParam String date) {
        return weatherPromptService.getWeatherInfo(city, date);
    }

    @PostMapping("/customer-service")
    public String handleCustomerInquiry(@RequestBody CustomerInquiryRequest request) {
        return customerServicePromptService.handleCustomerInquiry(
            request.customerMessage(), 
            request.customerName(), 
            request.orderHistory()
        );
    }

    @PostMapping("/order-status")
    public String generateOrderStatusUpdate(@RequestBody OrderStatusRequest request) {
        return customerServicePromptService.generateOrderStatusUpdate(
            request.orderNumber(), 
            request.status(), 
            request.estimatedDelivery()
        );
    }

    public record CustomerInquiryRequest(String customerMessage, String customerName, String orderHistory) {}
    
    public record OrderStatusRequest(String orderNumber, String status, String estimatedDelivery) {}
} 