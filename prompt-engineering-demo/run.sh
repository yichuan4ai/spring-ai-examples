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