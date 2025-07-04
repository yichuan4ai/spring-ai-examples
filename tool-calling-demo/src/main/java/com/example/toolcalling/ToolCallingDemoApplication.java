package com.example.toolcalling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolCallingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolCallingDemoApplication.class, args);
        
        System.out.println("🚀 Tool Calling Demo 启动成功！");
        System.out.println("📖 访问 http://localhost:8080/api/tools/health 检查服务状态");
        System.out.println("🔧 可用的API端点：");
        System.out.println("   POST /api/tools/chat - 综合工具调用");
        System.out.println("   POST /api/tools/calculator - 计算器功能");
        System.out.println("   POST /api/tools/weather - 天气查询");
        System.out.println("   POST /api/tools/files - 文件操作");
        System.out.println("   POST /api/tools/database - 数据库查询");
        System.out.println("   GET  /api/tools/list-files - 列出文件");
        System.out.println("   GET  /api/tools/user-count - 用户统计");
        System.out.println("💡 更多详情请查看 doc5.md 文档");
    }
} 