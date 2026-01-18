package com.experiment.pojo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PracticeDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String difficulty;
    private String difficultyText; // 难度中文描述
    private Integer duration;
    private Integer questionCount;
    private Long courseId;
    private String courseName; // 课程名称
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 学生练习相关字段
    private Integer score;
    private Integer totalScore;
    private Integer accuracy;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private Integer actualDuration; // 实际用时
    
    // 统计字段
    private Double averageScore; // 平均分
    private Integer participantCount; // 参与人数
    
    // 题目列表
    private List<Question> questions;
} 