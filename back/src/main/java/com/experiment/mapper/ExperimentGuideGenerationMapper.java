package com.experiment.mapper;

import com.experiment.pojo.ExperimentGuideGeneration;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 实验指导书生成 Mapper
 */
@Mapper
public interface ExperimentGuideGenerationMapper {
    
    @Insert("INSERT INTO experiment_guide_generation (teacher_id, course_id, chapter_name, " +
            "knowledge_points, difficulty_level, status) " +
            "VALUES (#{teacherId}, #{courseId}, #{chapterName}, " +
            "#{knowledgePoints}, #{difficultyLevel}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ExperimentGuideGeneration record);
    
    @Update("UPDATE experiment_guide_generation SET experiment_title = #{experimentTitle}, " +
            "experiment_objectives = #{experimentObjectives}, experiment_environment = #{experimentEnvironment}, " +
            "experiment_steps = #{experimentSteps}, experiment_code = #{experimentCode}, " +
            "expected_results = #{expectedResults}, thinking_questions = #{thinkingQuestions}, " +
            "reference_answers = #{referenceAnswers}, guide_url = #{guideUrl}, status = #{status} WHERE id = #{id}")
    void updateContent(ExperimentGuideGeneration record);
    
    @Select("SELECT * FROM experiment_guide_generation WHERE id = #{id}")
    ExperimentGuideGeneration selectById(Long id);
    
    @Select("SELECT * FROM experiment_guide_generation WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<ExperimentGuideGeneration> selectByTeacherId(Long teacherId);
    
    @Delete("DELETE FROM experiment_guide_generation WHERE id = #{id}")
    int deleteById(Long id);
}

