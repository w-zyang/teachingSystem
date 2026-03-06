<template>
  <div class="course-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 内容区域 -->
    <div v-else>
    <!-- 顶部课程基础信息区 -->
    <div class="course-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn" circle>
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <div class="course-info">
          <h1 class="course-title">{{ courseData.title }}</h1>
          <div class="course-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ courseData.teacherName }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ courseData.duration }}
            </span>
            <span class="meta-item">
              <el-icon><Document /></el-icon>
              {{ courseData.resourceCount }} 个资源
            </span>
            <el-tag :type="getStatusType(courseData.status)">{{ courseData.status }}</el-tag>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="progress-circle">
          <el-progress 
            type="circle" 
            :percentage="courseData.progress" 
            :width="100"
            :stroke-width="8"
          >
            <template #default="{ percentage }">
              <span class="percentage-value">{{ percentage }}%</span>
              <span class="percentage-label">完成度</span>
            </template>
          </el-progress>
        </div>
      </div>
    </div>

    <!-- Tabs 区域 -->
    <el-tabs v-model="activeTab" class="course-tabs">
      <el-tab-pane label="课程概览" name="overview">
        <div class="overview-content">
          <el-card class="info-card">
            <h3>课程简介</h3>
            <p>{{ courseData.description }}</p>
          </el-card>
          <el-card class="info-card">
            <h3>学习目标</h3>
            <ul>
              <li v-for="(goal, index) in courseData.goals" :key="index">{{ goal }}</li>
            </ul>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习资源" name="resources">
        <div class="resources-content">
          <div v-if="resources.length === 0" class="empty-state">
            <el-empty description="暂无学习资源" />
          </div>
          <div v-else class="resource-list">
            <div 
              v-for="resource in resources" 
              :key="resource.id"
              class="resource-item"
            >
              <div class="resource-icon">{{ getResourceIcon(resource.fileType) }}</div>
              <div class="resource-info">
                <h4>{{ resource.title }}</h4>
                <p class="resource-filename">{{ resource.fileName }}</p>
                <div class="resource-meta">
                  <span>{{ formatFileSize(resource.fileSize) }}</span>
                  <span>下载 {{ resource.downloadCount }} 次</span>
                </div>
              </div>
              <div class="resource-actions">
                <el-button type="success" size="small" @click="openNotes(resource)">
                  <el-icon><Edit /></el-icon>
                  做笔记
                </el-button>
                <el-button type="primary" size="small" @click="downloadResource(resource)">
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="课程测验" name="exams">
        <div class="exams-content">
          <!-- 分段控制器 -->
          <div class="exam-segmented">
            <el-segmented v-model="examStatus" :options="examStatusOptions" size="large" />
          </div>

          <!-- 待完成测验 -->
          <div v-if="examStatus === 'pending'" class="exam-list">
            <div v-if="pendingExams.length === 0" class="empty-state">
              <el-empty description="暂无待完成的测验" />
            </div>
            <div 
              v-for="exam in pendingExams" 
              :key="exam.id"
              class="exam-card pending"
            >
              <div class="exam-header">
                <h3>{{ exam.name }}</h3>
                <el-tag type="warning">待完成</el-tag>
              </div>
              <div class="exam-info">
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span class="deadline">截止时间：{{ exam.deadline }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Document /></el-icon>
                  <span>题量：{{ exam.questionCount }} 题</span>
                </div>
                <div class="info-item">
                  <el-icon><Star /></el-icon>
                  <span>总分：{{ exam.totalScore }} 分</span>
                </div>
              </div>
              <div class="exam-actions">
                <el-button type="primary" size="large" @click="startExam(exam)">
                  <el-icon><CaretRight /></el-icon>
                  开始测验
                </el-button>
              </div>
            </div>
          </div>

          <!-- 已完成测验 -->
          <div v-if="examStatus === 'completed'" class="exam-list">
            <div v-if="completedExams.length === 0" class="empty-state">
              <el-empty description="暂无已完成的测验" />
            </div>
            <div 
              v-for="exam in completedExams" 
              :key="exam.id"
              class="exam-card completed"
            >
              <div class="exam-header">
                <h3>{{ exam.name }}</h3>
                <el-tag type="success">已完成</el-tag>
              </div>
              <div class="exam-info">
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>提交时间：{{ exam.submitTime }}</span>
                </div>
                <div class="info-item score-highlight">
                  <el-icon><Trophy /></el-icon>
                  <span class="score">得分：{{ exam.score }} / {{ exam.totalScore }}</span>
                </div>
              </div>
              <div class="exam-actions">
                <el-button type="primary" plain size="large" @click="viewReport(exam)">
                  <el-icon><Document /></el-icon>
                  查看报告
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="课堂讨论" name="discussion">
        <div class="discussion-content">
          <!-- 发布新问题 -->
          <div class="post-question">
            <el-input
              v-model="newQuestion"
              type="textarea"
              :rows="4"
              placeholder="有什么问题想问老师或同学？"
              maxlength="500"
              show-word-limit
            />
            <div class="post-actions">
              <el-button type="primary" @click="postQuestion" :disabled="!newQuestion.trim()">
                <el-icon><ChatDotRound /></el-icon>
                发布问题
              </el-button>
            </div>
          </div>

          <!-- 问题列表 -->
          <div class="questions-list">
            <div v-if="discussions.length === 0" class="empty-state">
              <el-empty description="还没有讨论，快来发起第一个问题吧！" />
            </div>
            <div 
              v-for="discussion in discussions" 
              :key="discussion.id"
              class="discussion-item"
            >
              <!-- 老师已解答标签 -->
              <div v-if="discussion.hasTeacherReply" class="teacher-answered-badge">
                <el-icon><CircleCheck /></el-icon>
                老师已解答
              </div>

              <!-- 问题头部 -->
              <div class="discussion-header">
                <div class="user-info">
                  <el-avatar :size="40" :src="discussion.avatar || '/src/assets/author.png'" />
                  <div class="user-details">
                    <span class="username">{{ discussion.username }}</span>
                    <span class="time">{{ discussion.time }}</span>
                  </div>
                </div>
                <div class="discussion-actions">
                  <el-button 
                    :type="discussion.liked ? 'primary' : 'default'" 
                    size="small" 
                    @click="toggleLike(discussion)"
                  >
                    <el-icon><Star /></el-icon>
                    {{ discussion.upvotes }}
                  </el-button>
                </div>
              </div>

              <!-- 问题内容 -->
              <div class="discussion-body">
                <p class="question-text">{{ discussion.question }}</p>
              </div>

              <!-- 回复列表 -->
              <div v-if="discussion.replies && discussion.replies.length > 0" class="replies-list">
                <div 
                  v-for="reply in discussion.replies" 
                  :key="reply.id"
                  class="reply-item"
                  :class="{ 'teacher-reply': reply.isTeacher }"
                >
                  <div class="reply-header">
                    <el-avatar :size="32" :src="reply.avatar || '/src/assets/author.png'" />
                    <div class="reply-user-info">
                      <span class="reply-username">
                        {{ reply.username }}
                        <el-tag v-if="reply.isTeacher" type="success" size="small">教师</el-tag>
                      </span>
                      <span class="reply-time">{{ reply.time }}</span>
                    </div>
                  </div>
                  <div class="reply-content">{{ reply.content }}</div>
                </div>
              </div>

              <!-- 回复输入框 -->
              <div class="reply-input">
                <el-input
                  v-model="discussion.replyText"
                  placeholder="写下你的回复..."
                  size="small"
                >
                  <template #append>
                    <el-button @click="postReply(discussion)">回复</el-button>
                  </template>
                </el-input>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 笔记弹窗 - 全屏模式 -->
    <el-dialog 
      v-model="showNotesDialog" 
      title="智能笔记与标注" 
      fullscreen
      :close-on-click-modal="false"
    >
      <div class="notes-workspace">
        <!-- 左侧：课件预览区 -->
        <div class="document-preview">
          <div class="preview-header">
            <h3>{{ selectedResource?.title }}</h3>
            <div class="preview-tools">
              <el-button-group>
                <el-button size="small" @click="zoomOut">
                  <el-icon><ZoomOut /></el-icon>
                </el-button>
                <el-button size="small">{{ zoomLevel }}%</el-button>
                <el-button size="small" @click="zoomIn">
                  <el-icon><ZoomIn /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>
          <div class="preview-content" ref="previewContainer">
            <!-- 模拟课件内容 -->
            <div class="document-page" :style="{ transform: `scale(${zoomLevel / 100})` }">
              <div class="page-content">
                <h2>第一章：数据结构概述</h2>
                <p class="selectable-text" @mouseup="handleTextSelection">
                  数据结构是计算机存储、组织数据的方式。数据结构是指相互之间存在一种或多种特定关系的数据元素的集合。
                  通常情况下，精心选择的数据结构可以带来更高的运行或者存储效率。
                </p>
                <h3>1.1 基本概念</h3>
                <p class="selectable-text" @mouseup="handleTextSelection">
                  数据：是描述客观事物的符号，是计算机中可以操作的对象，是能被计算机识别，并输入给计算机处理的符号集合。
                </p>
                <p class="selectable-text" @mouseup="handleTextSelection">
                  数据元素：是组成数据的、有一定意义的基本单位，在计算机中通常作为整体处理。也被称为记录。
                </p>
                <p class="selectable-text" @mouseup="handleTextSelection">
                  数据项：一个数据元素可以由若干个数据项组成。数据项是数据不可分割的最小单位。
                </p>
                <h3>1.2 数据结构分类</h3>
                <ul class="selectable-text" @mouseup="handleTextSelection">
                  <li>线性结构：数据元素之间是一对一的关系</li>
                  <li>树形结构：数据元素之间是一对多的层次关系</li>
                  <li>图形结构：数据元素之间是多对多的关系</li>
                  <li>集合结构：数据元素除了同属于一个集合外，它们之间没有其他关系</li>
                </ul>
              </div>
              
              <!-- 标注层 -->
              <div class="annotations-layer">
                <div 
                  v-for="annotation in annotations" 
                  :key="annotation.id"
                  class="annotation-mark"
                  :style="{
                    top: annotation.top + 'px',
                    left: annotation.left + 'px',
                    width: annotation.width + 'px',
                    height: annotation.height + 'px',
                    backgroundColor: annotation.color
                  }"
                  @click="editAnnotation(annotation)"
                >
                  <el-tooltip :content="annotation.note" placement="top">
                    <div class="annotation-content"></div>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：笔记编辑区 -->
        <div class="notes-panel">
          <div class="panel-header">
            <h3>我的笔记</h3>
            <div class="annotation-tools">
              <el-button-group>
                <el-button 
                  :type="currentTool === 'highlight' ? 'primary' : 'default'"
                  size="small" 
                  @click="setTool('highlight')"
                >
                  <el-icon><Brush /></el-icon>
                  高亮
                </el-button>
                <el-button 
                  :type="currentTool === 'underline' ? 'primary' : 'default'"
                  size="small" 
                  @click="setTool('underline')"
                >
                  <el-icon><Edit /></el-icon>
                  下划线
                </el-button>
                <el-button 
                  :type="currentTool === 'note' ? 'primary' : 'default'"
                  size="small" 
                  @click="setTool('note')"
                >
                  <el-icon><ChatLineSquare /></el-icon>
                  批注
                </el-button>
              </el-button-group>
            </div>
          </div>

          <!-- 颜色选择器 -->
          <div class="color-picker" v-if="currentTool !== 'none'">
            <span>标注颜色：</span>
            <div class="color-options">
              <div 
                v-for="color in highlightColors" 
                :key="color.value"
                class="color-option"
                :class="{ active: selectedColor === color.value }"
                :style="{ backgroundColor: color.value }"
                @click="selectedColor = color.value"
              >
                <el-icon v-if="selectedColor === color.value"><Check /></el-icon>
              </div>
            </div>
          </div>

          <!-- 笔记列表 -->
          <div class="notes-list">
            <div v-if="notesList.length === 0" class="empty-notes">
              <el-empty description="还没有笔记，选中文字开始标注吧！" :image-size="100" />
            </div>
            <div v-else>
              <div 
                v-for="note in notesList" 
                :key="note.id"
                class="note-item"
                :style="{ borderLeftColor: note.color }"
              >
                <div class="note-header">
                  <el-tag :type="getNoteTypeTag(note.type)" size="small">
                    {{ getNoteTypeName(note.type) }}
                  </el-tag>
                  <span class="note-time">{{ note.time }}</span>
                </div>
                <div class="note-content">
                  <div class="note-text">{{ note.text }}</div>
                  <div v-if="note.note" class="note-comment">
                    <el-icon><ChatLineSquare /></el-icon>
                    {{ note.note }}
                  </div>
                </div>
                <div class="note-actions">
                  <el-button type="primary" link size="small" @click="locateNote(note)">
                    <el-icon><Location /></el-icon>
                    定位
                  </el-button>
                  <el-button type="danger" link size="small" @click="deleteNote(note)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 文本笔记区 -->
          <div class="text-notes">
            <h4>文字笔记</h4>
            <el-input
              v-model="currentNote"
              type="textarea"
              :rows="8"
              placeholder="记录你的学习心得、疑问、总结..."
              show-word-limit
              :maxlength="2000"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <div class="notes-footer">
          <div class="footer-info">
            <el-icon><InfoFilled /></el-icon>
            <span>已标注 {{ notesList.length }} 处，文字笔记 {{ currentNote.length }} 字</span>
          </div>
          <div class="footer-actions">
            <el-button @click="showNotesDialog = false">取消</el-button>
            <el-button type="primary" @click="saveNote">
              <el-icon><Check /></el-icon>
              保存笔记
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  ArrowLeft, User, Clock, Document, Download, CaretRight, Star, Trophy,
  Edit, ChatDotRound, CircleCheck, InfoFilled, Check, ZoomIn, ZoomOut,
  Brush, ChatLineSquare, Location, Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseById } from '@/api/course'
