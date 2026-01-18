-- ============================================
-- 验证数据是否存在的SQL查询
-- ============================================

-- 1. 检查表是否存在
SHOW TABLES LIKE 'teacher_efficiency_trend';

-- 2. 查看表结构
DESC teacher_efficiency_trend;

-- 3. 查看所有数据
SELECT * FROM teacher_efficiency_trend;

-- 4. 查看教师ID=2的数据
SELECT * FROM teacher_efficiency_trend WHERE teacher_id = 2;

-- 5. 按类型统计数据条数
SELECT 
    teacher_id,
    period_type,
    COUNT(*) as count,
    MIN(stat_date) as min_date,
    MAX(stat_date) as max_date
FROM teacher_efficiency_trend 
GROUP BY teacher_id, period_type;

-- 6. 如果上面查询结果为空，说明数据没有插入成功
-- 请执行下面的插入语句：

-- 清空旧数据（如果有）
DELETE FROM teacher_efficiency_trend WHERE teacher_id = 2;

-- 插入月度数据
INSERT INTO teacher_efficiency_trend 
(teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES 
(2, '2024-01-31', 'month', 52.00, 78.50, 18, 120, 45.0, 68.00, 82, 75.00, 6, 85.00, 25.00),
(2, '2024-02-29', 'month', 54.00, 80.20, 20, 125, 48.0, 70.00, 88, 78.00, 7, 87.00, 28.00),
(2, '2024-03-31', 'month', 53.00, 79.80, 19, 122, 46.5, 69.00, 84, 76.50, 6, 86.00, 26.50),
(2, '2024-04-30', 'month', 56.00, 82.50, 22, 130, 52.0, 73.00, 95, 82.00, 8, 89.00, 32.00),
(2, '2024-05-31', 'month', 55.00, 81.30, 21, 128, 50.0, 72.00, 92, 80.00, 7, 88.00, 30.00),
(2, '2024-06-30', 'month', 57.33, 83.80, 24, 135, 55.0, 75.00, 101, 85.00, 9, 91.00, 35.00);

-- 插入日度数据
INSERT INTO teacher_efficiency_trend 
(teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate)
VALUES 
(2, '2024-06-10', 'day', 55.00, 81.00, 3, 45, 4.0, 70.00, 32, 78.00, 1, 87.00, 28.00),
(2, '2024-06-11', 'day', 58.00, 84.50, 4, 48, 5.0, 75.00, 36, 82.00, 2, 90.00, 32.00),
(2, '2024-06-12', 'day', 56.00, 82.30, 3, 46, 4.5, 72.00, 33, 80.00, 1, 88.00, 30.00),
(2, '2024-06-13', 'day', 59.00, 85.20, 4, 50, 5.5, 78.00, 39, 85.00, 2, 92.00, 35.00),
(2, '2024-06-14', 'day', 57.00, 83.50, 4, 47, 5.0, 74.00, 35, 83.00, 2, 89.00, 31.00),
(2, '2024-06-15', 'day', 54.00, 80.80, 3, 44, 3.0, 68.00, 30, 76.00, 1, 86.00, 27.00),
(2, '2024-06-16', 'day', 52.00, 79.50, 2, 42, 2.0, 65.00, 27, 74.00, 1, 85.00, 25.00);

-- 7. 验证插入是否成功
SELECT '=== 验证结果 ===' as info;
SELECT 
    period_type as '类型',
    COUNT(*) as '数据条数',
    MIN(stat_date) as '开始日期',
    MAX(stat_date) as '结束日期'
FROM teacher_efficiency_trend 
WHERE teacher_id = 2
GROUP BY period_type;


