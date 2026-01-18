<template>
  <div class="student-analysis">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/balance.png" alt="学情分析" class="header-icon">
          <h1>学情数据分析</h1>
        </div>
        <p class="header-subtitle">智能分析学生学习情况，自动检测答案并提供个性化建议</p>
      </div>
      <div class="header-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>

    <div class="analysis-tabs">
      <div class="tab-header">
        <button 
          v-for="tab in tabs" 
          :key="tab.key"
          @click="activeTab = tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
        >
          <img :src="tab.iconSrc" :alt="tab.name" class="tab-icon">
          {{ tab.name }}
        </button>
      </div>

      <!-- 整体学情概览 -->
      <div v-if="activeTab === 'overview'" class="tab-content">
        <div class="overview-grid">
          <div class="analysis-card">
            <div class="card-header">
              <div class="header-left">
                <img src="@/assets/balance.png" alt="班级表现" class="card-icon">
                <h3>班级整体表现</h3>
              </div>
            </div>
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-icon">
                  <img src="@/assets/balance.png" alt="平均分" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ classStats.averageScore }}</div>
                  <div class="stat-label">平均分</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <img src="@/assets/author.png" alt="及格率" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ classStats.passRate }}%</div>
                  <div class="stat-label">及格率</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <img src="@/assets/category.png" alt="优秀率" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ classStats.excellentRate }}%</div>
                  <div class="stat-label">优秀率</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <img src="@/assets/time.png" alt="参与率" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ classStats.participationRate }}%</div>
                  <div class="stat-label">参与率</div>
                </div>
              </div>
            </div>
          </div>

          <div class="analysis-card">
            <div class="card-header">
              <div class="header-left">
                <img src="@/assets/category.png" alt="成绩分布" class="card-icon">
                <h3>成绩分布</h3>
              </div>
            </div>
            <div class="score-distribution">
              <div v-for="range in scoreRanges" :key="range.label" class="score-range">
                <div class="range-info">
                  <span class="range-label">{{ range.label }}</span>
                  <span class="range-count">{{ range.count }}人</span>
                </div>
                <div class="range-bar">
                  <div class="range-fill" :style="{ width: range.percentage + '%' }"></div>
                </div>
                <div class="range-percentage">{{ range.percentage }}%</div>
              </div>
            </div>
          </div>

          <div class="analysis-card">
            <div class="card-header">
              <div class="header-left">
                <img src="@/assets/balance.png" alt="知识点掌握" class="card-icon">
                <h3>知识点掌握情况</h3>
              </div>
            </div>
            <div class="knowledge-mastery">
              <div v-for="point in knowledgeMastery" :key="point.name" class="mastery-item">
                <div class="mastery-header">
                  <img src="@/assets/category.png" alt="知识点" class="mastery-icon">
                  <span class="mastery-name">{{ point.name }}</span>
                  <span class="mastery-rate">{{ point.masteryRate }}%</span>
                </div>
                <div class="mastery-bar">
                  <div class="mastery-fill" :style="{ width: point.masteryRate + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学生个人分析 -->
      <div v-if="activeTab === 'individual'" class="tab-content">
        <div class="student-selector">
          <div class="form-item">
            <label>
              <img src="@/assets/author.png" alt="选择学生" class="label-icon">
              选择学生
            </label>
            <select v-model="selectedStudent" @change="loadStudentAnalysis">
              <option value="">请选择学生</option>
              <option v-for="student in students" :key="student.id" :value="student.id">
                {{ student.name }} ({{ student.studentId }})
              </option>
            </select>
          </div>
        </div>

        <div v-if="selectedStudent && studentAnalysis" class="student-analysis-content">
          <div class="student-info">
            <div class="info-header">
              <img src="@/assets/author.png" alt="学生" class="student-icon">
              <h3>{{ studentAnalysis.name }} 的学习分析</h3>
            </div>
            <div class="student-stats">
              <div class="stat-card">
                <div class="stat-icon">
                  <img src="@/assets/balance.png" alt="平均分" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ studentAnalysis.averageScore }}</div>
                  <div class="stat-title">平均分</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <img src="@/assets/category.png" alt="排名" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ studentAnalysis.rank }}</div>
                  <div class="stat-title">班级排名</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <img src="@/assets/time.png" alt="完成率" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ studentAnalysis.completionRate }}%</div>
                  <div class="stat-title">作业完成率</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <img src="@/assets/author.png" alt="出勤率" class="icon-img">
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ studentAnalysis.attendanceRate }}%</div>
                  <div class="stat-title">出勤率</div>
                </div>
              </div>
            </div>
          </div>

          <div class="analysis-sections">
            <div class="analysis-card">
              <div class="card-header">
                <div class="header-left">
                  <img src="@/assets/balance.png" alt="学习趋势" class="card-icon">
                  <h4>学习趋势分析</h4>
                </div>
                <div class="trend-summary">
                  <span class="trend-indicator" :class="getTrendClass()">
                    {{ getTrendText() }}
                  </span>
                </div>
              </div>
              
              <!-- 趋势统计 -->
              <div class="trend-stats">
                <div class="trend-stat-item">
                  <div class="stat-label">最高分</div>
                  <div class="stat-value high">{{ getMaxScore() }}</div>
                </div>
                <div class="trend-stat-item">
                  <div class="stat-label">最低分</div>
                  <div class="stat-value low">{{ getMinScore() }}</div>
                </div>
                <div class="trend-stat-item">
                  <div class="stat-label">平均分</div>
                  <div class="stat-value avg">{{ getAvgScore() }}</div>
                </div>
                <div class="trend-stat-item">
                  <div class="stat-label">变化幅度</div>
                  <div class="stat-value change" :class="getChangeClass()">
                    {{ getScoreChange() }}
                  </div>
                </div>
              </div>
              
              <!-- 趋势图表 -->
              <div class="trend-chart-container">
                <div class="chart-grid">
                  <!-- Y轴刻度 -->
                  <div class="y-axis">
                    <div class="y-tick" v-for="tick in getYAxisTicks()" :key="tick">{{ tick }}</div>
                  </div>
                  
                  <!-- 主图表区域 -->
                  <div class="chart-area">
                    <!-- 背景网格线 -->
                    <div class="grid-lines">
                      <div class="grid-line horizontal" v-for="i in 5" :key="'h' + i"></div>
                      <div class="grid-line vertical" v-for="i in 7" :key="'v' + i"></div>
                    </div>
                    
                    <!-- 趋势线和点 -->
                    <svg class="trend-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
                      <!-- 渐变定义 -->
                      <defs>
                        <linearGradient id="trendGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                          <stop offset="0%" style="stop-color:#667eea;stop-opacity:0.3" />
                          <stop offset="100%" style="stop-color:#667eea;stop-opacity:0.1" />
                        </linearGradient>
                        <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                          <stop offset="0%" style="stop-color:#667eea" />
                          <stop offset="50%" style="stop-color:#764ba2" />
                          <stop offset="100%" style="stop-color:#667eea" />
                        </linearGradient>
                      </defs>
                      
                      <!-- 面积填充 -->
                      <path :d="getAreaPath()" fill="url(#trendGradient)" />
                      
                      <!-- 趋势线 -->
                      <path :d="getLinePath()" fill="none" stroke="url(#lineGradient)" stroke-width="0.8" />
                    </svg>
                    
                    <!-- 数据点 -->
                    <div class="trend-points">
                      <div 
                        v-for="(score, index) in studentAnalysis.trend" 
                        :key="index" 
                        class="trend-point"
                        :style="getPointStyle(score, index)"
                        @mouseover="showTooltip(score, index, $event)"
                        @mouseleave="hideTooltip"
                      >
                        <div class="point-dot"></div>
                        <div class="point-value">{{ score }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- X轴日期标签 -->
                <div class="x-axis">
                  <div 
                    v-for="(date, index) in getTrendDates()" 
                    :key="index" 
                    class="x-tick"
                  >
                    {{ date }}
                  </div>
                </div>
              </div>
              
              <!-- 趋势分析文字 -->
              <div class="trend-analysis">
                <div class="analysis-text">
                  <img src="@/assets/category.png" alt="分析" class="analysis-icon">
                  <span>{{ getTrendAnalysis() }}</span>
                </div>
              </div>
            </div>

            <div class="analysis-card">
              <div class="card-header">
                <div class="header-left">
                  <img src="@/assets/category.png" alt="薄弱知识点" class="card-icon">
                  <h4>薄弱知识点</h4>
                </div>
              </div>
              <div class="weak-points">
                <div v-for="point in studentAnalysis.weakPoints" :key="point.name" class="weak-point">
                  <div class="point-header">
                    <img src="@/assets/balance.png" alt="知识点" class="point-icon">
                    <div class="point-info">
                      <span class="point-name">{{ point.name }}</span>
                      <span class="point-score">{{ point.score }}分</span>
                    </div>
                  </div>
                  <div class="point-suggestion">{{ point.suggestion }}</div>
                </div>
              </div>
            </div>

            <div class="analysis-card">
              <div class="card-header">
                <div class="header-left">
                  <img src="@/assets/add.png" alt="AI建议" class="card-icon">
                  <h4>AI学习建议</h4>
                </div>
              </div>
              <div class="ai-suggestions">
                <div v-for="suggestion in studentAnalysis.suggestions" :key="suggestion.id" class="suggestion-item">
                  <div class="suggestion-header">
                    <img src="@/assets/category.png" alt="建议类型" class="suggestion-icon">
                    <span class="suggestion-type">{{ suggestion.type }}</span>
                    <span class="suggestion-priority" :class="getPriorityClass(suggestion.priority)">
                      {{ suggestion.priority }}
                    </span>
                  </div>
                  <div class="suggestion-content">{{ suggestion.content }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 答案检测分析 -->
      <div v-if="activeTab === 'detection'" class="tab-content">
        <div class="detection-controls">
          <div class="form-item">
            <label>
              <img src="@/assets/balance.png" alt="选择考核" class="label-icon">
              选择考核
            </label>
            <select v-model="selectedExam">
              <option value="">请选择考核</option>
              <option v-for="exam in exams" :key="exam.id" :value="exam.id">
                {{ exam.name }}
              </option>
            </select>
          </div>
          <button @click="analyzeAnswers" class="action-btn primary" :disabled="!selectedExam">
            <img src="@/assets/add.png" alt="分析" class="btn-icon">
            开始分析
          </button>
        </div>

        <div v-if="answerAnalysis" class="answer-analysis-content">
          <div class="analysis-summary">
            <div class="summary-header">
              <img src="@/assets/balance.png" alt="检测结果" class="summary-icon">
              <h3>答案检测结果</h3>
            </div>
            <div class="summary-stats">
              <div class="summary-item">
                <img src="@/assets/category.png" alt="总题数" class="item-icon">
                <div class="item-content">
                  <span class="summary-label">总题数：</span>
                  <span class="summary-value">{{ answerAnalysis.totalQuestions }}</span>
                </div>
              </div>
              <div class="summary-item">
                <img src="@/assets/delete.png" alt="错误率" class="item-icon">
                <div class="item-content">
                  <span class="summary-label">错误率：</span>
                  <span class="summary-value error">{{ answerAnalysis.errorRate }}%</span>
                </div>
              </div>
              <div class="summary-item">
                <img src="@/assets/author.png" alt="常见错误" class="item-icon">
                <div class="item-content">
                  <span class="summary-label">常见错误：</span>
                  <span class="summary-value">{{ answerAnalysis.commonErrors.length }}种</span>
                </div>
              </div>
            </div>
          </div>

          <div class="error-analysis">
            <div class="error-header">
              <img src="@/assets/category.png" alt="错误分析" class="error-icon">
              <h4>常见错误分析</h4>
            </div>
            <div class="error-list">
              <div v-for="error in answerAnalysis.commonErrors" :key="error.id" class="error-item">
                <div class="error-header">
                  <div class="error-info">
                    <img src="@/assets/balance.png" alt="题目" class="error-icon">
                    <span class="error-question">第{{ error.questionNumber }}题</span>
                  </div>
                  <span class="error-count">{{ error.count }}人错误</span>
                </div>
                <div class="error-details">
                  <div class="error-pattern">
                    <strong>错误模式：</strong>{{ error.pattern }}
                  </div>
                  <div class="error-reason">
                    <strong>错误原因：</strong>{{ error.reason }}
                  </div>
                  <div class="error-suggestion">
                    <strong>教学建议：</strong>{{ error.suggestion }}
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

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getStudentStats, getGradeDistribution, getErrorTopics, getLearningTrend, getTeacherClassStats } from '@/api/stats'
import { getCoursesByTeacherId } from '@/api/course'

// 响应式变量
const activeTab = ref('overview')
const selectedStudent = ref('')
const selectedExam = ref('')

// 标签页配置
const tabs = [
  { key: 'overview', name: '整体概览', iconSrc: '/src/assets/balance.png' },
  { key: 'individual', name: '个人分析', iconSrc: '/src/assets/author.png' },
  { key: 'detection', name: '答案检测', iconSrc: '/src/assets/category.png' }
]

// 班级统计数据
const classStats = ref({
  averageScore: 0,
  passRate: 0,
  excellentRate: 0,
  participationRate: 0
})

// 成绩分布
const scoreRanges = ref([])

// 知识点掌握情况
const knowledgeMastery = ref([])

// 学生列表
const students = ref([])

// 学生分析数据
const studentAnalysis = ref(null)

// 考试列表
const exams = ref([])

// 答案分析数据
const answerAnalysis = ref(null)

// 获取学生统计信息
const fetchStudentStats = async (studentId) => {
  try {
    const res = await getStudentStats(studentId)
    if (res && res.data) {
      console.log('学生统计信息:', res.data)
      return res.data
    }
  } catch (error) {
    console.error('获取学生统计信息失败:', error)
  }
}

// 获取成绩分布
const fetchGradeDistribution = async () => {
  try {
    const res = await getGradeDistribution()
    if (res && res.data) {
      console.log('成绩分布:', res.data)
      
      // 定义分数段映射，按分数从高到低排序
      const scoreRangeMap = {
        'excellent': { label: '90-100分', order: 1 },
        'good': { label: '80-89分', order: 2 },
        'average': { label: '70-79分', order: 3 },
        'below': { label: '60-69分', order: 4 },
        'poor': { label: '60分以下', order: 5 }
      }
      
      // 转换数据格式并排序
      scoreRanges.value = Object.entries(res.data)
        .filter(([key]) => scoreRangeMap[key]) // 只处理已定义的分数段
        .map(([key, percentage]) => ({
          label: scoreRangeMap[key].label,
          percentage: Math.round(percentage * 10) / 10, // 保留一位小数
          count: Math.round(percentage * 31 / 100), // 根据实际考试人数计算，估算为31人
          order: scoreRangeMap[key].order
        }))
        .sort((a, b) => a.order - b.order) // 按分数从高到低排序
    }
  } catch (error) {
    console.error('获取成绩分布失败:', error)
    // 使用默认数据
    scoreRanges.value = [
      { label: '90-100分', percentage: 15, count: 5, order: 1 },
      { label: '80-89分', percentage: 25, count: 8, order: 2 },
      { label: '70-79分', percentage: 30, count: 9, order: 3 },
      { label: '60-69分', percentage: 20, count: 6, order: 4 },
      { label: '60分以下', percentage: 10, count: 3, order: 5 }
    ]
  }
}

// 获取错误知识点统计
const fetchErrorTopics = async () => {
  try {
    const res = await getErrorTopics()
    if (res && res.data) {
      console.log('错误知识点:', res.data)
      knowledgeMastery.value = res.data.map(topic => ({
        name: topic.name,
        masteryRate: 100 - topic.errorRate // 掌握率 = 100 - 错误率
      }))
    }
  } catch (error) {
    console.error('获取错误知识点失败:', error)
    // 使用默认数据
    knowledgeMastery.value = [
      { name: '基础概念', masteryRate: 85 },
      { name: '核心原理', masteryRate: 72 },
      { name: '应用实践', masteryRate: 68 },
      { name: '综合运用', masteryRate: 55 }
    ]
  }
}

// 获取学习趋势数据
const fetchLearningTrend = async (studentId) => {
  try {
    const res = await getLearningTrend(studentId)
    if (res && res.data) {
      console.log('学习趋势:', res.data)
      return res.data
    }
  } catch (error) {
    console.error('获取学习趋势失败:', error)
    // 返回模拟数据
    return [85, 88, 92, 87, 90, 86, 89]
  }
}

// 初始化班级统计数据
const initClassStats = async () => {
  try {
    // 获取教师班级统计
    const res = await getTeacherClassStats(2) // teacher1的ID是2
    if (res && res.data) {
      console.log('班级统计数据:', res.data)
      classStats.value = {
        averageScore: res.data.averageScore || 0,
        passRate: res.data.passRate || 0,
        excellentRate: res.data.excellentRate || 0,
        participationRate: res.data.participationRate || 0
      }
    }
  } catch (error) {
    console.error('获取班级统计失败:', error)
    // 使用默认数据
    classStats.value = {
      averageScore: 0,
      passRate: 0,
      excellentRate: 0,
      participationRate: 0
    }
  }
}

// 获取教师的学生列表
const fetchStudents = async () => {
  try {
    // 首先获取教师的课程
    const courseRes = await getCoursesByTeacherId(2) // 假设teacher1的ID是2
    if (courseRes && courseRes.data && courseRes.data.length > 0) {
      // 获取选择了教师课程的学生列表
      const allStudents = []
      for (const course of courseRes.data) {
        try {
          // 这里需要一个新的API来获取课程的学生列表
          // 暂时使用模拟数据，但结构更真实
          const courseStudents = [
            { id: 2, name: '李同学', studentId: 'S2024002', courseId: course.id, courseName: course.name },
            { id: 3, name: '王小明', studentId: 'S2024003', courseId: course.id, courseName: course.name },
            { id: 4, name: '张小红', studentId: 'S2024004', courseId: course.id, courseName: course.name }
          ]
          allStudents.push(...courseStudents)
        } catch (error) {
          console.error(`获取课程 ${course.id} 的学生列表失败:`, error)
        }
      }
      
      // 去重学生（一个学生可能选择多门课程）
      const uniqueStudents = allStudents.reduce((unique, student) => {
        const existing = unique.find(s => s.id === student.id)
        if (!existing) {
          unique.push({
            id: student.id,
            name: student.name,
            studentId: student.studentId,
            courses: [{ id: student.courseId, name: student.courseName }]
          })
        } else {
          existing.courses.push({ id: student.courseId, name: student.courseName })
        }
        return unique
      }, [])
      
      students.value = uniqueStudents
      console.log('获取到的学生列表:', uniqueStudents)
    } else {
      // 如果没有课程数据，使用默认学生列表
      initDefaultStudents()
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    initDefaultStudents()
  }
}

// 初始化默认学生列表
const initDefaultStudents = () => {
  students.value = [
    { id: 2, name: '李同学', studentId: 'S2024002' },
    { id: 3, name: '王小明', studentId: 'S2024003' },
    { id: 4, name: '张小红', studentId: 'S2024004' },
    { id: 6, name: '刘小丽', studentId: 'S2024006' },
    { id: 8, name: '黄小花', studentId: 'S2024008' }
  ]
}

// 初始化考试列表
const initExams = () => {
  exams.value = [
    { id: 1, name: '期中考试' },
    { id: 2, name: '期末考试' },
    { id: 3, name: '单元测试1' },
    { id: 4, name: '单元测试2' }
  ]
}

// 加载学生分析
const loadStudentAnalysis = async () => {
  if (!selectedStudent.value) return
  
  try {
    // 获取学生统计信息
    const stats = await fetchStudentStats(selectedStudent.value)
    if (stats) {
      studentAnalysis.value = {
        name: students.value.find(s => s.id === selectedStudent.value)?.name || '未知学生',
        averageScore: stats.averageScore || 0,
        rank: stats.rank || 0,
        completionRate: stats.completionRate || 0,
        attendanceRate: stats.attendanceRate || 0,
        trend: await fetchLearningTrend(selectedStudent.value),
        weakPoints: [
          { name: '基础概念', score: 75, suggestion: '建议复习基础概念，多做练习题' },
          { name: '核心原理', score: 68, suggestion: '需要深入理解核心原理，可以观看相关视频' },
          { name: '应用实践', score: 82, suggestion: '应用能力较好，可以尝试更复杂的题目' }
        ],
        suggestions: [
          { id: 1, type: '学习建议', priority: '高', content: '建议每天复习30分钟，巩固基础知识' },
          { id: 2, type: '练习建议', priority: '中', content: '多做应用题，提高实践能力' },
          { id: 3, type: '时间管理', priority: '低', content: '合理安排学习时间，避免临时抱佛脚' }
        ]
      }
    }
  } catch (error) {
    console.error('加载学生分析失败:', error)
  }
}

// 分析答案
const analyzeAnswers = async () => {
  if (!selectedExam.value) return
  
  try {
    // 这里可以调用答案分析API
    console.log('分析考试答案:', selectedExam.value)
    
    // 模拟分析结果
    answerAnalysis.value = {
      totalQuestions: 20,
      errorRate: 15,
      commonErrors: [
        {
          id: 1,
          questionNumber: 5,
          count: 8,
          pattern: '概念混淆',
          suggestion: '加强基础概念的理解'
        },
        {
          id: 2,
          questionNumber: 12,
          count: 6,
          pattern: '计算错误',
          suggestion: '注意计算过程，避免粗心错误'
        },
        {
          id: 3,
          questionNumber: 18,
          count: 4,
          pattern: '理解偏差',
          suggestion: '仔细阅读题目，理解题意'
        }
      ]
    }
  } catch (error) {
    console.error('分析答案失败:', error)
  }
}

// 获取优先级样式类
const getPriorityClass = (priority) => {
  const classMap = {
    '高': 'priority-high',
    '中': 'priority-medium',
    '低': 'priority-low'
  }
  return classMap[priority] || ''
}

// ========== 趋势图表相关方法 ==========

// 获取最高分
const getMaxScore = () => {
  if (!studentAnalysis.value?.trend) return 0
  return Math.max(...studentAnalysis.value.trend)
}

// 获取最低分
const getMinScore = () => {
  if (!studentAnalysis.value?.trend) return 0
  return Math.min(...studentAnalysis.value.trend)
}

// 获取平均分
const getAvgScore = () => {
  if (!studentAnalysis.value?.trend) return 0
  const sum = studentAnalysis.value.trend.reduce((a, b) => a + b, 0)
  return Math.round(sum / studentAnalysis.value.trend.length * 10) / 10
}

// 获取分数变化
const getScoreChange = () => {
  if (!studentAnalysis.value?.trend || studentAnalysis.value.trend.length < 2) return '+0'
  const first = studentAnalysis.value.trend[0]
  const last = studentAnalysis.value.trend[studentAnalysis.value.trend.length - 1]
  const change = last - first
  return change >= 0 ? `+${change}` : `${change}`
}

// 获取变化样式类
const getChangeClass = () => {
  const change = getScoreChange()
  if (change.startsWith('+') && change !== '+0') return 'positive'
  if (change.startsWith('-')) return 'negative'
  return 'neutral'
}

// 获取趋势指示器文本
const getTrendText = () => {
  if (!studentAnalysis.value?.trend || studentAnalysis.value.trend.length < 2) return '暂无趋势'
  
  const trend = studentAnalysis.value.trend
  const recent = trend.slice(-3) // 最近3次的趋势
  
  if (recent.length < 2) return '暂无趋势'
  
  let upCount = 0
  let downCount = 0
  
  for (let i = 1; i < recent.length; i++) {
    if (recent[i] > recent[i-1]) upCount++
    else if (recent[i] < recent[i-1]) downCount++
  }
  
  if (upCount > downCount) return '上升趋势'
  if (downCount > upCount) return '下降趋势'
  return '稳定趋势'
}

// 获取趋势样式类
const getTrendClass = () => {
  const text = getTrendText()
  if (text.includes('上升')) return 'trend-up'
  if (text.includes('下降')) return 'trend-down'
  return 'trend-stable'
}

// 获取Y轴刻度
const getYAxisTicks = () => {
  return [100, 80, 60, 40, 20, 0]
}

// 获取趋势日期
const getTrendDates = () => {
  const dates = []
  const today = new Date()
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i * 7) // 每周一次
    dates.push(date.getMonth() + 1 + '/' + date.getDate())
  }
  return dates
}

