# Spring AI 实战指南：工具调用（Tool Calling）：赋予 AI 外部能力

## 5.1 Tool Calling 的设计思想：为什么 AI 需要"工具"？

想象一下，如果我们把 AI 大模型比作一个聪明的助手，那么这个助手虽然知识渊博，但它有一个天然的局限性：**它无法获取实时信息，也无法执行实际的操作**。

比如，当你问它"现在几点了？"，它无法回答，因为它没有时钟；当你让它"帮我发送一封邮件"，它也做不到，因为它没有邮件系统的权限。

这就是 **Tool Calling（工具调用）** 技术要解决的核心问题：**让 AI 能够调用外部工具和服务，获得"手脚"和"眼睛"**。

### 工具调用的工作原理

Tool Calling 的工作流程可以用一个简单的对话来理解：

**用户：** "现在北京的天气怎么样？"

**AI（内心独白）：** "我需要获取实时天气信息，我应该调用天气查询工具。"

**AI 调用：** `getWeather(city="北京")`

**工具返回：** `{"temperature": 15, "weather": "晴天", "humidity": 45}`

**AI 回复：** "北京现在的天气是晴天，气温 15°C，湿度 45%。"

在这个过程中，AI 模型本身并没有获取天气数据的能力，但它知道应该调用哪个工具，以及如何使用工具返回的数据来生成最终答案。

### Spring AI 中的工具调用架构

Spring AI 将工具调用分为两大类：

1. **信息检索工具**：用于获取外部数据（如天气、股价、新闻等）
2. **执行操作工具**：用于执行具体动作（如发送邮件、预订机票、创建文件等）

其核心设计哲学是：**AI 模型只负责决策，真正的执行由应用程序控制**。这种设计确保了安全性和可控性。

## 5.2 @Tool 注解：让普通 Java 方法变成 AI 工具

Spring AI 提供了一个极其优雅的方式来定义工具：使用 `@Tool` 注解。任何用这个注解标记的方法，都可以被 AI 模型调用。

### 基础示例：时间工具

让我们先创建一个最简单的工具——获取当前时间：

```java
import org.springframework.ai.tool.annotation.Tool;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTools {
    
    @Tool(description = "获取当前的日期和时间，格式为 yyyy-MM-dd HH:mm:ss")
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Tool(description = "获取指定时区的当前时间")
    public String getCurrentDateTimeInZone(String timeZone) {
        try {
            java.time.ZoneId zone = java.time.ZoneId.of(timeZone);
            return LocalDateTime.now(zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) 
                + " (" + timeZone + ")";
        } catch (Exception e) {
            return "无效的时区: " + timeZone;
        }
    }
}
```

**关键要点：**

- `@Tool` 注解的 `description` 参数非常重要，它告诉 AI 这个工具的功能。描述越清晰，AI 选择工具越准确。
- 工具方法应该返回字符串或可序列化的对象。
- 错误处理要优雅，返回有意义的错误信息而不是抛出异常。

### 计算器工具：处理参数传递

```java
import org.springframework.ai.tool.annotation.Tool;

public class CalculatorTools {
    
    @Tool(description = "执行基本的数学运算：加法、减法、乘法、除法")
    public String calculate(String expression) {
        try {
            // 简单的表达式计算（实际项目中建议使用专业的表达式计算库）
            String cleanExpression = expression.replace(" ", "");
            
            if (cleanExpression.contains("+")) {
                String[] parts = cleanExpression.split("\\+");
                double result = Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
                return String.format("%.2f", result);
            } else if (cleanExpression.contains("-")) {
                String[] parts = cleanExpression.split("-");
                double result = Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
                return String.format("%.2f", result);
            } else if (cleanExpression.contains("*")) {
                String[] parts = cleanExpression.split("\\*");
                double result = Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
                return String.format("%.2f", result);
            } else if (cleanExpression.contains("/")) {
                String[] parts = cleanExpression.split("/");
                double divisor = Double.parseDouble(parts[1]);
                if (divisor == 0) {
                    return "错误：除数不能为零";
                }
                double result = Double.parseDouble(parts[0]) / divisor;
                return String.format("%.2f", result);
            }
            
            return "不支持的运算表达式: " + expression;
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }
    
    @Tool(description = "计算两个数字的幂运算，返回 base 的 exponent 次方")
    public String power(double base, double exponent) {
        double result = Math.pow(base, exponent);
        return String.format("%.2f", result);
    }
}
```

