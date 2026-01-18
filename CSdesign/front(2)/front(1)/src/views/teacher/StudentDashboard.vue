<template>
  <div class="student-dashboard">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <h1>🎯 学生个人驾驶舱</h1>
      <p>深度分析学生学习数据，提供个性化洞察</p>
    </div>

    <!-- 学生选择器 -->
    <div class="student-selector">
      <select v-model="selectedStudentId" @change="loadStudentData" class="student-select">
        <option value="">选择学生</option>
        <option v-for="student in students" :key="student.id" :value="student.id">
          {{ student.realName }} ({{ student.username }})
        </option>
      </select>
    </div>

    <!-- 学生数据仪表板 -->
    <div v-if="selectedStudentId && studentData" class="dashboard-content">
      <!-- 基础信息卡片 -->
      <div class="info-cards">
        <div class="info-card">
          <div class="card-icon">📊</div>
          <div class="card-content">
            <h3>学习概况</h3>
            <div class="stat-item">
              <span>总学习时长</span>
              <span class="stat-value">{{ studentData.totalStudyTime }}小时</span>
            </div>
            <div class="stat-item">
              <span>平均成绩</span>
              <span class="stat-value">{{ studentData.averageScore }}分</span>
            </div>
          </div>
        </div>

        <div class="info-card">
          <div class="card-icon">🎯</div>
          <div class="card-content">
            <h3>智能学习</h3>
            <div class="stat-item">
              <span>学习路径完成</span>
              <span class="stat-value">{{ studentData.pathCompletion }}%</span>
            </div>
            <div class="stat-item">
              <span>情绪状态</span>
              <span class="stat-value">{{ studentData.emotionState }}</span>
            </div>
          </div>
        </div>

        <div class="info-card">
          <div class="card-icon">👥</div>
          <div class="card-content">
            <h3>协作学习</h3>
            <div class="stat-item">
              <span>学习伙伴</span>
              <span class="stat-value">{{ studentData.companionCount }}人</span>
            </div>
            <div class="stat-item">
              <span>小组参与</span>
              <span class="stat-value">{{ studentData.groupParticipation }}次</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细分析图表 -->
      <div class="analysis-charts">
        <div class="chart-card">
          <h3>📈 学习趋势分析</h3>
          <div class="trend-chart">
            <div v-for="(data, index) in studentData.learningTrend" 
                 :key="index" 
                 class="trend-item">
              <span class="trend-date">{{ data.date }}</span>
              <div class="trend-bar">
                <div class="trend-fill" :style="{ width: data.value + '%' }"></div>
              </div>
              <span class="trend-value">{{ data.value }}%</span>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>😊 情绪状态分析</h3>
          <div class="emotion-analysis">
            <div v-for="emotion in studentData.emotionAnalysis" 
                 :key="emotion.type" 
                 class="emotion-item">
              <span class="emotion-icon">{{ getEmotionIcon(emotion.type) }}</span>
              <span class="emotion-name">{{ emotion.name }}</span>
              <div class="emotion-bar">
                <div class="emotion-fill" :style="{ width: emotion.percentage + '%' }"></div>
              </div>
              <span class="emotion-percent">{{ emotion.percentage }}%</span>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>🎯 知识点掌握雷达图</h3>
          <div class="radar-chart">
            <div class="radar-container">
              <div v-for="(point, index) in studentData.knowledgeRadar" 
                   :key="point.name" 
                   class="radar-point"
                   :style="{ 
                     '--angle': (360 / studentData.knowledgeRadar.length * index) + 'deg',
                     '--score': point.score + '%'
                   }">
                <span class="point-label">{{ point.name }}</span>
                <span class="point-score">{{ point.score }}%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-card">
          <h3>⏰ 学习时间分布</h3>
          <div class="time-distribution">
            <div v-for="timeSlot in studentData.timeDistribution" 
                 :key="timeSlot.period" 
                 class="time-slot">
              <span class="time-period">{{ timeSlot.period }}</span>
              <div class="time-bar">
                <div class="time-fill" 
                     :style="{ height: timeSlot.hours * 10 + 'px' }"
                     :class="getTimeSlotClass(timeSlot.hours)"></div>
              </div>
              <span class="time-hours">{{ timeSlot.hours }}h</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 新增功能区域 -->
      <div class="advanced-analytics">
        <!-- 学习行为分析 -->
        <div class="analytics-card">
          <h3>🎭 学习行为分析</h3>
          <div class="behavior-grid">
            <div v-for="behavior in studentData.learningBehavior" 
                 :key="behavior.type" 
                 class="behavior-item">
              <div class="behavior-icon">{{ behavior.icon }}</div>
              <div class="behavior-content">
                <h4>{{ behavior.title }}</h4>
                <p>{{ behavior.description }}</p>
                <div class="behavior-score" :class="behavior.level">
                  {{ behavior.score }}分
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 作业完成情况 -->
        <div class="analytics-card">
          <h3>📚 作业完成详情</h3>
          <div class="homework-stats">
            <div class="homework-overview">
              <div class="stat-circle">
                <div class="circle-progress" :style="{ '--progress': studentData.homeworkCompletion + '%' }">
                  <span class="circle-text">{{ studentData.homeworkCompletion }}%</span>
                </div>
                <span class="circle-label">总完成率</span>
              </div>
              <div class="homework-details">
                <div class="detail-item">
                  <span class="detail-label">已完成</span>
                  <span class="detail-value completed">{{ studentData.homeworkStats.completed }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">进行中</span>
                  <span class="detail-value in-progress">{{ studentData.homeworkStats.inProgress }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">逾期</span>
                  <span class="detail-value overdue">{{ studentData.homeworkStats.overdue }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 考试成绩趋势 -->
        <div class="analytics-card exam-trend-card">
          <div class="trend-header">
            <h3>📊 考试成绩趋势</h3>
            <div class="trend-summary">
              <div class="summary-item">
                <span class="summary-label">最高分</span>
                <span class="summary-value high">{{ getMaxScore() }}分</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">平均分</span>
                <span class="summary-value average">{{ getAverageScore() }}分</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">提升幅度</span>
                <span class="summary-value" :class="getImprovementClass()">
                  {{ getScoreImprovement() }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="exam-trend-enhanced">
            <!-- 网格背景 -->
            <div class="trend-grid">
              <div v-for="i in 5" :key="i" class="grid-line" :style="{ bottom: (i * 20) + '%' }">
                <span class="grid-label">{{ i * 20 }}分</span>
              </div>
            </div>
            
            <!-- 趋势线 -->
            <svg class="trend-svg" viewBox="0 0 400 200" preserveAspectRatio="none">
              <!-- 渐变定义 -->
              <defs>
                <linearGradient id="trendGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" style="stop-color:#667eea;stop-opacity:0.3" />
                  <stop offset="100%" style="stop-color:#667eea;stop-opacity:0" />
                </linearGradient>
                <filter id="dropShadow">
                  <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.3"/>
                </filter>
              </defs>
              
              <!-- 面积填充 -->
              <path 
                :d="getAreaPath()" 
                fill="url(#trendGradient)" 
                stroke="none" />
              
              <!-- 趋势线 -->
              <path 
                :d="getLinePath()" 
                fill="none" 
                stroke="#667eea" 
                stroke-width="3" 
                stroke-linecap="round"
                filter="url(#dropShadow)" />
            </svg>
            
            <!-- 数据点 -->
            <div class="trend-points">
              <div v-for="(exam, index) in studentData.examTrend" 
                   :key="index" 
                   class="trend-point-enhanced"
                   :style="{ 
                     left: (index * (100 / (studentData.examTrend.length - 1))) + '%',
                     bottom: (exam.score * 2) + 'px'
                   }"
                   @mouseenter="showTooltip(index)"
                   @mouseleave="hideTooltip()">
                
                <!-- 脉冲动画圆圈 -->
                <div class="point-pulse" :class="getScoreClass(exam.score)"></div>
                
                <!-- 主要数据点 -->
                <div class="point-dot-enhanced" :class="getScoreClass(exam.score)">
                  <span class="point-score">{{ exam.score }}</span>
                </div>
                
                <!-- 增强的提示框 -->
                <div v-if="activeTooltip === index" class="enhanced-tooltip">
                  <div class="tooltip-arrow"></div>
                  <div class="tooltip-content">
                    <div class="tooltip-header">
                      <h4>{{ exam.name }}</h4>
                      <div class="tooltip-badge" :class="getScoreClass(exam.score)">
                        {{ getScoreLevel(exam.score) }}
                      </div>
                    </div>
                    <div class="tooltip-body">
                      <div class="tooltip-row">
                        <span class="tooltip-icon">📊</span>
                        <span class="tooltip-text">成绩: {{ exam.score }}分</span>
                      </div>
                      <div class="tooltip-row">
                        <span class="tooltip-icon">📅</span>
                        <span class="tooltip-text">{{ formatDate(exam.date) }}</span>
                      </div>
                      <div v-if="index > 0" class="tooltip-row">
                        <span class="tooltip-icon">📈</span>
                        <span class="tooltip-text">
                          比上次{{ getScoreChange(index) > 0 ? '提升' : '下降' }}
                          {{ Math.abs(getScoreChange(index)) }}分
                        </span>
                      </div>
                      <div class="tooltip-row">
                        <span class="tooltip-icon">🏆</span>
                        <span class="tooltip-text">班级排名: {{ exam.rank || 'N/A' }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 考试名称标签 -->
            <div class="trend-labels-enhanced">
              <div v-for="(exam, index) in studentData.examTrend" 
                   :key="index" 
                   class="trend-label-enhanced"
                   :style="{ left: (index * (100 / (studentData.examTrend.length - 1))) + '%' }">
                <div class="label-text">{{ exam.name }}</div>
                <div class="label-date">{{ formatShortDate(exam.date) }}</div>
              </div>
            </div>
          </div>
          
          <!-- 成绩分析 -->
          <div class="score-analysis">
            <div class="analysis-item">
              <div class="analysis-icon">📈</div>
              <div class="analysis-content">
                <h4>趋势分析</h4>
                <p>{{ getTrendAnalysis() }}</p>
              </div>
            </div>
            <div class="analysis-item">
              <div class="analysis-icon">🎯</div>
              <div class="analysis-content">
                <h4>目标建议</h4>
                <p>{{ getTargetSuggestion() }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 预警系统 -->
      <div v-if="studentData.warnings && studentData.warnings.length > 0" class="warning-section">
        <h3>⚠️ 学习预警</h3>
        <div class="warning-list">
          <div v-for="warning in studentData.warnings" 
               :key="warning.id" 
               class="warning-item"
               :class="warning.level">
            <div class="warning-icon">{{ getWarningIcon(warning.level) }}</div>
            <div class="warning-content">
              <h4>{{ warning.title }}</h4>
              <p>{{ warning.message }}</p>
              <div class="warning-action">
                <button @click="handleWarning(warning)" class="action-btn">
                  {{ warning.actionText }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 建议和洞察 -->
      <div class="insights-section">
        <h3>💡 个性化建议</h3>
        <div class="insights-grid">
          <div v-for="insight in studentData.insights" 
               :key="insight.id" 
               class="insight-card"
               :class="insight.priority">
            <div class="insight-header">
              <span class="insight-icon">{{ insight.icon }}</span>
              <h4>{{ insight.title }}</h4>
            </div>
            <p>{{ insight.content }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!selectedStudentId" class="empty-state">
      <div class="empty-icon">🎯</div>
      <h3>选择学生开始分析</h3>
      <p>选择一个学生查看其详细的学习数据分析</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getStudentsForTeacher, getStudentDetailData } from '@/api/dashboard'

export default {
  name: 'StudentDashboard',
  setup() {
    const selectedStudentId = ref('')
    const students = ref([])
    const studentData = ref(null)
    const activeTooltip = ref(null)

    const loadStudents = async () => {
      try {
        // 模拟学生数据
        students.value = [
          { id: 17, realName: '李同学', username: 'student17' },
          { id: 18, realName: '张同学', username: 'student18' },
          { id: 19, realName: '王同学', username: 'student19' }
        ]
      } catch (error) {
        console.error('加载学生列表失败:', error)
      }
    }

    const loadStudentData = async () => {
      if (!selectedStudentId.value) return
      
      try {
        // 模拟详细学生数据
        studentData.value = {
          totalStudyTime: 156,
          averageScore: 85,
          pathCompletion: 75,
          emotionState: '专注',
          companionCount: 3,
          groupParticipation: 8,
          learningTrend: [
            { date: '本周', value: 85 },
            { date: '上周', value: 78 },
            { date: '上上周', value: 82 },
            { date: '三周前', value: 76 }
          ],
          emotionAnalysis: [
            { type: 'focused', name: '专注', percentage: 45 },
            { type: 'happy', name: '开心', percentage: 30 },
            { type: 'calm', name: '平静', percentage: 20 },
            { type: 'confused', name: '困惑', percentage: 5 }
          ],
          // 新增：知识点掌握雷达图
          knowledgeRadar: [
            { name: '数据结构', score: 85 },
            { name: '算法设计', score: 78 },
            { name: '网络原理', score: 92 },
            { name: '操作系统', score: 88 },
            { name: '数据库', score: 75 },
            { name: 'Web开发', score: 90 }
          ],
          // 新增：学习时间分布
          timeDistribution: [
            { period: '8-10点', hours: 2.5 },
            { period: '10-12点', hours: 3.2 },
            { period: '14-16点', hours: 4.1 },
            { period: '16-18点', hours: 3.8 },
            { period: '19-21点', hours: 5.2 },
            { period: '21-23点', hours: 2.8 }
          ],
          // 新增：学习行为分析
          learningBehavior: [
            {
              type: 'concentration',
              icon: '🎯',
              title: '专注度',
              description: '学习过程中注意力集中程度',
              score: 88,
              level: 'excellent'
            },
            {
              type: 'persistence',
              icon: '💪',
              title: '坚持度',
              description: '面对困难时的持续学习能力',
              score: 82,
              level: 'good'
            },
            {
              type: 'interaction',
              icon: '💬',
              title: '互动性',
              description: '课堂参与和同伴交流积极性',
              score: 75,
              level: 'average'
            },
            {
              type: 'creativity',
              icon: '💡',
              title: '创新性',
              description: '思考问题和解决方案的创新程度',
              score: 90,
              level: 'excellent'
            }
          ],
          // 新增：作业完成情况
          homeworkCompletion: 92,
          homeworkStats: {
            completed: 28,
            inProgress: 3,
            overdue: 1
          },
          // 新增：考试成绩趋势
          examTrend: [
            { name: '期初', score: 78, date: '2024-02-15', rank: 15 },
            { name: '月考1', score: 82, date: '2024-03-20', rank: 12 },
            { name: '月考2', score: 85, date: '2024-04-18', rank: 8 },
            { name: '期中', score: 88, date: '2024-05-10', rank: 6 },
            { name: '月考3', score: 90, date: '2024-06-15', rank: 4 }
          ],
          // 新增：预警信息
          warnings: [
            {
              id: 1,
              level: 'warning',
              title: '作业提交延迟',
              message: '最近2次作业提交有延迟，建议关注时间管理',
              actionText: '查看详情'
            },
            {
              id: 2,
              level: 'info',
              title: '学习建议',
              message: '数据结构部分表现优秀，可以尝试更高难度的挑战',
              actionText: '推荐课程'
            }
          ],
          insights: [
            {
              id: 1,
              icon: '🎯',
              title: '学习专注度高',
              content: '该学生在智能学习系统中表现出较高的专注度，建议继续保持。',
              priority: 'high'
            },
            {
              id: 2,
              icon: '📚',
              title: '需要加强练习',
              content: '在数据结构部分错题较多，建议增加相关练习。',
              priority: 'medium'
            }
          ]
        }
      } catch (error) {
        console.error('加载学生数据失败:', error)
      }
    }

    const getEmotionIcon = (emotionType) => {
      const icons = {
        focused: '🎯',
        happy: '😊',
        calm: '😌',
        confused: '😕'
      }
      return icons[emotionType] || '😐'
    }

    const getTimeSlotClass = (hours) => {
      if (hours >= 4) return 'high-activity'
      if (hours >= 2) return 'medium-activity'
      return 'low-activity'
    }

    const getScoreClass = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'average'
      return 'poor'
    }

    const getWarningIcon = (level) => {
      const icons = {
        error: '🚨',
        warning: '⚠️',
        info: 'ℹ️'
      }
      return icons[level] || 'ℹ️'
    }

    const handleWarning = (warning) => {
      console.log('处理预警:', warning)
      // 这里可以实现具体的预警处理逻辑
      // 比如跳转到详情页面、发送通知等
    }

    // 新增：考试成绩趋势图相关方法
    const showTooltip = (index) => {
      activeTooltip.value = index
    }

    const hideTooltip = () => {
      activeTooltip.value = null
    }

    const getMaxScore = () => {
      if (!studentData.value?.examTrend) return 0
      return Math.max(...studentData.value.examTrend.map(exam => exam.score))
    }

    const getAverageScore = () => {
      if (!studentData.value?.examTrend) return 0
      const total = studentData.value.examTrend.reduce((sum, exam) => sum + exam.score, 0)
      return Math.round(total / studentData.value.examTrend.length)
    }

    const getScoreImprovement = () => {
      if (!studentData.value?.examTrend || studentData.value.examTrend.length < 2) return '暂无数据'
      const first = studentData.value.examTrend[0].score
      const last = studentData.value.examTrend[studentData.value.examTrend.length - 1].score
      const improvement = last - first
      return improvement > 0 ? `+${improvement}分` : `${improvement}分`
    }

    const getImprovementClass = () => {
      if (!studentData.value?.examTrend || studentData.value.examTrend.length < 2) return 'neutral'
      const first = studentData.value.examTrend[0].score
      const last = studentData.value.examTrend[studentData.value.examTrend.length - 1].score
      const improvement = last - first
      return improvement > 0 ? 'positive' : improvement < 0 ? 'negative' : 'neutral'
    }

    const getScoreChange = (index) => {
      if (!studentData.value?.examTrend || index === 0) return 0
      return studentData.value.examTrend[index].score - studentData.value.examTrend[index - 1].score
    }

    const getScoreLevel = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '中等'
      if (score >= 60) return '及格'
      return '待提高'
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      })
    }

    const formatShortDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', { 
        month: 'short', 
        day: 'numeric' 
      })
    }

    const getLinePath = () => {
      if (!studentData.value?.examTrend) return ''
      const points = studentData.value.examTrend.map((exam, index) => {
        const x = (index / (studentData.value.examTrend.length - 1)) * 400
        const y = 200 - (exam.score * 2)
        return `${x},${y}`
      })
      return `M${points.join('L')}`
    }

    const getAreaPath = () => {
      if (!studentData.value?.examTrend) return ''
      const points = studentData.value.examTrend.map((exam, index) => {
        const x = (index / (studentData.value.examTrend.length - 1)) * 400
        const y = 200 - (exam.score * 2)
        return `${x},${y}`
      })
      const firstX = 0
      const lastX = 400
      return `M${firstX},200 L${points.join('L')} L${lastX},200 Z`
    }

    const getTrendAnalysis = () => {
      if (!studentData.value?.examTrend || studentData.value.examTrend.length < 2) {
        return '数据不足，无法分析趋势'
      }
      
      const scores = studentData.value.examTrend.map(exam => exam.score)
      const improvements = []
      
      for (let i = 1; i < scores.length; i++) {
        improvements.push(scores[i] - scores[i-1])
      }
      
      const positiveCount = improvements.filter(imp => imp > 0).length
      const totalCount = improvements.length
      
      if (positiveCount / totalCount >= 0.7) {
        return '成绩呈稳定上升趋势，学习状态良好，继续保持！'
      } else if (positiveCount / totalCount >= 0.4) {
        return '成绩有所波动，整体趋势尚可，需要保持稳定性。'
      } else {
        return '成绩波动较大，建议重点关注学习方法和复习策略。'
      }
    }

    const getTargetSuggestion = () => {
      if (!studentData.value?.examTrend) return '暂无建议'
      
      const lastScore = studentData.value.examTrend[studentData.value.examTrend.length - 1].score
      
      if (lastScore >= 90) {
        return '表现优异！建议挑战更高难度的题目，巩固优势。'
      } else if (lastScore >= 80) {
        return '成绩良好，建议针对薄弱知识点进行专项训练。'
      } else if (lastScore >= 70) {
        return '基础扎实，建议增加练习量，提高解题速度。'
      } else {
        return '需要加强基础知识学习，建议制定详细的复习计划。'
      }
    }

    onMounted(() => {
      loadStudents()
    })

    return {
      selectedStudentId,
      students,
      studentData,
      activeTooltip,
      loadStudentData,
      getEmotionIcon,
      getTimeSlotClass,
      getScoreClass,
      getWarningIcon,
      handleWarning,
      // 新增：考试成绩趋势相关方法
      showTooltip,
      hideTooltip,
      getMaxScore,
      getAverageScore,
      getScoreImprovement,
      getImprovementClass,
      getScoreChange,
      getScoreLevel,
      formatDate,
      formatShortDate,
      getLinePath,
      getAreaPath,
      getTrendAnalysis,
      getTargetSuggestion
    }
  }
}
</script>

