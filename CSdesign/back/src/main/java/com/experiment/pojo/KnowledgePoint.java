package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class KnowledgePoint {
    private Long id;
    private String name;
    private String description;
    private String subject;
    private Integer difficultyLevel;
    private Integer estimatedDuration;
    private String keywords;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 