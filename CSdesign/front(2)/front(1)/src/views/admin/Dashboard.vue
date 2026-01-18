<template>
  <div class="admin-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-content">
        <div class="header-title">
          <h1>系统管理中心</h1>
          <div class="status-indicator">
            <span class="status-dot online"></span>
            <span class="status-text">系统运行正常</span>
          </div>
        </div>
        <p class="header-subtitle">智能教学管理系统的综合管理平台，实时监控系统状态和教学效果</p>
      </div>
      <div class="header-actions">
        <button @click="refreshData" class="action-btn primary" :disabled="isLoading">
          <span class="btn-icon">🔄</span>
          {{ isLoading ? '刷新中...' : '刷新数据' }}
        </button>
        <button @click="exportReport" class="action-btn secondary">
          <span class="btn-icon">📊</span>
          导出报告
        </button>
      </div>
    </div>

    <!-- 关键指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card" @click="navigateToUsers">
        <div class="metric-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <span class="icon">👥</span>
        </div>
        <div class="metric-content">
          <h3>{{ formatNumber(metrics.totalUsers) }}</h3>
          <p>用户总数</p>
          <div class="metric-details">
            <span class="metric-change positive">
              <span class="change-icon">📈</span>
              +15% 较上月
            </span>
            <span class="metric-trend">持续增长</span>
          </div>
        </div>
        <div class="metric-arrow">
          <span class="arrow-icon">→</span>
        </div>
      </div>
      
      <div class="metric-card" @click="navigateToTeachers">
        <div class="metric-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <span class="icon">👨‍🏫</span>
        </div>
        <div class="metric-content">
          <h3>{{ formatNumber(metrics.totalTeachers) }}</h3>
          <p>教师数量</p>
          <div class="metric-details">
            <span class="metric-change positive">
              <span class="change-icon">📈</span>
              +3 本月新增
            </span>
            <span class="metric-trend">稳定增长</span>
          </div>
        </div>
        <div class="metric-arrow">
          <span class="arrow-icon">→</span>
        </div>
      </div>
      
      <div class="metric-card" @click="navigateToStudents">
        <div class="metric-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <span class="icon">👨‍🎓</span>
        </div>
        <div class="metric-content">
          <h3>{{ formatNumber(metrics.totalStudents) }}</h3>
          <p>学生数量</p>
          <div class="metric-details">
            <span class="metric-change positive">
              <span class="change-icon">📈</span>
              +45 本月新增
            </span>
            <span class="metric-trend">快速增长</span>
          </div>
        </div>
        <div class="metric-arrow">
          <span class="arrow-icon">→</span>
        </div>
      </div>
      
      <div class="metric-card" @click="navigateToCourses">
        <div class="metric-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <span class="icon">📚</span>
        </div>
        <div class="metric-content">
          <h3>{{ formatNumber(metrics.totalCourses) }}</h3>
          <p>课程总数</p>
          <div class="metric-details">
            <span class="metric-change positive">
              <span class="change-icon">📈</span>
              +8 本月新增
            </span>
            <span class="metric-trend">内容丰富</span>
          </div>
        </div>
        <div class="metric-arrow">
          <span class="arrow-icon">→</span>
        </div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-content-grid">
      <!-- 使用情况统计 -->
      <div class="usage-stats card">
        <div class="card-header">
          <h3>学习使用统计</h3>
          <div class="card-actions">
            <div class="stats-tabs">
              <button 
                v-for="tab in usageTabs" 
                :key="tab.id"
                class="tab-btn"
                :class="{ 'active': activeUsageTab === tab.id }"
                @click="activeUsageTab = tab.id"
              >
                {{ tab.name }}
              </button>
            </div>
          </div>
        </div>
        
        <div class="usage-chart-container">
          <div class="usage-chart">
            <canvas ref="usageChart" width="400" height="200"></canvas>
          </div>
          
          <div class="usage-summary">
            <div class="summary-item">
              <div class="summary-icon">
                <span class="icon">📅</span>
              </div>
              <div class="summary-content">
                <span class="summary-label">今日学习活动</span>
                <span class="summary-value">{{ formatNumber(usageData.todayActive) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <div class="summary-icon">
                <span class="icon">📊</span>
              </div>
              <div class="summary-content">
                <span class="summary-label">本周学习活动</span>
                <span class="summary-value">{{ formatNumber(usageData.weekActive) }}</span>
              </div>
            </div>
            <div class="summary-item">
              <div class="summary-icon">
                <span class="icon">📈</span>
              </div>
              <div class="summary-content">
                <span class="summary-label">本月学习活动</span>
                <span class="summary-value">{{ formatNumber(usageData.monthActive) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 教学效率指数 -->
      <div class="efficiency-index card">
        <div class="card-header">
          <h3>教学效率指数</h3>
          <div class="efficiency-score">
            <div class="score-circle">
              <svg class="score-svg" viewBox="0 0 120 120">
                <circle class="score-bg" cx="60" cy="60" r="50"></circle>
                <circle 
                  class="score-progress" 
                  cx="60" 
                  cy="60" 
                  r="50"
                  :stroke-dasharray="`${efficiencyIndex.score * 3.14}, 314`"
                ></circle>
              </svg>
              <div class="score-content">
                <span class="score-value">{{ efficiencyIndex.score }}</span>
                <span class="score-label">综合指数</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="efficiency-breakdown">
          <div class="breakdown-item">
            <div class="breakdown-header">
              <span class="breakdown-label">课程完成率</span>
              <span class="breakdown-value">{{ efficiencyIndex.courseCompletion }}%</span>
            </div>
            <div class="breakdown-bar">
              <div class="bar-fill" :style="{ width: efficiencyIndex.courseCompletion + '%' }"></div>
            </div>
          </div>
          
          <div class="breakdown-item">
            <div class="breakdown-header">
              <span class="breakdown-label">学生参与度</span>
              <span class="breakdown-value">{{ efficiencyIndex.studentEngagement }}%</span>
            </div>
            <div class="breakdown-bar">
              <div class="bar-fill" :style="{ width: efficiencyIndex.studentEngagement + '%' }"></div>
            </div>
          </div>
          
          <div class="breakdown-item">
            <div class="breakdown-header">
              <span class="breakdown-label">学习效果</span>
              <span class="breakdown-value">{{ efficiencyIndex.learningEffect }}%</span>
            </div>
            <div class="breakdown-bar">
              <div class="bar-fill" :style="{ width: efficiencyIndex.learningEffect + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 学生学习效果分析 -->
    <div class="learning-analysis card">
      <div class="card-header">
        <h3>学习效果分析</h3>
        <div class="card-actions">
          <button @click="viewDetailedAnalysis" class="action-btn outline">
            <span class="btn-icon">📊</span>
            详细分析
          </button>
        </div>
      </div>
      
      <div class="analysis-grid">
        <div class="analysis-item">
          <h4>成绩分布</h4>
          <div class="grade-distribution">
            <div class="grade-item">
              <div class="grade-info">
                <span class="grade-range">90-100分</span>
                <span class="grade-percent">{{ gradeDistribution.excellent }}%</span>
              </div>
              <div class="grade-bar">
                <div class="bar-fill excellent" :style="{ width: gradeDistribution.excellent + '%' }"></div>
              </div>
            </div>
            <div class="grade-item">
              <div class="grade-info">
                <span class="grade-range">80-89分</span>
                <span class="grade-percent">{{ gradeDistribution.good }}%</span>
              </div>
              <div class="grade-bar">
                <div class="bar-fill good" :style="{ width: gradeDistribution.good + '%' }"></div>
              </div>
            </div>
            <div class="grade-item">
              <div class="grade-info">
                <span class="grade-range">70-79分</span>
                <span class="grade-percent">{{ gradeDistribution.average }}%</span>
              </div>
              <div class="grade-bar">
                <div class="bar-fill average" :style="{ width: gradeDistribution.average + '%' }"></div>
              </div>
            </div>
            <div class="grade-item">
              <div class="grade-info">
                <span class="grade-range">60-69分</span>
                <span class="grade-percent">{{ gradeDistribution.below }}%</span>
              </div>
              <div class="grade-bar">
                <div class="bar-fill below" :style="{ width: gradeDistribution.below + '%' }"></div>
              </div>
            </div>
            <div class="grade-item">
              <div class="grade-info">
                <span class="grade-range">60分以下</span>
                <span class="grade-percent">{{ gradeDistribution.poor }}%</span>
              </div>
              <div class="grade-bar">
                <div class="bar-fill poor" :style="{ width: gradeDistribution.poor + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="analysis-item">
          <h4>常见错误知识点</h4>
          <div class="error-topics">
            <div 
              v-for="topic in errorTopics" 
              :key="topic.id"
              class="error-topic"
              @click="viewTopicDetails(topic)"
            >
              <div class="topic-info">
                <span class="topic-name">{{ topic.name }}</span>
                <span class="topic-subject">{{ topic.subject }}</span>
              </div>
              <div class="topic-stats">
                <div class="stat-item">
                  <span class="stat-label">错误率</span>
                  <span class="stat-value error-rate">{{ topic.errorRate }}%</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">错误次数</span>
                  <span class="stat-value">{{ topic.errorCount }}次</span>
                </div>
              </div>
              <div class="topic-arrow">
                <span class="arrow-icon">→</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 系统状态监控 -->
    <div class="system-monitor card">
      <div class="card-header">
        <h3>系统状态监控</h3>
        <div class="card-actions">
          <span class="status-indicator online">
            <span class="status-dot"></span>
            系统正常
          </span>
        </div>
      </div>
      
      <div class="monitor-grid">
        <div class="monitor-item">
          <div class="monitor-header">
            <h4>服务器状态</h4>
            <span class="status-badge online">正常</span>
          </div>
          <div class="monitor-metrics">
            <div class="metric-row">
              <span class="metric-label">CPU使用率</span>
              <div class="metric-value">
                <span class="value">{{ systemStatus.cpu }}%</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: systemStatus.cpu + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="metric-row">
              <span class="metric-label">内存使用率</span>
              <div class="metric-value">
                <span class="value">{{ systemStatus.memory }}%</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: systemStatus.memory + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="metric-row">
              <span class="metric-label">磁盘使用率</span>
              <div class="metric-value">
                <span class="value">{{ systemStatus.disk }}%</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: systemStatus.disk + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="metric-row">
              <span class="metric-label">网络延迟</span>
              <div class="metric-value">
                <span class="value">{{ systemStatus.network }}ms</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="monitor-item">
          <div class="monitor-header">
            <h4>数据库状态</h4>
            <span class="status-badge online">正常</span>
          </div>
          <div class="monitor-metrics">
            <div class="metric-row">
              <span class="metric-label">连接数</span>
              <div class="metric-value">
                <span class="value">{{ dbStatus.connections }}</span>
              </div>
            </div>
            <div class="metric-row">
              <span class="metric-label">查询响应时间</span>
              <div class="metric-value">
                <span class="value">{{ dbStatus.queryTime }}ms</span>
              </div>
            </div>
            <div class="metric-row">
              <span class="metric-label">缓存命中率</span>
              <div class="metric-value">
                <span class="value">{{ dbStatus.cacheHit }}%</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: dbStatus.cacheHit + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getSystemStats, getUsageStats, getEfficiencyIndex, getGradeDistribution, getErrorTopics, getSystemStatus, getPerformanceMetrics } from '@/api/stats'

const router = useRouter()

// 响应式数据
const isLoading = ref(false)

// 系统指标数据
const metrics = ref({
  totalUsers: 0,
  totalTeachers: 0,
  totalStudents: 0,
  totalCourses: 0
})

// 使用情况数据
const usageData = ref({
  todayActive: 0,
  weekActive: 0,
  monthActive: 0
})

// 使用情况标签页
const usageTabs = ref([
  { id: 'daily', name: '日' },
  { id: 'weekly', name: '周' },
  { id: 'monthly', name: '月' }
])
const activeUsageTab = ref('daily')

// 效率指数数据
const efficiencyIndex = ref({
  score: 0,
  courseCompletion: 0,
  studentEngagement: 0,
  learningEffect: 0
})

// 成绩分布数据
const gradeDistribution = ref({
  excellent: 0,
  good: 0,
  average: 0,
  below: 0,
  poor: 0
})

// 错误知识点数据
const errorTopics = ref([])

// 系统状态数据
const systemStatus = ref({
  cpu: 0,
  memory: 0,
  disk: 0,
  network: 0
})

// 数据库状态数据
const dbStatus = ref({
  connections: 0,
  queryTime: 0,
  cacheHit: 0
})

// 加载系统统计数据
const loadSystemStats = async () => {
  try {
    const res = await getSystemStats()
    if (res && res.data) {
      console.log('系统统计数据:', res.data)
      metrics.value = {
        totalUsers: res.data.totalUsers || 0,
        totalTeachers: res.data.totalTeachers || 0,
        totalStudents: res.data.totalStudents || 0,
        totalCourses: res.data.totalCourses || 0
      }
    }
  } catch (error) {
    console.error('获取系统统计失败:', error)
  }
}

// 加载使用情况数据
const loadUsageStats = async () => {
  try {
    const res = await getUsageStats()
    if (res && res.data) {
      console.log('使用情况数据:', res.data)
      usageData.value = {
        todayActive: res.data.todayActive || 0,
        weekActive: res.data.weekActive || 0,
        monthActive: res.data.monthActive || 0
      }
    }
  } catch (error) {
    console.error('获取使用情况失败:', error)
  }
}

// 加载效率指数数据
const loadEfficiencyIndex = async () => {
  try {
    const res = await getEfficiencyIndex()
    if (res && res.data) {
      console.log('效率指数数据:', res.data)
      efficiencyIndex.value = {
        score: res.data.score || 0,
        courseCompletion: res.data.courseCompletion || 0,
        studentEngagement: res.data.studentEngagement || 0,
        learningEffect: res.data.learningEffect || 0
      }
    }
  } catch (error) {
    console.error('获取效率指数失败:', error)
  }
}

// 加载成绩分布数据
const loadGradeDistribution = async () => {
  try {
    const res = await getGradeDistribution()
    if (res && res.data) {
      console.log('成绩分布数据:', res.data)
      gradeDistribution.value = {
        excellent: res.data.excellent || 0,
        good: res.data.good || 0,
        average: res.data.average || 0,
        below: res.data.below || 0,
        poor: res.data.poor || 0
      }
    }
  } catch (error) {
    console.error('获取成绩分布失败:', error)
  }
}

// 加载错误知识点数据
const loadErrorTopics = async () => {
  try {
    const res = await getErrorTopics()
    if (res && res.data) {
      console.log('错误知识点数据:', res.data)
      errorTopics.value = res.data.map(topic => ({
        id: topic.id,
        name: topic.name,
        subject: topic.description || '通用',
        errorRate: topic.errorRate || 0,
        errorCount: topic.errorCount || 0
      }))
    }
  } catch (error) {
    console.error('获取错误知识点失败:', error)
  }
}

// 加载系统状态数据
const loadSystemStatus = async () => {
  try {
    const res = await getSystemStatus()
    if (res && res.data) {
      console.log('系统状态数据:', res.data)
      
      // 服务器状态
      if (res.data.server) {
        systemStatus.value = {
          cpu: res.data.server.cpu || 0,
          memory: res.data.server.memory || 0,
          disk: res.data.server.disk || 0,
          network: res.data.server.network || 0
        }
      }
      
      // 数据库状态
      if (res.data.database) {
        dbStatus.value = {
          connections: res.data.database.connections || 0,
          queryTime: res.data.database.queryTime || 0,
          cacheHit: res.data.database.cacheHit || 0
        }
      }
    }
  } catch (error) {
    console.error('获取系统状态失败:', error)
  }
}

// 加载所有数据
const loadAllData = async () => {
  isLoading.value = true
  try {
    await Promise.all([
      loadSystemStats(),
      loadUsageStats(),
      loadEfficiencyIndex(),
      loadGradeDistribution(),
      loadErrorTopics(),
      loadSystemStatus()
    ])
    
    // 数据加载完成后初始化图表
    await nextTick()
    initCharts()
    
  } catch (error) {
    console.error('数据加载失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 方法
const formatNumber = (num) => {
  return num.toLocaleString()
}

const refreshData = async () => {
  await loadAllData()
}

const exportReport = () => {
  console.log('导出报告')
}

const navigateToUsers = () => {
  router.push('/admin/users')
}

const navigateToTeachers = () => {
  router.push('/admin/users?role=teacher')
}

const navigateToStudents = () => {
  router.push('/admin/users?role=student')
}

const navigateToCourses = () => {
  router.push('/admin/courses')
}

const viewDetailedAnalysis = () => {
  router.push('/admin/analytics')
}

const viewTopicDetails = (topic) => {
  console.log('查看知识点详情:', topic)
}

// 初始化图表
const usageChart = ref(null)

const initCharts = () => {
  if (usageChart.value) {
    initUsageChart()
  }
}

const initUsageChart = () => {
  const canvas = usageChart.value
  const ctx = canvas.getContext('2d')
  
  // 清除画布
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 绘制简单的使用情况图表
  const data = [
    usageData.value.todayActive,
    usageData.value.weekActive / 7, // 日均
    usageData.value.monthActive / 30 // 日均
  ]
  
  const maxValue = Math.max(...data)
  const chartWidth = canvas.width - 80
  const chartHeight = canvas.height - 60
  const barWidth = chartWidth / data.length / 2
  
  // 绘制背景
  ctx.fillStyle = '#f8fafc'
  ctx.fillRect(40, 30, chartWidth, chartHeight)
  
  // 绘制数据条
  data.forEach((value, index) => {
    const barHeight = (value / maxValue) * (chartHeight - 20)
    const x = 50 + index * (chartWidth / data.length)
    const y = chartHeight + 20 - barHeight
    
    // 创建渐变
    const gradient = ctx.createLinearGradient(0, y, 0, y + barHeight)
    gradient.addColorStop(0, '#667eea')
    gradient.addColorStop(1, '#764ba2')
    
    ctx.fillStyle = gradient
    ctx.fillRect(x, y, barWidth, barHeight)
    
    // 添加数值标签
    ctx.fillStyle = '#374151'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText(Math.round(value), x + barWidth / 2, y - 5)
  })
  
  // 添加标签
  const labels = ['今日', '周均', '月均']
  ctx.fillStyle = '#6b7280'
  ctx.font = '11px Arial'
  labels.forEach((label, index) => {
    const x = 50 + index * (chartWidth / data.length) + barWidth / 2
    ctx.fillText(label, x, chartHeight + 45)
  })
}

// 页面加载时初始化
onMounted(async () => {
  console.log('管理员仪表盘加载完成')
  await loadAllData()
})
</script>

<style lang="scss" scoped>
.admin-dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部样式 */
.dashboard-header {
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
      
      .status-indicator {
        display: flex;
        align-items: center;
        gap: 8px;
        background: rgba(255, 255, 255, 0.2);
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.9rem;
        
        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: #10b981;
          animation: pulse 2s infinite;
        }
        
        .status-text {
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
        
        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
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
      
      .btn-icon {
        font-size: 1rem;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 指标卡片样式 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
  
  .metric-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    }
    
    .metric-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 16px;
      
      .icon {
        font-size: 1.8rem;
        color: white;
      }
    }
    
    .metric-content {
      h3 {
        margin: 0 0 4px 0;
        font-size: 2rem;
        font-weight: 700;
        color: #1e293b;
      }
      
      p {
        margin: 0 0 12px 0;
        color: #64748b;
        font-weight: 500;
      }
      
      .metric-details {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .metric-change {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 0.9rem;
          font-weight: 500;
          
          &.positive {
            color: #10b981;
          }
          
          .change-icon {
            font-size: 0.8rem;
          }
        }
        
        .metric-trend {
          font-size: 0.8rem;
          color: #94a3b8;
        }
      }
    }
    
    .metric-arrow {
      position: absolute;
      top: 24px;
      right: 24px;
      opacity: 0;
      transition: all 0.3s ease;
      
      .arrow-icon {
        font-size: 1.2rem;
        color: #64748b;
      }
    }
    
    &:hover .metric-arrow {
      opacity: 1;
      transform: translateX(4px);
    }
  }
}

/* 主要内容网格 */
.main-content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

/* 卡片通用样式 */
.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 1.25rem;
      font-weight: 600;
      color: #1e293b;
    }
    
    .card-actions {
      .stats-tabs {
        display: flex;
        gap: 4px;
        background: #f1f5f9;
        border-radius: 8px;
        padding: 4px;
        
        .tab-btn {
          padding: 6px 12px;
          border: none;
          background: none;
          border-radius: 6px;
          font-size: 0.9rem;
          font-weight: 500;
          color: #64748b;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &.active {
            background: white;
            color: #3b82f6;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          }
        }
      }
      
      .status-indicator {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 0.9rem;
        font-weight: 500;
        
        &.online {
          color: #10b981;
        }
        
        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: currentColor;
        }
      }
      
      .action-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        background: white;
        border-radius: 6px;
        font-size: 0.9rem;
        font-weight: 500;
        color: #64748b;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &.outline {
          &:hover {
            background: #f8fafc;
            border-color: #cbd5e1;
          }
        }
        
        .btn-icon {
          font-size: 0.9rem;
        }
      }
    }
  }
}

