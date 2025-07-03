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