-- =============================================
-- 完整数据库初始化脚本
-- 包含所有表结构和测试数据
-- 创建时间: 2025-01-XX
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除并重新创建数据库
DROP DATABASE IF EXISTS web_experiment;
CREATE DATABASE web_experiment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE web_experiment;

-- =============================================
-- 第一部分：基础表结构
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
-- 第二部分：智能学习功能表
-- =============================================

-- 知识点表
CREATE TABLE `knowledge_point` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '知识点名称',
    `description` TEXT COMMENT '知识点描述',
    `subject` VARCHAR(50) NOT NULL COMMENT '学科领域',
    `difficulty_level` INT DEFAULT 1 COMMENT '难度等级(1-5)',
    `estimated_duration` INT DEFAULT 60 COMMENT '预计学习时长(分钟)',
    `keywords` VARCHAR(200) COMMENT '关键词(逗号分隔)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_subject_difficulty` (`subject`, `difficulty_level`)
) COMMENT '知识点表';

-- 知识点依赖关系表
CREATE TABLE `knowledge_dependency` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `prerequisite_id` BIGINT NOT NULL COMMENT '前置知识点ID',
    `dependent_id` BIGINT NOT NULL COMMENT '依赖知识点ID',
    `dependency_type` VARCHAR(20) DEFAULT 'required' COMMENT '依赖类型:required,recommended,optional',
    `weight` DECIMAL(3,2) DEFAULT 1.00 COMMENT '依赖权重',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`prerequisite_id`) REFERENCES `knowledge_point`(`id`),
    FOREIGN KEY (`dependent_id`) REFERENCES `knowledge_point`(`id`),
    UNIQUE KEY `uk_dependency` (`prerequisite_id`, `dependent_id`)
) COMMENT '知识点依赖关系表';

-- 学习路径模板表
CREATE TABLE `learning_path_template` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路径名称',
    `description` TEXT COMMENT '路径描述',
    `subject` VARCHAR(50) NOT NULL COMMENT '学科领域',
    `target_level` VARCHAR(20) NOT NULL COMMENT '目标水平:beginner,intermediate,advanced',
    `estimated_duration` INT NOT NULL COMMENT '预计完成时长(小时)',
    `difficulty_progression` JSON COMMENT '难度递进配置',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '学习路径模板表';

-- 学习路径模板知识点关联表
CREATE TABLE `path_template_knowledge` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_id` BIGINT NOT NULL COMMENT '路径模板ID',
    `knowledge_point_id` BIGINT NOT NULL COMMENT '知识点ID',
    `sequence_order` INT NOT NULL COMMENT '学习顺序',
    `is_required` BOOLEAN DEFAULT TRUE COMMENT '是否必需',
    `weight` DECIMAL(3,2) DEFAULT 1.00 COMMENT '权重',
    FOREIGN KEY (`template_id`) REFERENCES `learning_path_template`(`id`),
    FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point`(`id`),
    UNIQUE KEY `uk_template_sequence` (`template_id`, `sequence_order`)
) COMMENT '学习路径模板知识点关联表';

-- 个性化学习路径表
CREATE TABLE `personalized_learning_path` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `template_id` BIGINT COMMENT '基础模板ID',
    `name` VARCHAR(100) NOT NULL COMMENT '路径名称',
    `current_step` INT DEFAULT 1 COMMENT '当前学习步骤',
    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成率(%)',
    `start_time` DATETIME COMMENT '开始时间',
    `estimated_completion` DATETIME COMMENT '预计完成时间',
    `actual_completion` DATETIME COMMENT '实际完成时间',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态:active,paused,completed,abandoned',
    `adaptation_config` JSON COMMENT '个性化配置',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`template_id`) REFERENCES `learning_path_template`(`id`)
) COMMENT '个性化学习路径表';

