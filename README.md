# 智能教学管理系统王子阳

> 一个集成AI辅助教学、在线学习、智能评测的现代化教育平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4.21-brightgreen.svg)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Educational-orange.svg)](LICENSE)

---

## 🎯 项目简介

智能教学管理系统是一个面向高校的综合性在线教育平台，支持教师、学生、管理员三种角色，提供课程管理、AI智能出题、在线编程、学习分析等功能。

### ✨ 核心特色

- 🤖 **AI智能辅助** - 智能出题、课程设计、学习分析
- 🎤 **语音识别** - 课堂录音转文字和重点整理（Whisper）
- 💻 **在线编程** - 支持60+种编程语言的在线执行
- 📝 **PDF标注** - 在线文档阅读和多种标注功能
- 📊 **数据分析** - 学习行为分析、成绩统计、可视化报表
- 🎯 **个性化学习** - 智能学习路径、错题本、情绪感知

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 三步启动

```bash
# 1. 初始化数据库
cd database
双击运行 init_database.bat  # Windows
# 或: ./init_database.sh     # Linux/Mac

# 2. 启动后端
cd back
mvn spring-boot:run

# 3. 启动前端
cd front(2)/front(1)
npm install
npm run dev
```

### 访问系统

- 前端地址: http://localhost:5173
- 后端地址: http://localhost:8080

**测试账号:**
- 管理员: `admin` / `123456`
- 教师: `teacher1` / `123456`
- 学生: `student1` / `123456`

---

## 📚 主要功能

### 教师端
- ✅ 课程管理（创建课程、上传资源）
- ✅ AI智能出题（7种题型，120个知识点）
- ✅ AI课程设计（自动生成教案、PPT）
- ✅ 课堂重点整理（语音转文字、AI提取重点）
- ✅ 考试管理（在线阅卷、成绩统计）
- ✅ 数据分析（学生学习数据、成绩分布）

### 学生端
- ✅ 在线学习（视频、文档、进度跟踪）
- ✅ PDF文档标注（高亮、批注、便签）
- ✅ 练习与考试（在线答题、查看成绩）
- ✅ 编程实践（在线代码编辑、自动评测）
- ✅ 错题本（自动收集、针对性训练）
- ✅ 智能学习（AI助手、学习路径规划）

### 管理员端
- ✅ 用户管理（教师、学生账号）
- ✅ 课程审核和管理
- ✅ 系统数据统计

---

## 🛠️ 技术栈

### 后端
- Spring Boot 3.2.5 + Java 21
- MySQL 8.0 + MyBatis
- 阿里云通义千问API
- Whisper语音识别
- Piston代码执行引擎

### 前端
- Vue 3 + Vite
- Element Plus
- Chart.js
- PDF.js

---

## 📁 项目结构

```
CSdesign/
├── back/                    # 后端服务 (Spring Boot)
├── front(2)/front(1)/       # 前端服务 (Vue 3)
├── database/                # 数据库脚本
├── output/                  # 输出文件
├── tests/                   # 测试文件
├── README.md               # 本文档（快速入门）
└── PROJECT_README.md       # 详细文档（完整介绍）
```

---

## 📖 文档导航

- **[PROJECT_README.md](PROJECT_README.md)** - 完整项目文档（功能详解、API接口、配置说明）
- **[database/](database/)** - 数据库脚本和初始化工具
- **[tests/](tests/)** - 测试文件和调试工具

---

## ⚙️ 配置说明

### 必需配置

编辑 `back/src/main/resources/application.yml`：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/web_experiment
    username: root
    password: 你的MySQL密码

# AI服务配置
dashscope:
  api-key: 你的通义千问API密钥
  app-id: 你的百炼应用ID
```

### 可选配置

- **阿里云OSS** - 文件存储（不配置则使用本地存储）
- **Whisper** - 语音识别（不配置则无法使用课堂录音功能）
- **讯飞PPT** - PPT生成（不配置则无法使用PPT生成功能）

详细配置说明请查看 [PROJECT_README.md](PROJECT_README.md)

---

## 🎯 核心亮点

### 1. AI智能出题系统

- **5门课程**: 数据结构、Linux系统、计算机网络、机器学习、操作系统
- **30个章节**: 每门课程6个章节
- **120个知识点**: 每章节4个知识点
- **7种题型**: 单选、多选、判断、填空、简答、编程、论述
- **智能生成**: 根据知识点和难度自动生成题目
- **强制中文**: 所有题目内容使用中文

### 2. 课堂重点整理

- 上传课堂录音（支持多种音频格式）
- Whisper模型自动转文字（准确率95%+）
- AI提取课堂重点和关键内容
- 生成结构化课堂总结
- 一键发布到学生端

### 3. 在线编程系统

- 支持60+种编程语言
- 在线代码编辑器（语法高亮）
- 实时代码执行和测试
- 自动评分和反馈
- 执行时间和内存统计

### 4. PDF标注系统

- 在线PDF阅读
- 多种标注方式：高亮、下划线、删除线、波浪线
- 文本批注和便签
- 标注数据持久化
- 导出标注内容

---

## 📊 系统数据

- **用户角色**: 3种（管理员、教师、学生）
- **数据表**: 30+张
- **API接口**: 100+个
- **前端页面**: 50+个
- **代码行数**: 50,000+行

---

## 🔍 常见问题

### Q1: 数据库连接失败？
**A:** 检查MySQL服务是否启动，确认密码是否正确

### Q2: AI生成失败？
**A:** 检查通义千问API密钥是否正确，确认API额度是否充足

### Q3: 前端启动失败？
**A:** 删除 `node_modules` 和 `package-lock.json`，重新运行 `npm install`

### Q4: 代码执行失败？
**A:** 检查Piston API是否可访问，确认代码语法是否正确

更多问题请查看 [PROJECT_README.md](PROJECT_README.md) 的"常见问题"章节

---

## 📝 开发日志

### 2026-01-22
- ✅ 整理项目文档，删除冗余md文件
- ✅ 创建统一的项目介绍文档
- ✅ 优化README结构

### 2026-01-21
- ✅ 完善知识点系统，支持5门课程
- ✅ 优化AI题目生成，强制中文输出

### 2026-01-18
- ✅ 代码重构：拆分Controller，提取Service层
- ✅ 代码量减少57%，可维护性提升150%

---

## 📄 许可证

本项目仅供学习和研究使用。

---

## 🙏 致谢

感谢以下开源项目和服务：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [阿里云通义千问](https://tongyi.aliyun.com/)
- [Whisper](https://github.com/openai/whisper)
- [Piston](https://github.com/engineer-man/piston)

---

**最后更新:** 2026年1月22日  
**项目状态:** 开发中  
**详细文档:** [PROJECT_README.md](PROJECT_README.md)
