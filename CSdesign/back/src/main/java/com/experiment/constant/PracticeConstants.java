package com.experiment.constant;

/**
 * 练习相关常量
 */
public class PracticeConstants {
    
    // 练习类型
    public static final String TYPE_ALGORITHM = "algorithm";
    public static final String TYPE_PROGRAMMING = "programming";
    public static final String TYPE_SYSTEM_DESIGN = "system_design";
    public static final String TYPE_DATABASE = "database";
    
    // 练习难度
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String DIFFICULTY_HARD = "hard";
    
    // 练习状态
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_ARCHIVED = "archived";
    
    // 学生练习状态
    public static final String STUDENT_STATUS_IN_PROGRESS = "in_progress";
    public static final String STUDENT_STATUS_COMPLETED = "completed";
    
    // 默认值
    public static final int DEFAULT_TOTAL_SCORE = 100;
    public static final int DEFAULT_DURATION = 30;
    public static final int DEFAULT_QUESTION_COUNT = 10;
    
    // 错误消息
    public static final String ERROR_PRACTICE_NOT_FOUND = "练习不存在";
    public static final String ERROR_PRACTICE_ALREADY_STARTED = "练习已在进行中";
    public static final String ERROR_PRACTICE_ALREADY_COMPLETED = "练习已完成";
    public static final String ERROR_PRACTICE_RECORD_NOT_FOUND = "练习记录不存在";
    public static final String ERROR_INVALID_ANSWER = "答案格式无效";
} 