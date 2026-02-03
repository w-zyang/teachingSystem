package com.experiment.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业批阅记录实体类
 */
@Data
public class HomeworkGrading {
    private Long id;
    private Long teacherId;
    private Long studentId;
    private Long courseId;
    private String homeworkTitle;
    private String submissionFileUrl;
    private String submissionFileName;
    private String submissionContent;
    private BigDecimal aiScore;
    private String aiComments;
    private String aiAnalysis;
    private BigDecimal teacherScore;
    private String teacherComments;
    private String status;
    private LocalDateTime submitTime;
    private LocalDateTime aiGradeTime;
    private LocalDateTime teacherReviewTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

