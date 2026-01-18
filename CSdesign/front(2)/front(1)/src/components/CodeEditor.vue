<template>
  <div class="code-editor">
    <div class="editor-header">
      <h3>{{ title }}</h3>
      <div class="editor-controls">
        <select v-model="selectedLanguage" class="language-select">
          <option value="71">Python (3.8.1)</option>
          <option value="62">Java (OpenJDK 13.0.1)</option>
          <option value="63">JavaScript (Node.js 12.14.0)</option>
          <option value="50">C (GCC 9.2.0)</option>
          <option value="54">C++ (GCC 9.2.0)</option>
          <option value="51">C# (Mono 6.6.0.161)</option>
          <option value="60">Go (1.13.5)</option>
          <option value="68">PHP (7.4.1)</option>
          <option value="72">Ruby (2.7.0)</option>
        </select>
        <button @click="runCode" :disabled="isRunning" class="btn btn-run">
          <i v-if="isRunning" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-play"></i>
          {{ isRunning ? '运行中...' : '运行代码' }}
        </button>
        <button @click="submitCode" :disabled="isSubmitting" class="btn btn-submit">
          <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-check"></i>
          {{ isSubmitting ? '提交中...' : '提交答案' }}
        </button>
      </div>
    </div>

    <div class="editor-body">
      <!-- 代码编辑区 -->
      <div class="code-area">
        <textarea
          v-model="code"
          class="code-textarea"
          placeholder="在此输入你的代码..."
          spellcheck="false"
        ></textarea>
      </div>

      <!-- 输入输出区 -->
      <div class="io-area">
        <div class="input-section">
          <h4>输入</h4>
          <textarea
            v-model="stdin"
            class="io-textarea"
            placeholder="标准输入（可选）"
          ></textarea>
        </div>

        <div class="output-section">
          <h4>输出</h4>
          <div v-if="output" class="output-content" :class="outputClass">
            <div class="output-status">
              <span class="status-badge" :class="statusClass">
                {{ output.status }}
              </span>
              <span v-if="output.time" class="time-info">
                <i class="fas fa-clock"></i> {{ output.time }}s
              </span>
              <span v-if="output.memory" class="memory-info">
                <i class="fas fa-memory"></i> {{ output.memory }} KB
              </span>
            </div>

            <div v-if="output.stdout" class="output-text">
              <strong>标准输出：</strong>
              <pre>{{ output.stdout }}</pre>
            </div>

            <div v-if="output.stderr" class="output-text error">
              <strong>错误输出：</strong>
              <pre>{{ output.stderr }}</pre>
            </div>

            <div v-if="output.compileOutput" class="output-text error">
              <strong>编译错误：</strong>
              <pre>{{ output.compileOutput }}</pre>
            </div>

            <div v-if="output.message" class="output-text">
              <strong>消息：</strong>
              <pre>{{ output.message }}</pre>
            </div>
          </div>
          <div v-else class="output-placeholder">
            运行代码后，结果将显示在这里
          </div>
        </div>
      </div>
    </div>

    <!-- 测试用例结果 -->
    <div v-if="testResults.length > 0" class="test-results">
      <h4>测试用例结果</h4>
      <div class="test-cases">
        <div
          v-for="(result, index) in testResults"
          :key="index"
          class="test-case"
          :class="result.passed ? 'passed' : 'failed'"
        >
          <div class="test-case-header">
            <span class="test-case-number">测试用例 {{ index + 1 }}</span>
            <span class="test-case-status">
              <i :class="result.passed ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
              {{ result.passed ? '通过' : '失败' }}
            </span>
          </div>
          <div class="test-case-details">
            <div><strong>输入：</strong>{{ result.input }}</div>
            <div><strong>期望输出：</strong>{{ result.expectedOutput }}</div>
            <div><strong>实际输出：</strong>{{ result.actualOutput }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  title: {
    type: String,
    default: '代码编辑器'
  },
  questionId: {
    type: Number,
    default: null
  },
  testCases: {
    type: Array,
    default: () => []
  },
  defaultLanguage: {
    type: String,
    default: '71' // Python
  },
  defaultCode: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['submit-success'])

// 响应式数据
const code = ref(props.defaultCode)
const stdin = ref('')
const selectedLanguage = ref(props.defaultLanguage)
const output = ref(null)
const isRunning = ref(false)
const isSubmitting = ref(false)
const testResults = ref([])

// 计算属性
const outputClass = computed(() => {
  if (!output.value) return ''
  const statusId = output.value.statusId
  if (statusId === 3) return 'success'
  if (statusId === 6) return 'compile-error'
  if (statusId >= 4) return 'error'
  return ''
})

const statusClass = computed(() => {
  if (!output.value) return ''
  const statusId = output.value.statusId
  if (statusId === 3) return 'status-success'
  if (statusId === 6) return 'status-compile-error'
  if (statusId >= 4) return 'status-error'
  return 'status-pending'
})

// 方法
const runCode = async () => {
  if (!code.value.trim()) {
    ElMessage.warning('请先输入代码')
    return
  }

  isRunning.value = true
  output.value = null

  try {
    const response = await axios.post('/api/code/run', {
      sourceCode: code.value,
      languageId: parseInt(selectedLanguage.value),
      stdin: stdin.value
    })

    if (response.data.success) {
      output.value = response.data.data
      ElMessage.success('代码运行完成')
    } else {
      ElMessage.error(response.data.message || '运行失败')
    }
  } catch (error) {
    console.error('运行代码失败:', error)
    ElMessage.error('运行失败: ' + (error.response?.data?.message || error.message))
  } finally {
    isRunning.value = false
  }
}

