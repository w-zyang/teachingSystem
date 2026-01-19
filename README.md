# 智能教学管理系统

## 项目简介

智能教学管理系统是一个面向大学生的综合性在线学习平台，提供多专业课程学习、智能教学管理服务，让学习更高效、更智能。

## 主要功能

### 学生端
- **在线学习**：浏览课程资源、观看视频、阅读文档
- **练习评估**：完成练习题、编程实践、查看成绩
- **智能学习**：AI学习助手、个性化学习路径规划、情感感知学习
- **错题训练**：智能错题分析、针对性训练
- **课堂重点**：AI自动整理课堂重点内容
- **VR教室**：沉浸式虚拟现实学习体验
- **学习笔记**：在线笔记、PDF标注功能

### 教师端
- **课程管理**：创建课程、上传资源、管理课程内容
- **AI课程设计**：智能生成课程大纲和教学内容
- **学生管理**：查看学生学习情况、成绩分析
- **试卷生成**：AI智能生成试卷
- **课堂总结**：自动生成课堂总结报告
- **数据分析**：学生学习数据可视化分析

### 管理员端
- **用户管理**：管理教师和学生账号
- **资源管理**：管理系统资源和课程
- **数据统计**：系统使用情况统计和分析

## 技术栈

### 后端
- **框架**：Spring Boot 2.7.x
- **数据库**：MySQL 8.0
- **ORM**：MyBatis
- **安全**：Spring Security + JWT
- **API文档**：Swagger/OpenAPI
- **代码执行引擎**：Piston API

### 前端
- **框架**：Vue 3
- **构建工具**：Vite
- **UI组件**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **富文本编辑器**：TinyMCE
- **PDF查看器**：PDF.js

### AI集成
- **大语言模型**：通义千问 API
- **功能**：智能问答、课程设计、试卷生成、课堂总结

## 项目结构

```
CSdesign/
├── back/                      # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/experiment/
│   │   │   │       ├── controller/    # 控制器层
│   │   │   │       ├── service/       # 服务层
│   │   │   │       ├── mapper/        # 数据访问层
│   │   │   │       ├── entity/        # 实体类
│   │   │   │       ├── config/        # 配置类
│   │   │   │       └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/            # MyBatis映射文件
│   │   │       └── application.yml    # 配置文件
│   │   └── test/                      # 测试代码
│   └── pom.xml                        # Maven配置
│
├── front(2)/front(1)/         # 前端项目
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   │   ├── student/       # 学生端页面
│   │   │   ├── teacher/       # 教师端页面
│   │   │   ├── admin/         # 管理员端页面
│   │   │   ├── auth/          # 认证页面
│   │   │   └── common/        # 公共页面
│   │   ├── components/        # 公共组件
│   │   ├── api/               # API接口
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── assets/            # 静态资源
│   ├── public/                # 公共资源
│   └── package.json           # npm配置
│
├── database/                  # 数据库脚本
│   ├── complete_database.sql  # 完整数据库脚本
│   └── *.sql                  # 其他SQL脚本
│
├── docs/                      # 文档
├── tests/                     # 测试文件
└── README.md                  # 项目说明
```

## 快速开始

### 环境要求
- JDK 11+
- MySQL 8.0+
- Node.js 16+
- Maven 3.6+

### 后端启动

1. 创建数据库并导入数据
```bash
cd database
mysql -u root -p < complete_database.sql
```

2. 配置数据库连接
编辑 `back/src/main/resources/application.yml`，配置数据库连接信息

3. 启动后端服务
```bash
cd back
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 安装依赖
```bash
cd front(2)/front(1)
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

## 核心特性

### 1. 智能学习助手
- AI驱动的智能问答系统
- 个性化学习建议
- 学习路径规划
- 情感感知学习

### 2. 编程实践
- 支持多种编程语言（Python, Java, C++, JavaScript等）
- 在线代码编辑器
- 实时代码执行和测试
- 自动评分系统

### 3. PDF标注系统
- 在线PDF阅读
- 文本标注和高亮
- 笔记关联
- 标注数据持久化

### 4. VR虚拟教室
- 沉浸式学习体验
- 3D场景交互
- 虚拟实验室

### 5. 数据分析
- 学习行为分析
- 成绩趋势分析
- 教学效果评估
- 可视化报表

## API文档

启动后端服务后，访问 Swagger API文档：
```
http://localhost:8080/swagger-ui.html
```

## 配置说明

### 通义千问API配置
在 `application.yml` 中配置：
```yaml
qianwen:
  api-key: your-api-key
  base-url: https://dashscope.aliyuncs.com/api/v1
```

### Piston代码执行引擎
在 `application.yml` 中配置：
```yaml
piston:
  api-url: https://emkc.org/api/v2/piston
```

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

© 2026 智能教学管理系统. 保留所有权利.

## 联系方式

如有问题或建议，请联系：
- Email: support@example.com
- 电话: 400-123-4567
