# 🚀 教学工具箱 - 快速测试指南

## ✅ 问题已解决

**问题**: ChapterMapper 方法重复定义
- XML文件中定义了 `findByCourseId`
- Java接口中也用 `@Select` 注解定义了 `findByCourseId`

**解决方案**: 删除了 `ChapterMapper.xml` 文件，统一使用注解方式

**编译状态**: ✅ BUILD SUCCESS

---

## 🎯 现在可以测试的功能

### 1. 课件更新助手
**流程**:
```
登录教师账号 → 教学工具箱 → 课件更新助手
→ 选择课程（自动从数据库加载）
→ 上传PPT文件或输入文件名
→ 输入更新要求
→ 提交 → AI分析中
```

**测试数据**:
- 课程：数据结构
- 文件名：数据结构第三章.pptx
- 更新要求：
  ```
  1. 更新2024-2025年最新的算法应用案例
  2. 补充AI相关的数据结构应用
  3. 更新性能对比图表
  ```

### 2. 实验指导书生成 ⭐ 重点测试
**流程**:
```
登录教师账号 → 教学工具箱 → 实验指导书生成
→ 选择课程（自动从数据库加载）
→ 选择章节（自动从数据库加载该课程的章节）✨
→ 添加知识点标签
→ 选择难度等级
→ 提交 → AI生成中
```

**测试数据**:
- 课程：数据结构
- 章节：第三章 栈和队列（从数据库自动加载）
- 知识点：
  - 栈的定义与实现
  - 栈的应用场景
  - 队列的定义与实现
  - 循环队列
- 难度：中等

**关键验证点**:
- ✅ 选择课程后，章节下拉框自动填充
- ✅ 章节数据来自数据库 `chapter` 表
- ✅ 章节按 `sort_order` 排序显示
- ✅ 如果课程无章节，显示友好提示

### 3. 主观题批阅助手
**流程**:
```
登录教师账号 → 教学工具箱 → 主观题批阅助手
→ 选择课程（自动从数据库加载）
→ 选择学生（自动从数据库加载，支持搜索）
→ 输入作业标题和内容
→ 可选：上传作业文件
→ 提交 → AI批阅 → 教师审阅
```

**测试数据**:
- 课程：数据结构
- 学生：张三（学号：2021001）
- 作业标题：数据结构第三章学习总结
- 作业内容：
  ```
  请论述栈和队列的区别与联系，并举例说明它们在实际开发中的应用场景。
  
  要求：
  1. 从数据结构特点分析
  2. 从操作方式对比
  3. 列举至少2个实际应用案例
  ```

---

## 🔧 启动步骤

### 1. 确保数据库已初始化
```bash
cd d:\Desktop\CSdesign\database
mysql -u root -p123456 < ai_teaching_assistant_v2.sql
```

**验证数据库**:
```sql
USE web_experiment;

-- 查看3个核心表
SHOW TABLES LIKE '%courseware%';
SHOW TABLES LIKE '%experiment%';
SHOW TABLES LIKE '%homework%';

-- 查看章节数据
SELECT * FROM chapter LIMIT 5;
```

### 2. 启动后端
```bash
cd d:\Desktop\CSdesign\back
mvn spring-boot:run
```

**验证启动成功**:
- 看到 "Started WebExperimentApplication"
- 访问: http://localhost:8080

### 3. 启动前端
```bash
cd d:\Desktop\CSdesign\front(2)\front(1)
npm run dev
```

**验证启动成功**:
- 看到 "Local: http://localhost:5173"
- 浏览器访问: http://localhost:5173

---

## 🧪 测试检查清单

### 前端测试
- [ ] 登录教师账号成功
- [ ] 进入"教学工具箱"页面
- [ ] 看到3个功能卡片

### 课件更新助手
- [ ] 点击卡片打开对话框
- [ ] 课程下拉框显示教师的课程
- [ ] 可以选择或上传PPT文件
- [ ] 可以输入更新要求
- [ ] 提交成功，显示"AI正在分析中"

### 实验指导书生成 ⭐
- [ ] 点击卡片打开对话框
- [ ] 课程下拉框显示教师的课程
- [ ] **选择课程后，章节下拉框自动填充**
- [ ] **章节列表来自数据库**
- [ ] 可以添加/删除知识点标签
- [ ] 可以选择难度等级
- [ ] 提交成功，显示"AI正在生成"

### 主观题批阅助手
- [ ] 点击卡片打开对话框
- [ ] 课程下拉框显示教师的课程
- [ ] 学生下拉框显示所有学生（支持搜索）
- [ ] 可以输入作业标题和内容
- [ ] 可以上传作业文件
- [ ] 提交成功

