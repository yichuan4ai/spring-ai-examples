# Spring AI 工具调用示例项目

这是一个基于 Spring AI 的工具调用（Tool Calling）示例项目，展示了如何使用 `@Tool` 注解创建各种 AI 可调用的工具。

## 🚀 快速开始

### 前置要求

- JDK 21 或更高版本
- Maven 3.6 或更高版本
- DeepSeek API Key

### 配置环境

1. 设置环境变量：
```bash
export DEEPSEEK_API_KEY=your_deepseek_api_key
```

2. 或者创建 `.env` 文件：
```bash
echo "DEEPSEEK_API_KEY=your_deepseek_api_key" > .env
```

### 启动应用

```bash
# 给脚本执行权限
chmod +x run.sh test_tools.sh

# 启动应用
./run.sh
```

## 🛠️ 可用工具

### 1. 时间工具 (DateTimeTools)
- 获取当前日期和时间
- 获取指定时区的时间
- 获取星期几

### 2. 计算器工具 (CalculatorTools)
- 基本数学运算（加减乘除）
- 幂运算
- 平方根计算
- 百分比计算

### 3. 天气工具 (WeatherTools)
- 查询城市天气信息（模拟数据）
- 天气对比
- 穿衣建议

### 4. 文件操作工具 (FileOperationTools)
- 创建文件
- 读取文件
- 列出文件
- 删除文件
- 获取文件信息

### 5. 数据库查询工具 (DatabaseQueryTools)
- 查询用户信息
- 用户统计
- 数据库连接状态检查

## 🌐 API 端点

### 综合工具调用
```bash
POST /api/tools/chat
Content-Type: application/json

{
  "message": "现在几点了？然后帮我算一下 100 * 50 等于多少？"
}
```

### 单一工具调用
```bash
# 计算器
POST /api/tools/calculator
{
  "message": "帮我计算 123 + 456"
}

# 天气查询
POST /api/tools/weather
{
  "message": "北京今天天气怎么样？"
}

# 文件操作
POST /api/tools/files
{
  "message": "创建一个名为 hello.txt 的文件"
}

# 数据库查询
POST /api/tools/database
{
  "message": "查询用户表中有多少用户"
}
```

### 直接工具调用
```bash
# 列出文件
GET /api/tools/list-files

# 获取用户统计
GET /api/tools/user-count

# 健康检查
GET /api/tools/health
```

## 🧪 运行测试

```bash
# 启动应用后，在新终端运行测试
./test_tools.sh
```

测试脚本会自动测试所有工具的功能。

## 📁 项目结构

```
tool-calling-demo/
├── src/main/java/com/example/toolcalling/
│   ├── ToolCallingDemoApplication.java     # 主启动类
│   ├── config/
│   │   └── ToolConfiguration.java          # 配置类
│   ├── controller/
│   │   └── ToolCallingController.java      # 控制器
│   ├── service/
│   │   └── ToolCallingChatService.java     # 服务类
│   └── tools/                              # 工具类目录
│       ├── DateTimeTools.java              # 时间工具
│       ├── CalculatorTools.java            # 计算器工具
│       ├── WeatherTools.java               # 天气工具
│       ├── FileOperationTools.java         # 文件操作工具
│       └── DatabaseQueryTools.java         # 数据库查询工具
├── src/main/resources/
│   ├── application.properties              # 应用配置
│   └── data.sql                           # 数据库初始化脚本
├── pom.xml                                # Maven 配置
├── run.sh                                 # 启动脚本
├── test_tools.sh                          # 测试脚本
└── README.md                              # 项目说明
```

## 💡 使用示例

### 复合工具调用
AI 会根据用户输入自动选择和组合多个工具：

```bash
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点了？然后算一下 100+200，最后告诉我北京天气"}'
```

### 智能工具选择
AI 会根据问题内容自动选择最合适的工具：

```bash
# 自动选择时间工具
{"message": "现在是什么时候？"}

# 自动选择计算器工具
{"message": "帮我算一下 50 * 80"}

# 自动选择天气工具
{"message": "今天适合穿什么衣服？上海天气"}

# 自动选择文件工具
{"message": "创建一个记录今天学习内容的文件"}

# 自动选择数据库工具
{"message": "有多少个注册用户？"}
```

## 🔧 扩展开发

### 添加新工具

1. 创建工具类：
```java
@Component
public class YourCustomTools {
    
    @Tool(description = "工具功能描述")
    public String yourMethod(String param) {
        // 工具逻辑
        return "结果";
    }
}
```

2. 在服务中注册：
```java
@Service
public class ToolCallingChatService {
    // 注入新工具
    private final YourCustomTools customTools;
    
    // 在 chatClient 中添加新工具
    .tools(dateTimeTools, calculatorTools, customTools)
}
```

### 集成真实 API

修改 `WeatherTools` 等工具类，将模拟数据替换为真实 API 调用。

## 🛡️ 安全注意事项

1. **输入验证**：所有工具都包含输入验证逻辑
2. **文件安全**：文件操作限制在工作目录内
3. **数据库安全**：使用参数化查询防止 SQL 注入
4. **权限控制**：可以添加用户权限检查

## 📚 相关文档

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Tool Calling 指南](../doc5.md)
- [DeepSeek API 文档](https://platform.deepseek.com/docs)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## �� 许可证

MIT License 