## 5.3 外部 API 集成：天气查询工具

现在让我们创建一个更实用的工具：天气查询。这个例子展示了如何集成外部 REST API。

### 天气服务工具

```java
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class WeatherTools {
    
    private final RestTemplate restTemplate;
    
    public WeatherTools() {
        this.restTemplate = new RestTemplate();
    }
    
    @Tool(description = "查询指定城市的当前天气情况，包括温度、天气状况、湿度等信息")
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
    
    // 模拟天气API调用（实际使用时替换为真实API）
    private WeatherResponse simulateWeatherAPI(String city) {
        // 简单的城市天气模拟
        return switch (city.toLowerCase()) {
            case "北京" -> new WeatherResponse("北京", "晴天", 15, 45, 8.5);
            case "上海" -> new WeatherResponse("上海", "多云", 18, 60, 12.0);
            case "广州" -> new WeatherResponse("广州", "小雨", 22, 80, 6.3);
            case "深圳" -> new WeatherResponse("深圳", "阴天", 20, 70, 9.2);
            default -> new WeatherResponse(city, "晴天", (int)(Math.random() * 30 + 5), 
                                         (int)(Math.random() * 50 + 30), Math.random() * 15 + 5);
        };
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
```

### 真实 API 集成示例

如果你想集成真实的天气 API，可以这样做：

```java
@Tool(description = "查询真实的天气信息")
public String getRealWeather(String city) {
    try {
        // 使用和风天气API（示例）
        String apiKey = "your-api-key";
        String url = String.format(
            "https://devapi.qweather.com/v7/weather/now?location=%s&key=%s",
            city, apiKey
        );
        
        // 调用真实API
        WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);
        
        if (response != null && "200".equals(response.code)) {
            WeatherData weather = response.now;
            return String.format("""
                城市：%s
                天气：%s  
                温度：%s°C
                体感温度：%s°C
                湿度：%s%%
                """, 
                city,
                weather.text,
                weather.temp,
                weather.feelsLike,
                weather.humidity
            );
        } else {
            return "无法获取天气信息";
        }
        
    } catch (Exception e) {
        return "天气查询失败：" + e.getMessage();
    }
}
```

## 5.4 工具注册与使用：将工具提供给 AI

定义好工具后，我们需要将它们注册到 ChatClient 中，让 AI 能够使用这些工具。

### 创建工具集成的 ChatClient

```java
package com.example.toolcalling.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ToolCallingChatService {
    
    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    private final CalculatorTools calculatorTools;
    private final WeatherTools weatherTools;
    
    public ToolCallingChatService(ChatModel chatModel, 
                                 DateTimeTools dateTimeTools,
                                 CalculatorTools calculatorTools,
                                 WeatherTools weatherTools) {
        this.dateTimeTools = dateTimeTools;
        this.calculatorTools = calculatorTools;
        this.weatherTools = weatherTools;
        
        // 创建支持工具调用的 ChatClient
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                    你是一个智能助手，可以使用多种工具来帮助用户。
                    你有以下工具可以使用：
                    1. 时间工具：获取当前时间和不同时区的时间
                    2. 计算器工具：执行数学运算
                    3. 天气工具：查询城市天气信息
                    
                    请根据用户的问题，智能选择合适的工具来提供准确的信息。
                    """)
                .build();
    }
    
    public String chatWithTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(dateTimeTools, calculatorTools, weatherTools)  // 注册所有工具
                .call()
                .content();
    }
    
    // 只使用特定工具的方法
    public String chatWithTimeTools(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(dateTimeTools)  // 只注册时间工具
                .call()
                .content();
    }
    
    public String chatWithCalculator(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(calculatorTools)  // 只注册计算器工具
                .call()
                .content();
    }
}
```

