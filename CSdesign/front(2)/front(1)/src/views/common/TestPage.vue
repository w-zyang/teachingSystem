<template>
  <div class="test-page">
    <div class="test-container">
      <h1>系统连接测试</h1>
      
      <div class="test-section">
        <h2>后端连接测试</h2>
        <button @click="testBackendConnection" :disabled="testing">
          {{ testing ? '测试中...' : '测试后端连接' }}
        </button>
        <div v-if="backendResult" class="result" :class="backendResult.success ? 'success' : 'error'">
          {{ backendResult.message }}
        </div>
      </div>
      
      <div class="test-section">
        <h2>登录功能测试</h2>
        <div class="login-test">
          <div class="test-account">
            <h3>测试账号</h3>
            <div class="account-item">
              <strong>管理员:</strong> admin / 123456
              <button @click="testLogin('admin', 'admin', '123456')" :disabled="loginTesting">
                测试登录
              </button>
            </div>
            <div class="account-item">
              <strong>教师:</strong> teacher1 / 123456
              <button @click="testLogin('teacher', 'teacher1', '123456')" :disabled="loginTesting">
                测试登录
              </button>
            </div>
            <div class="account-item">
              <strong>学生:</strong> student1 / 123456
              <button @click="testLogin('student', 'student1', '123456')" :disabled="loginTesting">
                测试登录
              </button>
            </div>
          </div>
        </div>
        <div v-if="loginResult" class="result" :class="loginResult.success ? 'success' : 'error'">
          {{ loginResult.message }}
        </div>
      </div>
      
      <div class="test-section">
        <h2>系统信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <strong>前端地址:</strong> {{ frontendUrl }}
          </div>
          <div class="info-item">
            <strong>后端地址:</strong> {{ backendUrl }}
          </div>
          <div class="info-item">
            <strong>当前时间:</strong> {{ currentTime }}
          </div>
          <div class="info-item">
            <strong>浏览器:</strong> {{ userAgent }}
          </div>
        </div>
      </div>
      
      <div class="actions">
        <router-link to="/login" class="btn btn-primary">前往登录页面</router-link>
        <router-link to="/landing" class="btn btn-secondary">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { login } from '@/api/user'

const testing = ref(false)
const loginTesting = ref(false)
const backendResult = ref(null)
const loginResult = ref(null)
const currentTime = ref('')
const frontendUrl = ref('')
const backendUrl = ref('')
const userAgent = ref('')

// 测试后端连接
const testBackendConnection = async () => {
  testing.value = true
  backendResult.value = null
  
  try {
    // 尝试访问一个简单的API端点
    const response = await fetch('http://localhost:8080/api/user/stats')
    if (response.ok) {
      backendResult.value = {
        success: true,
        message: '后端连接成功！服务器正常运行。'
      }
    } else {
      backendResult.value = {
        success: false,
        message: `后端连接失败！状态码: ${response.status}`
      }
    }
  } catch (error) {
    backendResult.value = {
      success: false,
      message: `后端连接失败！错误: ${error.message}`
    }
  } finally {
    testing.value = false
  }
}

// 测试登录功能
const testLogin = async (role, username, password) => {
  loginTesting.value = true
  loginResult.value = null
  
  try {
    const loginData = {
      username: username,
      password: password,
      role: role
    }
    
    console.log('测试登录:', loginData)
    
    const res = await login(loginData)
    
    if (res && res.code === 200) {
      loginResult.value = {
        success: true,
        message: `${role}登录测试成功！用户信息: ${res.data.user?.realName || username}`
      }
    } else {
      loginResult.value = {
        success: false,
        message: `${role}登录测试失败！错误: ${res?.msg || '未知错误'}`
      }
    }
  } catch (error) {
    loginResult.value = {
      success: false,
      message: `${role}登录测试失败！错误: ${error.message}`
    }
  } finally {
    loginTesting.value = false
  }
}

// 更新当前时间
const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

onMounted(() => {
  // 初始化系统信息
  frontendUrl.value = window.location.origin
  backendUrl.value = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  userAgent.value = navigator.userAgent
  
  // 更新时间
  updateTime()
  setInterval(updateTime, 1000)
})
</script>

<style lang="scss" scoped>
.test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.test-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 40px;
  font-size: 32px;
}

.test-section {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  
  h2 {
    color: #495057;
    margin-bottom: 20px;
    font-size: 24px;
  }
}

button {
  padding: 12px 24px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  margin: 5px;
  
  &:hover {
    background: #0056b3;
  }
  
  &:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
}

.result {
  margin-top: 15px;
  padding: 12px;
  border-radius: 6px;
  font-weight: 500;
  
  &.success {
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
  }
  
  &.error {
    background: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
  }
}

.login-test {
  .test-account {
    h3 {
      margin-bottom: 15px;
      color: #495057;
    }
  }
  
  .account-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    background: #f8f9fa;
    border-radius: 6px;
    margin-bottom: 10px;
    
    strong {
      color: #495057;
    }
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  
  strong {
    color: #495057;
  }
}

.actions {
  text-align: center;
  margin-top: 40px;
  
  .btn {
    display: inline-block;
    padding: 12px 24px;
    margin: 0 10px;
    border-radius: 6px;
    text-decoration: none;
    font-weight: 500;
    
    &.btn-primary {
      background: #007bff;
      color: white;
      
      &:hover {
        background: #0056b3;
      }
    }
    
    &.btn-secondary {
      background: #6c757d;
      color: white;
      
      &:hover {
        background: #545b62;
      }
    }
  }
}

@media (max-width: 768px) {
  .test-container {
    padding: 20px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .account-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 