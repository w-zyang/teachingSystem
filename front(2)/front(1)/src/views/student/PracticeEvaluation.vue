<template>
  <div class="practice-evaluation">
    <!-- 配置页面 -->
    <div v-if="currentPage === 'config'" class="config-page">
      <!-- 课程信息展示 -->
      <el-card v-if="courseInfo.courseId" class="course-info-card">
        <template #header>
          <div class="course-header">
            <h2>{{ courseInfo.courseName }} - 练习测试</h2>
            <el-tag type="primary">{{ courseInfo.teacherName }}</el-tag>
          </div>
        </template>
        <div class="course-description">
          <p>欢迎来到 {{ courseInfo.courseName }} 的练习测试模块！</p>
          <p>这里有老师为您精心准备的练习题目，请选择您要进行的测试。</p>
        </div>
      </el-card>

      <!-- 教师发布的考试列表 -->
      <el-card v-if="teacherExams.length > 0" class="exams-card">
        <template #header>
          <div class="card-header">
            <h3>教师发布的练习测试</h3>
            <el-tag type="success">{{ teacherExams.length }} 个可用测试</el-tag>
          </div>
        </template>
        
        <div class="exams-grid">
          <div 
            v-for="exam in teacherExams" 
            :key="exam.id"
            class="exam-card"
            @click="selectExam(exam)"
          >
            <div class="exam-header">
              <h4>{{ exam.name }}</h4>
              <div class="exam-tags">
                <el-tag :type="getExamTypeTag(exam.type)">{{ getExamTypeName(exam.type) }}</el-tag>
                <el-tag type="info" size="small">教师出题</el-tag>
              </div>
            </div>
            <div class="exam-info">
              <div class="info-item">
                <i class="el-icon-time"></i>
                <span>{{ exam.duration }}分钟</span>
              </div>
              <div class="info-item">
                <i class="el-icon-trophy"></i>
                <span>{{ exam.totalScore }}分</span>
              </div>
              <div class="info-item">
                <i class="el-icon-document"></i>
                <span>{{ exam.questions ? exam.questions.length : '未知' }}题</span>
              </div>
            </div>
            <div class="exam-description">
              <p>{{ exam.description || '暂无描述' }}</p>
            </div>
            <div class="exam-actions">
              <el-button type="primary" @click.stop="startExam(exam)">
                开始测试
              </el-button>
              <el-button @click.stop="previewExam(exam)">
                预览题目
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <!-- AI智能练习（备选方案） -->
      <el-card class="practice-card">
        <template #header>
          <div class="card-header">
            <h3>AI练习评测助手</h3>
            <el-tag type="warning">智能练习</el-tag>
          </div>
        </template>
        
        <!-- 练习设置 -->
        <div class="practice-settings">
          <h3>练习设置</h3>
          <el-form :model="practiceForm" label-width="120px" class="practice-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学科类型">
                  <el-select v-model="practiceForm.subject" placeholder="请选择学科">
                    <el-option label="JavaScript编程" value="javascript" />
                    <el-option label="算法与数据结构" value="algorithms" />
                    <el-option label="Vue.js开发" value="vue" />
                    <el-option label="数据库设计" value="database" />
                    <el-option label="数学计算" value="mathematics" />
                    <el-option label="TensorFlow.js开发" value="tensorflow-js" />
                    <el-option label="机器学习基础" value="machine-learning" />
                    <el-option label="深度学习应用" value="deep-learning" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="难度等级">
                  <el-select v-model="practiceForm.difficulty" placeholder="请选择难度">
                    <el-option label="初级" value="beginner" />
                    <el-option label="中级" value="intermediate" />
                    <el-option label="高级" value="advanced" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <!-- 新增：题目类型选择 -->
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="题目类型">
                  <el-checkbox-group v-model="practiceForm.questionTypes">
                    <el-checkbox label="choice">选择题</el-checkbox>
                    <el-checkbox label="fill">填空题</el-checkbox>
                    <el-checkbox label="short">简答题</el-checkbox>
                    <el-checkbox label="coding">编程题</el-checkbox>
                    <el-checkbox label="essay">论述题</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
            </el-row>
            
            <!-- 新增：各类型题目数量设置 -->
            <el-row :gutter="20" v-if="practiceForm.questionTypes.length > 0">
              <el-col :span="24">
                <el-form-item label="题目数量分配">
                  <div class="question-type-config">
                    <div v-for="type in practiceForm.questionTypes" :key="type" class="type-config-item">
                      <span class="type-name">{{ getQuestionTypeName(type) }}</span>
                      <el-input-number 
                        v-model="practiceForm.typeConfig[type]" 
                        :min="1" 
                        :max="10"
                        size="small"
                      />
                    </div>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="总题目数量">
                  <el-input-number v-model="totalQuestionCount" :min="1" :max="50" disabled />
                  <div class="question-count-tips">
                    <el-tag size="small" type="success">自动计算：各题型数量之和</el-tag>
                    <el-tag size="small" type="info">建议：5-20题适合单次练习</el-tag>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="练习时长">
                  <el-input-number v-model="practiceForm.timeLimit" :min="5" :max="120" />
                  <span class="unit">分钟</span>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="知识点">
              <el-input
                v-model="practiceForm.topics"
                placeholder="请输入想要练习的知识点，用逗号分隔..."
              />
            </el-form-item>
            
            <el-form-item label="特殊要求">
              <el-input
                v-model="practiceForm.requirements"
                type="textarea"
                :rows="3"
                placeholder="请输入特殊练习要求..."
              />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 生成练习按钮 -->
        <div class="generate-actions">
          <el-button 
            type="primary" 
            size="large"
            @click="generatePractice"
            :loading="generating"
            :disabled="!canGenerate"
          >
            <el-icon v-if="!generating"><Edit /></el-icon>
            <el-icon v-else><Loading /></el-icon>
            {{ generating ? 'AI正在生成题目，请耐心等待...' : '生成练习题目' }}
          </el-button>
          <el-button @click="resetForm">重置设置</el-button>
          <el-button type="info" @click="testBackend">测试后端连接</el-button>
          <el-button type="warning" @click="healthCheck">健康检查</el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 练习页面 -->
    <div v-if="currentPage === 'practice'" class="practice-page">
      <el-card class="questions-card">
        <template #header>
          <div class="questions-header">
            <div class="header-title">
              <h3>📝 练习题目</h3>
              <el-tag v-if="selectedExam" type="primary" size="small">
                来自：{{ selectedExam.name }}
              </el-tag>
            </div>
            <div class="practice-info">
              <span>共 {{ practiceQuestions.length }} 题</span>
              <span>预计用时: {{ practiceForm.timeLimit }} 分钟</span>
              <el-button type="success" @click="startPractice" v-if="!practiceStarted">
                开始练习
              </el-button>
              <el-button type="danger" @click="exitPractice" v-if="!practiceStarted">
                退出练习
              </el-button>
            </div>
          </div>
        </template>
        
        <div v-if="!practiceStarted" class="questions-preview">
          <div v-for="(question, index) in practiceQuestions" :key="index" class="question-preview">
            <div class="question-header">
              <span class="question-number">第 {{ index + 1 }} 题</span>
              <span class="question-type">{{ question.type }}</span>
              <span class="question-difficulty">{{ question.difficulty }}</span>
            </div>
            <div class="question-content">
              <h4>{{ question.title }}</h4>
              <div v-if="question.options" class="question-options">
                <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option">
                  {{ option }}
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 练习进行中 -->
        <div v-else class="practice-session">
          <div class="practice-timer">
            <el-progress 
              :percentage="timerProgress" 
              :format="timerFormat"
              status="warning"
            />
            <div class="timer-text">
              剩余时间: {{ formatTime(remainingTime) }}
            </div>
          </div>
          
          <div class="current-question">
            <div class="question-navigation">
              <el-button @click="previousQuestion" :disabled="currentQuestionIndex === 0">
                上一题
              </el-button>
              <span class="question-counter">
                {{ currentQuestionIndex + 1 }} / {{ practiceQuestions.length }}
              </span>
              <el-button @click="nextQuestion" :disabled="currentQuestionIndex === practiceQuestions.length - 1">
                下一题
              </el-button>
            </div>
            
            <div class="question-display">
              <div class="question-header">
                <span class="question-type">{{ getQuestionTypeName(currentQuestion.type) }}</span>
                <span class="question-difficulty">{{ currentQuestion.difficulty }}</span>
              </div>
              <div class="question-content">
                <h4>{{ currentQuestion.title }}</h4>
                
                <!-- 选择题 -->
                <div v-if="currentQuestion.type === 'choice' && currentQuestion.options" class="question-options">
                  <el-radio-group v-model="userAnswers[currentQuestionIndex]">
                    <el-radio 
                      v-for="(option, optIndex) in currentQuestion.options" 
                      :key="optIndex"
                      :label="String.fromCharCode(65 + optIndex)"
                      class="option-radio"
                    >
                      {{ option }}
                    </el-radio>
                  </el-radio-group>
                </div>
                
                <!-- 填空题 -->
                <div v-else-if="currentQuestion.type === 'fill'" class="question-fill">
                  <div class="fill-instruction">请在空白处填入合适的答案：</div>
                  <div v-if="currentQuestion.blanks" class="fill-blanks">
                    <div v-for="(blank, blankIndex) in currentQuestion.blanks" :key="blankIndex" class="blank-item">
                      <span class="blank-label">空白{{ blankIndex + 1 }}：</span>
                      <el-input 
                        v-model="userAnswers[currentQuestionIndex][blankIndex]" 
                        placeholder="请输入答案"
                        size="large"
                        class="blank-input"
                      />
                    </div>
                  </div>
                  <div v-else class="single-fill">
                    <el-input
                      v-model="userAnswers[currentQuestionIndex]"
                      placeholder="请输入答案"
                      size="large"
                    />
                  </div>
                </div>
                
                <!-- 简答题 -->
                <div v-else-if="currentQuestion.type === 'short'" class="question-short">
                  <div class="short-instruction">请详细回答以下问题：</div>
                  <el-input
                    v-model="userAnswers[currentQuestionIndex]"
                    type="textarea"
                    :rows="8"
                    placeholder="请详细阐述你的观点和理由..."
                    show-word-limit
                    :maxlength="2000"
                  />
                  <div class="short-tips">
                    <el-alert title="答题提示" type="info" show-icon :closable="false">
                      <ul>
                        <li>请条理清晰地阐述你的观点</li>
                        <li>可以结合具体例子说明</li>
                        <li>注意逻辑性和完整性</li>
                      </ul>
                    </el-alert>
                  </div>
                </div>
                
                <!-- 编程题 -->
                <div v-else-if="currentQuestion.type === 'coding'" class="question-coding">
                  <div class="coding-layout">
                    <!-- 左侧：题目描述 -->
                    <div class="coding-left">
                      <div class="coding-section">
                        <h4 class="section-title">编程要求：</h4>
                        <div class="section-content" v-html="formatQuestionContent(currentQuestion.requirements || currentQuestion.content)"></div>
                      </div>
                      
                      <div v-if="currentQuestion.examples && currentQuestion.examples.length > 0" class="coding-section">
                        <h4 class="section-title">示例：</h4>
                        <div v-for="(example, exIndex) in currentQuestion.examples" :key="exIndex" class="example-box">
                          <div class="example-label">示例 {{ exIndex + 1 }}：</div>
                          <div class="example-io">
                            <div class="io-item">
                              <span class="io-label">输入：</span>
                              <code class="io-value">{{ example.input }}</code>
                            </div>
                            <div class="io-item">
                              <span class="io-label">输出：</span>
                              <code class="io-value output">{{ example.output }}</code>
                            </div>
                          </div>
                        </div>
                      </div>
                      
                      <div v-if="currentQuestion.hints" class="coding-section">
                        <h4 class="section-title">提示：</h4>
                        <div class="section-content">{{ currentQuestion.hints }}</div>
                      </div>
                    </div>
                    
                    <!-- 右侧：代码编辑器组件 -->
                    <div class="coding-right">
                      <!-- 调试信息 -->
                      <div v-if="currentQuestion.testCases && currentQuestion.testCases.length > 0" style="padding: 10px; background: #e7f3ff; border-radius: 4px; margin-bottom: 10px; font-size: 12px;">
                        <strong>✅ 已配置 {{ currentQuestion.testCases.length }} 个测试用例</strong>
                      </div>
                      <div v-else style="padding: 10px; background: #fff3cd; border-radius: 4px; margin-bottom: 10px; font-size: 12px;">
                        <strong>⚠️ 该题目暂未配置测试用例，无法进行自动评测</strong>
                      </div>
                      
                      <CodeEditor
                        title="代码编辑"
                        :question-id="currentQuestion.id"
                        :default-language="'50'"
                        :default-code="userAnswers[currentQuestionIndex]"
                        :test-cases="currentQuestion.testCases || []"
                        @code-change="(code) => userAnswers[currentQuestionIndex] = code"
                        @submit-success="handleCodingSubmit"
                      />
                    </div>
                  </div>
                </div>
                
                <!-- 论述题 -->
                <div v-else-if="currentQuestion.type === 'essay'" class="question-essay">
                  <div class="essay-instruction">请深入分析并论述以下问题：</div>
                  <el-input
                    v-model="userAnswers[currentQuestionIndex]"
                    type="textarea"
                    :rows="12"
                    placeholder="请结合理论知识和实际情况，深入分析问题..."
                    show-word-limit
                    :maxlength="5000"
                  />
                  <div class="essay-tips">
                    <el-alert title="论述要求" type="warning" show-icon :closable="false">
                      <ul>
                        <li>观点明确，论证充分</li>
                        <li>结合理论知识和实践案例</li>
                        <li>逻辑清晰，层次分明</li>
                        <li>语言表达准确，文字流畅</li>
                      </ul>
                    </el-alert>
                  </div>
                </div>
                
                <!-- 默认文本输入 -->
                <div v-else class="question-textarea">
                  <el-input
                    v-model="userAnswers[currentQuestionIndex]"
                    type="textarea"
                    :rows="6"
                    placeholder="请输入你的答案..."
                  />
                </div>
              </div>
            </div>
            
            <div class="question-actions">
              <el-button @click="submitPractice" type="primary">
                提交练习
              </el-button>
              <el-button @click="pausePractice">暂停</el-button>
              <el-button type="danger" @click="exitPractice">退出练习</el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 结果页面 -->
    <div v-if="currentPage === 'result'" class="result-page">
      <el-card class="result-card">
        <template #header>
          <h3>练习结果</h3>
        </template>
        
        <div class="result-summary">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="result-stat">
                <div class="stat-number">{{ practiceResult.score }}</div>
                <div class="stat-label">总分</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="result-stat">
                <div class="stat-number">{{ practiceResult.correctCount }}</div>
                <div class="stat-label">正确题数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="result-stat">
                <div class="stat-number">{{ practiceResult.accuracy }}%</div>
                <div class="stat-label">正确率</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="result-stat">
                <div class="stat-number">{{ practiceResult.timeUsed }}</div>
                <div class="stat-label">用时(分钟)</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="result-details">
          <h4>详细分析</h4>
          <div v-for="(analysis, index) in practiceResult.analysis" :key="index" class="analysis-item">
            <div class="analysis-header">
              <h5>第{{ index + 1 }}题：{{ analysis.title }}</h5>
              <div class="analysis-status">
                <el-tag :type="analysis.isCorrect ? 'success' : 'danger'" size="small">
                  {{ analysis.isCorrect ? '正确' : '错误' }}
                </el-tag>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="generateReport(index)"
                  :loading="analysis.isTriggering"
                  style="margin-left: 10px;"
                >
                  生成分析报告
                </el-button>
              </div>
            </div>
            <div class="analysis-content">
              <p><strong>你的答案：</strong>{{ analysis.userAnswer || '未作答' }}</p>
              <p><strong>正确答案：</strong>{{ analysis.correctAnswer }}</p>
              <p v-if="analysis.explanation"><strong>题目解析：</strong>{{ analysis.explanation }}</p>
              
              <!-- 编程题特殊显示 -->
              <div v-if="analysis.codingScore !== undefined" class="coding-result">
                <p><strong>测试用例通过率：</strong>{{ analysis.codingScore }}%</p>
                <el-progress 
                  :percentage="analysis.codingScore" 
                  :color="analysis.codingScore === 100 ? '#67c23a' : '#e6a23c'"
                />
              </div>
            </div>
          </div>
        </div>
        
        <!-- 弹窗显示AI分析报告 -->
        <el-dialog 
          v-model="reportDialogVisible" 
          title="AI分析报告" 
          width="70%"
          :before-close="closeReportDialog"
        >
          <div v-if="currentReport" class="detail-content">
            <div class="detail-header">
              <h4>{{ currentReport.title }}</h4>
            </div>
            <div class="detail-analysis">
              <h5>详细分析：</h5>
              <div class="analysis-text" v-html="formatAnalysis(currentReport.detailedAnalysis)"></div>
            </div>
            <div class="detail-suggestion">
              <h5>学习建议：</h5>
              <div class="suggestion-text">{{ currentReport.suggestion }}</div>
            </div>
          </div>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="closeReportDialog">关闭</el-button>
            </span>
          </template>
        </el-dialog>
        
        <div class="result-actions">
          <el-button type="primary" @click="retryPractice">重新练习</el-button>
          <el-button @click="viewAnswers">查看答案</el-button>
          <el-button @click="exportResult">导出结果</el-button>
          <el-button type="info" @click="currentPage = 'config'">返回首页</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onUnmounted, nextTick, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Loading, CaretRight } from '@element-plus/icons-vue'
