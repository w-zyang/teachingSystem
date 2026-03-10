<template>
  <div class="ai-course-design">
    <!-- 课程选择区域 -->
    <el-card class="course-select-card" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <h3>📚 选择要设计的课程</h3>
          <el-tag type="info">请先选择课程</el-tag>
        </div>
      </template>
      
      <div class="course-select-section">
        <el-form :model="courseSelectForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="选择课程">
                <el-select 
                  v-model="courseSelectForm.selectedCourseId" 
                  placeholder="请选择要设计的课程"
                  @change="onCourseSelect"
                  style="width: 100%"
                  size="large"
                >
                  <el-option
                    v-for="course in teacherCourses"
                    :key="course.id"
                    :label="course.name"
                    :value="course.id"
                  >
                    <span style="float: left">{{ course.name }}</span>
                    <span style="float: right; color: #8cc5ff; font-size: 13px">{{ course.subject }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课程信息">
                <div v-if="selectedCourse" class="course-info-display">
                  <p><strong>课程名称：</strong>{{ selectedCourse.name }}</p>
                  <p><strong>学科领域：</strong>{{ selectedCourse.subject }}</p>
                  <p><strong>课程状态：</strong>
                    <el-tag :type="selectedCourse.status === 'published' ? 'success' : 'warning'">
                      {{ selectedCourse.status === 'published' ? '已发布' : '草稿' }}
                    </el-tag>
                  </p>
                </div>
                <div v-else class="no-course-selected">
                  <el-text type="info">请选择一门课程以开始AI设计</el-text>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>
    
    <!-- 功能选择标签页 -->
    <el-card class="function-tabs-card" :class="{ 'disabled-card': !selectedCourse }">
      <el-tabs v-model="activeFunction" type="border-card">
        <el-tab-pane label="课件生成" name="generate">
          <template #label>
            <span><el-icon><Document /></el-icon> 课件生成</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="课件更新" name="update">
          <template #label>
            <span><el-icon><Refresh /></el-icon> 课件更新</span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 课件生成 -->
    <el-card v-if="activeFunction === 'generate'" class="design-card" :class="{ 'disabled-card': !selectedCourse }">
      <template #header>
        <div class="card-header">
          <h2>课件生成</h2>
          <el-tag type="primary">智能备课</el-tag>
        </div>
      </template>
      
      <!-- 文件上传区域 -->
      <div class="upload-section">
        <h3>📁 上传已有文件</h3>
        <el-upload
          ref="uploadRef"
          :before-upload="beforeUpload"
          :file-list="fileList"
          :auto-upload="false"
          multiple
          drag
          accept=".doc,.docx,.pdf,.txt,.md,.ppt,.pptx"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 doc、docx、pdf、txt、md、ppt、pptx 格式文件，单个文件不超过10MB
            </div>
          </template>
        </el-upload>
        <div class="upload-actions">
          <el-button type="primary" @click="submitUpload" :loading="uploading">
            开始上传
          </el-button>
          <el-button @click="clearFiles">清空文件</el-button>
        </div>
      </div>

      <!-- 课程信息输入 -->
      <div class="course-info">
        <h3>📝 课程基本信息</h3>
        <el-form :model="courseForm" label-width="120px" class="course-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="课程名称">
                <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课程类型">
                <el-select v-model="courseForm.courseType" placeholder="请选择课程类型">
                  <el-option label="计算机科学" value="computer-science" />
                  <el-option label="数学" value="mathematics" />
                  <el-option label="物理" value="physics" />
                  <el-option label="化学" value="chemistry" />
                  <el-option label="生物学" value="biology" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="课程时长">
                <el-input-number v-model="courseForm.duration" :min="1" :max="100" placeholder="课时" />
                <span class="unit">课时</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="难度等级">
                <el-select v-model="courseForm.difficulty" placeholder="请选择难度等级">
                  <el-option label="初级" value="beginner" />
                  <el-option label="中级" value="intermediate" />
                  <el-option label="高级" value="advanced" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="课程大纲">
            <el-input
              v-model="courseForm.outline"
              type="textarea"
              :rows="4"
              placeholder="请输入课程大纲，包括主要知识点和学习目标..."
            />
          </el-form-item>
          
          <el-form-item label="特殊要求">
            <el-input
              v-model="courseForm.requirements"
              type="textarea"
              :rows="3"
              placeholder="请输入特殊教学要求或注意事项..."
            />
          </el-form-item>
        </el-form>
      </div>
      
      <!-- AI生成选项 -->
      <div class="ai-options">
        <h3>🤖 AI生成选项</h3>
        <el-checkbox-group v-model="selectedOptions">
          <el-checkbox label="ppt">生成PPT课件</el-checkbox>
          <el-checkbox label="lesson-plan">生成教案</el-checkbox>
          <el-checkbox label="content-design">教学内容设计</el-checkbox>
          <el-checkbox label="practice-exercises">实训练习设计</el-checkbox>
          <el-checkbox label="time-distribution">时间分布规划</el-checkbox>
          <el-checkbox label="exam-questions">考核题目生成</el-checkbox>
          <el-checkbox label="teaching-suggestions">教学建议</el-checkbox>
        </el-checkbox-group>
      </div>
      
      <!-- 生成按钮 -->
      <div class="generate-actions">
        <el-button 
          type="primary" 
          size="large"
          @click="generateCourseDesign"
          :loading="generating"
          :disabled="!canGenerate"
        >
          <el-icon><Edit /></el-icon>
          生成课程设计
        </el-button>
        <el-button @click="resetForm">重置表单</el-button>
      </div>

      <!-- 生成记录列表 -->
      <div v-if="generateRecords && generateRecords.length > 0" class="generate-records">
        <h3>生成记录</h3>
        <div class="record-item" v-for="item in generateRecords" :key="item.id">
          <div class="record-info">
            <h4>{{ item.courseName || '课程设计' }}</h4>
            <p>{{ item.outline || '课程大纲' }}</p>
            <span class="record-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="record-status">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <el-button v-if="item.status === 'completed'" type="primary" size="small" @click="viewGenerateResult(item.id)">
              查看结果
            </el-button>
            <el-button type="danger" size="small" @click="deleteGenerateRecord(item.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 课件更新功能 -->
    <el-card v-if="activeFunction === 'update'" class="update-card" :class="{ 'disabled-card': !selectedCourse }">
      <template #header>
        <div class="card-header">
          <h2>课件更新</h2>
          <el-tag type="success">AI搜索最新案例</el-tag>
        </div>
      </template>

      <div class="update-section">
        <el-form :model="updateForm" label-width="120px" class="update-form">
          <el-form-item label="课件文件">
            <el-upload
              class="upload-demo"
              :auto-upload="false"
              :on-change="handleUpdateFileChange"
              :limit="1"
              accept=".ppt,.pptx"
            >
              <el-button type="primary">选择PPT文件</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  已选择：{{ updateForm.fileName || '未选择文件' }}
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="更新要求">
            <el-input
              v-model="updateForm.updateRequirements"
              type="textarea"
              :rows="5"
              placeholder="请描述更新要求，例如：&#10;1. 更新2024-2025年最新的网络安全事件案例&#10;2. 补充AI安全相关的前沿知识&#10;3. 更新数据统计图表"
            />
          </el-form-item>
        </el-form>

        <div class="update-actions">
          <el-button 
            type="success" 
            size="large"
            @click="submitCoursewareUpdate"
            :loading="updating"
            :disabled="!canUpdate"
          >
            <el-icon><Refresh /></el-icon>
            提交更新请求
          </el-button>
          <el-button @click="resetUpdateForm">重置</el-button>
        </div>
      </div>

      <!-- 更新记录列表 -->
      <div v-if="updateRecords && updateRecords.length > 0" class="update-records">
        <h3>更新记录</h3>
        <div class="record-item" v-for="item in updateRecords" :key="item.id">
          <div class="record-info">
            <h4>{{ item.originalFileName }}</h4>
            <p>{{ item.updateRequirements }}</p>
            <span class="record-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="record-status">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <el-button v-if="item.status === 'suggestions_ready'" type="primary" size="small" @click="viewUpdateSuggestions(item.id)">
              查看建议
            </el-button>
            <el-button type="danger" size="small" @click="deleteUpdateRecord(item.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 生成结果展示 -->
    <div v-if="designResult" class="result-section">
      <el-card class="result-card">
        <template #header>
          <div class="result-header">
            <h3>AI生成结果</h3>
            <div class="result-actions">
              <el-button
                type="success"
                @click="downloadPPTFromBackend"
                :loading="pptGenerating"
              >
                {{ pptGenerating ? '正在生成PPT...' : (designResult.pptDownloadUrl ? '下载PPT' : '下载PPT') }}
              </el-button>
              <el-button type="warning" @click="downloadLessonPlan">下载教案</el-button>
              <el-button type="info" @click="exportToWord">导出Word</el-button>
              <el-button @click="saveDesign">保存设计</el-button>
            </div>
          </div>
        </template>

        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="PPT课件" name="ppt" v-if="designResult.ppt">
            <div class="ppt-section">
              <div class="ppt-preview">
                <h4>PPT预览</h4>
                <div class="ppt-slides">
                  <div v-for="(slide, index) in designResult.ppt.slides" :key="index" class="ppt-slide">
                    <h5>第{{ index + 1 }}页：{{ slide.title }}</h5>
                    <div class="slide-content" v-html="formatContent(slide.content)"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="教案" name="lessonPlan" v-if="designResult.lessonPlan">
            <div class="lesson-plan-section">
              <div class="lesson-plan-content">
                <h4>教案内容</h4>
                <div v-html="formatContent(designResult.lessonPlan.content)"></div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="教学内容" name="content" v-if="designResult.content">
            <div class="content-section">
              <div v-for="(section, index) in designResult.content" :key="index" class="content-item">
                <h4>{{ section.title }}</h4>
                <div class="content-details">
                  <p><strong>时长：</strong>{{ section.duration }}分钟</p>
                  <p><strong>重点：</strong>{{ section.keyPoints }}</p>
                  <div class="content-description">
                    <strong>详细内容：</strong>
                    <div v-html="formatContent(section.description)"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="实训练习" name="practice" v-if="designResult.practice">
            <div class="practice-section">
              <div v-for="(exercise, index) in designResult.practice" :key="index" class="exercise-item">
                <h4>{{ exercise.title }}</h4>
                <div class="exercise-details">
                  <p><strong>类型：</strong>{{ exercise.type }}</p>
                  <p><strong>难度：</strong>{{ exercise.difficulty }}</p>
                  <p><strong>时长：</strong>{{ exercise.duration }}分钟</p>
                  <div class="exercise-content">
                    <strong>练习内容：</strong>
                    <div v-html="formatContent(exercise.content)"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="考核题目" name="exam" v-if="designResult.exam">
            <div class="exam-section">
              <div v-for="(question, index) in designResult.exam" :key="index" class="question-item">
                <div class="question-header">
                  <span class="question-type">{{ question.type }}</span>
                  <span class="question-difficulty">{{ question.difficulty }}</span>
                  <span class="question-score">{{ question.score }}分</span>
                </div>
                <div class="question-content">
                  <h4>{{ question.title }}</h4>
                  <div v-if="question.options" class="question-options">
                    <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option">
                      {{ String.fromCharCode(65 + optIndex) }}. {{ option }}
                    </div>
                  </div>
                  <div v-if="question.answer" class="question-answer">
                    <strong>参考答案：</strong>
                    <div v-html="formatContent(question.answer)"></div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="时间分布" name="schedule" v-if="designResult.schedule">
            <div class="schedule-section">
              <el-timeline>
                <el-timeline-item
                  v-for="(item, index) in designResult.schedule"
                  :key="index"
                  :timestamp="item.week"
                  placement="top"
                >
                  <el-card>
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.description }}</p>
                    <div class="schedule-details">
                      <span class="duration">时长：{{ item.duration }}</span>
                      <span class="type">类型：{{ item.type }}</span>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-tab-pane>

          <el-tab-pane label="教学建议" name="suggestions" v-if="designResult.suggestions">
            <div class="suggestions-section">
              <div v-for="(suggestion, index) in designResult.suggestions" :key="index" class="suggestion-item">
                <h4>{{ suggestion.title }}</h4>
                <div class="suggestion-content" v-html="formatContent(suggestion.content)"></div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
    
    <!-- AI生成进度条 -->
    <AIGenerationProgress
      :visible="showProgress"
      title="AI 正在生成课程设计"
      :progress="progressValue"
      :current-step="currentStep"
      :steps="progressSteps"
      :message="progressMessage"
      tip="💡 提示：生成时间取决于选择的内容类型和复杂度，通常需要2-5分钟"
    />

    <!-- 课件更新建议查看弹窗 -->
    <SuggestionsViewDialog
      v-if="showUpdateSuggestionsDialog"
      :updateId="currentUpdateId"
      @close="showUpdateSuggestionsDialog = false"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Edit, Document, Refresh } from '@element-plus/icons-vue'
