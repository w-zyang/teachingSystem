package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentExam {
    private Long id;
    private Long studentId;
    private Long examId;
    private Integer score; // 得分
    private Integer totalScore; // 总分
    private String status; // submitted, graded
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime submitTime; // 提交时间
    private LocalDateTime gradeTime; // 批改时间
    private String teacherComment; // 教师评语
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 