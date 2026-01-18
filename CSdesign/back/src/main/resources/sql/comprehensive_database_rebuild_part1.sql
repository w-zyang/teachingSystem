-- =============================================
-- 综合数据库重建脚本 - 第一部分
-- 创建完整的学习管理系统数据库，包含大量真实数据
-- 目标：管理员端、教师端、学生端都有真实的数据支撑
-- =============================================

-- 删除并重新创建数据库
DROP DATABASE IF EXISTS web_experiment;
CREATE DATABASE web_experiment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE web_experiment;

-- =============================================
-- 表结构创建
-- =============================================

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：admin, teacher, student',
    `avatar` VARCHAR(200) COMMENT '头像URL',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active, inactive',
    `user_id` VARCHAR(50) COMMENT '学号或工号',
    `college` VARCHAR(100) COMMENT '学院',
    `major` VARCHAR(100) COMMENT '专业',
    `grade` VARCHAR(20) COMMENT '年级（学生）',
    `class_name` VARCHAR(50) COMMENT '班级（学生）',
    `subject` VARCHAR(100) COMMENT '任教专业（教师）',
    `register_date` DATETIME COMMENT '注册时间',
    `last_login` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_username_role` (`username`, `role`)
) COMMENT '用户表';

-- 课程表
CREATE TABLE `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL COMMENT '课程标题',
    `description` TEXT COMMENT '课程描述',
    `subject` VARCHAR(50) COMMENT '学科',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `cover_image` VARCHAR(200) COMMENT '封面图片URL',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft, published, archived',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`)
) COMMENT '课程表';

-- 章节表
CREATE TABLE `chapter` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(100) NOT NULL COMMENT '章节标题',
    `description` TEXT COMMENT '章节描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`)
) COMMENT '章节表';

-- 课时表
CREATE TABLE `lesson` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `title` VARCHAR(100) NOT NULL COMMENT '课时标题',
    `content` TEXT COMMENT '课时内容',
    `video_url` VARCHAR(200) COMMENT '视频URL',
    `duration` INT DEFAULT 0 COMMENT '时长（分钟）',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`)
) COMMENT '课时表';

-- 考试表
CREATE TABLE `exam` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '考试名称',
    `type` VARCHAR(20) NOT NULL COMMENT '考试类型：quiz, midterm, final, homework',
    `duration` INT NOT NULL COMMENT '考试时长（分钟）',
    `total_score` INT NOT NULL COMMENT '总分',
    `course_id` BIGINT NOT NULL COMMENT '关联课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '创建教师ID',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft, published, archived',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`)
) COMMENT '考试表';

-- 题目表
CREATE TABLE `question` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `type` VARCHAR(20) NOT NULL COMMENT '题目类型：single_choice, multiple_choice, true_false, fill_blank, essay',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `answer` TEXT COMMENT '正确答案',
    `analysis` TEXT COMMENT '题目解析',
    `score` INT NOT NULL COMMENT '分值',
    `difficulty` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
    `knowledge_point` VARCHAR(100) COMMENT '知识点',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    FOREIGN KEY (`exam_id`) REFERENCES `exam`(`id`)
) COMMENT '题目表';

-- 题目选项表
CREATE TABLE `question_option` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `key` VARCHAR(10) NOT NULL COMMENT '选项键：A, B, C, D',
    `content` TEXT NOT NULL COMMENT '选项内容',
    `is_correct` BOOLEAN DEFAULT FALSE COMMENT '是否为正确答案',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    FOREIGN KEY (`question_id`) REFERENCES `question`(`id`)
) COMMENT '题目选项表';

-- 学生考试记录表
CREATE TABLE `student_exam` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `score` INT COMMENT '得分',
    `total_score` INT NOT NULL COMMENT '总分',
    `status` VARCHAR(20) DEFAULT 'submitted' COMMENT '状态：submitted, graded',
    `start_time` DATETIME COMMENT '开始时间',
    `submit_time` DATETIME COMMENT '提交时间',
    `grade_time` DATETIME COMMENT '批改时间',
    `teacher_comment` TEXT COMMENT '教师评语',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`exam_id`) REFERENCES `exam`(`id`)
) COMMENT '学生考试记录表';

