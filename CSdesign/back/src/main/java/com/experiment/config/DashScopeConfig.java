package com.experiment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 通义千问API配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dashscope")
public class DashScopeConfig {
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 默认模型
     */
    private String model = "qwen-plus";
    
    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * 应用ID
     */
    private String appId;
} 