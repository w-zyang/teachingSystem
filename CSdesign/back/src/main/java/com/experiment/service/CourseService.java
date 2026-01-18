package com.experiment.service;

import com.experiment.pojo.Course;
import com.experiment.pojo.CourseDesignDTO;
import com.experiment.pojo.CourseQueryDTO;
import com.experiment.result.PageResult;
import com.experiment.result.Result;

public interface CourseService {
    
    // 分页查询课程列表
    PageResult<Course> getCourseList(CourseQueryDTO queryDTO);
    
    // 根据ID查询课程
    Result<Course> getCourseById(Long id);
    
    // 添加课程
    Result<String> addCourse(Course course);
    
    // 更新课程
    Result<String> updateCourse(Course course);
    
    // 删除课程
    Result<String> deleteCourse(Long id);
    
    // 更新课程状态
    Result<String> updateCourseStatus(Long id, String status);
    
    // 根据教师ID查询课程列表
    Result<Object> getCoursesByTeacherId(Long teacherId);
    
    // 根据学生ID查询课程列表
    Result<Object> getCoursesByStudentId(Long studentId);
    
    // AI生成课程大纲
    Result<Object> generateCourseOutline(CourseDesignDTO designDTO);
    
    // AI生成教学内容
    Result<Object> generateCourseContent(Long chapterId);
    
    // 获取课程统计信息
    Result<Object> getCourseStats();
} 