// 获取数据点位置样式
const getPointStyle = (score, index) => {
  if (!studentAnalysis.value?.trend) return {}
  
  const maxScore = 100
  const minScore = 0
  const percentage = ((score - minScore) / (maxScore - minScore)) * 100
  const leftPercentage = (index / (studentAnalysis.value.trend.length - 1)) * 100
  
  return {
    left: `${leftPercentage}%`,
    bottom: `${percentage}%`,
    transform: 'translate(-50%, 50%)'
  }
}

// 获取SVG路径 - 趋势线
const getLinePath = () => {
  if (!studentAnalysis.value?.trend) return ''
  
  const trend = studentAnalysis.value.trend
  const maxScore = 100
  const minScore = 0
  
  let path = ''
  
  trend.forEach((score, index) => {
    const x = (index / (trend.length - 1)) * 100
    const y = 100 - ((score - minScore) / (maxScore - minScore)) * 100
    
    if (index === 0) {
      path += `M ${x} ${y}`
    } else {
      path += ` L ${x} ${y}`
    }
  })
  
  return path
}

// 获取SVG路径 - 面积填充
const getAreaPath = () => {
  if (!studentAnalysis.value?.trend) return ''
  
  const trend = studentAnalysis.value.trend
  const maxScore = 100
  const minScore = 0
  
  let path = ''
  
  // 起始点
  path += `M 0 100`
  
  // 数据点
  trend.forEach((score, index) => {
    const x = (index / (trend.length - 1)) * 100
    const y = 100 - ((score - minScore) / (maxScore - minScore)) * 100
    path += ` L ${x} ${y}`
  })
  
  // 结束点
  path += ` L 100 100 Z`
  
  return path
}

