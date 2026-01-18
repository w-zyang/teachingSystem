package com.experiment.pojo;

import lombok.Data;

@Data
public class StudentStatsDTO {
    private Double totalStudyTime; // 学习时长
    private Integer completedCourses; // 已完成课程
    private Double avgScore; // 平均成绩
    private Integer streakDays; // 连续学习天数
    private Integer totalExams; // 总考试数
    private Integer completedExams; // 已完成考试
    private Double completionRate; // 完成率
    private Integer rank; // 班级排名
} 