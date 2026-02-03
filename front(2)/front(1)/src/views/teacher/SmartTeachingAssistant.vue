<template>
  <div class="smart-teaching-assistant">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>🧰 教学工具箱</h1>
        <p>AI赋能教学，让备课更高效</p>
      </div>
    </div>

    <!-- 功能导航卡片 -->
    <div class="feature-grid">
      <!-- 1. 更新课件案例 -->
      <div class="feature-card" @click="showCoursewareUpdater = true">
        <div class="feature-icon">🔄</div>
        <h3>更新课件案例</h3>
        <p>AI搜索最新案例，更新课件内容</p>
        <div class="feature-badge">推荐</div>
      </div>

      <!-- 2. 生成实验指导书 -->
      <div class="feature-card" @click="showExperimentGenerator = true">
        <div class="feature-icon">🔬</div>
        <h3>实验指导书</h3>
        <p>一键生成完整的实验指导书</p>
        <div class="feature-badge new">新功能</div>
      </div>

      <!-- 3. 智能作业批阅 -->
      <div class="feature-card" @click="showHomeworkGrading = true">
        <div class="feature-icon">✍️</div>
        <h3>智能批阅</h3>
        <p>AI自动批阅主观题作业，生成详细评语</p>
      </div>
    </div>

    <!-- 我的记录 -->
    <div class="records-section">
      <div class="section-tabs">
        <button 
          :class="['tab-btn', { active: activeTab === 'courseware' }]"
          @click="activeTab = 'courseware'; loadCoursewareList()">
          课件更新记录
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'experiment' }]"
          @click="activeTab = 'experiment'; loadExperimentList()">
          实验指导书
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'homework' }]"
          @click="activeTab = 'homework'; loadHomeworkList()">
          作业批阅
        </button>
      </div>

      <!-- 课件更新记录列表 -->
      <div v-if="activeTab === 'courseware'" class="records-list">
        <div v-if="coursewareList.length === 0" class="empty-state">
          <p>暂无课件更新记录</p>
        </div>
        <div v-else class="record-item" v-for="item in coursewareList" :key="item.id">
          <div class="record-info">
            <h4>{{ item.originalFileName }}</h4>
            <p>{{ item.updateRequirements }}</p>
            <span class="record-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="record-status">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <button v-if="item.status === 'suggestions_ready'" class="btn-view" @click="viewSuggestions(item.id)">
              查看建议
            </button>
            <button class="btn-delete" @click="deleteCourseware(item.id)">
              删除
            </button>
          </div>
        </div>
      </div>

      <!-- 实验指导书列表 -->
      <div v-if="activeTab === 'experiment'" class="records-list">
        <div v-if="experimentList.length === 0" class="empty-state">
          <p>暂无实验指导书</p>
        </div>
        <div v-else class="record-item" v-for="item in experimentList" :key="item.id">
          <div class="record-info">
            <h4>{{ item.chapterName }}</h4>
            <p>难度：{{ getDifficultyText(item.difficultyLevel) }}</p>
            <span class="record-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="record-status">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <button v-if="item.status === 'completed'" class="btn-view" @click="viewExperiment(item.id)">
              查看
            </button>
            <button class="btn-delete" @click="deleteExperiment(item.id)">
              删除
            </button>
          </div>
        </div>
      </div>

      <!-- 作业批阅列表 -->
      <div v-if="activeTab === 'homework'" class="records-list">
        <div v-if="homeworkList.length === 0" class="empty-state">
          <p>暂无作业批阅记录</p>
        </div>
        <div v-else class="record-item" v-for="item in homeworkList" :key="item.id">
          <div class="record-info">
            <h4>{{ item.homeworkTitle }}</h4>
            <p>学生ID: {{ item.studentId }}</p>
            <span class="record-time">{{ formatTime(item.submitTime) }}</span>
          </div>
          <div class="record-status">
            <span :class="['status-badge', item.status]">{{ getHomeworkStatusText(item.status) }}</span>
            <span v-if="item.aiScore" class="score-badge">{{ item.aiScore }}分</span>
            <button v-if="item.status === 'submitted'" class="btn-grade" @click="gradeHomework(item.id)">
              AI批阅
            </button>
            <button v-if="item.status === 'ai_graded'" class="btn-view" @click="viewGrading(item)">
              查看结果
            </button>
            <button class="btn-delete" @click="deleteHomework(item.id)">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 课件更新器弹窗 -->
    <CoursewareUpdaterDialog
      v-if="showCoursewareUpdater"
      @close="showCoursewareUpdater = false"
      @success="handleCoursewareUpdated"
    />

    <!-- 实验指导书生成器弹窗 -->
    <ExperimentGeneratorDialog
      v-if="showExperimentGenerator"
      @close="showExperimentGenerator = false"
      @success="handleExperimentGenerated"
    />

    <!-- 作业批阅弹窗 -->
    <HomeworkGradingDialog
      v-if="showHomeworkGrading"
      @close="showHomeworkGrading = false"
      @success="handleHomeworkSubmitted"
    />

    <!-- 课件更新建议查看弹窗 -->
    <SuggestionsViewDialog
      v-if="showSuggestionsView"
      :updateId="currentUpdateId"
      @close="showSuggestionsView = false"
    />

    <!-- 实验指导书查看弹窗 -->
    <ExperimentViewDialog
      v-if="showExperimentView"
      :guideId="currentExperimentId"
      @close="showExperimentView = false"
    />

    <!-- 批阅结果查看弹窗 -->
    <GradingResultDialog
      v-if="showGradingResult"
      :grading="currentGrading"
      @close="showGradingResult = false"
    />

    <!-- AI批阅进度条 -->
    <AIGenerationProgress
      :visible="showGradingProgress"
      title="AI 正在批阅作业"
      :progress="gradingProgressValue"
      :current-step="gradingCurrentStep"
      :steps="gradingProgressSteps"
      :message="gradingProgressMessage"
      tip="💡 提示：AI会分析作业内容并给出详细评语，通常需要30秒-1分钟"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as smartTeachingApi from '@/api/smartTeaching'
