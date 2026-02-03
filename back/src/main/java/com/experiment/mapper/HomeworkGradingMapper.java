package com.experiment.mapper;

import com.experiment.pojo.HomeworkGrading;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 作业批阅 Mapper
 */
@Mapper
public interface HomeworkGradingMapper {
    
    @Insert("INSERT INTO homework_grading (teacher_id, student_id, course_id, homework_title, " +
            "submission_file_url, submission_file_name, submission_content, status, submit_time) " +
            "VALUES (#{teacherId}, #{studentId}, #{courseId}, #{homeworkTitle}, " +
            "#{submissionFileUrl}, #{submissionFileName}, #{submissionContent}, #{status}, #{submitTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(HomeworkGrading record);
    
    @Update("UPDATE homework_grading SET ai_score = #{aiScore}, ai_comments = #{aiComments}, " +
            "ai_analysis = #{aiAnalysis}, status = #{status}, ai_grade_time = #{aiGradeTime} WHERE id = #{id}")
    void updateAiGrading(HomeworkGrading record);
    
    @Update("UPDATE homework_grading SET teacher_score = #{teacherScore}, teacher_comments = #{teacherComments}, " +
            "status = #{status}, teacher_review_time = #{teacherReviewTime} WHERE id = #{id}")
    void updateTeacherReview(HomeworkGrading record);
    
    @Select("SELECT * FROM homework_grading WHERE id = #{id}")
    HomeworkGrading selectById(Long id);
    
    @Select("SELECT * FROM homework_grading WHERE teacher_id = #{teacherId} ORDER BY submit_time DESC")
    List<HomeworkGrading> selectByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM homework_grading WHERE student_id = #{studentId} ORDER BY submit_time DESC")
    List<HomeworkGrading> selectByStudentId(Long studentId);
    
    @Delete("DELETE FROM homework_grading WHERE id = #{id}")
    int deleteById(Long id);
}

