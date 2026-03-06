<template>
  <div class="practice-session">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <p>正在生成题目...</p>
    </div>

    <!-- 完成页面 -->
    <div v-else-if="finished" class="finish-page">
      <div class="finish-icon">
        <el-icon><CircleCheck /></el-icon>
      </div>
      <h2>练习完成！</h2>
      <div class="statistics">
        <div class="stat-item">
          <span class="stat-label">总题数</span>
          <span class="stat-value">{{ questions.length }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">正确数</span>
          <span class="stat-value correct">{{ correctCount }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">错误数</span>
          <span class="stat-value incorrect">{{ questions.length - correctCount }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">正确率</span>
          <span class="stat-value">{{ Math.round((correctCount / questions.length) * 100) }}%</span>
        </div>
      </div>
      <div class="finish-actions">
        <el-button type="primary" size="large" @click="backToList">
          返回练习列表
        </el-button>
        <el-button size="large" @click="reviewWrong">
          查看错题
        </el-button>
      </div>
    </div>

    <!-- 刷题模式 -->
    <div v-else-if="mode === 'practice' && questions.length > 0" class="practice-mode">
      <!-- 顶部进度条 -->
      <div class="progress-header">
        <div class="progress-info">
          <span class="current-question">第 {{ currentQuestionIndex + 1 }} 题</span>
          <span class="total-questions">共 {{ questions.length }} 题</span>
        </div>
        <el-progress 
          :percentage="Math.round(((currentQuestionIndex + 1) / questions.length) * 100)" 
          :stroke-width="8"
          :show-text="false"
        />
      </div>

      <!-- 题目卡片 -->
      <div class="question-card">
        <div class="question-header">
          <el-tag :type="getQuestionTypeTag(currentQuestion.type)">
            {{ getQuestionTypeName(currentQuestion.type) }}
          </el-tag>
          <el-tag v-if="currentQuestion.difficulty" :type="getDifficultyTag(currentQuestion.difficulty)">
            {{ getDifficultyName(currentQuestion.difficulty) }}
          </el-tag>
          <span class="question-points">{{ currentQuestion.points || 10 }} 分</span>
        </div>

        <div class="question-content">
          <h3>{{ currentQuestion.title || currentQuestion.question }}</h3>
          <div v-if="currentQuestion.description" class="question-description">
            {{ currentQuestion.description }}
          </div>
        </div>

        <!-- 选择题 -->
        <div v-if="currentQuestion.type === 'choice'" class="answer-section">
          <el-radio-group v-model="currentAnswer" :disabled="showAnswer">
            <el-radio 
              v-for="(option, index) in getOptionList(currentQuestion)" 
              :key="index"
              :label="option.key || String.fromCharCode(65 + index)"
              class="option-item"
            >
              <span class="option-key">{{ option.key || String.fromCharCode(65 + index) }}.</span>
              <span class="option-text">{{ option.text || option }}</span>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 填空题 -->
        <div v-else-if="currentQuestion.type === 'fill'" class="answer-section">
          <el-input
            v-model="currentAnswer"
            type="textarea"
            :rows="3"
            placeholder="请输入答案"
            :disabled="showAnswer"
          />
        </div>

        <!-- 简答题 -->
        <div v-else-if="currentQuestion.type === 'short'" class="answer-section">
          <el-input
            v-model="currentAnswer"
            type="textarea"
            :rows="6"
            placeholder="请输入答案"
            :disabled="showAnswer"
          />
        </div>

        <!-- 编程题 -->
        <div v-else-if="currentQuestion.type === 'coding'" class="answer-section">
          <el-input
            v-model="currentAnswer"
            type="textarea"
            :rows="12"
            placeholder="请输入代码"
            :disabled="showAnswer"
            class="code-input"
          />
        </div>

        <!-- 答案解析（套卷演练不提前看答案） -->
        <div v-if="showAnswer && practiceType !== 'paper'" class="answer-analysis">
          <div class="result-banner" :class="isCorrect ? 'correct' : 'incorrect'">
            <el-icon v-if="isCorrect"><CircleCheck /></el-icon>
            <el-icon v-else><CircleClose /></el-icon>
            <span>{{ isCorrect ? '回答正确！' : '回答错误' }}</span>
          </div>

          <div class="correct-answer">
            <h4>正确答案：</h4>
            <p>{{ currentQuestion.answer || currentQuestion.correctAnswer }}</p>
          </div>

          <div v-if="currentQuestion.explanation" class="explanation">
            <h4>解析：</h4>
            <p>{{ currentQuestion.explanation }}</p>
          </div>

          <div v-if="aiEvaluation" class="ai-evaluation">
            <h4>AI 评价：</h4>
            <p>{{ aiEvaluation }}</p>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <!-- 套卷演练：只有上一题 / 下一题 / 交卷 -->
          <template v-if="practiceType === 'paper'">
            <el-button 
              v-if="currentQuestionIndex > 0"
              size="large"
              @click="prevQuestion"
            >
              上一题
            </el-button>
            <el-button 
              v-if="currentQuestionIndex < questions.length - 1"
              type="primary" 
              size="large"
              @click="goNextInExam"
            >
              下一题
            </el-button>
            <el-button 
              v-else
              type="success" 
              size="large"
              @click="finishPractice"
            >
              正式交卷
            </el-button>
          </template>

          <!-- 刷题模式：每题提交一次，再下一题 -->
          <template v-else>
            <el-button 
              v-if="!showAnswer" 
              type="primary" 
              size="large"
              @click="submitAnswer"
              :loading="evaluating"
              :disabled="!currentAnswer"
            >
              {{ evaluating ? 'AI 批阅中...' : '提交答案' }}
            </el-button>

            <template v-else>
              <el-button 
                v-if="currentQuestionIndex < questions.length - 1"
                type="primary" 
                size="large"
                @click="nextQuestion"
              >
                下一题
              </el-button>
              <el-button 
                v-else
                type="success" 
                size="large"
                @click="finishPractice"
              >
                完成练习
              </el-button>
            </template>
          </template>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { aiAPI } from '@/api/ai'

const router = useRouter()
const route = useRoute()

// 状态
const loading = ref(true)
const evaluating = ref(false)
const finished = ref(false)
const mode = ref('practice') // practice: 刷题模式, exam: 考试模式
const practiceType = ref('knowledge') // knowledge / ai / paper

// 题目数据
const questions = ref([])
const currentQuestionIndex = ref(0)
const currentAnswer = ref('')
const showAnswer = ref(false)
const isCorrect = ref(false)
const aiEvaluation = ref('')

// 答题记录
const answerRecords = ref([])
const correctCount = ref(0)

// 当前题目
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || {}
})

// 获取题型标签
const getQuestionTypeTag = (type) => {
  const map = {
    choice: 'primary',
    fill: 'success',
    short: 'warning',
    coding: 'danger'
  }
  return map[type] || 'info'
}

// 获取题型名称
const getQuestionTypeName = (type) => {
  const map = {
    choice: '选择题',
    fill: '填空题',
    short: '简答题',
    coding: '编程题'
  }
  return map[type] || '未知'
}

// 获取难度标签
const getDifficultyTag = (difficulty) => {
  const map = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger'
  }
  return map[difficulty] || 'info'
}

