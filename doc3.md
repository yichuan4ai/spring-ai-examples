# Spring AI 实战指南：模型集成与调优

## 模型集成与调优：选择最适合你的 AI 大脑

在前两篇文章中，我们已经成功搭建了第一个 Spring AI 应用，并学会了 Prompt 工程的基本技巧。今天，我们将进入一个更加核心的话题：**如何选择和调优最适合你业务需求的 AI 模型**。

如果说 Prompt 是与 AI 对话的语言，那么模型就是 AI 的"大脑"。不同的模型有着不同的"性格"：有的擅长文本对话，有的专精图像理解，有的则在代码生成方面表现出色。选择合适的模型，就像为不同的任务挑选合适的工具一样重要。

更重要的是，Spring AI 的设计理念让模型切换变得如同更换汽车引擎一样简单——你不需要重写整个应用，只需要调整配置文件即可。这种"模型即插即用"的特性，为我们的应用带来了极大的灵活性。

## 3.1 Spring AI 支持的模型类型全景

Spring AI 作为一个企业级的 AI 集成框架，支持三大类型的 AI 模型，每一类都有其独特的应用场景：

### 3.1.1 Chat 模型：AI 对话的核心

Chat 模型是我们最熟悉的类型，它专门用于文本对话、问答、内容生成等任务。在 Spring AI 中，所有的 Chat 模型都实现了统一的 `ChatModel` 接口。

**主要特点：**
- 理解自然语言并生成流畅的回复
- 支持多轮对话的上下文理解
- 可以完成各种文本处理任务（翻译、摘要、代码生成等）

**支持的提供商：**
- **OpenAI**：GPT-4, GPT-3.5 等
- **DeepSeek**：deepseek-chat, deepseek-coder 等
- **Anthropic**：Claude 系列
- **Azure OpenAI**：托管在 Azure 上的 OpenAI 模型
- **Ollama**：本地运行的开源模型

### 3.1.2 Embedding 模型：文本的"指纹识别器"

Embedding 模型将文本转换为数值向量，这些向量捕获了文本的语义信息。虽然它不能生成文本，但在检索、相似度计算、文档分类等场景中不可或缺。

**主要特点：**
- 将文本转换为高维向量表示
- 相似的文本会产生相似的向量
- 是构建 RAG（检索增强生成）系统的基础

**典型应用场景：**
- 文档相似度搜索
- 智能推荐系统
- 文本分类和聚类
- RAG 系统的向量检索

### 3.1.3 Image 模型：视觉理解的智能

Image 模型可以理解和生成图像，为我们的应用增加视觉智能。

**主要功能：**
- **图像理解**：分析图像内容，回答关于图像的问题
- **图像生成**：根据文本描述生成图像
- **多模态交互**：同时处理文本和图像输入

**支持的提供商：**
- **OpenAI**：GPT-4 Vision, DALL-E
- **StabilityAI**：Stable Diffusion 系列

## 3.2 创建模型集成演示项目

理论说得再多，不如实际动手感受。让我们创建一个全新的项目，演示如何在 Spring AI 中集成和使用不同类型的模型。

### 项目结构设计

```
model-integration-demo/
├── src/main/java/com/example/modelintegration/
│   ├── ModelIntegrationDemoApplication.java
│   ├── config/
│   │   ├── ChatClientFactory.java             # ChatClient 工厂类
│   │   ├── ChatClientFactoryConfig.java       # 工厂配置类
│   │   ├── EmbeddingConfig.java               # Embedding 配置类
│   │   ├── EmbeddingService.java              # Embedding 服务
│   │   └── SystemPrompts.java                 # 系统提示常量
│   ├── controller/
│   │   ├── ChatModelController.java           # Chat 模型演示
│   │   ├── EmbeddingModelController.java      # Embedding 模型演示
│   │   ├── ModelSelectionController.java      # 模型选择演示
│   │   └── ParameterTuningController.java     # 参数调优控制器
│   └── service/
│       ├── ModelSelectionService.java         # 模型选择服务
│       ├── ParameterTuningService.java        # 参数调优服务
│       └── ChatMetrics.java                   # 聊天指标监控
├── src/main/resources/
│   ├── application.properties                 # 基础配置
│   ├── application-openai.properties          # OpenAI 配置
│   ├── application-deepseek.properties        # DeepSeek 配置
│   └── application-ollama.properties          # 本地 Ollama 配置
├── test_apis.sh                               # API 测试脚本
├── run.sh                                     # 启动脚本
├── README.md                                  # 项目说明
├── CLAUDE.md                                  # Claude 使用指南
└── pom.xml
```

### Maven 依赖配置

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
    <artifactId>model-integration-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>model-integration-demo</name>
    <description>Spring AI Model Integration and Tuning Demo</description>
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0</spring-ai.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Actuator for monitoring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Micrometer for metrics -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Spring AI Chat 模型支持 -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-deepseek</artifactId>
        </dependency>

        <!-- 根据需求切换模型 -->
        <!--        <dependency>-->
        <!--            <groupId>org.springframework.ai</groupId>-->
        <!--            <artifactId>spring-ai-starter-model-ollama</artifactId>-->
        <!--        </dependency>-->
        <!--        <dependency>-->
        <!--            <groupId>org.springframework.ai</groupId>-->
        <!--            &lt;!&ndash; model OpenAI 已集成 Embedding&ndash;&gt;-->
        <!--            <artifactId>spring-ai-starter-model-openai</artifactId>-->
        <!--        </dependency>-->
        
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

## 3.3 本地模型 vs 云端模型：如何做出明智选择

