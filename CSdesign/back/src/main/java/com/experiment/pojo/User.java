package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String role; // teacher, student, admin

    private String status; // active, inactive, suspended

    private String avatar;

    private String realName;

    private String userId; // 学号或工号

    private String college; // 学院

    private String major; // 专业

    private String grade; // 年级（学生）

    private String className; // 班级（学生）

    private String subject; // 任教专业（教师）

    private LocalDateTime registerDate;

    private LocalDateTime lastLogin;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
