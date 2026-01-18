package com.experiment.mapper;

import com.experiment.pojo.ErrorQuestionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ErrorQuestionRecordMapper {
    
    /**
     * 插入错题记录
     */
    int insert(ErrorQuestionRecord record);
    
    /**
     * 批量插入错题记录
     */
    int batchInsert(@Param("records") List<ErrorQuestionRecord> records);
    
    /**
     * 根据学生ID查询所有错题
     */
    List<ErrorQuestionRecord> selectByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据学生ID和题目ID查询错题
     */
    ErrorQuestionRecord selectByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") String questionId);
    
    /**
     * 更新错题记录
     */
    int update(ErrorQuestionRecord record);
    
    /**
     * 删除错题记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据学生ID删除所有错题
     */
    int deleteByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查询学生错题统计
     */
    Map<String, Object> selectErrorStatistics(@Param("studentId") Long studentId);
}

