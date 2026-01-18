package com.experiment.service;

import java.util.Map;

import com.experiment.pojo.StudentStatsDTO;
import com.experiment.pojo.SystemStatsDTO;
import com.experiment.pojo.TeacherStatsDTO;

public interface StatsService {
    // 获取教师统计信息
    TeacherStatsDTO getTeacherStats(Long teacherId);
    
    // 获取教师班级统计
    Map<String, Object> getTeacherClassStats(Long teacherId);
    
    // 获取学生统计信息
    StudentStatsDTO getStudentStats(Long studentId);
    
    // 获取系统统计信息
    SystemStatsDTO getSystemStats();
    
    // 获取课程统计信息
    Map<String, Object> getCourseStats(Long courseId);
    
    // 获取成绩分布
    java.util.Map<String, Double> getGradeDistribution();
    
    // 获取错误知识点统计
    java.util.List<java.util.Map<String, Object>> getErrorTopics();
    
    // 获取学习趋势数据
    java.util.List<Double> getLearningTrend(Long studentId);
    
    // 获取使用统计
    Map<String, Object> getUsageStats();
    
    // 获取效率指数
    Map<String, Object> getEfficiencyIndex();
    
    // 获取系统状态
    Map<String, Object> getSystemStatus();
    
    // 获取性能指标
    Map<String, Object> getPerformanceMetrics();
    
    // 获取分析数据
    Map<String, Object> getAnalyticsData();
    
    // 获取系统信息
    Map<String, Object> getSystemInfo();
    
    // 获取学习趋势
    Map<String, Object> getLearningTrends();
    
    // 获取资源列表
    Map<String, Object> getResources(Map<String, Object> params);
    
    // 获取资源统计
    Map<String, Object> getResourceStats();
    
    // 上传资源
    Map<String, Object> uploadResource(Map<String, Object> resourceData);
    
    // 删除资源
    boolean deleteResource(Long id);
    
    // 更新资源
    Map<String, Object> updateResource(Map<String, Object> resourceData);
    
    // 获取教学效率趋势
    Map<String, Object> getEfficiencyTrend(Long teacherId, String type);
} 