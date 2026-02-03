<template>
  <el-dialog
    v-model="visible"
    title="课件更新建议"
    width="900px"
    @close="handleClose"
  >
    <div v-loading="loading" class="suggestions-container">
      <div v-if="suggestions && suggestions.status === 'suggestions_ready'">
        <!-- 文件信息 -->
        <div class="file-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="文件名">
              {{ suggestions.fileName }}
            </el-descriptions-item>
            <el-descriptions-item label="更新要求">
              {{ suggestions.updateRequirements }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- AI建议 -->
        <div class="suggestions-content">
          <h3>🤖 AI更新建议</h3>
          <div v-if="parsedSuggestions.length > 0" class="suggestions-list">
            <el-card 
              v-for="(item, index) in parsedSuggestions" 
              :key="index"
              class="suggestion-item"
              shadow="hover"
            >
              <template #header>
                <div class="card-header">
                  <span class="page-number">{{ item.page || `第 ${index + 1} 项` }}</span>
                  <el-tag type="primary">{{ item.section || '更新建议' }}</el-tag>
                </div>
              </template>
              <div class="suggestion-body">
                <div v-if="item.oldContent" class="old-content-box">
                  <h4>📄 原有内容：</h4>
                  <p>{{ item.oldContent }}</p>
                </div>
                <div class="suggestion-box">
                  <h4>💡 更新建议：</h4>
                  <p>{{ item.suggestion }}</p>
                </div>
                <div class="content-box" v-if="item.newContent || item.content">
                  <h4>✨ 推荐内容：</h4>
                  <p>{{ item.newContent || item.content }}</p>
                </div>
              </div>
            </el-card>
          </div>
          <div v-else class="raw-suggestions">
            <pre>{{ suggestions.suggestions }}</pre>
          </div>
        </div>
      </div>

      <div v-else-if="suggestions && suggestions.status === 'analyzing'" class="status-message">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>AI正在分析课件，生成更新建议...</p>
      </div>

      <div v-else-if="suggestions && suggestions.status === 'failed'" class="status-message error">
        <el-icon><CircleClose /></el-icon>
        <p>分析失败，请重试</p>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" v-if="suggestions && suggestions.status === 'suggestions_ready'">
        导出建议
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, defineProps, defineEmits } from 'vue'
import { Loading, CircleClose } from '@element-plus/icons-vue'
import * as smartTeachingApi from '@/api/smartTeaching'

const props = defineProps({
  updateId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close'])

const visible = ref(true)
const loading = ref(false)
const suggestions = ref(null)

const parsedSuggestions = computed(() => {
  if (!suggestions.value || !suggestions.value.suggestions) return []
  
  try {
    // 尝试提取JSON内容（AI可能在JSON前后添加了说明文字）
    let jsonContent = suggestions.value.suggestions
    
    // 如果响应包含```json标记，提取其中的JSON
    if (jsonContent.includes('```json')) {
      const startIdx = jsonContent.indexOf('```json') + 7
      const endIdx = jsonContent.indexOf('```', startIdx)
      if (endIdx > startIdx) {
        jsonContent = jsonContent.substring(startIdx, endIdx).trim()
      }
    } else if (jsonContent.includes('```')) {
      // 如果只有```标记
      const startIdx = jsonContent.indexOf('```') + 3
      const endIdx = jsonContent.indexOf('```', startIdx)
      if (endIdx > startIdx) {
        jsonContent = jsonContent.substring(startIdx, endIdx).trim()
      }
    }
    
    // 尝试找到第一个{和最后一个}
    const firstBrace = jsonContent.indexOf('{')
    const lastBrace = jsonContent.lastIndexOf('}')
    if (firstBrace >= 0 && lastBrace > firstBrace) {
      jsonContent = jsonContent.substring(firstBrace, lastBrace + 1)
    }
    
    const data = JSON.parse(jsonContent)
    if (data.updates && Array.isArray(data.updates)) {
      return data.updates
    }
  } catch (e) {
    console.log('无法解析JSON格式的建议:', e)
  }
  
  return []
})

const loadSuggestions = async () => {
  loading.value = true
  try {
    const res = await smartTeachingApi.getCoursewareSuggestions(props.updateId)
    // 兼容两种响应格式
    if (res.code === 1 || res.success === true) {
      suggestions.value = res.data
    }
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  emit('close')
}

onMounted(() => {
  loadSuggestions()
})
</script>

<style scoped>
.suggestions-container {
  min-height: 400px;
}

.file-info {
  margin-bottom: 24px;
}

.suggestions-content h3 {
  color: #409eff;
  font-size: 20px;
  margin-bottom: 16px;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-number {
  font-weight: 600;
  color: #333;
}

.suggestion-body {
  padding: 8px 0;
}

.old-content-box {
  background: #fff3e0;
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #ff9800;
  margin-bottom: 12px;
}

.old-content-box h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #ff9800;
}

.old-content-box p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.suggestion-box {
  background: #e3f2fd;
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #2196f3;
  margin-bottom: 12px;
}

.suggestion-box h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #2196f3;
}

.suggestion-box p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.suggestion-text {
  font-size: 15px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 12px;
}

.content-box {
  background: #e8f5e9;
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #4caf50;
}

.content-box h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #4caf50;
}

.content-box p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.raw-suggestions {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  max-height: 500px;
  overflow-y: auto;
}

.raw-suggestions pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
  line-height: 1.6;
}

.status-message {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.status-message .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.status-message.error {
  color: #f56c6c;
}

.status-message p {
  font-size: 16px;
  margin: 0;
}
</style>

