# Chat Memory Demo - Spring AI 记忆对话示例

这是一个展示 Spring AI Chat Memory 机制的完整示例项目，演示了如何让 AI 拥有"记忆"与上下文感知能力。

## 功能特性

- **内置记忆管理**: 使用 Spring AI 的自动配置 ChatMemory
- **多种使用方式**: Advisor 方式和直接 ChatMemory 操作
- **智能客服场景**: 完整的客服机器人实现
- **记忆策略配置**: 支持不同窗口大小的记忆策略
- **RESTful API**: 完整的 REST API 接口
- **测试脚本**: 自动化测试脚本

## 项目结构

```
chat-memory-demo/
├── src/main/java/com/example/chatmemory/
│   ├── ChatMemoryDemoApplication.java      # 主应用类
│   ├── config/
│   │   └── ChatMemoryConfig.java           # 配置类
│   ├── controller/
│   │   └── ChatMemoryController.java       # REST 控制器
│   └── service/
│       ├── MemorizedChatService.java       # Advisor 方式服务
│       ├── DirectMemoryChatService.java    # 直接操作服务
│       ├── IntelligentCustomerServiceBot.java # 智能客服
│       ├── ConversationIdService.java      # 对话ID管理
│       ├── MemoryCleanupService.java       # 记忆清理
│       └── MultiMemoryService.java         # 多记忆策略
├── src/main/resources/
│   └── application.properties              # 应用配置
├── run.sh                                  # 启动脚本
├── test_memory.sh                          # 测试脚本
└── pom.xml                                 # Maven 配置
```

## 快速开始

### 1. 环境要求

- Java 21+
- Maven 3.6+
- DeepSeek API Key

### 2. 设置 API 密钥

```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key
```

### 3. 启动应用

```bash
# 给脚本执行权限
chmod +x run.sh

# 启动应用
./run.sh
```

### 4. 运行测试

```bash
# 给脚本执行权限
chmod +x test_memory.sh

# 运行测试
./test_memory.sh
```

## API 接口

### 基础对话接口

#### 1. 创建新对话
```bash
POST /api/chat/new
```

#### 2. 使用 Advisor 方式对话
```bash
POST /api/chat/{conversationId}/advisor
Content-Type: application/json

{
  "message": "你好，我叫张三"
}
```

#### 3. 直接使用 ChatMemory 对话
```bash
POST /api/chat/{conversationId}/direct
Content-Type: application/json

{
  "message": "我刚才说的名字是什么？"
}
```

#### 4. 获取对话历史
```bash
GET /api/chat/{conversationId}/history?count=10
```

#### 5. 清除对话历史
```bash
DELETE /api/chat/{conversationId}
```

### 客服场景接口

#### 1. 首次客服咨询
```bash
POST /api/chat/customer-service/new
Content-Type: application/json

{
  "customerId": "cust_001",
  "customerName": "李四",
  "inquiry": "我的订单什么时候发货？"
}
```

#### 2. 继续客服对话
```bash
POST /api/chat/customer-service/{customerId}/continue
Content-Type: application/json

{
  "message": "我刚才问的订单有什么进展吗？"
}
```

#### 3. 健康检查
```bash
GET /api/chat/health
```

## 核心概念

### 1. MessageChatMemoryAdvisor

最简单的使用方式，自动管理对话记忆：

```java
ChatClient chatClient = ChatClient.builder(chatModel)
    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
    .build();

String response = chatClient.prompt()
    .user(message)
    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
    .call()
    .content();
```

### 2. 直接操作 ChatMemory

提供更精细的控制：

```java
// 获取历史消息
List<Message> history = chatMemory.get(conversationId);

// 添加新消息
chatMemory.add(conversationId, List.of(
    new UserMessage(userMessage),
    new AssistantMessage(assistantResponse)
));

// 清除对话
chatMemory.clear(conversationId);
```

### 3. 记忆窗口配置

可以配置不同大小的记忆窗口：

```java
ChatMemory chatMemory = MessageWindowChatMemory.builder()
    .maxMessages(30)  // 最多保留30条消息
    .build();
```

## 配置说明

### application.properties

```properties
# DeepSeek AI 配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7
spring.ai.deepseek.chat.options.max-tokens=1500

# 日志配置
logging.level.com.example.chatmemory=DEBUG
logging.level.org.springframework.ai=INFO

# 服务器配置
server.port=8080
```

## 使用场景

### 1. 智能客服
- 记住客户信息和历史问题
- 提供连贯的服务体验
- 支持多轮对话

### 2. 个人助手
- 记住用户偏好和历史对话
- 基于上下文提供个性化回答
- 长期记忆管理

### 3. 在线教育
- 记住学习进度和问题
- 提供个性化的学习建议
- 跟踪学习历史

## 最佳实践

1. **对话ID管理**: 使用规范的对话ID格式，便于追踪和管理
2. **记忆窗口设置**: 根据应用场景合理设置记忆窗口大小
3. **定期清理**: 实现定期清理机制，避免内存泄漏
4. **错误处理**: 添加适当的错误处理和降级策略

## 故障排除

### 1. API 密钥错误
确保正确设置了 DEEPSEEK_API_KEY 环境变量。

### 2. 应用启动失败
检查 Java 版本是否为 21+，以及 Maven 是否正确安装。

### 3. 测试失败
确保应用已完全启动，等待约 10 秒后再运行测试脚本。

## 扩展功能

可以基于此示例扩展以下功能：

1. **持久化存储**: 实现基于数据库的 ChatMemoryRepository
2. **分布式部署**: 支持 Redis 等分布式存储
3. **记忆压缩**: 实现智能的记忆压缩策略
4. **多模态记忆**: 支持图像、音频等多模态内容记忆

## 相关文档

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [DeepSeek API 文档](https://platform.deepseek.com/api-docs/)
- [Chat Memory 详细教程](../doc4.md)

## 许可证

MIT License 