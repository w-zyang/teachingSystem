package com.experiment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Judge0 代码评测服务
 */
@Slf4j
@Service
public class Judge0Service {

    @Value("${judge0.url:http://localhost:2358}")
    private String judge0Url;

    @Value("${judge0.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 提交代码到 Judge0
     * @param sourceCode 源代码
     * @param languageId 语言ID (例如: 71=Python, 62=Java, 63=JavaScript)
     * @param stdin 标准输入
     * @param expectedOutput 期望输出
     * @return 提交token
     */
    public String submitCode(String sourceCode, int languageId, String stdin, String expectedOutput) {
        try {
            // 参数验证
            if (sourceCode == null || sourceCode.trim().isEmpty()) {
                throw new IllegalArgumentException("源代码不能为空");
            }
            if (languageId <= 0) {
                throw new IllegalArgumentException("语言ID无效: " + languageId);
            }

            String url = judge0Url + "/submissions?base64_encoded=true&wait=false";

            // 构建请求体
            String encodedSourceCode = encodeBase64(sourceCode);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("source_code", encodedSourceCode);
            requestBody.put("language_id", languageId);
            
            if (stdin != null && !stdin.isEmpty()) {
                requestBody.put("stdin", encodeBase64(stdin));
            }
            
            if (expectedOutput != null && !expectedOutput.isEmpty()) {
                requestBody.put("expected_output", encodeBase64(expectedOutput));
            }

            // 打印调试信息
            log.info("提交代码到Judge0: languageId={}, codeLength={}", languageId, sourceCode.length());
            log.info("编码后的代码: {}", encodedSourceCode);
            log.info("请求体内容: {}", objectMapper.writeValueAsString(requestBody));
            log.debug("请求体: {}", requestBody);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // 如果配置了 API Key（RapidAPI），添加认证头
            if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("YOUR_RAPIDAPI_KEY_HERE")) {
                headers.set("X-RapidAPI-Key", apiKey);
                headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
                log.info("使用 RapidAPI 认证");
            }

            // 将 Map 转换为 JSON 字符串
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            // 打印完整的请求信息
            log.info("发送请求到: {}", url);
            log.info("请求头: {}", headers);
            log.info("请求体JSON: {}", jsonBody);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            log.info("Judge0响应状态: {}", response.getStatusCode());
            log.info("Judge0响应体: {}", response.getBody());

            // 解析响应获取token
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String token = jsonNode.get("token").asText();
            
            log.info("代码提交成功，token: {}", token);
            return token;

        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("提交代码到Judge0失败", e);
            throw new RuntimeException("代码提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取提交结果
     * @param token 提交token
     * @return 结果JSON
     */
    public Map<String, Object> getSubmissionResult(String token) {
        try {
            String url = judge0Url + "/submissions/" + token + "?base64_encoded=true";

            log.info("获取Judge0结果，token: {}, url: {}", token, url);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // 如果配置了 API Key（RapidAPI），添加认证头
            if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("YOUR_RAPIDAPI_KEY_HERE")) {
                headers.set("X-RapidAPI-Key", apiKey);
                headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            log.info("Judge0结果响应状态: {}", response.getStatusCode());
            log.info("Judge0结果响应体: {}", response.getBody());
            
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> result = new HashMap<>();
            
            // 获取状态信息
            int statusId = jsonNode.get("status").get("id").asInt();
            String statusDescription = jsonNode.get("status").get("description").asText();
            
            result.put("status", getStatusDescription(statusId));
            result.put("statusId", statusId);
            result.put("statusDescription", statusDescription);
            
            log.info("执行状态: id={}, description={}, 中文={}", statusId, statusDescription, getStatusDescription(statusId));
            
            // 解码输出
            if (jsonNode.has("stdout") && !jsonNode.get("stdout").isNull()) {
                String stdout = decodeBase64(jsonNode.get("stdout").asText());
                result.put("stdout", stdout);
                log.info("标准输出: {}", stdout);
            }
            
            if (jsonNode.has("stderr") && !jsonNode.get("stderr").isNull()) {
                String stderr = decodeBase64(jsonNode.get("stderr").asText());
                result.put("stderr", stderr);
                log.warn("标准错误: {}", stderr);
            }
            
            if (jsonNode.has("compile_output") && !jsonNode.get("compile_output").isNull()) {
                String compileOutput = decodeBase64(jsonNode.get("compile_output").asText());
                result.put("compileOutput", compileOutput);
                log.info("编译输出: {}", compileOutput);
            }
            
            if (jsonNode.has("message") && !jsonNode.get("message").isNull()) {
                String message = decodeBase64(jsonNode.get("message").asText());
                result.put("message", message);
                log.warn("消息: {}", message);
            }

            // 时间和内存
            if (jsonNode.has("time") && !jsonNode.get("time").isNull()) {
                result.put("time", jsonNode.get("time").asText());
            }
            
            if (jsonNode.has("memory") && !jsonNode.get("memory").isNull()) {
                result.put("memory", jsonNode.get("memory").asInt());
            }

            return result;

        } catch (Exception e) {
            log.error("获取Judge0结果失败", e);
            throw new RuntimeException("获取结果失败: " + e.getMessage());
        }
    }

    /**
     * 批量提交测试用例
     * @param sourceCode 源代码
     * @param languageId 语言ID
     * @param testCases 测试用例列表 [{input, output}]
     * @return 提交tokens列表
     */
    public String[] submitBatch(String sourceCode, int languageId, Map<String, String>[] testCases) {
        String[] tokens = new String[testCases.length];
        
        for (int i = 0; i < testCases.length; i++) {
            Map<String, String> testCase = testCases[i];
            tokens[i] = submitCode(
                sourceCode,
                languageId,
                testCase.get("input"),
                testCase.get("output")
            );
        }
        
        return tokens;
    }

    /**
     * 获取支持的编程语言列表
     */
    public String getSupportedLanguages() {
        try {
            String url = judge0Url + "/languages";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("获取语言列表失败", e);
            throw new RuntimeException("获取语言列表失败: " + e.getMessage());
        }
    }

    /**
     * Base64 编码
     */
    private String encodeBase64(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        try {
            return Base64.getEncoder().encodeToString(str.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("Base64编码失败: {}", e.getMessage());
            return Base64.getEncoder().encodeToString(str.getBytes());
        }
    }

    /**
     * Base64 解码
     */
    private String decodeBase64(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        try {
            // 检查是否是有效的 Base64 字符串
            if (!str.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                // 不是 Base64，直接返回原字符串
                return str;
            }
            return new String(Base64.getDecoder().decode(str), "UTF-8");
        } catch (Exception e) {
            // 解码失败，返回原字符串
            log.debug("Base64解码失败，返回原字符串: {}", e.getMessage());
            return str;
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDescription(int statusId) {
        switch (statusId) {
            case 1: return "队列中";
            case 2: return "处理中";
            case 3: return "通过";
            case 4: return "错误答案";
            case 5: return "超时";
            case 6: return "编译错误";
            case 7: return "运行时错误 (SIGSEGV)";
            case 8: return "运行时错误 (SIGXFSZ)";
            case 9: return "运行时错误 (SIGFPE)";
            case 10: return "运行时错误 (SIGABRT)";
            case 11: return "运行时错误 (NZEC)";
            case 12: return "运行时错误 (其他)";
            case 13: return "内部错误";
            case 14: return "执行错误";
            default: return "未知状态";
        }
    }
}
