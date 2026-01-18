package com.experiment.pojo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TeacherEfficiencyTrend {
    private Long id;
    private Long teacherId;
    private LocalDate statDate;
    private String periodType; // day/week/month
    
    // 核心指标
    private Double efficiencyScore;
    private Double avgScore;
    private Integer courseCount;
    private Integer studentCount;
    private Double teachingHours;
    
    // 参与度指标
    private Double studentEngagement;
    private Integer activeStudentCount;
    private Double completionRate;
    
    // 考试指标
    private Integer examCount;
    private Double passRate;
    private Double excellentRate;
    
    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
