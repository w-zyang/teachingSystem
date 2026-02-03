-- =============================================
-- 智能教学管理系统 - 演示数据
-- 用于比赛展示的完整演示数据
-- =============================================

USE web_experiment;

-- =============================================
-- 1. 用户数据（30个学生 + 5个教师 + 1个管理员）
-- =============================================

-- 管理员
INSERT INTO `user` (username, password, email, real_name, role, status, create_time) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '系统管理员', 'admin', 'active', NOW());

-- 教师数据
INSERT INTO `user` (username, password, email, real_name, role, subject, status, create_time) VALUES
('teacher1', 'e10adc3949ba59abbe56e057f20f883e', 'teacher1@example.com', '张教授', 'teacher', '数据结构', 'active', NOW()),
('teacher2', 'e10adc3949ba59abbe56e057f20f883e', 'teacher2@example.com', '王教授', 'teacher', 'Java程序设计', 'active', NOW()),
('teacher3', 'e10adc3949ba59abbe56e057f20f883e', 'teacher3@example.com', '李教授', 'teacher', '计算机网络', 'active', NOW()),
('teacher4', 'e10adc3949ba59abbe56e057f20f883e', 'teacher4@example.com', '赵教授', 'teacher', '操作系统', 'active', NOW()),
('teacher5', 'e10adc3949ba59abbe56e057f20f883e', 'teacher5@example.com', '刘教授', 'teacher', '机器学习', 'active', NOW());