import { getCourseResources } from '@/api/courseResource'
import { getPublishedExamsByCourseId, getExamById } from '@/api/exam'
import { getCourseProgress } from '@/api/progress'

const router = useRouter()
const route = useRoute()

const activeTab = ref('overview')
const examStatus = ref('pending')
const showNotesDialog = ref(false)
const selectedResource = ref(null)
const currentNote = ref('')
const newQuestion = ref('')
const zoomLevel = ref(100)
const currentTool = ref('none')
const selectedColor = ref('rgba(255, 235, 59, 0.4)')
const previewContainer = ref(null)
const annotations = ref([])
const notesList = ref([])
const loading = ref(false)

const highlightColors = [
  { name: '黄色', value: 'rgba(255, 235, 59, 0.4)' },
  { name: '绿色', value: 'rgba(76, 175, 80, 0.4)' },
  { name: '蓝色', value: 'rgba(33, 150, 243, 0.4)' },
  { name: '粉色', value: 'rgba(233, 30, 99, 0.4)' },
  { name: '橙色', value: 'rgba(255, 152, 0, 0.4)' }
]

const examStatusOptions = [
  { label: '待完成', value: 'pending' },
  { label: '已完成', value: 'completed' }
]

// 获取当前学生ID
const studentId = ref(localStorage.getItem('userId') || '17')

