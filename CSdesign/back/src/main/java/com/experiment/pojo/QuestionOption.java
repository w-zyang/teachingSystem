package com.experiment.pojo;

import lombok.Data;

@Data
public class QuestionOption {
    private Long id;
    private Long questionId;
    private String key; // A, B, C, D
    private String content; // 选项内容
    private Boolean isCorrect; // 是否为正确答案
    private Integer sortOrder; // 排序
} 