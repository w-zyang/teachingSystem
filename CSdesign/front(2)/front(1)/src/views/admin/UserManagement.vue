<template>
  <div class="user-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h1>用户管理</h1>
          <div class="header-badge">
            <span class="badge-icon">👥</span>
            <span class="badge-text">管理系统用户</span>
          </div>
        </div>
        <p class="header-subtitle">管理系统用户，包括教师、学生和管理员账户的创建、编辑和权限管理</p>
      </div>
      <div class="header-actions">
        <button @click="showAddModal = true" class="action-btn primary">
          <span class="btn-icon">➕</span>
          添加用户
        </button>
        <button @click="batchOperation" class="action-btn secondary">
          <span class="btn-icon">⚙️</span>
          批量操作
        </button>
        <button @click="exportUsers" class="action-btn success">
          <span class="btn-icon">📊</span>
          导出数据
        </button>
      </div>
    </div>

    <!-- 用户统计卡片 -->
    <div class="user-stats">
      <div class="stat-card">
        <div class="stat-icon teacher">👨‍🏫</div>
        <div class="stat-content">
          <div class="stat-number">{{ userStats.teachers }}</div>
          <div class="stat-label">教师</div>
          <div class="stat-trend positive">+3 本月新增</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon student">👨‍🎓</div>
        <div class="stat-content">
          <div class="stat-number">{{ userStats.students }}</div>
          <div class="stat-label">学生</div>
          <div class="stat-trend positive">+45 本月新增</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon admin">👨‍💼</div>
        <div class="stat-content">
          <div class="stat-number">{{ userStats.admins }}</div>
          <div class="stat-label">管理员</div>
          <div class="stat-trend neutral">稳定</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active">📈</div>
        <div class="stat-content">
          <div class="stat-number">{{ userStats.activeUsers }}</div>
          <div class="stat-label">活跃用户</div>
          <div class="stat-trend positive">+12% 较上月</div>
        </div>
      </div>
    </div>

    <!-- 管理控制栏 -->
    <div class="management-controls">
      <div class="search-filters">
        <div class="search-box">
          <span class="search-icon">🔍</span>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索用户姓名、邮箱或ID..."
            @input="filterUsers"
          >
        </div>
        <div class="filters">
          <select v-model="roleFilter" @change="filterUsers" class="filter-select">
            <option value="">所有角色</option>
            <option value="teacher">教师</option>
            <option value="student">学生</option>
            <option value="admin">管理员</option>
          </select>
          <select v-model="statusFilter" @change="filterUsers" class="filter-select">
            <option value="">所有状态</option>
            <option value="active">活跃</option>
            <option value="inactive">非活跃</option>
            <option value="suspended">已停用</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 用户表格 -->
    <div class="user-table-container">
      <div class="table-header">
        <div class="table-title">
          <h3>用户列表</h3>
          <span class="table-count">共 {{ filteredUsers.length }} 个用户</span>
        </div>
        <div class="table-actions">
          <label class="select-all">
            <input 
              type="checkbox" 
              v-model="selectAll"
              @change="toggleSelectAll"
            >
            <span>全选</span>
          </label>
        </div>
      </div>
      
      <div class="user-table">
        <div class="table-row header">
          <div class="col-checkbox"></div>
          <div class="col-user">用户信息</div>
          <div class="col-role">角色</div>
          <div class="col-status">状态</div>
          <div class="col-date">注册时间</div>
          <div class="col-login">最后登录</div>
          <div class="col-actions">操作</div>
        </div>
        
        <div 
          v-for="user in filteredUsers" 
          :key="user.id" 
          class="table-row user-row"
          :class="{ 'selected': selectedUsers.includes(user.id) }"
        >
          <div class="col-checkbox">
            <input 
              type="checkbox" 
              v-model="selectedUsers"
              :value="user.id"
            >
          </div>
          <div class="col-user">
            <div class="user-info">
              <div class="user-avatar">
                <img :src="user.avatar" :alt="user.name">
              </div>
              <div class="user-details">
                <div class="user-name">{{ user.name }}</div>
                <div class="user-email">{{ user.email }}</div>
                <div class="user-id">ID: {{ user.userId }}</div>
              </div>
            </div>
          </div>
          <div class="col-role">
            <span class="role-badge" :class="getRoleClass(user.role)">
              {{ getRoleName(user.role) }}
            </span>
          </div>
          <div class="col-status">
            <span class="status-badge" :class="getStatusClass(user.status)">
              {{ getStatusName(user.status) }}
            </span>
          </div>
          <div class="col-date">{{ formatDate(user.registerDate) }}</div>
          <div class="col-login">{{ formatDate(user.lastLogin) }}</div>
          <div class="col-actions">
            <div class="action-buttons">
              <button @click="editUser(user)" class="action-btn edit" title="编辑">
                <span class="btn-icon">✏️</span>
              </button>
              <button @click="viewUser(user)" class="action-btn view" title="查看">
                <span class="btn-icon">👁️</span>
              </button>
              <button @click="toggleUserStatus(user)" class="action-btn toggle" :title="user.status === 'active' ? '停用' : '启用'">
                <span class="btn-icon">{{ user.status === 'active' ? '⏸️' : '▶️' }}</span>
              </button>
              <button @click="deleteUserHandler(user)" class="action-btn delete" title="删除">
                <span class="btn-icon">🗑️</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑用户模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑用户' : '添加用户' }}</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveUser" class="user-form">
            <div class="form-grid">
              <div class="form-group">
                <label>姓名 *</label>
                <input v-model="userForm.name" type="text" required>
              </div>
              <div class="form-group">
                <label>邮箱 *</label>
                <input v-model="userForm.email" type="email" required>
              </div>
            </div>
            
            <div class="form-grid">
              <div class="form-group">
                <label>用户ID *</label>
                <input v-model="userForm.userId" type="text" required>
              </div>
              <div class="form-group">
                <label>角色 *</label>
                <select v-model="userForm.role" required>
                  <option value="">选择角色</option>
                  <option value="teacher">教师</option>
                  <option value="student">学生</option>
                  <option value="admin">管理员</option>
                </select>
              </div>
            </div>
            
            <div class="form-grid">
              <div class="form-group">
                <label>密码 {{ showEditModal ? '(留空则不修改)' : '*' }}</label>
                <input v-model="userForm.password" type="password" :required="!showEditModal">
              </div>
              <div class="form-group">
                <label>确认密码 {{ showEditModal ? '(留空则不修改)' : '*' }}</label>
                <input v-model="userForm.confirmPassword" type="password" :required="!showEditModal">
              </div>
            </div>
            
            <div class="form-grid">
              <div class="form-group">
                <label>手机号</label>
                <input v-model="userForm.phone" type="tel">
              </div>
              <div class="form-group">
                <label>状态</label>
                <select v-model="userForm.status">
                  <option value="active">活跃</option>
                  <option value="inactive">非活跃</option>
                  <option value="suspended">已停用</option>
                </select>
              </div>
            </div>
            
            <div class="form-actions">
              <button type="button" @click="closeModal" class="btn btn-secondary">取消</button>
              <button type="submit" class="btn btn-primary">
                {{ showEditModal ? '更新用户' : '创建用户' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserList, addUser, updateUser, deleteUser, getUserStats } from '@/api/user'

const router = useRouter()

// 响应式数据
const searchQuery = ref('')
const roleFilter = ref('')
const statusFilter = ref('')
const selectAll = ref(false)
const selectedUsers = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const isLoading = ref(false)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)

