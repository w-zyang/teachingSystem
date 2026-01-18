<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/author.png" alt="个人信息" class="header-icon">
          <h1>个人信息</h1>
        </div>
        <p class="header-subtitle">管理您的个人资料和账户设置</p>
      </div>
      <div class="header-actions">
        <button v-if="!isEditing" class="action-btn primary" @click="startEdit">
          <img src="@/assets/edit.png" alt="编辑" class="btn-icon">
          编辑信息
        </button>
        <div v-else class="btn-group">
          <button class="action-btn secondary" @click="cancelEdit">
            <img src="@/assets/delete.png" alt="取消" class="btn-icon">
            取消
          </button>
          <button class="action-btn primary" @click="saveEdit">
            <img src="@/assets/add.png" alt="保存" class="btn-icon">
            保存
          </button>
        </div>
      </div>
      <div class="header-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
    
    <!-- 头像区域 -->
    <div class="avatar-section">
      <div class="avatar-container">
        <div class="avatar">
          <div v-if="!userInfo?.avatar" class="avatar-placeholder">
            <span>{{ userInfo?.realName?.charAt(0) || 'U' }}</span>
          </div>
          <img v-else :src="userInfo.avatar" alt="头像" class="avatar-image">
          <div class="avatar-overlay">
            <img src="@/assets/edit.png" alt="编辑头像" class="overlay-icon">
          </div>
        </div>
        <div v-if="isEditing" class="avatar-upload">
          <label for="avatar-upload" class="upload-btn">
            <img src="@/assets/add.png" alt="上传" class="upload-icon">
            更换头像
          </label>
          <input id="avatar-upload" type="file" @change="uploadAvatar" hidden accept="image/*">
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-section">
      <div class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <p class="loading-text">正在加载个人信息...</p>
    </div>
    
    <!-- 信息卡片 -->
    <div v-else class="info-card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/author.png" alt="基本信息" class="header-icon">
          <h3>基本信息</h3>
        </div>
      </div>
      
      <!-- 查看模式 -->
      <div class="info-grid" v-if="!isEditing">
        <div class="info-row">
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="学号" class="label-icon">
              <span>学号</span>
            </div>
            <div class="info-value">{{ userInfo?.studentId || '2024001' }}</div>
          </div>
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="姓名" class="label-icon">
              <span>姓名</span>
            </div>
            <div class="info-value">{{ userInfo?.realName || userInfo?.username || '未设置' }}</div>
          </div>
        </div>
        
        <div class="info-row">
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="性别" class="label-icon">
              <span>性别</span>
            </div>
            <div class="info-value">{{ userInfo?.gender || '男' }}</div>
          </div>
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="年级" class="label-icon">
              <span>年级</span>
            </div>
            <div class="info-value">{{ userInfo?.grade || '2024级' }}</div>
          </div>
        </div>
        
        <div class="info-row">
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="学院" class="label-icon">
              <span>学院</span>
            </div>
            <div class="info-value">{{ userInfo?.college || '计算机与人工智能学院' }}</div>
          </div>
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="专业" class="label-icon">
              <span>专业</span>
            </div>
            <div class="info-value">{{ userInfo?.major || '计算机科学与技术' }}</div>
          </div>
        </div>
        
        <div class="info-row">
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="班级" class="label-icon">
              <span>班级</span>
            </div>
            <div class="info-value">{{ userInfo?.className || '计算机2401' }}</div>
          </div>
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/author.png" alt="联系电话" class="label-icon">
              <span>联系电话</span>
            </div>
            <div class="info-value">{{ userInfo?.phone || '18001138001' }}</div>
          </div>
        </div>
        
        <div class="info-row">
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/email.png" alt="电子邮箱" class="label-icon">
              <span>电子邮箱</span>
            </div>
            <div class="info-value">{{ userInfo?.email || '未设置' }}</div>
          </div>
          <div class="info-cell">
            <div class="info-label">
              <img src="@/assets/time.png" alt="入学时间" class="label-icon">
              <span>入学时间</span>
            </div>
            <div class="info-value">{{ userInfo?.admissionDate || '未设置' }}</div>
          </div>
        </div>
      </div>
      
      <!-- 编辑模式 -->
      <div class="edit-form" v-else>
        <div class="form-row">
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="学号" class="label-icon">
              学号
            </label>
            <input type="text" v-model="formData.studentId" disabled>
          </div>
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="姓名" class="label-icon">
              姓名
            </label>
            <input type="text" v-model="formData.realName">
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="性别" class="label-icon">
              性别
            </label>
            <select v-model="formData.gender">
              <option value="男">男</option>
              <option value="女">女</option>
              <option value="其他">其他</option>
            </select>
          </div>
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="年级" class="label-icon">
              年级
            </label>
            <input type="text" v-model="formData.grade">
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="学院" class="label-icon">
              学院
            </label>
            <input type="text" v-model="formData.college">
          </div>
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="专业" class="label-icon">
              专业
            </label>
            <input type="text" v-model="formData.major">
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="班级" class="label-icon">
              班级
            </label>
            <input type="text" v-model="formData.className">
          </div>
          <div class="form-cell">
            <label>
              <img src="@/assets/author.png" alt="联系电话" class="label-icon">
              联系电话
            </label>
            <input type="text" v-model="formData.phone">
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-cell">
            <label>
              <img src="@/assets/email.png" alt="电子邮箱" class="label-icon">
              电子邮箱
            </label>
            <input type="email" v-model="formData.email">
          </div>
          <div class="form-cell">
            <label>
              <img src="@/assets/time.png" alt="入学时间" class="label-icon">
              入学时间
            </label>
            <input type="date" v-model="formData.admissionDate">
          </div>
        </div>
      </div>
    </div>
    
    <!-- 学习统计 -->
    <div class="stats-card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/balance.png" alt="学习统计" class="header-icon">
          <h3>学习统计</h3>
        </div>
      </div>
      <div class="stats-content">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-icon">📚</div>
            <div class="stat-info">
              <div class="stat-value">{{ learningStats.totalCourses }}</div>
              <div class="stat-label">已选课程</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">⏱️</div>
            <div class="stat-info">
              <div class="stat-value">{{ learningStats.totalStudyTime }}h</div>
              <div class="stat-label">总学习时长</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">📝</div>
            <div class="stat-info">
              <div class="stat-value">{{ learningStats.completedExams }}</div>
              <div class="stat-label">完成考试</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon">🎯</div>
            <div class="stat-info">
              <div class="stat-value">{{ learningStats.avgScore }}%</div>
              <div class="stat-label">平均成绩</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 安全设置 -->
    <div class="security-card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/balance.png" alt="安全设置" class="header-icon">
          <h3>安全设置</h3>
        </div>
      </div>
      <div class="security-content">
        <div class="security-item">
          <div class="security-info">
            <img src="@/assets/balance.png" alt="密码" class="security-icon">
            <div class="security-text">
              <h4>账户密码</h4>
              <p>定期更新密码以确保账户安全</p>
            </div>
          </div>
          <button class="action-btn outline" @click="goToChangePwd">
            <img src="@/assets/edit.png" alt="修改密码" class="btn-icon">
            修改密码
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserById, updateUser, changePassword } from '@/api/user'

