<template>
  <div class="code-editor">
    <div class="editor-header">
      <h3>{{ title }}</h3>
      <div class="editor-controls">
        <!-- 语言选择下拉列表 -->
        <select v-model="selectedLanguage" class="language-select">
          <option value="71">Python</option>
          <option value="50">C</option>
        </select>
        <button @click="resetCode" class="btn btn-reset">
          <i class="fas fa-redo"></i>
          重置
        </button>
        <button @click="runCode" :disabled="isRunning" class="btn btn-run">
          <i v-if="isRunning" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-play"></i>
          {{ isRunning ? '运行中...' : '运行代码' }}
        </button>
      </div>
    </div>

    <div class="editor-body">
      <!-- 代码编辑区 -->
      <div class="code-area">
        <textarea
          v-model="code"
          class="code-textarea"
          placeholder="请在此处编写代码..."
          spellcheck="false"
        ></textarea>
      </div>
    </div>

    <!-- 红色分隔线 -->
    <div class="divider-line"></div>

    <!-- 输出结果区域 -->
    <div class="result-area">
      <div class="result-header">
        <h4>运行结果</h4>
        <span v-if="output" class="status-badge" :class="statusClass">
          {{ output.status }}
        </span>
      </div>
      
      <div v-if="output" class="output-content" :class="outputClass">
        <div v-if="output.stdout" class="output-section">
          <div class="output-label">📤 标准输出：</div>
          <pre class="output-text">{{ output.stdout }}</pre>
        </div>

        <div v-if="output.stderr" class="output-section error">
          <div class="output-label">❌ 错误输出：</div>
          <pre class="output-text">{{ output.stderr }}</pre>
        </div>

        <div v-if="output.compileOutput" class="output-section error">
          <div class="output-label">⚠️ 编译错误：</div>
          <pre class="output-text">{{ output.compileOutput }}</pre>
        </div>

        <div v-if="output.message" class="output-section">
          <div class="output-label">💬 消息：</div>
          <pre class="output-text">{{ output.message }}</pre>
        </div>

        <div v-if="output.exitCode !== undefined" class="output-section">
          <div class="output-label">🔢 退出码：</div>
          <span class="exit-code">{{ output.exitCode }}</span>
        </div>
      </div>
      
      <div v-else class="output-placeholder">
        <i class="fas fa-info-circle"></i>
        点击"运行代码"按钮后，结果将显示在这里
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
import { ref, computed, watch } from 'vue'
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

const emit = defineEmits(['submit-success', 'code-change'])

// 默认代码模板
const codeTemplates = {
  '71': `# Python 代码
# 在这里编写你的代码

`,
  '50': `// C 代码
#include <stdio.h>

int main() {
    // 在这里编写你的代码
    
    return 0;
}
`
}

// 响应式数据
const code = ref(props.defaultCode || codeTemplates[props.defaultLanguage])
const stdin = ref('')
const selectedLanguage = ref(props.defaultLanguage)
const output = ref(null)
const isRunning = ref(false)
const isSubmitting = ref(false)
const testResults = ref([])

// 监听语言切换，更新代码模板
watch(selectedLanguage, (newLang) => {
  if (!code.value.trim() || code.value === codeTemplates['71'] || code.value === codeTemplates['50']) {
    code.value = codeTemplates[newLang]
  }
})

// 监听代码变化，通知父组件
watch(code, (newCode) => {
  emit('code-change', newCode)
})

// 计算属性
const outputClass = computed(() => {
  if (!output.value) return ''
  
  // 如果有编译错误或stderr，显示错误样式
  if (output.value.compileOutput || output.value.stderr) {
    return 'error'
  }
  
  // 如果有stdout，显示成功样式
  if (output.value.stdout) {
    return 'success'
  }
  
  // 根据statusId判断
  const statusId = output.value.statusId
  if (statusId === 3) return 'success'
  if (statusId === 6) return 'compile-error'
  if (statusId >= 4) return 'error'
  
  return ''
})

