package com.experiment.mapper;

import com.experiment.pojo.CoursewareUpdate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 课件更新 Mapper
 */
@Mapper
public interface CoursewareUpdateMapper {
    
    @Insert("INSERT INTO courseware_update (teacher_id, course_id, original_file_url, original_file_name, " +
            "update_type, update_requirements, status) " +
            "VALUES (#{teacherId}, #{courseId}, #{originalFileUrl}, #{originalFileName}, " +
            "#{updateType}, #{updateRequirements}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CoursewareUpdate record);
    
    @Update("UPDATE courseware_update SET ai_suggestions = #{aiSuggestions}, status = #{status} WHERE id = #{id}")
    void updateSuggestions(CoursewareUpdate record);
    
    @Update("UPDATE courseware_update SET updated_file_url = #{updatedFileUrl}, status = #{status} WHERE id = #{id}")
    void updateFile(CoursewareUpdate record);
    
    @Select("SELECT * FROM courseware_update WHERE id = #{id}")
    CoursewareUpdate selectById(Long id);
    
    @Select("SELECT * FROM courseware_update WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<CoursewareUpdate> selectByTeacherId(Long teacherId);
    
    @Delete("DELETE FROM courseware_update WHERE id = #{id}")
    int deleteById(Long id);
}

