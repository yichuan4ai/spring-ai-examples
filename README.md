# Spring AI 实战指南 - 示例代码仓库

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-Latest-blue.svg)](https://docs.spring.io/spring-ai/reference/)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

欢迎来到 **《从零构建智能应用——Spring AI 实战指南》** 系列文章的配套代码仓库！

这个仓库包含了完整的 Spring AI 实战示例，从基础的聊天客户端到高级的模型集成和提示工程，帮助开发者快速掌握 Spring AI 的核心概念和最佳实践。

## 🎯 项目概述

本仓库是一个全面的 Spring AI 学习和实践资源，包含：

- **多个独立的演示项目**，涵盖不同的使用场景
- **渐进式学习路径**，从简单到复杂
- **生产就绪的代码示例**，可直接应用到实际项目
- **详细的文档和注释**，降低学习门槛

## 📁 项目结构

```
spring-ai-examples/
├── simple-chat-client/          # 简单聊天客户端
│   ├── 基础的 AI 对话应用
│   ├── DeepSeek 模型集成
│   └── 环境变量配置示例
├── prompt-engineering-demo/     # 提示工程演示
│   ├── Prompt 模板设计
│   ├── 动态参数绑定
│   └── 多场景应用示例
├── model-integration-demo/      # 模型集成演示
│   ├── 多模型切换对比
│   ├── 参数调优示例
│   └── 智能模型选择
└── README.md                    # 项目总览（本文件）
```

## 🚀 快速开始

### 系统要求

- **Java 17+** - 确保你的开发环境支持现代 Java 特性
- **Maven 3.6+** - 用于项目构建和依赖管理
- **AI 模型 API Key** - 支持 DeepSeek、OpenAI、Azure OpenAI 等

### 环境准备

1. **克隆仓库**
   ```bash
   git clone https://github.com/your-username/spring-ai-examples.git
   cd spring-ai-examples
   ```

2. **配置 API Key**
   ```bash
   # 设置 DeepSeek API Key（推荐用于入门）
   export DEEPSEEK_API_KEY=your_deepseek_api_key
   
   # 或者设置 OpenAI API Key
   export OPENAI_API_KEY=your_openai_api_key
   ```

3. **选择一个项目开始**
   
   建议按以下顺序学习：
   
   ```bash
   # 1. 从最简单的聊天客户端开始
   cd simple-chat-client
   ./run.sh
   
   # 2. 学习提示工程技巧
   cd ../prompt-engineering-demo
   ./run.sh
   
   # 3. 掌握高级模型集成
   cd ../model-integration-demo
   ./run.sh
   ```

## 📚 子项目详细介绍

### 1. 简单聊天客户端 (`simple-chat-client/`)

**适合人群：** Spring AI 新手、想快速体验 AI 集成的开发者

**核心特性：**
- 🎯 5分钟快速启动
- 🔧 环境变量配置
- 💬 基础对话能力
- 📖 详细的新手指南

**快速体验：**
```bash
cd simple-chat-client
./run.sh
# 访问 http://localhost:8080
```

[→ 查看详细文档](./simple-chat-client/README.md)

### 2. 提示工程演示 (`prompt-engineering-demo/`)

**适合人群：** 想深入理解 Prompt 设计艺术的开发者

**核心特性：**
- 🎨 Prompt 模板设计
- 🔄 动态参数绑定
- 🌤️ 天气查询示例
- 🤖 客服机器人示例
- 📋 订单状态更新示例

**应用场景：**
- 智能客服系统
- 内容生成应用
- 业务流程自动化

[→ 查看详细文档](./prompt-engineering-demo/README.md)

### 3. 模型集成演示 (`model-integration-demo/`)

**适合人群：** 需要生产级 AI 应用的开发者

**核心特性：**
- 🔄 多模型切换对比
- ⚙️ 参数调优实战
- 🧠 智能模型选择
- 📊 性能指标监控
- 🔧 模型配置管理

**生产特性：**
- 支持多种 AI 提供商
- 配置热更新
- 监控和指标收集
- 错误处理和降级

[→ 查看详细文档](./model-integration-demo/README.md)

## 🛠️ 支持的 AI 模型

| 提供商 | 模型类型 | 配置示例 | 推荐场景 |
|--------|----------|----------|----------|
| **DeepSeek** | Chat | `deepseek-chat` | 中文对话、成本敏感 |
| **OpenAI** | Chat/Embedding | `gpt-4`, `text-embedding-3-large` | 通用应用、高质量输出 |
| **Ollama** | 本地模型 | `llama2`, `mistral` | 私有部署、数据安全 |
| **Azure OpenAI** | 企业级 | `gpt-4-azure` | 企业应用、合规要求 |

## 📖 学习路径建议

### 🌟 初学者路径
1. **阅读项目概述** → 理解 Spring AI 的价值
2. **运行简单聊天客户端** → 体验第一个 AI 应用
3. **学习环境配置** → 掌握配置管理最佳实践

### 🚀 进阶开发者路径
1. **深入提示工程** → 掌握与 AI 高效对话的艺术
2. **模型选择与调优** → 了解不同模型的特点和适用场景
3. **集成多个模型** → 构建生产级的模型切换方案

### 🏗️ 架构师路径
1. **研究模型集成架构** → 设计可扩展的 AI 服务架构
2. **性能监控和优化** → 构建稳定可靠的生产系统
3. **成本控制策略** → 平衡功能需求与运营成本

## 🔧 配置和自定义

### 环境变量配置

```bash
# API Keys
DEEPSEEK_API_KEY=your_deepseek_api_key
OPENAI_API_KEY=your_openai_api_key

# 模型配置
SPRING_AI_CHAT_MODEL=deepseek-chat
SPRING_AI_TEMPERATURE=0.7
SPRING_AI_MAX_TOKENS=2048

# 应用配置
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### 自定义扩展

每个项目都设计了清晰的扩展点：

- **自定义 Prompt 模板** - 适配你的业务场景
- **集成新的 AI 模型** - 支持更多提供商
- **添加业务逻辑** - 构建完整的应用功能
- **监控和日志** - 生产环境必备的可观测性

## 🤝 贡献指南

我们欢迎社区贡献！你可以通过以下方式参与：

1. **报告问题** - 发现 Bug 或文档错误
2. **提出改进建议** - 分享你的使用经验
3. **贡献代码** - 添加新的示例或改进现有代码
4. **完善文档** - 帮助其他开发者更好地理解和使用

### 贡献步骤

```bash
# 1. Fork 仓库
# 2. 创建功能分支
git checkout -b feature/amazing-feature

# 3. 提交更改
git commit -m 'Add: 添加了惊人的新功能'

# 4. 推送到分支
git push origin feature/amazing-feature

# 5. 创建 Pull Request
```

## 📚 相关资源

### 官方文档
- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)

### 学习资源
- [Prompt 工程最佳实践](https://www.promptingguide.ai/)
- [OpenAI API 文档](https://platform.openai.com/docs)
- [DeepSeek 开发者指南](https://platform.deepseek.com/docs)

### 社区和支持
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Spring 社区论坛](https://spring.io/community)

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE)。这意味着你可以自由地使用、修改和分发代码，包括商业用途。

## 🙏 致谢

感谢以下项目和社区的支持：

- [Spring AI](https://github.com/spring-projects/spring-ai) - 强大的 AI 集成框架
- [Spring Boot](https://spring.io/projects/spring-boot) - 优秀的应用开发框架
- 所有贡献者和使用者的反馈和建议


---

⭐ **如果这个项目对你有帮助，别忘了给我们一个 Star！** ⭐ 