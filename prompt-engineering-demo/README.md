# Spring AI Prompt 工程演示项目

这是《从零构建智能应用——Spring AI 实战指南》第二篇的配套代码示例，展示了如何使用 Spring AI 进行 Prompt 工程实践。

## 项目结构

```
prompt-engineering-demo/
├── src/main/java/com/example/promptengineering/
│   ├── PromptEngineeringDemoApplication.java    # 主应用类
│   ├── controller/
│   │   └── PromptDemoController.java           # REST API 控制器
│   └── service/
│       ├── WeatherPromptService.java           # 天气查询 Prompt 服务
│       └── CustomerServicePromptService.java   # 客服 Prompt 服务
├── src/main/resources/
│   └── application.properties                  # 应用配置
└── pom.xml                                     # Maven 配置
```

## 快速开始

### 1. 环境准备

- JDK 21 或更高版本
- Maven 3.6 或更高版本
- DeepSeek API Key

### 2. 配置 API Key

在 `src/main/resources/application.properties` 中配置你的 DeepSeek API Key：

```properties
spring.ai.deepseek.api-key=your_api_key_here
```

或者通过环境变量设置：

```bash
export DEEPSEEK_API_KEY=your_api_key_here
```

### 3. 运行应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 使用示例

### 1. 天气查询 Prompt

**请求：**
```bash
curl "http://localhost:8080/api/prompt/weather?city=北京&date=2024-01-15"
```

**响应：**
```
您好！根据您查询的信息，北京在2024年1月15日的天气情况如下：

1. 天气状况描述：晴天，空气质量良好
2. 温度范围：-5°C 到 8°C
3. 出行建议：天气晴朗，适合外出活动，但注意保暖
4. 着装建议：建议穿着厚外套、围巾和手套，注意防寒保暖

祝您出行愉快！
```

### 2. 客服咨询 Prompt

**请求：**
```bash
curl -X POST "http://localhost:8080/api/prompt/customer-service" \
  -H "Content-Type: application/json" \
  -d '{
    "customerMessage": "我的订单什么时候能到？",
    "customerName": "张三",
    "orderHistory": "订单号：12345，状态：已发货，预计送达：2024-01-16"
  }'
```

**响应：**
```
您好，张三！

根据您的订单信息，订单号12345已经发货，预计在2024年1月16日送达。请您保持电话畅通，以便快递员联系您。

还有什么可以帮您的吗？
```

### 3. 订单状态更新 Prompt

**请求：**
```bash
curl -X POST "http://localhost:8080/api/prompt/order-status" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "12345",
    "status": "已发货",
    "estimatedDelivery": "2024-01-16"
  }'
```

**响应：**
```
尊敬的客户，您的订单12345已经发货，预计2024年1月16日送达。请保持电话畅通，注意查收包裹。如有疑问，请联系客服。
```

## Prompt 工程要点

### 1. 模板设计原则

- **明确角色定位**：为 AI 设定明确的身份和职责
- **提供具体指令**：详细说明期望的输出格式和要求
- **包含上下文信息**：提供必要的背景信息帮助 AI 理解
- **设置约束条件**：明确什么可以做，什么不能做

### 2. 动态参数绑定

使用 `PromptTemplate` 类实现动态参数绑定：

```java
String promptTemplate = "城市：{city}\n日期：{date}";
PromptTemplate template = new PromptTemplate(promptTemplate);
String filledPrompt = template.render(Map.of("city", city, "date", date));
```

### 3. 常见陷阱避免

- **指令不明确**：提供具体的、可操作的指令
- **上下文信息不足**：为 AI 提供足够的背景信息
- **输出格式不明确**：指定期望的输出格式和结构

## 扩展练习

1. **添加更多 Prompt 模板**：尝试创建代码审查、文档生成等 Prompt
2. **优化输出格式**：实验不同的输出格式，如 JSON、表格等
3. **参数验证**：添加输入参数的验证逻辑
4. **错误处理**：完善异常处理和错误提示

## 相关资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Prompt 工程最佳实践](https://www.promptingguide.ai/)
- [OpenAI Prompt 工程指南](https://platform.openai.com/docs/guides/prompt-engineering) 