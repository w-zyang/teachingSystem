package com.experiment.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.experiment.pojo.KnowledgePoint;
import com.experiment.pojo.LearningPath;

@Mapper
public interface LearningPathMapper {
    
    /**
     * 根据学生ID查询学习路径
     */
    @Select("SELECT * FROM personalized_learning_path WHERE student_id = #{studentId}")
    List<LearningPath> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 创建个性化学习路径
     */
    @Insert("INSERT INTO personalized_learning_path (student_id, template_id, name, current_step, completion_rate, start_time, status, adaptation_config) " +
            "VALUES (#{studentId}, #{templateId}, #{name}, #{currentStep}, #{completionRate}, #{startTime}, #{status}, #{adaptationConfig})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createLearningPath(LearningPath learningPath);
    
    /**
     * 更新学习路径进度
     */
    @Update("UPDATE personalized_learning_path SET current_step = #{currentStep}, completion_rate = #{completionRate}, " +
            "status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateProgress(LearningPath learningPath);
    
    /**
     * 获取推荐的学习路径模板
     */
    @Select("SELECT * FROM learning_path_template WHERE subject = #{subject} AND target_level = #{level} AND is_active = 1")
    List<Map<String, Object>> getPathTemplates(@Param("subject") String subject, @Param("level") String level);
    
    /**
     * 获取知识点列表
     */
    @Select("SELECT * FROM knowledge_point WHERE subject = #{subject} ORDER BY difficulty_level")
    List<KnowledgePoint> getKnowledgePoints(@Param("subject") String subject);
    
    /**
     * 获取学习路径详情包括步骤
     */
    @Select("SELECT p.*, s.step_number, s.status as step_status, s.mastery_level, k.name as knowledge_name, k.difficulty_level " +
            "FROM personalized_learning_path p " +
            "LEFT JOIN learning_path_step s ON p.id = s.path_id " +
            "LEFT JOIN knowledge_point k ON s.knowledge_point_id = k.id " +
            "WHERE p.id = #{pathId} ORDER BY s.step_number")
    List<Map<String, Object>> getPathDetails(@Param("pathId") Long pathId);
    
    /**
     * 根据学生能力生成推荐路径
     */
    @Select("SELECT la.*, kp.name, kp.difficulty_level, kp.estimated_duration " +
            "FROM learning_ability_assessment la " +
            "JOIN knowledge_point kp ON kp.subject = la.subject " +
            "WHERE la.student_id = #{studentId} AND la.subject = #{subject} " +
            "ORDER BY CASE WHEN kp.difficulty_level <= la.difficulty_preference THEN kp.difficulty_level ELSE 999 END")
    List<Map<String, Object>> generateRecommendedPath(@Param("studentId") Long studentId, @Param("subject") String subject);
    
    /**
     * 更新学习步骤状态
     */
    @Update("UPDATE learning_path_step SET status = #{status}, mastery_level = #{masteryLevel}, " +
            "study_duration = #{studyDuration}, complete_time = #{completeTime}, update_time = NOW() " +
            "WHERE path_id = #{pathId} AND step_number = #{stepNumber}")
    int updateStepStatus(@Param("pathId") Long pathId, @Param("stepNumber") Integer stepNumber, 
                        @Param("status") String status, @Param("masteryLevel") Double masteryLevel,
                        @Param("studyDuration") Integer studyDuration, @Param("completeTime") String completeTime);
    
    /**
     * 获取学习统计数据
     */
    @Select("SELECT COUNT(*) as total_paths, " +
            "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_paths, " +
            "AVG(completion_rate) as avg_completion_rate " +
            "FROM personalized_learning_path WHERE student_id = #{studentId}")
    Map<String, Object> getLearningStats(@Param("studentId") Long studentId);
} 