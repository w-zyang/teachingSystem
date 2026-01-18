package com.experiment.pojo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Lesson {
    private Long id;
    private Long chapterId;
    private String title;
    private String content; // 教学内容
    private String videoUrl; // 视频链接
    private String resources; // 教学资源
    private Integer duration; // 时长（分钟）
    private Integer sortOrder; // 排序
    private String status; // 状态：draft, published
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 