// 获取难度名称
const getDifficultyName = (difficulty) => {
  const map = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return map[difficulty] || '中等'
}

// 为套卷演练构造一套本地固定试卷，避免走 AI 接口超时
const buildLocalPaperQuestions = (count = 30) => {
  const baseQuestions = [
    {
      title: '关于 null 的选择题',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: 'null 表示“没有引用任何对象”' },
        { key: 'B', text: 'null 和 0 是完全等价的' },
        { key: 'C', text: '可以对 null 调用任意方法' },
        { key: 'D', text: 'null 可以参与算术运算' }
      ],
      answer: 'A',
      explanation: '在大多数编程语言中，null 表示“空引用”，并不等价于数字 0。对 null 调用方法或参与运算通常会导致异常。'
    },
    {
      title: '关于数组下标的说法，正确的是？',
      type: 'choice',
      difficulty: 'easy',
      points: 10,
      options: [
        { key: 'A', text: '大多数语言中数组下标从 1 开始' },
        { key: 'B', text: '大多数语言中数组下标从 0 开始' },
        { key: 'C', text: '数组下标可以是任意实数' },
        { key: 'D', text: '数组下标必须是负数' }
      ],
      answer: 'B',
      explanation: '在 C、Java、JavaScript 等大多数语言中，数组下标是从 0 开始的非负整数。'
    },
    {
      title: '关于二叉树遍历的说法，错误的是？',
      type: 'choice',
      difficulty: 'hard',
      points: 10,
      options: [
        { key: 'A', text: '前序遍历顺序为：根结点 → 左子树 → 右子树' },
        { key: 'B', text: '中序遍历顺序为：左子树 → 根结点 → 右子树' },
        { key: 'C', text: '后序遍历顺序为：左子树 → 右子树 → 根结点' },
        { key: 'D', text: '层序遍历等价于中序遍历' }
      ],
      answer: 'D',
      explanation: '层序遍历是按层从上到下、从左到右访问结点，与中序遍历的访问顺序不同。'
    },
    {
      title: '在 Java 中，关于 String 的说法正确的是？',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: 'String 是基本数据类型' },
        { key: 'B', text: 'String 是不可变对象' },
        { key: 'C', text: '使用 == 一定可以正确比较字符串内容' },
        { key: 'D', text: 'String 一定存放在栈内存中' }
      ],
      answer: 'B',
      explanation: 'String 在 Java 中是不可变对象，内容修改会产生新的字符串实例。'
    },
    {
      title: '关于 Linux 中的权限 rwx 的含义，错误的是？',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: 'r 表示可读' },
        { key: 'B', text: 'w 表示可写' },
        { key: 'C', text: 'x 表示可执行' },
        { key: 'D', text: 'w 表示可执行' }
      ],
      answer: 'D',
      explanation: 'w 表示写权限，x 才表示执行权限。'
    },
    {
      title: '关于网络协议 TCP 的特点，正确的是？',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: '面向无连接' },
        { key: 'B', text: '不保证可靠传输' },
        { key: 'C', text: '面向连接，可靠传输' },
        { key: 'D', text: '只在局域网中使用' }
      ],
      answer: 'C',
      explanation: 'TCP 是面向连接的可靠传输协议，通过序号、确认、重传等机制保证可靠性。'
    },
    {
      title: '关于时间复杂度的说法，正确的是？',
      type: 'choice',
      difficulty: 'easy',
      points: 10,
      options: [
        { key: 'A', text: 'O(1) 一定比 O(n) 快' },
        { key: 'B', text: 'O(log n) 一定比 O(n^2) 慢' },
        { key: 'C', text: 'O(n log n) 通常优于 O(n^2)' },
        { key: 'D', text: '时间复杂度越大算法越好' }
      ],
      answer: 'C',
      explanation: '在 n 较大时，O(n log n) 的增长速度明显慢于 O(n^2)，效率更高。'
    },
    {
      title: '关于哈希表（Hash Table）的说法，错误的是？',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: '查找的平均时间复杂度接近 O(1)' },
        { key: 'B', text: '可能发生冲突，需要解决冲突策略' },
        { key: 'C', text: '不允许存储键值对' },
        { key: 'D', text: '装载因子过大可能影响性能' }
      ],
      answer: 'C',
      explanation: '哈希表本质上就是一种键值对（key-value）存储结构。'
    },
    {
      title: '关于操作系统中的进程和线程，正确的是？',
      type: 'choice',
      difficulty: 'medium',
      points: 10,
      options: [
        { key: 'A', text: '一个进程只能有一个线程' },
        { key: 'B', text: '线程是资源分配的基本单位' },
        { key: 'C', text: '进程是资源分配的基本单位，线程是 CPU 调度的基本单位' },
        { key: 'D', text: '线程之间不能共享任何数据' }
      ],
      answer: 'C',
      explanation: '操作系统通常以进程为资源分配单位，以线程为 CPU 调度单位。'
    }
  ]

  const questions = []
  for (let i = 0; i < count; i++) {
    const base = baseQuestions[i % baseQuestions.length]
    questions.push({
      ...base,
      // 给每题加一个序号前缀，增强试卷感
      title: `第 ${i + 1} 题：${base.title}`
    })
  }
  return questions
}

