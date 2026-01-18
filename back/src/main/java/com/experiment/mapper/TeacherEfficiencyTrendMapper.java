package com.experiment.mapper;

import com.experiment.pojo.TeacherEfficiencyTrend;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TeacherEfficiencyTrendMapper {
    
    /**
     * 根据教师ID和时间范围查询效率趋势（月度）
     */
    @Select("SELECT * FROM teacher_efficiency_trend " +
            "WHERE teacher_id = #{teacherId} " +
            "AND period_type = 'month' " +
            "AND stat_date >= #{startDate} " +
            "AND stat_date <= #{endDate} " +
            "ORDER BY stat_date ASC")
    List<TeacherEfficiencyTrend> selectMonthlyTrend(@Param("teacherId") Long teacherId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 根据教师ID和时间范围查询效率趋势（日度）
     */
    @Select("SELECT * FROM teacher_efficiency_trend " +
            "WHERE teacher_id = #{teacherId} " +
            "AND period_type = 'day' " +
            "AND stat_date >= #{startDate} " +
            "AND stat_date <= #{endDate} " +
            "ORDER BY stat_date ASC")
    List<TeacherEfficiencyTrend> selectDailyTrend(@Param("teacherId") Long teacherId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 插入或更新效率趋势数据
     */
    @Insert("INSERT INTO teacher_efficiency_trend " +
            "(teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, " +
            "student_count, teaching_hours, student_engagement, active_student_count, " +
            "completion_rate, exam_count, pass_rate, excellent_rate) " +
            "VALUES (#{teacherId}, #{statDate}, #{periodType}, #{efficiencyScore}, #{avgScore}, " +
            "#{courseCount}, #{studentCount}, #{teachingHours}, #{studentEngagement}, " +
            "#{activeStudentCount}, #{completionRate}, #{examCount}, #{passRate}, #{excellentRate}) " +
            "ON DUPLICATE KEY UPDATE " +
            "efficiency_score = VALUES(efficiency_score), " +
            "avg_score = VALUES(avg_score), " +
            "course_count = VALUES(course_count), " +
            "student_count = VALUES(student_count), " +
            "teaching_hours = VALUES(teaching_hours), " +
            "student_engagement = VALUES(student_engagement), " +
            "active_student_count = VALUES(active_student_count), " +
            "completion_rate = VALUES(completion_rate), " +
            "exam_count = VALUES(exam_count), " +
            "pass_rate = VALUES(pass_rate), " +
            "excellent_rate = VALUES(excellent_rate), " +
            "updated_at = CURRENT_TIMESTAMP")
    int insertOrUpdate(TeacherEfficiencyTrend trend);
    
    /**
     * 获取教师最新的效率数据
     */
    @Select("SELECT * FROM teacher_efficiency_trend " +
            "WHERE teacher_id = #{teacherId} " +
            "AND period_type = #{periodType} " +
            "ORDER BY stat_date DESC " +
            "LIMIT 1")
    TeacherEfficiencyTrend selectLatest(@Param("teacherId") Long teacherId,
                                        @Param("periodType") String periodType);
    
    /**
     * 删除指定日期之前的数据（用于数据清理）
     */
    @Delete("DELETE FROM teacher_efficiency_trend " +
            "WHERE stat_date < #{beforeDate}")
    int deleteBeforeDate(@Param("beforeDate") LocalDate beforeDate);
}


