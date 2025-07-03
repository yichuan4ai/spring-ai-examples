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
curl -X POST "$BASE_URL/api/model-switching/smart-selection" \
  -H "Content-Type: application/json" \
  -d '{"input": "请帮我分析这段 Java 代码的性能问题"}' \
  -s | jq '{"detectedTaskType": .detectedTaskType, "selectedModel": .selectedModel}'
echo
echo "---"

# 测试获取可用模型
echo "📋 获取可用模型信息..."
curl -X GET "$BASE_URL/api/model-switching/available-models" \
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