// 获取当前登录学生ID
const userId = localStorage.getItem('userId') || 17 // 默认使用学生ID 17

// 响应式变量
const activeTab = ref('profile')
const userInfo = ref(null)
const isEditing = ref(false)
const isLoading = ref(false)
const message = ref('')
const messageType = ref('')

// 表单数据
const formData = ref({
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: '',
  studentId: '',
  gender: '',
  grade: '',
  college: '',
  major: '',
  className: '',
  admissionDate: ''
})

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 学习统计
const learningStats = ref({
  totalCourses: 3,
  totalStudyTime: 45,
  completedExams: 3,
  avgScore: 87
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getUserById(userId)
    console.log('API响应:', res)
    
    if (res && res.success && res.data) {
      userInfo.value = res.data
      formData.value = {
        username: res.data.username || '',
        realName: res.data.realName || '',
        email: res.data.email || '',
        phone: res.data.phone || '',
        avatar: res.data.avatar || '',
        studentId: res.data.userId || res.data.studentId || '',
        gender: res.data.gender || '',
        grade: res.data.grade || '',
        college: res.data.college || '',
        major: res.data.major || '',
        className: res.data.className || '',
        admissionDate: res.data.admissionDate || ''
      }
      console.log('用户信息:', res.data)
    } else {
      // 如果API调用失败，使用默认数据
      loadDefaultUserInfo()
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 使用默认数据
    loadDefaultUserInfo()
  }
}

// 加载默认用户信息
const loadDefaultUserInfo = () => {
  const defaultInfo = {
    id: 4,
    username: 'student1',
    realName: '李同学',
    email: 'student1@example.com',
    phone: '13800138001',
    avatar: '',
    userId: '2024001',
    gender: '男',
    grade: '2024级',
    college: '计算机科学与技术学院',
    major: '计算机科学与技术',
    className: '计算机科学与技术2024级1班',
    admissionDate: '2024-09-01',
    role: 'student',
    status: 'active',
    registerDate: '2024-01-01 09:00:00',
    lastLogin: '2024-01-16 14:30:00'
  }
  
  userInfo.value = defaultInfo
  formData.value = {
    username: defaultInfo.username,
    realName: defaultInfo.realName,
    email: defaultInfo.email,
    phone: defaultInfo.phone,
    avatar: defaultInfo.avatar,
    studentId: defaultInfo.userId,
    gender: defaultInfo.gender,
    grade: defaultInfo.grade,
    college: defaultInfo.college,
    major: defaultInfo.major,
    className: defaultInfo.className,
    admissionDate: defaultInfo.admissionDate
  }
  
  console.log('使用默认用户信息:', defaultInfo)
}

// 更新用户信息
const updateUserInfo = async () => {
  try {
    isLoading.value = true
    const res = await updateUser({
      id: userId,
      ...formData.value
    })
    if (res && res.success) {
      userInfo.value = { ...userInfo.value, ...formData.value }
      isEditing.value = false
      showMessage('个人信息更新成功', 'success')
      console.log('用户信息更新成功:', res.data)
    } else {
      showMessage('更新用户信息失败', 'error')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    showMessage('更新用户信息失败: ' + (error.message || '未知错误'), 'error')
  } finally {
    isLoading.value = false
  }
}

// 修改密码
const changeUserPassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    showMessage('新密码和确认密码不一致', 'error')
    return
  }
  
  try {
    isLoading.value = true
    const res = await changePassword({
      userId: userId,
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (res && res.data) {
      showMessage('密码修改成功', 'success')
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      console.log('密码修改成功:', res.data)
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    showMessage('修改密码失败', 'error')
  } finally {
    isLoading.value = false
  }
}

// 显示消息
const showMessage = (msg, type = 'info') => {
  ElMessage({
    message: msg,
    type: type,
    duration: 3000
  })
}

// 开始编辑
const startEdit = () => {
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  if (userInfo.value) {
    formData.value = {
      username: userInfo.value.username || '',
      realName: userInfo.value.realName || '',
      email: userInfo.value.email || '',
      phone: userInfo.value.phone || '',
      avatar: userInfo.value.avatar || '',
      studentId: userInfo.value.studentId || '',
      gender: userInfo.value.gender || '',
      grade: userInfo.value.grade || '',
      college: userInfo.value.college || '',
      major: userInfo.value.major || '',
      className: userInfo.value.className || '',
      admissionDate: userInfo.value.admissionDate || ''
    }
  }
}

// 保存编辑
const saveEdit = () => {
  updateUserInfo()
}

// 上传头像
const uploadAvatar = (event) => {
  const file = event.target.files[0]
  if (file) {
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      showMessage('请选择图片文件', 'error')
      return
    }
    
    // 检查文件大小（限制为2MB）
    if (file.size > 2 * 1024 * 1024) {
      showMessage('图片大小不能超过2MB', 'error')
      return
    }
    
    // 预览头像
    const reader = new FileReader()
    reader.onload = (e) => {
      formData.value.avatar = e.target.result
      userInfo.value.avatar = e.target.result
      showMessage('头像上传成功', 'success')
    }
    reader.readAsDataURL(file)
    
    // 这里可以调用文件上传API将文件上传到服务器
    // const formData = new FormData()
    // formData.append('file', file)
    // uploadAvatarAPI(formData)
  }
}

// 跳转到修改密码页面
const goToChangePwd = () => {
  // 跳转到修改密码页面
  window.location.href = '/student/change-pwd'
}

// 页面加载时获取数据
onMounted(async () => {
  isLoading.value = true
  try {
    await fetchUserInfo()
  } finally {
    isLoading.value = false
  }
})
</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 32px;
  border-radius: 24px;
  margin-bottom: 32px;
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
    margin-bottom: 24px;
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      h1 {
        margin: 0;
        font-size: 2.5rem;
        font-weight: 800;
        background: linear-gradient(135deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
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
    display: flex;
    gap: 16px;
    position: relative;
    z-index: 2;
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      border-radius: 12px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      border: none;
      
      .btn-icon {
        width: 18px;
        height: 18px;
        opacity: 0.8;
      }
      
      &.primary {
        background: rgba(255, 255, 255, 0.2);
        color: white;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
        }
      }
      
      &.secondary {
        background: rgba(255, 255, 255, 0.1);
        color: white;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2);
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: translateY(-2px);
        }
      }
    }
  }
  
  .header-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
      animation: float 6s ease-in-out infinite;
      
      &.shape-1 {
        width: 60px;
        height: 60px;
        top: 20px;
        right: 40px;
        animation-delay: 0s;
      }
      
      &.shape-2 {
        width: 40px;
        height: 40px;
        bottom: 30px;
        right: 80px;
        animation-delay: 2s;
      }
      
      &.shape-3 {
        width: 30px;
        height: 30px;
        top: 60px;
        right: 120px;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 头像区域 */
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
  
  .avatar-container {
    position: relative;
    width: 140px;
    height: 140px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    overflow: hidden;
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 16px 40px rgba(102, 126, 234, 0.4);
    }
    
    .avatar {
      position: relative;
      width: 100%;
      height: 100%;
      
      .avatar-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 48px;
        font-weight: 700;
        color: white;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      .avatar-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .avatar-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        opacity: 0;
        transition: opacity 0.3s ease;
        cursor: pointer;
        
        &:hover {
          opacity: 1;
        }
        
        .overlay-icon {
          width: 32px;
          height: 32px;
          opacity: 0.8;
        }
      }
    }
    
    .avatar-upload {
      margin-top: 16px;
      text-align: center;
      
      .upload-btn {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 10px 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        border: none;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
        }
        
        .upload-icon {
          width: 16px;
          height: 16px;
          opacity: 0.8;
        }
      }
    }
  }
}

