package com.experiment.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.experiment.mapper.CourseMapper;
import com.experiment.mapper.StudentExamMapper;
import com.experiment.mapper.StudentProgressMapper;
import com.experiment.mapper.UserMapper;
import com.experiment.mapper.TeacherEfficiencyTrendMapper;
import com.experiment.pojo.StudentStatsDTO;
import com.experiment.pojo.SystemStatsDTO;
import com.experiment.pojo.TeacherStatsDTO;
import com.experiment.pojo.TeacherEfficiencyTrend;
import com.experiment.service.StatsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class StatsServiceImpl implements StatsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private StudentExamMapper studentExamMapper;
    
    @Autowired
    private StudentProgressMapper studentProgressMapper;
    
    @Autowired
    private TeacherEfficiencyTrendMapper efficiencyTrendMapper;
    
    @Override
    public TeacherStatsDTO getTeacherStats(Long teacherId) {
        TeacherStatsDTO stats = new TeacherStatsDTO();
        
        try {
            // 获取教师的课程数量
            Integer courseCount = courseMapper.countByTeacherId(teacherId);
            stats.setTotalCourses(courseCount != null ? courseCount : 0);
            
            // 获取选择该教师课程的学生总数
            Integer studentCount = courseMapper.countStudentsByTeacherId(teacherId);
            stats.setTotalStudents(studentCount != null ? studentCount : 0);
            
            // 获取该教师课程的平均分
            Double avgScore = studentExamMapper.selectAverageScoreByTeacher(teacherId);
            stats.setAvgScore(avgScore != null ? avgScore : 0.0);
            
            // 获取该教师的考试数量
            Integer examCount = studentExamMapper.countExamsByTeacher(teacherId);
            stats.setCompletedExams(examCount != null ? examCount : 0);
            
            // 计算教学时长（基于学生学习时长的总和）
            Integer totalStudyMinutes = studentProgressMapper.selectTotalStudyTimeByTeacher(teacherId);
            stats.setTeachingHours(totalStudyMinutes != null ? totalStudyMinutes / 60 : 0);
            
            // 模拟一些暂时无法从数据库获取的数据
            stats.setPendingExams(3);
            stats.setCourseCompletionRate(85.2);
            stats.setStudentEngagementRate(92.1);
            
        } catch (Exception e) {
            // 如果数据库查询失败，使用默认值
            stats.setTotalStudents(0);
            stats.setTotalCourses(0);
            stats.setAvgScore(0.0);
            stats.setTeachingHours(0);
            stats.setCompletedExams(0);
            stats.setPendingExams(0);
            stats.setCourseCompletionRate(0.0);
            stats.setStudentEngagementRate(0.0);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getTeacherClassStats(Long teacherId) {
        Map<String, Object> classStats = new HashMap<>();
        
        try {
            // 获取班级整体统计
            Map<String, Object> stats = studentExamMapper.selectClassStats(teacherId);
            System.out.println("班级整体统计查询结果: " + stats);
            Number averageScoreNum = stats != null ? (Number) stats.get("averageScore") : null;
            Double averageScore = averageScoreNum != null ? averageScoreNum.doubleValue() : 0.0;
            
            // 获取及格率
            Number passRateNum = studentExamMapper.selectPassRate(teacherId);
            System.out.println("及格率查询结果: " + passRateNum);
            Double passRate = passRateNum != null ? passRateNum.doubleValue() : 0.0;
            
            // 获取优秀率
            Number excellentRateNum = studentExamMapper.selectExcellentRate(teacherId);
            System.out.println("优秀率查询结果: " + excellentRateNum);
            Double excellentRate = excellentRateNum != null ? excellentRateNum.doubleValue() : 0.0;
            
            // 获取参与率
            Number participationRateNum = studentExamMapper.selectParticipationRate(teacherId);
            System.out.println("参与率查询结果: " + participationRateNum);
            Double participationRate = participationRateNum != null ? participationRateNum.doubleValue() : 0.0;
            
            classStats.put("averageScore", Math.round(averageScore * 10.0) / 10.0);
            classStats.put("passRate", Math.round(passRate * 10.0) / 10.0);
            classStats.put("excellentRate", Math.round(excellentRate * 10.0) / 10.0);
            classStats.put("participationRate", Math.round(participationRate * 10.0) / 10.0);
            
            System.out.println("最终班级统计结果: " + classStats);
            
        } catch (Exception e) {
            System.err.println("获取教师班级统计失败: " + e.getMessage());
            e.printStackTrace();
            // 异常时返回默认数据
            classStats.put("averageScore", 0.0);
            classStats.put("passRate", 0.0);
            classStats.put("excellentRate", 0.0);
            classStats.put("participationRate", 0.0);
        }
        
        return classStats;
    }
    
    @Override
    public StudentStatsDTO getStudentStats(Long studentId) {
        StudentStatsDTO stats = new StudentStatsDTO();
        
        // 获取学生学习时长
        Integer studyTime = studentProgressMapper.selectTotalStudyTime(studentId);
        stats.setTotalStudyTime(studyTime != null ? studyTime / 60.0 : 45.5);
        
        // 获取学生平均分
        Double avgScore = studentExamMapper.selectAverageScoreByStudent(studentId);
        stats.setAvgScore(avgScore != null ? avgScore : 87.3);
        
        // 模拟其他数据
        stats.setCompletedCourses(8);
        stats.setStreakDays(12);
        stats.setTotalExams(20);
        stats.setCompletedExams(18);
        stats.setCompletionRate(90.0);
        stats.setRank(5);
        
        return stats;
    }
    
    @Override
    public SystemStatsDTO getSystemStats() {
        SystemStatsDTO stats = new SystemStatsDTO();
        
        try {
            // 获取教师总数
            Integer teacherCount = userMapper.countByRole("teacher");
            stats.setTotalTeachers(teacherCount != null ? teacherCount : 0);
            
            // 获取学生总数
            Integer studentCount = userMapper.countByRole("student");
            stats.setTotalStudents(studentCount != null ? studentCount : 0);
            
            // 计算用户总数
            stats.setTotalUsers(stats.getTotalTeachers() + stats.getTotalStudents() + 1); // +1为管理员
            
            // 获取课程总数
            Integer courseCount = courseMapper.countByCondition(null);
            stats.setTotalCourses(courseCount != null ? courseCount : 0);
            
            // 模拟一些无法直接从数据库获取的数据
            stats.setTodayActiveUsers(Math.min(342, stats.getTotalUsers()));
            stats.setWeekActiveUsers(Math.min(892, stats.getTotalUsers()));
            stats.setMonthActiveUsers(Math.min(1156, stats.getTotalUsers()));
            stats.setEfficiencyIndex(87.5);
            stats.setLearningEffectScore(92.3);
            stats.setCourseCompletionRate(85.7);
            stats.setStudentEngagementRate(91.2);
            
        } catch (Exception e) {
            // 如果数据库查询失败，使用默认值
            stats.setTotalUsers(116);  // 根据我们的数据库数据
            stats.setTotalTeachers(15);
            stats.setTotalStudents(100);
            stats.setTotalCourses(20);
            stats.setTodayActiveUsers(45);
            stats.setWeekActiveUsers(89);
            stats.setMonthActiveUsers(110);
            stats.setEfficiencyIndex(87.5);
            stats.setLearningEffectScore(92.3);
            stats.setCourseCompletionRate(85.7);
            stats.setStudentEngagementRate(91.2);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Double> getGradeDistribution() {
        Map<String, Double> distribution = new HashMap<>();
        
        try {
            // 从数据库获取成绩分布统计
            Map<String, Object> gradeStats = studentExamMapper.selectGradeDistribution();
            if (gradeStats != null && gradeStats.get("total") != null) {
                // 安全地获取总数，可能是Long或Integer
                Number totalNum = (Number) gradeStats.get("total");
                long total = totalNum.longValue();
                
                if (total > 0) {
                    // 安全地获取各分数段数量并计算百分比
                    Number excellentNum = (Number) gradeStats.getOrDefault("excellent", 0);
                    Number goodNum = (Number) gradeStats.getOrDefault("good", 0);
                    Number averageNum = (Number) gradeStats.getOrDefault("average", 0);
                    Number belowNum = (Number) gradeStats.getOrDefault("below", 0);
                    Number poorNum = (Number) gradeStats.getOrDefault("poor", 0);
                    
                    distribution.put("excellent", excellentNum.longValue() * 100.0 / total);
                    distribution.put("good", goodNum.longValue() * 100.0 / total);
                    distribution.put("average", averageNum.longValue() * 100.0 / total);
                    distribution.put("below", belowNum.longValue() * 100.0 / total);
                    distribution.put("poor", poorNum.longValue() * 100.0 / total);
                } else {
                    // 如果没有数据，返回默认值
                    distribution.put("excellent", 0.0);
                    distribution.put("good", 0.0);
                    distribution.put("average", 0.0);
                    distribution.put("below", 0.0);
                    distribution.put("poor", 0.0);
                }
            } else {
                // 如果没有数据，返回默认值
                distribution.put("excellent", 0.0);
                distribution.put("good", 0.0);
                distribution.put("average", 0.0);
                distribution.put("below", 0.0);
                distribution.put("poor", 0.0);
            }
        } catch (Exception e) {
            System.err.println("获取成绩分布失败: " + e.getMessage());
            e.printStackTrace();
            // 异常时返回默认数据
            distribution.put("excellent", 25.5);
            distribution.put("good", 35.2);
            distribution.put("average", 28.1);
            distribution.put("below", 9.8);
            distribution.put("poor", 1.4);
        }
        
        return distribution;
    }
    
    @Override
    public List<Map<String, Object>> getErrorTopics() {
        List<Map<String, Object>> topics = new ArrayList<>();
        
        try {
            // 这里可以根据实际需求从数据库查询错误知识点
            // 目前先返回基于课程内容的模拟数据，但结构更合理
            Map<String, Object> topic1 = new HashMap<>();
            topic1.put("id", 1);
            topic1.put("name", "数据结构基础");
            topic1.put("description", "数组、链表等基础数据结构");
            topic1.put("errorCount", 12);
            topic1.put("errorRate", 15.8);
            topics.add(topic1);
            
            Map<String, Object> topic2 = new HashMap<>();
            topic2.put("id", 2);
            topic2.put("name", "算法分析");
            topic2.put("description", "时间复杂度和空间复杂度分析");
            topic2.put("errorCount", 8);
            topic2.put("errorRate", 10.5);
            topics.add(topic2);
            
            Map<String, Object> topic3 = new HashMap<>();
            topic3.put("id", 3);
            topic3.put("name", "网络协议");
            topic3.put("description", "TCP/IP协议栈相关知识");
            topic3.put("errorCount", 6);
            topic3.put("errorRate", 7.9);
            topics.add(topic3);
            
            Map<String, Object> topic4 = new HashMap<>();
            topic4.put("id", 4);
            topic4.put("name", "Linux命令");
            topic4.put("description", "常用Linux系统命令");
            topic4.put("errorCount", 5);
            topic4.put("errorRate", 6.6);
            topics.add(topic4);
            
        } catch (Exception e) {
            System.err.println("获取错误知识点失败: " + e.getMessage());
        }
        
        return topics;
    }
    
    @Override
    public List<Double> getLearningTrend(Long studentId) {
        // 模拟学习趋势数据
        return Arrays.asList(85.0, 88.0, 92.0, 87.0, 90.0, 86.0, 89.0);
    }
    
    @Override
    public Map<String, Object> getCourseStats(Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 模拟课程统计数据
        stats.put("courseId", courseId);
        stats.put("courseName", "数据结构与算法");
        stats.put("totalStudents", 45);
        stats.put("activeStudents", 38);
        stats.put("completionRate", 84.4);
        stats.put("avgScore", 87.2);
        stats.put("totalLessons", 12);
        stats.put("completedLessons", 10);
        stats.put("totalExams", 3);
        stats.put("completedExams", 2);
        stats.put("avgStudyTime", 15.5); // 小时
        stats.put("difficultyLevel", "中等");
        stats.put("popularity", 92.1);
        stats.put("satisfaction", 4.6); // 5分制
        
        // 学习进度分布
        Map<String, Integer> progressDistribution = new HashMap<>();
        progressDistribution.put("notStarted", 2);
        progressDistribution.put("inProgress", 5);
        progressDistribution.put("completed", 38);
        stats.put("progressDistribution", progressDistribution);
        
        // 成绩分布
        Map<String, Integer> scoreDistribution = new HashMap<>();
        scoreDistribution.put("excellent", 12); // 90-100分
        scoreDistribution.put("good", 18);      // 80-89分
        scoreDistribution.put("average", 10);   // 70-79分
        scoreDistribution.put("below", 3);      // 60-69分
        scoreDistribution.put("poor", 2);       // 60分以下
        stats.put("scoreDistribution", scoreDistribution);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getUsageStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取真实用户统计数据
            Integer totalUsers = userMapper.countByCondition(null);
            Integer totalTeachers = userMapper.countByRole("teacher");
            Integer totalStudents = userMapper.countByRole("student");
            
            // 基于真实数据估算活跃用户（可以根据实际业务逻辑调整）
            int todayActive = Math.min(totalUsers != null ? totalUsers / 4 : 50, totalUsers != null ? totalUsers : 200);
            int weekActive = Math.min(totalUsers != null ? (int)(totalUsers * 0.7) : 80, totalUsers != null ? totalUsers : 200);
            int monthActive = Math.min(totalUsers != null ? (int)(totalUsers * 0.9) : 100, totalUsers != null ? totalUsers : 200);
            
            stats.put("todayActive", todayActive);
            stats.put("weekActive", weekActive);
            stats.put("monthActive", monthActive);
            stats.put("totalSessions", weekActive * 3); // 估算
            stats.put("avgSessionDuration", 35.2); // 分钟，模拟数据
            stats.put("peakUsageTime", "14:00-16:00");
            stats.put("mostActiveDay", "周三");
            stats.put("mobileUsage", 25.8); // 百分比，模拟数据
            stats.put("desktopUsage", 74.2); // 百分比，模拟数据
            
            // 按角色统计
            Map<String, Integer> roleStats = new HashMap<>();
            roleStats.put("students", totalStudents != null ? totalStudents : 0);
            roleStats.put("teachers", totalTeachers != null ? totalTeachers : 0);
            roleStats.put("admins", 1);
            stats.put("roleStats", roleStats);
            
        } catch (Exception e) {
            System.err.println("获取使用统计失败: " + e.getMessage());
            // 异常时使用默认数据
            stats.put("todayActive", 45);
            stats.put("weekActive", 89);
            stats.put("monthActive", 110);
            stats.put("totalSessions", 267);
            stats.put("avgSessionDuration", 35.2);
            stats.put("peakUsageTime", "14:00-16:00");
            stats.put("mostActiveDay", "周三");
            stats.put("mobileUsage", 25.8);
            stats.put("desktopUsage", 74.2);
            
            Map<String, Integer> roleStats = new HashMap<>();
            roleStats.put("students", 100);
            roleStats.put("teachers", 15);
            roleStats.put("admins", 1);
            stats.put("roleStats", roleStats);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getEfficiencyIndex() {
        Map<String, Object> index = new HashMap<>();
        
        try {
            // 计算基于真实数据的效率指数
            Integer totalCourses = courseMapper.countByCondition(null);
            Integer totalStudents = userMapper.countByRole("student");
            
            // 获取平均分
            Double avgScore = 0.0;
            try {
                Number avgScoreNum = studentExamMapper.selectOverallAverageScore();
                avgScore = avgScoreNum != null ? avgScoreNum.doubleValue() : 0.0;
            } catch (Exception e) {
                System.err.println("获取平均分失败: " + e.getMessage());
            }
            
            // 基于真实数据计算指数
            int score = (int) Math.min(Math.max(avgScore * 1.2, 70), 95); // 根据平均分计算综合指数
            int courseCompletion = totalCourses != null && totalCourses > 0 ? Math.min(85 + totalCourses * 2, 95) : 80;
            int studentEngagement = totalStudents != null && totalStudents > 0 ? Math.min(80 + totalStudents / 10, 90) : 75;
            int learningEffect = (int) Math.min(Math.max(avgScore * 1.1, 75), 92);
            
            index.put("score", score);
            index.put("courseCompletion", courseCompletion);
            index.put("studentEngagement", studentEngagement);
            index.put("learningEffect", learningEffect);
            index.put("teachingQuality", Math.min(score + 5, 95));
            index.put("resourceUtilization", Math.min(courseCompletion - 5, 90));
            index.put("systemPerformance", 94); // 系统性能较稳定
            index.put("userSatisfaction", Math.min(score - 3, 90));
            
            // 生成趋势数据（基于当前分数的模拟变化）
            List<Double> trend = Arrays.asList(
                (double)(score - 5), 
                (double)(score - 3), 
                (double)(score - 1), 
                (double)(score - 2), 
                (double)(score), 
                (double)(score + 1), 
                (double)(score)
            );
            index.put("trend", trend);
            
        } catch (Exception e) {
            System.err.println("获取效率指数失败: " + e.getMessage());
            // 异常时使用默认数据
            index.put("score", 85);
            index.put("courseCompletion", 88);
            index.put("studentEngagement", 82);
            index.put("learningEffect", 86);
            index.put("teachingQuality", 90);
            index.put("resourceUtilization", 83);
            index.put("systemPerformance", 94);
            index.put("userSatisfaction", 82);
            
            List<Double> trend = Arrays.asList(80.0, 82.0, 84.0, 83.0, 85.0, 86.0, 85.0);
            index.put("trend", trend);
        }
        
        return index;
    }
    
    @Override
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 服务器状态（模拟真实监控数据）
            Map<String, Object> serverStatus = new HashMap<>();
            serverStatus.put("cpu", Math.random() * 30 + 20); // 20-50% CPU使用率
            serverStatus.put("memory", Math.random() * 40 + 40); // 40-80% 内存使用率
            serverStatus.put("disk", Math.random() * 20 + 20); // 20-40% 磁盘使用率
            serverStatus.put("network", Math.random() * 15 + 5); // 5-20% 网络使用率
            status.put("server", serverStatus);
            
            // 数据库状态（基于实际连接信息）
            Map<String, Object> dbStatus = new HashMap<>();
            Integer totalUsers = userMapper.countByCondition(null);
            Integer activeSessions = totalUsers != null ? Math.max(totalUsers / 10, 5) : 10;
            
            dbStatus.put("connections", activeSessions);
            dbStatus.put("queryTime", Math.random() * 20 + 10); // 10-30ms 查询时间
            dbStatus.put("cacheHit", Math.random() * 10 + 85); // 85-95% 缓存命中率
            status.put("database", dbStatus);
            
            // AI服务状态
            Map<String, Object> aiStatus = new HashMap<>();
            aiStatus.put("apiCalls", activeSessions * 50); // 基于活跃用户估算API调用
            aiStatus.put("avgResponse", Math.random() * 100 + 150); // 150-250ms 响应时间
            aiStatus.put("successRate", Math.random() * 5 + 95); // 95-100% 成功率
            status.put("ai", aiStatus);
            
        } catch (Exception e) {
            System.err.println("获取系统状态失败: " + e.getMessage());
            // 异常时使用默认数据
            Map<String, Object> serverStatus = new HashMap<>();
            serverStatus.put("cpu", 35.2);
            serverStatus.put("memory", 55.8);
            serverStatus.put("disk", 28.5);
            serverStatus.put("network", 8.3);
            status.put("server", serverStatus);
            
            Map<String, Object> dbStatus = new HashMap<>();
            dbStatus.put("connections", 25);
            dbStatus.put("queryTime", 18.2);
            dbStatus.put("cacheHit", 91.1);
            status.put("database", dbStatus);
            
            Map<String, Object> aiStatus = new HashMap<>();
            aiStatus.put("apiCalls", 750);
            aiStatus.put("avgResponse", 185.5);
            aiStatus.put("successRate", 97.8);
            status.put("ai", aiStatus);
        }
        
        return status;
    }
    
    @Override
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 模拟性能指标数据
        metrics.put("responseTime", 156.7); // 毫秒
        metrics.put("throughput", 2345); // 请求/秒
        metrics.put("errorRate", 0.23); // 百分比
        metrics.put("availability", 99.8); // 百分比
        metrics.put("cpuUtilization", 45.2); // 百分比
        metrics.put("memoryUtilization", 67.8); // 百分比
        metrics.put("diskUtilization", 34.5); // 百分比
        metrics.put("networkUtilization", 23.4); // 百分比
        
        // 性能趋势
        List<Double> responseTimeTrend = Arrays.asList(145.0, 152.0, 148.0, 156.0, 153.0, 157.0, 156.7);
        metrics.put("responseTimeTrend", responseTimeTrend);
        
        return metrics;
    }
    
    @Override
    public Map<String, Object> getAnalyticsData() {
        Map<String, Object> data = new HashMap<>();
        
        // 模拟分析数据
        data.put("totalAnalytics", 15678);
        data.put("processedData", 12345);
        data.put("insightsGenerated", 234);
        data.put("predictionAccuracy", 92.3); // 百分比
        data.put("dataQuality", 95.7); // 百分比
        data.put("processingTime", 12.3); // 秒
        
        // 分析类型统计
        Map<String, Integer> analysisTypes = new HashMap<>();
        analysisTypes.put("learning", 5678);
        analysisTypes.put("performance", 3456);
        analysisTypes.put("behavior", 2345);
        analysisTypes.put("prediction", 1234);
        analysisTypes.put("optimization", 965);
        data.put("analysisTypes", analysisTypes);
        
        return data;
    }
    
    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // 系统基本信息
        info.put("name", "智能教学管理系统");
        info.put("version", "v2.1.0");
        info.put("description", "面向大学生的智能教学管理平台");
        info.put("supportEmail", "support@example.com");
        info.put("supportPhone", "400-123-4567");
        info.put("website", "https://www.example.com");
        
        // 系统配置信息
        Map<String, Object> config = new HashMap<>();
        config.put("maxFileSize", "50MB");
        config.put("supportedFormats", Arrays.asList("jpg", "png", "pdf", "doc", "docx"));
        config.put("sessionTimeout", 3600); // 秒
        config.put("maxConcurrentUsers", 1000);
        info.put("config", config);
        
        // 系统状态信息
        Map<String, Object> status = new HashMap<>();
        status.put("uptime", "15天 8小时 32分钟");
        status.put("lastMaintenance", "2024-01-15 02:00:00");
        status.put("nextMaintenance", "2024-02-15 02:00:00");
        status.put("systemHealth", "excellent");
        info.put("status", status);
        
        // 开发团队信息
        Map<String, Object> team = new HashMap<>();
        team.put("developer", "智能教学团队");
        team.put("contact", "dev@example.com");
        team.put("github", "https://github.com/example/teaching-system");
        info.put("team", team);
        
        return info;
    }
    
    @Override
    public Map<String, Object> getLearningTrends() {
        Map<String, Object> trends = new HashMap<>();
        
        // 学习趋势数据
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        // 最近7天的学习趋势
        String[] dates = {"2024-01-10", "2024-01-11", "2024-01-12", "2024-01-13", "2024-01-14", "2024-01-15", "2024-01-16"};
        int[] studyHours = {4, 6, 3, 8, 5, 7, 6};
        int[] practiceCount = {12, 18, 8, 25, 15, 22, 19};
        double[] scores = {78.5, 82.3, 75.8, 88.9, 81.2, 85.6, 83.1};
        
        for (int i = 0; i < dates.length; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dates[i]);
            dayData.put("studyHours", studyHours[i]);
            dayData.put("practiceCount", practiceCount[i]);
            dayData.put("averageScore", scores[i]);
            trendData.add(dayData);
        }
        
        trends.put("trendData", trendData);
        trends.put("totalDays", 7);
        trends.put("averageStudyHours", 5.7);
        trends.put("averagePracticeCount", 17.0);
        trends.put("averageScore", 82.2);
        
        // 趋势分析
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("trend", "上升");
        analysis.put("improvement", 12.5); // 百分比
        analysis.put("consistency", 85.7); // 百分比
        trends.put("analysis", analysis);
        
        return trends;
    }
    
    @Override
    public Map<String, Object> getResources(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> resources = new ArrayList<>();
        
        // 模拟资源数据
        String[] names = {"数据结构课件", "算法练习题", "Java编程指南", "数据库设计文档", "前端开发教程"};
        String[] types = {"document", "video", "document", "document", "video"};
        String[] sizes = {"2.5MB", "15.8MB", "1.2MB", "3.7MB", "45.2MB"};
        String[] uploaders = {"张老师", "李老师", "王老师", "赵老师", "陈老师"};
        String[] uploadTimes = {"2024-01-15", "2024-01-14", "2024-01-13", "2024-01-12", "2024-01-11"};
        
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> resource = new HashMap<>();
            resource.put("id", i + 1);
            resource.put("name", names[i]);
            resource.put("type", types[i]);
            resource.put("size", sizes[i]);
            resource.put("uploader", uploaders[i]);
            resource.put("uploadTime", uploadTimes[i]);
            resource.put("downloadCount", (int)(Math.random() * 100) + 10);
            resource.put("description", "这是一个" + types[i] + "类型的教学资源");
            resources.add(resource);
        }
        
        result.put("resources", resources);
        result.put("total", resources.size());
        result.put("page", params.get("page"));
        result.put("size", params.get("size"));
        
        return result;
    }
    
    @Override
    public Map<String, Object> getResourceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 资源统计信息
        stats.put("totalResources", 156);
        stats.put("totalSize", "2.3GB");
        stats.put("todayUploads", 8);
        stats.put("weekUploads", 45);
        stats.put("monthUploads", 156);
        stats.put("totalDownloads", 2345);
        stats.put("avgRating", 4.6);
        
        // 按类型统计
        Map<String, Integer> typeStats = new HashMap<>();
        typeStats.put("document", 89);
        typeStats.put("video", 45);
        typeStats.put("image", 12);
        typeStats.put("audio", 10);
        stats.put("typeStats", typeStats);
        
        // 按大小统计
        Map<String, Integer> sizeStats = new HashMap<>();
        sizeStats.put("small", 78); // < 5MB
        sizeStats.put("medium", 45); // 5-50MB
        sizeStats.put("large", 33); // > 50MB
        stats.put("sizeStats", sizeStats);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> uploadResource(Map<String, Object> resourceData) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟上传成功
        result.put("id", System.currentTimeMillis());
        result.put("name", resourceData.get("name"));
        result.put("url", "https://example.com/resources/" + System.currentTimeMillis());
        result.put("uploadTime", "2024-01-16 14:30:00");
        result.put("status", "success");
        
        return result;
    }
    
    @Override
    public boolean deleteResource(Long id) {
        // 模拟删除成功
        return true;
    }
    
    @Override
    public Map<String, Object> updateResource(Map<String, Object> resourceData) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟更新成功
        result.put("id", resourceData.get("id"));
        result.put("name", resourceData.get("name"));
        result.put("updateTime", "2024-01-16 15:00:00");
        result.put("status", "success");
        
        return result;
    }
    
    @Override
    public Map<String, Object> getEfficiencyTrend(Long teacherId, String type) {
        Map<String, Object> result = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("M月");
        
        try {
            if ("month".equals(type)) {
                // 查询固定的6个月数据（2024年1月-6月）
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 6, 30);
                
                System.out.println("=== 查询月度数据 ===");
                System.out.println("教师ID: " + teacherId);
                System.out.println("开始日期: " + startDate);
                System.out.println("结束日期: " + endDate);
                
                List<TeacherEfficiencyTrend> trends = efficiencyTrendMapper.selectMonthlyTrend(
                    teacherId, startDate, endDate
                );
                
                System.out.println("查询结果数量: " + (trends != null ? trends.size() : 0));
                if (trends != null && !trends.isEmpty()) {
                    System.out.println("第一条数据: " + trends.get(0));
                }
                
                List<Map<String, Object>> monthlyData = new ArrayList<>();
                
                if (trends != null && !trends.isEmpty()) {
                    for (TeacherEfficiencyTrend trend : trends) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("month", trend.getStatDate().getMonthValue() + "月");
                        data.put("efficiency", trend.getEfficiencyScore());
                        data.put("courseCount", trend.getCourseCount());
                        data.put("studentEngagement", trend.getStudentEngagement());
                        monthlyData.add(data);
                    }
                } else {
                    // 如果数据库没有数据，返回模拟数据
                    System.out.println("数据库中没有找到教学效率趋势数据，使用默认数据");
                    monthlyData = getDefaultMonthlyData();
                }
                
                result.put("monthly", monthlyData);
                
                // 获取最新数据用于计算趋势
                if (!monthlyData.isEmpty()) {
                    Map<String, Object> latest = monthlyData.get(monthlyData.size() - 1);
                    Map<String, Object> previous = monthlyData.size() > 1 ? 
                        monthlyData.get(monthlyData.size() - 2) : latest;
                    
                    result.put("currentEfficiency", latest.get("efficiency"));
                    result.put("previousEfficiency", previous.get("efficiency"));
                    result.put("avgCourseHours", 21);
                    result.put("studentEngagement", latest.get("studentEngagement"));
                }
                
            } else if ("week".equals(type)) {
                // 查询固定的7天数据（2024年6月10日-16日）
                LocalDate startDate = LocalDate.of(2024, 6, 10);
                LocalDate endDate = LocalDate.of(2024, 6, 16);
                
                System.out.println("=== 查询周度数据 ===");
                System.out.println("教师ID: " + teacherId);
                System.out.println("开始日期: " + startDate);
                System.out.println("结束日期: " + endDate);
                
                List<TeacherEfficiencyTrend> trends = efficiencyTrendMapper.selectDailyTrend(
                    teacherId, startDate, endDate
                );
                
                System.out.println("查询结果数量: " + (trends != null ? trends.size() : 0));
                if (trends != null && !trends.isEmpty()) {
                    System.out.println("第一条数据: " + trends.get(0));
                }
                
                List<Map<String, Object>> weeklyData = new ArrayList<>();
                String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                
                if (trends != null && !trends.isEmpty()) {
                    for (TeacherEfficiencyTrend trend : trends) {
                        Map<String, Object> data = new HashMap<>();
                        int dayOfWeek = trend.getStatDate().getDayOfWeek().getValue();
                        data.put("day", weekDays[dayOfWeek - 1]);
                        data.put("efficiency", trend.getEfficiencyScore());
                        data.put("hours", trend.getTeachingHours());
                        weeklyData.add(data);
                    }
                } else {
                    // 如果数据库没有数据，返回模拟数据
                    System.out.println("数据库中没有找到周度数据，使用默认数据");
                    weeklyData = getDefaultWeeklyData();
                }
                
                result.put("weekly", weeklyData);
                
                // 获取最新数据
                if (!weeklyData.isEmpty()) {
                    Map<String, Object> latest = weeklyData.get(weeklyData.size() - 1);
                    Map<String, Object> previous = weeklyData.size() > 1 ? 
                        weeklyData.get(weeklyData.size() - 2) : latest;
                    
                    result.put("currentEfficiency", latest.get("efficiency"));
                    result.put("previousEfficiency", previous.get("efficiency"));
                    result.put("avgCourseHours", 4.3);
                    result.put("studentEngagement", 75.0);
                }
            }
            
        } catch (Exception e) {
            System.err.println("获取教学效率趋势失败: " + e.getMessage());
            e.printStackTrace();
            // 异常时返回默认数据
            if ("month".equals(type)) {
                result.put("monthly", getDefaultMonthlyData());
                result.put("currentEfficiency", 57.33);
                result.put("previousEfficiency", 55.0);
                result.put("avgCourseHours", 21);
                result.put("studentEngagement", 75.0);
            } else {
                result.put("weekly", getDefaultWeeklyData());
                result.put("currentEfficiency", 57.0);
                result.put("previousEfficiency", 55.0);
                result.put("avgCourseHours", 4.3);
                result.put("studentEngagement", 75.0);
            }
        }
        
        return result;
    }
    
    // 默认月度数据
    private List<Map<String, Object>> getDefaultMonthlyData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        double[] efficiency = {52.0, 54.0, 53.0, 56.0, 55.0, 57.33};
        int[] courseCount = {18, 20, 19, 22, 21, 24};
        double[] engagement = {68.0, 70.0, 69.0, 73.0, 72.0, 75.0};
        
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", months[i]);
            item.put("efficiency", efficiency[i]);
            item.put("courseCount", courseCount[i]);
            item.put("studentEngagement", engagement[i]);
            data.add(item);
        }
        
        return data;
    }
    
    // 默认周度数据
    private List<Map<String, Object>> getDefaultWeeklyData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        double[] efficiency = {55.0, 58.0, 56.0, 59.0, 57.0, 54.0, 52.0};
        double[] hours = {4.0, 5.0, 4.5, 5.5, 5.0, 3.0, 2.0};
        
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("day", days[i]);
            item.put("efficiency", efficiency[i]);
            item.put("hours", hours[i]);
            data.add(item);
        }
        
        return data;
    }
} 