package com.experiment.pojo;

import lombok.Data;
import java.util.List;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequest {
    
    /**
     * 模型名称
     */
    private String model = "qwen-plus";
    
    /**
     * 消息列表
     */
    private List<ChatMessage> messages;
    
    /**
     * 温度参数，控制生成文本的随机性 [0, 2)
     */
    private Float temperature = 0.7f;
    
    /**
     * 核采样概率阈值 (0, 1.0]
     */
    private Float topP = 0.8f;
    
    /**
     * 最大输出token数
     */
    private Integer maxTokens = 2048;
    
    /**
     * 是否开启思考模式（仅Qwen3模型支持）
     */
    private Boolean enableThinking = false;
    
    /**
     * 思考预算（仅Qwen3模型支持）
     */
    private Integer thinkingBudget = 1000;
    
    /**
     * 重复惩罚参数
     */
    private Float repetitionPenalty = 1.1f;
    
    /**
     * 存在惩罚参数 [-2.0, 2.0]
     */
    private Float presencePenalty = 0.0f;
    
    /**
     * 是否启用联网搜索
     */
    private Boolean enableSearch = false;
    
    /**
     * 返回格式：text 或 json_object
     */
    private String responseFormat = "text";
} 