package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.Chapter;

@Mapper
public interface ChapterMapper {
    
    // 根据ID查询章节
    Chapter findById(@Param("id") Long id);
    
    // 根据课程ID查询章节列表
    List<Chapter> findByCourseId(@Param("courseId") Long courseId);
    
    // 插入新章节
    int insert(Chapter chapter);
    
    // 更新章节信息
    int update(Chapter chapter);
    
    // 删除章节
    int deleteById(@Param("id") Long id);
    
    // 根据课程ID删除章节
    int deleteByCourseId(@Param("courseId") Long courseId);
    
    // 更新排序
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
} 