在选择 AI 模型时，我们面临的第一个重要决策就是：使用本地模型还是云端模型？这个选择会影响到成本、性能、隐私、部署复杂度等多个方面。

### 3.3.1 云端模型的优势与劣势

**优势：**
1. **性能强劲**：云端模型通常参数量更大，性能更强
2. **开箱即用**：无需担心硬件要求，配置简单
3. **持续更新**：模型提供商会定期优化和更新模型
4. **多样化选择**：可以根据需求选择不同特色的模型

**劣势：**
1. **成本累积**：按使用量付费，大规模使用成本较高
2. **网络依赖**：需要稳定的网络连接
3. **数据隐私**：需要将数据发送到第三方服务器
4. **服务限制**：可能面临速率限制、服务中断等问题

### 3.3.2 本地模型的优势与劣势

**优势：**
1. **成本可控**：一次性部署成本，不按使用量付费
2. **数据安全**：数据不离开本地环境
3. **可定制性**：可以对模型进行微调和优化
4. **网络独立**：不依赖外部网络连接

**劣势：**
1. **硬件要求**：需要较高的 GPU 资源
2. **部署复杂**：需要更多的技术投入
3. **维护成本**：需要自行管理模型更新和优化
4. **性能限制**：受本地硬件限制，可能不如云端模型

### 3.3.3 决策框架：如何选择

| 使用场景           | 推荐方案     | 理由                                           |
|-------------------|-------------|-----------------------------------------------|
| **原型开发/学习**  | 云端模型     | 快速上手，专注业务逻辑                          |
| **小规模生产应用** | 云端模型     | 成本可控，运维简单                             |
| **大规模生产应用** | 混合方案     | 核心功能用本地，辅助功能用云端                   |
| **高隐私要求**     | 本地模型     | 数据不出本地环境                               |
| **低延迟要求**     | 本地模型     | 避免网络延迟                                   |
| **成本敏感**       | 本地模型     | 长期使用成本更低                               |

## 3.4 实战演示：多模型配置与切换

让我们通过实际代码演示如何在 Spring AI 中配置和切换不同的模型。

### 3.4.1 配置文件设计

首先，我们为不同的模型提供商创建独立的配置文件：

**`application.properties`（基础配置）：**
```properties
spring.application.name=model-integration-demo

# 激活特定的配置档案（可通过环境变量覆盖）
spring.profiles.active=deepseek

# 应用端口
server.port=8080

# 日志配置
logging.level.org.springframework.ai=DEBUG
logging.level.com.example.modelintegration=DEBUG

# Actuator 监控端点配置
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.info.env.enabled=true

# 应用信息
info.app.name=@project.name@
info.app.description=@project.description@
info.app.version=@project.version@
info.app.encoding=@project.build.sourceEncoding@
info.app.java.version=@java.version@
```

**`application-deepseek.properties`（DeepSeek 配置）：**
```properties
# DeepSeek Chat 模型配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7
spring.ai.deepseek.chat.options.max-tokens=1000

# 重试配置
spring.ai.deepseek.retry.max-attempts=3
spring.ai.deepseek.retry.backoff.delay=1s
spring.ai.deepseek.retry.backoff.multiplier=2

# 超时配置
spring.ai.deepseek.chat.options.timeout=30s

# 环境信息
info.model.provider=DeepSeek
info.model.name=deepseek-chat
info.model.type=Chat Model
```

**`application-openai.properties`（OpenAI 配置）：**
```properties
# OpenAI Chat 模型配置
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
spring.ai.openai.chat.options.max-tokens=1000

# OpenAI Embedding 模型配置
spring.ai.openai.embedding.enabled=true
spring.ai.openai.embedding.options.model=text-embedding-3-small

# 重试配置
spring.ai.openai.retry.max-attempts=3
spring.ai.openai.retry.backoff.delay=1s
spring.ai.openai.retry.backoff.multiplier=2

# 超时配置
spring.ai.openai.chat.options.timeout=30s

# 禁用其他模型配置（避免冲突）
spring.ai.deepseek.enabled=false
spring.ai.ollama.enabled=false

# 环境信息
info.model.provider=OpenAI
info.model.name=gpt-3.5-turbo
info.model.type=Chat Model + Embedding Model
```

**`application-ollama.properties`（本地 Ollama 配置）：**
```properties
# Ollama 本地模型配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama2
spring.ai.ollama.chat.options.temperature=0.7

# 环境信息
info.model.provider=Ollama (Local)
info.model.name=llama2
info.model.type=Local Chat Model
```

### 3.4.2 ChatClient 工厂类和配置

**`SystemPrompts.java`（系统提示常量）：**
```java
package com.example.modelintegration.config;

/**
 * 系统提示常量类
 * 集中管理不同模型和用途的系统提示
 */
public class SystemPrompts {

    public static final String PRIMARY =
            "你是一个专业的 AI 助手，擅长技术解答和代码分析。请用简洁明了的语言回答问题。";

    public static final String CODE = """
            你是一个资深的软件工程师，专门负责代码审查和分析。
            请用专业但通俗易懂的语言回答问题，并提供具体的代码示例。
            回答时请包含以下方面：
            1. 代码功能分析
            2. 潜在问题识别
            3. 改进建议
            4. 最佳实践推荐
            """;

    public static final String CREATIVE = """
            你是一个富有创造力的作家，擅长创意写作和内容创作。
            请用生动有趣的语言风格回答问题，适当使用比喻和形象化的表达。
            """;
}
```

