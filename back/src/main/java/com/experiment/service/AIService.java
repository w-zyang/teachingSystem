package com.experiment.service;

import com.experiment.pojo.ChatRequest;
import com.experiment.pojo.ChatResponse;

/**
 * AI服务接口
 */
public interface AIService {
    
    /**
     * 发送聊天请求
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 简单文本对话
     * @param message 用户消息
     * @return AI回复
     */
    String simpleChat(String message);
    
    /**
     * 带系统提示的对话
     * @param systemPrompt 系统提示
     * @param userMessage 用户消息
     * @return AI回复
     */
    String chatWithSystem(String systemPrompt, String userMessage);
    
    /**
     * 语音转文字（使用本地 Whisper 模型）
     * @param audioFilePath 音频文件本地路径
     * @return 转录后的文字
     */
    String speechToText(String audioFilePath);
    
    /**
     * 生成重点整理（使用阿里云百炼）
     * @param transcriptText 语音转文字的结果（由外部提供）
     * @param coursewareContent PPT文本内容
     * @param apiKey API密钥
     * @param appId 应用ID
     * @return Markdown格式的重点整理文档
     */
    String generateSummary(String transcriptText, String coursewareContent, String apiKey, String appId);
} 