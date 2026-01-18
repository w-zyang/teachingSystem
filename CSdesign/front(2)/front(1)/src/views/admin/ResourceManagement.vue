<template>
  <div class="resource-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h1>资源管理</h1>
          <div class="header-badge">
            <span class="badge-icon">📁</span>
            <span class="badge-text">教学资源管理</span>
          </div>
        </div>
        <p class="header-subtitle">管理系统内所有课件、练习等教学资源，支持上传、分类和权限管理</p>
      </div>
      <div class="header-actions">
        <button @click="openUploadModal" class="action-btn primary">
          <span class="btn-icon">📤</span>
          上传资源
        </button>
        <button @click="fetchResourceStats" class="action-btn secondary">
          <span class="btn-icon">📊</span>
          资源统计
        </button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon total">📚</div>
        <div class="stat-content">
          <div class="stat-number">{{ resourceStats.totalResources }}</div>
          <div class="stat-label">总资源数</div>
          <div class="stat-trend positive">+15 本月新增</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon size">📁</div>
        <div class="stat-content">
          <div class="stat-number">{{ resourceStats.totalSize }}</div>
          <div class="stat-label">总资源大小</div>
          <div class="stat-trend positive">+2.5GB 本月增长</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon documents">📝</div>
        <div class="stat-content">
          <div class="stat-number">{{ resourceStats.typeDistribution.document }}</div>
          <div class="stat-label">文档数量</div>
          <div class="stat-trend positive">+8 本月新增</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon recent">👥</div>
        <div class="stat-content">
          <div class="stat-number">{{ resourceStats.recentUploads }}</div>
          <div class="stat-label">最近上传资源</div>
          <div class="stat-trend positive">+5 本周新增</div>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <button class="btn btn-primary" @click="openUploadModal">
          <span class="icon">📤</span>
          上传资源
        </button>
        <button class="btn btn-secondary" @click="fetchResourceStats">
          <span class="icon">📊</span>
          资源统计
        </button>
      </div>
      <div class="right-actions">
        <div class="search-box">
          <input type="text" placeholder="搜索资源..." v-model="searchQuery">
          <button class="search-btn" @click="searchResources">🔍</button>
        </div>
        <select v-model="selectedType" class="subject-filter">
          <option value="">全部类型</option>
          <option value="document">文档</option>
          <option value="video">视频</option>
          <option value="audio">音频</option>
          <option value="image">图片</option>
          <option value="other">其他</option>
        </select>
        <select v-model="selectedCategory" class="subject-filter">
          <option value="">全部分类</option>
          <option value="course">课程资源</option>
          <option value="practice">练习资源</option>
          <option value="exam">考试资源</option>
          <option value="reference">参考资料</option>
          <option value="other">其他</option>
        </select>
      </div>
    </div>

    <!-- 资源列表 -->
    <div class="resource-list">
      <div class="list-header">
        <div class="header-item">资源名称</div>
        <div class="header-item">类型</div>
        <div class="header-item">分类</div>
        <div class="header-item">大小</div>
        <div class="header-item">上传时间</div>
        <div class="header-item">上传教师</div>
        <div class="header-item">下载次数</div>
        <div class="header-item">操作</div>
      </div>

      <div class="resource-item" v-for="resource in paginatedResources" :key="resource.id">
        <div class="item-name">
          <div class="resource-icon">{{ getResourceIcon(resource.type) }}</div>
          <div class="resource-info">
            <div class="resource-title">{{ resource.name }}</div>
            <div class="resource-desc">{{ resource.description }}</div>
          </div>
        </div>
        <div class="item-type">
          <span class="type-badge" :class="resource.type">{{ getTypeLabel(resource.type) }}</span>
        </div>
        <div class="item-category">
          <span class="category-badge" :class="resource.category">{{ getCategoryLabel(resource.category) }}</span>
        </div>
        <div class="item-size">{{ formatFileSize(resource.size) }}</div>
        <div class="item-time">{{ resource.uploadTime }}</div>
        <div class="item-teacher">{{ resource.uploader }}</div>
        <div class="item-usage">{{ resource.downloadCount }}</div>
        <div class="item-actions">
          <button class="action-btn" title="预览" @click="previewResource(resource)">
            <span class="icon">👁️</span>
          </button>
          <button class="action-btn" title="下载" @click="downloadResourceFile(resource)">
            <span class="icon">⬇️</span>
          </button>
          <button class="action-btn" title="编辑" @click="openEditModal(resource)">
            <span class="icon">✏️</span>
          </button>
          <button class="action-btn danger" title="删除" @click="openDeleteModal(resource)">
            <span class="icon">🗑️</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">上一页</button>
      <div class="page-numbers">
        <button class="page-btn" :class="{ active: currentPage === 1 }">1</button>
        <button class="page-btn" :class="{ active: currentPage === 2 }">2</button>
        <button class="page-btn" :class="{ active: currentPage === 3 }">3</button>
        <span class="page-ellipsis">...</span>
        <button class="page-btn" :class="{ active: currentPage === totalPages }">{{ totalPages }}</button>
      </div>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="handlePageChange(currentPage + 1)">下一页</button>
    </div>

    <!-- 资源统计图表 -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>📊 资源类型分布</h3>
        <div class="chart-container">
          <canvas ref="typeChart"></canvas>
        </div>
      </div>
      <div class="chart-card">
        <h3>📈 资源分类分布</h3>
        <div class="chart-container">
          <canvas ref="categoryChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 上传模态框 -->
    <div v-if="isUploadModalOpen" class="modal" @click.self="closeUploadModal">
      <div class="modal-content">
        <span class="close" @click="closeUploadModal">&times;</span>
        <h2>上传资源</h2>
        <form @submit.prevent="uploadNewResource">
          <div class="form-group">
            <label for="name">资源名称</label>
            <input type="text" id="name" v-model="resourceForm.name" required>
          </div>
          <div class="form-group">
            <label for="description">描述</label>
            <textarea id="description" v-model="resourceForm.description" required></textarea>
          </div>
          <div class="form-group">
            <label for="type">类型</label>
            <select id="type" v-model="resourceForm.type" required>
              <option value="">选择类型</option>
              <option v-for="option in typeOptions" :value="option.value">{{ option.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="category">分类</label>
            <select id="category" v-model="resourceForm.category" required>
              <option value="">选择分类</option>
              <option v-for="option in categoryOptions" :value="option.value">{{ option.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="tags">标签</label>
            <input type="text" id="tags" v-model="resourceForm.tags" placeholder="多个标签用逗号分隔">
          </div>
          <div class="form-group">
            <label for="courseId">关联课程ID</label>
            <input type="number" id="courseId" v-model="resourceForm.courseId" placeholder="输入课程ID（默认为1）" min="1">
          </div>
          <div class="form-group">
            <label for="teacherId">上传教师ID</label>
            <input type="number" id="teacherId" v-model="resourceForm.teacherId" placeholder="输入教师ID（默认为1）" min="1">
          </div>
          <div class="form-group">
            <label for="file">选择文件</label>
            <input type="file" id="file" @change="handleFileChange" accept=".doc,.docx,.pdf,.txt,.md,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.mp4,.avi,.mp3,.wav">
            <div v-if="uploadFile" class="file-info">
              已选择: {{ uploadFile.name }} ({{ formatFileSize(uploadFile.size) }})
            </div>
          </div>
          <div class="form-group">
            <label for="isPublic">公开</label>
            <input type="checkbox" id="isPublic" v-model="resourceForm.isPublic">
          </div>
          <div v-if="isUploading" class="upload-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
            </div>
            <span>{{ uploadProgress }}%</span>
          </div>
          <button type="submit" :disabled="isUploading">上传</button>
        </form>
      </div>
    </div>

    <!-- 编辑模态框 -->
    <div v-if="isEditModalOpen" class="modal" @click.self="isEditModalOpen = false">
      <div class="modal-content">
        <span class="close" @click="isEditModalOpen = false">&times;</span>
        <h2>编辑资源</h2>
        <form @submit.prevent="updateExistingResource">
          <div class="form-group">
            <label for="name">资源名称</label>
            <input type="text" id="name" v-model="resourceForm.name" required>
          </div>
          <div class="form-group">
            <label for="description">描述</label>
            <textarea id="description" v-model="resourceForm.description" required></textarea>
          </div>
          <div class="form-group">
            <label for="type">类型</label>
            <select id="type" v-model="resourceForm.type" required>
              <option v-for="option in typeOptions" :value="option.value">{{ option.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="category">分类</label>
            <select id="category" v-model="resourceForm.category" required>
              <option v-for="option in categoryOptions" :value="option.value">{{ option.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="tags">标签</label>
            <input type="text" id="tags" v-model="resourceForm.tags" placeholder="多个标签用逗号分隔">
          </div>
          <div class="form-group">
            <label for="isPublic">公开</label>
            <input type="checkbox" id="isPublic" v-model="resourceForm.isPublic">
          </div>
          <button type="submit">更新</button>
        </form>
      </div>
    </div>

    <!-- 删除模态框 -->
    <div v-if="isDeleteModalOpen" class="modal" @click.self="isDeleteModalOpen = false">
      <div class="modal-content">
        <span class="close" @click="isDeleteModalOpen = false">&times;</span>
        <h2>删除资源</h2>
        <p>确定要删除该资源吗？</p>
        <button @click="deleteExistingResource">删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { getResources, uploadResource, deleteResource, updateResource, getResourceStats } from '@/api/stats'
import { getAllResources, uploadCourseResource, deleteCourseResource } from '@/api/courseResource'
import Chart from 'chart.js/auto'

// 响应式变量
const resources = ref([])
const filteredResources = ref([])
const selectedResource = ref(null)
const isUploadModalOpen = ref(false)
const isEditModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const isLoading = ref(false)
const searchQuery = ref('')
const selectedType = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalResources = ref(0)

// 表单数据
const resourceForm = ref({
  name: '',
  description: '',
  type: 'document',
  category: 'course',
  tags: [],
  isPublic: true,
  courseId: 1,
  teacherId: 1
})

// 文件上传
const uploadFile = ref(null)
const uploadProgress = ref(0)
const isUploading = ref(false)

// 图表引用
const typeChart = ref(null)
const categoryChart = ref(null)
let typeChartInstance = null
let categoryChartInstance = null

// 资源类型选项
const typeOptions = [
  { value: 'document', label: '文档' },
  { value: 'video', label: '视频' },
  { value: 'audio', label: '音频' },
  { value: 'image', label: '图片' },
  { value: 'other', label: '其他' }
]

// 资源分类选项
const categoryOptions = [
  { value: 'course', label: '课程资源' },
  { value: 'practice', label: '练习资源' },
  { value: 'exam', label: '考试资源' },
  { value: 'reference', label: '参考资料' },
  { value: 'other', label: '其他' }
]

// 资源统计
const resourceStats = ref({
  totalResources: 0,
  totalSize: '0MB',
  typeDistribution: {
    document: 0,
    video: 0,
    audio: 0,
    image: 0,
    other: 0
  },
  categoryDistribution: {
    course: 0,
    practice: 0,
    exam: 0,
    reference: 0,
    other: 0
  },
  recentUploads: 0
})

// 计算属性
const paginatedResources = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredResources.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredResources.value.length / pageSize.value)
})

// 监听搜索和筛选条件变化
watch([searchQuery, selectedType, selectedCategory], () => {
  filterResources()
  currentPage.value = 1
})

// 获取资源列表
const fetchResources = async () => {
  try {
    isLoading.value = true
    
    // 尝试从课程资源API获取数据
    let resourceData = []
    try {
      // 使用后端实际存在的接口 - 获取所有资源（管理员权限）
      const courseRes = await getAllResources()
      
      if (courseRes && courseRes.data) {
        const courseResources = Array.isArray(courseRes.data) ? courseRes.data : []
        resourceData = courseResources.map(resource => ({
          id: resource.id,
          name: resource.fileName || resource.title || '未命名资源',
          description: resource.description || resource.title || '',
          type: getFileType(resource.fileName || ''),
          category: 'course',
          size: formatFileSize(resource.fileSize || 0),
          uploadTime: resource.uploadTime ? resource.uploadTime.split(' ')[0] : '',
          uploader: resource.teacherName || '未知',
          downloadCount: resource.downloadCount || 0,
          filePath: resource.fileUrl || '',
          url: resource.fileUrl || '',
          tags: []
        }))
        
        // 应用前端筛选
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase()
          resourceData = resourceData.filter(resource => 
            resource.name.toLowerCase().includes(query) ||
            resource.description.toLowerCase().includes(query)
          )
        }
      }
    } catch (courseError) {
      console.warn('获取课程资源失败，尝试通用资源API:', courseError)
    }
    
    // 如果课程资源API失败，尝试通用资源API
    if (resourceData.length === 0) {
      try {
        const generalRes = await getResources({
          page: currentPage.value,
          size: pageSize.value * 3,
          type: selectedType.value,
          category: selectedCategory.value,
          keyword: searchQuery.value
        })
        
        if (generalRes && generalRes.data) {
          const generalResources = Array.isArray(generalRes.data) ? generalRes.data : generalRes.data.records || []
          resourceData = generalResources.map(resource => ({
            id: resource.id,
            name: resource.name || '未命名资源',
            description: resource.description || '',
            type: resource.type || 'document',
            category: resource.category || 'course',
            size: resource.size || '0KB',
            uploadTime: resource.uploadTime || resource.createTime || '',
            uploader: resource.uploader || resource.uploaderName || '未知',
            downloadCount: resource.downloadCount || 0,
            filePath: resource.filePath || '',
            url: resource.url || '',
            tags: resource.tags || []
          }))
        }
      } catch (generalError) {
        console.warn('获取通用资源失败，使用默认数据:', generalError)
      }
    }
    
    // 如果所有API都失败了，记录错误但不使用模拟数据
    if (resourceData.length === 0) {
      console.warn('所有资源API调用失败，显示空列表')
      resourceData = []
    }
    
    resources.value = resourceData
    totalResources.value = resourceData.length
    filterResources()
    
    console.log('资源列表加载完成:', resourceData.length, '个资源')
    
  } catch (error) {
    console.error('获取资源列表失败:', error)
    resources.value = []
    totalResources.value = 0
  } finally {
    isLoading.value = false
  }
}

// 筛选资源
const filterResources = () => {
  let filtered = [...resources.value]
  
  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(resource => 
      resource.name.toLowerCase().includes(query) ||
      resource.description.toLowerCase().includes(query) ||
      resource.uploader.toLowerCase().includes(query) ||
      (resource.tags && resource.tags.some(tag => tag.toLowerCase().includes(query)))
    )
  }
  
  // 类型筛选
  if (selectedType.value) {
    filtered = filtered.filter(resource => resource.type === selectedType.value)
  }
  
  // 分类筛选
  if (selectedCategory.value) {
    filtered = filtered.filter(resource => resource.category === selectedCategory.value)
  }
  
  filteredResources.value = filtered
}

// 获取文件类型
const getFileType = (fileName) => {
  if (!fileName) return 'other'
  
  const ext = fileName.split('.').pop()?.toLowerCase()
  if (['doc', 'docx', 'pdf', 'txt', 'ppt', 'pptx'].includes(ext)) return 'document'
  if (['mp4', 'avi', 'mov', 'wmv', 'flv'].includes(ext)) return 'video'
  if (['mp3', 'wav', 'aac', 'flac'].includes(ext)) return 'audio'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg'].includes(ext)) return 'image'
  return 'other'
}

// 获取资源统计
const fetchResourceStats = async () => {
  try {
    const res = await getResourceStats()
    if (res && res.data) {
      resourceStats.value = {
        totalResources: res.data.totalResources || resources.value.length,
        totalSize: res.data.totalSize || calculateTotalSize(),
        typeDistribution: res.data.typeDistribution || calculateTypeDistribution(),
        categoryDistribution: res.data.categoryDistribution || calculateCategoryDistribution(),
        recentUploads: res.data.recentUploads || calculateRecentUploads()
      }
      console.log('资源统计:', resourceStats.value)
    } else {
      // 基于当前数据计算统计
      resourceStats.value = {
        totalResources: resources.value.length,
        totalSize: calculateTotalSize(),
        typeDistribution: calculateTypeDistribution(),
        categoryDistribution: calculateCategoryDistribution(),
        recentUploads: calculateRecentUploads()
      }
    }
    
    // 重新创建图表
    setTimeout(() => {
      createCharts()
    }, 100)
    
  } catch (error) {
    console.error('获取资源统计失败:', error)
    // 基于当前数据计算统计
    resourceStats.value = {
      totalResources: resources.value.length,
      totalSize: calculateTotalSize(),
      typeDistribution: calculateTypeDistribution(),
      categoryDistribution: calculateCategoryDistribution(),
      recentUploads: calculateRecentUploads()
    }
    
    // 即使出错也创建图表
    setTimeout(() => {
      createCharts()
    }, 100)
  }
}

// 计算总大小
const calculateTotalSize = () => {
  let totalBytes = 0
  resources.value.forEach(resource => {
    const sizeStr = resource.size || '0KB'
    const size = parseFloat(sizeStr)
    if (sizeStr.includes('GB')) totalBytes += size * 1024 * 1024 * 1024
    else if (sizeStr.includes('MB')) totalBytes += size * 1024 * 1024
    else if (sizeStr.includes('KB')) totalBytes += size * 1024
  })
  return formatFileSize(totalBytes)
}

// 计算类型分布
const calculateTypeDistribution = () => {
  const distribution = { document: 0, video: 0, audio: 0, image: 0, other: 0 }
  resources.value.forEach(resource => {
    distribution[resource.type] = (distribution[resource.type] || 0) + 1
  })
  return distribution
}

// 计算分类分布
const calculateCategoryDistribution = () => {
  const distribution = { course: 0, practice: 0, exam: 0, reference: 0, other: 0 }
  resources.value.forEach(resource => {
    distribution[resource.category] = (distribution[resource.category] || 0) + 1
  })
  return distribution
}

// 计算最近上传
const calculateRecentUploads = () => {
  const oneWeekAgo = new Date()
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7)
  
  return resources.value.filter(resource => {
    const uploadDate = new Date(resource.uploadTime)
    return uploadDate >= oneWeekAgo
  }).length
}

// 上传资源
const uploadNewResource = async () => {
  if (!uploadFile.value) {
    alert('请选择要上传的文件')
    return
  }
  
  try {
    isUploading.value = true
    uploadProgress.value = 0
    
    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += Math.random() * 10
      }
    }, 200)
    
    let uploadSuccess = false
    let uploadResult = null
    
    try {
      // 使用课程资源上传API（使用表单中的参数）
      const formData = new FormData()
      formData.append('file', uploadFile.value)
      formData.append('courseId', String(resourceForm.value.courseId || 1))
      formData.append('teacherId', String(resourceForm.value.teacherId || 1))
      formData.append('title', resourceForm.value.name)
      formData.append('description', resourceForm.value.description || '')
      
      const res = await uploadCourseResource(formData)
      if (res && res.code === 200) {
        uploadSuccess = true
        uploadResult = res.data
        console.log('课程资源上传成功:', res.data)
      }
    } catch (courseError) {
      console.error('课程资源上传失败:', courseError)
      
      // 如果课程资源上传失败，显示错误但不继续尝试其他API
      // 因为其他API的参数格式不兼容
      throw new Error('资源上传失败: ' + (courseError.response?.data?.msg || courseError.message || '未知错误'))
    }
    
    clearInterval(progressInterval)
    uploadProgress.value = 100
    
    if (uploadSuccess && uploadResult) {
      alert('资源上传成功！')
      
      // 添加新上传的资源到列表
      const newResource = {
        id: uploadResult.id,
        name: uploadResult.title || resourceForm.value.name,
        description: uploadResult.description || resourceForm.value.description,
        type: getFileType(uploadResult.fileName) || resourceForm.value.type,
        category: resourceForm.value.category,
        size: formatFileSize(uploadResult.fileSize || uploadFile.value.size),
        uploadTime: uploadResult.createTime ? uploadResult.createTime.split(' ')[0] : new Date().toISOString().split('T')[0],
        uploader: '当前用户',
        downloadCount: uploadResult.downloadCount || 0,
        filePath: uploadResult.fileUrl,
        url: uploadResult.fileUrl,
        tags: [],
        fileName: uploadResult.fileName
      }
      
      resources.value.unshift(newResource)
      totalResources.value += 1
      filterResources()
      await fetchResourceStats()
    } else {
      throw new Error('上传失败，未收到有效响应')
    }
    
    resetResourceForm()
    isUploadModalOpen.value = false
    
  } catch (error) {
    console.error('上传资源失败:', error)
    alert('上传失败: ' + (error.message || '未知错误'))
  } finally {
    isUploading.value = false
    uploadProgress.value = 0
  }
}

