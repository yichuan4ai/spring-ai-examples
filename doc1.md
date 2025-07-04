## Spring AI 实战指南：Spring AI 快速入门

### 1.1 Spring AI 的定位与生态优势：为什么选择它？

在当今这个 AI 浪潮汹涌的时代，大语言模型（LLM）已经不再是遥不可及的黑科技，而是我们日常开发中可以触及的强大工具。然而，如何将这些复杂的 AI 能力优雅地集成到我们熟悉的 Java 应用中，一直是许多开发者面临的挑战。

这时，Spring AI 应运而生。它不仅仅是一个简单的 SDK，更是一个基于 Spring 框架理念构建的 AI 应用开发框架。它的核心目标是：**让 Java 开发者能够以 Spring 的方式，轻松、高效地构建 AI 驱动的应用。**

**Spring AI 的核心优势在于：**

1.  **Spring 生态的无缝集成：** 如果你熟悉 Spring Boot、Spring Data、Spring Web 等，那么你几乎可以零成本地学习和使用 Spring AI。它沿用了 Spring 家族一贯的"约定优于配置"和"开箱即用"的特性，大大降低了学习曲线。
2.  **抽象与统一的 API：** 无论是 OpenAI、DeepSeek、Hugging Face 还是 Azure OpenAI，Spring AI 都提供了一套统一的 API 接口。这意味着你可以在不修改核心业务逻辑的情况下，轻松切换不同的 AI 模型提供商，这对于模型的选择和未来的扩展性至关重要。
3.  **企业级特性支持：** 作为 Spring 家族的一员，Spring AI 自然继承了 Spring Boot 在可观测性（Metrics, Tracing）、配置管理、安全性等方面的强大能力，使得构建生产级别的 AI 应用变得更加可靠。
4.  **模块化与可扩展性：** Spring AI 设计精巧，各个功能模块（如 Chat Client, Embedding, Tool Calling, RAG）都是独立的，你可以根据需求选择性地引入和使用，并且可以轻松扩展自定义功能。

简而言之，Spring AI 就像是 Spring 框架为 AI 领域量身定制的一把瑞士军刀，它让 AI 能力不再是孤立的黑箱，而是可以与你的业务逻辑紧密结合的强大组件。

### 1.2 对比 LangChain 等框架的差异

在 AI 应用开发领域，除了 Spring AI，你可能还听说过 LangChain。LangChain 是一个非常流行的框架，它支持多种编程语言（Python, JavaScript/TypeScript），旨在帮助开发者构建复杂的 LLM 应用。那么，Spring AI 和 LangChain 有何不同呢？

| 特性/框架     | Spring AI                                      | LangChain                                      |
| :------------ | :--------------------------------------------- | :--------------------------------------------- |
| **主要语言**  | Java                                           | Python, JavaScript/TypeScript                  |
| **生态集成**  | 深度集成 Spring 生态，如 Spring Boot, Spring Data | 独立生态，但提供与各种工具的集成               |
| **设计理念**  | Spring 风格，强调约定、依赖注入、模块化          | 链式调用、代理模式，强调组件组合               |
| **目标用户**  | Java 开发者，尤其是 Spring 开发者              | 跨语言开发者，AI/ML 工程师                     |
| **生产就绪**  | 继承 Spring Boot 的企业级特性，生产部署友好    | 社区活跃，但生产部署需额外考虑 Java 生态集成   |
| **抽象层次**  | 提供高层抽象，同时保留底层模型配置的灵活性     | 提供更细粒度的组件，允许高度定制化             |

**总结来说：**

*   如果你是 **Java 开发者**，并且你的项目已经基于 **Spring 框架**，那么 Spring AI 无疑是你的首选。它能让你在熟悉的开发环境中，以最平滑的方式接入 AI 能力，享受 Spring 生态带来的所有便利。
*   如果你是 **Python 开发者**，或者需要构建一个与特定语言生态无关的、高度定制化的 AI 应用，那么 LangChain 可能会更适合你。

Spring AI 并不是要取代 LangChain，而是为 Java 开发者提供了一个更符合其技术栈和开发习惯的强大选择。

