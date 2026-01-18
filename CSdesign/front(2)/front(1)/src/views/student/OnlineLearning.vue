<template>
  <div class="online-learning">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <img src="@/assets/category.png" alt="在线学习" class="header-icon">
          <h2>在线学习</h2>
        </div>
        <p class="header-subtitle">选择您感兴趣的课程开始学习</p>
      </div>
      <div class="header-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="search-box">
        <img src="@/assets/search.png" alt="搜索" class="search-icon">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索课程..."
          @input="filterCourses"
          class="search-input"
        >
      </div>
      
      <div class="filter-options">
        <div class="filter-group">
          <img src="@/assets/tag.png" alt="专业" class="filter-icon">
          <select v-model="selectedSubject" @change="filterCourses" class="filter-select">
            <option value="">所有专业方向</option>
            <option value="computer">计算机科学与技术</option>
            <option value="software">软件工程</option>
            <option value="network">网络工程</option>
            <option value="ai">人工智能</option>
            <option value="data">数据科学</option>
            <option value="math">数学</option>
            <option value="physics">物理学</option>
            <option value="chemistry">化学</option>
            <option value="biology">生物学</option>
            <option value="economics">经济学</option>
            <option value="management">管理学</option>
            <option value="other">其他专业</option>
          </select>
        </div>
        
        <div class="filter-group">
          <img src="@/assets/balance.png" alt="难度" class="filter-icon">
          <select v-model="selectedLevel" @change="filterCourses" class="filter-select">
            <option value="">所有难度</option>
            <option value="beginner">初级</option>
            <option value="intermediate">中级</option>
            <option value="advanced">高级</option>
          </select>
        </div>
        
        <div class="filter-group">
          <img src="@/assets/time.png" alt="排序" class="filter-icon">
          <select v-model="sortBy" @change="filterCourses" class="filter-select">
            <option value="popular">最受欢迎</option>
            <option value="newest">最新发布</option>
            <option value="rating">评分最高</option>
          </select>
        </div>
      </div>
    </div>
    
    <!-- 已选课程列表 -->
    <div v-if="loading" class="loading-section">
      <div class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <p class="loading-text">正在加载您的课程...</p>
    </div>
    
    <div v-else-if="enrolledCourses.length === 0" class="empty-section">
      <div class="empty-illustration">
        <div class="empty-circle"></div>
        <img src="@/assets/category.png" alt="暂无课程" class="empty-icon">
      </div>
      <h3 class="empty-title">暂无课程</h3>
      <p class="empty-description">您还没有选择任何课程</p>
      <p class="empty-suggestion">请联系教师或管理员为您分配课程</p>
      <div class="empty-actions">
        <button class="contact-btn" @click="contactTeacher">
          <img src="@/assets/author.png" alt="联系" class="btn-icon">
          联系教师
        </button>
      </div>
    </div>
    
    <div v-else class="courses-grid">
      <div 
        v-for="course in filteredCourses" 
        :key="course.id"
        class="course-card"
        @click="selectCourse(course)"
      >
        <!-- 课程封面 -->
        <div class="course-image">
          <div class="course-overlay"></div>
          <div class="course-badge">
            <span class="difficulty-badge" :class="getDifficultyClass(course.difficulty)">
              {{ course.difficulty || '中级' }}
            </span>
            <span class="rating-badge">
              <span class="rating-star">⭐</span>
              {{ course.rating || '4.8' }}
            </span>
          </div>
          <div class="course-category">{{ course.subject }}</div>
        </div>
        
        <!-- 课程内容 -->
        <div class="course-content">
          <div class="course-header">
            <h3 class="course-title">{{ course.title }}</h3>
            <div class="course-status" :class="getStatusClass(course.status)">
              {{ getStatusText(course.status) }}
            </div>
          </div>
          
          <p class="course-description">{{ course.description }}</p>
          
          <div class="course-meta">
            <div class="meta-row">
              <div class="meta-item">
                <img src="@/assets/author.png" alt="教师" class="meta-icon">
                <span>{{ course.teacherName || '未知教师' }}</span>
              </div>
              <div class="meta-item">
                <img src="@/assets/time.png" alt="时长" class="meta-icon">
                <span>{{ course.duration || '12周' }}</span>
              </div>
            </div>
            <div class="meta-row">
              <div class="meta-item">
                <img src="@/assets/category.png" alt="资源" class="meta-icon">
                <span>{{ getCourseResourceCount(course.id) }} 个资源</span>
              </div>
              <div class="meta-item">
                <img src="@/assets/QQ.png" alt="学生" class="meta-icon">
                <span>{{ course.studentsCount || '0' }} 人学习</span>
              </div>
            </div>
          </div>
          
          <!-- 学习进度 -->
          <div class="course-progress" v-if="course.progress !== undefined">
            <div class="progress-info">
              <span class="progress-label">学习进度</span>
              <span class="progress-value">{{ course.progress }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ 
                width: course.progress + '%',
                background: getProgressColor(course.progress)
              }"></div>
            </div>
            <div class="progress-text">
              {{ getProgressText(course.progress) }}
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="course-actions">
            <button @click.stop="viewCourseResources(course)" class="action-btn primary">
              <img src="@/assets/category.png" alt="查看资源" class="btn-icon">
              <span>学习资源</span>
            </button>
            <button @click.stop="viewCourseExams(course)" class="action-btn secondary">
              <img src="@/assets/edit.png" alt="考试练习" class="btn-icon">
              <span>练习测试</span>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 课程资源查看弹窗 -->
    <div v-if="showResourceModal" class="resource-modal" @click="closeResourceModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <div class="modal-title">
            <img src="@/assets/category.png" alt="课程资源" class="modal-icon">
            <h3>{{ selectedCourse?.title }} - 课程资源</h3>
          </div>
          <button @click="closeResourceModal" class="close-btn">
            <img src="@/assets/delete.png" alt="关闭" class="close-icon">
          </button>
        </div>
        
        <div class="modal-body">
          <div v-if="loadingResources" class="loading">
            <p>加载资源中...</p>
          </div>
          
          <div v-else-if="courseResources.length === 0" class="empty-resources">
            <img src="@/assets/category.png" alt="暂无资源" class="empty-icon">
            <p>该课程暂无资源</p>
          </div>
          
          <div v-else class="resources-list">
            <div 
              v-for="resource in courseResources" 
              :key="resource.id"
              class="resource-item"
            >
              <div class="resource-icon">
                <span class="icon-text">{{ getResourceIcon(resource.fileType) }}</span>
              </div>
              <div class="resource-info">
                <h4 class="resource-title">{{ resource.title }}</h4>
                <p class="resource-filename">{{ resource.fileName }}</p>
                <p class="resource-description" v-if="resource.description">
                  {{ resource.description }}
                </p>
                <div class="resource-meta">
                  <span>{{ formatFileSize(resource.fileSize) }}</span>
                  <span>·</span>
                  <span>下载 {{ resource.downloadCount }} 次</span>
                </div>
              </div>
              <div class="resource-actions">
                <button @click="previewResource(resource)" class="preview-btn">
                  <span class="btn-icon">👁️</span>
                  预览标注
                </button>
                <button @click="downloadCourseResource(resource)" class="download-btn">
                  <span class="btn-icon">⬇️</span>
                  下载
                </button>
              </div>
            </div>
          </div>
          
          <!-- 笔记功能按钮 -->
          <div class="notes-section" v-if="courseResources.length > 0">
            <div class="notes-header">
              <h4><i class="fas fa-sticky-note"></i> 学习工具</h4>
              <p class="notes-description">记录学习心得，标注重点内容，提升学习效果</p>
            </div>
            <div class="notes-actions">
              <button @click="openStudyNotes" class="notes-btn">
                <i class="fas fa-edit"></i>
                <span>学习笔记与标注</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习笔记模态框 -->
    <div v-if="showNotesModal" class="notes-modal-overlay" @click.self="closeNotesModal">
      <div class="notes-modal">
        <div class="notes-modal-header">
          <h3><i class="fas fa-sticky-note"></i> 学习笔记与标注</h3>
          <button @click="closeNotesModal" class="modal-close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="notes-modal-body">
          <StudyNotes 
            v-if="selectedCourse && courseResources.length > 0"
            :resource-id="courseResources[0].id"
            :resource-title="selectedCourse.title"
          />
        </div>
      </div>
    </div>

    <!-- 文档预览与标注模态框 -->
    <div v-if="showDocumentViewer" class="document-viewer-modal" @click.self="closeDocumentViewer">
      <div class="document-viewer-container">
        <div class="document-viewer-header">
          <div class="viewer-title">
            <i class="fas fa-file-alt"></i>
            <span>{{ selectedResource?.title }} - 在线预览与标注</span>
          </div>
          <button @click="closeDocumentViewer" class="viewer-close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="document-viewer-body">
          <DocumentViewer
            v-if="selectedResource && selectedCourse"
            :resourceId="selectedResource.id"
            :resourceTitle="selectedResource.title"
            :fileName="selectedResource.fileName"
            :courseId="selectedCourse.id"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourseResources } from '@/api/courseResource'
