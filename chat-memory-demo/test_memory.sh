#!/bin/bash

BASE_URL="http://localhost:8080/api/chat"

echo "=== 开始测试 Chat Memory 功能 ==="

# 检查服务是否运行
echo "检查服务状态..."
curl -s "$BASE_URL/health" | jq '.'

if [ $? -ne 0 ]; then
    echo "错误: 无法连接到服务，请确保应用已启动"
    exit 1
fi

# 1. 创建新对话
echo "1. 创建新对话..."
response=$(curl -s -X POST "$BASE_URL/new")
conversation_id=$(echo $response | jq -r '.conversationId')
echo "对话ID: $conversation_id"

# 2. 使用 Advisor 方式进行对话
echo -e "\n2. 第一轮对话（Advisor方式）- 自我介绍..."
curl -s -X POST "$BASE_URL/$conversation_id/advisor" \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，我叫张三，是一名 Java 开发者，今年 28 岁。"}' | jq '.'

echo -e "\n3. 第二轮对话（Advisor方式）- 测试记忆..."
curl -s -X POST "$BASE_URL/$conversation_id/advisor" \
  -H "Content-Type: application/json" \
  -d '{"message": "我刚才说的年龄是多少？"}' | jq '.'

# 4. 查看对话历史
echo -e "\n4. 查看对话历史..."
curl -s -X GET "$BASE_URL/$conversation_id/history" | jq '.'

# 5. 测试客服场景
echo -e "\n5. 测试客服场景 - 首次咨询..."
curl -s -X POST "$BASE_URL/customer-service/new" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust_001",
    "customerName": "李四",
    "inquiry": "我的订单12345什么时候发货？"
  }' | jq '.'

echo -e "\n6. 测试客服场景 - 继续对话..."
curl -s -X POST "$BASE_URL/customer-service/cust_001/continue" \
  -H "Content-Type: application/json" \
  -d '{"message": "我刚才问的订单有什么进展吗？"}' | jq '.'

echo -e "\n=== 测试完成 ===" 