<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo">
          <h3 v-if="!isSidebarCollapsed" class="logo-text">系统管理平台</h3>
        </div>
      </div>
      <nav class="sidebar-nav">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ 'active': $route.path === item.path }"
        >
          <div class="nav-icon">
            <img :src="item.iconSrc" :alt="item.name" class="icon-img">
          </div>
          <span v-if="!isSidebarCollapsed" class="nav-text">{{ item.name }}</span>
          <div class="nav-indicator"></div>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="toggleSidebar" class="toggle-btn">
          <img src="@/assets/search.png" alt="切换" class="toggle-icon">
        </button>
      </div>
    </aside>
    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-bar">
        <div class="top-bar-left">
          <div class="page-title">
            <img src="@/assets/author.png" alt="页面图标" class="title-icon">
            <h2>{{ currentPageTitle }}</h2>
          </div>
        </div>
        <div class="top-bar-right">
          <div class="notification-center">
            <button class="notification-btn">
              <img src="@/assets/time.png" alt="通知" class="notification-icon">
              <span class="notification-badge">{{ notifications.length }}</span>
            </button>
          </div>
          <div class="user-info">
            <div class="user-avatar" @click.stop="toggleUserMenu">
              <img src="@/assets/author.png" alt="Avatar" class="avatar">
              <div class="user-details">
                <span class="username">{{ userInfo?.realName || '管理员' }}</span>
                <span class="role">系统管理员</span>
              </div>
              <img src="@/assets/category.png" alt="下拉" class="dropdown-icon">
            </div>
            <teleport to="body">
              <div 
                class="user-menu" 
                v-if="showUserMenu" 
                @click.stop
                :style="userMenuStyle"
              >
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserById } from '@/api/user'

const router = useRouter()
const route = useRoute()

const isSidebarCollapsed = ref(false)
const showUserMenu = ref(false)
const userInfo = ref(null)
const notifications = ref([])
const userMenuStyle = ref({})

// 菜单项配置，图标与教师端保持一致
const menuItems = ref([
  { name: '仪表盘', path: '/admin/dashboard', iconSrc: '/src/assets/category.png' },
  { name: '用户管理', path: '/admin/users', iconSrc: '/src/assets/author.png' },
  { name: '资源管理', path: '/admin/resources', iconSrc: '/src/assets/search.png' },
  { name: '数据分析', path: '/admin/analytics', iconSrc: '/src/assets/balance.png' },
  { name: '系统设置', path: '/admin/settings', iconSrc: '/src/assets/tag.png' }
])

const currentPageTitle = computed(() => {
  const currentItem = menuItems.value.find(item => item.path === route.path)
  return currentItem ? currentItem.name : '系统管理平台'
})

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

const fetchUserInfo = async () => {
  try {
    const res = await getUserById(1)
    if (res && res.data) {
      userInfo.value = res.data
    }
  } catch (error) {
    userInfo.value = {
      username: 'admin',
      realName: '系统管理员',
      avatar: '',
      role: 'admin'
    }
  }
}

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
  if (showUserMenu.value) {
    nextTick(() => {
      calculateUserMenuPosition()
    })
  }
}

const showProfile = () => {
  router.push('/admin/profile')
  showUserMenu.value = false
}

const showSettings = () => {
  router.push('/admin/settings')
  showUserMenu.value = false
}

