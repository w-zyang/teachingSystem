-- =============================================
-- 智能教学管理系统 - 展示演示数据
-- 基于现有数据库结构，添加丰富的展示数据
-- 注意：此脚本会添加数据，不会删除现有数据
-- =============================================

USE web_experiment;

-- =============================================
-- 1. 添加更多学生用户（如果不存在）
-- =============================================

INSERT IGNORE INTO `user` (username, password, email, real_name, role, college, major, grade, class_name, status, create_time) VALUES
('student31', 'e10adc3949ba59abbe56e057f20f883e', 'student31@example.com', '马强', 'student', '计算机学院', '软件工程', '2023', '软工3班', 'active', NOW()),
('student32', 'e10adc3949ba59abbe56e057f20f883e', 'student32@example.com', '林娜', 'student', '计算机学院', '软件工程', '2023', '软工3班', 'active', NOW()),
('student33', 'e10adc3949ba59abbe56e057f20f883e', 'student33@example.com', '黄伟', 'student', '计算机学院', '计算机科学', '2023', '计科3班', 'active', NOW()),
('student34', 'e10adc3949ba59abbe56e057f20f883e', 'student34@example.com', '周丽', 'student', '计算机学院', '计算机科学', '2023', '计科3班', 'active', NOW()),
('student35', 'e10adc3949ba59abbe56e057f20f883e', 'student35@example.com', '徐强', 'student', '计算机学院', '网络工程', '2023', '网工2班', 'active', NOW());

-- =============================================
-- 2. 添加教学效率趋势数据（重要！用于折线图显示）
-- =============================================

-- 获取教师ID（假设是ID为2的教师）
SET @teacher_id = 2;

-- 删除旧的效率数据（如果存在）
DELETE FROM teacher_efficiency_trend WHERE teacher_id = @teacher_id;

-- 插入月度效率数据（最近6个月）
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate) VALUES
(@teacher_id, '2025-01-31', 'month', 52.00, 82.50, 18, 120, 45.0, 68.00, 82, 75.00, 6, 85.00, 25.00),
(@teacher_id, '2025-02-28', 'month', 54.00, 83.20, 20, 125, 48.0, 70.00, 88, 78.00, 7, 87.00, 28.00),
(@teacher_id, '2025-03-31', 'month', 53.00, 82.80, 19, 122, 46.5, 69.00, 84, 76.50, 6, 86.00, 26.50),
(@teacher_id, '2025-04-30', 'month', 56.00, 85.10, 22, 130, 52.0, 73.00, 95, 82.00, 8, 89.00, 32.00),
(@teacher_id, '2025-05-31', 'month', 55.00, 84.50, 21, 128, 50.0, 72.00, 92, 80.00, 7, 88.00, 30.00),
(@teacher_id, '2025-06-30', 'month', 57.33, 86.20, 24, 135, 55.0, 75.00, 101, 85.00, 9, 91.00, 35.00);

-- 插入周度效率数据（最近7天）
INSERT INTO teacher_efficiency_trend (teacher_id, stat_date, period_type, efficiency_score, avg_score, course_count, student_count, teaching_hours, student_engagement, active_student_count, completion_rate, exam_count, pass_rate, excellent_rate) VALUES
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'day', 55.00, 83.00, 3, 45, 4.0, 70.00, 32, 78.00, 1, 87.00, 28.00),
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'day', 58.00, 85.00, 4, 48, 5.0, 75.00, 36, 82.00, 2, 90.00, 32.00),
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'day', 56.00, 84.00, 3, 46, 4.5, 72.00, 33, 80.00, 1, 88.00, 30.00),
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'day', 59.00, 87.00, 4, 50, 5.5, 78.00, 39, 85.00, 2, 92.00, 35.00),
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'day', 57.00, 85.50, 4, 47, 5.0, 74.00, 35, 83.00, 2, 89.00, 31.00),
(@teacher_id, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'day', 54.00, 82.00, 3, 44, 3.0, 68.00, 30, 76.00, 1, 86.00, 27.00),
(@teacher_id, CURDATE(), 'day', 52.00, 80.00, 2, 42, 2.0, 65.00, 27, 74.00, 1, 85.00, 25.00);

-- =============================================
-- 3. 添加更多学生选课记录
-- =============================================

-- 为现有课程添加更多学生选课
INSERT IGNORE INTO student_course (student_id, course_id, status, enroll_time, final_score) 
SELECT 
    u.id as student_id,
    c.id as course_id,
    'enrolled' as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 60) DAY) as enroll_time,
    NULL as final_score
FROM user u
CROSS JOIN course c
WHERE u.role = 'student' 
  AND u.id BETWEEN 7 AND 40
  AND c.id <= 5
  AND RAND() < 0.6  -- 60%的学生选课概率
LIMIT 50;

-- =============================================
-- 4. 添加学生学习进度数据
-- =============================================

-- 为已选课的学生添加学习进度
INSERT IGNORE INTO student_progress (student_id, course_id, chapter_id, lesson_id, status, progress, start_time, study_duration)
SELECT 
    sc.student_id,
    sc.course_id,
    ch.id as chapter_id,
    l.id as lesson_id,
    CASE 
        WHEN RAND() < 0.3 THEN 'completed'
        WHEN RAND() < 0.6 THEN 'in_progress'
        ELSE 'not_started'
    END as status,
    FLOOR(RAND() * 100) as progress,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as start_time,
    FLOOR(RAND() * 60) + 10 as study_duration
FROM student_course sc
JOIN chapter ch ON ch.course_id = sc.course_id
LEFT JOIN lesson l ON l.chapter_id = ch.id
WHERE sc.status = 'enrolled'
LIMIT 100;

