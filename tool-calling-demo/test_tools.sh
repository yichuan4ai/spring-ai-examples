#!/bin/bash

echo "🧪 工具调用功能测试"
echo "===================="

BASE_URL="http://localhost:8080/api/tools"

# 检查服务状态
echo "🏥 检查服务状态..."
curl -s -X GET $BASE_URL/health | jq . || echo "请安装 jq 命令或忽略 JSON 格式化"

echo -e "\n"

# 测试时间工具
echo "📅 测试时间查询..."
curl -s -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点了？"}' | jq . || echo "时间查询请求已发送"

echo -e "\n"

# 测试计算器工具  
echo "🧮 测试计算器功能..."
curl -s -X POST $BASE_URL/calculator \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我计算 123 + 456"}' | jq . || echo "计算器请求已发送"

echo -e "\n"

# 测试天气工具
echo "🌤️ 测试天气查询..."
curl -s -X POST $BASE_URL/weather \
  -H "Content-Type: application/json" \
  -d '{"message": "北京今天天气怎么样？"}' | jq . || echo "天气查询请求已发送"

echo -e "\n"

# 测试文件操作
echo "📁 测试文件操作..."
curl -s -X POST $BASE_URL/files \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我创建一个名为 hello.txt 的文件，内容是：Hello Spring AI Tools!"}' | jq . || echo "文件操作请求已发送"

echo -e "\n"

# 测试数据库查询
echo "💾 测试数据库查询..."
curl -s -X POST $BASE_URL/database \
  -H "Content-Type: application/json" \
  -d '{"message": "查询用户表中有多少用户"}' | jq . || echo "数据库查询请求已发送"

echo -e "\n"

# 测试复合功能
echo "🔄 测试复合工具调用..."
curl -s -X POST $BASE_URL/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点了？然后帮我算一下 100 * 50 等于多少？最后告诉我上海的天气"}' | jq . || echo "复合功能请求已发送"

echo -e "\n"

# 测试基础工具组合
echo "⚙️ 测试基础工具组合..."
curl -s -X POST $BASE_URL/basic \
  -H "Content-Type: application/json" \
  -d '{"message": "现在几点，温度多少度，计算一下体感温度"}' | jq . || echo "基础工具组合请求已发送"

echo -e "\n"

# 测试数据工具组合
echo "📊 测试数据工具组合..."
curl -s -X POST $BASE_URL/data \
  -H "Content-Type: application/json" \
  -d '{"message": "列出所有文件，然后查询数据库中的用户信息"}' | jq . || echo "数据工具组合请求已发送"

echo -e "\n"

# 直接工具调用测试
echo "🔧 测试直接工具调用..."
echo "列出文件："
curl -s -X GET $BASE_URL/list-files | jq . || echo "列出文件请求已发送"

echo -e "\n用户统计："
curl -s -X GET $BASE_URL/user-count | jq . || echo "用户统计请求已发送"

echo -e "\n"
echo "✅ 测试完成！"
echo "💡 提示：如果看到错误信息，请确保："
echo "   1. 应用程序正在运行"
echo "   2. 环境变量 DEEPSEEK_API_KEY 已设置"
echo "   3. 端口 8080 没有被占用" 