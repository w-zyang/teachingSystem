package com.experiment.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LearningPath {
    private Long id;
    private Long studentId;
    private Long templateId;
    private String name;
    private Integer currentStep;
    private BigDecimal completionRate;
    private LocalDateTime startTime;
    private LocalDateTime estimatedCompletion;
    private LocalDateTime actualCompletion;
    private String status; // active, paused, completed, abandoned
    private String adaptationConfig; // JSON字符串
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 