**`ChatClientFactory.java`（ChatClient 工厂类）：**
```java
package com.example.modelintegration.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

/**
 * ChatClient 工厂类
 * 用于创建不同类型的 ChatClient 实例
 */
public class ChatClientFactory {

    private final String provider;
    private final ChatModel chatModel;

    private final ChatClient primaryChatClient;
    private final ChatClient codeChatClient;
    private final ChatClient creativeChatClient;

    public ChatClientFactory(String provider, ChatModel chatModel) {
        this.provider = provider;
        this.chatModel = chatModel;

        primaryChatClient = createChatClient(SystemPrompts.PRIMARY);
        codeChatClient = createChatClient(SystemPrompts.CODE);
        creativeChatClient = createChatClient(SystemPrompts.CREATIVE);
    }

    public ChatClient getPrimaryChatClient() {
        return primaryChatClient;
    }

    public ChatClient getCodeChatClient() {
        return codeChatClient;
    }

    public ChatClient getCreativeChatClient() {
        return creativeChatClient;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    private ChatClient createChatClient(String prompt) {
        return ChatClient.builder(chatModel)
                .defaultSystem(prompt)
                .build();
    }

    public String getProviderName() {
        return provider;
    }
}
```

**`ChatClientFactoryConfig.java`（工厂配置类）：**
```java
package com.example.modelintegration.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientFactoryConfig {

    @Value("${info.model.provider:Unknown}")
    private String provider;

    @Bean
    public ChatClientFactory chatClientFactory(ChatModel chatModel) {
        return new ChatClientFactory(provider, chatModel);
    }
}
```

### 3.4.3 Chat 模型演示控制器

```java
package com.example.modelintegration.controller;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatModelController {

    private final ChatClient primaryChatClient;
    private final ChatClient codeChatClient;
    private final ChatClient creativeChatClient;

    public ChatModelController(ChatClientFactory factory) {
        this.primaryChatClient = factory.getPrimaryChatClient();
        this.codeChatClient = factory.getCodeChatClient();
        this.creativeChatClient = factory.getCreativeChatClient();
    }

    /**
     * 基础对话接口
     */
    @PostMapping("/basic")
    public Map<String, Object> basicChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        ChatResponse response = primaryChatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        return Map.of(
                "response", response.getResult().getOutput().getText(),
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 代码分析专用接口
     */
    @PostMapping("/code-analysis")
    public Map<String, Object> analyzeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String question = request.get("question");

        String prompt = String.format("""
                请分析以下代码：
                
                \`\`\`
                %s
                \`\`\`
                
                问题：%s
                
                请提供详细的分析和建议。
                """, code, question);

        ChatResponse response = codeChatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "analysis", response.getResult().getOutput().getText(),
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "codeLength", code.length(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 创意写作接口
     */
    @PostMapping("/creative-writing")
    public Map<String, Object> creativeWriting(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String style = request.getOrDefault("style", "随意");

        String prompt = String.format("""
                请以'%s'风格，围绕'%s'这个主题进行创作。
                可以是诗歌、小故事、散文或其他创意形式。
                """, style, topic);

        ChatResponse response = creativeChatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "content", response.getResult().getOutput().getText(),
                "topic", topic,
                "style", style,
                "model", response.getMetadata().getModel(),
                "usage", response.getMetadata().getUsage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 简单的健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Chat Model Controller",
                "timestamp", System.currentTimeMillis()
        );
    }
}
```

## 3.5 参数调优：让 AI 更听话

不同的模型参数会显著影响 AI 的行为表现。理解这些参数的作用，是调优模型的关键。

### 3.5.1 核心参数详解

#### Temperature（温度）
**作用：** 控制输出的随机性和创造性
- **范围：** 0.0 - 2.0
- **效果：**
  - `0.0`：输出最确定，适合需要准确答案的场景
  - `0.7`：平衡创造性和一致性，适合大多数对话场景
  - `1.0`：较高创造性，适合内容创作
  - `2.0`：高度随机，适合实验性用途

#### Top P（核采样）
**作用：** 控制候选词汇的范围
- **范围：** 0.0 - 1.0
- **效果：**
  - `0.1`：只考虑概率最高的 10% 词汇，输出更集中
  - `0.9`：考虑概率最高的 90% 词汇，保持多样性

#### Max Tokens（最大令牌数）
**作用：** 限制输出的长度
- **建议：** 根据具体需求设置，避免不必要的成本

### 3.5.2 参数调优服务实现