// 获取趋势分析文本
const getTrendAnalysis = () => {
  if (!studentAnalysis.value?.trend) return '暂无数据分析'
  
  const trend = studentAnalysis.value.trend
  const avgScore = getAvgScore()
  const trendText = getTrendText()
  const change = getScoreChange()
  
  if (trendText.includes('上升')) {
    return `学习成绩呈现良好的上升趋势，相比初期提升了${change.replace('+', '')}分，建议保持当前学习节奏。`
  } else if (trendText.includes('下降')) {
    return `最近成绩有所下降${change}分，建议重点关注薄弱知识点，适当增加练习时间。`
  } else {
    return `成绩保持相对稳定，平均分为${avgScore}分，可以尝试挑战更有难度的内容。`
  }
}

// 悬浮提示相关
const tooltip = ref(null)

const showTooltip = (score, index, event) => {
  // 可以在这里实现悬浮提示功能
  console.log(`第${index + 1}次测试: ${score}分`)
}

const hideTooltip = () => {
  // 隐藏提示
}

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchGradeDistribution(),
    fetchErrorTopics(),
    fetchStudents(),
    initClassStats()
  ])
  
  initExams()
})
</script>

<style lang="scss" scoped>
.student-analysis {
  max-width: 1200px;
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

/* 标签页 */
.analysis-tabs {
  .tab-header {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    background: white;
    border-radius: 16px;
    padding: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    
    .tab-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      background: transparent;
      border: none;
      border-radius: 12px;
      font-size: 14px;
      font-weight: 600;
      color: #718096;
      cursor: pointer;
      transition: all 0.3s ease;
      flex: 1;
      
      .tab-icon {
        width: 20px;
        height: 20px;
        opacity: 0.6;
      }
      
      &:hover {
        background: #f7fafc;
        color: #4a5568;
      }
      
      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        
        .tab-icon {
          opacity: 0.9;
        }
      }
    }
  }
  
  .tab-content {
    .overview-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 24px;
    }
  }
}

