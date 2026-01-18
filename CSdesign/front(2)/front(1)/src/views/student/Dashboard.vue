<template>
  <div class="student-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/logo.png" alt="Logo" class="header-logo">
          <h1>学习中心</h1>
        </div>
        <p class="header-subtitle">欢迎使用智能教学管理系统，这里是您的学习管理中心</p>
      </div>
      <div class="header-actions">
        <button @click="$router.push('/student/online-learning')" class="action-btn primary">
          <img src="@/assets/add.png" alt="开始学习" class="btn-icon">
          开始学习
        </button>
        <button @click="$router.push('/student/practice-evaluation')" class="action-btn secondary">
          <img src="@/assets/search.png" alt="练习测试" class="btn-icon">
          练习测试
        </button>
      </div>
    </div>

    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-avatar">
          <img src="@/assets/author.png" alt="用户头像" class="avatar-img">
          <div class="avatar-glow"></div>
        </div>
        <div class="welcome-text">
          <h2>欢迎回来，{{ username }}！</h2>
          <p>今天也要努力学习哦，继续你的学习之旅吧！</p>
          <div class="welcome-stats">
            <span class="stat-item">
              <img src="@/assets/time.png" alt="日历" class="stat-icon">
              连续学习 {{ stats.streakDays }} 天
            </span>
            <span class="stat-item">
              <img src="@/assets/balance.png" alt="时钟" class="stat-icon">
              总学习时长 {{ stats.totalStudyTime }}h
            </span>
          </div>
        </div>
      </div>
      <div class="welcome-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
    
    <!-- 学习统计 -->
    <div class="stats-grid">
      <div class="stat-card" @click="$router.push('/student/online-learning')">
        <div class="stat-icon-wrapper">
          <img src="@/assets/time.png" alt="学习时长" class="stat-icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalStudyTime }}h</h3>
          <p>学习时长</p>
          <span class="stat-change positive">+3.2h 本周</span>
        </div>
        <div class="card-decoration"></div>
      </div>
      
      <div class="stat-card" @click="$router.push('/student/online-learning')">
        <div class="stat-icon-wrapper">
          <img src="@/assets/category.png" alt="已完成课程" class="stat-icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.completedCourses }}</h3>
          <p>已完成课程</p>
          <span class="stat-change positive">+2 本月</span>
        </div>
        <div class="card-decoration"></div>
      </div>
      
      <div class="stat-card" @click="$router.push('/student/practice-evaluation')">
        <div class="stat-icon-wrapper">
          <img src="@/assets/balance.png" alt="平均成绩" class="stat-icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.avgScore }}%</h3>
          <p>平均成绩</p>
          <span class="stat-change positive">+4.5% 较上月</span>
        </div>
        <div class="card-decoration"></div>
      </div>
      
      <div class="stat-card" @click="$router.push('/student/learning-assistant')">
        <div class="stat-icon-wrapper">
          <img src="@/assets/search.png" alt="连续学习天数" class="stat-icon-img">
          <div class="icon-glow"></div>
        </div>
        <div class="stat-content">
          <h3>{{ stats.streakDays }}</h3>
          <p>连续学习天数</p>
          <span class="stat-change positive">保持中</span>
        </div>
        <div class="card-decoration"></div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-content-grid">
      <!-- 学习进度 -->
      <div class="progress-section card">
        <div class="card-header">
          <div class="header-left">
            <img src="@/assets/tag.png" alt="课程进度" class="header-icon">
            <h3>课程进度</h3>
          </div>
          <button @click="$router.push('/student/online-learning')" class="view-all-btn">
            查看全部
          </button>
        </div>
        <div class="progress-list">
          <div 
            v-for="course in learningProgress" 
            :key="course.id"
            class="progress-item"
            @click="continueCourse(course)"
          >
            <div class="course-info">
              <div class="course-icon">
                <img src="@/assets/category.png" alt="课程" class="course-icon-img">
              </div>
              <div class="course-details">
                <h4>{{ course.title }}</h4>
                <p>{{ course.subject }} • {{ course.teacher }}</p>
              </div>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: course.progress + '%' }"></div>
            </div>
            <div class="progress-text">
              <span>{{ course.progress }}%</span>
              <button class="continue-btn">
                继续学习
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- AI学习助手 -->
      <div class="ai-assistant card">
        <div class="card-header">
          <div class="header-left">
            <img src="@/assets/search.png" alt="AI助手" class="header-icon">
            <h3>AI学习助手</h3>
          </div>
          <div class="assistant-status">
            <span class="status-dot online"></span>
            在线
          </div>
        </div>
        <div class="assistant-content">
          <div class="assistant-avatar">
            <img src="@/assets/author.png" alt="AI助手" class="assistant-img">
            <div class="assistant-glow"></div>
          </div>
          <div class="assistant-message">
            <p>{{ aiMessage }}</p>
          </div>
          <div class="assistant-actions">
            <button @click="askQuestion" class="action-btn primary">
              <img src="@/assets/add.png" alt="提问" class="btn-icon">
              提问
            </button>
            <button @click="getRecommendation" class="action-btn secondary">
              <img src="@/assets/search.png" alt="学习建议" class="btn-icon">
              学习建议
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 推荐练习 -->
    <div class="recommended-practice card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/balance.png" alt="推荐练习" class="header-icon">
          <h3>推荐练习</h3>
        </div>
        <button @click="$router.push('/student/practice-evaluation')" class="view-all-btn">
          查看全部
        </button>
      </div>
      <div class="practice-grid">
        <div 
          v-for="practice in recommendedPractice" 
          :key="practice.id"
          class="practice-card"
          @click="startPractice(practice)"
        >
          <div class="practice-icon" :class="practice.type">
            <img :src="practice.iconSrc" :alt="practice.title" class="practice-icon-img">
          </div>
          <div class="practice-content">
            <h4>{{ practice.title }}</h4>
            <p>{{ practice.description }}</p>
            <div class="practice-meta">
              <span class="difficulty" :class="practice.difficulty">{{ practice.difficultyText }}</span>
              <span class="duration">{{ practice.duration }}分钟</span>
            </div>
          </div>
          <div class="practice-arrow">
            <img src="@/assets/category.png" alt="箭头" class="arrow-icon">
          </div>
        </div>
      </div>
    </div>
    
    <!-- 最近学习 -->
    <div class="recent-learning card">
      <div class="card-header">
        <div class="header-left">
          <img src="@/assets/time.png" alt="最近学习" class="header-icon">
          <h3>最近学习活动</h3>
        </div>
        <button @click="viewAllActivities" class="view-all-btn">
          查看全部
        </button>
      </div>
      <div class="learning-timeline">
        <div 
          v-for="activity in recentLearning" 
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon">
            <img :src="activity.iconSrc" :alt="activity.title" class="activity-icon-img">
          </div>
          <div class="activity-content">
            <h4>{{ activity.title }}</h4>
            <p>{{ activity.description }}</p>
          </div>
          <span class="activity-time">{{ activity.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getStudentStats, getLearningTrend } from '@/api/stats'
import { getCoursesByStudentId } from '@/api/course'
import { getRecommendedPractices, getPracticeHistory } from '@/api/practice'
import { getUserById } from '@/api/user'

const router = useRouter()

// 从本地存储获取用户信息
const userId = localStorage.getItem('userId') || '17' // 默认使用学生ID 17

// 响应式变量
const username = ref(localStorage.getItem('realName') || localStorage.getItem('username') || '李同学')
const isLoading = ref(false)
const stats = ref({
  totalStudyTime: 0,
  completedCourses: 0,
  avgScore: 0,
  streakDays: 0
})

const learningProgress = ref([])
const recommendedPractice = ref([])
const recentLearning = ref([])
const aiMessage = ref('根据你的学习情况，我建议你今天复习一下数据结构的基础概念，然后完成相关的练习题。')

// 计算属性
const hasData = computed(() => {
  return stats.value.totalStudyTime > 0 || stats.value.completedCourses > 0
})

// 获取学生统计信息
const fetchStudentStats = async () => {
  try {
    isLoading.value = true
    console.log('开始获取学生统计信息，学生ID:', userId)
    const res = await getStudentStats(userId)
    console.log('学生统计信息API响应:', res)
    
    if (res && res.data) {
      stats.value = {
        totalStudyTime: res.data.totalStudyTime || 0,
        completedCourses: res.data.completedCourses || 0,
        avgScore: res.data.avgScore || 0,
        streakDays: res.data.streakDays || 0
      }
      console.log('学生统计信息:', res.data)
    }
  } catch (error) {
    console.error('获取学生统计信息失败:', error)
    console.error('错误详情:', error.response?.data)
    // 使用默认数据
    stats.value = {
      totalStudyTime: 45.5,
      completedCourses: 8,
      avgScore: 87.3,
      streakDays: 12
    }
  } finally {
    isLoading.value = false
  }
}

// 获取学生课程列表
const fetchStudentCourses = async () => {
  try {
    console.log('开始获取学生课程列表，学生ID:', userId)
    const res = await getCoursesByStudentId(userId)
    console.log('学生课程列表API响应:', res)
    
    if (res && (res.success === true || res.code === 200) && res.data && Array.isArray(res.data) && res.data.length > 0) {
      learningProgress.value = res.data.map(course => ({
        id: course.id,
        title: course.name || course.title,
        subject: course.subject || '计算机基础',
        teacher: course.teacherName || '张教授',
        progress: course.progress || Math.floor(Math.random() * 100)
      }))
      console.log('学生课程列表:', res.data)
      console.log('映射后的课程列表:', learningProgress.value)
    } else {
      console.warn('API响应数据为空或格式不正确, res:', res)
      generateDefaultCourses()
    }
  } catch (error) {
    console.error('获取学生课程列表失败:', error)
    console.error('错误详情:', error.response?.data)
    // 使用默认数据
    generateDefaultCourses()
  }
}

// 生成默认课程数据
const generateDefaultCourses = () => {
  learningProgress.value = [
    {
      id: 1,
      title: '数据结构与算法',
      subject: '计算机基础',
      teacher: '张教授',
      progress: 75
    },
    {
      id: 2,
      title: 'Java程序设计',
      subject: '编程语言',
      teacher: '王教授',
      progress: 60
    },
    {
      id: 3,
      title: '数据库系统',
      subject: '计算机基础',
      teacher: '李教授',
      progress: 45
    }
  ]
}

// 获取推荐练习
const fetchRecommendedPractices = async () => {
  try {
    console.log('开始获取推荐练习')
    const res = await getRecommendedPractices()
    console.log('推荐练习API响应:', res)
    
    if (res && res.data) {
      recommendedPractice.value = res.data.map(practice => ({
        id: practice.id,
        title: practice.title,
        description: practice.description,
        type: practice.type,
        iconSrc: getPracticeIconSrc(practice.type),
        difficulty: practice.difficulty,
        difficultyText: getDifficultyText(practice.difficulty),
        duration: practice.duration || 30
      }))
      console.log('推荐练习:', res.data)
    } else {
      console.warn('推荐练习API响应数据为空')
      generateDefaultPractices()
    }
  } catch (error) {
    console.error('获取推荐练习失败:', error)
    console.error('错误详情:', error.response?.data)
    // 使用默认数据
    generateDefaultPractices()
  }
}

// 生成默认练习数据
const generateDefaultPractices = () => {
  recommendedPractice.value = [
    {
      id: 1,
      title: '数据结构基础练习',
      description: '巩固数组、链表等基础数据结构的概念和应用',
      type: 'theory',
      iconSrc: '/src/assets/category.png',
      difficulty: 'easy',
      difficultyText: '简单',
      duration: 30
    },
    {
      id: 2,
      title: '算法思维训练',
      description: '提升算法思维，掌握常见算法模式',
      type: 'practice',
      iconSrc: '/src/assets/balance.png',
      difficulty: 'medium',
      difficultyText: '中等',
      duration: 45
    },
    {
      id: 3,
      title: '编程实战练习',
      description: '通过实际编程题目提升编程能力',
      type: 'coding',
      iconSrc: '/src/assets/search.png',
      difficulty: 'hard',
      difficultyText: '困难',
      duration: 60
    }
  ]
}

// 获取练习图标
const getPracticeIconSrc = (type) => {
  const iconMap = {
    theory: '/src/assets/category.png',
    practice: '/src/assets/balance.png',
    coding: '/src/assets/search.png',
    quiz: '/src/assets/time.png'
  }
  return iconMap[type] || '/src/assets/category.png'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const textMap = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return textMap[difficulty] || '中等'
}

// 获取学习历史
const fetchLearningHistory = async () => {
  try {
    console.log('开始获取学习历史')
    const res = await getPracticeHistory(userId)
    console.log('学习历史API响应:', res)
    
    if (res && res.data) {
      recentLearning.value = res.data.map(activity => ({
        id: activity.id,
        title: activity.title,
        description: activity.description,
        type: activity.type,
        iconSrc: getActivityIconSrc(activity.type),
        time: activity.time || '刚刚'
      }))
      console.log('学习历史:', res.data)
    } else {
      console.warn('学习历史API响应数据为空')
      generateDefaultActivities()
    }
  } catch (error) {
    console.error('获取学习历史失败:', error)
    console.error('错误详情:', error.response?.data)
    // 使用默认数据
    generateDefaultActivities()
  }
}

// 生成默认活动数据
const generateDefaultActivities = () => {
  recentLearning.value = [
    {
      id: 1,
      title: '完成了数据结构练习',
      description: '完成了数组和链表的练习题',
      type: 'practice',
      iconSrc: '/src/assets/category.png',
      time: '1小时前'
    },
    {
      id: 2,
      title: '观看了Java视频课程',
      description: '学习了面向对象编程基础',
      type: 'video',
      iconSrc: '/src/assets/balance.png',
      time: '3小时前'
    },
    {
      id: 3,
      title: '参加了在线测验',
      description: '完成了算法基础测验',
      type: 'quiz',
      iconSrc: '/src/assets/search.png',
      time: '5小时前'
    }
  ]
}

// 获取活动图标
const getActivityIconSrc = (type) => {
  const iconMap = {
    practice: '/src/assets/category.png',
    video: '/src/assets/balance.png',
    quiz: '/src/assets/search.png',
    exam: '/src/assets/time.png'
  }
  return iconMap[type] || '/src/assets/category.png'
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId') || '17'
    console.log('开始获取用户信息，用户ID:', userId)
    const res = await getUserById(userId)
    console.log('用户信息API响应:', res)
    
    if (res && res.data) {
      username.value = res.data.realName || res.data.username || '李同学'
      console.log('用户信息:', res.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    console.error('错误详情:', error.response?.data)
  }
}

// 继续课程
const continueCourse = (course) => {
  router.push(`/student/online-learning?courseId=${course.id}`)
}

// 开始练习
const startPractice = (practice) => {
  router.push(`/student/practice-evaluation?practiceId=${practice.id}`)
}

// 提问
const askQuestion = () => {
  router.push('/student/learning-assistant')
}

// 获取学习建议
const getRecommendation = () => {
  router.push('/student/learning-assistant')
}

// 查看所有活动
const viewAllActivities = () => {
  router.push('/student/online-learning')
}

// 页面加载时获取数据
onMounted(async () => {
  try {
    await Promise.all([
      fetchUserInfo(),
      fetchStudentStats(),
      fetchStudentCourses(),
      fetchRecommendedPractices(),
      fetchLearningHistory()
    ])
  } catch (error) {
    console.error('页面初始化失败:', error)
  }
})
</script>

<style lang="scss" scoped>
.student-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

/* 页面头部 */
.dashboard-header {
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
    display: flex;
    flex-direction: column;
    gap: 16px;
    position: relative;
    z-index: 1;
  }
  
  .header-title {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .header-logo {
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
  
  .header-actions {
    display: flex;
    gap: 16px;
    margin-top: 24px;
    
    .action-btn {
      display: flex;
      align-items: center;
      padding: 14px 28px;
      border-radius: 50px;
      border: 2px solid rgba(255, 255, 255, 0.3);
      background: rgba(255, 255, 255, 0.1);
      color: white;
      font-weight: 600;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.3s ease;
      backdrop-filter: blur(10px);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
      }
      
      &.primary {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.4);
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }
      }
      
      &.secondary {
        background: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.2);
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
      }
      
      .btn-icon {
        width: 18px;
        height: 18px;
        margin-right: 8px;
        opacity: 0.9;
      }
    }
  }
}

