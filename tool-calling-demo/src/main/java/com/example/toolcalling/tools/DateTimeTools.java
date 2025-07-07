package com.example.toolcalling.tools;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class DateTimeTools {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(description = "获取当前的日期和时间，格式为 yyyy-MM-dd HH:mm:ss")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(FORMATTER);
    }

    @Tool(description = "获取指定时区的当前时间")
    public String getCurrentDateTimeInZone(String timeZone) {
        try {
            ZoneId zone = ZoneId.of(timeZone);
            return LocalDateTime.now(zone).format(FORMATTER) + " (" + timeZone + ")";
        } catch (Exception e) {
            return "无效的时区: " + timeZone + "。请使用如 'Asia/Shanghai', 'America/New_York', 'Europe/London' 等标准时区格式。";
        }
    }

    @Tool(description = "获取当前的日期，格式为 yyyy-MM-dd")
    public String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Tool(description = "获取当前的时间，格式为 HH:mm:ss")
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Tool(description = "获取当前是星期几")
    public String getCurrentDayOfWeek() {
        return "今天是" + LocalDateTime.now().getDayOfWeek().toString();
    }
} 