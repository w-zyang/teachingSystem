<template>
  <div class="experiment-guide-section">
    <!-- 生成实验指导书 -->
    <el-card class="generate-card">
      <template #header>
        <div class="card-header">
          <h2>🔬 生成实验指导书</h2>
          <el-tag type="success">AI智能生成</el-tag>
        </div>
      </template>

      <el-form :model="form" label-width="120px" class="guide-form">
        <el-form-item label="选择课程">
          <el-select 
            v-model="form.courseId" 
            placeholder="请选择课程" 
            style="width: 100%"
            @change="onCourseChange"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            >
              <span style="float: left">{{ course.name }}</span>
              <span style="float: right; color: #8cc5ff; font-size: 13px">{{ course.subject }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="选择章节">
          <el-select 
            v-model="form.chapterId" 
            placeholder="请先选择课程" 
            style="width: 100%"
            :disabled="!form.courseId || chapterList.length === 0"
            @change="onChapterChange"
          >
            <el-option
              v-for="chapter in chapterList"
              :key="chapter.id"
              :label="chapter.title"
              :value="chapter.id"
            />
          </el-select>
          <div class="tips" style="margin-top: 8px;" v-if="form.courseId && chapterList.length === 0">
            <el-text type="warning" size="small">
              ⚠️ 该课程暂无章节，请先在课程管理中添加章节
            </el-text>
          </div>
        </el-form-item>

        <el-form-item label="知识点选择">
          <div v-if="knowledgePointsList.length === 0" class="tips">
            <el-text type="warning" size="small">
              ⚠️ 请先选择章节以加载知识点
            </el-text>
          </div>
          <div v-else class="knowledge-points-grid">
            <el-card
              v-for="point in knowledgePointsList"
              :key="point.id"
              :class="{ 'is-selected': point.selected }"
              class="knowledge-point-card"
              @click="toggleKnowledgePoint(point.id)"
              shadow="hover"
            >
              <div class="point-content">
                <div class="point-header">
                  <span class="point-name">{{ point.name }}</span>
                  <el-icon v-if="point.selected" color="#67C23A" :size="20">
                    <CircleCheck />
                  </el-icon>
                </div>
                <div class="point-description">{{ point.description }}</div>
              </div>
            </el-card>
          </div>
          <div class="tips" style="margin-top: 12px;">
            <el-text type="info" size="small">
              💡 已选择 {{ selectedKnowledgePointsCount }} 个知识点
            </el-text>
          </div>
        </el-form-item>
      </el-form>

      <div class="generate-actions">
        <el-button 
          type="primary" 
          size="large"
          @click="handleGenerate"
          :loading="generating"
          :disabled="!canGenerate"
        >
          <el-icon><Document /></el-icon>
          生成实验指导书
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </div>
    </el-card>

    <!-- 实验指导书列表 -->
    <el-card v-if="experimentList.length > 0" class="list-card">
      <template #header>
        <h3>📚 我的实验指导书</h3>
      </template>

      <div class="experiment-list">
        <div v-for="item in experimentList" :key="item.id" class="experiment-item">
          <div class="experiment-info">
            <h4>{{ item.chapterName }}</h4>
            <p>难度：{{ getDifficultyText(item.difficultyLevel) }}</p>
            <span class="experiment-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="experiment-actions">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <el-button v-if="item.status === 'completed'" type="primary" size="small" @click="viewExperiment(item.id)">
              查看
            </el-button>
            <el-button type="danger" size="small" @click="deleteExperiment(item.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 实验指导书查看弹窗 -->
    <ExperimentViewDialog
      v-if="showExperimentView"
      :guideId="currentExperimentId"
      @close="showExperimentView = false"
    />

    <!-- AI生成进度条 -->
    <AIGenerationProgress
      :visible="showProgress"
      title="AI 正在生成实验指导书"
      :progress="progressValue"
      :current-step="currentStep"
      :steps="progressSteps"
      :message="progressMessage"
      tip="💡 提示：生成时间取决于实验复杂度，通常需要1-2分钟"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, Document } from '@element-plus/icons-vue'
import * as smartTeachingApi from '@/api/smartTeaching'
import { getCoursesByTeacherId } from '@/api/course'
import ExperimentViewDialog from './ExperimentViewDialog.vue'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'

