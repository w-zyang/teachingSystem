package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.service.AIQuestionGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI题目生成Controller
 * 负责处理题目生成相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIQuestionController {
    
    @Autowired
    private AIQuestionGenerationService questionGenerationService;
    
    /**
     * 生成练习题目
     */
    @PostMapping("/generate-practice")
    public Result<Object> generatePractice(@RequestBody Map<String, Object> data) {
        log.info("收到生成练习题目请求: {}", data);
        try {
            String topic = (String) data.get("topic");
            String difficulty = (String) data.get("difficulty");
            Integer count = (Integer) data.get("count");
            List<String> questionTypes = (List<String>) data.get("questionTypes");
            Map<String, Integer> typeConfig = (Map<String, Integer>) data.get("typeConfig");
            
            Map<String, Object> result = questionGenerationService.generatePracticeQuestions(
                topic, difficulty, questionTypes, typeConfig, count
            );
            
            return Result.success("生成练习题目成功", result);
        } catch (Exception e) {
            log.error("生成练习题目失败", e);
            return Result.error("生成练习题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成考试题目
     */
    @PostMapping("/generate-exam")
    public Result<Object> generateExam(@RequestBody Map<String, Object> data) {
        log.info("收到生成考试题目请求: {}", data);
        try {
            Map<String, Object> result = questionGenerationService.generateExam(data);
            return Result.success("生成考试成功", result);
        } catch (Exception e) {
            log.error("生成考试失败", e);
            return Result.error("生成考试失败: " + e.getMessage());
        }
    }
}
