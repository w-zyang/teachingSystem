package com.experiment.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通义千问HTTP调用工具类
 */
@Slf4j
@Component
public class DashScopeHttpUtil {
    
    private static final String BASE_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    
    /**
     * 发送聊天请求
     */
    public static String sendChatRequest(String apiKey, String model, List<Map<String, String>> messages) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("input", Map.of("messages", messages));
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", 0.7);
            parameters.put("top_p", 0.8);
            parameters.put("max_tokens", 2048);
            parameters.put("result_format", "message");
            
            requestBody.put("parameters", parameters);
            
            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .timeout(Duration.ofSeconds(60))
                    .build();
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                Map<String, Object> output = (Map<String, Object>) responseMap.get("output");
                List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return (String) message.get("content");
                }
            }
            
            log.error("API调用失败，状态码: {}, 响应: {}", response.statusCode(), response.body());
            return "API调用失败";
            
        } catch (IOException | InterruptedException e) {
            log.error("HTTP请求异常", e);
            return "请求异常: " + e.getMessage();
        }
    }
    
    /**
     * 简单文本对话
     */
    public static String simpleChat(String apiKey, String message) {
        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", message)
        );
        return sendChatRequest(apiKey, "qwen-plus", messages);
    }
    
    /**
     * 带系统提示的对话
     */
    public static String chatWithSystem(String apiKey, String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );
        return sendChatRequest(apiKey, "qwen-plus", messages);
    }
} 