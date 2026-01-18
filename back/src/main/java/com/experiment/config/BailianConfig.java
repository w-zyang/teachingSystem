package com.experiment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云百炼AI配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bailian")
public class BailianConfig {
    
    /**
     * 语音转文字配置
     */
    private SpeechToText speechToText = new SpeechToText();
    
    /**
     * 重点整理生成配置
     */
    private SummaryGeneration summaryGeneration = new SummaryGeneration();
    
    @Data
    public static class SpeechToText {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 应用ID
         */
        private String appId;
    }
    
    @Data
    public static class SummaryGeneration {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 应用ID
         */
        private String appId;
    }
}
