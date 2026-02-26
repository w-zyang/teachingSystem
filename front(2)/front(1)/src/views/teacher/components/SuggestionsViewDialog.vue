<template>
  <el-dialog
    v-model="visible"
    title="课件更新建议"
    width="1100px"
    @close="handleClose"
    class="suggestions-dialog"
  >
    <div v-loading="loading" class="suggestions-container">
      <div v-if="suggestions && suggestions.status === 'suggestions_ready'">
        <!-- 文件信息 -->
        <div class="file-info">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="文件名">
              {{ suggestions.fileName }}
            </el-descriptions-item>
            <el-descriptions-item label="更新要求">
              {{ suggestions.updateRequirements }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 翻页式建议区域 -->
        <div v-if="parsedSuggestions.length > 0" class="pager-wrapper">
          <!-- 顶部进度点 -->
          <div class="page-progress-bar">
            <div class="page-indicators">
              <span
                v-for="(_, i) in parsedSuggestions"
                :key="i"
                :class="['dot', { active: i === currentPage }]"
                @click="currentPage = i"
              ></span>
            </div>
            <span class="page-counter">{{ currentPage + 1 }} / {{ parsedSuggestions.length }}</span>
          </div>

          <!-- 当前页内容 -->
          <transition :name="transitionName" mode="out-in">
            <div :key="currentPage" class="slide-suggestion-layout">

              <!-- 左侧：原始 PPT 幻灯片（真实截图 / 降级文本） -->
              <div class="ppt-slide-panel">
                <div class="panel-label">
                  <span class="label-icon">📄</span>
                  <span>原始幻灯片</span>
                  <span class="panel-subtitle">（{{ currentItem.page || `第 ${currentPage + 1} 页` }}）</span>
                </div>

                <!-- ① 优先：展示渲染好的图片 -->
                <div v-if="matchedSlideImage" class="ppt-slide-img-wrap">
                  <img
                    :src="`http://localhost:8080${matchedSlideImage}`"
                    :alt="`第 ${extractPageNum(currentItem.page)} 页幻灯片`"
                    class="ppt-slide-img"
                  />
                </div>

                <!-- ② 降级：无图片时用文本模拟版式 -->
                <div v-else class="ppt-slide-frame">
                  <div class="slide-inner">
                    <div class="slide-page-tag">
                      {{ matchedSlide ? `第 ${matchedSlide.pageNum} 页` : (currentItem.page || `第 ${currentPage + 1} 页`) }}
                    </div>
                    <template v-if="matchedSlide">
                      <div class="slide-title">{{ matchedSlide.title || '（无标题）' }}</div>
                      <div class="slide-content">
                        <p v-for="(line, li) in splitLines(matchedSlide.content)" :key="li" class="slide-line">{{ line }}</p>
                        <p v-if="!matchedSlide.content" class="slide-placeholder">（该页无正文内容）</p>
                      </div>
                    </template>
                    <template v-else>
                      <div class="slide-title">{{ currentItem.section || '内容页' }}</div>
                      <div class="slide-content">
                        <p v-for="(line, li) in splitLines(currentItem.oldContent)" :key="li" class="slide-line">{{ line }}</p>
                        <p v-if="!currentItem.oldContent" class="slide-placeholder">（无原始内容摘要）</p>
                      </div>
                    </template>
                  </div>
                </div>
              </div>

              <!-- 右侧：AI 建议 -->
              <div class="suggestion-panel">
                <div class="panel-label">
                  <span class="label-icon">🤖</span>
                  <span>AI 更新建议</span>
                </div>

                <!-- 原有内容（仅无图片时才有意义；有图片也展示，方便对比） -->
                <div class="suggestion-block suggestion-old" v-if="currentItem.oldContent">
                  <div class="block-title">
                    <span class="block-icon">📄</span>
                    原有内容
                  </div>
                  <p>{{ currentItem.oldContent }}</p>
                </div>

                <div class="suggestion-block suggestion-update" v-if="currentItem.suggestion">
                  <div class="block-title">
                    <span class="block-icon">💡</span>
                    更新建议
                  </div>
                  <p>{{ currentItem.suggestion }}</p>
                </div>

                <div class="suggestion-block suggestion-new" v-if="currentItem.newContent || currentItem.content">
                  <div class="block-title">
                    <span class="block-icon">✨</span>
                    推荐内容
                  </div>
                  <p>{{ currentItem.newContent || currentItem.content }}</p>
                </div>
              </div>

            </div>
          </transition>

          <!-- 翻页导航 -->
          <div class="pager-nav">
            <button
              class="nav-btn prev-btn"
              :disabled="currentPage === 0"
              @click="goPage(-1)"
            >
              <span class="nav-arrow">‹</span>
              上一页
            </button>
            <div class="nav-page-list">
              <button
                v-for="i in parsedSuggestions.length"
                :key="i"
                :class="['page-num-btn', { active: currentPage === i - 1 }]"
                @click="currentPage = i - 1"
              >{{ i }}</button>
            </div>
            <button
              class="nav-btn next-btn"
              :disabled="currentPage === parsedSuggestions.length - 1"
              @click="goPage(1)"
            >
              下一页
              <span class="nav-arrow">›</span>
            </button>
          </div>
        </div>

        <!-- 无法解析 JSON 时降级 -->
        <div v-else class="raw-suggestions">
          <pre>{{ suggestions.suggestions }}</pre>
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

// 翻页状态
const currentPage = ref(0)
const transitionName = ref('slide-left')

// ===================== 解析 AI 建议 JSON =====================
const parsedSuggestions = computed(() => {
  if (!suggestions.value || !suggestions.value.suggestions) return []
  try {
    let jsonContent = suggestions.value.suggestions

    if (jsonContent.includes('```json')) {
      const s = jsonContent.indexOf('```json') + 7
      const e = jsonContent.indexOf('```', s)
      if (e > s) jsonContent = jsonContent.substring(s, e).trim()
    } else if (jsonContent.includes('```')) {
      const s = jsonContent.indexOf('```') + 3
      const e = jsonContent.indexOf('```', s)
      if (e > s) jsonContent = jsonContent.substring(s, e).trim()
    }

    const fi = jsonContent.indexOf('[')
    const fb = jsonContent.indexOf('{')

    if (fi >= 0 && (fi < fb || fb < 0)) {
      const li = jsonContent.lastIndexOf(']')
      if (li > fi) {
        const arr = JSON.parse(jsonContent.substring(fi, li + 1))
        if (Array.isArray(arr)) return arr
      }
    } else if (fb >= 0) {
      const lb = jsonContent.lastIndexOf('}')
      if (lb > fb) jsonContent = jsonContent.substring(fb, lb + 1)
    }

    const data = JSON.parse(jsonContent)
    if (data.updates && Array.isArray(data.updates)) return data.updates
  } catch (e) {
    console.log('无法解析JSON格式的建议:', e)
  }
  return []
})

// 当前建议条目
const currentItem = computed(() => parsedSuggestions.value[currentPage.value] || {})

// ===================== 匹配原始幻灯片 =====================
// slideImages 来自后端返回的图片URL列表，如 ["/uploads/ppt-slides/1/slide_1.png", ...]
const slideImages = computed(() => (suggestions.value && suggestions.value.slideImages) || [])

// slides 来自后端返回的 suggestions.slides（文本，降级用），每项 {pageNum, title, content}
const slides = computed(() => (suggestions.value && suggestions.value.slides) || [])

/**
 * 将 AI 建议中的 page 字段（如 "第12页"、"第 12 页"、"12"）解析为数字
 */
const extractPageNum = (pageStr) => {
  if (!pageStr) return null
  const m = String(pageStr).match(/\d+/)
  return m ? parseInt(m[0], 10) : null
}

/**
 * 根据当前建议条目的 page 字段，找到对应的渲染图片 URL
 * slideImages 数组下标从 0 开始对应第 1 页
 */
const matchedSlideImage = computed(() => {
  if (slideImages.value.length === 0) return null
  const num = extractPageNum(currentItem.value.page)
  if (num === null) return null
  const idx = num - 1
  return (idx >= 0 && idx < slideImages.value.length) ? slideImages.value[idx] : null
})

/**
 * 根据当前建议条目的 page 字段，找到对应的原始幻灯片（文本，降级用）
 */
const matchedSlide = computed(() => {
  if (slides.value.length === 0) return null
  const num = extractPageNum(currentItem.value.page)
  if (num === null) return null
  return slides.value.find(s => parseInt(s.pageNum, 10) === num) || null
})

// ===================== 翻页 =====================
const goPage = (dir) => {
  transitionName.value = dir > 0 ? 'slide-left' : 'slide-right'
  const next = currentPage.value + dir
  if (next >= 0 && next < parsedSuggestions.value.length) {
    currentPage.value = next
  }
}

// 把幻灯片正文按换行切分成要点行
const splitLines = (text) => {
  if (!text) return []
  return text
    .split(/\n/)
    .map(s => s.trim())
    .filter(s => s.length > 0)
}

// ===================== 加载数据 =====================
const loadSuggestions = async () => {
  loading.value = true
  try {
    const res = await smartTeachingApi.getCoursewareSuggestions(props.updateId)
    if (res.code === 1 || res.success === true) {
      suggestions.value = res.data
    }
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleClose = () => emit('close')

onMounted(() => {
  loadSuggestions()
})
</script>

<style scoped>
.suggestions-container {
  min-height: 450px;
}

.file-info {
  margin-bottom: 20px;
}

/* ===== 翻页容器 ===== */
.pager-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== 顶部进度点 ===== */
.page-progress-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-indicators {
  display: flex;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #dcdfe6;
  cursor: pointer;
  transition: all 0.25s;
}

.dot.active {
  background: #667eea;
  transform: scale(1.3);
}

.page-counter {
  font-size: 13px;
  color: #909399;
  min-width: 50px;
  text-align: center;
}

/* ===== 左右两栏 ===== */
.slide-suggestion-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

/* ===== 面板标题 ===== */
.panel-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
}

.label-icon {
  font-size: 16px;
}

.panel-subtitle {
  font-weight: 400;
  color: #909399;
  font-size: 12px;
}

/* ===== PPT 幻灯片样式 ===== */
.ppt-slide-panel {
  display: flex;
  flex-direction: column;
}

/* 真实截图容器 */
.ppt-slide-img-wrap {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  background: #fff;
  line-height: 0;
}

.ppt-slide-img {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 8px;
}

.ppt-slide-frame {
  background: #1a1a2e;
  border-radius: 10px;
  padding: 4px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
  min-height: 320px;
}

.slide-inner {
  background: linear-gradient(135deg, #16213e 0%, #0f3460 100%);
  border-radius: 8px;
  padding: 24px 22px;
  min-height: 312px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
}

.slide-page-tag {
  position: absolute;
  top: 10px;
  right: 14px;
  background: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.7);
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}

.slide-title {
  font-size: 17px;
  font-weight: 700;
  color: #e0e0ff;
  border-bottom: 2px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 10px;
  margin-bottom: 4px;
  padding-right: 60px;
}

.slide-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow-y: auto;
  max-height: 220px;
}

.slide-line {
  font-size: 13px;
  color: rgba(200, 220, 255, 0.85);
  line-height: 1.65;
  margin: 0;
  padding-left: 10px;
  border-left: 2px solid rgba(100, 150, 255, 0.4);
}

.slide-placeholder {
  font-size: 13px;
  color: rgba(200, 220, 255, 0.35);
  font-style: italic;
  margin: 0;
}

/* ===== AI 建议面板 ===== */
.suggestion-panel {
  display: flex;
  flex-direction: column;
}

.suggestion-block {
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 12px;
}

.block-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 8px;
}

