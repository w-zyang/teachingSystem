package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Practice {
    private Long id;
    private String title;
    private String description;
    private String type; // algorithm, programming, system_design, database
    private String difficulty; // easy, medium, hard
    private Integer duration; // 练习时长（分钟）
    private Integer questionCount; // 题目数量
    private List<Question> questions; // 题目列表
    private Long courseId; // 关联课程ID
    private String status; // draft, published, archived
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 