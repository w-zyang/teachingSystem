package com.experiment.pojo;

import lombok.Data;
import java.util.List;

@Data
public class ChapterDesignDTO {
    private String title;
    private String description;
    private Integer duration;
    private String objectives;
    private List<LessonDesignDTO> lessons;
} 