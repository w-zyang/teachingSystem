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
              <!-- 平均分：靶心图标 + 趋势对比 -->
              <div class="stat-item">
                <div class="stat-icon kpi-icon target-icon">
                  <svg viewBox="0 0 24 24" class="icon-svg" aria-hidden="true">
                    <circle cx="12" cy="12" r="9" class="ring-outer" />
                    <circle cx="12" cy="12" r="5" class="ring-inner" />
                    <circle cx="12" cy="12" r="2" class="ring-center" />
                    <line x1="12" y1="3" x2="12" y2="0.5" class="target-arrow" />
                    <line x1="16.5" y1="7.5" x2="19" y2="5" class="target-arrow" />
                  </svg>
                </div>
                <div class="stat-content">
                  <div class="stat-main">
                    <div class="stat-value">{{ classStats.averageScore }}</div>
                    <div class="stat-label">平均分</div>
                  </div>
                  <div class="stat-trend" :class="getKpiTrendClass('averageScore')">
                    <span class="trend-arrow">{{ getKpiTrendArrow('averageScore') }}</span>
                    <span class="trend-text">较上次 {{ getKpiTrendText('averageScore') }}</span>
                  </div>
                </div>
              </div>

              <!-- 及格率 -->
              <div class="stat-item">
                <div class="stat-icon kpi-icon pass-icon">
                  <svg viewBox="0 0 24 24" class="icon-svg" aria-hidden="true">
                    <circle cx="12" cy="12" r="9" class="ring-outer" />
                    <polyline points="8,12 11,15 16,9" class="check-path" />
                  </svg>
                </div>
                <div class="stat-content">
                  <div class="stat-main">
                    <div class="stat-value">{{ classStats.passRate }}%</div>
                    <div class="stat-label">及格率</div>
                  </div>
                  <div class="stat-trend" :class="getKpiTrendClass('passRate')">
                    <span class="trend-arrow">{{ getKpiTrendArrow('passRate') }}</span>
                    <span class="trend-text">较上次 {{ getKpiTrendText('passRate') }}</span>
                  </div>
                </div>
              </div>

              <!-- 优秀率 -->
              <div class="stat-item">
                <div class="stat-icon kpi-icon excellent-icon">
                  <svg viewBox="0 0 24 24" class="icon-svg" aria-hidden="true">
                    <path d="M12 3l2.4 4.86 5.36.78-3.88 3.78.92 5.34L12 15.9 7.2 17.76l.92-5.34L4.24 8.64l5.36-.78z" class="star-path" />
                  </svg>
                </div>
                <div class="stat-content">
                  <div class="stat-main">
                    <div class="stat-value">{{ classStats.excellentRate }}%</div>
                    <div class="stat-label">优秀率</div>
                  </div>
                  <div class="stat-trend" :class="getKpiTrendClass('excellentRate')">
                    <span class="trend-arrow">{{ getKpiTrendArrow('excellentRate') }}</span>
                    <span class="trend-text">较上次 {{ getKpiTrendText('excellentRate') }}</span>
                  </div>
                </div>
              </div>

              <!-- 参与率：多用户图标 + 趋势对比 -->
              <div class="stat-item">
                <div class="stat-icon kpi-icon users-icon">
                  <svg viewBox="0 0 24 24" class="icon-svg" aria-hidden="true">
                    <!-- 前排用户 -->
                    <circle cx="9" cy="9" r="3" class="user-head" />
                    <path d="M5.5 15c0-2.2 1.6-4 3.5-4s3.5 1.8 3.5 4" class="user-body" />
                    <!-- 后排用户 -->
                    <circle cx="16" cy="8" r="2.6" class="user-head secondary" />
                    <path d="M13.5 14c0-2 1.3-3.6 3-3.6s3 1.6 3 3.6" class="user-body secondary" />
                  </svg>
                </div>
                <div class="stat-content">
                  <div class="stat-main">
                    <div class="stat-value">{{ classStats.participationRate }}%</div>
                    <div class="stat-label">参与率</div>
                  </div>
                  <div class="stat-trend" :class="getKpiTrendClass('participationRate')">
                    <span class="trend-arrow">{{ getKpiTrendArrow('participationRate') }}</span>
                    <span class="trend-text">较上次 {{ getKpiTrendText('participationRate') }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 成绩分布：直方图 + 正态分布曲线 -->
          <div class="analysis-card">
            <div class="card-header">
              <div class="header-left">
                <img src="@/assets/category.png" alt="成绩分布" class="card-icon">
                <h3>成绩分布</h3>
              </div>
            </div>
            <div class="score-chart-wrapper">
              <div class="score-chart-legend">
                <span class="legend-item">
                  <span class="legend-bar"></span> 分数段人数直方图
                </span>
                <span class="legend-item">
                  <span class="legend-line"></span> 拟合正态分布曲线
                </span>
              </div>
              <div ref="scoreChartRef" class="score-chart-container"></div>
            </div>
          </div>

          <!-- 全局知识图谱：信息安全体系（横向铺满一行） -->
          <div class="analysis-card knowledge-graph-card">
            <div class="card-header">
              <div class="header-left">
                <img src="@/assets/balance.png" alt="知识点掌握" class="card-icon">
                <h3>知识点掌握情况 · 全局知识图谱</h3>
              </div>
              <button class="action-btn secondary">
                <img src="@/assets/add.png" alt="智能生成" class="btn-icon">
                ✨ 针对薄弱图谱节点，一键生成补救课件/练习卷
              </button>
            </div>
            <div class="knowledge-graph-section">
              <div ref="knowledgeGraphRef" class="knowledge-graph-container"></div>

              <!-- 右侧抽屉：薄弱知识点详情 -->
              <div class="knowledge-drawer" :class="{ open: Boolean(activeKnowledgeNode) }">
                <div class="drawer-header">
                  <div class="drawer-title">
                    <span class="drawer-tag">薄弱知识点聚焦</span>
                    <h4>{{ activeKnowledgeNode ? activeKnowledgeNode.name : '请选择图谱中的红色节点' }}</h4>
                  </div>
                  <button class="drawer-close" @click="activeKnowledgeNode = null">×</button>
                </div>
                <div v-if="activeKnowledgeNode" class="drawer-body">
                  <div class="drawer-metric">
                    <div class="metric-label">班级得分率</div>
                    <div class="metric-value" :class="getNodeScoreClass(activeKnowledgeNode.scoreRate)">
                      {{ activeKnowledgeNode.scoreRate }}%
                    </div>
                    <div class="metric-desc">
                      节点大小代表考试权重，目前权重约 {{ activeKnowledgeNode.weight * 100 }}%，颜色越红表示风险越高。
                    </div>
                  </div>

                  <div class="drawer-section">
                    <div class="section-title">高频错题</div>
                    <ul class="question-list">
                      <li v-for="q in activeKnowledgeNode.highErrorQuestions" :key="q">
                        第 {{ q }} 题
                      </li>
                    </ul>
                  </div>

                  <div class="drawer-section">
                    <div class="section-title">教学建议</div>
                    <p class="section-text">
                      建议围绕该知识点组织一次针对性讲评课，重点拆解易错步骤与概念混淆点，
                      并结合真实错题展开板书推演。
                    </p>
                  </div>

                  <button class="action-btn primary full-width">
                    <img src="@/assets/add.png" alt="一键生成" class="btn-icon">
                    ✨ 针对该知识点一键生成补救课件/练习卷
                  </button>
                </div>
                <div v-else class="drawer-empty">
                  点击图谱中的<span class="danger-text">红色节点</span>，查看班级共性错题与智能补救方案。
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
                <div class="trend-legend">
                  <span class="legend-item">
                    <span class="legend-line personal"></span> 个人成绩
                  </span>
                  <span class="legend-item">
                    <span class="legend-line class"></span> 班级平均分
                  </span>
                </div>
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
                      
                      <!-- 面积填充：个人成绩 -->
                      <path :d="getAreaPath()" fill="url(#trendGradient)" />
                      
                      <!-- 趋势线：个人成绩 -->
                      <path :d="getLinePath()" fill="none" stroke="url(#lineGradient)" stroke-width="0.9" />

                      <!-- 趋势线：班级平均分（虚线） -->
                      <path
                        v-if="studentAnalysis && studentAnalysis.classTrend"
                        :d="getClassLinePath()"
                        fill="none"
                        stroke="#38bdf8"
                        stroke-width="0.9"
                        stroke-dasharray="3,3"
                      />
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

            <!-- 多维能力雷达图 -->
            <div class="analysis-card">
              <div class="card-header">
                <div class="header-left">
                  <img src="@/assets/category.png" alt="能力雷达" class="card-icon">
                  <h4>多维能力雷达图</h4>
                </div>
              </div>
              <div class="radar-card-body">
                <!-- 调试信息 -->
                <div v-if="!studentAnalysis?.abilities" style="padding: 20px; text-align: center; color: #999;">
                  正在加载能力数据...
                </div>
                <template v-else>
                  <div class="radar-legend">
                    <span class="legend-item">
                      <span class="legend-dot self"></span> 学生当前得分
                    </span>
                    <span class="legend-item">
                      <span class="legend-dot class"></span> 班级平均水平
                    </span>
                  </div>
                  <div ref="radarChartRef" class="radar-chart-container"></div>
                </template>
              </div>
            </div>

            <!-- 学习行为与习惯画像 -->
            <div class="analysis-card">
              <div class="card-header">
                <div class="header-left">
                  <img src="@/assets/time.png" alt="学习行为" class="card-icon">
                  <h4>学习行为与习惯画像</h4>
                </div>
              </div>
              <div class="behavior-card-body">
                <div class="behavior-metrics">
                  <div
                    v-for="metric in studentAnalysis.behaviors.metrics"
                    :key="metric.label"
                    class="behavior-metric"
                  >
                    <div class="metric-header">
                      <span class="metric-label">{{ metric.label }}</span>
                      <span class="metric-value">{{ metric.value }}%</span>
                    </div>
                    <div class="metric-bar">
                      <div class="metric-fill" :style="{ width: metric.value + '%' }"></div>
                    </div>
                  </div>
                </div>

                <div class="behavior-tags">
                  <div class="tag-group">
                    <div class="tag-title">正向习惯</div>
                    <div class="tag-list">
                      <span
                        v-for="tag in studentAnalysis.behaviors.positiveTags"
                        :key="tag"
                        class="habit-tag positive"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
                  <div class="tag-group">
                    <div class="tag-title">待改进行为</div>
                    <div class="tag-list">
                      <span
                        v-for="tag in studentAnalysis.behaviors.negativeTags"
                        :key="tag"
                        class="habit-tag negative"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
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
                <div
                  v-for="point in studentAnalysis.weakPoints"
                  :key="point.name"
                  class="weak-point"
                  :class="getWeakPointClass(point.score)"
                >
                  <div class="point-header">
                    <img src="@/assets/balance.png" alt="知识点" class="point-icon">
                    <div class="point-info">
                      <span class="point-name">{{ point.name }}</span>
                      <span class="point-score">{{ point.score }}分</span>
                    </div>
                    <button
                      class="action-btn mini"
                      type="button"
                      @click="handlePushPractice(point)"
                    >
                      推送专项练习
                    </button>
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
                <div
                  v-for="suggestion in studentAnalysis.suggestions"
                  :key="suggestion.id"
                  class="suggestion-item"
                  :class="getPriorityClass(suggestion.priority)"
                >
                  <div class="suggestion-header">
                    <img src="@/assets/category.png" alt="建议类型" class="suggestion-icon">
                    <span class="suggestion-type">{{ suggestion.type }}</span>
                    <span class="suggestion-priority" :class="getPriorityClass(suggestion.priority)">
                      {{ suggestion.priority }}
                    </span>
                    <button
                      v-if="suggestion.priority === '高'"
                      class="icon-btn"
                      type="button"
                      @click="handleNotifyParent(suggestion)"
                      title="发送提醒给家长"
                    >
                      ✉
                    </button>
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
import { ref, onMounted, computed, watch, nextTick, onBeforeUnmount } from 'vue'
import { getStudentStats, getGradeDistribution, getErrorTopics, getLearningTrend, getTeacherClassStats } from '@/api/stats'
import { getCoursesByTeacherId } from '@/api/course'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

// 响应式变量
const activeTab = ref('overview')
const selectedStudent = ref('')
const selectedExam = ref('')

// 监听标签页切换，当切换到个人分析时重新渲染雷达图
watch(activeTab, async (newTab) => {
  console.log('标签页切换到:', newTab)
  if (newTab === 'individual' && studentAnalysis.value) {
    await nextTick()
    await nextTick()
    console.log('标签页切换后渲染雷达图')
    renderRadarChart()
  }
})

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

// 上一次考试/上一周期的对比数据（当前阶段使用轻量 Mock，后续可接真实接口）
const previousClassStats = ref({
  averageScore: 72.1,
  passRate: 83.2,
  excellentRate: 18.4,
  participationRate: 91.5
})

// 成绩分布
const scoreRanges = ref([])

// ECharts 实例引用
const scoreChartRef = ref(null)
const scoreChartInstance = ref(null)

const knowledgeGraphRef = ref(null)
const knowledgeGraphInstance = ref(null)

const radarChartRef = ref(null)
const radarChartInstance = ref(null)

// 当前选中的知识图谱节点（用于右侧抽屉）
const activeKnowledgeNode = ref(null)

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

      // 定义分数段映射（按分数从高到低）
      const scoreRangeMap = {
        excellent: { label: '90-100分', order: 1 },
        good: { label: '80-89分', order: 2 },
        average: { label: '70-79分', order: 3 },
        below: { label: '60-69分', order: 4 },
        poor: { label: '60分以下', order: 5 }
      }

      // 后端返回的是各分数段“人数”，在前端统一按真实总人数计算百分比
      const entries = Object.entries(res.data).filter(([key]) => scoreRangeMap[key])
      const total = entries.reduce((sum, [, count]) => sum + (Number(count) || 0), 0)

      scoreRanges.value = entries
        .map(([key, count]) => {
          const numericCount = Number(count) || 0
          const percent = total === 0 ? 0 : (numericCount / total) * 100
          return {
            key,
            label: scoreRangeMap[key].label,
            percentage: Math.round(percent * 10) / 10, // 百分比保留一位小数
            count: numericCount,
            order: scoreRangeMap[key].order
          }
        })
        .sort((a, b) => a.order - b.order)
    }
  } catch (error) {
    console.error('获取成绩分布失败:', error)
    // 使用默认数据
    // 兜底 Mock：仍然保证“人数 -> 百分比”的数学一致性
    const mockCounts = [
      { label: '90-100分', count: 5, order: 1 },
      { label: '80-89分', count: 8, order: 2 },
      { label: '70-79分', count: 9, order: 3 },
      { label: '60-69分', count: 6, order: 4 },
      { label: '60分以下', count: 3, order: 5 }
    ]
    const total = mockCounts.reduce((sum, item) => sum + item.count, 0)
    scoreRanges.value = mockCounts.map(item => ({
      ...item,
      percentage: total === 0 ? 0 : Math.round((item.count / total) * 1000) / 10
    }))
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

// ========== 成绩分布直方图 + 正态分布曲线 ==========

// 计算并渲染成绩分布图
const renderScoreChart = () => {
  if (!scoreChartRef.value) return

  if (!scoreChartInstance.value) {
    scoreChartInstance.value = echarts.init(scoreChartRef.value)
  }

  const chart = scoreChartInstance.value

  // 固定的 X 轴顺序
  const xLabels = ['60分以下', '60-69分', '70-79分', '80-89分', '90-100分']
  const midPoints = [50, 65, 75, 85, 95]

  // 按标签对齐成绩分布数据
  const counts = []
  const percents = []
  xLabels.forEach(label => {
    const item = scoreRanges.value.find(r => r.label === label)
    counts.push(item ? item.count : 0)
    percents.push(item ? item.percentage : 0)
  })

  const totalCount = counts.reduce((s, v) => s + v, 0)
  const maxCount = Math.max(...counts, 0)

  // 计算均值和标准差，用于拟合“钟形曲线”
  let mean = 0
  let variance = 0
  if (totalCount > 0) {
    const weightedSum = midPoints.reduce((s, x, i) => s + x * counts[i], 0)
    mean = weightedSum / totalCount
    const weightedVar = midPoints.reduce((s, x, i) => s + counts[i] * Math.pow(x - mean, 2), 0)
    variance = weightedVar / totalCount
  }
  const std = variance > 0 ? Math.sqrt(variance) : 10

  // 生成拟合正态分布曲线，使其峰值与直方图高度在同一量纲
  const normalValues = midPoints.map(x => {
    const exponent = -Math.pow(x - mean, 2) / (2 * Math.pow(std, 2))
    const pdf = (1 / (std * Math.sqrt(2 * Math.PI))) * Math.exp(exponent)
    return pdf
  })
  const maxPdf = Math.max(...normalValues, 0.0001)
  const scale = maxCount > 0 ? maxCount / maxPdf : 1
  const normalScaled = normalValues.map(v => Math.round(v * scale * 10) / 10)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const bar = params.find(p => p.seriesName === '人数')
        const line = params.find(p => p.seriesName === '拟合曲线')
        const idx = bar ? bar.dataIndex : 0
        const label = xLabels[idx]
        const count = counts[idx] || 0
        const percent = percents[idx] || 0
        return [
          `${label}`,
          `人数：${count} 人`,
          `占比：${percent}%`,
          line ? `趋势：${line.value.toFixed(1)}（相对高度）` : ''
        ].filter(Boolean).join('<br/>')
      }
    },
    grid: {
      left: '6%',
      right: '4%',
      bottom: '10%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xLabels,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      nameTextStyle: { color: '#64748b' },
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      splitLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b' }
    },
    series: [
      {
        name: '人数',
        type: 'bar',
        data: counts,
        barWidth: '45%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#6366f1' },
            { offset: 1, color: '#a855f7' }
          ])
        }
      },
      {
        name: '拟合曲线',
        type: 'line',
        data: normalScaled,
        smooth: true,
        yAxisIndex: 0,
        lineStyle: {
          color: '#0ea5e9',
          width: 2
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#0ea5e9',
          borderColor: '#e0f2fe',
          borderWidth: 2
        }
      }
    ]
  }

  chart.setOption(option)
}

