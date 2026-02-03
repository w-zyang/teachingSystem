package com.experiment.mapper;

import com.experiment.pojo.TeachingResourcePackage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 教学资源包 Mapper
 */
@Mapper
public interface TeachingResourcePackageMapper {
    
    @Insert("INSERT INTO teaching_resource_package (teacher_id, course_id, chapter_name, knowledge_points, difficulty_level, " +
            "quiz_content, homework_content, experiment_content, summary_ppt, package_url, status) " +
            "VALUES (#{teacherId}, #{courseId}, #{chapterName}, #{knowledgePoints}, #{difficultyLevel}, " +
            "#{quizContent}, #{homeworkContent}, #{experimentContent}, #{summaryPpt}, #{packageUrl}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TeachingResourcePackage record);
    
    @Update("UPDATE teaching_resource_package SET status = #{status}, quiz_content = #{quizContent}, " +
            "homework_content = #{homeworkContent}, experiment_content = #{experimentContent}, " +
            "summary_ppt = #{summaryPpt}, package_url = #{packageUrl} WHERE id = #{id}")
    void update(TeachingResourcePackage record);
    
    @Select("SELECT * FROM teaching_resource_package WHERE id = #{id}")
    TeachingResourcePackage selectById(Long id);
    
    @Select("SELECT * FROM teaching_resource_package WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<TeachingResourcePackage> selectByTeacherId(Long teacherId);
}