/* 使用情况统计样式 */
.usage-stats {
  .usage-chart-container {
    display: grid;
    grid-template-columns: 1fr auto;
    gap: 24px;
    align-items: start;
    
    .usage-chart {
      background: #f8fafc;
      border-radius: 8px;
      padding: 16px;
      min-height: 200px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #64748b;
      font-size: 0.9rem;
    }
    
    .usage-summary {
      display: flex;
      flex-direction: column;
      gap: 16px;
      
      .summary-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        background: #f8fafc;
        border-radius: 8px;
        
        .summary-icon {
          width: 40px;
          height: 40px;
          background: #e2e8f0;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          .icon {
            font-size: 1.2rem;
            color: #64748b;
          }
        }
        
        .summary-content {
          display: flex;
          flex-direction: column;
          
          .summary-label {
            font-size: 0.8rem;
            color: #64748b;
            margin-bottom: 2px;
          }
          
          .summary-value {
            font-size: 1.1rem;
            font-weight: 600;
            color: #1e293b;
          }
        }
      }
    }
  }
}

/* 效率指数样式 */
.efficiency-index {
  .efficiency-score {
    .score-circle {
      position: relative;
      width: 80px;
      height: 80px;
      
      .score-svg {
        transform: rotate(-90deg);
        
        .score-bg {
          fill: none;
          stroke: #e2e8f0;
          stroke-width: 8;
        }
        
        .score-progress {
          fill: none;
          stroke: #3b82f6;
          stroke-width: 8;
          stroke-linecap: round;
          transition: stroke-dasharray 0.3s ease;
        }
      }
      
      .score-content {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        text-align: center;
        
        .score-value {
          display: block;
          font-size: 1.2rem;
          font-weight: 700;
          color: #1e293b;
        }
        
        .score-label {
          display: block;
          font-size: 0.7rem;
          color: #64748b;
        }
      }
    }
  }
  
  .efficiency-breakdown {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    .breakdown-item {
      .breakdown-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .breakdown-label {
          font-size: 0.9rem;
          color: #64748b;
        }
        
        .breakdown-value {
          font-size: 0.9rem;
          font-weight: 600;
          color: #1e293b;
        }
      }
      
      .breakdown-bar {
        height: 6px;
        background: #e2e8f0;
        border-radius: 3px;
        overflow: hidden;
        
        .bar-fill {
          height: 100%;
          background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 100%);
          border-radius: 3px;
          transition: width 0.3s ease;
        }
      }
    }
  }
}

