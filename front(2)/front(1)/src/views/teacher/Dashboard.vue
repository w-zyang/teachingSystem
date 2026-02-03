<template>
  <div class="teacher-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/author.png" alt="教师工作台" class="header-icon">
          <h1>教师工作台</h1>
        </div>
        <p class="header-subtitle">欢迎使用智能教学管理系统，这里是您的教学管理中心</p>
      </div>
      <div class="header-actions">
        <button @click="$router.push('/teacher/create-course')" class="action-btn primary">
          <img src="@/assets/add.png" alt="创建课程" class="btn-icon">
          创建课程
        </button>
        <button @click="$router.push('/teacher/exam-generation')" class="action-btn secondary">
          <img src="@/assets/balance.png" alt="生成考试" class="btn-icon">
          生成考试
        </button>
      </div>
      <div class="header-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <img src="@/assets/author.png" alt="学生总数" class="icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalStudents }}</h3>
          <p>学生总数</p>
          <span class="stat-change positive">
            <img src="@/assets/add.png" alt="增长" class="change-icon">
            +15% 较上月
          </span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <img src="@/assets/category.png" alt="课程数量" class="icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalCourses }}</h3>
          <p>课程数量</p>
          <span class="stat-change positive">
            <img src="@/assets/add.png" alt="增长" class="change-icon">
            +2 本月新增
          </span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <img src="@/assets/balance.png" alt="平均成绩" class="icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ formatAvgScore(stats.avgScore) }}%</h3>
          <p>平均成绩</p>
          <span class="stat-change positive">
            <img src="@/assets/add.png" alt="增长" class="change-icon">
            +6.8% 较上月
          </span>
        </div>
        <div class="stat-decoration"></div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <img src="@/assets/time.png" alt="教学时长" class="icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.teachingHours }}h</h3>
          <p>教学时长</p>
          <span class="stat-change positive">
            <img src="@/assets/add.png" alt="增长" class="change-icon">
            +12h 本周
          </span>
        </div>
        <div class="stat-decoration"></div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-content-grid">
      <!-- 快速操作 -->
      <div class="quick-actions card">
        <div class="card-header">
          <div class="header-left">
            <img src="@/assets/author.png" alt="快速操作" class="header-icon">
            <h3>快速操作</h3>
          </div>
          <p class="header-description">常用功能快速访问</p>
        </div>
        <div class="actions-grid">
          <button @click="$router.push('/teacher/create-course')" class="action-btn">
            <div class="action-icon">
              <img src="@/assets/add.png" alt="创建课程" class="icon-img">
            </div>
            <div class="action-content">
              <span class="action-title">创建新课程</span>
              <span class="action-desc">创建课程基础信息</span>
            </div>
            <div class="action-decoration"></div>
          </button>
          <button @click="$router.push('/teacher/ai-course-design')" class="action-btn">
            <div class="action-icon">
              <img src="@/assets/add.png" alt="设计课程" class="icon-img">
            </div>
            <div class="action-content">
              <span class="action-title">AI课程设计</span>
              <span class="action-desc">AI辅助课程设计</span>
            </div>
            <div class="action-decoration"></div>
          </button>
          <button @click="$router.push('/teacher/exam-generation')" class="action-btn">
            <div class="action-icon">
              <img src="@/assets/balance.png" alt="生成考核" class="icon-img">
            </div>
            <div class="action-content">
              <span class="action-title">生成考核</span>
              <span class="action-desc">智能出题系统</span>
            </div>
            <div class="action-decoration"></div>
          </button>
          <button @click="$router.push('/teacher/student-analysis')" class="action-btn">
            <div class="action-icon">
              <img src="@/assets/category.png" alt="学情分析" class="icon-img">
            </div>
            <div class="action-content">
              <span class="action-title">学情分析</span>
              <span class="action-desc">学生学习数据</span>
            </div>
            <div class="action-decoration"></div>
          </button>
          <button @click="$router.push('/teacher/resource-management')" class="action-btn">
            <div class="action-icon">
              <img src="@/assets/search.png" alt="上传资源" class="icon-img">
            </div>
            <div class="action-content">
              <span class="action-title">上传资源</span>
              <span class="action-desc">教学资源管理</span>
            </div>
            <div class="action-decoration"></div>
          </button>
        </div>
      </div>
      
      <!-- 教学效率图表 -->
      <div class="efficiency-chart card">
        <EfficiencyTrendChart :teacher-id="teacherId" />
      </div>
    </div>
    
    <!-- 最近活动 -->
    <div class="recent-activities card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/time.png" alt="最近活动" class="header-icon">
          <h3>最近活动</h3>
        </div>
        <p class="header-description">您的教学动态</p>
      </div>
      <div class="activities-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            <img :src="activity.iconSrc" :alt="activity.title" class="icon-img">
          </div>
          <div class="activity-content">
            <p class="activity-title">{{ activity.title }}</p>
            <p class="activity-time">{{ activity.time }}</p>
          </div>
          <div class="activity-actions">
            <button class="view-btn">
              <img src="@/assets/search.png" alt="查看" class="btn-icon">
              查看
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getTeacherStats, getCourseStats } from '@/api/stats'
import { getCoursesByTeacherId } from '@/api/course'
import EfficiencyTrendChart from '@/components/EfficiencyTrendChart.vue'

