package com.example.toolcalling.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileOperationTools {

    private final String workDirectory;

    public FileOperationTools(@Value("${app.workspace.directory:./workspace/}") String workDirectory) {
        this.workDirectory = workDirectory;
        // 确保工作目录存在
        try {
            Files.createDirectories(Paths.get(this.workDirectory));
        } catch (IOException e) {
            System.err.println("无法创建工作目录: " + e.getMessage());
        }
    }

    @Tool(description = "创建一个新文件并写入内容")
    public String createFile(String fileName, String content) {
        try {
            // 验证文件名安全性
            if (!isValidFileName(fileName)) {
                return "无效的文件名：" + fileName + "。文件名不能包含路径分隔符或特殊字符。";
            }

            Path filePath = Paths.get(workDirectory + fileName);
            Files.write(filePath, content.getBytes());
            return "文件 " + fileName + " 创建成功，内容长度：" + content.length() + " 字符";
        } catch (IOException e) {
            return "创建文件失败：" + e.getMessage();
        }
    }

    @Tool(description = "读取文件内容")
    public String readFile(String fileName) {
        try {
            if (!isValidFileName(fileName)) {
                return "无效的文件名：" + fileName;
            }

            Path filePath = Paths.get(workDirectory + fileName);
            if (!Files.exists(filePath)) {
                return "文件 " + fileName + " 不存在";
            }
            String content = Files.readString(filePath);
            return "文件 " + fileName + " 的内容：\n" + content;
        } catch (IOException e) {
            return "读取文件失败：" + e.getMessage();
        }
    }

    @Tool(description = "列出工作目录中的所有文件")
    public String listFiles() {
        try {
            List<String> files = Files.list(Paths.get(workDirectory))
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();

            if (files.isEmpty()) {
                return "工作目录为空";
            }

            return "工作目录中的文件列表：\n" + String.join("\n", files);
        } catch (IOException e) {
            return "列出文件失败：" + e.getMessage();
        }
    }

    @Tool(description = "删除指定的文件")
    public String deleteFile(String fileName) {
        try {
            if (!isValidFileName(fileName)) {
                return "无效的文件名：" + fileName;
            }

            Path filePath = Paths.get(workDirectory + fileName);
            if (!Files.exists(filePath)) {
                return "文件 " + fileName + " 不存在";
            }

            Files.delete(filePath);
            return "文件 " + fileName + " 删除成功";
        } catch (IOException e) {
            return "删除文件失败：" + e.getMessage();
        }
    }

    @Tool(description = "检查文件是否存在")
    public String fileExists(String fileName) {
        if (!isValidFileName(fileName)) {
            return "无效的文件名：" + fileName;
        }

        Path filePath = Paths.get(workDirectory + fileName);
        boolean exists = Files.exists(filePath);
        return "文件 " + fileName + (exists ? " 存在" : " 不存在");
    }

    @Tool(description = "获取文件信息（大小、修改时间等）")
    public String getFileInfo(String fileName) {
        try {
            if (!isValidFileName(fileName)) {
                return "无效的文件名：" + fileName;
            }

            Path filePath = Paths.get(workDirectory + fileName);
            if (!Files.exists(filePath)) {
                return "文件 " + fileName + " 不存在";
            }

            long size = Files.size(filePath);
            String lastModified = Files.getLastModifiedTime(filePath).toString();
            
            return String.format("""
                文件信息：%s
                大小：%d 字节
                最后修改时间：%s
                """, fileName, size, lastModified);
        } catch (IOException e) {
            return "获取文件信息失败：" + e.getMessage();
        }
    }

    // 验证文件名安全性
    private boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        // 不允许路径分隔符和一些危险字符
        return !fileName.contains("/") && !fileName.contains("\\") 
               && !fileName.contains("..") && !fileName.startsWith(".");
    }
} 