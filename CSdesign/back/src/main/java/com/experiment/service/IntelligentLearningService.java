package com.experiment.service;

import java.util.List;
import java.util.Map;

/**
 * 智能学习服务接口
 */
public interface IntelligentLearningService {
    
    // ============ 智能学习路径规划 ============
    
    /**
     * 生成个性化学习路径
     */
    Map<String, Object> generatePersonalizedPath(Long studentId, String subject, String targetLevel);
    
    /**
     * 获取学生的学习路径
     */
    List<Map<String, Object>> getStudentLearningPaths(Long studentId);
    
    /**
     * 更新学习进度
     */
    boolean updateLearningProgress(Long pathId, Integer stepNumber, Double masteryLevel, Integer studyDuration);
    
    /**
     * 获取学习路径详情
     */
    Map<String, Object> getLearningPathDetails(Long pathId);
    
    /**
     * 获取推荐的下一步学习内容
     */
    Map<String, Object> getNextLearningStep(Long studentId, Long pathId);
    
    // ============ 情绪感知学习助手 ============
    
    /**
     * 记录学生情绪状态
     */
    boolean recordEmotionState(Long studentId, String sessionId, String emotionType, Double intensity, String context);
    
    /**
     * 分析情绪并提供干预建议
     */
    Map<String, Object> analyzeEmotionAndSuggest(Long studentId, String sessionId);
    
    /**
     * 获取情绪趋势分析
     */
    Map<String, Object> getEmotionTrendAnalysis(Long studentId, Integer days);
    
    /**
     * 记录学习状态监控数据
     */
    boolean recordLearningStatus(Long studentId, String sessionId, Map<String, Object> statusData);
    
    /**
     * 获取学习状态统计
     */
    Map<String, Object> getLearningStatusStats(Long studentId, Integer hours);
    
    /**
     * 更新干预效果反馈
     */
    boolean updateInterventionFeedback(Long interventionId, Double effectivenessScore, String feedback);
    
    // ============ 智能同伴学习 ============
    
    /**
     * 智能匹配学习伙伴
     */
    List<Map<String, Object>> findLearningCompanions(Long studentId, Integer limit);
    
    /**
     * 创建学习伙伴关系
     */
    boolean createCompanionRelationship(Long student1Id, Long student2Id);
    
    /**
     * 获取学习伙伴列表
     */
    List<Map<String, Object>> getMyLearningCompanions(Long studentId);
    
    /**
     * 更新伙伴关系状态
     */
    boolean updateCompanionStatus(Long matchId, String status);
    
    /**
     * 创建学习小组
     */
    Map<String, Object> createStudyGroup(Map<String, Object> groupData);
    
    /**
     * 获取学习小组列表
     */
    List<Map<String, Object>> getStudyGroups(Long studentId, String subject, Integer limit);
    
    /**
     * 加入学习小组
     */
    boolean joinStudyGroup(Long groupId, Long studentId);
    
    /**
     * 获取小组成员
     */
    List<Map<String, Object>> getGroupMembers(Long groupId);
    
    /**
     * 创建协作学习会话
     */
    Map<String, Object> createCollaborativeSession(Map<String, Object> sessionData);
    
    /**
     * 获取即将到来的协作会话
     */
    List<Map<String, Object>> getUpcomingSessions(Integer limit);
    
    /**
     * 记录协作互动
     */
    boolean recordCollaborativeInteraction(Long sessionId, Long studentId, String interactionType, String content);
    
    /**
     * 评价协作伙伴
     */
    boolean rateCollaborativePartner(Long matchId, Double rating);
} 