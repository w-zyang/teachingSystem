package com.experiment.mapper;

import com.experiment.pojo.AiTeacherTutoring;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI教师辅导 Mapper
 */
@Mapper
public interface AiTeacherTutoringMapper {
    
    @Insert("INSERT INTO ai_teacher_tutoring (teacher_id, session_id, question, answer, knowledge_topic) " +
            "VALUES (#{teacherId}, #{sessionId}, #{question}, #{answer}, #{knowledgeTopic})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AiTeacherTutoring record);
    
    @Update("UPDATE ai_teacher_tutoring SET helpful = #{helpful} WHERE id = #{id}")
    void updateHelpful(@Param("id") Long id, @Param("helpful") Boolean helpful);
    
    @Select("SELECT * FROM ai_teacher_tutoring WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<AiTeacherTutoring> selectBySessionId(String sessionId);
    
    @Select("SELECT * FROM ai_teacher_tutoring WHERE teacher_id = #{teacherId} ORDER BY create_time DESC LIMIT 50")
    List<AiTeacherTutoring> selectRecentByTeacherId(Long teacherId);
}