import { aiAPI } from '@/api/ai'
import { getPublishedExamsByCourseId, getExamById } from '@/api/exam'
import { batchRecordErrorQuestions } from '@/api/errorQuestions'
import axios from 'axios'
import CodeEditor from '@/components/CodeEditor.vue'

export default {
  name: 'PracticeEvaluation',
  components: {
    Edit,
    Loading,
    CaretRight,
    CodeEditor
  },
  setup() {
    // 获取路由信息
    const route = useRoute()
    
    // 页面状态：'config' | 'practice' | 'result'
    const currentPage = ref('config')
    
    // 课程信息
    const courseInfo = reactive({
      courseId: route.query.courseId || null,
      courseName: route.query.courseName || '',
      teacherName: route.query.teacherName || ''
    })
    
    // 教师发布的考试列表
    const teacherExams = ref([])
    const loadingExams = ref(false)
    const selectedExam = ref(null)
    
    const practiceForm = reactive({
      subject: '',
      difficulty: 'intermediate',
      questionTypes: ['choice'], // 默认选择题
      typeConfig: { choice: 2, fill: 1, short: 1, coding: 1, essay: 1 }, // 默认配置
      timeLimit: 30,
      topics: '',
      requirements: ''
    })
    
    const generating = ref(false)
    const practiceQuestions = ref([])
    const practiceStarted = ref(false)
    const currentQuestionIndex = ref(0)
    const userAnswers = ref([])
    const practiceResult = ref(null)
    const remainingTime = ref(0)
    const timerInterval = ref(null)
    const codeResults = ref({})
    const reportDialogVisible = ref(false)
    const currentReport = ref(null)
    
    // 计算总题目数量（各题型数量之和）
    const totalQuestionCount = computed(() => {
      if (practiceForm.questionTypes.length === 0) {
        return 0
      }
      return practiceForm.questionTypes.reduce((sum, type) => {
        return sum + (practiceForm.typeConfig[type] || 0)
      }, 0)
    })
    
    // 计算是否可以生成
    const canGenerate = computed(() => {
      return practiceForm.subject && totalQuestionCount.value > 0
    })
    
    // 当前题目
    const currentQuestion = computed(() => {
      return practiceQuestions.value[currentQuestionIndex.value] || {}
    })
    
    // 计时器进度
    const timerProgress = computed(() => {
      const totalTime = practiceForm.timeLimit * 60
      return ((totalTime - remainingTime.value) / totalTime) * 100
    })
    
    // 生成练习题目
    const generatePractice = async () => {
      if (!canGenerate.value) {
        ElMessage.warning('请完善练习设置')
        return
      }
      
      generating.value = true
      
      // 显示详细的加载提示
      ElMessage({
        message: 'AI正在生成练习题目，预计需要1-3分钟，请耐心等待...',
        type: 'info',
        duration: 0, // 不自动关闭
        showClose: true
      })
      
      try {
        const requestData = {
          topic: practiceForm.subject,
          difficulty: practiceForm.difficulty,
          count: totalQuestionCount.value,
          topics: practiceForm.topics,
          requirements: practiceForm.requirements,
          questionTypes: practiceForm.questionTypes,
          typeConfig: practiceForm.typeConfig
        }
        
        console.log('发送生成练习题目请求:', requestData)
        const response = await aiAPI.generatePractice(requestData)
        console.log('收到生成练习题目响应:', response)
        
        if (response.success === true && response.data && response.data.questions) {
          // 处理题目数据：将 examples 转换为 testCases
          practiceQuestions.value = response.data.questions.map(q => {
            // 处理编程题的测试用例
            if (q.type === 'coding') {
              let testCases = q.testCases || []
              if (testCases.length === 0 && q.examples && Array.isArray(q.examples)) {
                console.log(`AI生成的编程题 "${q.title}" 从 examples 转换测试用例:`, q.examples)
                testCases = q.examples.map(ex => ({
                  input: ex.input,
                  output: ex.output
                }))
              }
              console.log(`编程题 "${q.title}" 测试用例数量:`, testCases.length)
              
              return {
                ...q,
                examples: q.examples || [], // 保留示例用于显示
                testCases: testCases, // 用于评测
                requirements: q.requirements || q.content || q.title, // 编程要求
                hints: q.hints || '' // 提示
              }
            }
            return q
          })
          
          // 根据题目类型初始化答案数组
          userAnswers.value = practiceQuestions.value.map(q => {
            if (q.type === 'fill' && q.blanks && Array.isArray(q.blanks)) {
              return Array(q.blanks.length).fill('')
            }
            return ''
          })
          
          // 保存到 LocalStorage
          savePracticeData()
          
          // 关闭加载提示并显示成功消息
          ElMessage.closeAll()
          ElMessage.success(`练习题目生成成功！共${practiceQuestions.value.length}道题目`)
          
          // 切换到练习页面
          currentPage.value = 'practice'
        } else {
          console.error('响应格式错误:', response)
          throw new Error(response.msg || '生成失败：响应格式错误')
        }
      } catch (error) {
        console.error('生成练习题目失败:', error)
        
        // 关闭加载提示
        ElMessage.closeAll()
        
        let errorMsg = '生成失败，请稍后重试'
        
        // 针对不同错误类型提供更友好的提示
        if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
          errorMsg = 'AI生成超时，这通常是因为题目生成需要较长时间。请稍后重试，或尝试减少题目数量。'
        } else if (error.response) {
          const status = error.response.status
          const data = error.response.data
          
          if (status === 429) {
            errorMsg = 'AI服务繁忙，请稍后重试'
          } else if (status >= 500) {
            errorMsg = 'AI服务暂时不可用，请稍后重试'
          } else {
            errorMsg = `生成失败：${data?.message || data?.msg || error.response.statusText}`
          }
        } else if (error.request) {
          errorMsg = 'AI服务连接失败，请检查网络连接或稍后重试'
        } else if (error.message) {
          errorMsg = `生成失败：${error.message}`
        }
        
        ElMessage.error({
          message: errorMsg,
          duration: 8000, // 延长显示时间
          showClose: true
        })
      } finally {
        generating.value = false
      }
    }
    
    // 重置表单
    const resetForm = () => {
      Object.assign(practiceForm, {
        subject: '',
        difficulty: 'intermediate',
        questionTypes: ['choice'],
        typeConfig: { choice: 2, fill: 1, short: 1, coding: 1, essay: 1 },
        timeLimit: 30,
        topics: '',
        requirements: ''
      })
      practiceQuestions.value = []
      practiceStarted.value = false
      practiceResult.value = null
      codeResults.value = {}
      currentPage.value = 'config' // 返回配置页面
      clearPracticeData() // 清除保存的数据
      ElMessage.success('设置已重置')
    }
    
    // 开始练习
    const startPractice = () => {
      practiceStarted.value = true
      currentQuestionIndex.value = 0
      remainingTime.value = practiceForm.timeLimit * 60
      startTimer()
      savePracticeData() // 保存状态
    }
    
    // 开始计时器
    const startTimer = () => {
      // 只在第一次开始时保存时间戳
      const existingStartTime = localStorage.getItem('practiceStartTime')
      if (!existingStartTime) {
        const startTimestamp = Date.now()
        localStorage.setItem('practiceStartTime', startTimestamp.toString())
        console.log('首次开始练习,记录开始时间:', new Date(startTimestamp).toLocaleString())
      } else {
        console.log('恢复练习,使用已有开始时间:', new Date(parseInt(existingStartTime)).toLocaleString())
      }
      
      timerInterval.value = setInterval(() => {
        if (remainingTime.value > 0) {
          remainingTime.value--
        } else {
          clearInterval(timerInterval.value)
          submitPractice()
        }
      }, 1000)
    }
    
    // 停止计时器
    const stopTimer = () => {
      if (timerInterval.value) {
        clearInterval(timerInterval.value)
        timerInterval.value = null
      }
    }
    
    // 格式化时间
    const formatTime = (seconds) => {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
    }
    
    // 计时器格式化
    const timerFormat = (percentage) => {
      return formatTime(remainingTime.value)
    }
    
    // 上一题
    const previousQuestion = () => {
      if (currentQuestionIndex.value > 0) {
        currentQuestionIndex.value--
        savePracticeData() // 保存当前进度
      }
    }
    
    // 下一题
    const nextQuestion = () => {
      if (currentQuestionIndex.value < practiceQuestions.value.length - 1) {
        currentQuestionIndex.value++
        savePracticeData() // 保存当前进度
      }
    }
    
    // 获取题目类型名称
    const getQuestionTypeName = (type) => {
      const typeMap = {
        choice: '选择题',
        fill: '填空题',
        short: '简答题',
        coding: '编程题',
        essay: '论述题'
      }
      return typeMap[type] || '未知类型'
    }
    
    // 运行代码（模拟）
    const runCode = (questionIndex) => {
      const code = userAnswers.value[questionIndex]
      if (!code || !code.trim()) {
        ElMessage.warning('请先输入代码')
        return
      }
      
      // 简单的代码检查和模拟运行
      try {
        // 这里可以集成在线代码执行服务，现在先做模拟
        let result = '代码运行成功！\n'
        
        // 模拟不同编程语言的运行结果
        if (code.includes('console.log')) {
          result += '输出: Hello World'
        } else if (code.includes('function') || code.includes('=>')) {
          result += '函数定义成功'
        } else if (code.includes('class')) {
          result += '类定义成功'
        } else {
          result += '代码执行完毕'
        }
        
        codeResults.value[questionIndex] = result
        ElMessage.success('代码运行完成')
      } catch (error) {
        codeResults.value[questionIndex] = '运行错误: ' + error.message
        ElMessage.error('代码运行失败')
      }
    }
    
    // 处理编程题提交结果
    const handleCodingSubmit = (result) => {
      console.log('编程题提交结果:', result)
      
      if (result.passed) {
        ElMessage.success(`恭喜！所有测试用例通过，得分：${result.score}`)
      } else {
        ElMessage.warning(`部分测试用例未通过，得分：${result.score}`)
      }
      
      // 可以在这里保存编程题的得分
      codeResults.value[currentQuestionIndex.value] = result
    }
    
    // 重置代码
    const resetCode = (questionIndex) => {
      userAnswers.value[questionIndex] = ''
      delete codeResults.value[questionIndex]
      ElMessage.success('代码已重置')
    }
    
    // 提交练习
    const submitPractice = async () => {
      stopTimer()
      
      // 清除开始时间戳,因为练习已结束
      localStorage.removeItem('practiceStartTime')
      
      // 检查是否有编程题未提交
      const hasUnsubmittedCoding = practiceQuestions.value.some((q, idx) => {
        return q.type === 'coding' && !codeResults.value[idx]
      })
      
      if (hasUnsubmittedCoding) {
        ElMessageBox.confirm(
          '检测到有编程题尚未提交评测，是否继续提交练习？未评测的编程题将得0分。',
          '提示',
          {
            confirmButtonText: '继续提交',
            cancelButtonText: '返回检查',
            type: 'warning'
          }
        ).then(() => {
          doSubmitPractice()
        }).catch(() => {
          // 用户取消，恢复计时器
          startTimer()
        })
      } else {
        doSubmitPractice()
      }
    }
    
    // 执行提交练习
    const doSubmitPractice = async () => {
      try {
        // 处理多题型答案
        const answers = practiceQuestions.value.map((q, idx) => {
          if (q.type === 'fill' && Array.isArray(userAnswers.value[idx])) {
            return userAnswers.value[idx].join('；')
          }
          return userAnswers.value[idx]
        })
        const requestData = {
          questions: practiceQuestions.value,
          answers,
          topic: practiceForm.subject,
          timeUsed: Math.round((practiceForm.timeLimit * 60 - remainingTime.value) / 60)
        }
        console.log('发送练习评测请求:', requestData)
        const response = await aiAPI.practiceEvaluation(requestData)
        console.log('收到练习评测响应:', response)
        if (response.success === true && response.data) {
          practiceResult.value = response.data
          
          // 合并编程题的评测结果
          practiceResult.value.analysis = practiceResult.value.analysis.map((item, idx) => {
            const question = practiceQuestions.value[idx]
            if (question.type === 'coding' && codeResults.value[idx]) {
              // 使用编程题的实际评测结果
              const codingResult = codeResults.value[idx]
              return {
                ...item,
                isCorrect: codingResult.passed || false,
                userAnswer: userAnswers.value[idx],
                correctAnswer: '通过所有测试用例',
                explanation: `测试用例通过率: ${codingResult.score}%`,
                codingScore: codingResult.score
              }
            }
            return item
          })
          
          // 重新计算总分（考虑编程题得分）
          let totalScore = 0
          let correctCount = 0
          practiceResult.value.analysis.forEach((item, idx) => {
            const question = practiceQuestions.value[idx]
            if (question.type === 'coding' && item.codingScore !== undefined) {
              // 编程题按测试用例通过率计分
              totalScore += (item.codingScore / 100) * (question.score || 10)
              if (item.codingScore === 100) correctCount++
            } else {
              // 其他题型
              if (item.isCorrect) {
                totalScore += (question.score || 10)
                correctCount++
              }
            }
          })
          
          practiceResult.value.score = Math.round(totalScore)
          practiceResult.value.correctCount = correctCount
          practiceResult.value.accuracy = Math.round((correctCount / practiceQuestions.value.length) * 100)
          
          // 记录错题
          await recordErrorQuestions(practiceResult.value.analysis)
          
          savePracticeData() // 保存结果
          ElMessage.success('练习评测完成！')
          
          // 切换到结果页面
          currentPage.value = 'result'
          
          // 启动AI分析状态检查
          startStatusCheck()
        } else {
          throw new Error(response.msg || '评测失败：响应格式错误')
        }
      } catch (error) {
        console.error('练习评测失败:', error)
        
        // 如果后端评测失败，使用前端简单评测作为备选
        ElMessage.warning('智能评测失败，使用基础评测')
        
        let correctCount = 0
        let totalScore = 0
        let analysis = []
        const questions = practiceQuestions.value
        const answers = userAnswers.value
        
        questions.forEach((q, idx) => {
          // 编程题特殊处理
          if (q.type === 'coding') {
            const codingResult = codeResults.value[idx]
            if (codingResult) {
              const score = (codingResult.score / 100) * (q.score || 10)
              totalScore += score
              if (codingResult.passed) correctCount++
              
              analysis.push({
                title: q.title,
                userAnswer: answers[idx],
                correctAnswer: '通过所有测试用例',
                isCorrect: codingResult.passed,
                explanation: `测试用例通过率: ${codingResult.score}%`,
                detailedAnalysis: `您的代码通过了 ${codingResult.results?.filter(r => r.passed).length || 0} / ${codingResult.results?.length || 0} 个测试用例`,
                suggestion: codingResult.passed ? '代码完全正确！' : '部分测试用例未通过，请检查代码逻辑',
                codingScore: codingResult.score
              })
            } else {
              // 未提交的编程题
              analysis.push({
                title: q.title,
                userAnswer: '未提交',
                correctAnswer: '通过所有测试用例',
                isCorrect: false,
                explanation: '未提交代码评测',
                detailedAnalysis: '您没有提交代码进行评测',
                suggestion: '请编写代码并点击"提交代码"按钮进行评测',
                codingScore: 0
              })
            }
            return
          }
          
          // 其他题型的评测逻辑
          const userAns = (answers[idx] || '').toString().trim()
          let stdAns = ''
          let isCorrect = false
          
          // 根据题目类型进行不同的答案匹配
          switch (q.type) {
            case 'choice':
              // 选择题：直接比较选项字母
              stdAns = (q.answer || '').toString().trim().toUpperCase()
              const userChoice = userAns.toUpperCase()
              isCorrect = userChoice === stdAns
              break
              
            case 'fill':
              // 填空题：比较答案内容
              stdAns = (q.answer || '').toString().trim()
              isCorrect = userAns.toLowerCase() === stdAns.toLowerCase()
              break
              
            case 'short':
              // 简答题：检查关键词匹配
              stdAns = (q.answer || q.referenceAnswer || '').toString().trim()
              isCorrect = checkShortAnswerSimilarity(userAns, stdAns)
              break
              
            case 'essay':
              // 论述题：检查内容完整性和关键词
              stdAns = (q.answer || '').toString().trim()
              isCorrect = checkEssayAnswerSimilarity(userAns, stdAns)
              break
              
            default:
              // 默认情况：直接比较
              stdAns = (q.answer || '').toString().trim()
              isCorrect = userAns.toLowerCase() === stdAns.toLowerCase()
          }
          
          if (isCorrect) {
            correctCount++
            totalScore += (q.score || 10)
          }
          
          analysis.push({
            title: q.title,
            userAnswer: userAns,
            correctAnswer: stdAns,
            isCorrect,
            explanation: q.explanation || '',
            detailedAnalysis: generateBasicAnalysis(q, userAns, stdAns, isCorrect, q.type),
            suggestion: generateBasicSuggestion(isCorrect, q.type, practiceForm.subject)
          })
        })
        
        const accuracy = questions.length > 0 ? Math.round((correctCount / questions.length) * 100) : 0
        
        practiceResult.value = {
          score: Math.round(totalScore),
          correctCount,
          accuracy,
          timeUsed: Math.round((practiceForm.timeLimit * 60 - remainingTime.value) / 60),
          analysis
        }
        
        // 记录错题
        await recordErrorQuestions(analysis)
        
        savePracticeData() // 保存结果
        ElMessage.success('练习提交成功！')
        
        // 切换到结果页面
        currentPage.value = 'result'
      }
    }
    
    // 记录错题到系统
    const recordErrorQuestions = async (analysisData) => {
      try {
        const studentId = localStorage.getItem('userId') || 17
        
        // 筛选出错误的题目
        const errorQuestions = []
        
        analysisData.forEach((analysis, index) => {
          if (!analysis.isCorrect) {
            const question = practiceQuestions.value[index]
            
            errorQuestions.push({
              questionId: question.id || `temp_${Date.now()}_${index}`,
              questionType: question.type,
              questionContent: question.title || question.content,
              options: question.options ? question.options.map((opt, idx) => ({
                key: String.fromCharCode(65 + idx),
                content: opt
              })) : null,
              userAnswer: analysis.userAnswer,
              correctAnswer: analysis.correctAnswer,
              knowledgePoint: practiceForm.topics || practiceForm.subject || '未分类',
              errorType: getErrorType(question.type, analysis.userAnswer, analysis.correctAnswer),
              errorReason: analysis.explanation || '答案错误',
              errorRate: 100 // 初始错误率
            })
          }
        })
        
        if (errorQuestions.length > 0) {
          console.log('准备记录错题:', errorQuestions)
          
          const response = await batchRecordErrorQuestions({
            studentId: parseInt(studentId),
            errorQuestions: errorQuestions,
            source: selectedExam.value ? 'exam' : 'practice'
          })
          
          if (response.success) {
            console.log(`成功记录 ${errorQuestions.length} 道错题`)
            ElMessage.success(`已记录 ${errorQuestions.length} 道错题到错题本`)
          } else {
            console.warn('记录错题失败:', response.msg)
          }
        } else {
          console.log('没有错题需要记录')
        }
      } catch (error) {
        console.error('记录错题失败:', error)
        // 不影响主流程，静默失败
      }
    }
    
    // 根据题目类型和答案判断错误类型
    const getErrorType = (questionType, userAnswer, correctAnswer) => {
      if (!userAnswer || userAnswer.trim() === '') {
        return '未作答'
      }
      
      switch (questionType) {
        case 'choice':
          return '选项错误'
        case 'fill':
          return '填空错误'
        case 'short':
          return '理解不完整'
        case 'coding':
          return '代码实现错误'
        case 'essay':
          return '论述不充分'
        default:
          return '答案错误'
      }
    }
    
    // 生成基础分析（前端备选）
    const generateBasicAnalysis = (question, userAnswer, correctAnswer, isCorrect, questionType) => {
      let analysis = isCorrect ? 
        '✅ 回答正确！你的答案完全正确，说明你已经很好地掌握了相关知识点。' :
        `❌ 回答错误。你的答案：${userAnswer}，正确答案：${correctAnswer}。建议复习相关知识点。`
      
      analysis += '\n\n### 知识点梳理\n本题涉及的核心知识点：\n- 基础概念理解\n- 应用场景分析\n- 技术原理掌握'
      
      analysis += '\n\n### 解题思路\n1. 仔细阅读题目，理解问题要求\n2. 分析题目涉及的知识点\n3. 运用相关知识进行推理\n4. 验证答案的合理性'
      
      return analysis
    }
    
    // 生成基础学习建议（前端备选）
    const generateBasicSuggestion = (isCorrect, questionType, topic) => {
      if (isCorrect) {
        return `✅ 回答正确！你对${topic}的${getQuestionTypeName(questionType)}掌握得很好。建议尝试更难的题目，或者学习相关的进阶知识。`
      } else {
        return `❌ 回答有误，建议重新学习${topic}的基础概念，多做类似练习。`
      }
    }
    
    // 格式化题目内容 - 将一大段文字解析成结构化的HTML
    const formatQuestionContent = (content) => {
      if (!content) return ''
      
      // 如果已经包含HTML标签，直接返回
      if (content.includes('<div>') || content.includes('<p>')) {
        return content
      }
      
      let formatted = content
      
      // 识别并格式化各个部分
      const sections = [
        { key: '题目描述', pattern: /题目描述[：:]([\s\S]*?)(?=输入格式|输出格式|示例|提示|$)/i },
        { key: '输入格式', pattern: /输入格式[：:]([\s\S]*?)(?=输出格式|示例|提示|$)/i },
        { key: '输出格式', pattern: /输出格式[：:]([\s\S]*?)(?=示例|提示|$)/i },
        { key: '示例', pattern: /示例[：:]?([\s\S]*?)(?=提示|$)/i },
        { key: '提示', pattern: /提示[：:]([\s\S]*?)$/i }
      ]
      
      let hasStructure = false
      let result = '<div class="formatted-question">'
      
      sections.forEach(section => {
        const match = content.match(section.pattern)
        if (match && match[1]) {
          hasStructure = true
          const sectionContent = match[1].trim()
          result += `
            <div class="question-section">
              <h4 class="section-title">${section.key}：</h4>
              <div class="section-content">${sectionContent.replace(/\n/g, '<br>')}</div>
            </div>
          `
        }
      })
      
      result += '</div>'
      
      // 如果识别到了结构，返回格式化后的内容
      if (hasStructure) {
        return result
      }
      
      // 否则，简单地将换行符转换为<br>
      return formatted.replace(/\n\n/g, '<br><br>').replace(/\n/g, '<br>')
    }
    
    // 格式化分析文本
    const formatAnalysis = (text) => {
      if (!text) return ''
      
      // 将markdown格式转换为HTML
      return text
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/### (.*?)\n/g, '<h3>$1</h3>')
        .replace(/## (.*?)\n/g, '<h2>$1</h2>')
        .replace(/# (.*?)\n/g, '<h1>$1</h1>')
        .replace(/\n/g, '<br>')
        .replace(/✅/g, '<span style="color: #67c23a;">✅</span>')
        .replace(/❌/g, '<span style="color: #f56c6c;">❌</span>')
    }
    
    // 检查简答题答案相似度
    const checkShortAnswerSimilarity = (userAnswer, correctAnswer) => {
      if (!userAnswer || !correctAnswer) return false
      
      // 提取关键词进行比较
      const userKeywords = extractKeywords(userAnswer)
      const correctKeywords = extractKeywords(correctAnswer)
      
      // 计算关键词匹配度
      const matchCount = userKeywords.filter(keyword => 
        correctKeywords.includes(keyword)
      ).length
      
      const similarity = matchCount / Math.max(userKeywords.length, correctKeywords.length)
      return similarity >= 0.3 // 30%的关键词匹配即认为正确
    }
    
    // 检查编程题答案相似度
    const checkCodingAnswerSimilarity = (userAnswer, correctAnswer) => {
      if (!userAnswer || !correctAnswer) return false
      
      // 检查代码结构关键词
      const codeKeywords = ['function', 'class', 'const', 'let', 'var', 'if', 'for', 'while', 'return', 'async', 'await']
      const userHasKeywords = codeKeywords.some(keyword => 
        userAnswer.toLowerCase().includes(keyword)
      )
      
      // 检查基本语法结构
      const hasBasicStructure = userAnswer.includes('{') && userAnswer.includes('}')
      
      // 检查长度合理性（至少有一定内容）
      const hasReasonableLength = userAnswer.length >= 20
      
      return userHasKeywords && hasBasicStructure && hasReasonableLength
    }
    
    // 检查论述题答案相似度
    const checkEssayAnswerSimilarity = (userAnswer, correctAnswer) => {
      if (!userAnswer || !correctAnswer) return false
      
      // 检查内容长度（论述题应该有足够的内容）
      const hasReasonableLength = userAnswer.length >= 50
      
      // 提取关键词进行比较
      const userKeywords = extractKeywords(userAnswer)
      const correctKeywords = extractKeywords(correctAnswer)
      
      // 计算关键词匹配度
      const matchCount = userKeywords.filter(keyword => 
        correctKeywords.includes(keyword)
      ).length
      
      const similarity = matchCount / Math.max(userKeywords.length, correctKeywords.length)
      
      return hasReasonableLength && similarity >= 0.2 // 20%的关键词匹配即认为正确
    }
    
    // 提取关键词的辅助函数
    const extractKeywords = (text) => {
      // 简单的关键词提取：去除常见停用词，保留有意义的词汇
      const stopWords = ['的', '是', '在', '有', '和', '与', '或', '但', '而', '如果', '因为', '所以', 'the', 'is', 'are', 'in', 'on', 'at', 'and', 'or', 'but', 'if', 'because', 'so']
      
      return text.toLowerCase()
        .replace(/[^\w\s\u4e00-\u9fa5]/g, ' ') // 保留中英文和数字
        .split(/\s+/)
        .filter(word => word.length > 1 && !stopWords.includes(word))
        .slice(0, 10) // 取前10个关键词
    }
    
    // 暂停练习
    const pausePractice = () => {
      stopTimer()
      ElMessage.info('练习已暂停')
    }
    
    // 退出练习
    const exitPractice = () => {
      ElMessageBox.confirm(
        '确定要退出当前练习吗？退出后将清除所有练习数据。',
        '确认退出',
        {
          confirmButtonText: '确定退出',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 停止计时器
        stopTimer()
        // 清除所有练习相关数据
        practiceQuestions.value = []
        practiceStarted.value = false
        currentQuestionIndex.value = 0
        userAnswers.value = []
        practiceResult.value = null
        remainingTime.value = 0
        codeResults.value = {}
        selectedExam.value = null
        // 返回配置页面
        currentPage.value = 'config'
        // 清除 LocalStorage 中的数据
        clearPracticeData()
        ElMessage.success('已退出练习')
      }).catch(() => {
        // 用户取消退出
      })
    }
    
    // 重新练习
    const retryPractice = () => {
      practiceResult.value = null
      practiceStarted.value = false
      currentQuestionIndex.value = 0
      userAnswers.value = new Array(practiceQuestions.value.length).fill('')
      // 返回练习页面
      currentPage.value = 'practice'
      ElMessage.success('可以重新开始练习')
    }
    
    // 查看答案
    const viewAnswers = () => {
      ElMessage.info('查看答案功能开发中...')
    }
    
    // 导出结果
    const exportResult = () => {
      ElMessage.info('导出功能开发中...')
    }
    
    // 测试后端连接
    const testBackend = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/ai/test')
        const data = await response.json()
        console.log('后端测试响应:', data)
        ElMessage.success('后端连接正常')
      } catch (error) {
        console.error('后端连接测试失败:', error)
        ElMessage.error('后端连接失败')
      }
    }
    
    // 健康检查
    const healthCheck = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/ai/health')
        const data = await response.json()
        console.log('健康检查响应:', data)
        
        if (data.success && data.data) {
          const health = data.data
          let message = '系统状态正常'
          if (health.aiTest === 'failed') {
            message = `AI服务异常：${health.aiError}`
          }
          ElMessage.success(message)
        } else {
          ElMessage.error('健康检查失败')
        }
      } catch (error) {
        console.error('健康检查失败:', error)
        ElMessage.error('健康检查失败')
      }
    }
    
    // 生成分析报告
    const generateReport = async (index) => {
      if (!practiceResult.value || !practiceResult.value.analysis[index]) {
        ElMessage.error('未找到题目数据')
        return
      }
      
      const analysisItem = practiceResult.value.analysis[index]
      const question = practiceQuestions.value[index]
      
      // 设置加载状态
      analysisItem.isTriggering = true
      
      try {
        const requestData = {
          question,
          userAnswer: analysisItem.userAnswer,
          correctAnswer: analysisItem.correctAnswer,
          isCorrect: analysisItem.isCorrect,
          topic: practiceForm.subject
        }
        
        const response = await aiAPI.triggerAIAnalysis(requestData)
        if (response.success && response.data) {
          // 更新分析结果
          analysisItem.detailedAnalysis = response.data.detailedAnalysis
          analysisItem.suggestion = response.data.suggestion
          analysisItem.needsAIAnalysis = false
          ElMessage.success('AI分析生成成功！')
          
          // 设置弹窗数据
          currentReport.value = {
            title: question.title,
            detailedAnalysis: response.data.detailedAnalysis,
            suggestion: response.data.suggestion
          }
          
          // 显示弹窗
          reportDialogVisible.value = true
        } else {
          throw new Error(response.msg || 'AI分析生成失败')
        }
      } catch (error) {
        console.error('生成分析报告失败:', error)
        ElMessage.error('生成分析报告失败: ' + error.message)
      } finally {
        analysisItem.isTriggering = false
      }
    }
    
    // 关闭弹窗
    const closeReportDialog = () => {
      currentReport.value = null
      reportDialogVisible.value = false
    }
    
    // 检查AI分析状态
    const checkAnalysisStatus = async () => {
      if (!practiceResult.value || !practiceResult.value.analysis) {
        return
      }
      
      try {
        const response = await aiAPI.checkAnalysisStatus({
          analysis: practiceResult.value.analysis
        })
        
        if (response.success && response.data) {
          const { statusList, allCompleted } = response.data
          
          // 更新分析状态
          statusList.forEach(status => {
            const index = status.index
            if (practiceResult.value.analysis[index]) {
              practiceResult.value.analysis[index].needsAIAnalysis = status.needsAIAnalysis
            }
          })
          
          // 如果所有分析都完成了，显示提示
          if (allCompleted) {
            ElMessage.success('所有AI分析已完成！')
          }
        }
      } catch (error) {
        console.error('检查AI分析状态失败:', error)
      }
    }
    
    // 定期检查AI分析状态
    let statusCheckInterval = null
    
    const startStatusCheck = () => {
      if (statusCheckInterval) {
        clearInterval(statusCheckInterval)
      }
      
      statusCheckInterval = setInterval(() => {
        checkAnalysisStatus()
      }, 5000) // 每5秒检查一次
    }
    
    const stopStatusCheck = () => {
      if (statusCheckInterval) {
        clearInterval(statusCheckInterval)
        statusCheckInterval = null
      }
    }
    
    // 获取教师发布的考试
    const fetchTeacherExams = async () => {
      if (!courseInfo.courseId) {
        console.log('没有课程ID，跳过获取教师考试')
        return
      }
      
      try {
        loadingExams.value = true
        console.log('获取课程已发布考试列表，课程ID:', courseInfo.courseId)
        
        // 使用新的API获取已发布的考试
        const response = await getPublishedExamsByCourseId(courseInfo.courseId)
        console.log('获取已发布考试响应:', response)
        
        if (response && response.success) {
          const exams = response.data || []
          console.log('成功获取已发布考试:', exams)
          
          // 为每个考试加载题目详情
          const examsWithQuestions = []
          for (const exam of exams) {
            try {
              const examDetail = await getExamById(exam.id)
              if (examDetail && examDetail.success && examDetail.data) {
                examsWithQuestions.push(examDetail.data)
              } else {
                examsWithQuestions.push(exam)
              }
            } catch (error) {
              console.error(`获取考试${exam.id}详情失败:`, error)
              examsWithQuestions.push(exam)
            }
          }
          
          teacherExams.value = examsWithQuestions
          console.log('加载题目详情后的考试列表:', teacherExams.value)
        } else {
          console.log('API调用成功但数据为空')
          teacherExams.value = []
        }
      } catch (error) {
        console.error('获取已发布考试失败:', error)
        // 提供示例数据 - 模拟教师端生成的考试
        teacherExams.value = [
          {
            id: 1,
            name: '数据结构与算法基础测试',
            type: 'quiz',
            duration: 40,
            totalScore: 100,
            description: '张教授精心设计的数据结构基础概念测试，包含栈、队列、链表等重要知识点',
            status: 'published',
            createTime: '2024-12-19 10:00:00',
            questions: [
              {
                id: 1,
                content: '下列关于栈（Stack）的描述，正确的是：',
                type: 'choice',
                score: 20,
                difficulty: 'easy',
                answer: 'A',
                analysis: '栈是一种后进先出(LIFO)的数据结构，只能在栈顶进行插入和删除操作。',
                options: [
                  { key: 'A', content: '栈是一种后进先出(LIFO)的数据结构' },
                  { key: 'B', content: '栈是一种先进先出(FIFO)的数据结构' },
                  { key: 'C', content: '栈可以在任意位置插入和删除元素' },
                  { key: 'D', content: '栈的大小是固定不变的' }
                ]
              },
              {
                id: 2,
                content: '队列（Queue）的基本操作包括：',
                type: 'choice',
                score: 20,
                difficulty: 'easy',
                answer: 'C',
                analysis: '队列的基本操作是入队(enqueue)和出队(dequeue)，分别在队尾插入元素和在队头删除元素。',
                options: [
                  { key: 'A', content: 'push和pop' },
                  { key: 'B', content: 'insert和delete' },
                  { key: 'C', content: 'enqueue和dequeue' },
                  { key: 'D', content: 'add和remove' }
                ]
              },
              {
                id: 3,
                content: '简述链表相比数组的优缺点：',
                type: 'short',
                score: 30,
                difficulty: 'medium',
                answer: '优点：动态分配内存，插入删除操作效率高；缺点：不支持随机访问，需要额外存储指针',
                analysis: '这道题考查学生对链表和数组两种数据结构特点的理解。'
              },
              {
                id: 4,
                content: '实现一个栈的基本操作（包括push、pop、isEmpty），请用你熟悉的编程语言编写代码：',
                type: 'coding',
                score: 30,
                difficulty: 'hard',
                answer: '参考实现：使用数组或链表实现栈的基本操作',
                analysis: '这道编程题考查学生对栈数据结构的实际编程能力。',
                testCases: [
                  {
                    input: '3\npush 1\npush 2\npop',
                    output: '2'
                  },
                  {
                    input: '5\npush 10\npush 20\npush 30\npop\npop',
                    output: '30\n20'
                  }
                ]
              }
            ]
          },
          {
            id: 2,
            name: 'Java程序设计综合练习',
            type: 'homework',
            duration: 60,
            totalScore: 120,
            description: '张教授设计的Java面向对象编程综合练习，涵盖类、对象、继承、多态等核心概念',
            status: 'published',
            createTime: '2024-12-18 14:00:00',
            questions: [
              {
                id: 5,
                content: 'Java语言的主要特点包括：',
                type: 'choice',
                score: 15,
                difficulty: 'easy',
                answer: 'D',
                analysis: 'Java语言具有面向对象、平台无关性、自动内存管理等多个重要特点。',
                options: [
                  { key: 'A', content: '仅仅是面向对象' },
                  { key: 'B', content: '仅仅是平台无关' },
                  { key: 'C', content: '仅仅是安全性高' },
                  { key: 'D', content: '面向对象、平台无关、安全性高、自动内存管理' }
                ]
              },
              {
                id: 6,
                content: '面向对象编程的三大特性是：',
                type: 'choice',
                score: 15,
                difficulty: 'easy',
                answer: 'B',
                analysis: '面向对象编程的三大特性是封装、继承和多态，这是面向对象编程的核心概念。',
                options: [
                  { key: 'A', content: '封装、抽象、继承' },
                  { key: 'B', content: '封装、继承、多态' },
                  { key: 'C', content: '继承、多态、抽象' },
                  { key: 'D', content: '抽象、封装、多态' }
                ]
              },
              {
                id: 7,
                content: '请解释Java中方法重载（Overloading）和方法重写（Overriding）的区别：',
                type: 'short',
                score: 40,
                difficulty: 'medium',
                answer: '重载是同一类中方法名相同但参数不同；重写是子类重新定义父类的方法',
                analysis: '这道题考查学生对Java中重载和重写概念的理解和区分能力。'
              },
              {
                id: 8,
                content: '设计一个Java类层次结构：定义一个Animal基类和Dog、Cat子类，体现继承和多态的特性。请编写完整代码：',
                type: 'coding',
                score: 50,
                difficulty: 'hard',
                answer: '需要定义Animal基类，包含共同属性和方法；Dog和Cat继承Animal并重写方法',
                analysis: '这道编程题综合考查学生对Java面向对象编程的掌握程度。',
                testCases: [
                  {
                    input: 'Dog\nBuddy',
                    output: 'Dog Buddy says: Woof!'
                  },
                  {
                    input: 'Cat\nWhiskers',
                    output: 'Cat Whiskers says: Meow!'
                  }
                ]
              }
            ]
          }
        ]
      } finally {
        loadingExams.value = false
      }
    }
    
    // 获取考试类型名称
    const getExamTypeName = (type) => {
      const typeMap = {
        'quiz': '小测验',
        'homework': '作业',
        'midterm': '期中考试',
        'final': '期末考试',
        'practice': '练习'
      }
      return typeMap[type] || type
    }
    
    // 获取考试类型标签
    const getExamTypeTag = (type) => {
      const tagMap = {
        'quiz': 'success',
        'homework': 'warning',
        'midterm': 'danger',
        'final': 'danger',
        'practice': 'info'
      }
      return tagMap[type] || 'info'
    }
    
    // 选择考试
    const selectExam = (exam) => {
      selectedExam.value = exam
      console.log('选择考试:', exam)
    }
    
    // 获取考试详细信息（包括具体题目）
    const fetchExamDetails = async (examId) => {
      try {
        console.log('获取考试详细信息，ID:', examId)
        const response = await axios.get(`/api/exam/${examId}`)
        console.log('考试详细信息响应:', response.data)
        
        if (response.data && response.data.success) {
          return response.data.data
        } else {
          throw new Error('获取考试详情失败')
        }
      } catch (error) {
        console.error('获取考试详情失败，使用示例数据:', error)
        
        // 当API调用失败时，从示例数据中查找对应的考试
        const examFromList = teacherExams.value.find(exam => exam.id === examId)
        if (examFromList) {
          console.log('找到示例考试数据:', examFromList)
          return examFromList
        } else {
          ElMessage.error('获取考试详情失败')
          return null
        }
      }
    }
    
    // 开始考试 - 获取教师生成的具体题目
    const startExam = async (exam) => {
      selectedExam.value = exam
      console.log('开始考试:', exam)
      
      // 获取考试的详细题目信息
      const examDetails = await fetchExamDetails(exam.id)
      if (examDetails && examDetails.questions) {
        console.log('原始题目数据:', examDetails.questions)
        
        // 将教师生成的题目转换为练习格式
        practiceQuestions.value = examDetails.questions.map(q => {
          // 处理测试用例：优先使用 testCases，如果没有则尝试从 examples 转换
          let testCases = q.testCases || []
          if (testCases.length === 0 && q.examples && Array.isArray(q.examples)) {
            console.log(`题目 ${q.id} 从 examples 转换测试用例:`, q.examples)
            testCases = q.examples.map(ex => ({
              input: ex.input,
              output: ex.output
            }))
          }
          
          console.log(`题目 ${q.id} (${q.type}) 测试用例数量:`, testCases.length)
          
          return {
            id: q.id,
            title: q.content,
            type: q.type,
            content: q.content,
            options: q.options ? q.options.map(opt => opt.content) : null,
            optionKeys: q.options ? q.options.map(opt => opt.key) : null,
            score: q.score,
            difficulty: q.difficulty || 'medium',
            answer: q.answer,
            explanation: q.analysis || q.explanation || '',
            examples: q.examples || [], // 保留示例用于显示
            testCases: testCases, // 包含测试用例
            requirements: q.requirements || q.content, // 编程要求
            hints: q.hints || '', // 提示
            examSource: true // 标记这是来自教师考试的题目
          }
        })
        
        console.log('转换后的题目数据:', practiceQuestions.value)
        
        // 初始化用户答案数组
        userAnswers.value = practiceQuestions.value.map(q => {
          if (q.type === 'fill' && q.blanks && Array.isArray(q.blanks)) {
            return Array(q.blanks.length).fill('')
          }
          return ''
        })
        
        // 设置考试时间
        practiceForm.timeLimit = examDetails.duration || exam.duration || 30
        
        ElMessage.success(`开始考试：${exam.name}，共${practiceQuestions.value.length}道题`)
        
        // 切换到练习页面并自动开始
        currentPage.value = 'practice'
        startPractice()
      } else {
        ElMessage.error('无法获取考试题目，请联系教师')
      }
    }
    
    // 预览考试 - 显示教师生成的具体题目
    const previewExam = async (exam) => {
      console.log('预览考试:', exam)
      
      // 获取考试的详细题目信息
      const examDetails = await fetchExamDetails(exam.id)
      if (examDetails && examDetails.questions) {
        // 将教师生成的题目转换为练习格式
        practiceQuestions.value = examDetails.questions.map(q => {
          // 处理测试用例：优先使用 testCases，如果没有则尝试从 examples 转换
          let testCases = q.testCases || []
          if (testCases.length === 0 && q.examples && Array.isArray(q.examples)) {
            testCases = q.examples.map(ex => ({
              input: ex.input,
              output: ex.output
            }))
          }
          
          return {
            id: q.id,
            title: q.content,
            type: q.type,
            content: q.content,
            options: q.options ? q.options.map(opt => opt.content) : null,
            optionKeys: q.options ? q.options.map(opt => opt.key) : null,
            score: q.score,
            difficulty: q.difficulty || 'medium',
            answer: q.answer,
            explanation: q.analysis || q.explanation || '',
            examples: q.examples || [], // 保留示例用于显示
            testCases: testCases, // 包含测试用例
            requirements: q.requirements || q.content, // 编程要求
            hints: q.hints || '', // 提示
            examSource: true // 标记这是来自教师考试的题目
          }
        })
        
        ElMessage.success(`预览考试：${exam.name}，共${practiceQuestions.value.length}道题`)
        
        // 切换到练习页面（预览模式）
        currentPage.value = 'practice'
      } else {
        ElMessage.error('无法获取考试题目')
      }
    }
    
    // 保存练习数据到 LocalStorage
    const savePracticeData = () => {
      const practiceData = {
        currentPage: currentPage.value,
        questions: practiceQuestions.value,
        form: practiceForm,
        answers: userAnswers.value,
        result: practiceResult.value,
        started: practiceStarted.value,
        currentIndex: currentQuestionIndex.value,
        remainingTime: remainingTime.value,
        selectedExam: selectedExam.value,
        timestamp: Date.now()
      }
      localStorage.setItem('practiceData', JSON.stringify(practiceData))
      console.log('练习数据已保存到 LocalStorage')
    }
    
    // 从 LocalStorage 恢复练习数据
    const restorePracticeData = () => {
      try {
        const savedData = localStorage.getItem('practiceData')
        if (savedData) {
          const practiceData = JSON.parse(savedData)
          
          // 恢复数据
          if (practiceData.questions && practiceData.questions.length > 0) {
            currentPage.value = practiceData.currentPage || 'config'
            practiceQuestions.value = practiceData.questions
            Object.assign(practiceForm, practiceData.form)
            userAnswers.value = practiceData.answers || []
            practiceResult.value = practiceData.result
            practiceStarted.value = practiceData.started || false
            currentQuestionIndex.value = practiceData.currentIndex || 0
            selectedExam.value = practiceData.selectedExam || null
            
            // 计算实际剩余时间（基于开始时间戳）
            const startTime = localStorage.getItem('practiceStartTime')
            if (startTime && practiceStarted.value) {
              const elapsedSeconds = Math.floor((Date.now() - parseInt(startTime)) / 1000)
              const totalSeconds = practiceForm.timeLimit * 60
              remainingTime.value = Math.max(0, totalSeconds - elapsedSeconds)
              
              console.log(`练习已进行 ${elapsedSeconds} 秒，剩余 ${remainingTime.value} 秒`)
              
              // 如果时间已到，自动提交并清除数据
              if (remainingTime.value <= 0) {
                ElMessage.warning('练习时间已到，自动提交')
                submitPractice()
                return
              }
            } else {
              remainingTime.value = practiceData.remainingTime || 0
            }
            
            console.log('练习数据已从 LocalStorage 恢复')
            ElMessage.success('已恢复上次的练习数据')
            
            // 如果练习正在进行中，恢复计时器
            if (practiceStarted.value && remainingTime.value > 0) {
              startTimer()
            }
          }
        }
      } catch (error) {
        console.error('恢复练习数据失败:', error)
        localStorage.removeItem('practiceData')
        localStorage.removeItem('practiceStartTime')
      }
    }
    
    // 清除练习数据
    const clearPracticeData = () => {
      localStorage.removeItem('practiceData')
      localStorage.removeItem('practiceStartTime')
      console.log('练习数据已清除')
    }
    
    // 页面挂载时获取数据
    onMounted(() => {
      console.log('页面挂载，课程信息:', courseInfo)
      
      // 恢复之前的练习数据
      restorePracticeData()
      
      if (courseInfo.courseId) {
        fetchTeacherExams()
      }
    })
    
    // 组件卸载时清理计时器
    onUnmounted(() => {
      stopTimer()
      stopStatusCheck()
    })
    
    return {
      // 页面状态
      currentPage,
      // 课程和考试相关
      courseInfo,
      teacherExams,
      loadingExams,
      selectedExam,
      fetchTeacherExams,
      fetchExamDetails,
      getExamTypeName,
      getExamTypeTag,
      selectExam,
      startExam,
      previewExam,
      // 原有的练习相关
      practiceForm,
      generating,
      practiceQuestions,
      practiceStarted,
      currentQuestionIndex,
      userAnswers,
      practiceResult,
      remainingTime,
      totalQuestionCount,
      canGenerate,
      currentQuestion,
      timerProgress,
      generatePractice,
      resetForm,
      startPractice,
      formatTime,
      timerFormat,
      previousQuestion,
      nextQuestion,
      submitPractice,
      pausePractice,
      exitPractice,
      retryPractice,
      viewAnswers,
      exportResult,
      testBackend,
      healthCheck,
      getQuestionTypeName,
      runCode,
      resetCode,
      handleCodingSubmit,
      codeResults,
      reportDialogVisible,
      currentReport,
      generateReport,
      closeReportDialog,
      formatAnalysis,
      formatQuestionContent,
      // 新增：数据持久化相关
      savePracticeData,
      restorePracticeData,
      clearPracticeData
    }
  }
}
</script>

