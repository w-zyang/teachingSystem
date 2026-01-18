<template>
  <div class="create-course">
    <el-card class="create-course-card">
      <template #header>
        <div class="card-header">
          <h2>创建新课程</h2>
          <el-tag type="primary">课程管理</el-tag>
        </div>
      </template>

      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="120px"
        class="course-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input
                v-model="courseForm.name"
                placeholder="请输入课程名称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学科领域" prop="subject">
              <el-select
                v-model="courseForm.subject"
                placeholder="请选择学科领域"
                style="width: 100%"
                clearable
              >
                <el-option label="计算机科学" value="计算机科学" />
                <el-option label="Java编程" value="Java编程" />
                <el-option label="数据库" value="数据库" />
                <el-option label="软件工程" value="软件工程" />
                <el-option label="操作系统" value="操作系统" />
                <el-option label="计算机网络" value="计算机网络" />
                <el-option label="人工智能" value="人工智能" />
                <el-option label="Web开发" value="Web开发" />
                <el-option label="网络安全" value="网络安全" />
                <el-option label="大数据" value="大数据" />
                <el-option label="移动开发" value="移动开发" />
                <el-option label="算法设计" value="算法设计" />
                <el-option label="深度学习" value="深度学习" />
                <el-option label="云计算" value="云计算" />
                <el-option label="物联网" value="物联网" />
                <el-option label="Linux系统" value="Linux系统" />
                <el-option label="Python编程" value="Python编程" />
                <el-option label="软件架构" value="软件架构" />
                <el-option label="区块链" value="区块链" />
                <el-option label="图形学" value="图形学" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述，包括课程内容、学习目标等..."
            clearable
          />
        </el-form-item>

        <el-form-item label="封面图片" prop="image">
          <el-input
            v-model="courseForm.image"
            placeholder="请输入封面图片URL（可选）"
            clearable
          />
          <div class="form-tip">可以稍后上传封面图片</div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="submitForm"
            :loading="submitting"
          >
            {{ submitting ? '创建中...' : '创建课程' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addCourse } from '@/api/course'

const router = useRouter()
const courseFormRef = ref(null)
const submitting = ref(false)

// 从localStorage获取教师ID
const getTeacherId = () => {
  const userId = localStorage.getItem('userId')
  return userId ? parseInt(userId) : 2 // 默认使用ID 2
}

// 表单数据
const courseForm = reactive({
  name: '',
  description: '',
  subject: '',
  image: '',
  teacherId: getTeacherId()
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 100, message: '课程名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择学科领域', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入课程描述', trigger: 'blur' },
    { min: 10, max: 1000, message: '课程描述长度在 10 到 1000 个字符', trigger: 'blur' }
  ]
}

// 提交表单
const submitForm = async () => {
  if (!courseFormRef.value) return

  try {
    await courseFormRef.value.validate()
    
    submitting.value = true
    
    // 确保teacherId已设置
    courseForm.teacherId = getTeacherId()
    
    console.log('提交课程数据:', courseForm)
    
    const response = await addCourse(courseForm)
    
    console.log('创建课程响应:', response)
    
    if (response && (response.success === true || response.code === 200)) {
      ElMessage.success('课程创建成功！')
      
      // 询问是否跳转到AI备课助手
      ElMessageBox.confirm(
        '课程创建成功！是否立即使用AI备课助手设计课程内容？',
        '提示',
        {
          confirmButtonText: '前往AI备课',
          cancelButtonText: '稍后再说',
          type: 'success'
        }
      ).then(() => {
        // 跳转到AI备课助手，并传递课程ID（如果返回了）
        router.push({
          path: '/teacher/ai-course-design',
          query: { courseId: response.data?.id || courseForm.name }
        })
      }).catch(() => {
        // 返回课程列表或工作台
        router.push('/teacher/dashboard')
      })
    } else {
      throw new Error(response?.msg || '创建课程失败')
    }
  } catch (error) {
    console.error('创建课程失败:', error)
    if (error !== 'cancel') {
      ElMessage.error(error.message || '创建课程失败，请检查网络连接或稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (courseFormRef.value) {
    courseFormRef.value.resetFields()
  }
  Object.assign(courseForm, {
    name: '',
    description: '',
    subject: '',
    image: '',
    teacherId: getTeacherId()
  })
}

onMounted(() => {
  // 确保teacherId已设置
  courseForm.teacherId = getTeacherId()
})
</script>

<style lang="scss" scoped>
.create-course {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.create-course-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 600;
      color: #2d3748;
    }
  }
}

.course-form {
  margin-top: 20px;

  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
  }

  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    border-radius: 6px;
  }
}

@media (max-width: 768px) {
  .create-course {
    padding: 10px;
  }

  .course-form {
    :deep(.el-col) {
      margin-bottom: 0;
    }
  }
}
</style>
