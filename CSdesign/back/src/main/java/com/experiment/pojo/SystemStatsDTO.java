package com.experiment.pojo;

import lombok.Data;

@Data
public class SystemStatsDTO {
    private Integer totalUsers; // 用户总数
    private Integer totalTeachers; // 教师数量
    private Integer totalStudents; // 学生数量
    private Integer totalCourses; // 课程总数
    private Integer todayActiveUsers; // 今日活跃用户
    private Integer weekActiveUsers; // 本周活跃用户
    private Integer monthActiveUsers; // 本月活跃用户
    private Double efficiencyIndex; // 教学效率指数
    private Double learningEffectScore; // 学习效果评分
    private Double courseCompletionRate; // 课程完成率
    private Double studentEngagementRate; // 学生参与度
} 