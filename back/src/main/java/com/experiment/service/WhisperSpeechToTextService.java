package com.experiment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Whisper 语音识别服务
 * 通过命令行调用本地 Whisper 模型进行语音转文字
 * 包含定期清理输出目录的功能
 */
@Slf4j
@Service
public class WhisperSpeechToTextService {
    
    /**
     * 固定输出目录
     */
    private static final String FIXED_OUTPUT_DIR = "D:\\my_git\\CSdesign\\output";
    
    /**
     * 文件保留时间（小时），超过此时间的文件将被删除
     * 默认保留 24 小时
     */
    @Value("${whisper.file-retention-hours:24}")
    private int fileRetentionHours;

    /**
     * Whisper 虚拟环境路径（例如：C:\Users\18037\anaconda3\envs\whisper\Scripts\activate）
     * 如果为空，则直接使用系统 PATH 中的 whisper 命令
     */
    @Value("${whisper.venv-path:}")
    private String venvPath;

    /**
     * Whisper 模型名称（例如：small, medium, large）
     */
    @Value("${whisper.model:small}")
    private String model;

    /**
     * 语言设置（例如：Chinese, English, auto）
     * 如果为空或 auto，则不指定语言参数，让 Whisper 自动检测
     */
    @Value("${whisper.language:Chinese}")
    private String language;

    /**
     * 输出目录（可选）
     * 如果为空，则 Whisper 会在音频文件同目录下生成转录文件
     * 如果指定，则在该目录下生成转录文件
     */
    @Value("${whisper.output-dir:}")
    private String outputDir;

    /**
     * 临时文件存储目录
     */
    @Value("${whisper.temp-dir:temp/whisper}")
    private String tempDir;

    /**
     * 命令执行超时时间（秒）
     */
    @Value("${whisper.timeout:300}")
    private int timeout;
    
    /**
     * 初始化方法：确保输出目录存在
     */
    @PostConstruct
    public void init() {
        try {
            Path outputDirPath = Paths.get(FIXED_OUTPUT_DIR);
            if (!Files.exists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
                log.info("创建 Whisper 输出目录: {}", FIXED_OUTPUT_DIR);
            }
            log.info("Whisper 输出目录已准备就绪: {}", FIXED_OUTPUT_DIR);
            log.info("文件保留时间: {} 小时", fileRetentionHours);
        } catch (IOException e) {
            log.error("创建 Whisper 输出目录失败", e);
        }
    }
    
    /**
     * 定期清理输出目录中的旧文件
     * 每天凌晨 2 点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldFiles() {
        log.info("========== 开始清理 Whisper 输出目录 ==========");
        try {
            Path outputDirPath = Paths.get(FIXED_OUTPUT_DIR);
            if (!Files.exists(outputDirPath)) {
                log.info("输出目录不存在，无需清理");
                return;
            }
            
            Instant cutoffTime = Instant.now().minus(fileRetentionHours, ChronoUnit.HOURS);
            log.info("将删除 {} 小时前的文件（早于 {}）", fileRetentionHours, cutoffTime);
            
            int deletedCount = 0;
            long deletedSize = 0;
            
            try (Stream<Path> files = Files.list(outputDirPath)) {
                for (Path file : files.toArray(Path[]::new)) {
                    if (Files.isRegularFile(file)) {
                        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                        Instant fileTime = attrs.creationTime().toInstant();
                        
                        if (fileTime.isBefore(cutoffTime)) {
                            long fileSize = Files.size(file);
                            Files.delete(file);
                            deletedCount++;
                            deletedSize += fileSize;
                            log.info("删除旧文件: {} (大小: {} bytes, 创建时间: {})", 
                                file.getFileName(), fileSize, fileTime);
                        }
                    }
                }
            }
            
            log.info("========== 清理完成 ==========");
            log.info("删除文件数: {}", deletedCount);
            log.info("释放空间: {} bytes ({} MB)", deletedSize, deletedSize / 1024 / 1024);
            
        } catch (IOException e) {
            log.error("清理输出目录失败", e);
        }
    }
    
    /**
     * 手动触发清理（可选，用于测试或手动清理）
     */
    public void manualCleanup() {
        log.info("手动触发清理任务");
        cleanupOldFiles();
    }

