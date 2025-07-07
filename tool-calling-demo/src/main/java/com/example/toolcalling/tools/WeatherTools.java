package com.example.toolcalling.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherTools {

    private final RestTemplate restTemplate;

    public WeatherTools() {
        this.restTemplate = new RestTemplate();
    }

    @Tool(description = "查询指定城市的当前天气情况，包括温度、天气状况、湿度等信息")
    @Cacheable(value = "weather", key = "#city")
    public String getWeather(String city) {
        try {
            // 这里使用模拟的天气数据，实际项目中应该调用真实的天气API
            // 例如 OpenWeatherMap, 和风天气等
            
            // 模拟调用天气API
            WeatherResponse weather = simulateWeatherAPI(city);
            
            return String.format("""
                城市：%s
                天气：%s
                温度：%d°C
                湿度：%d%%
                风速：%.1f km/h
                """, 
                weather.city,
                weather.weather,
                weather.temperature,
                weather.humidity,
                weather.windSpeed
            );
            
        } catch (Exception e) {
            return "无法获取 " + city + " 的天气信息：" + e.getMessage();
        }
    }

    @Tool(description = "获取多个城市的天气对比信息")
    public String compareWeather(String city1, String city2) {
        String weather1 = getWeather(city1);
        String weather2 = getWeather(city2);
        
        return String.format("""
            === 天气对比 ===
            
            %s
            
            ---
            
            %s
            """, weather1, weather2);
    }

    @Tool(description = "查询城市的温度范围建议，给出穿衣指导")
    public String getClothingAdvice(String city) {
        WeatherResponse weather = simulateWeatherAPI(city);
        
        String advice;
        if (weather.temperature >= 25) {
            advice = "建议穿轻薄的短袖、短裤，注意防晒";
        } else if (weather.temperature >= 15) {
            advice = "建议穿长袖衣物，可以带一件薄外套";
        } else if (weather.temperature >= 5) {
            advice = "建议穿厚外套，注意保暖";
        } else {
            advice = "建议穿厚重的冬装，戴帽子和手套";
        }
        
        return String.format("%s的天气是%s，温度%d°C。%s", 
                city, weather.weather, weather.temperature, advice);
    }

    // 模拟天气API调用（实际使用时替换为真实API）
    private WeatherResponse simulateWeatherAPI(String city) {
        // 简单的城市天气模拟
        return switch (city.toLowerCase()) {
            case "北京" -> new WeatherResponse("北京", "晴天", 15, 45, 8.5);
            case "上海" -> new WeatherResponse("上海", "多云", 18, 60, 12.0);
            case "广州" -> new WeatherResponse("广州", "小雨", 22, 80, 6.3);
            case "深圳" -> new WeatherResponse("深圳", "阴天", 20, 70, 9.2);
            case "杭州" -> new WeatherResponse("杭州", "晴天", 16, 55, 7.8);
            case "南京" -> new WeatherResponse("南京", "多云", 14, 65, 9.5);
            case "武汉" -> new WeatherResponse("武汉", "阴天", 17, 75, 6.2);
            case "成都" -> new WeatherResponse("成都", "阴天", 19, 80, 4.1);
            default -> new WeatherResponse(city, getRandomWeather(), 
                                         (int)(Math.random() * 30 + 5), 
                                         (int)(Math.random() * 50 + 30), 
                                         Math.random() * 15 + 5);
        };
    }

    private String getRandomWeather() {
        String[] weathers = {"晴天", "多云", "阴天", "小雨", "中雨", "雪天"};
        return weathers[(int)(Math.random() * weathers.length)];
    }

    // 天气响应数据类
    public static class WeatherResponse {
        public String city;
        public String weather;
        public int temperature;
        public int humidity;
        public double windSpeed;
        
        public WeatherResponse(String city, String weather, int temperature, int humidity, double windSpeed) {
            this.city = city;
            this.weather = weather;
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }
    }
} 