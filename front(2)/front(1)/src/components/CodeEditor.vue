<template>
  <div class="code-editor">
    <div class="editor-header">
      <div class="header-left">
        <h3>{{ title }}</h3>
        <!-- 语言选择下拉列表 -->
        <select v-model="selectedLanguage" class="language-select">
          <option value="62">Java</option>
          <option value="71">Python</option>
          <option value="50">C</option>
          <option value="54">C++</option>
          <option value="63">JavaScript</option>
          <option value="60">Go</option>
          <option value="51">C#</option>
          <option value="73">Rust</option>
          <option value="68">PHP</option>
          <option value="72">Ruby</option>
        </select>
      </div>
      <div class="editor-controls">
        <button @click="saveCode" class="btn btn-save" title="保存代码到本地">
          <i class="fas fa-save"></i>
          <span class="btn-text">保存代码</span>
        </button>
        <button @click="resetCode" class="btn btn-reset" title="重置为初始代码">
          <i class="fas fa-redo"></i>
          <span class="btn-text">重置</span>
        </button>
        <button @click="runCode" :disabled="isRunning" class="btn btn-run" title="运行代码查看输出">
          <i v-if="isRunning" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-play"></i>
          <span class="btn-text">{{ isRunning ? '运行中...' : '运行代码' }}</span>
        </button>
        <button v-if="testCases.length > 0" @click="submitCode" :disabled="isSubmitting" class="btn btn-submit" title="提交代码进行评测">
          <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-check"></i>
          <span class="btn-text">{{ isSubmitting ? '评测中...' : '提交代码' }}</span>
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

    <!-- 输入输出区域 -->
    <div class="io-area">
      <!-- 输入区域 -->
      <div class="input-section">
        <div class="section-header">
          <h4>📥 输入</h4>
        </div>
        <textarea
          v-model="stdin"
          class="input-textarea"
          placeholder="请输入测试数据..."
          spellcheck="false"
        ></textarea>
      </div>

      <!-- 输出区域 -->
      <div class="output-section-wrapper">
        <div class="section-header">
          <h4>📤 输出</h4>
          <span v-if="output" class="status-badge" :class="statusClass">
            {{ output.status }}
          </span>
        </div>
        
        <div v-if="output" class="output-content" :class="outputClass">
          <div v-if="output.stdout" class="output-block">
            <pre class="output-text">{{ output.stdout }}</pre>
          </div>

          <div v-if="output.stderr" class="output-block error">
            <div class="output-label">错误输出：</div>
            <pre class="output-text">{{ output.stderr }}</pre>
          </div>

          <div v-if="output.compileOutput" class="output-block error">
            <div class="output-label">编译错误：</div>
            <pre class="output-text">{{ output.compileOutput }}</pre>
          </div>

          <div v-if="output.message" class="output-block">
            <pre class="output-text">{{ output.message }}</pre>
          </div>
        </div>
        
        <div v-else class="output-placeholder">
          <i class="fas fa-info-circle"></i>
          点击"运行代码"查看结果
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
    default: '50' // C语言
  },
  defaultCode: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['submit-success', 'code-change'])

// 默认代码模板
const codeTemplates = {
  '62': `// Java 代码
public class Main {
    public static void main(String[] args) {
        // 在这里编写你的代码
        
    }
}
`,
  '71': `# Python 代码
# 在这里编写你的代码

`,
  '50': `// C 代码
#include <stdio.h>

int main() {
    // 在这里编写你的代码
    
    return 0;
}
`,
  '54': `// C++ 代码
#include <iostream>
using namespace std;

int main() {
    // 在这里编写你的代码
    
    return 0;
}
`,
  '63': `// JavaScript 代码
// 在这里编写你的代码

`,
  '60': `// Go 代码
package main

import "fmt"

func main() {
    // 在这里编写你的代码
    
}
`,
  '51': `// C# 代码
using System;

class Program {
    static void Main() {
        // 在这里编写你的代码
        
    }
}
`,
  '73': `// Rust 代码
fn main() {
    // 在这里编写你的代码
    
}
`,
  '68': `<?php
// PHP 代码
// 在这里编写你的代码

?>
`,
  '72': `# Ruby 代码
# 在这里编写你的代码

`
}

// 响应式数据
const code = ref('')
const stdin = ref('')
const selectedLanguage = ref(props.defaultLanguage)
const output = ref(null)
const isRunning = ref(false)
const isSubmitting = ref(false)
const testResults = ref([])

// 初始化代码：优先从localStorage恢复，否则使用默认代码或模板
const initCode = () => {
  const saveKey = `code_${props.questionId || 'default'}_${selectedLanguage.value}`
  const savedData = localStorage.getItem(saveKey)
  
  if (savedData) {
    try {
      const data = JSON.parse(savedData)
      code.value = data.code
      console.log('从本地恢复代码:', saveKey)
      return
    } catch (e) {
      console.error('恢复代码失败:', e)
    }
  }
  
  // 如果没有保存的代码，使用默认代码或模板
  code.value = props.defaultCode || codeTemplates[props.defaultLanguage]
}

// 初始化代码
initCode()