    /**
     * 从本地文件路径识别语音（本地运行，直接使用文件路径）
     * @param audioFilePath 音频文件本地路径
     * @return 转录后的文字
     */
    public String speechToTextFromFile(String audioFilePath) {
        try {
            log.info("========== 开始调用 Whisper 语音识别（本地文件） ==========");
            log.info("音频文件路径（原始）: {}", audioFilePath);

            if (audioFilePath == null || audioFilePath.isEmpty()) {
                throw new RuntimeException("音频文件路径不能为空");
            }

            // 清理路径：去除首尾的引号和空格
            audioFilePath = audioFilePath.trim();
            if ((audioFilePath.startsWith("\"") && audioFilePath.endsWith("\"")) ||
                (audioFilePath.startsWith("'") && audioFilePath.endsWith("'"))) {
                audioFilePath = audioFilePath.substring(1, audioFilePath.length() - 1).trim();
            }
            
            log.info("音频文件路径（清理后）: {}", audioFilePath);

            // 直接使用本地文件路径调用 Whisper
            return speechToTextFromFileInternal(audioFilePath);

        } catch (RuntimeException e) {
            log.error("Whisper 语音识别失败（RuntimeException）", e);
            throw e;
        } catch (Exception e) {
            log.error("Whisper 语音识别失败（Exception）", e);
            throw new RuntimeException("Whisper 语音识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从本地文件识别语音（内部方法）
     * @param audioFilePath 音频文件路径
     * @return 转录后的文字
     */
    private String speechToTextFromFileInternal(String audioFilePath) {
        try {
            log.info("========== 开始调用 Whisper 语音识别（从文件） ==========");
            log.info("音频文件路径: {}", audioFilePath);
            log.info("使用模型: {}", model);

            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                // 提供更详细的错误信息
                String errorMsg = String.format(
                    "音频文件不存在: %s\n" +
                    "请检查：\n" +
                    "1. 文件路径是否正确\n" +
                    "2. 文件是否已被移动或删除\n" +
                    "3. 文件路径中是否包含特殊字符\n" +
                    "4. 当前工作目录: %s",
                    audioFilePath, 
                    System.getProperty("user.dir")
                );
                log.error(errorMsg);
                throw new RuntimeException("音频文件不存在: " + audioFilePath);
            }
            
            // 检查文件是否可读
            if (!audioFile.canRead()) {
                throw new RuntimeException("音频文件无法读取（权限不足）: " + audioFilePath);
            }
            
            log.info("音频文件验证通过: 路径={}, 大小={} bytes", 
                audioFilePath, audioFile.length());

            // 构建 Whisper 命令
            String whisperCommand = buildWhisperCommand(audioFilePath);
            log.info("Whisper 命令: {}", whisperCommand);

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // 如果是 Windows 系统，使用 cmd /c 执行命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                if (venvPath != null && !venvPath.isEmpty()) {
                    String venvPathTrimmed = venvPath.trim();
                    String fullCommand;
                    
                    // 判断是 conda 环境名称还是完整路径
                    if (venvPathTrimmed.contains("\\") || venvPathTrimmed.contains("/") || 
                        venvPathTrimmed.endsWith(".bat") || venvPathTrimmed.endsWith(".cmd")) {
                        // 完整路径方式（venv/virtualenv）
                        String activateCommand = venvPathTrimmed;
                        if (!activateCommand.endsWith(".bat") && !activateCommand.endsWith(".cmd")) {
                            // 如果路径是目录，自动添加 activate.bat
                            if (activateCommand.endsWith("\\") || activateCommand.endsWith("/")) {
                                activateCommand += "activate.bat";
                            } else {
                                activateCommand += "\\activate.bat";
                            }
                        }
                        // 格式：call "activate.bat" && whisper ...
                        fullCommand = String.format("call \"%s\" && %s", activateCommand, whisperCommand);
                        log.info("使用完整路径激活虚拟环境");
                    } else {
                        // conda 环境名称方式
                        // 格式：conda activate whisper && whisper ...
                        fullCommand = String.format("conda activate %s && %s", venvPathTrimmed, whisperCommand);
                        log.info("使用 conda 环境名称: {}", venvPathTrimmed);
                    }
                    
                    log.info("Windows 完整命令: {}", fullCommand);
                    processBuilder.command("cmd", "/c", fullCommand);
                } else {
                    // 如果没有配置虚拟环境路径，直接执行 whisper 命令
                    log.warn("未配置虚拟环境路径，尝试直接执行 whisper 命令（可能失败）");
                    log.warn("请在 application.yml 中配置 whisper.venv-path");
                    processBuilder.command("cmd", "/c", whisperCommand);
                }
            } else {
                // Linux/Mac 系统
                if (venvPath != null && !venvPath.isEmpty()) {
                    String venvPathTrimmed = venvPath.trim();
                    String fullCommand;
                    
                    if (venvPathTrimmed.contains("/")) {
                        // 完整路径方式
                        fullCommand = String.format("source \"%s\" && %s", venvPathTrimmed, whisperCommand);
                    } else {
                        // conda 环境名称方式
                        fullCommand = String.format("conda activate %s && %s", venvPathTrimmed, whisperCommand);
                    }
                    
                    processBuilder.command("bash", "-c", fullCommand);
                } else {
                    processBuilder.command("bash", "-c", whisperCommand);
                }
            }

            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectErrorStream(true);

            log.info("开始执行 Whisper 命令...");
            Process process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("Whisper 输出: {}", line);
                }
            }

