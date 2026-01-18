package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 题库实体类 - 用于缓存AI生成的题目
 */
@Data
public class QuestionBank {
    private Long id;
    private String subject; // 学科/主题
    private String knowledgePoint; // 知识点
    private String type; // 题目类型：single_choice, multiple_choice, fill, short_answer, coding
    private String difficulty; // 难度：easy, medium, hard
    private String content; // 题目内容
    private String options; // 选项（JSON格式）
    private String answer; // 正确答案
    private String analysis; // 题目解析
    private Integer score; // 建议分值
    private String source; // 来源：ai_generated, manual, imported
    private Integer useCount; // 使用次数
    private Double avgScore; // 平均得分率
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