/* 分析卡片 */
.analysis-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .card-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3, h4 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
      
      h4 {
        font-size: 1.1rem;
      }
    }
  }
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: #f7fafc;
    border-radius: 12px;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .stat-icon {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .icon-img {
        width: 20px;
        height: 20px;
        opacity: 0.8;
      }
    }
    
    .stat-content {
      flex: 1;
      
      .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #718096;
        font-weight: 600;
      }
    }
  }
}

/* 成绩分布 */
.score-distribution {
  .score-range {
    margin-bottom: 16px;
    
    .range-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .range-label {
        font-size: 14px;
        font-weight: 600;
        color: #2d3748;
      }
      
      .range-count {
        font-size: 12px;
        color: #718096;
      }
    }
    
    .range-bar {
      height: 8px;
      background: #e2e8f0;
      border-radius: 4px;
      overflow: hidden;
      margin-bottom: 4px;
      
      .range-fill {
        height: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 4px;
        transition: width 0.3s ease;
      }
    }
    
    .range-percentage {
      font-size: 12px;
      color: #718096;
      text-align: right;
    }
  }
}

/* 知识点掌握 */
.knowledge-mastery {
  .mastery-item {
    margin-bottom: 16px;
    
    .mastery-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .mastery-icon {
        width: 16px;
        height: 16px;
        opacity: 0.6;
      }
      
      .mastery-name {
        flex: 1;
        font-size: 14px;
        font-weight: 600;
        color: #2d3748;
      }
      
      .mastery-rate {
        font-size: 12px;
        font-weight: 600;
        color: #10b981;
      }
    }
    
    .mastery-bar {
      height: 6px;
      background: #e2e8f0;
      border-radius: 3px;
      overflow: hidden;
      
      .mastery-fill {
        height: 100%;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border-radius: 3px;
        transition: width 0.3s ease;
      }
    }
  }
}

