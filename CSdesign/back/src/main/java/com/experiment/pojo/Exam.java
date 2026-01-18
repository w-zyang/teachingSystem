package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Exam {
    private Long id;
    private String name;
    private String type; // quiz, midterm, final, homework
    private Integer duration; // 考试时长（分钟）
    private Integer totalScore; // 总分
    private Long courseId; // 关联课程ID
    private Long teacherId; // 创建教师ID
    private String status; // draft, published, archived
    private List<Question> questions; // 题目列表
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 