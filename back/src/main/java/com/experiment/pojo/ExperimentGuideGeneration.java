package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 实验指导书生成记录实体类
 */
@Data
public class ExperimentGuideGeneration {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String chapterName;
    private String knowledgePoints;
    private String difficultyLevel;
    private String experimentTitle;
    private String experimentObjectives;
    private String experimentEnvironment;
    private String experimentSteps;
    private String experimentCode;
    private String expectedResults;
    private String thinkingQuestions;
    private String referenceAnswers;
    private String guideUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

