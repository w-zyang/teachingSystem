-- =============================================
-- AI智能备课助手数据库表（精简版 - 避免冲突）
-- 创建时间: 2026-01-31
-- =============================================

USE web_experiment;

-- 1. 课件更新记录表（保留）
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

-- 2. 实验指导书生成记录表（修改后）
CREATE TABLE IF NOT EXISTS `experiment_guide_generation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `chapter_name` VARCHAR(200) NOT NULL COMMENT '章节名称',
    `knowledge_points` TEXT COMMENT '知识点列表（JSON数组）',
    `difficulty_level` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
    `experiment_title` VARCHAR(200) COMMENT '实验标题',
    `experiment_objectives` TEXT COMMENT '实验目的（JSON数组）',
    `experiment_environment` TEXT COMMENT '实验环境和工具',
    `experiment_steps` LONGTEXT COMMENT '实验步骤（JSON格式）',
    `experiment_code` LONGTEXT COMMENT '示例代码',
    `expected_results` TEXT COMMENT '预期结果',
    `thinking_questions` TEXT COMMENT '思考题（JSON数组）',
    `reference_answers` TEXT COMMENT '参考答案（JSON数组）',
    `guide_url` VARCHAR(500) COMMENT '指导书下载URL',
    `status` VARCHAR(20) DEFAULT 'generating' COMMENT '状态：generating, completed, failed',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    INDEX `idx_teacher_id` (`teacher_id`)
) COMMENT '实验指导书生成记录表';

-- 3. 作业批阅记录表（保留）
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

-- 插入测试数据
INSERT INTO `courseware_update` (`teacher_id`, `course_id`, `original_file_name`, `update_type`, `update_requirements`, `status`) VALUES
(2, 1, '数据结构课件.pptx', 'case_update', '更新2024-2025年最新的算法应用案例', 'completed'),
(2, 1, '信息安全课件.pptx', 'case_update', '更新最新的网络安全事件案例', 'suggestions_ready');

INSERT INTO `experiment_guide_generation` (`teacher_id`, `course_id`, `chapter_name`, `knowledge_points`, `difficulty_level`, `experiment_title`, `status`) VALUES
(2, 1, '数据结构 - 第三章：栈和队列', '["栈的定义", "栈的应用", "队列的定义", "循环队列"]', 'medium', '栈和队列的实现与应用', 'completed'),
(2, 1, '数据结构 - 第四章：树和二叉树', '["树的基本概念", "二叉树遍历", "二叉搜索树"]', 'hard', '二叉树的遍历算法实现', 'completed');

COMMIT;