// 更新资源
const updateExistingResource = async () => {
  try {
    isLoading.value = true
    const res = await updateResource({
      id: selectedResource.value.id,
      ...resourceForm.value
    })
    if (res && res.data) {
      console.log('资源更新成功:', res.data)
      isEditModalOpen.value = false
      resetResourceForm()
      await fetchResources()
    }
  } catch (error) {
    console.error('更新资源失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 删除资源
const deleteExistingResource = async () => {
  try {
    isLoading.value = true
    const res = await deleteResource(selectedResource.value.id)
    if (res && res.data) {
      console.log('资源删除成功:', res.data)
      alert('资源删除成功！')
      isDeleteModalOpen.value = false
      selectedResource.value = null
      await fetchResources()
      await fetchResourceStats()
    } else {
      // 如果API调用失败，从本地列表中删除
      const index = resources.value.findIndex(r => r.id === selectedResource.value.id)
      if (index > -1) {
        resources.value.splice(index, 1)
        filterResources()
        await fetchResourceStats()
        alert('资源删除成功！（演示模式）')
      }
      isDeleteModalOpen.value = false
      selectedResource.value = null
    }
  } catch (error) {
    console.error('删除资源失败:', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  } finally {
    isLoading.value = false
  }
}

// 重置资源表单
const resetResourceForm = () => {
  resourceForm.value = {
    name: '',
    description: '',
    type: 'document',
    category: 'course',
    tags: [],
    isPublic: true,
    courseId: 1,
    teacherId: 1
  }
  uploadFile.value = null
}

// 打开上传模态框
const openUploadModal = () => {
  resetResourceForm()
  isUploadModalOpen.value = true
}

// 关闭上传模态框
const closeUploadModal = () => {
  isUploadModalOpen.value = false
  resetResourceForm()
}

// 打开编辑模态框
const openEditModal = (resource) => {
  selectedResource.value = resource
  resourceForm.value = {
    name: resource.name,
    description: resource.description,
    type: resource.type,
    category: resource.category,
    tags: resource.tags || [],
    isPublic: resource.isPublic !== false
  }
  isEditModalOpen.value = true
}

// 打开删除模态框
const openDeleteModal = (resource) => {
  selectedResource.value = resource
  isDeleteModalOpen.value = true
}

// 处理文件选择
const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    uploadFile.value = file
  }
}

// 搜索资源
const searchResources = () => {
  console.log('搜索资源:', searchQuery.value)
  fetchResources()
}

// 应用筛选条件
const applyFilters = () => {
  console.log('筛选资源:', { type: selectedType.value, category: selectedCategory.value })
  fetchResources()
}

// 重置筛选
const resetFilters = () => {
  searchQuery.value = ''
  selectedType.value = ''
  selectedCategory.value = ''
  fetchResources()
}

// 获取资源图标
const getResourceIcon = (type) => {
  const icons = {
    'document': '📄',
    'video': '🎥',
    'audio': '🎵',
    'image': '🖼️',
    'pdf': '📕',
    'excel': '📊',
    'powerpoint': '📊',
    'word': '📝',
    'other': '📁'
  }
  return icons[type] || icons['other']
}

// 获取类型标签
const getTypeLabel = (type) => {
  const labels = {
    'document': '文档',
    'video': '视频',
    'audio': '音频',
    'image': '图片',
    'pdf': 'PDF',
    'excel': 'Excel',
    'powerpoint': 'PPT',
    'word': 'Word',
    'other': '其他'
  }
  return labels[type] || labels['other']
}

// 获取分类标签
const getCategoryLabel = (category) => {
  const labels = {
    'course': '课程资源',
    'practice': '练习资源',
    'exam': '考试资源',
    'reference': '参考资料',
    'other': '其他'
  }
  return labels[category] || labels['other']
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (typeof bytes === 'string') {
    return bytes
  }
  
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理分页变化
const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// 预览资源
const previewResource = (resource) => {
  console.log('预览资源:', resource)
  if (resource.url) {
    // 在新窗口中打开资源
    window.open(resource.url, '_blank')
  } else {
    alert('暂无预览链接')
  }
}

// 下载资源
const downloadResourceFile = async (resource) => {
  try {
    console.log('下载资源:', resource)
    
    if (resource.url) {
      // 创建下载链接
      const link = document.createElement('a')
      link.href = resource.url
      link.download = resource.name
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      // 更新下载次数
      resource.downloadCount = (resource.downloadCount || 0) + 1
      
      alert('开始下载资源')
    } else {
      alert('暂无下载链接')
    }
  } catch (error) {
    console.error('下载资源失败:', error)
    alert('下载失败: ' + (error.message || '未知错误'))
  }
}

// 创建图表
const createCharts = () => {
  // 销毁旧图表实例
  if (typeChartInstance) {
    typeChartInstance.destroy()
    typeChartInstance = null
  }
  if (categoryChartInstance) {
    categoryChartInstance.destroy()
    categoryChartInstance = null
  }
  
  // 类型分布饼图
  const typeCtx = typeChart.value?.getContext('2d')
  if (typeCtx && resourceStats.value.typeDistribution) {
    const typeData = resourceStats.value.typeDistribution
    const typeLabels = []
    const typeValues = []
    const typeColors = []
    
    const colorMap = {
      document: '#FF6384',
      video: '#36A2EB', 
      audio: '#FFCE56',
      image: '#4BC0C0',
      other: '#9966FF'
    }
    
    Object.keys(typeData).forEach(key => {
      if (typeData[key] > 0) {
        typeLabels.push(getTypeLabel(key))
        typeValues.push(typeData[key])
        typeColors.push(colorMap[key] || '#CCCCCC')
      }
    })
    
    if (typeLabels.length > 0) {
      typeChartInstance = new Chart(typeCtx, {
        type: 'pie',
        data: {
          labels: typeLabels,
          datasets: [{
            data: typeValues,
            backgroundColor: typeColors,
            borderWidth: 2,
            borderColor: '#fff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                padding: 15,
                usePointStyle: true,
                font: {
                  size: 12
                }
              }
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const total = context.dataset.data.reduce((a, b) => a + b, 0)
                  const percentage = ((context.raw / total) * 100).toFixed(1)
                  return `${context.label}: ${context.raw} (${percentage}%)`
                }
              }
            }
          }
        }
      })
    } else {
      // 如果没有数据，显示提示信息
      typeCtx.clearRect(0, 0, typeCtx.canvas.width, typeCtx.canvas.height)
      typeCtx.font = '16px Arial'
      typeCtx.fillStyle = '#999'
      typeCtx.textAlign = 'center'
      typeCtx.fillText('暂无数据', typeCtx.canvas.width / 2, typeCtx.canvas.height / 2)
    }
  }
  
  // 分类分布柱状图
  const categoryCtx = categoryChart.value?.getContext('2d')
  if (categoryCtx && resourceStats.value.categoryDistribution) {
    const categoryData = resourceStats.value.categoryDistribution
    const categoryLabels = []
    const categoryValues = []
    
    Object.keys(categoryData).forEach(key => {
      categoryLabels.push(getCategoryLabel(key))
      categoryValues.push(categoryData[key])
    })
    
    if (categoryValues.some(val => val > 0)) {
      categoryChartInstance = new Chart(categoryCtx, {
        type: 'bar',
        data: {
          labels: categoryLabels,
          datasets: [{
            label: '资源数量',
            data: categoryValues,
            backgroundColor: 'rgba(54, 162, 235, 0.6)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1,
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  return `${context.label}: ${context.raw} 个资源`
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1,
                callback: function(value) {
                  return value + ' 个'
                }
              },
              grid: {
                color: 'rgba(0, 0, 0, 0.1)'
              }
            },
            x: {
              grid: {
                display: false
              }
            }
          }
        }
      })
    } else {
      // 如果没有数据，显示提示信息
      categoryCtx.clearRect(0, 0, categoryCtx.canvas.width, categoryCtx.canvas.height)
      categoryCtx.font = '16px Arial'
      categoryCtx.fillStyle = '#999'
      categoryCtx.textAlign = 'center'
      categoryCtx.fillText('暂无数据', categoryCtx.canvas.width / 2, categoryCtx.canvas.height / 2)
    }
  }
}

