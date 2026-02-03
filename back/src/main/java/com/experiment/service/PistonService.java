package com.experiment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Piston 代码执行服务
 * Piston 是一个免费的代码执行引擎,支持 50+ 种编程语言
 * 官方文档: https://github.com/engineer-man/piston
 */
@Slf4j
@Service
public class PistonService {

    @Value("${piston.url:https://emkc.org/api/v2/piston}")
    private String pistonUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 构造函数：初始化支持UTF-8的RestTemplate
    public PistonService() {
        this.restTemplate = new RestTemplate();
        // 添加UTF-8编码的消息转换器
        this.restTemplate.getMessageConverters().add(0, 
            new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * 语言映射表: 前端语言ID -> Piston 语言名称
     * 为了兼容性，前端使用数字ID，后端转换为Piston语言名称
     */
    private static final Map<Integer, String> LANGUAGE_MAP = new HashMap<>();
    
    static {
        // 常用语言映射
        LANGUAGE_MAP.put(71, "python");      // Python 3
        LANGUAGE_MAP.put(62, "java");        // Java
        LANGUAGE_MAP.put(63, "javascript");  // JavaScript (Node.js)
        LANGUAGE_MAP.put(50, "c");           // C
        LANGUAGE_MAP.put(54, "cpp");         // C++
        LANGUAGE_MAP.put(51, "csharp");      // C#
        LANGUAGE_MAP.put(60, "go");          // Go
        LANGUAGE_MAP.put(72, "ruby");        // Ruby
        LANGUAGE_MAP.put(73, "rust");        // Rust
        LANGUAGE_MAP.put(68, "php");         // PHP
        LANGUAGE_MAP.put(78, "kotlin");      // Kotlin
        LANGUAGE_MAP.put(82, "sql");         // SQL
        LANGUAGE_MAP.put(83, "swift");       // Swift
        LANGUAGE_MAP.put(74, "typescript");  // TypeScript
    }

    /**
     * 执行代码
     * @param sourceCode 源代码
     * @param languageId Judge0 语言ID
     * @param stdin 标准输入
     * @return 执行结果
     */
    public Map<String, Object> executeCode(String sourceCode, int languageId, String stdin) {
        try {
            // 参数验证
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                throw new IllegalArgumentException("源代码不能为空");
            }

            // 转换语言ID
            String language = LANGUAGE_MAP.get(languageId);
            if (language == null) {
                throw new IllegalArgumentException("不支持的语言ID: " + languageId);
            }

            log.info("执行代码: language={}, codeLength={}, stdinLength={}", 
                language, sourceCode.length(), stdin != null ? stdin.length() : 0);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("language", language);
            requestBody.put("version", "*"); // 使用最新版本
            
            // 文件内容
            Map<String, String> file = new HashMap<>();
            file.put("content", sourceCode);
            requestBody.put("files", Collections.singletonList(file));
            
            // 标准输入
            if (stdin != null && !stdin.isEmpty()) {
                requestBody.put("stdin", stdin);
            }

            // 设置请求头，明确指定UTF-8编码
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Accept-Charset", "UTF-8");

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("发送请求到 Piston: {}", pistonUrl + "/execute");
            log.debug("请求体: {}", jsonBody);

            // 发送请求
            String url = pistonUrl + "/execute";
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            log.info("Piston 响应状态: {}", response.getStatusCode());
            log.debug("Piston 响应体: {}", response.getBody());

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            Map<String, Object> result = new HashMap<>();
            
            // 获取运行结果
            JsonNode runNode = jsonNode.get("run");
            if (runNode != null) {
                // 标准输出 - 确保UTF-8编码正确处理
                if (runNode.has("stdout")) {
                    String stdout = runNode.get("stdout").asText();
                    
                    // 调试：输出原始字节和字符串
                    byte[] stdoutBytes = stdout.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    log.info("标准输出原始字节数: {}", stdoutBytes.length);
                    log.info("标准输出内容: {}", stdout);
                    log.info("标准输出字节(hex): {}", bytesToHex(stdoutBytes));
                    
                    result.put("stdout", stdout);
                }
                
                // 标准错误
                if (runNode.has("stderr")) {
                    String stderr = runNode.get("stderr").asText();
                    result.put("stderr", stderr);
                    if (!stderr.isEmpty()) {
                        log.warn("标准错误: {}", stderr);
                    }
                }
                
                // 退出码
                if (runNode.has("code")) {
                    int exitCode = runNode.get("code").asInt();
                    result.put("exitCode", exitCode);
                    
                    // 根据退出码设置状态
                    if (exitCode == 0) {
                        result.put("status", "通过");
                        result.put("statusId", 3); // 通过
                    } else {
                        result.put("status", "运行时错误");
                        result.put("statusId", 12); // 运行时错误
                    }
                }
                
                // 执行时间和内存(Piston 不提供,设置为 null)
                result.put("time", null);
                result.put("memory", null);
            }
            
            // 编译输出
            if (jsonNode.has("compile")) {
                JsonNode compileNode = jsonNode.get("compile");
                if (compileNode.has("output")) {
                    String compileOutput = compileNode.get("output").asText();
                    if (!compileOutput.isEmpty()) {
                        result.put("compileOutput", compileOutput);
                        result.put("status", "编译错误");
                        result.put("statusId", 6); // 编译错误
                        log.warn("编译输出: {}", compileOutput);
                    }
                }
            }
            
            // 语言信息
            if (jsonNode.has("language")) {
                result.put("language", jsonNode.get("language").asText());
            }
            if (jsonNode.has("version")) {
                result.put("version", jsonNode.get("version").asText());
            }

            return result;

        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("执行代码失败", e);
            throw new RuntimeException("代码执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的编程语言列表
     */
    public String getSupportedLanguages() {
        try {
            String url = pistonUrl + "/runtimes";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("获取语言列表失败", e);
            throw new RuntimeException("获取语言列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量执行测试用例
     * @param sourceCode 源代码
     * @param languageId 语言ID
     * @param testCases 测试用例列表 [{input, output}]
     * @return 执行结果列表
     */
    public List<Map<String, Object>> executeBatch(String sourceCode, int languageId, Map<String, String>[] testCases) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (int i = 0; i < testCases.length; i++) {
            Map<String, String> testCase = testCases[i];
            try {
                Map<String, Object> result = executeCode(
                    sourceCode,
                    languageId,
                    testCase.get("input")
                );
                
                // 检查输出是否匹配期望
                String expectedOutput = testCase.get("output");
                if (expectedOutput != null && !expectedOutput.isEmpty()) {
                    String actualOutput = (String) result.get("stdout");
                    if (actualOutput != null && actualOutput.trim().equals(expectedOutput.trim())) {
                        result.put("status", "通过");
                        result.put("statusId", 3);
                    } else {
                        result.put("status", "错误答案");
                        result.put("statusId", 4);
                        result.put("expectedOutput", expectedOutput);
                    }
                }
                
                result.put("testCaseIndex", i);
                results.add(result);
                
            } catch (Exception e) {
                log.error("测试用例 {} 执行失败", i, e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("testCaseIndex", i);
                errorResult.put("status", "执行错误");
                errorResult.put("statusId", 14);
                errorResult.put("message", e.getMessage());
                results.add(errorResult);
            }
        }
        
        return results;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDescription(int statusId) {
        switch (statusId) {
            case 3: return "通过";
            case 4: return "错误答案";
            case 5: return "超时";
            case 6: return "编译错误";
            case 12: return "运行时错误";
            case 14: return "执行错误";
            default: return "未知状态";
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串（用于调试）
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
