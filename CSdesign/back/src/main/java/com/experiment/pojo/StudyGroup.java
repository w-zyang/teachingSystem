package com.experiment.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudyGroup {
    private Long id;
    private String groupName;
    private String groupDescription;
    private String subject;
    private Long leaderId;
    private Integer maxMembers;
    private Integer currentMembers;
    private String groupType; // study, project, competition, practice
    private String privacyLevel; // public, private, invite_only
    private String activityLevel; // low, moderate, high
    private String learningGoals;
    private String groupRules;
    private String meetingSchedule; // JSON字符串
    private String performanceMetrics; // JSON字符串
    private String status; // active, inactive, disbanded
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 