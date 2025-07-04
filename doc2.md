# Spring AI 实战指南：Prompt 工程基础

## Prompt 工程基础：与 AI 高效对话的艺术

我们已经成功搭建了第一个 Spring AI 应用，体验了与 AI 对话的奇妙感觉。今天，我们将深入探讨一个看似简单却极其重要的主题：**Prompt 工程**。

如果你曾经与 ChatGPT 对话过，你可能会发现一个有趣的现象：同样的问题，用不同的方式表达，得到的答案质量可能天差地别。这就是 Prompt 工程的魅力所在——它不仅仅是一门技术，更是一门艺术。

## 2.1 环境准备与项目搭建

在开始学习 Prompt 工程之前，让我们先搭建一个完整的演示项目。本项目使用 DeepSeek AI 模型，它是一个高质量且成本友好的选择。

### 项目依赖配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.7</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>prompt-engineering-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>prompt-engineering-demo</name>
    <description>Spring AI Prompt Engineering Demo</description>
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

在 `src/main/resources/application.properties` 中配置 DeepSeek API：

```properties
spring.application.name=prompt-engineering-demo

# DeepSeek AI 配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7
```

**重要提醒：** 请通过环境变量设置你的 DeepSeek API Key：
```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key_here
```

### 2.2 Prompt 的组成部分：理解 AI 的"语言"

在开始编写代码之前，让我们先理解 Prompt 的基本结构。一个完整的 Prompt 通常包含以下几个核心要素：

#### 指令（Instructions）
这是 Prompt 的核心部分，告诉 AI 你想要它做什么。比如：
- "请帮我写一首关于春天的诗"
- "请分析这段代码的性能问题"
- "请将以下文本翻译成英文"

#### 上下文（Context）
为 AI 提供必要的背景信息，帮助它更好地理解任务。比如：
- "你是一位经验丰富的 Java 开发者"
- "这是一段 Spring Boot 应用的代码"
- "目标用户是初学者"

#### 输入（Input）
具体的数据或内容，让 AI 基于此进行处理。比如：
- 具体的代码片段
- 需要翻译的文本
- 需要分析的数据

#### 输出格式（Output Format）
指定你希望 AI 以什么样的格式返回结果。比如：
- "请以 JSON 格式返回"
- "请用表格形式展示"
- "请分点列出"

### 2.3 动态 Prompt 模板设计：让 AI 更灵活

在实际应用中，我们很少会使用静态的 Prompt。更多时候，我们需要根据不同的输入动态生成 Prompt。Spring AI 提供了强大的 `PromptTemplate` 类来帮助我们实现这一点。

让我们通过一个天气查询的例子来演示：

```java
package com.example.promptengineering.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class WeatherPromptService {

    private final ChatClient chatClient;

    public WeatherPromptService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String getWeatherInfo(String city, String date) {
        // 定义 Prompt 模板
        String promptTemplate = """
            你是一位专业的天气预报员。请根据以下信息为用户提供天气建议：
            
            城市：{city}
            日期：{date}
            
            请提供以下信息：
            1. 天气状况描述
            2. 温度范围
            3. 出行建议
            4. 着装建议
            
            请用友好的语气回答，就像在和朋友聊天一样。
            """;

        // 创建 PromptTemplate 实例
        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        // 填充模板参数
        String filledPrompt = template.render(Map.of(
            "city", city,
            "date", date
        ));

        // 调用 AI 模型
        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }
}
```

**代码解析：**

1. **模板定义**：我们使用文本块（Text Blocks）定义了一个包含占位符的 Prompt 模板
2. **参数绑定**：使用 `{city}` 和 `{date}` 作为占位符
3. **动态填充**：通过 `template.render()` 方法，将实际的参数值填充到模板中
4. **AI 调用**：将填充后的 Prompt 发送给 AI 模型

### 2.4 避免常见陷阱：让 AI 更听话

在 Prompt 工程中，有一些常见的陷阱需要我们特别注意：

#### 陷阱 1：指令不明确
**错误示例：**
```
请帮我优化代码
```

