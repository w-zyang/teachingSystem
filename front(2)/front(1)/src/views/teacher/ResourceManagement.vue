<template>
  <div class="resource-management">
    <!-- 页面顶部导航 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-main">
          <div class="header-icon">
            <i class="fas fa-folder-open"></i>
        </div>
          <div class="header-text">
            <h1>课程资源管理</h1>
            <p>上传、管理和分享你的教学资源</p>
      </div>
        </div>
      </div>
    </div>

    <!-- 课程选择卡片 -->
    <div class="course-section">
      <div class="section-header">
        <h2><i class="fas fa-graduation-cap"></i>选择课程</h2>
        <p>选择一个课程来管理其教学资源</p>
      </div>
      
      <div class="course-grid">
        <div 
          v-for="course in courses" 
          :key="course.id"
          class="course-card"
          :class="{ active: selectedCourseId === course.id }"
          @click="selectCourse(course)"
        >
          <div class="course-header">
            <div class="course-avatar">
              <i class="fas fa-book"></i>
            </div>
            <div class="course-badge">
              {{ course.resourceCount }} 个资源
            </div>
          </div>
          <div class="course-content">
            <h3>{{ course.name }}</h3>
            <p>{{ course.description || '暂无描述' }}</p>
          </div>
          <div class="course-footer">
            <span class="course-subject">{{ course.subject || '未分类' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 资源管理区域 -->
    <div v-if="selectedCourseId" class="resource-section">
      <!-- 搜索和筛选 -->
      <div class="toolbar">
        <div class="search-bar">
          <div class="search-input">
            <i class="fas fa-search"></i>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="搜索资源文件..."
              @input="handleSearch"
            >
          </div>
        </div>
        <div class="filter-group">
          <select v-model="fileTypeFilter" @change="handleFilter" class="filter-select">
            <option value="">所有类型</option>
            <option value="video">视频文件</option>
            <option value="document">文档文件</option>
            <option value="presentation">演示文稿</option>
            <option value="image">图片文件</option>
            <option value="audio">音频文件</option>
            <option value="archive">压缩文件</option>
          </select>
        </div>
        <div class="action-buttons">
          <button @click="refreshResources" class="btn btn-secondary">
            <i class="fas fa-sync-alt"></i>
            刷新
          </button>
          <button @click="openUploadModal" class="btn btn-primary">
            <i class="fas fa-cloud-upload-alt"></i>
            上传资源
          </button>
        </div>
      </div>
    </div>

      <!-- 资源列表 -->
    <div class="resource-content">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else-if="filteredResources.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="fas fa-folder-open"></i>
            </div>
          <h3>暂无资源文件</h3>
          <p>点击上传按钮开始添加教学资源</p>
          <button @click="openUploadModal" class="btn btn-primary">
            <i class="fas fa-plus"></i>
            上传第一个资源
              </button>
          </div>

        <div v-else class="resource-grid">
          <div 
            v-for="resource in filteredResources" 
            :key="resource.id"
            class="resource-card"
            @click="previewResourceHandler(resource)"
          >
            <div class="resource-preview">
              <div class="file-icon" :class="getFileTypeClass(resource.fileType)">
                <i :class="getFileIcon(resource.fileType)"></i>
            </div>
              <div class="file-type-badge">{{ getFileTypeName(resource.fileType) }}</div>
            </div>
            
            <div class="resource-info">
              <h4 class="resource-title" :title="resource.title">{{ resource.title }}</h4>
              <p class="resource-filename">{{ resource.fileName }}</p>
              <p v-if="resource.description" class="resource-description">
                {{ resource.description }}
              </p>
            </div>

            <div class="resource-actions" @click.stop>
              <button 
                @click="previewResourceHandler(resource)" 
                class="action-btn preview"
                title="预览标注"
              >
                <i class="fas fa-eye"></i>
              </button>
              <button 
                @click="downloadResource(resource)" 
                class="action-btn download"
                title="下载"
              >
                <i class="fas fa-download"></i>
              </button>
              <button 
                @click="editResource(resource)" 
                class="action-btn edit"
                title="编辑"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button 
                @click="deleteResourceHandler(resource)" 
                class="action-btn delete"
                title="删除"
              >
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传模态框 -->
    <div v-if="showUploadModal" class="modal-overlay" @click="closeUploadModal">
      <div class="upload-modal" @click.stop>
        <div class="modal-header">
          <h3><i class="fas fa-cloud-upload-alt"></i>上传教学资源</h3>
          <button @click="closeUploadModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <!-- 拖拽上传区域 -->
          <div 
            class="upload-zone"
            :class="{ 'dragover': isDragOver }"
            @drop="handleDrop"
            @dragover.prevent="handleDragOver"
            @dragleave="handleDragLeave"
            @click="triggerFileInput"
          >
            <input 
              ref="fileInput" 
              type="file" 
              multiple 
              @change="handleFileSelect"
              accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.mp4,.avi,.mov,.mkv,.mp3,.wav,.jpg,.jpeg,.png,.gif,.bmp,.zip,.rar,.7z"
              style="display: none"
            >
            
            <div class="upload-icon">
              <i class="fas fa-cloud-upload-alt"></i>
            </div>
            <h4>拖拽文件到这里或点击选择</h4>
            <p>支持 PDF、Word、PPT、Excel、视频、音频、图片等多种格式</p>
            <div class="supported-formats">
              <span class="format-tag">PDF</span>
              <span class="format-tag">DOC</span>
              <span class="format-tag">PPT</span>
              <span class="format-tag">MP4</span>
              <span class="format-tag">MP3</span>
              <span class="format-tag">JPG</span>
              <span class="format-tag">ZIP</span>
            </div>
          </div>
          
          <!-- 上传队列 -->
          <div v-if="uploadQueue.length > 0" class="upload-queue">
            <h4><i class="fas fa-list"></i>上传队列 ({{ uploadQueue.length }})</h4>
            <div class="queue-list">
              <div 
                v-for="file in uploadQueue" 
                :key="file.id"
                class="queue-item"
              >
                <div class="file-info">
                  <div class="file-icon">
                    <i :class="getFileIcon(getFileTypeFromName(file.name))"></i>
            </div>
                  <div class="file-details">
                    <h5>{{ file.title || file.name }}</h5>
                    <p>{{ formatFileSize(file.size) }}</p>
                  </div>
                </div>
                
                <div class="file-meta">
                  <input 
                    v-model="file.title"
                    type="text"
                    placeholder="输入资源标题"
                    class="title-input"
                  >
                  <textarea 
                    v-model="file.description"
                    placeholder="添加描述（可选）"
                    class="description-input"
                    rows="2"
                  ></textarea>
                </div>

                <div class="file-status">
                  <div v-if="file.status === 'uploading'" class="upload-progress">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: file.progress + '%' }"></div>
                  </div>
                    <span>{{ file.progress }}%</span>
                </div>
                  <span v-else-if="file.status === 'completed'" class="status-success">
                    <i class="fas fa-check"></i>完成
                  </span>
                  <span v-else-if="file.status === 'error'" class="status-error">
                    <i class="fas fa-exclamation-triangle"></i>失败
                  </span>
                  <span v-else class="status-pending">
                    <i class="fas fa-clock"></i>等待
                  </span>
              </div>

                <button 
                  @click="removeFromQueue(file)"
                  class="remove-btn"
                  :disabled="file.status === 'uploading'"
                >
                  <i class="fas fa-times"></i>
              </button>
        </div>
      </div>
    </div>

          <!-- 上传控制 -->
          <div v-if="uploadQueue.length > 0" class="upload-controls">
            <button 
              @click="startUpload"
              :disabled="isUploading"
              class="btn btn-primary"
            >
              <i class="fas fa-upload"></i>
              {{ isUploading ? '上传中...' : '开始上传' }}
            </button>
            <button 
              @click="clearQueue"
              :disabled="isUploading"
              class="btn btn-secondary"
            >
              <i class="fas fa-trash"></i>
              清空队列
          </button>
        </div>
            </div>
      </div>
    </div>

    <!-- 预览模态框 -->
    <div v-if="showPreviewModal" class="modal-overlay" @click="closePreviewModal">
      <div class="preview-modal" @click.stop>
        <div class="modal-header">
          <h3>
            <i :class="getFileIcon(previewResource?.fileType)"></i>
            {{ previewResource?.title }}
          </h3>
          <button @click="closePreviewModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="preview-content">
            <!-- 预览内容根据文件类型动态显示 -->
            <div v-if="isImageFile(previewResource?.fileName)" class="image-preview">
              <img :src="getResourceUrl(previewResource)" :alt="previewResource?.title">
            </div>
            <div v-else-if="isVideoFile(previewResource?.fileName)" class="video-preview">
              <video controls :src="getResourceUrl(previewResource)"></video>
            </div>
            <div v-else-if="isAudioFile(previewResource?.fileName)" class="audio-preview">
              <audio controls :src="getResourceUrl(previewResource)"></audio>
            </div>
            <div v-else class="file-info-preview">
              <div class="large-file-icon">
                <i :class="getFileIcon(previewResource?.fileType)"></i>
            </div>
              <h4>{{ previewResource?.title }}</h4>
              <p>{{ previewResource?.description }}</p>
              <div class="file-details">
                <span>文件大小: {{ formatFileSize(previewResource?.fileSize) }}</span>
                <span>下载次数: {{ previewResource?.downloadCount || 0 }}</span>
          </div>
              <button @click="downloadResource(previewResource)" class="btn btn-primary">
                <i class="fas fa-download"></i>
                下载文件
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoursesByTeacherId } from '@/api/course'
import { 
  getCourseResources, 
  uploadResource, 
  downloadResource as downloadResourceAPI,
  deleteResource as deleteResourceAPI 
} from '@/api/courseResource'