-- 学生数据（30个）
INSERT INTO `user` (username, password, email, real_name, role, college, major, grade, class_name, status, create_time) VALUES
('student1', 'e10adc3949ba59abbe56e057f20f883e', 'student1@example.com', '李明', 'student', '计算机学院', '软件工程', '2023', '软工1班', 'active', NOW()),
('student2', 'e10adc3949ba59abbe56e057f20f883e', 'student2@example.com', '王芳', 'student', '计算机学院', '软件工程', '2023', '软工1班', 'active', NOW()),
('student3', 'e10adc3949ba59abbe56e057f20f883e', 'student3@example.com', '张伟', 'student', '计算机学院', '计算机科学', '2023', '计科1班', 'active', NOW()),
('student4', 'e10adc3949ba59abbe56e057f20f883e', 'student4@example.com', '刘洋', 'student', '计算机学院', '计算机科学', '2023', '计科1班', 'active', NOW()),
('student5', 'e10adc3949ba59abbe56e057f20f883e', 'student5@example.com', '陈静', 'student', '计算机学院', '网络工程', '2023', '网工1班', 'active', NOW()),
('student6', 'e10adc3949ba59abbe56e057f20f883e', 'student6@example.com', '赵强', 'student', '计算机学院', '网络工程', '2023', '网工1班', 'active', NOW()),
('student7', 'e10adc3949ba59abbe56e057f20f883e', 'student7@example.com', '孙丽', 'student', '计算机学院', '软件工程', '2023', '软工2班', 'active', NOW()),
('student8', 'e10adc3949ba59abbe56e057f20f883e', 'student8@example.com', '周杰', 'student', '计算机学院', '软件工程', '2023', '软工2班', 'active', NOW()),
('student9', 'e10adc3949ba59abbe56e057f20f883e', 'student9@example.com', '吴敏', 'student', '计算机学院', '计算机科学', '2023', '计科2班', 'active', NOW()),
('student10', 'e10adc3949ba59abbe56e057f20f883e', 'student10@example.com', '郑浩', 'student', '计算机学院', '计算机科学', '2023', '计科2班', 'active', NOW()),
('student11', 'e10adc3949ba59abbe56e057f20f883e', 'student11@example.com', '钱磊', 'student', '计算机学院', '软件工程', '2024', '软工1班', 'active', NOW()),
('student12', 'e10adc3949ba59abbe56e057f20f883e', 'student12@example.com', '孙娜', 'student', '计算机学院', '软件工程', '2024', '软工1班', 'active', NOW()),
('student13', 'e10adc3949ba59abbe56e057f20f883e', 'student13@example.com', '李娟', 'student', '计算机学院', '计算机科学', '2024', '计科1班', 'active', NOW()),
('student14', 'e10adc3949ba59abbe56e057f20f883e', 'student14@example.com', '王刚', 'student', '计算机学院', '计算机科学', '2024', '计科1班', 'active', NOW()),
('student15', 'e10adc3949ba59abbe56e057f20f883e', 'student15@example.com', '张丽', 'student', '计算机学院', '网络工程', '2024', '网工1班', 'active', NOW()),
('student16', 'e10adc3949ba59abbe56e057f20f883e', 'student16@example.com', '刘强', 'student', '计算机学院', '网络工程', '2024', '网工1班', 'active', NOW()),
('student17', 'e10adc3949ba59abbe56e057f20f883e', 'student17@example.com', '陈伟', 'student', '计算机学院', '软件工程', '2024', '软工2班', 'active', NOW()),
('student18', 'e10adc3949ba59abbe56e057f20f883e', 'student18@example.com', '赵敏', 'student', '计算机学院', '软件工程', '2024', '软工2班', 'active', NOW()),
('student19', 'e10adc3949ba59abbe56e057f20f883e', 'student19@example.com', '孙强', 'student', '计算机学院', '计算机科学', '2024', '计科2班', 'active', NOW()),
('student20', 'e10adc3949ba59abbe56e057f20f883e', 'student20@example.com', '周丽', 'student', '计算机学院', '计算机科学', '2024', '计科2班', 'active', NOW()),
('student21', 'e10adc3949ba59abbe56e057f20f883e', 'student21@example.com', '吴刚', 'student', '计算机学院', '软件工程', '2022', '软工1班', 'active', NOW()),
('student22', 'e10adc3949ba59abbe56e057f20f883e', 'student22@example.com', '郑娜', 'student', '计算机学院', '软件工程', '2022', '软工1班', 'active', NOW()),
('student23', 'e10adc3949ba59abbe56e057f20f883e', 'student23@example.com', '钱伟', 'student', '计算机学院', '计算机科学', '2022', '计科1班', 'active', NOW()),
('student24', 'e10adc3949ba59abbe56e057f20f883e', 'student24@example.com', '孙磊', 'student', '计算机学院', '计算机科学', '2022', '计科1班', 'active', NOW()),
('student25', 'e10adc3949ba59abbe56e057f20f883e', 'student25@example.com', '李静', 'student', '计算机学院', '网络工程', '2022', '网工1班', 'active', NOW()),
('student26', 'e10adc3949ba59abbe56e057f20f883e', 'student26@example.com', '王浩', 'student', '计算机学院', '网络工程', '2022', '网工1班', 'active', NOW()),
('student27', 'e10adc3949ba59abbe56e057f20f883e', 'student27@example.com', '张娜', 'student', '计算机学院', '软件工程', '2022', '软工2班', 'active', NOW()),
('student28', 'e10adc3949ba59abbe56e057f20f883e', 'student28@example.com', '刘杰', 'student', '计算机学院', '软件工程', '2022', '软工2班', 'active', NOW()),
('student29', 'e10adc3949ba59abbe56e057f20f883e', 'student29@example.com', '陈敏', 'student', '计算机学院', '计算机科学', '2022', '计科2班', 'active', NOW()),
('student30', 'e10adc3949ba59abbe56e057f20f883e', 'student30@example.com', '赵浩', 'student', '计算机学院', '计算机科学', '2022', '计科2班', 'active', NOW());

-- =============================================
-- 2. 课程数据（8门课程，每门3-4个章节）
-- =============================================

INSERT INTO `course` (title, description, subject, teacher_id, status, create_time) VALUES
('数据结构与算法', '深入学习各种数据结构和算法，包括线性表、树、图、排序、查找等核心内容', '计算机基础', 2, 'published', NOW()),
('Java程序设计', '从零开始学习Java编程，掌握面向对象编程思想和Java核心技术', '编程语言', 3, 'published', NOW()),
('计算机网络', '系统学习计算机网络原理、协议和应用，理解互联网工作机制', '计算机基础', 4, 'published', NOW()),
('操作系统原理', '深入理解操作系统的核心概念，包括进程管理、内存管理、文件系统等', '计算机基础', 5, 'published', NOW()),
('机器学习基础', '入门机器学习，学习常见算法和应用场景', '人工智能', 6, 'published', NOW()),
('数据库系统', '学习关系型数据库原理、SQL语言和数据库设计', '计算机基础', 2, 'published', NOW()),
('Web前端开发', '学习HTML、CSS、JavaScript和Vue框架，开发现代化Web应用', '编程语言', 3, 'published', NOW()),
('Python编程', 'Python语言基础和高级特性，数据分析和Web开发应用', '编程语言', 4, 'published', NOW());

