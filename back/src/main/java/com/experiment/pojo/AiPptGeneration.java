package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI生成PPT记录实体类
 */
@Data
public class AiPptGeneration {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String topic;
    private String keywords;
    private Integer slideCount;
    private String style;
    private String content;
    private String pptUrl;
    private String taskId;
    private String status;
    private String errorMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