/* 学生选择器 */
.student-selector {
  margin-bottom: 24px;
  
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
    
    select {
      width: 100%;
      max-width: 300px;
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
    }
  }
}

/* 学生信息 */
.student-info {
  margin-bottom: 32px;
  
  .info-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    
    .student-icon {
      width: 32px;
      height: 32px;
      opacity: 0.7;
    }
    
    h3 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 700;
      color: #2d3748;
    }
  }
  
  .student-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    .stat-card {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px;
      background: white;
      border-radius: 16px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .icon-img {
          width: 24px;
          height: 24px;
          opacity: 0.8;
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-number {
          font-size: 1.8rem;
          font-weight: 700;
          color: #2d3748;
          margin-bottom: 4px;
        }
        
        .stat-title {
          font-size: 14px;
          color: #718096;
          font-weight: 600;
        }
      }
    }
  }
}

/* 分析区域 */
.analysis-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

/* 学习趋势 */
.trend-summary {
  .trend-indicator {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    
    &.trend-up {
      background: #d4edd8;
      color: #22c55e;
    }
    
    &.trend-down {
      background: #fecaca;
      color: #ef4444;
    }
    
    &.trend-stable {
      background: #dbeafe;
      color: #3b82f6;
    }
  }
}

.trend-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  
  .trend-stat-item {
    text-align: center;
    padding: 12px;
    background: #f8fafc;
    border-radius: 12px;
    
    .stat-label {
      font-size: 12px;
      color: #64748b;
      margin-bottom: 4px;
    }
    
    .stat-value {
      font-size: 18px;
      font-weight: 700;
      
      &.high {
        color: #22c55e;
      }
      
      &.low {
        color: #ef4444;
      }
      
      &.avg {
        color: #3b82f6;
      }
      
      &.change {
        &.positive {
          color: #22c55e;
        }
        
        &.negative {
          color: #ef4444;
        }
        
        &.neutral {
          color: #64748b;
        }
      }
    }
  }
}