/* 加载状态 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  
  .loading-spinner {
    margin-bottom: 20px;
    
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #e2e8f0;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }
  
  .loading-text {
    margin: 0;
    color: #718096;
    font-size: 16px;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .header-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  
  .info-grid {
    display: grid;
    gap: 20px;
    
    .info-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      
      .info-cell {
        padding: 16px;
        background: #f7fafc;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
        
        &:hover {
          background: white;
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        
        .info-label {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          
          .label-icon {
            width: 16px;
            height: 16px;
            opacity: 0.6;
          }
          
          span {
            font-size: 12px;
            color: #718096;
            font-weight: 500;
          }
        }
        
        .info-value {
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
        }
      }
    }
  }
  
  .edit-form {
    display: grid;
    gap: 20px;
    
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      
      .form-cell {
        label {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          font-size: 12px;
          color: #718096;
          font-weight: 500;
          
          .label-icon {
            width: 16px;
            height: 16px;
            opacity: 0.6;
          }
        }
        
        input, select {
          width: 100%;
          padding: 12px 16px;
          border: 2px solid #e2e8f0;
          border-radius: 12px;
          font-size: 14px;
          background: #f7fafc;
          transition: all 0.3s ease;
          
          &:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
          
          &:disabled {
            background: #edf2f7;
            color: #a0aec0;
            cursor: not-allowed;
          }
        }
      }
    }
  }
}

/* 学习统计卡片 */
.stats-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .header-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  
  .stats-content {
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
        background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
        border-radius: 16px;
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          
          .stat-value,
          .stat-label {
            color: white;
          }
        }
        
        .stat-icon {
          font-size: 32px;
          opacity: 0.8;
        }
        
        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2d3748;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 14px;
            color: #718096;
            font-weight: 500;
          }
        }
      }
    }
  }
}

