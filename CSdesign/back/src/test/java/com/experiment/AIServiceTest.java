package com.experiment;

import com.experiment.pojo.ChatMessage;
import com.experiment.pojo.ChatRequest;
import com.experiment.service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * AI服务测试类
 */
@SpringBootTest
public class AIServiceTest {
    
    @Autowired
    private AIService aiService;
    
    @Test
    public void testSimpleChat() {
        String response = aiService.simpleChat("你好，请介绍一下你自己");
        System.out.println("AI回复: " + response);
    }
    
    @Test
    public void testChatWithSystem() {
        String systemPrompt = "你是一个专业的学习助手，请用简洁的方式回答问题。";
        String userMessage = "什么是Java编程语言？";
        String response = aiService.chatWithSystem(systemPrompt, userMessage);
        System.out.println("AI回复: " + response);
    }
    
    @Test
    public void testFullChatRequest() {
        ChatRequest request = new ChatRequest();
        request.setModel("qwen-plus");
        request.setMessages(Arrays.asList(
                new ChatMessage("system", "你是一个友好的AI助手"),
                new ChatMessage("user", "请用一句话介绍Spring Boot")
        ));
        request.setTemperature(0.7f);
        request.setMaxTokens(100);
        
        var response = aiService.chat(request);
        System.out.println("状态码: " + response.getStatusCode());
        System.out.println("请求ID: " + response.getRequestId());
        if (response.getOutput() != null && response.getOutput().getChoices() != null && 
            response.getOutput().getChoices().length > 0) {
            System.out.println("AI回复: " + response.getOutput().getChoices()[0].getMessage().getContent());
        }
        if (response.getUsage() != null) {
            System.out.println("输入Token: " + response.getUsage().getInputTokens());
            System.out.println("输出Token: " + response.getUsage().getOutputTokens());
            System.out.println("总Token: " + response.getUsage().getTotalTokens());
        }
    }
} 