const submitCode = async () => {
  if (!code.value.trim()) {
    ElMessage.warning('请先输入代码')
    return
  }

  if (props.testCases.length === 0) {
    ElMessage.warning('没有测试用例')
    return
  }

  isSubmitting.value = true
  testResults.value = []

  try {
    // 批量提交测试用例
    const response = await axios.post('/api/code/submit-batch', {
      sourceCode: code.value,
      languageId: parseInt(selectedLanguage.value),
      testCases: props.testCases.map(tc => ({
        input: tc.input,
        output: tc.output
      }))
    })

    if (response.data.success) {
      const tokens = response.data.data.tokens
      ElMessage.success('代码已提交，正在评测...')

      // 轮询获取结果
      await pollResults(tokens)
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error) {
    console.error('提交代码失败:', error)
    ElMessage.error('提交失败: ' + (error.response?.data?.message || error.message))
  } finally {
    isSubmitting.value = false
  }
}

const pollResults = async (tokens) => {
  const results = []
  let allCompleted = false
  let attempts = 0
  const maxAttempts = 20

  while (!allCompleted && attempts < maxAttempts) {
    await new Promise(resolve => setTimeout(resolve, 1000)) // 等待1秒

    const promises = tokens.map(token =>
      axios.get(`/api/code/result/${token}`)
    )

    const responses = await Promise.all(promises)
    
    allCompleted = true
    results.length = 0

    responses.forEach((response, index) => {
      if (response.data.success) {
        const result = response.data.data
        const statusId = result.statusId

        // 检查是否还在处理中
        if (statusId === 1 || statusId === 2) {
          allCompleted = false
        }

        results.push({
          input: props.testCases[index].input,
          expectedOutput: props.testCases[index].output,
          actualOutput: result.stdout || result.stderr || result.compileOutput || '',
          passed: statusId === 3,
          status: result.status
        })
      }
    })

    attempts++
  }

  testResults.value = results

  // 计算通过率
  const passedCount = results.filter(r => r.passed).length
  const totalCount = results.length

  if (passedCount === totalCount) {
    ElMessage.success(`恭喜！所有测试用例通过 (${passedCount}/${totalCount})`)
    emit('submit-success', {
      passed: true,
      score: 100,
      results: results
    })
  } else {
    ElMessage.warning(`部分测试用例未通过 (${passedCount}/${totalCount})`)
    emit('submit-success', {
      passed: false,
      score: Math.round((passedCount / totalCount) * 100),
      results: results
    })
  }
}
</script>

<style scoped>
.code-editor {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
}

.editor-header {
  background: #2c3e50;
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.editor-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.language-select {
  padding: 8px 12px;
  border: 1px solid #34495e;
  border-radius: 4px;
  background: #34495e;
  color: white;
  font-size: 14px;
  cursor: pointer;
}

.language-select:focus {
  outline: none;
  border-color: #3498db;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-run {
  background: #27ae60;
  color: white;
}

.btn-run:hover:not(:disabled) {
  background: #229954;
}

.btn-submit {
  background: #3498db;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #2980b9;
}

.editor-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1px;
  background: #e0e0e0;
}

.code-area,
.io-area {
  background: white;
  padding: 16px;
}

.code-textarea {
  width: 100%;
  height: 400px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
}

.code-textarea:focus {
  outline: none;
  border-color: #3498db;
}

.io-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-section,
.output-section {
  flex: 1;
}

.io-area h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.io-textarea {
  width: 100%;
  height: 150px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  resize: vertical;
}

.io-textarea:focus {
  outline: none;
  border-color: #3498db;
}

.output-content {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #f8f9fa;
  max-height: 200px;
  overflow-y: auto;
}

.output-content.success {
  border-color: #27ae60;
  background: #d4edda;
}

.output-content.error {
  border-color: #e74c3c;
  background: #f8d7da;
}

.output-content.compile-error {
  border-color: #f39c12;
  background: #fff3cd;
}

.output-status {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-success {
  background: #27ae60;
  color: white;
}

.status-error {
  background: #e74c3c;
  color: white;
}

.status-compile-error {
  background: #f39c12;
  color: white;
}

.status-pending {
  background: #95a5a6;
  color: white;
}

.time-info,
.memory-info {
  font-size: 12px;
  color: #7f8c8d;
}

.output-text {
  margin-bottom: 12px;
}

.output-text:last-child {
  margin-bottom: 0;
}

.output-text strong {
  display: block;
  margin-bottom: 4px;
  color: #2c3e50;
}

.output-text pre {
  margin: 0;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.output-text.error pre {
  color: #e74c3c;
}

.output-placeholder {
  padding: 40px 20px;
  text-align: center;
  color: #95a5a6;
  font-style: italic;
}

.test-results {
  padding: 20px;
  border-top: 1px solid #e0e0e0;
}

.test-results h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
}

.test-cases {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.test-case {
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 6px;
  background: #f8f9fa;
}

.test-case.passed {
  border-color: #27ae60;
  background: #d4edda;
}

.test-case.failed {
  border-color: #e74c3c;
  background: #f8d7da;
}

.test-case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

.test-case-number {
  font-weight: 600;
  color: #2c3e50;
}

.test-case-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.test-case.passed .test-case-status {
  color: #27ae60;
}

.test-case.failed .test-case-status {
  color: #e74c3c;
}

.test-case-details {
  font-size: 14px;
}

.test-case-details > div {
  margin-bottom: 4px;
}

.test-case-details strong {
  color: #2c3e50;
  margin-right: 8px;
}
</style>
