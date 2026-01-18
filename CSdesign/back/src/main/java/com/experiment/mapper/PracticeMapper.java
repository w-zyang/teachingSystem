package com.experiment.mapper;

import com.experiment.pojo.Practice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PracticeMapper {
    // 插入练习
    int insert(Practice practice);
    
    // 根据ID查询练习
    Practice selectById(@Param("id") Long id);
    
    // 根据课程ID查询练习列表
    List<Practice> selectByCourseId(@Param("courseId") Long courseId);
    
    // 根据类型查询练习列表
    List<Practice> selectByType(@Param("type") String type);
    
    // 根据难度查询练习列表
    List<Practice> selectByDifficulty(@Param("difficulty") String difficulty);
    
    // 更新练习
    int update(Practice practice);
    
    // 删除练习
    int deleteById(@Param("id") Long id);
    
    // 分页查询练习
    List<Practice> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    // 统计练习总数
    int countTotal();
} 