-- 章节数据
INSERT INTO `chapter` (course_id, title, description, sort_order, create_time) VALUES
-- 数据结构与算法
(1, '第一章 线性表', '学习数组、链表等线性数据结构', 1, NOW()),
(1, '第二章 栈和队列', '掌握栈和队列的原理和应用', 2, NOW()),
(1, '第三章 树和二叉树', '理解树形结构和二叉树算法', 3, NOW()),
(1, '第四章 图论基础', '学习图的表示和遍历算法', 4, NOW()),
-- Java程序设计
(2, '第一章 Java基础', 'Java语法基础和面向对象概念', 1, NOW()),
(2, '第二章 面向对象编程', '类、对象、继承、多态等核心概念', 2, NOW()),
(2, '第三章 Java集合框架', 'List、Set、Map等集合的使用', 3, NOW()),
-- 计算机网络
(3, '第一章 网络基础', '计算机网络概述和体系结构', 1, NOW()),
(3, '第二章 传输层协议', 'TCP/UDP协议原理和应用', 2, NOW()),
(3, '第三章 应用层协议', 'HTTP、DNS等应用层协议', 3, NOW()),
-- 操作系统
(4, '第一章 操作系统概述', '操作系统的基本概念和功能', 1, NOW()),
(4, '第二章 进程管理', '进程、线程和并发控制', 2, NOW()),
(4, '第三章 内存管理', '内存分配和虚拟内存', 3, NOW()),
-- 机器学习
(5, '第一章 机器学习入门', '机器学习基本概念和分类', 1, NOW()),
(5, '第二章 监督学习', '回归和分类算法', 2, NOW()),
(5, '第三章 无监督学习', '聚类和降维算法', 3, NOW());

-- 课时数据（每章节2-3个课时）
INSERT INTO `lesson` (chapter_id, title, content, duration, sort_order, create_time) VALUES
-- 数据结构 - 第一章
(1, '1.1 数组的基本操作', '学习数组的创建、访问、插入和删除操作', 45, 1, NOW()),
(1, '1.2 链表的实现', '单链表、双链表的实现和应用', 50, 2, NOW()),
(1, '1.3 线性表的应用', '线性表在实际问题中的应用', 40, 3, NOW()),
-- 数据结构 - 第二章
(2, '2.1 栈的原理和实现', '栈的基本操作和应用场景', 45, 1, NOW()),
(2, '2.2 队列的原理和实现', '队列、循环队列和优先队列', 45, 2, NOW()),
-- Java - 第一章
(5, '1.1 Java开发环境搭建', 'JDK安装和IDE配置', 30, 1, NOW()),
(5, '1.2 Java基本语法', '变量、数据类型、运算符', 50, 2, NOW()),
(5, '1.3 控制流程', '条件语句和循环语句', 45, 3, NOW()),
-- Java - 第二章
(6, '2.1 类和对象', '类的定义和对象的创建', 50, 1, NOW()),
(6, '2.2 继承和多态', '继承机制和多态性', 55, 2, NOW());

-- =============================================
-- 3. 学生选课数据
-- =============================================