// 响应式数据
const courses = ref([])
const resources = ref([])
const selectedCourseId = ref(null)
const loading = ref(false)
const searchQuery = ref('')
const fileTypeFilter = ref('')

// 上传相关
const showUploadModal = ref(false)
const uploadQueue = ref([])
const isUploading = ref(false)
const isDragOver = ref(false)
const fileInput = ref(null)

// 预览相关
const showPreviewModal = ref(false)
const previewResource = ref(null)

// 获取当前教师ID
const teacherId = localStorage.getItem('userId') || '2'

// 计算属性
const totalResources = computed(() => resources.value.length)
const totalSize = computed(() => 
  resources.value.reduce((sum, resource) => sum + (resource.fileSize || 0), 0)
)
const totalDownloads = computed(() => 
  resources.value.reduce((sum, resource) => sum + (resource.downloadCount || 0), 0)
)

const filteredResources = computed(() => {
  return resources.value.filter(resource => {
    const matchesSearch = !searchQuery.value || 
      resource.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      resource.fileName.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesType = !fileTypeFilter.value || 
      getFileTypeCategory(resource.fileType) === fileTypeFilter.value
    
    return matchesSearch && matchesType
  })
})

// 生命周期
onMounted(async () => {
  await fetchCourses()
})

// 方法
const fetchCourses = async () => {
  try {
    loading.value = true
    const response = await getCoursesByTeacherId(teacherId)
    if (response && response.success && response.data) {
      courses.value = response.data.map(course => ({
        ...course,
        resourceCount: 0
      }))
      
      // 获取每个课程的资源数量
      for (const course of courses.value) {
        course.resourceCount = await getCourseResourceCount(course.id)
      }
      
      console.log('获取课程列表成功:', response.data)
      
      if (courses.value.length === 0) {
        ElMessage.warning('您还没有任何课程，请先在系统中创建课程')
      }
    } else {
      // 使用默认课程数据作为后备
      courses.value = [
        { id: 1, name: '数据结构', subject: '计算机科学', description: '数据结构与算法基础', resourceCount: 0 },
        { id: 2, name: 'Linux系统', subject: '操作系统', description: 'Linux系统管理与开发', resourceCount: 0 },
        { id: 3, name: '计算机网络', subject: '网络技术', description: '计算机网络原理与应用', resourceCount: 0 }
      ]
      
      for (const course of courses.value) {
        course.resourceCount = await getCourseResourceCount(course.id)
      }
      
      console.log('使用默认课程数据')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const selectCourse = (course) => {
  selectedCourseId.value = course.id
  ElMessage.success(`已选择课程：${course.title || course.name}`)
  fetchResources()
}

const fetchResources = async () => {
  if (!selectedCourseId.value) return
  
  try {
    loading.value = true
    const response = await getCourseResources(selectedCourseId.value)
    if (response && response.success && response.data) {
      resources.value = response.data
      console.log('获取课程资源成功:', response.data)
    } else {
      resources.value = []
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源列表失败: ' + error.message)
    resources.value = []
  } finally {
    loading.value = false
  }
}

const getCourseResourceCount = async (courseId) => {
  try {
    const response = await getCourseResources(courseId)
    return response && response.success && response.data ? response.data.length : 0
  } catch (error) {
    return 0
  }
}

const refreshResources = () => {
  fetchResources()
  ElMessage.success('已刷新资源列表')
}

const handleSearch = () => {
  // 搜索逻辑由computed属性处理
}

const handleFilter = () => {
  // 筛选逻辑由computed属性处理
}

// 上传相关方法
const openUploadModal = () => {
  if (!selectedCourseId.value) {
    ElMessage.warning('请先选择一门课程')
    return
  }
  showUploadModal.value = true
  uploadQueue.value = []
}

const closeUploadModal = () => {
  if (isUploading.value) {
    ElMessageBox.confirm('正在上传中，确定要关闭吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      showUploadModal.value = false
      uploadQueue.value = []
      isUploading.value = false
    })
  } else {
    showUploadModal.value = false
    uploadQueue.value = []
  }
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  addFilesToQueue(files)
  event.target.value = '' // 清空input
}



// 获取文件类型
const getFileType = (filename) => {
  const ext = filename.split('.').pop().toLowerCase()
  const typeMap = {
    pdf: 'document',
    doc: 'document',
    docx: 'document',
    ppt: 'presentation',
    pptx: 'presentation',
    mp4: 'video',
    avi: 'video',
    jpg: 'image',
    jpeg: 'image',
    png: 'image',
    mp3: 'audio',
    wav: 'audio'
  }
  return typeMap[ext] || 'document'
}

// 获取文件图标
const getFileIcon = (fileType) => {
  const iconMap = {
    pdf: 'fas fa-file-pdf',
    doc: 'fas fa-file-word',
    docx: 'fas fa-file-word',
    ppt: 'fas fa-file-powerpoint',
    pptx: 'fas fa-file-powerpoint',
    xls: 'fas fa-file-excel',
    xlsx: 'fas fa-file-excel',
    mp4: 'fas fa-file-video',
    avi: 'fas fa-file-video',
    mov: 'fas fa-file-video',
    mkv: 'fas fa-file-video',
    mp3: 'fas fa-file-audio',
    wav: 'fas fa-file-audio',
    jpg: 'fas fa-file-image',
    jpeg: 'fas fa-file-image',
    png: 'fas fa-file-image',
    gif: 'fas fa-file-image',
    bmp: 'fas fa-file-image',
    zip: 'fas fa-file-archive',
    rar: 'fas fa-file-archive',
    '7z': 'fas fa-file-archive'
  }
  return iconMap[fileType?.toLowerCase()] || 'fas fa-file'
}

// 获取文件图标样式类
const getFileIconClass = (type) => {
  return `file-icon-${type}`
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleDrop = (event) => {
  event.preventDefault()
  isDragOver.value = false
  const files = Array.from(event.dataTransfer.files)
  addFilesToQueue(files)
}

const handleDragOver = (event) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const addFilesToQueue = (files) => {
  const newFiles = files.map(file => ({
      id: Date.now() + Math.random(),
    file,
      name: file.name,
      size: file.size,
    title: file.name.split('.')[0],
    description: '',
    status: 'pending',
      progress: 0
  }))
  
  uploadQueue.value.push(...newFiles)
}

const removeFromQueue = (file) => {
  const index = uploadQueue.value.findIndex(f => f.id === file.id)
  if (index > -1) {
    uploadQueue.value.splice(index, 1)
  }
}

const clearQueue = () => {
  uploadQueue.value = []
}

const startUpload = async () => {
  if (!selectedCourseId.value) {
    ElMessage.error('请先选择课程')
    return
  }
  
  isUploading.value = true
  
  for (const fileItem of uploadQueue.value) {
    if (fileItem.status === 'completed') continue
    
    try {
      fileItem.status = 'uploading'
      fileItem.progress = 0
      
    const formData = new FormData()
      formData.append('file', fileItem.file)
      formData.append('title', fileItem.title || fileItem.name)
      formData.append('description', fileItem.description || '')
      formData.append('courseId', selectedCourseId.value)
      formData.append('teacherId', teacherId)
      
      // 模拟上传进度
      const uploadInterval = setInterval(() => {
        if (fileItem.progress < 90) {
          fileItem.progress += Math.random() * 20
        }
      }, 200)
      
      const response = await uploadResource(formData)
      
      clearInterval(uploadInterval)
      fileItem.progress = 100
      
      if (response && response.success) {
        fileItem.status = 'completed'
        ElMessage.success(`${fileItem.title} 上传成功`)
      } else {
        fileItem.status = 'error'
        ElMessage.error(`${fileItem.title} 上传失败: ${response?.msg || '未知错误'}`)
      }
    } catch (error) {
      fileItem.status = 'error'
      fileItem.progress = 0
      ElMessage.error(`${fileItem.title} 上传失败: ${error.message}`)
    }
  }
  
  isUploading.value = false
  
  // 刷新资源列表
  await fetchResources()
  
  // 检查是否全部成功
  const completedCount = uploadQueue.value.filter(f => f.status === 'completed').length
  if (completedCount === uploadQueue.value.length) {
    ElMessage.success(`所有文件上传成功！共${completedCount}个文件`)
    setTimeout(() => {
      closeUploadModal()
    }, 1500)
  }
}

// 资源操作方法
const previewResourceHandler = (resource) => {
  previewResource.value = resource
  showPreviewModal.value = true
}

const closePreviewModal = () => {
  showPreviewModal.value = false
  previewResource.value = null
}

const downloadResource = async (resource) => {
  try {
    // 创建下载链接
    const downloadUrl = `http://localhost:8080${resource.fileUrl}`
    
    // 创建临时a标签进行下载
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = resource.fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 更新下载次数
    await downloadResourceAPI(resource.id)
    resource.downloadCount = (resource.downloadCount || 0) + 1
    
    ElMessage.success('下载开始')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  }
}

const editResource = (resource) => {
  // 编辑资源逻辑
  ElMessage.info('编辑功能开发中...')
}

const shareResource = (resource) => {
  // 分享资源逻辑
  ElMessage.info('分享功能开发中...')
}

const deleteResourceHandler = async (resource) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资源"${resource.title}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true
      }
    )
    
    console.log('准备删除资源:', resource.id)
    const response = await deleteResourceAPI(resource.id)
    console.log('删除API响应:', response)
    
    if (response && response.success) {
      ElMessage.success({
        message: '资源删除成功！',
        type: 'success',
        duration: 2000
      })
      
      // 从列表中移除该资源
      const index = resources.value.findIndex(r => r.id === resource.id)
      if (index > -1) {
        resources.value.splice(index, 1)
      }
      
      // 刷新资源列表
      await fetchResources()
    } else {
      ElMessage.error({
        message: response?.msg || response?.message || '删除失败',
        type: 'error',
        duration: 3000
      })
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除资源失败:', error)
      ElMessage.error({
        message: '删除失败: ' + (error.message || '未知错误'),
        type: 'error',
        duration: 3000
      })
    }
  }
}