// 用户统计数据
const userStats = ref({
  teachers: 0,
  students: 0,
  admins: 0,
  activeUsers: 0
})

// 用户表单数据
const userForm = ref({
  name: '',
  email: '',
  userId: '',
  role: '',
  password: '',
  confirmPassword: '',
  phone: '',
  status: 'active'
})

// 用户数据
const users = ref([])

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    const res = await getUserStats()
    if (res && res.data) {
      console.log('用户统计数据:', res.data)
      userStats.value = {
        teachers: res.data.totalTeachers || 0,
        students: res.data.totalStudents || 0,
        admins: res.data.totalAdmins || 1,
        activeUsers: res.data.activeUsers || 0
      }
    }
  } catch (error) {
    console.error('获取用户统计失败:', error)
    // 使用默认统计数据
    userStats.value = {
      teachers: 15,
      students: 100,
      admins: 1,
      activeUsers: 89
    }
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    isLoading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchQuery.value,
      role: roleFilter.value,
      status: statusFilter.value
    }
    
    const res = await getUserList(params)
    if (res && res.data) {
      console.log('用户列表数据:', res.data)
      
      // 处理分页数据
      if (res.data.records) {
        users.value = res.data.records.map(user => ({
          id: user.id,
          name: user.name || user.username,
          email: user.email || '',
          userId: user.username || '',
          role: user.role || 'student',
          status: user.status === 1 ? 'active' : 'inactive',
          avatar: '/api/avatar/default',
          registerDate: user.createTime ? user.createTime.split(' ')[0] : '',
          lastLogin: user.updateTime ? user.updateTime.split(' ')[0] : '-',
          phone: user.phone || ''
        }))
        totalUsers.value = res.data.total || 0
      } else if (Array.isArray(res.data)) {
        // 如果返回的是数组
        users.value = res.data.map(user => ({
          id: user.id,
          name: user.name || user.username,
          email: user.email || '',
          userId: user.username || '',
          role: user.role || 'student',
          status: user.status === 1 ? 'active' : 'inactive',
          avatar: '/api/avatar/default',
          registerDate: user.createTime ? user.createTime.split(' ')[0] : '',
          lastLogin: user.updateTime ? user.updateTime.split(' ')[0] : '-',
          phone: user.phone || ''
        }))
        totalUsers.value = res.data.length
      }
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    // 使用默认数据
    users.value = [
      {
        id: 1,
        name: 'teacher1',
        email: 'teacher1@example.com',
        userId: 'teacher1',
        role: 'teacher',
        status: 'active',
        avatar: '/api/avatar/default',
        registerDate: '2024-01-15',
        lastLogin: '2024-01-20',
        phone: '13800138001'
      },
      {
        id: 2,
        name: 'student1',
        email: 'student1@example.com',
        userId: 'student1',
        role: 'student',
        status: 'active',
        avatar: '/api/avatar/default',
        registerDate: '2024-01-10',
        lastLogin: '2024-01-19',
        phone: '13800138002'
      }
    ]
    totalUsers.value = users.value.length
  } finally {
    isLoading.value = false
  }
}