const router = useRouter()

// 从localStorage获取教师ID
const getTeacherId = () => {
  const userId = localStorage.getItem('userId')
  return userId ? parseInt(userId) : 2 // 默认使用ID 2（如果未登录）
}

const teacherId = getTeacherId()

// 响应式变量
const stats = ref({
  totalStudents: 0,
  totalCourses: 0,
  avgScore: 0,
  teachingHours: 0
})

const courseList = ref([])
const recentActivities = ref([
  {
    id: 1,
    title: '创建了新课程《数据结构与算法》',
    time: '2小时前',
    type: 'course',
    iconSrc: '/src/assets/add.png'
  },
  {
    id: 2,
    title: '生成了期中考试试卷',
    time: '4小时前',
    type: 'exam',
    iconSrc: '/src/assets/balance.png'
  },
  {
    id: 3,
    title: '上传了教学资源《Java基础教程》',
    time: '6小时前',
    type: 'resource',
    iconSrc: '/src/assets/search.png'
  },
  {
    id: 4,
    title: '查看了学生张三的学习报告',
    time: '8小时前',
    type: 'analysis',
    iconSrc: '/src/assets/category.png'
  }
])
const isLoading = ref(false)

// 计算属性
const hasData = computed(() => {
  return stats.value.totalStudents > 0 || stats.value.totalCourses > 0
})

// 格式化平均成绩为百分比
const formatAvgScore = (score) => {
  if (!score || score === 0) return '0.00'
  // 如果分数已经是百分比格式（0-100），直接返回
  if (score <= 100) {
    return score.toFixed(2)
  }
  // 如果分数是0-1格式，转换为百分比
  return (score * 100).toFixed(2)
}

// 获取教师统计信息
const fetchTeacherStats = async () => {
  try {
    isLoading.value = true
    const res = await getTeacherStats(teacherId)
    if (res && res.data) {
      stats.value = {
        totalStudents: res.data.totalStudents || 0,
        totalCourses: res.data.totalCourses || 0,
        avgScore: res.data.avgScore || 0,
        teachingHours: res.data.teachingHours || 0
      }
      console.log('教师统计信息:', res.data)
    }
  } catch (error) {
    console.error('获取教师统计信息失败:', error)
    // 使用默认数据
    stats.value = {
      totalStudents: 156,
      totalCourses: 8,
      avgScore: 87.5,
      teachingHours: 24
    }
  } finally {
    isLoading.value = false
  }
}

// 获取课程统计信息
const fetchCourseStats = async () => {
  try {
    // 等待课程列表加载完成后再获取统计信息
    if (courseList.value.length > 0) {
      const courseId = courseList.value[0].id
      const res = await getCourseStats(courseId)
      if (res && res.data) {
        console.log('课程统计信息:', res.data)
      }
    }
  } catch (error) {
    console.error('获取课程统计信息失败:', error)
  }
}

// 获取教师课程列表
const fetchTeacherCourses = async () => {
  try {
    console.log('开始获取教师课程列表，教师ID:', teacherId)
    const res = await getCoursesByTeacherId(teacherId)
    console.log('API响应:', res)
    
    if (res && res.data) {
      courseList.value = res.data
      // 根据课程数据生成最近活动
      generateRecentActivities()
      console.log('教师课程列表:', res.data)
      
      // 获取课程列表后，再获取课程统计信息
      await fetchCourseStats()
    } else {
      console.warn('API响应数据为空')
      generateRecentActivities()
    }
  } catch (error) {
    console.error('获取教师课程列表失败:', error)
    console.error('错误详情:', error.response?.data)
    console.error('错误状态:', error.response?.status)
    // 生成默认活动
    generateRecentActivities()
  }
}

