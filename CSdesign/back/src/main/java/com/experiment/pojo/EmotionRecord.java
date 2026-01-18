package com.experiment.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmotionRecord {
    private Long id;
    private Long studentId;
    private String sessionId;
    private String detectionType; // text, facial, voice, behavioral
    private String emotionType; // happy, sad, frustrated, confused, excited, bored, focused, stressed
    private BigDecimal confidence;
    private BigDecimal intensity;
    private String context;
    private String rawData; // JSON字符串
    private LocalDateTime detectionTime;
    private Long courseId;
    private Long lessonId;
} 