// 统一获取选项列表，兼容数组或字符串格式
const getOptionList = (question) => {
  if (Array.isArray(question.options) && question.options.length > 0) {
    return question.options
  }

  // 某些试卷题可能把选项放在一个字符串里，例如：
  // "A. xxx\nB. xxx\nC. xxx\nD. xxx"
  const raw = question.options || question.optionText || ''
  if (typeof raw !== 'string' || !raw.trim()) {
    return []
  }

  // 先按换行拆分
  const lines = raw.split(/\r?\n/).map(l => l.trim()).filter(Boolean)
  if (lines.length === 0) {
    return []
  }

  // 识别形如 "A. xxx" / "A、xxx" / "A)" 的格式
  const pattern = /^([A-Z])[\.\、\)]\s*(.+)$/
  const result = lines.map((line, index) => {
    const match = line.match(pattern)
    if (match) {
      return { key: match[1], text: match[2] }
    }
    // 如果不符合格式，就按 A/B/C/D 顺序兜底
    const key = String.fromCharCode(65 + index)
    return { key, text: line }
  })

  return result
}

// 提交答案
const submitAnswer = async () => {
  if (!currentAnswer.value) {
    ElMessage.warning('请先作答')
    return
  }

  // 1）套卷演练：全部题目先本地判分，提交后再批量 AI 阅卷
  // 2）其它模式中，选择题 / 填空题也直接本地比对，只有主观题走 AI
  const isObjective =
    currentQuestion.value.type === 'choice' || currentQuestion.value.type === 'fill'

  if (practiceType.value === 'paper' || isObjective) {
    const correctAnswer = currentQuestion.value.answer || currentQuestion.value.correctAnswer || ''
    isCorrect.value = correctAnswer
      ? currentAnswer.value.trim() === String(correctAnswer).trim()
      : false
    aiEvaluation.value = ''

    // 如果已经有当前题目的记录，先更新再保存，避免重复
    const existingIndex = answerRecords.value.findIndex(
      r => r.questionIndex === currentQuestionIndex.value
    )
    const record = {
      questionIndex: currentQuestionIndex.value,
      question: currentQuestion.value,
      studentAnswer: currentAnswer.value,
      isCorrect: isCorrect.value
    }

    if (existingIndex >= 0) {
      answerRecords.value[existingIndex] = record
    } else {
      answerRecords.value.push(record)
    }

    if (isCorrect.value) {
      correctCount.value++
    }

    showAnswer.value = true
    return
  }

  // 主观题（简答 / 编程）才实时走 AI 阅卷
  try {
    evaluating.value = true

    const response = await aiAPI.practiceEvaluation({
      question: currentQuestion.value,
      studentAnswer: currentAnswer.value,
      correctAnswer: currentQuestion.value.answer || currentQuestion.value.correctAnswer
    })

    if (response && response.success) {
      const result = response.data
      isCorrect.value = result.isCorrect || false
      aiEvaluation.value = result.evaluation || result.feedback || ''

      answerRecords.value.push({
        questionIndex: currentQuestionIndex.value,
        question: currentQuestion.value,
        studentAnswer: currentAnswer.value,
        isCorrect: isCorrect.value,
        evaluation: aiEvaluation.value
      })

      if (isCorrect.value) {
        correctCount.value++
      }

      showAnswer.value = true
    } else {
      const correctAnswer = currentQuestion.value.answer || currentQuestion.value.correctAnswer
      isCorrect.value = currentAnswer.value.trim() === correctAnswer.trim()
      
      answerRecords.value.push({
        questionIndex: currentQuestionIndex.value,
        question: currentQuestion.value,
        studentAnswer: currentAnswer.value,
        isCorrect: isCorrect.value
      })

      if (isCorrect.value) {
        correctCount.value++
      }

      showAnswer.value = true
    }
  } catch (error) {
    console.error('批阅失败:', error)
    
    const correctAnswer = currentQuestion.value.answer || currentQuestion.value.correctAnswer
    isCorrect.value = currentAnswer.value.trim() === correctAnswer.trim()
    
    answerRecords.value.push({
      questionIndex: currentQuestionIndex.value,
      question: currentQuestion.value,
      studentAnswer: currentAnswer.value,
      isCorrect: isCorrect.value
    })

    if (isCorrect.value) {
      correctCount.value++
    }

    showAnswer.value = true
  } finally {
    evaluating.value = false
  }
}

