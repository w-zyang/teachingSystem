<!-- 
  现代化按钮组件
  支持多种样式和尺寸
-->
<template>
  <button 
    class="modern-button" 
    :class="buttonClass"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span class="button-icon" v-if="icon && !loading">
      <i :class="icon"></i>
    </span>
    <span class="button-icon loading-icon" v-if="loading">
      <i class="fas fa-spinner fa-spin"></i>
    </span>
    <span class="button-text">
      <slot></slot>
    </span>
    <span class="button-glow"></span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'success', 'warning', 'error', 'ghost', 'link'].includes(value)
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  icon: String,
  loading: Boolean,
  disabled: Boolean,
  block: Boolean,
  rounded: Boolean
})

const emit = defineEmits(['click'])

const buttonClass = computed(() => ({
  [`button-${props.variant}`]: true,
  [`button-${props.size}`]: true,
  'button-block': props.block,
  'button-rounded': props.rounded,
  'button-loading': props.loading,
  'button-disabled': props.disabled
}))

const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/theme.scss';

.modern-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  font-family: $font-family-base;
  font-weight: $font-weight-semibold;
  border: none;
  cursor: pointer;
  transition: all $transition-base;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
  
  &:focus {
    outline: none;
  }
  
  &:active:not(.button-disabled):not(.button-loading) {
    transform: translateY(1px);
  }
  
  // 按钮光效
  .button-glow {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left $transition-slow;
  }
  
  &:hover:not(.button-disabled):not(.button-loading) .button-glow {
    left: 100%;
  }
  
  .button-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    
    i {
      font-size: inherit;
    }
  }
  
  .button-text {
    position: relative;
    z-index: 1;
  }
  
  // 尺寸变体
  &.button-small {
    padding: 8px 16px;
    font-size: $font-size-sm;
    border-radius: $radius-md;
    
    .button-icon i {
      font-size: 14px;
    }
  }
  
  &.button-medium {
    padding: 12px 24px;
    font-size: $font-size-base;
    border-radius: $radius-lg;
    
    .button-icon i {
      font-size: 16px;
    }
  }
  
  &.button-large {
    padding: 16px 32px;
    font-size: $font-size-lg;
    border-radius: $radius-xl;
    
    .button-icon i {
      font-size: 20px;
    }
  }
  
  // 样式变体
  &.button-primary {
    background: $primary-gradient;
    color: $white;
    box-shadow: $shadow-md;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      box-shadow: $shadow-glow;
      transform: translateY(-2px);
    }
  }
  
  &.button-secondary {
    background: $white;
    color: $primary-color;
    border: 2px solid $primary-color;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      background: $primary-color;
      color: $white;
      transform: translateY(-2px);
    }
  }
  
  &.button-success {
    background: linear-gradient(135deg, $accent-color, $accent-light);
    color: $white;
    box-shadow: $shadow-md;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      box-shadow: 0 0 20px rgba($accent-color, 0.4);
      transform: translateY(-2px);
    }
  }
  
  &.button-warning {
    background: linear-gradient(135deg, $secondary-color, $secondary-light);
    color: $white;
    box-shadow: $shadow-md;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      box-shadow: $shadow-glow-secondary;
      transform: translateY(-2px);
    }
  }
  
  &.button-error {
    background: linear-gradient(135deg, $error-color, #F87171);
    color: $white;
    box-shadow: $shadow-md;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      box-shadow: 0 0 20px rgba($error-color, 0.4);
      transform: translateY(-2px);
    }
  }
  
  &.button-ghost {
    background: transparent;
    color: $text-primary;
    border: 1px solid $border-color;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      background: $bg-secondary;
      border-color: $primary-color;
      color: $primary-color;
    }
  }
  
  &.button-link {
    background: transparent;
    color: $primary-color;
    padding: 4px 8px;
    
    &:hover:not(.button-disabled):not(.button-loading) {
      color: $primary-dark;
      text-decoration: underline;
    }
  }
  
  // 块级按钮
  &.button-block {
    width: 100%;
    display: flex;
  }
  
  // 圆形按钮
  &.button-rounded {
    border-radius: $radius-full;
  }
  
  // 禁用状态
  &.button-disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  // 加载状态
  &.button-loading {
    cursor: wait;
    
    .loading-icon i {
      animation: rotate 1s linear infinite;
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>


