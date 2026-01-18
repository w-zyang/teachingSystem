package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentAnswer {
    private Long id;
    private Long studentExamId;
    private Long questionId;
    private String answer; // 学生答案
    private Boolean isCorrect; // 是否正确
    private Integer score; // 得分
    private String teacherComment; // 教师评语
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 