<template>
  <div class="document-viewer">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <h3>{{ resourceTitle }}</h3>
        <span class="file-info">{{ fileName }}</span>
      </div>
      <div class="toolbar-center">
        <!-- 标注工具 -->
        <div class="annotation-tools">
          <button 
            v-for="tool in annotationTools" 
            :key="tool.type"
            @click="selectTool(tool.type)"
            :class="['tool-btn', { active: selectedTool === tool.type }]"
            :title="tool.name"
          >
            <i :class="tool.icon"></i>
            <span>{{ tool.name }}</span>
          </button>
        </div>
      </div>
      <div class="toolbar-right">
        <div class="zoom-controls">
          <button @click="zoomOut" class="zoom-btn">
            <i class="fas fa-search-minus"></i>
          </button>
          <span class="zoom-level">{{ Math.round(zoomLevel * 100) }}%</span>
          <button @click="zoomIn" class="zoom-btn">
            <i class="fas fa-search-plus"></i>
          </button>
        </div>
        <button @click="toggleNotesPanel" class="notes-toggle" :class="{ active: showNotesPanel }">
          <i class="fas fa-sticky-note"></i>
          笔记面板
        </button>
        <button @click="toggleAIPanel" class="ai-toggle" :class="{ active: showAIPanel }">
          <i class="fas fa-robot"></i>
          AI助手
        </button>
        <button @click="enterVRClassroom" class="vr-toggle">
          <i class="fas fa-vr-cardboard"></i>
          进入VR教室
        </button>
        <button @click="goBack" class="exit-btn">
          <i class="fas fa-times"></i>
          退出
        </button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="viewer-container">
      <!-- 文档预览区域 -->
      <div class="document-area" :class="{ 'with-notes-panel': showNotesPanel, 'with-ai-panel': showAIPanel }">
        <div class="document-wrapper" :style="{ transform: `scale(${zoomLevel})` }">
          <!-- 文档内容 -->
          <div 
            ref="documentContent"
            class="document-content"
            @mouseup="handleTextSelection"
            @click="handleDocumentClick"
          >
            <!-- 加载中状态 -->
            <div v-if="isLoadingDocument" class="document-loading">
              <i class="fas fa-spinner fa-spin"></i>
              <p>正在加载文档内容...</p>
            </div>
            
            <!-- 文档内容 -->
            <div v-else class="document-page">
              <h1>{{ resourceTitle }}</h1>
              <div class="document-body" v-html="formatDocumentContent(documentText)"></div>
            </div>
          </div>
              </div>
            </div>

      <!-- 选择工具提示 - 移到 document-wrapper 外面 -->
          <div 
            v-if="showSelectionTooltip" 
            class="selection-tooltip"
            :style="{ top: tooltipPosition.y + 'px', left: tooltipPosition.x + 'px' }"
          >
            <button @click="createHighlight" class="tooltip-btn highlight">
              <i class="fas fa-highlighter"></i>
            </button>
            <button @click="createUnderline" class="tooltip-btn underline">
              <i class="fas fa-underline"></i>
            </button>
            <button @click="createComment" class="tooltip-btn comment">
              <i class="fas fa-comment"></i>
            </button>
      </div>

      <!-- 笔记面板 -->
      <div v-if="showNotesPanel" class="notes-panel">
        <div class="notes-header">
          <h4><i class="fas fa-list"></i> 我的批注</h4>
          <div class="notes-stats">
            <span>共 {{ commentAnnotations.length }} 个批注</span>
            <button v-if="annotations.length > 0" @click="clearAllAnnotations" class="clear-all-btn" title="清空所有标注">
              <i class="fas fa-trash-alt"></i>
            </button>
          </div>
        </div>
        
        <div class="notes-list">
          <!-- 没有标注时的提示 -->
          <div v-if="commentAnnotations.length === 0" class="empty-annotations">
            <div class="empty-icon">
              <i class="fas fa-comment"></i>
            </div>
            <div class="empty-text">
              <h4>暂无批注</h4>
              <p>选择文档中的文字，点击批注按钮添加您的想法</p>
            </div>
          </div>
          
          <!-- 标注列表 -->
          <div 
            v-for="annotation in commentAnnotations" 
            :key="annotation.id"
            :class="['note-item', annotation.type]"
            @click="jumpToAnnotation(annotation)"
          >
            <div class="note-header">
              <div class="note-type">
                <span 
                  class="color-indicator" 
                  :style="{ 
                    backgroundColor: annotation.color || '#00FF00',
                    width: '20px',
                    height: '20px',
                    borderRadius: '50%',
                    border: '3px solid #000',
                    display: 'inline-block',
                    marginRight: '8px'
                  }"
                  :title="`颜色: ${annotation.color || '默认'}`"
                ></span>
                <i :class="getAnnotationIcon(annotation.type)"></i>
                <span>{{ getAnnotationTypeName(annotation.type) }}</span>
              </div>
              <div class="note-actions">
                <button @click.stop="editAnnotation(annotation)" class="action-btn">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click.stop="deleteAnnotation(annotation.id)" class="action-btn">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
            
            <div v-if="annotation.selectedText" class="note-text">
              "{{ annotation.selectedText }}"
            </div>
            
            <div v-if="annotation.comment" class="note-comment">
              {{ annotation.comment }}
            </div>
            
            <div class="note-meta">
              <span>第{{ annotation.pageNumber }}页</span>
              <span>{{ formatDate(annotation.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- AI助手面板 -->
      <div v-if="showAIPanel" class="ai-panel">
        <div class="ai-header">
          <h4><i class="fas fa-robot"></i> AI助手</h4>
          <div class="ai-tabs">
            <button 
              :class="['tab-btn', { active: activeTab === 'chat' }]" 
              @click="activeTab = 'chat'"
            >
              <i class="fas fa-comments"></i>
              文件提问
            </button>
            <button 
              :class="['tab-btn', { active: activeTab === 'mindmap' }]" 
              @click="activeTab = 'mindmap'"
            >
              <i class="fas fa-project-diagram"></i>
              思维导图
            </button>
          </div>
        </div>
        
        <!-- 文件提问面板 -->
        <div v-show="activeTab === 'chat'" class="chat-panel">
          <div class="chat-messages" ref="chatMessages">
            <div 
              v-for="message in chatMessages" 
              :key="message.id"
              :class="['message', message.type]"
            >
              <div class="message-content">
                <div class="message-text">{{ message.text }}</div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
              </div>
            </div>
          </div>
          
          <div class="chat-input">
            <div class="input-group">
              <input 
                v-model="chatInput" 
                @keyup.enter="sendMessage"
                placeholder="请输入关于此文档的问题..."
                class="chat-input-field"
              />
              <button 
                @click="sendMessage" 
                :disabled="!chatInput.trim() || isLoading"
                class="send-btn"
              >
                <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-paper-plane"></i>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 思维导图面板 -->
        <div v-show="activeTab === 'mindmap'" class="mindmap-panel">
          <div class="mindmap-controls">
                         <button 
               @click="generateMindmapFunc" 
               :disabled="isGeneratingMindmap"
               class="generate-btn"
             >
              <i v-if="isGeneratingMindmap" class="fas fa-spinner fa-spin"></i>
              <i v-else class="fas fa-magic"></i>
              生成思维导图
            </button>
            <button 
              v-if="mindmapData"
              @click="exportMindmap"
              class="export-btn"
            >
              <i class="fas fa-download"></i>
              导出
            </button>
          </div>
          
          <div class="mindmap-container">
            <div v-if="!mindmapData && !isGeneratingMindmap" class="mindmap-placeholder">
              <i class="fas fa-project-diagram"></i>
              <p>点击"生成思维导图"按钮，基于当前文档内容生成思维导图</p>
            </div>
            
            <div v-if="isGeneratingMindmap" class="mindmap-loading">
              <i class="fas fa-spinner fa-spin"></i>
              <p>正在生成思维导图...</p>
            </div>
            
            <div v-if="mindmapData" class="mindmap-viewer" v-html="mindmapData"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标注编辑模态框 -->
    <div v-if="showAnnotationModal" class="modal-overlay" @click.self="closeAnnotationModal">
      <div class="annotation-modal">
        <div class="modal-header">
          <h3>编辑标注</h3>
          <button @click="closeAnnotationModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标注类型</label>
            <select v-model="annotationForm.type">
              <option value="highlight">高亮标记</option>
              <option value="underline">下划线</option>
              <option value="comment">文字批注</option>
              <option value="sticky-note">便签</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>标注颜色</label>
            <div class="color-picker">
              <button 
                v-for="color in annotationColors"
                :key="color.value"
                @click.stop="selectColor(color.value)"
                :class="['color-option', { active: annotationForm.color === color.value }]"
                :style="{ backgroundColor: color.value }"
                :title="color.name"
              ></button>
            </div>
          </div>
          
          <div class="form-group">
            <label>备注说明</label>
            <textarea 
              v-model="annotationForm.comment"
              placeholder="添加您的注释..."
              rows="4"
            ></textarea>
          </div>
          
          <div class="modal-actions">
            <button @click="closeAnnotationModal" class="btn btn-secondary">取消</button>
            <button @click="saveAnnotation" class="btn btn-primary">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { 
  createAnnotation, 
  updateAnnotation, 
  deleteAnnotation as deleteAnnotationApi,
  getResourceAnnotations 
} from '@/api/notes'
import { chatWithDocument, generateMindmap } from '@/api/ai'
import { getResourceContent } from '@/api/courseResource'
import { recordResourceStudy } from '@/api/progress'

const router = useRouter()

// Props
const props = defineProps({
  resourceId: {
    type: Number,
    required: true
  },
  resourceTitle: {
    type: String,
    default: '文档'
  },
  fileName: {
    type: String,
    default: '未知文件'
  },
  courseId: {
    type: Number,
    required: true
  }
})

// 文档内容
const documentText = ref('')
const originalDocumentText = ref('') // 保存原始文档内容，永不修改
const isLoadingDocument = ref(false)

// 响应式数据
const documentContent = ref(null)
const annotations = ref([])
const selectedText = ref('')
const selectedRange = ref(null)
const zoomLevel = ref(1)
const showNotesPanel = ref(true)
const showAIPanel = ref(false)
const showSelectionTooltip = ref(false)
const showAnnotationModal = ref(false)
const selectedTool = ref(null)  // 默认不选择任何工具
const currentAnnotation = ref(null)

// AI助手相关数据
const activeTab = ref('chat')
const chatMessages = ref([])
const chatInput = ref('')
const isLoading = ref(false)
const mindmapData = ref('')
const isGeneratingMindmap = ref(false)
const messageIdCounter = ref(0)

// 获取学生ID
const studentId = ref(localStorage.getItem('userId') || 17)

// 计算属性：只显示批注类型的标注
const commentAnnotations = computed(() => {
  return annotations.value.filter(ann => ann.type === 'comment')
})

// 位置数据
const tooltipPosition = reactive({ x: 0, y: 0 })

// 表单数据
const annotationForm = reactive({
  type: 'highlight',
  color: '#FFFF00',
  comment: '',
  selectedText: '',
  pageNumber: 1,
  position: null
})

// 配置数据
const annotationTools = [
  { type: 'highlight', name: '高亮', icon: 'fas fa-highlighter' },
  { type: 'underline', name: '下划线', icon: 'fas fa-underline' },
  { type: 'comment', name: '批注', icon: 'fas fa-comment' },
  { type: 'sticky-note', name: '便签', icon: 'fas fa-sticky-note' }
]

const annotationColors = [
  { name: '黄色', value: '#FFFF00' },
  { name: '绿色', value: '#90EE90' },
  { name: '蓝色', value: '#87CEEB' },
  { name: '粉色', value: '#FFB6C1' },
  { name: '橙色', value: '#FFA500' }
]

// 方法
const loadDocumentContent = async () => {
  isLoadingDocument.value = true
  try {
    console.log('📖 开始加载文档内容，资源ID:', props.resourceId)
    const response = await getResourceContent(props.resourceId)
    console.log('📦 文档API响应:', response)
    
    if (response && response.success) {
      // 保存原始文档内容，永不修改
      originalDocumentText.value = response.data
      documentText.value = response.data
      console.log('✅ 文档内容加载成功，长度:', response.data.length)
      ElMessage.success('文档加载成功')
      
      // 记录学习进度
      recordStudyProgress()
      
      // 文档加载完成后，如果已经有标注数据，立即渲染
      if (annotations.value.length > 0) {
        console.log('🎨 文档加载完成，开始渲染已有的', annotations.value.length, '个标注')
        await nextTick()
        refreshDocumentDisplay()
      }
    } else {
      const errorMsg = response?.message || '加载文档内容失败'
      console.error('❌ 加载失败:', errorMsg)
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('❌ 加载文档内容异常:', error)
    ElMessage.error('加载文档内容失败: ' + (error.message || '未知错误'))
  } finally {
    isLoadingDocument.value = false
  }
}

// 记录学习进度
const recordStudyProgress = async () => {
  try {
    console.log('准备记录学习进度:', {
      studentId: studentId.value,
      courseId: props.courseId,
      resourceId: props.resourceId
    })
    
    if (props.courseId && props.resourceId) {
      const response = await recordResourceStudy(studentId.value, props.courseId, props.resourceId)
      console.log('学习进度记录API响应:', response)
      console.log('响应数据类型:', typeof response)
      console.log('响应数据详情:', JSON.stringify(response, null, 2))
      
      if (response && response.success) {
        console.log('✅ 学习进度记录成功')
        
        // 根据进度显示不同的鼓励语句
        const progressData = response.data || {}
        console.log('进度数据:', progressData)
        const progress = progressData.progress || 0
        const studiedCount = progressData.studiedResourceCount || 0
        const totalCount = progressData.totalResourceCount || 0
        console.log(`解析结果: progress=${progress}, studiedCount=${studiedCount}, totalCount=${totalCount}`)
        
        let message = '学习进度已更新'
        let messageType = 'success'
        
        if (progress >= 100) {
          message = `🎉 恭喜！您已完成本课程所有资源的学习！`
          messageType = 'success'
        } else if (progress >= 80) {
          message = `💪 太棒了！学习进度 ${progress}%，即将完成课程！`
          messageType = 'success'
        } else if (progress >= 60) {
          message = `👍 做得好！学习进度 ${progress}%，继续保持！`
          messageType = 'success'
        } else if (progress >= 40) {
          message = `📚 不错！学习进度 ${progress}%，加油！`
          messageType = 'success'
        } else if (progress >= 20) {
          message = `🌟 很好！学习进度 ${progress}%，继续努力！`
          messageType = 'success'
        } else if (progress > 0) {
          message = `✨ 开始学习了！当前进度 ${progress}%`
          messageType = 'success'
        } else {
          message = `🚀 开启学习之旅！已学习 ${studiedCount}/${totalCount} 个资源`
          messageType = 'info'
        }
        
        ElMessage({
          message: message,
          type: messageType,
          duration: 3000,
          showClose: true
        })
      } else {
        console.warn('⚠️ 学习进度记录失败:', response?.message || response?.msg)
        // 静默失败，不打扰用户
      }
    } else {
      console.warn('⚠️ 缺少必要参数，无法记录学习进度')
    }
  } catch (error) {
    console.error('❌ 记录学习进度异常:', error)
    // 静默失败，不打扰用户
  }
}

const loadAnnotations = async () => {
  try {
    console.log('🔍 开始加载标注，学生ID:', studentId.value, '资源ID:', props.resourceId)
    const response = await getResourceAnnotations(studentId.value, props.resourceId)
    console.log('📦 获取标注API完整响应:', JSON.stringify(response, null, 2))
    
    if (response && response.success) {
      // 确保获取正确的数据
      const annotationsData = response.data || []
      annotations.value = Array.isArray(annotationsData) ? annotationsData : []
      console.log('✅ 成功加载标注数据:', annotations.value)
      console.log('📊 标注数量:', annotations.value.length)
      
      // 详细检查每个标注
      annotations.value.forEach((ann, index) => {
        console.log(`📌 标注 #${index + 1}:`, {
          id: ann.id,
          type: ann.type,
          color: ann.color,
          selectedText: ann.selectedText?.substring(0, 50) + '...',
          position: ann.position,
          pageNumber: ann.pageNumber
        })
      })
      
      // 等待文档内容加载完成后再渲染标注
      if (documentText.value) {
        console.log('📄 文档已加载，立即渲染标注')
      await nextTick()
      refreshDocumentDisplay()
    } else {
        console.log('⏳ 文档尚未加载，等待文档加载完成后渲染标注')
      }
    } else {
      console.warn('⚠️ 获取标注失败:', response?.msg || response?.message)
      ElMessage.warning('获取标注失败: ' + (response?.msg || response?.message || '未知错误'))
    }
  } catch (error) {
    console.error('❌ 加载标注异常:', error)
    ElMessage.error('加载标注失败: ' + error.message)
  }
}

// 刷新文档显示以应用标注
const refreshDocumentDisplay = () => {
  console.log('🔄 刷新文档显示，当前标注数量:', annotations.value.length)
  
  // 从原始文档内容重新渲染，确保不会丢失文本
  const originalText = originalDocumentText.value || documentText.value
  
  if (!originalText) {
    console.warn('⚠️ 没有文档内容可以渲染')
    return
  }
  
  console.log('📄 原始文档长度:', originalText.length)
  
  // 强制重新渲染
  documentText.value = ''
  nextTick(() => {
    documentText.value = originalText
    console.log('✅ 文档内容已重新设置，Vue将重新渲染')
    
    // 再次确认标注数据
    nextTick(() => {
      console.log('🎨 Vue渲染完成，当前标注列表:', annotations.value.map(a => ({
        id: a.id,
        type: a.type,
        text: a.selectedText?.substring(0, 30)
      })))
    })
  })
}

const handleTextSelection = (event) => {
  const selection = window.getSelection()
  if (selection.rangeCount > 0 && selection.toString().trim()) {
    selectedText.value = selection.toString().trim()
    selectedRange.value = selection.getRangeAt(0)
    
    // 如果已经选择了工具，直接创建标注
    if (selectedTool.value === 'highlight') {
      createHighlight()
      return
    } else if (selectedTool.value === 'underline') {
      createUnderline()
      return
    } else if (selectedTool.value === 'comment') {
      createComment()
      return
    }
    
    // 如果没有选择工具，显示工具提示让用户选择
    // 获取选中文本的精确位置
    const range = selection.getRangeAt(0)
    const rect = range.getBoundingClientRect()
    
    console.log('🎯 开始计算工具栏位置')
    console.log('📍 选中文本的 rect:', {
      top: rect.top,
      bottom: rect.bottom,
      left: rect.left,
      right: rect.right,
      width: rect.width,
      height: rect.height
    })
    console.log('📜 页面滚动:', {
      scrollY: window.scrollY,
      scrollX: window.scrollX
    })
    console.log('🖥️ 视口尺寸:', {
      width: window.innerWidth,
      height: window.innerHeight
    })
    
    // 计算工具提示的位置
    const tooltipWidth = 152 // 工具提示的实际宽度 (3个按钮 * 40px + 间距)
    const tooltipHeight = 52 // 工具提示的实际高度
    const padding = 10 // 边距
    
    // 水平位置：选中文本的中心点
    let x = rect.left + (rect.width / 2)
    
    // 垂直位置：选中文本的顶部
    // rect.top 是相对于视口的，position: fixed 也是相对于视口的
    // CSS 的 translate(-50%, -100%) 会让工具栏向上偏移自身高度
    let y = rect.top
    
    // 检查上方空间
    const spaceAbove = rect.top
    const spaceBelow = window.innerHeight - rect.bottom
    
    console.log('📏 空间检查:', { 
      spaceAbove, 
      spaceBelow, 
      tooltipHeight,
      needSpace: tooltipHeight + padding
    })
    
    // 如果上方空间不足，显示在下方
    if (spaceAbove < tooltipHeight + padding) {
      // 显示在下方：rect.bottom
      // 因为 CSS 有 translateY(-100%)，需要加上工具栏高度才能显示在下方
      y = rect.bottom + tooltipHeight + padding
      console.log('⬇️ 工具栏显示在下方, y =', y)
    } else {
      // 显示在上方：rect.top - padding
      // CSS 的 translateY(-100%) 会自动向上偏移工具栏高度
      y = rect.top - padding
      console.log('⬆️ 工具栏显示在上方, y =', y)
    }
    
    // 确保X坐标不会让工具栏超出左右边界
    const halfWidth = tooltipWidth / 2
    if (x - halfWidth < padding) {
      x = halfWidth + padding
      console.log('⬅️ 调整X坐标避免超出左边界, x =', x)
    } else if (x + halfWidth > window.innerWidth - padding) {
      x = window.innerWidth - halfWidth - padding
      console.log('➡️ 调整X坐标避免超出右边界, x =', x)
    }
    
    tooltipPosition.x = x
    tooltipPosition.y = y
    
    console.log('✅ 最终工具栏位置:', { x, y })
    console.log('📐 工具栏将显示在:', `left: ${x}px, top: ${y}px`)
    console.log('📐 经过 CSS transform 后实际位置:', `水平居中于 ${x}px, 垂直在 ${y}px 上方 ${tooltipHeight}px`)
    
    showSelectionTooltip.value = true
    
    // 标记当前正在显示工具栏，防止立即被点击事件隐藏
    justShowedTooltip = true
    setTimeout(() => {
      justShowedTooltip = false
    }, 100)
  } else {
    hideSelectionTooltip()
  }
}

// 标记是否刚刚显示了工具栏
let justShowedTooltip = false

// 监听页面滚动，隐藏工具栏
const handleScroll = () => {
  if (showSelectionTooltip.value) {
    console.log('📜 页面滚动，隐藏工具栏')
    hideSelectionTooltip()
  }
}

// 监听点击事件，如果点击的不是工具栏，则隐藏
const handleGlobalClick = (event) => {
  // 如果刚刚显示了工具栏，忽略这次点击
  if (justShowedTooltip) {
    console.log('⏭️ 忽略刚显示工具栏后的点击事件')
    return
  }
  
  if (showSelectionTooltip.value) {
    // 检查点击的是否是工具栏或其子元素
    const tooltip = document.querySelector('.selection-tooltip')
    if (tooltip && !tooltip.contains(event.target)) {
      console.log('🖱️ 点击了工具栏外部，隐藏工具栏')
      hideSelectionTooltip()
    }
  }
}

const handleDocumentClick = (event) => {
  if (selectedTool.value === 'sticky-note') {
    createStickyNoteAt(event.pageX, event.pageY)
  }
}

const hideSelectionTooltip = () => {
  showSelectionTooltip.value = false
  selectedText.value = ''
  selectedRange.value = null
}

const createHighlight = () => {
  createAnnotationWithSelection('highlight')
}

const createUnderline = () => {
  createAnnotationWithSelection('underline')
}

const createComment = () => {
  if (!selectedText.value || !selectedRange.value) return
  
  // 弹出输入框让用户输入批注内容
  ElMessageBox.prompt('请输入批注内容', '添加批注', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入您的批注...',
    inputType: 'textarea',
    customClass: 'annotation-prompt-box',
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return '批注内容不能为空'
      }
      return true
    }
  }).then(({ value }) => {
    const annotation = {
      studentId: parseInt(studentId.value),
      resourceId: props.resourceId,
      type: 'comment',
      selectedText: selectedText.value,
      pageNumber: 1,
      position: JSON.stringify({ simple: true }),
      color: '#4CAF50',  // 使用绿色
      comment: value.trim(),
      importance: 3
    }
    saveNewAnnotation(annotation)
    hideSelectionTooltip()
  }).catch(() => {
    // 用户取消
    hideSelectionTooltip()
  })
}

const createStickyNoteAt = (x, y) => {
  const rect = documentContent.value.getBoundingClientRect()
  const relativeX = x - rect.left
  const relativeY = y - rect.top
  
  const annotation = {
    studentId: parseInt(studentId.value),
    resourceId: props.resourceId,
    type: 'sticky-note',
    selectedText: '',
    pageNumber: 1,
    position: JSON.stringify({ x: relativeX, y: relativeY }),
    color: '#FFFF00',
    comment: '点击编辑便签内容...',
    importance: 3
  }
  
  saveNewAnnotation(annotation)
}

const createAnnotationWithSelection = (type) => {
  if (!selectedText.value || !selectedRange.value) return
  
  // 保存原始选中的文本，不做任何修改
  const originalText = selectedText.value
  
  console.log('📝 原始选中文本:', originalText)
  console.log('📝 文本长度:', originalText.length)
  console.log('📝 文本字符编码:', Array.from(originalText).map(c => c.charCodeAt(0)))
  
  // 使用简单的文本搜索模式
  const positionInfo = {
    simple: true,
    text: originalText  // 保存原始文本
  }
  
  console.log('💾 保存标注位置信息:', positionInfo)
  
  const annotation = {
    studentId: parseInt(studentId.value),
    resourceId: props.resourceId,
    type: type,
    selectedText: originalText,  // 保存原始文本
    pageNumber: 1,
    position: JSON.stringify(positionInfo),
    color: getDefaultColor(type),
    comment: '',
    importance: 3
  }
  
  console.log('📦 准备发送的标注对象:', annotation)
  console.log('📦 selectedText字段值:', annotation.selectedText)
  console.log('📦 selectedText字段长度:', annotation.selectedText.length)
  
  saveNewAnnotation(annotation)
  hideSelectionTooltip()
}

const saveNewAnnotation = async (annotation) => {
  try {
    console.log('💾 准备保存新标注:', annotation)
    console.log('💾 发送前 selectedText:', annotation.selectedText)
    console.log('💾 发送前 selectedText 长度:', annotation.selectedText?.length)
    
    const response = await createAnnotation(annotation)
    console.log('📦 创建标注API响应:', response)
    
    if (response && response.success) {
      const newAnnotation = response.data
      console.log('✅ 标注创建成功，返回数据:', newAnnotation)
      console.log('✅ 返回的 selectedText:', newAnnotation.selectedText)
      console.log('✅ 返回的 selectedText 长度:', newAnnotation.selectedText?.length)
      console.log('✅ 返回的 position:', newAnnotation.position)
      
      // 添加到标注列表
      annotations.value.push(newAnnotation)
      console.log('📊 当前标注总数:', annotations.value.length)
      
      ElMessage.success('标注创建成功')
      
      // 重新渲染文档以显示新标注
      console.log('🎨 开始重新渲染文档以显示新标注')
      await nextTick()
      refreshDocumentDisplay()
      console.log('✅ 文档重新渲染完成')
    } else {
      console.error('❌ 创建标注失败:', response?.msg || response?.message)
      ElMessage.error('创建标注失败: ' + (response?.msg || response?.message || '未知错误'))
    }
  } catch (error) {
    console.error('❌ 创建标注异常:', error)
    ElMessage.error('创建标注失败: ' + error.message)
  }
}

const getDefaultColor = (type) => {
  const colorMap = {
    'highlight': '#FFFF00',
    'underline': '#FF0000',
    'comment': '#4CAF50',
    'sticky-note': '#FFB6C1'
  }
  return colorMap[type] || '#FFFF00'
}

const selectTool = (tool) => {
  // 如果点击的是当前已选中的工具，则取消选择
  if (selectedTool.value === tool) {
    selectedTool.value = null
    ElMessage({
      message: '已取消标注模式',
      type: 'info',
      duration: 2000
    })
    return
  }
  
  selectedTool.value = tool
  
  // 给用户提示当前选择的工具
  const toolNames = {
    'highlight': '高亮模式',
    'underline': '下划线模式',
    'comment': '批注模式',
    'sticky-note': '便签模式'
  }
  
  ElMessage({
    message: `已切换到${toolNames[tool]}，选择文字即可标注`,
    type: 'success',
    duration: 2000
  })
}

const zoomIn = () => {
  zoomLevel.value = Math.min(zoomLevel.value + 0.25, 3)
}

const zoomOut = () => {
  zoomLevel.value = Math.max(zoomLevel.value - 0.25, 0.5)
}

const toggleNotesPanel = () => {
  showNotesPanel.value = !showNotesPanel.value
}

const toggleAIPanel = () => {
  showAIPanel.value = !showAIPanel.value
}

// AI助手相关方法
const sendMessage = async () => {
  if (!chatInput.value.trim()) return
  
  const userMessage = {
    id: messageIdCounter.value++,
    type: 'user',
    text: chatInput.value,
    timestamp: new Date()
  }
  
  chatMessages.value.push(userMessage)
  
  const question = chatInput.value
  chatInput.value = ''
  isLoading.value = true
  
  try {
    // 获取文档内容
    const documentText = getDocumentText()
    
    const response = await chatWithDocument({
      question: question,
      documentContent: documentText,
      resourceId: props.resourceId
    })
    
    const aiMessage = {
      id: messageIdCounter.value++,
      type: 'ai',
      text: response.data.answer,
      timestamp: new Date()
    }
    
    chatMessages.value.push(aiMessage)
    
    // 滚动到底部
    nextTick(() => {
      const messagesEl = document.querySelector('.chat-messages')
      if (messagesEl) {
        messagesEl.scrollTop = messagesEl.scrollHeight
      }
    })
    
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const generateMindmapFunc = async () => {
  isGeneratingMindmap.value = true
  
  try {
    const documentText = getDocumentText()
    
    const response = await generateMindmap({
      documentContent: documentText,
      resourceId: props.resourceId
    })
    
    mindmapData.value = response.data.mindmapHtml
    ElMessage.success('思维导图生成成功')
    
  } catch (error) {
    console.error('生成思维导图失败:', error)
    ElMessage.error('生成思维导图失败，请重试')
  } finally {
    isGeneratingMindmap.value = false
  }
}

const exportMindmap = () => {
  if (!mindmapData.value) return
  
  const blob = new Blob([mindmapData.value], { type: 'text/html' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${props.resourceTitle || '文档'}_思维导图.html`
  a.click()
  URL.revokeObjectURL(url)
}

const getDocumentText = () => {
  // 返回实际加载的文档内容
  return documentText.value || documentContent.value?.innerText || ''
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const enterVRClassroom = () => {
  // 跳转到VR教室
  router.push('/student/vr-classroom')
  ElMessage.success('正在进入VR虚拟教室...')
}

const goBack = () => {
  // 返回上一页
  router.back()
}

// 格式化文档内容为HTML,并应用标注
const formatDocumentContent = (text) => {
  console.log('📝 formatDocumentContent 被调用')
  console.log('📄 文档文本长度:', text?.length || 0)
  console.log('📌 当前标注数量:', annotations.value.length)
  
  if (!text) {
    console.warn('⚠️ 没有文档文本')
    return ''
  }
  
  // 将纯文本转换为HTML格式
  // 处理标题
  let html = text
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^#### (.+)$/gm, '<h4>$1</h4>')
  
  // 处理段落
  html = html.split('\n\n').map(para => {
    if (para.trim() && !para.startsWith('<h')) {
      return `<p>${para.replace(/\n/g, '<br>')}</p>`
    }
    return para
  }).join('\n')
  
  console.log('📄 HTML转换完成，长度:', html.length)
  
  // 应用标注高亮
  if (annotations.value.length > 0) {
    console.log('🎨 开始应用', annotations.value.length, '个标注到HTML')
  html = applyAnnotationsToHTML(html)
    console.log('✅ 标注应用完成')
  } else {
    console.log('ℹ️ 没有标注需要应用')
  }
  
  return html
}

// 将标注应用到HTML内容中 - 基于位置偏移量的精确匹配
const applyAnnotationsToHTML = (html) => {
  if (!annotations.value || annotations.value.length === 0) {
    console.log('❌ 没有标注需要应用')
    return html
  }
  
  console.log('🎨 开始应用标注到HTML，标注数量:', annotations.value.length)
  console.log('📄 HTML长度:', html.length)
  
  // 创建临时DOM元素来操作
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = html
  
  // 获取文档的纯文本内容
  const fullText = tempDiv.innerText
  console.log('📝 文档纯文本长度:', fullText.length)
  console.log('📝 文档纯文本前100字符:', fullText.substring(0, 100))
  
  // 辅助函数：创建标注包装元素
  const createAnnotationWrapper = (type, color, annotation) => {
    console.log(`🎨 创建标注包装器: type=${type}, color=${color}`)
    let wrapper
    if (type === 'highlight') {
      wrapper = document.createElement('mark')
      wrapper.className = 'annotation-highlight'
      wrapper.style.backgroundColor = color
      wrapper.style.padding = '2px 4px'
      wrapper.style.borderRadius = '3px'
      wrapper.style.display = 'inline'
      wrapper.style.fontSize = 'inherit'
      wrapper.style.fontFamily = 'inherit'
      wrapper.style.fontWeight = 'inherit'
      wrapper.style.lineHeight = 'inherit'
      wrapper.setAttribute('data-annotation-id', annotation.id)
      wrapper.title = '高亮标注'
    } else if (type === 'underline') {
      wrapper = document.createElement('u')
      wrapper.className = 'annotation-underline'
      wrapper.style.textDecoration = 'underline'
      wrapper.style.textDecorationColor = color
      wrapper.style.textDecorationThickness = '3px'
      wrapper.style.textUnderlineOffset = '3px'
      wrapper.style.display = 'inline'
      wrapper.style.fontSize = 'inherit'
      wrapper.style.fontFamily = 'inherit'
      wrapper.style.fontWeight = 'inherit'
      wrapper.style.lineHeight = 'inherit'
      wrapper.setAttribute('data-annotation-id', annotation.id)
      wrapper.title = '下划线标注'
    } else if (type === 'comment') {
      wrapper = document.createElement('span')
      wrapper.className = 'annotation-comment'
      wrapper.style.backgroundColor = 'rgba(76, 175, 80, 0.25)'
      wrapper.style.borderLeft = `4px solid ${color}`
      wrapper.style.padding = '2px 6px'
      wrapper.style.display = 'inline'
      wrapper.style.borderRadius = '2px'
      wrapper.style.fontSize = 'inherit'
      wrapper.style.fontFamily = 'inherit'
      wrapper.style.fontWeight = 'inherit'
      wrapper.style.lineHeight = 'inherit'
      wrapper.setAttribute('data-annotation-id', annotation.id)
      wrapper.title = annotation.comment || '批注'
      
      // 添加批注图标
      const icon = document.createElement('sup')
      icon.style.color = color
      icon.style.fontWeight = 'bold'
      icon.style.marginLeft = '4px'
      icon.style.fontSize = '0.9em'
      icon.textContent = '💬'
      wrapper.appendChild(icon)
    }
    return wrapper
  }
  
  // 按起始位置排序，从后往前应用（避免位置偏移）
  const sortedAnnotations = [...annotations.value]
    .filter(ann => {
      const hasText = ann.selectedText && ann.selectedText.trim().length > 0
      const hasPosition = ann.position
      console.log(`📌 检查标注 #${ann.id}: hasText=${hasText}, hasPosition=${hasPosition}, type=${ann.type}`)
      return hasText && hasPosition
    })
    .map(ann => {
      try {
        const posInfo = JSON.parse(ann.position)
        console.log(`📍 标注 #${ann.id} 位置信息:`, posInfo)
        // 兼容旧格式：如果是 {simple: true}，使用文本搜索
        if (posInfo.simple === true) {
          console.log(`📍 标注 #${ann.id} 使用简单文本搜索模式`)
          return { ...ann, posInfo: { text: ann.selectedText, useTextSearch: true } }
        }
        return { ...ann, posInfo }
      } catch (e) {
        console.error(`❌ 解析标注 #${ann.id} 位置信息失败:`, e)
        return null
      }
    })
    .filter(ann => ann && ann.posInfo)
    .sort((a, b) => {
      // 有startOffset的排在前面，按位置从后往前
      const aOffset = a.posInfo.startOffset || 0
      const bOffset = b.posInfo.startOffset || 0
      return bOffset - aOffset
    })
  
  console.log('✅ 有效标注数量:', sortedAnnotations.length)
  
  sortedAnnotations.forEach((annotation, index) => {
    console.log(`\n🔧 处理标注 #${index + 1}/${sortedAnnotations.length}`)
    console.log(`   ID: ${annotation.id}`)
    console.log(`   类型: ${annotation.type}`)
    console.log(`   颜色: ${annotation.color}`)
    console.log(`   文本: "${annotation.selectedText}"`)
    
    const posInfo = annotation.posInfo
    const color = annotation.color || '#FFFF00'
    const type = annotation.type
    
    // 如果是旧格式或标记为使用文本搜索
    if (posInfo.useTextSearch) {
      console.log(`   📍 使用文本搜索模式`)
      
      const searchText = posInfo.text
      console.log(`   🔍 搜索文本: "${searchText}"`)
      console.log(`   📏 搜索文本长度: ${searchText.length}`)
      
      // 直接在HTML中搜索原始文本
      const walker = document.createTreeWalker(tempDiv, NodeFilter.SHOW_TEXT, null, false)
      const textNodes = []
      let node
      
      while (node = walker.nextNode()) {
        // 跳过已标注的节点
        if (node.parentNode && node.parentNode.hasAttribute && node.parentNode.hasAttribute('data-annotation-id')) {
          continue
        }
        textNodes.push(node)
      }
      
      // 构建完整文本
      let fullText = ''
      const nodeMap = []
      
      textNodes.forEach(textNode => {
        const nodeText = textNode.textContent
        const startIdx = fullText.length
        
        for (let i = 0; i < nodeText.length; i++) {
          nodeMap.push({
            node: textNode,
            offset: i
          })
        }
        
        fullText += nodeText
      })
      
      console.log(`   📄 完整文本长度: ${fullText.length}`)
      console.log(`   📄 完整文本前100字符: "${fullText.substring(0, 100)}"`)
      
      // 在完整文本中查找（先尝试精确匹配）
      let searchIndex = fullText.indexOf(searchText)
      let actualSearchText = searchText  // 使用变量存储实际搜索文本
      
      // 如果精确匹配失败，尝试模糊匹配（忽略空白差异）
      if (searchIndex === -1) {
        console.warn(`   ⚠️ 精确匹配失败，尝试模糊匹配`)
        
        // 尝试通过正则表达式进行模糊匹配（允许空白字符的差异）
        // 将搜索文本中的空白字符替换为 \s+ 以匹配任意空白
        const escapeRegex = (str) => str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
        const flexiblePattern = escapeRegex(searchText).replace(/\s+/g, '\\s+')
        const regex = new RegExp(flexiblePattern, 'i')
        
        console.log(`   🔍 使用正则模式: ${flexiblePattern}`)
        
        const match = fullText.match(regex)
        
        if (!match) {
          console.warn(`   ❌ 文本搜索未找到: "${searchText}"`)
          console.warn(`   💡 提示: 文档可能已被修改，或文本包含特殊字符`)
          return
        }
        
        searchIndex = match.index
        // 更新搜索文本为实际匹配到的文本
        actualSearchText = match[0]
        console.log(`   ✅ 模糊匹配成功，位置: ${searchIndex}`)
        console.log(`   📝 实际匹配文本: "${actualSearchText}"`)
      }
      
      console.log(`   ✅ 找到文本位置: ${searchIndex}`)
      
      // 获取起始和结束节点信息
      const startInfo = nodeMap[searchIndex]
      const endInfo = nodeMap[searchIndex + actualSearchText.length - 1]
      
      if (!startInfo || !endInfo) {
        console.warn(`   ❌ 无法获取节点信息`)
        return
      }
      
      console.log(`   📍 起始节点偏移: ${startInfo.offset}, 结束节点偏移: ${endInfo.offset}`)
      
      // 创建标注包装器
          const wrapper = createAnnotationWrapper(type, color, annotation)
      
      // 情况1: 在同一个文本节点内
      if (startInfo.node === endInfo.node) {
        const parent = startInfo.node.parentNode
        const nodeText = startInfo.node.textContent
        
        const beforeText = nodeText.substring(0, startInfo.offset)
        const matchedText = nodeText.substring(startInfo.offset, endInfo.offset + 1)
        const afterText = nodeText.substring(endInfo.offset + 1)
        
          wrapper.textContent = matchedText
          
          if (beforeText) {
          parent.insertBefore(document.createTextNode(beforeText), startInfo.node)
          }
        parent.insertBefore(wrapper, startInfo.node)
          if (afterText) {
          parent.insertBefore(document.createTextNode(afterText), startInfo.node)
          }
        parent.removeChild(startInfo.node)
        
        console.log(`   ✅ 文本搜索成功应用标注（单节点）`)
      } else {
        // 情况2: 跨多个文本节点
        console.log(`   ✅ 跨节点标注`)
        
        // 收集所有涉及的节点
        const nodesToWrap = []
        let collecting = false
        
        for (const textNode of textNodes) {
          if (textNode === startInfo.node) {
            collecting = true
          }
          if (collecting) {
            nodesToWrap.push(textNode)
          }
          if (textNode === endInfo.node) {
          break
        }
      }
      
        if (nodesToWrap.length > 0) {
          const firstNode = nodesToWrap[0]
          const lastNode = nodesToWrap[nodesToWrap.length - 1]
          const parent = firstNode.parentNode
          
          // 处理第一个节点
          const firstText = firstNode.textContent
          const firstBefore = firstText.substring(0, startInfo.offset)
          const firstMatched = firstText.substring(startInfo.offset)
          
          if (firstBefore) {
            parent.insertBefore(document.createTextNode(firstBefore), firstNode)
          }
          
          wrapper.appendChild(document.createTextNode(firstMatched))
          
          // 处理中间节点
          for (let i = 1; i < nodesToWrap.length - 1; i++) {
            wrapper.appendChild(document.createTextNode(nodesToWrap[i].textContent))
          }
          
          // 处理最后一个节点
          if (nodesToWrap.length > 1) {
            const lastText = lastNode.textContent
            const lastMatched = lastText.substring(0, endInfo.offset + 1)
            const lastAfter = lastText.substring(endInfo.offset + 1)
            
            wrapper.appendChild(document.createTextNode(lastMatched))
            
            parent.insertBefore(wrapper, firstNode)
            
            if (lastAfter) {
              parent.insertBefore(document.createTextNode(lastAfter), firstNode)
            }
          } else {
            parent.insertBefore(wrapper, firstNode)
          }
          
          // 删除原始节点
          nodesToWrap.forEach(n => {
            if (n.parentNode) {
              n.parentNode.removeChild(n)
            }
          })
          
          console.log(`   ✅ 文本搜索成功应用标注（跨${nodesToWrap.length}个节点）`)
      }
      }
      
      return
    }
    
    // 使用位置偏移量匹配
    const { startOffset, length, text } = posInfo
    
    console.log(`   📍 使用位置偏移量: offset=${startOffset}, length=${length}`)
    
    // 验证文本是否匹配
    const expectedText = fullText.substring(startOffset, startOffset + length)
    let usePositionMatch = (expectedText === text)
    
    if (!usePositionMatch) {
      console.warn(`   ⚠️ 位置匹配失败，尝试使用文本搜索`)
      console.warn(`   期望: "${text}"`)
      console.warn(`   实际: "${expectedText}"`)
      // 降级到文本搜索模式
    }
    
    // 使用TreeWalker找到对应位置的文本节点
    const walker = document.createTreeWalker(
      tempDiv,
      NodeFilter.SHOW_TEXT,
      null,
      false
    )
    
    let currentOffset = 0
    let startNode = null
    let startNodeOffset = 0
    let endNode = null
    let endNodeOffset = 0
    let found = false
    
    // 如果位置匹配成功，使用精确位置
    if (usePositionMatch) {
      let node
      while (node = walker.nextNode()) {
        // 跳过已标注的节点
        if (node.parentNode && node.parentNode.hasAttribute && node.parentNode.hasAttribute('data-annotation-id')) {
          continue
        }
        
        const nodeLength = node.textContent.length
        
        // 找到起始节点
        if (!startNode && currentOffset + nodeLength > startOffset) {
          startNode = node
          startNodeOffset = startOffset - currentOffset
        }
        
        // 找到结束节点
        if (!endNode && currentOffset + nodeLength >= startOffset + length) {
          endNode = node
          endNodeOffset = startOffset + length - currentOffset
          found = true
          break
        }
        
        currentOffset += nodeLength
      }
    }
    
    // 如果位置匹配失败，使用文本搜索
    if (!found) {
      console.log('   📍 使用文本搜索模式（降级）')
      const walker2 = document.createTreeWalker(tempDiv, NodeFilter.SHOW_TEXT, null, false)
      const nodesToProcess = []
      let node
      
      while (node = walker2.nextNode()) {
        if (node.parentNode && node.parentNode.hasAttribute && node.parentNode.hasAttribute('data-annotation-id')) {
          continue
        }
        nodesToProcess.push(node)
      }
      
      // 尝试单节点匹配
      for (let i = 0; i < nodesToProcess.length && !found; i++) {
        const textNode = nodesToProcess[i]
        const nodeText = textNode.textContent
        const textIndex = nodeText.indexOf(text)
        
        if (textIndex !== -1) {
          startNode = textNode
          startNodeOffset = textIndex
          endNode = textNode
          endNodeOffset = textIndex + text.length
          found = true
          console.log(`   ✅ 文本搜索找到匹配（单节点）`)
          break
        }
      }
    }
    
    if (!startNode || !endNode || !found) {
      console.warn(`   ❌ 未找到对应的文本节点`)
      return
    }
    
    console.log(`   ✅ 找到位置: 起始节点偏移=${startNodeOffset}, 结束节点偏移=${endNodeOffset}`)
    
    // 创建包装元素
    const wrapper = createAnnotationWrapper(type, color, annotation)
    
    // 情况1: 在同一个文本节点内
    if (startNode === endNode) {
      const parent = startNode.parentNode
      const nodeText = startNode.textContent
      
      const beforeText = nodeText.substring(0, startNodeOffset)
      const matchedText = nodeText.substring(startNodeOffset, endNodeOffset)
      const afterText = nodeText.substring(endNodeOffset)
      
      wrapper.textContent = matchedText
      
      if (beforeText) {
        parent.insertBefore(document.createTextNode(beforeText), startNode)
      }
      parent.insertBefore(wrapper, startNode)
      if (afterText) {
        parent.insertBefore(document.createTextNode(afterText), startNode)
      }
      parent.removeChild(startNode)
      
      console.log(`   ✅ 成功应用标注（单节点）`)
    } else {
      // 情况2: 跨多个文本节点
      console.log(`   ✅ 跨节点标注`)
      
      // 收集所有涉及的节点
      const walker2 = document.createTreeWalker(tempDiv, NodeFilter.SHOW_TEXT, null, false)
      const nodesToWrap = []
      let collecting = false
      
      while (node = walker2.nextNode()) {
        if (node === startNode) {
          collecting = true
        }
        if (collecting) {
          nodesToWrap.push(node)
        }
        if (node === endNode) {
          break
        }
      }
      
      if (nodesToWrap.length > 0) {
        const firstNode = nodesToWrap[0]
        const lastNode = nodesToWrap[nodesToWrap.length - 1]
        const parent = firstNode.parentNode
        
        // 处理第一个节点
        const firstText = firstNode.textContent
        const firstBefore = firstText.substring(0, startNodeOffset)
        const firstMatched = firstText.substring(startNodeOffset)
        
        if (firstBefore) {
          parent.insertBefore(document.createTextNode(firstBefore), firstNode)
        }
        
        wrapper.appendChild(document.createTextNode(firstMatched))
        
        // 处理中间节点
        for (let i = 1; i < nodesToWrap.length - 1; i++) {
          wrapper.appendChild(document.createTextNode(nodesToWrap[i].textContent))
        }
        
        // 处理最后一个节点
        if (nodesToWrap.length > 1) {
          const lastText = lastNode.textContent
          const lastMatched = lastText.substring(0, endNodeOffset)
          const lastAfter = lastText.substring(endNodeOffset)
          
          wrapper.appendChild(document.createTextNode(lastMatched))
          
          parent.insertBefore(wrapper, firstNode)
          
          if (lastAfter) {
            parent.insertBefore(document.createTextNode(lastAfter), firstNode)
          }
        } else {
          parent.insertBefore(wrapper, firstNode)
        }
        
        // 删除原始节点
        nodesToWrap.forEach(n => {
          if (n.parentNode) {
            n.parentNode.removeChild(n)
          }
        })
        
        console.log(`   ✅ 成功应用标注（跨${nodesToWrap.length}个节点）`)
      }
    }
  })
  
  const resultHTML = tempDiv.innerHTML
  console.log('✅ 标注应用完成，最终HTML长度:', resultHTML.length)
  return resultHTML
}

const selectColor = (color) => {
  annotationForm.color = color
}

const editAnnotation = (annotation) => {
  currentAnnotation.value = annotation
  annotationForm.type = annotation.type
  annotationForm.color = annotation.color
  annotationForm.comment = annotation.comment
  annotationForm.selectedText = annotation.selectedText
  showAnnotationModal.value = true
}

const closeAnnotationModal = () => {
  // 清理定时器
  if (saveAnnotationTimer) {
    clearTimeout(saveAnnotationTimer)
    saveAnnotationTimer = null
  }
  
  showAnnotationModal.value = false
  currentAnnotation.value = null
}

// 防抖保存标注
let saveAnnotationTimer = null
const saveAnnotation = async () => {
  if (!currentAnnotation.value) {
    closeAnnotationModal()
    return
  }
  
  // 清除之前的定时器
  if (saveAnnotationTimer) {
    clearTimeout(saveAnnotationTimer)
  }
  
  // 立即更新UI,延迟保存到后端
  const index = annotations.value.findIndex(a => a.id === currentAnnotation.value.id)
  if (index !== -1) {
    annotations.value[index] = {
      ...annotations.value[index],
      type: annotationForm.type,
      color: annotationForm.color,
      comment: annotationForm.comment
    }
  }
  
  // 重新渲染文档以显示更新的标注
  await nextTick()
  refreshDocumentDisplay()
  
  // 延迟保存到后端
  saveAnnotationTimer = setTimeout(async () => {
    try {
      const updatedData = {
        ...currentAnnotation.value,
        type: annotationForm.type,
        color: annotationForm.color,
        comment: annotationForm.comment
      }
      
      const response = await updateAnnotation(currentAnnotation.value.id, updatedData)
      if (response.success) {
        console.log('标注已保存到后端')
      }
    } catch (error) {
      console.error('保存标注失败:', error)
      ElMessage.error('保存标注失败')
    }
  }, 500)
  
  ElMessage.success('标注更新成功')
  closeAnnotationModal()
}

const deleteAnnotation = async (annotationId) => {
  try {
    await deleteAnnotationApi(annotationId)
    annotations.value = annotations.value.filter(a => a.id !== annotationId)
    ElMessage.success('标注删除成功')
    // 重新渲染文档
    await nextTick()
    refreshDocumentDisplay()
  } catch (error) {
    console.error('删除标注失败:', error)
    ElMessage.error('删除标注失败')
  }
}

const clearAllAnnotations = async () => {
  try {
    // 确认删除
    await ElMessageBox.confirm(
      `确定要删除所有 ${annotations.value.length} 个标注吗？此操作不可恢复！`,
      '清空所有标注',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 删除所有标注
    const deletePromises = annotations.value.map(ann => deleteAnnotationApi(ann.id))
    await Promise.all(deletePromises)
    
    annotations.value = []
    ElMessage.success('已清空所有标注')
    
    // 重新渲染文档
    await nextTick()
    refreshDocumentDisplay()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空标注失败:', error)
      ElMessage.error('清空标注失败')
    }
  }
}

const jumpToAnnotation = (annotation) => {
  // 简化实现，滚动到顶部
  documentContent.value.scrollIntoView({ behavior: 'smooth' })
}

const getAnnotationIcon = (type) => {
  const icons = {
    'highlight': 'fas fa-highlighter',
    'underline': 'fas fa-underline',
    'comment': 'fas fa-comment',
    'sticky-note': 'fas fa-sticky-note'
  }
  return icons[type] || 'fas fa-marker'
}

const getAnnotationTypeName = (type) => {
  const names = {
    'highlight': '高亮',
    'underline': '下划线',
    'comment': '批注',
    'sticky-note': '便签'
  }
  return names[type] || type
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(async () => {
  console.log('🚀 DocumentViewer组件已挂载')
  console.log('📋 Props:', {
    resourceId: props.resourceId,
    resourceTitle: props.resourceTitle,
    courseId: props.courseId
  })
  
  // 先加载文档内容
  await loadDocumentContent()
  
  // 然后加载标注
  await loadAnnotations()
  
  // 添加滚动和点击事件监听
  window.addEventListener('scroll', handleScroll, true) // 使用捕获阶段
  document.addEventListener('click', handleGlobalClick)
  
  console.log('✅ 初始化完成')
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  console.log('🔚 DocumentViewer组件卸载，移除事件监听')
  window.removeEventListener('scroll', handleScroll, true)
  document.removeEventListener('click', handleGlobalClick)
})
</script>

<style scoped>
.document-viewer {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.toolbar {
  background: white;
  border-bottom: 1px solid #e0e0e0;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.toolbar-left h3 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.file-info {
  color: #6c757d;
  font-size: 0.9rem;
}

.annotation-tools {
  display: flex;
  gap: 8px;
}

.tool-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 2px solid #dee2e6;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.tool-btn:hover {
  border-color: #3498db;
  background: #f8f9fa;
}

.tool-btn.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}

.zoom-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.zoom-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.zoom-btn:hover {
  background: #e9ecef;
}

.zoom-level {
  font-weight: 600;
  color: #495057;
  min-width: 50px;
  text-align: center;
}

.notes-toggle {
  background: #17a2b8;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notes-toggle:hover {
  background: #138496;
}

.notes-toggle.active {
  background: #0f6674;
}

.ai-toggle {
  background: #9b59b6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}

.ai-toggle:hover {
  background: #8e44ad;
}

.ai-toggle.active {
  background: #7d3c98;
}

.vr-toggle {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}

.vr-toggle:hover {
  background: #c0392b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.exit-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}

.exit-btn:hover {
  background: #5a6268;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
}

/* 主要内容区域 */
.viewer-container {
  flex: 1;
  display: flex;
  overflow: hidden;
  margin-top: 70px; /* 为固定工具栏留出空间 */
}

.document-area {
  flex: 1;
  overflow: auto;
  position: relative;
  transition: all 0.3s ease;
}

.document-area.with-notes-panel {
  flex: 0 0 70%;
}

.document-area.with-ai-panel {
  flex: 0 0 60%;
}

.document-area.with-notes-panel.with-ai-panel {
  flex: 0 0 40%;
}

.document-wrapper {
  padding: 20px;
  transform-origin: top left;
  transition: transform 0.3s ease;
}

.document-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  position: relative;
  user-select: text;
}

.document-page {
  padding: 40px;
  line-height: 1.8;
  color: #2c3e50;
}

.document-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 40px;
  color: #6c757d;
}

.document-loading i {
  font-size: 48px;
  margin-bottom: 20px;
  color: #3498db;
}

.document-loading p {
  font-size: 16px;
  margin: 0;
}

.document-body {
  margin-top: 20px;
}

.document-body p {
  margin: 1rem 0;
  text-align: justify;
}

.document-body h1,
.document-body h2,
.document-body h3,
.document-body h4 {
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.document-page h1, .document-page h2 {
  color: #2c3e50;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.document-page h1 {
  font-size: 2rem;
  border-bottom: 2px solid #3498db;
  padding-bottom: 0.5rem;
}

.document-page h2 {
  font-size: 1.5rem;
  color: #3498db;
}

.document-page ul {
  margin: 1rem 0;
  padding-left: 2rem;
}

.document-page li {
  margin: 0.5rem 0;
}

/* 标注层 */
.annotations-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.annotation-mark {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.annotation-mark:hover {
  opacity: 0.6 !important;
}

.sticky-note {
  background: #ffeb3b;
  border-radius: 4px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  cursor: pointer;
  font-size: 16px;
  color: #f57f17;
}

/* 选择工具提示 */
.selection-tooltip {
  position: fixed;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(0,0,0,0.1);
  padding: 6px;
  display: flex;
  gap: 4px;
  z-index: 10000;
  transform: translate(-50%, -100%);
  animation: tooltipFadeIn 0.2s ease;
  pointer-events: auto;
}

@keyframes tooltipFadeIn {
  from {
    opacity: 0;
    transform: translate(-50%, -100%) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -100%) scale(1);
  }
}

.tooltip-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.tooltip-btn.highlight {
  background: #fff59d;
  color: #f57f17;
}

.tooltip-btn.underline {
  background: #ffcdd2;
  color: #d32f2f;
}

.tooltip-btn.comment {
  background: #c8e6c9;
  color: #388e3c;
}

.tooltip-btn:hover {
  transform: scale(1.15);
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.tooltip-btn:active {
  transform: scale(1.05);
}

/* 笔记面板 */
.notes-panel {
  width: 30%;
  background: white;
  border-left: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
}

.notes-header {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.notes-header h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notes-stats {
  color: #6c757d;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 12px;
}

.clear-all-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.clear-all-btn:hover {
  background: #c82333;
  transform: scale(1.05);
}

.notes-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

/* 空状态提示样式 */
.empty-annotations {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: #6c757d;
  height: 400px;
}

.empty-icon {
  font-size: 48px;
  color: #dee2e6;
  margin-bottom: 20px;
}

.empty-text h4 {
  margin: 0 0 8px 0;
  color: #495057;
  font-size: 16px;
}

.empty-text p {
  margin: 0 0 24px 0;
  color: #6c757d;
  font-size: 14px;
}

.empty-tips {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 13px;
  color: #495057;
}

.tip-item i {
  width: 16px;
  color: #6c757d;
}

.note-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid #3498db;
}

.note-item:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.note-item.highlight {
  border-left-color: #ffeb3b;
}

.note-item.underline {
  border-left-color: #f44336;
}

.note-item.comment {
  border-left-color: #4caf50;
}

.note-item.sticky-note {
  border-left-color: #ff9800;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.note-type {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #495057;
}

.color-indicator {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.3), 0 2px 6px rgba(0,0,0,0.2);
  flex-shrink: 0;
  display: inline-block;
  min-width: 18px;
  min-height: 18px;
  margin-right: 2px;
}

.note-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #dee2e6;
  color: #495057;
}

.note-text {
  background: white;
  padding: 8px;
  border-radius: 6px;
  font-style: italic;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #495057;
}

.note-comment {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.note-meta {
  display: flex;
  gap: 12px;
  font-size: 0.8rem;
  color: #adb5bd;
}

/* AI助手面板样式 */
.ai-panel {
  width: 30%;
  background: white;
  border-left: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
}

.ai-header {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.ai-header h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 2px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.tab-btn:hover {
  border-color: #9b59b6;
  background: #f8f9fa;
}

.tab-btn.active {
  background: #9b59b6;
  color: white;
  border-color: #9b59b6;
}

/* 聊天面板样式 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.message.user {
  justify-content: flex-end;
}

.message.user .message-content {
  background: #9b59b6;
  color: white;
  border-radius: 18px 18px 4px 18px;
  max-width: 80%;
}

.message.ai {
  justify-content: flex-start;
}

.message.ai .message-content {
  background: #f8f9fa;
  color: #2c3e50;
  border-radius: 18px 18px 18px 4px;
  max-width: 80%;
  border: 1px solid #e9ecef;
}

.message-content {
  padding: 12px 16px;
  word-wrap: break-word;
}

.message-text {
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 4px;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.7;
}

.chat-input {
  padding: 16px;
  border-top: 1px solid #e9ecef;
}

.input-group {
  display: flex;
  gap: 8px;
}

.chat-input-field {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.3s ease;
}

.chat-input-field:focus {
  border-color: #9b59b6;
}

.send-btn {
  padding: 10px 12px;
  background: #9b59b6;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover {
  background: #8e44ad;
}

.send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 思维导图面板样式 */
.mindmap-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.mindmap-controls {
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  gap: 8px;
}

.generate-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.generate-btn:hover {
  background: #2980b9;
}

.generate-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.export-btn:hover {
  background: #229954;
}

.mindmap-container {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

.mindmap-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
  text-align: center;
}

.mindmap-placeholder i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #dee2e6;
}

.mindmap-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
}

.mindmap-loading i {
  font-size: 32px;
  margin-bottom: 16px;
}

.mindmap-viewer {
  width: 100%;
  height: 100%;
  min-height: 400px;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: auto;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.annotation-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6c757d;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.color-picker {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.color-option {
  width: 32px;
  height: 32px;
  border: 3px solid transparent;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.color-option.active {
  border-color: #2c3e50;
  transform: scale(1.2);
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* 文档内标注样式 */
.annotation-highlight {
  cursor: pointer;
  transition: all 0.2s ease;
}

.annotation-highlight:hover {
  opacity: 0.8;
  box-shadow: 0 0 8px rgba(0,0,0,0.2);
}

.annotation-underline {
  cursor: pointer;
  transition: all 0.2s ease;
}

.annotation-underline:hover {
  text-decoration-thickness: 4px !important;
  text-shadow: 0 0 1px rgba(0,0,0,0.1);
}

.annotation-comment {
  cursor: pointer;
  transition: all 0.2s ease;
}

.annotation-comment:hover {
  background-color: rgba(76, 175, 80, 0.35) !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.15);
}

.annotation-comment sup {
  font-size: 0.85em;
  vertical-align: super;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 12px;
    padding: 16px;
  }
  
  .annotation-tools {
    justify-content: center;
  }
  
  .viewer-container {
    flex-direction: column;
  }
  
  .document-area.with-notes-panel {
    flex: 1;
  }
  
  .notes-panel {
    width: 100%;
    max-height: 40vh;
  }
}

/* 标注目标样式 - 用于视觉指示预期标注位置 */
.highlight-target {
  background-color: rgba(255, 255, 0, 0.1);
  border: 1px dashed rgba(255, 255, 0, 0.3);
  padding: 1px 2px;
  border-radius: 2px;
  position: relative;
}

.highlight-target::after {
  content: "📝";
  position: absolute;
  right: -20px;
  top: -5px;
  font-size: 12px;
  opacity: 0.6;
}

.underline-target {
  text-decoration: underline;
  text-decoration-color: rgba(255, 0, 0, 0.3);
  text-decoration-style: dashed;
  position: relative;
}

.underline-target::after {
  content: "📎";
  position: absolute;
  right: -20px;
  top: -5px;
  font-size: 12px;
  opacity: 0.6;
}

.comment-target {
  background-color: rgba(0, 255, 0, 0.1);
  border-left: 3px solid rgba(0, 255, 0, 0.3);
  padding-left: 5px;
  position: relative;
}

.comment-target::after {
  content: "💬";
  position: absolute;
  right: -20px;
  top: -5px;
  font-size: 12px;
  opacity: 0.6;
}
</style>

<style>
/* 全局样式：确保ElMessageBox显示在最顶层 */
.el-message-box__wrapper {
  z-index: 9999 !important;
}

.annotation-prompt-box {
  z-index: 10000 !important;
}

.el-overlay {
  z-index: 9998 !important;
}
</style> 