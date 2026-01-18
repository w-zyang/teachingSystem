<template>
  <div class="debug-container">
    <h1>🔍 课堂总结调试工具</h1>
    
    <div class="debug-section">
      <h2>1. API 连接测试</h2>
      <button @click="testAPI" class="test-btn">测试 API 连接</button>
      <div v-if="apiResult" class="result-box" :class="{ error: apiResult.error }">
        <h3>{{ apiResult.title }}</h3>
        <pre>{{ JSON.stringify(apiResult.data, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>2. 数据库查询结果</h2>
      <button @click="loadSummaries" class="test-btn">加载课堂总结</button>
      <div v-if="summaries.length > 0" class="result-box">
        <h3>✅ 找到 {{ summaries.length }} 条记录</h3>
        <div v-for="summary in summaries" :key="summary.id" class="summary-item">
          <p><strong>ID:</strong> {{ summary.id }}</p>
          <p><strong>标题:</strong> {{ summary.title }}</p>
          <p><strong>状态:</strong> {{ summary.status }}</p>
          <p><strong>课程:</strong> {{ summary.courseName }}</p>
          <p><strong>教师:</strong> {{ summary.teacherName }}</p>
          <p><strong>发布时间:</strong> {{ summary.publishTime }}</p>
        </div>
      </div>
      <div v-else-if="loaded" class="result-box error">
        <h3>❌ 未找到已发布的课堂总结</h3>
        <p>可能的原因：</p>
        <ul>
          <li>教师端未发布课堂总结（status 不是 'PUBLISHED'）</li>
          <li>后端接口返回空数据</li>
          <li>数据库查询条件有误</li>
        </ul>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>3. 网络请求日志</h2>
      <div class="result-box">
        <pre>{{ requestLog }}</pre>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>4. 环境信息</h2>
      <div class="result-box">
        <p><strong>API Base URL:</strong> {{ baseURL }}</p>
        <p><strong>完整 API 地址:</strong> {{ fullAPIUrl }}</p>
        <p><strong>Token:</strong> {{ hasToken ? '✅ 已设置' : '❌ 未设置' }}</p>
        <p><strong>用户角色:</strong> {{ userRole }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import classSummaryApi from '@/api/classSummary'

const apiResult = ref(null)
const summaries = ref([])
const loaded = ref(false)
const requestLog = ref('等待请求...')

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const fullAPIUrl = computed(() => `${baseURL}/api/class-summary/published`)
const hasToken = computed(() => !!localStorage.getItem('token'))
const userRole = computed(() => localStorage.getItem('userRole') || '未设置')

const testAPI = async () => {
  try {
    requestLog.value = '正在发送请求...\n'
    requestLog.value += `URL: ${fullAPIUrl.value}\n`
    requestLog.value += `Method: GET\n`
    requestLog.value += `Token: ${hasToken.value ? '已设置' : '未设置'}\n\n`
    
    const response = await fetch(fullAPIUrl.value, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(localStorage.getItem('token') && {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        })
      }
    })
    
    requestLog.value += `响应状态: ${response.status} ${response.statusText}\n`
    
    const data = await response.json()
    
    requestLog.value += `响应数据: ${JSON.stringify(data, null, 2)}\n`
    
    apiResult.value = {
      title: response.ok ? '✅ API 连接成功' : '❌ API 返回错误',
      data: data,
      error: !response.ok
    }
  } catch (error) {
    requestLog.value += `错误: ${error.message}\n`
    requestLog.value += `堆栈: ${error.stack}\n`
    
    apiResult.value = {
      title: '❌ API 连接失败',
      data: {
        error: error.message,
        stack: error.stack
      },
      error: true
    }
  }
}

const loadSummaries = async () => {
  try {
    requestLog.value = '正在加载课堂总结...\n'
    
    const response = await classSummaryApi.getAllPublishedSummaries()
    
    requestLog.value += `API 响应: ${JSON.stringify(response, null, 2)}\n`
    
    if (response.code === 1) {
      summaries.value = response.data || []
      requestLog.value += `成功加载 ${summaries.value.length} 条记录\n`
    } else {
      requestLog.value += `API 返回错误: ${response.msg || response.message}\n`
    }
    
    loaded.value = true
  } catch (error) {
    requestLog.value += `加载失败: ${error.message}\n`
    console.error('加载课堂总结失败:', error)
    loaded.value = true
  }
}
</script>

<style scoped>
.debug-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  color: #2c3e50;
  border-bottom: 3px solid #3498db;
  padding-bottom: 10px;
  margin-bottom: 30px;
}

.debug-section {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.debug-section h2 {
  color: #34495e;
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
}

.test-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.test-btn:hover {
  background: #2980b9;
}

.result-box {
  margin-top: 15px;
  padding: 15px;
  background: #ecf0f1;
  border-left: 4px solid #3498db;
  border-radius: 4px;
}

.result-box.error {
  background: #fadbd8;
  border-left-color: #e74c3c;
}

.result-box h3 {
  margin-top: 0;
  color: #2c3e50;
}

.result-box pre {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.5;
}

.result-box ul {
  margin: 10px 0;
  padding-left: 20px;
}

.result-box li {
  margin: 5px 0;
  color: #e74c3c;
}

.summary-item {
  background: white;
  padding: 15px;
  margin: 10px 0;
  border-radius: 4px;
  border: 1px solid #bdc3c7;
}

.summary-item p {
  margin: 5px 0;
  color: #34495e;
}

.summary-item strong {
  color: #2c3e50;
  font-weight: 600;
}
</style>
