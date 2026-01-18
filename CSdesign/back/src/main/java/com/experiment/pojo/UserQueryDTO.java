package com.experiment.pojo;

import lombok.Data;

@Data
public class UserQueryDTO {
    private String searchQuery;
    private String roleFilter;
    private String statusFilter;
    private Integer page;
    private Integer size;
} 