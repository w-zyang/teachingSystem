<template>
  <div class="experiment-guide-generator">
    <!-- 生成表单 -->
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <h3>📝 实验指导书生成</h3>
        </div>
      </template>

      <el-form :model="form" label-width="120px">
        <el-form-item label="所属课程" required>
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%;" @change="onCourseChange">
            <el-option 
              v-for="course in teacherCourses" 
              :key="course.id" 
              :label="course.name" 
              :value="course.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="章节名称" required>
          <el-input v-model="form.chapterName" placeholder="例如：第一章 Java基础" />
        </el-form-item>

        <el-form-item label="知识点" required>
          <el-select 
            v-model="form.knowledgePoints" 
            multiple 
            filterable 
            allow-create 
            placeholder="请选择或输入知识点"
            style="width: 100%;">
            <el-option label="变量与数据类型" value="变量与数据类型" />
            <el-option label="控制结构" value="控制结构" />
            <el-option label="函数与方法" value="函数与方法" />
            <el-option label="面向对象编程" value="面向对象编程" />
            <el-option label="异常处理" value="异常处理" />
          </el-select>
        </el-form-item>

        <el-form-item label="难度等级">
          <el-radio-group v-model="form.difficultyLevel">
            <el-radio label="beginner">初级</el-radio>
            <el-radio label="intermediate">中级</el-radio>
            <el-radio label="advanced">高级</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="generateGuide" :loading="generating" :disabled="!canGenerate">
            <el-icon><Document /></el-icon>
            生成实验指导书
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成记录列表 -->
    <el-card class="records-card" v-if="records.length > 0">
      <template #header>
        <div class="card-header">
          <h3>📚 生成记录</h3>
        </div>
      </template>

      <div class="records-list">
        <div v-for="record in records" :key="record.id" class="record-item">
          <div class="record-info">
            <h4>{{ record.chapterName }}</h4>
            <p>难度：{{ getDifficultyText(record.difficultyLevel) }}</p>
            <span class="record-time">{{ formatTime(record.createTime) }}</span>
          </div>
          <div class="record-actions">
            <el-tag :type="getStatusType(record.status)">{{ getStatusText(record.status) }}</el-tag>
            <el-button 
              v-if="record.status === 'completed'" 
              type="primary" 
              size="small" 
              @click="viewGuide(record.id)">
              查看详情
            </el-button>
            <el-button type="danger" size="small" @click="deleteRecord(record.id)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog 
      v-model="showDetailDialog" 
      title="实验指导书详情" 
      width="80%" 
      :close-on-click-modal="false">
      <div v-if="currentGuide" class="guide-detail">
        <h2>{{ currentGuide.title }}</h2>

        <div class="section">
          <h3>一、实验目的</h3>
          <ul>
            <li v-for="(obj, index) in parseJSON(currentGuide.objectives)" :key="index">{{ obj }}</li>
          </ul>
        </div>

        <div class="section">
          <h3>二、实验环境</h3>
          <div v-html="formatEnvironment(currentGuide.environment)"></div>
        </div>

        <div class="section">
          <h3>三、实验步骤</h3>
          <ol>
            <li v-for="(step, index) in parseJSON(currentGuide.steps)" :key="index">{{ step }}</li>
          </ol>
        </div>

        <div class="section" v-if="currentGuide.code">
          <h3>四、示例代码</h3>
          <pre><code>{{ currentGuide.code }}</code></pre>
        </div>

        <div class="section">
          <h3>五、预期结果</h3>
          <div v-html="formatResults(currentGuide.expectedResults)"></div>
        </div>

        <div class="section">
          <h3>六、思考题</h3>
          <ol>
            <li v-for="(q, index) in parseJSON(currentGuide.questions)" :key="index">{{ q }}</li>
          </ol>
        </div>

        <div class="section">
          <h3>七、参考答案</h3>
          <ol>
            <li v-for="(a, index) in parseJSON(currentGuide.answers)" :key="index">{{ a }}</li>
          </ol>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="downloadGuide">下载Word</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getCoursesByTeacherId } from '@/api/course'
import * as smartTeachingApi from '@/api/smartTeaching'

// 数据
const teacherCourses = ref([])
const form = reactive({
  courseId: null,
  chapterName: '',
  knowledgePoints: [],
  difficultyLevel: 'intermediate'
})
const generating = ref(false)
const records = ref([])
const showDetailDialog = ref(false)
const currentGuide = ref(null)

// 计算属性
const canGenerate = computed(() => {
  return form.courseId && form.chapterName && form.knowledgePoints.length > 0
})

// 方法
const loadTeacherCourses = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    const res = await getCoursesByTeacherId(teacherId)
    if (res.code === 1) {
      teacherCourses.value = res.data || []
    }
  } catch (error) {
    console.error('加载课程列表失败', error)
  }
}

const onCourseChange = () => {
  // 课程切换时的处理
}

