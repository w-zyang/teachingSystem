package com.experiment.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.pojo.ErrorQuestionAnalysisDTO;
import com.experiment.pojo.ErrorQuestionTrainingDTO;
import com.experiment.result.Result;
import com.experiment.service.ErrorQuestionAnalysisService;

import lombok.extern.slf4j.Slf4j;

/**
 * 错题分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/error-questions")
public class ErrorQuestionController {

    @Autowired
    private ErrorQuestionAnalysisService errorQuestionAnalysisService;

    /**
     * 获取学生的历史错题分析
     */
    @GetMapping("/analysis/{studentId}")
    public Result<List<ErrorQuestionAnalysisDTO>> analyzeStudentErrorQuestions(@PathVariable Long studentId) {
        log.info("获取学生 {} 的历史错题分析", studentId);
        
        try {
            List<ErrorQuestionAnalysisDTO> analysisResults = errorQuestionAnalysisService.analyzeStudentErrorQuestions(studentId);
            return Result.success("获取错题分析成功", analysisResults);
        } catch (Exception e) {
            log.error("获取学生错题分析失败", e);
            return Result.error("获取错题分析失败：" + e.getMessage());
        }
    }

    /**
     * 分析特定错题
     */
    @GetMapping("/analysis/{studentId}/{questionId}")
    public Result<ErrorQuestionAnalysisDTO> analyzeSpecificErrorQuestion(@PathVariable Long studentId, @PathVariable Long questionId) {
        log.info("分析学生 {} 的特定错题 {}", studentId, questionId);
        
        try {
            ErrorQuestionAnalysisDTO analysisResult = errorQuestionAnalysisService.analyzeSpecificErrorQuestion(studentId, questionId);
            if (analysisResult != null) {
                return Result.success("分析错题成功", analysisResult);
            } else {
                return Result.error("未找到相关错题信息");
            }
        } catch (Exception e) {
            log.error("分析特定错题失败", e);
            return Result.error("分析错题失败：" + e.getMessage());
        }
    }

    /**
     * 生成相似题目训练
     */
    @PostMapping("/training/similar")
    public Result<ErrorQuestionTrainingDTO> generateSimilarQuestions(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        Long originalQuestionId = ((Number) request.get("originalQuestionId")).longValue();
        Integer questionCount = (Integer) request.getOrDefault("questionCount", 5);
        
        log.info("为学生 {} 生成相似题目训练，原题ID: {}，数量: {}", studentId, originalQuestionId, questionCount);
        
        try {
            ErrorQuestionTrainingDTO training = errorQuestionAnalysisService.generateSimilarQuestions(studentId, originalQuestionId, questionCount);
            return Result.success("生成相似题目成功", training);
        } catch (Exception e) {
            log.error("生成相似题目失败", e);
            return Result.error("生成相似题目失败：" + e.getMessage());
        }
    }

    /**
     * 根据知识点生成训练题目
     */
    @PostMapping("/training/knowledge-point")
    public Result<ErrorQuestionTrainingDTO> generateKnowledgePointQuestions(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        String knowledgePoint = (String) request.get("knowledgePoint");
        Integer questionCount = (Integer) request.getOrDefault("questionCount", 5);
        
        log.info("为学生 {} 生成知识点训练题目，知识点: {}，数量: {}", studentId, knowledgePoint, questionCount);
        
        try {
            ErrorQuestionTrainingDTO training = errorQuestionAnalysisService.generateKnowledgePointQuestions(studentId, knowledgePoint, questionCount);
            return Result.success("生成知识点题目成功", training);
        } catch (Exception e) {
            log.error("生成知识点题目失败", e);
            return Result.error("生成知识点题目失败：" + e.getMessage());
        }
    }

    /**
     * 生成综合错题训练
     */
    @PostMapping("/training/comprehensive")
    public Result<ErrorQuestionTrainingDTO> generateComprehensiveTraining(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        Integer questionCount = (Integer) request.getOrDefault("questionCount", 10);
        
        log.info("为学生 {} 生成综合错题训练，数量: {}", studentId, questionCount);
        
        try {
            ErrorQuestionTrainingDTO training = errorQuestionAnalysisService.generateComprehensiveTraining(studentId, questionCount);
            return Result.success("生成综合训练成功", training);
        } catch (Exception e) {
            log.error("生成综合训练失败", e);
            return Result.error("生成综合训练失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生错题统计
     */
    @GetMapping("/statistics/{studentId}")
    public Result<Map<String, Object>> getErrorQuestionStatistics(@PathVariable Long studentId) {
        log.info("获取学生 {} 的错题统计", studentId);
        
        try {
            Map<String, Object> statistics = errorQuestionAnalysisService.getErrorQuestionStatistics(studentId);
            return Result.success("获取错题统计成功", statistics);
        } catch (Exception e) {
            log.error("获取错题统计失败", e);
            return Result.error("获取错题统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生错题分布情况
     */
    @GetMapping("/distribution/{studentId}")
    public Result<Map<String, Object>> getErrorQuestionDistribution(@PathVariable Long studentId) {
        log.info("获取学生 {} 的错题分布", studentId);
        
        try {
            Map<String, Object> distribution = errorQuestionAnalysisService.getErrorQuestionDistribution(studentId);
            return Result.success("获取错题分布成功", distribution);
        } catch (Exception e) {
            log.error("获取错题分布失败", e);
            return Result.error("获取错题分布失败：" + e.getMessage());
        }
    }

    /**
     * 评估训练效果
     */
    @PostMapping("/training/evaluate")
    public Result<Map<String, Object>> evaluateTrainingEffect(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        Long trainingId = ((Number) request.get("trainingId")).longValue();
        @SuppressWarnings("unchecked")
        List<String> answers = (List<String>) request.get("answers");
        
        log.info("评估学生 {} 的训练效果，训练ID: {}", studentId, trainingId);
        
        try {
            Map<String, Object> evaluation = errorQuestionAnalysisService.evaluateTrainingEffect(studentId, trainingId, answers);
            return Result.success("评估训练效果成功", evaluation);
        } catch (Exception e) {
            log.error("评估训练效果失败", e);
            return Result.error("评估训练效果失败：" + e.getMessage());
        }
    }
    
    /**
     * 记录单个错题
     */
    @PostMapping("/record")
    public Result<String> recordErrorQuestion(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> question = (Map<String, Object>) request.get("question");
        
        log.info("记录学生 {} 的错题", studentId);
        
        try {
            boolean success = errorQuestionAnalysisService.recordErrorQuestion(studentId, question);
            if (success) {
                return Result.success("记录错题成功", "OK");
            } else {
                return Result.error("记录错题失败");
            }
        } catch (Exception e) {
            log.error("记录错题失败", e);
            return Result.error("记录错题失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量记录错题
     */
    @PostMapping("/batch-record")
    public Result<String> batchRecordErrorQuestions(@RequestBody Map<String, Object> request) {
        Long studentId = ((Number) request.get("studentId")).longValue();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> errorQuestions = (List<Map<String, Object>>) request.get("errorQuestions");
        String source = (String) request.getOrDefault("source", "practice");
        
        log.info("批量记录学生 {} 的错题，数量: {}，来源: {}", studentId, errorQuestions.size(), source);
        
        try {
            int count = errorQuestionAnalysisService.batchRecordErrorQuestions(studentId, errorQuestions, source);
            if (count > 0) {
                return Result.success("批量记录错题成功", "已记录 " + count + " 道错题");
            } else {
                return Result.error("记录错题失败");
            }
        } catch (Exception e) {
            log.error("批量记录错题失败", e);
            return Result.error("批量记录错题失败：" + e.getMessage());
        }
    }
} 