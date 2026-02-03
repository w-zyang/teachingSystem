package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 章节实体类
 */
@Data
public class Chapter {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 章节下的课时列表（非数据库字段）
    private List<Lesson> lessons;
}
