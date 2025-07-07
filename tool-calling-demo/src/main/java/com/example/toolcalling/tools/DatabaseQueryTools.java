package com.example.toolcalling.tools;

import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseQueryTools {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseQueryTools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "查询用户表中的用户信息，可以按姓名或ID查询")
    public String queryUser(String searchTerm) {
        try {
            String sql;
            Object[] params;

            // 判断是按ID还是按姓名查询
            if (searchTerm.matches("\\d+")) {
                sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
                params = new Object[]{Integer.parseInt(searchTerm)};
            } else {
                sql = "SELECT id, name, email, created_at FROM users WHERE name LIKE ?";
                params = new Object[]{"%" + searchTerm + "%"};
            }

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

            if (results.isEmpty()) {
                return "未找到匹配的用户：" + searchTerm;
            }

            StringBuilder sb = new StringBuilder("查询结果：\n");
            for (Map<String, Object> row : results) {
                sb.append(String.format("ID: %s, 姓名: %s, 邮箱: %s, 创建时间: %s\n",
                        row.get("id"), row.get("name"), row.get("email"), row.get("created_at")));
            }

            return sb.toString();

        } catch (Exception e) {
            return "数据库查询失败：" + e.getMessage();
        }
    }

    @Tool(description = "获取用户总数统计")
    public String getUserCount() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            return "系统中共有 " + count + " 个用户";
        } catch (Exception e) {
            return "获取用户数量失败：" + e.getMessage();
        }
    }

    @Tool(description = "获取所有用户的列表")
    public String getAllUsers() {
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT id, name, email, created_at FROM users ORDER BY id");

            if (results.isEmpty()) {
                return "用户表中没有数据";
            }

            StringBuilder sb = new StringBuilder("所有用户列表：\n");
            for (Map<String, Object> row : results) {
                sb.append(String.format("ID: %s, 姓名: %s, 邮箱: %s, 创建时间: %s\n",
                        row.get("id"), row.get("name"), row.get("email"), row.get("created_at")));
            }

            return sb.toString();
        } catch (Exception e) {
            return "获取用户列表失败：" + e.getMessage();
        }
    }

    @Tool(description = "根据邮箱查询用户")
    public String getUserByEmail(String email) {
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT id, name, email, created_at FROM users WHERE email = ?", email);

            if (results.isEmpty()) {
                return "未找到邮箱为 " + email + " 的用户";
            }

            Map<String, Object> user = results.get(0);
            return String.format("找到用户：ID: %s, 姓名: %s, 邮箱: %s, 创建时间: %s",
                    user.get("id"), user.get("name"), user.get("email"), user.get("created_at"));

        } catch (Exception e) {
            return "邮箱查询失败：" + e.getMessage();
        }
    }

    @Tool(description = "获取最近注册的用户")
    public String getRecentUsers(int limit) {
        try {
            if (limit <= 0 || limit > 20) {
                return "查询数量必须在1-20之间";
            }

            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT id, name, email, created_at FROM users ORDER BY created_at DESC LIMIT ?", limit);

            if (results.isEmpty()) {
                return "没有找到用户";
            }

            StringBuilder sb = new StringBuilder("最近 " + limit + " 个注册的用户：\n");
            for (Map<String, Object> row : results) {
                sb.append(String.format("ID: %s, 姓名: %s, 邮箱: %s, 创建时间: %s\n",
                        row.get("id"), row.get("name"), row.get("email"), row.get("created_at")));
            }

            return sb.toString();
        } catch (Exception e) {
            return "获取最近用户失败：" + e.getMessage();
        }
    }

    @Tool(description = "检查数据库连接状态")
    public String checkDatabaseConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "数据库连接正常，测试查询返回：" + result;
        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }
} 