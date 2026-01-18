package com.experiment.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.ClassSummary;

/**
 * 课堂重点整理数据库映射接口
 */
@Mapper
public interface ClassSummaryMapper {
    
    /**
     * 插入课堂总结
     */
    void insert(ClassSummary classSummary);
    
    /**
     * 根据ID查询课堂总结
     */
    ClassSummary selectById(@Param("id") Long id);
    
    /**
     * 根据教师ID查询课堂总结列表
     */
    List<ClassSummary> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据课程ID查询已发布的课堂总结列表
     */
    List<ClassSummary> selectPublishedByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询所有已发布的课堂总结（学生端使用）
     */
    List<ClassSummary> selectAllPublished();
    
    /**
     * 更新课堂总结
     */
    void update(ClassSummary classSummary);
    
    /**
     * 更新转录文本
     */
    void updateTranscriptText(@Param("id") Long id, @Param("transcriptText") String transcriptText);
    
    /**
     * 更新AI生成的总结内容
     */
    void updateSummaryContent(@Param("id") Long id, @Param("summaryContent") String summaryContent);
    
    /**
     * 更新最终文档内容
     */
    void updateFinalContent(@Param("id") Long id, @Param("finalContent") String finalContent);
    
    /**
     * 更新状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") String status, @Param("publishTime") LocalDateTime publishTime);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 删除课堂总结
     */
    void deleteById(@Param("id") Long id);
    
    /**
     * 根据关键词搜索已发布的课堂总结
     */
    List<ClassSummary> searchPublished(@Param("keyword") String keyword);
} 