const courseList = ref([])
const chapterList = ref([])
const knowledgePointsList = ref([])
const experimentList = ref([])
const generating = ref(false)
const showExperimentView = ref(false)
const currentExperimentId = ref(null)

const form = ref({
  courseId: null,
  chapterId: null,
  chapterName: ''
})

// 进度条相关
const showProgress = ref(false)
const progressValue = ref(0)
const currentStep = ref(0)
const progressMessage = ref('')
const progressSteps = ref([
  { title: '准备数据', desc: '正在准备实验生成所需的数据...' },
  { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
  { title: '生成实验内容', desc: 'AI正在生成实验指导书，请耐心等待...' },
  { title: '完成', desc: '实验指导书生成完成！' }
])

// 知识点数据映射（简化版，实际应该从后端获取）
const chapterKnowledgePointsMap = {
  'ch01': [
    { id: 101, name: '顺序表', description: '顺序表的定义、操作与实现', selected: false },
    { id: 102, name: '链表', description: '单链表、双链表、循环链表', selected: false },
    { id: 103, name: '线性表应用', description: '线性表的实际应用场景', selected: false }
  ],
  'ch02': [
    { id: 201, name: '栈的基本操作', description: '栈的定义、入栈、出栈操作', selected: false },
    { id: 202, name: '队列的基本操作', description: '队列的定义、入队、出队操作', selected: false },
    { id: 203, name: '栈的应用', description: '表达式求值、括号匹配等', selected: false }
  ]
}

// 计算已选择的知识点数量
const selectedKnowledgePointsCount = computed(() => {
  return knowledgePointsList.value.filter(p => p.selected).length
})

// 计算是否可以生成
const canGenerate = computed(() => {
  return form.value.courseId && form.value.chapterId && selectedKnowledgePointsCount.value > 0
})

// 加载课程列表
const loadCourses = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    const res = await getCoursesByTeacherId(teacherId)
    
    let courses = []
    if (res.data && res.data.data && Array.isArray(res.data.data)) {
      courses = res.data.data
    } else if (res.data && Array.isArray(res.data)) {
      courses = res.data
    }
    
    courseList.value = courses
    
    if (courseList.value.length === 0) {
      ElMessage.warning('您还没有任何课程')
    }
  } catch (error) {
    console.error('加载课程列表失败', error)
    ElMessage.error('加载课程列表失败')
  }
}

// 课程改变
const onCourseChange = async (courseId) => {
  form.value.chapterId = null
  form.value.chapterName = ''
  chapterList.value = []
  knowledgePointsList.value = []
  
  try {
    const res = await smartTeachingApi.getChaptersByCourseId(courseId)
    if (res.code === 1 || res.success === true) {
      chapterList.value = res.data || []
    }
  } catch (error) {
    console.error('加载章节列表失败', error)
  }
}

// 章节改变
const onChapterChange = (chapterId) => {
  const chapter = chapterList.value.find(c => c.id === chapterId)
  if (chapter) {
    form.value.chapterName = chapter.title
    loadKnowledgePoints(chapter)
  }
}

// 加载知识点
const loadKnowledgePoints = (chapter) => {
  // 简化版：使用默认知识点
  knowledgePointsList.value = [
    { id: 1, name: '基础概念', description: '课程基础概念和定义', selected: false },
    { id: 2, name: '核心原理', description: '课程核心原理和理论', selected: false },
    { id: 3, name: '应用实践', description: '实际应用和案例分析', selected: false },
    { id: 4, name: '综合运用', description: '综合运用和问题解决', selected: false }
  ]
}

// 切换知识点选择
const toggleKnowledgePoint = (id) => {
  const point = knowledgePointsList.value.find(p => p.id === id)
  if (point) {
    point.selected = !point.selected
  }
}

