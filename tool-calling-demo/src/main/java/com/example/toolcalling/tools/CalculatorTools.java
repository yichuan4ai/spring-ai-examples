package com.example.toolcalling.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @Tool(description = "执行基本的数学运算：加法、减法、乘法、除法")
    public String calculate(String expression) {
        try {
            // 简单的表达式计算（实际项目中建议使用专业的表达式计算库）
            String cleanExpression = expression.replace(" ", "");
            
            if (cleanExpression.contains("+")) {
                String[] parts = cleanExpression.split("\\+");
                if (parts.length == 2) {
                    double result = Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
                    return formatResult(result);
                }
            } else if (cleanExpression.contains("-") && !cleanExpression.startsWith("-")) {
                String[] parts = cleanExpression.split("-");
                if (parts.length == 2) {
                    double result = Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
                    return formatResult(result);
                }
            } else if (cleanExpression.contains("*")) {
                String[] parts = cleanExpression.split("\\*");
                if (parts.length == 2) {
                    double result = Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
                    return formatResult(result);
                }
            } else if (cleanExpression.contains("/")) {
                String[] parts = cleanExpression.split("/");
                if (parts.length == 2) {
                    double divisor = Double.parseDouble(parts[1]);
                    if (divisor == 0) {
                        return "错误：除数不能为零";
                    }
                    double result = Double.parseDouble(parts[0]) / divisor;
                    return formatResult(result);
                }
            }
            
            return "不支持的运算表达式: " + expression + "。请使用简单的两数运算，如 '10 + 5'";
        } catch (NumberFormatException e) {
            return "计算错误：无效的数字格式";
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }

    @Tool(description = "计算两个数字的幂运算，返回 base 的 exponent 次方")
    public String power(double base, double exponent) {
        try {
            double result = Math.pow(base, exponent);
            return formatResult(result);
        } catch (Exception e) {
            return "幂运算错误: " + e.getMessage();
        }
    }

    @Tool(description = "计算数字的平方根")
    public String squareRoot(double number) {
        if (number < 0) {
            return "错误：不能计算负数的平方根";
        }
        double result = Math.sqrt(number);
        return formatResult(result);
    }

    @Tool(description = "计算百分比，例如计算 50 是 200 的百分之几")
    public String percentage(double part, double whole) {
        if (whole == 0) {
            return "错误：总数不能为零";
        }
        double result = (part / whole) * 100;
        return formatResult(result) + "%";
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.format("%.2f", result);
        }
    }
} 