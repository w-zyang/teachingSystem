<template>
  <div class="data-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1>📊 数据驾驶舱</h1>
        <p>全面掌控教师和学生数据，洞察平台运营情况</p>
      </div>
      <div class="header-actions">
        <div class="refresh-indicator" :class="{ active: isRefreshing }">
          <button @click="refreshAllData" class="refresh-btn">
            <span class="refresh-icon">🔄</span>
            自动刷新
          </button>
          <span class="last-update">{{ lastUpdateTime }}</span>
        </div>
        <button @click="exportData" class="export-btn">
          📊 导出报告
        </button>
        <div class="notification-bell" @click="toggleNotifications">
          <span class="bell-icon">🔔</span>
          <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
        </div>
      </div>
    </div>

    <!-- 通知中心 -->
    <div v-if="showNotifications" class="notification-panel">
      <div class="notification-header">
        <h3>📬 系统通知</h3>
        <button @click="markAllAsRead" class="mark-read-btn">标记已读</button>
      </div>
      <div class="notification-list">
        <div v-for="notification in notifications" 
             :key="notification.id" 
             class="notification-item"
             :class="{ unread: !notification.read }">
          <div class="notification-icon">{{ notification.icon }}</div>
          <div class="notification-content">
            <h4>{{ notification.title }}</h4>
            <p>{{ notification.message }}</p>
            <span class="notification-time">{{ formatTime(notification.time) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 视图切换 -->
    <div class="view-tabs">
      <button @click="activeView = 'teacher'" 
              :class="{ active: activeView === 'teacher' }"
              class="tab-btn">
        👩‍🏫 教师数据
      </button>
      <button @click="activeView = 'student'" 
              :class="{ active: activeView === 'student' }"
              class="tab-btn">
        👨‍🎓 学生数据
      </button>
      <button @click="activeView = 'system'" 
              :class="{ active: activeView === 'system' }"
              class="tab-btn">
        ⚙️ 系统分析
      </button>
    </div>

    <!-- 教师数据视图 -->
    <div v-if="activeView === 'teacher'" class="teacher-dashboard">
      <div class="section-header">
        <h2>教师工作效率分析</h2>
        <select v-model="selectedTeacherId" @change="loadTeacherData" class="teacher-select">
          <option value="">选择教师</option>
          <option v-for="teacher in teachers" :key="teacher.id" :value="teacher.id">
            {{ teacher.realName }} - {{ teacher.subject }}
          </option>
        </select>
      </div>

      <div v-if="selectedTeacherId && teacherData" class="teacher-content">
        <!-- 教师概览卡片 -->
        <div class="overview-cards">
          <div class="overview-card">
            <div class="card-icon">📚</div>
            <div class="card-data">
              <h3>课程管理</h3>
              <div class="data-item">
                <span>管理课程</span>
                <span class="data-value">{{ teacherData.courseCount }}门</span>
              </div>
              <div class="data-item">
                <span>学生总数</span>
                <span class="data-value">{{ teacherData.studentCount }}人</span>
              </div>
            </div>
          </div>

          <div class="overview-card">
            <div class="card-icon">📝</div>
            <div class="card-data">
              <h3>教学活动</h3>
              <div class="data-item">
                <span>创建考试</span>
                <span class="data-value">{{ teacherData.examCount }}次</span>
              </div>
              <div class="data-item">
                <span>批改作业</span>
                <span class="data-value">{{ teacherData.gradedCount }}份</span>
              </div>
            </div>
          </div>

          <div class="overview-card">
            <div class="card-icon">🎯</div>
            <div class="card-data">
              <h3>教学效果</h3>
              <div class="data-item">
                <span>平均分</span>
                <span class="data-value">{{ teacherData.avgScore }}分</span>
              </div>
              <div class="data-item">
                <span>及格率</span>
                <span class="data-value">{{ teacherData.passRate }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 教师详细分析 -->
        <div class="teacher-analysis">
          <div class="analysis-card">
            <h3>📈 教学效率趋势</h3>
            <div class="efficiency-chart">
              <div v-for="data in teacherData.efficiencyTrend" 
                   :key="data.month" 
                   class="chart-item">
                <span class="chart-label">{{ data.month }}</span>
                <div class="chart-bar">
                  <div class="chart-fill" :style="{ height: data.value + '%' }"></div>
                </div>
                <span class="chart-value">{{ data.value }}%</span>
              </div>
            </div>
          </div>

          <div class="analysis-card">
            <h3>👥 学生表现分布</h3>
            <div class="performance-dist">
              <div v-for="grade in teacherData.gradeDistribution" 
                   :key="grade.level" 
                   class="grade-item">
                <span class="grade-label">{{ grade.level }}</span>
                <div class="grade-bar">
                  <div class="grade-fill" :style="{ width: grade.percentage + '%' }"></div>
                </div>
                <span class="grade-percent">{{ grade.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学生数据视图 -->
    <div v-if="activeView === 'student'" class="student-dashboard-admin">
      <div class="section-header">
        <h2>学生学习数据分析</h2>
        <select v-model="selectedStudentId" @change="loadStudentDataAdmin" class="student-select">
          <option value="">选择学生</option>
          <option v-for="student in students" :key="student.id" :value="student.id">
            {{ student.realName }} ({{ student.username }})
          </option>
        </select>
      </div>

      <div v-if="selectedStudentId && studentDataAdmin" class="student-content-admin">
        <!-- 学习概况 -->
        <div class="learning-overview">
          <div class="metric-card">
            <div class="metric-icon">⏱️</div>
            <div class="metric-content">
              <h4>学习时长</h4>
              <div class="metric-value">{{ studentDataAdmin.totalHours }}小时</div>
              <div class="metric-trend">本月 +25小时</div>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon">📊</div>
            <div class="metric-content">
              <h4>学习进度</h4>
              <div class="metric-value">{{ studentDataAdmin.progress }}%</div>
              <div class="metric-trend">本周 +8%</div>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon">🎯</div>
            <div class="metric-content">
              <h4>智能功能使用</h4>
              <div class="metric-value">{{ studentDataAdmin.aiUsage }}次</div>
              <div class="metric-trend">活跃用户</div>
            </div>
          </div>
        </div>

        <!-- 详细分析 -->
        <div class="detailed-analysis">
          <div class="analysis-section">
            <h3>🧠 智能学习功能使用情况</h3>
            <div class="feature-usage">
              <div v-for="feature in studentDataAdmin.featureUsage" 
                   :key="feature.name" 
                   class="feature-item">
                <span class="feature-name">{{ feature.name }}</span>
                <div class="usage-bar">
                  <div class="usage-fill" :style="{ width: feature.usage + '%' }"></div>
                </div>
                <span class="usage-value">{{ feature.usage }}%</span>
              </div>
            </div>
          </div>

          <div class="analysis-section">
            <h3>😊 情绪状态分析</h3>
            <div class="emotion-radar">
              <div v-for="emotion in studentDataAdmin.emotionData" 
                   :key="emotion.type" 
                   class="emotion-stat">
                <span class="emotion-icon">{{ getEmotionIcon(emotion.type) }}</span>
                <span class="emotion-name">{{ emotion.name }}</span>
                <span class="emotion-percentage">{{ emotion.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统分析视图 -->
    <div v-if="activeView === 'system'" class="system-dashboard">
      <div class="system-header">
        <h2>🖥️ 系统整体分析</h2>
        <div class="real-time-indicator">
          <span class="status-dot" :class="systemData.status"></span>
          <span>实时监控</span>
        </div>
      </div>
      
      <!-- 实时监控面板 -->
      <div class="monitoring-panel">
        <div class="monitor-card enhanced-monitor">
          <div class="monitor-header">
            <h3>📈 实时数据监控</h3>
            <div class="monitor-status">
              <span class="status-indicator" :class="getSystemStatus()"></span>
              <span class="status-text">{{ getSystemStatusText() }}</span>
            </div>
          </div>
          <div class="real-time-stats-enhanced">
            <div class="stat-item-enhanced">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">👥</span>
                <div class="stat-pulse"></div>
              </div>
              <div class="stat-content">
                <div class="stat-value-large">{{ systemData.activeUsers }}</div>
                <div class="stat-label">在线用户</div>
                <div class="stat-trend positive">
                  <span class="trend-icon">📈</span>
                  <span>+12% 较昨日</span>
                </div>
              </div>
              <div class="stat-chart">
                <canvas ref="userChart" width="60" height="40"></canvas>
              </div>
            </div>
            
            <div class="stat-item-enhanced">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">💾</span>
                <div class="stat-pulse" :class="getCpuStatusClass()"></div>
              </div>
              <div class="stat-content">
                <div class="stat-value-large">{{ systemData.cpuUsage }}%</div>
                <div class="stat-label">CPU使用率</div>
                <div class="progress-ring">
                  <svg class="progress-ring-svg" width="50" height="50">
                    <circle 
                      class="progress-ring-circle-bg" 
                      cx="25" cy="25" r="20"/>
                    <circle 
                      class="progress-ring-circle" 
                      cx="25" cy="25" r="20"
                      :style="{ 
                        strokeDasharray: `${systemData.cpuUsage * 1.25} 125`,
                        stroke: getCpuColor()
                      }"/>
                  </svg>
                </div>
              </div>
            </div>
            
            <div class="stat-item-enhanced">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">🗄️</span>
                <div class="stat-pulse" :class="getMemoryStatusClass()"></div>
              </div>
              <div class="stat-content">
                <div class="stat-value-large">{{ systemData.memoryUsage }}%</div>
                <div class="stat-label">内存使用率</div>
                <div class="memory-bar">
                  <div class="memory-fill" 
                       :style="{ 
                         width: systemData.memoryUsage + '%',
                         background: getMemoryColor()
                       }"></div>
                </div>
              </div>
            </div>
            
            <div class="stat-item-enhanced">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">⚡</span>
                <div class="stat-pulse" :class="getResponseStatusClass()"></div>
              </div>
              <div class="stat-content">
                <div class="stat-value-large">{{ systemData.responseTime }}ms</div>
                <div class="stat-label">响应时间</div>
                <div class="response-indicator">
                  <div class="response-bars">
                    <div v-for="i in 5" :key="i" 
                         class="response-bar"
                         :class="{ active: i <= getResponseBars() }"
                         :style="{ animationDelay: i * 0.1 + 's' }"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户活跃度热力图 -->
        <div class="monitor-card heatmap-card">
          <div class="card-header-enhanced">
            <h3>🔥 用户活跃度热力图</h3>
            <div class="time-filter">
              <button v-for="period in timePeriods" 
                      :key="period.value"
                      @click="selectedPeriod = period.value"
                      :class="{ active: selectedPeriod === period.value }"
                      class="period-btn">
                {{ period.label }}
              </button>
            </div>
          </div>
          <div class="heatmap-enhanced">
            <div class="heatmap-legend">
              <span class="legend-label">活跃度:</span>
              <div class="legend-scale">
                <div class="legend-item">
                  <div class="legend-color very-low"></div>
                  <span>0-20%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color low"></div>
                  <span>20-40%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color medium"></div>
                  <span>40-60%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color high"></div>
                  <span>60-80%</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color very-high"></div>
                  <span>80-100%</span>
                </div>
              </div>
            </div>
            <div class="heatmap-grid-enhanced">
              <div v-for="(hour, index) in systemData.activityHeatmap" 
                   :key="index" 
                   class="heatmap-cell-enhanced"
                   :class="getHeatmapClass(hour.activity)"
                   :title="`${hour.time}: ${hour.activity}% 活跃度`"
                   @mouseenter="showHeatmapTooltip(hour, $event)"
                   @mouseleave="hideHeatmapTooltip">
                <span class="cell-time">{{ hour.time }}</span>
                <span class="cell-activity">{{ hour.activity }}%</span>
                <div class="cell-glow"></div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 新增：高级分析面板 -->
        <div class="monitor-card advanced-analytics">
          <h3>🎯 高级数据分析</h3>
          <div class="analytics-grid">
            <!-- 用户增长趋势 -->
            <div class="analytics-item">
              <h4>📊 用户增长趋势</h4>
              <div class="growth-chart">
                <svg class="growth-svg" viewBox="0 0 300 150">
                  <defs>
                    <linearGradient id="growthGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                      <stop offset="0%" style="stop-color:#4CAF50;stop-opacity:0.8" />
                      <stop offset="100%" style="stop-color:#4CAF50;stop-opacity:0.1" />
                    </linearGradient>
                  </defs>
                  <path :d="getUserGrowthPath()" fill="url(#growthGradient)" stroke="none" />
                  <path :d="getUserGrowthLine()" fill="none" stroke="#4CAF50" stroke-width="3" />
                </svg>
                <div class="growth-stats">
                  <div class="growth-stat">
                    <span class="stat-number">+{{ userGrowth.thisMonth }}</span>
                    <span class="stat-text">本月新增</span>
                  </div>
                  <div class="growth-stat">
                    <span class="stat-number">{{ userGrowth.growthRate }}%</span>
                    <span class="stat-text">增长率</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 收益分析 -->
            <div class="analytics-item">
              <h4>💰 收益分析</h4>
              <div class="revenue-analysis">
                <div class="revenue-circle">
                  <svg class="revenue-svg" width="120" height="120">
                    <circle cx="60" cy="60" r="50" 
                            fill="none" stroke="#e2e8f0" stroke-width="8"/>
                    <circle cx="60" cy="60" r="50" 
                            fill="none" stroke="#667eea" stroke-width="8"
                            :stroke-dasharray="`${revenueProgress * 3.14} 314`"
                            transform="rotate(-90 60 60)"/>
                  </svg>
                  <div class="revenue-text">
                    <span class="revenue-amount">￥{{ monthlyRevenue }}</span>
                    <span class="revenue-label">月收益</span>
                  </div>
                </div>
                <div class="revenue-breakdown">
                  <div class="revenue-item">
                    <span class="revenue-dot course"></span>
                    <span>课程收益: 65%</span>
                  </div>
                  <div class="revenue-item">
                    <span class="revenue-dot vip"></span>
                    <span>VIP会员: 25%</span>
                  </div>
                  <div class="revenue-item">
                    <span class="revenue-dot other"></span>
                    <span>其他: 10%</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 学习效果评估 -->
            <div class="analytics-item">
              <h4>🎓 学习效果评估</h4>
              <div class="learning-assessment">
                <div class="assessment-radar">
                  <canvas ref="radarChart" width="200" height="200"></canvas>
                </div>
                <div class="assessment-metrics">
                  <div class="metric-row">
                    <span class="metric-name">平均成绩</span>
                    <div class="metric-bar">
                      <div class="metric-fill" style="width: 85%"></div>
                    </div>
                    <span class="metric-value">85分</span>
                  </div>
                  <div class="metric-row">
                    <span class="metric-name">完成率</span>
                    <div class="metric-bar">
                      <div class="metric-fill" style="width: 78%"></div>
                    </div>
                    <span class="metric-value">78%</span>
                  </div>
                  <div class="metric-row">
                    <span class="metric-name">满意度</span>
                    <div class="metric-bar">
                      <div class="metric-fill" style="width: 92%"></div>
                    </div>
                    <span class="metric-value">92%</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 实时警报 -->
            <div class="analytics-item alerts-panel">
              <h4>⚠️ 实时警报</h4>
              <div class="alerts-list">
                <div v-for="alert in realtimeAlerts" 
                     :key="alert.id" 
                     class="alert-item"
                     :class="alert.level">
                  <div class="alert-icon">{{ alert.icon }}</div>
                  <div class="alert-content">
                    <div class="alert-title">{{ alert.title }}</div>
                    <div class="alert-message">{{ alert.message }}</div>
                    <div class="alert-time">{{ alert.time }}</div>
                  </div>
                  <button @click="dismissAlert(alert.id)" class="alert-dismiss">×</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 智能功能效果分析 -->
      <div class="system-metrics">
        <div class="system-card">
          <h3>🤖 AI功能使用统计</h3>
          <div class="ai-usage-chart">
            <div v-for="feature in systemData.aiFeatures" 
                 :key="feature.name" 
                 class="feature-usage-item">
              <div class="feature-header">
                <span class="feature-icon">{{ feature.icon }}</span>
                <span class="feature-name">{{ feature.name }}</span>
                <span class="usage-percentage">{{ feature.usage }}%</span>
              </div>
              <div class="usage-bar">
                <div class="usage-fill" 
                     :style="{ width: feature.usage + '%' }"
                     :class="getUsageClass(feature.usage)"></div>
              </div>
              <div class="feature-stats">
                <span>日活跃: {{ feature.dailyActive }}</span>
                <span>满意度: {{ feature.satisfaction }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 课程受欢迎度排行 -->
        <div class="system-card">
          <h3>🏆 课程受欢迎度排行</h3>
          <div class="course-ranking">
            <div v-for="(course, index) in systemData.popularCourses" 
                 :key="course.id" 
                 class="ranking-item">
              <div class="rank-number" :class="getRankClass(index + 1)">
                {{ index + 1 }}
              </div>
              <div class="course-info">
                <h4>{{ course.name }}</h4>
                <p>{{ course.teacher }} • {{ course.students }}人学习</p>
              </div>
              <div class="course-metrics">
                <div class="metric">
                  <span class="metric-value">{{ course.rating }}</span>
                  <span class="metric-label">评分</span>
                </div>
                <div class="metric">
                  <span class="metric-value">{{ course.completion }}%</span>
                  <span class="metric-label">完成率</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 智能预测分析 -->
        <div class="system-card">
          <h3>🔮 智能预测分析</h3>
          <div class="prediction-panel">
            <div class="prediction-item">
              <div class="prediction-icon">📊</div>
              <div class="prediction-content">
                <h4>用户增长预测</h4>
                <p>预计下月新增用户: <strong>{{ systemData.predictions.userGrowth }}</strong></p>
                <div class="confidence">置信度: {{ systemData.predictions.userGrowthConfidence }}%</div>
              </div>
            </div>
            <div class="prediction-item">
              <div class="prediction-icon">💰</div>
              <div class="prediction-content">
                <h4>收益预测</h4>
                <p>预计月收益: <strong>￥{{ systemData.predictions.revenue }}</strong></p>
                <div class="confidence">置信度: {{ systemData.predictions.revenueConfidence }}%</div>
              </div>
            </div>
            <div class="prediction-item">
              <div class="prediction-icon">⚠️</div>
              <div class="prediction-content">
                <h4>风险预警</h4>
                <p>{{ systemData.predictions.riskWarning }}</p>
                <div class="risk-level" :class="systemData.predictions.riskLevel">
                  {{ systemData.predictions.riskLevel }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 教师工作效率对比 -->
        <div class="system-card">
          <h3>👩‍🏫 教师效率对比</h3>
          <div class="teacher-comparison">
            <div v-for="teacher in systemData.teacherEfficiency" 
                 :key="teacher.id" 
                 class="teacher-efficiency-item">
              <div class="teacher-info">
                <div class="teacher-avatar">{{ teacher.name.charAt(0) }}</div>
                <div class="teacher-details">
                  <h4>{{ teacher.name }}</h4>
                  <p>{{ teacher.subject }}</p>
                </div>
              </div>
              <div class="efficiency-metrics">
                <div class="metric-circle" :style="{ '--progress': teacher.efficiency + '%' }">
                  <span class="circle-text">{{ teacher.efficiency }}%</span>
                </div>
                <div class="efficiency-breakdown">
                  <div class="breakdown-item">
                    <span>课程质量</span>
                    <span class="breakdown-value">{{ teacher.courseQuality }}%</span>
                  </div>
                  <div class="breakdown-item">
                    <span>学生满意度</span>
                    <span class="breakdown-value">{{ teacher.satisfaction }}%</span>
                  </div>
                  <div class="breakdown-item">
                    <span>响应速度</span>
                    <span class="breakdown-value">{{ teacher.responseTime }}h</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getTeachersForAdmin, getTeacherDetailData, getAllStudentsForAdmin, getStudentDetailDataForAdmin, getSystemAnalytics } from '@/api/dashboard'

export default {
  name: 'DataDashboard',
  setup() {
    const activeView = ref('teacher')
    const selectedTeacherId = ref('')
    const selectedStudentId = ref('')
    
    const teachers = ref([])
    const students = ref([])
    const teacherData = ref(null)
    const studentDataAdmin = ref(null)
    
    // 新增：数据刷新和通知相关
    const isRefreshing = ref(false)
    const lastUpdateTime = ref(new Date().toLocaleTimeString())
    const showNotifications = ref(false)
    const unreadCount = ref(3)
    const selectedPeriod = ref('today')
    
    // 新增：高级分析数据
    const userGrowth = ref({
      thisMonth: 245,
      growthRate: 18.5,
      monthlyData: [120, 145, 180, 200, 235, 245]
    })
    const monthlyRevenue = ref(125800)
    const revenueProgress = ref(75)
    
    // 通知数据
    const notifications = ref([
      {
        id: 1,
        icon: '⚠️',
        title: 'CPU使用率过高',
        message: 'CPU使用率已达到85%，建议检查系统性能',
        time: new Date(Date.now() - 10 * 60000),
        read: false
      },
      {
        id: 2,
        icon: '📊',
        title: '用户增长异常',
        message: '今日新增用户较昨日增长120%，需要关注',
        time: new Date(Date.now() - 30 * 60000),
        read: false
      },
      {
        id: 3,
        icon: '🎯',
        title: '课程评分更新',
        message: 'JavaScript高级课程收到新的5星评价',
        time: new Date(Date.now() - 60 * 60000),
        read: true
      }
    ])
    
    // 时间段选择
    const timePeriods = ref([
      { value: 'today', label: '今日' },
      { value: 'week', label: '本周' },
      { value: 'month', label: '本月' }
    ])
    
    // 实时警报
    const realtimeAlerts = ref([
      {
        id: 1,
        level: 'warning',
        icon: '⚠️',
        title: '系统负载过高',
        message: 'CPU使用率已达到85%',
        time: '2分钟前'
      },
      {
        id: 2,
        level: 'info',
        icon: '📈',
        title: '用户活跃度上升',
        message: '在线用户数突破1300人',
        time: '5分钟前'
      }
    ])
    const systemData = ref({
      status: 'online',
      activeUsers: 1247,
      cpuUsage: 45,
      memoryUsage: 68,
      responseTime: 120,
      // 用户活跃度热力图数据
      activityHeatmap: [
        { time: '00-02', activity: 15 },
        { time: '02-04', activity: 8 },
        { time: '04-06', activity: 5 },
        { time: '06-08', activity: 25 },
        { time: '08-10', activity: 65 },
        { time: '10-12', activity: 85 },
        { time: '12-14', activity: 55 },
        { time: '14-16', activity: 90 },
        { time: '16-18', activity: 75 },
        { time: '18-20', activity: 60 },
        { time: '20-22', activity: 80 },
        { time: '22-24', activity: 40 }
      ],
      // AI功能使用统计
      aiFeatures: [
        {
          name: '智能路径规划',
          icon: '🗺️',
          usage: 87,
          dailyActive: 423,
          satisfaction: 92
        },
        {
          name: '情绪感知助手',
          icon: '😊',
          usage: 76,
          dailyActive: 356,
          satisfaction: 89
        },
        {
          name: '智能同伴学习',
          icon: '👥',
          usage: 68,
          dailyActive: 298,
          satisfaction: 85
        },
        {
          name: 'AI备课助手',
          icon: '📚',
          usage: 94,
          dailyActive: 187,
          satisfaction: 96
        }
      ],
      // 课程受欢迎度排行
      popularCourses: [
        {
          id: 1,
          name: 'Python编程基础',
          teacher: '张教授',
          students: 1245,
          rating: 4.8,
          completion: 92
        },
        {
          id: 2,
          name: '数据结构与算法',
          teacher: '李老师',
          students: 987,
          rating: 4.7,
          completion: 88
        },
        {
          id: 3,
          name: 'Web前端开发',
          teacher: '王老师',
          students: 823,
          rating: 4.6,
          completion: 85
        },
        {
          id: 4,
          name: '机器学习入门',
          teacher: '陈教授',
          students: 756,
          rating: 4.9,
          completion: 78
        }
      ],
      // 智能预测分析
      predictions: {
        userGrowth: '+285人',
        userGrowthConfidence: 87,
        revenue: '45,600',
        revenueConfidence: 82,
        riskWarning: '系统负载可能在下周达到峰值，建议提前扩容',
        riskLevel: 'medium'
      },
      // 教师效率对比
      teacherEfficiency: [
        {
          id: 1,
          name: '张教授',
          subject: 'Python编程',
          efficiency: 94,
          courseQuality: 96,
          satisfaction: 92,
          responseTime: 2.1
        },
        {
          id: 2,
          name: '李老师',
          subject: '数据结构',
          efficiency: 88,
          courseQuality: 89,
          satisfaction: 87,
          responseTime: 3.2
        },
        {
          id: 3,
          name: '王老师',
          subject: 'Web开发',
          efficiency: 91,
          courseQuality: 93,
          satisfaction: 89,
          responseTime: 2.8
        }
      ]
    })

    const loadTeachers = async () => {
      teachers.value = [
        { id: 1, realName: '张老师', subject: '计算机科学' },
        { id: 2, realName: '李老师', subject: '数据结构' }
      ]
    }

    const loadStudents = async () => {
      students.value = [
        { id: 17, realName: '李同学', username: 'student17' },
        { id: 18, realName: '张同学', username: 'student18' }
      ]
    }

    const loadTeacherData = async () => {
      if (!selectedTeacherId.value) return
      
      teacherData.value = {
        courseCount: 5,
        studentCount: 120,
        examCount: 15,
        gradedCount: 450,
        avgScore: 82,
        passRate: 88,
        efficiencyTrend: [
          { month: '1月', value: 75 },
          { month: '2月', value: 80 },
          { month: '3月', value: 85 },
          { month: '4月', value: 88 }
        ],
        gradeDistribution: [
          { level: '优秀', percentage: 25 },
          { level: '良好', percentage: 35 },
          { level: '中等', percentage: 28 },
          { level: '及格', percentage: 12 }
        ]
      }
    }

    const loadStudentDataAdmin = async () => {
      if (!selectedStudentId.value) return
      
      studentDataAdmin.value = {
        totalHours: 156,
        progress: 75,
        aiUsage: 89,
        featureUsage: [
          { name: '智能路径规划', usage: 85 },
          { name: '情绪感知助手', usage: 72 },
          { name: '智能同伴学习', usage: 68 }
        ],
        emotionData: [
          { type: 'focused', name: '专注', percentage: 45 },
          { type: 'happy', name: '开心', percentage: 30 },
          { type: 'calm', name: '平静', percentage: 25 }
        ]
      }
    }

    const getEmotionIcon = (type) => {
      const icons = { focused: '🎯', happy: '😊', calm: '😌' }
      return icons[type] || '😐'
    }

    const getHeatmapClass = (activity) => {
      if (activity >= 80) return 'very-high'
      if (activity >= 60) return 'high'
      if (activity >= 40) return 'medium'
      if (activity >= 20) return 'low'
      return 'very-low'
    }

    const getUsageClass = (usage) => {
      if (usage >= 90) return 'excellent'
      if (usage >= 70) return 'good'
      if (usage >= 50) return 'average'
      return 'poor'
    }

    const getRankClass = (rank) => {
      if (rank === 1) return 'gold'
      if (rank === 2) return 'silver'
      if (rank === 3) return 'bronze'
      return 'default'
    }

    // 新增：页面头部功能方法
    const refreshAllData = async () => {
      isRefreshing.value = true
      try {
        // 模拟数据刷新
        await new Promise(resolve => setTimeout(resolve, 2000))
        lastUpdateTime.value = new Date().toLocaleTimeString()
        // 这里可以调用实际的数据刷新API
      } finally {
        isRefreshing.value = false
      }
    }

    const exportData = () => {
      // 导出数据功能
      const data = {
        systemStatus: systemData.value,
        teachers: teachers.value,
        students: students.value,
        exportTime: new Date().toISOString()
      }
      
      const dataStr = JSON.stringify(data, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      
      const link = document.createElement('a')
      link.href = URL.createObjectURL(dataBlob)
      link.download = `dashboard-report-${new Date().toISOString().split('T')[0]}.json`
      link.click()
    }

    const toggleNotifications = () => {
      showNotifications.value = !showNotifications.value
    }

    const markAllAsRead = () => {
      notifications.value.forEach(n => n.read = true)
      unreadCount.value = 0
    }

    const formatTime = (time) => {
      const now = new Date()
      const diff = now - time
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(minutes / 60)
      
      if (hours > 0) return `${hours}小时前`
      if (minutes > 0) return `${minutes}分钟前`
      return '刚刚'
    }

    // 新增：系统状态相关方法
    const getSystemStatus = () => {
      const cpu = systemData.value.cpuUsage
      const memory = systemData.value.memoryUsage
      const response = systemData.value.responseTime
      
      if (cpu > 80 || memory > 90 || response > 500) return 'error'
      if (cpu > 60 || memory > 70 || response > 300) return 'warning'
      return 'success'
    }

    const getSystemStatusText = () => {
      const status = getSystemStatus()
      switch(status) {
        case 'error': return '系统异常'
        case 'warning': return '性能警告'
        case 'success': return '运行正常'
        default: return '未知状态'
      }
    }

    const getCpuStatusClass = () => {
      const cpu = systemData.value.cpuUsage
      if (cpu > 80) return 'error'
      if (cpu > 60) return 'warning'
      return 'success'
    }

    const getMemoryStatusClass = () => {
      const memory = systemData.value.memoryUsage
      if (memory > 90) return 'error'
      if (memory > 70) return 'warning'
      return 'success'
    }

    const getResponseStatusClass = () => {
      const response = systemData.value.responseTime
      if (response > 500) return 'error'
      if (response > 300) return 'warning'
      return 'success'
    }

    const getCpuColor = () => {
      const cpu = systemData.value.cpuUsage
      if (cpu > 80) return '#f56565'
      if (cpu > 60) return '#ed8936'
      return '#48bb78'
    }

    const getMemoryColor = () => {
      const memory = systemData.value.memoryUsage
      if (memory > 90) return '#f56565'
      if (memory > 70) return '#ed8936'
      return '#48bb78'
    }

    const getResponseBars = () => {
      const response = systemData.value.responseTime
      if (response < 100) return 5
      if (response < 200) return 4
      if (response < 300) return 3
      if (response < 500) return 2
      return 1
    }

    // 新增：热力图相关方法
    const showHeatmapTooltip = (hour, event) => {
      // 可以在这里显示自定义提示框
      console.log('显示热力图提示:', hour)
    }

    const hideHeatmapTooltip = () => {
      // 隐藏提示框
    }

    // 新增：用户增长图表方法
    const getUserGrowthPath = () => {
      const data = userGrowth.value.monthlyData
      const width = 300
      const height = 150
      const points = data.map((value, index) => {
        const x = (index / (data.length - 1)) * width
        const y = height - (value / Math.max(...data)) * height
        return `${x},${y}`
      })
      return `M0,${height} L${points.join(' L')} L${width},${height} Z`
    }

    const getUserGrowthLine = () => {
      const data = userGrowth.value.monthlyData
      const width = 300
      const height = 150
      const points = data.map((value, index) => {
        const x = (index / (data.length - 1)) * width
        const y = height - (value / Math.max(...data)) * height
        return `${x},${y}`
      })
      return `M${points.join(' L')}`
    }

    // 新增：警报相关方法
    const dismissAlert = (alertId) => {
      const index = realtimeAlerts.value.findIndex(alert => alert.id === alertId)
      if (index > -1) {
        realtimeAlerts.value.splice(index, 1)
      }
    }

    onMounted(() => {
      loadTeachers()
      loadStudents()
    })

    return {
      activeView,
      selectedTeacherId,
      selectedStudentId,
      teachers,
      students,
      teacherData,
      studentDataAdmin,
      systemData,
      // 新增的响应式数据
      isRefreshing,
      lastUpdateTime,
      showNotifications,
      unreadCount,
      selectedPeriod,
      notifications,
      timePeriods,
      realtimeAlerts,
      userGrowth,
      monthlyRevenue,
      revenueProgress,
      // 原有方法
      loadTeacherData,
      loadStudentDataAdmin,
      getEmotionIcon,
      getHeatmapClass,
      getUsageClass,
      getRankClass,
      // 新增的方法
      refreshAllData,
      exportData,
      toggleNotifications,
      markAllAsRead,
      formatTime,
      getSystemStatus,
      getSystemStatusText,
      getCpuStatusClass,
      getMemoryStatusClass,
      getResponseStatusClass,
      getCpuColor,
      getMemoryColor,
      getResponseBars,
      showHeatmapTooltip,
      hideHeatmapTooltip,
      getUserGrowthPath,
      getUserGrowthLine,
      dismissAlert
    }
  }
}
</script>

<style scoped>
.data-dashboard {
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

/* 页面头部样式优化 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.1);
  padding: 24px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
}

.header-content h1 {
  font-size: 2.5rem;
  margin: 0 0 8px 0;
  background: linear-gradient(45deg, #fff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-content p {
  margin: 0;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.refresh-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.refresh-indicator.active .refresh-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.last-update {
  font-size: 12px;
  opacity: 0.7;
}

.export-btn {
  padding: 10px 20px;
  background: linear-gradient(45deg, #4CAF50, #45a049);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.export-btn:hover {
  background: linear-gradient(45deg, #45a049, #4CAF50);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.notification-bell {
  position: relative;
  padding: 8px;
  cursor: pointer;
  font-size: 1.5rem;
  transition: transform 0.3s ease;
}

.notification-bell:hover {
  transform: scale(1.1);
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s infinite;
}

/* 通知面板样式 */
.notification-panel {
  position: fixed;
  top: 120px;
  right: 24px;
  width: 350px;
  max-height: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from { transform: translateX(100%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.notification-header h3 {
  margin: 0;
  color: #2d3748;
}

.mark-read-btn {
  padding: 4px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f7fafc;
  transition: background 0.2s ease;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item.unread {
  background: #ebf8ff;
  border-left: 4px solid #667eea;
}

.notification-icon {
  font-size: 1.2rem;
}

.notification-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #2d3748;
}

.notification-content p {
  margin: 0 0 4px 0;
  font-size: 13px;
  color: #4a5568;
}

.notification-time {
  font-size: 11px;
  color: #718096;
}

.view-tabs {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;
}

.tab-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn.active {
  background: white;
  color: #667eea;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
}

.teacher-select, .student-select {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  min-width: 200px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-icon {
  font-size: 3rem;
}

.data-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.data-value {
  font-weight: bold;
  color: #667eea;
}

.teacher-analysis {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.analysis-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.efficiency-chart {
  display: flex;
  align-items: end;
  gap: 16px;
  height: 200px;
  margin-top: 16px;
}

.chart-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.chart-bar {
  width: 20px;
  height: 120px;
  background: #e2e8f0;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.chart-fill {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: linear-gradient(0deg, #667eea, #764ba2);
}

.grade-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.grade-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.grade-fill {
  height: 100%;
  background: linear-gradient(90deg, #48bb78, #38a169);
}

.learning-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.metric-icon {
  font-size: 2.5rem;
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
}

.detailed-analysis {
  display: grid;
  gap: 24px;
}

.analysis-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.usage-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.usage-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.system-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.system-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.growth-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.growth-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.growth-fill {
  height: 100%;
  background: linear-gradient(90deg, #48bb78, #38a169);
}

/* 增强的系统监控样式 */
.system-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.real-time-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-dot.online {
  background: #48bb78;
}

.status-dot.offline {
  background: #f56565;
}

.monitoring-panel {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.monitor-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.monitor-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

/* 增强的实时监控面板 */
.enhanced-monitor {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.monitor-header h3 {
  margin: 0;
  color: white;
}

.monitor-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-indicator.success { background: #4CAF50; }
.status-indicator.warning { background: #FF9800; }
.status-indicator.error { background: #f44336; }

.status-text {
  font-size: 14px;
  opacity: 0.9;
}

.real-time-stats-enhanced {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.stat-item-enhanced {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.stat-item-enhanced:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.02);
}

.stat-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon-wrapper .stat-icon {
  font-size: 2rem;
  z-index: 2;
}

.stat-pulse {
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  opacity: 0.6;
  animation: stat-pulse 2s infinite;
}

.stat-pulse.success { background: #4CAF50; }
.stat-pulse.warning { background: #FF9800; }
.stat-pulse.error { background: #f44336; }

@keyframes stat-pulse {
  0% { transform: scale(0.8); opacity: 0.6; }
  50% { transform: scale(1.2); opacity: 0.3; }
  100% { transform: scale(0.8); opacity: 0.6; }
}

.stat-content {
  flex: 1;
}

.stat-value-large {
  font-size: 1.8rem;
  font-weight: bold;
  color: white;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
}

.stat-trend.positive {
  color: #4CAF50;
}

.trend-icon {
  font-size: 10px;
}

/* 进度环 */
.progress-ring {
  margin-top: 8px;
}

.progress-ring-svg {
  transform: rotate(-90deg);
}

.progress-ring-circle-bg {
  fill: none;
  stroke: rgba(255, 255, 255, 0.2);
  stroke-width: 4;
}

.progress-ring-circle {
  fill: none;
  stroke-width: 4;
  stroke-linecap: round;
  transition: stroke-dasharray 0.5s ease;
}

/* 内存条 */
.memory-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
  margin-top: 8px;
}

.memory-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.5s ease;
}

/* 响应时间指示器 */
.response-indicator {
  margin-top: 8px;
}

.response-bars {
  display: flex;
  gap: 2px;
  align-items: end;
}

.response-bar {
  width: 4px;
  height: 16px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  transition: all 0.3s ease;
}

.response-bar.active {
  background: #4CAF50;
  animation: bar-bounce 1s infinite;
}

@keyframes bar-bounce {
  0%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(1.3); }
}

.stat-chart {
  width: 60px;
  height: 40px;
}

/* 热力图增强样式 */
.heatmap-card {
  grid-column: span 2;
}

.card-header-enhanced {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header-enhanced h3 {
  margin: 0;
  color: #2d3748;
}

.time-filter {
  display: flex;
  gap: 8px;
}

.period-btn {
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.period-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.period-btn:hover {
  background: #f7fafc;
}

.period-btn.active:hover {
  background: #5a67d8;
}

.heatmap-enhanced {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.heatmap-legend {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 12px;
}

.legend-label {
  font-weight: 500;
  color: #4a5568;
}

.legend-scale {
  display: flex;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.very-low { background: #e2e8f0; }
.legend-color.low { background: #bee3f8; }
.legend-color.medium { background: #faf089; }
.legend-color.high { background: #c6f6d5; }
.legend-color.very-high { background: #f56565; }

.heatmap-grid-enhanced {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.heatmap-cell-enhanced {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.heatmap-cell-enhanced:hover {
  transform: scale(1.1) rotate(2deg);
  z-index: 10;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.heatmap-cell-enhanced.very-low {
  background: linear-gradient(135deg, #e2e8f0, #cbd5e0);
  color: #718096;
}

.heatmap-cell-enhanced.low {
  background: linear-gradient(135deg, #bee3f8, #90cdf4);
  color: #2c5282;
}

.heatmap-cell-enhanced.medium {
  background: linear-gradient(135deg, #faf089, #f6e05e);
  color: #744210;
}

.heatmap-cell-enhanced.high {
  background: linear-gradient(135deg, #c6f6d5, #9ae6b4);
  color: #22543d;
}

.heatmap-cell-enhanced.very-high {
  background: linear-gradient(135deg, #f56565, #fc8181);
  color: white;
}

.cell-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.3), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.heatmap-cell-enhanced:hover .cell-glow {
  opacity: 1;
}

.cell-time {
  font-weight: bold;
  margin-bottom: 2px;
}

.cell-activity {
  font-size: 9px;
  opacity: 0.8;
}

/* 高级分析面板样式 */
.advanced-analytics {
  grid-column: span 3;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.advanced-analytics h3 {
  color: white;
  margin: 0 0 24px 0;
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.analytics-item {
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.analytics-item h4 {
  margin: 0 0 16px 0;
  color: white;
  font-size: 14px;
}

/* 用户增长图表 */
.growth-chart {
  position: relative;
}

.growth-svg {
  width: 100%;
  height: 100px;
  margin-bottom: 12px;
}

.growth-stats {
  display: flex;
  justify-content: space-between;
}

.growth-stat {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #4CAF50;
}

.stat-text {
  font-size: 11px;
  opacity: 0.8;
}

/* 收益分析 */
.revenue-analysis {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.revenue-circle {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.revenue-text {
  position: absolute;
  text-align: center;
}

.revenue-amount {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: white;
}

.revenue-label {
  font-size: 11px;
  opacity: 0.8;
}

.revenue-breakdown {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.revenue-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.revenue-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.revenue-dot.course { background: #667eea; }
.revenue-dot.vip { background: #f093fb; }
.revenue-dot.other { background: #4CAF50; }

/* 学习效果评估 */
.learning-assessment {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assessment-radar {
  display: flex;
  justify-content: center;
}

.assessment-metrics {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.metric-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.metric-name {
  width: 60px;
  font-size: 11px;
}

.metric-bar {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #45a049);
  border-radius: 3px;
}

.metric-value {
  font-size: 11px;
  font-weight: bold;
  min-width: 30px;
  text-align: right;
}

/* 实时警报 */
.alerts-panel {
  grid-column: span 2;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  position: relative;
}

.alert-item.warning {
  background: rgba(255, 152, 0, 0.2);
  border-left: 4px solid #FF9800;
}

.alert-item.info {
  background: rgba(33, 150, 243, 0.2);
  border-left: 4px solid #2196F3;
}

.alert-item.error {
  background: rgba(244, 67, 54, 0.2);
  border-left: 4px solid #f44336;
}

.alert-icon {
  font-size: 1.2rem;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: bold;
  margin-bottom: 4px;
  font-size: 13px;
}

.alert-message {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 4px;
}

.alert-time {
  font-size: 10px;
  opacity: 0.7;
}

.alert-dismiss {
  position: absolute;
  top: 8px;
  right: 8px;
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.alert-dismiss:hover {
  opacity: 1;
}

.ai-usage-chart {
  margin-top: 16px;
}

.feature-usage-item {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.feature-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.feature-icon {
  font-size: 1.2rem;
}

.feature-name {
  flex: 1;
  font-weight: 500;
}

.usage-percentage {
  font-weight: bold;
  color: #667eea;
}

.usage-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.usage-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.usage-fill.excellent {
  background: linear-gradient(90deg, #48bb78, #38a169);
}

.usage-fill.good {
  background: linear-gradient(90deg, #4299e1, #3182ce);
}

.usage-fill.average {
  background: linear-gradient(90deg, #ed8936, #dd6b20);
}

.usage-fill.poor {
  background: linear-gradient(90deg, #f56565, #e53e3e);
}

.feature-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #718096;
}

.course-ranking {
  margin-top: 16px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  background: #f8fafc;
  transition: background 0.2s ease;
}

.ranking-item:hover {
  background: #e2e8f0;
}

.rank-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
}

.rank-number.gold {
  background: linear-gradient(135deg, #ffd700, #ffed4a);
  color: #744210;
}

.rank-number.silver {
  background: linear-gradient(135deg, #c0c0c0, #e2e8f0);
  color: #4a5568;
}

.rank-number.bronze {
  background: linear-gradient(135deg, #cd7f32, #d69e2e);
  color: white;
}

.rank-number.default {
  background: #718096;
}

.course-info h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
}

.course-info p {
  margin: 0;
  font-size: 14px;
  color: #718096;
}

.course-metrics {
  display: flex;
  gap: 24px;
}

.metric {
  text-align: center;
}

.metric-value {
  display: block;
  font-weight: bold;
  color: #667eea;
}

.metric-label {
  font-size: 12px;
  color: #718096;
}

.prediction-panel {
  display: grid;
  gap: 16px;
  margin-top: 16px;
}

.prediction-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.prediction-icon {
  font-size: 2rem;
}

.prediction-content h4 {
  margin: 0 0 8px 0;
  color: #2d3748;
}

.prediction-content p {
  margin: 0 0 8px 0;
  color: #4a5568;
}

.confidence {
  font-size: 12px;
  color: #48bb78;
}

.risk-level {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.risk-level.low {
  background: #c6f6d5;
  color: #22543d;
}

.risk-level.medium {
  background: #faf089;
  color: #744210;
}

.risk-level.high {
  background: #fed7d7;
  color: #742a2a;
}

.teacher-comparison {
  margin-top: 16px;
}

.teacher-efficiency-item {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 16px;
}

.teacher-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.teacher-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
}

.teacher-details h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
}

.teacher-details p {
  margin: 0;
  font-size: 14px;
  color: #718096;
}

.efficiency-metrics {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.metric-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: conic-gradient(#667eea 0deg, #667eea calc(var(--progress) * 3.6deg), #e2e8f0 calc(var(--progress) * 3.6deg));
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.metric-circle::before {
  content: '';
  position: absolute;
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
}

.circle-text {
  position: relative;
  z-index: 1;
  font-weight: bold;
  color: #667eea;
}

.efficiency-breakdown {
  flex: 1;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.breakdown-value {
  font-weight: bold;
  color: #667eea;
}

/* 添加通用的动画效果 */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.monitor-card {
  animation: fadeIn 0.6s ease;
}

.monitor-card:nth-child(1) { animation-delay: 0.1s; }
.monitor-card:nth-child(2) { animation-delay: 0.2s; }
.monitor-card:nth-child(3) { animation-delay: 0.3s; }

/* 响应式设计优化 */
@media (max-width: 1200px) {
  .monitoring-panel {
    grid-template-columns: 1fr 1fr;
  }
  
  .advanced-analytics {
    grid-column: span 2;
  }
  
  .heatmap-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .view-tabs {
    flex-direction: column;
    gap: 8px;
  }
  
  .teacher-analysis,
  .system-metrics {
    grid-template-columns: 1fr;
  }
  
  .monitoring-panel {
    grid-template-columns: 1fr;
  }
  
  .real-time-stats-enhanced {
    grid-template-columns: 1fr;
  }
  
  .heatmap-grid-enhanced {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .analytics-grid {
    grid-template-columns: 1fr;
  }
  
  .heatmap-card,
  .advanced-analytics {
    grid-column: span 1;
  }
  
  .alerts-panel {
    grid-column: span 1;
  }
  
  .teacher-efficiency-item {
    flex-direction: column;
    text-align: center;
  }
  
  .efficiency-metrics {
    justify-content: center;
  }
  
  .notification-panel {
    right: 12px;
    left: 12px;
    width: auto;
  }
  
  .time-filter {
    flex-wrap: wrap;
    gap: 4px;
  }
  
  .period-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
  
  .card-header-enhanced {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .revenue-analysis {
    align-items: stretch;
  }
  
  .growth-stats {
    justify-content: space-around;
  }
}

@media (max-width: 480px) {
  .data-dashboard {
    padding: 16px;
  }
  
  .header-content h1 {
    font-size: 2rem;
  }
  
  .monitor-card {
    padding: 16px;
  }
  
  .stat-item-enhanced {
    padding: 16px;
    gap: 12px;
  }
  
  .stat-value-large {
    font-size: 1.4rem;
  }
  
  .heatmap-grid-enhanced {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }
  
  .heatmap-cell-enhanced {
    font-size: 9px;
  }
  
  .analytics-item {
    padding: 16px;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .learning-overview {
    grid-template-columns: 1fr;
  }
}

/* 打印样式 */
@media print {
  .dashboard-header .header-actions,
  .notification-panel,
  .view-tabs {
    display: none;
  }
  
  .data-dashboard {
    background: white;
    color: black;
  }
  
  .monitor-card {
    background: white;
    border: 1px solid #ccc;
    box-shadow: none;
         page-break-inside: avoid;
   }
 }
</style> 