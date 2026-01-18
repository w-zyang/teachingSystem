<template>
  <div class="learning-path-planning">
    <div class="section-header">
      <h3>🗺️ 智能学习路径规划</h3>
      <p>AI为您定制专属的学习路线图</p>
    </div>

    <!-- 创建新路径 -->
    <div class="create-path-card">
      <h4>📝 创建新的学习路径</h4>
      <div class="create-form">
        <div class="form-group">
          <label>学科领域</label>
          <select v-model="newPath.subject">
            <option value="">请选择学科</option>
            <option value="Java编程">Java编程</option>
            <option value="数据结构">数据结构</option>
            <option value="算法设计">算法设计</option>
            <option value="Web开发">Web开发</option>
            <option value="机器学习">机器学习</option>
            <option value="软件工程">软件工程</option>
          </select>
        </div>
        <div class="form-group">
          <label>目标级别</label>
          <select v-model="newPath.targetLevel">
            <option value="beginner">初学者</option>
            <option value="intermediate">中级</option>
            <option value="advanced">高级</option>
          </select>
        </div>
        <button @click="generatePath" :disabled="!newPath.subject || generating" class="generate-btn">
          <span v-if="generating">🔄 生成中...</span>
          <span v-else">🚀 生成智能路径</span>
        </button>
      </div>
    </div>

    <!-- 我的学习路径 -->
    <div class="my-paths-section">
      <h4>📚 我的学习路径</h4>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="learningPaths.length === 0" class="empty-state">
        <div class="empty-icon">📖</div>
        <p>还没有学习路径，快来创建第一个吧！</p>
      </div>
      <div v-else class="paths-grid">
        <div v-for="path in learningPaths" :key="path.id" class="path-card">
          <div class="path-header">
            <h5>{{ path.name }}</h5>
            <span class="path-status" :class="path.status">{{ getStatusText(path.status) }}</span>
          </div>
          
          <div class="path-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: (path.completionRate * 100) + '%' }"></div>
            </div>
            <span class="progress-text">{{ Math.round(path.completionRate * 100) }}%</span>
          </div>
          
          <div class="path-info">
            <div class="info-item">
              <span class="label">当前步骤:</span>
              <span class="value">第 {{ path.currentStep }} 步</span>
            </div>
            <div class="info-item">
              <span class="label">开始时间:</span>
              <span class="value">{{ formatDate(path.startTime) }}</span>
            </div>
            <div class="info-item" v-if="path.estimatedCompletion">
              <span class="label">预计完成:</span>
              <span class="value">{{ formatDate(path.estimatedCompletion) }}</span>
            </div>
          </div>
          
          <div class="path-actions">
            <button @click="viewPathDetails(path.id)" class="detail-btn">查看详情</button>
            <button @click="getNextStep(path.id)" class="next-btn">下一步建议</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 路径详情弹窗 -->
    <div v-if="showPathDetails" class="modal-overlay" @click="closePathDetails">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h4>📋 学习路径详情</h4>
          <button @click="closePathDetails" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div v-if="pathDetails">
            <div class="path-overview">
              <h5>{{ pathDetails.pathInfo.name }}</h5>
              <p>总体进度: {{ Math.round(pathDetails.pathInfo.completion_rate * 100) }}%</p>
            </div>
            <div class="steps-list">
              <h6>学习步骤</h6>
              <div v-for="step in pathDetails.steps" :key="step.step_number" class="step-item">
                <div class="step-number">{{ step.step_number }}</div>
                <div class="step-content">
                  <div class="step-title">{{ step.knowledge_name }}</div>
                  <div class="step-status" :class="step.step_status">
                    {{ getStepStatusText(step.step_status) }}
                  </div>
                  <div v-if="step.mastery_level" class="mastery-level">
                    掌握度: {{ Math.round(step.mastery_level * 100) }}%
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 下一步建议弹窗 -->
    <div v-if="showNextStep" class="modal-overlay" @click="closeNextStep">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h4>💡 下一步学习建议</h4>
          <button @click="closeNextStep" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div v-if="nextStepInfo">
            <div class="next-step-card">
              <h5>🎯 推荐学习内容</h5>
              <div class="step-info">
                <div class="info-row">
                  <span class="label">步骤:</span>
                  <span class="value">第 {{ nextStepInfo.stepNumber }} 步</span>
                </div>
                <div class="info-row">
                  <span class="label">知识点:</span>
                  <span class="value">{{ nextStepInfo.knowledgeName }}</span>
                </div>
                <div class="info-row">
                  <span class="label">难度级别:</span>
                  <span class="value difficulty" :class="'level-' + nextStepInfo.difficultyLevel">
                    {{ getDifficultyText(nextStepInfo.difficultyLevel) }}
                  </span>
                </div>
              </div>
              <div class="recommendation">
                <h6>📚 学习建议</h6>
                <p>{{ nextStepInfo.recommendation }}</p>
              </div>
              <div class="action-buttons">
                <button @click="startLearning" class="start-btn">开始学习</button>
                <button @click="markProgress" class="progress-btn">标记完成</button>
              </div>
            </div>
          </div>
          <div v-else>
            <div class="no-next-step">
              <div class="success-icon">🎉</div>
              <h5>恭喜！</h5>
              <p>您已完成当前学习路径的所有步骤</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import intelligentApi from '@/api/intelligent'

