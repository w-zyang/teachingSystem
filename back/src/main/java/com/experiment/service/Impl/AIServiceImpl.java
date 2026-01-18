package com.experiment.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.experiment.config.DashScopeConfig;
import com.experiment.utils.AliOssProperties;
import com.experiment.pojo.ChatMessage;
import com.experiment.pojo.ChatRequest;
import com.experiment.pojo.ChatResponse;
import com.experiment.service.AIService;
import com.experiment.utils.AliOssUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * AI服务实现类
 */
@Slf4j
@Service
public class AIServiceImpl implements AIService {
    
    @Autowired
    private DashScopeConfig dashScopeConfig;
    
    @Autowired
    private AliOssProperties aliOssProperties;
    
    @Autowired
    private com.experiment.service.WhisperSpeechToTextService whisperSpeechToTextService;
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            // 设置API Key
            String apiKey = dashScopeConfig.getApiKey();
            if (apiKey != null && !apiKey.isEmpty() && !"your-api-key-here".equals(apiKey)) {
                System.setProperty("DASHSCOPE_API_KEY", apiKey);
            } else {
                log.warn("API密钥未配置或无效，使用模拟AI响应");
                return generateMockResponse(request);
            }
            
            // 构建消息列表
            List<Message> messages = new ArrayList<>();
            for (ChatMessage chatMessage : request.getMessages()) {
                messages.add(Message.builder()
                        .role(chatMessage.getRole().toLowerCase())
                        .content(chatMessage.getContent())
                        .build());
            }
            
            // 构建参数
            QwenParam param = QwenParam.builder()
                    .model(request.getModel())
                    .messages(messages)
                    .temperature(request.getTemperature())
                    .topP(request.getTopP().doubleValue())
                    .maxTokens(request.getMaxTokens())
                    .resultFormat("message")
                    .build();
            
            // 调用API
            Generation generation = new Generation();
            GenerationResult result = generation.call(param);
            
            // 构建响应
            ChatResponse response = new ChatResponse();
            response.setStatusCode(200);
            response.setRequestId(result.getRequestId());
            
            ChatResponse.Output output = new ChatResponse.Output();
            output.setText(result.getOutput().getText());
            output.setFinishReason(result.getOutput().getFinishReason());
            
            if (result.getOutput().getChoices() != null && !result.getOutput().getChoices().isEmpty()) {
                ChatResponse.Choice choice = new ChatResponse.Choice();
                choice.setFinishReason(result.getOutput().getChoices().get(0).getFinishReason());
                
                ChatMessage message = new ChatMessage();
                message.setRole(result.getOutput().getChoices().get(0).getMessage().getRole());
                message.setContent(result.getOutput().getChoices().get(0).getMessage().getContent());
                choice.setMessage(message);
                
                output.setChoices(new ChatResponse.Choice[]{choice});
            }
            
            response.setOutput(output);
            
            // 设置Token使用情况
            if (result.getUsage() != null) {
                ChatResponse.Usage usage = new ChatResponse.Usage();
                usage.setInputTokens(result.getUsage().getInputTokens());
                usage.setOutputTokens(result.getUsage().getOutputTokens());
                usage.setTotalTokens(result.getUsage().getTotalTokens());
                response.setUsage(usage);
            }
            
