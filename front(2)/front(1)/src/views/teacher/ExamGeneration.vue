<template>
  <div class="exam-generation">
    <el-row :gutter="24" class="main-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="config-card">
          <template #header>
            <div class="header-left">
              <el-icon><User /></el-icon>
              <span style="font-weight:bold;font-size:18px;">考核设置</span>
            </div>
          </template>
          <el-form label-width="90px" label-position="left">
            <el-form-item label="所属课程" required>
              <el-select v-model="selectedCourseId" @change="onCourseChange" placeholder="请选择课程" style="width: 100%;">
                <el-option 
                  v-for="course in teacherCourses" 
                  :key="course.id" 
                  :label="course.name" 
                  :value="course.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="考核名称">
              <el-input v-model="examConfig.name" placeholder="请输入考核名称" />
            </el-form-item>
            <el-form-item label="考核类型">
              <el-select v-model="examConfig.type" style="width: 160px;">
                <el-option label="平时作业" value="homework" />
                <el-option label="考试" value="exam" />
              </el-select>
            </el-form-item>
            <el-form-item label="考核时长(分钟)">
              <el-input-number v-model="examConfig.duration" :min="1" :max="300" />
            </el-form-item>
            <el-form-item label="总分">
              <el-input-number v-model="examConfig.totalScore" :min="1" :max="1000" />
            </el-form-item>
          </el-form>
          
          <!-- 当前选择信息展示 -->
          <div style="margin-top: 16px; padding: 12px; background: #f5f7fa; border-radius: 8px;">
            <div style="font-size: 14px; color: #606266; margin-bottom: 8px;">
              <strong>当前选择：</strong>
            </div>
            <div style="font-size: 13px; color: #909399; line-height: 1.6;">
              <div>📚 课程：{{ selectedCourseId ? teacherCourses.find(c => c.id === selectedCourseId)?.name || '未选择' : '未选择' }}</div>
              <div>📖 科目：{{ selectedCourseId ? teacherCourses.find(c => c.id === selectedCourseId)?.subject || '未选择' : '未选择' }}</div>
              <div>📄 章节：{{ selectedChapter ? availableChapters.find(c => c.value === selectedChapter)?.label || selectedChapter : '未选择' }}</div>
              <div>🎯 知识库：{{ selectedKnowledgeBase || '未选择' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="config-card">
          <template #header>
            <div class="header-left">
              <el-icon><List /></el-icon>
              <span style="font-weight:bold;font-size:18px;">题目配置</span>
            </div>
          </template>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12" :md="12" v-for="type in questionTypes" :key="type.key">
              <el-card class="type-card">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <div>
                    <div style="font-weight:bold;">{{ type.name }}</div>
                    <div style="color:#888;font-size:13px;">每题{{ type.scorePer }}分 难度:{{ type.difficulty }}</div>
                  </div>
                  <el-input-number v-model="type.count" :min="0" :max="100" size="small" />
                  <span style="margin-left:4px;">题</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="24" class="main-row">
      <el-col :xs="24" :sm="24" :md="16">
        <el-card class="config-card">
          <template #header>
            <div class="header-left">
              <el-icon><Collection /></el-icon>
              <span style="font-weight:bold;font-size:18px;">知识点选择</span>
            </div>
          </template>
          <el-form inline>
            <el-form-item label="章节选择">
              <el-select v-model="selectedChapter" @change="onChapterChange" style="width: 300px;" :disabled="!selectedCourseId">
                <el-option v-for="chapter in availableChapters" :key="chapter.value" :label="chapter.label" :value="chapter.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="知识库">
              <el-tag type="success">{{ selectedKnowledgeBase || '请先选择课程和章节' }}</el-tag>
            </el-form-item>
          </el-form>
          <el-row :gutter="16" style="margin-top:12px;">
            <el-col :xs="24" :sm="12" :md="12" v-for="point in knowledgePoints" :key="point.id" style="margin-bottom: 12px;">
              <el-card :class="{ 'is-selected': point.selected }" @click="toggleKnowledgePoint(point.id)">
                <div style="display:flex;justify-content:space-between;align-items:center;">
                  <div>
                    <div style="font-weight:bold;">{{ point.name }}</div>
                    <div style="color:#888;font-size:13px;">{{ point.description }}</div>
                  </div>
                  <el-input-number v-model="point.weight" :min="1" :max="10" size="small" @click.stop />
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8">
        <el-card class="config-card">
          <template #header>
            <div class="header-left">
              <el-icon><MagicStick /></el-icon>
              <span style="font-weight:bold;font-size:18px;">智能生成考核</span>
            </div>
          </template>
          <div style="margin: 24px 0; text-align: center;">
            <el-button type="primary" size="large" :loading="generating" @click="generateExamHandler" style="width: 80%; font-size: 18px;">
              <el-icon><MagicStick /></el-icon>
              <span v-if="generating">生成中...</span>
              <span v-else>智能生成考核</span>
            </el-button>
            <div style="margin-top: 16px; color: #888;">AI将根据知识点权重和题目配置自动生成考核内容</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 历史考试列表 -->
    <div v-if="examList.length > 0" class="history-exams">
      <div class="history-header">
        <div class="header-left">
          <img src="@/assets/time.png" alt="历史" class="history-icon">
          <h3>历史考核管理</h3>
        </div>
        <div class="history-count">
          <el-tag type="info">{{ examList.length }} 个考试</el-tag>
        </div>
      </div>
      
      <!-- 课程分类标签页 -->
      <div class="course-tabs">
        <el-tabs v-model="activeCourseTab" @tab-click="handleCourseTabClick">
          <el-tab-pane label="全部课程" name="all">
            <div class="exams-grid">
              <div v-for="exam in examList" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/tag.png" alt="课程" class="info-icon">
                    <span>{{ getCourseName(exam.courseId) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="数据结构" name="1">
            <div v-if="getExamsByCourse(1).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(1)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无数据结构课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="Linux系统" name="2">
            <div v-if="getExamsByCourse(2).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(2)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无Linux系统课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="计算机网络" name="3">
            <div v-if="getExamsByCourse(3).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(3)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无计算机网络课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 生成的考核内容 -->
    <div v-if="generatedExam" class="generated-exam">
      <div class="exam-card">
        <div class="exam-header">
          <div class="header-left">
            <img src="@/assets/balance.png" alt="考核" class="exam-icon">
            <h3>{{ generatedExam.name }}</h3>
          </div>
          <div class="exam-info">
            <div class="info-item">
              <img src="@/assets/time.png" alt="时长" class="info-icon">
              <span>{{ generatedExam.duration }}分钟</span>
            </div>
            <div class="info-item">
              <img src="@/assets/balance.png" alt="总分" class="info-icon">
              <span>{{ generatedExam.totalScore }}分</span>
            </div>
            <div class="info-item">
              <img src="@/assets/category.png" alt="题目数" class="info-icon">
              <span>{{ generatedExam.questionCount }}题</span>
            </div>
          </div>
        </div>

        <div class="exam-actions">
          <button @click="previewExam" class="action-btn secondary">
            <img src="@/assets/search.png" alt="预览" class="btn-icon">
            预览考核
          </button>
          <button @click="exportExam" class="action-btn success">
            <img src="@/assets/add.png" alt="导出" class="btn-icon">
            导出PDF
          </button>
          <button @click="saveExam" class="action-btn primary">
            <img src="@/assets/add.png" alt="保存" class="btn-icon">
            保存考核
          </button>
          <button @click="publishExamHandler" class="action-btn warning">
            <img src="@/assets/add.png" alt="发布" class="btn-icon">
            发布到学生端
          </button>
        </div>

        <div class="questions-preview">
          <div v-for="(question, index) in generatedExam.questions" :key="index" class="question-item">
            <div class="question-header">
              <div class="question-meta">
                <span class="question-number">{{ index + 1 }}.</span>
                <span class="question-type-badge" :class="getQuestionTypeClass(question.type)">
                  <img :src="getQuestionTypeIcon(question.type)" :alt="getQuestionTypeName(question.type)" class="badge-icon">
                  {{ getQuestionTypeName(question.type) }}
                </span>
              </div>
              <span class="question-score">{{ question.score }}分</span>
            </div>
            <div class="question-content" v-html="formatQuestionContent(question.content)"></div>
            <div v-if="question.options" class="question-options">
              <div v-for="option in question.options" :key="option.key" class="option">
                <img src="@/assets/author.png" alt="选项" class="option-icon">
                <span class="option-key">{{ option.key }}.</span>
                <span class="option-content">{{ option.content }}</span>
              </div>
            </div>
            <div class="question-answer">
              <div class="answer-header">
                <img src="@/assets/balance.png" alt="答案" class="answer-icon">
                <strong>参考答案：</strong>
              </div>
              <span>{{ question.answer }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预览弹窗 -->
    <div v-if="showPreview" class="modal-overlay" @click="closePreview">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <div class="header-left">
            <img src="@/assets/search.png" alt="预览" class="modal-icon">
            <h3>考核预览</h3>
          </div>
          <button @click="closePreview" class="close-btn">
            <img src="@/assets/delete.png" alt="关闭" class="close-icon">
          </button>
        </div>
        <div class="modal-body">
          <div class="exam-preview">
            <div class="preview-header">
              <h2>{{ generatedExam.name }}</h2>
              <div class="preview-info">
                <div class="info-row">
                  <img src="@/assets/time.png" alt="时间" class="info-icon">
                  <p>考试时间：{{ generatedExam.duration }}分钟</p>
                </div>
                <div class="info-row">
                  <img src="@/assets/balance.png" alt="总分" class="info-icon">
                  <p>总分：{{ generatedExam.totalScore }}分</p>
                </div>
                <div class="info-row">
                  <img src="@/assets/author.png" alt="注意" class="info-icon">
                  <p>注意事项：请独立完成，不得抄袭</p>
                </div>
              </div>
            </div>
            <div class="preview-questions">
              <div v-for="(question, index) in generatedExam.questions" :key="index" class="preview-question">
                <div class="preview-question-header">
                  <span class="preview-number">{{ index + 1 }}.</span>
                  <span class="preview-type">{{ getQuestionTypeName(question.type) }}</span>
                  <span class="preview-score">({{ question.score }}分)</span>
                </div>
                <div class="preview-content" v-html="formatQuestionContent(question.content)"></div>
                <div v-if="question.options" class="preview-options">
                  <div v-for="option in question.options" :key="option.key" class="preview-option">
                    <span class="preview-option-key">{{ option.key }}.</span>
                    <span class="preview-option-content">{{ option.content }}</span>
                  </div>
                </div>
                <div class="preview-answer-space">
                  <div class="answer-line"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getExamsByTeacherId, getExamById, createExam, updateExam, deleteExam, publishExam, archiveExam } from '@/api/exam'
import { getCoursesByTeacherId } from '@/api/course'
import { aiAPI } from '@/api/ai'
import { ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElInputNumber, ElButton, ElTag, ElRow, ElCol, ElIcon, ElMessage } from 'element-plus'
import { User, List, Collection, MagicStick } from '@element-plus/icons-vue'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

// 获取当前教师ID，假设是teacher1(ID=2)
const teacherId = localStorage.getItem('userId') || '2'

// 响应式变量
const examConfig = ref({
  name: '',
  type: 'choice',
  duration: 60,
  totalScore: 100
})

const questionTypes = ref([
  { key: 'choice', name: '单选题', count: 0, scorePer: 2, difficulty: '简单', iconSrc: '/src/assets/author.png' },
  { key: 'multiple', name: '多选题', count: 0, scorePer: 4, difficulty: '中等', iconSrc: '/src/assets/category.png' },
  { key: 'fill', name: '填空题', count: 0, scorePer: 3, difficulty: '中等', iconSrc: '/src/assets/balance.png' },
  { key: 'essay', name: '简答题', count: 0, scorePer: 10, difficulty: '困难', iconSrc: '/src/assets/time.png' },
  { key: 'programming', name: '编程题', count: 0, scorePer: 20, difficulty: '困难', iconSrc: '/src/assets/code.png' }
])

const knowledgePoints = ref([])
const generatedExam = ref(null)
const generating = ref(false)
const showPreview = ref(false)
const examList = ref([])
const teacherCourses = ref([])
const selectedCourseId = ref(null)
const activeCourseTab = ref('all')

// 新增：科目-章节-知识库数据结构
const subjectChapterMap = {
  '嵌入式Linux': [
    { value: 'ch07', label: 'ch07-TensorFlow.js应用开发', knowledgeBase: 'TensorFlow.js' },
    { value: 'ch08', label: 'ch08-TensorFlow Lite', knowledgeBase: 'TensorFlow Lite' },
    { value: 'ch09', label: 'ch09-嵌入式Python开发', knowledgeBase: '嵌入式Python' }
  ],
  'Linux系统编程': [
    { value: 'ch07', label: 'ch07-进程管理与通信', knowledgeBase: 'Linux进程管理' },
    { value: 'ch08', label: 'ch08-文件系统与I/O', knowledgeBase: 'Linux文件系统' },
    { value: 'ch09', label: 'ch09-网络编程', knowledgeBase: 'Linux网络编程' }
  ],
  'Linux内核开发': [
    { value: 'ch07', label: 'ch07-内核模块开发', knowledgeBase: 'Linux内核模块' },
    { value: 'ch08', label: 'ch08-设备驱动开发', knowledgeBase: 'Linux设备驱动' },
    { value: 'ch09', label: 'ch09-内核调试技术', knowledgeBase: 'Linux内核调试' }
  ],
  '计算机网络': [
    { value: 'ch07', label: 'ch07-TCP/IP协议栈', knowledgeBase: 'TCP/IP' },
    { value: 'ch08', label: 'ch08-路由与交换', knowledgeBase: '路由交换' },
    { value: 'ch09', label: 'ch09-网络安全', knowledgeBase: '网络安全' }
  ]
}
const chapterKnowledgePointsMap = {
  ch07: [
    { id: 1, name: '张量操作', description: 'TensorFlow.js张量基础操作', selected: false, weight: 5 },
    { id: 2, name: '模型训练', description: '深度学习模型训练与评估', selected: false, weight: 7 },
    { id: 3, name: '进程管理', description: 'Linux进程创建、调度与管理', selected: false, weight: 6 },
    { id: 4, name: '进程通信', description: 'IPC机制：管道、信号量、共享内存', selected: false, weight: 8 }
  ],
  ch08: [
    { id: 5, name: '模型转换', description: 'TensorFlow到TensorFlow Lite转换', selected: false, weight: 5 },
    { id: 6, name: 'Lite部署', description: '移动端和嵌入式设备部署', selected: false, weight: 6 },
    { id: 7, name: '文件系统', description: 'Linux文件系统原理与操作', selected: false, weight: 7 },
    { id: 8, name: 'I/O操作', description: '文件I/O、设备I/O编程', selected: false, weight: 8 }
  ],
  ch09: [
    { id: 9, name: 'Python语法', description: '嵌入式Python基础语法', selected: false, weight: 5 },
    { id: 10, name: '嵌入式调用', description: 'C/C++与Python混合编程', selected: false, weight: 7 },
    { id: 11, name: '网络编程', description: 'Socket编程与网络协议', selected: false, weight: 8 },
    { id: 12, name: '网络安全', description: '加密、认证与安全通信', selected: false, weight: 9 }
  ]
}
const selectedChapter = ref('')
const selectedKnowledgeBase = ref('')
const availableChapters = computed(() => {
  if (!selectedCourseId.value) return []
  const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
  return selectedCourse ? subjectChapterMap[selectedCourse.subject] || [] : []
})

// 课程变化处理
function onCourseChange() {
  selectedChapter.value = ''
  selectedKnowledgeBase.value = ''
  knowledgePoints.value = []
  
  // 如果选择了课程，自动选择第一个章节
  if (selectedCourseId.value && availableChapters.value.length > 0) {
    selectedChapter.value = availableChapters.value[0].value
    onChapterChange()
  }
}

function onChapterChange() {
  if (!selectedCourseId.value) return
  
  const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
  if (!selectedCourse) return
  
  const chapter = (subjectChapterMap[selectedCourse.subject] || []).find(c => c.value === selectedChapter.value)
  selectedKnowledgeBase.value = chapter ? chapter.knowledgeBase : ''
  // 切换知识点
  knowledgePoints.value = chapterKnowledgePointsMap[selectedChapter.value]?.map(p => ({ ...p })) || []
}

// 获取教师考试列表
const fetchTeacherExams = async () => {
  try {
    const res = await getExamsByTeacherId(teacherId)
    if (res && res.data) {
      examList.value = res.data
      console.log('教师考试列表:', res.data)
    }
  } catch (error) {
    console.error('获取教师考试列表失败:', error)
  }
}

// 获取教师课程列表
const fetchTeacherCourses = async () => {
  try {
    // 获取当前用户信息，这里假设是teacher1(ID=2)
    const currentTeacherId = localStorage.getItem('userId') || '2'
    const res = await getCoursesByTeacherId(currentTeacherId)
    
    if (res && (res.success === true || res.code === 200) && res.data && Array.isArray(res.data) && res.data.length > 0) {
      teacherCourses.value = res.data
      console.log('从API获取教师课程列表:', res.data)
      ElMessage.success(`成功加载 ${res.data.length} 门课程`)
      // 如果有课程，默认选择第一个
      selectedCourseId.value = res.data[0].id
      // 更新科目章节映射
      updateSubjectChapterMap(res.data)
    } else {
      console.log('没有找到课程数据')
      ElMessage.warning('您还没有任何课程，请先在系统中创建课程')
      teacherCourses.value = []
      selectedCourseId.value = null
    }
  } catch (error) {
    console.error('获取教师课程列表失败:', error)
    ElMessage.error('获取课程列表失败: ' + error.message)
    teacherCourses.value = []
    selectedCourseId.value = null
  }
}

// 根据实际课程更新科目章节映射
const updateSubjectChapterMap = (courses) => {
  // 清空原有映射
  Object.keys(subjectChapterMap).forEach(key => {
    delete subjectChapterMap[key]
  })
  
  // 根据实际课程重建映射
  courses.forEach(course => {
    if (!subjectChapterMap[course.subject]) {
      subjectChapterMap[course.subject] = []
    }
    
    // 根据课程名称创建对应的章节结构
    let chapters = []
    
    if (course.name.includes('Linux') || course.name.includes('linux')) {
      // Linux相关课程，对应阿里云知识库
      chapters = [
        { value: 'ch01', label: '第一章 Linux基础', knowledgeBase: 'Linux基础' },
        { value: 'ch02', label: '第二章 系统管理', knowledgeBase: 'Linux系统管理' },
        { value: 'ch03', label: '第三章 网络配置', knowledgeBase: 'Linux网络' },
        { value: 'ch04', label: '第四章 安全管理', knowledgeBase: 'Linux安全' },
        { value: 'ch07', label: '第七章 进程管理', knowledgeBase: 'linux7' },
        { value: 'ch08', label: '第八章 文件系统', knowledgeBase: 'linux8' },
        { value: 'ch09', label: '第九章 网络编程', knowledgeBase: 'linux9' }
      ]
    } else if (course.name.includes('数据结构') || course.name.includes('算法')) {
      // 数据结构课程
      chapters = [
        { value: 'ch01', label: '第一章 线性表', knowledgeBase: '数据结构基础' },
        { value: 'ch02', label: '第二章 栈和队列', knowledgeBase: '栈队列原理' },
        { value: 'ch03', label: '第三章 树和二叉树', knowledgeBase: '树结构应用' },
        { value: 'ch04', label: '第四章 图', knowledgeBase: '图算法练习' }
      ]
    } else if (course.name.includes('网络') || course.name.includes('计算机网络')) {
      // 计算机网络课程
      chapters = [
        { value: 'ch01', label: '第一章 网络基础', knowledgeBase: '网络基础概念' },
        { value: 'ch02', label: '第二章 物理层', knowledgeBase: '物理层原理' },
        { value: 'ch03', label: '第三章 数据链路层', knowledgeBase: '数据链路应用' },
        { value: 'ch04', label: '第四章 网络层', knowledgeBase: '网络层练习' },
        { value: 'ch07', label: '第七章 TCP/IP协议', knowledgeBase: 'TCP协议' },
        { value: 'ch08', label: '第八章 路由与交换', knowledgeBase: '路由交换' },
        { value: 'ch09', label: '第九章 网络安全', knowledgeBase: '网络安全' }
      ]
    } else {
      // 其他课程使用默认章节
      chapters = [
        { value: 'ch01', label: '第一章 基础概念', knowledgeBase: `${course.name}基础` },
        { value: 'ch02', label: '第二章 核心原理', knowledgeBase: `${course.name}原理` },
        { value: 'ch03', label: '第三章 实践应用', knowledgeBase: `${course.name}应用` },
        { value: 'ch04', label: '第四章 综合练习', knowledgeBase: `${course.name}练习` }
      ]
    }
    
    subjectChapterMap[course.subject] = chapters
  })
  
  // 触发课程变化处理，自动选择第一个课程和章节
  if (courses.length > 0 && selectedCourseId.value) {
    onCourseChange()
  }
}

// 获取考试详情
const fetchExamById = async (id) => {
  try {
    const res = await getExamById(id)
    if (res && res.data) {
      console.log('考试详情:', res.data)
      return res.data
    }
  } catch (error) {
    console.error('获取考试详情失败:', error)
  }
}

// 创建考试
const createExamData = async (examData) => {
  try {
    console.log('准备发送的考试数据:', JSON.stringify(examData, null, 2))
    const res = await createExam(examData)
    console.log('后端响应原始数据:', res)
    console.log('后端响应结构详情:', {
      '响应类型': typeof res,
      '是否为空': res === null || res === undefined,
      '所有字段': res ? Object.keys(res) : '无字段',
      'success字段': res?.success,
      'success类型': typeof res?.success,
      'msg字段': res?.msg,
      'message字段': res?.message,
      'data字段': res?.data,
      'data类型': typeof res?.data
    })
    
    // 根据后端实际Result格式判断：success=true 或 消息包含"成功"
    const isSuccess = (res && res.success === true) || 
                     (res && res.msg && (res.msg.includes('成功') || res.msg.includes('success')))
    
    if (isSuccess) {
      console.log('考试创建成功 - success:', res.success, 'data:', res.data)
      return res.data || res
    } else {
      console.error('后端返回失败 - success:', res?.success, 'msg:', res?.msg)
      return null
    }
  } catch (error) {
    console.error('创建考试网络异常:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      statusText: error.response?.statusText
    })
    return null
  }
}

// 更新考试
const updateExamData = async (examData) => {
  try {
    const res = await updateExam(examData)
    if (res && res.success === true) {
      console.log('考试更新成功:', res.data)
      return res.data
    }
    return null
  } catch (error) {
    console.error('更新考试失败:', error)
    return null
  }
}

// 删除考试
const deleteExamData = async (id) => {
  try {
    const res = await deleteExam(id)
    if (res && res.success === true) {
      console.log('考试删除成功')
      return true
    }
    return false
  } catch (error) {
    console.error('删除考试失败:', error)
    return false
  }
}

// 发布考试
const publishExamData = async (id) => {
  try {
    console.log('准备发布考试，ID:', id)
    const res = await publishExam(id)
    console.log('发布考试响应:', res)
    console.log('发布考试响应详情:', {
      '响应类型': typeof res,
      '所有字段': res ? Object.keys(res) : '无字段',
      'success字段': res?.success,
      'success类型': typeof res?.success,
      'msg字段': res?.msg,
      'data字段': res?.data
    })
    
    if (res && res.success === true) {
      console.log('考试发布成功 - success:', res.success, 'msg:', res.msg)
      return true
    } else {
      console.error('考试发布失败 - success:', res?.success, 'msg:', res?.msg)
      return false
    }
  } catch (error) {
    console.error('发布考试网络异常:', error)
    console.error('发布考试错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      statusText: error.response?.statusText
    })
    return false
  }
}

// 归档考试
const archiveExamData = async (id) => {
  try {
    const res = await archiveExam(id)
    if (res && res.success === true) {
      console.log('考试归档成功')
      return true
    }
    return false
  } catch (error) {
    console.error('归档考试失败:', error)
    return false
  }
}

// 智能生成考试
const generateExamData = async (params) => {
  try {
    // 构建AI请求数据
    const aiRequestData = {
      subject: params.subject || '计算机科学',
      difficulty: params.difficulty || '中等',
      questionCount: params.questionCount,
      examType: params.examType,
      examName: params.examName,
      knowledgePoints: params.knowledgePoints,
      questionTypes: params.questionTypes
    }
    
    console.log('发送AI考试生成请求:', aiRequestData)
    const response = await aiAPI.generateExam(aiRequestData)
    console.log('AI考试生成响应完整结构:', response)
    console.log('响应字段详情 - success:', response?.success, 'typeof success:', typeof response?.success)
    console.log('响应字段详情 - msg:', response?.msg)
    console.log('响应字段详情 - data:', response?.data)
    console.log('响应所有键:', response ? Object.keys(response) : 'null')
    
    // 处理后端Result格式的响应
    // 注意：响应拦截器已经返回了response.data，所以这里的response就是后端的Result对象
    const isSuccess = (response && response.success === true) || 
                     (response && response.msg && response.msg.includes('生成成功'))
    
    if (isSuccess) {
      if (response.data && response.data.questions && response.data.questions.length > 0) {
        console.log('AI生成考试成功 - questions数量:', response.data.questions.length)
        return {
          success: true,
          data: response.data
        }
      } else {
        console.warn('AI生成成功但题目数据为空:', response.data)
        // 如果msg表示成功但没有data，可能data在response的其他字段
        if (response.questions && response.questions.length > 0) {
          console.log('在response直接找到questions - 数量:', response.questions.length)
          return {
            success: true,
            data: { questions: response.questions }
          }
        }
        return {
          success: false,
          error: '生成的题目数据为空，请重试'
        }
      }
    } else {
      console.warn('AI生成考试响应失败 - success:', response?.success, 'msg:', response?.msg)
      return {
        success: false,
        error: response?.msg || '生成失败'
      }
    }
  } catch (error) {
    console.error('AI生成考试网络异常:', error)
    
    let errorMsg = '网络错误'
    if (error.response) {
      // 服务器响应错误
      errorMsg = error.response.data?.msg || error.response.statusText || '服务器错误'
    } else if (error.request) {
      // 网络连接错误
      errorMsg = '网络连接失败，请检查网络'
    } else {
      // 其他错误
      errorMsg = error.message || '未知错误'
    }
    
    return {
      success: false,
      error: errorMsg
    }
  }
}

// 切换知识点选择
const toggleKnowledgePoint = (id) => {
  const point = knowledgePoints.value.find(p => p.id === id)
  if (point) {
    point.selected = !point.selected
  }
}

// 生成考试
const generateExamHandler = async () => {
  generating.value = true
  
  // 验证课程选择
  if (!selectedCourseId.value) {
    ElMessage.warning('请先选择课程')
    generating.value = false
    return
  }
  
  // 计算总题目数
  const totalQuestions = questionTypes.value.reduce((sum, type) => sum + type.count, 0)
  if (totalQuestions === 0) {
    ElMessage.warning('请至少选择一道题目')
    generating.value = false
    return
  }
  
  // 计算总分
  const totalScore = questionTypes.value.reduce((sum, type) => sum + (type.count * type.scorePer), 0)
  
  // 获取选中的知识点
  const selectedKnowledgePoints = knowledgePoints.value.filter(p => p.selected)
  if (selectedKnowledgePoints.length === 0) {
    ElMessage.warning('请至少选择一个知识点')
    generating.value = false
    return
  }
  
  // 显示详细的加载提示
  ElMessage({
    message: 'AI正在生成考核题目，预计需要1-3分钟，请耐心等待...',
    type: 'info',
    duration: 0, // 不自动关闭
    showClose: true
  })
  
  try {
    // 准备生成参数
    const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
    const params = {
      subject: selectedCourse?.subject || '',
      chapter: selectedChapter.value,
      knowledgeBase: selectedKnowledgeBase.value,
      difficulty: '中等', // 可根据需要扩展
      examName: examConfig.value.name,
      examType: examConfig.value.type,
      questionCount: totalQuestions,
      duration: examConfig.value.duration,
      totalScore: totalScore,
      knowledgePoints: selectedKnowledgePoints.map(p => ({
        id: p.id,
        name: p.name,
        weight: p.weight
      })),
      questionTypes: questionTypes.value.filter(type => type.count > 0).map(type => ({
        type: type.key,
        count: type.count,
        scorePer: type.scorePer,
        difficulty: type.difficulty
      }))
    }
    
    console.log('生成考试参数:', params)
    
    // 调用AI API生成考试
    const result = await generateExamData(params)
    console.log('generateExamData返回结果 - success:', result.success, 'data:', result.data ? '有数据' : '无数据', 'questions:', result.data?.questions?.length || 0)
    
    if (result.success && result.data && result.data.questions && result.data.questions.length > 0) {
      generatedExam.value = {
        name: examConfig.value.name,
        duration: examConfig.value.duration,
        totalScore: totalScore,
        questionCount: totalQuestions,
        questions: result.data.questions
      }
      
      // 关闭加载提示并显示成功消息
      ElMessage.closeAll()
      ElMessage.success(`考核题目生成成功！共${result.data.questions.length}道题目`)
      
    } else {
      console.log('AI生成失败，使用模拟数据:', result.error)
      
      // 生成失败时使用模拟数据
      generatedExam.value = {
        name: examConfig.value.name,
        duration: examConfig.value.duration,
        totalScore: totalScore,
        questionCount: totalQuestions,
        questions: generateMockQuestions()
      }
      
      // 关闭加载提示
      ElMessage.closeAll()
      
      let errorMsg = 'AI生成失败，已使用模拟题目'
      
      // 针对不同错误类型提供更友好的提示
      if (result.error) {
        if (result.error.includes('timeout')) {
          errorMsg = 'AI生成超时，这通常是因为题目生成需要较长时间。已使用模拟题目，请稍后重试或尝试减少题目数量。'
        } else if (result.error.includes('繁忙') || result.error.includes('429')) {
          errorMsg = 'AI服务繁忙，已使用模拟题目，请稍后重试'
        } else if (result.error.includes('服务')) {
          errorMsg = 'AI服务暂时不可用，已使用模拟题目，请稍后重试'
        } else {
          errorMsg = `AI生成失败：${result.error}，已使用模拟题目`
        }
      }
      
      ElMessage.warning({
        message: errorMsg,
        duration: 8000,
        showClose: true
      })
    }
  } catch (error) {
    console.error('生成考试异常:', error)
    
    // 关闭加载提示
    ElMessage.closeAll()
    
    // 生成失败时使用模拟数据
    generatedExam.value = {
      name: examConfig.value.name,
      duration: examConfig.value.duration,
      totalScore: totalScore,
      questionCount: totalQuestions,
      questions: generateMockQuestions()
    }
    
    ElMessage.error({
      message: '生成考试时发生异常，已使用模拟题目',
      duration: 8000,
      showClose: true
    })
  } finally {
    generating.value = false
  }
}

// 生成模拟题目（当API失败时使用）
const generateMockQuestions = () => {
  const questions = []
  let questionIndex = 1
  
  questionTypes.value.forEach(type => {
    for (let i = 0; i < type.count; i++) {
      const question = {
        id: questionIndex,
        type: type.key,
        content: `这是第${questionIndex}题，类型为${type.name}`,
        score: type.scorePer,
        answer: `第${questionIndex}题的参考答案`
      }
      
      if (type.key === 'choice' || type.key === 'multiple') {
        question.options = [
          { key: 'A', content: '选项A' },
          { key: 'B', content: '选项B' },
          { key: 'C', content: '选项C' },
          { key: 'D', content: '选项D' }
        ]
      }
      
      questions.push(question)
      questionIndex++
    }
  })
  
  return questions
}

// 预览考试
const previewExam = () => {
  showPreview.value = true
}

// 关闭预览
const closePreview = () => {
  showPreview.value = false
}

// 导出PDF
const exportExam = () => {
  if (!generatedExam.value) return
  const examDom = document.querySelector('.exam-card')
  html2canvas(examDom).then(canvas => {
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const imgProps = pdf.getImageProperties(imgData)
    const pdfWidth = pageWidth
    const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width
    pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight)
    pdf.save(`${generatedExam.value.name || 'exam'}.pdf`)
  })
}

// 转换题目数据格式以匹配后端实体
const convertQuestionsForBackend = (questions) => {
  return questions.map((question, index) => {
    const convertedQuestion = {
      type: question.type,
      content: question.content,
      answer: Array.isArray(question.answer) ? question.answer.join(',') : String(question.answer),
      analysis: question.explanation || question.analysis || '', // 字段名转换
      score: Number(question.score) || 0,
      difficulty: question.difficulty || 'medium',
      knowledgePoint: question.knowledgePoint || '',
      sortOrder: index + 1
    }
    
    // 处理选项数据
    if (question.options && Array.isArray(question.options)) {
      convertedQuestion.options = question.options.map((option, optIndex) => ({
        key: option.key,
        content: option.content,
        isCorrect: question.answer === option.key || (Array.isArray(question.answer) && question.answer.includes(option.key)),
        sortOrder: optIndex + 1
      }))
    }
    
    return convertedQuestion
  })
}

// 保存考试
const saveExam = async () => {
  if (!generatedExam.value) {
    ElMessage.warning('请先生成考试')
    return
  }
  
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  
  try {
    const examData = {
      name: String(generatedExam.value.name || ''),
      type: String(examConfig.value.type || 'homework'),
      duration: Number(generatedExam.value.duration) || 60,
      totalScore: Number(generatedExam.value.totalScore) || 100,
      courseId: Number(selectedCourseId.value),
      teacherId: Number(teacherId),
      status: 'draft',
      questions: convertQuestionsForBackend(generatedExam.value.questions)
    }
    
    console.log('转换后的考试数据:', examData)
    
    const savedExam = await createExamData(examData)
    if (savedExam) {
      ElMessage.success('考试保存成功')
      console.log('考试保存成功')
      // 重新获取考试列表
      await fetchTeacherExams()
    }
  } catch (error) {
    console.error('保存考试失败:', error)
    ElMessage.error('保存考试失败')
  }
}

// 发布考试
const publishExamHandler = async () => {
  if (!generatedExam.value) {
    ElMessage.warning('请先生成考试')
    return
  }
  
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  
  try {
    // 先保存考试
    const examData = {
      name: String(generatedExam.value.name || ''),
      type: String(examConfig.value.type || 'homework'),
      duration: Number(generatedExam.value.duration) || 60,
      totalScore: Number(generatedExam.value.totalScore) || 100,
      courseId: Number(selectedCourseId.value),
      teacherId: Number(teacherId),
      status: 'draft',
      questions: convertQuestionsForBackend(generatedExam.value.questions)
    }
    
    const savedExam = await createExamData(examData)
    if (savedExam && savedExam.id) {
      const result = await publishExamData(savedExam.id)
      if (result) {
        ElMessage.success('考试发布成功，学生端可见！')
        // 重新获取考试列表
        await fetchTeacherExams()
      } else {
        ElMessage.error('考试发布失败！')
      }
    } else {
      ElMessage.error('保存考试失败，无法发布！')
    }
  } catch (error) {
    console.error('发布考试失败:', error)
    ElMessage.error('发布考试失败！')
  }
}

// 获取题目类型名称
const getQuestionTypeName = (type) => {
  const typeMap = {
    choice: '单选题',
    multiple: '多选题',
    fill: '填空题',
    essay: '简答题',
    short: '简答题',  // 后端AI使用的简答题类型
    programming: '编程题',
    coding: '编程题',  // 后端AI使用的编程题类型
    unknown: '综合题'  // 后端默认题目可能使用的类型
  }
  return typeMap[type] || '未知类型'
}

// 获取题目类型样式类
const getQuestionTypeClass = (type) => {
  const classMap = {
    choice: 'type-choice',
    multiple: 'type-multiple',
    fill: 'type-fill',
    essay: 'type-essay',
    short: 'type-essay',  // 简答题使用相同样式
    programming: 'type-programming',
    coding: 'type-programming',  // 编程题使用相同样式
    unknown: 'type-unknown'
  }
  return classMap[type] || ''
}

// 获取题目类型图标
const getQuestionTypeIcon = (type) => {
  const iconMap = {
    choice: '/src/assets/author.png',
    multiple: '/src/assets/category.png',
    fill: '/src/assets/balance.png',
    essay: '/src/assets/time.png',
    short: '/src/assets/time.png',  // 简答题使用相同图标
    programming: '/src/assets/code.png',
    coding: '/src/assets/code.png',  // 编程题使用相同图标
    unknown: '/src/assets/category.png'
  }
  return iconMap[type] || '/src/assets/author.png'
}

// 获取考试类型名称
const getExamTypeName = (type) => {
  const typeMap = {
    quiz: '小测验',
    homework: '作业',
    midterm: '期中考试',
    final: '期末考试',
    practice: '练习'
  }
  return typeMap[type] || type
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    draft: 'info',
    published: 'success',
    archived: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    draft: '草稿',
    published: '已发布',
    archived: '已归档'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化题目内容，自动识别并美化排版
const formatQuestionContent = (content) => {
  if (!content) return ''
  
  let html = ''
  const lines = content.split('\n')
  
  const sectionKeywords = ['编程要求', '题目描述', '输入格式', '输出格式', '示例', '数据范围', '注意', '说明', '提示', '解释']
  
  for (let line of lines) {
    line = line.trim()
    if (!line) {
      html += '<br>'
      continue
    }
    
    // 检查是否是章节标题
    let isSection = false
    for (let keyword of sectionKeywords) {
      if (line.includes(keyword + '：') || line.includes(keyword + ':')) {
        const parts = line.split(/[：:]/)
        html += `<div class="content-section-title">📌 ${parts[0]}</div>`
        if (parts[1] && parts[1].trim()) {
          html += `<div class="content-line">${escapeHtml(parts[1].trim())}</div>`
        }
        isSection = true
        break
      }
    }
    
    if (!isSection) {
      // 检查是否是代码行
      if (line.match(/^(def|function|class|import|from|return|if|for|while|#|\/\/)/)) {
        html += `<div class="content-code">${escapeHtml(line)}</div>`
      } else if (line.startsWith('输入：') || line.startsWith('输出：')) {
        html += `<div class="content-example">${escapeHtml(line)}</div>`
      } else {
        html += `<div class="content-line">${escapeHtml(line)}</div>`
      }
    }
  }
  
  return html
}

// HTML转义函数
const escapeHtml = (text) => {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

// 根据课程ID获取考试列表
const getExamsByCourse = (courseId) => {
  return examList.value.filter(exam => exam.courseId === courseId)
}

// 获取课程名称
const getCourseName = (courseId) => {
  const courseMap = {
    1: '数据结构',
    2: 'Linux系统',
    3: '计算机网络'
  }
  return courseMap[courseId] || '未知课程'
}

// 处理课程标签页点击
const handleCourseTabClick = (tab) => {
  console.log('切换到课程标签页:', tab.name)
  activeCourseTab.value = tab.name
}

// 查看考试详情
const viewExamDetails = async (exam) => {
  try {
    const examDetails = await fetchExamById(exam.id)
    if (examDetails) {
      console.log('考试详情数据:', examDetails)
      console.log('题目数据:', examDetails.questions)
      
      // 将考试详情转换为生成格式，用于预览
      generatedExam.value = {
        name: examDetails.name,
        duration: examDetails.duration,
        totalScore: examDetails.totalScore,
        questionCount: examDetails.questions?.length || 0,
        questions: examDetails.questions || []
      }
      
      // 确保每个题目都有选项数据
      if (generatedExam.value.questions) {
        generatedExam.value.questions.forEach((question, index) => {
          console.log(`题目${index + 1}:`, question)
          console.log(`题目${index + 1}选项:`, question.options)
        })
      }
      
      showPreview.value = true
      ElMessage.success(`查看考试：${exam.name}`)
    }
  } catch (error) {
    console.error('获取考试详情失败:', error)
    ElMessage.error('获取考试详情失败')
  }
}

// 编辑考试
const editExam = (exam) => {
  // 将现有考试加载到编辑表单中
  examConfig.value = {
    name: exam.name,
    type: exam.type,
    duration: exam.duration,
    totalScore: exam.totalScore
  }
  selectedCourseId.value = exam.courseId
  
  // 获取考试详情并转换为生成格式
  fetchExamById(exam.id).then(examDetails => {
    if (examDetails && examDetails.questions) {
      generatedExam.value = {
        name: examDetails.name,
        duration: examDetails.duration,
        totalScore: examDetails.totalScore,
        questionCount: examDetails.questions.length,
        questions: examDetails.questions
      }
      ElMessage.success(`已加载考试：${exam.name}`)
    }
  }).catch(error => {
    console.error('加载考试失败:', error)
    ElMessage.error('加载考试失败')
  })
}

// 发布现有考试
const publishExistingExam = async (exam) => {
  try {
    const result = await publishExamData(exam.id)
    if (result) {
      ElMessage.success(`考试"${exam.name}"发布成功，学生端可见！`)
      await fetchTeacherExams() // 重新获取列表
    } else {
      ElMessage.error('发布失败')
    }
  } catch (error) {
    console.error('发布考试失败:', error)
    ElMessage.error('发布考试失败')
  }
}

// 归档现有考试
const archiveExistingExam = async (exam) => {
  try {
    const result = await archiveExamData(exam.id)
    if (result) {
      ElMessage.success(`考试"${exam.name}"已归档`)
      await fetchTeacherExams() // 重新获取列表
    } else {
      ElMessage.error('归档失败')
    }
  } catch (error) {
    console.error('归档考试失败:', error)
    ElMessage.error('归档考试失败')
  }
}

// 删除现有考试
const deleteExistingExam = async (exam) => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确定要删除考试"${exam.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      const result = await deleteExamData(exam.id)
      if (result) {
        ElMessage.success(`考试"${exam.name}"已删除`)
        await fetchTeacherExams() // 重新获取列表
      } else {
        ElMessage.error('删除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除考试失败:', error)
      ElMessage.error('删除考试失败')
    }
  }
}

// 初始化知识点数据
const initKnowledgePoints = () => {
  knowledgePoints.value = [
    { id: 1, name: '基础概念', description: '课程基础概念和定义', selected: false, weight: 5 },
    { id: 2, name: '核心原理', description: '课程核心原理和理论', selected: false, weight: 8 },
    { id: 3, name: '应用实践', description: '实际应用和案例分析', selected: false, weight: 6 },
    { id: 4, name: '综合运用', description: '综合运用和问题解决', selected: false, weight: 7 }
  ]
}

// 课程选择变量已在上面定义

// 页面加载时获取数据
onMounted(async () => {
  await fetchTeacherCourses()
  await fetchTeacherExams()
  
  // 如果没有知识点，则使用默认知识点
  if (knowledgePoints.value.length === 0) {
    initKnowledgePoints()
  }
  
  console.log('初始化完成:', {
    selectedCourse: selectedCourseId.value,
    selectedChapter: selectedChapter.value,
    selectedKnowledgeBase: selectedKnowledgeBase.value,
    knowledgePointsCount: knowledgePoints.value.length
  })
})
</script>

<style lang="scss" scoped>
.exam-generation {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 32px;
  border-radius: 24px;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
    pointer-events: none;
  }
  
  .header-content {
    position: relative;
    z-index: 2;
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      h1 {
        margin: 0;
        font-size: 2.5rem;
        font-weight: 800;
        background: linear-gradient(135deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
    
    .header-subtitle {
      margin: 0;
      font-size: 1.1rem;
      opacity: 0.9;
      font-weight: 400;
    }
  }
  
  .header-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
      animation: float 6s ease-in-out infinite;
      
      &.shape-1 {
        width: 60px;
        height: 60px;
        top: 20px;
        right: 40px;
        animation-delay: 0s;
      }
      
      &.shape-2 {
        width: 40px;
        height: 40px;
        bottom: 30px;
        right: 80px;
        animation-delay: 2s;
      }
      
      &.shape-3 {
        width: 30px;
        height: 30px;
        top: 60px;
        right: 120px;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 配置卡片 */
.config-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .card-header {
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .card-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    .form-item {
      label {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #718096;
        margin-bottom: 8px;
        
        .label-icon {
          width: 16px;
          height: 16px;
          opacity: 0.6;
        }
      }
      
      input, select {
        width: 100%;
        padding: 12px 16px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 14px;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        &::placeholder {
          color: #a0aec0;
        }
      }
    }
  }
}

/* 题目类型 */
.question-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .question-type {
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 16px;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .type-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .type-info {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .type-icon {
          width: 20px;
          height: 20px;
          opacity: 0.7;
        }
        
        .type-name {
          font-size: 16px;
          font-weight: 600;
          color: #2d3748;
        }
      }
      
      .type-controls {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .count-input {
          width: 60px;
          padding: 8px 12px;
          border: 1px solid #e2e8f0;
          border-radius: 6px;
          text-align: center;
          font-size: 14px;
          
          &:focus {
            outline: none;
            border-color: #667eea;
          }
        }
        
        .unit {
          font-size: 14px;
          color: #718096;
        }
      }
    }
    
    .type-details {
      display: flex;
      gap: 16px;
      
      .score-per {
        font-size: 12px;
        color: #10b981;
        font-weight: 600;
      }
      
      .difficulty {
        font-size: 12px;
        color: #718096;
      }
    }
  }
}

/* 知识点选择 */
.knowledge-points {
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  .knowledge-point {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-color: #667eea;
      
      .point-info .point-content .point-name {
        color: white;
      }
      
      .point-info .point-content .point-desc {
        color: rgba(255, 255, 255, 0.8);
      }
    }
    
    .point-info {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;
      
      .point-icon {
        width: 20px;
        height: 20px;
        opacity: 0.7;
      }
      
      .point-content {
        .point-name {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 4px;
        }
        
        .point-desc {
          display: block;
          font-size: 12px;
          color: #718096;
        }
      }
    }
    
    .point-weight {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .weight-input {
        width: 50px;
        padding: 6px 8px;
        border: 1px solid #e2e8f0;
        border-radius: 6px;
        text-align: center;
        font-size: 12px;
        
        &:focus {
          outline: none;
          border-color: #667eea;
        }
      }
      
      .weight-label {
        font-size: 12px;
        color: #718096;
      }
    }
  }
}

/* 生成区域 */
.generate-section {
  text-align: center;
  
  .generate-btn {
    width: 100%;
    padding: 16px 32px;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }
  
  .generate-tip {
    font-size: 14px;
    color: #718096;
    margin: 0;
  }
}

/* 按钮样式 */
.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  
  .btn-icon {
    width: 18px;
    height: 18px;
    opacity: 0.8;
  }
  
  &.primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
    }
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }
  }
  
  &.secondary {
    background: #f7fafc;
    color: #718096;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: #edf2f7;
      transform: translateY(-2px);
    }
  }
  
  &.success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
    }
  }
}