// 课程数据
const courseData = ref({
  id: route.query.courseId || '1',
  title: route.query.courseName || '加载中...',
  teacherName: '加载中...',
  duration: '--',
  resourceCount: 0,
  status: '进行中',
  progress: 0,
  description: '加载中...',
  goals: []
})

// 学习资源
const resources = ref([])

// 待完成测验
const pendingExams = ref([])

// 已完成测验
const completedExams = ref([])

// 讨论数据
const discussions = ref([
  {
    id: 1,
    username: '李明',
    avatar: '/src/assets/author.png',
    time: '2026-02-27 10:30',
    question: '老师，关于栈的应用场景，除了表达式求值和括号匹配，还有哪些常见的应用？',
    upvotes: 15,
    liked: false,
    hasTeacherReply: true,
    replyText: '',
    replies: [
      {
        id: 1,
        username: '张教授',
        avatar: '/src/assets/author.png',
        time: '2026-02-27 11:00',
        content: '很好的问题！栈还广泛应用于：1. 函数调用栈（递归实现）2. 浏览器的前进后退功能 3. 撤销/重做操作 4. 深度优先搜索（DFS）5. 编译器的语法分析等。建议你多做一些相关练习来加深理解。',
        isTeacher: true
      },
      {
        id: 2,
        username: '王小红',
        avatar: '/src/assets/author.png',
        time: '2026-02-27 14:20',
        content: '感谢老师的解答，我也学到了！',
        isTeacher: false
      }
    ]
  },
  {
    id: 2,
    username: '张伟',
    avatar: '/src/assets/author.png',
    time: '2026-02-26 16:45',
    question: '队列的循环实现中，如何判断队列是满还是空？这两种状态的判断条件是什么？',
    upvotes: 8,
    liked: false,
    hasTeacherReply: false,
    replyText: '',
    replies: [
      {
        id: 3,
        username: '刘强',
        avatar: '/src/assets/author.png',
        time: '2026-02-26 17:10',
        content: '我记得可以通过预留一个空位来区分，或者增加一个计数器变量。',
        isTeacher: false
      }
    ]
  },
  {
    id: 3,
    username: '陈晓',
    avatar: '/src/assets/author.png',
    time: '2026-02-25 09:15',
    question: '链表和数组各有什么优缺点？在什么场景下应该选择链表而不是数组？',
    upvotes: 12,
    liked: true,
    hasTeacherReply: true,
    replyText: '',
    replies: [
      {
        id: 4,
        username: '张教授',
        avatar: '/src/assets/author.png',
        time: '2026-02-25 10:30',
        content: '数组优点：随机访问快O(1)，内存连续；缺点：插入删除慢O(n)，大小固定。链表优点：插入删除快O(1)，动态扩展；缺点：随机访问慢O(n)，额外指针开销。选择原则：频繁插入删除用链表，频繁随机访问用数组。',
        isTeacher: true
      }
    ]
  }
])