-- 学生答案表
CREATE TABLE `student_answer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `student_exam_id` BIGINT NOT NULL COMMENT '学生考试记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `answer` TEXT COMMENT '学生答案',
    `is_correct` BOOLEAN COMMENT '是否正确',
    `score` INT COMMENT '得分',
    `teacher_comment` TEXT COMMENT '教师评语',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_exam_id`) REFERENCES `student_exam`(`id`),
    FOREIGN KEY (`question_id`) REFERENCES `question`(`id`)
) COMMENT '学生答案表';

-- 练习表
CREATE TABLE `practice` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL COMMENT '练习标题',
    `description` TEXT COMMENT '练习描述',
    `type` VARCHAR(20) NOT NULL COMMENT '练习类型：algorithm, programming, system_design, database',
    `difficulty` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
    `duration` INT NOT NULL COMMENT '练习时长（分钟）',
    `question_count` INT NOT NULL COMMENT '题目数量',
    `course_id` BIGINT COMMENT '关联课程ID',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft, published, archived',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`)
) COMMENT '练习表';

-- 学生练习记录表
CREATE TABLE `student_practice` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `practice_id` BIGINT NOT NULL COMMENT '练习ID',
    `score` INT COMMENT '得分',
    `total_score` INT NOT NULL COMMENT '总分',
    `accuracy` INT COMMENT '正确率',
    `status` VARCHAR(20) DEFAULT 'in_progress' COMMENT '状态：in_progress, completed',
    `start_time` DATETIME COMMENT '开始时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `duration` INT COMMENT '实际用时（分钟）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`practice_id`) REFERENCES `practice`(`id`)
) COMMENT '学生练习记录表';

-- 学生学习进度表
CREATE TABLE `student_progress` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT COMMENT '章节ID',
    `lesson_id` BIGINT COMMENT '课时ID',
    `status` VARCHAR(20) DEFAULT 'not_started' COMMENT '状态：not_started, in_progress, completed',
    `progress` INT DEFAULT 0 COMMENT '进度百分比',
    `start_time` DATETIME COMMENT '开始学习时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `study_duration` INT DEFAULT 0 COMMENT '学习时长（分钟）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`),
    FOREIGN KEY (`lesson_id`) REFERENCES `lesson`(`id`)
) COMMENT '学生学习进度表';

-- 课程资源表
CREATE TABLE `course_resource` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '上传教师ID',
    `title` VARCHAR(100) NOT NULL COMMENT '资源标题',
    `description` TEXT COMMENT '资源描述',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型：ppt, pdf, doc, video, image',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `status` VARCHAR(20) DEFAULT 'published' COMMENT '状态：draft, published, deleted',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`)
) COMMENT '课程资源表';

-- 学生选课表
CREATE TABLE `student_course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `status` VARCHAR(20) DEFAULT 'enrolled' COMMENT '状态：enrolled, completed, dropped',
    `enroll_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `final_score` INT COMMENT '最终成绩',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`)
) COMMENT '学生选课表';

-- 课程通知表
CREATE TABLE `course_notice` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '发布教师ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` VARCHAR(20) DEFAULT 'general' COMMENT '通知类型：general, exam, homework, announcement',
    `priority` VARCHAR(20) DEFAULT 'normal' COMMENT '优先级：low, normal, high, urgent',
    `is_top` BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    `read_count` INT DEFAULT 0 COMMENT '阅读次数',
    `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`)
) COMMENT '课程通知表';

-- 通知阅读记录表
CREATE TABLE `notice_read_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `notice_id` BIGINT NOT NULL COMMENT '通知ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    FOREIGN KEY (`notice_id`) REFERENCES `course_notice`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_notice_student` (`notice_id`, `student_id`)
) COMMENT '通知阅读记录表';

-- =============================================
-- 用户数据 - 分角色插入大量用户
-- =============================================

