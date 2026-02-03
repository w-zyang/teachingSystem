<template>
  <el-dialog
    v-model="visible"
    title="批阅结果"
    width="800px"
    @close="handleClose"
  >
    <div v-if="grading" class="grading-container">
      <!-- 基本信息 -->
      <el-descriptions :column="2" border class="info-section">
        <el-descriptions-item label="作业标题">
          {{ grading.homeworkTitle }}
        </el-descriptions-item>
        <el-descriptions-item label="学生ID">
          {{ grading.studentId }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ formatTime(grading.submitTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="批阅时间">
          {{ formatTime(grading.aiGradeTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- AI评分 -->
      <div class="score-section">
        <h3>📊 AI评分</h3>
        <div class="score-display">
          <el-progress 
            type="circle" 
            :percentage="grading.aiScore" 
            :width="120"
            :color="getScoreColor(grading.aiScore)"
          >
            <template #default="{ percentage }">
              <span class="score-text">{{ percentage }}</span>
              <span class="score-label">分</span>
            </template>
          </el-progress>
        </div>
      </div>

      <!-- AI评语 -->
      <div class="comments-section">
        <h3>💬 AI评语</h3>
        <div class="comments-box">
          {{ grading.aiComments }}
        </div>
      </div>

      <!-- 详细分析 -->
      <div v-if="parsedAnalysis" class="analysis-section">
        <h3>📋 详细分析</h3>
        
        <!-- 优点 -->
        <div class="analysis-item" v-if="parsedAnalysis.strengths">
          <h4>✅ 优点</h4>
          <ul>
            <li v-for="(item, index) in parsedAnalysis.strengths" :key="index">
              {{ item }}
            </li>
          </ul>
        </div>

        <!-- 不足 -->
        <div class="analysis-item" v-if="parsedAnalysis.weaknesses">
          <h4>⚠️ 不足</h4>
          <ul>
            <li v-for="(item, index) in parsedAnalysis.weaknesses" :key="index">
              {{ item }}
            </li>
          </ul>
        </div>

        <!-- 改进建议 -->
        <div class="analysis-item" v-if="parsedAnalysis.suggestions">
          <h4>💡 改进建议</h4>
          <ul>
            <li v-for="(item, index) in parsedAnalysis.suggestions" :key="index">
              {{ item }}
            </li>
          </ul>
        </div>
      </div>

      <!-- 教师审阅 -->
      <div class="teacher-review-section" v-if="grading.status === 'ai_graded'">
        <h3>👨‍🏫 教师审阅</h3>
        <el-form :model="reviewForm" label-width="100px">
          <el-form-item label="最终评分">
            <el-input-number 
              v-model="reviewForm.teacherScore" 
              :min="0" 
              :max="100" 
              :step="1"
            />
          </el-form-item>
          <el-form-item label="教师评语">
            <el-input
              v-model="reviewForm.teacherComments"
              type="textarea"
              :rows="4"
              placeholder="请输入您的评语和建议"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 教师已审阅 -->
      <div class="teacher-reviewed" v-if="grading.teacherScore">
        <el-divider />
        <h3>👨‍🏫 教师审阅结果</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="最终评分">
            <el-tag type="success" size="large">{{ grading.teacherScore }}分</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审阅时间">
            {{ formatTime(grading.teacherReviewTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="教师评语" :span="2">
            {{ grading.teacherComments }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button 
        v-if="grading && grading.status === 'ai_graded'" 
        type="primary" 
        @click="submitReview"
        :loading="submitting"
      >
        提交审阅
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import * as smartTeachingApi from '@/api/smartTeaching'

const props = defineProps({
  grading: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const submitting = ref(false)
const reviewForm = ref({
  teacherScore: props.grading.aiScore || 0,
  teacherComments: ''
})

const parsedAnalysis = computed(() => {
  if (!props.grading.aiAnalysis) return null
  try {
    return JSON.parse(props.grading.aiAnalysis)
  } catch {
    return null
  }
})

const getScoreColor = (score) => {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const submitReview = async () => {
  if (!reviewForm.value.teacherComments) {
    ElMessage.warning('请输入教师评语')
    return
  }

  submitting.value = true
  try {
    await smartTeachingApi.reviewHomework({
      homeworkId: props.grading.id,
      teacherScore: reviewForm.value.teacherScore,
      teacherComments: reviewForm.value.teacherComments
    })
    ElMessage.success('审阅提交成功')
    emit('success')
    emit('close')
  } catch (error) {
    ElMessage.error('提交失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
.grading-container {
  max-height: 70vh;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
}

.score-section {
  margin-bottom: 24px;
}

.score-section h3,
.comments-section h3,
.analysis-section h3,
.teacher-review-section h3,
.teacher-reviewed h3 {
  color: #409eff;
  font-size: 18px;
  margin-bottom: 16px;
}

.score-display {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.score-text {
  font-size: 32px;
  font-weight: bold;
}

.score-label {
  font-size: 16px;
  margin-left: 4px;
}

.comments-section {
  margin-bottom: 24px;
}

.comments-box {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  line-height: 1.8;
  color: #555;
  border-left: 4px solid #409eff;
}

.analysis-section {
  margin-bottom: 24px;
}

.analysis-item {
  margin-bottom: 16px;
}

.analysis-item h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.analysis-item ul {
  margin: 0;
  padding-left: 24px;
}

.analysis-item li {
  margin-bottom: 8px;
  line-height: 1.6;
  color: #666;
}

.teacher-review-section {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  margin-top: 24px;
}

.teacher-reviewed {
  margin-top: 24px;
}
</style>