import { getCoursesByStudentId } from '@/api/course'
import { getCourseProgress } from '@/api/progress'
import StudyNotes from './StudyNotes.vue'
import DocumentViewer from './DocumentViewer.vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const loadingResources = ref(false)
const showResourceModal = ref(false)
const showNotesModal = ref(false)
const showDocumentViewer = ref(false)
const selectedResource = ref(null)
const searchQuery = ref('')
const selectedSubject = ref('')
const selectedLevel = ref('')
const sortBy = ref('popular')
const selectedCourse = ref(null)

// 数据
const enrolledCourses = ref([])
const courseResources = ref([])
const resourceCountMap = ref({})

// 获取当前登录学生ID
const studentId = ref(localStorage.getItem('userId') || 17)

// 计算属性
const filteredCourses = computed(() => {
  return enrolledCourses.value.filter(course => {
    const matchesSearch = !searchQuery.value || 
      course.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      course.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesSubject = !selectedSubject.value || course.subject === selectedSubject.value
    
    return matchesSearch && matchesSubject
  })
})

// 获取课程资源数量
const getCourseResourceCount = (courseId) => {
  return resourceCountMap.value[courseId] || 0
}

// 查看课程资源
const viewCourseResources = async (course) => {
  selectedCourse.value = course
  showResourceModal.value = true
  
  try {
    loadingResources.value = true
    const response = await getCourseResources(course.id)
    console.log('获取课程资源响应:', response)
    
    if (response && response.success && response.data) {
      courseResources.value = response.data
      console.log('课程资源列表:', courseResources.value)
    } else {
      courseResources.value = []
      console.log('未获取到课程资源数据')
    }
  } catch (error) {
    console.error('获取课程资源失败:', error)
    courseResources.value = []
  } finally {
    loadingResources.value = false
  }
}