export default {
  name: 'LearningPathPlanning',
  props: {
    studentId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      generating: false,
      learningPaths: [],
      newPath: {
        subject: '',
        targetLevel: 'beginner'
      },
      showPathDetails: false,
      pathDetails: null,
      showNextStep: false,
      nextStepInfo: null,
      currentPathId: null
    }
  },
  mounted() {
    this.loadLearningPaths()
  },
  methods: {
    async loadLearningPaths() {
      this.loading = true
      try {
        const response = await intelligentApi.getStudentLearningPaths(this.studentId)
        if (response.data.flag) {
          this.learningPaths = response.data.data
        }
      } catch (error) {
        console.error('加载学习路径失败:', error)
        this.$message.error('加载学习路径失败')
      } finally {
        this.loading = false
      }
    },

    async generatePath() {
      if (!this.newPath.subject) {
        this.$message.warning('请选择学科领域')
        return
      }

      this.generating = true
      try {
        const response = await intelligentApi.generateLearningPath({
          studentId: this.studentId,
          subject: this.newPath.subject,
          targetLevel: this.newPath.targetLevel
        })

        if (response.data.flag) {
          this.$message.success('学习路径生成成功！')
          this.newPath = { subject: '', targetLevel: 'beginner' }
          this.loadLearningPaths()
        } else {
          this.$message.error(response.data.message || '生成失败')
        }
      } catch (error) {
        console.error('生成学习路径失败:', error)
        this.$message.error('生成学习路径失败')
      } finally {
        this.generating = false
      }
    },

    async viewPathDetails(pathId) {
      try {
        const response = await intelligentApi.getLearningPathDetails(pathId)
        if (response.data.flag) {
          this.pathDetails = response.data.data
          this.showPathDetails = true
        }
      } catch (error) {
        console.error('获取路径详情失败:', error)
        this.$message.error('获取路径详情失败')
      }
    },

    async getNextStep(pathId) {
      this.currentPathId = pathId
      try {
        const response = await intelligentApi.getNextLearningStep(pathId, this.studentId)
        if (response.data.flag) {
          this.nextStepInfo = response.data.data
        } else {
          this.nextStepInfo = null
        }
        this.showNextStep = true
      } catch (error) {
        console.error('获取下一步建议失败:', error)
        this.$message.error('获取下一步建议失败')
      }
    },

    async markProgress() {
      if (!this.nextStepInfo || !this.currentPathId) return

      try {
        const response = await intelligentApi.updateLearningProgress({
          pathId: this.currentPathId,
          stepNumber: this.nextStepInfo.stepNumber,
          masteryLevel: 0.8,
          studyDuration: 60
        })

        if (response.data.flag) {
          this.$message.success('进度更新成功！')
          this.closeNextStep()
          this.loadLearningPaths()
        }
      } catch (error) {
        console.error('更新进度失败:', error)
        this.$message.error('更新进度失败')
      }
    },

    startLearning() {
      this.$message.info('正在跳转到学习内容...')
      // 这里可以跳转到具体的学习页面
      this.closeNextStep()
    },

    closePathDetails() {
      this.showPathDetails = false
      this.pathDetails = null
    },

    closeNextStep() {
      this.showNextStep = false
      this.nextStepInfo = null
      this.currentPathId = null
    },

    getStatusText(status) {
      const statusMap = {
        'active': '进行中',
        'paused': '已暂停',
        'completed': '已完成',
        'abandoned': '已放弃'
      }
      return statusMap[status] || status
    },

    getStepStatusText(status) {
      const statusMap = {
        'pending': '待学习',
        'in_progress': '学习中',
        'completed': '已完成'
      }
      return statusMap[status] || '待学习'
    },

    getDifficultyText(level) {
      const difficultyMap = {
        1: '入门',
        2: '基础',
        3: '中级',
        4: '高级',
        5: '专家'
      }
      return difficultyMap[level] || '未知'
    },

    formatDate(dateStr) {
      if (!dateStr) return '未知'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style scoped>
.learning-path-planning {
  padding: 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 30px;
}

.section-header h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.section-header p {
  color: #7f8c8d;
}

.create-path-card {
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  color: white;
  padding: 25px;
  border-radius: 15px;
  margin-bottom: 30px;
}

.create-path-card h4 {
  margin-bottom: 20px;
}

.create-form {
  display: flex;
  gap: 20px;
  align-items: end;
  flex-wrap: wrap;
}

.form-group {
  display: flex;
  flex-direction: column;
  min-width: 150px;
}

.form-group label {
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group select {
  padding: 10px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
}

.generate-btn {
  background: #00b894;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.generate-btn:hover:not(:disabled) {
  background: #00a085;
  transform: translateY(-2px);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.my-paths-section h4 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.paths-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.path-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.path-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
}

.path-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.path-header h5 {
  color: #2c3e50;
  margin: 0;
}

.path-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: bold;
}

.path-status.active {
  background: #e8f8f5;
  color: #00b894;
}

.path-status.completed {
  background: #e8f4fd;
  color: #0984e3;
}

.path-status.paused {
  background: #fef7e0;
  color: #f39c12;
}

.path-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #00b894, #00cec9);
  transition: width 0.3s ease;
}

.progress-text {
  font-weight: bold;
  color: #2c3e50;
}

.path-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-item .label {
  color: #7f8c8d;
}

.info-item .value {
  color: #2c3e50;
  font-weight: 500;
}

.path-actions {
  display: flex;
  gap: 10px;
}

.detail-btn, .next-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.detail-btn {
  background: #ddd;
  color: #2c3e50;
}

.detail-btn:hover {
  background: #bdc3c7;
}

.next-btn {
  background: #74b9ff;
  color: white;
}

.next-btn:hover {
  background: #0984e3;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 15px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ecf0f1;
}

.modal-header h4 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 5px;
  color: #7f8c8d;
}