/* 学习效果分析样式 */
.learning-analysis {
  .analysis-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    
    .analysis-item {
      h4 {
        margin: 0 0 16px 0;
        font-size: 1.1rem;
        font-weight: 600;
        color: #1e293b;
      }
      
      .grade-distribution {
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        .grade-item {
          .grade-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 6px;
            
            .grade-range {
              font-size: 0.9rem;
              color: #64748b;
            }
            
            .grade-percent {
              font-size: 0.9rem;
              font-weight: 600;
              color: #1e293b;
            }
          }
          
          .grade-bar {
            height: 8px;
            background: #e2e8f0;
            border-radius: 4px;
            overflow: hidden;
            
            .bar-fill {
              height: 100%;
              border-radius: 4px;
              transition: width 0.3s ease;
              
              &.excellent {
                background: #10b981;
              }
              
              &.good {
                background: #3b82f6;
              }
              
              &.average {
                background: #f59e0b;
              }
              
              &.below {
                background: #f97316;
              }
              
              &.poor {
                background: #ef4444;
              }
            }
          }
        }
      }
      
      .error-topics {
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        .error-topic {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px;
          background: #f8fafc;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &:hover {
            background: #f1f5f9;
            transform: translateX(4px);
          }
          
          .topic-info {
            flex: 1;
            
            .topic-name {
              display: block;
              font-weight: 500;
              color: #1e293b;
              margin-bottom: 2px;
            }
            
            .topic-subject {
              font-size: 0.8rem;
              color: #64748b;
            }
          }
          
          .topic-stats {
            display: flex;
            flex-direction: column;
            gap: 4px;
            text-align: right;
            
            .stat-item {
              .stat-label {
                font-size: 0.7rem;
                color: #64748b;
              }
              
              .stat-value {
                font-size: 0.8rem;
                font-weight: 600;
                color: #1e293b;
                
                &.error-rate {
                  color: #ef4444;
                }
              }
            }
          }
          
          .topic-arrow {
            .arrow-icon {
              font-size: 1rem;
              color: #64748b;
            }
          }
        }
      }
    }
  }
}