const goToChangePwd = () => {
  router.push('/change-pwd')
  showUserMenu.value = false
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

onMounted(() => {
  fetchUserInfo()
  document.addEventListener('click', (event) => {
    const userMenu = document.querySelector('.user-menu')
    const avatar = document.querySelector('.user-avatar')
    if (userMenu && !userMenu.contains(event.target) && !avatar?.contains(event.target)) {
      showUserMenu.value = false
    }
  })
})
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
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
  &.collapsed {
    width: 80px;
    .logo-text {
      opacity: 0;
      transform: translateX(-20px);
    }
    .nav-text {
      opacity: 0;
      transform: translateX(-20px);
    }
  }
  .sidebar-header {
    padding: 24px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    position: relative;
    z-index: 2;
    .logo-container {
      display: flex;
      align-items: center;
      gap: 12px;
      .logo {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      .logo-text {
        margin: 0;
        font-size: 1.2rem;
        font-weight: 700;
        transition: all 0.3s ease;
      }
    }
  }
  .sidebar-nav {
    flex: 1;
    padding: 16px 0;
    position: relative;
    z-index: 2;
    .nav-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px 24px;
      color: white;
      text-decoration: none;
      transition: all 0.3s ease;
      position: relative;
      margin: 4px 16px;
      border-radius: 12px;
      &:hover {
        background: rgba(255, 255, 255, 0.1);
        transform: translateX(4px);
      }
      &.active {
        background: rgba(255, 255, 255, 0.2);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        .nav-indicator {
          opacity: 1;
          transform: scale(1);
        }
      }
      .nav-icon {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        .icon-img {
          width: 20px;
          height: 20px;
          opacity: 0.9;
        }
      }
      .nav-text {
        font-size: 14px;
        font-weight: 600;
        transition: all 0.3s ease;
      }
      .nav-indicator {
        position: absolute;
        right: 12px;
        width: 6px;
        height: 6px;
        background: white;
        border-radius: 50%;
        opacity: 0;
        transform: scale(0);
        transition: all 0.3s ease;
      }
    }
  }
  .sidebar-footer {
    padding: 16px 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    position: relative;
    z-index: 2;
    .toggle-btn {
      width: 100%;
      height: 40px;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 8px;
      color: white;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        transform: translateY(-2px);
      }
      .toggle-icon {
        width: 16px;
        height: 16px;
        opacity: 0.8;
        transition: transform 0.3s ease;
      }
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
  background: white;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e2e8f0;
  .top-bar-left {
    .page-title {
      display: flex;
      align-items: center;
      gap: 12px;
      .title-icon {
        width: 32px;
        height: 32px;
        opacity: 0.7;
      }
      h2 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  .top-bar-right {
    display: flex;
    align-items: center;
    gap: 24px;
    .notification-center {
      .notification-btn {
        position: relative;
        width: 40px;
        height: 40px;
        background: #f7fafc;
        border: 1px solid #e2e8f0;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s ease;
        &:hover {
          background: #edf2f7;
          transform: translateY(-2px);
        }
        .notification-icon {
          width: 20px;
          height: 20px;
          opacity: 0.6;
        }
        .notification-badge {
          position: absolute;
          top: -4px;
          right: -4px;
          width: 18px;
          height: 18px;
          background: #e53e3e;
          color: white;
          border-radius: 50%;
          font-size: 10px;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: center;
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
        background: #f7fafc;
        border: 1px solid #e2e8f0;
        border-radius: 24px;
        cursor: pointer;
        transition: all 0.3s ease;
        &:hover {
          background: #edf2f7;
          transform: translateY(-2px);
        }
        .avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
        }
        .user-details {
          display: flex;
          flex-direction: column;
          .username {
            font-size: 14px;
            font-weight: 600;
            color: #2d3748;
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
        width: 280px;
        background: white;
        border-radius: 16px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
        border: 1px solid #e2e8f0;
        margin-top: 8px;
        z-index: 99999;
        overflow: hidden;
        .menu-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 16px 20px;
          cursor: pointer;
          transition: all 0.3s ease;
          &:hover {
            background: #f7fafc;
          }
          &.logout-item {
            color: #e53e3e;
            &:hover {
              background: #fed7d7;
            }
          }
          .menu-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
          }
          .menu-user-info {
            display: flex;
            flex-direction: column;
            .menu-username {
              font-size: 14px;
              font-weight: 600;
              color: #2d3748;
            }
            .menu-role {
              font-size: 12px;
              color: #718096;
            }
          }
          .menu-icon {
            width: 20px;
            height: 20px;
            opacity: 0.6;
          }
          span {
            font-size: 14px;
            color: #2d3748;
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
  padding: 24px;
  overflow-y: auto;
  background: #f7fafc;
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
    .top-bar-right {
      gap: 16px;
    }
  }
  .page-content {
    padding: 16px;
  }
}
</style> 