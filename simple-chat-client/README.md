# Simple Chat Client

一个基于 Spring AI 的简单聊天客户端应用，集成了 DeepSeek AI 模型。

## 🚀 项目特性

- 基于 Spring Boot 3.x 和 Spring AI
- 集成 DeepSeek AI 聊天模型
- 支持环境变量配置
- 简单易用的启动脚本

## 📋 系统要求

- Java 17 或更高版本
- Maven 3.6 或更高版本
- DeepSeek API 密钥

## 🔧 环境配置

### 1. 获取 DeepSeek API 密钥

1. 访问 [DeepSeek 官网](https://platform.deepseek.com/)
2. 注册并登录账户
3. 在控制台中获取 API 密钥

### 2. 配置环境变量

#### 方法一：使用 .env 文件（推荐）

1. 复制环境变量模板：
   ```bash
   cp .env.example .env
   ```

2. 编辑 `.env` 文件，设置您的 API 密钥：
   ```bash
   # DeepSeek API 配置
   DEEPSEEK_API_KEY=your_deepseek_api_key_here
   
   # 其他环境变量可以在这里添加
   # SPRING_PROFILES_ACTIVE=dev
   # SERVER_PORT=8080
   ```

#### 方法二：直接设置环境变量

```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key_here
```

## 🚀 启动应用

### 使用启动脚本（推荐）

```bash
# 确保脚本有执行权限
chmod +x run.sh

# 启动应用
./run.sh
```

### 使用 Maven 命令

```bash
# 直接使用 Maven 启动
mvn spring-boot:run

# 或者先编译再运行
mvn clean compile
mvn spring-boot:run
```

### 使用 IDE

1. 在 IDE 中打开项目
2. 设置环境变量 `DEEPSEEK_API_KEY`
3. 运行 `SimpleChatClientApplication` 主类

## 📁 项目结构

```
simple-chat-client/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/chatclient/
│   │   │       ├── controller/
│   │   │       │   └── ChatController.java
│   │   │       └── SimpleChatClientApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .env                    # 环境变量配置（不提交到版本控制）
├── .env.example           # 环境变量模板
├── .gitignore             # Git 忽略文件
├── run.sh                 # 启动脚本
├── pom.xml                # Maven 配置
└── README.md              # 项目说明
```

## ⚙️ 配置说明

### application.properties

```properties
spring.application.name=simple-chat-client

# DeepSeek 配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
spring.ai.deepseek.chat.options.temperature=0.7
```

### 配置参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `spring.ai.deepseek.api-key` | DeepSeek API 密钥 | 从环境变量读取 |
| `spring.ai.deepseek.chat.options.model` | 使用的模型 | deepseek-chat |
| `spring.ai.deepseek.chat.options.temperature` | 温度参数（控制随机性） | 0.7 |

## 🔒 安全注意事项

- ✅ `.env` 文件已添加到 `.gitignore`，不会提交到版本控制
- ✅ API 密钥通过环境变量配置，避免硬编码
- ✅ 提供了 `.env.example` 模板文件供团队协作

## 🐛 常见问题

### Q: 启动时提示 "未找到 .env 文件"
A: 请确保在项目根目录下创建 `.env` 文件，并参考 `.env.example` 设置正确的环境变量。

### Q: API 调用失败
A: 请检查：
1. API 密钥是否正确设置
2. 网络连接是否正常
3. DeepSeek 服务是否可用

### Q: 端口被占用
A: 可以在 `.env` 文件中添加 `SERVER_PORT=8081` 来更改端口。

## 📝 开发说明

### 添加新的环境变量

1. 在 `.env` 文件中添加变量
2. 在 `.env.example` 中添加对应的模板
3. 在 `application.properties` 中使用 `${VARIABLE_NAME}` 引用

### 修改启动脚本

`run.sh` 脚本会自动：
- 检查 `.env` 文件是否存在
- 加载环境变量
- 验证项目结构
- 启动 Spring Boot 应用

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证。

## 📞 支持

如有问题，请提交 Issue 或联系项目维护者。 