```java
package com.example.modelintegration.service;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParameterTuningService {

    private final ChatModel chatModel;

    public ParameterTuningService(ChatClientFactory factory) {
        this.chatModel = factory.getChatModel();
    }

    /**
     * 测试不同 Temperature 值的效果
     */
    public List<Map<String, Object>> testTemperatureEffects(String prompt) {
        double[] temperatures = {0.0, 0.3, 0.7, 1.0, 1.5};
        List<Map<String, Object>> results = new ArrayList<>();

        for (double temperature : temperatures) {
            try {
                ChatClient client = ChatClient.builder(chatModel)
                        .defaultOptions(ChatOptions.builder()
                                .temperature(temperature)
                                .maxTokens(200)
                                .build())
                        .build();

                long startTime = System.currentTimeMillis();
                String response = client.prompt()
                        .user(prompt)
                        .call()
                        .content();
                long duration = System.currentTimeMillis() - startTime;

                results.add(Map.of(
                        "temperature", temperature,
                        "response", response,
                        "responseLength", response.length(),
                        "durationMs", duration,
                        "status", "success"
                ));
            } catch (Exception e) {
                results.add(Map.of(
                        "temperature", temperature,
                        "error", e.getMessage(),
                        "status", "failed"
                ));
            }
        }

        return results;
    }

    /**
     * 对比不同参数组合的效果
     */
    public Map<String, Object> compareParameterCombinations(String prompt) {
        Map<String, Object> results = new HashMap<>();

        // 保守配置：适合需要准确性的场景
        results.put("conservative", testConfiguration(prompt, 0.0, 0.1, 200, 
                "保守配置 - 适合需要准确答案的场景"));

        // 平衡配置：适合一般对话场景
        results.put("balanced", testConfiguration(prompt, 0.7, 0.9, 500,
                "平衡配置 - 适合日常对话场景"));

        // 创造性配置：适合内容创作场景
        results.put("creative", testConfiguration(prompt, 1.2, 0.95, 800,
                "创造性配置 - 适合内容创作场景"));

        return results;
    }

    /**
     * 获取参数调优建议
     */
    public Map<String, Object> getParameterRecommendations(String useCase) {
        Map<String, Object> recommendations = new HashMap<>();

        switch (useCase.toLowerCase()) {
            case "qa":
                recommendations.put("temperature", 0.0);
                recommendations.put("topP", 0.1);
                recommendations.put("maxTokens", 200);
                recommendations.put("description", "问答系统 - 需要准确、一致的答案");
                break;
            case "creative":
                recommendations.put("temperature", 1.2);
                recommendations.put("topP", 0.95);
                recommendations.put("maxTokens", 800);
                recommendations.put("description", "创意写作 - 需要创造性和多样性");
                break;
            case "chat":
                recommendations.put("temperature", 0.7);
                recommendations.put("topP", 0.9);
                recommendations.put("maxTokens", 500);
                recommendations.put("description", "日常对话 - 平衡准确性和自然性");
                break;
            case "code":
                recommendations.put("temperature", 0.2);
                recommendations.put("topP", 0.3);
                recommendations.put("maxTokens", 1000);
                recommendations.put("description", "代码生成 - 需要精确的语法和逻辑");
                break;
            default:
                recommendations.put("temperature", 0.7);
                recommendations.put("topP", 0.9);
                recommendations.put("maxTokens", 500);
                recommendations.put("description", "通用场景 - 平衡配置");
        }

        recommendations.put("useCase", useCase);
        recommendations.put("timestamp", System.currentTimeMillis());

        return recommendations;
    }

    private Map<String, Object> testConfiguration(String prompt, double temperature, 
                                                  double topP, int maxTokens, String description) {
        try {
            ChatClient client = ChatClient.builder(chatModel)
                    .defaultOptions(ChatOptions.builder()
                            .temperature(temperature)
                            .topP(topP)
                            .maxTokens(maxTokens)
                            .build())
                    .build();

            long startTime = System.currentTimeMillis();
            String response = client.prompt()
                    .user(prompt)
                    .call()
                    .content();
            long duration = System.currentTimeMillis() - startTime;

            return Map.of(
                    "description", description,
                    "parameters", Map.of(
                            "temperature", temperature,
                            "topP", topP,
                            "maxTokens", maxTokens
                    ),
                    "response", response,
                    "responseLength", response.length(),
                    "durationMs", duration,
                    "status", "success"
            );
        } catch (Exception e) {
            return Map.of(
                    "description", description,
                    "parameters", Map.of(
                            "temperature", temperature,
                            "topP", topP,
                            "maxTokens", maxTokens
                    ),
                    "error", e.getMessage(),
                    "status", "failed"
            );
        }
    }
}
```

### 3.5.3 参数调优演示控制器

```java
package com.example.modelintegration.controller;

import com.example.modelintegration.service.ParameterTuningService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tuning")
public class ParameterTuningController {

    private final ParameterTuningService tuningService;

    public ParameterTuningController(ParameterTuningService tuningService) {
        this.tuningService = tuningService;
    }

    /**
     * 测试不同 Temperature 的效果
     */
    @PostMapping("/temperature-test")
    public Map<String, Object> testTemperature(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Map.of(
                "error", "Prompt cannot be empty",
                "status", "failed"
            );
        }
        
        List<Map<String, Object>> results = tuningService.testTemperatureEffects(prompt);
        
        return Map.of(
            "prompt", prompt,
            "results", results,
            "totalTests", results.size(),
            "description", "不同temperature值对输出随机性和创造性的影响测试",
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 对比不同参数组合
     */
    @PostMapping("/parameter-comparison")
    public Map<String, Object> compareParameters(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        if (prompt == null || prompt.trim().isEmpty()) {
            return Map.of(
                "error", "Prompt cannot be empty",
                "status", "failed"
            );
        }
        
        Map<String, Object> results = tuningService.compareParameterCombinations(prompt);
        
        return Map.of(
            "prompt", prompt,
            "configurations", results,
            "description", "保守、平衡、创造性三种参数配置的对比测试",
            "recommendations", Map.of(
                "conservative", "适合需要准确答案的场景，如问答系统、事实查询",
                "balanced", "适合日常对话场景，平衡准确性和自然性",
                "creative", "适合创意写作、头脑风暴等需要发散思维的场景"
            ),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取参数调优建议
     */
    @GetMapping("/recommendations/{useCase}")
    public Map<String, Object> getRecommendations(@PathVariable String useCase) {
        return tuningService.getParameterRecommendations(useCase);
    }

    /**
     * 获取所有支持的用例类型
     */
    @GetMapping("/use-cases")
    public Map<String, Object> getUseCases() {
        return Map.of(
            "useCases", Map.of(
                "qa", "问答系统 - 需要准确、一致的答案",
                "creative", "创意写作 - 需要创造性和多样性",
                "chat", "日常对话 - 平衡准确性和自然性",
                "code", "代码生成 - 需要精确的语法和逻辑"
            ),
            "parameterGuide", Map.of(
                "temperature", "控制输出随机性，0.0最确定，2.0最随机",
                "topP", "核采样，控制候选词汇范围，0.1最集中，1.0最多样",
                "maxTokens", "最大输出长度，根据需求设置"
            ),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "OK",
            "service", "Parameter Tuning Controller",
            "timestamp", System.currentTimeMillis()
        );
    }
}
```