// 查看课程考试
const viewCourseExams = (course) => {
  console.log('准备查看课程考试:', course)
  // 跳转到练习评估页面，传递课程信息
  router.push({
    path: '/student/practice-evaluation',
    query: {
      courseId: course.id,
      courseName: course.title,
      teacherName: course.teacherName
    }
  })
}

// 关闭资源模态框
const closeResourceModal = () => {
  showResourceModal.value = false
  selectedCourse.value = null
  courseResources.value = []
}

// 打开学习笔记
const openStudyNotes = () => {
  showNotesModal.value = true
}

// 关闭学习笔记模态框
const closeNotesModal = () => {
  showNotesModal.value = false
}

// 预览资源（文档标注功能）
const previewResource = (resource) => {
  selectedResource.value = resource
  showDocumentViewer.value = true
  console.log('开始预览资源:', resource)
}

// 关闭文档预览
const closeDocumentViewer = () => {
  showDocumentViewer.value = false
  selectedResource.value = null
}

// 下载课程资源
const downloadCourseResource = async (resource) => {
  try {
    console.log('开始下载资源:', resource)
    
    // 记录下载次数
    try {
      await axios.post(`/api/course-resources/${resource.id}/download`)
      console.log('下载次数记录成功')
    } catch (error) {
      console.warn('记录下载次数失败:', error)
    }
    
    // 构建下载链接
    let downloadUrl = resource.fileUrl
    
    // 如果是相对路径，添加服务器地址
    if (downloadUrl && !downloadUrl.startsWith('http')) {
      downloadUrl = `http://localhost:8080${downloadUrl}`
    }
    
    console.log('下载链接:', downloadUrl)

    // 使用fetch下载文件
    const response = await fetch(downloadUrl)
    if (!response.ok) {
      throw new Error('下载失败')
    }
    
    // 获取文件blob
    const blob = await response.blob()
    
    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = resource.fileName || '下载文件'
    link.style.display = 'none'
    
    // 添加到页面并触发下载
    document.body.appendChild(link)
    link.click()
    
    // 清理
    setTimeout(() => {
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }, 100)

    // 更新本地显示的下载次数
    resource.downloadCount = (resource.downloadCount || 0) + 1
    
    // 显示成功消息
    ElMessage.success(`开始下载: ${resource.fileName}`)
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败，请稍后重试')
  }
}

