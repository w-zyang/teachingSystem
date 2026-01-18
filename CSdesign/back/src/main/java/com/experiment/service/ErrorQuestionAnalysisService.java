package com.experiment.service;

import java.util.List;
import java.util.Map;

import com.experiment.pojo.ErrorQuestionAnalysisDTO;
import com.experiment.pojo.ErrorQuestionTrainingDTO;

/**
 * 错题分析服务接口
 */
public interface ErrorQuestionAnalysisService {
    
    /**
     * 分析学生的历史错题
     * @param studentId 学生ID
     * @return 错题分析结果列表
     */
    List<ErrorQuestionAnalysisDTO> analyzeStudentErrorQuestions(Long studentId);
    
    /**
     * 根据特定错题分析相关错误
     * @param studentId 学生ID
     * @param questionId 题目ID
     * @return 错题分析结果
     */
    ErrorQuestionAnalysisDTO analyzeSpecificErrorQuestion(Long studentId, Long questionId);
    
    /**
     * 生成相似题目训练
     * @param studentId 学生ID
     * @param originalQuestionId 原始错题ID
     * @param questionCount 生成题目数量
     * @return 训练题目数据
     */
    ErrorQuestionTrainingDTO generateSimilarQuestions(Long studentId, Long originalQuestionId, Integer questionCount);
    
    /**
     * 根据知识点生成针对性训练题目
     * @param studentId 学生ID
     * @param knowledgePoint 知识点
     * @param questionCount 生成题目数量
     * @return 训练题目数据
     */
    ErrorQuestionTrainingDTO generateKnowledgePointQuestions(Long studentId, String knowledgePoint, Integer questionCount);
    
    /**
     * 生成综合错题训练
     * @param studentId 学生ID
     * @param questionCount 生成题目数量
     * @return 训练题目数据
     */
    ErrorQuestionTrainingDTO generateComprehensiveTraining(Long studentId, Integer questionCount);
    
    /**
     * 获取学生错题统计
     * @param studentId 学生ID
     * @return 错题统计数据
     */
    Map<String, Object> getErrorQuestionStatistics(Long studentId);
    
    /**
     * 获取错题分布情况
     * @param studentId 学生ID
     * @return 错题分布数据
     */
    Map<String, Object> getErrorQuestionDistribution(Long studentId);
    
    /**
     * 评估错题训练效果
     * @param studentId 学生ID
     * @param trainingId 训练ID
     * @param answers 学生答案
     * @return 评估结果
     */
    Map<String, Object> evaluateTrainingEffect(Long studentId, Long trainingId, List<String> answers);
    
    /**
     * 记录单个错题
     * @param studentId 学生ID
     * @param errorQuestion 错题信息
     * @return 是否成功
     */
    boolean recordErrorQuestion(Long studentId, Map<String, Object> errorQuestion);
    
    /**
     * 批量记录错题
     * @param studentId 学生ID
     * @param errorQuestions 错题列表
     * @param source 来源
     * @return 记录数量
     */
    int batchRecordErrorQuestions(Long studentId, List<Map<String, Object>> errorQuestions, String source);
} 