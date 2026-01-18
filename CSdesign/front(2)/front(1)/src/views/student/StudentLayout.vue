<template>
  <div class="student-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ 'collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo">
          <div class="logo-glow"></div>
        </div>
        <h3 v-if="!sidebarCollapsed" class="sidebar-title">学习中心</h3>
      </div>
      
      <nav class="sidebar-nav">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ 'active': $route.path === item.path }"
        >
          <div class="nav-icon-wrapper">
            <img :src="item.iconSrc" :alt="item.name" class="nav-icon">
            <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
          </div>
          <span v-if="!sidebarCollapsed" class="nav-text">{{ item.name }}</span>
          <div class="nav-indicator" v-if="!sidebarCollapsed"></div>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <button @click="toggleSidebar" class="toggle-btn">
          <img src="@/assets/search.png" alt="切换" class="toggle-icon">
        </button>
      </div>
    </aside>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="top-bar-left">
          <h2 class="page-title">{{ currentPageTitle }}</h2>
          <div class="breadcrumb">
            <span class="breadcrumb-item">学生中心</span>
            <span class="breadcrumb-separator">/</span>
            <span class="breadcrumb-item">{{ currentPageTitle }}</span>
          </div>
        </div>
        
        <div class="top-bar-right">
          <!-- 通知区域 -->
          <div class="notifications">
            <button class="notification-btn">
              <img src="@/assets/time.png" alt="通知" class="notification-icon">
              <span class="notification-badge" v-if="notificationCount > 0">{{ notificationCount }}</span>
            </button>
          </div>
          
          <!-- 学生信息 -->
          <div class="user-info">
            <div class="user-avatar" @click="toggleUserMenu">
              <div class="avatar-container">
                <img src="@/assets/author.png" alt="Avatar" class="avatar">
                <div class="avatar-status"></div>
              </div>
              <div class="user-details" v-if="!sidebarCollapsed">
                <span class="username">{{ username }}</span>
                <span class="role">学生</span>
              </div>
              <img src="@/assets/category.png" alt="下拉" class="dropdown-icon">
            </div>
            
            <!-- 用户菜单 -->
            <teleport to="body">
              <div 
                class="user-menu" 
                v-if="showUserMenu" 
                @click.stop
                :style="userMenuStyle"
              >
                <div class="menu-items">
                  <div class="menu-item" @click="showProfile">
                    <img src="@/assets/author.png" alt="个人资料" class="menu-icon">
                    <span>个人资料</span>
                  </div>
                  <div class="menu-item" @click="showSettings">
                    <img src="@/assets/tag.png" alt="设置" class="menu-icon">
                    <span>设置</span>
                  </div>
                  <div class="menu-item" @click="goToChangePwd">
                    <img src="@/assets/balance.png" alt="修改密码" class="menu-icon">
                    <span>修改密码</span>
                  </div>
                </div>
                <div class="menu-divider"></div>
                <div class="menu-item logout-item" @click="logout">
                  <img src="@/assets/delete.png" alt="退出" class="menu-icon">
                  <span>退出登录</span>
                </div>
              </div>
            </teleport>
          </div>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <main class="page-content">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getUserById } from '@/api/user'

const router = useRouter()

// 从本地存储获取用户信息
const userId = localStorage.getItem('userId') || '17' // 默认使用学生ID 17
const username = ref(localStorage.getItem('username') || 'student')
const realName = ref(localStorage.getItem('realName') || '李同学')
const userRole = ref(localStorage.getItem('userRole') || 'student')

// 响应式变量
const sidebarCollapsed = ref(false)
const userInfo = ref(null)
const notifications = ref([])
const showUserMenu = ref(false)
const notificationCount = ref(2)
const userMenuStyle = ref({})

// 计算属性
const currentPageTitle = computed(() => {
  const route = router.currentRoute.value
  const menuItem = menuItems.value.find(item => item.path === route.path)
  return menuItem ? menuItem.title : '学习中心'
})