const goBack = () => {
  router.back()
}

const getStatusType = (status) => {
  const map = {
    '进行中': 'success',
    '已完成': 'info',
    '未开始': 'warning'
  }
  return map[status] || 'info'
}

const getResourceIcon = (fileType) => {
  const iconMap = {
    'pdf': '📄',
    'pptx': '📊',
    'docx': '📝',
    'mp4': '🎥',
    'zip': '📦'
  }
  return iconMap[fileType] || '📄'
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const openNotes = (resource) => {
  selectedResource.value = resource
  // 加载该资源的笔记和标注
  const savedData = localStorage.getItem(`notes_${resource.id}`)
  if (savedData) {
    const data = JSON.parse(savedData)
    currentNote.value = data.textNote || ''
    notesList.value = data.annotations || []
    annotations.value = data.annotations || []
  } else {
    currentNote.value = ''
    notesList.value = []
    annotations.value = []
  }
  showNotesDialog.value = true
}

const saveNote = () => {
  if (selectedResource.value) {
    const data = {
      textNote: currentNote.value,
      annotations: notesList.value
    }
    localStorage.setItem(`notes_${selectedResource.value.id}`, JSON.stringify(data))
    ElMessage.success('笔记保存成功！')
    showNotesDialog.value = false
  }
}

const zoomIn = () => {
  if (zoomLevel.value < 200) {
    zoomLevel.value += 10
  }
}

const zoomOut = () => {
  if (zoomLevel.value > 50) {
    zoomLevel.value -= 10
  }
}

const setTool = (tool) => {
  currentTool.value = tool
  if (tool !== 'none') {
    ElMessage.info(`已切换到${tool === 'highlight' ? '高亮' : tool === 'underline' ? '下划线' : '批注'}模式，请选中文字`)
  }
}

const handleTextSelection = (event) => {
  const selection = window.getSelection()
  const selectedText = selection.toString().trim()
  
  if (!selectedText || currentTool.value === 'none') return
  
  // 获取选中文本的位置
  const range = selection.getRangeAt(0)
  const rect = range.getBoundingClientRect()
  const pageElement = event.currentTarget.closest('.document-page')
  
  if (!pageElement) return
  
  const pageRect = pageElement.getBoundingClientRect()
  
  // 计算相对于文档页面的位置
  const annotation = {
    id: Date.now(),
    text: selectedText,
    type: currentTool.value,
    color: selectedColor.value,
    top: rect.top - pageRect.top + pageElement.scrollTop,
    left: rect.left - pageRect.left + pageElement.scrollLeft,
    width: rect.width,
    height: rect.height,
    time: new Date().toLocaleString('zh-CN'),
    note: ''
  }
  
  // 如果是批注模式，弹出输入框
  if (currentTool.value === 'note') {
    ElMessageBox.prompt('请输入批注内容', '添加批注', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '写下你的想法、疑问或总结...'
    }).then(({ value }) => {
      annotation.note = value || ''
      notesList.value.push(annotation)
      annotations.value.push(annotation)
      ElMessage.success('标注添加成功！')
    }).catch(() => {
      ElMessage.info('已取消批注')
    })
  } else {
    notesList.value.push(annotation)
    annotations.value.push(annotation)
    ElMessage.success('标注添加成功！')
  }
  
  // 清除选中
  selection.removeAllRanges()
}

