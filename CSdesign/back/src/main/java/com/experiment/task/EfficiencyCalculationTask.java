package com.experiment.task;

import com.experiment.mapper.*;
import com.experiment.pojo.TeacherEfficiencyTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 教学效率数据定时计算任务
 * 每天凌晨2点自动计算前一天的教学效率数据
 */
@Component
public class EfficiencyCalculationTask {
    
    @Autowired
    private TeacherEfficiencyTrendMapper efficiencyTrendMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private StudentExamMapper studentExamMapper;
    
    @Autowired
    private StudentProgressMapper studentProgressMapper;
    
    /**
     * 每天凌晨2点执行
     * 计算前一天的教学效率数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateDailyEfficiency() {
        System.out.println("=== 开始计算每日教学效率 ===");
        
        try {
            // 获取所有教师
            List<Long> teacherIds = getAllTeacherIds();
            
            // 计算昨天的日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            for (Long teacherId : teacherIds) {
                try {
                    // 计算该教师昨天的教学效率
                    TeacherEfficiencyTrend trend = calculateDailyEfficiencyForTeacher(teacherId, yesterday);
                    
                    if (trend != null) {
                        // 保存到数据库
                        efficiencyTrendMapper.insertOrUpdate(trend);
                        System.out.println("✓ 教师ID=" + teacherId + " 的日度数据已更新");
                    }
                } catch (Exception e) {
                    System.err.println("✗ 计算教师ID=" + teacherId + " 的数据失败: " + e.getMessage());
                }
            }
            
            System.out.println("=== 每日教学效率计算完成 ===");
        } catch (Exception e) {
            System.err.println("每日教学效率计算任务失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 每月1号凌晨3点执行
     * 计算上个月的月度教学效率数据
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void calculateMonthlyEfficiency() {
        System.out.println("=== 开始计算月度教学效率 ===");
        
        try {
            // 获取所有教师
            List<Long> teacherIds = getAllTeacherIds();
            
            // 计算上个月
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            LocalDate lastDayOfMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            
            for (Long teacherId : teacherIds) {
                try {
                    // 计算该教师上个月的教学效率
                    TeacherEfficiencyTrend trend = calculateMonthlyEfficiencyForTeacher(teacherId, lastDayOfMonth);
                    
                    if (trend != null) {
                        // 保存到数据库
                        efficiencyTrendMapper.insertOrUpdate(trend);
                        System.out.println("✓ 教师ID=" + teacherId + " 的月度数据已更新");
                    }
                } catch (Exception e) {
                    System.err.println("✗ 计算教师ID=" + teacherId + " 的数据失败: " + e.getMessage());
                }
            }
            
            System.out.println("=== 月度教学效率计算完成 ===");
        } catch (Exception e) {
            System.err.println("月度教学效率计算任务失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 计算某个教师某一天的教学效率
     */
    private TeacherEfficiencyTrend calculateDailyEfficiencyForTeacher(Long teacherId, LocalDate date) {
        try {
            TeacherEfficiencyTrend trend = new TeacherEfficiencyTrend();
            trend.setTeacherId(teacherId);
            trend.setStatDate(date);
            trend.setPeriodType("day");
            
            // 1. 获取当天的平均成绩
            Double avgScore = getAvgScoreForDate(teacherId, date);
            trend.setAvgScore(avgScore != null ? avgScore : 0.0);
            
            // 2. 获取当天的课程数量
            Integer courseCount = getCourseCountForDate(teacherId, date);
            trend.setCourseCount(courseCount != null ? courseCount : 0);
            
            // 3. 获取当天的学生数量
            Integer studentCount = getStudentCountForDate(teacherId, date);
            trend.setStudentCount(studentCount != null ? studentCount : 0);
            
            // 4. 获取当天的教学时长（小时）
            Double teachingHours = getTeachingHoursForDate(teacherId, date);
            trend.setTeachingHours(teachingHours != null ? teachingHours : 0.0);
            
            // 5. 计算学生参与度
            Double engagement = calculateStudentEngagement(teacherId, date);
            trend.setStudentEngagement(engagement);
            
            // 6. 获取活跃学生数
            Integer activeStudents = getActiveStudentCount(teacherId, date);
            trend.setActiveStudentCount(activeStudents != null ? activeStudents : 0);
            
            // 7. 计算课程完成率
            Double completionRate = calculateCompletionRate(teacherId, date);
            trend.setCompletionRate(completionRate);
            
            // 8. 获取考试数量
            Integer examCount = getExamCountForDate(teacherId, date);
            trend.setExamCount(examCount != null ? examCount : 0);
            
            // 9. 计算及格率
            Double passRate = calculatePassRate(teacherId, date);
            trend.setPassRate(passRate);
            
            // 10. 计算优秀率
            Double excellentRate = calculateExcellentRate(teacherId, date);
            trend.setExcellentRate(excellentRate);
            
            // 11. 计算综合教学效率分数
            Double efficiencyScore = calculateEfficiencyScore(
                avgScore, engagement, completionRate, passRate
            );
            trend.setEfficiencyScore(efficiencyScore);
            
            return trend;
        } catch (Exception e) {
            System.err.println("计算日度效率失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 计算某个教师某个月的教学效率
     */
    private TeacherEfficiencyTrend calculateMonthlyEfficiencyForTeacher(Long teacherId, LocalDate monthEnd) {
        try {
            LocalDate monthStart = monthEnd.withDayOfMonth(1);
            
            TeacherEfficiencyTrend trend = new TeacherEfficiencyTrend();
            trend.setTeacherId(teacherId);
            trend.setStatDate(monthEnd);
            trend.setPeriodType("month");
            
            // 1. 获取本月的平均成绩
            Double avgScore = getAvgScoreForMonth(teacherId, monthStart, monthEnd);
            trend.setAvgScore(avgScore != null ? avgScore : 0.0);
            
            // 2. 获取本月的课程数量
            Integer courseCount = getCourseCountForMonth(teacherId, monthStart, monthEnd);
            trend.setCourseCount(courseCount != null ? courseCount : 0);
            
            // 3. 获取本月的学生数量
            Integer studentCount = getStudentCountForMonth(teacherId, monthStart, monthEnd);
            trend.setStudentCount(studentCount != null ? studentCount : 0);
            
            // 4. 获取本月的教学时长（小时）
            Double teachingHours = getTeachingHoursForMonth(teacherId, monthStart, monthEnd);
            trend.setTeachingHours(teachingHours != null ? teachingHours : 0.0);
            
            // 5. 计算学生参与度
            Double engagement = calculateStudentEngagementForMonth(teacherId, monthStart, monthEnd);
            trend.setStudentEngagement(engagement);
            
            // 6. 获取活跃学生数
            Integer activeStudents = getActiveStudentCountForMonth(teacherId, monthStart, monthEnd);
            trend.setActiveStudentCount(activeStudents != null ? activeStudents : 0);
            
            // 7. 计算课程完成率
            Double completionRate = calculateCompletionRateForMonth(teacherId, monthStart, monthEnd);
            trend.setCompletionRate(completionRate);
            
            // 8. 获取考试数量
            Integer examCount = getExamCountForMonth(teacherId, monthStart, monthEnd);
            trend.setExamCount(examCount != null ? examCount : 0);
            
            // 9. 计算及格率
            Double passRate = calculatePassRateForMonth(teacherId, monthStart, monthEnd);
            trend.setPassRate(passRate);
            
            // 10. 计算优秀率
            Double excellentRate = calculateExcellentRateForMonth(teacherId, monthStart, monthEnd);
            trend.setExcellentRate(excellentRate);
            
            // 11. 计算综合教学效率分数
            Double efficiencyScore = calculateEfficiencyScore(
                avgScore, engagement, completionRate, passRate
            );
            trend.setEfficiencyScore(efficiencyScore);
            
            return trend;
        } catch (Exception e) {
            System.err.println("计算月度效率失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 核心算法：计算教学效率分数
     * 
     * 公式：效率分数 = (平均成绩权重 × 成绩百分比 + 
     *                  参与度权重 × 参与度 + 
     *                  完成率权重 × 完成率 + 
     *                  及格率权重 × 及格率) / 总权重
     * 
     * 然后归一化到 0-100 的合理区间
     */
    private Double calculateEfficiencyScore(Double avgScore, Double engagement, 
                                           Double completionRate, Double passRate) {
        // 权重配置
        double scoreWeight = 0.35;      // 平均成绩权重 35%
        double engagementWeight = 0.25; // 学生参与度权重 25%
        double completionWeight = 0.20; // 完成率权重 20%
        double passWeight = 0.20;       // 及格率权重 20%
        
        // 归一化处理（将所有指标转换为0-100的百分比）
        double normalizedScore = (avgScore != null ? avgScore : 0.0);  // 假设成绩已经是0-100
        double normalizedEngagement = (engagement != null ? engagement : 0.0);
        double normalizedCompletion = (completionRate != null ? completionRate : 0.0);
        double normalizedPass = (passRate != null ? passRate : 0.0);
        
        // 计算加权平均
        double efficiency = (normalizedScore * scoreWeight +
                           normalizedEngagement * engagementWeight +
                           normalizedCompletion * completionWeight +
                           normalizedPass * passWeight);
        
        // 映射到合理区间（40-95分）
        // 这样可以避免出现极端的0分或100分
        double mappedEfficiency = 40 + (efficiency * 0.55);
        
        // 确保在合理范围内
        mappedEfficiency = Math.max(40, Math.min(95, mappedEfficiency));
        
        // 保留两位小数
        return Math.round(mappedEfficiency * 100.0) / 100.0;
    }
    
    // ==================== 辅助方法 ====================
    
    private List<Long> getAllTeacherIds() {
        // 实现：从数据库获取所有教师ID
        // 这里需要在UserMapper中添加相应方法
        // 暂时返回示例数据
        return List.of(2L, 3L, 4L);
    }
    
    private Double getAvgScoreForDate(Long teacherId, LocalDate date) {
        // 实现：查询该教师在指定日期的平均成绩
        // SELECT AVG(score) FROM student_exam WHERE teacher_id = ? AND DATE(submit_time) = ?
        return null; // 需要实现
    }
    
    private Integer getCourseCountForDate(Long teacherId, LocalDate date) {
        // 实现：查询该教师在指定日期教授的课程数量
        return null; // 需要实现
    }
    
    private Integer getStudentCountForDate(Long teacherId, LocalDate date) {
        // 实现：查询该教师在指定日期的学生数量
        return null; // 需要实现
    }
    
    private Double getTeachingHoursForDate(Long teacherId, LocalDate date) {
        // 实现：查询该教师在指定日期的教学时长
        // SELECT SUM(study_time) / 60 FROM student_progress WHERE teacher_id = ? AND DATE(study_date) = ?
        return null; // 需要实现
    }
    
    private Double calculateStudentEngagement(Long teacherId, LocalDate date) {
        // 实现：计算学生参与度
        // 参与度 = (活跃学生数 / 总学生数) × 100
        Integer activeCount = getActiveStudentCount(teacherId, date);
        Integer totalCount = getStudentCountForDate(teacherId, date);
        
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        
        return (activeCount != null ? activeCount : 0) * 100.0 / totalCount;
    }
    
    private Integer getActiveStudentCount(Long teacherId, LocalDate date) {
        // 实现：查询活跃学生数（当天有学习记录的学生）
        return null; // 需要实现
    }
    
    private Double calculateCompletionRate(Long teacherId, LocalDate date) {
        // 实现：计算课程完成率
        return null; // 需要实现
    }
    
    private Integer getExamCountForDate(Long teacherId, LocalDate date) {
        // 实现：查询考试数量
        return null; // 需要实现
    }
    
    private Double calculatePassRate(Long teacherId, LocalDate date) {
        // 实现：计算及格率
        // 及格率 = (及格人数 / 总人数) × 100
        return null; // 需要实现
    }
    
    private Double calculateExcellentRate(Long teacherId, LocalDate date) {
        // 实现：计算优秀率（90分以上）
        return null; // 需要实现
    }
    
    // 月度数据查询方法（类似日度，但查询时间范围是一个月）
    private Double getAvgScoreForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Integer getCourseCountForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Integer getStudentCountForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Double getTeachingHoursForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Double calculateStudentEngagementForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Integer getActiveStudentCountForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Double calculateCompletionRateForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Integer getExamCountForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Double calculatePassRateForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
    
    private Double calculateExcellentRateForMonth(Long teacherId, LocalDate start, LocalDate end) {
        return null; // 需要实现
    }
}


