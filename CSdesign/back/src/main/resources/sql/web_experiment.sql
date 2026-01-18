/*
 Navicat Premium Dump SQL

 Source Server         : db_courseArrangement
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : web_experiment

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 05/07/2025 19:34:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '章节描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `chapter_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '章节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter
-- ----------------------------
INSERT INTO `chapter` VALUES (1, 1, '线性数据结构', '数组、链表、栈、队列等线性数据结构的原理和实现', 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (2, 1, '树形数据结构', '二叉树、平衡树、堆等树形数据结构的概念和应用', 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (3, 1, '图论算法', '图的表示方法、遍历算法、最短路径、最小生成树等', 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (4, 1, '排序算法', '冒泡排序、快速排序、归并排序、堆排序等排序算法', 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (5, 1, '查找算法', '顺序查找、二分查找、哈希查找等查找算法', 5, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (6, 2, 'Java语言基础', 'Java语法、数据类型、控制结构、方法定义等基础知识', 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (7, 2, '面向对象编程', '类与对象、继承、多态、封装等面向对象概念', 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (8, 2, '异常处理', 'Java异常处理机制、自定义异常、异常处理最佳实践', 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (9, 2, '集合框架', 'List、Set、Map等集合类的使用和原理', 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (10, 2, '多线程编程', '线程创建、同步机制、线程池等多线程编程技术', 5, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (11, 3, '关系数据库理论', '关系模型、关系代数、函数依赖、范式理论等', 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (12, 3, 'SQL语言', 'DDL、DML、DCL等SQL语句的语法和应用', 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (13, 3, '数据库设计', 'E-R模型、数据库设计方法、设计规范等', 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (14, 3, '事务处理', 'ACID特性、并发控制、死锁处理等事务管理', 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (15, 3, '数据库优化', '索引设计、查询优化、性能调优等优化技术', 5, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (16, 4, '软件工程概论', '软件工程的基本概念、发展历程、方法学等', 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (17, 4, '需求工程', '需求获取、需求分析、需求规格说明等需求工程', 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (18, 4, '系统设计', '概要设计、详细设计、架构设计等系统设计方法', 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (19, 4, '软件测试', '测试理论、测试方法、测试工具、测试管理等', 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (20, 4, '项目管理', '软件项目计划、进度控制、质量管理、风险管理等', 5, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (21, 5, '操作系统概述', '操作系统的功能、结构、发展历程等基本概念', 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (22, 5, '进程管理', '进程概念、进程调度、进程同步、进程通信等', 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (23, 5, '内存管理', '内存分配、虚拟内存、页面置换算法等内存管理', 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (24, 5, '文件系统', '文件系统结构、文件操作、目录管理等文件系统', 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `chapter` VALUES (25, 5, 'I/O系统', '设备管理、I/O调度、缓冲技术等I/O系统管理', 5, '2025-07-03 09:03:09', '2025-07-03 09:03:09');

-- ----------------------------
-- Table structure for class_summary
-- ----------------------------
DROP TABLE IF EXISTS `class_summary`;
CREATE TABLE `class_summary`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课堂标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课堂描述',
  `audio_file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '录音文件路径',
  `audio_duration` int NULL DEFAULT NULL COMMENT '录音时长（秒）',
  `courseware_file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课件文件路径',
  `transcript_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '转录的文本内容',
  `summary_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI生成的重点整理文档',
  `final_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教师编辑后的最终文档',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，REVIEWING-审核中，PUBLISHED-已发布',
  `keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关键词标签',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE,
  CONSTRAINT `fk_class_summary_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_class_summary_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课堂重点整理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_summary
-- ----------------------------
INSERT INTO `class_summary` VALUES (1, 16, 2, 'Linux基础命令操作', '本次课堂讲解了Linux系统的基础命令，包括文件操作、目录管理、权限设置等核心内容。', NULL, NULL, NULL, '今天我们学习的主题是Linux系统的基础命令操作...', '# Linux基础命令操作课堂总结...', '# Linux基础命令操作课堂总结...', 'PUBLISHED', 'Linux,命令行,文件系统,权限管理,基础操作', 15, '2025-07-03 15:53:09', '2025-07-03 15:34:16', '2025-07-03 15:53:08');

-- ----------------------------
-- Table structure for collaborative_interaction_record
-- ----------------------------
DROP TABLE IF EXISTS `collaborative_interaction_record`;
CREATE TABLE `collaborative_interaction_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `interaction_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交互类型:message,question,answer,resource_share,screen_share,code_review,vote,reaction',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '交互内容',
  `target_student_id` bigint NULL DEFAULT NULL COMMENT '目标学生ID(私聊或指定对象)',
  `metadata` json NULL COMMENT '元数据(文件、链接、代码等)',
  `interaction_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交互时间',
  `response_count` int NULL DEFAULT 0 COMMENT '回应数量',
  `helpful_votes` int NULL DEFAULT 0 COMMENT '有用投票数',
  `quality_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '质量评分(0-1)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `target_student_id`(`target_student_id` ASC) USING BTREE,
  INDEX `idx_session_time`(`session_id` ASC, `interaction_time` ASC) USING BTREE,
  INDEX `idx_interaction_type`(`interaction_type` ASC) USING BTREE,
  CONSTRAINT `collaborative_interaction_record_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `collaborative_learning_session` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `collaborative_interaction_record_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `collaborative_interaction_record_ibfk_3` FOREIGN KEY (`target_student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '协作学习互动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collaborative_interaction_record
-- ----------------------------

-- ----------------------------
-- Table structure for collaborative_learning_session
-- ----------------------------
DROP TABLE IF EXISTS `collaborative_learning_session`;
CREATE TABLE `collaborative_learning_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话名称',
  `session_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话类型:study_group,peer_tutoring,project_collaboration,discussion,quiz_competition',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学科',
  `topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主题',
  `max_participants` int NULL DEFAULT 4 COMMENT '最大参与人数',
  `current_participants` int NULL DEFAULT 1 COMMENT '当前参与人数',
  `session_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '会话描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `estimated_duration` int NOT NULL COMMENT '预计时长(分钟)',
  `actual_duration` int NULL DEFAULT NULL COMMENT '实际时长(分钟)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'scheduled' COMMENT '状态:scheduled,active,completed,cancelled',
  `meeting_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议链接',
  `shared_documents` json NULL COMMENT '共享文档',
  `learning_goals` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习目标',
  `success_metrics` json NULL COMMENT '成功指标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_session_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_subject_topic`(`subject` ASC, `topic` ASC) USING BTREE,
  CONSTRAINT `collaborative_learning_session_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '协作学习会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collaborative_learning_session
-- ----------------------------

-- ----------------------------
-- Table structure for collaborative_session_participant
-- ----------------------------
DROP TABLE IF EXISTS `collaborative_session_participant`;
CREATE TABLE `collaborative_session_participant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'participant' COMMENT '角色:creator,moderator,participant,observer',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `leave_time` datetime NULL DEFAULT NULL COMMENT '离开时间',
  `participation_score` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '参与度评分(0-1)',
  `contribution_score` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '贡献度评分(0-1)',
  `interaction_count` int NULL DEFAULT 0 COMMENT '交互次数',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `resource_shared_count` int NULL DEFAULT 0 COMMENT '分享资源数量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态:active,inactive,left',
  `peer_rating` decimal(3, 2) NULL DEFAULT NULL COMMENT '同伴评分(0-1)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_student`(`session_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `collaborative_session_participant_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `collaborative_learning_session` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `collaborative_session_participant_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '协作学习参与者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collaborative_session_participant
-- ----------------------------

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学科',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `cover_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'draft' COMMENT '状态：draft, published, archived',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '数据结构与算法基础', '计算机科学的核心课程，涵盖线性表、栈、队列、树、图等基础数据结构和排序、查找等算法', '计算机科学', 2, '/images/covers/data-structure.jpg', 'published', '2024-01-15 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (2, 'Java程序设计', '面向对象编程语言Java的全面学习，包括语法基础、面向对象思想、集合框架、异常处理等', 'Java编程', 2, '/images/covers/java.jpg', 'published', '2024-01-16 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (3, '数据库系统原理', '关系数据库的理论基础和实践应用，包括SQL语言、数据库设计、事务处理、并发控制等', '数据库', 3, '/images/covers/database.jpg', 'published', '2024-01-17 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (4, '软件工程', '软件开发的工程化方法，包括需求分析、系统设计、编码实现、测试维护等软件生命周期', '软件工程', 4, '/images/covers/software-eng.jpg', 'published', '2024-01-18 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (5, '操作系统原理', '计算机操作系统的基本概念和原理，包括进程管理、内存管理、文件系统、I/O管理等', '操作系统', 5, '/images/covers/os.jpg', 'published', '2024-01-19 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (6, '计算机网络', '计算机网络的体系结构和协议，包括OSI模型、TCP/IP协议族、网络安全等', '计算机网络', 2, '/images/covers/network.jpg', 'published', '2024-01-20 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (7, '机器学习基础', '人工智能的重要分支，包括监督学习、无监督学习、深度学习的基本理论和算法', '人工智能', 7, '/images/covers/ml.jpg', 'published', '2024-01-21 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (8, 'Web前端开发', '现代Web前端技术，包括HTML5、CSS3、JavaScript、Vue.js框架的应用开发', 'Web开发', 8, '/images/covers/frontend.jpg', 'published', '2024-01-22 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (9, '网络安全概论', '信息安全的基本理论和实践，包括密码学、网络攻防、安全协议、安全管理等', '网络安全', 9, '/images/covers/security.jpg', 'published', '2024-01-23 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (10, '大数据技术与应用', '大数据处理的关键技术，包括Hadoop、Spark、分布式存储、数据挖掘等', '大数据', 10, '/images/covers/bigdata.jpg', 'published', '2024-01-24 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (11, '移动应用开发', '移动端应用开发技术，包括Android、iOS平台的原生开发和跨平台开发', '移动开发', 11, '/images/covers/mobile.jpg', 'published', '2024-01-25 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (12, '算法设计与分析', '高级算法设计技术，包括分治、动态规划、贪心、回溯等算法策略', '算法设计', 12, '/images/covers/algorithm.jpg', 'published', '2024-01-26 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (13, '深度学习', '神经网络和深度学习理论，包括卷积神经网络、循环神经网络、生成对抗网络等', '深度学习', 13, '/images/covers/dl.jpg', 'published', '2024-01-27 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (14, '云计算技术', '云计算的架构和服务模式，包括虚拟化、容器技术、微服务、云原生应用等', '云计算', 14, '/images/covers/cloud.jpg', 'published', '2024-01-28 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (15, '物联网技术', '物联网系统的设计和实现，包括传感器网络、通信协议、数据处理、应用开发等', '物联网', 15, '/images/covers/iot.jpg', 'published', '2024-01-29 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (16, 'Linux系统管理', 'Linux操作系统的使用和管理，包括系统安装、配置、服务管理、脚本编程等', 'Linux系统', 2, '/images/covers/linux.jpg', 'published', '2024-01-30 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (17, 'Python程序设计', 'Python语言的基础和应用，包括语法基础、数据分析、Web开发、人工智能应用等', 'Python编程', 7, '/images/covers/python.jpg', 'published', '2024-02-01 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (18, '微服务架构', '现代软件架构设计，包括微服务拆分、服务治理、API网关、容器编排等', '软件架构', 4, '/images/covers/microservice.jpg', 'published', '2024-02-02 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (19, '区块链技术', '分布式账本技术的原理和应用，包括共识算法、智能合约、加密货币、联盟链等', '区块链', 9, '/images/covers/blockchain.jpg', 'published', '2024-02-03 10:00:00', '2025-07-03 09:03:09');
INSERT INTO `course` VALUES (20, '计算机图形学', '计算机图形学的基本理论和算法，包括几何变换、光照模型、纹理映射、渲染技术等', '图形学', 12, '/images/covers/graphics.jpg', 'published', '2024-02-04 10:00:00', '2025-07-03 09:03:09');

-- ----------------------------
-- Table structure for course_notice
-- ----------------------------
DROP TABLE IF EXISTS `course_notice`;
CREATE TABLE `course_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '发布教师ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'general' COMMENT '通知类型：general, exam, homework, announcement',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'normal' COMMENT '优先级：low, normal, high, urgent',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `read_count` int NULL DEFAULT 0 COMMENT '阅读次数',
  `publish_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `course_notice_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_notice_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_notice
-- ----------------------------

-- ----------------------------
-- Table structure for course_resource
-- ----------------------------
DROP TABLE IF EXISTS `course_resource`;
CREATE TABLE `course_resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '上传教师ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资源描述',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件URL',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型：ppt, pdf, doc, video, image',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'published' COMMENT '状态：draft, published, deleted',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `course_resource_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_resource_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_resource
-- ----------------------------
INSERT INTO `course_resource` VALUES (1, 1, 2, '数据结构课程大纲', '本课程的详细教学大纲和学习要求', '数据结构课程大纲.pdf', '/uploads/course1/course-outline.pdf', 'pdf', 1024000, 45, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (2, 1, 2, '线性表算法实现代码', '数组、链表、栈、队列的C++实现代码', '线性表代码.zip', '/uploads/course1/linear-data-structures.zip', 'zip', 2048000, 32, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (3, 1, 2, '二叉树遍历算法PPT', '二叉树前序、中序、后序遍历的讲解', '二叉树遍历.pptx', '/uploads/course1/binary-tree-traversal.pptx', 'ppt', 5120000, 28, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (4, 1, 2, '排序算法性能比较', '各种排序算法的时间复杂度和性能测试', '排序算法比较.xlsx', '/uploads/course1/sorting-comparison.xlsx', 'excel', 512000, 21, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (5, 1, 2, '图论算法视频教程', '最短路径和最小生成树算法讲解', '图论算法.mp4', '/uploads/course1/graph-algorithms.mp4', 'video', 52428800, 18, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (6, 2, 2, 'Java开发环境配置指南', 'JDK、Eclipse、IntelliJ IDEA的安装配置', 'Java环境配置.pdf', '/uploads/course2/java-setup-guide.pdf', 'pdf', 2048000, 67, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (7, 2, 2, 'Java基础语法练习题', '变量、循环、条件语句等基础练习', 'Java基础练习.docx', '/uploads/course2/java-basic-exercises.docx', 'doc', 1024000, 54, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (8, 2, 2, '面向对象编程实例', '类、继承、多态的实际应用案例', '面向对象实例.zip', '/uploads/course2/oop-examples.zip', 'zip', 3072000, 41, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (9, 2, 2, 'Java集合框架详解', 'ArrayList、HashMap等集合类的使用', 'Java集合框架.pptx', '/uploads/course2/java-collections.pptx', 'ppt', 4096000, 39, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (10, 2, 2, '多线程编程示例', '线程创建、同步、线程池的代码示例', '多线程编程.zip', '/uploads/course2/multithreading-examples.zip', 'zip', 1536000, 25, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (11, 3, 3, '关系数据库理论基础', 'E-R模型、关系代数、范式理论讲解', '数据库理论.pdf', '/uploads/course3/database-theory.pdf', 'pdf', 3072000, 58, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (12, 3, 3, 'SQL语法参考手册', 'DDL、DML、DCL等SQL语句详细说明', 'SQL参考手册.pdf', '/uploads/course3/sql-reference.pdf', 'pdf', 2560000, 72, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (13, 3, 3, '数据库设计实例', '学生管理系统的数据库设计过程', '数据库设计实例.docx', '/uploads/course3/database-design-example.docx', 'doc', 1536000, 43, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (14, 3, 3, 'MySQL实验指导', '数据库安装、配置、基本操作指导', 'MySQL实验指导.pdf', '/uploads/course3/mysql-lab-guide.pdf', 'pdf', 2048000, 65, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (15, 3, 3, '数据库优化技巧', '索引设计、查询优化、性能调优', '数据库优化.pptx', '/uploads/course3/database-optimization.pptx', 'ppt', 6144000, 29, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (16, 4, 4, '软件工程方法论', '瀑布模型、敏捷开发等方法比较', '软件工程方法论.pdf', '/uploads/course4/software-methodologies.pdf', 'pdf', 2560000, 52, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (17, 4, 4, '需求分析模板', '需求获取、分析、规格说明的模板', '需求分析模板.zip', '/uploads/course4/requirements-templates.zip', 'zip', 1024000, 38, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (18, 4, 4, 'UML建模工具教程', 'Visio、StarUML等建模工具使用', 'UML建模教程.pptx', '/uploads/course4/uml-modeling.pptx', 'ppt', 8192000, 31, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (19, 4, 4, '软件测试案例', '黑盒测试、白盒测试的实际案例', '软件测试案例.docx', '/uploads/course4/testing-cases.docx', 'doc', 2048000, 27, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (20, 4, 4, '项目管理工具', 'Project、Jira等项目管理工具介绍', '项目管理工具.pdf', '/uploads/course4/project-management-tools.pdf', 'pdf', 3072000, 24, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (21, 5, 5, '操作系统概论', '操作系统的发展历程和基本概念', '操作系统概论.pdf', '/uploads/course5/os-introduction.pdf', 'pdf', 4096000, 61, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (22, 5, 5, '进程调度算法模拟', '先来先服务、短作业优先等算法模拟', '进程调度模拟.zip', '/uploads/course5/process-scheduling.zip', 'zip', 2048000, 35, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (23, 5, 5, '内存管理实验', '分页、分段、虚拟内存的实验指导', '内存管理实验.pdf', '/uploads/course5/memory-management-lab.pdf', 'pdf', 1536000, 42, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (24, 5, 5, 'Linux系统编程', 'Linux环境下的系统调用和编程', 'Linux系统编程.zip', '/uploads/course5/linux-programming.zip', 'zip', 5120000, 28, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (25, 6, 2, '网络协议栈详解', 'OSI七层模型和TCP/IP协议族', '网络协议栈.pdf', '/uploads/course6/network-protocols.pdf', 'pdf', 3584000, 59, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (26, 6, 2, 'Wireshark抓包分析', '网络数据包的捕获和分析方法', 'Wireshark教程.pdf', '/uploads/course6/wireshark-tutorial.pdf', 'pdf', 2560000, 47, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (27, 6, 2, '网络配置实验', '路由器、交换机的配置实验', '网络配置实验.docx', '/uploads/course6/network-config-lab.docx', 'doc', 1024000, 33, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (28, 6, 2, 'Socket编程示例', 'TCP和UDP的Socket编程实例', 'Socket编程.zip', '/uploads/course6/socket-programming.zip', 'zip', 1536000, 26, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (29, 16, 2, 'Linux系统安装指南', 'Ubuntu、CentOS等Linux发行版的安装配置', 'Linux安装指南.pdf', '/uploads/course16/linux-install-guide.pdf', 'pdf', 3072000, 42, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (30, 16, 2, 'Shell脚本编程教程', 'Bash脚本的语法和实际应用案例', 'Shell脚本教程.pdf', '/uploads/course16/shell-scripting.pdf', 'pdf', 2048000, 39, 'published', '2025-07-03 09:03:16', '2025-07-03 10:12:33');
INSERT INTO `course_resource` VALUES (31, 16, 2, '系统管理实验手册', '用户管理、权限控制、进程管理等实验', '系统管理实验.docx', '/uploads/course16/linux-admin-lab.docx', 'doc', 1536000, 35, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (32, 16, 2, 'Linux常用命令速查表', '文件操作、网络配置、系统监控等命令', 'Linux命令速查.pdf', '/uploads/course16/linux-commands.pdf', 'pdf', 1024000, 51, 'published', '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `course_resource` VALUES (33, 16, 2, 'cp09-样章示例-嵌入式Python开发', '', 'cp09-样章示例-嵌入式Python开发.doc', '/uploads/course16/08aa0780-d7e7-426c-8621-ceec35c233f7.doc', 'doc', 16101528, 1, 'published', '2025-07-03 10:16:53', '2025-07-03 10:16:58');
INSERT INTO `course_resource` VALUES (34, 16, 2, 'cp08-样章示例-TensorFlow Lite', '', 'cp08-样章示例-TensorFlow Lite.docx', '/uploads/course16/2db69251-fa60-4098-92d9-c19d84847cae.docx', 'doc', 1621363, 1, 'published', '2025-07-03 10:23:49', '2025-07-03 10:56:35');
INSERT INTO `course_resource` VALUES (35, 16, 2, '项目8 TensorFlow Lite教程', 'TensorFlow Lite移动端机器学习开发教程，包含花卉识别项目实战', 'tensorflow_lite_tutorial.html', '/test/tensorflow_lite_tutorial.html', 'html', 15360, 0, 'published', '2025-07-05 19:26:00', '2025-07-05 19:26:00');

-- ----------------------------
-- Table structure for emotion_detection_record
-- ----------------------------
DROP TABLE IF EXISTS `emotion_detection_record`;
CREATE TABLE `emotion_detection_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学习会话ID',
  `detection_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测类型:text,facial,voice,behavioral',
  `emotion_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '情绪类型:happy,sad,frustrated,confused,excited,bored,focused,stressed',
  `confidence` decimal(3, 2) NOT NULL COMMENT '置信度(0-1)',
  `intensity` decimal(3, 2) NOT NULL COMMENT '情绪强度(0-1)',
  `context` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上下文信息',
  `raw_data` json NULL COMMENT '原始检测数据',
  `detection_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检测时间',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID',
  `lesson_id` bigint NULL DEFAULT NULL COMMENT '关联课时ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `lesson_id`(`lesson_id` ASC) USING BTREE,
  INDEX `idx_student_session`(`student_id` ASC, `session_id` ASC) USING BTREE,
  INDEX `idx_detection_time`(`detection_time` ASC) USING BTREE,
  CONSTRAINT `emotion_detection_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `emotion_detection_record_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `emotion_detection_record_ibfk_3` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '情绪检测记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emotion_detection_record
-- ----------------------------
INSERT INTO `emotion_detection_record` VALUES (1, 17, 'session_1751645691164', 'behavioral', 'calm', 0.80, 0.50, '自动检测记录', NULL, '2025-07-05 00:14:51', NULL, NULL);

-- ----------------------------
-- Table structure for emotion_intervention_record
-- ----------------------------
DROP TABLE IF EXISTS `emotion_intervention_record`;
CREATE TABLE `emotion_intervention_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `emotion_record_id` bigint NOT NULL COMMENT '情绪检测记录ID',
  `intervention_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '干预类型:encouragement,break_suggestion,difficulty_adjustment,content_recommendation,music_therapy',
  `intervention_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '干预内容',
  `trigger_threshold` decimal(3, 2) NULL DEFAULT NULL COMMENT '触发阈值',
  `effectiveness_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '干预效果评分(0-1)',
  `student_feedback` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学生反馈:helpful,neutral,unhelpful',
  `intervention_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '干预时间',
  `duration` int NULL DEFAULT NULL COMMENT '干预持续时间(分钟)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `emotion_record_id`(`emotion_record_id` ASC) USING BTREE,
  INDEX `idx_intervention_type`(`intervention_type` ASC) USING BTREE,
  CONSTRAINT `emotion_intervention_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `emotion_intervention_record_ibfk_2` FOREIGN KEY (`emotion_record_id`) REFERENCES `emotion_detection_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '情绪干预记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emotion_intervention_record
-- ----------------------------

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试类型：quiz, midterm, final, homework',
  `duration` int NOT NULL COMMENT '考试时长（分钟）',
  `total_score` int NOT NULL COMMENT '总分',
  `course_id` bigint NOT NULL COMMENT '关联课程ID',
  `teacher_id` bigint NOT NULL COMMENT '创建教师ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'draft' COMMENT '状态：draft, published, archived',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `exam_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `exam_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '数据结构期中考试', 'midterm', 120, 100, 1, 2, 'published', '2024-04-15 09:00:00', '2024-04-20 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (2, '数据结构期末考试', 'final', 150, 100, 1, 2, 'published', '2024-06-15 09:00:00', '2024-06-20 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (3, 'Java编程测验', 'quiz', 60, 50, 2, 2, 'published', '2024-04-10 14:00:00', '2024-04-15 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (4, 'Java程序设计期末考试', 'final', 120, 100, 2, 2, 'published', '2024-06-10 09:00:00', '2024-06-15 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (5, '数据库原理考试', 'midterm', 90, 80, 3, 3, 'published', '2024-04-20 10:00:00', '2024-04-25 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (6, '软件工程项目考核', 'homework', 240, 100, 4, 4, 'published', '2024-05-01 00:00:00', '2024-05-30 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (7, '操作系统理论考试', 'midterm', 100, 90, 5, 5, 'published', '2024-04-25 09:00:00', '2024-04-30 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (8, '计算机网络实验考试', 'quiz', 90, 60, 6, 2, 'published', '2024-04-18 14:00:00', '2024-04-23 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (9, '机器学习算法测试', 'quiz', 75, 70, 7, 7, 'published', '2024-04-12 15:00:00', '2024-04-17 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (10, 'Web前端技术考核', 'homework', 180, 80, 8, 8, 'published', '2024-04-08 00:00:00', '2024-04-22 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (11, '网络安全实践考试', 'midterm', 110, 85, 9, 9, 'published', '2024-04-22 10:00:00', '2024-04-27 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (12, '大数据技术应用考试', 'final', 135, 100, 10, 10, 'published', '2024-06-05 09:00:00', '2024-06-10 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (13, '移动开发项目答辩', 'homework', 300, 100, 11, 11, 'published', '2024-05-15 00:00:00', '2024-06-15 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (14, '算法设计期末考试', 'final', 150, 100, 12, 12, 'published', '2024-06-12 09:00:00', '2024-06-17 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (15, '深度学习理论考试', 'midterm', 120, 90, 13, 13, 'published', '2024-04-28 10:00:00', '2024-05-03 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (16, '云计算技术实践', 'quiz', 90, 65, 14, 14, 'published', '2024-04-16 14:00:00', '2024-04-21 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (17, 'Linux系统管理考试', 'midterm', 100, 85, 16, 2, 'published', '2024-04-24 09:00:00', '2024-04-29 23:59:59', '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `exam` VALUES (18, '111', 'choice', 60, 39, 16, 2, 'draft', NULL, NULL, '2025-07-03 10:12:21', '2025-07-03 10:12:21');
INSERT INTO `exam` VALUES (19, '111', 'choice', 60, 39, 16, 2, 'published', NULL, NULL, '2025-07-03 10:12:22', '2025-07-03 10:12:22');
INSERT INTO `exam` VALUES (20, '期中', 'choice', 60, 39, 16, 2, 'draft', NULL, NULL, '2025-07-03 11:09:59', '2025-07-03 11:09:59');
INSERT INTO `exam` VALUES (21, '期中', 'choice', 60, 39, 16, 2, 'published', NULL, NULL, '2025-07-03 11:10:00', '2025-07-03 11:10:00');

-- ----------------------------
-- Table structure for knowledge_dependency
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_dependency`;
CREATE TABLE `knowledge_dependency`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prerequisite_id` bigint NOT NULL COMMENT '前置知识点ID',
  `dependent_id` bigint NOT NULL COMMENT '依赖知识点ID',
  `dependency_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'required' COMMENT '依赖类型:required,recommended,optional',
  `weight` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '依赖权重',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dependency`(`prerequisite_id` ASC, `dependent_id` ASC) USING BTREE,
  INDEX `dependent_id`(`dependent_id` ASC) USING BTREE,
  CONSTRAINT `knowledge_dependency_ibfk_1` FOREIGN KEY (`prerequisite_id`) REFERENCES `knowledge_point` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `knowledge_dependency_ibfk_2` FOREIGN KEY (`dependent_id`) REFERENCES `knowledge_point` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识点依赖关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_dependency
-- ----------------------------
INSERT INTO `knowledge_dependency` VALUES (1, 1, 2, 'required', 1.00, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (2, 2, 3, 'required', 1.00, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (3, 3, 4, 'required', 0.90, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (4, 3, 5, 'recommended', 0.70, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (5, 6, 7, 'required', 1.00, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (6, 7, 8, 'required', 0.80, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (7, 8, 9, 'required', 0.90, '2025-07-04 23:21:15');
INSERT INTO `knowledge_dependency` VALUES (8, 6, 10, 'recommended', 0.60, '2025-07-04 23:21:15');

-- ----------------------------
-- Table structure for knowledge_point
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_point`;
CREATE TABLE `knowledge_point`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识点名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '知识点描述',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学科领域',
  `difficulty_level` int NULL DEFAULT 1 COMMENT '难度等级(1-5)',
  `estimated_duration` int NULL DEFAULT 60 COMMENT '预计学习时长(分钟)',
  `keywords` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关键词(逗号分隔)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subject_difficulty`(`subject` ASC, `difficulty_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_point
-- ----------------------------
INSERT INTO `knowledge_point` VALUES (1, '变量与数据类型', 'Java基础数据类型、变量声明与初始化', 'Java编程', 1, 60, 'Java,变量,数据类型,基础', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (2, '控制结构', 'if语句、循环语句、分支语句', 'Java编程', 2, 90, 'Java,控制结构,循环,条件', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (3, '面向对象基础', '类、对象、封装、继承、多态', 'Java编程', 3, 120, 'Java,面向对象,类,对象', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (4, '集合框架', 'List、Set、Map等集合类的使用', 'Java编程', 4, 150, 'Java,集合,List,Map', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (5, '异常处理', 'try-catch、异常类型、自定义异常', 'Java编程', 3, 90, 'Java,异常,try-catch', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (6, '线性表', '数组、链表的定义与操作', '数据结构', 2, 120, '数据结构,数组,链表,线性表', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (7, '栈与队列', '栈和队列的概念与应用', '数据结构', 3, 100, '数据结构,栈,队列,LIFO,FIFO', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (8, '树与二叉树', '树的定义、二叉树遍历算法', '数据结构', 4, 180, '数据结构,树,二叉树,遍历', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (9, '图论基础', '图的表示方法、搜索算法', '数据结构', 5, 200, '数据结构,图,搜索,算法', '2025-07-04 23:21:15', '2025-07-04 23:21:15');
INSERT INTO `knowledge_point` VALUES (10, '排序算法', '冒泡、选择、插入、快速排序等', '算法', 3, 150, '算法,排序,冒泡,快排', '2025-07-04 23:21:15', '2025-07-04 23:21:15');

-- ----------------------------
-- Table structure for learning_ability_assessment
-- ----------------------------
DROP TABLE IF EXISTS `learning_ability_assessment`;
CREATE TABLE `learning_ability_assessment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学科领域',
  `learning_speed` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '学习速度系数',
  `retention_rate` decimal(3, 2) NULL DEFAULT 0.80 COMMENT '知识保持率',
  `difficulty_preference` int NULL DEFAULT 3 COMMENT '难度偏好(1-5)',
  `learning_style` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'visual' COMMENT '学习风格:visual,auditory,kinesthetic,mixed',
  `concentration_span` int NULL DEFAULT 45 COMMENT '专注时长(分钟)',
  `optimal_study_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'morning' COMMENT '最佳学习时间:morning,afternoon,evening,night',
  `assessment_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评估时间',
  `confidence_score` decimal(3, 2) NULL DEFAULT 0.70 COMMENT '自信度评分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_subject`(`student_id` ASC, `subject` ASC) USING BTREE,
  CONSTRAINT `learning_ability_assessment_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习能力评估表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_ability_assessment
-- ----------------------------
INSERT INTO `learning_ability_assessment` VALUES (1, 2, 'Java编程', 1.20, 0.85, 3, 'visual', 50, 'morning', '2025-07-04 23:21:16', 0.80, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_ability_assessment` VALUES (2, 2, '数据结构', 1.00, 0.80, 4, 'visual', 45, 'morning', '2025-07-04 23:21:16', 0.75, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_ability_assessment` VALUES (3, 3, 'Java编程', 0.90, 0.75, 2, 'auditory', 40, 'afternoon', '2025-07-04 23:21:16', 0.70, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_ability_assessment` VALUES (4, 4, '数据结构', 1.10, 0.88, 4, 'kinesthetic', 55, 'evening', '2025-07-04 23:21:16', 0.85, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_ability_assessment` VALUES (5, 5, 'Java编程', 0.80, 0.70, 2, 'visual', 35, 'night', '2025-07-04 23:21:16', 0.65, '2025-07-04 23:21:16', '2025-07-04 23:21:16');

-- ----------------------------
-- Table structure for learning_companion_matching
-- ----------------------------
DROP TABLE IF EXISTS `learning_companion_matching`;
CREATE TABLE `learning_companion_matching`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student1_id` bigint NOT NULL COMMENT '学生1 ID',
  `student2_id` bigint NOT NULL COMMENT '学生2 ID',
  `matching_score` decimal(3, 2) NOT NULL COMMENT '匹配度(0-1)',
  `matching_criteria` json NULL COMMENT '匹配标准',
  `common_subjects` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '共同学科',
  `complementary_strengths` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '互补优势',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态:pending,accepted,rejected,active,inactive',
  `match_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `activation_time` datetime NULL DEFAULT NULL COMMENT '激活时间',
  `collaboration_count` int NULL DEFAULT 0 COMMENT '协作次数',
  `satisfaction_rating` decimal(3, 2) NULL DEFAULT NULL COMMENT '满意度评分(0-1)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_pair`(`student1_id` ASC, `student2_id` ASC) USING BTREE,
  INDEX `student2_id`(`student2_id` ASC) USING BTREE,
  INDEX `idx_matching_score`(`matching_score` DESC) USING BTREE,
  CONSTRAINT `learning_companion_matching_ibfk_1` FOREIGN KEY (`student1_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `learning_companion_matching_ibfk_2` FOREIGN KEY (`student2_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习伙伴匹配表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_companion_matching
-- ----------------------------
INSERT INTO `learning_companion_matching` VALUES (1, 2, 3, 0.85, NULL, 'Java编程,数据结构', '编程能力强-理论基础好', 'active', '2025-07-04 23:21:20', NULL, 5, 0.88, '2025-07-04 23:21:20', '2025-07-04 23:21:20');
INSERT INTO `learning_companion_matching` VALUES (2, 4, 6, 0.90, NULL, '数据结构,算法', '算法设计-代码实现', 'active', '2025-07-04 23:21:20', NULL, 8, 0.92, '2025-07-04 23:21:20', '2025-07-04 23:21:20');
INSERT INTO `learning_companion_matching` VALUES (3, 5, 7, 0.75, NULL, 'Java编程', '项目经验-学习能力', 'active', '2025-07-04 23:21:20', NULL, 3, 0.80, '2025-07-04 23:21:20', '2025-07-04 23:21:20');
INSERT INTO `learning_companion_matching` VALUES (4, 32, 92, 0.82, NULL, '软件工程,机器学习', '工程实践-理论研究', 'pending', '2025-07-04 23:21:20', NULL, 0, NULL, '2025-07-04 23:21:20', '2025-07-04 23:21:20');

-- ----------------------------
-- Table structure for learning_path_step
-- ----------------------------
DROP TABLE IF EXISTS `learning_path_step`;
CREATE TABLE `learning_path_step`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `path_id` bigint NOT NULL COMMENT '学习路径ID',
  `knowledge_point_id` bigint NOT NULL COMMENT '知识点ID',
  `step_number` int NOT NULL COMMENT '步骤序号',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态:pending,in_progress,completed,skipped',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `study_duration` int NULL DEFAULT 0 COMMENT '实际学习时长(分钟)',
  `mastery_level` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '掌握程度(0-1)',
  `difficulty_rating` int NULL DEFAULT NULL COMMENT '学生主观难度评分(1-5)',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习笔记',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_path_step`(`path_id` ASC, `step_number` ASC) USING BTREE,
  INDEX `knowledge_point_id`(`knowledge_point_id` ASC) USING BTREE,
  CONSTRAINT `learning_path_step_ibfk_1` FOREIGN KEY (`path_id`) REFERENCES `personalized_learning_path` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `learning_path_step_ibfk_2` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习路径步骤表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_path_step
-- ----------------------------

-- ----------------------------
-- Table structure for learning_path_template
-- ----------------------------
DROP TABLE IF EXISTS `learning_path_template`;
CREATE TABLE `learning_path_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路径描述',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学科领域',
  `target_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标水平:beginner,intermediate,advanced',
  `estimated_duration` int NOT NULL COMMENT '预计完成时长(小时)',
  `difficulty_progression` json NULL COMMENT '难度递进配置',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习路径模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_path_template
-- ----------------------------
INSERT INTO `learning_path_template` VALUES (1, 'Java编程入门路径', '从零基础到Java初级程序员的完整学习路径', 'Java编程', 'beginner', 40, '{\"progression\": \"gradual\", \"end_difficulty\": 3, \"start_difficulty\": 1}', 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_path_template` VALUES (2, '数据结构掌握路径', '系统学习各种数据结构的实现和应用', '数据结构', 'intermediate', 60, '{\"progression\": \"steep\", \"end_difficulty\": 5, \"start_difficulty\": 2}', 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_path_template` VALUES (3, '算法进阶路径', '深入学习常用算法的设计与分析', '算法', 'advanced', 80, '{\"progression\": \"moderate\", \"end_difficulty\": 5, \"start_difficulty\": 3}', 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');

-- ----------------------------
-- Table structure for learning_status_monitor
-- ----------------------------
DROP TABLE IF EXISTS `learning_status_monitor`;
CREATE TABLE `learning_status_monitor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学习会话ID',
  `focus_level` decimal(3, 2) NULL DEFAULT 0.80 COMMENT '专注度(0-1)',
  `engagement_score` decimal(3, 2) NULL DEFAULT 0.70 COMMENT '参与度(0-1)',
  `fatigue_level` decimal(3, 2) NULL DEFAULT 0.20 COMMENT '疲劳度(0-1)',
  `learning_efficiency` decimal(3, 2) NULL DEFAULT 0.75 COMMENT '学习效率(0-1)',
  `interaction_frequency` int NULL DEFAULT 0 COMMENT '交互频次',
  `pause_duration` int NULL DEFAULT 0 COMMENT '暂停时长(秒)',
  `scroll_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '滚动速度(像素/秒)',
  `click_pattern` json NULL COMMENT '点击模式数据',
  `typing_pattern` json NULL COMMENT '输入模式数据',
  `monitor_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '监控时间',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_student_session_monitor`(`student_id` ASC, `session_id` ASC) USING BTREE,
  CONSTRAINT `learning_status_monitor_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `learning_status_monitor_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习状态监控表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_status_monitor
-- ----------------------------

-- ----------------------------
-- Table structure for learning_suggestion_config
-- ----------------------------
DROP TABLE IF EXISTS `learning_suggestion_config`;
CREATE TABLE `learning_suggestion_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emotion_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '情绪类型',
  `intensity_range` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '强度范围:low,medium,high',
  `suggestion_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '建议类型',
  `suggestion_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '建议内容模板',
  `priority` int NULL DEFAULT 5 COMMENT '优先级(1-10)',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_emotion_intensity_type`(`emotion_type` ASC, `intensity_range` ASC, `suggestion_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习建议配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_suggestion_config
-- ----------------------------
INSERT INTO `learning_suggestion_config` VALUES (1, 'frustrated', 'high', 'break_suggestion', '检测到您可能感到沮丧，建议休息5-10分钟，做些深呼吸或简单运动。', 9, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (2, 'frustrated', 'medium', 'difficulty_adjustment', '学习内容可能有些困难，为您推荐一些基础资料来巩固前置知识。', 7, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (3, 'bored', 'high', 'content_recommendation', '内容可能不够有趣，为您推荐一些互动练习或实际项目案例。', 8, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (4, 'confused', 'high', 'encouragement', '遇到困难很正常！建议先回顾相关知识点，或寻求同伴帮助。', 8, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (5, 'stressed', 'high', 'music_therapy', '检测到学习压力较大，为您播放一些舒缓的背景音乐。', 6, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (6, 'focused', 'high', 'encouragement', '您的专注度很高！继续保持这种状态，学习效果会很棒。', 5, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `learning_suggestion_config` VALUES (7, 'excited', 'high', 'encouragement', '您的学习热情很高！可以尝试一些挑战性更强的内容。', 6, 1, '2025-07-04 23:21:16', '2025-07-04 23:21:16');

-- ----------------------------
-- Table structure for lesson
-- ----------------------------
DROP TABLE IF EXISTS `lesson`;
CREATE TABLE `lesson`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课时标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课时内容',
  `video_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频URL',
  `duration` int NULL DEFAULT 0 COMMENT '时长（分钟）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chapter_id`(`chapter_id` ASC) USING BTREE,
  CONSTRAINT `lesson_ibfk_1` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课时表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lesson
-- ----------------------------
INSERT INTO `lesson` VALUES (1, 1, '数组与链表', '数组和链表的基本概念、操作实现和性能比较', '/videos/ds/array-list.mp4', 45, 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (2, 1, '栈的应用', '栈的定义、实现和在括号匹配、表达式求值中的应用', '/videos/ds/stack.mp4', 40, 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (3, 1, '队列与循环队列', '队列的概念、实现和在操作系统、网络中的应用', '/videos/ds/queue.mp4', 35, 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (4, 2, '二叉树基础', '二叉树的定义、性质和基本操作实现', '/videos/ds/binary-tree.mp4', 50, 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (5, 2, '二叉搜索树', '二叉搜索树的插入、删除、查找操作', '/videos/ds/bst.mp4', 45, 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (6, 2, '平衡二叉树', 'AVL树和红黑树的基本概念和平衡操作', '/videos/ds/balanced-tree.mp4', 55, 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (7, 2, '堆与优先队列', '堆的性质、建堆过程和在排序中的应用', '/videos/ds/heap.mp4', 40, 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (8, 6, 'Java环境搭建', 'JDK安装配置、IDE使用、第一个Java程序', '/videos/java/setup.mp4', 30, 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (9, 6, '基本数据类型', 'Java的8种基本数据类型和类型转换', '/videos/java/datatype.mp4', 35, 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (10, 6, '控制结构', '条件语句、循环语句、跳转语句的使用', '/videos/java/control.mp4', 40, 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (11, 6, '方法与递归', '方法定义、参数传递、递归算法实现', '/videos/java/method.mp4', 45, 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (12, 7, '类与对象', '类的定义、对象的创建和使用、构造方法', '/videos/java/class-object.mp4', 50, 1, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (13, 7, '继承与多态', '继承关系、方法重写、多态的实现机制', '/videos/java/inherit.mp4', 55, 2, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (14, 7, '抽象类与接口', '抽象类和接口的定义、区别和使用场景', '/videos/java/abstract.mp4', 45, 3, '2025-07-03 09:03:09', '2025-07-03 09:03:09');
INSERT INTO `lesson` VALUES (15, 7, '内部类', '成员内部类、局部内部类、匿名内部类的应用', '/videos/java/inner-class.mp4', 40, 4, '2025-07-03 09:03:09', '2025-07-03 09:03:09');

-- ----------------------------
-- Table structure for notice_read_record
-- ----------------------------
DROP TABLE IF EXISTS `notice_read_record`;
CREATE TABLE `notice_read_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notice_student`(`notice_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `notice_read_record_ibfk_1` FOREIGN KEY (`notice_id`) REFERENCES `course_notice` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `notice_read_record_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_read_record
-- ----------------------------

-- ----------------------------
-- Table structure for path_template_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `path_template_knowledge`;
CREATE TABLE `path_template_knowledge`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '路径模板ID',
  `knowledge_point_id` bigint NOT NULL COMMENT '知识点ID',
  `sequence_order` int NOT NULL COMMENT '学习顺序',
  `is_required` tinyint(1) NULL DEFAULT 1 COMMENT '是否必需',
  `weight` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '权重',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_sequence`(`template_id` ASC, `sequence_order` ASC) USING BTREE,
  INDEX `knowledge_point_id`(`knowledge_point_id` ASC) USING BTREE,
  CONSTRAINT `path_template_knowledge_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `learning_path_template` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `path_template_knowledge_ibfk_2` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习路径模板知识点关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of path_template_knowledge
-- ----------------------------
INSERT INTO `path_template_knowledge` VALUES (1, 1, 1, 1, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (2, 1, 2, 2, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (3, 1, 3, 3, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (4, 1, 4, 4, 1, 0.90);
INSERT INTO `path_template_knowledge` VALUES (5, 1, 5, 5, 0, 0.70);
INSERT INTO `path_template_knowledge` VALUES (6, 2, 6, 1, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (7, 2, 7, 2, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (8, 2, 8, 3, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (9, 2, 9, 4, 1, 0.90);
INSERT INTO `path_template_knowledge` VALUES (10, 3, 10, 1, 1, 1.00);
INSERT INTO `path_template_knowledge` VALUES (11, 3, 8, 2, 1, 0.80);
INSERT INTO `path_template_knowledge` VALUES (12, 3, 9, 3, 1, 0.90);

-- ----------------------------
-- Table structure for personalized_learning_path
-- ----------------------------
DROP TABLE IF EXISTS `personalized_learning_path`;
CREATE TABLE `personalized_learning_path`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '基础模板ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径名称',
  `current_step` int NULL DEFAULT 1 COMMENT '当前学习步骤',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率(%)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `estimated_completion` datetime NULL DEFAULT NULL COMMENT '预计完成时间',
  `actual_completion` datetime NULL DEFAULT NULL COMMENT '实际完成时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态:active,paused,completed,abandoned',
  `adaptation_config` json NULL COMMENT '个性化配置',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `template_id`(`template_id` ASC) USING BTREE,
  CONSTRAINT `personalized_learning_path_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `personalized_learning_path_ibfk_2` FOREIGN KEY (`template_id`) REFERENCES `learning_path_template` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '个性化学习路径表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of personalized_learning_path
-- ----------------------------
INSERT INTO `personalized_learning_path` VALUES (1, 17, 1, 'Java编程个性化学习路径', 1, 0.00, '2025-07-05 00:10:24', NULL, NULL, 'active', NULL, '2025-07-05 00:10:23', '2025-07-05 00:10:23');
INSERT INTO `personalized_learning_path` VALUES (2, 17, 1, 'Java编程个性化学习路径', 1, 0.00, '2025-07-05 00:15:01', NULL, NULL, 'active', NULL, '2025-07-05 00:15:00', '2025-07-05 00:15:00');

-- ----------------------------
-- Table structure for practice
-- ----------------------------
DROP TABLE IF EXISTS `practice`;
CREATE TABLE `practice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '练习标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '练习描述',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '练习类型：algorithm, programming, system_design, database',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
  `duration` int NOT NULL COMMENT '练习时长（分钟）',
  `question_count` int NOT NULL COMMENT '题目数量',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'draft' COMMENT '状态：draft, published, archived',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `practice_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '练习表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of practice
-- ----------------------------
INSERT INTO `practice` VALUES (1, '线性表基础练习', '数组和链表的基本操作练习题', 'algorithm', 'easy', 30, 10, 1, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (2, '栈和队列应用', '栈和队列在实际问题中的应用练习', 'algorithm', 'medium', 45, 8, 1, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (3, 'Java语法基础练习', 'Java基本语法和面向对象概念练习', 'programming', 'easy', 25, 12, 2, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (4, 'Java集合框架练习', 'Java集合类的使用和应用练习', 'programming', 'medium', 40, 10, 2, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (5, 'SQL查询练习', '基本SQL查询语句的编写练习', 'database', 'easy', 35, 15, 3, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (6, '数据库设计练习', 'E-R图设计和数据库建模练习', 'database', 'hard', 60, 6, 3, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (7, '软件需求分析', '软件需求获取和分析的案例练习', 'system_design', 'medium', 50, 8, 4, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (8, '系统架构设计', '大型系统的架构设计练习', 'system_design', 'hard', 90, 5, 4, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (9, '进程调度算法', '操作系统进程调度算法的实现练习', 'algorithm', 'hard', 70, 6, 5, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');
INSERT INTO `practice` VALUES (10, '网络协议分析', 'TCP/IP协议栈的理解和应用练习', 'algorithm', 'medium', 55, 8, 6, 'published', '2025-07-03 09:03:10', '2025-07-03 09:03:10');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型：single_choice, multiple_choice, true_false, fill_blank, essay',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题目解析',
  `score` int NOT NULL COMMENT '分值',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'medium' COMMENT '难度：easy, medium, hard',
  `knowledge_point` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '知识点',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `exam_id`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 'single_choice', '以下哪种数据结构适合实现函数调用的管理？\nA. 队列\nB. 栈\nC. 链表\nD. 数组', 'B', '函数调用遵循\"后进先出\"的原则，因此栈是最适合的数据结构。当函数被调用时，其信息被压入栈中；当函数返回时，其信息从栈中弹出。', 10, 'medium', '栈的应用', 1);
INSERT INTO `question` VALUES (2, 1, 'single_choice', '在一个有n个节点的完全二叉树中，叶子节点的个数是？\nA. n/2\nB. ?n/2?\nC. ?n/2?\nD. n-1', 'C', '在完全二叉树中，叶子节点的个数等于?n/2?。这是因为除了最后一层外，其他层都是满的，最后一层的节点数决定了叶子节点的总数。', 10, 'hard', '二叉树性质', 2);
INSERT INTO `question` VALUES (3, 1, 'multiple_choice', '关于链表的描述，正确的是？\nA. 链表的插入和删除操作时间复杂度为O(1)\nB. 链表不支持随机访问\nC. 链表需要额外的存储空间存储指针\nD. 链表的空间是连续分配的', 'A,B,C', '链表的优点是插入删除快（O(1)），缺点是不支持随机访问、需要额外指针空间、空间不连续。选项D错误，链表的存储空间是不连续的。', 15, 'medium', '链表特性', 3);
INSERT INTO `question` VALUES (4, 1, 'true_false', '快速排序的平均时间复杂度是O(n log n)，最坏时间复杂度是O(n?)。', 'true', '快速排序在平均情况下表现良好，时间复杂度为O(n log n)，但在最坏情况下（如已排序的数组），时间复杂度退化为O(n?)。', 10, 'medium', '排序算法复杂度', 4);
INSERT INTO `question` VALUES (5, 1, 'fill_blank', '在深度优先搜索(DFS)中，通常使用____数据结构来实现，而在广度优先搜索(BFS)中，通常使用____数据结构来实现。', '栈;队列', 'DFS使用栈实现（递归本质上也是栈），遵循深度优先的原则；BFS使用队列实现，遵循广度优先的原则，逐层访问。', 15, 'medium', '图的遍历', 5);
INSERT INTO `question` VALUES (6, 3, 'single_choice', 'Java中，以下哪个关键字用于表示一个类不能被继承？\nA. static\nB. final\nC. abstract\nD. private', 'B', 'final关键字修饰类时表示该类不能被继承，如String类。static用于静态成员，abstract用于抽象类，private用于访问控制。', 5, 'easy', 'Java关键字', 1);
INSERT INTO `question` VALUES (7, 3, 'single_choice', 'Java中的多态性主要体现在？\nA. 方法重载\nB. 方法重写\nC. 接口实现\nD. 以上都是', 'D', 'Java的多态性体现在多个方面：方法重载（编译时多态）、方法重写（运行时多态）、接口实现等，这些都是多态的表现形式。', 8, 'medium', 'Java多态', 2);
INSERT INTO `question` VALUES (8, 3, 'multiple_choice', '关于Java异常处理，正确的说法是？\nA. try块后必须跟catch块\nB. finally块中的代码一定会执行\nC. 可以有多个catch块\nD. Exception是所有异常的父类', 'C,D', 'Java异常处理中，try后可以只跟finally而不跟catch；finally块在某些极端情况下可能不执行；可以有多个catch块处理不同异常；Exception确实是大部分异常的父类。', 12, 'medium', 'Java异常处理', 3);
INSERT INTO `question` VALUES (9, 3, 'true_false', 'Java中的String对象是可变的。', 'false', 'Java中的String对象是不可变的（immutable）。当对String进行修改操作时，实际上是创建了新的String对象，原对象保持不变。', 5, 'easy', 'String特性', 4);
INSERT INTO `question` VALUES (10, 3, 'essay', '请简述Java中ArrayList和LinkedList的区别，并说明在什么场景下使用哪种更合适。', '', 'ArrayList基于数组实现，支持随机访问，查找快O(1)但插入删除慢O(n)；LinkedList基于链表实现，不支持随机访问，查找慢O(n)但插入删除快O(1)。频繁查找用ArrayList，频繁增删用LinkedList。', 20, 'hard', '集合框架', 5);
INSERT INTO `question` VALUES (11, 18, 'choice', '以下哪种操作可以改变张量的形状？', 'A', 'reshape操作可以重新定义张量的维度，其他选项用于不同用途。', 2, '简单', '', 1);
INSERT INTO `question` VALUES (12, 18, 'multiple', '下列哪些因素可能影响模型训练的速度？', 'A,B,C,D', '所有列出的因素都会影响模型训练的速度。', 4, '中等', '', 2);
INSERT INTO `question` VALUES (13, 18, 'fill', '在操作系统中，进程通过____机制共享内存进行通信。', '共享内存', '共享内存是进程间通信的一种高效方式。', 3, '中等', '', 3);
INSERT INTO `question` VALUES (14, 18, 'short', '解释什么是死锁以及死锁产生的四个必要条件。', '互斥、持有并等待、不可抢占资源、循环等待', '死锁是指多个进程互相等待对方释放资源而无法继续执行的状态。四个必要条件是：互斥、持有并等待、不可抢占资源和循环等待。', 10, '困难', '', 4);
INSERT INTO `question` VALUES (15, 18, 'coding', '编写一个Python函数，实现使用多进程处理任务，并通过队列返回结果。', 'import multiprocessing\n\ndef worker(task_queue, result_queue):\n    while True:\n        item = task_queue.get()\n        if item is None:\n            break\n        result = item * item\n        result_queue.put(result)\n\ndef main():\n    tasks = [1, 2, 3, 4, 5]\n    task_queue = multiprocessing.Queue()\n    result_queue = multiprocessing.Queue()\n\n    for item in tasks:\n        task_queue.put(item)\n\n    processes = []\n    num_workers = 3\n    for _ in range(num_workers):\n        p = multiprocessing.Process(target=worker, args=(task_queue, result_queue))\n        processes.append(p)\n        p.start()\n\n    for _ in range(num_workers):\n        task_queue.put(None)\n\n    for p in processes:\n        p.join()\n\n    results = []\n    while not result_queue.empty():\n        results.append(result_queue.get())\n\n    print(results)\n\nif __name__ == \'__main__\':\n    main()', '该代码演示了如何创建多个进程并通过队列传递任务和结果。', 20, '困难', '', 5);
INSERT INTO `question` VALUES (16, 19, 'choice', '以下哪种操作可以改变张量的形状？', 'A', 'reshape操作可以重新定义张量的维度，其他选项用于不同用途。', 2, '简单', '', 1);
INSERT INTO `question` VALUES (17, 19, 'multiple', '下列哪些因素可能影响模型训练的速度？', 'A,B,C,D', '所有列出的因素都会影响模型训练的速度。', 4, '中等', '', 2);
INSERT INTO `question` VALUES (18, 19, 'fill', '在操作系统中，进程通过____机制共享内存进行通信。', '共享内存', '共享内存是进程间通信的一种高效方式。', 3, '中等', '', 3);
INSERT INTO `question` VALUES (19, 19, 'short', '解释什么是死锁以及死锁产生的四个必要条件。', '互斥、持有并等待、不可抢占资源、循环等待', '死锁是指多个进程互相等待对方释放资源而无法继续执行的状态。四个必要条件是：互斥、持有并等待、不可抢占资源和循环等待。', 10, '困难', '', 4);
INSERT INTO `question` VALUES (20, 19, 'coding', '编写一个Python函数，实现使用多进程处理任务，并通过队列返回结果。', 'import multiprocessing\n\ndef worker(task_queue, result_queue):\n    while True:\n        item = task_queue.get()\n        if item is None:\n            break\n        result = item * item\n        result_queue.put(result)\n\ndef main():\n    tasks = [1, 2, 3, 4, 5]\n    task_queue = multiprocessing.Queue()\n    result_queue = multiprocessing.Queue()\n\n    for item in tasks:\n        task_queue.put(item)\n\n    processes = []\n    num_workers = 3\n    for _ in range(num_workers):\n        p = multiprocessing.Process(target=worker, args=(task_queue, result_queue))\n        processes.append(p)\n        p.start()\n\n    for _ in range(num_workers):\n        task_queue.put(None)\n\n    for p in processes:\n        p.join()\n\n    results = []\n    while not result_queue.empty():\n        results.append(result_queue.get())\n\n    print(results)\n\nif __name__ == \'__main__\':\n    main()', '该代码演示了如何创建多个进程并通过队列传递任务和结果。', 20, '困难', '', 5);
INSERT INTO `question` VALUES (21, 20, 'choice', 'Python中用于定义函数的关键字是？', 'A', '在Python中，使用关键字\'def\'来定义一个函数。', 2, '简单', '', 1);
INSERT INTO `question` VALUES (22, 20, 'multiple', '以下哪些属于嵌入式系统的特点？', 'A,B,D', '嵌入式系统通常是为特定功能设计的，具有实时性和资源受限等特点，一般不需要复杂的人机交互。', 4, '中等', '', 2);
INSERT INTO `question` VALUES (23, 20, 'fill', '在TCP/IP协议栈中，HTTP协议工作在________层。', '应用', 'HTTP协议是一种应用层协议，用于客户端与服务器之间的数据交换。', 3, '中等', '', 3);
INSERT INTO `question` VALUES (24, 20, 'short', '请简述SYN Flood攻击的基本原理及防御方法。', 'SYN Flood是一种DDoS攻击形式，攻击者发送大量伪造源地址的SYN请求，占用服务器资源导致无法响应正常连接。常见防御方式包括设置防火墙规则、启用SYN Cookie等。', '该攻击利用TCP三次握手的漏洞，发送大量半开连接耗尽服务器资源。', 10, '困难', '', 4);
INSERT INTO `question` VALUES (25, 20, 'coding', '编写一个Python程序，使用socket库实现简单的TCP服务端和客户端通信：服务端接收客户端消息并原样返回。', 'import socket\n\n# 服务端\nserver_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\nserver_socket.bind((\'localhost\', 12345))\nserver_socket.listen(1)\nprint(\'Server is listening...\')\nconn, addr = server_socket.accept()\nwhile True:\n    data = conn.recv(1024)\n    if not data:\n        break\n    conn.sendall(data)\nconn.close()\n\n# 客户端\nclient_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\nclient_socket.connect((\'localhost\', 12345))\nclient_socket.sendall(b\'Hello, Server!\')\ndata = client_socket.recv(1024)\nprint(\'Received:\', data.decode())\nclient_socket.close()', '通过socket编程实现基本的TCP回显服务。', 20, '困难', '', 5);
INSERT INTO `question` VALUES (26, 21, 'choice', 'Python中用于定义函数的关键字是？', 'A', '在Python中，使用关键字\'def\'来定义一个函数。', 2, '简单', '', 1);
INSERT INTO `question` VALUES (27, 21, 'multiple', '以下哪些属于嵌入式系统的特点？', 'A,B,D', '嵌入式系统通常是为特定功能设计的，具有实时性和资源受限等特点，一般不需要复杂的人机交互。', 4, '中等', '', 2);
INSERT INTO `question` VALUES (28, 21, 'fill', '在TCP/IP协议栈中，HTTP协议工作在________层。', '应用', 'HTTP协议是一种应用层协议，用于客户端与服务器之间的数据交换。', 3, '中等', '', 3);
INSERT INTO `question` VALUES (29, 21, 'short', '请简述SYN Flood攻击的基本原理及防御方法。', 'SYN Flood是一种DDoS攻击形式，攻击者发送大量伪造源地址的SYN请求，占用服务器资源导致无法响应正常连接。常见防御方式包括设置防火墙规则、启用SYN Cookie等。', '该攻击利用TCP三次握手的漏洞，发送大量半开连接耗尽服务器资源。', 10, '困难', '', 4);
INSERT INTO `question` VALUES (30, 21, 'coding', '编写一个Python程序，使用socket库实现简单的TCP服务端和客户端通信：服务端接收客户端消息并原样返回。', 'import socket\n\n# 服务端\nserver_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\nserver_socket.bind((\'localhost\', 12345))\nserver_socket.listen(1)\nprint(\'Server is listening...\')\nconn, addr = server_socket.accept()\nwhile True:\n    data = conn.recv(1024)\n    if not data:\n        break\n    conn.sendall(data)\nconn.close()\n\n# 客户端\nclient_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\nclient_socket.connect((\'localhost\', 12345))\nclient_socket.sendall(b\'Hello, Server!\')\ndata = client_socket.recv(1024)\nprint(\'Received:\', data.decode())\nclient_socket.close()', '通过socket编程实现基本的TCP回显服务。', 20, '困难', '', 5);

-- ----------------------------
-- Table structure for question_option
-- ----------------------------
DROP TABLE IF EXISTS `question_option`;
CREATE TABLE `question_option`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `key` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项键：A, B, C, D',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项内容',
  `is_correct` tinyint(1) NULL DEFAULT 0 COMMENT '是否为正确答案',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `question_option_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题目选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_option
-- ----------------------------
INSERT INTO `question_option` VALUES (1, 1, 'A', '队列', 0, 1);
INSERT INTO `question_option` VALUES (2, 1, 'B', '栈', 1, 2);
INSERT INTO `question_option` VALUES (3, 1, 'C', '链表', 0, 3);
INSERT INTO `question_option` VALUES (4, 1, 'D', '数组', 0, 4);
INSERT INTO `question_option` VALUES (5, 2, 'A', 'n/2', 0, 1);
INSERT INTO `question_option` VALUES (6, 2, 'B', '?n/2?', 0, 2);
INSERT INTO `question_option` VALUES (7, 2, 'C', '?n/2?', 1, 3);
INSERT INTO `question_option` VALUES (8, 2, 'D', 'n-1', 0, 4);
INSERT INTO `question_option` VALUES (9, 3, 'A', '链表的插入和删除操作时间复杂度为O(1)', 1, 1);
INSERT INTO `question_option` VALUES (10, 3, 'B', '链表不支持随机访问', 1, 2);
INSERT INTO `question_option` VALUES (11, 3, 'C', '链表需要额外的存储空间存储指针', 1, 3);
INSERT INTO `question_option` VALUES (12, 3, 'D', '链表的空间是连续分配的', 0, 4);
INSERT INTO `question_option` VALUES (13, 6, 'A', 'static', 0, 1);
INSERT INTO `question_option` VALUES (14, 6, 'B', 'final', 1, 2);
INSERT INTO `question_option` VALUES (15, 6, 'C', 'abstract', 0, 3);
INSERT INTO `question_option` VALUES (16, 6, 'D', 'private', 0, 4);
INSERT INTO `question_option` VALUES (17, 7, 'A', '方法重载', 0, 1);
INSERT INTO `question_option` VALUES (18, 7, 'B', '方法重写', 0, 2);
INSERT INTO `question_option` VALUES (19, 7, 'C', '接口实现', 0, 3);
INSERT INTO `question_option` VALUES (20, 7, 'D', '以上都是', 1, 4);
INSERT INTO `question_option` VALUES (21, 8, 'A', 'try块后必须跟catch块', 0, 1);
INSERT INTO `question_option` VALUES (22, 8, 'B', 'finally块中的代码一定会执行', 0, 2);
INSERT INTO `question_option` VALUES (23, 8, 'C', '可以有多个catch块', 1, 3);
INSERT INTO `question_option` VALUES (24, 8, 'D', 'Exception是所有异常的父类', 1, 4);
INSERT INTO `question_option` VALUES (25, 11, 'A', 'reshape', 1, 1);
INSERT INTO `question_option` VALUES (26, 11, 'B', 'transpose', 0, 2);
INSERT INTO `question_option` VALUES (27, 11, 'C', 'slice', 0, 3);
INSERT INTO `question_option` VALUES (28, 11, 'D', '以上都可以', 0, 4);
INSERT INTO `question_option` VALUES (29, 12, 'A', '学习率设置', 1, 1);
INSERT INTO `question_option` VALUES (30, 12, 'B', '批量大小（batch size）', 1, 2);
INSERT INTO `question_option` VALUES (31, 12, 'C', '优化器选择', 1, 3);
INSERT INTO `question_option` VALUES (32, 12, 'D', '数据集中的样本数量', 1, 4);
INSERT INTO `question_option` VALUES (33, 16, 'A', 'reshape', 1, 1);
INSERT INTO `question_option` VALUES (34, 16, 'B', 'transpose', 0, 2);
INSERT INTO `question_option` VALUES (35, 16, 'C', 'slice', 0, 3);
INSERT INTO `question_option` VALUES (36, 16, 'D', '以上都可以', 0, 4);
INSERT INTO `question_option` VALUES (37, 17, 'A', '学习率设置', 1, 1);
INSERT INTO `question_option` VALUES (38, 17, 'B', '批量大小（batch size）', 1, 2);
INSERT INTO `question_option` VALUES (39, 17, 'C', '优化器选择', 1, 3);
INSERT INTO `question_option` VALUES (40, 17, 'D', '数据集中的样本数量', 1, 4);
INSERT INTO `question_option` VALUES (41, 21, 'A', 'def', 1, 1);
INSERT INTO `question_option` VALUES (42, 21, 'B', 'function', 0, 2);
INSERT INTO `question_option` VALUES (43, 21, 'C', 'fun', 0, 3);
INSERT INTO `question_option` VALUES (44, 21, 'D', 'define', 0, 4);
INSERT INTO `question_option` VALUES (45, 22, 'A', '实时性要求高', 1, 1);
INSERT INTO `question_option` VALUES (46, 22, 'B', '资源有限', 1, 2);
INSERT INTO `question_option` VALUES (47, 22, 'C', '用户界面友好', 0, 3);
INSERT INTO `question_option` VALUES (48, 22, 'D', '专用性强', 1, 4);
INSERT INTO `question_option` VALUES (49, 26, 'A', 'def', 1, 1);
INSERT INTO `question_option` VALUES (50, 26, 'B', 'function', 0, 2);
INSERT INTO `question_option` VALUES (51, 26, 'C', 'fun', 0, 3);
INSERT INTO `question_option` VALUES (52, 26, 'D', 'define', 0, 4);
INSERT INTO `question_option` VALUES (53, 27, 'A', '实时性要求高', 1, 1);
INSERT INTO `question_option` VALUES (54, 27, 'B', '资源有限', 1, 2);
INSERT INTO `question_option` VALUES (55, 27, 'C', '用户界面友好', 0, 3);
INSERT INTO `question_option` VALUES (56, 27, 'D', '专用性强', 1, 4);

-- ----------------------------
-- Table structure for student_annotation
-- ----------------------------
DROP TABLE IF EXISTS `student_annotation`;
CREATE TABLE `student_annotation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `resource_id` bigint NOT NULL COMMENT '课程资源ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'highlight' COMMENT '标注类型：highlight, underline, comment, bookmark',
  `selected_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '被标注的文本内容',
  `page_number` int NULL DEFAULT 1 COMMENT '页码',
  `position` json NULL COMMENT '位置信息（起始位置、结束位置等）',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#FFFF00' COMMENT '标注颜色',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `importance` int NULL DEFAULT 3 COMMENT '重要程度（1-5）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_resource_type`(`student_id` ASC, `resource_id` ASC, `type` ASC) USING BTREE,
  INDEX `idx_resource_page`(`resource_id` ASC, `page_number` ASC) USING BTREE,
  CONSTRAINT `student_annotation_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_annotation_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `course_resource` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生标注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_annotation
-- ----------------------------
INSERT INTO `student_annotation` VALUES (1, 17, 1, 'highlight', 'Linux是一个开源的类Unix操作系统', 1, NULL, '#FFFF00', '这是Linux的核心定义', 5, '2025-07-03 12:06:12', '2025-07-03 12:06:12');
INSERT INTO `student_annotation` VALUES (2, 17, 1, 'underline', 'Shell是用户与内核交互的接口', 1, NULL, '#FF6347', '重要概念', 4, '2025-07-03 12:06:12', '2025-07-03 12:06:12');
INSERT INTO `student_annotation` VALUES (3, 17, 1, 'comment', '', 2, NULL, '#90EE90', '这部分讲解了文件权限管理，是实际应用的关键', 4, '2025-07-03 12:06:12', '2025-07-03 12:06:12');
INSERT INTO `student_annotation` VALUES (4, 4, 1, 'highlight', '时间复杂度O(n)', 3, NULL, '#FFB6C1', '算法分析的重要指标', 5, '2025-07-03 12:06:12', '2025-07-03 12:06:12');
INSERT INTO `student_annotation` VALUES (5, 4, 1, 'bookmark', '', 5, NULL, '#DDA0DD', '重要章节：排序算法', 5, '2025-07-03 12:06:12', '2025-07-03 12:06:12');
INSERT INTO `student_annotation` VALUES (14, 17, 35, 'highlight', 'TensorFlow Lite是一个轻量、快速、兼容度高的专门针对移动式应用场景的深度学习工具', 1, '{\"top\": 120, \"left\": 200, \"width\": 450, \"height\": 18, \"textAfter\": \"，把移动端及 IoT 设备\", \"textBefore\": \"团队又在 2017 年底上线了 \", \"paragraphIndex\": 2}', '#FFFF00', 'TensorFlow Lite核心定义', 5, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (15, 17, 35, 'underline', '全球有超过40亿的设备上部署着TFLite', 1, '{\"top\": 180, \"left\": 200, \"width\": 280, \"height\": 18, \"textAfter\": \"，例如Google Assistant\", \"textBefore\": \"\", \"paragraphIndex\": 5}', '#FF0000', '重要统计数据', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (16, 17, 35, 'comment', 'TensorFlow Lite 二进制文件的大小约为 1 MB', 1, '{\"top\": 240, \"left\": 200, \"width\": 320, \"height\": 18, \"textAfter\": \"（针对 32 位 ARM build）\", \"textBefore\": \"更轻量。\", \"paragraphIndex\": 8}', '#00FF00', '轻量化优势明显', 3, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (17, 17, 35, 'highlight', 'TensorFlow Lite 解释器(Interpreter)', 1, '{\"top\": 300, \"left\": 200, \"width\": 250, \"height\": 18, \"textAfter\": \"\", \"textBefore\": \"\", \"paragraphIndex\": 12}', '#FFFF00', '四大核心组件之一', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (18, 17, 35, 'highlight', 'TensorFlow Lite 转换器(Converter)', 1, '{\"top\": 320, \"left\": 200, \"width\": 240, \"height\": 18, \"textAfter\": \"\", \"textBefore\": \"\", \"paragraphIndex\": 13}', '#FFFF00', '四大核心组件之一', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (19, 17, 35, 'underline', '量化使用了一些技术，可以降低权重的精确表示', 1, '{\"top\": 380, \"left\": 200, \"width\": 300, \"height\": 18, \"textAfter\": \"，并且可选的降低存储\", \"textBefore\": \"\", \"paragraphIndex\": 18}', '#FF0000', '模型优化关键技术', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (20, 17, 35, 'comment', 'MobileNet V2是基于一个流线型的架构', 1, '{\"top\": 440, \"left\": 200, \"width\": 280, \"height\": 18, \"textAfter\": \"，它使用深度可分离的卷积\", \"textBefore\": \"选择\", \"paragraphIndex\": 22}', '#00FF00', '花卉识别项目使用的模型', 3, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (21, 17, 35, 'sticky-note', '', 1, '{\"x\": 500, \"y\": 200}', '#FFB6C1', '重要：TensorFlow Lite支持Android、iOS以及嵌入式设备', 5, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (22, 17, 35, 'highlight', '选择模型、转换模型、部署到设备、优化模型', 1, '{\"top\": 500, \"left\": 200, \"width\": 350, \"height\": 18, \"textAfter\": \"\", \"textBefore\": \"工作流程包括如下步骤：\", \"paragraphIndex\": 25}', '#FFFF00', 'TensorFlow Lite标准开发流程', 5, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (23, 17, 35, 'underline', 'tf.lite.TFLiteConverter.from_saved_model()', 1, '{\"top\": 560, \"left\": 200, \"width\": 280, \"height\": 18, \"textAfter\": \"\", \"textBefore\": \"用来转换 SavedModel 格式模型。\", \"paragraphIndex\": 28}', '#FF0000', '重要API接口', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (24, 17, 35, 'comment', '模型运行速度可以提高 5.5 倍', 1, '{\"top\": 620, \"left\": 200, \"width\": 220, \"height\": 18, \"textAfter\": \"。\", \"textBefore\": \"MobileNet图像分类，\", \"paragraphIndex\": 32}', '#00FF00', 'GPU加速效果显著', 3, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (25, 17, 35, 'sticky-note', '', 1, '{\"x\": 600, \"y\": 300}', '#FFB6C1', '关键学习点：\n1. 理解TensorFlow Lite架构\n2. 掌握模型转换流程\n3. 学会Android部署', 4, '2025-07-05 19:26:00', '2025-07-05 19:26:00');
INSERT INTO `student_annotation` VALUES (26, 17, 34, 'highlight', 'nsorFlow Lite的整体', 1, '{\"top\": 571.1979370117188, \"left\": 120.89583206176758, \"width\": 143.15625, \"height\": 18, \"endOffset\": 20, \"textAfter\": \"架构\", \"textBefore\": \"掌握Te\", \"startOffset\": 4, \"endContainer\": \"LI\", \"paragraphIndex\": 7, \"startContainer\": \"LI\"}', '#FFFF00', '', 3, '2025-07-05 19:28:20', '2025-07-05 19:28:20');
INSERT INTO `student_annotation` VALUES (27, 17, 34, 'highlight', 'sorFlow Lite转', 1, '{\"top\": 607.9895935058594, \"left\": 129.79167556762695, \"width\": 102.26040649414062, \"height\": 18, \"endOffset\": 18, \"textAfter\": \"换器作用\", \"textBefore\": \"掌握Ten\", \"startOffset\": 5, \"endContainer\": \"LI\", \"paragraphIndex\": 8, \"startContainer\": \"LI\"}', '#FFFF00', '', 3, '2025-07-05 19:28:24', '2025-07-05 19:28:24');
INSERT INTO `student_annotation` VALUES (28, 17, 34, 'underline', '的整体架构', 1, '{\"top\": 571.1979370117188, \"left\": 216.04167556762695, \"width\": 80.01040649414062, \"height\": 18, \"endOffset\": 22, \"textAfter\": \"\", \"textBefore\": \"掌握TensorFlow Lite\", \"startOffset\": 17, \"endContainer\": \"LI\", \"paragraphIndex\": 7, \"startContainer\": \"LI\"}', '#FF0000', '', 3, '2025-07-05 19:28:34', '2025-07-05 19:28:34');

-- ----------------------------
-- Table structure for student_answer
-- ----------------------------
DROP TABLE IF EXISTS `student_answer`;
CREATE TABLE `student_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_exam_id` bigint NOT NULL COMMENT '学生考试记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学生答案',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT '是否正确',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教师评语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_exam_id`(`student_exam_id` ASC) USING BTREE,
  INDEX `question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `student_answer_ibfk_1` FOREIGN KEY (`student_exam_id`) REFERENCES `student_exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_answer_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生答案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answer
-- ----------------------------

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'enrolled' COMMENT '状态：enrolled, completed, dropped',
  `enroll_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `final_score` int NULL DEFAULT NULL COMMENT '最终成绩',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course`(`student_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `student_course_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_course_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生选课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO `student_course` VALUES (1, 2, 1, 'enrolled', '2024-02-20 09:00:00', NULL, 87, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (2, 2, 6, 'enrolled', '2024-02-20 09:30:00', NULL, 92, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (3, 2, 16, 'enrolled', '2024-02-20 10:00:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (4, 3, 1, 'enrolled', '2024-02-20 10:30:00', NULL, 78, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (5, 3, 2, 'enrolled', '2024-02-20 11:00:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (6, 3, 5, 'enrolled', '2024-02-20 11:30:00', NULL, 80, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (7, 4, 1, 'enrolled', '2024-02-20 12:00:00', NULL, 91, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (8, 4, 3, 'enrolled', '2024-02-20 12:30:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (9, 4, 6, 'enrolled', '2024-02-20 13:00:00', NULL, 83, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (10, 5, 2, 'enrolled', '2024-02-20 13:30:00', NULL, 79, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (11, 5, 5, 'enrolled', '2024-02-20 14:00:00', NULL, 86, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (12, 5, 7, 'enrolled', '2024-02-20 14:30:00', NULL, 75, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (13, 6, 1, 'enrolled', '2024-02-20 15:00:00', NULL, 89, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (14, 6, 3, 'enrolled', '2024-02-20 15:30:00', NULL, 92, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (15, 6, 8, 'enrolled', '2024-02-20 16:00:00', NULL, 84, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (16, 7, 2, 'enrolled', '2024-02-21 09:00:00', NULL, 82, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (17, 7, 6, 'enrolled', '2024-02-21 09:30:00', NULL, 77, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (18, 7, 9, 'enrolled', '2024-02-21 10:00:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (19, 8, 1, 'enrolled', '2024-02-21 10:30:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (20, 8, 5, 'enrolled', '2024-02-21 11:00:00', NULL, 79, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (21, 8, 10, 'enrolled', '2024-02-21 11:30:00', NULL, 81, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (22, 9, 2, 'enrolled', '2024-02-21 12:00:00', NULL, 90, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (23, 9, 3, 'enrolled', '2024-02-21 12:30:00', NULL, 86, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (24, 9, 7, 'enrolled', '2024-02-21 13:00:00', NULL, 83, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (25, 10, 1, 'enrolled', '2024-02-21 13:30:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (26, 10, 6, 'enrolled', '2024-02-21 14:00:00', NULL, 91, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (27, 10, 8, 'enrolled', '2024-02-21 14:30:00', NULL, 87, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (28, 32, 4, 'enrolled', '2024-02-21 15:00:00', NULL, 93, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (29, 32, 2, 'enrolled', '2024-02-21 15:30:00', NULL, 89, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (30, 32, 11, 'enrolled', '2024-02-21 16:00:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (31, 33, 4, 'enrolled', '2024-02-21 16:30:00', NULL, 87, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (32, 33, 8, 'enrolled', '2024-02-21 17:00:00', NULL, 91, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (33, 33, 18, 'enrolled', '2024-02-21 17:30:00', NULL, 82, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (34, 34, 2, 'enrolled', '2024-02-22 09:00:00', NULL, 84, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (35, 34, 4, 'enrolled', '2024-02-22 09:30:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (36, 34, 11, 'enrolled', '2024-02-22 10:00:00', NULL, 79, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (37, 35, 4, 'enrolled', '2024-02-22 10:30:00', NULL, 92, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (38, 35, 3, 'enrolled', '2024-02-22 11:00:00', NULL, 86, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (39, 35, 8, 'enrolled', '2024-02-22 11:30:00', NULL, 90, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (40, 92, 7, 'enrolled', '2024-02-22 12:00:00', NULL, 94, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (41, 92, 13, 'enrolled', '2024-02-22 12:30:00', NULL, 91, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (42, 92, 17, 'enrolled', '2024-02-22 13:00:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (43, 93, 7, 'enrolled', '2024-02-22 13:30:00', NULL, 89, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (44, 93, 13, 'enrolled', '2024-02-22 14:00:00', NULL, 87, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (45, 93, 10, 'enrolled', '2024-02-22 14:30:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (46, 94, 13, 'enrolled', '2024-02-22 15:00:00', NULL, 92, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (47, 94, 17, 'enrolled', '2024-02-22 15:30:00', NULL, 90, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (48, 94, 7, 'enrolled', '2024-02-22 16:00:00', NULL, 86, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (49, 77, 6, 'enrolled', '2024-02-23 09:00:00', NULL, 88, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (50, 77, 9, 'enrolled', '2024-02-23 09:30:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (51, 77, 14, 'enrolled', '2024-02-23 10:00:00', NULL, 82, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (52, 78, 6, 'enrolled', '2024-02-23 10:30:00', NULL, 91, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (53, 78, 9, 'enrolled', '2024-02-23 11:00:00', NULL, 89, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (54, 78, 19, 'enrolled', '2024-02-23 11:30:00', NULL, 87, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (55, 57, 3, 'enrolled', '2024-02-23 12:00:00', NULL, 90, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (56, 57, 15, 'enrolled', '2024-02-23 12:30:00', NULL, 86, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (57, 57, 10, 'enrolled', '2024-02-23 13:00:00', NULL, 83, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (58, 72, 3, 'enrolled', '2024-02-23 13:30:00', NULL, 92, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (59, 72, 10, 'enrolled', '2024-02-23 14:00:00', NULL, 89, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (60, 72, 17, 'enrolled', '2024-02-23 14:30:00', NULL, 85, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_course` VALUES (61, 17, 16, 'enrolled', '2025-07-03 10:21:03', NULL, NULL, '2025-07-03 10:21:03', '2025-07-03 10:21:03');

-- ----------------------------
-- Table structure for student_exam
-- ----------------------------
DROP TABLE IF EXISTS `student_exam`;
CREATE TABLE `student_exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `total_score` int NOT NULL COMMENT '总分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'submitted' COMMENT '状态：submitted, graded',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `grade_time` datetime NULL DEFAULT NULL COMMENT '批改时间',
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教师评语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `exam_id`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `student_exam_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_exam_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_exam
-- ----------------------------
INSERT INTO `student_exam` VALUES (1, 2, 1, 87, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 11:00:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (2, 3, 1, 78, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 10:45:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (3, 4, 1, 91, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 11:15:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (4, 6, 1, 89, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 10:55:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (5, 8, 1, 85, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 11:05:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (6, 10, 1, 88, 100, 'graded', '2024-04-15 09:00:00', '2024-04-15 10:50:00', '2024-04-16 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (7, 2, 3, 92, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:55:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (8, 3, 3, 42, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:58:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (9, 5, 3, 39, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:52:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (10, 7, 3, 41, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:48:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (11, 9, 3, 45, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:50:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (12, 32, 3, 44, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:53:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (13, 34, 3, 42, 50, 'graded', '2024-04-10 14:00:00', '2024-04-10 14:57:00', '2024-04-11 09:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (14, 2, 5, 85, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:25:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (15, 4, 5, 88, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:20:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (16, 6, 5, 73, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:30:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (17, 9, 5, 69, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:22:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (18, 35, 5, 69, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:18:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (19, 57, 5, 72, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:28:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (20, 72, 5, 74, 80, 'graded', '2024-04-20 10:00:00', '2024-04-20 11:15:00', '2024-04-21 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (21, 5, 9, 52, 70, 'graded', '2024-04-12 15:00:00', '2024-04-12 16:10:00', '2024-04-13 11:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (22, 9, 9, 58, 70, 'graded', '2024-04-12 15:00:00', '2024-04-12 16:05:00', '2024-04-13 11:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (23, 92, 9, 66, 70, 'graded', '2024-04-12 15:00:00', '2024-04-12 16:12:00', '2024-04-13 11:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (24, 93, 9, 62, 70, 'graded', '2024-04-12 15:00:00', '2024-04-12 16:08:00', '2024-04-13 11:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (25, 94, 9, 60, 70, 'graded', '2024-04-12 15:00:00', '2024-04-12 16:15:00', '2024-04-13 11:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (26, 4, 8, 50, 60, 'graded', '2024-04-18 14:00:00', '2024-04-18 15:25:00', '2024-04-19 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (27, 7, 8, 46, 60, 'graded', '2024-04-18 14:00:00', '2024-04-18 15:30:00', '2024-04-19 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (28, 10, 8, 52, 60, 'graded', '2024-04-18 14:00:00', '2024-04-18 15:22:00', '2024-04-19 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (29, 77, 8, 53, 60, 'graded', '2024-04-18 14:00:00', '2024-04-18 15:28:00', '2024-04-19 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (30, 78, 8, 55, 60, 'graded', '2024-04-18 14:00:00', '2024-04-18 15:20:00', '2024-04-19 10:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_exam` VALUES (31, 2, 17, 85, 85, 'graded', '2024-04-24 09:00:00', '2024-04-24 10:35:00', '2024-04-25 14:00:00', NULL, '2025-07-03 09:03:16', '2025-07-03 09:03:16');

-- ----------------------------
-- Table structure for student_note
-- ----------------------------
DROP TABLE IF EXISTS `student_note`;
CREATE TABLE `student_note`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `resource_id` bigint NOT NULL COMMENT '课程资源ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '笔记标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '笔记内容',
  `page_number` int NULL DEFAULT 1 COMMENT '页码（PDF等分页文档）',
  `position` json NULL COMMENT '位置信息（坐标、选择范围等）',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签（用逗号分隔）',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#FFD700' COMMENT '笔记颜色',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开（供其他学生查看）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_resource`(`student_id` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_resource_public`(`resource_id` ASC, `is_public` ASC) USING BTREE,
  CONSTRAINT `student_note_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_note_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `course_resource` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生笔记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_note
-- ----------------------------
INSERT INTO `student_note` VALUES (1, 17, 1, 'Linux基础概念', '学习Linux系统的基本概念：内核、Shell、文件系统。这些是理解Linux的核心要素。', 1, NULL, 'Linux,基础,概念', '#FFD700', 0, '2025-07-03 12:06:10', '2025-07-03 12:06:10');
INSERT INTO `student_note` VALUES (2, 17, 1, '常用命令总结', 'ls、cd、pwd、mkdir、rmdir等基本命令的使用方法和参数说明。', 2, NULL, 'Linux,命令,总结', '#87CEEB', 0, '2025-07-03 12:06:10', '2025-07-03 12:06:10');
INSERT INTO `student_note` VALUES (3, 4, 1, '数据结构重点', '数组和链表的区别：数组连续存储，访问快；链表动态存储，插入删除灵活。', 1, NULL, '数据结构,重点', '#98FB98', 0, '2025-07-03 12:06:10', '2025-07-03 12:06:10');
INSERT INTO `student_note` VALUES (4, 17, 34, '123', 'Linux', 1, NULL, '', '#87CEEB', 0, '2025-07-03 12:24:47', '2025-07-03 12:24:47');
INSERT INTO `student_note` VALUES (5, 17, 34, '134', '123', 1, NULL, '', '#98FB98', 0, '2025-07-03 16:05:34', '2025-07-03 16:05:34');

-- ----------------------------
-- Table structure for student_practice
-- ----------------------------
DROP TABLE IF EXISTS `student_practice`;
CREATE TABLE `student_practice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `practice_id` bigint NOT NULL COMMENT '练习ID',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `total_score` int NOT NULL COMMENT '总分',
  `accuracy` int NULL DEFAULT NULL COMMENT '正确率',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'in_progress' COMMENT '状态：in_progress, completed',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `duration` int NULL DEFAULT NULL COMMENT '实际用时（分钟）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `practice_id`(`practice_id` ASC) USING BTREE,
  CONSTRAINT `student_practice_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_practice_ibfk_2` FOREIGN KEY (`practice_id`) REFERENCES `practice` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生练习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_practice
-- ----------------------------
INSERT INTO `student_practice` VALUES (1, 2, 1, 85, 100, 85, 'completed', '2024-03-10 14:00:00', '2024-03-10 14:25:00', 25, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (2, 3, 1, 72, 100, 72, 'completed', '2024-03-10 15:00:00', '2024-03-10 15:28:00', 28, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (3, 4, 1, 91, 100, 91, 'completed', '2024-03-10 16:00:00', '2024-03-10 16:22:00', 22, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (4, 6, 1, 88, 100, 88, 'completed', '2024-03-11 14:00:00', '2024-03-11 14:30:00', 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (5, 8, 1, 79, 100, 79, 'completed', '2024-03-11 15:00:00', '2024-03-11 15:26:00', 26, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (6, 10, 1, 86, 100, 86, 'completed', '2024-03-11 16:00:00', '2024-03-11 16:24:00', 24, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (7, 2, 2, 92, 100, 92, 'completed', '2024-03-15 14:00:00', '2024-03-15 14:40:00', 40, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (8, 4, 2, 89, 100, 89, 'completed', '2024-03-15 15:00:00', '2024-03-15 15:42:00', 42, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (9, 6, 2, 85, 100, 85, 'completed', '2024-03-15 16:00:00', '2024-03-15 16:45:00', 45, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (10, 8, 2, 81, 100, 81, 'completed', '2024-03-16 14:00:00', '2024-03-16 14:43:00', 43, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (11, 10, 2, 87, 100, 87, 'completed', '2024-03-16 15:00:00', '2024-03-16 15:38:00', 38, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (12, 2, 3, 94, 100, 94, 'completed', '2024-03-12 14:00:00', '2024-03-12 14:20:00', 20, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (13, 3, 3, 83, 100, 83, 'completed', '2024-03-12 15:00:00', '2024-03-12 15:25:00', 25, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (14, 5, 3, 76, 100, 76, 'completed', '2024-03-12 16:00:00', '2024-03-12 16:24:00', 24, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (15, 7, 3, 80, 100, 80, 'completed', '2024-03-13 14:00:00', '2024-03-13 14:22:00', 22, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (16, 9, 3, 88, 100, 88, 'completed', '2024-03-13 15:00:00', '2024-03-13 15:23:00', 23, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (17, 32, 3, 91, 100, 91, 'completed', '2024-03-13 16:00:00', '2024-03-13 16:21:00', 21, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (18, 34, 3, 85, 100, 85, 'completed', '2024-03-14 14:00:00', '2024-03-14 14:25:00', 25, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (19, 2, 4, 89, 100, 89, 'completed', '2024-03-20 14:00:00', '2024-03-20 14:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (20, 9, 4, 85, 100, 85, 'completed', '2024-03-20 15:00:00', '2024-03-20 15:38:00', 38, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (21, 32, 4, 92, 100, 92, 'completed', '2024-03-20 16:00:00', '2024-03-20 16:33:00', 33, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (22, 2, 5, 87, 100, 87, 'completed', '2024-03-18 14:00:00', '2024-03-18 14:32:00', 32, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (23, 4, 5, 90, 100, 90, 'completed', '2024-03-18 15:00:00', '2024-03-18 15:30:00', 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (24, 6, 5, 83, 100, 83, 'completed', '2024-03-18 16:00:00', '2024-03-18 16:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (25, 9, 5, 86, 100, 86, 'completed', '2024-03-19 14:00:00', '2024-03-19 14:33:00', 33, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (26, 35, 5, 92, 100, 92, 'completed', '2024-03-19 15:00:00', '2024-03-19 15:28:00', 28, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (27, 57, 5, 88, 100, 88, 'completed', '2024-03-19 16:00:00', '2024-03-19 16:31:00', 31, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_practice` VALUES (28, 72, 5, 94, 100, 94, 'completed', '2024-03-20 14:00:00', '2024-03-20 14:29:00', 29, '2025-07-03 09:03:16', '2025-07-03 09:03:16');

-- ----------------------------
-- Table structure for student_progress
-- ----------------------------
DROP TABLE IF EXISTS `student_progress`;
CREATE TABLE `student_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `chapter_id` bigint NULL DEFAULT NULL COMMENT '章节ID',
  `lesson_id` bigint NULL DEFAULT NULL COMMENT '课时ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'not_started' COMMENT '状态：not_started, in_progress, completed',
  `progress` int NULL DEFAULT 0 COMMENT '进度百分比',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始学习时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `study_duration` int NULL DEFAULT 0 COMMENT '学习时长（分钟）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `chapter_id`(`chapter_id` ASC) USING BTREE,
  INDEX `lesson_id`(`lesson_id` ASC) USING BTREE,
  CONSTRAINT `student_progress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_progress_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_progress_ibfk_3` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_progress_ibfk_4` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生学习进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_progress
-- ----------------------------
INSERT INTO `student_progress` VALUES (1, 2, 1, 1, 1, 'completed', 100, '2024-02-25 09:00:00', '2024-02-25 09:45:00', 45, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (2, 2, 1, 1, 2, 'completed', 100, '2024-02-26 09:00:00', '2024-02-26 09:40:00', 40, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (3, 2, 1, 1, 3, 'completed', 100, '2024-02-27 09:00:00', '2024-02-27 09:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (4, 2, 1, 2, 4, 'completed', 100, '2024-02-28 09:00:00', '2024-02-28 09:50:00', 50, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (5, 2, 1, 2, 5, 'completed', 100, '2024-03-01 09:00:00', '2024-03-01 09:45:00', 45, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (6, 2, 1, 2, 6, 'in_progress', 60, '2024-03-02 09:00:00', NULL, 33, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (7, 2, 2, 6, 12, 'completed', 100, '2024-03-03 14:00:00', '2024-03-03 14:30:00', 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (8, 2, 2, 6, 13, 'completed', 100, '2024-03-04 14:00:00', '2024-03-04 14:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (9, 2, 2, 6, 14, 'completed', 100, '2024-03-05 14:00:00', '2024-03-05 14:40:00', 40, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (10, 2, 2, 6, 15, 'in_progress', 80, '2024-03-06 14:00:00', NULL, 36, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (11, 4, 1, 1, 1, 'completed', 100, '2024-03-01 10:00:00', '2024-03-01 10:45:00', 45, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (12, 4, 1, 1, 2, 'completed', 100, '2024-03-02 10:00:00', '2024-03-02 10:40:00', 40, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (13, 4, 1, 1, 3, 'completed', 100, '2024-03-03 10:00:00', '2024-03-03 10:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (14, 4, 1, 2, 4, 'completed', 100, '2024-03-04 10:00:00', '2024-03-04 10:50:00', 50, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (15, 4, 1, 2, 5, 'in_progress', 70, '2024-03-05 10:00:00', NULL, 32, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (16, 4, 3, 11, NULL, 'completed', 100, '2024-03-06 15:00:00', '2024-03-06 16:00:00', 60, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (17, 4, 6, NULL, NULL, 'in_progress', 25, '2024-03-07 16:00:00', NULL, 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (18, 32, 4, NULL, NULL, 'in_progress', 40, '2024-03-01 09:00:00', NULL, 180, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (19, 32, 2, 6, 12, 'completed', 100, '2024-03-01 14:00:00', '2024-03-01 14:30:00', 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (20, 32, 2, 6, 13, 'completed', 100, '2024-03-02 14:00:00', '2024-03-02 14:35:00', 35, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (21, 32, 2, 6, 14, 'in_progress', 75, '2024-03-03 14:00:00', NULL, 30, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (22, 32, 11, NULL, NULL, 'not_started', 0, NULL, NULL, 0, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (23, 92, 7, NULL, NULL, 'in_progress', 65, '2024-02-25 10:00:00', NULL, 240, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (24, 92, 13, NULL, NULL, 'in_progress', 35, '2024-03-01 10:00:00', NULL, 150, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (25, 92, 17, NULL, NULL, 'not_started', 0, NULL, NULL, 0, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (26, 2, 1, NULL, NULL, 'in_progress', 75, '2024-03-01 09:00:00', NULL, 180, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (27, 2, 6, NULL, NULL, 'in_progress', 85, '2024-03-05 14:00:00', NULL, 210, '2025-07-03 09:03:16', '2025-07-03 09:03:16');
INSERT INTO `student_progress` VALUES (28, 2, 16, NULL, NULL, 'in_progress', 60, '2024-03-10 16:00:00', NULL, 150, '2025-07-03 09:03:16', '2025-07-03 09:03:16');

-- ----------------------------
-- Table structure for study_group
-- ----------------------------
DROP TABLE IF EXISTS `study_group`;
CREATE TABLE `study_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '小组名称',
  `group_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '小组描述',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学科',
  `leader_id` bigint NOT NULL COMMENT '组长ID',
  `max_members` int NULL DEFAULT 6 COMMENT '最大成员数',
  `current_members` int NULL DEFAULT 1 COMMENT '当前成员数',
  `group_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'study' COMMENT '小组类型:study,project,competition,practice',
  `privacy_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'public' COMMENT '隐私级别:public,private,invite_only',
  `activity_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'moderate' COMMENT '活跃度:low,moderate,high',
  `learning_goals` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习目标',
  `group_rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '小组规则',
  `meeting_schedule` json NULL COMMENT '会议安排',
  `performance_metrics` json NULL COMMENT '绩效指标',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态:active,inactive,disbanded',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `leader_id`(`leader_id` ASC) USING BTREE,
  INDEX `idx_subject_type`(`subject` ASC, `group_type` ASC) USING BTREE,
  CONSTRAINT `study_group_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习小组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_group
-- ----------------------------
INSERT INTO `study_group` VALUES (1, 'Java学习互助组', '专注于Java编程学习的互助小组，欢迎初学者加入', 'Java编程', 2, 6, 3, 'study', 'public', 'moderate', '掌握Java基础语法，能够编写简单的Java程序', NULL, NULL, NULL, 'active', '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `study_group` VALUES (2, '算法竞赛训练队', '备战ACM等算法竞赛的训练小组', '算法', 4, 8, 4, 'competition', 'public', 'moderate', '提高算法设计能力，在竞赛中取得好成绩', NULL, NULL, NULL, 'active', '2025-07-04 23:21:16', '2025-07-04 23:21:16');
INSERT INTO `study_group` VALUES (3, '数据结构项目组', '通过实际项目学习数据结构的应用', '数据结构', 5, 5, 2, 'project', 'public', 'moderate', '完成一个综合性的数据结构应用项目', NULL, NULL, NULL, 'active', '2025-07-04 23:21:16', '2025-07-04 23:21:16');

-- ----------------------------
-- Table structure for study_group_member
-- ----------------------------
DROP TABLE IF EXISTS `study_group_member`;
CREATE TABLE `study_group_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` bigint NOT NULL COMMENT '小组ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'member' COMMENT '角色:leader,co_leader,member',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `contribution_score` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '贡献度(0-1)',
  `attendance_rate` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '出勤率(0-1)',
  `peer_rating` decimal(3, 2) NULL DEFAULT NULL COMMENT '同伴评分(0-1)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态:active,inactive,left,kicked',
  `leave_time` datetime NULL DEFAULT NULL COMMENT '离开时间',
  `leave_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '离开原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_student`(`group_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `study_group_member_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `study_group` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `study_group_member_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学习小组成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_group_member
-- ----------------------------
INSERT INTO `study_group_member` VALUES (1, 1, 2, 'leader', '2025-07-04 23:21:16', 0.95, 1.00, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (2, 1, 3, 'member', '2025-07-04 23:21:16', 0.80, 0.90, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (3, 1, 7, 'member', '2025-07-04 23:21:16', 0.75, 0.85, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (4, 2, 4, 'leader', '2025-07-04 23:21:16', 0.90, 0.95, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (5, 2, 6, 'member', '2025-07-04 23:21:16', 0.85, 0.90, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (6, 2, 8, 'member', '2025-07-04 23:21:16', 0.70, 0.80, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (7, 2, 32, 'member', '2025-07-04 23:21:16', 0.88, 0.92, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (8, 3, 5, 'leader', '2025-07-04 23:21:16', 0.85, 0.90, NULL, 'active', NULL, NULL);
INSERT INTO `study_group_member` VALUES (9, 3, 92, 'member', '2025-07-04 23:21:16', 0.80, 0.85, NULL, 'active', NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（MD5加密）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'student' COMMENT '角色：admin, teacher, student',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态：active, inactive',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学号或工号',
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学院',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级（学生）',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级（学生）',
  `subject` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任教专业（教师）',
  `register_date` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_role`(`username` ASC, `role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@university.edu', NULL, '系统管理员', 'admin', NULL, 'active', 'ADM001', NULL, NULL, NULL, NULL, NULL, '2024-01-01 08:00:00', '2025-07-05 00:37:30', '2025-07-03 09:02:54', '2025-07-05 00:37:30');
INSERT INTO `user` VALUES (2, 'teacher1', 'e10adc3949ba59abbe56e057f20f883e', 'zhang.prof@university.edu', NULL, '张教授', 'teacher', NULL, 'active', 'T2024001', '计算机科学与技术学院', NULL, NULL, NULL, '数据结构、计算机网络、Linux系统', '2020-09-01 09:00:00', '2025-07-05 18:40:11', '2025-07-03 09:02:56', '2025-07-05 18:40:11');
INSERT INTO `user` VALUES (3, 'teacher2', 'e10adc3949ba59abbe56e057f20f883e', 'wang.prof@university.edu', NULL, '王教授', 'teacher', NULL, 'active', 'T2024002', '计算机科学与技术学院', NULL, NULL, NULL, 'Java程序设计', '2019-03-15 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (4, 'teacher3', 'e10adc3949ba59abbe56e057f20f883e', 'li.prof@university.edu', NULL, '李教授', 'teacher', NULL, 'active', 'T2024003', '信息工程学院', NULL, NULL, NULL, '数据库系统', '2018-08-20 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (5, 'teacher4', 'e10adc3949ba59abbe56e057f20f883e', 'chen.prof@university.edu', NULL, '陈教授', 'teacher', NULL, 'active', 'T2024004', '软件工程学院', NULL, NULL, NULL, '软件工程', '2021-02-10 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (6, 'teacher5', 'e10adc3949ba59abbe56e057f20f883e', 'zhao.prof@university.edu', NULL, '赵教授', 'teacher', NULL, 'active', 'T2024005', '计算机科学与技术学院', NULL, NULL, NULL, '操作系统', '2017-06-01 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (7, 'teacher6', 'e10adc3949ba59abbe56e057f20f883e', 'liu.prof@university.edu', NULL, '刘教授', 'teacher', NULL, 'active', 'T2024006', '网络工程学院', NULL, NULL, NULL, '计算机网络', '2020-04-12 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (8, 'teacher7', 'e10adc3949ba59abbe56e057f20f883e', 'huang.prof@university.edu', NULL, '黄教授', 'teacher', NULL, 'active', 'T2024007', '人工智能学院', NULL, NULL, NULL, '机器学习', '2019-11-05 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (9, 'teacher8', 'e10adc3949ba59abbe56e057f20f883e', 'wu.prof@university.edu', NULL, '吴教授', 'teacher', NULL, 'active', 'T2024008', '计算机科学与技术学院', NULL, NULL, NULL, 'Web开发', '2022-01-08 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (10, 'teacher9', 'e10adc3949ba59abbe56e057f20f883e', 'xu.prof@university.edu', NULL, '徐教授', 'teacher', NULL, 'active', 'T2024009', '信息安全学院', NULL, NULL, NULL, '网络安全', '2018-09-12 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (11, 'teacher10', 'e10adc3949ba59abbe56e057f20f883e', 'sun.prof@university.edu', NULL, '孙教授', 'teacher', NULL, 'active', 'T2024010', '数据科学学院', NULL, NULL, NULL, '大数据技术', '2020-07-20 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (12, 'teacher11', 'e10adc3949ba59abbe56e057f20f883e', 'ma.prof@university.edu', NULL, '马教授', 'teacher', NULL, 'active', 'T2024011', '软件工程学院', NULL, NULL, NULL, '移动开发', '2021-05-14 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (13, 'teacher12', 'e10adc3949ba59abbe56e057f20f883e', 'zhu.prof@university.edu', NULL, '朱教授', 'teacher', NULL, 'active', 'T2024012', '计算机科学与技术学院', NULL, NULL, NULL, '算法设计', '2019-12-03 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (14, 'teacher13', 'e10adc3949ba59abbe56e057f20f883e', 'guo.prof@university.edu', NULL, '郭教授', 'teacher', NULL, 'active', 'T2024013', '人工智能学院', NULL, NULL, NULL, '深度学习', '2020-10-18 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (15, 'teacher14', 'e10adc3949ba59abbe56e057f20f883e', 'he.prof@university.edu', NULL, '何教授', 'teacher', NULL, 'active', 'T2024014', '网络工程学院', NULL, NULL, NULL, '云计算', '2018-04-25 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (16, 'teacher15', 'e10adc3949ba59abbe56e057f20f883e', 'tang.prof@university.edu', NULL, '唐教授', 'teacher', NULL, 'active', 'T2024015', '信息工程学院', NULL, NULL, NULL, '物联网', '2021-08-16 09:00:00', '2025-07-03 09:02:56', '2025-07-03 09:02:56', '2025-07-03 09:02:56');
INSERT INTO `user` VALUES (17, 'student1', 'e10adc3949ba59abbe56e057f20f883e', 'li.student@university.edu', NULL, '李同学', 'student', NULL, 'active', '2024001', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-1班', NULL, '2024-01-01 09:00:00', '2025-07-05 18:48:26', '2025-07-03 09:03:03', '2025-07-05 18:48:26');
INSERT INTO `user` VALUES (18, 'student2', 'e10adc3949ba59abbe56e057f20f883e', 'wang.student1@university.edu', NULL, '王小明', 'student', NULL, 'active', '2024002', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-1班', NULL, '2024-01-01 09:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (19, 'student3', 'e10adc3949ba59abbe56e057f20f883e', 'zhang.student1@university.edu', NULL, '张小红', 'student', NULL, 'active', '2024003', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-1班', NULL, '2024-01-01 10:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (20, 'student4', 'e10adc3949ba59abbe56e057f20f883e', 'chen.student1@university.edu', NULL, '陈小强', 'student', NULL, 'active', '2024004', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-1班', NULL, '2024-01-01 10:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (21, 'student5', 'e10adc3949ba59abbe56e057f20f883e', 'liu.student1@university.edu', NULL, '刘小丽', 'student', NULL, 'active', '2024005', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-1班', NULL, '2024-01-01 11:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (22, 'student6', 'e10adc3949ba59abbe56e057f20f883e', 'zhao.student1@university.edu', NULL, '赵小军', 'student', NULL, 'active', '2024006', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-2班', NULL, '2024-01-01 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (23, 'student7', 'e10adc3949ba59abbe56e057f20f883e', 'huang.student1@university.edu', NULL, '黄小花', 'student', NULL, 'active', '2024007', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-2班', NULL, '2024-01-01 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (24, 'student8', 'e10adc3949ba59abbe56e057f20f883e', 'xu.student1@university.edu', NULL, '徐小东', 'student', NULL, 'active', '2024008', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-2班', NULL, '2024-01-01 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (25, 'student9', 'e10adc3949ba59abbe56e057f20f883e', 'wu.student1@university.edu', NULL, '吴小敏', 'student', NULL, 'active', '2024009', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-2班', NULL, '2024-01-01 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (26, 'student10', 'e10adc3949ba59abbe56e057f20f883e', 'sun.student1@university.edu', NULL, '孙小亮', 'student', NULL, 'active', '2024010', '计算机科学与技术学院', '计算机科学与技术', '2024级', '计科2024-2班', NULL, '2024-01-01 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (27, 'student11', 'e10adc3949ba59abbe56e057f20f883e', 'ma.student1@university.edu', NULL, '马小芳', 'student', NULL, 'active', '2024011', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-1班', NULL, '2023-09-01 09:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (28, 'student12', 'e10adc3949ba59abbe56e057f20f883e', 'guo.student1@university.edu', NULL, '郭小伟', 'student', NULL, 'active', '2024012', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-1班', NULL, '2023-09-01 09:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (29, 'student13', 'e10adc3949ba59abbe56e057f20f883e', 'he.student1@university.edu', NULL, '何小玲', 'student', NULL, 'active', '2024013', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-1班', NULL, '2023-09-01 10:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (30, 'student14', 'e10adc3949ba59abbe56e057f20f883e', 'tang.student1@university.edu', NULL, '唐小刚', 'student', NULL, 'active', '2024014', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-1班', NULL, '2023-09-01 10:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (31, 'student15', 'e10adc3949ba59abbe56e057f20f883e', 'feng.student1@university.edu', NULL, '冯小梅', 'student', NULL, 'active', '2024015', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-1班', NULL, '2023-09-01 11:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (32, 'student16', 'e10adc3949ba59abbe56e057f20f883e', 'deng.student1@university.edu', NULL, '邓小华', 'student', NULL, 'active', '2024016', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-2班', NULL, '2023-09-01 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (33, 'student17', 'e10adc3949ba59abbe56e057f20f883e', 'cao.student1@university.edu', NULL, '曹小玉', 'student', NULL, 'active', '2024017', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-2班', NULL, '2023-09-01 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (34, 'student18', 'e10adc3949ba59abbe56e057f20f883e', 'peng.student1@university.edu', NULL, '彭小龙', 'student', NULL, 'active', '2024018', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-2班', NULL, '2023-09-01 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (35, 'student19', 'e10adc3949ba59abbe56e057f20f883e', 'zeng.student1@university.edu', NULL, '曾小燕', 'student', NULL, 'active', '2024019', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-2班', NULL, '2023-09-01 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (36, 'student20', 'e10adc3949ba59abbe56e057f20f883e', 'xie.student1@university.edu', NULL, '谢小宇', 'student', NULL, 'active', '2024020', '计算机科学与技术学院', '计算机科学与技术', '2023级', '计科2023-2班', NULL, '2023-09-01 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (37, 'student21', 'e10adc3949ba59abbe56e057f20f883e', 'yan.student1@university.edu', NULL, '严小慧', 'student', NULL, 'active', '2024021', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-1班', NULL, '2022-09-01 09:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (38, 'student22', 'e10adc3949ba59abbe56e057f20f883e', 'luo.student1@university.edu', NULL, '罗小峰', 'student', NULL, 'active', '2024022', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-1班', NULL, '2022-09-01 09:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (39, 'student23', 'e10adc3949ba59abbe56e057f20f883e', 'jiang.student1@university.edu', NULL, '江小琳', 'student', NULL, 'active', '2024023', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-1班', NULL, '2022-09-01 10:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (40, 'student24', 'e10adc3949ba59abbe56e057f20f883e', 'han.student1@university.edu', NULL, '韩小杰', 'student', NULL, 'active', '2024024', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-1班', NULL, '2022-09-01 10:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (41, 'student25', 'e10adc3949ba59abbe56e057f20f883e', 'song.student1@university.edu', NULL, '宋小娟', 'student', NULL, 'active', '2024025', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-1班', NULL, '2022-09-01 11:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (42, 'student26', 'e10adc3949ba59abbe56e057f20f883e', 'dai.student1@university.edu', NULL, '戴小松', 'student', NULL, 'active', '2024026', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-2班', NULL, '2022-09-01 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (43, 'student27', 'e10adc3949ba59abbe56e057f20f883e', 'pan.student1@university.edu', NULL, '潘小雨', 'student', NULL, 'active', '2024027', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-2班', NULL, '2022-09-01 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (44, 'student28', 'e10adc3949ba59abbe56e057f20f883e', 'yao.student1@university.edu', NULL, '姚小勇', 'student', NULL, 'active', '2024028', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-2班', NULL, '2022-09-01 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (45, 'student29', 'e10adc3949ba59abbe56e057f20f883e', 'shi.student1@university.edu', NULL, '石小静', 'student', NULL, 'active', '2024029', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-2班', NULL, '2022-09-01 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (46, 'student30', 'e10adc3949ba59abbe56e057f20f883e', 'long.student1@university.edu', NULL, '龙小波', 'student', NULL, 'active', '2024030', '计算机科学与技术学院', '计算机科学与技术', '2022级', '计科2022-2班', NULL, '2022-09-01 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (47, 'student31', 'e10adc3949ba59abbe56e057f20f883e', 'qin.student@university.edu', NULL, '秦小虎', 'student', NULL, 'active', '2024031', '软件工程学院', '软件工程', '2024级', '软工2024-1班', NULL, '2024-01-01 14:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (48, 'student32', 'e10adc3949ba59abbe56e057f20f883e', 'yuan.student@university.edu', NULL, '袁小凤', 'student', NULL, 'active', '2024032', '软件工程学院', '软件工程', '2024级', '软工2024-1班', NULL, '2024-01-01 14:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (49, 'student33', 'e10adc3949ba59abbe56e057f20f883e', 'wei.student@university.edu', NULL, '魏小康', 'student', NULL, 'active', '2024033', '软件工程学院', '软件工程', '2024级', '软工2024-1班', NULL, '2024-01-01 15:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (50, 'student34', 'e10adc3949ba59abbe56e057f20f883e', 'fu.student@university.edu', NULL, '傅小霞', 'student', NULL, 'active', '2024034', '软件工程学院', '软件工程', '2024级', '软工2024-1班', NULL, '2024-01-01 15:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (51, 'student35', 'e10adc3949ba59abbe56e057f20f883e', 'gu.student@university.edu', NULL, '顾小磊', 'student', NULL, 'active', '2024035', '软件工程学院', '软件工程', '2024级', '软工2024-1班', NULL, '2024-01-01 16:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (52, 'student36', 'e10adc3949ba59abbe56e057f20f883e', 'meng.student@university.edu', NULL, '孟小雅', 'student', NULL, 'active', '2024036', '软件工程学院', '软件工程', '2024级', '软工2024-2班', NULL, '2024-01-01 16:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (53, 'student37', 'e10adc3949ba59abbe56e057f20f883e', 'bai.student@university.edu', NULL, '白小鹏', 'student', NULL, 'active', '2024037', '软件工程学院', '软件工程', '2024级', '软工2024-2班', NULL, '2024-01-01 17:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (54, 'student38', 'e10adc3949ba59abbe56e057f20f883e', 'mao.student@university.edu', NULL, '毛小英', 'student', NULL, 'active', '2024038', '软件工程学院', '软件工程', '2024级', '软工2024-2班', NULL, '2024-01-01 17:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (55, 'student39', 'e10adc3949ba59abbe56e057f20f883e', 'fang.student@university.edu', NULL, '方小豪', 'student', NULL, 'active', '2024039', '软件工程学院', '软件工程', '2024级', '软工2024-2班', NULL, '2024-01-01 18:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (56, 'student40', 'e10adc3949ba59abbe56e057f20f883e', 'ren.student@university.edu', NULL, '任小蕾', 'student', NULL, 'active', '2024040', '软件工程学院', '软件工程', '2024级', '软工2024-2班', NULL, '2024-01-01 18:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (57, 'student41', 'e10adc3949ba59abbe56e057f20f883e', 'yu.student@university.edu', NULL, '于小斌', 'student', NULL, 'active', '2024041', '软件工程学院', '软件工程', '2023级', '软工2023-1班', NULL, '2023-09-01 14:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (58, 'student42', 'e10adc3949ba59abbe56e057f20f883e', 'jin.student@university.edu', NULL, '金小婷', 'student', NULL, 'active', '2024042', '软件工程学院', '软件工程', '2023级', '软工2023-1班', NULL, '2023-09-01 14:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (59, 'student43', 'e10adc3949ba59abbe56e057f20f883e', 'zou.student@university.edu', NULL, '邹小飞', 'student', NULL, 'active', '2024043', '软件工程学院', '软件工程', '2023级', '软工2023-1班', NULL, '2023-09-01 15:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (60, 'student44', 'e10adc3949ba59abbe56e057f20f883e', 'tian.student@university.edu', NULL, '田小丹', 'student', NULL, 'active', '2024044', '软件工程学院', '软件工程', '2023级', '软工2023-1班', NULL, '2023-09-01 15:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (61, 'student45', 'e10adc3949ba59abbe56e057f20f883e', 'shao.student@university.edu', NULL, '邵小翔', 'student', NULL, 'active', '2024045', '软件工程学院', '软件工程', '2023级', '软工2023-1班', NULL, '2023-09-01 16:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (62, 'student46', 'e10adc3949ba59abbe56e057f20f883e', 'wan.student@university.edu', NULL, '万小婵', 'student', NULL, 'active', '2024046', '软件工程学院', '软件工程', '2023级', '软工2023-2班', NULL, '2023-09-01 16:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (63, 'student47', 'e10adc3949ba59abbe56e057f20f883e', 'du.student@university.edu', NULL, '杜小江', 'student', NULL, 'active', '2024047', '软件工程学院', '软件工程', '2023级', '软工2023-2班', NULL, '2023-09-01 17:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (64, 'student48', 'e10adc3949ba59abbe56e057f20f883e', 'yi.student@university.edu', NULL, '易小晴', 'student', NULL, 'active', '2024048', '软件工程学院', '软件工程', '2023级', '软工2023-2班', NULL, '2023-09-01 17:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (65, 'student49', 'e10adc3949ba59abbe56e057f20f883e', 'ji.student@university.edu', NULL, '吉小帆', 'student', NULL, 'active', '2024049', '软件工程学院', '软件工程', '2023级', '软工2023-2班', NULL, '2023-09-01 18:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (66, 'student50', 'e10adc3949ba59abbe56e057f20f883e', 'hou.student@university.edu', NULL, '侯小雪', 'student', NULL, 'active', '2024050', '软件工程学院', '软件工程', '2023级', '软工2023-2班', NULL, '2023-09-01 18:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (67, 'student51', 'e10adc3949ba59abbe56e057f20f883e', 'xia.student@university.edu', NULL, '夏小涛', 'student', NULL, 'active', '2024051', '软件工程学院', '软件工程', '2022级', '软工2022-1班', NULL, '2022-09-01 14:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (68, 'student52', 'e10adc3949ba59abbe56e057f20f883e', 'zhong.student@university.edu', NULL, '钟小秋', 'student', NULL, 'active', '2024052', '软件工程学院', '软件工程', '2022级', '软工2022-1班', NULL, '2022-09-01 14:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (69, 'student53', 'e10adc3949ba59abbe56e057f20f883e', 'liang.student@university.edu', NULL, '梁小浩', 'student', NULL, 'active', '2024053', '软件工程学院', '软件工程', '2022级', '软工2022-1班', NULL, '2022-09-01 15:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (70, 'student54', 'e10adc3949ba59abbe56e057f20f883e', 'min.student@university.edu', NULL, '闵小月', 'student', NULL, 'active', '2024054', '软件工程学院', '软件工程', '2022级', '软工2022-1班', NULL, '2022-09-01 15:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (71, 'student55', 'e10adc3949ba59abbe56e057f20f883e', 'chang.student@university.edu', NULL, '常小星', 'student', NULL, 'active', '2024055', '软件工程学院', '软件工程', '2022级', '软工2022-1班', NULL, '2022-09-01 16:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (72, 'student56', 'e10adc3949ba59abbe56e057f20f883e', 'ge.student@university.edu', NULL, '葛小贤', 'student', NULL, 'active', '2024056', '信息工程学院', '信息工程', '2024级', '信工2024-1班', NULL, '2024-01-01 19:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (73, 'student57', 'e10adc3949ba59abbe56e057f20f883e', 'lan.student@university.edu', NULL, '兰小欣', 'student', NULL, 'active', '2024057', '信息工程学院', '信息工程', '2024级', '信工2024-1班', NULL, '2024-01-01 19:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (74, 'student58', 'e10adc3949ba59abbe56e057f20f883e', 'cheng.student@university.edu', NULL, '程小鸿', 'student', NULL, 'active', '2024058', '信息工程学院', '信息工程', '2024级', '信工2024-1班', NULL, '2024-01-01 20:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (75, 'student59', 'e10adc3949ba59abbe56e057f20f883e', 'kuang.student@university.edu', NULL, '邝小倩', 'student', NULL, 'active', '2024059', '信息工程学院', '信息工程', '2024级', '信工2024-1班', NULL, '2024-01-01 20:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (76, 'student60', 'e10adc3949ba59abbe56e057f20f883e', 'lou.student@university.edu', NULL, '娄小昊', 'student', NULL, 'active', '2024060', '信息工程学院', '信息工程', '2024级', '信工2024-1班', NULL, '2024-01-01 21:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (77, 'student61', 'e10adc3949ba59abbe56e057f20f883e', 'cui.student@university.edu', NULL, '崔小艳', 'student', NULL, 'active', '2024061', '信息工程学院', '信息工程', '2023级', '信工2023-1班', NULL, '2023-09-01 19:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (78, 'student62', 'e10adc3949ba59abbe56e057f20f883e', 'bian.student@university.edu', NULL, '卞小宏', 'student', NULL, 'active', '2024062', '信息工程学院', '信息工程', '2023级', '信工2023-1班', NULL, '2023-09-01 19:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (79, 'student63', 'e10adc3949ba59abbe56e057f20f883e', 'huo.student@university.edu', NULL, '霍小洁', 'student', NULL, 'active', '2024063', '信息工程学院', '信息工程', '2023级', '信工2023-1班', NULL, '2023-09-01 20:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (80, 'student64', 'e10adc3949ba59abbe56e057f20f883e', 'kang.student@university.edu', NULL, '康小威', 'student', NULL, 'active', '2024064', '信息工程学院', '信息工程', '2023级', '信工2023-1班', NULL, '2023-09-01 20:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (81, 'student65', 'e10adc3949ba59abbe56e057f20f883e', 'pu.student@university.edu', NULL, '蒲小琪', 'student', NULL, 'active', '2024065', '信息工程学院', '信息工程', '2023级', '信工2023-1班', NULL, '2023-09-01 21:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (82, 'student66', 'e10adc3949ba59abbe56e057f20f883e', 'shen.student@university.edu', NULL, '沈小楠', 'student', NULL, 'active', '2024066', '信息工程学院', '信息工程', '2022级', '信工2022-1班', NULL, '2022-09-01 19:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (83, 'student67', 'e10adc3949ba59abbe56e057f20f883e', 'jia.student@university.edu', NULL, '贾小辉', 'student', NULL, 'active', '2024067', '信息工程学院', '信息工程', '2022级', '信工2022-1班', NULL, '2022-09-01 19:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (84, 'student68', 'e10adc3949ba59abbe56e057f20f883e', 'ning.student@university.edu', NULL, '宁小萍', 'student', NULL, 'active', '2024068', '信息工程学院', '信息工程', '2022级', '信工2022-1班', NULL, '2022-09-01 20:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (85, 'student69', 'e10adc3949ba59abbe56e057f20f883e', 'qu.student@university.edu', NULL, '屈小阳', 'student', NULL, 'active', '2024069', '信息工程学院', '信息工程', '2022级', '信工2022-1班', NULL, '2022-09-01 20:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (86, 'student70', 'e10adc3949ba59abbe56e057f20f883e', 'an.student@university.edu', NULL, '安小云', 'student', NULL, 'active', '2024070', '信息工程学院', '信息工程', '2022级', '信工2022-1班', NULL, '2022-09-01 21:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (87, 'student71', 'e10adc3949ba59abbe56e057f20f883e', 'wen.student@university.edu', NULL, '温小森', 'student', NULL, 'active', '2024071', '信息工程学院', '数据库技术', '2024级', '数据库2024-1班', NULL, '2024-01-02 09:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (88, 'student72', 'e10adc3949ba59abbe56e057f20f883e', 'rong.student@university.edu', NULL, '荣小薇', 'student', NULL, 'active', '2024072', '信息工程学院', '数据库技术', '2024级', '数据库2024-1班', NULL, '2024-01-02 09:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (89, 'student73', 'e10adc3949ba59abbe56e057f20f883e', 'gong.student@university.edu', NULL, '巩小凯', 'student', NULL, 'active', '2024073', '信息工程学院', '数据库技术', '2024级', '数据库2024-1班', NULL, '2024-01-02 10:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (90, 'student74', 'e10adc3949ba59abbe56e057f20f883e', 'sui.student@university.edu', NULL, '隋小桃', 'student', NULL, 'active', '2024074', '信息工程学院', '数据库技术', '2024级', '数据库2024-1班', NULL, '2024-01-02 10:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (91, 'student75', 'e10adc3949ba59abbe56e057f20f883e', 'dong.student@university.edu', NULL, '董小航', 'student', NULL, 'active', '2024075', '信息工程学院', '数据库技术', '2024级', '数据库2024-1班', NULL, '2024-01-02 11:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (92, 'student76', 'e10adc3949ba59abbe56e057f20f883e', 'qiao.student@university.edu', NULL, '乔小羽', 'student', NULL, 'active', '2024076', '网络工程学院', '网络工程', '2024级', '网工2024-1班', NULL, '2024-01-02 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (93, 'student77', 'e10adc3949ba59abbe56e057f20f883e', 'lai.student@university.edu', NULL, '赖小斌', 'student', NULL, 'active', '2024077', '网络工程学院', '网络工程', '2024级', '网工2024-1班', NULL, '2024-01-02 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (94, 'student78', 'e10adc3949ba59abbe56e057f20f883e', 'cai.student@university.edu', NULL, '蔡小岚', 'student', NULL, 'active', '2024078', '网络工程学院', '网络工程', '2024级', '网工2024-1班', NULL, '2024-01-02 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (95, 'student79', 'e10adc3949ba59abbe56e057f20f883e', 'niu.student@university.edu', NULL, '牛小鹏', 'student', NULL, 'active', '2024079', '网络工程学院', '网络工程', '2024级', '网工2024-1班', NULL, '2024-01-02 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (96, 'student80', 'e10adc3949ba59abbe56e057f20f883e', 'bu.student@university.edu', NULL, '卜小娟', 'student', NULL, 'active', '2024080', '网络工程学院', '网络工程', '2024级', '网工2024-1班', NULL, '2024-01-02 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (97, 'student81', 'e10adc3949ba59abbe56e057f20f883e', 'fan.student@university.edu', NULL, '范小晨', 'student', NULL, 'active', '2024081', '网络工程学院', '网络工程', '2023级', '网工2023-1班', NULL, '2023-09-02 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (98, 'student82', 'e10adc3949ba59abbe56e057f20f883e', 'gao.student@university.edu', NULL, '高小慧', 'student', NULL, 'active', '2024082', '网络工程学院', '网络工程', '2023级', '网工2023-1班', NULL, '2023-09-02 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (99, 'student83', 'e10adc3949ba59abbe56e057f20f883e', 'yin.student@university.edu', NULL, '殷小强', 'student', NULL, 'active', '2024083', '网络工程学院', '网络工程', '2023级', '网工2023-1班', NULL, '2023-09-02 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (100, 'student84', 'e10adc3949ba59abbe56e057f20f883e', 'tu.student@university.edu', NULL, '涂小莉', 'student', NULL, 'active', '2024084', '网络工程学院', '网络工程', '2023级', '网工2023-1班', NULL, '2023-09-02 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (101, 'student85', 'e10adc3949ba59abbe56e057f20f883e', 'kong.student@university.edu', NULL, '孔小杰', 'student', NULL, 'active', '2024085', '网络工程学院', '网络工程', '2023级', '网工2023-1班', NULL, '2023-09-02 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (102, 'student86', 'e10adc3949ba59abbe56e057f20f883e', 'mo.student@university.edu', NULL, '莫小丽', 'student', NULL, 'active', '2024086', '网络工程学院', '网络工程', '2022级', '网工2022-1班', NULL, '2022-09-02 11:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (103, 'student87', 'e10adc3949ba59abbe56e057f20f883e', 'bo.student@university.edu', NULL, '薄小勇', 'student', NULL, 'active', '2024087', '网络工程学院', '网络工程', '2022级', '网工2022-1班', NULL, '2022-09-02 12:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (104, 'student88', 'e10adc3949ba59abbe56e057f20f883e', 'sang.student@university.edu', NULL, '桑小芳', 'student', NULL, 'active', '2024088', '网络工程学院', '网络工程', '2022级', '网工2022-1班', NULL, '2022-09-02 12:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (105, 'student89', 'e10adc3949ba59abbe56e057f20f883e', 'lian.student@university.edu', NULL, '连小磊', 'student', NULL, 'active', '2024089', '网络工程学院', '网络工程', '2022级', '网工2022-1班', NULL, '2022-09-02 13:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (106, 'student90', 'e10adc3949ba59abbe56e057f20f883e', 'xi.student@university.edu', NULL, '席小雅', 'student', NULL, 'active', '2024090', '网络工程学院', '网络工程', '2022级', '网工2022-1班', NULL, '2022-09-02 13:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (107, 'student91', 'e10adc3949ba59abbe56e057f20f883e', 'zuo.student@university.edu', NULL, '左小鹤', 'student', NULL, 'active', '2024091', '人工智能学院', '人工智能', '2024级', '人工智能2024-1班', NULL, '2024-01-02 14:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (108, 'student92', 'e10adc3949ba59abbe56e057f20f883e', 'mi.student@university.edu', NULL, '米小曼', 'student', NULL, 'active', '2024092', '人工智能学院', '人工智能', '2024级', '人工智能2024-1班', NULL, '2024-01-02 14:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (109, 'student93', 'e10adc3949ba59abbe56e057f20f883e', 'shui.student@university.edu', NULL, '水小辰', 'student', NULL, 'active', '2024093', '人工智能学院', '人工智能', '2024级', '人工智能2024-1班', NULL, '2024-01-02 15:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (110, 'student94', 'e10adc3949ba59abbe56e057f20f883e', 'mu.student@university.edu', NULL, '穆小晴', 'student', NULL, 'active', '2024094', '人工智能学院', '人工智能', '2024级', '人工智能2024-1班', NULL, '2024-01-02 15:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (111, 'student95', 'e10adc3949ba59abbe56e057f20f883e', 'zha.student@university.edu', NULL, '查小翔', 'student', NULL, 'active', '2024095', '人工智能学院', '人工智能', '2024级', '人工智能2024-1班', NULL, '2024-01-02 16:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (112, 'student96', 'e10adc3949ba59abbe56e057f20f883e', 'shu.student@university.edu', NULL, '舒小慧', 'student', NULL, 'active', '2024096', '人工智能学院', '人工智能', '2023级', '人工智能2023-1班', NULL, '2023-09-02 14:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (113, 'student97', 'e10adc3949ba59abbe56e057f20f883e', 'hua.student@university.edu', NULL, '花小鹏', 'student', NULL, 'active', '2024097', '人工智能学院', '人工智能', '2023级', '人工智能2023-1班', NULL, '2023-09-02 14:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (114, 'student98', 'e10adc3949ba59abbe56e057f20f883e', 'xu.student2@university.edu', NULL, '许小婷', 'student', NULL, 'active', '2024098', '人工智能学院', '人工智能', '2023级', '人工智能2023-1班', NULL, '2023-09-02 15:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (115, 'student99', 'e10adc3949ba59abbe56e057f20f883e', 'hui.student@university.edu', NULL, '慧小杰', 'student', NULL, 'active', '2024099', '人工智能学院', '人工智能', '2023级', '人工智能2023-1班', NULL, '2023-09-02 15:30:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');
INSERT INTO `user` VALUES (116, 'student100', 'e10adc3949ba59abbe56e057f20f883e', 'ze.student@university.edu', NULL, '泽小欣', 'student', NULL, 'active', '2024100', '人工智能学院', '人工智能', '2023级', '人工智能2023-1班', NULL, '2023-09-02 16:00:00', '2025-07-03 09:03:03', '2025-07-03 09:03:03', '2025-07-03 09:03:03');

SET FOREIGN_KEY_CHECKS = 1;