/* 欢迎区域 */
.welcome-section {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .welcome-content {
    display: flex;
    align-items: center;
    gap: 24px;
    position: relative;
    z-index: 2;
    
    .welcome-avatar {
      position: relative;
      
      .avatar-img {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        object-fit: cover;
        border: 4px solid white;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }
      
      .avatar-glow {
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
    }
    
    .welcome-text {
      flex: 1;
      
      h2 {
        margin: 0 0 8px 0;
        font-size: 1.8rem;
        font-weight: 700;
        color: #2d3748;
      }
      
      p {
        margin: 0 0 16px 0;
        color: #718096;
        font-size: 1rem;
      }
      
      .welcome-stats {
        display: flex;
        gap: 24px;
        
        .stat-item {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #4a5568;
          font-weight: 500;
          font-size: 14px;
          
          .stat-icon {
            width: 16px;
            height: 16px;
            opacity: 0.7;
          }
        }
      }
    }
  }
  
  .welcome-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
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

/* 学习统计 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
  
  .stat-card {
    background: white;
    border-radius: 20px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
      
      .stat-icon-wrapper {
        transform: scale(1.1);
      }
      
      .card-decoration {
        opacity: 1;
      }
    }
    
    .stat-icon-wrapper {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 20px;
      position: relative;
      transition: transform 0.3s ease;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      
      .stat-icon-img {
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
      h3 {
        margin: 0 0 8px 0;
        font-size: 2rem;
        font-weight: 800;
        color: #2d3748;
      }
      
      p {
        margin: 0 0 8px 0;
        color: #718096;
        font-weight: 500;
      }
      
      .stat-change {
        font-size: 14px;
        font-weight: 600;
        
        &.positive {
          color: #48bb78;
        }
        
        &.negative {
          color: #e53e3e;
        }
      }
    }
    
    .card-decoration {
      position: absolute;
      top: 0;
      right: 0;
      width: 100px;
      height: 100px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
      border-radius: 0 20px 0 100px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }
}

/* 主要内容区域 */
.main-content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
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
    
    .view-all-btn {
      padding: 8px 16px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
      }
    }
  }
}