import { aiAPI } from '@/api/ai'
import { getCoursesByTeacherId } from '@/api/course'
import * as smartTeachingApi from '@/api/smartTeaching'
import { saveAs } from 'file-saver'
import { Document as DocxDocument, Packer, Paragraph, TextRun } from 'docx'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'
import SuggestionsViewDialog from './components/SuggestionsViewDialog.vue'

export default {
  name: 'AICourseDesign',
  components: {
    UploadFilled,
    Edit,
    Document,
    Refresh,
    AIGenerationProgress,
    SuggestionsViewDialog
  },
  setup() {
    // 功能标签页
    const activeFunction = ref('generate')
    
    // 课程选择相关数据
    const courseSelectForm = reactive({
      selectedCourseId: null
    })
    const teacherCourses = ref([])
    const selectedCourse = ref(null)
    const loadingCourses = ref(false)
    
    // 课件更新相关数据
    const updateForm = ref({
      fileName: '',
      fileUrl: '',
      updateRequirements: '',
      file: null
    })
    const updating = ref(false)
    const updateRecords = ref([])
    const showUpdateSuggestionsDialog = ref(false)
    const currentUpdateId = ref(null)

    // 课件生成记录相关数据
    const generateRecords = ref([])

    const courseForm = reactive({
      courseName: '',
      courseType: '',
      duration: 16,
      difficulty: 'intermediate',
      outline: '',
      requirements: ''
    })

    const selectedOptions = ref(['ppt', 'lesson-plan', 'content-design'])
    const generating = ref(false)
    const uploading = ref(false)
    const designResult = ref(null)
    const activeTab = ref('ppt')
    const fileList = ref([])
    const uploadRef = ref()
    const pptGenerating = ref(false)

    // 进度条相关
    const showProgress = ref(false)
    const progressValue = ref(0)
    const currentStep = ref(0)
    const progressMessage = ref('')
    const progressSteps = ref([
      { title: '准备数据', desc: '正在准备课程设计所需的数据...' },
      { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
      { title: '生成内容', desc: 'AI正在生成课件、教案等内容...' },
      { title: '完成', desc: '课程设计生成完成！' }
    ])

    // 计算是否可以生成
    const canGenerate = computed(() => {
      return selectedCourse.value &&
             ((courseForm.courseName &&
               courseForm.courseType &&
               courseForm.outline &&
               selectedOptions.value.length > 0) ||
              fileList.value.length > 0)
    })

    // 文件上传前检查
    const beforeUpload = (file) => {
      const isValidType = /\.(doc|docx|pdf|txt|md|ppt|pptx)$/i.test(file.name)
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isValidType) {
        ElMessage.error('只能上传 doc、docx、pdf、txt、md、ppt、pptx 格式的文件!')
        return false
      }
      if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB!')
        return false
      }
      return true
    }

    // 提交上传
    const submitUpload = async () => {
      if (fileList.value.length === 0) {
        ElMessage.warning('请先选择要上传的文件')
        return
      }

      uploading.value = true
      try {
        for (const file of fileList.value) {
          const formData = new FormData()
          formData.append('file', file.raw || file)

          const response = await aiAPI.uploadFile(formData)
          if (response.code === 200) {
            // 更新文件信息
            file.response = response.data
            ElMessage.success(`${file.name} 上传成功`)

            // 如果上传的是文档文件，可以自动提取内容作为课程大纲
            if (file.name.match(/\.(doc|docx|pdf|txt|md)$/i) && !courseForm.outline) {
              courseForm.outline = `基于上传文件 ${file.name} 的内容生成课程大纲`
            }
          } else {
            ElMessage.error(`${file.name} 上传失败: ${response.msg}`)
          }
        }
      } catch (error) {
        console.error('文件上传失败:', error)
        ElMessage.error('文件上传失败: ' + (error.response?.data?.msg || error.message))
      } finally {
        uploading.value = false
      }
    }

    // 清空文件
    const clearFiles = () => {
      fileList.value = []
      uploadRef.value.clearFiles()
    }

    // 获取教师课程列表
    const loadTeacherCourses = async () => {
      loadingCourses.value = true
      try {
        // 获取当前用户信息，这里假设是teacher1(ID=2)
        const teacherId = localStorage.getItem('userId') || '2'
        const response = await getCoursesByTeacherId(teacherId)

        if (response.success === true || response.code === 200) {
          teacherCourses.value = response.data || []
          if (teacherCourses.value.length === 0) {
            ElMessage.warning('您还没有任何课程，请先在系统中创建课程')
          } else {
            ElMessage.success(`成功加载 ${teacherCourses.value.length} 门课程`)
          }
        } else {
          throw new Error(response.msg || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取教师课程失败:', error)
        ElMessage.error('获取课程列表失败: ' + error.message)
      } finally {
        loadingCourses.value = false
      }
    }

    // 选择课程
    const onCourseSelect = (courseId) => {
      const course = teacherCourses.value.find(c => c.id === courseId)
      if (course) {
        selectedCourse.value = course
        // 自动填充课程基本信息
        courseForm.courseName = course.name
        courseForm.courseType = course.subject
        courseForm.outline = course.description || `为《${course.name}》课程生成AI备课内容`
        ElMessage.success(`已选择课程：${course.name}`)
      } else {
        selectedCourse.value = null
        // 清空表单
        Object.assign(courseForm, {
          courseName: '',
          courseType: '',
          duration: 16,
          difficulty: 'intermediate',
          outline: '',
          requirements: ''
        })
      }
    }

    // 页面加载时获取课程列表
    onMounted(() => {
      loadTeacherCourses()
    })

    // 生成课程设计
    const generateCourseDesign = async () => {
      if (!canGenerate.value) {
        ElMessage.warning('请完善课程信息或上传文件，并选择生成选项')
        return
      }

      generating.value = true
      
      // 显示进度条
      showProgress.value = true
      progressValue.value = 0
      currentStep.value = 0
      progressMessage.value = '正在准备数据...'

      try {
        // 步骤1: 准备数据
        progressValue.value = 10
        await new Promise(resolve => setTimeout(resolve, 300))
        
        // 获取教师ID
        const teacherId = localStorage.getItem('userId') || '2'
        
        // 确保数据类型正确
        const courseInfo = {
          ...courseForm,
          duration: courseForm.duration.toString(), // 确保duration是字符串
          difficulty: courseForm.difficulty.toString() // 确保difficulty是字符串
        }

        const requestData = {
          teacherId: teacherId,
          courseId: selectedCourse.value ? selectedCourse.value.id : null,
          courseInfo: courseInfo,
          options: selectedOptions.value,
          uploadedFiles: fileList.value.map(file => ({
            name: file.name,
            url: file.response?.data?.url || file.url
          }))
        }

        // 步骤2: 调用AI服务
        currentStep.value = 1
        progressValue.value = 25
        progressMessage.value = '正在连接AI服务...'
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 步骤3: 生成内容
        currentStep.value = 2
        progressValue.value = 40
        progressMessage.value = 'AI正在生成课程内容，请耐心等待...'
        
        // 模拟进度增长
        const progressInterval = setInterval(() => {
          if (progressValue.value < 85) {
            progressValue.value += Math.random() * 5
          }
        }, 1000)

        const response = await aiAPI.courseDesign(requestData)
        clearInterval(progressInterval)
        
        progressValue.value = 90

        if (response.success === true) {
          // 检查是否有错误信息
          if (response.data.error) {
            throw new Error(response.data.error)
          }

          designResult.value = response.data
          // 根据生成的内容设置默认标签页
          if (response.data.ppt) {
            activeTab.value = 'ppt'
          } else if (response.data.lessonPlan) {
            activeTab.value = 'lessonPlan'
          } else {
            activeTab.value = 'content'
          }

          // 步骤4: 完成
          currentStep.value = 3
          progressValue.value = 100
          progressMessage.value = '生成完成！'
          
          await new Promise(resolve => setTimeout(resolve, 800))
          showProgress.value = false

          // 刷新生成记录列表
          loadGenerateRecords()

          // 检查是否有PPT下载链接，如果有就自动下载
          if (response.data.pptDownloadUrl) {
            setTimeout(() => {
              window.open(response.data.pptDownloadUrl, '_blank')
              ElMessage.success('PPT已生成完成并开始下载！')
            }, 1000) // 延迟1秒下载，让用户看到成功消息
          } else if (selectedOptions.value.includes('ppt')) {
            // 如果选择了PPT选项但没有下载链接，说明PPT还在生成中
            pptGenerating.value = true
            ElMessage.success('AI备课助手生成成功！PPT正在后台生成中...')

            // 启动定时器检查PPT生成状态
            startPPTStatusCheck()
          } else {
            ElMessage.success('AI备课助手生成成功！')
          }
        } else {
          showProgress.value = false
          throw new Error(response.msg || '生成失败')
        }
      } catch (error) {
        showProgress.value = false
        console.error('AI备课助手生成失败:', error)
        let errorMsg = 'AI备课助手生成失败，请稍后重试'

        if (error.message) {
          if (error.message.includes('格式不正确')) {
            errorMsg = 'AI生成的内容格式有误，请重试或简化课程信息'
          } else if (error.message.includes('网络')) {
            errorMsg = '网络连接失败，请检查网络连接'
          } else {
            errorMsg = error.message
          }
        }

        ElMessage.error(errorMsg)
      } finally {
        generating.value = false
      }
    }

    // 重置表单
    const resetForm = () => {
      Object.assign(courseForm, {
        courseName: '',
        courseType: '',
        duration: 16,
        difficulty: 'intermediate',
        outline: '',
        requirements: ''
      })
      selectedOptions.value = ['ppt', 'lesson-plan', 'content-design']
      designResult.value = null
      clearFiles()
      ElMessage.success('表单已重置')
    }

    // 保存设计
    const saveDesign = async () => {
      try {
        await ElMessageBox.confirm('确定要保存这个课程设计吗？', '保存确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })

        // 这里可以调用保存API
        ElMessage.success('课程设计已保存')
      } catch {
        // 用户取消
      }
    }

    // 下载教案
    const downloadLessonPlan = () => {
      if (!designResult.value?.lessonPlan) {
        ElMessage.warning('没有可下载的教案')
        return
      }

      try {
        const doc = new Document({
          sections: [{
            properties: {},
            children: [
              new Paragraph({
                children: [new TextRun({ text: courseForm.courseName || '课程教案', bold: true, size: 32 })]
              }),
              new Paragraph({
                children: [new TextRun({ text: `课程类型：${courseForm.courseType}`, size: 24 })]
              }),
              new Paragraph({
                children: [new TextRun({ text: `课程时长：${courseForm.duration}课时`, size: 24 })]
              }),
              new Paragraph({
                children: [new TextRun({ text: `难度等级：${courseForm.difficulty}`, size: 24 })]
              }),
              new Paragraph({
                children: [new TextRun({ text: '教案内容', bold: true, size: 28 })]
              }),
              new Paragraph({
                children: [new TextRun({ text: designResult.value.lessonPlan.content, size: 20 })]
              })
            ]
          }]
        })

        Packer.toBlob(doc).then(blob => {
          saveAs(blob, `${courseForm.courseName || '课程教案'}.docx`)
          ElMessage.success('教案下载成功')
        })
      } catch (error) {
        console.error('教案生成失败:', error)
        ElMessage.error('教案生成失败')
      }
    }

    // 导出Word
    const exportToWord = async () => {
      if (!designResult.value) {
        ElMessage.warning('请先生成课程设计')
        return
      }

      try {
        const doc = new Document({
          sections: [{
            properties: {},
            children: [
              new Paragraph({ children: [new TextRun({ text: `课程名称：${courseForm.courseName}`, bold: true, size: 32 })] }),
              new Paragraph({ children: [new TextRun({ text: `课程类型：${courseForm.courseType}`, size: 28 })] }),
              new Paragraph({ children: [new TextRun({ text: `课程大纲：${courseForm.outline}`, size: 24 })] }),
              ...(designResult.value.content ? designResult.value.content.map(section =>
                new Paragraph({ children: [new TextRun({ text: `${section.title}: ${section.description}`, size: 22 })] })
              ) : [])
            ]
          }]
        })
        const blob = await Packer.toBlob(doc)
        saveAs(blob, `${courseForm.courseName || '课程设计'}.docx`)
        ElMessage.success('Word文档导出成功')
      } catch (error) {
        console.error('Word导出失败:', error)
        ElMessage.error('Word导出失败')
      }
    }

    // 格式化内容
    const formatContent = (content) => {
      if (!content) return ''
      return content
        .replace(/\n/g, '<br>')
        .replace(/```(\w+)?\n([\s\S]*?)```/g, '<pre><code>$2</code></pre>')
        .replace(/`([^`]+)`/g, '<code>$1</code>')
    }

    // 启动PPT状态检查定时器
    const startPPTStatusCheck = () => {
      const checkInterval = setInterval(async () => {
        if (!pptGenerating.value || !designResult.value?.pptTaskId) {
          clearInterval(checkInterval)
          return
        }

        try {
          // 调用后端API检查PPT状态
          const response = await aiAPI.checkPPTStatus(designResult.value.pptTaskId)

          if (response.success === true) {
            const status = response.data
            
            if (status.completed) {
              // PPT生成完成
              pptGenerating.value = false
              clearInterval(checkInterval)
              
              if (status.downloadUrl) {
                // 生成成功
                designResult.value.pptDownloadUrl = status.downloadUrl
                window.open(status.downloadUrl, '_blank')
                ElMessage.success('PPT生成完成并开始下载！')
              } else if (status.error) {
                // 生成失败
                ElMessage.error('PPT生成失败: ' + status.error)
              }
            } else {
              // 仍在处理中
              log.info('PPT正在生成中...')
            }
          }
        } catch (error) {
          console.error('检查PPT状态失败:', error)
        }
      }, 5000) // 每5秒检查一次

      // 设置最大检查时间（5分钟）
      setTimeout(() => {
        if (pptGenerating.value) {
          pptGenerating.value = false
          clearInterval(checkInterval)
          ElMessage.warning('PPT生成超时，请稍后手动查询或重试')
        }
      }, 300000)
    }

    // 下载PPT
    const downloadPPTFromBackend = async () => {
      if (!designResult.value) {
        ElMessage.warning('请先生成课程设计，再下载PPT')
        return
      }

      // 如果已经有PPT下载链接，直接下载
      if (designResult.value.pptDownloadUrl) {
        try {
          // 使用fetch下载文件并重命名
          const response = await fetch(designResult.value.pptDownloadUrl)
          const blob = await response.blob()
          
          // 生成有意义的文件名
          const fileName = `${courseForm.courseName || '课程'}_AI课件_${new Date().toLocaleDateString().replace(/\//g, '-')}.pptx`
          
          // 使用file-saver保存文件
          saveAs(blob, fileName)
          ElMessage.success('PPT下载成功！')
        } catch (error) {
          console.error('PPT下载失败:', error)
          // 如果fetch失败，回退到直接打开链接
          window.open(designResult.value.pptDownloadUrl, '_blank')
          ElMessage.success('开始下载PPT！')
        }
        return
      }

      // 如果有任务ID且正在生成中，检查状态
      if (designResult.value.pptTaskId) {
        if (pptGenerating.value) {
          ElMessage.info('PPT正在生成中，请稍候...')
          return
        }
        
        // 手动检查一次状态
        try {
          const response = await aiAPI.checkPPTStatus(designResult.value.pptTaskId)
          
          if (response.success === true) {
            const status = response.data
            
            if (status.completed) {
              if (status.downloadUrl) {
                designResult.value.pptDownloadUrl = status.downloadUrl
                
                // 下载并重命名
                const fileResponse = await fetch(status.downloadUrl)
                const blob = await fileResponse.blob()
                const fileName = `${courseForm.courseName || '课程'}_AI课件_${new Date().toLocaleDateString().replace(/\//g, '-')}.pptx`
                saveAs(blob, fileName)
                
                ElMessage.success('PPT已生成完成，下载成功！')
                return
              } else if (status.error) {
                ElMessage.error('PPT生成失败: ' + status.error)
                return
              }
            } else {
              ElMessage.info('PPT正在生成中，请稍候...')
              pptGenerating.value = true
              startPPTStatusCheck()
              return
            }
          }
        } catch (error) {
          console.error('检查PPT状态失败:', error)
        }
      }

      // 如果没有任务ID，需要重新生成
      ElMessage.warning('请重新生成课程设计以创建PPT')
    }

    // 计算是否可以更新
    const canUpdate = computed(() => {
      return selectedCourse.value && updateForm.value.file && updateForm.value.updateRequirements
    })

    // 处理更新文件选择
    const handleUpdateFileChange = (file) => {
      updateForm.value.fileName = file.name
      updateForm.value.file = file.raw
    }

    // 提交课件更新
    const submitCoursewareUpdate = async () => {
      if (!canUpdate.value) {
        ElMessage.warning('请选择PPT文件并填写更新要求')
        return
      }

      updating.value = true
      showProgress.value = true
      progressValue.value = 0
      currentStep.value = 0
      progressMessage.value = '正在准备数据...'

      try {
        progressValue.value = 10
        await new Promise(resolve => setTimeout(resolve, 300))

        const teacherId = localStorage.getItem('userId') || '2'

        // 上传文件
        currentStep.value = 1
        progressValue.value = 25
        progressMessage.value = '正在上传PPT文件...'

        const formData = new FormData()
        formData.append('file', updateForm.value.file)

        const uploadRes = await fetch('http://localhost:8080/upload', {
          method: 'POST',
          body: formData
        })

        if (!uploadRes.ok) {
          throw new Error('文件上传失败')
        }

        const uploadData = await uploadRes.json()
        let fileUrl = uploadData.data || uploadData.data?.url

        if (!fileUrl || fileUrl.startsWith('blob:')) {
          throw new Error('文件URL无效')
        }

        // 调用AI分析
        currentStep.value = 2
        progressValue.value = 40
        progressMessage.value = 'AI正在分析课件...'

        const res = await smartTeachingApi.updateCourseware({
          teacherId,
          courseId: selectedCourse.value.id,
          fileUrl: fileUrl,
          fileName: updateForm.value.fileName,
          updateRequirements: updateForm.value.updateRequirements
        })

        const updateId = res.data?.updateId || res.data?.data?.updateId || res.updateId

        if (!updateId) {
          throw new Error('获取更新ID失败')
        }

        // 轮询检查状态
        const checkStatus = async () => {
          try {
            const statusRes = await smartTeachingApi.getCoursewareSuggestions(updateId)
            const status = statusRes.data?.status || statusRes.status

            if (status === 'suggestions_ready') {
              currentStep.value = 3
              progressValue.value = 100
              progressMessage.value = '分析完成！'

              await new Promise(resolve => setTimeout(resolve, 800))
              showProgress.value = false

              ElMessage.success('AI分析完成！')
              loadUpdateRecords()
              resetUpdateForm()
            } else if (status === 'failed') {
              showProgress.value = false
              ElMessage.error('AI分析失败')
              loadUpdateRecords()
            } else {
              if (progressValue.value < 85) {
                progressValue.value += 2
              }
              setTimeout(checkStatus, 3000)
            }
          } catch (error) {
            if (progressValue.value < 85) {
              setTimeout(checkStatus, 3000)
            } else {
              showProgress.value = false
              ElMessage.warning('分析超时，请稍后查看结果')
              loadUpdateRecords()
            }
          }
        }

        setTimeout(checkStatus, 3000)
      } catch (error) {
        showProgress.value = false
        console.error('提交失败:', error)
        ElMessage.error('提交失败：' + (error.message || '未知错误'))
      } finally {
        updating.value = false
      }
    }

    // 重置更新表单
    const resetUpdateForm = () => {
      updateForm.value = {
        fileName: '',
        fileUrl: '',
        updateRequirements: '',
        file: null
      }
    }

    // 加载更新记录
    const loadUpdateRecords = async () => {
      try {
        const teacherId = localStorage.getItem('userId') || '2'
        const res = await smartTeachingApi.getCoursewareUpdates(teacherId)
        if (res.code === 1 || res.success === true) {
          updateRecords.value = res.data || []
        }
      } catch (error) {
        console.error('加载更新记录失败', error)
      }
    }

    // 查看更新建议
    const viewUpdateSuggestions = (id) => {
      currentUpdateId.value = id
      showUpdateSuggestionsDialog.value = true
    }

    // 删除更新记录
    const deleteUpdateRecord = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这条更新记录吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await smartTeachingApi.deleteCoursewareUpdate(id)
        if (res.code === 1 || res.success === true) {
          ElMessage.success('删除成功')
          loadUpdateRecords()
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
        'analyzing': '分析中',
        'suggestions_ready': '建议已生成',
        'generating': '生成中',
        'completed': '已完成',
        'failed': '失败'
      }
      return map[status] || status
    }

    // 加载生成记录
    const loadGenerateRecords = async () => {
      try {
        const teacherId = localStorage.getItem('userId') || '2'
        const res = await smartTeachingApi.getCourseDesigns(teacherId)
        if (res.code === 1 || res.success === true) {
          generateRecords.value = res.data || []
        }
      } catch (error) {
        // 如果是404错误，说明后端还没实现该接口，静默处理
        if (error.response?.status === 404) {
          console.log('课件生成记录接口暂未实现，等待后端开发')
          generateRecords.value = []
        } else {
          console.error('加载生成记录失败', error)
        }
      }
    }

    // 查看生成结果
    const viewGenerateResult = async (id) => {
      try {
        const res = await smartTeachingApi.getCourseDesignDetail(id)
        if (res.code === 1 || res.success === true) {
          designResult.value = res.data
          // 根据生成的内容设置默认标签页
          if (res.data.ppt) {
            activeTab.value = 'ppt'
          } else if (res.data.lessonPlan) {
            activeTab.value = 'lessonPlan'
          } else {
            activeTab.value = 'content'
          }
          ElMessage.success('已加载生成结果')
        } else {
          ElMessage.error('加载失败')
        }
      } catch (error) {
        console.error('加载生成结果失败', error)
        ElMessage.error('加载失败')
      }
    }

    // 删除生成记录
    const deleteGenerateRecord = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这条生成记录吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await smartTeachingApi.deleteCourseDesign(id)
        if (res.code === 1 || res.success === true) {
          ElMessage.success('删除成功')
          loadGenerateRecords()
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

    // 页面加载时获取课程列表
    onMounted(() => {
      loadTeacherCourses()
      loadUpdateRecords()
      loadGenerateRecords()
    })

    return {
      // 课程选择相关
      courseSelectForm,
      teacherCourses,
      selectedCourse,
      loadingCourses,
      loadTeacherCourses,
      onCourseSelect,
      // 原有数据和方法
      courseForm,
      selectedOptions,
      generating,
      uploading,
      designResult,
      activeTab,
      fileList,
      uploadRef,
      pptGenerating,
      canGenerate,
      beforeUpload,
      submitUpload,
      clearFiles,
      generateCourseDesign,
      resetForm,
      saveDesign,
      downloadLessonPlan,
      exportToWord,
      formatContent,
      startPPTStatusCheck,
      downloadPPTFromBackend,
      // 课件更新相关
      activeFunction,
      updateForm,
      updating,
      updateRecords,
      showUpdateSuggestionsDialog,
      currentUpdateId,
      canUpdate,
      handleUpdateFileChange,
      submitCoursewareUpdate,
      resetUpdateForm,
      loadUpdateRecords,
      viewUpdateSuggestions,
      deleteUpdateRecord,
      formatTime,
      getStatusText,
      // 课件生成记录相关
      generateRecords,
      loadGenerateRecords,
      viewGenerateResult,
      deleteGenerateRecord,
      // 进度条相关
      showProgress,
      progressValue,
      currentStep,
      progressMessage,
      progressSteps
    }
  }
}
</script>

<style scoped>
.ai-course-design {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.course-select-card {
  border: 2px solid #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.function-tabs-card {
  margin-bottom: 20px;
}

.function-tabs-card :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 30px;
}

.course-select-section {
  padding: 10px 0;
}

.course-info-display {
  line-height: 1.8;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.course-info-display p {
  margin: 5px 0;
  color: #303133;
}

.no-course-selected {
  padding: 20px;
  text-align: center;
  color: #909399;
  background-color: #f5f7fa;
  border-radius: 6px;
  border: 1px dashed #dcdfe6;
}

.disabled-card {
  opacity: 0.6;
  pointer-events: none;
  position: relative;
}

.disabled-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 1;
}

.design-card {
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

.upload-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  background-color: #fafafa;
}

.upload-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.upload-actions {
  margin-top: 15px;
  text-align: center;
}

.course-info {
  margin-bottom: 30px;
}

.course-info h3 {
  margin-bottom: 20px;
  color: #303133;
}

.ai-options {
  margin-bottom: 30px;
}

.ai-options h3 {
  margin-bottom: 15px;
  color: #303133;
}

.generate-actions {
  text-align: center;
  margin-top: 30px;
}

.result-section {
  margin-top: 20px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header h3 {
  margin: 0;
  color: #303133;
}

.result-actions {
  display: flex;
  gap: 10px;
}

.ppt-section,
.lesson-plan-section {
  padding: 20px;
}

.ppt-slides {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ppt-slide {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 15px;
  background-color: #fafafa;
}

.ppt-slide h5 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.slide-content {
  line-height: 1.6;
}

.lesson-plan-content {
  line-height: 1.8;
  font-size: 16px;
}

.content-section,
.practice-section,
.exam-section,
.schedule-section,
.suggestions-section {
  padding: 20px;
}

.content-item,
.exercise-item,
.question-item,
.suggestion-item {
  margin-bottom: 25px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background-color: #fafafa;
}

.content-item h4,
.exercise-item h4,
.question-item h4,
.suggestion-item h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.content-details,
.exercise-details {
  line-height: 1.6;
}

.content-description,
.exercise-content {
  margin-top: 10px;
}

.update-card {
  margin-bottom: 20px;
}

.update-section {
  padding: 20px;
}

.update-form {
  margin-bottom: 30px;
}

.update-actions {
  text-align: center;
  margin-top: 30px;
}

.update-records {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #e4e7ed;
}

.update-records h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
}

/* 生成记录样式（与更新记录相同） */
.generate-records {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #e4e7ed;
}

.generate-records h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.record-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.record-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.record-info p {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #666;
}

.record-time {
  font-size: 12px;
  color: #999;
}

.record-status {
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

.status-badge.analyzing {
  background: #fff3cd;
  color: #856404;
}

.status-badge.generating {
  background: #fff3cd;
  color: #856404;
}

.status-badge.suggestions_ready {
  background: #d4edda;
  color: #155724;
}

.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.failed {
  background: #f8d7da;
  color: #721c24;
}

.question-header {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.question-type,
.question-difficulty,
.question-score {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.question-type {
  background-color: #e1f3d8;
  color: #67c23a;
}

.question-difficulty {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.question-score {
  background-color: #f0f9ff;
  color: #409eff;
}

.question-options {
  margin: 10px 0;
}

.option {
  margin: 5px 0;
  padding: 5px 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.question-answer {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.schedule-details {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.duration,
.type {
  padding: 4px 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
}

.unit {
  margin-left: 10px;
  color: #909399;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 120px;
}

:deep(.el-upload__tip) {
  color: #909399;
  font-size: 12px;
  margin-top: 10px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-timeline-item__content) {
  margin-left: 20px;
}
</style> 