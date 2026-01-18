package com.experiment.pojo;

import lombok.Data;

/**
 * 聊天响应DTO
 */
@Data
public class ChatResponse {
    
    /**
     * 状态码
     */
    private Integer statusCode;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 错误码
     */
    private String code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 输出内容
     */
    private Output output;
    
    /**
     * Token使用情况
     */
    private Usage usage;
    
    @Data
    public static class Output {
        private String text;
        private String finishReason;
        private Choice[] choices;
    }
    
    @Data
    public static class Choice {
        private String finishReason;
        private ChatMessage message;
    }
    
    @Data
    public static class Usage {
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer totalTokens;
    }
} 