/* 进度区域 */
.progress-list {
  .progress-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    margin-bottom: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f7fafc;
      transform: translateX(4px);
    }
    
    .course-info {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;
      
      .course-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .course-icon-img {
          width: 20px;
          height: 20px;
          opacity: 0.9;
        }
      }
      
      .course-details {
        h4 {
          margin: 0 0 4px 0;
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
        }
        
        p {
          margin: 0;
          font-size: 12px;
          color: #718096;
        }
      }
    }
    
    .progress-bar {
      width: 120px;
      height: 6px;
      background: #e2e8f0;
      border-radius: 3px;
      overflow: hidden;
      
      .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        border-radius: 3px;
        transition: width 0.3s ease;
      }
    }
    
    .progress-text {
      display: flex;
      align-items: center;
      gap: 12px;
      
      span {
        font-size: 12px;
        font-weight: 600;
        color: #4a5568;
        min-width: 40px;
      }
      
      .continue-btn {
        padding: 6px 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 15px;
        font-size: 11px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
        }
      }
    }
  }
}

/* AI助手区域 */
.ai-assistant {
  .assistant-status {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;
    color: #718096;
    
    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      
      &.online {
        background: #48bb78;
        animation: pulse 2s ease-in-out infinite;
      }
    }
  }
  
  .assistant-content {
    text-align: center;
    
    .assistant-avatar {
      position: relative;
      display: inline-block;
      margin-bottom: 16px;
      
      .assistant-img {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid #e2e8f0;
      }
      
      .assistant-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 80px;
        height: 80px;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.2) 0%, transparent 70%);
        border-radius: 50%;
        animation: pulse 2s ease-in-out infinite;
      }
    }
    
    .assistant-message {
      margin-bottom: 20px;
      
      p {
        margin: 0;
        color: #4a5568;
        font-size: 14px;
        line-height: 1.5;
      }
    }
    
    .assistant-actions {
      display: flex;
      gap: 12px;
      justify-content: center;
      
      .action-btn {
        display: flex;
        align-items: center;
        padding: 10px 16px;
        border-radius: 20px;
        border: none;
        font-size: 12px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &.primary {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
          }
        }
        
        &.secondary {
          background: #f7fafc;
          color: #4a5568;
          border: 1px solid #e2e8f0;
          
          &:hover {
            background: #edf2f7;
            transform: translateY(-2px);
          }
        }
        
        .btn-icon {
          width: 14px;
          height: 14px;
          margin-right: 6px;
          opacity: 0.8;
        }
      }
    }
  }
}