-- 每个学生选3-5门课程
INSERT INTO `student_course` (student_id, course_id, status, enroll_time, final_score) VALUES
-- 学生1-10选课
(7, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 60 DAY), NULL),
(7, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 55 DAY), NULL),
(7, 3, 'completed', DATE_SUB(NOW(), INTERVAL 90 DAY), 88),
(8, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 58 DAY), NULL),
(8, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 50 DAY), NULL),
(9, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 62 DAY), NULL),
(9, 3, 'enrolled', DATE_SUB(NOW(), INTERVAL 45 DAY), NULL),
(9, 4, 'completed', DATE_SUB(NOW(), INTERVAL 100 DAY), 92),
(10, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 55 DAY), NULL),
(10, 3, 'enrolled', DATE_SUB(NOW(), INTERVAL 48 DAY), NULL),
(11, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 60 DAY), NULL),
(11, 4, 'enrolled', DATE_SUB(NOW(), INTERVAL 40 DAY), NULL),
(12, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 52 DAY), NULL),
(12, 5, 'enrolled', DATE_SUB(NOW(), INTERVAL 35 DAY), NULL),
(13, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 65 DAY), NULL),
(13, 3, 'enrolled', DATE_SUB(NOW(), INTERVAL 42 DAY), NULL),
(14, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 58 DAY), NULL),
(14, 4, 'enrolled', DATE_SUB(NOW(), INTERVAL 38 DAY), NULL),
(15, 3, 'enrolled', DATE_SUB(NOW(), INTERVAL 50 DAY), NULL),
(15, 5, 'enrolled', DATE_SUB(NOW(), INTERVAL 30 DAY), NULL),
(16, 1, 'enrolled', DATE_SUB(NOW(), INTERVAL 63 DAY), NULL),
(16, 2, 'enrolled', DATE_SUB(NOW(), INTERVAL 54 DAY), NULL);

-- =============================================
-- 4. 学习进度数据
-- =============================================

INSERT INTO `student_progress` (student_id, course_id, chapter_id, lesson_id, status, progress, start_time, study_duration) VALUES
-- 学生1的学习进度
(7, 1, 1, 1, 'completed', 100, DATE_SUB(NOW(), INTERVAL 50 DAY), 45),
(7, 1, 1, 2, 'completed', 100, DATE_SUB(NOW(), INTERVAL 48 DAY), 50),
(7, 1, 1, 3, 'in_progress', 60, DATE_SUB(NOW(), INTERVAL 2 DAY), 25),
(7, 2, 5, 6, 'completed', 100, DATE_SUB(NOW(), INTERVAL 45 DAY), 30),
(7, 2, 5, 7, 'in_progress', 70, DATE_SUB(NOW(), INTERVAL 1 DAY), 35),
-- 学生2的学习进度
(8, 1, 1, 1, 'completed', 100, DATE_SUB(NOW(), INTERVAL 52 DAY), 45),
(8, 1, 1, 2, 'in_progress', 80, DATE_SUB(NOW(), INTERVAL 3 DAY), 40),
(8, 2, 5, 6, 'completed', 100, DATE_SUB(NOW(), INTERVAL 40 DAY), 30);

-- =============================================
-- 5. 考试数据
-- =============================================

INSERT INTO `exam` (name, type, duration, total_score, course_id, teacher_id, status, start_time, end_time, create_time) VALUES
('数据结构期中考试', 'midterm', 120, 100, 1, 2, 'published', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
('Java基础测验', 'quiz', 60, 100, 2, 3, 'published', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_ADD(NOW(), INTERVAL 40 DAY), NOW()),
('计算机网络期末考试', 'final', 150, 100, 3, 4, 'published', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 50 DAY), NOW()),
('操作系统期中考试', 'midterm', 120, 100, 4, 5, 'published', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY), NOW()),
('机器学习作业', 'homework', 180, 100, 5, 6, 'published', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 45 DAY), NOW());

-- =============================================
-- 6. 学生考试记录
-- =============================================

