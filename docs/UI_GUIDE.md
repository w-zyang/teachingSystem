# 🎨 UI优化使用指南

## 快速开始

### 1️⃣ 导入演示数据

**Windows用户**:
```bash
cd database
双击运行 import_demo_data.bat
# 输入MySQL密码后自动导入
```

**Mac/Linux用户**:
```bash
cd database
mysql -u root -p web_experiment < demo_data.sql
```

### 2️⃣ 启动项目

```bash
# 终端1 - 启动后端
cd back
mvn spring-boot:run

# 终端2 - 启动前端
cd front(2)/front(1)
npm run dev
```

### 3️⃣ 访问系统

打开浏览器访问: http://localhost:5173

**测试账号**:
- 管理员: `admin` / `123456`
- 教师: `teacher1` / `123456`
- 学生: `student1` / `123456`

---

## 🎨 如何使用新的UI主题

### 方法一：在组件中引入主题

```vue
<style lang="scss" scoped>
@import '@/assets/styles/theme.scss';

.my-component {
  background: $bg-primary;
  color: $text-primary;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  box-shadow: $shadow-md;
  
  &:hover {
    box-shadow: $shadow-xl;
    transform: translateY(-4px);
  }
}
</style>
```

### 方法二：使用混合宏

```vue
<style lang="scss" scoped>
@import '@/assets/styles/theme.scss';

.my-card {
  @include card-style;
}

.my-button {
  @include button-primary;
}

.my-title {
  @include text-gradient($primary-gradient);
}
</style>
```

### 方法三：使用工具类

```vue
<template>
  <div class="flex-center">
    <h1 class="text-ellipsis">标题</h1>
  </div>
</template>
```

---

## 🧩 使用现代化组件

### ModernCard 组件

```vue
<template>
  <ModernCard
    title="课程统计"
    subtitle="本月学习数据"
    icon="fas fa-chart-line"
    variant="primary"
    :hoverable="true"
  >
    <template #actions>
      <button>查看详情</button>
    </template>
    
    <!-- 卡片内容 -->
    <div class="stats">
      <p>学习时长: 45小时</p>
      <p>完成课程: 8门</p>
    </div>
    
    <template #footer>
      <p>更新时间: 2026-01-28</p>
    </template>
  </ModernCard>
</template>

<script setup>
import ModernCard from '@/components/ModernCard.vue'
</script>
```

### ModernButton 组件

```vue
<template>
  <div class="button-group">
    <!-- 主要按钮 -->
    <ModernButton 
      variant="primary" 
      size="large"
      icon="fas fa-rocket"
      @click="handleClick"
    >
      开始学习
    </ModernButton>
    
    <!-- 次要按钮 -->
    <ModernButton 
      variant="secondary"
      icon="fas fa-book"
    >
      查看课程
    </ModernButton>
    
    <!-- 加载状态 -->
    <ModernButton 
      variant="success"
      :loading="isLoading"
    >
      提交作业
    </ModernButton>
    
    <!-- 块级按钮 -->
    <ModernButton 
      variant="primary"
      :block="true"
      rounded
    >
      立即注册
    </ModernButton>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ModernButton from '@/components/ModernButton.vue'

const isLoading = ref(false)

const handleClick = () => {
  console.log('按钮被点击')
}
</script>
```

---

## 🎨 主题变量速查表

### 颜色变量

```scss
// 主色调
$primary-color: #4F46E5;
$primary-light: #6366F1;
$primary-dark: #4338CA;
$primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

// 辅助色
$secondary-color: #F59E0B;
$secondary-light: #FBBF24;
$secondary-dark: #D97706;

// 强调色
$accent-color: #10B981;
$accent-light: #34D399;
$accent-dark: #059669;

// 状态色
$success-color: #10B981;
$warning-color: #F59E0B;
$error-color: #EF4444;
$info-color: #3B82F6;

// 文字色
$text-primary: #111827;
$text-secondary: #6B7280;
$text-tertiary: #9CA3AF;
$text-inverse: #FFFFFF;

// 背景色
$bg-primary: #FFFFFF;
$bg-secondary: #F9FAFB;
$bg-tertiary: #F3F4F6;
```

