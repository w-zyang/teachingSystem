<template>
  <div class="error-question-training">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2><i class="el-icon-warning"></i> 智能错题训练</h2>
      <p class="subtitle">基于您的历史错题，生成个性化训练内容</p>
    </div>

    <!-- 错题统计看板 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalErrorQuestions || 0 }}</div>
            <div class="stats-label">历史错题</div>
          </div>
          <i class="el-icon-document stats-icon error-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ (statistics.averageErrorRate || 0).toFixed(1) }}%</div>
            <div class="stats-label">平均错误率</div>
          </div>
          <i class="el-icon-warning stats-icon warning-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ Object.keys(statistics.knowledgePointDistribution || {}).length }}</div>
            <div class="stats-label">涉及知识点</div>
          </div>
          <i class="el-icon-collection stats-icon knowledge-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ completedTrainings }}</div>
            <div class="stats-label">完成训练</div>
          </div>
          <i class="el-icon-trophy stats-icon success-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 训练弹窗 - 使用原生模态框 -->
    <div v-if="trainingDialogVisible" class="training-modal-overlay" @click.self="handleTrainingDialogClose">
      <div class="training-modal">
        <div class="training-modal-header">
          <h3>{{ trainingDialogTitle }}</h3>
          <button class="close-btn" @click="handleTrainingDialogClose">×</button>
        </div>
        <div class="training-modal-body">
      <!-- 训练进行中 -->
      <div v-if="currentTraining && !trainingCompleted" class="training-content">
        <!-- 训练头部信息 -->
        <div class="training-header">
          <div class="training-info">
            <span><i class="el-icon-document"></i> 题目进度: {{ currentQuestionIndex + 1 }} / {{ currentTraining.questions.length }}</span>
            <span><i class="el-icon-time"></i> 已用时: {{ formatTime(elapsedTime) }}</span>
            <span><i class="el-icon-timer"></i> 剩余时间: {{ formatTime(remainingTime) }}</span>
            <span><i class="el-icon-trophy"></i> 训练类型: {{ getTrainingTypeText(currentTraining.trainingType) }}</span>
          </div>
          <el-button type="danger" size="small" @click="exitTraining">
            <i class="el-icon-close"></i> 退出训练
          </el-button>
        </div>

        <!-- 进度条 -->
        <el-progress 
          :percentage="Math.round(((currentQuestionIndex + 1) / currentTraining.questions.length) * 100)" 
          :color="'#409eff'"
          class="training-progress-bar"
        ></el-progress>

        <!-- 当前题目 -->
        <div class="current-question">
          <div class="question-header">
            <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
            <el-tag :type="getQuestionTypeTag(currentQuestion.type)">
              {{ getQuestionTypeText(currentQuestion.type) }}
            </el-tag>
          </div>

          <div class="question-content">
            <h4>{{ currentQuestion.title }}</h4>

            <!-- 选择题 -->
            <div v-if="currentQuestion.type === 'choice'" class="options">
              <el-radio-group v-model="userAnswers[currentQuestionIndex]">
                <el-radio 
                  v-for="(option, index) in currentQuestion.options" 
                  :key="index"
                  :label="option.charAt(0)"
                  class="option-item"
                >
                  {{ option }}
                </el-radio>
              </el-radio-group>
            </div>

            <!-- 填空题 -->
            <div v-else-if="currentQuestion.type === 'fill'" class="fill-input">
              <el-input
                v-model="userAnswers[currentQuestionIndex]"
                placeholder="请输入答案"
                clearable
              ></el-input>
            </div>

            <!-- 简答题 -->
            <div v-else-if="currentQuestion.type === 'short'" class="short-input">
              <el-input
                v-model="userAnswers[currentQuestionIndex]"
                type="textarea"
                :rows="6"
                placeholder="请输入你的答案"
              ></el-input>
            </div>

            <!-- 编程题 -->
            <div v-else-if="currentQuestion.type === 'coding'" class="coding-input">
              <el-input
                v-model="userAnswers[currentQuestionIndex]"
                type="textarea"
                :rows="12"
                placeholder="请输入你的代码"
              ></el-input>
            </div>
          </div>

          <!-- 题目导航和操作按钮 -->
          <div class="question-actions">
            <el-button 
              @click="previousQuestion" 
              :disabled="currentQuestionIndex === 0"
            >
              <i class="el-icon-arrow-left"></i> 上一题
            </el-button>
            
            <el-button 
              v-if="currentQuestionIndex < currentTraining.questions.length - 1"
              type="primary"
              @click="nextQuestion"
            >
              下一题 <i class="el-icon-arrow-right"></i>
            </el-button>
            
            <el-button 
              v-else
              type="success"
              @click="submitTraining"
            >
              <i class="el-icon-check"></i> 提交训练
            </el-button>
          </div>
        </div>
      </div>

      <!-- 训练结果 -->
      <div v-else-if="trainingCompleted && trainingResult" class="training-result">
        <div class="result-header">
          <i class="el-icon-success" style="font-size: 60px; color: #67c23a;"></i>
          <h3>训练完成！</h3>
        </div>

        <el-row :gutter="20" class="result-stats">
          <el-col :span="8">
            <div class="result-stat">
              <div class="stat-number">{{ trainingResult.accuracy }}%</div>
              <div class="stat-label">正确率</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-stat">
              <div class="stat-number">{{ trainingResult.correctCount }} / {{ trainingResult.totalQuestions }}</div>
              <div class="stat-label">正确题数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-stat">
              <div class="stat-number">{{ formatTime(totalTime) }}</div>
              <div class="stat-label">用时</div>
            </div>
          </el-col>
        </el-row>

        <div class="improvement-suggestions">
          <h4><i class="el-icon-star-on"></i> 改进建议</h4>
          <ul>
            <li v-for="(suggestion, index) in trainingResult.improvementSuggestions" :key="index">
              {{ suggestion }}
            </li>
          </ul>
        </div>

        <div class="result-actions">
          <el-button type="primary" @click="restartTraining">
            <i class="el-icon-refresh"></i> 重新训练
          </el-button>
          <el-button type="success" @click="generateNewTraining">
            <i class="el-icon-plus"></i> 生成新训练
          </el-button>
          <el-button @click="closeTrainingDialog">
            <i class="el-icon-close"></i> 关闭
          </el-button>
        </div>
      </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <el-row class="main-content">
      <!-- 错题分析列表（全宽） -->
      <el-col :span="24">
        <el-card shadow="always" class="content-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-analysis"></i> 错题分析</span>
            <el-button type="primary" size="small" @click="refreshErrorAnalysis">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          
          <div v-loading="loadingAnalysis" class="error-list">
            <div v-if="errorAnalysisList.length === 0" class="no-data">
              <i class="el-icon-info"></i>
              <p>暂无错题记录</p>
            </div>
            
            <div 
              v-for="error in errorAnalysisList" 
              :key="error.questionId"
              class="error-item"
              :class="{ active: selectedError && selectedError.questionId === error.questionId }"
            >
              <div class="error-header">
                <div class="header-left">
                  <span class="question-type" :class="error.questionType">
                    {{ getQuestionTypeText(error.questionType) }}
                  </span>
                  <span class="error-rate" :class="getErrorRateClass(error.errorRate)">
                    错误率: {{ error.errorRate }}%
                  </span>
                </div>
                <div class="header-right">
                  <span class="knowledge-point">
                    <i class="el-icon-collection-tag"></i>
                    {{ error.knowledgePoint }}
                  </span>
                </div>
              </div>
              
              <div class="error-content">
                <div class="question-title">
                  <h4>{{ error.questionContent }}</h4>
                </div>
                
                <!-- 选择题选项 -->
                <div v-if="error.questionType === 'choice' && error.options" class="question-options">
                  <div 
                    v-for="(option, index) in error.options" 
                    :key="index"
                    class="option-item"
                    :class="{
                      'correct-option': option.key === error.correctAnswer,
                      'wrong-option': option.key === error.userAnswer && option.key !== error.correctAnswer
                    }"
                  >
                    <span class="option-label">{{ option.key }}.</span>
                    <span class="option-text">{{ option.content }}</span>
                    <span v-if="option.key === error.correctAnswer" class="option-tag correct">✓ 正确答案</span>
                    <span v-if="option.key === error.userAnswer && option.key !== error.correctAnswer" class="option-tag wrong">✗ 你的答案</span>
                  </div>
                </div>
                
                <!-- 填空题/简答题答案 -->
                <div v-else-if="error.questionType === 'fill' || error.questionType === 'short'" class="answer-comparison">
                  <div class="answer-row">
                    <span class="answer-label wrong-label">你的答案：</span>
                    <span class="answer-value wrong-answer">{{ error.userAnswer || '未作答' }}</span>
                  </div>
                  <div class="answer-row">
                    <span class="answer-label correct-label">正确答案：</span>
                    <span class="answer-value correct-answer">{{ error.correctAnswer }}</span>
                  </div>
                </div>
                
                <!-- 编程题答案 -->
                <div v-else-if="error.questionType === 'coding'" class="code-comparison">
                  <div class="code-block">
                    <div class="code-header wrong-header">你的代码：</div>
                    <pre class="code-content">{{ error.userAnswer || '未作答' }}</pre>
                  </div>
                  <div class="code-block">
                    <div class="code-header correct-header">参考答案：</div>
                    <pre class="code-content">{{ error.correctAnswer }}</pre>
                  </div>
                </div>
                
                <!-- 错误分析 -->
                <div class="error-analysis">
                  <div class="analysis-item">
                    <i class="el-icon-warning-outline"></i>
                    <span class="analysis-label">错误类型：</span>
                    <span class="analysis-value">{{ error.errorType }}</span>
                  </div>
                  <div class="analysis-item">
                    <i class="el-icon-info"></i>
                    <span class="analysis-label">错误原因：</span>
                    <span class="analysis-value">{{ error.errorReason }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 底部操作按钮 -->
          <div class="error-list-footer">
            <el-button type="primary" size="default" @click="generateComprehensiveTraining" :loading="generatingTraining">
              <i class="el-icon-magic-stick"></i> 生成综合训练
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { 
  getErrorQuestionAnalysis, 
  getErrorQuestionStatistics,
  generateSimilarQuestions,
  generateKnowledgePointQuestions,
  generateComprehensiveTraining,
  evaluateTrainingEffect
} from '@/api/errorQuestions'

export default {
  name: 'ErrorQuestionTraining',
  data() {
    return {
      studentId: 17,
      errorAnalysisList: [],
      selectedError: null,
      loadingAnalysis: false,
      statistics: {},
      completedTrainings: 0,
      currentTraining: null,
      currentQuestionIndex: 0,
      userAnswers: [],
      trainingCompleted: false,
      trainingResult: null,
      startTime: null,
      elapsedTime: 0,
      totalTime: 0,
      timer: null,
      generatingTraining: false,
      trainingDialogVisible: false,
      trainingDialogTitle: '智能训练',
      timeLimit: 30, // 训练时间限制（分钟）
      remainingTime: 0 // 剩余时间（秒）
    }
  },
  
  computed: {
    currentQuestion() {
      if (this.currentTraining && this.currentTraining.questions) {
        return this.currentTraining.questions[this.currentQuestionIndex] || {}
      }
      return {}
    }
  },
  
  mounted() {
    // 先尝试恢复数据
    this.restoreTrainingData()
    // 然后加载最新数据
    this.initializeData()
  },
  
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    // 页面卸载时保存数据
    this.saveTrainingData()
  },
  
  methods: {
    async initializeData() {
      await Promise.all([
        this.loadErrorAnalysis(),
        this.loadStatistics()
      ])
    },
    
    async loadErrorAnalysis() {
      this.loadingAnalysis = true
      try {
        const response = await getErrorQuestionAnalysis(this.studentId)
        console.log('错题分析API响应:', response)
        if (response.success) {
          this.errorAnalysisList = response.data || []
          console.log('错题列表:', this.errorAnalysisList)
          
          // 如果没有数据，使用模拟数据进行测试
          if (this.errorAnalysisList.length === 0) {
            console.log('使用模拟错题数据')
            this.errorAnalysisList = this.getMockErrorData()
          }
          
          // 加载完错题后立即计算统计数据
          this.calculateStatistics()
        }
      } catch (error) {
        this.$message.error('加载错题分析失败，使用模拟数据')
        console.error(error)
        // 使用模拟数据
        this.errorAnalysisList = this.getMockErrorData()
        // 计算统计数据
        this.calculateStatistics()
      } finally {
        this.loadingAnalysis = false
      }
    },
    
    // 获取模拟错题数据
    getMockErrorData() {
      return [
        {
          questionId: 1,
          questionType: 'choice',
          questionContent: 'Linux系统中，下列哪个命令用于查看当前目录下的文件？',
          options: [
            { key: 'A', content: 'ls' },
            { key: 'B', content: 'cd' },
            { key: 'C', content: 'pwd' },
            { key: 'D', content: 'mkdir' }
          ],
          userAnswer: 'B',
          correctAnswer: 'A',
          knowledgePoint: 'Linux基础命令',
          errorType: '概念混淆',
          errorReason: '将ls命令与cd命令功能混淆',
          errorRate: 75
        },
        {
          questionId: 2,
          questionType: 'choice',
          questionContent: '数据结构中，栈的特点是什么？',
          options: [
            { key: 'A', content: '先进先出(FIFO)' },
            { key: 'B', content: '后进先出(LIFO)' },
            { key: 'C', content: '随机访问' },
            { key: 'D', content: '顺序访问' }
          ],
          userAnswer: 'A',
          correctAnswer: 'B',
          knowledgePoint: '数据结构基础',
          errorType: '理解错误',
          errorReason: '对LIFO(后进先出)概念理解不准确',
          errorRate: 66.7
        },
        {
          questionId: 3,
          questionType: 'choice',
          questionContent: '网络协议TCP和UDP的主要区别是什么？',
          options: [
            { key: 'A', content: 'TCP是面向连接的，UDP是无连接的' },
            { key: 'B', content: 'TCP速度快，UDP速度慢' },
            { key: 'C', content: 'TCP不可靠，UDP可靠' },
            { key: 'D', content: 'TCP用于局域网，UDP用于广域网' }
          ],
          userAnswer: 'B',
          correctAnswer: 'A',
          knowledgePoint: '网络协议',
          errorType: '概念混淆',
          errorReason: '对协议特性理解不全面',
          errorRate: 80
        },
        {
          questionId: 4,
          questionType: 'fill',
          questionContent: 'JavaScript中，用于声明常量的关键字是______。',
          userAnswer: 'var',
          correctAnswer: 'const',
          knowledgePoint: 'JavaScript基础',
          errorType: '知识点遗漏',
          errorReason: '对ES6新特性不熟悉',
          errorRate: 55
        },
        {
          questionId: 5,
          questionType: 'short',
          questionContent: '请简述HTTP和HTTPS的区别。',
          userAnswer: 'HTTPS比HTTP更安全',
          correctAnswer: 'HTTPS是HTTP的安全版本，使用SSL/TLS协议进行加密传输，默认端口443，而HTTP是明文传输，默认端口80',
          knowledgePoint: '网络安全',
          errorType: '回答不完整',
          errorReason: '只提到了安全性，没有说明加密机制和端口差异',
          errorRate: 70
        }
      ]
    },
    
    async loadStatistics() {
      try {
        const response = await getErrorQuestionStatistics(this.studentId)
        if (response.success) {
          this.statistics = response.data || {}
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
      
      // 如果API返回的统计数据为空，则根据错题列表计算
      if (!this.statistics.totalErrorQuestions && this.errorAnalysisList.length > 0) {
        this.calculateStatistics()
      }
    },
    
    // 根据错题列表计算统计数据
    calculateStatistics() {
      const errorList = this.errorAnalysisList
      
      if (errorList.length === 0) {
        this.statistics = {
          totalErrorQuestions: 0,
          averageErrorRate: 0,
          knowledgePointDistribution: {},
          questionTypeDistribution: {},
          errorTypeDistribution: {}
        }
        return
      }
      
      // 计算总错题数
      const totalErrorQuestions = errorList.length
      
      // 计算平均错误率
      const totalErrorRate = errorList.reduce((sum, error) => sum + (error.errorRate || 0), 0)
      const averageErrorRate = totalErrorRate / totalErrorQuestions
      
      // 统计知识点分布
      const knowledgePointDistribution = {}
      errorList.forEach(error => {
        const kp = error.knowledgePoint || '未分类'
        knowledgePointDistribution[kp] = (knowledgePointDistribution[kp] || 0) + 1
      })
      
      // 统计题目类型分布
      const questionTypeDistribution = {}
      errorList.forEach(error => {
        const type = error.questionType || 'unknown'
        questionTypeDistribution[type] = (questionTypeDistribution[type] || 0) + 1
      })
      
      // 统计错误类型分布
      const errorTypeDistribution = {}
      errorList.forEach(error => {
        const type = error.errorType || '未知错误'
        errorTypeDistribution[type] = (errorTypeDistribution[type] || 0) + 1
      })
      
      this.statistics = {
        totalErrorQuestions,
        averageErrorRate,
        knowledgePointDistribution,
        questionTypeDistribution,
        errorTypeDistribution
      }
      
      console.log('计算的统计数据:', this.statistics)
    },
    
    refreshErrorAnalysis() {
      this.loadErrorAnalysis()
    },
    
    selectError(error) {
      this.selectedError = error
    },
    
    async generateSimilarTraining(error) {
      this.generatingTraining = true
      try {
        const response = await generateSimilarQuestions({
          studentId: this.studentId,
          originalQuestionId: error.questionId,
          questionCount: 5
        })
        
        if (response.success) {
          this.startTraining(response.data)
          this.$message.success('相似题目训练生成成功')
        } else {
          this.$message.error(response.msg || '生成训练失败')
        }
      } catch (error) {
        this.$message.error('生成训练失败')
        console.error(error)
      } finally {
        this.generatingTraining = false
      }
    },
    
    async generateKnowledgeTraining(error) {
      this.generatingTraining = true
      try {
        const response = await generateKnowledgePointQuestions({
          studentId: this.studentId,
          knowledgePoint: error.knowledgePoint,
          questionCount: 5
        })
        
        if (response.success) {
          this.startTraining(response.data)
          this.$message.success('知识点训练生成成功')
        } else {
          this.$message.error(response.msg || '生成训练失败')
        }
      } catch (error) {
        this.$message.error('生成训练失败')
        console.error(error)
      } finally {
        this.generatingTraining = false
      }
    },
    
    showKnowledgePointDialog() {
      // 简化版本，直接使用默认知识点
      this.generateKnowledgeTraining({ knowledgePoint: 'Linux基础命令' })
    },
    
    async generateComprehensiveTraining() {
      this.generatingTraining = true
      try {
        console.log('开始生成综合训练，学生ID:', this.studentId)
        const response = await generateComprehensiveTraining({
          studentId: this.studentId,
          questionCount: 10
        })
        
        console.log('综合训练API响应:', response)
        console.log('响应数据:', response.data)
        
        if (response.success) {
          console.log('训练数据:', response.data)
          console.log('题目列表:', response.data.questions)
          this.startTraining(response.data)
          this.$message.success('综合训练生成成功')
        } else {
          console.error('生成训练失败:', response.msg)
          this.$message.error(response.msg || '生成训练失败')
        }
      } catch (error) {
        console.error('生成训练异常:', error)
        this.$message.error('生成训练失败')
      } finally {
        this.generatingTraining = false
      }
    },
    
    startTraining(trainingData) {
      console.log('startTraining 被调用，训练数据:', trainingData)
      
      if (!trainingData || !trainingData.questions || trainingData.questions.length === 0) {
        console.error('训练数据无效:', trainingData)
        this.$message.error('训练数据无效，请重试')
        return
      }
      
      console.log('题目数量:', trainingData.questions.length)
      console.log('第一道题:', trainingData.questions[0])
      console.log('第一道题的title:', trainingData.questions[0].title)
      console.log('第一道题的type:', trainingData.questions[0].type)
      console.log('第一道题的options:', trainingData.questions[0].options)
      
      this.currentTraining = trainingData
      this.currentQuestionIndex = 0
      this.userAnswers = new Array(trainingData.questions.length).fill('')
      this.trainingCompleted = false
      this.trainingResult = null
      
      // 检查是否已有开始时间戳，如果没有则设置新的
      const existingStartTime = localStorage.getItem('errorTrainingStartTime')
      if (!existingStartTime) {
        this.startTime = Date.now()
        localStorage.setItem('errorTrainingStartTime', this.startTime.toString())
        console.log('设置新的训练开始时间:', this.startTime)
      } else {
        this.startTime = parseInt(existingStartTime)
        console.log('使用已存在的训练开始时间:', this.startTime)
      }
      
      this.elapsedTime = 0
      this.remainingTime = this.timeLimit * 60
      
      // 设置弹窗标题
      this.trainingDialogTitle = this.getTrainingTypeText(trainingData.trainingType || 'comprehensive')
      
      console.log('准备显示训练弹窗')
      console.log('currentTraining:', this.currentTraining)
      console.log('currentQuestion:', this.currentQuestion)
      
      // 显示训练弹窗
      this.trainingDialogVisible = true
      console.log('训练弹窗状态:', this.trainingDialogVisible)
      
      // 使用 $nextTick 确保 DOM 更新后再检查
      this.$nextTick(() => {
        console.log('DOM更新后，弹窗状态:', this.trainingDialogVisible)
        console.log('currentTraining是否存在:', !!this.currentTraining)
        console.log('trainingCompleted:', this.trainingCompleted)
      })
      
      // 启动计时器，基于时间戳计算
      this.timer = setInterval(() => {
        const elapsedSeconds = Math.floor((Date.now() - this.startTime) / 1000)
        this.elapsedTime = elapsedSeconds
        const totalSeconds = this.timeLimit * 60
        this.remainingTime = Math.max(0, totalSeconds - elapsedSeconds)
        
        // 如果时间到了，自动提交并清除数据
        if (this.remainingTime <= 0) {
          this.$message.warning('训练时间已到，自动提交')
          this.submitTraining()
        }
      }, 1000)
      
      // 保存训练数据
      this.saveTrainingData()
    },
    
    // 关闭训练弹窗
    closeTrainingDialog() {
      this.trainingDialogVisible = false
      // 清除训练数据
      this.currentTraining = null
      this.trainingCompleted = false
      this.trainingResult = null
      this.clearTrainingData()
    },
    
    // 处理训练弹窗关闭事件
    handleTrainingDialogClose() {
      if (this.currentTraining && !this.trainingCompleted) {
        this.$confirm('训练尚未完成，确定要关闭吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.exitTraining()
        }).catch(() => {
          this.trainingDialogVisible = true
        })
      }
    },
    
    getQuestionTypeTag(type) {
      const tagMap = {
        'choice': 'primary',
        'fill': 'success',
        'short': 'warning',
        'coding': 'danger'
      }
      return tagMap[type] || 'info'
    },
    
    nextQuestion() {
      if (this.currentQuestionIndex < this.currentTraining.questions.length - 1) {
        this.currentQuestionIndex++
        this.saveTrainingData() // 保存进度
      }
    },
    
    previousQuestion() {
      if (this.currentQuestionIndex > 0) {
        this.currentQuestionIndex--
        this.saveTrainingData() // 保存进度
      }
    },
    
    async submitTraining() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
      
      // 清除开始时间戳
      localStorage.removeItem('errorTrainingStartTime')
      
      this.totalTime = Math.floor((Date.now() - this.startTime) / 1000)
      
      try {
        const response = await evaluateTrainingEffect({
          studentId: this.studentId,
          trainingId: this.currentTraining.id || Date.now(),
          answers: this.userAnswers
        })
        
        if (response.success) {
          this.trainingResult = response.data
          this.trainingCompleted = true
          this.completedTrainings++
          
          // 如果是时间到自动提交，清除所有数据
          if (this.remainingTime <= 0) {
            this.clearTrainingData()
            this.$message.success('训练时间已到，已自动提交并清除数据')
          } else {
            this.saveTrainingData() // 保存结果
            this.$message.success('训练完成！')
          }
        } else {
          this.$message.error('提交训练失败')
        }
      } catch (error) {
        this.$message.error('提交训练失败')
        console.error(error)
      }
    },
    
    restartTraining() {
      this.currentQuestionIndex = 0
      this.userAnswers = new Array(this.currentTraining.questions.length).fill('')
      this.trainingCompleted = false
      this.trainingResult = null
      
      // 重新设置开始时间戳
      this.startTime = Date.now()
      localStorage.setItem('errorTrainingStartTime', this.startTime.toString())
      
      this.elapsedTime = 0
      this.remainingTime = this.timeLimit * 60
      
      // 启动计时器
      this.timer = setInterval(() => {
        const elapsedSeconds = Math.floor((Date.now() - this.startTime) / 1000)
        this.elapsedTime = elapsedSeconds
        const totalSeconds = this.timeLimit * 60
        this.remainingTime = Math.max(0, totalSeconds - elapsedSeconds)
        
        // 如果时间到了，自动提交并清除数据
        if (this.remainingTime <= 0) {
          this.$message.warning('训练时间已到，自动提交')
          this.submitTraining()
        }
      }, 1000)
      
      this.saveTrainingData() // 保存重新开始的状态
    },
    
    generateNewTraining() {
      this.currentTraining = null
      this.trainingCompleted = false
      this.trainingResult = null
      this.trainingDialogVisible = false
      
      // 清除开始时间戳
      localStorage.removeItem('errorTrainingStartTime')
      this.clearTrainingData() // 清除旧数据
    },
    
    // 退出训练
    exitTraining() {
      // 停止计时器
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
      // 清除开始时间戳
      localStorage.removeItem('errorTrainingStartTime')
      // 清除所有训练相关数据
      this.currentTraining = null
      this.currentQuestionIndex = 0
      this.userAnswers = []
      this.trainingCompleted = false
      this.trainingResult = null
      this.startTime = null
      this.elapsedTime = 0
      this.totalTime = 0
      this.remainingTime = 0
      this.selectedError = null
      this.trainingDialogVisible = false
      // 清除 LocalStorage 中的数据
      this.clearTrainingData()
      this.$message.success('已退出训练')
    },
    
    getQuestionTypeText(type) {
      const typeMap = {
        'choice': '选择题',
        'fill': '填空题',
        'short': '简答题',
        'coding': '编程题'
      }
      return typeMap[type] || type
    },
    
    getErrorRateClass(rate) {
      if (rate >= 80) return 'high-error'
      if (rate >= 60) return 'medium-error'
      return 'low-error'
    },
    
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}:${secs.toString().padStart(2, '0')}`
    },
    
    getTrainingTypeText(type) {
      const typeMap = {
        'similar': '相似题目训练',
        'knowledge_point': '知识点训练',
        'comprehensive': '综合训练'
      }
      return typeMap[type] || type
    },
    
    // 保存训练数据到 LocalStorage
    saveTrainingData() {
      const trainingData = {
        currentTraining: this.currentTraining,
        currentQuestionIndex: this.currentQuestionIndex,
        userAnswers: this.userAnswers,
        trainingCompleted: this.trainingCompleted,
        trainingResult: this.trainingResult,
        startTime: this.startTime,
        elapsedTime: this.elapsedTime,
        totalTime: this.totalTime,
        remainingTime: this.remainingTime,
        completedTrainings: this.completedTrainings,
        selectedError: this.selectedError,
        timestamp: Date.now()
      }
      localStorage.setItem('errorTrainingData', JSON.stringify(trainingData))
      console.log('错题训练数据已保存到 LocalStorage')
    },
    
    // 从 LocalStorage 恢复训练数据
    restoreTrainingData() {
      try {
        const savedData = localStorage.getItem('errorTrainingData')
        if (savedData) {
          const trainingData = JSON.parse(savedData)
          
          // 恢复数据
          if (trainingData.currentTraining) {
            this.currentTraining = trainingData.currentTraining
            this.currentQuestionIndex = trainingData.currentQuestionIndex || 0
            this.userAnswers = trainingData.userAnswers || []
            this.trainingCompleted = trainingData.trainingCompleted || false
            this.trainingResult = trainingData.trainingResult
            this.completedTrainings = trainingData.completedTrainings || 0
            this.selectedError = trainingData.selectedError
            
            // 恢复开始时间戳
            const startTime = localStorage.getItem('errorTrainingStartTime')
            if (startTime) {
              this.startTime = parseInt(startTime)
              
              // 计算实际剩余时间（基于开始时间戳）
              const elapsedSeconds = Math.floor((Date.now() - this.startTime) / 1000)
              const totalSeconds = this.timeLimit * 60
              this.remainingTime = Math.max(0, totalSeconds - elapsedSeconds)
              this.elapsedTime = elapsedSeconds
              
              console.log(`训练已进行 ${elapsedSeconds} 秒，剩余 ${this.remainingTime} 秒`)
              
              // 如果时间已到，自动提交并清除数据
              if (this.remainingTime <= 0) {
                this.$message.warning('训练时间已到，自动提交')
                this.submitTraining()
                return
              }
            } else {
              this.startTime = trainingData.startTime
              this.elapsedTime = trainingData.elapsedTime || 0
              this.remainingTime = trainingData.remainingTime || 0
            }
            
            this.totalTime = trainingData.totalTime || 0
            
            // 重要：恢复弹窗显示状态
            this.trainingDialogVisible = true
            
            console.log('错题训练数据已从 LocalStorage 恢复')
            this.$message.success('已恢复上次的训练数据')
            
            // 如果训练正在进行中且未完成，恢复计时器
            if (!this.trainingCompleted && this.startTime) {
              this.timer = setInterval(() => {
                const elapsedSeconds = Math.floor((Date.now() - this.startTime) / 1000)
                this.elapsedTime = elapsedSeconds
                const totalSeconds = this.timeLimit * 60
                this.remainingTime = Math.max(0, totalSeconds - elapsedSeconds)
                
                // 如果时间到了，自动提交并清除数据
                if (this.remainingTime <= 0) {
                  this.$message.warning('训练时间已到，自动提交')
                  this.submitTraining()
                }
              }, 1000)
            }
          }
        }
      } catch (error) {
        console.error('恢复训练数据失败:', error)
        localStorage.removeItem('errorTrainingData')
        localStorage.removeItem('errorTrainingStartTime')
      }
    },
    
    // 清除训练数据
    clearTrainingData() {
      localStorage.removeItem('errorTrainingData')
      localStorage.removeItem('errorTrainingStartTime')
      console.log('训练数据已清除')
    }
  }
}
</script>

<style scoped>
.error-question-training {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  color: #303133;
  margin-bottom: 10px;
}

.page-header .subtitle {
  color: #909399;
  font-size: 16px;
}

.stats-cards {
  margin-bottom: 30px;
}

.stats-card {
  position: relative;
  overflow: hidden;
}

.stats-content {
  padding: 20px;
}

.stats-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stats-label {
  color: #909399;
  font-size: 14px;
}

.stats-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 40px;
  opacity: 0.3;
}

.error-icon { color: #f56c6c; }
.warning-icon { color: #e6a23c; }
.knowledge-icon { color: #409eff; }
.success-icon { color: #67c23a; }

.main-content {
  margin-bottom: 30px;
}

.content-card {
  height: 700px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-list {
  height: 600px;
  overflow-y: auto;
}

.error-list-footer {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #ebeef5;
  background: #f5f7fa;
}

.error-list-footer .el-button {
  min-width: 200px;
}

.no-data {
  text-align: center;
  padding: 50px;
  color: #909399;
}

.error-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  margin-bottom: 20px;
  background: white;
  transition: all 0.3s;
}

.error-item:hover,
.error-item.active {
  border-color: #409eff;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.error-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f5f7fa;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
}

.question-type {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.question-type.choice { background: #409eff; }
.question-type.fill { background: #67c23a; }
.question-type.short { background: #e6a23c; }
.question-type.coding { background: #f56c6c; }

.error-rate {
  font-weight: 600;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 4px;
  background: #f5f7fa;
}

.high-error { 
  color: #f56c6c; 
  background: #fef0f0;
}
.medium-error { 
  color: #e6a23c; 
  background: #fdf6ec;
}
.low-error { 
  color: #67c23a; 
  background: #f0f9ff;
}

.knowledge-point {
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.knowledge-point i {
  color: #409eff;
}

.error-content {
  margin-top: 16px;
}

.question-title h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.6;
}

/* 选择题选项样式 */
.question-options {
  margin: 16px 0;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 10px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
  position: relative;
}

.option-item:last-child {
  margin-bottom: 0;
}

.option-label {
  font-weight: 600;
  color: #606266;
  margin-right: 12px;
  min-width: 24px;
}

.option-text {
  flex: 1;
  color: #303133;
  line-height: 1.6;
}

.option-tag {
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  margin-left: 12px;
}

.option-tag.correct {
  background: #f0f9ff;
  color: #67c23a;
  border: 1px solid #67c23a;
}

.option-tag.wrong {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #f56c6c;
}

.correct-option {
  border-color: #67c23a;
  background: #f0f9ff;
}

.correct-option .option-label {
  color: #67c23a;
}

.wrong-option {
  border-color: #f56c6c;
  background: #fef0f0;
}

.wrong-option .option-label {
  color: #f56c6c;
}

/* 答案对比样式 */
.answer-comparison {
  margin: 16px 0;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.answer-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding: 10px;
  border-radius: 6px;
}

.answer-row:last-child {
  margin-bottom: 0;
}

.answer-label {
  font-weight: 600;
  min-width: 90px;
  font-size: 14px;
}

.wrong-label {
  color: #f56c6c;
}

.correct-label {
  color: #67c23a;
}

.answer-value {
  flex: 1;
  padding: 8px 12px;
  border-radius: 4px;
  line-height: 1.6;
}

.wrong-answer {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.correct-answer {
  background: #f0f9ff;
  color: #67c23a;
  border: 1px solid #c6e2b5;
}

/* 编程题代码对比样式 */
.code-comparison {
  margin: 16px 0;
}

.code-block {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.code-block:last-child {
  margin-bottom: 0;
}

.code-header {
  padding: 8px 16px;
  font-weight: 600;
  font-size: 13px;
  color: white;
}

.wrong-header {
  background: #f56c6c;
}

.correct-header {
  background: #67c23a;
}

.code-content {
  margin: 0;
  padding: 16px;
  background: #1e1e1e;
  color: #d4d4d4;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.6;
  overflow-x: auto;
  border: 1px solid #e4e7ed;
  border-top: none;
}

/* 错误分析样式 */
.error-analysis {
  margin-top: 16px;
  padding: 16px;
  background: #fff7e6;
  border-left: 4px solid #e6a23c;
  border-radius: 4px;
}

.analysis-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  color: #606266;
  line-height: 1.6;
}

.analysis-item:last-child {
  margin-bottom: 0;
}

.analysis-item i {
  color: #e6a23c;
  margin-right: 8px;
  margin-top: 2px;
}

.analysis-label {
  font-weight: 600;
  margin-right: 8px;
  color: #303133;
}

.analysis-value {
  flex: 1;
}

.training-config {
  text-align: center;
  padding: 30px;
}

.quick-actions {
  margin-top: 30px;
}

.quick-actions h4 {
  color: #303133;
  margin-bottom: 20px;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.action-content {
  text-align: center;
  padding: 20px;
}

.action-icon {
  font-size: 36px;
  color: #409eff;
  margin-bottom: 10px;
}

.action-content h5 {
  color: #303133;
  margin-bottom: 8px;
}

.action-content p {
  color: #909399;
  font-size: 12px;
}

.training-progress {
  padding: 20px;
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.training-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #909399;
}

.current-question {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
  border: 1px solid #ebeef5;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  color: #409eff;
}

.question-content h4 {
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.6;
}

.options {
  margin: 20px 0;
}

.option-item {
  display: block;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.fill-input,
.short-input {
  margin: 20px 0;
}

.question-actions {
  text-align: center;
  margin-top: 30px;
}

.question-actions .el-button {
  margin: 0 10px;
}

.training-result {
  padding: 20px;
  text-align: center;
}

.result-header h3 {
  color: #67c23a;
  margin-bottom: 20px;
}

.result-stats {
  margin: 30px 0;
}

.result-stat {
  text-align: center;
}

.result-stat .stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.result-stat .stat-label {
  color: #909399;
  font-size: 14px;
}

.improvement-suggestions {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  text-align: left;
}

.improvement-suggestions h4 {
  color: #409eff;
  margin-bottom: 15px;
}

.improvement-suggestions ul {
  list-style: none;
  padding: 0;
}

.improvement-suggestions li {
  padding: 5px 0;
  color: #606266;
}

.improvement-suggestions li:before {
  content: "•";
  color: #409eff;
  margin-right: 8px;
}

.result-actions {
  margin-top: 30px;
}

.result-actions .el-button {
  margin: 0 10px;
}

/* 训练弹窗样式 */
.training-dialog {
  margin-top: 5vh !important;
}

.training-dialog .el-dialog__body {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.training-content {
  padding: 10px;
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.training-info {
  display: flex;
  gap: 30px;
  font-size: 14px;
  color: #606266;
}

.training-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.training-info i {
  color: #409eff;
}

.training-progress-bar {
  margin-bottom: 30px;
}

.current-question {
  background: white;
  border-radius: 8px;
  padding: 30px;
  border: 1px solid #ebeef5;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f5f7fa;
}

.question-number {
  font-weight: bold;
  font-size: 18px;
  color: #409eff;
}

.question-content h4 {
  color: #303133;
  margin-bottom: 25px;
  line-height: 1.8;
  font-size: 16px;
}

.options {
  margin: 25px 0;
}

.options .el-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.options .option-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
  margin: 0;
  width: 100%;
}

.options .option-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.options .el-radio__input.is-checked + .el-radio__label {
  color: #409eff;
}

.fill-input,
.short-input,
.coding-input {
  margin: 25px 0;
}

.question-actions {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.question-actions .el-button {
  margin: 0 10px;
  min-width: 120px;
}

.training-result {
  padding: 20px;
  text-align: center;
}

.result-header {
  margin-bottom: 30px;
}

.result-header h3 {
  color: #67c23a;
  margin-top: 15px;
  font-size: 24px;
}

.result-stats {
  margin: 40px 0;
}

.result-stat {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.result-stat .stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.result-stat .stat-label {
  color: #909399;
  font-size: 14px;
}

.improvement-suggestions {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  padding: 25px;
  margin: 30px 0;
  text-align: left;
}

.improvement-suggestions h4 {
  color: #409eff;
  margin-bottom: 15px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.improvement-suggestions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.improvement-suggestions li {
  padding: 8px 0;
  color: #606266;
  line-height: 1.6;
}

.improvement-suggestions li:before {
  content: "•";
  color: #409eff;
  margin-right: 10px;
  font-weight: bold;
}

.result-actions {
  margin-top: 30px;
}

.result-actions .el-button {
  margin: 0 10px;
  min-width: 140px;
}

/* 原生训练模态框样式 */
.training-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.training-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
}

.training-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #ebeef5;
  background: #f5f7fa;
  border-radius: 12px 12px 0 0;
}

.training-modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #303133;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 32px;
  color: #909399;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  transition: all 0.3s;
}

.close-btn:hover {
  color: #f56c6c;
  transform: rotate(90deg);
}

.training-modal-body {
  padding: 20px 30px;
  overflow-y: auto;
  flex: 1;
}
</style> 