package com.experiment.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.pojo.StudentStatsDTO;
import com.experiment.pojo.SystemStatsDTO;
import com.experiment.pojo.TeacherStatsDTO;
import com.experiment.result.Result;
import com.experiment.service.StatsService;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    // 获取教师统计信息
    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherStats(@PathVariable Long teacherId) {
        TeacherStatsDTO stats = statsService.getTeacherStats(teacherId);
        return Result.success("获取教师统计信息成功", stats);
    }
    
    // 获取教师班级统计
    @GetMapping("/teacher/{teacherId}/class")
    public Result getTeacherClassStats(@PathVariable Long teacherId) {
        Map<String, Object> stats = statsService.getTeacherClassStats(teacherId);
        return Result.success("获取教师班级统计成功", stats);
    }
    
    // 获取学生统计信息
    @GetMapping("/student/{studentId}")
    public Result getStudentStats(@PathVariable Long studentId) {
        StudentStatsDTO stats = statsService.getStudentStats(studentId);
        return Result.success("获取学生统计信息成功", stats);
    }
    
    // 获取系统统计信息
    @GetMapping("/system")
    public Result getSystemStats() {
        SystemStatsDTO stats = statsService.getSystemStats();
        return Result.success("获取系统统计信息成功", stats);
    }
    
    // 获取课程统计信息
    @GetMapping("/course/{courseId}")
    public Result getCourseStats(@PathVariable Long courseId) {
        Map<String, Object> stats = statsService.getCourseStats(courseId);
        return Result.success("获取课程统计信息成功", stats);
    }
    
    // 获取成绩分布
    @GetMapping("/grade-distribution")
    public Result getGradeDistribution() {
        Map<String, Double> distribution = statsService.getGradeDistribution();
        return Result.success("获取成绩分布成功", distribution);
    }
    
    // 获取错误知识点统计
    @GetMapping("/error-topics")
    public Result getErrorTopics() {
        List<Map<String, Object>> topics = statsService.getErrorTopics();
        return Result.success("获取错误知识点统计成功", topics);
    }
    
    // 获取学习趋势数据
    @GetMapping("/learning-trend/{studentId}")
    public Result getLearningTrend(@PathVariable Long studentId) {
        List<Double> trend = statsService.getLearningTrend(studentId);
        return Result.success("获取学习趋势成功", trend);
    }
    
    // 获取使用统计
    @GetMapping("/usage")
    public Result getUsageStats() {
        Map<String, Object> stats = statsService.getUsageStats();
        return Result.success("获取使用统计成功", stats);
    }
    
    // 获取效率指数
    @GetMapping("/efficiency")
    public Result getEfficiencyIndex() {
        Map<String, Object> index = statsService.getEfficiencyIndex();
        return Result.success("获取效率指数成功", index);
    }
    
    // 获取系统状态
    @GetMapping("/status")
    public Result getSystemStatus() {
        Map<String, Object> status = statsService.getSystemStatus();
        return Result.success("获取系统状态成功", status);
    }
    
    // 获取性能指标
    @GetMapping("/performance")
    public Result getPerformanceMetrics() {
        Map<String, Object> metrics = statsService.getPerformanceMetrics();
        return Result.success("获取性能指标成功", metrics);
    }
    
    // 获取分析数据
    @GetMapping("/analytics")
    public Result getAnalyticsData() {
        Map<String, Object> data = statsService.getAnalyticsData();
        return Result.success("获取分析数据成功", data);
    }
    
    // 获取系统信息
    @GetMapping("/info")
    public Result getSystemInfo() {
        Map<String, Object> info = statsService.getSystemInfo();
        return Result.success("获取系统信息成功", info);
    }
    
    // 获取学习趋势
    @GetMapping("/learning-trends")
    public Result getLearningTrends() {
        Map<String, Object> trends = statsService.getLearningTrends();
        return Result.success("获取学习趋势成功", trends);
    }
    
    // 获取资源列表
    @GetMapping("/resources")
    public Result getResources(@RequestParam(required = false) String type,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("size", size);
        
        Map<String, Object> result = statsService.getResources(params);
        return Result.success("获取资源列表成功", result);
    }
    
    // 获取资源统计
    @GetMapping("/resources/stats")
    public Result getResourceStats() {
        Map<String, Object> stats = statsService.getResourceStats();
        return Result.success("获取资源统计成功", stats);
    }
    
    // 上传资源
    @PostMapping("/resources/upload")
    public Result uploadResource(@RequestBody Map<String, Object> resourceData) {
        Map<String, Object> result = statsService.uploadResource(resourceData);
        return Result.success("上传资源成功", result);
    }
    
    // 删除资源
    @DeleteMapping("/resources/{id}")
    public Result deleteResource(@PathVariable Long id) {
        boolean success = statsService.deleteResource(id);
        if (success) {
            return Result.success("删除资源成功");
        } else {
            return Result.error("删除资源失败");
        }
    }
    
    // 更新资源
    @PutMapping("/resources/update")
    public Result updateResource(@RequestBody Map<String, Object> resourceData) {
        Map<String, Object> result = statsService.updateResource(resourceData);
        return Result.success("更新资源成功", result);
    }
    
    // 获取教学效率趋势
    @GetMapping("/teacher/{teacherId}/efficiency-trend")
    public Result getEfficiencyTrend(
        @PathVariable Long teacherId,
        @RequestParam(defaultValue = "month") String type
    ) {
        Map<String, Object> trend = statsService.getEfficiencyTrend(teacherId, type);
        return Result.success("获取教学效率趋势成功", trend);
    }
} 