### 间距变量

```scss
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 32px;
$spacing-2xl: 48px;
$spacing-3xl: 64px;
```

### 圆角变量

```scss
$radius-sm: 6px;
$radius-md: 12px;
$radius-lg: 16px;
$radius-xl: 20px;
$radius-2xl: 24px;
$radius-full: 9999px;
```

### 阴影变量

```scss
$shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
$shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
$shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
$shadow-glow: 0 0 20px rgba(79, 70, 229, 0.3);
```

---

## 📱 响应式设计

### 使用响应式混合宏

```scss
.my-component {
  padding: $spacing-xl;
  
  @include responsive(md) {
    padding: $spacing-lg;
  }
  
  @include responsive(sm) {
    padding: $spacing-md;
  }
}
```

### 断点说明

```scss
$breakpoint-xs: 480px;   // 超小屏幕
$breakpoint-sm: 640px;   // 小屏幕
$breakpoint-md: 768px;   // 中等屏幕
$breakpoint-lg: 1024px;  // 大屏幕
$breakpoint-xl: 1280px;  // 超大屏幕
$breakpoint-2xl: 1536px; // 2K屏幕
```

---

## 🎬 动画效果

### 使用内置动画

```vue
<template>
  <div class="animated-element">
    内容
  </div>
</template>

<style lang="scss" scoped>
.animated-element {
  animation: fadeIn 0.6s ease-out;
  
  &:hover {
    animation: bounce 0.5s ease-in-out;
  }
}
</style>
```

### 可用动画

- `fadeIn` - 淡入
- `slideInRight` - 从右滑入
- `pulse` - 脉冲
- `bounce` - 弹跳
- `rotate` - 旋转
- `shimmer` - 闪烁

---

## 💡 最佳实践

### 1. 统一使用主题变量

❌ **不推荐**:
```scss
.my-button {
  background: #4F46E5;
  padding: 12px 24px;
  border-radius: 16px;
}
```

✅ **推荐**:
```scss
.my-button {
  background: $primary-color;
  padding: $spacing-md $spacing-lg;
  border-radius: $radius-lg;
}
```

### 2. 使用混合宏简化代码

❌ **不推荐**:
```scss
.my-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
    transform: translateY(-4px);
  }
}
```

✅ **推荐**:
```scss
.my-card {
  @include card-style;
}
```

### 3. 响应式优先

```scss
.container {
  // 移动端优先
  padding: $spacing-md;
  
  // 平板及以上
  @include responsive(md) {
    padding: $spacing-lg;
  }
  
  // 桌面端
  @include responsive(lg) {
    padding: $spacing-xl;
  }
}
```

---

## 🐛 常见问题

### Q1: 主题变量不生效？

**A**: 确保在组件中正确引入主题文件：
```scss
@import '@/assets/styles/theme.scss';
```

### Q2: 组件样式被覆盖？

**A**: 使用 `scoped` 属性或提高选择器优先级：
```vue
<style lang="scss" scoped>
// 样式只作用于当前组件
</style>
```

### Q3: 响应式不生效？

**A**: 检查是否正确使用响应式混合宏：
```scss
@include responsive(md) {
  // 在768px以下生效
}
```

---

## 📚 参考资源

- **主题配置**: `frontend/src/assets/styles/theme.scss`
- **组件示例**: `frontend/src/components/ModernCard.vue`
- **优化报告**: `OPTIMIZATION_REPORT.md`
- **演示数据**: `database/demo_data.sql`

---

## 🎯 下一步

1. ✅ 导入演示数据
2. ✅ 启动项目测试
3. 🔄 使用新组件优化现有页面
4. 🔄 统一所有页面的视觉风格
5. 🔄 准备比赛演示材料

---

**更新时间**: 2026年1月28日
**版本**: v2.0

