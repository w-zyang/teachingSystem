-- 清理旧的标注数据脚本
-- 用于删除使用旧格式（规范化文本）保存的标注

-- 1. 查看当前标注数据
SELECT 
    id,
    student_id,
    resource_id,
    type,
    selected_text,
    LENGTH(selected_text) as text_length,
    position,
    create_time
FROM student_annotation
WHERE resource_id = 38  -- 替换为你的资源ID
ORDER BY create_time DESC;

-- 2. 备份标注数据（可选）
CREATE TABLE IF NOT EXISTS student_annotation_backup_20260118 AS
SELECT * FROM student_annotation;

-- 3. 删除特定资源的所有标注（谨慎操作！）
-- 取消下面的注释来执行删除
-- DELETE FROM student_annotation WHERE resource_id = 38;

-- 4. 删除所有标注（非常谨慎！）
-- 取消下面的注释来执行删除
-- DELETE FROM student_annotation;

-- 5. 查看删除后的结果
SELECT COUNT(*) as remaining_annotations 
FROM student_annotation 
WHERE resource_id = 38;

-- 6. 如果需要恢复数据
-- INSERT INTO student_annotation 
-- SELECT * FROM student_annotation_backup_20260118;