// 监听语言切换，更新代码模板
watch(selectedLanguage, (newLang) => {
  // 尝试恢复该语言的保存代码
  const saveKey = `code_${props.questionId || 'default'}_${newLang}`
  const savedData = localStorage.getItem(saveKey)
  
  if (savedData) {
    try {
      const data = JSON.parse(savedData)
      code.value = data.code
      console.log('切换语言，恢复保存的代码:', saveKey)
      return
    } catch (e) {
      console.error('恢复代码失败:', e)
    }
  }
  
  // 检查当前代码是否是某个模板
  const isTemplate = Object.values(codeTemplates).some(template => code.value.trim() === template.trim())
  
  if (!code.value.trim() || isTemplate) {
    code.value = codeTemplates[newLang] || codeTemplates['71']
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
const saveCode = () => {
  if (!code.value.trim()) {
    ElMessage.warning('代码为空，无需保存')
    return
  }
  
  // 保存到 localStorage
  const saveKey = `code_${props.questionId || 'default'}_${selectedLanguage.value}`
  const saveData = {
    code: code.value,
    language: selectedLanguage.value,
    timestamp: Date.now(),
    questionId: props.questionId
  }
  
  localStorage.setItem(saveKey, JSON.stringify(saveData))
  ElMessage.success('代码已保存到本地')
}

const resetCode = () => {
  code.value = codeTemplates[selectedLanguage.value]
  output.value = null
  testResults.value = []
  stdin.value = ''
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
      stdin: stdin.value || ''
    })

    // 检查 success 字段（而不是 code 字段）
    if (response.data.success === true && response.data.data) {
      const result = response.data.data
      
      // 构建输出对象
      output.value = {
        status: result.status || '完成',
        statusId: result.statusId || 3,
        stdout: result.stdout || result.output || '',
        stderr: result.stderr || '',
        compileOutput: result.compileOutput || result.compile_output || '',
        message: result.message || ''
      }
      
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
    // 使用 Piston 批量执行测试用例
    const response = await axios.post('http://localhost:8080/api/code/run-batch', {
      sourceCode: code.value,
      languageId: parseInt(selectedLanguage.value),
      testCases: props.testCases.map(tc => ({
        input: tc.input,
        output: tc.output
      }))
    })

    if (response.data.success) {
      const results = response.data.data
      ElMessage.success('代码评测完成')

      // 直接处理结果（Piston 直接返回结果，不需要轮询）
      processResults(results)
    } else {
      ElMessage.error(response.data.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交代码失败:', error)
    ElMessage.error('提交失败: ' + (error.response?.data?.msg || error.message))
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

const processResults = (pistonResults) => {
  const results = []
  
  pistonResults.forEach((result, index) => {
    const actualOutput = (result.stdout || '').trim()
    const expectedOutput = (props.testCases[index].output || '').trim()
    const passed = actualOutput === expectedOutput
    
    results.push({
      input: props.testCases[index].input,
      expectedOutput: expectedOutput,
      actualOutput: actualOutput,
      passed: passed,
      status: result.status || '完成'
    })
  })
  
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
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.editor-header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.editor-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.language-select {
  padding: 6px 10px;
  border: 1px solid #34495e;
  border-radius: 4px;
  background: #34495e;
  color: white;
  font-size: 13px;
  cursor: pointer;
}

.language-select:focus {
  outline: none;
  border-color: #3498db;
}

.language-select option {
  background: #2c3e50;
  color: white;
}

.btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.btn:hover:not(:disabled) {
  opacity: 0.9;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn i {
  font-size: 13px;
}

.btn-text {
  font-size: 13px;
}

.btn-save {
  background: #9b59b6;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background: #8e44ad;
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

/* 响应式设计 */
@media (max-width: 1024px) {
  .btn-text {
    display: none;
  }
  
  .btn {
    padding: 6px 10px;
  }
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
  height: 700px;
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

/* 输入输出区域 */
.io-area {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding: 15px 20px;
  background: #f8f9fa;
}

.input-section,
.output-section-wrapper {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 2px solid #e0e0e0;
  min-height: 30px;
  flex-shrink: 0;
}

.section-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 15px;
  font-weight: 600;
}

.input-textarea {
  width: 100%;
  height: 180px;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  resize: none;
  background: white;
  color: #2c3e50;
  box-sizing: border-box;
}

.input-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.input-textarea::placeholder {
  color: #95a5a6;
}

.output-content {
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 6px;
  background: white;
  height: 180px;
  overflow-y: auto;
  box-sizing: border-box;
}

.output-content.success {
  border-color: #27ae60;
  background: #f0f9ff;
}

.output-content.error {
  border-color: #e74c3c;
  background: #fff5f5;
}

.output-content.compile-error {
  border-color: #f39c12;
  background: #fffbf0;
}

.output-block {
  margin-bottom: 8px;
}

.output-block:last-child {
  margin-bottom: 0;
}

.output-label {
  font-weight: 600;
  color: #e74c3c;
  margin-bottom: 6px;
  font-size: 13px;
}

.output-text {
  margin: 0;
  padding: 8px;
  background: transparent;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: #2c3e50;
  border: none;
}

.output-block.error .output-text {
  color: #e74c3c;
  background: transparent;
}

.output-placeholder {
  padding: 40px 20px;
  text-align: center;
  color: #95a5a6;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  height: 180px;
  justify-content: center;
  box-sizing: border-box;
  border: 2px solid #ddd;
  border-radius: 6px;
  background: white;
}

.output-placeholder i {
  font-size: 32px;
  opacity: 0.5;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .io-area {
    grid-template-columns: 1fr;
  }
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
