<template>
  <div v-if="visible" class="progress-overlay">
    <div class="progress-container">
      <div class="progress-header">
        <div class="progress-icon">
          <svg class="spinner" viewBox="0 0 50 50">
            <circle class="path" cx="25" cy="25" r="20" fill="none" stroke-width="4"></circle>
          </svg>
        </div>
        <h3 class="progress-title">{{ title }}</h3>
      </div>
      
      <div class="progress-content">
        <div class="progress-bar-container">
          <div class="progress-bar" :style="{ width: Math.round(progress) + '%' }">
            <span class="progress-text">{{ Math.round(progress) }}%</span>
          </div>
        </div>
        
        <div class="progress-steps">
          <div 
            v-for="(step, index) in steps" 
            :key="index"
            class="progress-step"
            :class="{ 
              'active': currentStep === index, 
              'completed': currentStep > index,
              'pending': currentStep < index
            }"
          >
            <div class="step-icon">
              <svg v-if="currentStep > index" class="check-icon" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
              <span v-else class="step-number">{{ index + 1 }}</span>
            </div>
            <div class="step-content">
              <div class="step-title">{{ step.title }}</div>
              <div class="step-desc">{{ step.desc }}</div>
            </div>
          </div>
        </div>
        
        <div class="progress-message">
          <p>{{ message }}</p>
          <p class="progress-tip">{{ tip }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'AI 正在生成中...'
  },
  progress: {
    type: Number,
    default: 0
  },
  currentStep: {
    type: Number,
    default: 0
  },
  steps: {
    type: Array,
    default: () => [
      { title: '准备数据', desc: '正在准备生成所需的数据...' },
      { title: '调用AI', desc: '正在调用AI服务...' },
      { title: '生成内容', desc: '正在生成内容...' },
      { title: '完成', desc: '生成完成！' }
    ]
  },
  message: {
    type: String,
    default: '请耐心等待，这可能需要1-3分钟...'
  },
  tip: {
    type: String,
    default: '💡 提示：生成时间取决于内容复杂度和AI服务响应速度'
  }
})
</script>

<style lang="scss" scoped>
.progress-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.progress-container {
  background: white;
  border-radius: 24px;
  padding: 40px;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.4s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.progress-header {
  text-align: center;
  margin-bottom: 32px;
  
  .progress-icon {
    margin: 0 auto 20px;
    width: 80px;
    height: 80px;
    
    .spinner {
      animation: rotate 2s linear infinite;
      width: 80px;
      height: 80px;
      
      .path {
        stroke: #667eea;
        stroke-linecap: round;
        animation: dash 1.5s ease-in-out infinite;
      }
    }
  }
  
  .progress-title {
    margin: 0;
    font-size: 24px;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

@keyframes rotate {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}

.progress-content {
  .progress-bar-container {
    background: #f0f0f0;
    border-radius: 12px;
    height: 32px;
    overflow: hidden;
    margin-bottom: 32px;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .progress-bar {
      height: 100%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      transition: width 0.5s ease;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      padding-right: 12px;
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(
          90deg,
          transparent,
          rgba(255, 255, 255, 0.3),
          transparent
        );
        animation: shimmer 2s infinite;
      }
      
      .progress-text {
        position: relative;
        z-index: 1;
        color: white;
        font-weight: 700;
        font-size: 14px;
      }
    }
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-steps {
  margin-bottom: 32px;
  
  .progress-step {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    margin-bottom: 12px;
    transition: all 0.3s ease;
    
    &.active {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      border: 2px solid #667eea;
      
      .step-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        animation: pulse 2s infinite;
      }
      
      .step-title {
        color: #667eea;
        font-weight: 700;
      }
    }
    
    &.completed {
      background: rgba(72, 187, 120, 0.1);
      border: 2px solid #48bb78;
      
      .step-icon {
        background: #48bb78;
        color: white;
      }
      
      .step-title {
        color: #2d3748;
      }
    }
    
    &.pending {
      background: #f7fafc;
      border: 2px solid #e2e8f0;
      opacity: 0.6;
      
      .step-icon {
        background: #e2e8f0;
        color: #a0aec0;
      }
      
      .step-title {
        color: #a0aec0;
      }
    }
    
    .step-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      transition: all 0.3s ease;
      
      .check-icon {
        width: 24px;
        height: 24px;
        fill: white;
      }
      
      .step-number {
        font-weight: 700;
        font-size: 16px;
      }
    }
    
    .step-content {
      flex: 1;
      
      .step-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 4px;
        transition: all 0.3s ease;
      }
      
      .step-desc {
        font-size: 14px;
        color: #718096;
        line-height: 1.5;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.7);
  }
  50% {
    box-shadow: 0 0 0 10px rgba(102, 126, 234, 0);
  }
}

.progress-message {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  
  p {
    margin: 0 0 8px 0;
    font-size: 15px;
    color: #2d3748;
    font-weight: 500;
  }
  
  .progress-tip {
    font-size: 13px;
    color: #718096;
    margin: 0;
  }
}

@media (max-width: 768px) {
  .progress-container {
    padding: 24px;
    
    .progress-header {
      margin-bottom: 24px;
      
      .progress-icon {
        width: 60px;
        height: 60px;
        
        .spinner {
          width: 60px;
          height: 60px;
        }
      }
      
      .progress-title {
        font-size: 20px;
      }
    }
    
    .progress-steps {
      .progress-step {
        padding: 12px;
        
        .step-icon {
          width: 32px;
          height: 32px;
          
          .step-number {
            font-size: 14px;
          }
        }
        
        .step-content {
          .step-title {
            font-size: 14px;
          }
          
          .step-desc {
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style>

