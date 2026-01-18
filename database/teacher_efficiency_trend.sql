-- 创建教学效率趋势统计表
CREATE TABLE IF NOT EXISTS teacher_efficiency_trend (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    period_type VARCHAR(10) NOT NULL COMMENT '周期类型：day/week/month',
    
    -- 核心指标
    efficiency_score DECIMAL(5,2) COMMENT '教学效率分数',
    avg_score DECIMAL(5,2) COMMENT '平均成绩',
    course_count INT COMMENT '课程数量',
    student_count INT COMMENT '学生数量',
    teaching_hours DECIMAL(5,1) COMMENT '教学时长（小时）',
    
    -- 参与度指标
    student_engagement DECIMAL(5,2) COMMENT '学生参与度',
    active_student_count INT COMMENT '活跃学生数',
    completion_rate DECIMAL(5,2) COMMENT '课程完成率',
    
    -- 考试指标
    exam_count INT COMMENT '考试数量',
    pass_rate DECIMAL(5,2) COMMENT '及格率',
    excellent_rate DECIMAL(5,2) COMMENT '优秀率',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_teacher_date_period (teacher_id, stat_date, period_type),
    INDEX idx_teacher_date (teacher_id, stat_date),
    INDEX idx_period (period_type, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师教学效率趋势统计表';

-- 为教师ID=2（张教授）插入最近6个月的测试数据
-- 1月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-01-31', 'month', 52.00, 78.50, 18, 120, 45.0, 68.00, 82, 75.00, 6, 85.00, 25.00);

-- 2月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-02-29', 'month', 54.00, 80.20, 20, 125, 48.0, 70.00, 88, 78.00, 7, 87.00, 28.00);

-- 3月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-03-31', 'month', 53.00, 79.80, 19, 122, 46.5, 69.00, 84, 76.50, 6, 86.00, 26.50);

-- 4月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-04-30', 'month', 56.00, 82.50, 22, 130, 52.0, 73.00, 95, 82.00, 8, 89.00, 32.00);

-- 5月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-05-31', 'month', 55.00, 81.30, 21, 128, 50.0, 72.00, 92, 80.00, 7, 88.00, 30.00);

-- 6月数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES (2, '2024-06-30', 'month', 57.33, 83.80, 24, 135, 55.0, 75.00, 101, 85.00, 9, 91.00, 35.00);

-- 插入最近7天的数据（周度数据）
-- 假设今天是2024-06-16，插入最近7天的数据
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES 
(2, '2024-06-10', 'day', 55.00, 81.00, 3, 45, 4.0, 70.00, 32, 78.00, 1, 87.00, 28.00),
(2, '2024-06-11', 'day', 58.00, 84.50, 4, 48, 5.0, 75.00, 36, 82.00, 2, 90.00, 32.00),
(2, '2024-06-12', 'day', 56.00, 82.30, 3, 46, 4.5, 72.00, 33, 80.00, 1, 88.00, 30.00),
(2, '2024-06-13', 'day', 59.00, 85.20, 4, 50, 5.5, 78.00, 39, 85.00, 2, 92.00, 35.00),
(2, '2024-06-14', 'day', 57.00, 83.50, 4, 47, 5.0, 74.00, 35, 83.00, 2, 89.00, 31.00),
(2, '2024-06-15', 'day', 54.00, 80.80, 3, 44, 3.0, 68.00, 30, 76.00, 1, 86.00, 27.00),
(2, '2024-06-16', 'day', 52.00, 79.50, 2, 42, 2.0, 65.00, 27, 74.00, 1, 85.00, 25.00);

-- 为其他教师也插入一些数据（可选）
-- 教师ID=3
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES 
(3, '2024-06-30', 'month', 62.50, 86.20, 20, 110, 48.0, 80.00, 88, 88.00, 8, 93.00, 38.00),
(3, '2024-05-31', 'month', 60.00, 84.50, 19, 108, 46.0, 78.00, 84, 86.00, 7, 91.00, 36.00);

-- 教师ID=4
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES 
(4, '2024-06-30', 'month', 58.80, 84.00, 22, 115, 50.0, 76.00, 87, 84.00, 8, 90.00, 33.00),
(4, '2024-05-31', 'month', 56.50, 82.30, 21, 112, 48.0, 74.00, 83, 82.00, 7, 88.00, 31.00);