.block-icon {
  font-size: 15px;
}

.suggestion-block p {
  margin: 0;
  font-size: 14px;
  color: #555;
  line-height: 1.7;
}

.suggestion-old {
  background: #fff3e0;
  border-left: 3px solid #ff9800;
}

.suggestion-old .block-title {
  color: #e65100;
}

.suggestion-update {
  background: #e3f2fd;
  border-left: 3px solid #2196f3;
}

.suggestion-update .block-title {
  color: #1565c0;
}

.suggestion-new {
  background: #e8f5e9;
  border-left: 3px solid #4caf50;
}

.suggestion-new .block-title {
  color: #2e7d32;
}

/* ===== 翻页导航 ===== */
.pager-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 10px 0 4px;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 18px;
  border: none;
  border-radius: 20px;
  background: #667eea;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s;
  min-width: 90px;
  justify-content: center;
}

.nav-btn:disabled {
  background: #dcdfe6;
  color: #c0c4cc;
  cursor: not-allowed;
}

.nav-btn:not(:disabled):hover {
  background: #5a6fd8;
  transform: scale(1.04);
}

.nav-arrow {
  font-size: 20px;
  line-height: 1;
}

.nav-page-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: center;
}

.page-num-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1.5px solid #dcdfe6;
  background: white;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-num-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
  font-weight: 600;
}

.page-num-btn:hover:not(.active) {
  border-color: #667eea;
  color: #667eea;
}

/* ===== 翻页过渡动画 ===== */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.3s ease;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(40px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-40px);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-40px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(40px);
}

/* ===== 降级显示 ===== */
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

/* ===== 状态提示 ===== */
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
