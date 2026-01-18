<template>
  <div class="not-found-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <div class="not-found-content">
      <div class="error-card">
        <div class="error-illustration">
          <div class="error-number">404</div>
          <div class="error-icon">
            <img src="@/assets/search.png" alt="搜索" class="search-icon">
          </div>
          <div class="error-glow"></div>
        </div>
        
        <div class="error-message">
          <h1 class="error-title">页面未找到</h1>
          <p class="error-description">抱歉，您访问的页面不存在或已被移除。</p>
          <p class="error-suggestion">请检查网址是否正确，或返回首页继续浏览。</p>
        </div>
        
        <div class="error-actions">
          <button @click="goHome" class="btn btn-primary home-btn">
            <img src="@/assets/author.png" alt="首页" class="btn-icon">
            返回首页
          </button>
          <button @click="goBack" class="btn btn-secondary back-btn">
            <img src="@/assets/time.png" alt="返回" class="btn-icon">
            返回上页
          </button>
        </div>
        
        <div class="helpful-links">
          <h3 class="links-title">
            <img src="@/assets/tag.png" alt="链接" class="links-icon">
            您可能想要访问：
          </h3>
          <div class="links-grid">
            <router-link to="/login" class="link-item">
              <img src="@/assets/author.png" alt="登录" class="link-icon">
              <span>登录页面</span>
            </router-link>
            <router-link to="/register" class="link-item">
              <img src="@/assets/add.png" alt="注册" class="link-icon">
              <span>注册页面</span>
            </router-link>
            <router-link to="/landing" class="link-item">
              <img src="@/assets/category.png" alt="首页" class="link-icon">
              <span>系统首页</span>
            </router-link>
            <a href="#" @click="contactSupport" class="link-item">
              <img src="@/assets/email.png" alt="支持" class="link-icon">
              <span>联系支持</span>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSystemInfo } from '@/api/stats'

const router = useRouter()

// 响应式变量
const systemInfo = ref({
  name: '智能教学管理系统',
  supportEmail: 'support@example.com'
})

const errorInfo = ref({
  code: '404',
  title: '页面未找到',
  description: '抱歉，您访问的页面不存在或已被移除。',
  suggestions: [
    '检查URL是否正确',
    '返回首页重新导航',
    '联系系统管理员'
  ]
})

// 获取系统信息
const fetchSystemInfo = async () => {
  try {
    const res = await getSystemInfo()
    if (res && res.data) {
      systemInfo.value = {
        name: res.data.name || '智能教学管理系统',
        supportEmail: res.data.supportEmail || 'support@example.com'
      }
      console.log('系统信息:', res.data)
    }
  } catch (error) {
    console.error('获取系统信息失败:', error)
    // 使用默认数据
    systemInfo.value = {
      name: '智能教学管理系统',
      supportEmail: 'support@example.com'
    }
  }
}

// 返回首页
const goHome = () => {
  router.push('/landing')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 联系支持
const contactSupport = () => {
  window.open(`mailto:${systemInfo.value.supportEmail}?subject=页面访问问题`, '_blank')
}

// 页面加载时获取数据
onMounted(async () => {
  await fetchSystemInfo()
})
</script>

<style lang="scss" scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
  
  &.shape-1 {
    width: 80px;
    height: 80px;
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }
  
  &.shape-2 {
    width: 120px;
    height: 120px;
    top: 20%;
    right: 15%;
    animation-delay: 2s;
  }
  
  &.shape-3 {
    width: 60px;
    height: 60px;
    bottom: 20%;
    left: 20%;
    animation-delay: 4s;
  }
  
  &.shape-4 {
    width: 100px;
    height: 100px;
    bottom: 10%;
    right: 10%;
    animation-delay: 1s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.not-found-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 600px;
}

.error-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2);
  }
}

.error-illustration {
  position: relative;
  margin-bottom: 32px;
  
  .error-number {
    font-size: 120px;
    font-weight: 900;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
    margin-bottom: 16px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .error-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #f7fafc, #edf2f7);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    
    .search-icon {
      width: 40px;
      height: 40px;
      opacity: 0.7;
    }
  }
  
  .error-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.2) 0%, transparent 70%);
    border-radius: 50%;
    animation: pulse 2s ease-in-out infinite;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

.error-message {
  margin-bottom: 32px;
  
  .error-title {
    color: #2d3748;
    font-size: 32px;
    font-weight: 800;
    margin-bottom: 16px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .error-description {
    color: #4a5568;
    font-size: 18px;
    line-height: 1.6;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .error-suggestion {
    color: #718096;
    font-size: 16px;
    line-height: 1.6;
  }
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin: 32px 0;
  
  @media (max-width: 480px) {
    flex-direction: column;
    gap: 12px;
  }
  
  .btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 32px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    text-decoration: none;
    transition: all 0.3s ease;
    border: none;
    cursor: pointer;
    
    .btn-icon {
      width: 20px;
      height: 20px;
    }
    
    &.btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
      }
    }
    
    &.btn-secondary {
      background: rgba(255, 255, 255, 0.9);
      color: #667eea;
      border: 2px solid #e2e8f0;
      
      &:hover {
        background: white;
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.helpful-links {
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid #e2e8f0;
  
  .links-title {
    font-size: 20px;
    font-weight: 700;
    color: #2d3748;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .links-icon {
      width: 24px;
      height: 24px;
      opacity: 0.7;
    }
  }
  
  .links-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    .link-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px 20px;
      background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
      border-radius: 12px;
      text-decoration: none;
      color: #4a5568;
      font-weight: 500;
      transition: all 0.3s ease;
      border: 1px solid #e2e8f0;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        background: white;
        color: #667eea;
      }
      
      .link-icon {
        width: 20px;
        height: 20px;
        opacity: 0.7;
      }
      
      span {
        font-size: 14px;
      }
    }
  }
}

@media (max-width: 480px) {
  .error-card {
    padding: 32px 24px;
  }
  
  .error-illustration .error-number {
    font-size: 80px;
  }
  
  .error-message .error-title {
    font-size: 24px;
  }
  
  .error-message .error-description {
    font-size: 16px;
  }
  
  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style> 