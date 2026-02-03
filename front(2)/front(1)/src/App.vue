<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
// App component
</script>

<style lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=Poppins:wght@600;700;800;900&display=swap');
@import './assets/styles/theme.scss';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

body {
  font-family: $font-family-base;
  font-size: $font-size-base;
  line-height: $line-height-normal;
  color: $text-primary;
  background: $bg-secondary;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  min-height: 100vh;
}

// 全局滚动条样式
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: $gray-100;
  border-radius: $radius-full;
}

::-webkit-scrollbar-thumb {
  background: $gray-300;
  border-radius: $radius-full;
  transition: background $transition-base;
  
  &:hover {
    background: $gray-400;
  }
}

// 全局链接样式
a {
  color: $text-link;
  text-decoration: none;
  transition: color $transition-base;
  
  &:hover {
    color: $text-link-hover;
  }
}

// 全局按钮重置
button {
  font-family: inherit;
  cursor: pointer;
  border: none;
  outline: none;
  
  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
}

// 全局输入框样式
input, textarea, select {
  font-family: inherit;
  font-size: inherit;
  outline: none;
}

// 全局标题样式
h1, h2, h3, h4, h5, h6 {
  font-family: $font-family-heading;
  font-weight: $font-weight-bold;
  line-height: $line-height-tight;
  color: $text-primary;
}

h1 { font-size: $font-size-5xl; }
h2 { font-size: $font-size-4xl; }
h3 { font-size: $font-size-3xl; }
h4 { font-size: $font-size-2xl; }
h5 { font-size: $font-size-xl; }
h6 { font-size: $font-size-lg; }

// 全局段落样式
p {
  margin-bottom: $spacing-md;
  line-height: $line-height-relaxed;
}

// 全局列表样式
ul, ol {
  list-style: none;
}

// 全局图片样式
img {
  max-width: 100%;
  height: auto;
  display: block;
}

// Element Plus 样式覆盖
.el-button--primary {
  @include button-primary;
}

.el-card {
  @include card-style;
}

// 加载动画
.loading-spinner {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid $gray-200;
  border-top-color: $primary-color;
  border-radius: 50%;
  animation: rotate 1s linear infinite;
}

// 页面过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity $transition-base;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active,
.slide-leave-active {
  transition: transform $transition-base, opacity $transition-base;
}

.slide-enter-from {
  transform: translateX(30px);
  opacity: 0;
}

.slide-leave-to {
  transform: translateX(-30px);
  opacity: 0;
}

// 响应式容器
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 $spacing-lg;
  
  @include responsive(md) {
    padding: 0 $spacing-md;
  }
}

.container-fluid {
  width: 100%;
  padding: 0 $spacing-lg;
  
  @include responsive(md) {
    padding: 0 $spacing-md;
  }
}

// 工具类
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }

.mt-1 { margin-top: $spacing-xs; }
.mt-2 { margin-top: $spacing-sm; }
.mt-3 { margin-top: $spacing-md; }
.mt-4 { margin-top: $spacing-lg; }
.mt-5 { margin-top: $spacing-xl; }

.mb-1 { margin-bottom: $spacing-xs; }
.mb-2 { margin-bottom: $spacing-sm; }
.mb-3 { margin-bottom: $spacing-md; }
.mb-4 { margin-bottom: $spacing-lg; }
.mb-5 { margin-bottom: $spacing-xl; }

.pt-1 { padding-top: $spacing-xs; }
.pt-2 { padding-top: $spacing-sm; }
.pt-3 { padding-top: $spacing-md; }
.pt-4 { padding-top: $spacing-lg; }
.pt-5 { padding-top: $spacing-xl; }

.pb-1 { padding-bottom: $spacing-xs; }
.pb-2 { padding-bottom: $spacing-sm; }
.pb-3 { padding-bottom: $spacing-md; }
.pb-4 { padding-bottom: $spacing-lg; }
.pb-5 { padding-bottom: $spacing-xl; }
</style> 