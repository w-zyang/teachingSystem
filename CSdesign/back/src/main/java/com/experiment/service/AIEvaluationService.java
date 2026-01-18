package com.experiment.service;

import java.util.Map;

/**
 * AI评测分析服务接口
 */
public interface AIEvaluationService {
    
    /**
     * 练习评测
     * @param data 练习数据（包含题目、答案等）
     * @return 评测结果
     */
    Map<String, Object> evaluatePractice(Map<String, Object> data);
    
    /**
     * 分析学生数据
     * @param data 学生答题数据
     * @return 分析结果
     */
    Map<String, Object> analyzeStudentData(Map<String, Object> data);
    
    /**
     * 生成详细分析
     * @param data 分析数据
     * @return 详细分析结果
     */
    Map<String, Object> generateDetailedAnalysis(Map<String, Object> data);
    
    /**
     * 检查AI分析状态
     * @param data 分析数据
     * @return 状态信息
     */
    Map<String, Object> checkAnalysisStatus(Map<String, Object> data);
    
    /**
     * 手动触发AI分析
     * @param data 分析数据
     * @return 分析结果
     */
    Map<String, Object> triggerAIAnalysis(Map<String, Object> data);
}
