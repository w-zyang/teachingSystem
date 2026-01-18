package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.QuestionOption;

@Mapper
public interface QuestionOptionMapper {
    // 插入选项
    int insert(QuestionOption option);
    
    // 根据ID查询选项
    QuestionOption selectById(@Param("id") Long id);
    
    // 根据题目ID查询选项列表
    List<QuestionOption> selectByQuestionId(@Param("questionId") Long questionId);
    
    // 批量插入选项
    int batchInsert(@Param("options") List<QuestionOption> options);
    
    // 更新选项
    int update(QuestionOption option);
    
    // 删除选项
    int deleteById(@Param("id") Long id);
    
    // 根据题目ID删除选项
    int deleteByQuestionId(@Param("questionId") Long questionId);
} 