### 控制器层：暴露工具调用接口

```java
package com.example.toolcalling.controller;

import com.example.toolcalling.service.ToolCallingChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tools")
public class ToolCallingController {
    
    private final ToolCallingChatService chatService;
    
    public ToolCallingController(ToolCallingChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/chat")
    public Map<String, Object> chatWithTools(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        long startTime = System.currentTimeMillis();
        String response = chatService.chatWithTools(message);
        long duration = System.currentTimeMillis() - startTime;
        
        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "availableTools", "DateTime, Calculator, Weather",
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }
    
    @PostMapping("/calculator")
    public Map<String, Object> useCalculator(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.chatWithCalculator(message);
        
        return Map.of(
                "userMessage", message,
                "aiResponse", response,
                "toolType", "Calculator",
                "timestamp", System.currentTimeMillis()
        );
    }
    
    @PostMapping("/weather")
    public Map<String, Object> checkWeather(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        // 这里我们直接使用天气工具，展示工具的直接调用
        WeatherTools weatherTools = new WeatherTools();
        String city = extractCityFromMessage(message);
        
        if (city != null) {
            String weatherInfo = weatherTools.getWeather(city);
            return Map.of(
                    "userMessage", message,
                    "city", city,
                    "weatherInfo", weatherInfo,
                    "method", "Direct Tool Call",
                    "timestamp", System.currentTimeMillis()
            );
        } else {
            // 如果无法提取城市名，使用 AI 来理解和调用工具
            String response = chatService.chatWithTools(message);
            return Map.of(
                    "userMessage", message,
                    "aiResponse", response,
                    "method", "AI-Guided Tool Call",
                    "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    // 简单的城市名提取逻辑
    private String extractCityFromMessage(String message) {
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "武汉", "成都"};
        for (String city : cities) {
            if (message.contains(city)) {
                return city;
            }
        }
        return null;
    }
}
```

## 5.5 高级工具调用：文件操作与数据库查询

### 文件操作工具

```java
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.List;

@Component  
public class FileOperationTools {
    
    private final String WORK_DIRECTORY = "./workspace/";
    
    public FileOperationTools() {
        // 确保工作目录存在
        try {
            Files.createDirectories(Paths.get(WORK_DIRECTORY));
        } catch (IOException e) {
            System.err.println("无法创建工作目录: " + e.getMessage());
        }
    }
    
    @Tool(description = "创建一个新文件并写入内容")
    public String createFile(String fileName, String content) {
        try {
            Path filePath = Paths.get(WORK_DIRECTORY + fileName);
            Files.write(filePath, content.getBytes());
            return "文件 " + fileName + " 创建成功，内容长度：" + content.length() + " 字符";
        } catch (IOException e) {
            return "创建文件失败：" + e.getMessage();
        }
    }
    
    @Tool(description = "读取文件内容")
    public String readFile(String fileName) {
        try {
            Path filePath = Paths.get(WORK_DIRECTORY + fileName);
            if (!Files.exists(filePath)) {
                return "文件 " + fileName + " 不存在";
            }
            String content = Files.readString(filePath);
            return "文件内容：\n" + content;
        } catch (IOException e) {
            return "读取文件失败：" + e.getMessage();
        }
    }
    
    @Tool(description = "列出工作目录中的所有文件")
    public String listFiles() {
        try {
            List<String> files = Files.list(Paths.get(WORK_DIRECTORY))
                    .map(path -> path.getFileName().toString())
                    .toList();
            
            if (files.isEmpty()) {
                return "工作目录为空";
            }
            
            return "文件列表：\n" + String.join("\n", files);
        } catch (IOException e) {
            return "列出文件失败：" + e.getMessage();
        }
    }
}
```