-- 学习路径步骤表
CREATE TABLE `learning_path_step` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `path_id` BIGINT NOT NULL COMMENT '学习路径ID',
    `knowledge_point_id` BIGINT NOT NULL COMMENT '知识点ID',
    `step_number` INT NOT NULL COMMENT '步骤序号',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态:pending,in_progress,completed,skipped',
    `start_time` DATETIME COMMENT '开始时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `study_duration` INT DEFAULT 0 COMMENT '实际学习时长(分钟)',
    `mastery_level` DECIMAL(3,2) DEFAULT 0.00 COMMENT '掌握程度(0-1)',
    `difficulty_rating` INT COMMENT '学生主观难度评分(1-5)',
    `notes` TEXT COMMENT '学习笔记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`path_id`) REFERENCES `personalized_learning_path`(`id`),
    FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point`(`id`),
    UNIQUE KEY `uk_path_step` (`path_id`, `step_number`)
) COMMENT '学习路径步骤表';

-- 学习能力评估表
CREATE TABLE `learning_ability_assessment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `subject` VARCHAR(50) NOT NULL COMMENT '学科领域',
    `learning_speed` DECIMAL(3,2) DEFAULT 1.00 COMMENT '学习速度系数',
    `retention_rate` DECIMAL(3,2) DEFAULT 0.80 COMMENT '知识保持率',
    `difficulty_preference` INT DEFAULT 3 COMMENT '难度偏好(1-5)',
    `learning_style` VARCHAR(20) DEFAULT 'visual' COMMENT '学习风格:visual,auditory,kinesthetic,mixed',
    `concentration_span` INT DEFAULT 45 COMMENT '专注时长(分钟)',
    `optimal_study_time` VARCHAR(20) DEFAULT 'morning' COMMENT '最佳学习时间:morning,afternoon,evening,night',
    `assessment_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评估时间',
    `confidence_score` DECIMAL(3,2) DEFAULT 0.70 COMMENT '自信度评分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_student_subject` (`student_id`, `subject`)
) COMMENT '学习能力评估表';

-- 情绪检测记录表
CREATE TABLE `emotion_detection_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `session_id` VARCHAR(100) NOT NULL COMMENT '学习会话ID',
    `detection_type` VARCHAR(20) NOT NULL COMMENT '检测类型:text,facial,voice,behavioral',
    `emotion_type` VARCHAR(20) NOT NULL COMMENT '情绪类型:happy,sad,frustrated,confused,excited,bored,focused,stressed',
    `confidence` DECIMAL(3,2) NOT NULL COMMENT '置信度(0-1)',
    `intensity` DECIMAL(3,2) NOT NULL COMMENT '情绪强度(0-1)',
    `context` VARCHAR(200) COMMENT '上下文信息',
    `raw_data` JSON COMMENT '原始检测数据',
    `detection_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '检测时间',
    `course_id` BIGINT COMMENT '关联课程ID',
    `lesson_id` BIGINT COMMENT '关联课时ID',
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`lesson_id`) REFERENCES `lesson`(`id`),
    INDEX `idx_student_session` (`student_id`, `session_id`),
    INDEX `idx_detection_time` (`detection_time`)
) COMMENT '情绪检测记录表';

-- 学习状态监控表
CREATE TABLE `learning_status_monitor` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `session_id` VARCHAR(100) NOT NULL COMMENT '学习会话ID',
    `focus_level` DECIMAL(3,2) DEFAULT 0.80 COMMENT '专注度(0-1)',
    `engagement_score` DECIMAL(3,2) DEFAULT 0.70 COMMENT '参与度(0-1)',
    `fatigue_level` DECIMAL(3,2) DEFAULT 0.20 COMMENT '疲劳度(0-1)',
    `learning_efficiency` DECIMAL(3,2) DEFAULT 0.75 COMMENT '学习效率(0-1)',
    `interaction_frequency` INT DEFAULT 0 COMMENT '交互频次',
    `pause_duration` INT DEFAULT 0 COMMENT '暂停时长(秒)',
    `scroll_speed` DECIMAL(5,2) COMMENT '滚动速度(像素/秒)',
    `click_pattern` JSON COMMENT '点击模式数据',
    `typing_pattern` JSON COMMENT '输入模式数据',
    `monitor_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '监控时间',
    `course_id` BIGINT COMMENT '关联课程ID',
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_student_session_monitor` (`student_id`, `session_id`)
) COMMENT '学习状态监控表';

