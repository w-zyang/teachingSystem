package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课件更新记录实体类
 */
@Data
public class CoursewareUpdate {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String originalFileUrl;
    private String originalFileName;
    private String updateType;
    private String updateRequirements;
    private String aiSuggestions;
    private String updatedFileUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

