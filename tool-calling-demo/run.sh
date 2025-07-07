#!/bin/bash

# Tool Calling Demo 启动脚本
echo "🚀 正在启动 Tool Calling Demo..."

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
    echo "💡 提示：请设置环境变量 DEEPSEEK_API_KEY"
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
check_env_var "DEEPSEEK_API_KEY"

# 检查是否存在 pom.xml 文件
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误：未找到 pom.xml 文件，请确保在正确的项目目录中运行此脚本"
    exit 1
fi

echo "🔧 启动应用..."

# 设置 JVM 参数
export JAVA_OPTS="-Xmx1024m -Xms512m"

# 检查 Java 版本
echo "☕ 检查 Java 版本..."
java -version

# 启动应用
echo "🎯 启动 Spring Boot 应用..."
./mvnw clean spring-boot:run \
    -Dspring-boot.run.jvmArguments="$JAVA_OPTS" \
    -Dspring.profiles.active=default

echo "👋 应用已停止运行" 