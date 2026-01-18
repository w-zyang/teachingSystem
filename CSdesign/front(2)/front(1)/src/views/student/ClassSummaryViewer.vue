<template>
  <div class="class-summary-viewer">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>📚 课堂重点整理</h2>
      <p class="subtitle">查看教师发布的课堂重点整理文档</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课堂总结..."
            @keyup.enter="searchSummaries"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="searchSummaries">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 课堂总结列表 -->
    <div class="summary-list" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="8" v-for="summary in summaryList" :key="summary.id" style="margin-bottom: 20px;">
          <el-card class="summary-card" @click="viewSummary(summary)">
            <div class="card-header">
              <h3>{{ summary.title }}</h3>
              <el-tag size="small" type="success">{{ summary.courseName }}</el-tag>
            </div>
            <p class="card-desc">{{ summary.description }}</p>
            <div class="card-meta">
              <span>👨‍🏫 {{ summary.teacherName }}</span>
              <span>📅 {{ formatDate(summary.publishTime) }}</span>
              <span>👁️ {{ summary.viewCount || 0 }}</span>
            </div>
            <div class="card-actions">
              <el-button type="primary" size="small" @click.stop="viewSummary(summary)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <div v-if="summaryList.length === 0 && !loading" class="empty-state">
        <p>暂无课堂总结</p>
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" :title="selectedSummary?.title" width="80%">
      <div v-if="selectedSummary" class="summary-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag type="success" size="small">{{ selectedSummary.courseName }}</el-tag>
            <span>教师：{{ selectedSummary.teacherName }}</span>
            <span>发布时间：{{ formatDate(selectedSummary.publishTime) }}</span>
          </div>
          <p>{{ selectedSummary.description }}</p>
        </div>
        
        <div class="detail-content">
          <div class="content-viewer" v-html="renderMarkdown(selectedSummary.finalContent)"></div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="downloadSummary" type="primary">下载文档</el-button>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import classSummaryApi from '@/api/classSummary'

export default {
  name: 'ClassSummaryViewer',
  components: {
    Search
  },
  setup() {
    const loading = ref(false)
    const summaryList = ref([])
    const searchKeyword = ref('')
    const showDetailDialog = ref(false)
    const selectedSummary = ref(null)
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    const renderMarkdown = (content) => {
      if (!content) return ''
      return content
        .replace(/### (.*)/g, '<h3>$1</h3>')
        .replace(/## (.*)/g, '<h2>$1</h2>')
        .replace(/# (.*)/g, '<h1>$1</h1>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/`(.*?)`/g, '<code>$1</code>')
        .replace(/\n/g, '<br>')
    }
    
    const loadAllSummaries = async () => {
      loading.value = true
      try {
        console.log('========== 开始加载课堂总结 ==========')
        const response = await classSummaryApi.getAllPublishedSummaries()
        console.log('API 响应:', response)
        console.log('响应 success:', response.success)
        console.log('响应 code:', response.code)
        console.log('响应数据:', response.data)
        
        // 兼容两种响应格式：{ success: true } 或 { code: 1 }
        if (response.success === true || response.code === 1) {
          summaryList.value = response.data || []
          console.log('成功加载课堂总结，数量:', summaryList.value.length)
          if (summaryList.value.length === 0) {
            console.warn('⚠️ API 返回成功但数据为空')
            ElMessage.warning('暂无已发布的课堂总结')
          } else {
            ElMessage.success(`成功加载 ${summaryList.value.length} 个课堂总结`)
          }
        } else {
          console.error('API 返回错误，响应:', response)
          ElMessage.error(response.msg || '加载失败')
        }
      } catch (error) {
        console.error('========== 加载失败 ==========')
        console.error('错误对象:', error)
        console.error('错误信息:', error.message)
        console.error('错误响应:', error.response)
        ElMessage.error('加载课堂总结失败: ' + (error.message || '未知错误'))
      } finally {
        loading.value = false
        console.log('========== 加载完成 ==========')
      }
    }
    
    const searchSummaries = async () => {
      if (!searchKeyword.value.trim()) {
        await loadAllSummaries()
        return
      }
      
      loading.value = true
      try {
        const response = await classSummaryApi.searchPublishedSummaries(searchKeyword.value)
        console.log('搜索响应:', response)
        
        // 兼容两种响应格式：{ success: true } 或 { code: 1 }
        if (response.success === true || response.code === 1) {
          summaryList.value = response.data || []
          ElMessage.success(`找到 ${summaryList.value.length} 个相关结果`)
        } else {
          ElMessage.error(response.msg || '搜索失败')
        }
      } catch (error) {
        console.error('搜索失败:', error)
        ElMessage.error('搜索失败')
      } finally {
        loading.value = false
      }
    }
    
    const viewSummary = (summary) => {
      selectedSummary.value = summary
      showDetailDialog.value = true
    }
    
    const downloadSummary = () => {
      if (!selectedSummary.value) return
      
      const content = selectedSummary.value.finalContent
      const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${selectedSummary.value.title}.md`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      
      ElMessage.success('文档下载成功')
    }
    
    onMounted(() => {
      loadAllSummaries()
    })
    
    return {
      loading,
      summaryList,
      searchKeyword,
      showDetailDialog,
      selectedSummary,
      formatDate,
      renderMarkdown,
      searchSummaries,
      viewSummary,
      downloadSummary
    }
  }
}
</script>

<style scoped>
.class-summary-viewer {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.subtitle {
  color: #909399;
  margin: 0;
}

/* 搜索区域 */
.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 课堂总结卡片 */
.summary-list {
  min-height: 400px;
}

.summary-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  flex: 1;
  margin-right: 12px;
}

.card-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  margin: 16px 0;
  padding: 12px 0;
  border-top: 1px solid #e4e7ed;
  font-size: 12px;
  color: #909399;
}

.card-meta span {
  display: block;
  margin-bottom: 4px;
}

.card-actions {
  text-align: center;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px;
  color: #909399;
}

/* 详情对话框 */
.summary-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
  font-size: 14px;
  color: #606266;
}

.content-viewer {
  background: #fafafa;
  padding: 24px;
  border-radius: 8px;
  line-height: 1.8;
}

.content-viewer h1,
.content-viewer h2,
.content-viewer h3 {
  color: #303133;
  margin: 20px 0 12px 0;
}

.content-viewer h1 {
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.content-viewer code {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e6a23c;
}
</style> 