const statusClass = computed(() => {
  if (!output.value) return ''
  
  // 如果有编译错误或stderr，显示错误状态
  if (output.value.compileOutput || output.value.stderr) {
    return 'status-error'
  }
  
  // 如果有stdout，显示成功状态
  if (output.value.stdout) {
    return 'status-success'
  }
  
  // 根据statusId判断
  const statusId = output.value.statusId
  if (statusId === 3) return 'status-success'
  if (statusId === 6) return 'status-compile-error'
  if (statusId >= 4) return 'status-error'
  
  return 'status-pending'
})

// 方法
const resetCode = () => {
  code.value = codeTemplates[selectedLanguage.value]
  output.value = null
  testResults.value = []
  ElMessage.info('代码已重置')
}

const runCode = async () => {
  if (!code.value.trim()) {
    ElMessage.warning('请先输入代码')
    return
  }
  
  isRunning.value = true
  output.value = null

  try {
    const response = await axios.post('http://localhost:8080/api/code/run', {
      sourceCode: code.value,
      languageId: parseInt(selectedLanguage.value),
      stdin: stdin.value
    })

    console.log('API 完整响应:', response)
    console.log('API 响应数据:', response.data)

    // 检查 success 字段（而不是 code 字段）
    if (response.data.success === true && response.data.data) {
      const result = response.data.data
      console.log('解析的结果数据:', result)
      
      // 构建输出对象
      output.value = {
        status: result.status || '完成',
        statusId: result.statusId || 3,
        stdout: result.stdout || result.output || '',
        stderr: result.stderr || '',
        compileOutput: result.compileOutput || result.compile_output || '',
        message: result.message || '',
        exitCode: result.exitCode !== undefined ? result.exitCode : (result.exit_code !== undefined ? result.exit_code : 0)
      }
      
      console.log('设置的 output.value:', output.value)
      
      // 根据结果显示不同的消息
      if (output.value.stderr || output.value.compileOutput) {
        ElMessage.warning('代码运行完成，但有错误或警告')
      } else if (output.value.stdout) {
        ElMessage.success('代码运行成功')
      } else {
        ElMessage.info('代码运行完成')
      }
    } else {
      ElMessage.error(response.data.msg || '运行失败')
      output.value = {
        status: '错误',
        statusId: 14,
        message: response.data.msg || '运行失败',
        stderr: response.data.msg || '运行失败'
      }
    }
  } catch (error) {
    console.error('运行代码失败:', error)
    ElMessage.error('运行失败: ' + (error.response?.data?.msg || error.message))
    output.value = {
      status: '错误',
      statusId: 14,
      message: error.response?.data?.msg || error.message,
      stderr: error.response?.data?.msg || error.message
    }
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
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-reset {
  background: #95a5a6;
  color: white;
}

.btn-reset:hover:not(:disabled) {
  background: #7f8c8d;
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
  background: white;
  padding: 20px;
}

.code-area {
  width: 100%;
}

.code-textarea {
  width: 100%;
  height: 350px;
  padding: 16px;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  background: #1e1e1e;
  color: #d4d4d4;
}

.code-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.code-textarea::placeholder {
  color: #6a737d;
}

/* 红色分隔线 */
.divider-line {
  height: 3px;
  background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
  box-shadow: 0 2px 4px rgba(231, 76, 60, 0.3);
}

/* 结果区域 */
.result-area {
  background: white;
  padding: 20px;
  min-height: 300px;
  max-height: 500px;
  overflow-y: auto;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #ecf0f1;
}

.result-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.output-content {
  padding: 16px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background: #f8f9fa;
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

.output-section {
  margin-bottom: 16px;
}

.output-section:last-child {
  margin-bottom: 0;
}

.output-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 14px;
}

.output-text {
  margin: 0;
  padding: 12px;
  background: white;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: #2c3e50;
  border: 1px solid #e0e0e0;
}

.output-section.error .output-text {
  color: #e74c3c;
  background: #fff5f5;
}

.exit-code {
  display: inline-block;
  padding: 4px 12px;
  background: white;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-weight: 600;
  color: #2c3e50;
  border: 1px solid #e0e0e0;
}

.output-placeholder {
  padding: 60px 20px;
  text-align: center;
  color: #95a5a6;
  font-size: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.output-placeholder i {
  font-size: 48px;
  opacity: 0.5;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
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