## 3.6 模型路由切换

### 3.6.1 模型选择服务

```java
package com.example.modelintegration.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.example.modelintegration.config.ChatClientFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ModelSelectionService {

    private final Map<String, ChatClient> chatClients;
    private final Map<String, String> modelDescriptions;

    public ModelSelectionService(ChatClientFactory clientFactory) {
        // 创建不同配置的客户端
        this.chatClients = new HashMap<>();
        this.modelDescriptions = new HashMap<>();

        // 基础模型
        this.chatClients.put("default", clientFactory.getPrimaryChatClient());
        this.modelDescriptions.put("default", "通用助手，适合日常问答");

        // 代码分析模型
        this.chatClients.put("technical", clientFactory.getCodeChatClient());
        this.modelDescriptions.put("technical", "技术专家，专注代码分析和技术解答");

        // 创意写作模型
        this.chatClients.put("creative", clientFactory.getCreativeChatClient());
        this.modelDescriptions.put("creative", "创意作家，擅长文学创作和内容创意");

        // 业务咨询模型
        this.chatClients.put("business", ChatClient.builder(clientFactory.getChatModel())
                .defaultSystem("""
                        你是一个资深的商业顾问，擅长商业分析、市场策略和企业管理。
                        请用专业且易懂的语言回答商业相关问题。
                        """)
                .build());
        this.modelDescriptions.put("business", "商业顾问，专注商业分析和策略规划");
    }

    /**
     * 根据任务类型选择合适的模型配置
     */
    public String processWithBestModel(String input, String taskType) {
        ChatClient selectedClient = selectModelForTask(taskType);

        return selectedClient.prompt()
                .user(input)
                .call()
                .content();
    }

    /**
     * 智能任务路由 - 根据输入内容自动选择最合适的模型
     */
    public Map<String, Object> smartTaskRouting(String input) {
        String taskType = detectTaskType(input);
        ChatClient selectedClient = selectModelForTask(taskType);

        long startTime = System.currentTimeMillis();
        String response = selectedClient.prompt()
                .user(input)
                .call()
                .content();
        long duration = System.currentTimeMillis() - startTime;

        return Map.of(
                "input", input,
                "detectedTaskType", taskType,
                "selectedModel", getModelNameForTask(taskType),
                "response", response,
                "duration", duration,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 获取所有可用的模型信息
     */
    public Map<String, Object> getAvailableModels() {
        Map<String, Object> models = new HashMap<>();

        chatClients.keySet().forEach(modelType -> {
            models.put(modelType, Map.of(
                    "name", getModelNameForTask(modelType),
                    "description", modelDescriptions.get(modelType),
                    "suitableFor", getSuitableTasksForModel(modelType)
            ));
        });

        return Map.of(
                "models", models,
                "totalCount", models.size(),
                "timestamp", System.currentTimeMillis()
        );
    }

    private ChatClient selectModelForTask(String taskType) {
        return switch (taskType.toLowerCase()) {
            case "creative", "writing", "story" -> chatClients.get("creative");
            case "technical", "code", "programming" -> chatClients.get("technical");
            case "business", "strategy", "analysis" -> chatClients.get("business");
            default -> chatClients.get("default");
        };
    }

    private String detectTaskType(String input) {
        String lowerInput = input.toLowerCase();

        // 代码相关关键词
        if (lowerInput.contains("代码") || lowerInput.contains("程序") ||
                lowerInput.contains("函数") || lowerInput.contains("bug") ||
                lowerInput.contains("编程") || lowerInput.contains("算法")) {
            return "technical";
        }

        // 创意写作相关关键词
        if (lowerInput.contains("写作") || lowerInput.contains("故事") ||
                lowerInput.contains("诗歌") || lowerInput.contains("创意") ||
                lowerInput.contains("小说") || lowerInput.contains("文章")) {
            return "creative";
        }

        // 商业相关关键词
        if (lowerInput.contains("商业") || lowerInput.contains("市场") ||
                lowerInput.contains("策略") || lowerInput.contains("管理") ||
                lowerInput.contains("营销") || lowerInput.contains("分析")) {
            return "business";
        }

        return "default";
    }

    private String getModelNameForTask(String taskType) {
        return switch (taskType.toLowerCase()) {
            case "creative" -> "Creative Writing Assistant";
            case "technical" -> "Technical Expert";
            case "business" -> "Business Consultant";
            default -> "General Assistant";
        };
    }

    private String[] getSuitableTasksForModel(String modelType) {
        return switch (modelType.toLowerCase()) {
            case "creative" -> new String[]{"创意写作", "内容创作", "故事编写", "诗歌创作"};
            case "technical" -> new String[]{"代码分析", "技术咨询", "程序调试", "架构设计"};
            case "business" -> new String[]{"商业分析", "市场策略", "管理咨询", "数据分析"};
            default -> new String[]{"日常问答", "通用咨询", "信息查询", "学习辅导"};
        };
    }
}
```

### 3.6.2 模型路由切换演示

