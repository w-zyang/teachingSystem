<template>
  <el-dialog
    v-model="visible"
    title="更新课件案例"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="选择课程">
        <el-select 
          v-model="form.courseId" 
          placeholder="请选择课程" 
          style="width: 100%"
          :teleported="false"
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
      <el-form-item label="课件文件">
        <el-upload
          class="upload-demo"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept=".ppt,.pptx"
        >
          <el-button type="primary">选择PPT文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              已选择：{{ form.fileName || '未选择文件' }}
            </div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item label="更新要求">
        <el-input
          v-model="form.updateRequirements"
          type="textarea"
          :rows="5"
          placeholder="请描述更新要求，例如：&#10;1. 更新2024-2025年最新的网络安全事件案例&#10;2. 补充AI安全相关的前沿知识&#10;3. 更新数据统计图表"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
    </template>
  </el-dialog>

  <!-- AI分析进度条 -->
  <AIGenerationProgress
    :visible="showProgress"
    title="AI 正在分析课件"
    :progress="progressValue"
    :current-step="currentStep"
    :steps="progressSteps"
    :message="progressMessage"
    tip="💡 提示：AI会分析课件内容并搜索最新案例，通常需要1-2分钟"
  />
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import * as smartTeachingApi from '@/api/smartTeaching'
import { getCoursesByTeacherId } from '@/api/course'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const submitting = ref(false)
const courseList = ref([])
const form = ref({
  courseId: null,
  fileName: '',
  fileUrl: '',
  updateRequirements: '',
  file: null // 保存文件对象
})

// 进度条相关
const showProgress = ref(false)
const progressValue = ref(0)
const currentStep = ref(0)
const progressMessage = ref('')
const progressSteps = ref([
  { title: '准备数据', desc: '正在准备课件分析所需的数据...' },
  { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
  { title: '分析课件内容', desc: 'AI正在分析课件并搜索最新案例...' },
  { title: '完成', desc: '分析完成！' }
])

// 加载教师的课程列表
const loadCourses = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    console.log('正在加载课程，教师ID:', teacherId)
    const res = await getCoursesByTeacherId(teacherId)
    console.log('课程API响应:', res)
    
    // 处理响应数据
    // 响应格式: {data: {success: true, msg: '...', data: Array(5)}}
    let courses = []
    if (res.data && res.data.data && Array.isArray(res.data.data)) {
      courses = res.data.data
    } else if (res.data && Array.isArray(res.data)) {
      courses = res.data
    }
    
    courseList.value = [...courses]
    console.log('课程列表:', courseList.value)
    
    if (courseList.value.length === 0) {
      ElMessage.warning('您还没有任何课程')
    } else {
      ElMessage.success(`已加载 ${courseList.value.length} 门课程`)
    }
  } catch (error) {
    console.error('加载课程列表失败', error)
    ElMessage.error('加载课程列表失败：' + error.message)
  }
}

// 课程改变时
const onCourseChange = (courseId) => {
  const course = courseList.value.find(c => c.id === courseId)
  if (course) {
    ElMessage.success(`已选择课程：${course.name}`)
  }
}

// 处理文件选择
const handleFileChange = (file) => {
  form.value.fileName = file.name
  form.value.file = file.raw // 保存原始文件对象
  // 不再使用Blob URL，而是在提交时上传
}

const handleClose = () => {
  emit('close')
}

const handleSubmit = async () => {
  if (!form.value.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!form.value.file) {
    ElMessage.warning('请选择PPT文件')
    return
  }
  if (!form.value.updateRequirements) {
    ElMessage.warning('请填写更新要求')
    return
  }

  submitting.value = true
  showProgress.value = true
  progressValue.value = 0
  currentStep.value = 0
  progressMessage.value = '正在准备数据...'
  
  try {
    // 步骤1: 准备数据
    progressValue.value = 10
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const teacherId = localStorage.getItem('userId') || '2'
    
    // 步骤2: 上传文件到OSS
    currentStep.value = 1
    progressValue.value = 25
    progressMessage.value = '正在上传PPT文件到服务器...'
    
    const formData = new FormData()
    formData.append('file', form.value.file)
    
    // 调用文件上传接口
    const uploadRes = await fetch('http://localhost:8080/upload', {
      method: 'POST',
      body: formData
    })
    
    if (!uploadRes.ok) {
      throw new Error('文件上传失败')
    }
    
    const uploadData = await uploadRes.json()
    console.log('文件上传响应:', uploadData)
    
    // 获取上传后的文件URL
    let fileUrl = ''
    if (uploadData.code === 1 && uploadData.data) {
      fileUrl = uploadData.data
    } else if (uploadData.success && uploadData.data) {
      fileUrl = uploadData.data
    } else if (uploadData.data && uploadData.data.url) {
      fileUrl = uploadData.data.url
    } else {
      console.error('无法解析文件URL，响应数据:', uploadData)
      throw new Error('无法获取文件URL')
    }
    
    console.log('文件上传成功，URL:', fileUrl)
    
    if (!fileUrl || fileUrl.startsWith('blob:')) {
      console.error('文件URL无效:', fileUrl)
      throw new Error('文件URL无效，请重试')
    }
    
    // 步骤3: 调用AI分析
    currentStep.value = 2
    progressValue.value = 40
    progressMessage.value = 'AI正在分析课件并搜索最新案例...'
    
    const res = await smartTeachingApi.updateCourseware({
      teacherId,
      courseId: form.value.courseId,
      fileUrl: fileUrl, // 使用真实的OSS URL
      fileName: form.value.fileName,
      updateRequirements: form.value.updateRequirements
    })
    
    // 正确提取updateId（后端返回的是 {code, msg, data: {updateId, message}}）
    const updateId = res.data?.updateId || res.data?.data?.updateId || res.updateId
    console.log('课件更新任务已提交，ID:', updateId)
    
    if (!updateId || typeof updateId !== 'number') {
      console.error('无效的updateId:', updateId, '完整响应:', res)
      throw new Error('获取更新ID失败')
    }
    
    // 开始轮询检查状态
    const checkStatus = async () => {
      try {
        const statusRes = await smartTeachingApi.getCoursewareSuggestions(updateId)
        console.log('状态检查响应:', statusRes)
        
        const status = statusRes.data?.status || statusRes.status
        
        if (status === 'suggestions_ready') {
          // 分析完成
          currentStep.value = 3
          progressValue.value = 100
          progressMessage.value = '分析完成！'
          
          await new Promise(resolve => setTimeout(resolve, 800))
          showProgress.value = false
          
          ElMessage.success('AI分析完成！')
          emit('success')
        } else if (status === 'failed') {
          // 分析失败
          showProgress.value = false
          ElMessage.error('AI分析失败')
          emit('success') // 仍然刷新列表
        } else {
          // 仍在分析中，继续轮询
          if (progressValue.value < 85) {
            progressValue.value += 2
          }
          setTimeout(checkStatus, 3000) // 3秒后再次检查
        }
      } catch (error) {
        console.error('检查状态失败:', error)
        // 出错后继续尝试，最多尝试30次（90秒）
        if (progressValue.value < 85) {
          setTimeout(checkStatus, 3000)
        } else {
          showProgress.value = false
          ElMessage.warning('分析超时，请稍后查看结果')
          emit('success')
        }
      }
    }
    
    // 开始第一次检查
    setTimeout(checkStatus, 3000)
  } catch (error) {
    showProgress.value = false
    console.error('提交失败:', error)
    ElMessage.error('提交失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.upload-demo {
  width: 100%;
}
</style>