// 计算用户菜单位置
const calculateUserMenuPosition = () => {
  const avatarElement = document.querySelector('.user-avatar')
  if (avatarElement) {
    const rect = avatarElement.getBoundingClientRect()
    userMenuStyle.value = {
      position: 'fixed',
      top: `${rect.bottom + 8}px`,
      right: `${window.innerWidth - rect.right}px`,
      zIndex: '99999'
    }
  }
}

// 导航菜单
const menuItems = ref([
  {
    id: 'dashboard',
    name: '学习中心',
    title: '学习中心',
    iconSrc: '/src/assets/author.png',
    path: '/student/dashboard',
    badge: null
  },
  {
    id: 'online-learning',
    name: '在线学习',
    title: '在线学习',
    iconSrc: '/src/assets/category.png',
    path: '/student/online-learning',
    badge: null
  },
  {
    id: 'practice-evaluation',
    name: '练习评估',
    title: '练习评估',
    iconSrc: '/src/assets/balance.png',
    path: '/student/practice-evaluation',
    badge: null
  },
  {
    id: 'extended-learning',
    name: '扩展学习',
    title: '扩展学习',
    iconSrc: '/src/assets/time.png',
    path: '/student/extended-learning',
    badge: null
  },
  {
    id: 'learning-assistant',
    name: '学习助手',
    title: '学习助手',
    iconSrc: '/src/assets/search.png',
    path: '/student/learning-assistant',
    badge: null
  },
  {
    id: 'intelligent-learning',
    name: '智能学习',
    title: '智能学习',
    iconSrc: '/src/assets/local.png',
    path: '/student/intelligent-learning',
    badge: 'NEW'
  },
  {
    id: 'error-question-training',
    name: '错题训练',
    title: '智能错题训练',
    iconSrc: '/src/assets/balance.png',
    path: '/student/error-question-training',
    badge: null
  },
  {
    id: 'class-summary-viewer',
    name: '课堂重点',
    title: '课堂重点整理',
    iconSrc: '/src/assets/title.png',
    path: '/student/class-summary-viewer',
    badge: null
  },
  {
    id: 'vr-classroom',
    name: 'VR教室',
    title: 'VR虚拟教室',
    iconSrc: '/src/assets/VX.png',
    path: '/student/vr-classroom',
    badge: 'VR'
  },
  {
    id: 'profile',
    name: '个人中心',
    title: '个人中心',
    iconSrc: '/src/assets/tag.png',
    path: '/student/profile',
    badge: null
  }
])

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    console.log('开始获取用户信息，用户ID:', userId)
    const res = await getUserById(userId)
    console.log('用户信息API响应:', res)
    
    if (res && res.data) {
      userInfo.value = res.data
      // 更新本地存储的用户信息
      username.value = res.data.realName || res.data.username || username.value
      realName.value = res.data.realName || realName.value
      userRole.value = res.data.role || userRole.value
      
      localStorage.setItem('username', username.value)
      localStorage.setItem('realName', realName.value)
      localStorage.setItem('userRole', userRole.value)
      
      console.log('用户信息:', res.data)
    } else {
      console.warn('用户信息API响应数据为空，使用本地存储信息')
      // 使用本地存储的信息
      userInfo.value = {
        id: userId,
        username: username.value,
        realName: realName.value,
        role: userRole.value,
        avatar: ''
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    console.error('错误详情:', error.response?.data)
    // 使用本地存储的信息作为默认数据
    userInfo.value = {
      id: userId,
      username: username.value,
      realName: realName.value,
      role: userRole.value,
      avatar: ''
    }
  }
}