-- 情绪干预记录表
CREATE TABLE `emotion_intervention_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `emotion_record_id` BIGINT NOT NULL COMMENT '情绪检测记录ID',
    `intervention_type` VARCHAR(30) NOT NULL COMMENT '干预类型:encouragement,break_suggestion,difficulty_adjustment,content_recommendation,music_therapy',
    `intervention_content` TEXT NOT NULL COMMENT '干预内容',
    `trigger_threshold` DECIMAL(3,2) COMMENT '触发阈值',
    `effectiveness_score` DECIMAL(3,2) COMMENT '干预效果评分(0-1)',
    `student_feedback` VARCHAR(20) COMMENT '学生反馈:helpful,neutral,unhelpful',
    `intervention_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '干预时间',
    `duration` INT COMMENT '干预持续时间(分钟)',
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`emotion_record_id`) REFERENCES `emotion_detection_record`(`id`),
    INDEX `idx_intervention_type` (`intervention_type`)
) COMMENT '情绪干预记录表';

-- 学习建议配置表
CREATE TABLE `learning_suggestion_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `emotion_type` VARCHAR(20) NOT NULL COMMENT '情绪类型',
    `intensity_range` VARCHAR(10) NOT NULL COMMENT '强度范围:low,medium,high',
    `suggestion_type` VARCHAR(30) NOT NULL COMMENT '建议类型',
    `suggestion_content` TEXT NOT NULL COMMENT '建议内容模板',
    `priority` INT DEFAULT 5 COMMENT '优先级(1-10)',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_emotion_intensity_type` (`emotion_type`, `intensity_range`, `suggestion_type`)
) COMMENT '学习建议配置表';

-- 学习伙伴匹配表
CREATE TABLE `learning_companion_matching` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student1_id` BIGINT NOT NULL COMMENT '学生1 ID',
    `student2_id` BIGINT NOT NULL COMMENT '学生2 ID',
    `matching_score` DECIMAL(3,2) NOT NULL COMMENT '匹配度(0-1)',
    `matching_criteria` JSON COMMENT '匹配标准',
    `common_subjects` VARCHAR(200) COMMENT '共同学科',
    `complementary_strengths` VARCHAR(200) COMMENT '互补优势',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态:pending,accepted,rejected,active,inactive',
    `match_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
    `activation_time` DATETIME COMMENT '激活时间',
    `collaboration_count` INT DEFAULT 0 COMMENT '协作次数',
    `satisfaction_rating` DECIMAL(3,2) COMMENT '满意度评分(0-1)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`student1_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`student2_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_student_pair` (`student1_id`, `student2_id`),
    INDEX `idx_matching_score` (`matching_score` DESC)
) COMMENT '学习伙伴匹配表';

-- 协作学习会话表
CREATE TABLE `collaborative_learning_session` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `session_name` VARCHAR(100) NOT NULL COMMENT '会话名称',
    `session_type` VARCHAR(20) NOT NULL COMMENT '会话类型:study_group,peer_tutoring,project_collaboration,discussion,quiz_competition',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `subject` VARCHAR(50) NOT NULL COMMENT '学科',
    `topic` VARCHAR(100) NOT NULL COMMENT '主题',
    `max_participants` INT DEFAULT 4 COMMENT '最大参与人数',
    `current_participants` INT DEFAULT 1 COMMENT '当前参与人数',
    `session_description` TEXT COMMENT '会话描述',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `estimated_duration` INT NOT NULL COMMENT '预计时长(分钟)',
    `actual_duration` INT COMMENT '实际时长(分钟)',
    `status` VARCHAR(20) DEFAULT 'scheduled' COMMENT '状态:scheduled,active,completed,cancelled',
    `meeting_url` VARCHAR(200) COMMENT '会议链接',
    `shared_documents` JSON COMMENT '共享文档',
    `learning_goals` TEXT COMMENT '学习目标',
    `success_metrics` JSON COMMENT '成功指标',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`creator_id`) REFERENCES `user`(`id`),
    INDEX `idx_session_time` (`start_time`, `end_time`),
    INDEX `idx_subject_topic` (`subject`, `topic`)
) COMMENT '协作学习会话表';

