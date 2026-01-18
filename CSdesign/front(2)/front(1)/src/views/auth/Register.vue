<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <div class="register-content">
      <div class="register-card">
        <div class="register-header">
          <router-link to="/landing" class="logo-link">
            <div class="logo-container">
              <img src="@/assets/logo.png" alt="Logo" class="logo">
              <div class="logo-glow"></div>
            </div>
          </router-link>
          <h1 class="title">创建新账户</h1>
          <p class="subtitle">加入智能教学管理系统，开启学习之旅</p>
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">填写信息</span>
            <span class="divider-line"></span>
          </div>
        </div>
        
        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-row">
            <div class="form-group">
              <label for="role" class="form-label">
                <img src="@/assets/category.png" alt="角色" class="label-icon">
                角色选择
              </label>
              <div class="select-wrapper">
                <select id="role" v-model="form.role" required class="form-select">
                  <option value="">请选择角色</option>
                  <option value="teacher">👨‍🏫 教师</option>
                  <option value="student">👨‍🎓 学生</option>
                </select>
                <div class="select-arrow"></div>
              </div>
            </div>
          </div>

          <div class="form-row">
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
              <label for="realName" class="form-label">
                <img src="@/assets/title.png" alt="姓名" class="label-icon">
                真实姓名
              </label>
              <div class="input-wrapper">
                <input 
                  type="text" 
                  id="realName" 
                  v-model="form.realName" 
                  placeholder="请输入真实姓名"
                  required
                  class="form-input"
                >
                <div class="input-focus-border"></div>
              </div>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="email" class="form-label">
                <img src="@/assets/email.png" alt="邮箱" class="label-icon">
                邮箱地址
              </label>
              <div class="input-wrapper">
                <input 
                  type="email" 
                  id="email" 
                  v-model="form.email" 
                  placeholder="请输入邮箱地址"
                  required
                  class="form-input"
                >
                <div class="input-focus-border"></div>
              </div>
            </div>
            
            <div class="form-group">
              <label for="phone" class="form-label">
                <img src="@/assets/local.png" alt="电话" class="label-icon">
                手机号码
              </label>
              <div class="input-wrapper">
                <input 
                  type="tel" 
                  id="phone" 
                  v-model="form.phone" 
                  placeholder="请输入手机号码"
                  required
                  class="form-input"
                >
                <div class="input-focus-border"></div>
              </div>
            </div>
          </div>
          
          <div class="form-row">
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
                  class="form-input"
                >
                <div class="input-focus-border"></div>
              </div>
            </div>
            
            <div class="form-group">
              <label for="confirmPassword" class="form-label">
                <img src="@/assets/balance.png" alt="确认密码" class="label-icon">
                确认密码
              </label>
              <div class="input-wrapper">
                <input 
                  type="password" 
                  id="confirmPassword" 
                  v-model="form.confirmPassword" 
                  placeholder="请再次输入密码"
                  required
                  class="form-input"
                >
                <div class="input-focus-border"></div>
              </div>
            </div>
          </div>
          
          <div class="form-options">
            <label class="checkbox-wrapper">
              <input type="checkbox" v-model="form.agree" class="checkbox-input" required>
              <span class="checkbox-custom"></span>
              <span class="checkbox-label">我已阅读并同意 <a href="#" class="terms-link">服务条款</a> 和 <a href="#" class="terms-link">隐私政策</a></span>
            </label>
          </div>
          
          <button type="submit" class="btn btn-primary register-btn" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-else class="btn-icon">🎉</span>
            {{ loading ? '注册中...' : '立即注册' }}
          </button>
        </form>
        
        <div class="register-footer">
          <p class="login-link">已有账号？ <router-link to="/login" class="link-highlight">立即登录</router-link></p>
          <p class="system-info">注册即表示您同意我们的服务条款和隐私政策</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

const router = useRouter()

// 响应式变量
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  role: '',
  agreeTerms: false
})

// 表单验证
const errors = ref({})

