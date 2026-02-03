package com.experiment.mapper;

import com.experiment.pojo.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 章节 Mapper
 */
@Mapper
public interface ChapterMapper {
    
    @Select("SELECT * FROM chapter WHERE course_id = #{courseId} ORDER BY sort_order ASC")
    List<Chapter> selectByCourseId(Long courseId);
    
    @Select("SELECT * FROM chapter WHERE id = #{id}")
    Chapter selectById(Long id);
    
    @Select("SELECT * FROM chapter WHERE course_id = #{courseId} ORDER BY sort_order ASC")
    List<Chapter> findByCourseId(Long courseId);
    
    @Delete("DELETE FROM chapter WHERE course_id = #{courseId}")
    void deleteByCourseId(Long courseId);
}
