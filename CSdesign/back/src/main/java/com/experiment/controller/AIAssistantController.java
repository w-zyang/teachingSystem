package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.service.AIAssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI助手Controller
 * 负责处理AI助手相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIAssistantController {
    
    @Autowired
    private AIAssistantService assistantService;
    
    /**
     * 学习助手
     */
    @PostMapping("/learning-assistant")
    public Result<String> learningAssistant(@RequestBody Map<String, String> data) {
        log.info("收到学习助手请求");
        try {
            String message = data.get("message");
            String response = assistantService.learningAssistant(message);
            return Result.success("学习助手回复成功", response);
        } catch (Exception e) {
            log.error("学习助手处理失败", e);
            return Result.error("学习助手处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 代码助手
     */
    @PostMapping("/code-assistant")
    public Result<String> codeAssistant(@RequestParam String question) {
        log.info("收到代码助手请求: {}", question);
        try {
            String response = assistantService.codeAssistant(question);
            return Result.success("代码助手回复成功", response);
        } catch (Exception e) {
            log.error("代码助手处理失败", e);
            return Result.error("代码助手处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 写作助手
     */
    @PostMapping("/writing-assistant")
    public Result<String> writingAssistant(@RequestParam String request) {
        log.info("收到写作助手请求");
        try {
            String response = assistantService.writingAssistant(request);
            return Result.success("写作助手回复成功", response);
        } catch (Exception e) {
            log.error("写作助手处理失败", e);
            return Result.error("写作助手处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 课程设计助手
     */
    @PostMapping("/course-design")
    public Result<Object> courseDesign(@RequestBody Map<String, Object> data) {
        log.info("收到课程设计请求");
        try {
            Map<String, Object> result = assistantService.courseDesign(data);
            return Result.success("课程设计完成", result);
        } catch (Exception e) {
            log.error("课程设计失败", e);
            return Result.error("课程设计失败: " + e.getMessage());
        }
    }
    
    /**
     * 与文档对话
     */
    @PostMapping("/chat-with-document")
    public Result<Object> chatWithDocument(@RequestBody Map<String, Object> data) {
        log.info("收到文档对话请求");
        try {
            Map<String, Object> result = assistantService.chatWithDocument(data);
            return Result.success("文档对话完成", result);
        } catch (Exception e) {
            log.error("文档对话失败", e);
            return Result.error("文档对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成思维导图
     */
    @PostMapping("/generate-mindmap")
    public Result<Object> generateMindmap(@RequestBody Map<String, Object> data) {
        log.info("收到生成思维导图请求");
        try {
            Map<String, Object> result = assistantService.generateMindmap(data);
            return Result.success("思维导图生成成功", result);
        } catch (Exception e) {
            log.error("思维导图生成失败", e);
            return Result.error("思维导图生成失败: " + e.getMessage());
        }
    }
}