const generateGuide = async () => {
  if (!canGenerate.value) {
    ElMessage.warning('请填写完整信息')
    return
  }

  generating.value = true
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    const res = await smartTeachingApi.generateExperimentGuide({
      teacherId: parseInt(teacherId),
      courseId: form.courseId,
      chapterName: form.chapterName,
      knowledgePoints: form.knowledgePoints,
      difficultyLevel: form.difficultyLevel
    })

    if (res.code === 1 || res.success === true) {
      ElMessage.success('实验指导书生成中，请稍候...')
      
      // 轮询检查状态
      const guideId = res.data
      checkGenerationStatus(guideId)
      
      // 刷新记录列表
      loadRecords()
    } else {
      ElMessage.error('生成失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('生成失败', error)
    ElMessage.error('生成失败：' + (error.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

const checkGenerationStatus = async (guideId) => {
  let attempts = 0
  const maxAttempts = 20
  
  const check = async () => {
    try {
      const res = await smartTeachingApi.getExperimentGuide(guideId)
      if (res.code === 1 && res.data.status === 'completed') {
        ElMessage.success('实验指导书生成完成！')
        loadRecords()
        return
      }
      
      attempts++
      if (attempts < maxAttempts) {
        setTimeout(check, 3000)
      } else {
        ElMessage.warning('生成超时，请稍后查看')
        loadRecords()
      }
    } catch (error) {
      console.error('检查状态失败', error)
    }
  }
  
  setTimeout(check, 3000)
}

const loadRecords = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    const res = await smartTeachingApi.getExperimentGuides(teacherId)
    if (res.code === 1 || res.success === true) {
      records.value = res.data || []
    }
  } catch (error) {
    console.error('加载记录失败', error)
  }
}

const viewGuide = async (guideId) => {
  try {
    const res = await smartTeachingApi.getExperimentGuide(guideId)
    if (res.code === 1 || res.success === true) {
      currentGuide.value = res.data
      showDetailDialog.value = true
    } else {
      ElMessage.error('加载失败')
    }
  } catch (error) {
    console.error('加载详情失败', error)
    ElMessage.error('加载失败')
  }
}

const deleteRecord = async (guideId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await smartTeachingApi.deleteExperimentGuide(guideId)
    if (res.code === 1 || res.success === true) {
      ElMessage.success('删除成功')
      loadRecords()
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

const resetForm = () => {
  form.courseId = null
  form.chapterName = ''
  form.knowledgePoints = []
  form.difficultyLevel = 'intermediate'
}

const downloadGuide = () => {
  ElMessage.info('下载功能开发中...')
}

// 辅助方法
const parseJSON = (str) => {
  try {
    return JSON.parse(str)
  } catch {
    return []
  }
}

const formatEnvironment = (env) => {
  try {
    const envObj = JSON.parse(env)
    return `
      <p><strong>操作系统：</strong>${envObj.os || ''}</p>
      <p><strong>编程语言：</strong>${envObj.language || ''}</p>
      <p><strong>开发环境：</strong>${envObj.ide || ''}</p>
      <p><strong>工具：</strong>${envObj.tools ? envObj.tools.join('、') : ''}</p>
    `
  } catch {
    return `<p>${env}</p>`
  }
}

const formatResults = (results) => {
  try {
    const arr = JSON.parse(results)
    return '<ul>' + arr.map(r => `<li>${r}</li>`).join('') + '</ul>'
  } catch {
    return `<p>${results}</p>`
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const getDifficultyText = (level) => {
  const map = {
    'beginner': '初级',
    'intermediate': '中级',
    'advanced': '高级'
  }
  return map[level] || level
}

const getStatusText = (status) => {
  const map = {
    'generating': '生成中',
    'completed': '已完成',
    'failed': '失败'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    'generating': 'warning',
    'completed': 'success',
    'failed': 'danger'
  }
  return map[status] || 'info'
}

// 生命周期
onMounted(() => {
  loadTeacherCourses()
  loadRecords()
})
</script>

<style lang="scss" scoped>
.experiment-guide-generator {
  padding: 20px;
}

.form-card, .records-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    font-size: 18px;
    color: #303133;
  }
}

.records-list {
  .record-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 12px;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }

    .record-info {
      flex: 1;

      h4 {
        margin: 0 0 8px 0;
        font-size: 16px;
        color: #303133;
      }

      p {
        margin: 4px 0;
        font-size: 14px;
        color: #606266;
      }

      .record-time {
        font-size: 12px;
        color: #909399;
      }
    }

    .record-actions {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }
}

.guide-detail {
  .section {
    margin-bottom: 24px;

    h3 {
      font-size: 16px;
      color: #303133;
      margin-bottom: 12px;
      border-left: 4px solid #409eff;
      padding-left: 12px;
    }

    ul, ol {
      padding-left: 24px;

      li {
        margin-bottom: 8px;
        line-height: 1.6;
      }
    }

    pre {
      background: #f5f7fa;
      padding: 16px;
      border-radius: 4px;
      overflow-x: auto;

      code {
        font-family: 'Courier New', monospace;
        font-size: 14px;
      }
    }
  }
}
</style>









