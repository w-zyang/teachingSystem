package com.experiment.config;

import com.experiment.mapper.TeacherEfficiencyTrendMapper;
import com.experiment.pojo.TeacherEfficiencyTrend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 数据库初始化配置
 * 在应用启动时自动创建表和插入测试数据
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private TeacherEfficiencyTrendMapper efficiencyTrendMapper;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // 检查表是否存在
            String checkTableSql = "SHOW TABLES LIKE 'teacher_efficiency_trend'";
            var result = jdbcTemplate.queryForList(checkTableSql);
            
            if (result.isEmpty()) {
                System.out.println("=== 开始初始化教学效率趋势表 ===");
                createTable();
                insertTestData();
                System.out.println("=== 教学效率趋势表初始化完成 ===");
            } else {
                System.out.println("教学效率趋势表已存在，跳过初始化");
                // 检查是否有数据
                String checkDataSql = "SELECT COUNT(*) FROM teacher_efficiency_trend WHERE teacher_id = 2";
                Integer count = jdbcTemplate.queryForObject(checkDataSql, Integer.class);
                if (count == null || count == 0) {
                    System.out.println("检测到表中无数据，开始插入测试数据...");
                    insertTestData();
                    System.out.println("测试数据插入完成");
                }
            }
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTable() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS teacher_efficiency_trend (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                teacher_id BIGINT NOT NULL COMMENT '教师ID',
                stat_date DATE NOT NULL COMMENT '统计日期',
                period_type VARCHAR(10) NOT NULL COMMENT '周期类型：day/week/month',
                
                efficiency_score DECIMAL(5,2) COMMENT '教学效率分数',
                avg_score DECIMAL(5,2) COMMENT '平均成绩',
                course_count INT COMMENT '课程数量',
                student_count INT COMMENT '学生数量',
                teaching_hours DECIMAL(5,1) COMMENT '教学时长（小时）',
                
                student_engagement DECIMAL(5,2) COMMENT '学生参与度',
                active_student_count INT COMMENT '活跃学生数',
                completion_rate DECIMAL(5,2) COMMENT '课程完成率',
                
                exam_count INT COMMENT '考试数量',
                pass_rate DECIMAL(5,2) COMMENT '及格率',
                excellent_rate DECIMAL(5,2) COMMENT '优秀率',
                
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                
                UNIQUE KEY uk_teacher_date_period (teacher_id, stat_date, period_type),
                INDEX idx_teacher_date (teacher_id, stat_date),
                INDEX idx_period (period_type, stat_date)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师教学效率趋势统计表'
            """;
        
        jdbcTemplate.execute(createTableSql);
        System.out.println("✓ 表创建成功: teacher_efficiency_trend");
    }
    
    private void insertTestData() {
        if (efficiencyTrendMapper == null) {
            System.err.println("TeacherEfficiencyTrendMapper 未注入，跳过数据插入");
            return;
        }
        
        // 插入月度数据（最近6个月）
        insertMonthlyData();
        
        // 插入日度数据（最近7天）
        insertDailyData();
        
        System.out.println("✓ 测试数据插入成功");
    }
    
    private void insertMonthlyData() {
        // 月度数据
        Object[][] monthlyData = {
            {2, "2024-01-31", 52.00, 78.50, 18, 120, 45.0, 68.00, 82, 75.00, 6, 85.00, 25.00},
            {2, "2024-02-29", 54.00, 80.20, 20, 125, 48.0, 70.00, 88, 78.00, 7, 87.00, 28.00},
            {2, "2024-03-31", 53.00, 79.80, 19, 122, 46.5, 69.00, 84, 76.50, 6, 86.00, 26.50},
            {2, "2024-04-30", 56.00, 82.50, 22, 130, 52.0, 73.00, 95, 82.00, 8, 89.00, 32.00},
            {2, "2024-05-31", 55.00, 81.30, 21, 128, 50.0, 72.00, 92, 80.00, 7, 88.00, 30.00},
            {2, "2024-06-30", 57.33, 83.80, 24, 135, 55.0, 75.00, 101, 85.00, 9, 91.00, 35.00}
        };
        
        for (Object[] data : monthlyData) {
            TeacherEfficiencyTrend trend = new TeacherEfficiencyTrend();
            trend.setTeacherId(((Integer) data[0]).longValue());
            trend.setStatDate(LocalDate.parse((String) data[1]));
            trend.setPeriodType("month");
            trend.setEfficiencyScore((Double) data[2]);
            trend.setAvgScore((Double) data[3]);
            trend.setCourseCount((Integer) data[4]);
            trend.setStudentCount((Integer) data[5]);
            trend.setTeachingHours((Double) data[6]);
            trend.setStudentEngagement((Double) data[7]);
            trend.setActiveStudentCount((Integer) data[8]);
            trend.setCompletionRate((Double) data[9]);
            trend.setExamCount((Integer) data[10]);
            trend.setPassRate((Double) data[11]);
            trend.setExcellentRate((Double) data[12]);
            
            efficiencyTrendMapper.insertOrUpdate(trend);
        }
        
        System.out.println("  ✓ 插入6个月的月度数据");
    }
    
    private void insertDailyData() {
        // 日度数据（最近7天）
        Object[][] dailyData = {
            {2, "2024-06-10", 55.00, 81.00, 3, 45, 4.0, 70.00, 32, 78.00, 1, 87.00, 28.00},
            {2, "2024-06-11", 58.00, 84.50, 4, 48, 5.0, 75.00, 36, 82.00, 2, 90.00, 32.00},
            {2, "2024-06-12", 56.00, 82.30, 3, 46, 4.5, 72.00, 33, 80.00, 1, 88.00, 30.00},
            {2, "2024-06-13", 59.00, 85.20, 4, 50, 5.5, 78.00, 39, 85.00, 2, 92.00, 35.00},
            {2, "2024-06-14", 57.00, 83.50, 4, 47, 5.0, 74.00, 35, 83.00, 2, 89.00, 31.00},
            {2, "2024-06-15", 54.00, 80.80, 3, 44, 3.0, 68.00, 30, 76.00, 1, 86.00, 27.00},
            {2, "2024-06-16", 52.00, 79.50, 2, 42, 2.0, 65.00, 27, 74.00, 1, 85.00, 25.00}
        };
        
        for (Object[] data : dailyData) {
            TeacherEfficiencyTrend trend = new TeacherEfficiencyTrend();
            trend.setTeacherId(((Integer) data[0]).longValue());
            trend.setStatDate(LocalDate.parse((String) data[1]));
            trend.setPeriodType("day");
            trend.setEfficiencyScore((Double) data[2]);
            trend.setAvgScore((Double) data[3]);
            trend.setCourseCount((Integer) data[4]);
            trend.setStudentCount((Integer) data[5]);
            trend.setTeachingHours((Double) data[6]);
            trend.setStudentEngagement((Double) data[7]);
            trend.setActiveStudentCount((Integer) data[8]);
            trend.setCompletionRate((Double) data[9]);
            trend.setExamCount((Integer) data[10]);
            trend.setPassRate((Double) data[11]);
            trend.setExcellentRate((Double) data[12]);
            
            efficiencyTrendMapper.insertOrUpdate(trend);
        }
        
        System.out.println("  ✓ 插入7天的日度数据");
    }
}


