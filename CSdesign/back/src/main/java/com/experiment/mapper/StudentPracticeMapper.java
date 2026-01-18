package com.experiment.mapper;

import com.experiment.pojo.StudentPractice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StudentPracticeMapper {
    // 插入学生练习记录
    int insert(StudentPractice studentPractice);
    
    // 根据ID查询学生练习记录
    StudentPractice selectById(@Param("id") Long id);
    
    // 根据学生ID查询练习记录
    List<StudentPractice> selectByStudentId(@Param("studentId") Long studentId);
    
    // 根据练习ID查询学生记录
    List<StudentPractice> selectByPracticeId(@Param("practiceId") Long practiceId);
    
    // 根据学生ID和练习ID查询记录
    StudentPractice selectByStudentAndPractice(@Param("studentId") Long studentId, @Param("practiceId") Long practiceId);
    
    // 更新学生练习记录
    int update(StudentPractice studentPractice);
    
    // 删除学生练习记录
    int deleteById(@Param("id") Long id);
    
    // 统计学生练习平均分
    Double selectAverageScoreByStudent(@Param("studentId") Long studentId);
    
    // 统计练习平均分
    Double selectAverageScoreByPractice(@Param("practiceId") Long practiceId);
} 