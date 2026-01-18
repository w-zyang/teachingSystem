package com.experiment.pojo;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
    private String subject; // 教师专业
    private String grade; // 学生年级
    private String college; // 学生学院
    private String className; // 学生班级
    private String studentId; // 学生学号
    private String realName;
    private String phone;
    private Boolean agree; // 同意协议
} 