// 当成绩分布数据变化时重新渲染图表
watch(scoreRanges, async (val) => {
  if (!val || !val.length) return
  await nextTick()
  renderScoreChart()
})

// ========== 全局知识图谱（graph） ==========

// 根据得分率返回颜色
const getNodeColor = (scoreRate) => {
  if (scoreRate > 85) return '#22c55e' // 绿
  if (scoreRate >= 60) return '#eab308' // 黄
  return '#ef4444' // 红
}

// Drawer 中分数颜色
const getNodeScoreClass = (scoreRate) => {
  if (scoreRate > 85) return 'score-good'
  if (scoreRate >= 60) return 'score-warning'
  return 'score-danger'
}

// 构造信息安全体系知识图谱数据
const buildKnowledgeGraphData = () => {
  // Level 0: 中心节点
  const center = {
    id: 'center',
    name: '信息安全体系',
    category: 0,
    symbolSize: 70,
    scoreRate: 78,
    weight: 0.0,
    highErrorQuestions: [],
    draggable: true
  }

  // Level 1 & 2: 大章和知识点（根据需求文案）
  const chapters = [
    {
      id: 'chap1',
      name: '密码学基础（权重高）',
      weight: 0.2,
      children: [
        { id: 'c1-1', name: '对称加密 (AES/DES)', scoreRate: 85, weight: 0.05, highErrorQuestions: [] },
        { id: 'c1-2', name: '非对称加密与 RSA 算法', scoreRate: 65, weight: 0.05, highErrorQuestions: [5, 12] },
        { id: 'c1-3', name: '哈希函数与数字签名', scoreRate: 45, weight: 0.05, highErrorQuestions: [5, 12] },
        { id: 'c1-4', name: 'PKI 与数字证书体系', scoreRate: 60, weight: 0.03, highErrorQuestions: [8] },
        { id: 'c1-5', name: '椭圆曲线密码学 (ECC)', scoreRate: 38, weight: 0.02, highErrorQuestions: [16, 18] }
      ]
    },
    {
      id: 'chap2',
      name: '网络与通信安全（权重极高）',
      weight: 0.25,
      children: [
        { id: 'c2-1', name: '防火墙原理与 ACL', scoreRate: 90, weight: 0.06, highErrorQuestions: [] },
        { id: 'c2-2', name: '入侵检测/防御系统 (IDS/IPS)', scoreRate: 70, weight: 0.05, highErrorQuestions: [9] },
        { id: 'c2-3', name: 'ARP 欺骗与局域网安全', scoreRate: 82, weight: 0.04, highErrorQuestions: [] },
        { id: 'c2-4', name: 'VPN 与 IPSec 协议隧道', scoreRate: 62, weight: 0.05, highErrorQuestions: [10] },
        { id: 'c2-5', name: 'DDoS 攻击原理与流量清洗', scoreRate: 55, weight: 0.06, highErrorQuestions: [11, 17] },
        { id: 'c2-6', name: '零信任网络架构基础', scoreRate: 42, weight: 0.07, highErrorQuestions: [19, 20] }
      ]
    },
    {
      id: 'chap3',
      name: '系统与应用安全（权重极高）',
      weight: 0.25,
      children: [
        { id: 'c3-1', name: '访问控制模型 (RBAC/MAC/DAC)', scoreRate: 80, weight: 0.05, highErrorQuestions: [] },
        { id: 'c3-2', name: 'Web 漏洞：SQL 注入攻防', scoreRate: 88, weight: 0.06, highErrorQuestions: [] },
        { id: 'c3-3', name: 'Web 漏洞：XSS 与 CSRF', scoreRate: 68, weight: 0.05, highErrorQuestions: [6, 13] },
        { id: 'c3-4', name: '缓冲区溢出漏洞分析', scoreRate: 35, weight: 0.06, highErrorQuestions: [7, 14] },
        { id: 'c3-5', name: '恶意软件与勒索病毒识别', scoreRate: 78, weight: 0.04, highErrorQuestions: [] },
        { id: 'c3-6', name: 'OAuth 2.0 与 JWT 身份认证', scoreRate: 64, weight: 0.04, highErrorQuestions: [15] }
      ]
    },
    {
      id: 'chap4',
      name: '安全管理与合规（权重中等）',
      weight: 0.15,
      children: [
        { id: 'c4-1', name: '网络安全等级保护 2.0', scoreRate: 88, weight: 0.04, highErrorQuestions: [] },
        { id: 'c4-2', name: '风险评估模型 (STRIDE/DREAD)', scoreRate: 72, weight: 0.04, highErrorQuestions: [3] },
        { id: 'c4-3', name: 'ISO 27001 信息安全管理体系', scoreRate: 85, weight: 0.03, highErrorQuestions: [] },
        { id: 'c4-4', name: '应急响应与业务连续性 (BCP)', scoreRate: 66, weight: 0.04, highErrorQuestions: [4] }
      ]
    },
    {
      id: 'chap5',
      name: '数据安全与隐私保护（前沿大章）',
      weight: 0.15,
      children: [
        { id: 'c5-1', name: '《数据安全法》与 GDPR 基础', scoreRate: 80, weight: 0.04, highErrorQuestions: [] },
        { id: 'c5-2', name: '数据脱敏与匿名化 (k-anonymity)', scoreRate: 75, weight: 0.04, highErrorQuestions: [2] },
        { id: 'c5-3', name: '联邦学习与同态加密初步', scoreRate: 30, weight: 0.07, highErrorQuestions: [1, 9, 18] }
      ]
    }
  ]

  const nodes = [center]
  const links = []

  chapters.forEach(chap => {
    const chapNode = {
      id: chap.id,
      name: chap.name,
      category: 1,
      symbolSize: 40 + chap.weight * 40,
      scoreRate: 70,
      weight: chap.weight,
      highErrorQuestions: [],
      draggable: true
    }
    nodes.push(chapNode)
    links.push({ source: center.id, target: chap.id })

    chap.children.forEach(item => {
      const node = {
        id: item.id,
        name: item.name,
        category: 2,
        symbolSize: 20 + item.weight * 120,
        scoreRate: item.scoreRate,
        weight: item.weight,
        highErrorQuestions: item.highErrorQuestions
      }
      nodes.push(node)
      links.push({ source: chap.id, target: item.id })
    })
  })

  return { nodes, links }
}

