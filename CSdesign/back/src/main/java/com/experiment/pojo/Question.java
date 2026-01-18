package com.experiment.pojo;

import lombok.Data;
import java.util.List;

@Data
public class Question {
    private Long id;
    private Long examId;
    private String type; // choice, fill, short, coding
    private String content; // 题目内容
    private List<QuestionOption> options; // 选项（选择题）
    private String answer; // 正确答案
    private String analysis; // 题目解析
    private Integer score; // 分值
    private String difficulty; // 难度：easy, medium, hard
    private String knowledgePoint; // 知识点
    private Integer sortOrder; // 排序
} 