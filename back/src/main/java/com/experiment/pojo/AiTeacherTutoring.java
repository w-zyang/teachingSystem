package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI教师辅导记录实体类
 */
@Data
public class AiTeacherTutoring {
    private Long id;
    private Long teacherId;
    private String sessionId;
    private String question;
    private String answer;
    private String knowledgeTopic;
    private Boolean helpful;
    private LocalDateTime createTime;
}

