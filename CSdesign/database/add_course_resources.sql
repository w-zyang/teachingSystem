USE web_experiment;

-- 为课程1（数据结构与算法）添加资源
INSERT INTO course_resource (course_id, teacher_id, title, description, file_name, file_url, file_type, file_size, download_count, status, create_time, update_time) VALUES
(1, 1, '数据结构基础教程', '涵盖线性表、栈、队列、树、图等基本数据结构，以及常见算法的时间复杂度分析', '数据结构基础.txt', '/uploads/course1/数据结构基础.txt', 'text', 45678, 0, 'published', NOW(), NOW()),
(1, 1, '算法分析与设计', '详细讲解分治法、动态规划、贪心算法、回溯法等算法设计策略，包含大量经典算法问题', '算法分析与设计.txt', '/uploads/course1/算法分析与设计.txt', 'text', 38456, 0, 'published', NOW(), NOW());

-- 为课程6（计算机网络）添加资源
INSERT INTO course_resource (course_id, teacher_id, title, description, file_name, file_url, file_type, file_size, download_count, status, create_time, update_time) VALUES
(6, 1, '计算机网络基础教程', '系统讲解计算机网络的基本概念、OSI模型、TCP/IP协议族、网络设备等核心知识', '计算机网络基础.txt', '/uploads/course6/计算机网络基础.txt', 'text', 52341, 0, 'published', NOW(), NOW()),
(6, 1, '网络协议详解', '深入剖析TCP、UDP、IP、HTTP、DNS等重要网络协议的工作原理和报文格式', '网络协议详解.txt', '/uploads/course6/网络协议详解.txt', 'text', 48923, 0, 'published', NOW(), NOW());

-- 查看插入结果
SELECT id, course_id, title, file_name, file_type, status FROM course_resource WHERE course_id IN (1, 6) ORDER BY course_id, id;

