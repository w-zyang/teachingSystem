package com.experiment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xunfei.ppt")
public class XunfeiConfig {
    private String appId;
    private String apiSecret;
    private String apiUrl;
    
    // Getters
    public String getAppId() {
        return appId;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    // Setters
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