// 初始化知识图谱
const renderKnowledgeGraph = () => {
  if (!knowledgeGraphRef.value) return

  if (!knowledgeGraphInstance.value) {
    knowledgeGraphInstance.value = echarts.init(knowledgeGraphRef.value)
  }

  const graph = knowledgeGraphInstance.value
  const { nodes, links } = buildKnowledgeGraphData()

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const data = params.data
        if (!data || !data.scoreRate) {
          return data.name || ''
        }
        const list = (data.highErrorQuestions || []).map(q => `第${q}题`).join('、') || '暂无高频错题'
        return [
          `<strong>${data.name}</strong>`,
          `得分率：${data.scoreRate}%`,
          `高频错题：${list}`
        ].join('<br/>')
      }
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        roam: true,
        focusNodeAdjacency: true,
        symbol: 'circle',
        label: {
          show: true,
          position: 'right',
          formatter: '{b}',
          color: '#0f172a',
          fontSize: 11
        },
        force: {
          repulsion: 140,
          gravity: 0.05,
          edgeLength: [80, 160]
        },
        lineStyle: {
          color: '#cbd5e1',
          width: 1,
          curveness: 0.1
        },
        data: nodes.map(n => ({
          ...n,
          itemStyle: {
            color: getNodeColor(n.scoreRate || 75),
            borderColor: '#e5e7eb',
            borderWidth: 1
          }
        })),
        links
      }
    ]
  }

  graph.setOption(option)

  // 点击红色节点时打开右侧抽屉
  graph.off('click')
  graph.on('click', (params) => {
    const data = params.data || {}
    if (data.scoreRate !== undefined && data.scoreRate < 60) {
      activeKnowledgeNode.value = {
        name: data.name,
        scoreRate: data.scoreRate,
        weight: data.weight,
        highErrorQuestions: data.highErrorQuestions || []
      }
    }
  })
}

