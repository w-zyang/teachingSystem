package com.experiment.pojo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 错题训练数据传输对象
 */
@Data
public class ErrorQuestionTrainingDTO {
    private Long studentId;                // 学生ID
    private String trainingType;           // 训练类型：similar, knowledge_point, comprehensive
    private Long originalQuestionId;       // 原始错题ID
    private String knowledgePoint;         // 目标知识点
    private String difficulty;             // 难度等级
    private Integer questionCount;         // 生成题目数量
    private List<Map<String, Object>> questions; // 生成的题目列表
    private String analysisReport;         // 分析报告
    private List<String> targetErrorTypes; // 目标错误类型列表
    private Double expectedImprovement;    // 预期提升度
    private LocalDateTime createTime;      // 创建时间
} 