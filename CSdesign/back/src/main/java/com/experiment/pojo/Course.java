package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Course {
    private Long id;
    private String name;
    private String description;
    private String subject; // 专业方向
    private String grade; // 适用年级
    private Integer duration; // 课程时长
    private String objectives; // 学习目标
    private String level; // 难度等级：beginner, intermediate, advanced
    private String levelText; // 难度等级文本
    private String image; // 课程封面
    private Long teacherId; // 教师ID
    private String teacherName; // 教师姓名
    private Integer students; // 学习人数
    private Double rating; // 评分
    private String status; // 状态：draft, published, archived
    private List<Chapter> chapters; // 课程章节
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 