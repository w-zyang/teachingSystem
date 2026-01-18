<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <div class="login-content">
      <div class="login-card">
        <div class="login-header">
          <router-link to="/landing" class="logo-link">
            <div class="logo-container">
              <img src="@/assets/logo.png" alt="Logo" class="logo">
              <div class="logo-glow"></div>
            </div>
          </router-link>
          <h1 class="title">智能教学管理系统</h1>
          <p class="subtitle">面向大学生的智能教学管理平台</p>
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">欢迎回来</span>
            <span class="divider-line"></span>
          </div>
        </div>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="role" class="form-label">
              <img src="@/assets/category.png" alt="角色" class="label-icon">
              角色选择
            </label>
            <div class="select-wrapper">
              <select id="role" v-model="form.role" required class="form-select">
                <option value="">请选择角色</option>
                <option value="teacher"> 教师</option>
                <option value="student"> 学生</option>
                <option value="admin"> 管理员</option>
              </select>
              <div class="select-arrow"></div>
            </div>
          </div>

          <div class="form-group">
            <label for="username" class="form-label">
              <img src="@/assets/author.png" alt="用户" class="label-icon">
              用户名
            </label>
            <div class="input-wrapper">
              <input 
                type="text" 
                id="username" 
                v-model="form.username" 
                placeholder="请输入用户名"
                required
                class="form-input"
              >
              <div class="input-focus-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="password" class="form-label">
              <img src="@/assets/balance.png" alt="密码" class="label-icon">
              密码
            </label>
            <div class="input-wrapper">
              <input 
                type="password" 
                id="password" 
                v-model="form.password" 
                placeholder="请输入密码"
                required
                :class="['form-input', passwordError ? 'input-error' : '']"
                @input="passwordError = ''"
              >
              <div class="input-focus-border"></div>
            </div>
            <div v-if="passwordError" class="error-tip">{{ passwordError }}</div>
          </div>
          
          <div class="form-options">
            <label class="checkbox-wrapper">
              <input type="checkbox" v-model="form.remember" class="checkbox-input">
              <span class="checkbox-custom"></span>
              <span class="checkbox-label">记住我</span>
            </label>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>
          
          <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-else class="btn-icon"></span>
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>
        
        <div class="login-footer">
          <p class="register-link">还没有账号？ <router-link to="/register" class="link-highlight">立即注册</router-link></p>
          <p class="system-info">支持多专业课程学习，提供智能教学管理服务</p>
        </div>
        
        <!-- 测试账号提示 -->
        <div class="test-accounts">
          <h3 class="test-title">
            <img src="@/assets/tag.png" alt="测试" class="test-icon">
            测试账号
          </h3>
          <div class="account-list">
            <div class="account-item">
              <span class="account-role">👨‍💼 管理员:</span>
              <span class="account-info">admin / 123456</span>
            </div>
            <div class="account-item">
              <span class="account-role">👨‍🏫 教师:</span>
              <span class="account-info">teacher1 / 123456</span>
            </div>
            <div class="account-item">
              <span class="account-role">👨‍🎓 学生:</span>
              <span class="account-info">student1 / 123456</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'

const router = useRouter()

// 响应式变量
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  role: '',
  remember: false
})
const passwordError = ref('')