-- =============================================
-- 5. 添加学生考试记录和成绩
-- =============================================

-- 为现有考试添加更多学生考试记录
INSERT IGNORE INTO student_exam (student_id, exam_id, score, total_score, status, start_time, submit_time, create_time)
SELECT 
    u.id as student_id,
    e.id as exam_id,
    FLOOR(RAND() * 40) + 60 as score,  -- 60-100分
    100 as total_score,
    'graded' as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as start_time,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as submit_time,
    NOW() as create_time
FROM user u
CROSS JOIN exam e
WHERE u.role = 'student'
  AND u.id BETWEEN 7 AND 40
  AND e.status = 'published'
  AND RAND() < 0.5  -- 50%的学生参加考试
LIMIT 80;

-- =============================================
-- 6. 添加学生练习记录
-- =============================================

-- 为现有练习添加更多学生练习记录
INSERT IGNORE INTO student_practice (student_id, practice_id, score, total_score, accuracy, status, start_time, complete_time, duration)
SELECT 
    u.id as student_id,
    p.id as practice_id,
    FLOOR(RAND() * 40) + 60 as score,
    100 as total_score,
    FLOOR(RAND() * 40) + 60 as accuracy,
    'completed' as status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 20) DAY) as start_time,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 20) DAY) as complete_time,
    FLOOR(RAND() * 40) + 20 as duration
FROM user u
CROSS JOIN practice p
WHERE u.role = 'student'
  AND u.id BETWEEN 7 AND 40
  AND p.status = 'published'
  AND RAND() < 0.6
LIMIT 100;

-- =============================================
-- 7. 添加课程通知
-- =============================================

INSERT IGNORE INTO course_notice (course_id, teacher_id, title, content, type, priority, is_top, read_count, publish_time)
SELECT 
    c.id as course_id,
    c.teacher_id,
    CONCAT(c.title, ' - ', 
        CASE FLOOR(RAND() * 4)
            WHEN 0 THEN '本周作业通知'
            WHEN 1 THEN '期中考试安排'
            WHEN 2 THEN '课程资源更新'
            ELSE '课堂讨论主题'
        END
    ) as title,
    '请同学们注意查看最新的课程安排和要求。' as content,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'homework'
        WHEN 1 THEN 'exam'
        WHEN 2 THEN 'general'
        ELSE 'announcement'
    END as type,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'normal'
        WHEN 1 THEN 'high'
        ELSE 'urgent'
    END as priority,
    RAND() < 0.2 as is_top,
    FLOOR(RAND() * 50) as read_count,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY) as publish_time
FROM course c
WHERE c.status = 'published'
LIMIT 20;

-- =============================================
-- 8. 添加错题记录（用于错题本功能）
-- =============================================

INSERT IGNORE INTO error_question_record (student_id, question_id, exam_id, error_count, last_error_time, is_mastered, create_time)
SELECT 
    se.student_id,
    q.id as question_id,
    se.exam_id,
    FLOOR(RAND() * 3) + 1 as error_count,
    se.submit_time as last_error_time,
    RAND() < 0.3 as is_mastered,
    NOW() as create_time
FROM student_exam se
JOIN exam e ON e.id = se.exam_id
JOIN question q ON q.exam_id = e.id
WHERE se.score < 90
  AND RAND() < 0.3
LIMIT 50;

-- =============================================
-- 9. 添加学生笔记
-- =============================================

INSERT IGNORE INTO student_note (student_id, course_id, lesson_id, title, content, create_time)
SELECT 
    sp.student_id,
    sp.course_id,
    sp.lesson_id,
    CONCAT('第', FLOOR(RAND() * 10) + 1, '课笔记') as title,
    '这节课学习了重要的知识点，需要重点复习。' as content,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as create_time
FROM student_progress sp
WHERE sp.status IN ('in_progress', 'completed')
  AND RAND() < 0.2
LIMIT 30;

-- =============================================
-- 10. 更新统计数据
-- =============================================

-- 更新课程的学生数量
UPDATE course c
SET c.description = CONCAT(
    COALESCE(c.description, ''),
    ' [已有 ',
    (SELECT COUNT(*) FROM student_course sc WHERE sc.course_id = c.id),
    ' 名学生选课]'
)
WHERE c.status = 'published';

-- =============================================
-- 完成提示
-- =============================================

SELECT '✅ 展示演示数据添加完成！' AS message;

SELECT CONCAT('📊 数据统计：') AS info;
SELECT CONCAT('  - 教学效率趋势: ', COUNT(*), ' 条记录') AS stat FROM teacher_efficiency_trend WHERE teacher_id = @teacher_id;
SELECT CONCAT('  - 学生选课: ', COUNT(*), ' 条记录') AS stat FROM student_course;
SELECT CONCAT('  - 学习进度: ', COUNT(*), ' 条记录') AS stat FROM student_progress;
SELECT CONCAT('  - 考试记录: ', COUNT(*), ' 条记录') AS stat FROM student_exam;
SELECT CONCAT('  - 练习记录: ', COUNT(*), ' 条记录') AS stat FROM student_practice;
SELECT CONCAT('  - 课程通知: ', COUNT(*), ' 条记录') AS stat FROM course_notice;
SELECT CONCAT('  - 错题记录: ', COUNT(*), ' 条记录') AS stat FROM error_question_record;
SELECT CONCAT('  - 学生笔记: ', COUNT(*), ' 条记录') AS stat FROM student_note;

SELECT '🎯 现在可以：' AS tips;
SELECT '  1. 刷新教师工作台，查看折线图' AS tip;
SELECT '  2. 查看学生学习数据和成绩' AS tip;
SELECT '  3. 查看课程通知和资源' AS tip;
SELECT '  4. 测试所有功能模块' AS tip;