const getFileTypeClass = (fileType) => {
  const classMap = {
    pdf: 'file-pdf',
    doc: 'file-doc',
    docx: 'file-doc',
    ppt: 'file-ppt',
    pptx: 'file-ppt',
    xls: 'file-excel',
    xlsx: 'file-excel',
    mp4: 'file-video',
    avi: 'file-video',
    mov: 'file-video',
    mkv: 'file-video',
    mp3: 'file-audio',
    wav: 'file-audio',
    jpg: 'file-image',
    jpeg: 'file-image',
    png: 'file-image',
    gif: 'file-image',
    bmp: 'file-image',
    zip: 'file-archive',
    rar: 'file-archive',
    '7z': 'file-archive'
  }
  return classMap[fileType?.toLowerCase()] || 'file-default'
}

const getFileTypeName = (fileType) => {
  const nameMap = {
    pdf: 'PDF',
    doc: 'Word',
    docx: 'Word',
    ppt: 'PowerPoint',
    pptx: 'PowerPoint',
    xls: 'Excel',
    xlsx: 'Excel',
    mp4: '视频',
    avi: '视频',
    mov: '视频',
    mkv: '视频',
    mp3: '音频',
    wav: '音频',
    jpg: '图片',
    jpeg: '图片',
    png: '图片',
    gif: '图片',
    bmp: '图片',
    zip: '压缩包',
    rar: '压缩包',
    '7z': '压缩包'
  }
  return nameMap[fileType?.toLowerCase()] || '文件'
}

