package com.experiment.pojo;

import lombok.Data;
import java.util.List;

@Data
public class CourseDesignDTO {
    private String name;
    private String subject;
    private String grade;
    private Integer duration;
    private String description;
    private String objectives;
    private List<ChapterDesignDTO> chapters;
} 