package com.experiment.controller;

import com.experiment.pojo.Question;
import com.experiment.result.Result;
import com.experiment.service.QuestionGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库管理控制器
 */
@RestController
@RequestMapping("/api/question-bank")
@CrossOrigin
@Slf4j
public class QuestionBankController {
    
    @Autowired(required = false)
    private QuestionGenerationService questionGenerationService;
    
    /**
     * 智能生成题目（优先从题库获取）
     */
    @PostMapping("/generate")
    public Result<List<Question>> generateQuestions(@RequestBody Map<String, Object> params) {
        if (questionGenerationService == null) {
            return Result.error("智能出题服务未启用");
        }
        
        try {
            String subject = (String) params.get("subject");
            String knowledgePoint = (String) params.get("knowledgePoint");
            String type = (String) params.get("type");
            String difficulty = (String) params.get("difficulty");
            Integer count = (Integer) params.get("count");
            
            log.info("生成题目请求：subject={}, knowledgePoint={}, type={}, difficulty={}, count={}", 
                     subject, knowledgePoint, type, difficulty, count);
            
            List<Question> questions = questionGenerationService.generateQuestions(
                subject, knowledgePoint, type, difficulty, count);
            
            return Result.success("生成题目成功", questions);
            
        } catch (Exception e) {
            log.error("生成题目失败", e);
            return Result.error("生成题目失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量生成题目到题库（预生成）
     */
    @PostMapping("/batch-generate")
    public Result<Map<String, Object>> batchGenerateQuestions(@RequestBody Map<String, Object> params) {
        if (questionGenerationService == null) {
            return Result.error("智能出题服务未启用");
        }
        
        try {
            String subject = (String) params.get("subject");
            String knowledgePoint = (String) params.get("knowledgePoint");
            String type = (String) params.get("type");
            String difficulty = (String) params.get("difficulty");
            Integer count = (Integer) params.get("count");
            
            log.info("批量生成题目到题库：subject={}, knowledgePoint={}, type={}, difficulty={}, count={}", 
                     subject, knowledgePoint, type, difficulty, count);
            
            int generatedCount = questionGenerationService.generateAndCacheQuestions(
                subject, knowledgePoint, type, difficulty, count);
            
            Map<String, Object> result = new HashMap<>();
            result.put("generatedCount", generatedCount);
            result.put("message", "成功生成并缓存" + generatedCount + "道题目");
            
            return Result.success("批量生成成功", result);
            
        } catch (Exception e) {
            log.error("批量生成题目失败", e);
            return Result.error("批量生成题目失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查题库是否有足够的题目
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> checkQuestionBank(
            @RequestParam String subject,
            @RequestParam String knowledgePoint,
            @RequestParam String type,
            @RequestParam String difficulty,
            @RequestParam Integer requiredCount) {
        
        if (questionGenerationService == null) {
            return Result.error("智能出题服务未启用");
        }
        
        try {
            boolean hasEnough = questionGenerationService.hasEnoughQuestions(
                subject, knowledgePoint, type, difficulty, requiredCount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasEnough", hasEnough);
            result.put("message", hasEnough ? "题库充足" : "题库不足，需要生成新题目");
            
            return Result.success("检查完成", result);
            
        } catch (Exception e) {
            log.error("检查题库失败", e);
            return Result.error("检查题库失败：" + e.getMessage());
        }
    }
    
    /**
     * 从题库获取题目（不生成新题）
     */
    @GetMapping("/get")
    public Result<List<Question>> getQuestionsFromBank(
            @RequestParam String subject,
            @RequestParam String knowledgePoint,
            @RequestParam String type,
            @RequestParam String difficulty,
            @RequestParam Integer count) {
        
        if (questionGenerationService == null) {
            return Result.error("智能出题服务未启用");
        }
        
        try {
            List<Question> questions = questionGenerationService.getQuestionsFromBank(
                subject, knowledgePoint, type, difficulty, count);
            
            return Result.success("获取题目成功", questions);
            
        } catch (Exception e) {
            log.error("获取题库题目失败", e);
            return Result.error("获取题库题目失败：" + e.getMessage());
        }
    }
}
