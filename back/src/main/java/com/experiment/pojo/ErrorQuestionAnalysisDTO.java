package com.experiment.pojo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 错题分析数据传输对象
 */
@Data
public class ErrorQuestionAnalysisDTO {
    private Long questionId;           // 题目ID
    private String questionContent;     // 题目内容
    private String questionType;        // 题目类型：choice, fill, short, coding
    private String knowledgePoint;      // 知识点
    private String topic;              // 主题
    private String difficulty;         // 难度等级
    private String studentAnswer;      // 学生答案
    private String correctAnswer;      // 正确答案
    private String errorType;          // 错误类型
    private String errorReason;        // 错误原因
    private Integer errorCount;        // 错误次数
    private Double errorRate;          // 错误率
    private LocalDateTime lastErrorTime; // 最后错误时间
    private List<String> relatedConcepts; // 相关概念
    private String improvementSuggestion; // 改进建议
    private List<Map<String, String>> options; // 题目选项（用于选择题）
} 