const editAnnotation = (annotation) => {
  ElMessageBox.prompt('编辑批注内容', '编辑批注', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputValue: annotation.note,
    inputPlaceholder: '写下你的想法、疑问或总结...'
  }).then(({ value }) => {
    annotation.note = value || ''
    ElMessage.success('批注更新成功！')
  }).catch(() => {})
}

const getNoteTypeName = (type) => {
  const map = {
    highlight: '高亮',
    underline: '下划线',
    note: '批注'
  }
  return map[type] || type
}

const getNoteTypeTag = (type) => {
  const map = {
    highlight: 'warning',
    underline: 'primary',
    note: 'success'
  }
  return map[type] || 'info'
}

const locateNote = (note) => {
  ElMessage.info('定位到标注位置')
  // 这里可以滚动到对应位置
}

const deleteNote = (note) => {
  ElMessageBox.confirm('确定要删除这条标注吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = notesList.value.findIndex(n => n.id === note.id)
    if (index > -1) {
      notesList.value.splice(index, 1)
      annotations.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

// 加载课程详情
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.query.courseId
    
    if (!courseId) {
      ElMessage.error('课程ID不存在')
      return
    }
    
    const response = await getCourseById(courseId)
    console.log('课程详情响应:', response)
    
    if (response && response.success && response.data) {
      const course = response.data
      courseData.value = {
        id: course.id,
        title: course.name || course.title,
        teacherName: course.teacherName || '未知教师',
        duration: course.duration || '未设置',
        resourceCount: 0, // 将在加载资源后更新
        status: course.status === 'active' ? '进行中' : course.status === 'completed' ? '已完成' : '未开始',
        progress: 0, // 将在加载进度后更新
        description: course.description || '暂无课程简介',
        goals: course.goals || []
      }
      
      // 如果goals是字符串，尝试解析
      if (typeof courseData.value.goals === 'string') {
        try {
          courseData.value.goals = JSON.parse(courseData.value.goals)
        } catch {
          courseData.value.goals = courseData.value.goals.split('\n').filter(g => g.trim())
        }
      }
      
      // 加载课程资源
      await loadCourseResources(courseId)
      
      // 加载课程进度
      await loadCourseProgress(courseId)
      
      // 加载课程测验
      await loadCourseExams(courseId)
    }
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('加载课程详情失败')
  } finally {
    loading.value = false
  }
}

// 加载课程资源
const loadCourseResources = async (courseId) => {
  try {
    const response = await getCourseResources(courseId)
    console.log('课程资源响应:', response)
    
    if (response && response.success && response.data) {
      resources.value = response.data.map(resource => ({
        id: resource.id,
        title: resource.title,
        fileName: resource.fileName,
        fileType: resource.fileType || getFileTypeFromName(resource.fileName),
        fileSize: resource.fileSize || 0,
        downloadCount: resource.downloadCount || 0,
        fileUrl: resource.fileUrl
      }))
      
      // 更新资源数量
      courseData.value.resourceCount = resources.value.length
    }
  } catch (error) {
    console.error('加载课程资源失败:', error)
  }
}

// 加载课程进度
const loadCourseProgress = async (courseId) => {
  try {
    const response = await getCourseProgress(studentId.value, courseId)
    console.log('课程进度响应:', response)
    
    if (response && response.success && response.data) {
      courseData.value.progress = response.data.progress || 0
    }
  } catch (error) {
    console.error('加载课程进度失败:', error)
  }
}

// 加载课程测验
const loadCourseExams = async (courseId) => {
  try {
    const response = await getPublishedExamsByCourseId(courseId)
    console.log('课程测验响应:', response)
    
    if (response && response.success && response.data) {
      // 获取详细的测验信息
      const examsWithDetails = []
      for (const exam of response.data) {
        try {
          const detail = await getExamById(exam.id)
          if (detail && detail.success && detail.data) {
            examsWithDetails.push(detail.data)
          } else {
            examsWithDetails.push(exam)
          }
        } catch {
          examsWithDetails.push(exam)
        }
      }
      
      // 分类为待完成和已完成
      // TODO: 这里需要根据学生的提交记录来判断，暂时全部放入待完成
      pendingExams.value = examsWithDetails.map(exam => ({
        id: exam.id,
        name: exam.name || exam.title,
        deadline: exam.endTime || '未设置',
        questionCount: exam.questions ? exam.questions.length : 0,
        totalScore: exam.totalScore || 100
      }))
      
      // 已完成的测验需要从提交记录中获取
      // TODO: 调用获取学生提交记录的API
      completedExams.value = []
    }
  } catch (error) {
    console.error('加载课程测验失败:', error)
  }
}

// 从文件名获取文件类型
const getFileTypeFromName = (fileName) => {
  if (!fileName) return 'unknown'
  const ext = fileName.split('.').pop().toLowerCase()
  return ext
}

const downloadResource = (resource) => {
  ElMessage.success(`开始下载：${resource.fileName}`)
}

const startExam = (exam) => {
  router.push({
    path: '/student/practice-evaluation',
    query: {
      examId: exam.id,
      courseId: courseData.value.id
    }
  })
}

const viewReport = (exam) => {
  ElMessage.info('查看报告功能开发中...')
}

const postQuestion = () => {
  if (!newQuestion.value.trim()) return
  
  const question = {
    id: Date.now(),
    username: localStorage.getItem('username') || '当前用户',
    avatar: '/src/assets/author.png',
    time: new Date().toLocaleString('zh-CN'),
    question: newQuestion.value,
    upvotes: 0,
    liked: false,
    hasTeacherReply: false,
    replyText: '',
    replies: []
  }
  
  discussions.value.unshift(question)
  newQuestion.value = ''
  ElMessage.success('问题发布成功！')
}

const toggleLike = (discussion) => {
  discussion.liked = !discussion.liked
  discussion.upvotes += discussion.liked ? 1 : -1
}

const postReply = (discussion) => {
  if (!discussion.replyText || !discussion.replyText.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  const reply = {
    id: Date.now(),
    username: localStorage.getItem('username') || '当前用户',
    avatar: '/src/assets/author.png',
    time: new Date().toLocaleString('zh-CN'),
    content: discussion.replyText,
    isTeacher: false
  }
  
  if (!discussion.replies) {
    discussion.replies = []
  }
  
  discussion.replies.push(reply)
  discussion.replyText = ''
  ElMessage.success('回复成功！')
}

onMounted(() => {
  console.log('课程详情页加载，课程ID:', courseData.value.id)
  loadCourseDetail()
})
</script>

<style lang="scss" scoped>
.course-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.loading-container {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.course-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    flex: 1;

    .back-btn {
      background: rgba(255, 255, 255, 0.2);
      border: none;
      color: white;
      font-size: 20px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateX(-4px);
      }
    }

    .course-info {
      .course-title {
        margin: 0 0 12px 0;
        font-size: 32px;
        font-weight: 700;
      }

      .course-meta {
        display: flex;
        gap: 24px;
        align-items: center;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          opacity: 0.95;
        }
      }
    }
  }

  .header-right {
    .progress-circle {
      :deep(.el-progress__text) {
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .percentage-value {
          font-size: 24px;
          font-weight: 700;
          color: #667eea;
        }
        
        .percentage-label {
          font-size: 12px;
          color: #6b7280;
          margin-top: 4px;
        }
      }
    }
  }
}

