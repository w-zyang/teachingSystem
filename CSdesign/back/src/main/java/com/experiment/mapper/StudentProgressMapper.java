package com.experiment.mapper;

import com.experiment.pojo.StudentProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StudentProgressMapper {
    // 插入学习进度
    int insert(StudentProgress progress);
    
    // 根据ID查询学习进度
    StudentProgress selectById(@Param("id") Long id);
    
    // 根据学生ID查询学习进度
    List<StudentProgress> selectByStudentId(@Param("studentId") Long studentId);
    
    // 根据学生ID和课程ID查询学习进度
    StudentProgress selectByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    // 根据学生ID和章节ID查询学习进度
    List<StudentProgress> selectByStudentAndChapter(@Param("studentId") Long studentId, @Param("chapterId") Long chapterId);
    
    // 更新学习进度
    int update(StudentProgress progress);
    
    // 删除学习进度
    int deleteById(@Param("id") Long id);
    
    // 统计学生学习时长
    Integer selectTotalStudyTime(@Param("studentId") Long studentId);
    
    // 统计课程完成率
    Double selectCourseCompletionRate(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    // 统计教师课程的总学习时长
    Integer selectTotalStudyTimeByTeacher(@Param("teacherId") Long teacherId);
} 