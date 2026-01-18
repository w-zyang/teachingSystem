package com.experiment.pojo;

import lombok.Data;

@Data
public class TeacherStatsDTO {
    private Integer totalStudents; // 学生总数
    private Integer totalCourses; // 课程数量
    private Double avgScore; // 平均成绩
    private Integer teachingHours; // 教学时长
    private Integer completedExams; // 已完成考试
    private Integer pendingExams; // 待批改考试
    private Double courseCompletionRate; // 课程完成率
    private Double studentEngagementRate; // 学生参与度
} 