            return response;
            
        } catch (NoApiKeyException e) {
            log.error("API Key未设置", e);
            return generateMockResponse(request);
        } catch (InputRequiredException e) {
            log.error("输入参数错误", e);
            return buildErrorResponse("输入参数错误", e.getMessage());
        } catch (ApiException e) {
            log.error("API调用异常", e);
            return generateMockResponse(request);
        } catch (Exception e) {
            log.error("未知异常", e);
            return generateMockResponse(request);
        }
    }
    
    @Override
    public String simpleChat(String message) {
        ChatRequest request = new ChatRequest();
        request.setModel(dashScopeConfig.getModel());
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", message));
        request.setMessages(messages);
        
        ChatResponse response = chat(request);
        
        if (response.getStatusCode() == 200 && response.getOutput() != null && 
            response.getOutput().getChoices() != null && response.getOutput().getChoices().length > 0) {
            return response.getOutput().getChoices()[0].getMessage().getContent();
        } else {
            return "抱歉，AI服务暂时不可用：" + response.getMessage();
        }
    }
    
    @Override
    public String chatWithSystem(String systemPrompt, String userMessage) {
        return chatWithSystemWithRetry(systemPrompt, userMessage, 3);
    }
    
    /**
     * 带重试机制的AI对话（使用百炼应用API）
     */
    private String chatWithSystemWithRetry(String systemPrompt, String userMessage, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String apiKey = dashScopeConfig.getApiKey();
                String appId = dashScopeConfig.getAppId();
                
                if (apiKey == null || apiKey.isEmpty()) {
                    log.warn("API密钥未配置");
                    return "API密钥未配置，无法调用AI服务。";
                }
                
                if (appId == null || appId.isEmpty()) {
                    log.warn("应用ID未配置");
                    return "应用ID未配置，无法调用百炼应用。";
                }

                String url = "https://dashscope.aliyuncs.com/api/v1/apps/" + appId + "/completion";
                org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(15000);  // 连接超时：15秒
                factory.setReadTimeout(120000);    // 读取超时：120秒（2分钟）
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate(factory);

                java.util.Map<String, Object> input = new java.util.HashMap<>();
                input.put("prompt", systemPrompt + "\n\n" + userMessage);
                
                java.util.Map<String, Object> body = new java.util.HashMap<>();
                body.put("input", input);
                body.put("parameters", new java.util.HashMap<>());

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + apiKey);

                org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(body, headers);
                
                log.info("准备调用百炼应用API (第{}次尝试): {}", attempt, url);
                org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                log.info("百炼应用API响应状态: {}", response.getStatusCode());

                // 解析返回的JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.getBody());
                String content = root.path("output").path("text").asText();
                
                // 去除markdown代码块包裹
                if (content.startsWith("```json")) {
                    content = content.replaceFirst("```json", "").trim();
                }
                if (content.startsWith("```")) {
                    content = content.replaceFirst("```", "").trim();
                }
                if (content.endsWith("```")) {
                    content = content.substring(0, content.length() - 3).trim();
                }
                
                log.info("百炼应用API调用成功");
                return content;
                
            } catch (Exception e) {
                log.error("百炼应用API调用异常 (第{}次尝试): {}", attempt, e.getMessage(), e);
                if (attempt == maxRetries) {
                    return "AI服务调用异常：" + e.getMessage();
                } else {
                    try {
                        Thread.sleep(2000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return "AI服务调用被中断";
                    }
                }
            }
        }
        return "AI服务调用失败，已达到最大重试次数";
    }
    
    /**
     * 生成模拟AI响应
     */
    private ChatResponse generateMockResponse(ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setStatusCode(200);
        response.setRequestId("mock-" + System.currentTimeMillis());
        
        ChatResponse.Output output = new ChatResponse.Output();
        output.setText("这是一个模拟的AI响应。");
        output.setFinishReason("stop");
        
        ChatResponse.Choice choice = new ChatResponse.Choice();
        choice.setFinishReason("stop");
        
        ChatMessage message = new ChatMessage();
        message.setRole("assistant");
        message.setContent("这是一个模拟的AI响应。由于API密钥未配置，系统使用模拟模式。");
        choice.setMessage(message);
        
        output.setChoices(new ChatResponse.Choice[]{choice});
        response.setOutput(output);
        
        ChatResponse.Usage usage = new ChatResponse.Usage();
        usage.setInputTokens(0);
        usage.setOutputTokens(0);
        usage.setTotalTokens(0);
        response.setUsage(usage);
        
        return response;
    }
    
    /**
     * 构建错误响应
     */
    private ChatResponse buildErrorResponse(String code, String message) {
        ChatResponse response = new ChatResponse();
        response.setStatusCode(500);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    @Override
    public String speechToText(String audioFilePath) {
        try {
            log.info("========== 调用语音转文字服务（Whisper） ==========");
            log.info("音频文件路径: {}", audioFilePath);
            
            if (audioFilePath == null || audioFilePath.isEmpty()) {
                throw new RuntimeException("音频文件路径不能为空");
            }
            
            // 调用 Whisper 语音识别服务（直接使用本地文件路径）
            String transcriptText = whisperSpeechToTextService.speechToTextFromFile(audioFilePath);
            
            log.info("========== 语音转文字成功 ==========");
            log.info("转录文本长度: {} 字符", transcriptText.length());
            
            return transcriptText;
            
        } catch (RuntimeException e) {
            log.error("语音转文字失败", e);
            throw e;
        } catch (Exception e) {
            log.error("语音转文字失败", e);
            throw new RuntimeException("语音转文字失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateSummary(String transcriptText, String coursewareContent, String apiKey, String appId) {
        try {
            log.info("========== 开始调用阿里云百炼生成重点整理API ==========");
            log.info("转录文本长度: {}", transcriptText != null ? transcriptText.length() : 0);
            log.info("课件内容长度: {}", coursewareContent != null ? coursewareContent.length() : 0);
            log.info("课件内容是否为空: {}", coursewareContent == null || coursewareContent.trim().isEmpty());
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-summary-generation-api-key")) {
                throw new RuntimeException("重点整理生成API密钥未配置");
            }
            
            if (appId == null || appId.isEmpty() || appId.equals("your-summary-generation-app-id")) {
                throw new RuntimeException("重点整理生成应用ID未配置");
            }
            
            // 构建请求URL
            String url = "https://dashscope.aliyuncs.com/api/v1/apps/" + appId + "/completion";
            
            // 创建RestTemplate
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(30000);
            factory.setReadTimeout(120000);
            org.springframework.web.client.RestTemplate restTemplate = 
                new org.springframework.web.client.RestTemplate(factory);
            
            // 构建提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("请基于以下课堂录音转录内容");
            
            // 检查课件内容是否为空
            boolean hasCourseware = coursewareContent != null && !coursewareContent.trim().isEmpty();
            if (hasCourseware) {
                prompt.append("和课件内容");
            }
            prompt.append("，生成一份结构化的课堂重点整理文档。\n\n");
            
            prompt.append("要求：\n");
            prompt.append("1. 提取课堂的核心知识点和重点概念\n");
            prompt.append("2. 整理成清晰的层次结构（使用Markdown格式）\n");
            prompt.append("3. 包含关键概念、重要公式、案例分析等\n");
            prompt.append("4. 添加学习要点和注意事项\n");
            prompt.append("5. 如果有实例或案例，请详细说明\n\n");
            
            prompt.append("课堂录音转录内容：\n");
            prompt.append(transcriptText);
            
            if (hasCourseware) {
                prompt.append("\n\n课件内容：\n");
                prompt.append(coursewareContent);
                log.info("已包含课件内容到提示词中");
            } else {
                log.info("课件内容为空，仅使用转录文本生成重点整理");
            }
            
            // 构建请求体
            java.util.Map<String, Object> input = new java.util.HashMap<>();
            input.put("prompt", prompt.toString());
            
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("input", input);
            body.put("parameters", new java.util.HashMap<>());
            
            // 设置请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = 
                new org.springframework.http.HttpEntity<>(body, headers);
            
            log.info("调用重点整理生成API: {}", url);
            log.info("提示词总长度: {} 字符", prompt.length());
            
            org.springframework.http.ResponseEntity<String> response = 
                restTemplate.postForEntity(url, entity, String.class);
            
            log.info("重点整理生成API响应状态: {}", response.getStatusCode());
            
            // 解析返回的JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = 
                new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.getBody());
            
            String summaryContent = root.path("output").path("text").asText();
            
            if (summaryContent == null || summaryContent.isEmpty()) {
                // 尝试其他可能的路径
                summaryContent = root.path("data").path("text").asText();
            }
            
            if (summaryContent == null || summaryContent.isEmpty()) {
                log.error("API返回的JSON: {}", response.getBody());
                throw new RuntimeException("API返回的重点整理内容为空");
            }
            
            // 去除markdown代码块包裹（如果有）
            if (summaryContent.startsWith("```markdown")) {
                summaryContent = summaryContent.replaceFirst("```markdown", "").trim();
            }
            if (summaryContent.startsWith("```")) {
                summaryContent = summaryContent.replaceFirst("```", "").trim();
            }
            if (summaryContent.endsWith("```")) {
                summaryContent = summaryContent.substring(0, summaryContent.length() - 3).trim();
            }
            
            log.info("重点整理生成成功，内容长度: {}", summaryContent.length());
            log.info("========== 重点整理生成完成 ==========");
            return summaryContent;
            
        } catch (Exception e) {
            log.error("重点整理生成API调用失败", e);
            throw new RuntimeException("重点整理生成失败: " + e.getMessage(), e);
        }
    }
}
