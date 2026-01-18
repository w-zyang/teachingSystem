package com.experiment.mapper;

import com.experiment.pojo.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentAnswerMapper {
    // 插入学生答案
    int insert(StudentAnswer studentAnswer);
    
    // 根据ID查询学生答案
    StudentAnswer selectById(@Param("id") Long id);
    
    // 根据学生考试ID查询所有答案
    List<StudentAnswer> selectByStudentExamId(@Param("studentExamId") Long studentExamId);
    
    // 根据学生考试ID和题目ID查询答案
    StudentAnswer selectByStudentExamAndQuestion(@Param("studentExamId") Long studentExamId, @Param("questionId") Long questionId);
    
    // 批量插入学生答案
    int batchInsert(@Param("answers") List<StudentAnswer> answers);
    
    // 更新学生答案
    int update(StudentAnswer studentAnswer);
    
    // 删除学生答案
    int deleteById(@Param("id") Long id);
    
    // 根据学生考试ID删除答案
    int deleteByStudentExamId(@Param("studentExamId") Long studentExamId);
    
    // 查询学生的所有错题（通过学生ID）
    List<Map<String, Object>> selectErrorQuestionsByStudentId(@Param("studentId") Long studentId);
    
    // 查询学生某道题的错误记录
    List<Map<String, Object>> selectErrorRecordsByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
    
    // 统计学生某道题的错误次数
    Integer countErrorsByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
    
    // 统计学生某道题的总答题次数
    Integer countTotalByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
}
