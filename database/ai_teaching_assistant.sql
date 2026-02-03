-- =============================================
-- AI智能备课助手数据库表
-- 创建时间: 2026-01-31
-- =============================================

USE web_experiment;

-- 1. AI生成的PPT记录表
CREATE TABLE IF NOT EXISTS `ai_ppt_generation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_id` BIGINT COMMENT '关联课程ID（可选）',
    `topic` VARCHAR(200) NOT NULL COMMENT 'PPT主题',
    `keywords` TEXT COMMENT '关键词（JSON数组）',
    `slide_count` INT DEFAULT 10 COMMENT '页数',
    `style` VARCHAR(50) DEFAULT 'professional' COMMENT '风格：professional, creative, academic',
    `content` LONGTEXT COMMENT 'AI生成的内容（JSON格式）',
    `ppt_url` VARCHAR(500) COMMENT 'PPT文件下载URL',
    `task_id` VARCHAR(100) COMMENT '讯飞任务ID',
    `status` VARCHAR(20) DEFAULT 'generating' COMMENT '状态：generating, completed, failed',
    `error_message` TEXT COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_status` (`status`)
) COMMENT 'AI生成PPT记录表';

-- 2. 课件更新记录表
CREATE TABLE IF NOT EXISTS `courseware_update` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `original_file_url` VARCHAR(500) COMMENT '原始PPT文件URL',
    `original_file_name` VARCHAR(200) COMMENT '原始文件名',
    `update_type` VARCHAR(50) NOT NULL COMMENT '更新类型：case_update, content_add, knowledge_update',
    `update_requirements` TEXT COMMENT '更新要求描述',
    `ai_suggestions` LONGTEXT COMMENT 'AI生成的更新建议（JSON格式）',
    `updated_file_url` VARCHAR(500) COMMENT '更新后的文件URL',
    `status` VARCHAR(20) DEFAULT 'analyzing' COMMENT '状态：analyzing, suggestions_ready, updating, completed, failed',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`)
) COMMENT '课件更新记录表';

-- 3. 教学资源包生成记录表
CREATE TABLE IF NOT EXISTS `teaching_resource_package` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `chapter_name` VARCHAR(200) NOT NULL COMMENT '章节名称',
    `knowledge_points` TEXT COMMENT '知识点列表（JSON数组）',
    `difficulty_level` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
    `quiz_content` LONGTEXT COMMENT '课堂测验内容（JSON格式）',
    `homework_content` LONGTEXT COMMENT '作业内容（JSON格式）',
    `experiment_content` LONGTEXT COMMENT '实验指导书内容（JSON格式）',
    `summary_ppt` TEXT COMMENT '知识点总结PPT内容',
    `package_url` VARCHAR(500) COMMENT '资源包下载URL（ZIP）',
    `status` VARCHAR(20) DEFAULT 'generating' COMMENT '状态：generating, completed, failed',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`)
) COMMENT '教学资源包生成记录表';

-- 4. 作业批阅记录表
CREATE TABLE IF NOT EXISTS `homework_grading` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `homework_title` VARCHAR(200) NOT NULL COMMENT '作业标题',
    `submission_file_url` VARCHAR(500) COMMENT '学生提交的文件URL',
    `submission_file_name` VARCHAR(200) COMMENT '提交文件名',
    `submission_content` LONGTEXT COMMENT '作业内容文本',
    `ai_score` DECIMAL(5,2) COMMENT 'AI评分',
    `ai_comments` TEXT COMMENT 'AI评语',
    `ai_analysis` LONGTEXT COMMENT 'AI详细分析（JSON格式）',
    `teacher_score` DECIMAL(5,2) COMMENT '教师最终评分',
    `teacher_comments` TEXT COMMENT '教师评语',
    `status` VARCHAR(20) DEFAULT 'submitted' COMMENT '状态：submitted, ai_graded, teacher_reviewed, completed',
    `submit_time` DATETIME COMMENT '提交时间',
    `ai_grade_time` DATETIME COMMENT 'AI批阅时间',
    `teacher_review_time` DATETIME COMMENT '教师审阅时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`student_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_status` (`status`)
) COMMENT '作业批阅记录表';

-- 5. AI教师辅导记录表（知识点答疑）
CREATE TABLE IF NOT EXISTS `ai_teacher_tutoring` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `session_id` VARCHAR(100) NOT NULL COMMENT '会话ID',
    `question` TEXT NOT NULL COMMENT '教师提问',
    `answer` LONGTEXT COMMENT 'AI回答',
    `knowledge_topic` VARCHAR(200) COMMENT '知识主题',
    `helpful` TINYINT(1) COMMENT '是否有帮助（1=是，0=否）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_session_id` (`session_id`)
) COMMENT 'AI教师辅导记录表';

-- 插入初始测试数据
INSERT INTO `ai_ppt_generation` (`teacher_id`, `topic`, `keywords`, `slide_count`, `style`, `status`) VALUES
(2, 'MCP技术详解', '["MCP", "模型上下文协议", "AI集成", "Claude"]', 15, 'professional', 'completed'),
(2, '2024年信息安全事件分析', '["网络安全", "数据泄露", "勒索软件", "案例分析"]', 12, 'academic', 'completed');

INSERT INTO `teaching_resource_package` (`teacher_id`, `course_id`, `chapter_name`, `knowledge_points`, `difficulty_level`, `status`) VALUES
(2, 1, '数据结构 - 第三章：栈和队列', '["栈的定义", "栈的应用", "队列的定义", "循环队列"]', 'medium', 'completed'),
(2, 1, '数据结构 - 第四章：树和二叉树', '["树的基本概念", "二叉树遍历", "二叉搜索树", "平衡树"]', 'hard', 'completed');

COMMIT;