// 获取通知
const fetchNotifications = async () => {
  try {
    // 这里可以调用通知API
    // const res = await getNotifications(userId)
    // notifications.value = res.data
    
    // 使用模拟数据
    notifications.value = [
      {
        id: 1,
        title: '新课程上线',
        content: '数据结构与算法课程已上线',
        time: '2小时前',
        read: false
      },
      {
        id: 2,
        title: '练习提醒',
        content: '你有3个练习待完成',
        time: '1天前',
        read: true
      }
    ]
    
    // 计算未读通知数量
    notificationCount.value = notifications.value.filter(n => !n.read).length
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
  if (showUserMenu.value) {
    // 等待DOM更新后计算位置
    nextTick(() => {
      calculateUserMenuPosition()
    })
  }
}

// 显示个人资料
const showProfile = () => {
  router.push('/student/profile')
  showUserMenu.value = false
}

// 显示设置
const showSettings = () => {
  // 可以跳转到设置页面
  showUserMenu.value = false
}

// 修改密码
const goToChangePwd = () => {
  router.push('/change-pwd')
  showUserMenu.value = false
}

// 退出登录
const logout = () => {
  // 清除所有本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('realName')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userEmail')
  
  // 跳转到登录页
  router.push('/login')
}

// 标记通知为已读
const markNotificationAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId)
  if (notification) {
    notification.read = true
    notificationCount.value = notifications.value.filter(n => !n.read).length
  }
}

// 页面加载时获取数据
onMounted(async () => {
  try {
    await Promise.all([
      fetchUserInfo(),
      fetchNotifications()
    ])
  } catch (error) {
    console.error('页面初始化失败:', error)
  }
})
</script>