-- 协作学习参与者表
CREATE TABLE `collaborative_session_participant` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `role` VARCHAR(20) DEFAULT 'participant' COMMENT '角色:creator,moderator,participant,observer',
    `join_time` DATETIME COMMENT '加入时间',
    `leave_time` DATETIME COMMENT '离开时间',
    `participation_score` DECIMAL(3,2) DEFAULT 0.00 COMMENT '参与度评分(0-1)',
    `contribution_score` DECIMAL(3,2) DEFAULT 0.00 COMMENT '贡献度评分(0-1)',
    `interaction_count` INT DEFAULT 0 COMMENT '交互次数',
    `message_count` INT DEFAULT 0 COMMENT '消息数量',
    `resource_shared_count` INT DEFAULT 0 COMMENT '分享资源数量',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态:active,inactive,left',
    `peer_rating` DECIMAL(3,2) COMMENT '同伴评分(0-1)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`session_id`) REFERENCES `collaborative_learning_session`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_session_student` (`session_id`, `student_id`)
) COMMENT '协作学习参与者表';

-- 协作学习互动记录表
CREATE TABLE `collaborative_interaction_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `interaction_type` VARCHAR(30) NOT NULL COMMENT '交互类型:message,question,answer,resource_share,screen_share,code_review,vote,reaction',
    `content` TEXT COMMENT '交互内容',
    `target_student_id` BIGINT COMMENT '目标学生ID(私聊或指定对象)',
    `metadata` JSON COMMENT '元数据(文件、链接、代码等)',
    `interaction_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交互时间',
    `response_count` INT DEFAULT 0 COMMENT '回应数量',
    `helpful_votes` INT DEFAULT 0 COMMENT '有用投票数',
    `quality_score` DECIMAL(3,2) COMMENT '质量评分(0-1)',
    FOREIGN KEY (`session_id`) REFERENCES `collaborative_learning_session`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`target_student_id`) REFERENCES `user`(`id`),
    INDEX `idx_session_time` (`session_id`, `interaction_time`),
    INDEX `idx_interaction_type` (`interaction_type`)
) COMMENT '协作学习互动记录表';

-- 学习小组表
CREATE TABLE `study_group` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `group_name` VARCHAR(100) NOT NULL COMMENT '小组名称',
    `group_description` TEXT COMMENT '小组描述',
    `subject` VARCHAR(50) NOT NULL COMMENT '学科',
    `leader_id` BIGINT NOT NULL COMMENT '组长ID',
    `max_members` INT DEFAULT 6 COMMENT '最大成员数',
    `current_members` INT DEFAULT 1 COMMENT '当前成员数',
    `group_type` VARCHAR(20) DEFAULT 'study' COMMENT '小组类型:study,project,competition,practice',
    `privacy_level` VARCHAR(20) DEFAULT 'public' COMMENT '隐私级别:public,private,invite_only',
    `activity_level` VARCHAR(20) DEFAULT 'moderate' COMMENT '活跃度:low,moderate,high',
    `learning_goals` TEXT COMMENT '学习目标',
    `group_rules` TEXT COMMENT '小组规则',
    `meeting_schedule` JSON COMMENT '会议安排',
    `performance_metrics` JSON COMMENT '绩效指标',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态:active,inactive,disbanded',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`leader_id`) REFERENCES `user`(`id`),
    INDEX `idx_subject_type` (`subject`, `group_type`)
) COMMENT '学习小组表';