            // 等待进程完成
            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Whisper 命令执行超时（超过 " + timeout + " 秒）");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("Whisper 命令执行失败，退出码: {}", exitCode);
                log.error("命令输出: {}", output.toString());
                
                // 提供更友好的错误信息
                String errorOutput = output.toString();
                String errorMessage = "Whisper 命令执行失败，退出码: " + exitCode;
                
                if (errorOutput.contains("不是内部或外部命令") || 
                    errorOutput.contains("不是内部或外部命令") ||
                    errorOutput.contains("command not found")) {
                    errorMessage += "\n\n可能的原因：\n" +
                        "1. 未配置虚拟环境路径（whisper.venv-path）\n" +
                        "2. 虚拟环境路径配置不正确\n" +
                        "3. whisper 命令不在系统 PATH 中\n\n" +
                        "解决方案：\n" +
                        "在 application.yml 中配置正确的虚拟环境路径，例如：\n" +
                        "whisper:\n" +
                        "  venv-path: C:\\Users\\18037\\anaconda3\\envs\\whisper\\Scripts\\activate.bat\n" +
                        "  或者只指定目录（会自动添加 activate.bat）：\n" +
                        "  venv-path: C:\\Users\\18037\\anaconda3\\envs\\whisper\\Scripts";
                }
                