```java
package com.example.modelintegration.controller;

import com.example.modelintegration.service.ModelSelectionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/model-selecting")
public class ModelSelectionController {

    private final ModelSelectionService modelService;

    public ModelSelectionController(ModelSelectionService modelService) {
        this.modelService = modelService;
    }

    /**
     * 智能模型选择
     */
    @PostMapping("/smart-selection")
    public Map<String, Object> smartSelection(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String taskType = request.get("taskType");

        if (taskType != null && !taskType.trim().isEmpty()) {
            // 手动指定任务类型
            String response = modelService.processWithBestModel(input, taskType);
            return Map.of(
                    "input", input,
                    "taskType", taskType,
                    "response", response,
                    "selectedModel", getModelNameForTask(taskType),
                    "mode", "manual"
            );
        } else {
            // 自动检测任务类型
            return modelService.smartTaskRouting(input);
        }
    }

    /**
     * 获取可用模型信息
     */
    @GetMapping("/available-models")
    public Map<String, Object> getAvailableModels() {
        return modelService.getAvailableModels();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Model Selection Controller",
                "timestamp", System.currentTimeMillis()
        );
    }

    private String getModelNameForTask(String taskType) {
        if (taskType == null) return "General Assistant";

        return switch (taskType.toLowerCase()) {
            case "creative", "writing" -> "Creative Writing Assistant";
            case "technical", "code" -> "Technical Expert";
            case "business" -> "Business Consultant";
            default -> "General Assistant";
        };
    }
}
```

## 3.7 启动脚本和使用指南

**`run.sh`（启动脚本）：**

```bash
#!/bin/bash

# Model Integration Demo 启动脚本
echo "🚀 正在启动 Model Integration Demo..."

# 检查并加载 .env 文件
if [ -f ".env" ]; then
    echo "📄 正在加载 .env 文件中的环境变量..."
    # 读取 .env 文件并设置环境变量
    while IFS= read -r line; do
        # 跳过注释行和空行
        if [[ ! "$line" =~ ^[[:space:]]*# ]] && [[ -n "$line" ]]; then
            export "$line"
        fi
    done < .env
    echo "✅ 环境变量加载完成"
else
    echo "⚠️  警告：未找到 .env 文件，将使用默认环境变量"
    echo "💡 提示：复制 .env.example 为 .env 并配置你的 API Keys"
fi

# 检查环境变量
check_env_var() {
    if [ -z "${!1}" ]; then
        echo "⚠️  警告：环境变量 $1 未设置"
        return 1
    else
        echo "✅ 环境变量 $1 已设置"
        return 0
    fi
}

echo "📋 检查环境变量..."

# 根据激活的 profile 检查相应的环境变量
ACTIVE_PROFILE=${SPRING_PROFILES_ACTIVE:-deepseek}

case $ACTIVE_PROFILE in
    "openai")
        check_env_var "OPENAI_API_KEY"
        ;;
    "deepseek")
        check_env_var "DEEPSEEK_API_KEY"
        ;;
    "ollama")
        echo "ℹ️  使用本地 Ollama，请确保 Ollama 服务已启动"
        if ! curl -s http://localhost:11434/api/tags > /dev/null; then
            echo "❌ Ollama 服务未启动或不可访问"
            echo "请先启动 Ollama: ollama serve"
            exit 1
        else
            echo "✅ Ollama 服务运行正常"
        fi
        ;;
    *)
        echo "⚠️  未知的 profile: $ACTIVE_PROFILE"
        echo "支持的 profiles: openai, deepseek, ollama"
        ;;
esac

# 检查是否存在 pom.xml 文件
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误：未找到 pom.xml 文件，请确保在正确的项目目录中运行此脚本"
    exit 1
fi

echo "🔧 使用 profile: $ACTIVE_PROFILE"
echo "🔧 启动应用..."

# 设置 JVM 参数
export MAVEN_OPTS="-Xmx2g -Xms1g"

# 启动应用
export SPRING_PROFILES_ACTIVE=$ACTIVE_PROFILE
mvn spring-boot:run

if [ $? -ne 0 ]; then
    echo "❌ 应用启动失败"
    exit 1
fi
```

**`test_apis.sh`（API 测试脚本）：**