<style scoped>
.student-dashboard {
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 32px;
}

.dashboard-header h1 {
  font-size: 2.5rem;
  color: #2d3748;
  margin-bottom: 8px;
}

.student-selector {
  margin-bottom: 32px;
  text-align: center;
}

.student-select {
  padding: 12px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 16px;
  min-width: 300px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-icon {
  font-size: 3rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-value {
  font-weight: bold;
  color: #667eea;
}

.analysis-charts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.trend-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.trend-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.emotion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.emotion-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.emotion-fill {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #feca57);
}

.insights-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.insights-grid {
  display: grid;
  gap: 16px;
  margin-top: 16px;
}

.insight-card {
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.insight-card.high {
  border-left-color: #e53e3e;
  background: #fed7d7;
}

.insight-card.medium {
  border-left-color: #d69e2e;
  background: #faf089;
}

.insight-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.empty-state {
  text-align: center;
  padding: 64px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

/* 新增功能样式 */
.radar-chart {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.radar-container {
  position: relative;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 2px solid #e2e8f0;
}

.radar-point {
  position: absolute;
  width: 100%;
  height: 100%;
  transform: rotate(var(--angle));
}

.point-label {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  white-space: nowrap;
}

.point-score {
  position: absolute;
  top: calc(100% - var(--score));
  left: 50%;
  transform: translateX(-50%);
  background: #667eea;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
}

.time-distribution {
  display: flex;
  align-items: end;
  gap: 16px;
  height: 150px;
  padding: 16px 0;
}

.time-slot {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.time-bar {
  width: 20px;
  height: 80px;
  background: #f7fafc;
  border-radius: 4px;
  display: flex;
  align-items: end;
  overflow: hidden;
}

.time-fill {
  width: 100%;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
}

.time-fill.high-activity {
  background: linear-gradient(0deg, #f56565, #fc8181);
}

.time-fill.medium-activity {
  background: linear-gradient(0deg, #ed8936, #f6ad55);
}

.time-fill.low-activity {
  background: linear-gradient(0deg, #48bb78, #68d391);
}

.advanced-analytics {
  display: grid;
  gap: 24px;
  margin-bottom: 32px;
}

.analytics-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.behavior-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.behavior-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border-left: 4px solid #667eea;
}

.behavior-icon {
  font-size: 2rem;
}

.behavior-content h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
}

.behavior-content p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #718096;
}

.behavior-score {
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 14px;
}

.behavior-score.excellent {
  background: #c6f6d5;
  color: #22543d;
}

.behavior-score.good {
  background: #bee3f8;
  color: #2c5282;
}

.behavior-score.average {
  background: #faf089;
  color: #744210;
}

.homework-overview {
  display: flex;
  align-items: center;
  gap: 32px;
}

.stat-circle {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.circle-progress {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(#667eea 0deg, #667eea calc(var(--progress) * 3.6deg), #e2e8f0 calc(var(--progress) * 3.6deg));
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.circle-progress::before {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 50%;
}

.circle-text {
  position: relative;
  z-index: 1;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.homework-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.detail-value {
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 12px;
}

.detail-value.completed {
  background: #c6f6d5;
  color: #22543d;
}

.detail-value.in-progress {
  background: #faf089;
  color: #744210;
}

.detail-value.overdue {
  background: #fed7d7;
  color: #742a2a;
}

/* 考试成绩趋势样式 - 增强版 */
.exam-trend-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  overflow: hidden;
}

.trend-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 25px;
  flex-wrap: wrap;
  gap: 15px;
}

.trend-header h3 {
  color: white;
  margin: 0;
  font-size: 20px;
}

.trend-summary {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
}

.summary-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.summary-value {
  font-size: 16px;
  font-weight: bold;
}

.summary-value.high { color: #4CAF50; }
.summary-value.average { color: #FFC107; }
.summary-value.positive { color: #4CAF50; }
.summary-value.negative { color: #FF5722; }
.summary-value.neutral { color: #9E9E9E; }

.exam-trend-enhanced {
  position: relative;
  height: 250px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin: 20px 0;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.trend-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.grid-line {
  position: absolute;
  width: 100%;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  left: 0;
}

.grid-label {
  position: absolute;
  left: 10px;
  top: -8px;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
}

.trend-svg {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 2;
  padding: 0 30px;
  box-sizing: border-box;
}

.trend-points {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 3;
  padding: 0 30px;
  box-sizing: border-box;
}

.trend-point-enhanced {
  position: absolute;
  transform: translateX(-50%);
  cursor: pointer;
}

.point-pulse {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 2s infinite;
  opacity: 0.6;
}

.point-pulse.excellent { background: #4CAF50; }
.point-pulse.good { background: #2196F3; }
.point-pulse.average { background: #FF9800; }
.point-pulse.poor { background: #f44336; }

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(0.8); opacity: 0.6; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.3; }
  100% { transform: translate(-50%, -50%) scale(0.8); opacity: 0.6; }
}

.point-dot-enhanced {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  position: relative;
  z-index: 4;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.point-dot-enhanced:hover {
  transform: scale(1.2);
  box-shadow: 0 6px 20px rgba(0,0,0,0.4);
}

.point-dot-enhanced.excellent { background: #4CAF50; }
.point-dot-enhanced.good { background: #2196F3; }
.point-dot-enhanced.average { background: #FF9800; }
.point-dot-enhanced.poor { background: #f44336; }

.point-score {
  font-size: 8px;
  font-weight: bold;
  color: white;
}

.enhanced-tooltip {
  position: absolute;
  bottom: 35px;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.2);
  z-index: 10;
  min-width: 200px;
  animation: fadeInUp 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translate(-50%, 10px);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}

.tooltip-arrow {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid white;
}

.tooltip-content {
  padding: 16px;
  color: #333;
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.tooltip-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.tooltip-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: bold;
  color: white;
}

.tooltip-badge.excellent { background: #4CAF50; }
.tooltip-badge.good { background: #2196F3; }
.tooltip-badge.average { background: #FF9800; }
.tooltip-badge.poor { background: #f44336; }

.tooltip-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tooltip-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.tooltip-icon {
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.tooltip-text {
  color: #666;
}

.trend-labels-enhanced {
  position: absolute;
  bottom: 10px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  z-index: 3;
  padding: 0 30px;
  box-sizing: border-box;
}

.trend-label-enhanced {
  position: absolute;
  transform: translateX(-50%);
  text-align: center;
}

.label-text {
  font-size: 11px;
  font-weight: bold;
  color: white;
  margin-bottom: 2px;
}

.label-date {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.7);
}

.score-analysis {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.analysis-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: rgba(255, 255, 255, 0.1);
  padding: 16px;
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.analysis-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.analysis-content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: white;
}

.analysis-content p {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
}

.warning-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 32px;
}

.warning-list {
  display: grid;
  gap: 16px;
  margin-top: 16px;
}

.warning-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid;
}

.warning-item.error {
  background: #fed7d7;
  border-left-color: #e53e3e;
}

.warning-item.warning {
  background: #faf089;
  border-left-color: #d69e2e;
}

.warning-item.info {
  background: #bee3f8;
  border-left-color: #3182ce;
}

.warning-icon {
  font-size: 1.5rem;
}

.warning-content {
  flex: 1;
}

.warning-content h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
}

.warning-content p {
  margin: 0 0 8px 0;
  color: #718096;
}

.action-btn {
  padding: 6px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.action-btn:hover {
  background: #5a67d8;
}

@media (max-width: 768px) {
  .analysis-charts {
    grid-template-columns: 1fr;
  }
  
  .info-cards {
    grid-template-columns: 1fr;
  }
  
  .behavior-grid {
    grid-template-columns: 1fr;
  }
  
  .homework-overview {
    flex-direction: column;
    text-align: center;
  }
  
  /* 趋势图移动端优化 */
  .trend-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .trend-summary {
    justify-content: space-around;
  }
  
  .score-analysis {
    grid-template-columns: 1fr;
  }
  
  .enhanced-tooltip {
    min-width: 160px;
    font-size: 11px;
  }
  
  .exam-trend-enhanced {
    height: 200px;
  }
}
</style> 