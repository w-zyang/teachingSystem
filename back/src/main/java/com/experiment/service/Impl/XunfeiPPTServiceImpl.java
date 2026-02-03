package com.experiment.service.Impl;

import com.experiment.config.XunfeiConfig;
import com.experiment.service.XunfeiPPTService;
import com.experiment.utils.ApiAuthAlgorithm;
import com.experiment.utils.XunfeiApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class XunfeiPPTServiceImpl implements XunfeiPPTService {

    @Autowired
    private XunfeiConfig xunfeiConfig;

    private final ApiAuthAlgorithm authAlgorithm = new ApiAuthAlgorithm();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generatePPT(String content, String title) {
        log.info("开始调用讯飞API生成PPT，标题: {}", title);
        
        try {
            // 限制内容长度，避免超过12000字限制
            String truncatedContent = content;
            if (content.length() > 12000) {
                truncatedContent = content.substring(0, 12000);
                log.warn("内容过长，已截断至12000字符");
            }
            
            // 生成时间戳和签名
            long timestamp = System.currentTimeMillis() / 1000;
            String signature = authAlgorithm.getSignature(
                xunfeiConfig.getAppId(), 
                xunfeiConfig.getApiSecret(), 
                timestamp
            );
            
            log.info("请求参数 - appId: {}, timestamp: {}, signature: {}", 
                xunfeiConfig.getAppId(), timestamp, signature);
            
            // 创建API客户端
            XunfeiApiClient apiClient = new XunfeiApiClient(xunfeiConfig.getApiUrl());
            
            // 添加延迟避免请求过快
            Thread.sleep(2000);
            
            // 调用API
            String response = apiClient.createPPT(
                xunfeiConfig.getAppId(),
                String.valueOf(timestamp),
                signature,
                truncatedContent
            );
            
            log.info("讯飞API响应: {}", response);
            
            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                String sid = jsonNode.get("data").get("sid").asText();
                log.info("PPT生成任务已提交，SID: {}", sid);
                return sid;
            } else {
                String errorMsg = jsonNode.has("desc") ? jsonNode.get("desc").asText() : "未知错误";
                int errorCode = jsonNode.has("code") ? jsonNode.get("code").asInt() : -1;
                log.error("讯飞API返回错误: code={}, desc={}", errorCode, errorMsg);
                throw new RuntimeException("讯飞API返回错误: " + errorMsg);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("请求被中断");
        } catch (Exception e) {
            log.error("生成PPT时发生错误", e);
            throw new RuntimeException("生成PPT失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> checkPPTStatus(String taskId) {
        log.info("查询PPT生成状态，任务ID: {}", taskId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 生成时间戳和签名
            long timestamp = System.currentTimeMillis() / 1000;
            String signature = authAlgorithm.getSignature(
                xunfeiConfig.getAppId(), 
                xunfeiConfig.getApiSecret(), 
                timestamp
            );
            
            // 创建API客户端
            XunfeiApiClient apiClient = new XunfeiApiClient(xunfeiConfig.getApiUrl());
            
            // 查询进度
            String response = apiClient.checkProgress(
                xunfeiConfig.getAppId(),
                String.valueOf(timestamp),
                signature,
                taskId
            );
            
            log.info("PPT状态查询响应: {}", response);
            
            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                JsonNode data = jsonNode.get("data");
                String pptStatus = data.get("pptStatus").asText();
                
                result.put("status", pptStatus);
                
                if ("done".equals(pptStatus)) {
                    String pptUrl = data.get("pptUrl").asText();
                    result.put("downloadUrl", pptUrl);
                    result.put("completed", true);
                    
                    if (data.has("totalPages")) {
                        result.put("totalPages", data.get("totalPages").asInt());
                    }
                    
                    log.info("PPT生成完成，下载地址: {}", pptUrl);
                } else if ("build_failed".equals(pptStatus)) {
                    String errMsg = data.has("errMsg") ? data.get("errMsg").asText() : "生成失败";
                    result.put("completed", true);
                    result.put("error", errMsg);
                } else {
                    result.put("completed", false);
                    result.put("status", "building");
                    
                    if (data.has("totalPages") && data.has("donePages")) {
                        result.put("totalPages", data.get("totalPages").asInt());
                        result.put("donePages", data.get("donePages").asInt());
                    }
                }
            } else {
                String errorMsg = jsonNode.has("desc") ? jsonNode.get("desc").asText() : "查询失败";
                result.put("completed", true);
                result.put("error", errorMsg);
            }
            
        } catch (Exception e) {
            log.error("查询PPT状态时发生错误", e);
            result.put("completed", true);
            result.put("error", "查询PPT状态失败: " + e.getMessage());
        }
        
        return result;
    }
}