// ========== 多维能力雷达图 ==========

const renderRadarChart = () => {
  console.log('renderRadarChart called', {
    hasRef: !!radarChartRef.value,
    hasAnalysis: !!studentAnalysis.value,
    hasAbilities: !!studentAnalysis.value?.abilities
  })

  if (!radarChartRef.value) {
    console.warn('雷达图容器未找到')
    return
  }

  if (!studentAnalysis.value?.abilities) {
    console.warn('学生能力数据未加载')
    return
  }

  if (!radarChartInstance.value) {
    console.log('初始化雷达图实例')
    radarChartInstance.value = echarts.init(radarChartRef.value)
  }

  const radar = radarChartInstance.value
  const { dimensions, self, classAvg } = studentAnalysis.value.abilities

  console.log('雷达图数据:', { dimensions, self, classAvg })

  const indicators = dimensions.map(name => ({
    name,
    max: 100
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const values = params.value
        let html = `<strong>${params.seriesName}</strong><br/>`
        dimensions.forEach((dim, idx) => {
          const selfScore = self[idx]
          const classScore = classAvg[idx]
          const diff = Math.round((selfScore - classScore) * 10) / 10
          const symbol = diff >= 0 ? '+' : ''
          html += `${dim}：${selfScore} 分（班级均分 ${classScore}，差值 ${symbol}${diff}）<br/>`
        })
        return html
      }
    },
    radar: {
      indicator: indicators,
      radius: '65%',
      splitNumber: 4,
      splitLine: {
        lineStyle: {
          color: ['#e5e7eb', '#d1d5db', '#9ca3af', '#6b7280']
        }
      },
      splitArea: {
        areaStyle: {
          color: ['#f9fafb', '#eff6ff', '#eef2ff', '#f9fafb']
        }
      },
      axisLine: {
        lineStyle: {
          color: '#cbd5e1'
        }
      }
    },
    series: [
      {
        name: '能力雷达',
        type: 'radar',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.6)' },
            { offset: 1, color: 'rgba(139,92,246,0.2)' }
          ])
        },
        lineStyle: {
          color: '#4f46e5',
          width: 2
        },
        symbol: 'circle',
        symbolSize: 4,
        itemStyle: {
          color: '#4f46e5'
        },
        data: [
          {
            value: self,
            name: '学生当前得分'
          },
          {
            value: classAvg,
            name: '班级平均水平',
            lineStyle: {
              color: '#38bdf8',
              width: 2,
              type: 'dashed'
            },
            areaStyle: {
              color: 'rgba(56,189,248,0.15)'
            },
            itemStyle: {
              color: '#38bdf8'
            }
          }
        ]
      }
    ]
  }

  console.log('设置雷达图配置')
  radar.setOption(option, true)
  console.log('雷达图渲染完成')
}