.trend-chart-container {
  margin-bottom: 20px;
  
  .chart-grid {
    display: flex;
    height: 200px;
    
    .y-axis {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding-right: 12px;
      width: 40px;
      
      .y-tick {
        font-size: 11px;
        color: #94a3b8;
        text-align: right;
      }
    }
    
    .chart-area {
      flex: 1;
      position: relative;
      background: #fafbfc;
      border-radius: 8px;
      overflow: hidden;
      
      .grid-lines {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        
        .grid-line {
          position: absolute;
          
          &.horizontal {
            width: 100%;
            height: 1px;
            background: #e2e8f0;
            
            &:nth-child(1) { top: 0%; }
            &:nth-child(2) { top: 20%; }
            &:nth-child(3) { top: 40%; }
            &:nth-child(4) { top: 60%; }
            &:nth-child(5) { top: 80%; }
          }
          
          &.vertical {
            height: 100%;
            width: 1px;
            background: #e2e8f0;
            
            &:nth-child(6) { left: 0%; }
            &:nth-child(7) { left: 16.67%; }
            &:nth-child(8) { left: 33.33%; }
            &:nth-child(9) { left: 50%; }
            &:nth-child(10) { left: 66.67%; }
            &:nth-child(11) { left: 83.33%; }
            &:nth-child(12) { left: 100%; }
          }
        }
      }
      
      .trend-svg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
      }
      
      .trend-points {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 2;
        
        .trend-point {
          position: absolute;
          cursor: pointer;
          
          .point-dot {
            width: 8px;
            height: 8px;
            background: #667eea;
            border: 2px solid white;
            border-radius: 50%;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
            transition: all 0.3s ease;
            
            &:hover {
              transform: scale(1.5);
              box-shadow: 0 4px 12px rgba(102, 126, 234, 0.5);
            }
          }
          
          .point-value {
            position: absolute;
            top: -25px;
            left: 50%;
            transform: translateX(-50%);
            background: #667eea;
            color: white;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 10px;
            font-weight: 600;
            white-space: nowrap;
            opacity: 0;
            transition: opacity 0.3s ease;
          }
          
          &:hover .point-value {
            opacity: 1;
          }
        }
      }
    }
  }
  
  .x-axis {
    display: flex;
    justify-content: space-between;
    margin-left: 40px;
    margin-top: 8px;
    
    .x-tick {
      font-size: 11px;
      color: #94a3b8;
      text-align: center;
    }
  }
}