const getFileTypeCategory = (fileType) => {
  const categoryMap = {
    mp4: 'video',
    avi: 'video',
    mov: 'video',
    mkv: 'video',
    pdf: 'document',
    doc: 'document',
    docx: 'document',
    ppt: 'presentation',
    pptx: 'presentation',
    xls: 'document',
    xlsx: 'document',
    jpg: 'image',
    jpeg: 'image',
    png: 'image',
    gif: 'image',
    bmp: 'image',
    mp3: 'audio',
    wav: 'audio',
    zip: 'archive',
    rar: 'archive',
    '7z': 'archive'
  }
  return categoryMap[fileType?.toLowerCase()] || 'other'
}

const getFileTypeFromName = (fileName) => {
  const extension = fileName.split('.').pop()?.toLowerCase()
  return extension || 'unknown'
}

const isImageFile = (fileName) => {
  const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  const ext = getFileTypeFromName(fileName)
  return imageExts.includes(ext)
}

const isVideoFile = (fileName) => {
  const videoExts = ['mp4', 'avi', 'mov', 'mkv', 'webm']
  const ext = getFileTypeFromName(fileName)
  return videoExts.includes(ext)
}

const isAudioFile = (fileName) => {
  const audioExts = ['mp3', 'wav', 'ogg', 'flac']
  const ext = getFileTypeFromName(fileName)
  return audioExts.includes(ext)
}

const getResourceUrl = (resource) => {
  // 返回实际的资源URL
  return `http://localhost:8080${resource.fileUrl}`
}

// 更新文件信息
const updateFileInfo = (fileId, field, value) => {
  const fileItem = uploadQueue.value.find(f => f.id === fileId)
  if (fileItem) {
    fileItem[field] = value
  }
}
</script>