// 下一题
const nextQuestion = () => {
  currentQuestionIndex.value++
  currentAnswer.value = ''
  showAnswer.value = false
  isCorrect.value = false
  aiEvaluation.value = ''
}

// 套卷模式下的「下一题」，会自动记录当前题答案
const goNextInExam = () => {
  submitAnswer()
  nextQuestion()
}

// 上一题（主要用于套卷）
const prevQuestion = () => {
  if (currentQuestionIndex.value === 0) return
  currentQuestionIndex.value--
  const record = answerRecords.value.find(r => r.questionIndex === currentQuestionIndex.value)
  currentAnswer.value = record ? record.studentAnswer : ''
  showAnswer.value = false
  isCorrect.value = false
  aiEvaluation.value = ''
}

// 完成练习
const finishPractice = async () => {
  // 套卷演练：最后交卷前，先确保当前题答案已记录
  if (practiceType.value === 'paper') {
    submitAnswer()
  }

  // 套卷演练：统一提交后，再批量调用一次 AI 阅卷（目前主要用于主观题）
  if (practiceType.value === 'paper') {
    try {
      const subjectiveRecords = answerRecords.value.filter(record =>
        record.question &&
        (record.question.type === 'short' || record.question.type === 'coding')
      )

      if (subjectiveRecords.length > 0) {
        const response = await aiAPI.practiceEvaluation({
          questions: subjectiveRecords.map(r => r.question),
          answers: subjectiveRecords.map(r => r.studentAnswer)
        })

        if (response && response.success && Array.isArray(response.data)) {
          response.data.forEach((item, index) => {
            const rec = subjectiveRecords[index]
            if (!rec) return
            rec.evaluation = item.evaluation || item.feedback || ''
          })
        }
      }
    } catch (error) {
      console.error('套卷批量 AI 阅卷失败:', error)
    }
  }

  finished.value = true
}