.trend-analysis {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 12px;
  
  .analysis-text {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    font-size: 13px;
    color: #0369a1;
    line-height: 1.5;
    
    .analysis-icon {
      width: 16px;
      height: 16px;
      margin-top: 1px;
      opacity: 0.7;
    }
  }
}

/* 薄弱知识点 */
.weak-points {
  .weak-point {
    background: #fef2f2;
    border: 1px solid #fecaca;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    
    .point-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .point-icon {
        width: 16px;
        height: 16px;
        opacity: 0.6;
      }
      
      .point-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex: 1;
        
        .point-name {
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
        }
        
        .point-score {
          font-size: 12px;
          color: #e53e3e;
          font-weight: 600;
        }
      }
    }
    
    .point-suggestion {
      font-size: 13px;
      color: #718096;
      line-height: 1.5;
    }
  }
}

/* AI建议 */
.ai-suggestions {
  .suggestion-item {
    background: #f0fff4;
    border: 1px solid #c6f6d5;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    
    .suggestion-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .suggestion-icon {
        width: 16px;
        height: 16px;
        opacity: 0.6;
      }
      
      .suggestion-type {
        flex: 1;
        font-size: 14px;
        font-weight: 600;
        color: #2d3748;
      }
      
      .suggestion-priority {
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 10px;
        font-weight: 600;
        
        &.high {
          background: #fed7d7;
          color: #e53e3e;
        }
        
        &.medium {
          background: #fef3c7;
          color: #d97706;
        }
        
        &.low {
          background: #dbeafe;
          color: #1e40af;
        }
      }
    }
    
    .suggestion-content {
      font-size: 13px;
      color: #4a5568;
      line-height: 1.5;
    }
  }
}