<style lang="scss" scoped>
.resource-management {
  padding: 40px 24px 40px 24px;
  background: #f8f9ff;
  min-height: 100vh;
}

/* 现代化的页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px;
  margin-bottom: 32px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.2);
  
  .header-content {
    .header-main {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 24px;
      
      .header-icon {
        display: flex;
        align-items: center;
        gap: 16px;
        
        i {
          font-size: 48px;
          color: white;
          background: rgba(255, 255, 255, 0.2);
          padding: 16px;
          border-radius: 16px;
          backdrop-filter: blur(10px);
        }
        
        .header-text {
          h1 {
            color: white;
            font-size: 2.5rem;
            font-weight: 700;
            margin: 0 0 8px 0;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          }
          
          p {
            color: rgba(255, 255, 255, 0.9);
            font-size: 1.1rem;
            margin: 0;
          }
        }
      }
      
      .header-stats {
        display: flex;
        gap: 24px;
        
        .stat-item {
          text-align: center;
          background: rgba(255, 255, 255, 0.1);
          padding: 16px 24px;
          border-radius: 16px;
          backdrop-filter: blur(10px);
          
          .stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: white;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 0.9rem;
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }
    }
  }
}

/* 课程选择区域 */
.course-section {
  background: white;
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  
  .section-header {
    text-align: center;
    margin-bottom: 32px;
    
    h2 {
      font-size: 1.8rem;
      font-weight: 600;
      color: #2d3748;
      margin-bottom: 8px;
      
      i {
        color: #667eea;
        margin-right: 12px;
      }
    }
    
    p {
      color: #718096;
      font-size: 1rem;
      margin: 0;
    }
  }
  
  .course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
    
    .course-card {
      background: white;
      border: 2px solid #e2e8f0;
      border-radius: 16px;
      padding: 24px;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        transform: scaleX(0);
        transition: transform 0.3s ease;
      }
      
      &:hover {
        border-color: #667eea;
        transform: translateY(-4px);
        box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
        
        &::before {
          transform: scaleX(1);
        }
      }
      
      &.active {
        border-color: #667eea;
        background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
        
        &::before {
          transform: scaleX(1);
        }
      }
      
      .course-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .course-avatar {
          width: 48px;
          height: 48px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          i {
            color: white;
            font-size: 20px;
          }
        }
        
        .course-badge {
          background: #e3f2fd;
          color: #1976d2;
          padding: 4px 12px;
          border-radius: 20px;
          font-size: 0.8rem;
          font-weight: 600;
        }
      }
      
      .course-content {
        h3 {
          font-size: 1.2rem;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 8px;
        }
        
        p {
          color: #718096;
          font-size: 0.9rem;
          line-height: 1.5;
          margin-bottom: 16px;
        }
      }
      
      .course-footer {
        .course-subject {
          background: #f0fff4;
          color: #22543d;
          padding: 4px 8px;
          border-radius: 8px;
          font-size: 0.8rem;
          font-weight: 500;
        }
      }
    }
  }
}

/* 资源管理区域 */
.resource-section {
  background: white;
  border-radius: 20px;
  padding: 40px 32px 40px 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-top: 40px;
  margin-bottom: 40px;
  
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    padding: 24px;
    background: #f8f9ff;
    border-radius: 16px;
    
    .search-bar {
      display: flex;
      gap: 16px;
      flex: 1;
      max-width: 400px;
      
      .search-input {
        position: relative;
        width: 100%;
        
        i {
          position: absolute;
          left: 16px;
          top: 50%;
          transform: translateY(-50%);
          color: #a0aec0;
          z-index: 1;
        }
        
        input {
          width: 100%;
          min-width: 180px;
          padding: 12px 16px 12px 48px;
          border: 2px solid #e2e8f0;
          border-radius: 12px;
          font-size: 14px;
          transition: all 0.3s ease;
          background: white;
          
          &:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }
    }
    
    .filter-group {
      min-width: 160px;
      
      .filter-select {
        width: 100%;
        padding: 12px 16px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        background: white;
        font-size: 14px;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }
    
    .action-buttons {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
}

/* 按钮样式 */
.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  
  &.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
    }
  }
  
  &.btn-secondary {
    background: #f7fafc;
    color: #4a5568;
    border: 2px solid #e2e8f0;
    
    &:hover {
      background: #edf2f7;
      border-color: #cbd5e0;
    }
  }
}

/* 加载和空状态 */
.loading-state {
  text-align: center;
  padding: 60px 0;
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #e2e8f0;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 16px;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  
  .empty-icon {
    font-size: 4rem;
    color: #cbd5e0;
    margin-bottom: 24px;
  }
  
  h3 {
    font-size: 1.5rem;
    color: #4a5568;
    margin-bottom: 8px;
  }
  
  p {
    color: #718096;
    margin-bottom: 24px;
  }
}