-- 学习小组成员表
CREATE TABLE `study_group_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `group_id` BIGINT NOT NULL COMMENT '小组ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `role` VARCHAR(20) DEFAULT 'member' COMMENT '角色:leader,co_leader,member',
    `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `contribution_score` DECIMAL(3,2) DEFAULT 0.00 COMMENT '贡献度(0-1)',
    `attendance_rate` DECIMAL(3,2) DEFAULT 1.00 COMMENT '出勤率(0-1)',
    `peer_rating` DECIMAL(3,2) COMMENT '同伴评分(0-1)',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态:active,inactive,left,kicked',
    `leave_time` DATETIME COMMENT '离开时间',
    `leave_reason` VARCHAR(100) COMMENT '离开原因',
    FOREIGN KEY (`group_id`) REFERENCES `study_group`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_group_student` (`group_id`, `student_id`)
) COMMENT '学习小组成员表';

-- =============================================
-- 第三部分：课堂重点整理表
-- =============================================

-- 课堂重点整理表
CREATE TABLE `class_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `title` varchar(200) NOT NULL COMMENT '课堂标题',
  `description` text COMMENT '课堂描述',
  `audio_file_path` varchar(500) COMMENT '录音文件路径',
  `audio_duration` int COMMENT '录音时长（秒）',
  `courseware_file_path` varchar(500) COMMENT '课件文件路径',
  `transcript_text` longtext COMMENT '转录的文本内容',
  `summary_content` longtext COMMENT 'AI生成的重点整理文档',
  `final_content` longtext COMMENT '教师编辑后的最终文档',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，REVIEWING-审核中，PUBLISHED-已发布',
  `keywords` varchar(500) COMMENT '关键词标签',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `publish_time` datetime COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_publish_time` (`publish_time`),
  CONSTRAINT `fk_class_summary_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_class_summary_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课堂重点整理表';

-- =============================================
-- 第四部分：学生笔记和标注表
-- =============================================

-- 学生笔记表
CREATE TABLE `student_note` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `resource_id` BIGINT NOT NULL COMMENT '课程资源ID',
    `title` VARCHAR(100) NOT NULL COMMENT '笔记标题',
    `content` TEXT NOT NULL COMMENT '笔记内容',
    `page_number` INT DEFAULT 1 COMMENT '页码（PDF等分页文档）',
    `position` JSON COMMENT '位置信息（坐标、选择范围等）',
    `tags` VARCHAR(200) COMMENT '标签（用逗号分隔）',
    `color` VARCHAR(20) DEFAULT '#FFD700' COMMENT '笔记颜色',
    `is_public` BOOLEAN DEFAULT FALSE COMMENT '是否公开（供其他学生查看）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`resource_id`) REFERENCES `course_resource`(`id`),
    INDEX `idx_student_resource` (`student_id`, `resource_id`),
    INDEX `idx_resource_public` (`resource_id`, `is_public`)
) COMMENT '学生笔记表';

-- 学生标注表
CREATE TABLE `student_annotation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `resource_id` BIGINT NOT NULL COMMENT '课程资源ID',
    `type` VARCHAR(20) NOT NULL DEFAULT 'highlight' COMMENT '标注类型：highlight, underline, comment, sticky-note',
    `selected_text` TEXT COMMENT '被标注的文本内容',
    `page_number` INT DEFAULT 1 COMMENT '页码',
    `position` JSON COMMENT '位置信息（起始位置、结束位置等）',
    `color` VARCHAR(20) DEFAULT '#FFFF00' COMMENT '标注颜色',
    `comment` TEXT COMMENT '备注说明',
    `importance` INT DEFAULT 3 COMMENT '重要程度（1-5）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`resource_id`) REFERENCES `course_resource`(`id`),
    INDEX `idx_student_resource_type` (`student_id`, `resource_id`, `type`),
    INDEX `idx_resource_page` (`resource_id`, `page_number`),
    INDEX `idx_student_annotation_create_time` (`create_time`),
    INDEX `idx_student_note_create_time` (`create_time`)
) COMMENT '学生标注表';

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 注意：由于数据量较大，基础数据插入请参考以下文件：
-- 1. comprehensive_database_rebuild_part1.sql - 用户数据
-- 2. comprehensive_database_rebuild_part2.sql - 学生数据
-- 3. comprehensive_database_rebuild_part3.sql - 课程、考试、练习数据
-- 4. comprehensive_database_rebuild_part4.sql - 选课、学习进度、资源数据
-- 5. intelligent_features_database.sql - 智能学习功能数据
-- 6. class_summary_table.sql - 课堂重点整理示例数据
-- 
-- 或者直接使用 init_database.bat 脚本自动执行所有SQL文件
-- =============================================