// 生成最近活动（基于课程数据）
const generateRecentActivities = () => {
  const activities = []
  
  // 基于课程列表生成活动
  if (courseList.value.length > 0) {
    courseList.value.slice(0, 3).forEach((course, index) => {
      activities.push({
        id: index + 1,
        type: 'course',
        icon: 'fas fa-book',
        title: `创建了课程《${course.name || '数据结构与算法'}》`,
        time: `${index + 1}天前`
      })
    })
  }
  
  // 添加一些默认活动
  if (activities.length === 0) {
    activities.push(
      {
        id: 1,
        type: 'course',
        icon: 'fas fa-book',
        title: '创建了新课程《数据结构与算法》',
        time: '2小时前'
      },
      {
        id: 2,
        type: 'exam',
        icon: 'fas fa-file-alt',
        title: '生成了期中考试试卷',
        time: '4小时前'
      },
      {
        id: 3,
        type: 'analysis',
        icon: 'fas fa-chart-line',
        title: '查看了学生成绩分析报告',
        time: '1天前'
      },
      {
        id: 4,
        type: 'resource',
        icon: 'fas fa-upload',
        title: '上传了教学资源《算法导论》',
        time: '2天前'
      }
    )
  }
  
  recentActivities.value = activities
}

// 页面加载时获取数据
onMounted(async () => {
  try {
    await Promise.all([
      fetchTeacherStats(),
      fetchTeacherCourses()
    ])
  } catch (error) {
    console.error('页面初始化失败:', error)
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/theme.scss';

.teacher-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

/* 页面头部 */
.page-header {
  @include gradient-bg($bg-gradient-1);
  color: $white;
  padding: $spacing-2xl $spacing-xl;
  border-radius: $radius-2xl;
  margin-bottom: $spacing-xl;
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

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
  
  .stat-card {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    gap: 20px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
      
      .stat-icon {
        transform: scale(1.1);
      }
      
      .stat-decoration {
        opacity: 1;
      }
    }
    
    .stat-icon {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      transition: transform 0.3s ease;
      flex-shrink: 0;
      
      .icon-img {
        width: 32px;
        height: 32px;
        opacity: 0.9;
      }
      
      .icon-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 80px;
        height: 80px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
        border-radius: 50%;
        animation: pulse 2s ease-in-out infinite;
      }
    }
    
    .stat-content {
      flex: 1;
      
      h3 {
        margin: 0 0 8px 0;
        font-size: 2rem;
        font-weight: 800;
        color: #2d3748;
      }
      
      p {
        margin: 0 0 8px 0;
        font-size: 14px;
        color: #718096;
        font-weight: 500;
      }
      
      .stat-change {
        display: flex;
        align-items: center;
        font-size: 12px;
        font-weight: 600;
        
        .change-icon {
          width: 14px;
          height: 14px;
          margin-right: 4px;
        }
        
        &.positive {
          color: #10b981;
        }
        
        &.negative {
          color: #ef4444;
        }
      }
    }
    
    .stat-decoration {
      position: absolute;
      top: 0;
      right: 0;
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
      border-radius: 0 20px 0 80px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
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

/* 主要内容区域 */
.main-content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

/* 卡片通用样式 */
.card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
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
    
    .header-description {
      margin: 8px 0 0 36px;
      color: #718096;
      font-size: 14px;
    }
  }
}

/* 快速操作 */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  
  .action-btn {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    border: none;
    
    &:hover {
      background: white;
      transform: translateY(-4px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
      
      .action-icon {
        transform: scale(1.1);
      }
      
      .action-decoration {
        opacity: 1;
      }
    }
    
    .action-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: transform 0.3s ease;
      flex-shrink: 0;
      
      .icon-img {
        width: 24px;
        height: 24px;
        opacity: 0.9;
      }
    }
    
    .action-content {
      flex: 1;
      
      .action-title {
        display: block;
        font-size: 16px;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 4px;
      }
      
      .action-desc {
        display: block;
        font-size: 12px;
        color: #718096;
      }
    }
    
    .action-decoration {
      position: absolute;
      top: 0;
      right: 0;
      width: 60px;
      height: 60px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
      border-radius: 0 16px 0 60px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }
}

/* 效率图表 */
.chart-container {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .chart-placeholder {
    text-align: center;
    color: #718096;
    
    .chart-icon {
      width: 48px;
      height: 48px;
      opacity: 0.5;
      margin-bottom: 12px;
    }
    
    p {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: 600;
    }
    
    span {
      font-size: 14px;
    }
  }
}

/* 最近活动 */
.activities-list {
  .activity-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    margin-bottom: 12px;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateX(4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .activity-icon {
      width: 40px;
      height: 40px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
    
    .activity-content {
      flex: 1;
      
      .activity-title {
        margin: 0 0 4px 0;
        font-size: 14px;
        font-weight: 600;
        color: #2d3748;
      }
      
      .activity-time {
        margin: 0;
        font-size: 12px;
        color: #718096;
      }
    }
    
    .activity-actions {
      .view-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        background: transparent;
        color: #667eea;
        border: 2px solid #667eea;
        border-radius: 8px;
        font-size: 12px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          background: #667eea;
          color: white;
          transform: translateY(-2px);
        }
        
        .btn-icon {
          width: 14px;
          height: 14px;
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
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .main-content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .card {
    padding: 20px;
  }
}
</style> 