// 处理登录
const handleLogin = async () => {
  // 表单验证
  if (!form.value.username.trim()) {
    alert('请输入用户名')
    return
  }
  
  if (!form.value.password.trim()) {
    passwordError.value = '请输入密码'
    return
  } else {
    passwordError.value = ''
  }
  
  if (!form.value.role) {
    alert('请选择角色')
    return
  }
  
  loading.value = true
  
  try {
    const loginData = {
      username: form.value.username.trim(),
      password: form.value.password,
      role: form.value.role
    }
    
    console.log('发送登录请求:', loginData)
    
    const res = await login(loginData)
    
    console.log('登录响应:', res)
    
    if (res && res.success === true && res.data) {
      // 保存用户信息到本地存储
      const userData = res.data.user || res.data
      localStorage.setItem('token', res.data.token || 'mock-token-' + Date.now())
      localStorage.setItem('userId', userData.id || '1')
      localStorage.setItem('userRole', form.value.role)
      localStorage.setItem('username', form.value.username)
      localStorage.setItem('realName', userData.realName || form.value.username)
      localStorage.setItem('userEmail', userData.email || '')
      
      // 如果选择记住我，保存登录信息
      if (form.value.remember) {
        localStorage.setItem('rememberedUsername', form.value.username)
        localStorage.setItem('rememberedRole', form.value.role)
      } else {
        localStorage.removeItem('rememberedUsername')
        localStorage.removeItem('rememberedRole')
      }
      
      console.log('登录成功，用户信息:', userData)
      
      // 根据角色跳转到对应页面
      let targetPath = ''
      switch (form.value.role) {
        case 'teacher':
          targetPath = '/teacher/dashboard'
          break
        case 'student':
          targetPath = '/student/dashboard'
          break
        case 'admin':
          targetPath = '/admin/dashboard'
          break
        default:
          alert('请选择正确的角色')
          loading.value = false
          return
      }
      
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        router.push(targetPath)
      }, 500)
      
    } else {
      const errorMsg = res?.msg || '登录失败，请检查用户名和密码'
      alert(errorMsg)
    }
  } catch (error) {
    console.error('登录失败:', error)
    
    // 如果是网络错误，提供更友好的提示
    if (error.code === 'ERR_NETWORK') {
      alert('网络连接失败，请检查网络连接或稍后重试')
    } else if (error.response?.status === 404) {
      alert('登录接口不存在，请联系管理员')
    } else if (error.response?.status === 500) {
      alert('服务器内部错误，请联系管理员')
    } else {
      alert('登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}

// 页面加载时检查是否有记住的登录信息
const checkRememberedLogin = () => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  const rememberedRole = localStorage.getItem('rememberedRole')
  
  if (rememberedUsername && rememberedRole) {
    form.value.username = rememberedUsername
    form.value.role = rememberedRole
    form.value.remember = true
  }
}

// 页面加载时执行
onMounted(() => {
  checkRememberedLogin()
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
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

.login-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 450px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  
  .logo-link {
    display: inline-block;
    cursor: pointer;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
    }
  }
  
  .logo-container {
    position: relative;
    display: inline-block;
    margin-bottom: 20px;
  }
  
  .logo {
    width: 80px;
    height: 80px;
    border-radius: 20px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    transition: transform 0.3s ease;
    position: relative;
    z-index: 2;
    
    &:hover {
      transform: rotate(5deg) scale(1.1);
    }
  }
  
  .logo-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100px;
    height: 100px;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
    border-radius: 50%;
    animation: pulse 2s ease-in-out infinite;
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
  
  .title {
    color: #2d3748;
    font-size: 28px;
    font-weight: 800;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .subtitle {
    color: #718096;
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 24px;
  }
  
  .divider {
    display: flex;
    align-items: center;
    margin: 24px 0;
    
    .divider-line {
      flex: 1;
      height: 1px;
      background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
    }
    
    .divider-text {
      padding: 0 16px;
      color: #a0aec0;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

.login-form {
  .form-group {
    margin-bottom: 24px;
    position: relative;
    
    .form-label {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      font-weight: 600;
      color: #2d3748;
      font-size: 14px;
      
      .label-icon {
        width: 16px;
        height: 16px;
        margin-right: 8px;
        opacity: 0.7;
      }
    }
    
    .input-wrapper, .select-wrapper {
      position: relative;
      width: 100%;
      box-sizing: border-box;
      
      .form-input, .form-select {
        width: 100%;
        padding: 16px 20px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        font-size: 16px;
        transition: all 0.3s ease;
        background: #f7fafc;
        color: #2d3748;
        box-sizing: border-box;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          background: white;
          box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
          transform: translateY(-2px);
        }
        
        &::placeholder {
          color: #a0aec0;
        }
      }
      
      .form-select {
        -webkit-appearance: none;
        -moz-appearance: none;
        appearance: none;
        background-image: none;
      }
      
      .input-focus-border {
        position: absolute;
        bottom: 0;
        left: 50%;
        width: 0;
        height: 2px;
        background: linear-gradient(90deg, #667eea, #764ba2);
        transition: all 0.3s ease;
        transform: translateX(-50%);
      }
      
      .form-input:focus + .input-focus-border {
        width: 100%;
      }
    }
    
    .select-wrapper {
      .select-arrow {
        position: absolute;
        right: 16px;
        top: 50%;
        transform: translateY(-50%);
        width: 0;
        height: 0;
        border-left: 6px solid transparent;
        border-right: 6px solid transparent;
        border-top: 6px solid #a0aec0;
        pointer-events: none;
        transition: transform 0.3s ease;
      }
      
      .form-select:focus + .select-arrow {
        transform: translateY(-50%) rotate(180deg);
      }
    }
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 24px 0;
  
  .checkbox-wrapper {
    display: flex;
    align-items: center;
    position: relative;
    cursor: pointer;
    
    .checkbox-input {
      position: absolute;
      opacity: 0;
      cursor: pointer;
    }
    
    .checkbox-custom {
      width: 20px;
      height: 20px;
      border: 2px solid #e2e8f0;
      border-radius: 6px;
      margin-right: 12px;
      position: relative;
      transition: all 0.3s ease;
      background: white;
      
      &::after {
        content: '✓';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: white;
        font-size: 12px;
        font-weight: bold;
        opacity: 0;
        transition: opacity 0.3s ease;
      }
    }
    
    .checkbox-input:checked + .checkbox-custom {
      background: #667eea;
      border-color: #667eea;
      
      &::after {
        opacity: 1;
      }
    }
    
    .checkbox-label {
      font-size: 14px;
      color: #4a5568;
      font-weight: 500;
    }
  }
  
  .forgot-password {
    font-size: 14px;
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s ease;
    
    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }
  }
}

.login-btn {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
    transition: left 0.5s;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
    
    &::before {
      left: 100%;
    }
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
    
    &:hover::before {
      left: -100%;
    }
  }
  
  .btn-icon {
    font-size: 18px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 32px;
  
  .register-link {
    margin: 16px 0;
    font-size: 14px;
    color: #4a5568;
    
    .link-highlight {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
      transition: color 0.3s ease;
      
      &:hover {
        color: #764ba2;
        text-decoration: underline;
      }
    }
  }
  
  .system-info {
    font-size: 12px;
    color: #a0aec0;
    line-height: 1.5;
  }
}

.test-accounts {
  margin-top: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  
  .test-title {
    margin: 0 0 20px 0;
    font-size: 16px;
    color: #2d3748;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .test-icon {
      width: 20px;
      height: 20px;
      opacity: 0.7;
    }
  }
  
  .account-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .account-item {
    padding: 12px 16px;
    background: white;
    border-radius: 8px;
    font-size: 14px;
    border: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .account-role {
      color: #4a5568;
      font-weight: 600;
    }
    
    .account-info {
      color: #667eea;
      font-family: 'Courier New', monospace;
      font-weight: 500;
    }
  }
}

/* 加载动画 */
.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-header .title {
    font-size: 24px;
  }
  
  .form-options {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

// 密码输入错误样式
.input-wrapper .form-input.input-error,
input.form-input.input-error {
  border: 2px solid #e53e3e !important;
  box-shadow: 0 0 0 2px rgba(229,62,62,0.15) !important;
}
.error-tip {
  color: #e53e3e;
  font-size: 13px;
  margin-top: 4px;
  margin-left: 2px;
}
</style> 