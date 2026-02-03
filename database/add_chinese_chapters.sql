-- 添加中文章节数据

USE web_experiment;

-- 操作系统 (course_id = 6)
INSERT INTO chapter (course_id, title, description, sort_order) VALUES
(6, '第一章 操作系统概述', '操作系统的基本概念、发展历史和主要功能', 1),
(6, '第二章 进程管理', '进程的概念、进程调度、进程同步与通信', 2),
(6, '第三章 内存管理', '内存分配、虚拟内存、页面置换算法', 3),
(6, '第四章 文件系统', '文件系统的组织、目录结构、文件操作', 4);

-- Linux系统编程 (course_id = 16)
INSERT INTO chapter (course_id, title, description, sort_order) VALUES
(16, '第一章 Linux基础', 'Linux系统介绍、命令行基础、文件系统', 1),
(16, '第二章 Shell编程', 'Shell脚本编写、变量、流程控制', 2),
(16, '第三章 系统编程', '进程管理、文件I/O、信号处理', 3),
(16, '第四章 网络编程', 'Socket编程、TCP/IP协议、网络应用开发', 4);

-- 软件工程实践 (course_id = 21)
INSERT INTO chapter (course_id, title, description, sort_order) VALUES
(21, '第一章 软件工程概论', '软件工程的基本概念、软件生命周期', 1),
(21, '第二章 需求分析', '需求获取、需求分析方法、需求文档编写', 2),
(21, '第三章 系统设计', '概要设计、详细设计、设计模式', 3),
(21, '第四章 软件测试', '测试方法、测试用例设计、自动化测试', 4),
(21, '第五章 项目管理', '项目计划、团队协作、敏捷开发', 5);