// 学生分析变化时，更新雷达图
watch(studentAnalysis, (newVal) => {
  console.log('studentAnalysis changed:', newVal)
  nextTick().then(() => {
    renderRadarChart()
  })
}, { deep: true })

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

// 根据统计数据生成一套业务自洽的 Mock 学情数据
const buildStudentAnalysisFromStats = async (stats) => {
  // 顶部关键指标：如果后端没有给，使用符合业务的展示值
  const averageScore = stats?.averageScore && stats.averageScore > 0 ? stats.averageScore : 76.5
  const completionRate = stats?.completionRate && stats.completionRate > 0 ? stats.completionRate : 92
  const attendanceRate = stats?.attendanceRate && stats.attendanceRate > 0 ? stats.attendanceRate : 96

  // 班级排名使用“15 / 45”形式，如果后端不提供 rank，则回退到固定示例
  const rankText = stats?.rank && stats.totalStudents
    ? `${stats.rank} / ${stats.totalStudents}`
    : '15 / 45'

  // 生成带有波动的趋势数组（围绕平均分上下浮动）
  const base = averageScore
  const deltas = [-6, -2, 0, +3, -4, +5, +1]
  const trend = deltas.map((d, idx) => {
    const value = Math.max(0, Math.min(100, Math.round((base + d + idx) * 10) / 10))
    return value
  })

  // 班级平均分趋势：整体比个人平滑一些，略晚起步
  const classBase = 75
  const classDeltas = [-2, -1, 0, +1, 0, +1, +2]
  const classTrend = classDeltas.map((d, idx) => {
    const value = Math.max(0, Math.min(100, Math.round((classBase + d + idx * 0.5) * 10) / 10))
    return value
  })

  // 多维能力雷达：学生 vs 班级均分
  const abilities = {
    dimensions: ['概念理解', '逻辑推导', '应用实践', '错题订正', '创新思维'],
    self: [78, 74, 82, 70, 68],
    classAvg: [75, 72, 78, 65, 66]
  }

  // 学习行为与习惯画像
  const behaviors = {
    metrics: [
      { label: '视频完播率', value: 85 },
      { label: '作业按时提交率', value: 90 },
      { label: '笔记整理覆盖率', value: 72 },
      { label: '错题订正完成率', value: 64 }
    ],
    positiveTags: ['按时交作业', '主动复习', '课堂记笔记', '考前系统梳理'],
    negativeTags: ['多次未订正错题', '部分章节练习量偏少', '答题时间波动较大']
  }

  // 薄弱知识点：分数段覆盖高 / 中 / 低，用于驱动颜色和文案
  const weakPoints = [
    { name: '缓冲区溢出漏洞分析', score: 38, suggestion: '基础编程与内存模型掌握不足，建议配合示例代码进行逐行调试。' },
    { name: '联邦学习与同态加密初步', score: 32, suggestion: '高阶数学与机器学习基础偏弱，可从概念讲解视频和简化例题入手。' },
    { name: 'Web 漏洞：XSS 与 CSRF', score: 62, suggestion: '概念容易混淆，建议通过对比表和真实案例加深理解。' },
    { name: 'PKI 与数字证书体系', score: 74, suggestion: '流程记忆不牢，可绘制完整时序图并配合练习题反复巩固。' },
    { name: '防火墙原理与 ACL', score: 86, suggestion: '整体掌握较好，可以尝试更多综合配置场景题。' }
  ]

  // AI 学习建议：根据平均分、薄弱知识点和行为指标动态生成
  const sortedWeak = [...weakPoints].sort((a, b) => a.score - b.score)
  const weakest = sortedWeak[0]
  const secondWeak = sortedWeak[1]
  const lowBehaviorMetrics = behaviors.metrics.filter(m => m.value < 70)

  const suggestions = []
  if (averageScore < 40) {
    suggestions.push({
      id: 1,
      type: '基础补救',
      priority: '高',
      content: `基础极度薄弱，建议从最基础概念重新梳理，优先针对「${weakest?.name || '核心基础模块'}」安排入门讲义与配套练习。`
    })
  } else if (averageScore < 60) {
    suggestions.push({
      id: 1,
      type: '重点提分',
      priority: '高',
      content: `当前整体成绩偏低，优先锁定「${weakest?.name || '薄弱知识点1'}」和「${secondWeak?.name || '薄弱知识点2'}」两大薄弱模块，集中安排针对性练习。`
    })
  } else if (averageScore < 80) {
    suggestions.push({
      id: 1,
      type: '查漏补缺',
      priority: '中',
      content: `基础已经打牢，可以围绕「${weakest?.name || '得分波动较大的章节'}」设置阶段性小测和错题回顾，持续抬高分数下限。`
    })
  } else {
    suggestions.push({
      id: 1,
      type: '能力拔高',
      priority: '中',
      content: `成绩表现优秀，可在掌握较好的模块上增加综合案例训练，同时针对「${weakest?.name || '相对薄弱模块'}」设计进阶题目，防止短板拉低整体表现。`
    })
  }

  if (completionRate < 70) {
    suggestions.push({
      id: 2,
      type: '作业执行力',
      priority: '高',
      content: '作业完成率偏低，建议与老师共同制定固定的作业时间段，并通过学习平台开启作业提醒与逾期告警。'
    })
  } else if (completionRate < 90) {
    suggestions.push({
      id: 2,
      type: '时间管理',
      priority: '中',
      content: '适当提前安排作业与预习时间，避免临近截止日期集中完成，可将大作业拆分为多次小任务分布在一周内。'
    })
  } else {
    suggestions.push({
      id: 2,
      type: '学习节奏',
      priority: '低',
      content: '作业完成率表现优异，可以适当加入拓展阅读或实践项目，例如结合课程内容完成一个小型实践报告。'
    })
  }

  suggestions.push({
    id: 3,
    type: '学习心态',
    priority: '低',
    content: '保持稳定的学习频率与良好作息，比短期的“突击学习”更有利于长期记忆。'
  })

  return {
    name: students.value.find(s => s.id === selectedStudent.value)?.name || '未知学生',
    averageScore,
    rank: rankText,
    completionRate,
    attendanceRate,
    trend,
    classTrend,
    abilities,
    behaviors,
    weakPoints,
    suggestions
  }
}