.close-btn:hover {
  color: #2c3e50;
}

.modal-body {
  padding: 20px;
}

.path-overview {
  text-align: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.steps-list h6 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.step-item {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.step-number {
  width: 30px;
  height: 30px;
  background: #74b9ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-title {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.step-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  margin-bottom: 5px;
}

.step-status.pending {
  background: #fef7e0;
  color: #f39c12;
}

.step-status.in_progress {
  background: #e8f4fd;
  color: #0984e3;
}

.step-status.completed {
  background: #e8f8f5;
  color: #00b894;
}

.mastery-level {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.next-step-card {
  background: linear-gradient(135deg, #a29bfe, #6c5ce7);
  color: white;
  padding: 25px;
  border-radius: 15px;
}

.next-step-card h5 {
  margin-bottom: 20px;
}

.step-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.info-row .label {
  opacity: 0.8;
}

.info-row .value {
  font-weight: bold;
}

.difficulty.level-1 { color: #00b894; }
.difficulty.level-2 { color: #74b9ff; }
.difficulty.level-3 { color: #fdcb6e; }
.difficulty.level-4 { color: #fd79a8; }
.difficulty.level-5 { color: #e84393; }

.recommendation {
  background: rgba(255, 255, 255, 0.1);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.recommendation h6 {
  margin-bottom: 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.start-btn, .progress-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.start-btn {
  background: #00b894;
  color: white;
}

.start-btn:hover {
  background: #00a085;
}

.progress-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.progress-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.no-next-step {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .create-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-group {
    min-width: unset;
  }
  
  .paths-grid {
    grid-template-columns: 1fr;
  }
  
  .path-actions {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 10px;
  }
}
</style> 