### 后端API测试
使用浏览器或Postman测试：

**获取章节列表**:
```
GET http://localhost:8080/api/smart-teaching/chapters/1
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "获取成功",
  "data": [
    {
      "id": 1,
      "courseId": 1,
      "title": "第一章 绪论",
      "description": "...",
      "sortOrder": 1
    },
    {
      "id": 2,
      "courseId": 1,
      "title": "第二章 线性表",
      "description": "...",
      "sortOrder": 2
    }
  ]
}
```

---

## 🐛 常见问题

### 问题1: 后端启动失败 - ChapterMapper重复定义
**原因**: XML文件和注解方式冲突
**解决**: 已删除 `ChapterMapper.xml` 文件 ✅

### 问题2: 章节下拉框为空
**原因**: 
1. 数据库中该课程没有章节数据
2. API调用失败

**解决**:
```sql
-- 检查章节数据
SELECT * FROM chapter WHERE course_id = 1;

-- 如果没有数据，插入测试数据
INSERT INTO chapter (course_id, title, description, sort_order) VALUES
(1, '第一章 绪论', '数据结构基本概念', 1),
(1, '第二章 线性表', '线性表的定义与实现', 2),
(1, '第三章 栈和队列', '栈和队列的应用', 3);
```

### 问题3: 前端显示"该课程暂无章节"
**这是正常的！** 说明功能正常工作，只是该课程确实没有章节数据。

**解决**: 在课程管理中添加章节，或使用上面的SQL插入测试数据。

### 问题4: AI生成失败
**原因**: 
1. 阿里云API密钥未配置
2. 网络连接问题

**解决**:
检查 `application-piston.yml` 中的配置：
```yaml
alibaba:
  ai:
    api-key: your-api-key-here
```

---

## 📊 数据库表结构

### chapter 表（已存在）
```sql
CREATE TABLE chapter (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  sort_order INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_course_id (course_id)
);
```

### courseware_update 表（新建）
```sql
CREATE TABLE courseware_update (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  course_id BIGINT,
  file_name VARCHAR(200),
  original_file_url VARCHAR(500),
  update_requirements TEXT,
  update_suggestions TEXT,
  status VARCHAR(20) DEFAULT 'processing',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### experiment_guide_generation 表（新建）
```sql
CREATE TABLE experiment_guide_generation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  course_id BIGINT,
  chapter_name VARCHAR(200),
  knowledge_points TEXT,
  difficulty_level VARCHAR(20),
  guide_content TEXT,
  status VARCHAR(20) DEFAULT 'generating',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### homework_grading 表（新建）
```sql
CREATE TABLE homework_grading (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  course_id BIGINT,
  homework_title VARCHAR(200),
  file_url VARCHAR(500),
  file_name VARCHAR(200),
  content TEXT,
  ai_score DECIMAL(5,2),
  ai_comments TEXT,
  teacher_score DECIMAL(5,2),
  teacher_comments TEXT,
  status VARCHAR(20) DEFAULT 'submitted',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🎉 测试成功标志

当你看到以下情况，说明功能完全正常：

1. ✅ 后端启动无错误
2. ✅ 前端启动无错误
3. ✅ 登录成功
4. ✅ 教学工具箱页面显示3个卡片
5. ✅ **选择课程后章节自动加载**（重点）
6. ✅ 可以提交各种任务
7. ✅ 数据库中有对应的记录

---

## 📝 答辩演示建议

### 演示顺序
1. **展示系统架构** - 前后端分离、数据库设计
2. **演示课件更新** - 展示文件上传和AI分析
3. **演示实验指导书生成** - **重点展示章节自动加载功能**
4. **演示作业批阅** - 展示AI批阅和教师审阅流程
5. **展示数据库记录** - 证明数据已保存

### 重点强调
- ✨ **完全自动化** - 课程、章节、学生信息全部从数据库自动加载
- ✨ **级联加载** - 选择课程后自动加载该课程的章节
- ✨ **AI深度集成** - 3个功能都使用阿里云通义千问API
- ✨ **异步处理** - AI任务异步执行，不阻塞用户操作
- ✨ **人机协作** - AI批阅后教师可以修改

---

## 🚀 现在就开始测试吧！

1. 启动后端（已编译成功）
2. 启动前端
3. 登录教师账号
4. 进入教学工具箱
5. 测试3个功能

**祝测试顺利！** 🎊