// 加载学生分析
const loadStudentAnalysis = async () => {
  if (!selectedStudent.value) {
    console.warn('未选择学生')
    return
  }
  
  console.log('开始加载学生分析，学生ID:', selectedStudent.value)
  
  try {
    const stats = await fetchStudentStats(selectedStudent.value)
    const analysis = await buildStudentAnalysisFromStats(stats)
    studentAnalysis.value = analysis
    console.log('学生分析数据已设置:', analysis)
    
    // 确保雷达图在数据更新后渲染
    await nextTick()
    await nextTick()
    renderRadarChart()
  } catch (error) {
    console.error('加载学生分析失败:', error)
    // 回退到完全基于 Mock 的学情数据
    const analysis = await buildStudentAnalysisFromStats(null)
    studentAnalysis.value = analysis
    console.log('使用 Mock 数据:', analysis)
    
    // 确保雷达图在数据更新后渲染
    await nextTick()
    await nextTick()
    renderRadarChart()
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

// 薄弱知识点颜色等级
const getWeakPointClass = (score) => {
  if (score < 60) return 'weak-danger'
  if (score < 80) return 'weak-warning'
  return 'weak-good'
}

// 推送专项练习
const handlePushPractice = (point) => {
  const name = studentAnalysis.value?.name || '该学生'
  ElMessage.success(`已成功向${name}推送【${point.name}】专项练习卷`)
}

// 高优先级建议：发送提醒给家长
const handleNotifyParent = (suggestion) => {
  const name = studentAnalysis.value?.name || '该学生'
  ElMessage.success(`已向${name}的家长发送提醒：「${suggestion.type} - ${suggestion.content}」`)
}

// ========== 班级 KPI 趋势标签相关 ========== 

// 计算指定 KPI 与上一期的差值（保留一位小数）
const getKpiDelta = (key) => {
  const current = Number(classStats.value[key] || 0)
  const previous = Number(previousClassStats.value[key] || 0)
  const delta = current - previous
  return Math.round(delta * 10) / 10
}

// 获取趋势文本（如 +2.1 分 / -3%）
const getKpiTrendText = (key) => {
  const delta = getKpiDelta(key)
  const unit = key === 'averageScore' ? '分' : '%'
  const prefix = delta > 0 ? '+' : ''
  return `${prefix}${delta}${unit}`
}

// 获取趋势方向箭头
const getKpiTrendArrow = (key) => {
  const delta = getKpiDelta(key)
  if (delta > 0) return '↑'
  if (delta < 0) return '↓'
  return '→'
}

// 获取趋势样式类（控制颜色）
const getKpiTrendClass = (key) => {
  const delta = getKpiDelta(key)
  if (delta > 0) return 'trend-positive'
  if (delta < 0) return 'trend-negative'
  return 'trend-neutral'
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

// 构建给定数据序列的 SVG 折线路径
const buildLinePath = (series) => {
  if (!series || !series.length) return ''

  const maxScore = 100
  const minScore = 0
  
  let path = ''
  
  series.forEach((score, index) => {
    const x = (index / (series.length - 1)) * 100
    const y = 100 - ((score - minScore) / (maxScore - minScore)) * 100
    
    if (index === 0) {
      path += `M ${x} ${y}`
    } else {
      path += ` L ${x} ${y}`
    }
  })
  
  return path
}

// 获取SVG路径 - 趋势线（个人）
const getLinePath = () => {
  if (!studentAnalysis.value?.trend) return ''
  return buildLinePath(studentAnalysis.value.trend)
}

// 获取SVG路径 - 趋势线（班级平均分）
const getClassLinePath = () => {
  if (!studentAnalysis.value?.classTrend) return ''
  return buildLinePath(studentAnalysis.value.classTrend)
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
  
  const avgScore = getAvgScore()
  const trendText = getTrendText()
  const change = getScoreChange()

  // 极低分数的兜底文案：禁止出现“0 分还建议挑战更高难度”
  if (avgScore <= 5) {
    return '当前成绩几乎为零，基础极度薄弱，建议从最基础概念开始重新梳理，配合老师的入门示例与习题。'
  }

  if (avgScore < 40) {
    return `平均分约为 ${avgScore} 分，整体掌握严重不足，应优先搭建完整的知识框架，再逐步增加练习密度。`
  }

  if (avgScore < 60) {
    if (trendText.includes('上升')) {
      return `虽然目前平均分只有 ${avgScore} 分，但趋势在缓慢上升（变化 ${change} 分），建议保持努力并集中突破 2–3 个核心薄弱点。`
    }
    if (trendText.includes('下降')) {
      return `平均分 ${avgScore} 分且近期呈下降趋势（变化 ${change} 分），需要及时复盘错题并减少遗忘。`
    }
    return `平均分 ${avgScore} 分，整体偏低但波动不大，建议通过有计划的阶段测验逐步提升。`
  }

  if (avgScore < 80) {
    if (trendText.includes('上升')) {
      return `平均分 ${avgScore} 分并呈上升趋势（变化 ${change} 分），可以在巩固基础的同时适当增加综合应用题。`
    }
    if (trendText.includes('下降')) {
      return `平均分 ${avgScore} 分但近期略有回落（变化 ${change} 分），建议回看近期错题，避免同类错误反复出现。`
    }
    return `成绩基本稳定在 ${avgScore} 分左右，可通过错题归类和总结笔记进一步抬高分数下限。`
  }

  // 80 分以上：鼓励拔高
  if (trendText.includes('上升')) {
    return `学习成绩表现优秀，平均分达到 ${avgScore} 分且仍在上升（变化 ${change} 分），可以尝试挑战更高难度的综合案例。`
  }
  if (trendText.includes('下降')) {
    return `平均分 ${avgScore} 分，整体水平较好，但近期出现下滑（变化 ${change} 分），建议查找干扰因素并及时调整学习节奏。`
  }
  return `成绩较为稳定，平均分约 ${avgScore} 分，可以在保持现有状态的基础上，尝试更多开放性与项目型任务。`
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

// 定义 handleResize 函数，使其可以在 onMounted 和 onBeforeUnmount 中使用
const handleResize = () => {
  if (scoreChartInstance.value) scoreChartInstance.value.resize()
  if (knowledgeGraphInstance.value) knowledgeGraphInstance.value.resize()
  if (radarChartInstance.value) radarChartInstance.value.resize()
}

// 页面加载时获取数据 & 初始化图表
onMounted(async () => {
  console.log('StudentAnalysis 组件已挂载')
  
  await Promise.all([
    fetchGradeDistribution(),
    fetchErrorTopics(),
    fetchStudents(),
    initClassStats()
  ])

  await nextTick()
  renderScoreChart()
  renderKnowledgeGraph()

  // 默认选中第一个学生并加载个人分析
  if (!selectedStudent.value && students.value.length > 0) {
    console.log('默认选中第一个学生:', students.value[0])
    selectedStudent.value = students.value[0].id
    await loadStudentAnalysis()
    // 学生分析加载完成后渲染雷达图
    console.log('学生分析加载完成，准备渲染雷达图')
    await nextTick()
    await nextTick() // 双重 nextTick 确保 DOM 完全更新
    renderRadarChart()
  }

  initExams()

  // 窗口尺寸变化时自适应
  window.addEventListener('resize', handleResize)
  
  console.log('StudentAnalysis 初始化完成')
})

onBeforeUnmount(() => {
  // 移除窗口尺寸变化监听
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  if (scoreChartInstance.value) {
    scoreChartInstance.value.dispose()
    scoreChartInstance.value = null
  }
  if (knowledgeGraphInstance.value) {
    knowledgeGraphInstance.value.dispose()
    knowledgeGraphInstance.value = null
  }
  if (radarChartInstance.value) {
    radarChartInstance.value.dispose()
    radarChartInstance.value = null
  }
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

      /* 让全局知识图谱卡片横向占满一整行，并与上方卡片左右对齐 */
      .knowledge-graph-card {
        grid-column: 1 / -1;
      }
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

    /* KPI 定制图标样式 */
    .kpi-icon {
      background: radial-gradient(circle at 0 0, rgba(255,255,255,0.4), transparent 55%), 
                  linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
      
      .icon-svg {
        width: 22px;
        height: 22px;
      }

      .ring-outer {
        fill: none;
        stroke: rgba(255,255,255,0.5);
        stroke-width: 1.2;
      }

      .ring-inner {
        fill: none;
        stroke: rgba(255,255,255,0.8);
        stroke-width: 1.4;
      }

      .ring-center {
        fill: #fbbf24;
      }

      .target-arrow {
        stroke: #fbbf24;
        stroke-width: 1.4;
        stroke-linecap: round;
      }

      .check-path {
        fill: none;
        stroke: #22c55e;
        stroke-width: 1.8;
        stroke-linecap: round;
        stroke-linejoin: round;
      }

      .star-path {
        fill: #facc15;
        stroke: rgba(255,255,255,0.8);
        stroke-width: 0.6;
      }

      .user-head {
        fill: #e0f2fe;
      }

      .user-head.secondary {
        fill: #c4b5fd;
      }

      .user-body {
        fill: none;
        stroke: #e0f2fe;
        stroke-width: 1.6;
        stroke-linecap: round;
      }

      .user-body.secondary {
        stroke: #c4b5fd;
      }
    }
    
    .stat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;
      
      .stat-main {
        display: flex;
        flex-direction: column;
      }

      .stat-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 2px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #718096;
        font-weight: 600;
      }

      .stat-trend {
        align-self: flex-start;
        padding: 2px 8px;
        border-radius: 999px;
        font-size: 11px;
        font-weight: 600;
        display: inline-flex;
        align-items: center;
        gap: 4px;

        .trend-arrow {
          font-size: 12px;
        }

        &.trend-positive {
          background: #dcfce7;
          color: #16a34a;
        }

        &.trend-negative {
          background: #fee2e2;
          color: #dc2626;
        }

        &.trend-neutral {
          background: #e5e7eb;
          color: #4b5563;
        }
      }
    }
  }
}

/* 成绩分布：直方图 + 曲线 */
.score-chart-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .score-chart-legend {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    font-size: 12px;
    color: #64748b;

    .legend-item {
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }

    .legend-bar {
      width: 18px;
      height: 6px;
      border-radius: 4px;
      background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
    }

    .legend-line {
      width: 18px;
      height: 0;
      border-top: 2px solid #0ea5e9;
    }
  }

  .score-chart-container {
    width: 100%;
    height: 260px;
  }
}

/* 知识图谱 + 右侧抽屉 */
.knowledge-graph-section {
  display: flex;
  gap: 16px;
  align-items: stretch;
  position: relative;

  .knowledge-graph-container {
    flex: 1;
    width: 100%;
    min-height: 320px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    background: radial-gradient(circle at 0 0, #eff6ff, #ffffff);
  }

  .knowledge-drawer {
    width: 0;
    opacity: 0;
    transform: translateX(8px);
    transition: all 0.25s ease;
    overflow: hidden;

    &.open {
      width: 280px;
      opacity: 1;
      transform: translateX(0);
    }

    .drawer-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;

      .drawer-title {
        h4 {
          margin: 4px 0 0;
          font-size: 14px;
          color: #0f172a;
        }

        .drawer-tag {
          display: inline-flex;
          align-items: center;
          padding: 2px 8px;
          border-radius: 999px;
          font-size: 11px;
          background: #fee2e2;
          color: #b91c1c;
          font-weight: 600;
        }
      }

      .drawer-close {
        border: none;
        background: #f3f4f6;
        border-radius: 999px;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 14px;
        color: #4b5563;
      }
    }

    .drawer-body {
      background: #f9fafb;
      border-radius: 16px;
      padding: 12px 12px 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;

      .drawer-metric {
        .metric-label {
          font-size: 12px;
          color: #6b7280;
          margin-bottom: 4px;
        }
        .metric-value {
          font-size: 22px;
          font-weight: 700;
          margin-bottom: 4px;

          &.score-good {
            color: #16a34a;
          }
          &.score-warning {
            color: #eab308;
          }
          &.score-danger {
            color: #dc2626;
          }
        }
        .metric-desc {
          font-size: 11px;
          color: #6b7280;
          line-height: 1.4;
        }
      }

      .drawer-section {
        .section-title {
          font-size: 12px;
          font-weight: 600;
          color: #111827;
          margin-bottom: 4px;
        }
        .section-text {
          font-size: 12px;
          color: #4b5563;
          line-height: 1.5;
        }
        .question-list {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            font-size: 12px;
            color: #374151;
            padding: 2px 0;
          }
        }
      }

      .full-width {
        width: 100%;
        justify-content: center;
      }
    }

    .drawer-empty {
      font-size: 12px;
      color: #6b7280;
      padding: 8px;
      border-radius: 12px;
      background: #f9fafb;

      .danger-text {
        color: #dc2626;
        font-weight: 600;
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
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
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
  
  .trend-legend {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-bottom: 6px;
    font-size: 11px;
    color: #6b7280;

    .legend-item {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }

    .legend-line {
      width: 14px;
      height: 0;
      border-top-width: 2px;
      border-top-style: solid;

      &.personal {
        border-top-color: #4f46e5;
      }

      &.class {
        border-top-color: #38bdf8;
        border-top-style: dashed;
      }
    }
  }

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

/* 多维能力雷达图 */
.radar-card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;

  .radar-legend {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    font-size: 11px;
    color: #6b7280;

    .legend-item {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }

    .legend-dot {
      width: 8px;
      height: 8px;
      border-radius: 999px;

      &.self {
        background: #4f46e5;
      }

      &.class {
        background: #38bdf8;
      }
    }
  }

  .radar-chart-container {
    width: 100%;
    height: 260px;
  }
}

/* 学习行为与习惯画像 */
.behavior-card-body {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .behavior-metrics {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .behavior-metric {
      .metric-header {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #4b5563;

        .metric-label {
          font-weight: 600;
        }

        .metric-value {
          font-weight: 600;
          color: #2563eb;
        }
      }

      .metric-bar {
        height: 6px;
        border-radius: 999px;
        background: #e5e7eb;
        overflow: hidden;
        margin-top: 4px;

        .metric-fill {
          height: 100%;
          border-radius: 999px;
          background: linear-gradient(90deg, #22c55e, #16a34a);
          transition: width 0.3s ease;
        }
      }
    }
  }

  .behavior-tags {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .tag-group {
      .tag-title {
        font-size: 12px;
        font-weight: 600;
        color: #374151;
        margin-bottom: 4px;
      }

      .tag-list {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
      }
    }

    .habit-tag {
      padding: 4px 10px;
      border-radius: 999px;
      font-size: 11px;
      font-weight: 500;

      &.positive {
        background: #dcfce7;
        color: #166534;
      }

      &.negative {
        background: #fef3c7;
        color: #92400e;
      }
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
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    border: 1px solid transparent;
    background: #f9fafb;

    &.weak-danger {
      background: #fef2f2;
      border-color: #fecaca;
    }

    &.weak-warning {
      background: #fffbeb;
      border-color: #fef3c7;
    }

    &.weak-good {
      background: #ecfdf3;
      border-color: #bbf7d0;
    }
    
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

      .action-btn.mini {
        padding: 4px 10px;
        font-size: 11px;
        border-radius: 999px;
        background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
        color: #fff;
        border: none;
        cursor: pointer;
        white-space: nowrap;
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
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    border: 1px solid transparent;
    background: #f9fafb;

    &.priority-high {
      background: #fef2f2;
      border-color: #fecaca;
    }

    &.priority-medium {
      background: #fffbeb;
      border-color: #fef3c7;
    }

    &.priority-low {
      background: #ecfdf3;
      border-color: #bbf7d0;
    }
    
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
        
        &.priority-high {
          background: #fecaca;
          color: #b91c1c;
        }
        
        &.priority-medium {
          background: #fef3c7;
          color: #92400e;
        }
        
        &.priority-low {
          background: #bbf7d0;
          color: #166534;
        }
      }

      .icon-btn {
        width: 26px;
        height: 26px;
        border-radius: 999px;
        border: none;
        background: #fee2e2;
        color: #b91c1c;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 13px;
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