.course-tabs {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

  :deep(.el-tabs__header) {
    margin-bottom: 24px;
  }

  :deep(.el-tabs__item) {
    font-size: 16px;
    font-weight: 600;
  }
}

.overview-content {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .info-card {
    h3 {
      margin: 0 0 16px 0;
      font-size: 20px;
      color: #374151;
    }

    p {
      line-height: 1.8;
      color: #6b7280;
      margin: 0;
    }

    ul {
      margin: 0;
      padding-left: 24px;
      
      li {
        line-height: 2;
        color: #6b7280;
      }
    }
  }
}

.resources-content {
  .resource-list {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .resource-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      transition: all 0.3s;

      &:hover {
        border-color: #667eea;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        transform: translateY(-2px);
      }

      .resource-icon {
        font-size: 40px;
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
        border-radius: 12px;
      }

      .resource-info {
        flex: 1;

        h4 {
          margin: 0 0 8px 0;
          font-size: 16px;
          color: #374151;
        }

        .resource-filename {
          margin: 0 0 8px 0;
          font-size: 13px;
          color: #9ca3af;
          font-family: monospace;
        }

        .resource-meta {
          display: flex;
          gap: 12px;
          font-size: 12px;
          color: #9ca3af;
        }
      }
    }
  }
}

.exams-content {
  .exam-segmented {
    margin-bottom: 24px;
    display: flex;
    justify-content: center;
  }

  .exam-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 20px;

    .exam-card {
      border: 2px solid #e5e7eb;
      border-radius: 16px;
      padding: 24px;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      &.pending {
        border-color: #fbbf24;
        background: linear-gradient(135deg, #fffbeb, #ffffff);

        .deadline {
          color: #dc2626;
          font-weight: 600;
        }
      }

      &.completed {
        border-color: #10b981;
        background: linear-gradient(135deg, #f0fdf4, #ffffff);
      }

      .exam-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 18px;
          color: #374151;
        }
      }

      .exam-info {
        display: flex;
        flex-direction: column;
        gap: 12px;
        margin-bottom: 20px;

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #6b7280;
          font-size: 14px;

          &.score-highlight {
            .score {
              font-size: 18px;
              font-weight: 700;
              color: #10b981;
            }
          }
        }
      }

      .exam-actions {
        display: flex;
        justify-content: center;
      }
    }
  }
}

