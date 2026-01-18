package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentProgress {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long chapterId;
    private Long lessonId;
    private String status; // not_started, in_progress, completed
    private Integer progress; // 进度百分比
    private LocalDateTime startTime; // 开始学习时间
    private LocalDateTime completeTime; // 完成时间
    private Integer studyDuration; // 学习时长（分钟）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 