### 1.3 五分钟搭建第一个 ChatClient（集成 DeepSeek）

理论讲得再多，不如动手实践来得实在。现在，让我们一起用 5 分钟，搭建你的第一个 Spring AI 应用，并与 DeepSeek 模型进行对话。

**前提条件：**

*   JDK 21 或更高版本
*   Maven 3.6 或更高版本
*   一个 DeepSeek API Key (如果你没有，可以去 DeepSeek 官网注册并获取)

#### 步骤 1：创建 Spring Boot 项目并配置 Maven 依赖

你可以使用 Spring Initializr (start.spring.io) 来快速创建一个 Spring Boot 项目。

选择以下依赖：

*   **Spring Web** (用于构建 RESTful 应用，虽然我们这里只是简单示例，但通常会用到)

下载项目并解压。

** 修改 `pom.xml`，请确保你的 `pom.xml` 文件包含以下核心依赖：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.4.7</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>simple-chat-client</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>simple-chat-client</name>
	<description>simple-chat-client</description>
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

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

**重要说明：**

*   **`spring-boot-starter-parent` 版本：** 请确保你的 Spring Boot 版本是最新的稳定版。在撰写本文时，`3.4.7` 是一个较新的版本。
*   **`spring-ai.version`：** Spring AI 的版本号非常重要。请访问 Spring AI 的官方文档或 GitHub 仓库，查找最新的稳定版本。例如，在撰写本文时，`1.0.0` 是一个可用的版本。
*   **`spring-ai-bom`：** 引入 `spring-ai-bom` (Bill Of Materials) 是一个最佳实践。它允许你统一管理所有 Spring AI 相关依赖的版本，避免版本冲突。一旦引入了 BOM，你就不需要在 `spring-ai-starter-model-deepseek` 等具体模块中指定版本号了。
*   **Java 版本：** Spring AI 1.0.0 要求 Java 21 或更高版本，这是为了利用最新的 Java 特性。

完成 `pom.xml` 配置后，保存文件，Maven 会自动下载所需的依赖。

#### 步骤 2：配置 DeepSeek API Key

在 `src/main/resources/application.properties` 文件中，添加你的 DeepSeek API Key：

```properties
spring.application.name=simple-chat-client

# DeepSeek
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7
```

请将 `DEEPSEEK_API_KEY` 替换为你的实际 API Key。
**注意：** 在生产环境中，API Key 应该通过环境变量或更安全的配置方式管理，而不是直接硬编码在配置文件中。

#### 步骤 3：编写你的第一个 ChatClient

创建一个新的 Java 类，例如 `com.example.chatclient.controller.ChatController`：

```java
package com.example.chatclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @GetMapping("/ok")
    public String ok() {
        return "OK";
    }

    private final ChatClient chatClient;

    // Spring AI 会自动注入配置好的 ChatClient
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你好，Spring AI！") String message) {
        // 使用 ChatClient 发送消息并获取回复
        String response = chatClient.prompt()
                .user(message) // 设置用户输入
                .call()        // 调用 AI 模型
                .content();    // 获取回复内容
        return response;
    }
}
```

**代码解析：**

*   `@RestController`：标准的 Spring Web 注解，表示这是一个 RESTful 控制器。
*   `ChatClient chatClient;`：这是 Spring AI 提供的核心接口，用于与 AI 模型进行交互。
*   `public ChatController(ChatClient.Builder chatClientBuilder)`：Spring AI 会自动为我们提供一个 `ChatClient.Builder` 实例。我们通过 `builder.build()` 来构建 `ChatClient` 实例。
*   `chatClient.prompt().user(message).call().content();`：这是与 AI 模型对话的核心链式调用。
    *   `prompt()`：开始构建一个 Prompt。
    *   `user(message)`：设置用户发送的消息内容。
    *   `call()`：执行 AI 模型调用。
    *   `content()`：获取 AI 模型返回的文本内容。

#### 步骤 4：创建启动脚本（可选但推荐）

为了更方便地管理环境变量和启动应用，你可以创建一个启动脚本 `run.sh`：