INSERT INTO `student_exam` (student_id, exam_id, score, total_score, status, start_time, submit_time, create_time) VALUES
(7, 1, 85, 100, 'graded', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
(8, 1, 92, 100, 'graded', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
(9, 1, 78, 100, 'graded', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY), NOW()),
(10, 1, 88, 100, 'graded', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY), NOW()),
(7, 2, 90, 100, 'graded', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(8, 2, 87, 100, 'graded', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(11, 2, 95, 100, 'graded', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
(12, 2, 82, 100, 'graded', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), NOW());

-- =============================================
-- 7. 练习数据
-- =============================================

INSERT INTO `practice` (title, description, type, difficulty, duration, question_count, course_id, status, create_time) VALUES
('数组和链表练习', '巩固数组和链表的基础知识', 'algorithm', 'easy', 30, 10, 1, 'published', NOW()),
('栈和队列应用', '练习栈和队列的实际应用', 'algorithm', 'medium', 45, 15, 1, 'published', NOW()),
('Java基础语法练习', 'Java基础语法综合练习', 'programming', 'easy', 40, 12, 2, 'published', NOW()),
('面向对象编程练习', '类和对象的综合应用', 'programming', 'medium', 60, 20, 2, 'published', NOW()),
('网络协议分析', '分析常见网络协议', 'system_design', 'hard', 90, 25, 3, 'published', NOW());

-- =============================================
-- 8. 学生练习记录
-- =============================================

INSERT INTO `student_practice` (student_id, practice_id, score, total_score, accuracy, status, start_time, complete_time, duration) VALUES
(7, 1, 85, 100, 85, 'completed', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 28),
(7, 2, 78, 100, 78, 'completed', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 42),
(8, 1, 92, 100, 92, 'completed', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 25),
(8, 3, 88, 100, 88, 'completed', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 38),
(9, 1, 75, 100, 75, 'completed', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 30),
(10, 2, 90, 100, 90, 'completed', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), 43);

-- =============================================
-- 9. 课程资源数据
-- =============================================

INSERT INTO `course_resource` (course_id, teacher_id, title, description, file_name, file_url, file_type, file_size, download_count, status, create_time) VALUES
(1, 2, '数据结构课件-第一章', '线性表相关课件', '第一章-线性表.pptx', '/resources/course1/chapter1.pptx', 'ppt', 2048000, 45, 'published', NOW()),
(1, 2, '数据结构实验指导书', '实验指导和代码示例', '实验指导书.pdf', '/resources/course1/lab_guide.pdf', 'pdf', 1024000, 38, 'published', NOW()),
(2, 3, 'Java编程基础课件', 'Java语法基础PPT', 'Java基础.pptx', '/resources/course2/java_basic.pptx', 'ppt', 3072000, 52, 'published', NOW()),
(2, 3, 'Java代码示例', '课程配套代码示例', 'code_examples.zip', '/resources/course2/examples.zip', 'doc', 512000, 41, 'published', NOW()),
(3, 4, '计算机网络课件', '网络协议详解', '网络协议.pptx', '/resources/course3/network.pptx', 'ppt', 4096000, 35, 'published', NOW());

-- =============================================
-- 10. 课程通知数据
-- =============================================

INSERT INTO `course_notice` (course_id, teacher_id, title, content, type, priority, is_top, read_count, publish_time) VALUES
(1, 2, '期中考试通知', '本课程期中考试将于下周三进行，请同学们做好准备。考试范围：第1-4章内容。', 'exam', 'high', TRUE, 28, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 2, '作业提交提醒', '第三章作业截止日期为本周五，请尚未提交的同学抓紧时间。', 'homework', 'normal', FALSE, 22, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 3, '课程资源更新', '已上传Java面向对象编程相关课件和代码示例，请同学们及时下载学习。', 'general', 'normal', FALSE, 35, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 4, '实验课安排', '下周实验课将进行网络协议分析实验，请提前预习相关内容。', 'announcement', 'normal', FALSE, 18, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 5, '期末考试安排', '期末考试时间已确定，详见教务系统通知。', 'exam', 'urgent', TRUE, 42, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- =============================================
-- 完成提示
-- =============================================

SELECT '演示数据导入完成！' AS message;
SELECT CONCAT('用户总数: ', COUNT(*), ' (管理员: 1, 教师: 5, 学生: 30)') AS user_stats FROM user;
SELECT CONCAT('课程总数: ', COUNT(*)) AS course_stats FROM course;
SELECT CONCAT('章节总数: ', COUNT(*)) AS chapter_stats FROM chapter;
SELECT CONCAT('课时总数: ', COUNT(*)) AS lesson_stats FROM lesson;
SELECT CONCAT('考试总数: ', COUNT(*)) AS exam_stats FROM exam;
SELECT CONCAT('练习总数: ', COUNT(*)) AS practice_stats FROM practice;
SELECT CONCAT('课程资源: ', COUNT(*)) AS resource_stats FROM course_resource;
SELECT CONCAT('课程通知: ', COUNT(*)) AS notice_stats FROM course_notice;


