package com.experiment.mapper;

import com.experiment.pojo.StudentExam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface StudentExamMapper {
    // 插入学生考试记录
    int insert(StudentExam studentExam);
    
    // 根据ID查询学生考试记录
    StudentExam selectById(@Param("id") Long id);
    
    // 根据学生ID查询考试记录
    List<StudentExam> selectByStudentId(@Param("studentId") Long studentId);
    
    // 根据考试ID查询学生记录
    List<StudentExam> selectByExamId(@Param("examId") Long examId);
    
    // 根据学生ID和考试ID查询记录
    StudentExam selectByStudentAndExam(@Param("studentId") Long studentId, @Param("examId") Long examId);
    
    // 更新学生考试记录
    int update(StudentExam studentExam);
    
    // 删除学生考试记录
    int deleteById(@Param("id") Long id);
    
    // 统计学生考试平均分
    Double selectAverageScoreByStudent(@Param("studentId") Long studentId);
    
    // 统计考试平均分
    Double selectAverageScoreByExam(@Param("examId") Long examId);
    
    // 统计教师课程的平均分
    Double selectAverageScoreByTeacher(@Param("teacherId") Long teacherId);
    
    // 统计整体平均分
    Number selectOverallAverageScore();
    
    // 统计教师的考试数量
    Integer countExamsByTeacher(@Param("teacherId") Long teacherId);
    
    // 获取成绩分布统计
    Map<String, Object> selectGradeDistribution();
    
    // 获取班级整体统计
    Map<String, Object> selectClassStats(@Param("teacherId") Long teacherId);
    
    // 统计及格率
    Number selectPassRate(@Param("teacherId") Long teacherId);
    
    // 统计优秀率  
    Number selectExcellentRate(@Param("teacherId") Long teacherId);
    
    // 统计参与率
    Number selectParticipationRate(@Param("teacherId") Long teacherId);
    
    // 获取教师课程的总学生数
    Integer countTotalStudentsByTeacher(@Param("teacherId") Long teacherId);
    
    // 获取教师课程的参与考试学生数
    Integer countParticipatedStudentsByTeacher(@Param("teacherId") Long teacherId);
} 