/* 历史考试列表 */
.history-exams {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .history-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
    
    .history-count {
      .el-tag {
        font-size: 12px;
        padding: 4px 8px;
      }
    }
  }
  
  .exams-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    
    .exam-item {
      background: #f7fafc;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 20px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        border-color: #667eea;
      }
      
      .exam-item-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        h4 {
          margin: 0;
          font-size: 1.1rem;
          font-weight: 600;
          color: #2d3748;
          flex: 1;
          margin-right: 12px;
        }
        
        .exam-status {
          .el-tag {
            font-size: 11px;
            padding: 2px 6px;
          }
        }
      }
      
      .exam-item-info {
        margin-bottom: 16px;
        
        .info-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          font-size: 13px;
          color: #718096;
          
          .info-icon {
            width: 14px;
            height: 14px;
            opacity: 0.6;
          }
          
          span {
            flex: 1;
          }
        }
      }
      
      .exam-item-actions {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        
        .el-button {
          font-size: 12px;
          padding: 6px 12px;
        }
      }
    }
  }
}

/* 生成的考核 */
.generated-exam {
  margin-top: 32px;
  
  .exam-card {
    background: white;
    border-radius: 20px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .exam-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .exam-icon {
          width: 32px;
          height: 32px;
          opacity: 0.7;
        }
        
        h3 {
          margin: 0;
          font-size: 1.5rem;
          font-weight: 700;
          color: #2d3748;
        }
      }
      
      .exam-info {
        display: flex;
        gap: 16px;
        
        .info-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #718096;
          
          .info-icon {
            width: 16px;
            height: 16px;
            opacity: 0.6;
          }
        }
      }
    }
    
    .exam-actions {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;
    }
    
    .questions-preview {
      .question-item {
        background: #f7fafc;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        padding: 20px;
        margin-bottom: 16px;
        
        .question-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
          
          .question-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .question-number {
              font-size: 16px;
              font-weight: 700;
              color: #667eea;
            }
            
            .question-type-badge {
              display: flex;
              align-items: center;
              gap: 4px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
              padding: 4px 8px;
              border-radius: 6px;
              font-size: 12px;
              font-weight: 600;
              
              .badge-icon {
                width: 12px;
                height: 12px;
                opacity: 0.8;
              }
            }
          }
          
          .question-score {
            background: #e6fffa;
            color: #319795;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
          }
        }
        
        .question-content {
          font-size: 14px;
          color: #2d3748;
          margin-bottom: 12px;
          line-height: 1.8;
          white-space: pre-wrap;
          word-wrap: break-word;
        }
        
        .content-section-title {
          font-weight: 600;
          color: #2d3748;
          margin: 16px 0 8px 0;
          padding: 8px 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-radius: 6px;
          font-size: 15px;
        }
        
        .content-line {
          margin: 6px 0;
          padding-left: 12px;
          color: #4a5568;
          line-height: 1.8;
        }
        
        .content-code {
          margin: 6px 0;
          padding: 8px 12px;
          background: #2d3748;
          color: #f8f9fa;
          border-radius: 4px;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 13px;
          overflow-x: auto;
        }
        
        .content-example {
          margin: 6px 0;
          padding: 8px 12px;
          background: #f0fff4;
          border-left: 4px solid #48bb78;
          border-radius: 4px;
          color: #2d3748;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 13px;
        }
        
        .question-options {
          margin-bottom: 12px;
          
          .option {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 0;
            
            .option-icon {
              width: 16px;
              height: 16px;
              opacity: 0.6;
            }
            
            .option-key {
              font-weight: 600;
              color: #667eea;
              min-width: 20px;
            }
            
            .option-content {
              font-size: 14px;
              color: #4a5568;
            }
          }
        }
        
        .question-answer {
          background: #f0fff4;
          border: 1px solid #c6f6d5;
          border-radius: 8px;
          padding: 12px;
          
          .answer-header {
            display: flex;
            align-items: center;
            gap: 6px;
            margin-bottom: 6px;
            
            .answer-icon {
              width: 16px;
              height: 16px;
              opacity: 0.7;
            }
            
            strong {
              font-size: 14px;
              color: #2d3748;
            }
          }
          
          span {
            font-size: 14px;
            color: #4a5568;
            line-height: 1.5;
          }
        }
      }
    }
  }
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  
  .modal-content {
    background: white;
    border-radius: 20px;
    max-width: 800px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      border-bottom: 1px solid #e2e8f0;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .modal-icon {
          width: 24px;
          height: 24px;
          opacity: 0.7;
        }
        
        h3 {
          margin: 0;
          font-size: 1.3rem;
          font-weight: 700;
          color: #2d3748;
        }
      }
      
      .close-btn {
        background: none;
        border: none;
        cursor: pointer;
        padding: 8px;
        border-radius: 8px;
        transition: all 0.3s ease;
        
        &:hover {
          background: #f7fafc;
        }
        
        .close-icon {
          width: 20px;
          height: 20px;
          opacity: 0.6;
        }
      }
    }
    
    .modal-body {
      padding: 24px;
      
      .exam-preview {
        .preview-header {
          text-align: center;
          margin-bottom: 32px;
          
          h2 {
            margin: 0 0 16px 0;
            font-size: 1.8rem;
            font-weight: 700;
            color: #2d3748;
          }
          
          .preview-info {
            .info-row {
              display: flex;
              align-items: center;
              justify-content: center;
              gap: 8px;
              margin-bottom: 8px;
              
              .info-icon {
                width: 16px;
                height: 16px;
                opacity: 0.6;
              }
              
              p {
                margin: 0;
                font-size: 14px;
                color: #718096;
              }
            }
          }
        }
        
        .preview-questions {
          .preview-question {
            margin-bottom: 24px;
            padding: 20px;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            
            .preview-question-header {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 12px;
              
              .preview-number {
                font-weight: 700;
                color: #667eea;
              }
              
              .preview-type {
                background: #e6fffa;
                color: #319795;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 600;
              }
              
              .preview-score {
                color: #718096;
                font-size: 14px;
              }
            }
            
            .preview-content {
              font-size: 14px;
              color: #2d3748;
              margin-bottom: 12px;
              line-height: 1.8;
              white-space: pre-wrap;
              word-wrap: break-word;
            }
            
            .preview-content .content-section-title {
              font-weight: 600;
              color: white;
              margin: 16px 0 8px 0;
              padding: 8px 12px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              border-radius: 6px;
              font-size: 15px;
            }
            
            .preview-content .content-line {
              margin: 6px 0;
              padding-left: 12px;
              color: #4a5568;
              line-height: 1.8;
            }
            
            .preview-content .content-code {
              margin: 6px 0;
              padding: 8px 12px;
              background: #2d3748;
              color: #f8f9fa;
              border-radius: 4px;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
              overflow-x: auto;
            }
            
            .preview-content .content-example {
              margin: 6px 0;
              padding: 8px 12px;
              background: #f0fff4;
              border-left: 4px solid #48bb78;
              border-radius: 4px;
              color: #2d3748;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
            }
            
            .preview-options {
              margin-bottom: 16px;
              
              .preview-option {
                display: flex;
                gap: 8px;
                padding: 4px 0;
                
                .preview-option-key {
                  font-weight: 600;
                  color: #667eea;
                  min-width: 20px;
                }
                
                .preview-option-content {
                  font-size: 14px;
                  color: #4a5568;
                }
              }
            }
            
            .preview-answer-space {
              .answer-line {
                height: 2px;
                background: #e2e8f0;
                margin: 16px 0;
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;
    
    .header-title h1 {
      font-size: 2rem;
    }
  }
  
  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .exam-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .exam-actions {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}

.exam-generation .el-card {
  margin-bottom: 24px;
  border-radius: 12px;
}
.exam-generation .el-card__header {
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}
.exam-generation .is-selected {
  border: 2px solid #409EFF;
  background: #f0faff;
}
.main-row {
  margin-bottom: 0 !important;
}

.type-programming {
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 8px;
  font-weight: bold;
}

.type-unknown {
  background: #f6f6f6;
  color: #666666;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 8px;
  font-weight: bold;
}

/* 课程标签页样式 */
.course-tabs {
  margin-top: 20px;
  
  .el-tabs__header {
    margin-bottom: 24px;
  }
  
  .el-tabs__item {
    font-size: 16px;
    font-weight: 600;
    padding: 12px 24px;
    
    &.is-active {
      color: #667eea;
    }
  }
  
  .el-tabs__active-bar {
    background-color: #667eea;
    height: 3px;
  }
  
  .el-tab-pane {
    padding: 0;
  }
}

/* 空状态提示 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  
  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    opacity: 0.3;
  }
  
  .empty-text {
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  .empty-desc {
    font-size: 14px;
    opacity: 0.7;
  }
}
</style> 