// 获取资源图标
const getResourceIcon = (fileType) => {
  const iconMap = {
    'ppt': '📊',
    'pptx': '📊',
    'pdf': '📄',
    'doc': '📝',
    'docx': '📝',
    'video': '🎥',
    'image': '🖼️',
    'zip': '📦',
    'rar': '📦',
    'mp3': '🎵',
    'wav': '🎵',
    'mp4': '🎥',
    'avi': '🎥',
    'mov': '🎥',
    'jpg': '🖼️',
    'jpeg': '🖼️',
    'png': '🖼️',
    'gif': '🖼️'
  }
  return iconMap[fileType?.toLowerCase()] || '📄'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取难度等级样式
const getDifficultyClass = (difficulty) => {
  const difficultyMap = {
    '初级': 'difficulty-beginner',
    '中级': 'difficulty-intermediate', 
    '高级': 'difficulty-advanced',
    'beginner': 'difficulty-beginner',
    'intermediate': 'difficulty-intermediate',
    'advanced': 'difficulty-advanced'
  }
  return difficultyMap[difficulty] || 'difficulty-intermediate'
}

// 获取课程状态样式
const getStatusClass = (status) => {
  const statusMap = {
    'active': 'status-active',
    'completed': 'status-completed',
    'paused': 'status-paused',
    'draft': 'status-draft'
  }
  return statusMap[status] || 'status-active'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'active': '进行中',
    'completed': '已完成',
    'paused': '已暂停',
    'draft': '草稿'
  }
  return statusMap[status] || '进行中'
}

// 获取进度颜色
const getProgressColor = (progress) => {
  if (progress >= 80) return 'linear-gradient(90deg, #10b981, #34d399)'
  if (progress >= 60) return 'linear-gradient(90deg, #3b82f6, #60a5fa)'
  if (progress >= 40) return 'linear-gradient(90deg, #f59e0b, #fbbf24)'
  return 'linear-gradient(90deg, #ef4444, #f87171)'
}

// 获取进度文本
const getProgressText = (progress) => {
  if (progress >= 90) return '即将完成！'
  if (progress >= 70) return '进展良好'
  if (progress >= 50) return '持续学习中'
  if (progress >= 30) return '继续加油'
  return '刚刚开始'
}