<style scoped>
.practice-evaluation {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 100px);
}

/* 页面切换动画 */
.config-page,
.practice-page,
.result-page {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 课程信息卡片样式 */
.course-info-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.course-info-card :deep(.el-card__header) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-header h2 {
  margin: 0;
  color: white;
  font-size: 24px;
}

.course-description {
  padding: 10px 0;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
}

/* 考试列表样式 */
.exams-card {
  margin-bottom: 20px;
}

.exams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.exam-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.exam-header h4 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  margin-right: 10px;
}

.exam-tags {
  display: flex;
  flex-direction: column;
  gap: 5px;
  align-items: flex-end;
}

.exam-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 14px;
}

.info-item i {
  margin-right: 5px;
  color: #909399;
}

.exam-description {
  margin-bottom: 15px;
  color: #606266;
  line-height: 1.5;
}

.exam-actions {
  display: flex;
  gap: 10px;
}

.exam-actions .el-button {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exams-grid {
    grid-template-columns: 1fr;
  }
  
  .course-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

.practice-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.practice-settings {
  margin-bottom: 30px;
}

.practice-settings h3 {
  margin-bottom: 20px;
  color: #303133;
  border-bottom: 2px solid #e6a23c;
  padding-bottom: 10px;
}

.practice-form {
  max-width: 100%;
}

.unit {
  margin-left: 10px;
  color: #606266;
}

.question-count-tips {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.generate-actions {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
}

.questions-section {
  margin-top: 20px;
  scroll-margin-top: 20px;
  transition: all 0.3s ease;
}

.questions-section.highlight {
  animation: highlight-section 2s ease-in-out;
}

@keyframes highlight-section {
  0% {
    transform: translateY(-10px);
    opacity: 0.8;
  }
  50% {
    transform: translateY(0);
    opacity: 1;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}

.questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.questions-header h3 {
  margin: 0;
  color: #303133;
  font-size: 1.3rem;
}

.practice-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.questions-preview {
  padding: 20px 0;
}

.question-preview {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #e6a23c;
}

.question-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-type,
.question-difficulty {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.question-type {
  background: #e1f3d8;
  color: #67c23a;
}

.question-difficulty {
  background: #fdf6ec;
  color: #e6a23c;
}

.question-content h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.question-options {
  margin: 15px 0;
}

.option {
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.practice-session {
  padding: 10px 0;
}

.practice-timer {
  margin-bottom: 20px;
}

.timer-text {
  text-align: center;
  margin-top: 10px;
  font-size: 16px;
  color: #e6a23c;
  font-weight: bold;
}

.current-question {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-top: 20px;
}

.question-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.question-counter {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.question-display {
  margin-bottom: 30px;
}

.question-actions {
  text-align: center;
}

.option-radio {
  display: block;
  margin: 15px 0;
  padding: 10px;
  border-radius: 6px;
  transition: background 0.3s;
}

.option-radio:hover {
  background: #f0f9ff;
}

.question-textarea {
  margin-top: 20px;
}

.result-section {
  margin-top: 20px;
}

.result-summary {
  margin-bottom: 30px;
}

.result-stat {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.result-details {
  margin-bottom: 30px;
}

.result-details h4 {
  margin-bottom: 20px;
  color: #303133;
}

.analysis-item {
  margin-bottom: 25px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.analysis-item h5 {
  margin: 0 0 15px 0;
  color: #303133;
}

.suggestions {
  margin-top: 15px;
}

.suggestions ul {
  margin: 10px 0;
  padding-left: 20px;
}

.suggestions li {
  margin: 5px 0;
  color: #606266;
}

.result-actions {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 题目类型配置样式 */
.question-type-config {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.type-config-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #f5f7fa;
}

.type-name {
  font-weight: 500;
  color: #606266;
  min-width: 60px;
}

/* 填空题样式 */
.question-fill {
  margin-top: 20px;
}

.fill-instruction {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.fill-blanks {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.blank-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.blank-label {
  min-width: 80px;
  font-weight: 500;
  color: #409eff;
}

.blank-input {
  flex: 1;
  max-width: 300px;
}

.single-fill {
  max-width: 400px;
}

/* 简答题样式 */
.question-short {
  margin-top: 20px;
}

.short-instruction {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.short-tips {
  margin-top: 15px;
}

.short-tips ul {
  margin: 10px 0 0 0;
  padding-left: 20px;
}

.short-tips li {
  margin-bottom: 5px;
  color: #606266;
}

/* 编程题样式 - 左右布局 */
.question-coding {
  margin-top: 20px;
}

.coding-layout {
  display: flex;
  gap: 20px;
  height: auto;
  min-height: 600px;
}

/* 左侧题目描述 */
.coding-left {
  flex: 0 0 45%;
  overflow-y: auto;
  padding: 20px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.coding-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.coding-title {
  margin: 0 0 24px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  line-height: 1.5;
}

.coding-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.coding-section:last-child {
  border-bottom: none;
}

.section-title {
  margin: 0 0 12px 0;
  color: #409eff;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background: #409eff;
  margin-right: 8px;
  border-radius: 2px;
}

.section-content {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.example-box {
  margin-bottom: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.example-box:last-child {
  margin-bottom: 0;
}

.example-label {
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  font-size: 14px;
}

.example-io {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.io-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.io-label {
  color: #909399;
  font-size: 13px;
  min-width: 40px;
  font-weight: 500;
}

.io-value {
  flex: 1;
  padding: 8px 12px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', monospace;
  font-size: 13px;
  color: #606266;
  display: block;
}

.io-value.output {
  color: #67c23a;
  font-weight: 500;
}

/* 右侧代码编辑器 */
.coding-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.coding-right :deep(.code-editor) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 滚动条样式 */
.coding-left::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.coding-left::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

.coding-left::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.coding-left::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 论述题样式 */
.question-essay {
  margin-top: 20px;
}

.essay-instruction {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.essay-tips {
  margin-top: 15px;
}

.essay-tips ul {
  margin: 10px 0 0 0;
  padding-left: 20px;
}

.essay-tips li {
  margin-bottom: 5px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .practice-evaluation {
    padding: 10px;
  }
  
  .questions-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .practice-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .question-navigation {
    flex-direction: column;
    gap: 15px;
  }
  
  .current-question {
    padding: 20px;
  }
  
  .question-type-config {
    flex-direction: column;
  }
  
  .type-config-item {
    width: 100%;
  }
  
  .blank-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .blank-input {
    max-width: 100%;
  }
  
  .coding-actions {
    flex-direction: column;
  }
}

/* 详细解答弹窗样式 */
.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.analysis-header h5 {
  margin: 0;
  flex: 1;
  margin-right: 15px;
}

.analysis-status {
  display: flex;
  align-items: center;
}

.analysis-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-top: 10px;
}

.analysis-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.coding-result {
  margin-top: 15px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.coding-result p {
  margin-bottom: 10px;
  font-weight: 600;
  color: #409eff;
}

.detail-content {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #ebeef5;
}

.detail-header h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.detail-answers {
  margin-bottom: 25px;
}

.answer-item {
  margin-bottom: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.answer-item h5 {
  margin: 0 0 10px 0;
  color: #409eff;
  font-size: 14px;
}

.answer-content {
  padding: 10px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  min-height: 40px;
  line-height: 1.6;
}

.answer-content.correct {
  border-color: #67c23a;
  background: #f0f9ff;
  color: #67c23a;
}

.detail-analysis {
  margin-bottom: 25px;
}

.detail-analysis h5 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.analysis-text {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #e6a23c;
  line-height: 1.8;
  color: #606266;
}

.analysis-text h1,
.analysis-text h2,
.analysis-text h3 {
  margin: 15px 0 10px 0;
  color: #303133;
}

.analysis-text h1 {
  font-size: 18px;
}

.analysis-text h2 {
  font-size: 16px;
}

.analysis-text h3 {
  font-size: 14px;
}

.detail-suggestion {
  margin-bottom: 20px;
}

.detail-suggestion h5 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.suggestion-text {
  padding: 15px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 4px solid #67c23a;
  line-height: 1.6;
  color: #606266;
  font-weight: 500;
}

/* AI分析状态显示样式 */
.ai-analysis-status {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.ai-analysis-status .el-tag {
  margin-right: 10px;
}

.ai-analysis-status .el-button {
  margin-left: 10px;
}

/* 格式化题目内容样式 */
.formatted-question {
  line-height: 1.8;
}

.formatted-question .question-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.formatted-question .question-section:last-child {
  margin-bottom: 0;
}

.formatted-question .section-title {
  margin: 0 0 10px 0;
  color: #409eff;
  font-size: 15px;
  font-weight: 600;
}

.formatted-question .section-content {
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
}

</style> 