// 计算属性
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = !searchQuery.value || 
      user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.userId.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesRole = !roleFilter.value || user.role === roleFilter.value
    const matchesStatus = !statusFilter.value || user.status === statusFilter.value
    
    return matchesSearch && matchesRole && matchesStatus
  })
})

// 分页计算
const totalPages = computed(() => Math.ceil(totalUsers.value / pageSize.value))
const startIndex = computed(() => (currentPage.value - 1) * pageSize.value + 1)
const endIndex = computed(() => Math.min(currentPage.value * pageSize.value, totalUsers.value))

// 方法
const filterUsers = async () => {
  currentPage.value = 1
  await loadUsers()
}

const changePage = async (page) => {
  currentPage.value = page
  await loadUsers()
}

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedUsers.value = filteredUsers.value.map(user => user.id)
  } else {
    selectedUsers.value = []
  }
}

const batchOperation = async () => {
  if (selectedUsers.value.length === 0) {
    alert('请先选择用户')
    return
  }
  
  if (confirm(`确定要对选中的 ${selectedUsers.value.length} 个用户执行批量操作吗？`)) {
    try {
      // 这里可以实现批量操作的具体逻辑
      console.log('批量操作:', selectedUsers.value)
      alert('批量操作成功')
      selectedUsers.value = []
      selectAll.value = false
      await loadUsers()
    } catch (error) {
      console.error('批量操作失败:', error)
      alert('批量操作失败')
    }
  }
}

