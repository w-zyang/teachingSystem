<template>
  <div class="change-pwd-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <div class="change-pwd-content">
      <div class="change-pwd-card">
        <div class="change-pwd-header">
          <router-link to="/landing" class="logo-link">
            <div class="logo-container">
              <img src="@/assets/logo.png" alt="Logo" class="logo">
              <div class="logo-glow"></div>
            </div>
          </router-link>
          <h1 class="title">修改密码</h1>
          <p class="subtitle">为了账户安全，请设置新的密码</p>
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">安全验证</span>
            <span class="divider-line"></span>
          </div>
        </div>
        
        <form @submit.prevent="handleChangePassword" class="change-pwd-form">
          <div class="form-group">
            <label for="oldPassword" class="form-label">
              <img src="@/assets/balance.png" alt="原密码" class="label-icon">
              当前密码
            </label>
            <div class="input-wrapper">
              <input 
                type="password" 
                id="oldPassword" 
                v-model="form.oldPassword" 
                placeholder="请输入当前密码"
                required
                class="form-input"
              >
              <div class="input-focus-border"></div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="newPassword" class="form-label">
              <img src="@/assets/balance.png" alt="新密码" class="label-icon">
              新密码
            </label>
            <div class="input-wrapper">
              <input 
                type="password" 
                id="newPassword" 
                v-model="form.newPassword" 
                placeholder="请输入新密码"
                required
                class="form-input"
              >
              <div class="input-focus-border"></div>
            </div>
            <div class="password-strength">
              <div class="strength-bar">
                <div class="strength-fill" :class="passwordStrengthClass"></div>
              </div>
              <span class="strength-text">{{ passwordStrengthText }}</span>
            </div>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword" class="form-label">
              <img src="@/assets/balance.png" alt="确认密码" class="label-icon">
              确认新密码
            </label>
            <div class="input-wrapper">
              <input 
                type="password" 
                id="confirmPassword" 
                v-model="form.confirmPassword" 
                placeholder="请再次输入新密码"
                required
                class="form-input"
              >
              <div class="input-focus-border"></div>
            </div>
            <div class="password-match" v-if="form.confirmPassword">
              <span class="match-icon" :class="{ 'match': passwordsMatch, 'unmatch': !passwordsMatch }">
                {{ passwordsMatch ? '✓' : '✗' }}
              </span>
              <span class="match-text" :class="{ 'match': passwordsMatch, 'unmatch': !passwordsMatch }">
                {{ passwordsMatch ? '密码匹配' : '密码不匹配' }}
              </span>
            </div>
          </div>
          
          <div class="password-tips">
            <h4 class="tips-title">
              <img src="@/assets/tag.png" alt="提示" class="tips-icon">
              密码要求
            </h4>
            <ul class="tips-list">
              <li class="tip-item" :class="{ 'valid': form.newPassword.length >= 8 }">
                <span class="tip-icon">{{ form.newPassword.length >= 8 ? '✓' : '○' }}</span>
                至少8个字符
              </li>
              <li class="tip-item" :class="{ 'valid': hasUpperCase }">
                <span class="tip-icon">{{ hasUpperCase ? '✓' : '○' }}</span>
                包含大写字母
              </li>
              <li class="tip-item" :class="{ 'valid': hasLowerCase }">
                <span class="tip-icon">{{ hasLowerCase ? '✓' : '○' }}</span>
                包含小写字母
              </li>
              <li class="tip-item" :class="{ 'valid': hasNumber }">
                <span class="tip-icon">{{ hasNumber ? '✓' : '○' }}</span>
                包含数字
              </li>
              <li class="tip-item" :class="{ 'valid': hasSpecialChar }">
                <span class="tip-icon">{{ hasSpecialChar ? '✓' : '○' }}</span>
                包含特殊字符
              </li>
            </ul>
          </div>
          
          <button type="submit" class="btn btn-primary change-pwd-btn" :disabled="loading || !canSubmit">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-else class="btn-icon">🔒</span>
            {{ loading ? '修改中...' : '确认修改' }}
          </button>
        </form>
        
        <div class="change-pwd-footer">
          <p class="back-link">
            <router-link to="/login" class="link-highlight">← 返回登录</router-link>
          </p>
          <p class="system-info">密码修改成功后，请使用新密码登录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { changePassword } from '@/api/user'

const router = useRouter()

// 响应式变量
const loading = ref(false)
const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证
const errors = ref({})

// 验证表单
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.oldPassword) {
    errors.value.oldPassword = '请输入当前密码'
  }
  
  if (!form.value.newPassword) {
    errors.value.newPassword = '请输入新密码'
  } else if (form.value.newPassword.length < 6) {
    errors.value.newPassword = '新密码至少6个字符'
  } else if (form.value.newPassword === form.value.oldPassword) {
    errors.value.newPassword = '新密码不能与当前密码相同'
  }
  
  if (!form.value.confirmPassword) {
    errors.value.confirmPassword = '请确认新密码'
  } else if (form.value.newPassword !== form.value.confirmPassword) {
    errors.value.confirmPassword = '两次输入的新密码不一致'
  }
  
  return Object.keys(errors.value).length === 0
}