// 返回列表
const backToList = () => {
  router.push('/student/practice-evaluation-new')
}

// 查看错题
const reviewWrong = () => {
  const wrongQuestions = answerRecords.value.filter(record => !record.isCorrect)
  if (wrongQuestions.length === 0) {
    ElMessage.success('恭喜！没有错题')
    return
  }
  
  // 重置到第一道错题
  const firstWrongIndex = wrongQuestions[0].questionIndex
  currentQuestionIndex.value = firstWrongIndex
  currentAnswer.value = ''
  showAnswer.value = false
  finished.value = false
}

// 加载题目
const loadQuestions = async () => {
  try {
    loading.value = true

    // 从路由获取参数
    const practiceData = route.query
    
    console.log('练习参数:', practiceData)

    // 如果是套卷演练（paper），优先使用本地固定试卷，避免 AI 调用超时
    if (practiceData.type === 'paper') {
      const count = parseInt(practiceData.questionCount) || 30
      questions.value = buildLocalPaperQuestions(count)
      mode.value = 'practice'
      practiceType.value = 'paper'
      return
    }

    // 其它模式调用 AI 生成题目
    const requestData = {
      subject: practiceData.subject || 'data-structure',
      knowledgePoints: practiceData.knowledgePoints ? JSON.parse(practiceData.knowledgePoints) : [],
      difficulty: practiceData.difficulty || 'medium',
      count: parseInt(practiceData.questionCount) || 10, // 后端期望的是 count
      questionTypes: practiceData.questionTypes ? JSON.parse(practiceData.questionTypes) : ['choice'],
      type: practiceData.type || 'knowledge',
      mode: practiceData.mode || 'practice'
    }
    
    console.log('发送到后端的数据:', requestData)
    
    const response = await aiAPI.generatePractice(requestData)

    console.log('后端响应:', response)

    if (response && response.success && response.data) {
      questions.value = response.data.questions || response.data || []
      mode.value = practiceData.mode || 'practice'
      practiceType.value = practiceData.type || 'knowledge'
      
      if (questions.value.length === 0) {
        ElMessage.error('生成题目失败，请重试')
        router.back()
      }
    } else {
      ElMessage.error('生成题目失败，请重试')
      router.back()
    }
  } catch (error) {
    console.error('加载题目失败:', error)
    ElMessage.error('加载题目失败：' + (error.message || '请重试'))
    router.back()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style lang="scss" scoped>
.practice-session {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  color: white;

  .loading-icon {
    font-size: 60px;
    animation: spin 1s linear infinite;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    margin: 0;
  }
}

.practice-mode {
  max-width: 900px;
  margin: 0 auto;
}

.progress-header {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  .progress-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 15px;
    color: #374151;

    .current-question {
      font-weight: 700;
      color: #667eea;
    }

    .total-questions {
      color: #6b7280;
    }
  }
}

.question-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

  .question-header {
    display: flex;
    gap: 12px;
    align-items: center;
    margin-bottom: 24px;

    .question-points {
      margin-left: auto;
      font-size: 16px;
      font-weight: 700;
      color: #667eea;
    }
  }

  .question-content {
    margin-bottom: 32px;

    h3 {
      margin: 0 0 16px 0;
      font-size: 20px;
      font-weight: 600;
      color: #374151;
      line-height: 1.6;
    }

    .question-description {
      color: #6b7280;
      line-height: 1.8;
    }
  }

  .answer-section {
    margin-bottom: 32px;

    .option-item {
      display: flex;
      align-items: flex-start;
      padding: 16px;
      margin-bottom: 12px;
      border: 2px solid #e5e7eb;
      border-radius: 12px;
      transition: all 0.3s;

      &:hover {
        border-color: #667eea;
        background: #f8fafc;
      }

      .option-key {
        font-weight: 700;
        color: #667eea;
        margin-right: 12px;
      }

      .option-text {
        flex: 1;
        line-height: 1.6;
      }
    }

    .code-input {
      :deep(textarea) {
        font-family: 'Consolas', 'Monaco', monospace;
        font-size: 14px;
      }
    }
  }

  .answer-analysis {
    background: #f8fafc;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 32px;

    .result-banner {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: 600;

      &.correct {
        background: #dcfce7;
        color: #10b981;
      }

      &.incorrect {
        background: #fee2e2;
        color: #ef4444;
      }

      .el-icon {
        font-size: 24px;
      }
    }

    h4 {
      margin: 0 0 12px 0;
      font-size: 15px;
      font-weight: 700;
      color: #374151;
    }

    p {
      margin: 0 0 20px 0;
      line-height: 1.8;
      color: #6b7280;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .ai-evaluation {
      padding-top: 20px;
      border-top: 1px solid #e5e7eb;

      p {
        color: #667eea;
        font-weight: 500;
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 16px;

    .el-button {
      min-width: 160px;
    }
  }
}

.finish-page {
  max-width: 600px;
  margin: 0 auto;
  background: white;
  border-radius: 24px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

  .finish-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto 24px;
    background: linear-gradient(135deg, #10b981, #059669);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    .el-icon {
      font-size: 60px;
      color: white;
    }
  }

  h2 {
    margin: 0 0 40px 0;
    font-size: 32px;
    font-weight: 700;
    color: #374151;
  }

  .statistics {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 40px;

    .stat-item {
      background: #f8fafc;
      border-radius: 12px;
      padding: 24px;

      .stat-label {
        display: block;
        font-size: 14px;
        color: #6b7280;
        margin-bottom: 8px;
      }

      .stat-value {
        display: block;
        font-size: 32px;
        font-weight: 700;
        color: #374151;

        &.correct {
          color: #10b981;
        }

        &.incorrect {
          color: #ef4444;
        }
      }
    }
  }

  .finish-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