```bash
#!/bin/bash

# API 测试脚本
BASE_URL="http://localhost:8080"

echo "🧪 Spring AI 模型集成演示 - API 测试脚本"
echo "========================================"

# 检查服务是否运行
echo "📡 检查服务状态..."
if ! curl -s "$BASE_URL/api/chat/health" > /dev/null; then
    echo "❌ 服务未启动或不可访问，请先启动应用"
    exit 1
fi
echo "✅ 服务运行正常"
echo

# 测试基础聊天
echo "💬 测试基础聊天功能..."
curl -X POST "$BASE_URL/api/chat/basic" \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请简单介绍一下 Spring AI"}' \
  -s | jq '.'
echo
echo "---"

# 测试代码分析
echo "🔍 测试代码分析功能..."
curl -X POST "$BASE_URL/api/chat/code-analysis" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Calculator { public int add(int a, int b) { return a + b; } }",
    "question": "这个类有什么可以改进的地方？"
  }' \
  -s | jq '.analysis' -r
echo
echo "---"

# 测试创意写作
echo "✍️ 测试创意写作功能..."
curl -X POST "$BASE_URL/api/chat/creative-writing" \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "人工智能的未来",
    "style": "科幻小说片段"
  }' \
  -s | jq '.content' -r
echo
echo "---"

# 测试参数调优
echo "🎛️ 测试参数调优功能..."
curl -X POST "$BASE_URL/api/tuning/temperature-test" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "描述一个理想的工作日"}' \
  -s | jq '.results[0:2]'
echo
echo "---"

# 测试智能模型选择
echo "🤖 测试智能模型选择..."
curl -X POST "$BASE_URL/api/model-selecting/smart-selection" \
  -H "Content-Type: application/json" \
  -d '{"input": "请帮我分析这段 Java 代码的性能问题"}' \
  -s | jq '{"detectedTaskType": .detectedTaskType, "selectedModel": .selectedModel}'
echo
echo "---"

# 测试获取可用模型
echo "📋 获取可用模型信息..."
curl -X GET "$BASE_URL/api/model-selecting/available-models" \
  -s | jq '.models | keys'
echo
echo "---"

# 测试参数建议
echo "💡 获取参数调优建议..."
curl -X GET "$BASE_URL/api/tuning/recommendations/code" \
  -s | jq '.recommendation'
echo
echo "---"

# 如果启用了 OpenAI embedding，测试 embedding 功能
echo "🔤 测试文本嵌入功能（如果可用）..."
EMBED_RESPONSE=$(curl -X POST "$BASE_URL/api/embedding/embed" \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI 很棒"}' \
  -s 2>/dev/null)

if echo "$EMBED_RESPONSE" | jq -e '.embedding' > /dev/null 2>&1; then
    echo "✅ Embedding 功能可用"
    echo "$EMBED_RESPONSE" | jq '{"text": .text, "dimensions": .dimensions}'
    
    # 测试相似度计算
    echo
    echo "📏 测试文本相似度计算..."
    curl -X POST "$BASE_URL/api/embedding/similarity" \
      -H "Content-Type: application/json" \
      -d '{
        "text1": "Spring AI 是一个强大的框架",
        "text2": "Spring AI 是用于 AI 开发的工具"
      }' \
      -s | jq '{"similarity": .similarity, "percentage": .similarityPercentage}'
else
    echo "ℹ️  Embedding 功能未启用（需要 OpenAI profile）"
fi

echo
echo "---"
echo "🎉 测试完成！所有主要功能都已验证。"
echo
echo "💡 提示："
echo "- 可以通过修改环境变量 SPRING_PROFILES_ACTIVE 切换不同的模型"
echo "- 访问 http://localhost:8080/actuator/health 查看应用健康状态"
echo "- 访问 http://localhost:8080/actuator/metrics 查看监控指标"
```

## 3.9 使用示例

### 切换到 DeepSeek
```bash
export SPRING_PROFILES_ACTIVE=deepseek
export DEEPSEEK_API_KEY=your_api_key
./run.sh
```

### 切换到 OpenAI
```bash
export SPRING_PROFILES_ACTIVE=openai
export OPENAI_API_KEY=your_api_key
./run.sh
```

### API 测试示例

**基础功能测试：**
```bash
# 基础对话
curl -X POST http://localhost:8080/api/chat/basic \
  -H "Content-Type: application/json" \
  -d '{"message": "解释一下什么是 Spring AI"}'

# 代码分析
curl -X POST http://localhost:8080/api/chat/code-analysis \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Calculator { public int add(int a, int b) { return a + b; } }",
    "question": "这个类有什么可以改进的地方？"
  }'

# 创意写作
curl -X POST http://localhost:8080/api/chat/creative-writing \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "人工智能的未来",
    "style": "科幻小说片段"
  }'
```

**参数调优测试：**
```bash
# 温度参数测试
curl -X POST http://localhost:8080/api/tuning/temperature-test \
  -H "Content-Type: application/json" \
  -d '{"prompt": "写一首关于春天的诗"}'

# 参数组合对比
curl -X POST http://localhost:8080/api/tuning/parameter-comparison \
  -H "Content-Type: application/json" \
  -d '{"prompt": "如何优化 Java 应用的性能？"}'

# 获取参数建议
curl -X GET http://localhost:8080/api/tuning/recommendations/creative

# 获取所有用例类型
curl -X GET http://localhost:8080/api/tuning/use-cases
```

**模型切换和对比：**
```bash
# 智能模型选择（自动检测）
curl -X POST http://localhost:8080/api/model-selecting/smart-selection \
  -H "Content-Type: application/json" \
  -d '{"input": "请帮我分析这段 Java 代码的性能问题"}'

# 智能模型选择（手动指定）
curl -X POST http://localhost:8080/api/model-selecting/smart-selection \
  -H "Content-Type: application/json" \
  -d '{"input": "写一个关于春天的故事", "taskType": "creative"}'

# 获取可用模型
curl -X GET http://localhost:8080/api/model-selecting/available-models
```

**Embedding 功能测试（需要 OpenAI profile）：**
```bash
# 文本嵌入
curl -X POST http://localhost:8080/api/embedding/embed \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI 很棒"}'

# 批量文本嵌入
curl -X POST http://localhost:8080/api/embedding/embed-batch \
  -H "Content-Type: application/json" \
  -d '{
    "texts": ["Spring AI 很棒", "Java 开发框架", "人工智能应用"]
  }'

# 相似度计算
curl -X POST http://localhost:8080/api/embedding/similarity \
  -H "Content-Type: application/json" \
  -d '{
    "text1": "Spring AI 是一个强大的框架",
    "text2": "Spring AI 是用于 AI 开发的工具"
  }'
```

**监控和健康检查：**
```bash
# 健康检查
curl -X GET http://localhost:8080/api/chat/health
curl -X GET http://localhost:8080/api/tuning/health
curl -X GET http://localhost:8080/api/model-selecting/health

# 应用信息
curl -X GET http://localhost:8080/actuator/info

# 监控指标
curl -X GET http://localhost:8080/actuator/metrics
curl -X GET http://localhost:8080/actuator/health
```