// 处理修改密码
const handleChangePassword = async () => {
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    const userId = localStorage.getItem('userId') || '1'
    const changePasswordData = {
      userId: userId,
      oldPassword: form.value.oldPassword,
      newPassword: form.value.newPassword
    }
    
    const res = await changePassword(changePasswordData)
    
    if (res && res.data) {
      console.log('密码修改成功:', res.data)
      alert('密码修改成功！')
      
      // 清空表单
      form.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      
      // 根据用户角色跳转到对应页面
      const userRole = localStorage.getItem('userRole')
      switch (userRole) {
        case 'teacher':
          router.push('/teacher/dashboard')
          break
        case 'student':
          router.push('/student/dashboard')
          break
        case 'admin':
          router.push('/admin/dashboard')
          break
        default:
          router.push('/login')
      }
    } else {
      alert('密码修改失败，请重试')
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    if (error.response && error.response.data) {
      alert(error.response.data.message || '密码修改失败，请重试')
    } else {
      alert('密码修改失败，请检查网络连接或联系管理员')
    }
  } finally {
    loading.value = false
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

// 返回上一页
const goBack = () => {
  const userRole = localStorage.getItem('userRole')
  switch (userRole) {
    case 'teacher':
      router.push('/teacher/dashboard')
      break
    case 'student':
      router.push('/student/dashboard')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    default:
      router.push('/login')
  }
}

// 获取密码强度
const passwordStrength = computed(() => {
  const password = form.value.newPassword
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 8) strength += 1
  if (/[A-Z]/.test(password)) strength += 1
  if (/[a-z]/.test(password)) strength += 1
  if (/[0-9]/.test(password)) strength += 1
  if (/[^A-Za-z0-9]/.test(password)) strength += 1
  
  return strength
})

// 获取密码强度类
const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return 'weak'
  if (strength <= 3) return 'medium'
  if (strength <= 4) return 'strong'
  return 'very-strong'
})

// 获取密码强度文本
const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return '弱'
  if (strength <= 3) return '中等'
  if (strength <= 4) return '强'
  return '很强'
})

// 获取表单验证状态
const isFormValid = computed(() => {
  return form.value.oldPassword && 
         form.value.newPassword && 
         form.value.confirmPassword && 
         form.value.newPassword === form.value.confirmPassword &&
         passwordStrength.value >= 3
})

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('username')
  router.push('/login')
  alert('已退出登录')
}
</script>

<style lang="scss" scoped>
.change-pwd-container {
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

.change-pwd-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 500px;
}

.change-pwd-card {
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

.change-pwd-header {
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

.change-pwd-form {
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
    
    .input-wrapper {
      position: relative;
      
      .form-input {
        width: 100%;
        padding: 16px 20px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        font-size: 16px;
        transition: all 0.3s ease;
        background: #f7fafc;
        color: #2d3748;
        
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
  }
}

.password-strength {
  margin-top: 12px;
  
  .strength-bar {
    width: 100%;
    height: 4px;
    background: #e2e8f0;
    border-radius: 2px;
    overflow: hidden;
    margin-bottom: 8px;
    
    .strength-fill {
      height: 100%;
      transition: all 0.3s ease;
      border-radius: 2px;
      
      &.weak {
        width: 33%;
        background: #f56565;
      }
      
      &.medium {
        width: 66%;
        background: #ed8936;
      }
      
      &.strong {
        width: 100%;
        background: #48bb78;
      }
    }
  }
  
  .strength-text {
    font-size: 12px;
    color: #718096;
    font-weight: 500;
  }
}

.password-match {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  
  .match-icon {
    font-size: 16px;
    font-weight: bold;
    
    &.match {
      color: #48bb78;
    }
    
    &.unmatch {
      color: #f56565;
    }
  }
  
  .match-text {
    font-size: 12px;
    font-weight: 500;
    
    &.match {
      color: #48bb78;
    }
    
    &.unmatch {
      color: #f56565;
    }
  }
}

.password-tips {
  margin: 24px 0;
  padding: 20px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  
  .tips-title {
    margin: 0 0 16px 0;
    font-size: 16px;
    color: #2d3748;
    display: flex;
    align-items: center;
    gap: 8px;
    
    .tips-icon {
      width: 18px;
      height: 18px;
      opacity: 0.7;
    }
  }
  
  .tips-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    
    @media (max-width: 480px) {
      grid-template-columns: 1fr;
    }
  }
  
  .tip-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #718096;
    transition: color 0.3s ease;
    
    &.valid {
      color: #48bb78;
    }
    
    .tip-icon {
      font-size: 16px;
      font-weight: bold;
    }
  }
}

.change-pwd-btn {
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

.change-pwd-footer {
  text-align: center;
  margin-top: 32px;
  
  .back-link {
    margin: 16px 0;
    font-size: 14px;
    
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

@media (max-width: 480px) {
  .change-pwd-card {
    padding: 30px 20px;
  }
  
  .change-pwd-header .title {
    font-size: 24px;
  }
  
  .tips-list {
    grid-template-columns: 1fr;
  }
}
</style> 