### 数据库查询工具

```java
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseQueryTools {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DatabaseQueryTools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Tool(description = "查询用户表中的用户信息，可以按姓名或ID查询")
    public String queryUser(String searchTerm) {
        try {
            String sql;
            Object[] params;
            
            // 判断是按ID还是按姓名查询
            if (searchTerm.matches("\\d+")) {
                sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
                params = new Object[]{Integer.parseInt(searchTerm)};
            } else {
                sql = "SELECT id, name, email, created_at FROM users WHERE name LIKE ?";
                params = new Object[]{"%" + searchTerm + "%"};
            }
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
            
            if (results.isEmpty()) {
                return "未找到匹配的用户：" + searchTerm;
            }
            
            StringBuilder sb = new StringBuilder("查询结果：\n");
            for (Map<String, Object> row : results) {
                sb.append(String.format("ID: %s, 姓名: %s, 邮箱: %s, 创建时间: %s\n",
                        row.get("id"), row.get("name"), row.get("email"), row.get("created_at")));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            return "数据库查询失败：" + e.getMessage();
        }
    }
    
    @Tool(description = "获取用户总数统计")
    public String getUserCount() {
        try {
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            return "系统中共有 " + count + " 个用户";
        } catch (Exception e) {
            return "获取用户数量失败：" + e.getMessage();
        }
    }
}
```

## 5.6 完整项目配置与测试

### Maven 依赖配置

创建一个完整的工具调用示例项目，`pom.xml` 配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.7</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>tool-calling-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>tool-calling-demo</name>
    <description>Spring AI Tool Calling Demo</description>
    
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-deepseek</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 应用配置

```properties
# application.properties
spring.application.name=tool-calling-demo

# DeepSeek 配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7

# H2 数据库配置（用于演示数据库工具）
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.sql.init.mode=always

# 日志配置
logging.level.org.springframework.ai=DEBUG
```

### 数据库初始化脚本

创建 `src/main/resources/data.sql`：

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO users (name, email) VALUES 
('张三', 'zhangsan@example.com'),
('李四', 'lisi@example.com'),
('王五', 'wangwu@example.com'),
('赵六', 'zhaoliu@example.com');
```

### 启动类配置

```java
package com.example.toolcalling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolCallingDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToolCallingDemoApplication.class, args);
    }
}
```

### 配置类：注册工具为 Bean

```java
package com.example.toolcalling.config;

import com.example.toolcalling.tools.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfiguration {
    
    @Bean
    public DateTimeTools dateTimeTools() {
        return new DateTimeTools();
    }
    
    @Bean  
    public CalculatorTools calculatorTools() {
        return new CalculatorTools();
    }
    
    @Bean
    public WeatherTools weatherTools() {
        return new WeatherTools();
    }
    
    @Bean
    public FileOperationTools fileOperationTools() {
        return new FileOperationTools();
    }
}
```

## 5.7 工具调用实战测试

### 创建测试脚本

创建 `test_tools.sh`：

```bash
#!/bin/bash

echo "🧪 工具调用功能测试"
echo "===================="

BASE_URL="http://localhost:8080/api/tools"

# 测试时间工具
echo "📅 测试时间查询..."
curl -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点了？"}' | jq .

echo -e "\n"

# 测试计算器工具  
echo "🧮 测试计算器功能..."
curl -X POST $BASE_URL/calculator \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我计算 123 + 456"}' | jq .

echo -e "\n"

# 测试天气工具
echo "🌤️ 测试天气查询..."
curl -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "北京今天天气怎么样？"}' | jq .

echo -e "\n"

# 测试复合功能
echo "🔄 测试复合工具调用..."
curl -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点了？然后帮我算一下 100 * 50 等于多少？最后告诉我上海的天气"}' | jq .

echo -e "\n"

