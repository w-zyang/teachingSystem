package com.experiment.service;

import java.util.Map;

/**
 * AI助手服务接口
 */
public interface AIAssistantService {
    
    /**
     * 学习助手
     * @param message 学习问题
     * @return AI回复
     */
    String learningAssistant(String message);
    
    /**
     * 代码助手
     * @param question 编程问题
     * @return AI回复
     */
    String codeAssistant(String question);
    
    /**
     * 写作助手
     * @param request 写作请求
     * @return AI回复
     */
    String writingAssistant(String request);
    
    /**
     * 课程设计助手
     * @param data 课程设计数据
     * @return 设计结果
     */
    Map<String, Object> courseDesign(Map<String, Object> data);
    
    /**
     * 与文档对话
     * @param data 对话数据
     * @return AI回复
     */
    Map<String, Object> chatWithDocument(Map<String, Object> data);
    
    /**
     * 生成思维导图
     * @param data 思维导图数据
     * @return 思维导图结果
     */
    Map<String, Object> generateMindmap(Map<String, Object> data);
}
