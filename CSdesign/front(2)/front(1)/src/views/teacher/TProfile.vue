<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/author.png" alt="个人资料" class="header-icon">
          <h1>个人资料</h1>
        </div>
        <p class="header-subtitle">管理您的个人信息和账户设置</p>
      </div>
      <div class="header-actions">
        <button v-if="!isEditing" class="action-btn primary" @click="startEditing">
          <img src="@/assets/edit.png" alt="编辑" class="btn-icon">
          编辑信息
        </button>
        <div v-else class="btn-group">
          <button class="action-btn secondary" @click="cancelEditing">
            <img src="@/assets/delete.png" alt="关闭" class="btn-icon">
            取消
          </button>
          <button class="action-btn primary" @click="saveProfile">
            <img src="@/assets/add.png" alt="保存" class="btn-icon">
            保存
          </button>
        </div>
      </div>
      <div class="header-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
      </div>
    </div>
    
    <!-- 头像区域 -->
    <div class="avatar-section">
      <div class="avatar-container">
        <div class="avatar">
          <img v-if="profile?.avatar" :src="profile.avatar" alt="头像" class="avatar-img">
          <div v-else class="avatar-placeholder">
            <img src="@/assets/author.png" alt="默认头像" class="default-avatar">
          </div>
          <div class="avatar-overlay">
            <img src="@/assets/author.png" alt="头像装饰" class="overlay-icon">
          </div>
        </div>
        <div class="avatar-info">
          <h2 class="teacher-name">{{ profile?.name || '张教授' }}</h2>
          <p class="teacher-title">{{ profile?.title || '教授' }} · {{ profile?.department || '计算机科学与技术学院' }}</p>
        </div>
        <div v-if="isEditing" class="avatar-upload">
          <label for="avatar-upload" class="upload-btn">
            <img src="@/assets/add.png" alt="上传" class="upload-icon">
            更换头像
          </label>
          <input id="avatar-upload" type="file" @change="handleAvatarChange" hidden>
        </div>
      </div>
    </div>
    
    <!-- 信息卡片 -->
    <div class="info-section">
      <div class="info-card">
        <div class="card-header">
          <div class="header-left">
            <img src="@/assets/author.png" alt="基本信息" class="header-icon">
            <h3>基本信息</h3>
          </div>
        </div>
        
        <div class="info-grid" v-if="!isEditing">
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/author.png" alt="工号" class="label-icon">
              工号
            </div>
            <div class="info-value">{{ profile?.teacherId || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/author.png" alt="姓名" class="label-icon">
              姓名
            </div>
            <div class="info-value">{{ profile?.name || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/author.png" alt="性别" class="label-icon">
              性别
            </div>
            <div class="info-value">{{ profile?.gender || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/author.png" alt="职称" class="label-icon">
              职称
            </div>
            <div class="info-value">{{ profile?.title || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/category.png" alt="所属院系" class="label-icon">
              所属院系
            </div>
            <div class="info-value">{{ profile?.department || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/category.png" alt="专业方向" class="label-icon">
              专业方向
            </div>
            <div class="info-value">{{ profile?.specialization || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/local.png" alt="办公室" class="label-icon">
              办公室
            </div>
            <div class="info-value">{{ profile?.officeLocation || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/author.png" alt="联系电话" class="label-icon">
              联系电话
            </div>
            <div class="info-value">{{ profile?.phone || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/email.png" alt="电子邮箱" class="label-icon">
              电子邮箱
            </div>
            <div class="info-value">{{ profile?.email || 'N/A' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <img src="@/assets/time.png" alt="入职时间" class="label-icon">
              入职时间
            </div>
            <div class="info-value">{{ profile?.hireDate || 'N/A' }}</div>
          </div>
        </div>
        
        <!-- 编辑表单 -->
        <div class="edit-form" v-else>
          <div class="form-grid">
            <div class="form-item">
              <label>
                <img src="@/assets/author.png" alt="工号" class="label-icon">
                工号
              </label>
              <input type="text" v-model="editingProfile.teacherId" disabled>
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/author.png" alt="姓名" class="label-icon">
                姓名
              </label>
              <input type="text" v-model="editingProfile.name">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/author.png" alt="性别" class="label-icon">
                性别
              </label>
              <select v-model="editingProfile.gender">
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/author.png" alt="职称" class="label-icon">
                职称
              </label>
              <select v-model="editingProfile.title">
                <option value="教授">教授</option>
                <option value="副教授">副教授</option>
                <option value="讲师">讲师</option>
                <option value="助教">助教</option>
              </select>
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/category.png" alt="所属院系" class="label-icon">
                所属院系
              </label>
              <input type="text" v-model="editingProfile.department">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/category.png" alt="专业方向" class="label-icon">
                专业方向
              </label>
              <input type="text" v-model="editingProfile.specialization">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/local.png" alt="办公室" class="label-icon">
                办公室
              </label>
              <input type="text" v-model="editingProfile.officeLocation">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/author.png" alt="联系电话" class="label-icon">
                联系电话
              </label>
              <input type="text" v-model="editingProfile.phone">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/email.png" alt="电子邮箱" class="label-icon">
                电子邮箱
              </label>
              <input type="email" v-model="editingProfile.email">
            </div>
            <div class="form-item">
              <label>
                <img src="@/assets/time.png" alt="入职时间" class="label-icon">
                入职时间
              </label>
              <input type="date" v-model="editingProfile.hireDate">
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 教学信息 -->
    <div class="stats-section">
      <div class="section-header">
        <div class="header-left">
          <img src="@/assets/balance.png" alt="教学信息" class="header-icon">
          <h3>教学信息</h3>
        </div>
      </div>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <img src="@/assets/category.png" alt="课程" class="icon-img">
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ profile?.coursesCount || 0 }}</div>
            <div class="stat-label">授课课程数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <img src="@/assets/author.png" alt="学生" class="icon-img">
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ profile?.studentsCount || 0 }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <img src="@/assets/time.png" alt="课时" class="icon-img">
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ profile?.totalHours || 0 }}</div>
            <div class="stat-label">总课时</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 安全设置 -->
    <div class="security-section">
      <div class="section-header">
        <div class="header-left">
          <img src="@/assets/balance.png" alt="安全设置" class="header-icon">
          <h3>安全设置</h3>
        </div>
      </div>
      <div class="security-card">
        <div class="security-item">
          <div class="security-info">
            <img src="@/assets/balance.png" alt="密码" class="security-icon">
            <div class="security-details">
              <h4>账户密码</h4>
              <p>定期更新密码以确保账户安全</p>
            </div>
          </div>
          <button class="security-btn" @click="goToChangePwd">
            <img src="@/assets/edit.png" alt="修改" class="btn-icon">
            修改密码
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserById, updateUser, changePassword, uploadAvatar } from '@/api/user'

// 响应式变量
const isEditing = ref(false)
const profile = ref({
  avatar: '',
  teacherId: '',
  gender: '',
  department: '',
  title: '',
  email: '',
  name: '',
  specialization: '',
  phone: '',
  hireDate: '',
  officeLocation: '',
  coursesCount: 0,
  studentsCount: 0,
  totalHours: 0
})

const editingProfile = ref(null)

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId') || 1
    const res = await getUserById(userId)
    if (res && res.data) {
      profile.value = {
        avatar: res.data.avatar || '',
        teacherId: res.data.teacherId || res.data.username || '',
        gender: res.data.gender || '男',
        department: res.data.department || '计算机科学与技术学院',
        title: res.data.title || '教授',
        email: res.data.email || '',
        name: res.data.realName || res.data.username || '张教授',
        specialization: res.data.specialization || '人工智能',
        phone: res.data.phone || '',
        hireDate: res.data.hireDate || '2020-09-01',
        officeLocation: res.data.officeLocation || 'A栋501',
        coursesCount: res.data.coursesCount || 5,
        studentsCount: res.data.studentsCount || 180,
        totalHours: res.data.totalHours || 240
      }
      console.log('用户信息:', res.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 使用默认数据
    profile.value = {
      avatar: '',
      teacherId: 'T2024001',
      gender: '男',
      department: '计算机科学与技术学院',
      title: '教授',
      email: 'zhang@example.com',
      name: '张教授',
      specialization: '人工智能',
      phone: '13800138000',
      hireDate: '2020-09-01',
      officeLocation: 'A栋501',
      coursesCount: 5,
      studentsCount: 180,
      totalHours: 240
    }
  }
}

// 开始编辑
const startEditing = () => {
  // 创建一个副本进行编辑，避免直接修改原始数据
  editingProfile.value = JSON.parse(JSON.stringify(profile.value))
  isEditing.value = true
}

// 取消编辑
const cancelEditing = () => {
  // 取消编辑，恢复原始数据
  isEditing.value = false
  editingProfile.value = null
}

// 保存个人信息
const saveProfile = async () => {
  try {
    const userId = localStorage.getItem('userId') || 1
    const updateData = {
      id: userId,
      ...editingProfile.value
    }
    
    const res = await updateUser(updateData)
    if (res && res.code === 200) {
      // 更新本地数据
      profile.value = { ...editingProfile.value }
      isEditing.value = false
      editingProfile.value = null
      console.log('个人信息更新成功')
      // 这里可以显示成功消息
    } else {
      console.error('更新失败:', res?.message)
    }
  } catch (error) {
    console.error('保存个人信息失败:', error)
  }
}

// 处理头像上传
const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    // 上传头像
    const res = await uploadAvatar(file)
    if (res && res.code === 200) {
      // 更新头像URL
      if (editingProfile.value) {
        editingProfile.value.avatar = res.data
      } else {
        profile.value.avatar = res.data
      }
      console.log('头像上传成功:', res.data)
    } else {
      console.error('头像上传失败:', res?.message)
    }
  } catch (error) {
    console.error('头像上传失败:', error)
  }
}

// 跳转到修改密码页面
const goToChangePwd = () => {
  // 这里可以跳转到修改密码页面或打开修改密码弹窗
  console.log('跳转到修改密码页面')
}

// 页面加载时获取数据
onMounted(async () => {
  await fetchUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 1000px;
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
  margin-bottom: 32px;
  
  .avatar-container {
    display: flex;
    align-items: center;
    gap: 24px;
    background: white;
    padding: 32px;
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .avatar {
      position: relative;
      width: 120px;
      height: 120px;
      border-radius: 50%;
      overflow: hidden;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      
      .avatar-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .avatar-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .default-avatar {
          width: 60px;
          height: 60px;
          opacity: 0.8;
        }
      }
      
      .avatar-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s ease;
        
        &:hover {
          opacity: 1;
        }
        
        .overlay-icon {
          width: 32px;
          height: 32px;
          opacity: 0.9;
        }
      }
    }
    
    .avatar-info {
      flex: 1;
      
      .teacher-name {
        margin: 0 0 8px 0;
        font-size: 1.8rem;
        font-weight: 700;
        color: #2d3748;
      }
      
      .teacher-title {
        margin: 0;
        font-size: 1rem;
        color: #718096;
        font-weight: 500;
      }
    }
    
    .avatar-upload {
      .upload-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
        }
        
        .upload-icon {
          width: 18px;
          height: 18px;
          opacity: 0.8;
        }
      }
    }
  }
}

/* 信息卡片 */
.info-section {
  margin-bottom: 32px;
  
  .info-card {
    background: white;
    border-radius: 20px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .card-header {
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
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 20px;
      
      .info-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: #f7fafc;
        border-radius: 12px;
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
        
        &:hover {
          background: white;
          transform: translateX(4px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .info-label {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          font-weight: 600;
          color: #718096;
          min-width: 100px;
          
          .label-icon {
            width: 16px;
            height: 16px;
            opacity: 0.6;
          }
        }
        
        .info-value {
          flex: 1;
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
        }
      }
    }
    
    .edit-form {
      .form-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 20px;
        
        .form-item {
          label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 600;
            color: #718096;
            margin-bottom: 8px;
            
            .label-icon {
              width: 16px;
              height: 16px;
              opacity: 0.6;
            }
          }
          
          input, select {
            width: 100%;
            padding: 12px 16px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s ease;
            
            &:focus {
              outline: none;
              border-color: #667eea;
              box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            
            &:disabled {
              background: #f7fafc;
              color: #718096;
            }
          }
        }
      }
    }
  }
}

/* 教学信息 */
.stats-section {
  margin-bottom: 32px;
  
  .section-header {
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
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    
    .stat-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        .icon-img {
          width: 24px;
          height: 24px;
          opacity: 0.9;
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-value {
          font-size: 1.8rem;
          font-weight: 800;
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

/* 安全设置 */
.security-section {
  .section-header {
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
  
  .security-card {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .security-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .security-info {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .security-icon {
          width: 40px;
          height: 40px;
          border-radius: 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          
          img {
            width: 20px;
            height: 20px;
            opacity: 0.9;
          }
        }
        
        .security-details {
          h4 {
            margin: 0 0 4px 0;
            font-size: 1.1rem;
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
      
      .security-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 20px;
        background: transparent;
        color: #667eea;
        border: 2px solid #667eea;
        border-radius: 12px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          background: #667eea;
          color: white;
          transform: translateY(-2px);
        }
        
        .btn-icon {
          width: 16px;
          height: 16px;
          opacity: 0.8;
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
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .security-item {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>