**正确示例：**
```
请分析以下 Java 代码的性能问题，并提供具体的优化建议：
1. 时间复杂度分析
2. 内存使用优化
3. 具体的代码改进方案

代码：
{code}
```

#### 陷阱 2：上下文信息不足
**错误示例：**
```
请解释这段代码
```

**正确示例：**
```
你是一位资深的 Java 开发者，请为初学者解释以下 Spring Boot 代码的工作原理。
请从以下几个方面进行解释：
1. 注解的作用
2. 方法的执行流程
3. 相关的设计模式

代码：
{code}
```

#### 陷阱 3：输出格式不明确
**错误示例：**
```
请列出这个项目的优点和缺点
```

**正确示例：**
```
请分析这个项目的优缺点，并按以下格式返回：

**优点：**
- 优点1
- 优点2

**缺点：**
- 缺点1
- 缺点2

**改进建议：**
- 建议1
- 建议2
```

### 2.5 实战：构建一个智能客服 Prompt 系统

让我们通过一个完整的例子，来展示如何在实际项目中应用 Prompt 工程：

```java
package com.example.promptengineering.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerServicePromptService {

    private final ChatClient chatClient;

    public CustomerServicePromptService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String handleCustomerInquiry(String customerMessage, String customerName, String orderHistory) {
        // 定义客服 Prompt 模板
        String promptTemplate = "你是一位专业的客服代表，名字叫小明。请根据以下信息为客户提供帮助：\n\n" +
                "客户姓名：{customerName}\n" +
                "客户消息：{customerMessage}\n" +
                "历史订单：{orderHistory}\n\n" +
                "请遵循以下原则：\n" +
                "1. 保持友好、耐心的态度\n" +
                "2. 如果客户询问订单相关的问题，请参考历史订单信息\n" +
                "3. 如果不确定的信息，请建议客户联系人工客服\n" +
                "4. 回答要简洁明了，避免过于冗长\n" +
                "5. 如果客户表达不满，要表示理解和歉意\n\n" +
                "请以\"您好，{customerName}！\"开头，以\"还有什么可以帮您的吗？\"结尾。";

        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        String filledPrompt = template.render(Map.of(
            "customerName", customerName,
            "customerMessage", customerMessage,
            "orderHistory", orderHistory
        ));

        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }

    public String generateOrderStatusUpdate(String orderNumber, String status, String estimatedDelivery) {
        String promptTemplate = "请生成一条订单状态更新的通知消息：\n\n" +
                "订单号：{orderNumber}\n" +
                "当前状态：{status}\n" +
                "预计送达时间：{estimatedDelivery}\n\n" +
                "要求：\n" +
                "1. 语气要专业但友好\n" +
                "2. 包含所有必要信息\n" +
                "3. 如果状态是\"已发货\"，要提醒客户注意查收\n" +
                "4. 如果状态是\"配送中\"，要提醒客户保持电话畅通\n" +
                "5. 字数控制在 100 字以内";

        PromptTemplate template = new PromptTemplate(promptTemplate);
        
        String filledPrompt = template.render(Map.of(
            "orderNumber", orderNumber,
            "status", status,
            "estimatedDelivery", estimatedDelivery
        ));

        return chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();
    }
}
```

### 2.6 RESTful API 接口设计

为了让我们的 Prompt 服务更加实用，我们需要提供 RESTful API 接口：

```java
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
```

### 2.7 API 使用示例

现在我们可以通过 HTTP 请求来测试我们的 Prompt 工程实现：

#### 天气查询 API
```bash
curl "http://localhost:8080/api/prompt/weather?city=北京&date=2024-01-15"
```

**响应示例：**
```
您好！根据您查询的信息，北京在2024年1月15日的天气情况如下：

1. 天气状况描述：晴天，空气质量良好
2. 温度范围：-5°C 到 8°C
3. 出行建议：天气晴朗，适合外出活动，但注意保暖
4. 着装建议：建议穿着厚外套、围巾和手套，注意防寒保暖

祝您出行愉快！
```

#### 客服咨询 API
```bash
curl -X POST "http://localhost:8080/api/prompt/customer-service" \
  -H "Content-Type: application/json" \
  -d '{
    "customerMessage": "我的订单什么时候能到？",
    "customerName": "张三",
    "orderHistory": "订单号：12345，状态：已发货，预计送达：2024-01-16"
  }'
```