/* 推荐练习 */
.practice-grid {
  display: grid;
  gap: 16px;
  
  .practice-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: #f7fafc;
      transform: translateX(4px);
      border-color: #667eea;
    }
    
    .practice-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      
      .practice-icon-img {
        width: 24px;
        height: 24px;
        opacity: 0.9;
      }
    }
    
    .practice-content {
      flex: 1;
      
      h4 {
        margin: 0 0 4px 0;
        font-size: 14px;
        font-weight: 600;
        color: #2d3748;
      }
      
      p {
        margin: 0 0 8px 0;
        font-size: 12px;
        color: #718096;
      }
      
      .practice-meta {
        display: flex;
        gap: 12px;
        
        .difficulty {
          padding: 2px 8px;
          border-radius: 10px;
          font-size: 10px;
          font-weight: 600;
          
          &.easy {
            background: #c6f6d5;
            color: #22543d;
          }
          
          &.medium {
            background: #fef5e7;
            color: #744210;
          }
          
          &.hard {
            background: #fed7d7;
            color: #742a2a;
          }
        }
        
        .duration {
          font-size: 10px;
          color: #718096;
        }
      }
    }
    
    .practice-arrow {
      .arrow-icon {
        width: 16px;
        height: 16px;
        opacity: 0.5;
        transition: transform 0.3s ease;
      }
    }
    
    &:hover .arrow-icon {
      transform: translateX(4px);
    }
  }
}