// 生成实验指导书
const handleGenerate = async () => {
  if (!canGenerate.value) {
    ElMessage.warning('请完善表单信息')
    return
  }

  generating.value = true
  showProgress.value = true
  progressValue.value = 0
  currentStep.value = 0
  progressMessage.value = '正在准备数据...'

  try {
    progressValue.value = 10
    await new Promise(resolve => setTimeout(resolve, 300))

    const teacherId = localStorage.getItem('userId') || '2'
    const course = courseList.value.find(c => c.id === form.value.courseId)
    const chapterName = `${course.name} - ${form.value.chapterName}`
    const selectedPoints = knowledgePointsList.value.filter(p => p.selected).map(p => p.name)

    currentStep.value = 1
    progressValue.value = 25
    progressMessage.value = '正在连接AI服务...'
    await new Promise(resolve => setTimeout(resolve, 500))

    currentStep.value = 2
    progressValue.value = 40
    progressMessage.value = 'AI正在生成实验指导书...'

    const submitRes = await smartTeachingApi.generateExperimentGuide({
      teacherId,
      courseId: form.value.courseId,
      chapterName: chapterName,
      knowledgePoints: selectedPoints,
      difficultyLevel: 'medium'
    })

    const isSuccess = (submitRes.code === 1) || (submitRes.success === true)
    if (!isSuccess || !submitRes.data) {
      throw new Error('提交失败')
    }

    const guideId = submitRes.data

    // 轮询检查状态
    let checkCount = 0
    const maxChecks = 60

    const checkStatus = async () => {
      checkCount++
      if (checkCount > maxChecks) {
        throw new Error('生成超时')
      }

      const statusRes = await smartTeachingApi.getExperimentGuide(guideId)
      const isSuccess = (statusRes.code === 1) || (statusRes.success === true)

      if (isSuccess && statusRes.data) {
        const status = statusRes.data.status

        if (status === 'completed') {
          progressValue.value = 95
          return statusRes.data
        } else if (status === 'failed') {
          throw new Error('生成失败')
        } else {
          progressValue.value = Math.min(40 + checkCount * 0.7, 90)
          await new Promise(resolve => setTimeout(resolve, 2000))
          return await checkStatus()
        }
      } else {
        throw new Error('查询状态失败')
      }
    }

    await checkStatus()

    currentStep.value = 3
    progressValue.value = 100
    progressMessage.value = '生成完成！'

    await new Promise(resolve => setTimeout(resolve, 500))
    showProgress.value = false

    ElMessage.success('实验指导书生成成功！')
    loadExperimentList()
    resetForm()
  } catch (error) {
    showProgress.value = false
    console.error('生成失败:', error)
    ElMessage.error('生成失败：' + (error.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    courseId: null,
    chapterId: null,
    chapterName: ''
  }
  knowledgePointsList.value = []
}

// 加载实验指导书列表
const loadExperimentList = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    const res = await smartTeachingApi.getExperimentGuides(teacherId)
    if (res.code === 1 || res.success === true) {
      experimentList.value = res.data || []
    }
  } catch (error) {
    console.error('加载实验指导书列表失败', error)
  }
}

// 查看实验指导书
const viewExperiment = (id) => {
  currentExperimentId.value = id
  showExperimentView.value = true
}

// 删除实验指导书
const deleteExperiment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条实验指导书吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await smartTeachingApi.deleteExperimentGuide(id)
    if (res.code === 1 || res.success === true) {
      ElMessage.success('删除成功')
      loadExperimentList()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'generating': '生成中',
    'completed': '已完成',
    'failed': '失败'
  }
  return map[status] || status
}

// 获取难度文本
const getDifficultyText = (level) => {
  const map = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return map[level] || level
}

onMounted(() => {
  loadCourses()
  loadExperimentList()
})
</script>

<style scoped>
.experiment-guide-section {
  padding: 20px;
}

.generate-card,
.list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.guide-form {
  margin-bottom: 30px;
}

.knowledge-points-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.knowledge-point-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e2e8f0;
}

.knowledge-point-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.knowledge-point-card.is-selected {
  border-color: #67C23A;
  background: #f0f9ff;
}

.point-content {
  padding: 4px;
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.point-name {
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
}

.point-description {
  font-size: 12px;
  color: #718096;
  line-height: 1.5;
}

.generate-actions {
  text-align: center;
  margin-top: 30px;
}

.experiment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.experiment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.experiment-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.experiment-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.experiment-info p {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #666;
}

.experiment-time {
  font-size: 12px;
  color: #999;
}

.experiment-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.generating {
  background: #fff3cd;
  color: #856404;
}

.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.failed {
  background: #f8d7da;
  color: #721c24;
}

.tips {
  margin-top: 8px;
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>