/* 资源网格 */
.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  
  .resource-card {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      border-color: #667eea;
      transform: translateY(-4px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
    }
    
    .resource-preview {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .file-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
          font-size: 24px;
          color: white;
        }
        
        &.file-pdf { background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%); }
        &.file-doc { background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); }
        &.file-ppt { background: linear-gradient(135deg, #ea580c 0%, #c2410c 100%); }
        &.file-excel { background: linear-gradient(135deg, #16a34a 0%, #15803d 100%); }
        &.file-video { background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%); }
        &.file-audio { background: linear-gradient(135deg, #059669 0%, #047857 100%); }
        &.file-image { background: linear-gradient(135deg, #0891b2 0%, #0e7490 100%); }
        &.file-archive { background: linear-gradient(135deg, #ca8a04 0%, #a16207 100%); }
        &.file-default { background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%); }
      }
      
      .file-type-badge {
        background: #f0f9ff;
        color: #0369a1;
        padding: 4px 8px;
        border-radius: 8px;
        font-size: 0.75rem;
        font-weight: 600;
      }
    }
    
    .resource-info {
      .resource-title {
        font-size: 1.1rem;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .resource-filename {
        font-size: 0.85rem;
        color: #718096;
        margin-bottom: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .resource-meta {
        display: flex;
        justify-content: space-between;
        font-size: 0.8rem;
        color: #a0aec0;
        margin-bottom: 8px;
        
        .download-count {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
      
      .resource-description {
        font-size: 0.85rem;
        color: #718096;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
    
    .resource-actions {
      position: absolute;
      top: 16px;
      right: 16px;
      display: flex;
      gap: 8px;
      opacity: 0;
      transform: translateY(-4px);
      transition: all 0.3s ease;
      z-index: 10;
      
      .action-btn {
        width: 36px;
        height: 36px;
        border: none;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        
        i {
          font-size: 15px;
        }
        
        &.preview {
          background: #dbeafe;
          color: #1e40af;
          &:hover { 
            background: #bfdbfe;
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
          }
        }
        
        &.download {
          background: #e0f2fe;
          color: #0277bd;
          &:hover { 
            background: #b3e5fc;
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(2, 119, 189, 0.3);
          }
        }
        
        &.edit {
          background: #f3e8ff;
          color: #7c3aed;
          &:hover { 
            background: #e9d5ff;
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
          }
        }
        
        &.delete {
          background: #fef2f2;
          color: #dc2626;
          &:hover { 
            background: #fee2e2;
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
          }
        }
      }
    }
    
    &:hover .resource-actions {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 32px;
  border-radius: 24px;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
    pointer-events: none;
  }
  
  .header-content {
    position: relative;
    z-index: 2;
    
    .header-main {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      .header-text {
      h1 {
        margin: 0;
        font-size: 2.5rem;
        font-weight: 800;
        background: linear-gradient(135deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }
    
        p {
      margin: 0;
      font-size: 1.1rem;
      opacity: 0.9;
      font-weight: 400;
        }
      }
    }
    
    .header-stats {
      display: flex;
      gap: 16px;
      
      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        
        .stat-number {
          font-size: 1.8rem;
          font-weight: 700;
          color: #2d3748;
        }
        
        .stat-label {
          font-size: 14px;
          color: #718096;
          font-weight: 600;
        }
      }
    }
  }
  
  .header-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
      animation: float 6s ease-in-out infinite;
      
      &.shape-1 {
        width: 60px;
        height: 60px;
        top: 20px;
        right: 40px;
        animation-delay: 0s;
      }
      
      &.shape-2 {
        width: 40px;
        height: 40px;
        bottom: 30px;
        right: 80px;
        animation-delay: 2s;
      }
      
      &.shape-3 {
        width: 30px;
        height: 30px;
        top: 60px;
        right: 120px;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 资源控制 */
.resource-controls {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .search-section {
    display: flex;
    gap: 16px;
    margin-bottom: 20px;
    
    .search-box {
      flex: 1;
      position: relative;
      
      .search-icon {
        position: absolute;
        left: 16px;
        top: 50%;
        transform: translateY(-50%);
        width: 20px;
        height: 20px;
        opacity: 0.6;
        pointer-events: none;
      }
      
      input {
        width: 100%;
        padding: 12px 16px 12px 48px;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        font-size: 14px;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        &::placeholder {
          color: #a0aec0;
        }
      }
      
      .search-btn {
        position: absolute;
        right: 8px;
        top: 50%;
        transform: translateY(-50%);
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 8px;
        padding: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-50%) scale(1.05);
        }
        
        .btn-icon {
          width: 16px;
          height: 16px;
          opacity: 0.8;
        }
      }
    }
    
    .filter-controls {
      display: flex;
      gap: 12px;
      
      .filter-item {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .filter-icon {
          width: 16px;
          height: 16px;
          opacity: 0.6;
        }
        
        select {
          padding: 12px 16px;
          border: 1px solid #e2e8f0;
          border-radius: 8px;
          font-size: 14px;
          background: white;
          transition: all 0.3s ease;
          
          &:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }
    }
  }
  
  .action-buttons {
    display: flex;
    gap: 12px;
  }
}

/* 按钮样式 */
.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  
  .btn-icon {
    width: 18px;
    height: 18px;
    opacity: 0.8;
  }
  
  &.primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
    }
  }
  
  &.secondary {
    background: #f7fafc;
    color: #718096;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: #edf2f7;
      transform: translateY(-2px);
    }
  }
  
  &.success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
    }
  }
}

/* 资源内容 */
.resource-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  
  .folder-view {
    background: white;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .breadcrumb {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 24px;
      padding: 12px 16px;
      background: #f7fafc;
      border-radius: 12px;
      
      .breadcrumb-item {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        font-size: 14px;
        color: #718096;
        
        .breadcrumb-icon {
          width: 16px;
          height: 16px;
          opacity: 0.6;
        }
        
        &:hover {
          background: #e2e8f0;
          color: #4a5568;
        }
        
        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          
          .breadcrumb-icon {
            opacity: 0.8;
          }
        }
      }
    }
    
    .resource-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px;
      
      .resource-item {
        background: #f7fafc;
        border: 1px solid #e2e8f0;
        border-radius: 16px;
        padding: 20px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        
        &:hover {
          background: white;
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
          
          .resource-actions {
            opacity: 1;
            transform: translateY(0);
          }
        }
        
        &.folder {
          border-color: #c6f6d5;
          background: #f0fff4;
          
          &:hover {
            background: white;
            border-color: #10b981;
          }
        }
        
        .resource-icon {
          width: 48px;
          height: 48px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 12px;
          
          .icon-img {
            width: 24px;
            height: 24px;
            opacity: 0.8;
          }
          
          &.document {
            background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
          }
          
          &.video {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
          }
          
          &.image {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          }
          
          &.audio {
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
          }
          
          &.presentation {
            background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
          }
        }
        
        .resource-info {
          margin-bottom: 12px;
          
          .resource-name {
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 4px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          
          .resource-meta {
            font-size: 12px;
            color: #718096;
          }
        }
        
        .resource-actions {
          display: flex;
          gap: 8px;
          opacity: 0;
          transform: translateY(10px);
          transition: all 0.3s ease;
          
          .action-btn {
            width: 32px;
            height: 32px;
            padding: 0;
            border-radius: 8px;
            background: #f7fafc;
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            
            &:hover {
              background: #edf2f7;
              transform: scale(1.05);
            }
            
            .action-icon {
              width: 16px;
              height: 16px;
              opacity: 0.6;
            }
          }
        }
      }
    }
  }
  
  .resource-stats {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    .stats-card {
      background: white;
      border-radius: 16px;
      padding: 20px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }
      
      .stats-icon {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 12px;
        
        .icon-img {
          width: 24px;
          height: 24px;
          opacity: 0.8;
        }
      }
      
      .stats-content {
        .stats-number {
          font-size: 1.8rem;
          font-weight: 700;
          color: #2d3748;
          margin-bottom: 4px;
        }
        
        .stats-label {
          font-size: 14px;
          color: #718096;
          font-weight: 600;
        }
      }
    }
  }
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
  
  .modal-content {
    background: white;
    border-radius: 20px;
    max-width: 600px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    
    &.large {
      max-width: 800px;
    }
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      border-bottom: 1px solid #e2e8f0;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .modal-icon {
          width: 24px;
          height: 24px;
          opacity: 0.7;
        }
        
        h3 {
          margin: 0;
          font-size: 1.3rem;
          font-weight: 700;
          color: #2d3748;
        }
      }
      
      .close-btn {
        background: none;
        border: none;
        cursor: pointer;
        padding: 8px;
        border-radius: 8px;
        transition: all 0.3s ease;
        
        &:hover {
          background: #f7fafc;
        }
        
        .close-icon {
          width: 20px;
          height: 20px;
          opacity: 0.6;
        }
      }
    }
    
    .modal-body {
      padding: 24px;
      
      .upload-area {
        border: 2px dashed #e2e8f0;
        border-radius: 16px;
        padding: 40px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          border-color: #667eea;
          background: #f7fafc;
        }
        
        .upload-icon {
          width: 64px;
          height: 64px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 16px;
          
          .icon-img {
            width: 32px;
            height: 32px;
            opacity: 0.8;
          }
        }
        
        .upload-text {
          p {
            margin: 0 0 8px 0;
            font-size: 16px;
            color: #2d3748;
            font-weight: 600;
          }
          
          .upload-hint {
            font-size: 14px;
            color: #718096;
            font-weight: 400;
          }
        }
      }
      
      .upload-queue {
        margin-top: 24px;
        
        .queue-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;
          
          .queue-icon {
            width: 20px;
            height: 20px;
            opacity: 0.6;
          }
          
          h4 {
            margin: 0;
            font-size: 1.1rem;
            font-weight: 600;
            color: #2d3748;
          }
        }
        
        .upload-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px;
          background: #f7fafc;
          border-radius: 8px;
          margin-bottom: 8px;
          
          .upload-info {
            flex: 1;
            
            .upload-name {
              font-size: 14px;
              font-weight: 600;
              color: #2d3748;
              margin-bottom: 4px;
            }
            
            .upload-progress {
              display: flex;
              align-items: center;
              gap: 8px;
              
              .progress-bar {
                flex: 1;
                height: 4px;
                background: #e2e8f0;
                border-radius: 2px;
                overflow: hidden;
                
                .progress-fill {
                  height: 100%;
                  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                  border-radius: 2px;
                  transition: width 0.3s ease;
                }
              }
              
              .progress-text {
                font-size: 12px;
                color: #718096;
                min-width: 40px;
              }
            }
          }
          
          .cancel-btn {
            background: #fed7d7;
            color: #e53e3e;
            border: none;
            border-radius: 6px;
            padding: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &:hover {
              background: #feb2b2;
            }
            
            .cancel-icon {
              width: 16px;
              height: 16px;
              opacity: 0.8;
            }
          }
        }
      }
      
      .file-preview {
        .image-preview {
          text-align: center;
          
          img {
            max-width: 100%;
            max-height: 400px;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
          }
        }
      }
    }
  }
}

