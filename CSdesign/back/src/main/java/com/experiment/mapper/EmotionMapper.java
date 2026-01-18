package com.experiment.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.experiment.pojo.EmotionRecord;

@Mapper
public interface EmotionMapper {
    
    /**
     * 记录情绪检测结果
     */
    @Insert("INSERT INTO emotion_detection_record (student_id, session_id, detection_type, emotion_type, confidence, intensity, context, raw_data, course_id, lesson_id) " +
            "VALUES (#{studentId}, #{sessionId}, #{detectionType}, #{emotionType}, #{confidence}, #{intensity}, #{context}, #{rawData}, #{courseId}, #{lessonId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int recordEmotion(EmotionRecord emotionRecord);
    
    /**
     * 获取学生最近的情绪记录
     */
    @Select("SELECT * FROM emotion_detection_record WHERE student_id = #{studentId} AND session_id = #{sessionId} " +
            "ORDER BY detection_time DESC LIMIT #{limit}")
    List<EmotionRecord> getRecentEmotions(@Param("studentId") Long studentId, @Param("sessionId") String sessionId, @Param("limit") Integer limit);
    
    /**
     * 分析学生情绪趋势
     */
    @Select("SELECT emotion_type, AVG(intensity) as avg_intensity, COUNT(*) as count, " +
            "DATE(detection_time) as date " +
            "FROM emotion_detection_record " +
            "WHERE student_id = #{studentId} AND detection_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY emotion_type, DATE(detection_time) " +
            "ORDER BY date DESC")
    List<Map<String, Object>> analyzeEmotionTrend(@Param("studentId") Long studentId, @Param("days") Integer days);
    
    /**
     * 记录学习状态监控
     */
    @Insert("INSERT INTO learning_status_monitor (student_id, session_id, focus_level, engagement_score, fatigue_level, " +
            "learning_efficiency, interaction_frequency, pause_duration, scroll_speed, click_pattern, typing_pattern, course_id) " +
            "VALUES (#{studentId}, #{sessionId}, #{focusLevel}, #{engagementScore}, #{fatigueLevel}, #{learningEfficiency}, " +
            "#{interactionFrequency}, #{pauseDuration}, #{scrollSpeed}, #{clickPattern}, #{typingPattern}, #{courseId})")
    int recordLearningStatus(@Param("studentId") Long studentId, @Param("sessionId") String sessionId,
                           @Param("focusLevel") Double focusLevel, @Param("engagementScore") Double engagementScore,
                           @Param("fatigueLevel") Double fatigueLevel, @Param("learningEfficiency") Double learningEfficiency,
                           @Param("interactionFrequency") Integer interactionFrequency, @Param("pauseDuration") Integer pauseDuration,
                           @Param("scrollSpeed") Double scrollSpeed, @Param("clickPattern") String clickPattern,
                           @Param("typingPattern") String typingPattern, @Param("courseId") Long courseId);
    
    /**
     * 获取情绪干预建议
     */
    @Select("SELECT suggestion_type, suggestion_content, priority FROM learning_suggestion_config " +
            "WHERE emotion_type = #{emotionType} AND " +
            "CASE " +
            "WHEN #{intensity} < 0.3 THEN intensity_range = 'low' " +
            "WHEN #{intensity} < 0.7 THEN intensity_range = 'medium' " +
            "ELSE intensity_range = 'high' " +
            "END " +
            "AND is_active = 1 ORDER BY priority DESC")
    List<Map<String, Object>> getSuggestions(@Param("emotionType") String emotionType, @Param("intensity") Double intensity);
    
    /**
     * 记录干预措施
     */
    @Insert("INSERT INTO emotion_intervention_record (student_id, emotion_record_id, intervention_type, intervention_content, trigger_threshold) " +
            "VALUES (#{studentId}, #{emotionRecordId}, #{interventionType}, #{interventionContent}, #{triggerThreshold})")
    int recordIntervention(@Param("studentId") Long studentId, @Param("emotionRecordId") Long emotionRecordId,
                          @Param("interventionType") String interventionType, @Param("interventionContent") String interventionContent,
                          @Param("triggerThreshold") Double triggerThreshold);
    
    /**
     * 获取学习状态统计
     */
    @Select("SELECT AVG(focus_level) as avg_focus, AVG(engagement_score) as avg_engagement, " +
            "AVG(fatigue_level) as avg_fatigue, AVG(learning_efficiency) as avg_efficiency " +
            "FROM learning_status_monitor " +
            "WHERE student_id = #{studentId} AND monitor_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR)")
    Map<String, Object> getLearningStatusStats(@Param("studentId") Long studentId, @Param("hours") Integer hours);
    
    /**
     * 更新干预效果反馈
     */
    @Update("UPDATE emotion_intervention_record SET effectiveness_score = #{effectivenessScore}, " +
            "student_feedback = #{studentFeedback} WHERE id = #{interventionId}")
    int updateInterventionFeedback(@Param("interventionId") Long interventionId, 
                                  @Param("effectivenessScore") Double effectivenessScore,
                                  @Param("studentFeedback") String studentFeedback);
} 