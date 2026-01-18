package com.experiment.service;

import java.util.List;
import java.util.Map;

/**
 * AI题目生成服务接口
 */
public interface AIQuestionGenerationService {
    
    /**
     * 生成练习题目
     * @param topic 主题
     * @param difficulty 难度
     * @param questionTypes 题目类型列表
     * @param typeConfig 各类型题目数量配置
     * @param count 总题目数量
     * @return 生成的题目列表
     */
    Map<String, Object> generatePracticeQuestions(
        String topic, 
        String difficulty, 
        List<String> questionTypes,
        Map<String, Integer> typeConfig,
        Integer count
    );
    
    /**
     * 生成考试题目
     * @param data 考试生成数据
     * @return 生成的考试
     */
    Map<String, Object> generateExam(Map<String, Object> data);
    
    /**
     * 根据题目类型生成题目
     * @param topic 主题
     * @param difficulty 难度
     * @param questionType 题目类型
     * @param count 数量
     * @return 题目列表
     */
    List<Map<String, Object>> generateQuestionsByType(
        String topic, 
        String difficulty, 
        String questionType, 
        int count
    );
}