-- 管理员账户
INSERT INTO `user` (`username`, `password`, `email`, `real_name`, `role`, `user_id`, `register_date`, `last_login`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@university.edu', '系统管理员', 'admin', 'ADM001', '2024-01-01 08:00:00', NOW(), 'active');

-- 教师账户 (15名教师)
INSERT INTO `user` (`username`, `password`, `email`, `real_name`, `role`, `user_id`, `college`, `subject`, `register_date`, `last_login`, `status`) VALUES
('teacher1', 'e10adc3949ba59abbe56e057f20f883e', 'zhang.prof@university.edu', '张教授', 'teacher', 'T2024001', '计算机科学与技术学院', '数据结构、计算机网络、Linux系统', '2020-09-01 09:00:00', NOW(), 'active'),
('teacher2', 'e10adc3949ba59abbe56e057f20f883e', 'wang.prof@university.edu', '王教授', 'teacher', 'T2024002', '计算机科学与技术学院', 'Java程序设计', '2019-03-15 09:00:00', NOW(), 'active'),
('teacher3', 'e10adc3949ba59abbe56e057f20f883e', 'li.prof@university.edu', '李教授', 'teacher', 'T2024003', '信息工程学院', '数据库系统', '2018-08-20 09:00:00', NOW(), 'active'),
('teacher4', 'e10adc3949ba59abbe56e057f20f883e', 'chen.prof@university.edu', '陈教授', 'teacher', 'T2024004', '软件工程学院', '软件工程', '2021-02-10 09:00:00', NOW(), 'active'),
('teacher5', 'e10adc3949ba59abbe56e057f20f883e', 'zhao.prof@university.edu', '赵教授', 'teacher', 'T2024005', '计算机科学与技术学院', '操作系统', '2017-06-01 09:00:00', NOW(), 'active'),
('teacher6', 'e10adc3949ba59abbe56e057f20f883e', 'liu.prof@university.edu', '刘教授', 'teacher', 'T2024006', '网络工程学院', '计算机网络', '2020-04-12 09:00:00', NOW(), 'active'),
('teacher7', 'e10adc3949ba59abbe56e057f20f883e', 'huang.prof@university.edu', '黄教授', 'teacher', 'T2024007', '人工智能学院', '机器学习', '2019-11-05 09:00:00', NOW(), 'active'),
('teacher8', 'e10adc3949ba59abbe56e057f20f883e', 'wu.prof@university.edu', '吴教授', 'teacher', 'T2024008', '计算机科学与技术学院', 'Web开发', '2022-01-08 09:00:00', NOW(), 'active'),
('teacher9', 'e10adc3949ba59abbe56e057f20f883e', 'xu.prof@university.edu', '徐教授', 'teacher', 'T2024009', '信息安全学院', '网络安全', '2018-09-12 09:00:00', NOW(), 'active'),
('teacher10', 'e10adc3949ba59abbe56e057f20f883e', 'sun.prof@university.edu', '孙教授', 'teacher', 'T2024010', '数据科学学院', '大数据技术', '2020-07-20 09:00:00', NOW(), 'active'),
('teacher11', 'e10adc3949ba59abbe56e057f20f883e', 'ma.prof@university.edu', '马教授', 'teacher', 'T2024011', '软件工程学院', '移动开发', '2021-05-14 09:00:00', NOW(), 'active'),
('teacher12', 'e10adc3949ba59abbe56e057f20f883e', 'zhu.prof@university.edu', '朱教授', 'teacher', 'T2024012', '计算机科学与技术学院', '算法设计', '2019-12-03 09:00:00', NOW(), 'active'),
('teacher13', 'e10adc3949ba59abbe56e057f20f883e', 'guo.prof@university.edu', '郭教授', 'teacher', 'T2024013', '人工智能学院', '深度学习', '2020-10-18 09:00:00', NOW(), 'active'),
('teacher14', 'e10adc3949ba59abbe56e057f20f883e', 'he.prof@university.edu', '何教授', 'teacher', 'T2024014', '网络工程学院', '云计算', '2018-04-25 09:00:00', NOW(), 'active'),
('teacher15', 'e10adc3949ba59abbe56e057f20f883e', 'tang.prof@university.edu', '唐教授', 'teacher', 'T2024015', '信息工程学院', '物联网', '2021-08-16 09:00:00', NOW(), 'active'); 