/* 上传弹窗样式 */
.upload-modal {
  background: white;
  border-radius: 24px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
  animation: slideUp 0.4s ease-out;
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32px 32px 24px;
    border-bottom: 1px solid #f1f5f9;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 24px 24px 0 0;
    
    h3 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 700;
      color: #1e293b;
      display: flex;
      align-items: center;
      gap: 12px;
      
      i {
        color: #667eea;
        font-size: 1.8rem;
      }
    }
    
    .close-btn {
      background: #f1f5f9;
      border: none;
      border-radius: 12px;
      padding: 12px;
      cursor: pointer;
      color: #64748b;
      transition: all 0.3s ease;
      
      &:hover {
        background: #e2e8f0;
        color: #374151;
        transform: scale(1.05);
      }
      
      i {
        font-size: 18px;
      }
    }
  }
  
  .modal-body {
    padding: 32px;
  }
}

/* 预览弹窗样式 */
.preview-modal {
  background: white;
  border-radius: 24px;
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
  animation: slideUp 0.4s ease-out;
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32px 32px 24px;
    border-bottom: 1px solid #f1f5f9;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 24px 24px 0 0;
    
    h3 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 700;
      color: #1e293b;
      display: flex;
      align-items: center;
      gap: 12px;
      
      i {
        color: #667eea;
        font-size: 1.8rem;
      }
    }
    
    .close-btn {
      background: #f1f5f9;
      border: none;
      border-radius: 12px;
      padding: 12px;
      cursor: pointer;
      color: #64748b;
      transition: all 0.3s ease;
      
      &:hover {
        background: #e2e8f0;
        color: #374151;
        transform: scale(1.05);
      }
      
      i {
        font-size: 18px;
      }
    }
  }
  
  .modal-body {
    padding: 32px;
  }
}

