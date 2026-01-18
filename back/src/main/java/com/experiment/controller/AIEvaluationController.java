package com.experiment.controller;

import com.experiment.result.Result;
import com.experiment.service.AIEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI评测分析Controller
 * 负责处理评测和分析相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIEvaluationController {
    
    @Autowired
    private AIEvaluationService evaluationService;
    
    /**
     * 练习评测
     */
    @PostMapping("/practice-evaluation")
    public Result<Object> practiceEvaluation(@RequestBody Map<String, Object> data) {
        log.info("收到练习评测请求");
        try {
            Map<String, Object> result = evaluationService.evaluatePractice(data);
            return Result.success("评测完成", result);
        } catch (Exception e) {
            log.error("练习评测失败", e);
            return Result.error("练习评测失败: " + e.getMessage());
        }
    }
    
    /**
     * 分析学生数据
     */
    @PostMapping("/analyze-student-data")
    public Result<Object> analyzeStudentData(@RequestBody Map<String, Object> data) {
        log.info("收到学生数据分析请求");
        try {
            Map<String, Object> result = evaluationService.analyzeStudentData(data);
            return Result.success("分析完成", result);
        } catch (Exception e) {
            log.error("学生数据分析失败", e);
            return Result.error("学生数据分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成详细分析
     */
    @PostMapping("/generate-detailed-analysis")
    public Result<Object> generateDetailedAnalysis(@RequestBody Map<String, Object> data) {
        log.info("收到生成详细分析请求");
        try {
            Map<String, Object> result = evaluationService.generateDetailedAnalysis(data);
            return Result.success("详细分析生成成功", result);
        } catch (Exception e) {
            log.error("生成详细分析失败", e);
            return Result.error("生成详细分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查AI分析状态
     */
    @PostMapping("/check-analysis-status")
    public Result<Object> checkAnalysisStatus(@RequestBody Map<String, Object> data) {
        log.info("检查AI分析状态");
        try {
            Map<String, Object> result = evaluationService.checkAnalysisStatus(data);
            return Result.success("状态检查完成", result);
        } catch (Exception e) {
            log.error("状态检查失败", e);
            return Result.error("状态检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发AI分析
     */
    @PostMapping("/trigger-ai-analysis")
    public Result<Object> triggerAIAnalysis(@RequestBody Map<String, Object> data) {
        log.info("手动触发AI分析");
        try {
            Map<String, Object> result = evaluationService.triggerAIAnalysis(data);
            return Result.success("AI分析完成", result);
        } catch (Exception e) {
            log.error("AI分析失败", e);
            return Result.error("AI分析失败: " + e.getMessage());
        }
    }
}
