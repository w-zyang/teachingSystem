package com.experiment.pojo;

import lombok.Data;

@Data
public class CourseQueryDTO {
    private String searchQuery;
    private String subject;
    private String level;
    private String sortBy; // popular, newest, rating
    private Integer page;
    private Integer size;
} 