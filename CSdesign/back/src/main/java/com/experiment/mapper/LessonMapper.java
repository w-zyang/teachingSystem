package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.Lesson;

@Mapper
public interface LessonMapper {
    
    // 根据ID查询课时
    Lesson findById(@Param("id") Long id);
    
    // 根据章节ID查询课时列表
    List<Lesson> findByChapterId(@Param("chapterId") Long chapterId);
    
    // 插入新课时
    int insert(Lesson lesson);
    
    // 更新课时信息
    int update(Lesson lesson);
    
    // 删除课时
    int deleteById(@Param("id") Long id);
    
    // 根据章节ID删除课时
    int deleteByChapterId(@Param("chapterId") Long chapterId);
    
    // 更新排序
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
} 