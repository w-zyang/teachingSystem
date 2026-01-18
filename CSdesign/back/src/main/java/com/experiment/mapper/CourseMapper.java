package com.experiment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.experiment.pojo.Course;
import com.experiment.pojo.CourseQueryDTO;

@Mapper
public interface CourseMapper {
    
    // 根据ID查询课程
    Course findById(@Param("id") Long id);
    
    // 插入新课程
    int insert(Course course);
    
    // 更新课程信息
    int update(Course course);
    
    // 删除课程
    int deleteById(@Param("id") Long id);
    
    // 分页查询课程列表
    List<Course> findByCondition(CourseQueryDTO queryDTO);
    
    // 统计课程总数
    int countByCondition(CourseQueryDTO queryDTO);
    
    // 根据教师ID查询课程列表
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);
    
    // 根据学生ID查询课程列表
    List<Course> findByStudentId(@Param("studentId") Long studentId);
    
    // 更新课程状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 更新学习人数
    int updateStudents(@Param("id") Long id, @Param("students") Integer students);
    
    // 更新评分
    int updateRating(@Param("id") Long id, @Param("rating") Double rating);
    
    // 统计教师的课程数量
    Integer countByTeacherId(@Param("teacherId") Long teacherId);
    
    // 统计选择教师课程的学生总数
    Integer countStudentsByTeacherId(@Param("teacherId") Long teacherId);
} 