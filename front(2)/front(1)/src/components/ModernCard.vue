<!-- 
  现代化卡片组件示例
  展示如何使用新的主题系统
-->
<template>
  <div class="modern-card" :class="cardClass">
    <div class="card-header" v-if="$slots.header || title">
      <slot name="header">
        <div class="header-content">
          <div class="header-icon" v-if="icon">
            <i :class="icon"></i>
          </div>
          <div class="header-text">
            <h3 class="card-title">{{ title }}</h3>
            <p class="card-subtitle" v-if="subtitle">{{ subtitle }}</p>
          </div>
        </div>
      </slot>
      <div class="header-actions" v-if="$slots.actions">
        <slot name="actions"></slot>
      </div>
    </div>
    
    <div class="card-body">
      <slot></slot>
    </div>
    
    <div class="card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
    
    <div class="card-decoration"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  subtitle: String,
  icon: String,
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'primary', 'success', 'warning', 'error'].includes(value)
  },
  hoverable: {
    type: Boolean,
    default: true
  },
  bordered: {
    type: Boolean,
    default: true
  }
})

const cardClass = computed(() => ({
  [`card-${props.variant}`]: true,
  'card-hoverable': props.hoverable,
  'card-bordered': props.bordered
}))
</script>

<style lang="scss" scoped>
@import '@/assets/styles/theme.scss';

.modern-card {
  background: $bg-primary;
  border-radius: $radius-xl;
  padding: $spacing-lg;
  position: relative;
  overflow: hidden;
  transition: all $transition-base;
  
  &.card-bordered {
    border: 1px solid $border-color;
  }
  
  &.card-hoverable {
    cursor: pointer;
    
    &:hover {
      box-shadow: $shadow-xl;
      transform: translateY(-4px);
      
      .card-decoration {
        opacity: 1;
      }
    }
  }
  
  // 卡片装饰
  .card-decoration {
    position: absolute;
    top: 0;
    right: 0;
    width: 120px;
    height: 120px;
    background: linear-gradient(135deg, rgba($primary-color, 0.05), rgba($primary-light, 0.05));
    border-radius: 0 $radius-xl 0 120px;
    opacity: 0;
    transition: opacity $transition-base;
    pointer-events: none;
  }
  
  // 卡片头部
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: $spacing-lg;
    position: relative;
    z-index: 1;
    
    .header-content {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      flex: 1;
      
      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: $radius-md;
        background: $primary-gradient;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        i {
          font-size: 24px;
          color: $white;
        }
      }
      
      .header-text {
        flex: 1;
        
        .card-title {
          margin: 0 0 4px 0;
          font-size: $font-size-xl;
          font-weight: $font-weight-bold;
          color: $text-primary;
        }
        
        .card-subtitle {
          margin: 0;
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }
    }
    
    .header-actions {
      display: flex;
      gap: $spacing-sm;
    }
  }
  
  // 卡片主体
  .card-body {
    position: relative;
    z-index: 1;
  }
  
  // 卡片底部
  .card-footer {
    margin-top: $spacing-lg;
    padding-top: $spacing-md;
    border-top: 1px solid $border-color;
    position: relative;
    z-index: 1;
  }
  
  // 变体样式
  &.card-primary {
    .header-icon {
      background: $primary-gradient;
    }
    
    .card-decoration {
      background: linear-gradient(135deg, rgba($primary-color, 0.1), rgba($primary-light, 0.05));
    }
  }
  
  &.card-success {
    .header-icon {
      background: linear-gradient(135deg, $accent-color, $accent-light);
    }
    
    .card-decoration {
      background: linear-gradient(135deg, rgba($accent-color, 0.1), rgba($accent-light, 0.05));
    }
  }
  
  &.card-warning {
    .header-icon {
      background: linear-gradient(135deg, $secondary-color, $secondary-light);
    }
    
    .card-decoration {
      background: linear-gradient(135deg, rgba($secondary-color, 0.1), rgba($secondary-light, 0.05));
    }
  }
  
  &.card-error {
    .header-icon {
      background: linear-gradient(135deg, $error-color, #F87171);
    }
    
    .card-decoration {
      background: linear-gradient(135deg, rgba($error-color, 0.1), rgba(#F87171, 0.05));
    }
  }
}

// 响应式
@include responsive(md) {
  .modern-card {
    padding: $spacing-md;
    
    .card-header {
      flex-direction: column;
      gap: $spacing-md;
      
      .header-actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
}
</style>