<style lang="scss" scoped>
.student-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
    pointer-events: none;
  }
  
  &.collapsed {
    width: 80px;
    
    .sidebar-title {
      opacity: 0;
      transform: translateX(-20px);
    }
    
    .nav-text {
      opacity: 0;
      transform: translateX(-20px);
    }
    
    .nav-indicator {
      opacity: 0;
    }
  }
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
  
  .logo-container {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .logo {
    width: 40px;
    height: 40px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    transition: transform 0.3s ease;
    
    &:hover {
      transform: scale(1.1) rotate(5deg);
    }
  }
  
  .logo-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 60px;
    height: 60px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
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
  
  .sidebar-title {
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    transition: all 0.3s ease;
    background: linear-gradient(135deg, #ffffff, #f0f0f0);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  position: relative;
  z-index: 1;
  
  .nav-item {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    margin: 4px 12px;
    border-radius: 12px;
    text-decoration: none;
    color: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      color: white;
      transform: translateX(4px);
      
      &::before {
        left: 100%;
      }
      
      .nav-icon {
        transform: scale(1.1);
      }
    }
    
    &.active {
      background: rgba(255, 255, 255, 0.2);
      color: white;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      
      .nav-icon-wrapper {
        background: rgba(255, 255, 255, 0.2);
      }
      
      .nav-indicator {
        opacity: 1;
        transform: scaleX(1);
      }
    }
    
    .nav-icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.1);
      position: relative;
      
      .nav-icon {
        width: 20px;
        height: 20px;
        opacity: 0.9;
        transition: transform 0.3s ease;
      }
      
      .nav-badge {
        position: absolute;
        top: -6px;
        right: -6px;
        background: linear-gradient(135deg, #ff4757, #ff3742);
        color: white;
        font-size: 10px;
        font-weight: 700;
        padding: 2px 6px;
        border-radius: 8px;
        min-width: 16px;
        height: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 2px solid rgba(255, 255, 255, 0.9);
        box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
        animation: pulse 2s infinite;
      }
    }
    
    .nav-text {
      font-weight: 600;
      font-size: 14px;
      transition: all 0.3s ease;
    }
    
    .nav-indicator {
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%) scaleX(0);
      width: 4px;
      height: 20px;
      background: white;
      border-radius: 2px;
      opacity: 0;
      transition: all 0.3s ease;
    }
  }
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
  
  .toggle-btn {
    width: 100%;
    height: 40px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 10px;
    color: white;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: scale(1.05);
    }
    
    .toggle-icon {
      width: 16px;
      height: 16px;
      opacity: 0.8;
      transition: transform 0.3s ease;
    }
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  .top-bar-left {
    .page-title {
      font-size: 28px;
      font-weight: 800;
      color: #2d3748;
      margin: 0 0 4px 0;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    
    .breadcrumb {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      
      .breadcrumb-item {
        color: #718096;
        font-weight: 500;
        
        &:last-child {
          color: #667eea;
          font-weight: 600;
        }
      }
      
      .breadcrumb-separator {
        color: #cbd5e0;
      }
    }
  }
  
  .top-bar-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .notifications {
      .notification-btn {
        position: relative;
        width: 44px;
        height: 44px;
        background: linear-gradient(135deg, #f7fafc, #edf2f7);
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        
        .notification-icon {
          width: 20px;
          height: 20px;
          opacity: 0.7;
        }
        
        .notification-badge {
          position: absolute;
          top: -4px;
          right: -4px;
          background: #e53e3e;
          color: white;
          font-size: 10px;
          font-weight: 600;
          padding: 2px 6px;
          border-radius: 10px;
          min-width: 16px;
          text-align: center;
        }
      }
    }
    
    .user-info {
      position: relative;
      
      .user-avatar {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px 16px;
        background: linear-gradient(135deg, #f7fafc, #edf2f7);
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        
        .avatar-container {
          position: relative;
          
          .avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #e2e8f0;
          }
          
          .avatar-status {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #48bb78;
            border: 2px solid white;
          }
        }
        
        .user-details {
          display: flex;
          flex-direction: column;
          
          .username {
            font-weight: 600;
            color: #2d3748;
            font-size: 14px;
          }
          
          .role {
            font-size: 12px;
            color: #718096;
          }
        }
        
        .dropdown-icon {
          width: 16px;
          height: 16px;
          opacity: 0.6;
          transition: transform 0.3s ease;
        }
      }
      
      .user-menu {
        position: absolute;
        top: 100%;
        right: 0;
        margin-top: 8px;
        background: white;
        border-radius: 16px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        border: 1px solid #e2e8f0;
        min-width: 240px;
        z-index: 99999;
        overflow: hidden;
        
        .menu-items {
          padding: 8px;
        }
        
        .menu-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 16px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;
          color: #4a5568;
          
          &:hover {
            background: #f7fafc;
            color: #667eea;
          }
          
          .menu-icon {
            width: 18px;
            height: 18px;
            opacity: 0.7;
          }
          
          span {
            font-size: 14px;
            font-weight: 500;
          }
          
          &.logout-item {
            color: #e53e3e;
            
            &:hover {
              background: #fed7d7;
              color: #c53030;
            }
          }
        }
        
        .menu-divider {
          height: 1px;
          background: #e2e8f0;
          margin: 8px 0;
        }
      }
    }
  }
}

.page-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    
    &.collapsed {
      transform: translateX(0);
    }
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .top-bar {
    padding: 16px 20px;
    
    .top-bar-left .page-title {
      font-size: 24px;
    }
  }
  
  .page-content {
    padding: 20px;
  }
}

// 动画定义
@keyframes pulse {
  0% {
    box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
    transform: scale(1);
  }
  50% {
    box-shadow: 0 4px 8px rgba(255, 71, 87, 0.5);
    transform: scale(1.05);
  }
  100% {
    box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
    transform: scale(1);
  }
}
</style>

<style>
.user-menu {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  min-width: 180px;
  padding: 12px 0;
  margin-top: 8px;
  z-index: 99999;
}
.user-menu .menu-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
.user-menu .menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.user-menu .menu-item:hover {
  background: #f8fafc;
}
.user-menu .menu-icon {
  width: 20px;
  height: 20px;
}
.user-menu .menu-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 4px 0;
}
</style> 