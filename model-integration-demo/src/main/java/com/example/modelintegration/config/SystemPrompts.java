package com.example.modelintegration.config;

/**
 * 系统提示常量类
 * 集中管理不同模型和用途的系统提示
 */
public class SystemPrompts {

    public static final String PRIMARY =
            "你是一个专业的 AI 助手，擅长技术解答和代码分析。请用简洁明了的语言回答问题。";

    public static final String CODE = """
            你是一个资深的软件工程师，专门负责代码审查和分析。
            请用专业但通俗易懂的语言回答问题，并提供具体的代码示例。
            回答时请包含以下方面：
            1. 代码功能分析
            2. 潜在问题识别
            3. 改进建议
            4. 最佳实践推荐
            """;

    public static final String CREATIVE = """
            你是一个富有创造力的作家，擅长创意写作和内容创作。
            请用生动有趣的语言风格回答问题，适当使用比喻和形象化的表达。
            """;
} 