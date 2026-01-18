package com.experiment.pojo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 错题记录实体类
 */
@Data
public class ErrorQuestionRecord {
    private Long id;
    private Long studentId;
    private String questionId; // 可以是数字ID或临时ID
    private String questionType; // choice, fill, short, coding, essay
    private String questionContent; // 题目内容
    private String questionOptions; // JSON格式的选项（如果是选择题）
    private String userAnswer; // 学生答案
    private String correctAnswer; // 正确答案
    private String knowledgePoint; // 知识点
    private String errorType; // 错误类型
    private String errorReason; // 错误原因
    private String source; // 来源：practice, exam
    private Integer errorCount; // 错误次数
    private Double errorRate; // 错误率
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