// 页面加载时获取资源
onMounted(async () => {
  await fetchResources()
  await fetchResourceStats()
  
  // 等待DOM更新后创建图表
  setTimeout(() => {
    createCharts()
  }, 100)
})
</script>

<style scoped>
.resource-management {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
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
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      h1 {
        margin: 0;
        font-size: 2rem;
        font-weight: 700;
      }
      
      .header-badge {
        display: flex;
        align-items: center;
        gap: 6px;
        background: rgba(255, 255, 255, 0.2);
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.9rem;
        
        .badge-icon {
          font-size: 1rem;
        }
        
        .badge-text {
          font-weight: 500;
        }
      }
    }
    
    .header-subtitle {
      margin: 0;
      font-size: 1.1rem;
      opacity: 0.9;
      font-weight: 400;
    }
  }
  
  .header-actions {
    position: absolute;
    top: 32px;
    right: 32px;
    z-index: 2;
    display: flex;
    gap: 12px;
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      border: none;
      border-radius: 8px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &.primary {
        background: rgba(255, 255, 255, 0.2);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }
      }
      
      &.secondary {
        background: rgba(255, 255, 255, 0.1);
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.2);
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
      }
      
      .btn-icon {
        font-size: 1rem;
      }
    }
  }
}

/* 统计概览样式 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
  
  .stat-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    border: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.8rem;
      
      &.total {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      
      &.size {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
      
      &.documents {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
      
      &.recent {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }
    
    .stat-content {
      flex: 1;
      
      .stat-number {
        font-size: 2rem;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 0.9rem;
        color: #64748b;
        margin-bottom: 4px;
      }
      
      .stat-trend {
        font-size: 0.8rem;
        font-weight: 500;
        
        &.positive {
          color: #10b981;
        }
        
        &.negative {
          color: #ef4444;
        }
      }
    }
  }
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.left-actions, .right-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

.btn-secondary:hover {
  background: #e9ecef;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box input {
  padding: 10px 40px 10px 16px;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  font-size: 14px;
  width: 250px;
}

.search-btn {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
}

.subject-filter {
  padding: 10px 16px;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  font-size: 14px;
  background: white;
}

/* 资源列表样式 */
.resource-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.list-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 1fr;
  padding: 16px 24px;
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #dee2e6;
}