/* 安全设置卡片 */
.security-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .header-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  
  .security-content {
    .security-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      background: #f7fafc;
      border-radius: 16px;
      border: 1px solid #e2e8f0;
      transition: all 0.3s ease;
      
      &:hover {
        background: white;
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }
      
      .security-info {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .security-icon {
          width: 32px;
          height: 32px;
          opacity: 0.7;
        }
        
        .security-text {
          h4 {
            margin: 0 0 4px 0;
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
          }
          
          p {
            margin: 0;
            font-size: 14px;
            color: #718096;
          }
        }
      }
      
      .action-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px 20px;
        border-radius: 12px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        border: none;
        
        .btn-icon {
          width: 16px;
          height: 16px;
          opacity: 0.8;
        }
        
        &.outline {
          background: transparent;
          color: #667eea;
          border: 2px solid #667eea;
          
          &:hover {
            background: #667eea;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;
    
    .header-title h1 {
      font-size: 2rem;
    }
    
    .header-actions {
      flex-direction: column;
      gap: 12px;
    }
  }
  
  .avatar-section .avatar-container {
    width: 120px;
    height: 120px;
  }
  
  .info-card,
  .stats-card,
  .security-card {
    padding: 20px;
    
    .info-grid .info-row,
    .edit-form .form-row {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
  
  .stats-card .stats-content .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    
    .stat-item {
      padding: 16px;
      
      .stat-icon {
        font-size: 24px;
      }
      
      .stat-info .stat-value {
        font-size: 20px;
      }
    }
  }
  
  .security-card .security-content .security-item {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