// 筛选课程
const filterCourses = () => {
  // 这个方法已经通过computed自动执行，保留空方法以兼容模板调用
}

// 选择课程
const selectCourse = (course) => {
  // 可以添加课程详情页面跳转逻辑
  console.log('选择课程:', course)
}

// 联系教师
const contactTeacher = () => {
  // 可以添加联系教师的逻辑，比如跳转到消息页面或打开联系方式弹窗
  alert('联系教师功能正在开发中...')
}

// 加载学生的课程列表
const loadEnrolledCourses = async () => {
  loading.value = true
  try {
    const response = await getCoursesByStudentId(studentId.value)
    console.log('获取学生课程响应:', response)
    
    if (response && response.success && response.data) {
      enrolledCourses.value = response.data.map(course => ({
        ...course,
        progress: course.progress || 0,
        difficulty: course.difficulty || '中级',
        studentsCount: course.studentsCount || 0,
        rating: course.rating || 4.5,
        duration: course.duration || '12周',
        status: course.status || 'active'
      }))
      
      // 加载每个课程的资源数量和学习进度
      await loadResourceCounts()
      await loadCourseProgress()
    } else {
      enrolledCourses.value = []
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
    enrolledCourses.value = []
  } finally {
    loading.value = false
  }
}

// 加载课程资源数量
const loadResourceCounts = async () => {
  for (const course of enrolledCourses.value) {
    try {
      const response = await getCourseResources(course.id)
      if (response && response.success && response.data) {
        resourceCountMap.value[course.id] = response.data.length
      }
    } catch (error) {
      console.error(`加载课程${course.id}资源数量失败:`, error)
      resourceCountMap.value[course.id] = 0
    }
  }
}

// 加载课程学习进度
const loadCourseProgress = async () => {
  for (const course of enrolledCourses.value) {
    try {
      const response = await getCourseProgress(studentId.value, course.id)
      console.log(`课程${course.id}进度响应:`, response)
      if (response && response.success && response.data) {
        // 更新课程的进度
        course.progress = response.data.progress || 0
        console.log(`课程${course.id}进度已更新为: ${course.progress}%`)
      }
    } catch (error) {
      console.error(`加载课程${course.id}进度失败:`, error)
      course.progress = 0
    }
  }
}

// 页面挂载时初始化数据
onMounted(() => {
  loadEnrolledCourses()
})
</script>

<style lang="scss" scoped>
.online-learning {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
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
  }
  
  .header-title {
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
    
    h2 {
      margin: 0;
      font-size: 2.5rem;
      font-weight: 800;
      background: linear-gradient(135deg, #ffffff, #f0f0f0);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
  
  .header-subtitle {
    margin: 0;
    font-size: 1.1rem;
    opacity: 0.9;
    font-weight: 400;
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

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 筛选和搜索 */
.filter-section {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .search-box {
    position: relative;
    margin-bottom: 20px;
    
    
    .search-icon {
      position: absolute;
      left: 16px;
      top: 50%;
      transform: translateY(-50%);
      width: 20px;
      height: 20px;
      opacity: 0.6;
    }
    
    .search-input {
      width: 92.5%;
      padding: 16px 16px 16px 48px;
      border: 2px solid #e2e8f0;
      border-radius: 16px;
      font-size: 16px;
      background: #f7fafc;
      transition: all 0.3s ease;
      
      &:focus {
        outline: none;
        border-color: #667eea;
        background: white;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
      
      &::placeholder {
        color: #a0aec0;
      }
    }
  }
  
  .filter-options {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    .filter-group {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .filter-icon {
        width: 20px;
        height: 20px;
        opacity: 0.7;
      }
      
      .filter-select {
        flex: 1;
        padding: 12px 16px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        font-size: 14px;
        background: #f7fafc;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          background: white;
        }
      }
    }
  }
}

/* 加载状态 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 32px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  
  .loading-spinner {
    margin-bottom: 24px;
    
    .spinner {
      width: 48px;
      height: 48px;
      border: 4px solid #e5e7eb;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }
  
  .loading-text {
    margin: 0;
    color: #6b7280;
    font-size: 16px;
    font-weight: 500;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空状态 */
.empty-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 32px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  text-align: center;
  
  .empty-illustration {
    position: relative;
    margin-bottom: 32px;
    
    .empty-circle {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
      position: relative;
      animation: float 3s ease-in-out infinite;
      
      &::before {
        content: '';
        position: absolute;
        top: 20px;
        left: 20px;
        right: 20px;
        bottom: 20px;
        border-radius: 50%;
        background: linear-gradient(135deg, #ffffff, #f9fafb);
        box-shadow: inset 0 4px 8px rgba(0, 0, 0, 0.1);
      }
    }
    
    .empty-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 48px;
      height: 48px;
      opacity: 0.6;
      z-index: 2;
    }
  }
  
  .empty-title {
    margin: 0 0 12px 0;
    font-size: 1.5rem;
    font-weight: 700;
    color: #374151;
  }
  
  .empty-description {
    margin: 0 0 8px 0;
    color: #6b7280;
    font-size: 16px;
    line-height: 1.5;
  }
  
  .empty-suggestion {
    margin: 0 0 32px 0;
    color: #9ca3af;
    font-size: 14px;
  }
  
  .empty-actions {
    .contact-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      border: none;
      border-radius: 16px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      
      .btn-icon {
        width: 16px;
        height: 16px;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  animation: fadeInUp 0.6s ease-out;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

/* 课程卡片 */
.course-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  
  &:hover {
    transform: translateY(-12px) scale(1.02);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
    
    .course-overlay {
      opacity: 0.3;
    }
    
    .course-actions .action-btn {
      transform: translateY(0);
      opacity: 1;
    }
  }
  
  /* 课程封面 */
  .course-image {
    height: 200px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;
    
    .course-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(0,0,0,0.2), rgba(0,0,0,0.1));
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    .course-badge {
      position: absolute;
      top: 16px;
      right: 16px;
      display: flex;
      flex-direction: column;
      gap: 8px;
      z-index: 2;
      
      .difficulty-badge, .rating-badge {
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.2);
      }
      
      .difficulty-badge {
        &.difficulty-beginner {
          background: rgba(34, 197, 94, 0.9);
          color: white;
        }
        &.difficulty-intermediate {
          background: rgba(59, 130, 246, 0.9);
          color: white;
        }
        &.difficulty-advanced {
          background: rgba(239, 68, 68, 0.9);
          color: white;
        }
      }
      
      .rating-badge {
        background: rgba(255, 255, 255, 0.9);
        color: #1a1a1a;
        display: flex;
        align-items: center;
        gap: 4px;
        
        .rating-star {
          font-size: 14px;
        }
      }
    }
    
    .course-category {
      position: absolute;
      bottom: 16px;
      left: 16px;
      padding: 8px 16px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 16px;
      font-size: 14px;
      font-weight: 600;
      color: #374151;
      backdrop-filter: blur(10px);
    }
  }
  
  /* 课程内容 */
  .course-content {
    padding: 24px;
    
    .course-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
      
      .course-title {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 700;
        color: #1f2937;
        line-height: 1.4;
        flex: 1;
        margin-right: 12px;
      }
      
      .course-status {
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        white-space: nowrap;
        
        &.status-active {
          background: #dcfce7;
          color: #166534;
        }
        &.status-completed {
          background: #e0e7ff;
          color: #3730a3;
        }
        &.status-paused {
          background: #fef3c7;
          color: #92400e;
        }
        &.status-draft {
          background: #f3f4f6;
          color: #374151;
        }
      }
    }
    
    .course-description {
      margin: 0 0 20px 0;
      color: #6b7280;
      font-size: 14px;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .course-meta {
      margin-bottom: 20px;
      
      .meta-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #6b7280;
        font-size: 13px;
        
        .meta-icon {
          width: 16px;
          height: 16px;
          opacity: 0.7;
        }
      }
    }
    
    .course-progress {
      margin-bottom: 24px;
      padding: 16px;
      background: linear-gradient(135deg, #f8fafc, #e2e8f0);
      border-radius: 16px;
      border: 1px solid #e2e8f0;
      
      .progress-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .progress-label {
          font-size: 14px;
          font-weight: 600;
          color: #374151;
        }
        
        .progress-value {
          font-size: 14px;
          font-weight: 700;
          color: #1f2937;
        }
      }
      
      .progress-bar {
        height: 8px;
        background: #e5e7eb;
        border-radius: 8px;
        overflow: hidden;
        margin-bottom: 8px;
        
        .progress-fill {
          height: 100%;
          border-radius: 8px;
          transition: all 0.3s ease;
          position: relative;
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            animation: shimmer 2s infinite;
          }
        }
      }
      
      .progress-text {
        font-size: 12px;
        color: #6b7280;
        text-align: center;
        font-weight: 500;
      }
    }
    
    .course-actions {
      display: flex;
      gap: 12px;
      
      .action-btn {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 12px 16px;
        border: none;
        border-radius: 12px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        transform: translateY(8px);
        opacity: 0.8;
        
        .btn-icon {
          width: 16px;
          height: 16px;
        }
        
        &.primary {
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: white;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
          }
        }
        
        &.secondary {
          background: white;
          color: #374151;
          border: 2px solid #e5e7eb;
          
          &:hover {
            background: #f9fafb;
            border-color: #d1d5db;
            transform: translateY(-2px);
          }
        }
      }
    }
  }
}

/* 资源模态框 */
.resource-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease-out;
  
  .modal-content {
    background: white;
    border-radius: 24px;
    width: 90%;
    max-width: 800px;
    max-height: 80vh;
    overflow: hidden;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
    animation: slideUp 0.3s ease-out;
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      border-bottom: 1px solid #e5e7eb;
      background: linear-gradient(135deg, #f8fafc, #f1f5f9);
      
      .modal-title {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .modal-icon {
          width: 32px;
          height: 32px;
          padding: 8px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 12px;
        }
        
        h3 {
          margin: 0;
          font-size: 1.25rem;
          font-weight: 700;
          color: #374151;
        }
      }
      
      .close-btn {
        width: 40px;
        height: 40px;
        border: none;
        background: #f3f4f6;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .close-icon {
          width: 20px;
          height: 20px;
          opacity: 0.7;
        }
        
        &:hover {
          background: #e5e7eb;
          transform: scale(1.05);
        }
      }
    }
    
    .modal-body {
      padding: 24px;
      max-height: 60vh;
      overflow-y: auto;
      
      .loading {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px;
        color: #6b7280;
      }
      
      .empty-resources {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 40px;
        text-align: center;
        
        .empty-icon {
          width: 64px;
          height: 64px;
          opacity: 0.5;
          margin-bottom: 16px;
        }
        
        p {
          margin: 0;
          color: #6b7280;
          font-size: 16px;
        }
      }
      
      .resources-list {
        .resource-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 16px;
          border: 1px solid #e5e7eb;
          border-radius: 16px;
          margin-bottom: 12px;
          transition: all 0.3s ease;
          background: white;
          
          &:hover {
            border-color: #d1d5db;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
          }
          
          .resource-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
            display: flex;
            align-items: center;
            justify-content: center;
            
            .icon-text {
              font-size: 24px;
              opacity: 0.8;
            }
          }
          
          .resource-info {
            flex: 1;
            
            .resource-title {
              margin: 0 0 4px 0;
              font-size: 16px;
              font-weight: 600;
              color: #374151;
            }
            
            .resource-filename {
              margin: 0 0 8px 0;
              font-size: 14px;
              color: #6b7280;
              font-family: monospace;
              background: #f3f4f6;
              padding: 2px 8px;
              border-radius: 6px;
              display: inline-block;
            }
            
            .resource-description {
              margin: 0 0 8px 0;
              font-size: 14px;
              color: #6b7280;
              line-height: 1.4;
            }
            
            .resource-meta {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 12px;
              color: #9ca3af;
            }
          }
          
          .resource-actions {
            .download-btn {
              display: flex;
              align-items: center;
              gap: 6px;
              padding: 8px 16px;
              background: linear-gradient(135deg, #667eea, #764ba2);
              color: white;
              border: none;
              border-radius: 8px;
              font-size: 14px;
              font-weight: 600;
              cursor: pointer;
              transition: all 0.3s ease;
              
              .btn-icon {
                font-size: 14px;
              }
              
              &:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
              }
            }
          }
        }
      }
    }
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;
    
    .header-title h2 {
      font-size: 2rem;
    }
  }
  
  .filter-section {
    padding: 20px;
    
    .filter-options {
      grid-template-columns: 1fr;
    }
  }
  
  .courses-grid {
    gap: 16px;
  }
  
  .course-card .course-content {
    padding: 20px;
  }
  
  .resource-modal {
    padding: 10px;
    
    .modal-content {
      width: 95%;
      max-height: 90vh;
      
      .modal-header,
      .modal-body {
        padding: 20px;
      }
      
      .modal-body .resources-list .resource-item {
        flex-direction: column;
        text-align: center;
        
        .resource-info {
          text-align: center;
        }
      }
    }
  }
}

