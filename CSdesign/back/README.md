# 智能教学管理系统 - 后端

## 项目概述

这是一个基于Spring Boot的智能教学管理系统后端，支持教师、学生和管理员三种角色的功能。

## 技术栈

- Spring Boot 2.7.0
- MyBatis
- MySQL 8.0
- Maven

## 功能模块

### 1. 用户管理模块
- 用户注册、登录、修改密码
- 支持三种角色：管理员(admin)、教师(teacher)、学生(student)
- 用户信息管理

### 2. 课程管理模块
- 课程创建、编辑、删除
- 章节和课时管理
- 课程状态管理（草稿、发布、归档）

### 3. 考试管理模块（新增）
- 考试创建和管理
- 智能考试生成
- 题目管理（单选、多选、判断、填空、简答）
- 题目选项管理
- 考试状态管理（草稿、发布、归档）

### 4. 学生考试模块（新增）
- 学生参加考试
- 考试答案提交
- 考试成绩记录
- 教师评语功能

### 5. 练习管理模块（新增）
- 练习类型管理（算法、编程、系统设计、数据库）
- 练习难度分级（简单、中等、困难）
- 学生练习记录
- 练习成绩统计

### 6. 学习进度管理模块（新增）
- 学生学习进度跟踪
- 课程完成率统计
- 学习时长记录
- 学习状态管理

### 7. 统计分析模块（新增）
- 教师统计信息（学生数、课程数、平均分、教学时长等）
- 学生统计信息（学习时长、完成课程、平均成绩、连续学习天数等）
- 系统统计信息（用户数、活跃度、效率指数等）
- 成绩分布分析
- 错误知识点统计
- 学习趋势分析

## 数据库设计

### 核心表结构

1. **user** - 用户表
2. **course** - 课程表
3. **chapter** - 章节表
4. **lesson** - 课时表
5. **exam** - 考试表（新增）
6. **question** - 题目表（新增）
7. **question_option** - 题目选项表（新增）
8. **student_exam** - 学生考试记录表（新增）
9. **student_answer** - 学生答案表（新增）
10. **practice** - 练习表（新增）
11. **student_practice** - 学生练习记录表（新增）
12. **student_progress** - 学生学习进度表（新增）

## API接口

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `PUT /api/user/change-pwd` - 修改密码
- `GET /api/user/page` - 分页查询用户
- `PUT /api/user` - 更新用户信息
- `DELETE /api/user/{id}` - 删除用户

### 课程相关
- `POST /api/course` - 创建课程
- `GET /api/course/{id}` - 获取课程详情
- `GET /api/course/page` - 分页查询课程
- `PUT /api/course` - 更新课程
- `DELETE /api/course/{id}` - 删除课程

### 考试相关（新增）
- `POST /api/exam` - 创建考试
- `GET /api/exam/{id}` - 获取考试详情
- `GET /api/exam/teacher/{teacherId}` - 获取教师考试列表
- `GET /api/exam/course/{courseId}` - 获取课程考试列表
- `GET /api/exam/page` - 分页查询考试
- `PUT /api/exam` - 更新考试
- `DELETE /api/exam/{id}` - 删除考试
- `PUT /api/exam/{id}/publish` - 发布考试
- `PUT /api/exam/{id}/archive` - 归档考试
- `POST /api/exam/generate` - 智能生成考试

### 统计相关（新增）
- `GET /api/stats/teacher/{teacherId}` - 获取教师统计信息
- `GET /api/stats/student/{studentId}` - 获取学生统计信息
- `GET /api/stats/system` - 获取系统统计信息
- `GET /api/stats/grade-distribution` - 获取成绩分布
- `GET /api/stats/error-topics` - 获取错误知识点统计
- `GET /api/stats/learning-trend/{studentId}` - 获取学习趋势

### 练习相关（新增）
- `GET /api/practice/types` - 获取练习类型列表
- `GET /api/practice/recommended` - 获取推荐练习
- `GET /api/practice/history/{studentId}` - 获取练习历史
- `POST /api/practice/start` - 开始练习
- `POST /api/practice/submit` - 提交练习答案

## 运行说明

1. 确保已安装MySQL 8.0
2. 执行 `src/main/resources/sql/init.sql` 创建数据库和表
3. 修改 `application.yml` 中的数据库连接配置
4. 运行 `WebExperimentApplication.java`

## 默认账号

- 管理员：admin / 123456
- 教师：teacher1 / 123456, teacher2 / 123456
- 学生：student1 / 123456, student2 / 123456

## 项目结构

```
src/main/java/com/experiment/
├── config/           # 配置类
├── controller/       # 控制器
├── mapper/          # MyBatis映射接口
├── pojo/            # 实体类和DTO
├── result/          # 结果封装类
├── service/         # 服务接口
│   └── Impl/        # 服务实现类
└── utils/           # 工具类
```

## 新增功能特点

1. **智能考试生成**：支持根据课程和知识点自动生成考试题目
2. **多维度统计分析**：提供教师、学生、系统三个维度的详细统计
3. **学习进度跟踪**：实时跟踪学生学习进度和时长
4. **练习系统**：支持多种类型的编程练习和评测
5. **答案检测分析**：自动检测学生答案错误并提供分析
6. **个性化建议**：基于学习数据提供个性化学习建议

## 注意事项

- 目前使用模拟数据，实际部署时需要连接真实数据库
- 密码使用BCrypt加密，默认密码为123456
- 所有接口都支持跨域访问
- 建议在生产环境中添加适当的权限控制和数据验证 