# 测试文件操作
echo "📁 测试文件操作..."
curl -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我创建一个名为 hello.txt 的文件，内容是：Hello Spring AI Tools!"}' | jq .

echo -e "\n"

echo "✅ 测试完成！"
```

### 运行和测试

```bash
# 启动应用
./mvnw spring-boot:run

# 运行测试（新终端）
chmod +x test_tools.sh
./test_tools.sh
```

## 5.8 工具调用的最佳实践

### 1. 工具设计原则

**单一职责：** 每个工具应该只负责一种特定的功能。

```java
// ✅ 好的设计
@Tool(description = "获取当前时间")
public String getCurrentTime() { ... }

@Tool(description = "获取指定时区的时间") 
public String getTimeInZone(String timezone) { ... }

// ❌ 不好的设计
@Tool(description = "处理时间相关的各种操作")
public String timeOperations(String operation, String param1, String param2) { ... }
```

**清晰的描述：** 工具描述要详细，让 AI 能够准确理解何时使用。

```java
// ✅ 好的描述
@Tool(description = "查询指定城市的实时天气信息，包括温度、湿度、天气状况等")
public String getWeather(String city) { ... }

// ❌ 模糊的描述  
@Tool(description = "天气")
public String weather(String input) { ... }
```

### 2. 错误处理策略

工具方法应该优雅地处理错误，返回有意义的错误信息：

```java
@Tool(description = "查询股票价格")
public String getStockPrice(String symbol) {
    try {
        // 调用股票API
        return callStockAPI(symbol);
    } catch (ApiException e) {
        return "无法获取股票信息：API 服务暂时不可用";
    } catch (IllegalArgumentException e) {
        return "无效的股票代码：" + symbol;
    } catch (Exception e) {
        return "查询股票价格时发生错误，请稍后重试";
    }
}
```

### 3. 性能优化

**缓存常用结果：**

```java
@Service
public class WeatherTools {
    
    @Cacheable(value = "weather", key = "#city")
    @Tool(description = "查询城市天气")
    public String getWeather(String city) {
        // 天气API调用逻辑
    }
}
```

**异步处理长时间操作：**

```java
@Tool(description = "生成报告（异步操作）")
public String generateReport(String reportType) {
    // 启动异步任务
    CompletableFuture.runAsync(() -> {
        // 长时间的报告生成逻辑
    });
    
    return "报告生成任务已启动，请稍后查看结果";
}
```

### 4. 安全考虑

**输入验证：**

```java
@Tool(description = "执行系统命令")
public String executeCommand(String command) {
    // 白名单验证
    List<String> allowedCommands = Arrays.asList("ls", "pwd", "date");
    if (!allowedCommands.contains(command)) {
        return "不允许执行的命令：" + command;
    }
    
    // 执行安全的命令
    return executeSystemCommand(command);
}
```

**权限控制：**

```java
@Tool(description = "删除文件")
public String deleteFile(String fileName, String userRole) {
    if (!"admin".equals(userRole)) {
        return "权限不足：只有管理员可以删除文件";
    }
    
    // 执行删除操作
}
```

## 5.9 小结：工具调用让 AI 更强大

通过本篇文章，我们学习了：

1. **工具调用的核心概念**：让 AI 能够调用外部服务和执行实际操作
2. **@Tool 注解的使用**：简单优雅地定义 AI 可调用的工具
3. **实际工具的实现**：时间、计算器、天气、文件操作、数据库查询等
4. **工具注册与使用**：在 ChatClient 中集成多种工具
5. **最佳实践**：工具设计、错误处理、性能优化、安全考虑

工具调用是 Spring AI 最强大的特性之一，它真正实现了让 AI "手脚并用"的能力。通过合理设计和使用工具，我们可以构建出真正有用的 AI 应用，不仅能对话，还能执行实际的业务操作。

在下一篇文章中，我们将探讨**模型上下文协议（MCP）**，了解如何构建可互操作的 AI 工具生态系统。
