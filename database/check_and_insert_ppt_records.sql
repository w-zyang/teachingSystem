-- 检查和插入PPT生成记录

-- 1. 查看ai_ppt_generation表结构
DESC ai_ppt_generation;

-- 2. 查看当前所有记录
SELECT * FROM ai_ppt_generation;

-- 3. 查看教师ID为2的记录
SELECT * FROM ai_ppt_generation WHERE teacher_id = 2;

-- 4. 查看最近生成的记录
SELECT * FROM ai_ppt_generation ORDER BY create_time DESC LIMIT 10;

-- 5. 如果表为空或没有你的记录，插入测试数据
INSERT INTO ai_ppt_generation (
    teacher_id, 
    course_id, 
    topic, 
    keywords, 
    slide_count, 
    style, 
    status, 
    content,
    ppt_url,
    create_time
)
VALUES 
(2, 1, 'MCP技术详解', 'MCP, 技术架构, 应用场景', 15, 'professional', 'completed', 
 '{"title":"MCP技术详解","slides":[{"title":"封面","content":"MCP技术详解"}]}',
 'http://localhost:8080/uploads/ppt/mcp-tech.pptx',
 NOW()),
 
(2, 1, '信息安全基础', '网络安全, 加密技术, 防火墙', 20, 'academic', 'completed', 
 '{"title":"信息安全基础","slides":[{"title":"封面","content":"信息安全基础"}]}',
 'http://localhost:8080/uploads/ppt/security-basics.pptx',
 NOW()),
 
(2, 2, 'AI与机器学习', '人工智能, 深度学习, 神经网络', 25, 'creative', 'generating', 
 '{"title":"AI与机器学习","slides":[{"title":"封面","content":"AI与机器学习"}]}',
 NULL,
 NOW());

-- 6. 再次查看插入后的记录
SELECT * FROM ai_ppt_generation WHERE teacher_id = 2 ORDER BY create_time DESC;

-- 7. 统计记录数
SELECT 
    teacher_id,
    COUNT(*) as total_records,
    SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed,
    SUM(CASE WHEN status = 'generating' THEN 1 ELSE 0 END) as generating,
    SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) as failed
FROM ai_ppt_generation
GROUP BY teacher_id;