/* 检测控制 */
.detection-controls {
  display: flex;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
  
  .form-item {
    flex: 1;
    max-width: 300px;
    
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
    
    select {
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
    }
  }
}

/* 按钮样式 */
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
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
    }
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }
  }
}

/* 答案分析 */
.answer-analysis-content {
  .analysis-summary {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;
    
    .summary-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      
      .summary-icon {
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
    
    .summary-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      
      .summary-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: #f7fafc;
        border-radius: 12px;
        
        .item-icon {
          width: 20px;
          height: 20px;
          opacity: 0.6;
        }
        
        .item-content {
          flex: 1;
          
          .summary-label {
            font-size: 14px;
            color: #718096;
            margin-right: 8px;
          }
          
          .summary-value {
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
            
            &.error {
              color: #e53e3e;
            }
          }
        }
      }
    }
  }
  
  .error-analysis {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    
    .error-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      
      .error-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h4 {
        margin: 0;
        font-size: 1.1rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
    
    .error-list {
      .error-item {
        background: #fef2f2;
        border: 1px solid #fecaca;
        border-radius: 12px;
        padding: 16px;
        margin-bottom: 12px;
        
        .error-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .error-info {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .error-icon {
              width: 16px;
              height: 16px;
              opacity: 0.6;
            }
            
            .error-question {
              font-size: 14px;
              font-weight: 600;
              color: #2d3748;
            }
          }
          
          .error-count {
            background: #fed7d7;
            color: #e53e3e;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
          }
        }
        
        .error-details {
          .error-pattern {
            font-size: 13px;
            color: #718096;
            line-height: 1.5;
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
  }
  
  .tab-header {
    flex-direction: column;
    
    .tab-btn {
      justify-content: center;
    }
  }
  
  .overview-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .student-stats {
    grid-template-columns: 1fr;
  }
  
  .analysis-sections {
    grid-template-columns: 1fr;
  }
  
  .detection-controls {
    flex-direction: column;
    align-items: stretch;
    
    .form-item {
      max-width: none;
    }
  }
  
  .summary-stats {
    grid-template-columns: 1fr;
  }
}
</style> 