// 验证表单
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.username) {
    errors.value.username = '请输入用户名'
  } else if (form.value.username.length < 3) {
    errors.value.username = '用户名至少3个字符'
  }
  
  if (!form.value.password) {
    errors.value.password = '请输入密码'
  } else if (form.value.password.length < 6) {
    errors.value.password = '密码至少6个字符'
  }
  
  if (!form.value.confirmPassword) {
    errors.value.confirmPassword = '请确认密码'
  } else if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = '两次输入的密码不一致'
  }
  
  if (!form.value.realName) {
    errors.value.realName = '请输入真实姓名'
  }
  
  if (!form.value.email) {
    errors.value.email = '请输入邮箱'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    errors.value.email = '请输入有效的邮箱地址'
  }
  
  if (!form.value.phone) {
    errors.value.phone = '请输入手机号'
  } else if (!/^1[3-9]\d{9}$/.test(form.value.phone)) {
    errors.value.phone = '请输入有效的手机号'
  }
  
  if (!form.value.role) {
    errors.value.role = '请选择角色'
  }
  
  if (!form.value.agreeTerms) {
    errors.value.agreeTerms = '请同意用户协议和隐私政策'
  }
  
  return Object.keys(errors.value).length === 0
}

// 处理注册
const handleRegister = async () => {
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    const registerData = {
      username: form.value.username,
      password: form.value.password,
      realName: form.value.realName,
      email: form.value.email,
      phone: form.value.phone,
      role: form.value.role
    }
    
    const res = await register(registerData)
    
    if (res && res.data) {
      console.log('注册成功:', res.data)
      alert('注册成功！请登录')
      router.push('/login')
    } else {
      alert('注册失败，请重试')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.response && error.response.data) {
      alert(error.response.data.message || '注册失败，请重试')
    } else {
      alert('注册失败，请检查网络连接或联系管理员')
    }
  } finally {
    loading.value = false
  }
}

// 检查用户名是否可用
const checkUsername = async () => {
  if (!form.value.username || form.value.username.length < 3) {
    return
  }
  
  try {
    // 这里可以调用检查用户名API
    // const res = await checkUsernameAvailable(form.value.username)
    // 暂时使用模拟检查
    console.log('检查用户名:', form.value.username)
  } catch (error) {
    console.error('检查用户名失败:', error)
  }
}

// 获取错误信息
const getErrorMessage = (field) => {
  return errors.value[field] || ''
}

// 清除错误信息
const clearError = (field) => {
  if (errors.value[field]) {
    delete errors.value[field]
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
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

.register-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 50px 60px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  width: 100%;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2);
  }
}

.register-header {
  text-align: center;
  margin-bottom: 50px;
  
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

.register-form {
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 24px;
    
    // 角色选择单独一行
    &:first-child {
      grid-template-columns: 1fr;
      max-width: 100%;
    }
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 20px;
      margin-bottom: 20px;
    }
  }
  
  .form-group {
    position: relative;
    
    .form-label {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      font-weight: 600;
      color: #2d3748;
      font-size: 15px;
      
      .label-icon {
        width: 18px;
        height: 18px;
        margin-right: 8px;
        opacity: 0.7;
      }
    }
    
    .input-wrapper, .select-wrapper {
      position: relative;
      
      .form-input, .form-select {
        width: 100%;
        padding: 18px 22px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        font-size: 16px;
        transition: all 0.3s ease;
        background: #f7fafc;
        color: #2d3748;
        min-height: 56px;
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
  margin: 28px 0;
  
  .checkbox-wrapper {
    display: flex;
    align-items: flex-start;
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
      margin-top: 2px;
      position: relative;
      transition: all 0.3s ease;
      background: white;
      flex-shrink: 0;
      
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
      line-height: 1.5;
      
      .terms-link {
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
  }
}

.register-btn {
  width: 100%;
  padding: 18px;
  font-size: 17px;
  font-weight: 600;
  margin-top: 28px;
  min-height: 56px;
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

.register-footer {
  text-align: center;
  margin-top: 32px;
  
  .login-link {
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

@media (max-width: 768px) {
  .register-container {
    padding: 20px 15px;
    align-items: flex-start;
  }
  
  .register-content {
    max-width: 100%;
  }
  
  .register-card {
    padding: 30px 20px;
  }
  
  .register-header {
    margin-bottom: 30px;
    
    .title {
      font-size: 24px;
    }
    
    .subtitle {
      font-size: 14px;
    }
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
    margin-bottom: 16px;
  }
  
  .form-group {
    .form-label {
      font-size: 13px;
      margin-bottom: 6px;
    }
    
    .input-wrapper .form-input,
    .select-wrapper .form-select {
      padding: 14px 16px;
      font-size: 15px;
    }
  }
  
  .form-options {
    margin: 20px 0;
    
    .checkbox-wrapper .checkbox-label {
      font-size: 13px;
    }
  }
  
  .register-btn {
    padding: 14px;
    font-size: 15px;
    margin-top: 20px;
  }
  
  .register-footer {
    margin-top: 24px;
    
    .login-link {
      font-size: 13px;
    }
    
    .system-info {
      font-size: 11px;
    }
  }
}
</style> 