/* 笔记功能样式 */
.notes-section {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  border: 1px solid #dee2e6;
}

.notes-header {
  margin-bottom: 16px;
  text-align: center;
}

.notes-header h4 {
  margin: 0 0 8px 0;
  color: #495057;
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.notes-header h4 i {
  color: #17a2b8;
}

.notes-description {
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
  line-height: 1.4;
}

.notes-actions {
  display: flex;
  justify-content: center;
}

.notes-btn {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(23, 162, 184, 0.3);
}

.notes-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(23, 162, 184, 0.4);
  background: linear-gradient(135deg, #138496 0%, #0f6674 100%);
}

.notes-btn i {
  font-size: 1rem;
}

/* 学习笔记模态框样式 */
.notes-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  padding: 20px;
}

.notes-modal {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 1400px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

.notes-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.notes-modal-header h3 {
  margin: 0;
  color: #495057;
  font-size: 1.4rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.notes-modal-header h3 i {
  color: #17a2b8;
}

.modal-close-btn {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.modal-close-btn i {
  font-size: 1.2rem;
}

.notes-modal-body {
  padding: 0;
  max-height: calc(90vh - 80px);
  overflow-y: auto;
}

/* 预览按钮样式 */
.resource-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.preview-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #28a745, #20c997);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.preview-btn .btn-icon {
  font-size: 14px;
}

.preview-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
  background: linear-gradient(135deg, #1e7e34, #17a2b8);
}

/* 文档预览模态框样式 */
.document-viewer-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 3000;
  backdrop-filter: blur(10px);
  animation: fadeIn 0.3s ease-out;
}

.document-viewer-container {
  width: 95%;
  height: 95%;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.4);
  animation: slideUp 0.3s ease-out;
  display: flex;
  flex-direction: column;
}

.document-viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  flex-shrink: 0;
}

.viewer-title {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #495057;
  font-size: 1.2rem;
  font-weight: 600;
}

.viewer-title i {
  color: #17a2b8;
  font-size: 1.4rem;
}

.viewer-close-btn {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 12px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
}

.viewer-close-btn:hover {
  background: #f8f9fa;
  color: #495057;
  transform: scale(1.1);
}

.viewer-close-btn i {
  font-size: 1.2rem;
}

.document-viewer-body {
  flex: 1;
  overflow: hidden;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style> 