package com.experiment.service;

import com.experiment.pojo.Question;
import java.util.List;

/**
 * 智能出题服务接口
 */
public interface QuestionGenerationService {
    
    /**
     * 智能生成题目（优先从题库获取，不足时AI生成）
     * @param subject 学科/主题
     * @param knowledgePoint 知识点
     * @param type 题目类型
     * @param difficulty 难度
     * @param count 数量
     * @return 题目列表
     */
    List<Question> generateQuestions(String subject, String knowledgePoint, 
                                     String type, String difficulty, Integer count);
    
    /**
     * 使用AI批量生成题目并缓存到题库
     * @param subject 学科/主题
     * @param knowledgePoint 知识点
     * @param type 题目类型
     * @param difficulty 难度
     * @param count 数量
     * @return 生成的题目数量
     */
    int generateAndCacheQuestions(String subject, String knowledgePoint, 
                                  String type, String difficulty, Integer count);
    
    /**
     * 从题库获取题目
     * @param subject 学科/主题
     * @param knowledgePoint 知识点
     * @param type 题目类型
     * @param difficulty 难度
     * @param count 数量
     * @return 题目列表
     */
    List<Question> getQuestionsFromBank(String subject, String knowledgePoint, 
                                       String type, String difficulty, Integer count);
    
    /**
     * 检查题库中是否有足够的题目
     * @param subject 学科/主题
     * @param knowledgePoint 知识点
     * @param type 题目类型
     * @param difficulty 难度
     * @param requiredCount 需要的数量
     * @return 是否足够
     */
    boolean hasEnoughQuestions(String subject, String knowledgePoint, 
                              String type, String difficulty, Integer requiredCount);
}
