# ✅ 修复 examConfig.type 错误

## 问题原因
- `examConfig` 是用 `ref()` 定义的响应式对象
- 在模板中访问ref对象的属性时，需要使用 `.value`
- 之前有些地方使用了 `examConfig.type`，有些地方使用了 `examConfig.value.type`
- 导致类型不一致，出现 "Cannot read properties of undefined (reading 'type')" 错误

## ✅ 已修复的地方

### 1. 考核类型下拉框
```vue
<!-- 修复前 -->
<el-select v-model="examConfig.type">

<!-- 修复后 -->
<el-select v-model="examConfig.value.type">
```

### 2. 配置区域标题
```vue
<!-- 修复前 -->
{{ examConfig.type === 'exam' ? '题目配置' : ... }}

<!-- 修复后 -->
{{ examConfig.value.type === 'exam' ? '题目配置' : ... }}
```

### 3. 考试类型条件判断
```vue
<!-- 修复前 -->
<div v-if="examConfig.type === 'exam'">

<!-- 修复后 -->
<div v-if="examConfig.value.type === 'exam'">
```

### 4. 实验指导书条件判断
```vue
<!-- 修复前 -->
<div v-else-if="examConfig.type === 'experiment'">

<!-- 修复后 -->
<div v-else-if="examConfig.value.type === 'experiment'">
```

### 5. 大作业条件判断
```vue
<!-- 修复前 -->
<div v-else-if="examConfig.type === 'assignment'">

<!-- 修复后 -->
<div v-else-if="examConfig.value.type === 'assignment'">
```

### 6. 按钮文本
```vue
<!-- 修复前 -->
{{ examConfig.type === 'exam' ? '下一步：配置题目' : ... }}

<!-- 修复后 -->
{{ examConfig.value.type === 'exam' ? '下一步：配置题目' : ... }}
```

### 7. goToStep 方法
```javascript
// 修复前
if (examConfig.type === 'experiment') {

// 修复后
if (examConfig.value.type === 'experiment') {
```

## 🔧 清除缓存

修复后请清除浏览器缓存：

```javascript
localStorage.removeItem('examConfig')
localStorage.removeItem('generatedExam')
sessionStorage.removeItem('temp_generated_exam')
location.reload()
```

## ✅ 修复后的效果

1. 页面不再报错
2. 考核类型默认显示"考试"
3. 切换类型时，右侧配置区域正确显示
4. 点击按钮时，正确执行对应的操作

## 📝 Vue3 Ref 使用规则

### 在 script 中
```javascript
const examConfig = ref({ type: 'exam' })

// 访问值需要使用 .value
console.log(examConfig.value.type)
examConfig.value.type = 'experiment'
```

### 在 template 中
```vue
<!-- Vue会自动解包ref，但访问嵌套属性时仍需要.value -->
<div>{{ examConfig.value.type }}</div>
<input v-model="examConfig.value.type">
```

## 🎯 现在可以正常使用了！

刷新页面后：
1. 考核类型默认显示"考试"
2. 选择"实验指导书"，显示章节和知识点选择
3. 选择"大作业"，显示作业配置表单
4. 所有功能正常工作

