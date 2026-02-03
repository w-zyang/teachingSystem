package com.experiment.mapper;

import com.experiment.pojo.AiPptGeneration;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI生成PPT Mapper
 */
@Mapper
public interface AiPptGenerationMapper {
    
    @Insert("INSERT INTO ai_ppt_generation (teacher_id, course_id, topic, keywords, slide_count, style, content, ppt_url, task_id, status, error_message) " +
            "VALUES (#{teacherId}, #{courseId}, #{topic}, #{keywords}, #{slideCount}, #{style}, #{content}, #{pptUrl}, #{taskId}, #{status}, #{errorMessage})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AiPptGeneration record);
    
    @Update("UPDATE ai_ppt_generation SET status = #{status}, content = #{content}, ppt_url = #{pptUrl}, error_message = #{errorMessage} WHERE id = #{id}")
    void updateStatus(AiPptGeneration record);
    
    @Select("SELECT * FROM ai_ppt_generation WHERE id = #{id}")
    AiPptGeneration selectById(Long id);
    
    @Select("SELECT * FROM ai_ppt_generation WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<AiPptGeneration> selectByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM ai_ppt_generation WHERE task_id = #{taskId}")
    AiPptGeneration selectByTaskId(String taskId);
}