/* 上传区域样式 */
.upload-zone {
  border: 3px dashed #cbd5e1;
  border-radius: 20px;
  padding: 48px 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="rgba(102,126,234,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23dots)"/></svg>');
    opacity: 0.5;
    pointer-events: none;
  }
  
  &.dragover {
    border-color: #667eea;
    background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%);
    transform: scale(1.02);
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);
    
    .upload-icon i {
      animation: bounce 0.6s ease-in-out;
    }
  }
  
  &:hover {
    border-color: #667eea;
    background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%);
    transform: translateY(-2px);
    box-shadow: 0 12px 40px rgba(102, 126, 234, 0.15);
  }
  
  .upload-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 24px;
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
    
    i {
      font-size: 32px;
      color: white;
    }
  }
  
  h4 {
    margin: 0 0 12px 0;
    font-size: 1.3rem;
    font-weight: 700;
    color: #1e293b;
  }
  
  p {
    margin: 0 0 24px 0;
    font-size: 1rem;
    color: #64748b;
    line-height: 1.6;
  }
  
  .supported-formats {
    display: flex;
    gap: 8px;
    justify-content: center;
    flex-wrap: wrap;
    
    .format-tag {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    }
  }
}

/* 上传队列样式 */
.upload-queue {
  margin-top: 32px;
  
  h4 {
    margin: 0 0 20px 0;
    font-size: 1.2rem;
    font-weight: 700;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 12px;
    
    i {
      color: #667eea;
      font-size: 1.3rem;
    }
  }
  
  .queue-list {
    max-height: 300px;
    overflow-y: auto;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: #f1f5f9;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #cbd5e1;
      border-radius: 3px;
      
      &:hover {
        background: #94a3b8;
      }
    }
  }
}

/* 队列项样式 */
.queue-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  margin-bottom: 16px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border-color: #667eea;
  }
  
  .file-info {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 200px;
    
    .file-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      i {
        color: white;
        font-size: 20px;
      }
    }
    
    .file-details {
      h5 {
        margin: 0 0 4px 0;
        font-size: 14px;
        font-weight: 600;
        color: #1e293b;
        word-break: break-word;
      }
      
      p {
        margin: 0;
        font-size: 12px;
        color: #64748b;
      }
    }
  }
  
  .file-meta {
    flex: 1;
    min-width: 0;
    
    .title-input {
      width: 100%;
      padding: 8px 12px;
      border: 1px solid #d1d5db;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #1e293b;
      margin-bottom: 8px;
      transition: all 0.3s ease;
      
      &:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
    }
    
    .description-input {
      width: 100%;
      padding: 8px 12px;
      border: 1px solid #d1d5db;
      border-radius: 8px;
      font-size: 13px;
      color: #374151;
      resize: vertical;
      min-height: 60px;
      transition: all 0.3s ease;
      
      &:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
    }
  }
  
  .file-status {
    min-width: 100px;
    text-align: center;
    
    .upload-progress {
      .progress-bar {
        width: 100%;
        height: 6px;
        background: #e2e8f0;
        border-radius: 3px;
        overflow: hidden;
        margin-bottom: 8px;
        
        .progress-fill {
          height: 100%;
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          border-radius: 3px;
          transition: width 0.3s ease;
        }
      }
      
      span {
        font-size: 12px;
        font-weight: 600;
        color: #059669;
      }
    }
    
    .status-success {
      color: #059669;
      font-weight: 600;
      font-size: 14px;
      
      i {
        margin-right: 4px;
      }
    }
    
    .status-error {
      color: #dc2626;
      font-weight: 600;
      font-size: 14px;
      
      i {
        margin-right: 4px;
      }
    }
    
    .status-pending {
      color: #d97706;
      font-weight: 600;
      font-size: 14px;
      
      i {
        margin-right: 4px;
      }
    }
  }
  
  .remove-btn {
    background: #fef2f2;
    color: #dc2626;
    border: none;
    border-radius: 8px;
    padding: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: #fee2e2;
      transform: scale(1.1);
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
      
      &:hover {
        transform: none;
      }
    }
    
    i {
      font-size: 14px;
    }
  }
}

/* 上传控制按钮 */
.upload-controls {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 16px;
  justify-content: center;
  
  .btn {
    padding: 14px 28px;
    border: none;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 140px;
    justify-content: center;
    
    &.btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
      
      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 32px rgba(102, 126, 234, 0.4);
      }
      
      &:disabled {
        opacity: 0.7;
        cursor: not-allowed;
        transform: none;
      }
    }
    
    &.btn-secondary {
      background: #f1f5f9;
      color: #64748b;
      border: 1px solid #d1d5db;
      
      &:hover:not(:disabled) {
        background: #e2e8f0;
        color: #374151;
        transform: translateY(-1px);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    i {
      font-size: 16px;
    }
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(60px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes bounce {
  0%, 20%, 60%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  80% {
    transform: translateY(-5px);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;
    
    .header-title h1 {
      font-size: 2rem;
    }
  }
  
  .resource-controls {
    .search-section {
      flex-direction: column;
      gap: 12px;
      
      .filter-controls {
        flex-direction: column;
      }
    }
    
    .action-buttons {
      flex-direction: column;
    }
  }
  
  .resource-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .resource-grid {
    grid-template-columns: 1fr;
  }
  
  .resource-stats {
    flex-direction: row;
    overflow-x: auto;
    
    .stats-card {
      min-width: 200px;
    }
  }
}
</style>

 