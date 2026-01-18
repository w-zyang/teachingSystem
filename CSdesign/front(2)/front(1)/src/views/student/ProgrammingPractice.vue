<template>
  <div class="programming-practice">
    <div class="practice-header">
      <h2>编程练习</h2>
      <button @click="goBack" class="btn-back">
        <i class="fas fa-arrow-left"></i>
        返回
      </button>
    </div>

    <div class="practice-content">
      <!-- 题目描述 -->
      <div class="question-panel">
        <div class="question-header">
          <h3>{{ question.title }}</h3>
          <span class="difficulty" :class="'difficulty-' + question.difficulty">
            {{ getDifficultyText(question.difficulty) }}
          </span>
        </div>

        <div class="question-body">
          <!-- 如果题目内容是结构化的，使用原有格式 -->
          <template v-if="question.description">
            <div class="section">
              <h4>📝 题目描述</h4>
              <div class="content-text" v-html="formatContent(question.description)"></div>
            </div>

            <div class="section" v-if="question.inputFormat">
              <h4>📥 输入格式</h4>
              <div class="content-text" v-html="formatContent(question.inputFormat)"></div>
            </div>

            <div class="section" v-if="question.outputFormat">
              <h4>📤 输出格式</h4>
              <div class="content-text" v-html="formatContent(question.outputFormat)"></div>
            </div>

            <div class="section" v-if="question.examples && question.examples.length > 0">
              <h4>💡 示例</h4>
              <div v-for="(example, index) in question.examples" :key="index" class="example">
                <div class="example-title">示例 {{ index + 1 }}</div>
                <div class="example-content">
                  <div class="example-input">
                    <strong>输入：</strong>
                    <pre>{{ example.input }}</pre>
                  </div>
                  <div class="example-output">
                    <strong>输出：</strong>
                    <pre>{{ example.output }}</pre>
                  </div>
                  <div v-if="example.explanation" class="example-explanation">
                    <strong>解释：</strong>
                    <p>{{ example.explanation }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="question.constraints && question.constraints.length > 0" class="section">
              <h4>⚠️ 数据范围</h4>
              <ul>
                <li v-for="(constraint, index) in question.constraints" :key="index">
                  {{ constraint }}
                </li>
              </ul>
            </div>
          </template>

          <!-- 如果题目内容是纯文本（从AI生成的content字段），自动解析格式 -->
          <template v-else-if="question.content">
            <div class="formatted-content" v-html="parseAndFormatContent(question.content)"></div>
          </template>
        </div>
      </div>

      <!-- 代码编辑器 -->
      <div class="editor-panel">
        <CodeEditor
          :title="question.title"
          :question-id="question.id"
          :test-cases="question.testCases"
          :default-language="defaultLanguage"
          :default-code="defaultCode"
          @submit-success="handleSubmitSuccess"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CodeEditor from '@/components/CodeEditor.vue'

const router = useRouter()

// 示例题目数据
const question = ref({
  id: 1,
  title: '两数之和',
  difficulty: 'easy',
  description: '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。你可以按任意顺序返回答案。',
  inputFormat: '第一行包含一个整数 n，表示数组长度。\n第二行包含 n 个整数，表示数组 nums。\n第三行包含一个整数 target。',
  outputFormat: '输出两个整数，表示两个数的下标（下标从 0 开始），用空格分隔。',
  examples: [
    {
      input: '4\n2 7 11 15\n9',
      output: '0 1',
      explanation: '因为 nums[0] + nums[1] == 9，返回 [0, 1]。'
    },
    {
      input: '3\n3 2 4\n6',
      output: '1 2',
      explanation: '因为 nums[1] + nums[2] == 6，返回 [1, 2]。'
    }
  ],
  constraints: [
    '2 <= nums.length <= 10^4',
    '-10^9 <= nums[i] <= 10^9',
    '-10^9 <= target <= 10^9',
    '只会存在一个有效答案'
  ],
  testCases: [
    {
      input: '4\n2 7 11 15\n9',
      output: '0 1'
    },
    {
      input: '3\n3 2 4\n6',
      output: '1 2'
    },
    {
      input: '2\n3 3\n6',
      output: '0 1'
    }
  ]
})

const defaultLanguage = ref('71') // Python
const defaultCode = ref(`# Python 示例代码
n = int(input())
nums = list(map(int, input().split()))
target = int(input())

# 在这里编写你的代码
for i in range(n):
    for j in range(i + 1, n):
        if nums[i] + nums[j] == target:
            print(i, j)
            break
`)

const getDifficultyText = (difficulty) => {
  const map = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return map[difficulty] || difficulty
}

/**
 * 格式化内容文本，将换行符转换为HTML
 */
const formatContent = (text) => {
  if (!text) return ''
  return text
    .replace(/\n/g, '<br>')
    .replace(/  /g, '&nbsp;&nbsp;')
}

/**
 * 解析并格式化AI生成的题目内容
 * 自动识别"编程要求"、"输入格式"、"输出格式"、"示例"等部分
 */
const parseAndFormatContent = (content) => {
  if (!content) return ''
  
  let html = '<div class="auto-formatted-content">'
  
  // 按行分割内容
  const lines = content.split('\n')
  let currentSection = ''
  let sectionContent = []
  
  const sectionIcons = {
    '编程要求': '📝',
    '题目描述': '📝',
    '输入格式': '📥',
    '输出格式': '📤',
    '示例': '💡',
    '数据范围': '⚠️',
    '注意': '⚠️',
    '说明': 'ℹ️',
    '提示': '💡'
  }
  
  const flushSection = () => {
    if (currentSection && sectionContent.length > 0) {
      const icon = sectionIcons[currentSection] || '▪️'
      html += `<div class="section">
        <h4>${icon} ${currentSection}</h4>
        <div class="content-text">${sectionContent.join('<br>')}</div>
      </div>`
      sectionContent = []
    }
  }
  
  for (let line of lines) {
    line = line.trim()
    
    // 检查是否是章节标题
    let isSection = false
    for (let sectionName of Object.keys(sectionIcons)) {
      if (line.includes(sectionName + '：') || line.includes(sectionName + ':')) {
        flushSection()
        currentSection = sectionName
        isSection = true
        // 如果标题后面还有内容，添加到section中
        const afterColon = line.split(/[：:]/)[1]
        if (afterColon && afterColon.trim()) {
          sectionContent.push(afterColon.trim())
        }
        break
      }
    }
    
    if (!isSection && line) {
      // 处理代码块（以def、function、class等开头）
      if (line.match(/^(def|function|class|import|from|#|\/\/)/)) {
        sectionContent.push(`<code class="inline-code">${escapeHtml(line)}</code>`)
      } else {
        sectionContent.push(escapeHtml(line))
      }
    }
  }
  
  flushSection()
  html += '</div>'
  
  return html
}

/**
 * HTML转义
 */
const escapeHtml = (text) => {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

const handleSubmitSuccess = (result) => {
  console.log('提交结果:', result)
  
  if (result.passed) {
    ElMessage.success({
      message: `恭喜！你的答案正确，得分：${result.score}`,
      duration: 3000
    })
  } else {
    ElMessage.warning({
      message: `部分测试用例未通过，得分：${result.score}`,
      duration: 3000
    })
  }

  // 这里可以保存提交记录到后端
  // saveSubmissionRecord(result)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  console.log('编程练习页面已加载')
})
</script>

<style scoped>
.programming-practice {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.practice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.practice-header h2 {
  margin: 0;
  color: #2c3e50;
}

.btn-back {
  padding: 8px 16px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #5a6268;
}

.practice-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.question-panel,
.editor-panel {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
}

.question-panel {
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

.question-header {
  background: #3498db;
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-header h3 {
  margin: 0;
  font-size: 1.3rem;
}

.difficulty {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.difficulty-easy {
  background: #27ae60;
}

.difficulty-medium {
  background: #f39c12;
}

.difficulty-hard {
  background: #e74c3c;
}

.question-body {
  padding: 20px;
}

.section {
  margin-bottom: 24px;
}

.section:last-child {
  margin-bottom: 0;
}

.section h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  border-bottom: 2px solid #3498db;
  padding-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section p {
  margin: 0;
  line-height: 1.8;
  color: #555;
}

.content-text {
  line-height: 1.8;
  color: #555;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.formatted-content {
  line-height: 1.8;
}

.formatted-content .section {
  margin-bottom: 24px;
}

.formatted-content .section:last-child {
  margin-bottom: 0;
}

.formatted-content .content-text {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #3498db;
}

.inline-code {
  display: block;
  padding: 8px 12px;
  background: #2c3e50;
  color: #f8f9fa;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  margin: 4px 0;
  overflow-x: auto;
}

.section ul {
  margin: 0;
  padding-left: 20px;
}

.section li {
  margin-bottom: 8px;
  color: #555;
}

.example {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #3498db;
}

.example:last-child {
  margin-bottom: 0;
}

.example-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.example-content > div {
  margin-bottom: 8px;
}

.example-content > div:last-child {
  margin-bottom: 0;
}

.example-content strong {
  color: #2c3e50;
  display: block;
  margin-bottom: 4px;
}

.example-content pre {
  margin: 0;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  border: 1px solid #ddd;
}

.example-explanation p {
  margin: 0;
  color: #555;
  font-style: italic;
}

@media (max-width: 1200px) {
  .practice-content {
    grid-template-columns: 1fr;
  }

  .question-panel {
    max-height: none;
  }
}
</style>