.empty-state {
  padding: 60px 0;
}

/* 讨论区样式 */
.discussion-content {
  .post-question {
    background: #f8fafc;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;

    .post-actions {
      margin-top: 12px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .questions-list {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .discussion-item {
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      padding: 20px;
      position: relative;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }

      .teacher-answered-badge {
        position: absolute;
        top: -12px;
        right: 20px;
        background: linear-gradient(135deg, #10b981, #059669);
        color: white;
        padding: 6px 16px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 6px;
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        animation: pulse 2s infinite;

        .el-icon {
          font-size: 16px;
        }
      }

      .discussion-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .user-details {
            display: flex;
            flex-direction: column;

            .username {
              font-weight: 600;
              color: #374151;
              font-size: 15px;
            }

            .time {
              font-size: 12px;
              color: #9ca3af;
            }
          }
        }
      }

      .discussion-body {
        margin-bottom: 16px;

        .question-text {
          font-size: 15px;
          line-height: 1.8;
          color: #374151;
          margin: 0;
        }
      }

      .replies-list {
        background: #f8fafc;
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 12px;

        .reply-item {
          padding: 12px;
          border-radius: 8px;
          margin-bottom: 12px;
          background: white;

          &:last-child {
            margin-bottom: 0;
          }

          &.teacher-reply {
            background: linear-gradient(135deg, #f0fdf4, #dcfce7);
            border-left: 3px solid #10b981;
          }

          .reply-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;

            .reply-user-info {
              display: flex;
              flex-direction: column;

              .reply-username {
                font-weight: 600;
                color: #374151;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 6px;
              }

              .reply-time {
                font-size: 11px;
                color: #9ca3af;
              }
            }
          }

          .reply-content {
            font-size: 14px;
            line-height: 1.6;
            color: #4b5563;
            padding-left: 42px;
          }
        }
      }

      .reply-input {
        margin-top: 12px;
      }
    }
  }
}