## 3.10 Embedding 模型演示

在 Spring AI 中，除了 Chat 模型之外，Embedding 模型也是重要的组成部分。当使用 OpenAI 配置时，我们可以使用 Embedding 功能进行文本向量化处理。

### 3.10.1 Embedding 控制器实现

```java
package com.example.modelintegration.controller;

import java.util.List;
import java.util.Map;

import com.example.modelintegration.config.EmbeddingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embedding")
@ConditionalOnBean(EmbeddingService.class)
public class EmbeddingModelController {

    private final EmbeddingService embeddingService;

    public EmbeddingModelController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    /**
     * 单个文本嵌入
     */
    @PostMapping("/embed")
    public Map<String, Object> embedText(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        float[] embedding = embeddingService.embed(text);

        return Map.of(
                "text", text,
                "embedding", embedding,
                "dimensions", embedding.length,
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 批量文本嵌入
     */
    @PostMapping("/embed-batch")
    public Map<String, Object> embedBatch(@RequestBody Map<String, List<String>> request) {
        List<String> texts = request.get("texts");

        Map<String, float[]> embeddings = embeddingService.embedBatch(texts);

        return Map.of(
                "texts", texts,
                "embeddings", embeddings,
                "count", texts.size(),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 文本相似度计算
     */
    @PostMapping("/similarity")
    public Map<String, Object> calculateSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");

        double similarity = embeddingService.calculateSimilarity(text1, text2);

        return Map.of(
                "text1", text1,
                "text2", text2,
                "similarity", similarity,
                "similarityPercentage", String.format("%.2f%%", similarity * 100),
                "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "Embedding Model Controller",
                "timestamp", System.currentTimeMillis()
        );
    }
}
```

### 3.10.2 项目特性总结

在这个演示项目中，我们实现了以下核心特性：

1. **智能任务路由**：根据用户输入自动选择最适合的模型配置
2. **多模型支持**：支持 OpenAI、DeepSeek、Ollama 等多种模型提供商
3. **参数调优**：提供温度、Top-P 等参数的调优建议和测试
4. **Embedding 功能**：文本向量化、相似度计算（OpenAI 配置时可用）
5. **监控和健康检查**：集成 Actuator 提供完整的监控体系
6. **错误处理**：完善的输入验证和错误处理机制
7. **灵活配置**：支持环境变量和多 profile 配置管理

## 3.11 小结与展望

通过这篇文章，我们深入探讨了 Spring AI 的模型集成和调优能力。我们学会了：

1. **理解不同模型类型**：Chat、Embedding、Image 模型各有其用武之地
2. **模型选择策略**：根据需求在本地模型和云端模型之间做出明智选择
3. **参数调优技巧**：通过调整 Temperature、Top P 等参数优化模型表现
4. **无缝切换提供商**：在不修改业务代码的情况下切换不同的 AI 提供商
5. **智能任务路由**：自动识别任务类型并选择最适合的模型配置
6. **Embedding 功能**：文本向量化和相似度计算的实际应用
7. **参数调优建议系统**：基于用例提供最佳参数配置建议
8. **监控和健康检查**：全面的性能监控和健康检查机制
9. **错误处理和输入验证**：完善的错误处理和用户输入验证
10. **生产环境最佳实践**：配置管理、容错处理、监控指标等企业级特性

### 实际项目亮点

在这个演示项目中，我们实现了以下核心特性：

- **智能路由系统**：根据用户输入自动选择最适合的模型配置
- **多模型支持**：支持 OpenAI、DeepSeek、Ollama 等多种模型提供商
- **参数调优工具**：提供温度参数测试和参数组合对比功能
- **Embedding 集成**：文本向量化、相似度计算（OpenAI 配置时可用）
- **监控和健康检查**：集成 Actuator 提供完整的监控体系
- **错误处理机制**：优雅的错误处理和用户友好的错误信息
- **配置管理**：灵活的环境配置和多 profile 配置管理

Spring AI 的模型抽象设计让我们能够像使用数据库一样使用 AI 模型——通过配置切换"数据源"，而业务逻辑保持不变。这种设计哲学大大降低了 AI 应用的开发和维护成本。

### 实际使用建议

通过这个项目的实践，我们可以总结出以下使用建议：

1. **选择合适的模型提供商**：根据具体需求选择 OpenAI、DeepSeek 或本地 Ollama
2. **合理配置参数**：使用我们提供的参数调优工具找到最佳配置
3. **利用智能路由**：让系统自动选择最适合的模型配置
4. **监控应用性能**：利用 Actuator 端点监控应用健康状态
5. **处理错误情况**：确保应用在网络异常或模型服务不可用时能够优雅处理

在下一篇文章中，我们将探讨 **Chat Memory 机制**，学习如何让 AI 拥有"记忆"，实现真正的多轮对话体验。我们将深入了解短期记忆、长期记忆的实现，以及如何在有限的上下文窗口内高效管理对话历史。

记住，选择合适的模型就像选择合适的工具一样重要。没有万能的模型，只有最适合特定场景的模型。多实践、多测试，你就能找到最适合你业务需求的"AI 大脑"。

---

**相关资源：**
- [Spring AI 官方文档 - 模型支持](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 文档](https://platform.openai.com/docs/)
- [DeepSeek API 文档](https://platform.deepseek.com/api-docs/)
- [Ollama 本地模型部署指南](https://ollama.ai/)
- [项目源码地址](./model-integration-demo/)
