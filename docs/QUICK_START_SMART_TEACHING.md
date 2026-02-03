# 🚀 教学工具箱 - 5分钟快速启动

> 最简化的启动指南，让你快速运行系统

---

## 📦 第一步：导入数据库（1分钟）

```bash
cd d:\Desktop\CSdesign\database
init_smart_teaching_v2.bat
```

输入MySQL密码：`123456`

**预期结果**：
```
✅ 数据库初始化成功！
已创建3个数据表
```

---

## 🔧 第二步：启动后端（1分钟）

```bash
cd d:\Desktop\CSdesign\back
mvn spring-boot:run
```

**预期结果**：
```
Started WebExperimentApplication
后端运行在 http://localhost:8080
```

---

## 🎨 第三步：启动前端（1分钟）

```bash
cd d:\Desktop\CSdesign\front(2)\front(1)
npm run dev
```

**预期结果**：
```
➜  Local:   http://localhost:5173/
```

---

## 🎯 第四步：登录使用（2分钟）

1. **打开浏览器**：http://localhost:5173
2. **登录账号**：teacher1 / 123456
3. **点击菜单**：教学工具箱

---

## ✨ 三大功能

### 1. 📝 课件更新助手
```
选择课程 → 上传PPT → 输入更新要求 → 查看AI建议
```

### 2. 🔬 实验指导书生成
```
选择课程 → 输入章节 → 添加知识点 → 生成指导书
```

### 3. ✍️ 主观题批阅
```
选择学生 → 提交作业 → AI批阅 → 教师审阅
```

---

## 🐛 常见问题

### 数据库初始化失败
- 检查MySQL密码
- 确认web_experiment数据库存在

### 后端启动失败
- 检查8080端口是否被占用
- 确认数据库连接正常

### 前端无法访问
- 确认前端服务已启动
- 检查5173端口

---

## 📚 详细文档

需要更多信息？查看：
- `TEACHING_TOOLBOX_GUIDE.md` - 完整使用指南
- `CONFLICT_ANALYSIS.md` - 功能冲突分析

---

**就这么简单！** 🎉
