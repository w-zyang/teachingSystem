package com.experiment.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LearningCompanion {
    private Long id;
    private Long student1Id;
    private Long student2Id;
    private BigDecimal matchingScore;
    private String matchingCriteria; // JSON字符串
    private String commonSubjects;
    private String complementaryStrengths;
    private String status; // pending, accepted, rejected, active, inactive
    private LocalDateTime matchTime;
    private LocalDateTime activationTime;
    private Integer collaborationCount;
    private BigDecimal satisfactionRating;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 