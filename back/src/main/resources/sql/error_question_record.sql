-- 创建错题记录表
CREATE TABLE IF NOT EXISTS `error_question_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` BIGINT(20) NOT NULL COMMENT '学生ID',
  `question_id` VARCHAR(100) NOT NULL COMMENT '题目ID（可以是数字ID或临时ID）',
  `question_type` VARCHAR(20) NOT NULL COMMENT '题目类型：choice, fill, short, coding, essay',
  `question_content` TEXT NOT NULL COMMENT '题目内容',
  `question_options` TEXT COMMENT '题目选项（JSON格式）',
  `user_answer` TEXT COMMENT '学生答案',
  `correct_answer` TEXT COMMENT '正确答案',
  `knowledge_point` VARCHAR(100) COMMENT '知识点',
  `error_type` VARCHAR(50) COMMENT '错误类型',
  `error_reason` TEXT COMMENT '错误原因',
  `source` VARCHAR(20) DEFAULT 'practice' COMMENT '来源：practice, exam',
  `error_count` INT DEFAULT 1 COMMENT '错误次数',
  `error_rate` DOUBLE DEFAULT 100.0 COMMENT '错误率',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_knowledge_point` (`knowledge_point`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题记录表';