/* 系统监控样式 */
.system-monitor {
  .monitor-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    
    .monitor-item {
      .monitor-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        h4 {
          margin: 0;
          font-size: 1rem;
          font-weight: 600;
          color: #1e293b;
        }
        
        .status-badge {
          padding: 4px 8px;
          border-radius: 12px;
          font-size: 0.8rem;
          font-weight: 500;
          
          &.online {
            background: #dcfce7;
            color: #166534;
          }
        }
      }
      
      .monitor-metrics {
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        .metric-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .metric-label {
            font-size: 0.9rem;
            color: #64748b;
          }
          
          .metric-value {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .value {
              font-size: 0.9rem;
              font-weight: 600;
              color: #1e293b;
              min-width: 40px;
              text-align: right;
            }
            
            .progress-bar {
              width: 80px;
              height: 6px;
              background: #e2e8f0;
              border-radius: 3px;
              overflow: hidden;
              
              .progress-fill {
                height: 100%;
                background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 100%);
                border-radius: 3px;
                transition: width 0.3s ease;
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
  .main-content-grid {
    grid-template-columns: 1fr;
  }
  
  .analysis-grid {
    grid-template-columns: 1fr;
  }
  
  .monitor-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .dashboard-header {
    .header-actions {
      position: static;
      margin-top: 16px;
    }
  }
  
  .usage-chart-container {
    grid-template-columns: 1fr;
  }
}
</style> 