/* 笔记工作区样式 */
.notes-workspace {
  display: flex;
  height: calc(100vh - 200px);
  gap: 20px;

  .document-preview {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    overflow: hidden;
    background: #f8fafc;

    .preview-header {
      background: white;
      padding: 16px 20px;
      border-bottom: 1px solid #e5e7eb;
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        font-size: 16px;
        color: #374151;
      }
    }

    .preview-content {
      flex: 1;
      overflow: auto;
      padding: 40px;
      position: relative;

      .document-page {
        background: white;
        padding: 60px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        max-width: 800px;
        margin: 0 auto;
        position: relative;
        transform-origin: top center;
        transition: transform 0.3s;

        .page-content {
          h2 {
            color: #1f2937;
            font-size: 28px;
            margin: 0 0 20px 0;
            border-bottom: 3px solid #667eea;
            padding-bottom: 12px;
          }

          h3 {
            color: #374151;
            font-size: 20px;
            margin: 24px 0 12px 0;
          }

          p {
            line-height: 1.8;
            color: #4b5563;
            margin: 12px 0;
            font-size: 15px;
          }

          ul {
            line-height: 2;
            color: #4b5563;
            padding-left: 24px;

            li {
              margin: 8px 0;
            }
          }

          .selectable-text {
            cursor: text;
            user-select: text;

            &::selection {
              background: rgba(102, 126, 234, 0.3);
            }
          }
        }

        .annotations-layer {
          position: absolute;
          top: 60px;
          left: 60px;
          right: 60px;
          bottom: 60px;
          pointer-events: none;
          z-index: 10;

          .annotation-mark {
            position: absolute;
            pointer-events: auto;
            cursor: pointer;
            border-radius: 2px;
            transition: all 0.2s;
            mix-blend-mode: multiply;

            &:hover {
              opacity: 0.8;
              box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
            }

            .annotation-content {
              width: 100%;
              height: 100%;
            }
          }
        }
      }
    }
  }

  .notes-panel {
    width: 400px;
    display: flex;
    flex-direction: column;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    background: white;
    overflow: hidden;

    .panel-header {
      padding: 16px 20px;
      border-bottom: 1px solid #e5e7eb;
      background: linear-gradient(135deg, #f8fafc, #ffffff);

      h3 {
        margin: 0 0 12px 0;
        font-size: 16px;
        color: #374151;
      }
    }

    .color-picker {
      padding: 12px 20px;
      border-bottom: 1px solid #e5e7eb;
      display: flex;
      align-items: center;
      gap: 12px;
      background: #f8fafc;

      span {
        font-size: 13px;
        color: #6b7280;
        white-space: nowrap;
      }

      .color-options {
        display: flex;
        gap: 8px;

        .color-option {
          width: 32px;
          height: 32px;
          border-radius: 6px;
          cursor: pointer;
          border: 2px solid transparent;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.2s;

          &:hover {
            transform: scale(1.1);
          }

          &.active {
            border-color: #667eea;
            box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);

            .el-icon {
              color: white;
              font-size: 16px;
              font-weight: bold;
            }
          }
        }
      }
    }

    .notes-list {
      flex: 1;
      overflow-y: auto;
      padding: 16px;

      .empty-notes {
        padding: 40px 20px;
      }

      .note-item {
        background: #f8fafc;
        border-left: 4px solid #667eea;
        border-radius: 8px;
        padding: 12px;
        margin-bottom: 12px;
        transition: all 0.2s;

        &:hover {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transform: translateX(4px);
        }

        .note-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .note-time {
            font-size: 11px;
            color: #9ca3af;
          }
        }

        .note-content {
          .note-text {
            font-size: 13px;
            color: #374151;
            line-height: 1.6;
            margin-bottom: 8px;
            font-weight: 500;
          }

          .note-comment {
            font-size: 12px;
            color: #6b7280;
            line-height: 1.5;
            padding: 8px;
            background: white;
            border-radius: 6px;
            display: flex;
            gap: 6px;

            .el-icon {
              flex-shrink: 0;
              margin-top: 2px;
              color: #667eea;
            }
          }
        }

        .note-actions {
          margin-top: 8px;
          display: flex;
          gap: 8px;
          justify-content: flex-end;
        }
      }
    }

    .text-notes {
      padding: 16px;
      border-top: 1px solid #e5e7eb;
      background: #f8fafc;

      h4 {
        margin: 0 0 12px 0;
        font-size: 14px;
        color: #374151;
      }
    }
  }
}

.notes-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafc;
  border-top: 1px solid #e5e7eb;

  .footer-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #6b7280;
    font-size: 14px;

    .el-icon {
      color: #667eea;
    }
  }

  .footer-actions {
    display: flex;
    gap: 12px;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@media (max-width: 768px) {
  .course-header {
    flex-direction: column;
    gap: 20px;

    .header-left {
      width: 100%;
    }
  }

  .exam-list {
    grid-template-columns: 1fr !important;
  }

  .notes-workspace {
    flex-direction: column;
    height: auto;

    .notes-panel {
      width: 100%;
    }
  }
}
</style>