.resource-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 1fr;
  padding: 16px 24px;
  border-bottom: 1px solid #f1f3f4;
  align-items: center;
}

.resource-item:hover {
  background: #f8f9fa;
}

.item-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resource-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
}

.resource-title {
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.resource-desc {
  font-size: 12px;
  color: #666;
}

.subject-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.subject-tag.math { background: #e3f2fd; color: #1976d2; }
.subject-tag.physics { background: #f3e5f5; color: #7b1fa2; }
.subject-tag.chemistry { background: #e8f5e8; color: #388e3c; }
.subject-tag.biology { background: #fff3e0; color: #f57c00; }
.subject-tag.computer { background: #fce4ec; color: #c2185b; }

.type-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.document { background: #e8f5e8; color: #388e3c; }
.type-badge.video { background: #e3f2fd; color: #1976d2; }
.type-badge.audio { background: #fff3e0; color: #f57c00; }
.type-badge.image { background: #fce4ec; color: #c2185b; }
.type-badge.pdf { background: #ffebee; color: #d32f2f; }
.type-badge.excel { background: #e0f2f1; color: #00695c; }
.type-badge.powerpoint { background: #f3e5f5; color: #7b1fa2; }
.type-badge.word { background: #e1f5fe; color: #0277bd; }
.type-badge.other { background: #f5f5f5; color: #616161; }

.category-badge.course { background: #e8f5e8; color: #388e3c; }
.category-badge.practice { background: #fff3e0; color: #f57c00; }
.category-badge.exam { background: #e3f2fd; color: #1976d2; }
.category-badge.reference { background: #fce4ec; color: #c2185b; }
.category-badge.other { background: #f5f5f5; color: #616161; }

.item-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background: #f8f9fa;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e9ecef;
}

.action-btn.danger:hover {
  background: #f8d7da;
  color: #721c24;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 32px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
}

.page-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-ellipsis {
  color: #666;
  padding: 0 8px;
}

/* 图表区域样式 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin: 0 0 20px 0;
  color: #1a1a1a;
  font-size: 18px;
  font-weight: 600;
}

.chart-container {
  height: 300px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
  
  canvas {
    max-width: 100%;
    max-height: 100%;
  }
}

/* 上传进度样式 */
.upload-progress {
  margin-top: 16px;
  
  .progress-bar {
    width: 100%;
    height: 8px;
    background: #e2e8f0;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 8px;
    
    .progress-fill {
      height: 100%;
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      border-radius: 4px;
      transition: width 0.3s ease;
    }
  }
  
  span {
    font-size: 14px;
    color: #4a5568;
  }
}

.file-info {
  margin-top: 8px;
  padding: 8px;
  background: #f7fafc;
  border-radius: 4px;
  font-size: 14px;
  color: #4a5568;
}

/* 模态框样式 */
.modal {
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background-color: #fefefe;
  padding: 30px;
  border: none;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
  position: absolute;
  right: 20px;
  top: 15px;
  cursor: pointer;
  z-index: 1001;
}

.close:hover,
.close:focus {
  color: #000;
  text-decoration: none;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

button[type="submit"] {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

button[type="submit"]:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

button[type="submit"]:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header .header-actions {
    position: static;
    margin-top: 16px;
    flex-wrap: wrap;
  }
  
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
}
</style> 