package com.experiment.mapper;

import com.experiment.pojo.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ExamMapper {
    // 插入考试
    int insert(Exam exam);
    
    // 根据ID查询考试
    Exam selectById(@Param("id") Long id);
    
    // 根据教师ID查询考试列表
    List<Exam> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    // 根据课程ID查询考试列表
    List<Exam> selectByCourseId(@Param("courseId") Long courseId);
    
    // 根据课程ID查询已发布的考试列表（学生端使用）
    List<Exam> selectPublishedByCourseId(@Param("courseId") Long courseId);
    
    // 更新考试
    int update(Exam exam);
    
    // 更新考试状态
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateTime") java.time.LocalDateTime updateTime);
    
    // 删除考试
    int deleteById(@Param("id") Long id);
    
    // 分页查询考试
    List<Exam> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    // 统计考试总数
    int countTotal();
} 