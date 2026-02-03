<template>
  <div class="exam-page-wrapper">
    <div class="exam-container">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
// 考试页面包装组件，提供美观的背景和布局
</script>

<style scoped>
.exam-page-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.exam-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-page-wrapper {
    padding: 12px;
  }
}
</style>
