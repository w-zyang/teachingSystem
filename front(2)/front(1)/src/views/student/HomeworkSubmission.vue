<template>
  <div class="homework-submission">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>📝 大作业提交与查看</h1>
        <p>提交作业并查看AI批阅结果</p>
      </div>
    </div>

    <!-- 功能标签页 -->
    <el-tabs v-model="activeTab" type="border-card" class="homework-tabs">
      <el-tab-pane label="提交作业" name="submit">
        <div class="submit-section">
          <el-card class="submit-card">
            <template #header>
              <h3>提交新作业</h3>
            </template>

            <el-form :model="submitForm" label-width="120px" class="submit-form">
              <el-form-item label="选择课程">
                <el-select 
                  v-model="submitForm.courseId" 
                  placeholder="请选择课程" 
                  style="width: 100%"
                  @change="onCourseChange"
                >
                  <el-option
                    v-for="course in courseList"
                    :key="course.id"
                    :label="course.name"
                    :value="course.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="作业标题">
                <el-input
                  v-model="submitForm.title"
                  placeholder="请输入作业标题"
                />
              </el-form-item>

              <el-form-item label="作业内容">
                <el-input
                  v-model="submitForm.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入作业内容或说明"
                />
              </el-form-item>

              <el-form-item label="上传文件">
                <el-upload
                  class="upload-demo"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :limit="1"
                  accept=".doc,.docx,.pdf,.txt,.zip,.rar"
                >
                  <el-button type="primary">选择文件</el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      支持 Word、PDF、TXT、ZIP、RAR 格式，单个文件不超过10MB
                    </div>
                  </template>
                </el-upload>
                <div v-if="submitForm.fileName" class="file-info">
                  已选择：{{ submitForm.fileName }}
                </div>
              </el-form-item>
            </el-form>

            <div class="submit-actions">
              <el-button 
                type="primary" 
                size="large"
                @click="handleSubmit"
                :loading="submitting"
                :disabled="!canSubmit"
              >
                提交作业
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的作业" name="list">
        <div class="homework-list-section">
          <div v-if="homeworkList.length === 0" class="empty-state">
            <p>暂无作业记录</p>
          </div>
          <div v-else class="homework-list">
            <el-card 
              v-for="homework in homeworkList" 
              :key="homework.id" 
              class="homework-item"
              shadow="hover"
            >
              <div class="homework-header">
                <h3>{{ homework.homeworkTitle }}</h3>
                <el-tag :type="getStatusType(homework.status)">
                  {{ getStatusText(homework.status) }}
                </el-tag>
              </div>

              <div class="homework-info">
                <div class="info-item">
                  <span class="label">课程：</span>
                  <span class="value">{{ homework.courseName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">提交时间：</span>
                  <span class="value">{{ formatTime(homework.submitTime) }}</span>
                </div>
                <div class="info-item" v-if="homework.aiScore">
                  <span class="label">AI评分：</span>
                  <span class="value score">{{ homework.aiScore }}分</span>
                </div>
                <div class="info-item" v-if="homework.teacherScore">
                  <span class="label">教师评分：</span>
                  <span class="value score">{{ homework.teacherScore }}分</span>
                </div>
              </div>

              <div class="homework-actions">
                <el-button type="primary" size="small" @click="viewDetail(homework)">
                  查看详情
                </el-button>
                <el-button 
                  v-if="homework.status === 'submitted'" 
                  type="success" 
                  size="small"
                  @click="requestAIGrading(homework.id)"
                  :loading="homework.grading"
                >
                  请求AI批阅
                </el-button>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 作业详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      title="作业详情"
      width="70%"
      :close-on-click-modal="false"
    >
      <div v-if="currentHomework" class="homework-detail">
        <div class="detail-section">
          <h4>作业信息</h4>
          <div class="detail-content">
            <p><strong>标题：</strong>{{ currentHomework.homeworkTitle }}</p>
            <p><strong>课程：</strong>{{ currentHomework.courseName }}</p>
            <p><strong>提交时间：</strong>{{ formatTime(currentHomework.submitTime) }}</p>
            <p><strong>状态：</strong>
              <el-tag :type="getStatusType(currentHomework.status)">
                {{ getStatusText(currentHomework.status) }}
              </el-tag>
            </p>
          </div>
        </div>

        <div class="detail-section" v-if="currentHomework.content">
          <h4>作业内容</h4>
          <div class="detail-content">
            <p>{{ currentHomework.content }}</p>
          </div>
        </div>

        <div class="detail-section" v-if="currentHomework.fileUrl">
          <h4>提交文件</h4>
          <div class="detail-content">
            <a :href="currentHomework.fileUrl" target="_blank" class="file-link">
              <el-icon><Document /></el-icon>
              {{ currentHomework.fileName }}
            </a>
          </div>
        </div>

        <div class="detail-section" v-if="currentHomework.aiScore">
          <h4>AI评分</h4>
          <div class="detail-content score-section">
            <div class="score-display">{{ currentHomework.aiScore }}分</div>
            <div class="feedback-content">
              <strong>AI评语：</strong>
              <p>{{ currentHomework.aiFeedback }}</p>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="currentHomework.teacherScore">
          <h4>教师评分</h4>
          <div class="detail-content score-section">
            <div class="score-display">{{ currentHomework.teacherScore }}分</div>
            <div class="feedback-content" v-if="currentHomework.teacherFeedback">
              <strong>教师评语：</strong>
              <p>{{ currentHomework.teacherFeedback }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 提交进度条 -->
    <el-dialog
      v-model="showProgress"
      title="正在提交作业"
      width="500px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="progress-content">
        <el-progress :percentage="progressValue" :status="progressStatus" />
        <p class="progress-message">{{ progressMessage }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import * as smartTeachingApi from '@/api/smartTeaching'

const activeTab = ref('submit')
const courseList = ref([])
const homeworkList = ref([])
const submitting = ref(false)
const showDetailDialog = ref(false)
const currentHomework = ref(null)
const showProgress = ref(false)
const progressValue = ref(0)
const progressStatus = ref('')
const progressMessage = ref('')

const submitForm = ref({
  courseId: null,
  title: '',
  content: '',
  fileName: '',
  file: null
})

const studentId = localStorage.getItem('userId') || '1'

// 计算是否可以提交
const canSubmit = computed(() => {
  return submitForm.value.courseId && 
         submitForm.value.title && 
         submitForm.value.content && 
         submitForm.value.file
})

// 加载课程列表
const loadCourses = async () => {
  try {
    // 这里应该调用API获取学生的课程列表
    // const res = await getStudentCourses(studentId)
    // courseList.value = res.data
    
    // 临时使用模拟数据
    courseList.value = [
      { id: 1, name: '数据结构' },
      { id: 2, name: 'Java程序设计' },
      { id: 3, name: '操作系统' }
    ]
  } catch (error) {
    console.error('加载课程列表失败', error)
  }
}

// 课程改变
const onCourseChange = (courseId) => {
  const course = courseList.value.find(c => c.id === courseId)
  if (course) {
    ElMessage.success(`已选择课程：${course.name}`)
  }
}

// 处理文件选择
const handleFileChange = (file) => {
  submitForm.value.fileName = file.name
  submitForm.value.file = file.raw
}

// 提交作业
const handleSubmit = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请完善作业信息')
    return
  }

  submitting.value = true
  showProgress.value = true
  progressValue.value = 0
  progressStatus.value = ''
  progressMessage.value = '正在上传文件...'

  try {
    // 上传文件
    progressValue.value = 30
    const formData = new FormData()
    formData.append('file', submitForm.value.file)

    const uploadRes = await fetch('http://localhost:8080/upload', {
      method: 'POST',
      body: formData
    })

    if (!uploadRes.ok) {
      throw new Error('文件上传失败')
    }

    const uploadData = await uploadRes.json()
    const fileUrl = uploadData.data || uploadData.data?.url

    if (!fileUrl) {
      throw new Error('获取文件URL失败')
    }

    // 提交作业
    progressValue.value = 60
    progressMessage.value = '正在保存作业记录...'

    const course = courseList.value.find(c => c.id === submitForm.value.courseId)
    
    const res = await smartTeachingApi.submitHomework({
      studentId: studentId,
      courseId: submitForm.value.courseId,
      courseName: course.name,
      homeworkTitle: submitForm.value.title,
      content: submitForm.value.content,
      fileUrl: fileUrl,
      fileName: submitForm.value.fileName
    })

    if (res.code === 1 || res.success === true) {
      progressValue.value = 100
      progressStatus.value = 'success'
      progressMessage.value = '作业提交成功！'

      setTimeout(() => {
        showProgress.value = false
        ElMessage.success('作业提交成功')
        resetForm()
        activeTab.value = 'list'
        loadHomeworkList()
      }, 1500)
    } else {
      throw new Error('提交失败')
    }
  } catch (error) {
    progressStatus.value = 'exception'
    progressMessage.value = '提交失败：' + (error.message || '未知错误')
    console.error('提交失败:', error)
    
    setTimeout(() => {
      showProgress.value = false
      ElMessage.error('提交失败：' + (error.message || '未知错误'))
    }, 2000)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  submitForm.value = {
    courseId: null,
    title: '',
    content: '',
    fileName: '',
    file: null
  }
}

// 加载作业列表
const loadHomeworkList = async () => {
  try {
    const res = await smartTeachingApi.getStudentHomeworkList(studentId)
    if (res.code === 1 || res.success === true) {
      homeworkList.value = res.data || []
    }
  } catch (error) {
    console.error('加载作业列表失败', error)
  }
}

// 查看详情
const viewDetail = (homework) => {
  currentHomework.value = homework
  showDetailDialog.value = true
}

// 请求AI批阅
const requestAIGrading = async (homeworkId) => {
  try {
    const homework = homeworkList.value.find(h => h.id === homeworkId)
    if (homework) {
      homework.grading = true
    }

    ElMessage.info('正在请求AI批阅...')
    
    const res = await smartTeachingApi.gradeHomework(homeworkId)
    
    if (res.code === 1 || res.success === true) {
      ElMessage.success('AI批阅完成')
      loadHomeworkList()
    } else {
      throw new Error('批阅失败')
    }
  } catch (error) {
    console.error('AI批阅失败:', error)
    ElMessage.error('AI批阅失败')
  } finally {
    const homework = homeworkList.value.find(h => h.id === homeworkId)
    if (homework) {
      homework.grading = false
    }
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'submitted': 'warning',
    'ai_graded': 'info',
    'teacher_reviewed': 'success',
    'completed': 'success'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'submitted': '待批阅',
    'ai_graded': 'AI已批阅',
    'teacher_reviewed': '教师已审阅',
    'completed': '已完成'
  }
  return map[status] || status
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadCourses()
  loadHomeworkList()
})
</script>

<style scoped>
.homework-submission {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  color: white;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content h1 {
  font-size: 32px;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.header-content p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.homework-tabs {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.submit-section {
  padding: 20px;
}

.submit-card {
  max-width: 800px;
  margin: 0 auto;
}

.submit-form {
  margin-top: 20px;
}

.file-info {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  color: #1890ff;
  font-size: 14px;
}

.submit-actions {
  text-align: center;
  margin-top: 30px;
}

.homework-list-section {
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.homework-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.homework-item {
  transition: all 0.3s ease;
}

.homework-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.homework-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.homework-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.homework-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #666;
  min-width: 80px;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.info-item .value.score {
  color: #667eea;
  font-weight: 600;
  font-size: 16px;
}

.homework-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.homework-detail {
  .detail-section {
    margin-bottom: 24px;
    
    h4 {
      margin: 0 0 12px 0;
      font-size: 16px;
      color: #333;
      border-bottom: 2px solid #e9ecef;
      padding-bottom: 8px;
    }
    
    .detail-content {
      line-height: 1.6;
      color: #666;
      
      p {
        margin: 8px 0;
      }
    }
    
    .file-link {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 8px 16px;
      background: #667eea;
      color: white;
      text-decoration: none;
      border-radius: 6px;
      transition: all 0.3s ease;
      
      &:hover {
        background: #5568d3;
        transform: translateY(-2px);
      }
    }
    
    .score-section {
      .score-display {
        font-size: 48px;
        font-weight: 700;
        color: #667eea;
        text-align: center;
        margin-bottom: 16px;
      }
      
      .feedback-content {
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        border-left: 4px solid #667eea;
        
        strong {
          color: #333;
          display: block;
          margin-bottom: 8px;
        }
        
        p {
          margin: 0;
          line-height: 1.8;
        }
      }
    }
  }
}

.progress-content {
  padding: 20px;
  
  .progress-message {
    text-align: center;
    margin-top: 16px;
    color: #666;
    font-size: 14px;
  }
}
</style>