/* 最近学习 */
.recent-learning {
  .learning-timeline {
    .activity-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px solid #e2e8f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .activity-icon {
        width: 32px;
        height: 32px;
        border-radius: 8px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        
        .activity-icon-img {
          width: 16px;
          height: 16px;
          opacity: 0.9;
        }
      }
      
      .activity-content {
        flex: 1;
        
        h4 {
          margin: 0 0 2px 0;
          font-size: 13px;
          font-weight: 600;
          color: #2d3748;
        }
        
        p {
          margin: 0;
          font-size: 11px;
          color: #718096;
        }
      }
      
      .activity-time {
        font-size: 11px;
        color: #a0aec0;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 24px 20px;
    
    .header-title h1 {
      font-size: 2rem;
    }
    
    .header-actions {
      flex-direction: column;
      
      .action-btn {
        width: 100%;
        justify-content: center;
      }
    }
  }
  
  .welcome-section {
    padding: 24px 20px;
    
    .welcome-content {
      flex-direction: column;
      text-align: center;
      
      .welcome-stats {
        justify-content: center;
      }
    }
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    
    .stat-card {
      padding: 24px 20px;
    }
  }
  
  .main-content-grid {
    gap: 16px;
  }
  
  .card {
    padding: 20px;
  }
}
</style> 