import CoursewareUpdaterDialog from './components/CoursewareUpdaterDialog.vue'
import ExperimentGeneratorDialog from './components/ExperimentGeneratorDialog.vue'
import HomeworkGradingDialog from './components/HomeworkGradingDialog.vue'
import SuggestionsViewDialog from './components/SuggestionsViewDialog.vue'
import ExperimentViewDialog from './components/ExperimentViewDialog.vue'
import GradingResultDialog from './components/GradingResultDialog.vue'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'

export default {
  name: 'TeachingToolbox',
  components: {
    CoursewareUpdaterDialog,
    ExperimentGeneratorDialog,
    HomeworkGradingDialog,
    SuggestionsViewDialog,
    ExperimentViewDialog,
    GradingResultDialog,
    AIGenerationProgress
  },
  setup() {
    const activeTab = ref('courseware')
    const coursewareList = ref([])
    const experimentList = ref([])
    const homeworkList = ref([])
    
    const showCoursewareUpdater = ref(false)
    const showExperimentGenerator = ref(false)
    const showHomeworkGrading = ref(false)
    const showSuggestionsView = ref(false)
    const showExperimentView = ref(false)
    const showGradingResult = ref(false)
    
    const currentUpdateId = ref(null)
    const currentExperimentId = ref(null)
    const currentGrading = ref(null)

    const teacherId = ref(localStorage.getItem('userId') || '2')

    // AI批阅进度条相关
    const showGradingProgress = ref(false)
    const gradingProgressValue = ref(0)
    const gradingCurrentStep = ref(0)
    const gradingProgressMessage = ref('')
    const gradingProgressSteps = ref([
      { title: '准备数据', desc: '正在准备批阅所需的数据...' },
      { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
      { title: 'AI批阅中', desc: 'AI正在分析作业内容并生成评语...' },
      { title: '完成', desc: '批阅完成！' }
    ])

    // 加载课件更新列表
    const loadCoursewareList = async () => {
      try {
        const res = await smartTeachingApi.getCoursewareUpdates(teacherId.value)
        // 兼容两种响应格式
        if (res.code === 1 || res.success === true) {
          coursewareList.value = res.data || []
        }
      } catch (error) {
        console.error('加载课件更新列表失败', error)
      }
    }

    // 加载实验指导书列表
    const loadExperimentList = async () => {
      try {
        console.log('正在加载实验指导书列表，教师ID:', teacherId.value)
        const res = await smartTeachingApi.getExperimentGuides(teacherId.value)
        console.log('实验指导书列表API响应:', res)
        // 兼容两种响应格式：{code: 1, data: ...} 或 {success: true, data: ...}
        if (res.code === 1 || res.success === true) {
          experimentList.value = res.data || []
          console.log('实验指导书列表加载成功，数量:', experimentList.value.length)
          console.log('列表内容:', experimentList.value)
        } else {
          console.error('加载失败，响应:', res)
        }
      } catch (error) {
        console.error('加载实验指导书列表失败', error)
      }
    }

    // 加载作业列表
    const loadHomeworkList = async () => {
      try {
        const res = await smartTeachingApi.getHomeworkList(teacherId.value)
        // 兼容两种响应格式
        if (res.code === 1 || res.success === true) {
          homeworkList.value = res.data || []
        }
      } catch (error) {
        console.error('加载作业列表失败', error)
      }
    }

    // 处理课件更新成功
    const handleCoursewareUpdated = () => {
      showCoursewareUpdater.value = false
      loadCoursewareList()
      ElMessage.success('课件分析任务已提交')
    }

    // 处理实验指导书生成成功
    const handleExperimentGenerated = () => {
      console.log('实验指导书生成成功，准备刷新列表')
      showExperimentGenerator.value = false
      // 切换到实验指导书标签并刷新列表
      activeTab.value = 'experiment'
      console.log('已切换到实验指导书标签，activeTab:', activeTab.value)
      
      // 延迟刷新，确保标签切换完成
      setTimeout(() => {
        console.log('开始加载实验指导书列表')
        loadExperimentList()
      }, 100)
      
      ElMessage.success('实验指导书已生成，正在刷新列表...')
    }

    // 处理作业提交成功
    const handleHomeworkSubmitted = () => {
      showHomeworkGrading.value = false
      loadHomeworkList()
      ElMessage.success('作业提交成功')
    }

    // 查看更新建议
    const viewSuggestions = (id) => {
      currentUpdateId.value = id
      showSuggestionsView.value = true
    }

    // 删除课件更新记录
    const deleteCourseware = async (id) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这条课件更新记录吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const res = await smartTeachingApi.deleteCoursewareUpdate(id)
        if (res.code === 1 || res.success === true) {
          ElMessage.success('删除成功')
          loadCoursewareList()
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

    // 查看实验指导书
    const viewExperiment = (id) => {
      currentExperimentId.value = id
      showExperimentView.value = true
    }

    // 删除实验指导书
    const deleteExperiment = async (id) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这条实验指导书记录吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
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

    // 批阅作业
    const gradeHomework = async (id) => {
      showGradingProgress.value = true
      gradingProgressValue.value = 0
      gradingCurrentStep.value = 0
      gradingProgressMessage.value = '正在准备数据...'
      
      try {
        // 步骤1: 准备数据
        gradingProgressValue.value = 10
        await new Promise(resolve => setTimeout(resolve, 300))
        
        // 步骤2: 调用AI服务
        gradingCurrentStep.value = 1
        gradingProgressValue.value = 25
        gradingProgressMessage.value = '正在连接AI服务...'
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 步骤3: AI批阅
        gradingCurrentStep.value = 2
        gradingProgressValue.value = 40
        gradingProgressMessage.value = 'AI正在分析作业内容并生成评语...'
        
        // 模拟进度增长
        const progressInterval = setInterval(() => {
          if (gradingProgressValue.value < 85) {
            gradingProgressValue.value += Math.random() * 5
          }
        }, 800)
        
        const res = await smartTeachingApi.gradeHomework(id)
        
        clearInterval(progressInterval)
        
        // 兼容两种响应格式
        if (res.code === 1 || res.success === true) {
          // 步骤4: 完成
          gradingCurrentStep.value = 3
          gradingProgressValue.value = 100
          gradingProgressMessage.value = '批阅完成！'
          
          await new Promise(resolve => setTimeout(resolve, 800))
          showGradingProgress.value = false
          
          ElMessage.success('批阅完成')
          loadHomeworkList()
        } else {
          showGradingProgress.value = false
          ElMessage.error('批阅失败')
        }
      } catch (error) {
        showGradingProgress.value = false
        console.error('批阅失败，错误详情:', error)
        ElMessage.error('批阅失败：' + (error.message || '未知错误'))
      }
    }

    // 查看批阅结果
    const viewGrading = (grading) => {
      currentGrading.value = grading
      showGradingResult.value = true
    }

    // 删除作业记录
    const deleteHomework = async (id) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这条作业记录吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const res = await smartTeachingApi.deleteHomework(id)
        if (res.code === 1 || res.success === true) {
          ElMessage.success('删除成功')
          loadHomeworkList()
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

    // 获取难度文本
    const getDifficultyText = (level) => {
      const map = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
      }
      return map[level] || level
    }

    // 获取作业状态文本
    const getHomeworkStatusText = (status) => {
      const map = {
        'submitted': '待批阅',
        'ai_graded': 'AI已批阅',
        'teacher_reviewed': '教师已审阅',
        'completed': '已完成'
      }
      return map[status] || status
    }

    onMounted(() => {
      // 根据当前标签加载对应的列表
      if (activeTab.value === 'courseware') {
        loadCoursewareList()
      } else if (activeTab.value === 'experiment') {
        loadExperimentList()
      } else if (activeTab.value === 'homework') {
        loadHomeworkList()
      }
    })

    return {
      activeTab,
      coursewareList,
      experimentList,
      homeworkList,
      showCoursewareUpdater,
      showExperimentGenerator,
      showHomeworkGrading,
      showSuggestionsView,
      showExperimentView,
      showGradingResult,
      currentUpdateId,
      currentExperimentId,
      currentGrading,
      loadCoursewareList,
      loadExperimentList,
      loadHomeworkList,
      handleCoursewareUpdated,
      handleExperimentGenerated,
      handleHomeworkSubmitted,
      viewSuggestions,
      deleteCourseware,
      viewExperiment,
      deleteExperiment,
      gradeHomework,
      viewGrading,
      deleteHomework,
      formatTime,
      getStatusText,
      getDifficultyText,
      getHomeworkStatusText,
      showGradingProgress,
      gradingProgressValue,
      gradingCurrentStep,
      gradingProgressMessage,
      gradingProgressSteps
    }
  }
}
</script>

<style scoped>
.smart-teaching-assistant {
  padding: 24px;
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

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.feature-card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #f0f0f0;
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 20px;
  margin: 0 0 8px 0;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.feature-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: #ff6b6b;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.feature-badge.new {
  background: #51cf66;
}

.records-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-btn {
  padding: 12px 24px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 15px;
  color: #666;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.record-item:hover {
  background: #e9ecef;
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

.status-badge.analyzing,
.status-badge.generating {
  background: #fff3cd;
  color: #856404;
}

.status-badge.suggestions_ready,
.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.failed {
  background: #f8d7da;
  color: #721c24;
}

.score-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  background: #667eea;
  color: white;
}

.btn-view,
.btn-grade,
.btn-delete {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-view {
  background: #51cf66;
  color: white;
}

.btn-grade {
  background: #ff6b6b;
  color: white;
}

.btn-delete {
  background: #f56c6c;
  color: white;
}

.btn-view:hover,
.btn-grade:hover,
.btn-delete:hover {
  opacity: 0.8;
  transform: scale(1.05);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  font-size: 16px;
}
</style>
