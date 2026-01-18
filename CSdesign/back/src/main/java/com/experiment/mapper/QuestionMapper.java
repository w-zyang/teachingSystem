package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.Question;

@Mapper
public interface QuestionMapper {
    // 插入题目
    int insert(Question question);
    
    // 根据ID查询题目
    Question selectById(@Param("id") Long id);
    
    // 根据考试ID查询题目列表
    List<Question> selectByExamId(@Param("examId") Long examId);
    
    // 批量插入题目
    int batchInsert(@Param("questions") List<Question> questions);
    
    // 更新题目
    int update(Question question);
    
    // 删除题目
    int deleteById(@Param("id") Long id);
    
    // 根据考试ID删除题目
    int deleteByExamId(@Param("examId") Long examId);
} 