```bash
#!/bin/bash

# Simple Chat Client 启动脚本
echo "🚀 正在启动 Simple Chat Client..."

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
fi

# 检查是否存在 pom.xml 文件
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误：未找到 pom.xml 文件，请确保在正确的项目目录中运行此脚本"
    exit 1
fi

# 启动 Spring Boot 应用
echo "🔧 使用 Maven 启动应用..."
mvn spring-boot:run

# 如果启动失败，显示错误信息
if [ $? -ne 0 ]; then
    echo "❌ 应用启动失败"
    exit 1
fi
```

同时创建一个 `.env` 文件来存储环境变量：

```bash
# DeepSeek API 配置
DEEPSEEK_API_KEY=your_deepseek_api_key_here

# 其他环境变量可以在这里添加
# SPRING_PROFILES_ACTIVE=dev
# SERVER_PORT=8080
```

#### 步骤 5：运行你的应用

在你的项目根目录，打开终端，运行 Spring Boot 应用：

**使用启动脚本（推荐）：**
```bash
chmod +x run.sh
./run.sh
```

**或者直接使用 Maven：**
```bash
mvn spring-boot:run
```

或者使用你的 IDE 运行 `SimpleChatClientApplication` 类。

#### 步骤 6：测试你的 AI 应用

应用启动后，打开浏览器或使用 Postman/curl 访问以下 URL：

```
http://localhost:8080/chat?message=请用一句话介绍一下Spring框架
```

你将看到类似这样的回复（具体内容可能因模型而异）：

```
Spring框架是一个开源的Java平台，它提供了一个全面的编程和配置模型，用于构建现代企业级应用程序。
```

恭喜你！你已经成功搭建并运行了你的第一个 Spring AI 应用，并与 DeepSeek 模型进行了对话。是不是非常简单？

### 1.4 代码示例：`@Bean` 配置 ChatClient 的基础模板

在上面的例子中，我们通过构造函数注入 `ChatClient.Builder` 来构建 `ChatClient`。在更复杂的场景中，你可能希望通过 `@Bean` 的方式来集中配置和管理 `ChatClient` 实例，例如，如果你需要自定义一些全局行为或者注入特定的拦截器。

你可以在你的 Spring Boot 应用主类或者任何 `@Configuration` 类中，定义一个 `@Bean` 方法来创建 `ChatClient`：

```java
package com.example.chatclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // 假设你已经配置了 ChatModel，Spring AI 会自动为你注入
    // 例如，如果你引入了 spring-ai-starter-model-deepseek 依赖，它会自动配置 DeepSeek ChatModel
    @Bean
    public ChatClient customChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                // 你可以在这里进行更多自定义配置，例如：
                // .defaultSystem("你是一个乐于助人的AI助手。")
                // .defaultOptions(DeepSeekChatOptions.builder().withTemperature(0.7f).build())
                .build();
    }
}
```

然后，在你的 `ChatController` 中，你可以直接注入这个自定义的 `ChatClient`：

```java
package com.example.chatclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient customChatClient; // 注入我们自定义的 ChatClient

    public ChatController(ChatClient customChatClient) {
        this.customChatClient = customChatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你好，Spring AI！") String message) {
        String response = customChatClient.prompt()
                .user(message)
                .call()
                .content();
        return response;
    }
}
```

通过 `@Bean` 配置，你可以更灵活地控制 `ChatClient` 的创建过程，例如设置默认的系统消息、调整模型参数等，这为后续的进阶配置打下了基础。

### 1.5 小结与展望

在本篇中，我们初步了解了 Spring AI 的定位、优势以及它与 LangChain 等框架的区别。最重要的是，我们亲手搭建并运行了第一个 Spring AI 应用，体验了与 DeepSeek 模型对话的乐趣。

你可能已经发现，Spring AI 的设计哲学就是"简单而强大"。它将复杂的 AI 模型交互封装在简洁的 API 背后，让开发者能够专注于业务逻辑，而不是底层细节。

在接下来的文章中，我们将逐步深入 Spring AI 的各个核心组件，包括 Prompt 工程、模型集成与调优、Chat Memory、Tool Calling 等，帮助你构建更智能、更强大的 AI 应用。

敬请期待下一篇：《Prompt 工程基础：与 AI 高效对话的艺术》！