<template>
  <div class="question-type-test">
    <el-card class="test-header">
      <h1>题型渲染测试页面</h1>
      <p>测试所有题型的显示和交互功能</p>
    </el-card>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 单选题测试 -->
      <el-tab-pane label="单选题" name="choice">
        <QuestionRenderer
          :question="testQuestions.choice"
          :question-number="1"
          v-model="answers.choice"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>用户答案：</strong>{{ answers.choice || '未作答' }}</p>
          <p><strong>正确答案：</strong>{{ testQuestions.choice.answer }}</p>
        </div>
      </el-tab-pane>

      <!-- 多选题测试 -->
      <el-tab-pane label="多选题" name="multiple">
        <QuestionRenderer
          :question="testQuestions.multiple"
          :question-number="2"
          v-model="answers.multiple"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>用户答案：</strong>{{ answers.multiple.length > 0 ? answers.multiple.join(', ') : '未作答' }}</p>
          <p><strong>正确答案：</strong>{{ testQuestions.multiple.answer.join(', ') }}</p>
          <p><strong>答案类型：</strong>{{ typeof testQuestions.multiple.answer }} ({{ Array.isArray(testQuestions.multiple.answer) ? '数组' : '非数组' }})</p>
        </div>
      </el-tab-pane>

      <!-- 判断题测试 -->
      <el-tab-pane label="判断题" name="true_false">
        <QuestionRenderer
          :question="testQuestions.true_false"
          :question-number="3"
          v-model="answers.true_false"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>用户答案：</strong>{{ answers.true_false === null ? '未作答' : (answers.true_false ? '正确' : '错误') }}</p>
          <p><strong>正确答案：</strong>{{ testQuestions.true_false.answer ? '正确' : '错误' }}</p>
          <p><strong>答案类型：</strong>{{ typeof testQuestions.true_false.answer }} (布尔值)</p>
        </div>
      </el-tab-pane>

      <!-- 填空题测试 -->
      <el-tab-pane label="填空题" name="fill">
        <QuestionRenderer
          :question="testQuestions.fill"
          :question-number="4"
          v-model="answers.fill"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>用户答案：</strong>{{ answers.fill || '未作答' }}</p>
          <p><strong>正确答案：</strong>{{ testQuestions.fill.answer }}</p>
        </div>
      </el-tab-pane>

      <!-- 简答题测试 -->
      <el-tab-pane label="简答题" name="short">
        <QuestionRenderer
          :question="testQuestions.short"
          :question-number="5"
          v-model="answers.short"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>用户答案：</strong>{{ answers.short || '未作答' }}</p>
        </div>
      </el-tab-pane>

      <!-- 编程题测试 -->
      <el-tab-pane label="编程题" name="coding">
        <QuestionRenderer
          :question="testQuestions.coding"
          :question-number="6"
          v-model="answers.coding"
          :show-answer="showAnswers"
        />
        <div class="test-info">
          <p><strong>代码长度：</strong>{{ answers.coding.length }} 字符</p>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-card class="test-controls">
      <el-space>
        <el-button type="primary" @click="showAnswers = !showAnswers">
          {{ showAnswers ? '隐藏答案' : '显示答案' }}
        </el-button>
        <el-button @click="checkAllAnswers">检查所有答案</el-button>
        <el-button @click="resetAllAnswers">重置答案</el-button>
        <el-button type="success" @click="showResults">查看结果</el-button>
      </el-space>
    </el-card>

    <!-- 结果对话框 -->
    <el-dialog v-model="resultsVisible" title="答题结果" width="600px">
      <el-table :data="results" border>
        <el-table-column prop="type" label="题型" width="100" />
        <el-table-column prop="userAnswer" label="用户答案" />
        <el-table-column prop="correctAnswer" label="正确答案" />
        <el-table-column prop="isCorrect" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isCorrect ? 'success' : 'danger'">
              {{ row.isCorrect ? '✓' : '✗' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="results-summary">
        <h3>总分：{{ totalScore }} / {{ maxScore }}</h3>
        <p>正确率：{{ correctRate }}%</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import QuestionRenderer from '@/components/QuestionRenderer.vue'
import { checkAnswer, formatAnswer } from '@/config/questionTypes'
import { ElMessage } from 'element-plus'

const activeTab = ref('choice')
const showAnswers = ref(false)
const resultsVisible = ref(false)

// 测试题目数据
const testQuestions = ref({
  choice: {
    type: 'choice',
    content: '在软件开发过程中，哪种软件开发模型以迭代和增量开发为核心？',
    options: [
      'A. 瀑布模型',
      'B. V模型',
      'C. 敏捷模型',
      'D. 螺旋模型'
    ],
    answer: 'C',
    score: 10,
    explanation: '敏捷模型强调迭代开发、客户协作和响应变化，是以迭代和增量开发为核心的软件开发模型。'
  },
  
  multiple: {
    type: 'multiple',
    content: '以下哪些是软件工程的基本原则？',
    options: [
      'A. 模块化',
      'B. 随意性',
      'C. 抽象化',
      'D. 信息隐藏'
    ],
    answer: ['A', 'C', 'D'],
    score: 15,
    explanation: '软件工程的基本原则包括模块化、抽象化和信息隐藏。随意性不是软件工程原则。'
  },
  
  true_false: {
    type: 'true_false',
    content: '软件测试只能发现错误，不能证明软件没有错误。',
    answer: true,
    score: 5,
    explanation: '这是正确的。软件测试可以发现程序中的错误，但无法证明程序完全没有错误，这是软件测试的基本原理之一。'
  },
  
  fill: {
    type: 'fill',
    content: '在UML中，____是用来描述系统静态结构的图。',
    answer: '类图',
    score: 10,
    explanation: '类图是UML中用来描述系统静态结构的主要图形。'
  },
  
  short: {
    type: 'short',
    content: '请简述软件工程中功能需求和非功能需求的区别，并各举一个例子。',
    answer: '功能需求描述系统应该做什么，即系统的功能和行为，例如：用户登录功能。非功能需求描述系统应该如何工作，包括性能、安全性、可用性等质量属性，例如：系统响应时间不超过2秒。',
    score: 20,
    explanation: '功能需求关注"做什么"，非功能需求关注"如何做"。'
  },
  
  coding: {
    type: 'coding',
    title: '邮箱地址验证',
    content: '编写一个程序，验证给定的字符串是否是有效的邮箱地址。',
    requirements: '实现一个函数，判断输入的字符串是否符合邮箱地址的基本格式（包含@符号，@前后都有字符，@后有域名）。',
    inputFormat: '输入一个字符串，表示待验证的邮箱地址。',
    outputFormat: '如果是有效邮箱，输出"valid"；否则输出"invalid"。',
    examples: [
      {
        input: 'user@example.com',
        output: 'valid',
        explanation: '这是一个标准的邮箱地址格式'
      },
      {
        input: 'invalid.email',
        output: 'invalid',
        explanation: '缺少@符号，不是有效的邮箱地址'
      }
    ],
    testCases: [
      { input: 'test@test.com', output: 'valid' },
      { input: 'user@domain.co.uk', output: 'valid' },
      { input: '@example.com', output: 'invalid' },
      { input: 'user@', output: 'invalid' },
      { input: 'plaintext', output: 'invalid' }
    ],
    hints: '可以使用正则表达式或字符串处理方法来验证邮箱格式。',
    answer: `function validateEmail(email) {
  const regex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
  return regex.test(email) ? "valid" : "invalid";
}`,
    score: 30
  }
})

// 用户答案
const answers = ref({
  choice: '',
  multiple: [],
  true_false: null,
  fill: '',
  short: '',
  coding: ''
})

// 检查所有答案
const checkAllAnswers = () => {
  const results = []
  let correctCount = 0
  
  Object.keys(testQuestions.value).forEach(type => {
    const question = testQuestions.value[type]
    const userAnswer = answers.value[type]
    const isCorrect = checkAnswer(question, userAnswer)
    
    if (isCorrect) correctCount++
    
    results.push({
      type: question.type,
      isCorrect: isCorrect,
      userAnswer: formatAnswer(question.type, userAnswer),
      correctAnswer: formatAnswer(question.type, question.answer)
    })
  })
  
  ElMessage.success(`检查完成！正确 ${correctCount} / ${Object.keys(testQuestions.value).length} 题`)
}

// 重置所有答案
const resetAllAnswers = () => {
  answers.value = {
    choice: '',
    multiple: [],
    true_false: null,
    fill: '',
    short: '',
    coding: ''
  }
  ElMessage.info('已重置所有答案')
}

// 计算结果
const results = computed(() => {
  return Object.keys(testQuestions.value).map(type => {
    const question = testQuestions.value[type]
    const userAnswer = answers.value[type]
    const isCorrect = checkAnswer(question, userAnswer)
    
    return {
      type: question.type,
      isCorrect: isCorrect,
      userAnswer: formatAnswer(question.type, userAnswer) || '未作答',
      correctAnswer: formatAnswer(question.type, question.answer)
    }
  })
})

// 总分
const totalScore = computed(() => {
  return results.value.reduce((sum, result) => {
    const question = testQuestions.value[Object.keys(testQuestions.value).find(k => testQuestions.value[k].type === result.type)]
    return sum + (result.isCorrect ? question.score : 0)
  }, 0)
})

// 最高分
const maxScore = computed(() => {
  return Object.values(testQuestions.value).reduce((sum, q) => sum + q.score, 0)
})

// 正确率
const correctRate = computed(() => {
  return ((totalScore.value / maxScore.value) * 100).toFixed(1)
})

// 显示结果
const showResults = () => {
  resultsVisible.value = true
}
</script>

<style scoped>
.question-type-test {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.test-header {
  margin-bottom: 20px;
  text-align: center;
}

.test-header h1 {
  margin: 0 0 10px 0;
  color: #409EFF;
}

.test-header p {
  margin: 0;
  color: #909399;
}

.test-info {
  margin-top: 20px;
  padding: 16px;
  background: #F5F7FA;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
}

.test-info p {
  margin: 8px 0;
  color: #606266;
}

.test-controls {
  margin-top: 20px;
  text-align: center;
}

.results-summary {
  margin-top: 20px;
  padding: 16px;
  background: #F0F9FF;
  border-radius: 4px;
  text-align: center;
}

.results-summary h3 {
  margin: 0 0 10px 0;
  color: #409EFF;
  font-size: 24px;
}

.results-summary p {
  margin: 0;
  color: #606266;
  font-size: 16px;
}
</style>