#### 订单状态更新 API
```bash
curl -X POST "http://localhost:8080/api/prompt/order-status" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "12345",
    "status": "已发货",
    "estimatedDelivery": "2024-01-16"
  }'
```

### 2.8 项目运行指南

1. **克隆项目**：
```bash
git clone https://github.com/yichuan4ai/spring-ai-examples.git
cd prompt-engineering-demo
```

2. **配置环境变量**：
```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key_here
```

3. **运行应用**：
```bash
mvn spring-boot:run
```

或者使用提供的脚本：
```bash
chmod +x run.sh
./run.sh
```

应用将在 `http://localhost:8080` 启动。

### 2.9 Prompt 工程的最佳实践

基于我的实践经验，我总结了一些 Prompt 工程的最佳实践：

#### 1. 明确角色定位
始终为 AI 设定一个明确的角色，比如：
- "你是一位经验丰富的 Java 开发者"
- "你是一位专业的客服代表"
- "你是一位数据分析师"

#### 2. 提供具体示例
在 Prompt 中包含具体的示例，帮助 AI 更好地理解你的期望：

```
请按照以下格式回答问题：

问题：什么是 Spring Boot？
回答：Spring Boot 是一个基于 Spring 框架的快速开发工具，它简化了 Spring 应用的配置和部署过程。

现在请回答：什么是 Spring AI？
```

#### 3. 分步骤指导
对于复杂的任务，将其分解为多个步骤：

```
请按以下步骤分析这段代码：
1. 首先，识别代码的主要功能
2. 然后，分析可能存在的性能问题
3. 最后，提供具体的优化建议
```

#### 4. 设置约束条件
明确告诉 AI 什么可以做，什么不能做：

```
请回答以下问题，但请注意：
- 不要提供具体的代码实现
- 只提供概念性的解释
- 如果涉及敏感信息，请说明无法提供
```

### 2.10 测试和优化你的 Prompt

Prompt 工程是一个迭代的过程，我们需要不断测试和优化：

```java
@Service
public class PromptTestingService {

    private final ChatClient chatClient;

    public PromptTestingService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public PromptTestResult testPrompt(String promptTemplate, Map<String, Object> parameters, String expectedOutput) {
        PromptTemplate template = new PromptTemplate(promptTemplate);
        String filledPrompt = template.render(parameters);
        
        String actualOutput = chatClient.prompt()
                .user(filledPrompt)
                .call()
                .content();

        return new PromptTestResult(filledPrompt, actualOutput, expectedOutput);
    }
}

public record PromptTestResult(String prompt, String actualOutput, String expectedOutput) {
    public boolean isSuccessful() {
        // 简单的相似度检查，实际项目中可以使用更复杂的算法
        return actualOutput.contains(expectedOutput) || expectedOutput.contains(actualOutput);
    }
}
```

### 2.11 总结

Prompt 工程是 AI 应用开发中的一门重要技能。通过合理设计 Prompt，我们可以：

1. **提高 AI 回答的质量**：明确的指令和上下文让 AI 更准确地理解我们的需求
2. **增强应用的灵活性**：动态模板让我们可以根据不同情况生成合适的 Prompt
3. **降低开发成本**：好的 Prompt 设计可以减少对 AI 模型的依赖，提高响应速度
4. **提升用户体验**：专业的 Prompt 设计让 AI 的回答更加人性化和有用

在下一篇文章中，我们将深入探讨模型集成与调优，学习如何选择最适合你需求的 AI 模型，以及如何通过参数调优来获得更好的效果。

记住，Prompt 工程没有标准答案，关键是要根据你的具体需求不断实践和优化。就像编程一样，熟能生巧，多写多练，你就能掌握这门艺术。

如果你有任何问题或想法，欢迎在评论区与我交流。我们下期再见！

---

**相关资源：**
- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Prompt 工程最佳实践指南](https://www.promptingguide.ai/)
- [DeepSeek API 文档](https://platform.deepseek.com/api-docs/)
- [项目源码地址](./prompt-engineering-demo/) 