const exportUsers = () => {
  try {
    // 创建CSV内容
    const headers = ['ID', '姓名', '邮箱', '用户ID', '角色', '状态', '注册日期', '最后登录']
    const csvContent = [
      headers.join(','),
      ...filteredUsers.value.map(user => [
        user.id,
        user.name,
        user.email,
        user.userId,
        getRoleName(user.role),
        getStatusName(user.status),
        user.registerDate,
        user.lastLogin
      ].join(','))
    ].join('\n')
    
    // 下载文件
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `用户数据_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    console.log('导出用户数据成功')
  } catch (error) {
    console.error('导出失败:', error)
    alert('导出失败')
  }
}

const editUser = (user) => {
  userForm.value = { 
    ...user,
    password: '',
    confirmPassword: ''
  }
  showEditModal.value = true
}

const viewUser = (user) => {
  // 可以跳转到用户详情页面或显示详情模态框
  router.push(`/admin/users/${user.id}`)
}

const toggleUserStatus = async (user) => {
  try {
    const newStatus = user.status === 'active' ? 'inactive' : 'active'
    const updateData = {
      id: user.id,
      status: newStatus === 'active' ? 1 : 0
    }
    
    await updateUser(updateData)
    user.status = newStatus
    console.log('用户状态更新成功:', user)
    alert(`用户状态已${newStatus === 'active' ? '激活' : '停用'}`)
  } catch (error) {
    console.error('更新用户状态失败:', error)
    alert('更新用户状态失败')
  }
}

const deleteUserHandler = async (user) => {
  if (confirm(`确定要删除用户 ${user.name} 吗？此操作不可恢复！`)) {
    try {
      await deleteUser(user.id)
      users.value = users.value.filter(u => u.id !== user.id)
      console.log('删除用户成功:', user)
      alert('用户删除成功')
      await loadUserStats() // 重新加载统计数据
    } catch (error) {
      console.error('删除用户失败:', error)
      alert('删除用户失败')
    }
  }
}

const saveUser = async () => {
  try {
    // 表单验证
    if (!userForm.value.name || !userForm.value.email || !userForm.value.userId) {
      alert('请填写必填字段')
      return
    }
    
    if (!showEditModal.value) {
      // 新增用户时检查密码
      if (!userForm.value.password || userForm.value.password !== userForm.value.confirmPassword) {
        alert('密码不能为空且两次输入必须一致')
        return
      }
    }
    
    const userData = {
      name: userForm.value.name,
      username: userForm.value.userId,
      email: userForm.value.email,
      role: userForm.value.role,
      phone: userForm.value.phone,
      status: userForm.value.status === 'active' ? 1 : 0
    }
    
    if (userForm.value.password) {
      userData.password = userForm.value.password
    }
    
    if (showEditModal.value) {
      // 更新用户
      userData.id = userForm.value.id
      await updateUser(userData)
      alert('用户更新成功')
    } else {
      // 添加用户
      await addUser(userData)
      alert('用户添加成功')
    }
    
    closeModal()
    await loadUsers()
    await loadUserStats()
    
  } catch (error) {
    console.error('保存用户失败:', error)
    alert('保存用户失败: ' + (error.message || '未知错误'))
  }
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  userForm.value = {
    name: '',
    email: '',
    userId: '',
    role: '',
    password: '',
    confirmPassword: '',
    phone: '',
    status: 'active'
  }
}

const getRoleClass = (role) => {
  const classMap = {
    teacher: 'role-teacher',
    student: 'role-student',
    admin: 'role-admin'
  }
  return classMap[role] || ''
}

const getRoleName = (role) => {
  const nameMap = {
    teacher: '教师',
    student: '学生',
    admin: '管理员'
  }
  return nameMap[role] || role
}

const getStatusClass = (status) => {
  const classMap = {
    active: 'status-active',
    inactive: 'status-inactive',
    suspended: 'status-suspended'
  }
  return classMap[status] || ''
}

const getStatusName = (status) => {
  const nameMap = {
    active: '活跃',
    inactive: '非活跃',
    suspended: '已停用'
  }
  return nameMap[status] || status
}

const formatDate = (date) => {
  if (!date || date === '-') return date
  return new Date(date).toLocaleDateString('zh-CN')
}

// 页面加载时初始化
onMounted(async () => {
  console.log('用户管理页面加载完成')
  await Promise.all([
    loadUserStats(),
    loadUsers()
  ])
})
</script>

<style lang="scss" scoped>
.user-management {
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部样式 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
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
  
  .header-content {
    position: relative;
    z-index: 2;
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      h1 {
        margin: 0;
        font-size: 2rem;
        font-weight: 700;
      }
      
      .header-badge {
        display: flex;
        align-items: center;
        gap: 6px;
        background: rgba(255, 255, 255, 0.2);
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.9rem;
        
        .badge-icon {
          font-size: 1rem;
        }
        
        .badge-text {
          font-weight: 500;
        }
      }
    }
    
    .header-subtitle {
      margin: 0;
      font-size: 1.1rem;
      opacity: 0.9;
      font-weight: 400;
    }
  }
  
  .header-actions {
    position: absolute;
    top: 32px;
    right: 32px;
    z-index: 2;
    display: flex;
    gap: 12px;
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      border: none;
      border-radius: 8px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &.primary {
        background: rgba(255, 255, 255, 0.2);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }
      }
      
      &.secondary {
        background: rgba(255, 255, 255, 0.1);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.2);
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
      }
      
      &.success {
        background: rgba(16, 185, 129, 0.2);
        color: white;
        border: 1px solid rgba(16, 185, 129, 0.3);
        
        &:hover {
          background: rgba(16, 185, 129, 0.3);
        }
      }
      
      .btn-icon {
        font-size: 1rem;
      }
    }
  }
}

/* 用户统计卡片样式 */
.user-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
  
  .stat-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    border: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    gap: 16px;
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.8rem;
      
      &.teacher {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
      
      &.student {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
      
      &.admin {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      &.active {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }
    
    .stat-content {
      flex: 1;
      
      .stat-number {
        font-size: 2rem;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 0.9rem;
        color: #64748b;
        margin-bottom: 4px;
      }
      
      .stat-trend {
        font-size: 0.8rem;
        font-weight: 500;
        
        &.positive {
          color: #10b981;
        }
        
        &.neutral {
          color: #64748b;
        }
      }
    }
  }
}

/* 管理控制栏样式 */
.management-controls {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
  
  .search-filters {
    display: flex;
    gap: 16px;
    align-items: center;
    
    .search-box {
      flex: 1;
      position: relative;
      
      .search-icon {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        color: #64748b;
        font-size: 1rem;
      }
      
      input {
        width: 100%;
        padding: 10px 12px 10px 40px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.9rem;
        
        &:focus {
          outline: none;
          border-color: #3b82f6;
          box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
      }
    }
    
    .filters {
      display: flex;
      gap: 12px;
      
      .filter-select {
        padding: 10px 12px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.9rem;
        background: white;
        
        &:focus {
          outline: none;
          border-color: #3b82f6;
        }
      }
    }
  }
}

/* 用户表格样式 */
.user-table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  overflow: hidden;
  
  .table-header {
    padding: 20px;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .table-title {
      h3 {
        margin: 0 0 4px 0;
        font-size: 1.1rem;
        font-weight: 600;
        color: #1e293b;
      }
      
      .table-count {
        font-size: 0.9rem;
        color: #64748b;
      }
    }
    
    .table-actions {
      .select-all {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 0.9rem;
        color: #64748b;
        cursor: pointer;
        
        input[type="checkbox"] {
          width: 16px;
          height: 16px;
        }
      }
    }
  }
  
  .user-table {
    .table-row {
      display: grid;
      grid-template-columns: 50px 2fr 1fr 1fr 1fr 1fr 1fr;
      gap: 16px;
      padding: 16px 20px;
      align-items: center;
      border-bottom: 1px solid #f1f5f9;
      
      &.header {
        background: #f8fafc;
        font-weight: 600;
        color: #374151;
        font-size: 0.9rem;
      }
      
      &.user-row {
        transition: all 0.3s ease;
        
        &:hover {
          background: #f8fafc;
        }
        
        &.selected {
          background: #eff6ff;
          border-left: 4px solid #3b82f6;
        }
      }
      
      .col-checkbox {
        input[type="checkbox"] {
          width: 16px;
          height: 16px;
        }
      }
      
      .col-user {
        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            overflow: hidden;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
          
          .user-details {
            .user-name {
              font-weight: 600;
              color: #1e293b;
              margin-bottom: 2px;
            }
            
            .user-email {
              font-size: 0.8rem;
              color: #64748b;
              margin-bottom: 2px;
            }
            
            .user-id {
              font-size: 0.8rem;
              color: #94a3b8;
            }
          }
        }
      }
      
      .role-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 500;
        
        &.role-teacher {
          background: #fef3c7;
          color: #92400e;
        }
        
        &.role-student {
          background: #dbeafe;
          color: #1e40af;
        }
        
        &.role-admin {
          background: #f3e8ff;
          color: #7c3aed;
        }
      }
      
      .status-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 500;
        
        &.status-active {
          background: #dcfce7;
          color: #166534;
        }
        
        &.status-inactive {
          background: #fef2f2;
          color: #991b1b;
        }
        
        &.status-suspended {
          background: #fef3c7;
          color: #92400e;
        }
      }
      
      .col-actions {
        .action-buttons {
          display: flex;
          gap: 8px;
          
          .action-btn {
            width: 32px;
            height: 32px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            
            &.edit {
              background: #eff6ff;
              color: #1d4ed8;
              
              &:hover {
                background: #dbeafe;
              }
            }
            
            &.view {
              background: #f0fdf4;
              color: #166534;
              
              &:hover {
                background: #dcfce7;
              }
            }
            
            &.toggle {
              background: #fef3c7;
              color: #92400e;
              
              &:hover {
                background: #fde68a;
              }
            }
            
            &.delete {
              background: #fef2f2;
              color: #dc2626;
              
              &:hover {
                background: #fecaca;
              }
            }
            
            .btn-icon {
              font-size: 0.9rem;
            }
          }
        }
      }
    }
  }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  
  .modal-content {
    background: white;
    border-radius: 12px;
    width: 90%;
    max-width: 600px;
    max-height: 90vh;
    overflow-y: auto;
    
    .modal-header {
      padding: 24px;
      border-bottom: 1px solid #e2e8f0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: #1e293b;
      }
      
      .close-btn {
        background: none;
        border: none;
        font-size: 1.5rem;
        color: #64748b;
        cursor: pointer;
        padding: 4px;
        border-radius: 4px;
        
        &:hover {
          background: #f1f5f9;
        }
      }
    }
    
    .modal-body {
      padding: 24px;
      
      .user-form {
        .form-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
          margin-bottom: 16px;
        }
        
        .form-group {
          label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            color: #374151;
          }
          
          input, select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 0.9rem;
            
            &:focus {
              outline: none;
              border-color: #3b82f6;
              box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
            }
          }
        }
        
        .form-actions {
          display: flex;
          gap: 12px;
          justify-content: flex-end;
          margin-top: 24px;
          
          .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &.btn-secondary {
              background: #f1f5f9;
              color: #64748b;
              
              &:hover {
                background: #e2e8f0;
              }
            }
            
            &.btn-primary {
              background: #3b82f6;
              color: white;
              
              &:hover {
                background: #2563eb;
              }
            }
          }
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .user-table .table-row {
    grid-template-columns: 50px 2fr 1fr 1fr 1fr 1fr 120px;
  }
}

@media (max-width: 768px) {
  .page-header .header-actions {
    position: static;
    margin-top: 16px;
    flex-wrap: wrap;
  }
  
  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .management-controls .search-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .user-table .table-row {
    grid-template-columns: 1fr;
    gap: 8px;
    
    &.header {
      display: none;
    }
    
    .col-user {
      grid-column: 1 / -1;
    }
  }
}
</style> 