                errorMessage += "\n\n命令输出: " + errorOutput;
                throw new RuntimeException(errorMessage);
            }

            log.info("Whisper 命令执行成功");

            // 读取转录结果
            // Whisper 默认会在同目录下生成 .txt 文件
            String transcriptText = readTranscriptFile(audioFilePath);
            
            log.info("========== Whisper 语音识别成功 ==========");
            log.info("转录文本长度: {} 字符", transcriptText.length());
            log.info("转录文本前200字符: {}", 
                transcriptText.length() > 200 ? transcriptText.substring(0, 200) + "..." : transcriptText);

            return transcriptText;

        } catch (RuntimeException e) {
            log.error("Whisper 语音识别失败（RuntimeException）", e);
            throw e;
        } catch (Exception e) {
            log.error("Whisper 语音识别失败（Exception）", e);
            throw new RuntimeException("Whisper 语音识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建 Whisper 命令
     */
    private String buildWhisperCommand(String audioFilePath) {
        // 转义文件路径中的空格和特殊字符
        String escapedPath = escapePath(audioFilePath);
        StringBuilder command = new StringBuilder();
        command.append("whisper \"").append(escapedPath).append("\"");
        command.append(" --model ").append(model);
        
        // 添加语言参数（如果指定了且不是 auto）
        if (language != null && !language.isEmpty() && !language.equalsIgnoreCase("auto")) {
            command.append(" --language ").append(language);
        }
        
        // 使用固定输出目录
        command.append(" --output_dir \"").append(FIXED_OUTPUT_DIR).append("\"");
        
        // 指定输出格式为 txt
        command.append(" --output_format txt");
        
        return command.toString();
    }

    /**
     * 转义文件路径
     */
    private String escapePath(String path) {
        // Windows 路径中的反斜杠需要转义
        return path.replace("\\", "\\\\");
    }

    /**
     * 读取 Whisper 生成的转录文件
     * Whisper 会在固定输出目录下生成 .txt 文件
     */
    private String readTranscriptFile(String audioFilePath) throws IOException {
        // 获取音频文件的基础名称（不含扩展名）
        String audioFileName = new File(audioFilePath).getName();
        String baseName = audioFileName.substring(0, audioFileName.lastIndexOf('.'));
        
        // 使用固定输出目录
        Path outputDirPath = Paths.get(FIXED_OUTPUT_DIR);
        
        // 确保输出目录存在
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
            log.info("创建输出目录: {}", FIXED_OUTPUT_DIR);
        }
        
        Path txtFile = outputDirPath.resolve(baseName + ".txt");
        Path jsonFile = outputDirPath.resolve(baseName + ".json");
        
        log.info("查找转录文件: {}", txtFile);

        // 优先读取 .txt 文件
        if (Files.exists(txtFile)) {
            log.info("找到转录文件: {}", txtFile);
            String content = Files.readString(txtFile, java.nio.charset.StandardCharsets.UTF_8).trim();
            // 移除时间戳格式（如果存在）
            // Whisper 的 .txt 文件可能包含时间戳，格式如：[00:00.000 --> 00:04.800] 文本内容
            return removeTimestamps(content);
        }
        
        // 如果 .txt 文件不存在，尝试读取 .json 文件
        if (Files.exists(jsonFile)) {
            log.info("找到 JSON 转录文件: {}", jsonFile);
            return readTranscriptFromJson(jsonFile);
        }
        
        // 列出输出目录中的所有文件，帮助调试
        log.error("输出目录中的文件列表:");
        try {
            Files.list(outputDirPath).forEach(path -> log.error("  - {}", path.getFileName()));
        } catch (IOException e) {
            log.error("无法列出输出目录文件", e);
        }
        
        throw new RuntimeException("未找到 Whisper 生成的转录文件: " + txtFile + " 或 " + jsonFile);
    }
    
    /**
     * 移除文本中的时间戳
     * Whisper 生成的文本可能包含时间戳格式：[00:00.000 --> 00:04.800] 文本内容
     */
    private String removeTimestamps(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // 使用正则表达式移除时间戳：[HH:MM:SS.mmm --> HH:MM:SS.mmm]
        String cleaned = text.replaceAll("\\[\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s*-->\\s*\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\]\\s*", "");
        
        // 清理多余的空行和空格
        cleaned = cleaned.replaceAll("\\n\\s*\\n", "\n").trim();
        
        return cleaned;
    }

    /**
     * 从 JSON 文件读取转录文本
     */
    private String readTranscriptFromJson(Path jsonFile) throws IOException {
        String jsonContent = Files.readString(jsonFile, java.nio.charset.StandardCharsets.UTF_8);
        // 简单的 JSON 解析，提取 text 字段
        // 如果 JSON 格式复杂，可以使用 Jackson 等库
        int textStart = jsonContent.indexOf("\"text\":\"");
        if (textStart == -1) {
            throw new RuntimeException("无法从 JSON 文件中提取转录文本");
        }
        textStart += 8; // 跳过 "text":"
        int textEnd = jsonContent.indexOf("\"", textStart);
        if (textEnd == -1) {
            throw new RuntimeException("无法从 JSON 文件中提取转录文本");
        }
        return jsonContent.substring(textStart, textEnd);
    }
}
