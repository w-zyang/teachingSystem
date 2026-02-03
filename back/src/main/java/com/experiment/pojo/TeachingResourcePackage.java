package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学资源包实体类
 */
@Data
public class TeachingResourcePackage {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String chapterName;
    private String knowledgePoints;
    private String difficultyLevel;
    private String quizContent;
    private String homeworkContent;
    private String experimentContent;
    private String summaryPpt;
    private String packageUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

