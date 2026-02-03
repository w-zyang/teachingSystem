<template>
  <el-dialog
    v-model="visible"
    title="提交作业（主观题）"
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
      <el-form-item label="选择学生">
        <el-select 
          v-model="form.studentId" 
          placeholder="请选择学生" 
          filterable
          style="width: 100%"
          :teleported="false"
        >
          <el-option
            v-for="student in studentList"
            :key="student.id"
            :label="`${student.realName || student.username} (${student.username})`"
            :value="student.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="题目内容">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="10"
          placeholder="请输入作业题目内容（论述题、分析题等主观题）&#10;&#10;示例：&#10;请论述栈和队列的区别与联系，并举例说明它们在实际开发中的应用场景。要求：&#10;1. 从数据结构特点分析&#10;2. 从操作方式对比&#10;3. 列举至少2个实际应用案例"
        />
      </el-form-item>
      <el-form-item label="学生作业">
        <el-upload
          class="upload-demo"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept=".doc,.docx,.pdf,.txt"
        >
          <el-button size="small">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              上传学生的作业答案（支持 Word、PDF、TXT 格式）
            </div>
          </template>
        </el-upload>
        <div v-if="form.fileName" class="file-info">
          已选择：{{ form.fileName }}
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
    </template>
  </el-dialog>

  <!-- 提交进度条 -->
  <AIGenerationProgress
    :visible="showProgress"
    title="正在提交作业"
    :progress="progressValue"
    :current-step="currentStep"
    :steps="progressSteps"
    :message="progressMessage"
    tip="💡 提示：作业提交后可以使用AI批阅功能"
  />
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import * as smartTeachingApi from '@/api/smartTeaching'
import { getCoursesByTeacherId } from '@/api/course'
import { getUserList } from '@/api/user'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const submitting = ref(false)
const courseList = ref([])
const studentList = ref([])
const form = ref({
  courseId: null,
  studentId: null,
  homeworkTitle: '',
  content: '',
  fileUrl: '',
  fileName: '',
  file: null // 保存文件对象
})

// 进度条相关
const showProgress = ref(false)
const progressValue = ref(0)
const currentStep = ref(0)
const progressMessage = ref('')
const progressSteps = ref([
  { title: '准备数据', desc: '正在准备作业提交数据...' },
  { title: '上传文件', desc: '正在上传作业文件...' },
  { title: '保存记录', desc: '正在保存作业记录...' },
  { title: '完成', desc: '作业提交完成！' }
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

// 加载学生列表
const loadStudents = async () => {
  try {
    console.log('正在加载学生列表...')
    const res = await getUserList({ roleFilter: 'student', page: 1, size: 1000 })
    console.log('学生API响应:', res)
    
    // 处理响应数据 - 分页格式: {data: {total: 116, records: Array(10)}}
    let students = []
    if (res.data && res.data.records && Array.isArray(res.data.records)) {
      students = res.data.records
    } else if (res.data && res.data.data && Array.isArray(res.data.data)) {
      students = res.data.data
    } else if (res.data && Array.isArray(res.data)) {
      students = res.data
    } else if (res.code === 1 && Array.isArray(res.data)) {
      students = res.data
    }
    
    studentList.value = [...students]
    console.log('学生列表:', studentList.value)
    console.log('学生数量:', studentList.value.length)
    
    if (studentList.value.length === 0) {
      ElMessage.warning('没有找到学生数据')
    } else {
      ElMessage.success(`已加载 ${studentList.value.length} 名学生`)
    }
  } catch (error) {
    console.error('加载学生列表失败', error)
    ElMessage.error('加载学生列表失败：' + error.message)
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
  if (!form.value.studentId) {
    ElMessage.warning('请选择学生')
    return
  }
  if (!form.value.content) {
    ElMessage.warning('请填写题目内容')
    return
  }
  if (!form.value.file) {
    ElMessage.warning('请上传学生作业文件')
    return
  }

  submitting.value = true
  showProgress.value = true
  progressValue.value = 0
  currentStep.value = 0
  progressMessage.value = '正在准备数据...'
  
  try {
    // 步骤1: 准备数据
    progressValue.value = 20
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const teacherId = localStorage.getItem('userId') || '2'
    const course = courseList.value.find(c => c.id === form.value.courseId)
    const student = studentList.value.find(s => s.id === form.value.studentId)
    
    // 自动生成作业标题
    const homeworkTitle = `${course.name} - ${student.realName || student.username}的作业`
    
    // 步骤2: 上传文件到OSS
    currentStep.value = 1
    progressValue.value = 40
    progressMessage.value = '正在上传作业文件到服务器...'
    
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
      // 格式1: {code: 1, data: "url"}
      fileUrl = uploadData.data
    } else if (uploadData.success && uploadData.data) {
      // 格式2: {success: true, data: "url"}
      fileUrl = uploadData.data
    } else if (uploadData.data && uploadData.data.url) {
      // 格式3: {data: {url: "url"}}
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
    
    // 步骤3: 保存记录
    currentStep.value = 2
    progressValue.value = 60
    progressMessage.value = '正在保存作业记录...'
    
    const submitData = {
      teacherId,
      studentId: form.value.studentId,
      courseId: form.value.courseId,
      homeworkTitle: homeworkTitle,
      fileUrl: fileUrl, // 使用真实的OSS URL
      fileName: form.value.fileName,
      content: form.value.content
    }
    
    console.log('准备提交作业数据:', submitData)
    
    await smartTeachingApi.submitHomework(submitData)
    
    // 步骤4: 完成
    currentStep.value = 3
    progressValue.value = 100
    progressMessage.value = '作业提交完成！'
    
    await new Promise(resolve => setTimeout(resolve, 500))
    showProgress.value = false
    
    ElMessage.success('作业提交成功')
    emit('success')
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
  loadStudents()
})
</script>

<style scoped>
.upload-demo {
  width: 100%;
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
</style>
