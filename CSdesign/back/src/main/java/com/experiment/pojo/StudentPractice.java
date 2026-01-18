package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentPractice {
    private Long id;
    private Long studentId;
    private Long practiceId;
    private Integer score; // 得分
    private Integer totalScore; // 总分
    private Integer accuracy; // 正确率
    private String status; // in_progress, completed
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime completeTime; // 完成时间
    private Integer duration; // 实际用时（分钟）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 