package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Chapter {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer duration; // 课时数
    private String objectives; // 教学目标
    private Integer sortOrder; // 排序
    private List<Lesson> lessons; // 课时列表
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 