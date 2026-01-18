<template>
  <div class="emotion-aware-learning">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>🧠 情绪感知学习助手</h2>
      <p>通过智能分析您的学习情绪状态，提供个性化的学习建议和干预措施</p>
    </div>

    <!-- 实时情绪监控卡片 -->
    <div class="emotion-monitor-card">
      <h3>📊 实时情绪监控</h3>
      <div class="current-emotion">
        <div class="emotion-display">
          <div class="emotion-icon" :class="currentEmotion.type">
            {{ getEmotionIcon(currentEmotion.type) }}
          </div>
          <div class="emotion-info">
            <h4>{{ getEmotionName(currentEmotion.type) }}</h4>
            <div class="intensity-bar">
              <div class="intensity-fill" :style="{ width: (currentEmotion.intensity * 100) + '%' }"></div>
            </div>
            <span class="intensity-text">强度: {{ (currentEmotion.intensity * 100).toFixed(0) }}%</span>
          </div>
        </div>
        
        <div class="emotion-actions">
          <button @click="recordCurrentEmotion" class="btn btn-primary">
            📝 记录当前情绪
          </button>
          <button @click="requestSuggestions" class="btn btn-secondary">
            💡 获取建议
          </button>
        </div>
      </div>
    </div>

    <!-- 情绪记录表单 -->
    <div class="emotion-record-form" v-if="showRecordForm">
      <h3>✍️ 手动记录情绪</h3>
      <form @submit.prevent="submitEmotionRecord">
        <div class="form-row">
          <div class="form-group">
            <label>情绪类型:</label>
            <select v-model="emotionForm.emotionType" required>
              <option value="">请选择情绪类型</option>
              <option value="happy">😊 开心</option>
              <option value="focused">🎯 专注</option>
              <option value="confused">😕 困惑</option>
              <option value="frustrated">😤 沮丧</option>
              <option value="bored">😴 无聊</option>
              <option value="stressed">😰 压力</option>
              <option value="excited">🤩 兴奋</option>
              <option value="calm">😌 平静</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>情绪强度: {{ emotionForm.intensity }}</label>
            <input type="range" v-model="emotionForm.intensity" min="0" max="1" step="0.1">
          </div>
        </div>
        
        <div class="form-group">
          <label>学习情境:</label>
          <textarea v-model="emotionForm.context" placeholder="描述您当前的学习情况和感受..." rows="3"></textarea>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn btn-primary">保存记录</button>
          <button type="button" @click="showRecordForm = false" class="btn btn-secondary">取消</button>
        </div>
      </form>
    </div>

    <!-- 智能建议卡片 -->
    <div class="suggestions-card" v-if="suggestions.length > 0">
      <h3>💡 个性化学习建议</h3>
      <div class="suggestions-list">
        <div v-for="suggestion in suggestions" :key="suggestion.id" class="suggestion-item">
          <div class="suggestion-header">
            <span class="suggestion-type">{{ suggestion.suggestionType }}</span>
            <span class="suggestion-priority" :class="suggestion.priority">{{ suggestion.priority }}</span>
          </div>
          <p class="suggestion-content">{{ suggestion.content }}</p>
          <div class="suggestion-actions">
            <button @click="applySuggestion(suggestion)" class="btn btn-sm btn-primary">
              ✅ 采纳建议
            </button>
            <button @click="dismissSuggestion(suggestion)" class="btn btn-sm btn-secondary">
              ❌ 暂不采纳
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 情绪趋势分析 -->
    <div class="emotion-trend-card">
      <h3>📈 情绪趋势分析</h3>
      <div class="trend-controls">
        <button @click="loadTrendData(7)" :class="{ active: trendDays === 7 }" class="btn btn-sm">7天</button>
        <button @click="loadTrendData(14)" :class="{ active: trendDays === 14 }" class="btn btn-sm">14天</button>
        <button @click="loadTrendData(30)" :class="{ active: trendDays === 30 }" class="btn btn-sm">30天</button>
      </div>
      
      <div class="trend-chart" v-if="trendData.length > 0">
        <div class="chart-placeholder">
          <p>📊 情绪趋势图表 ({{ trendDays }}天)</p>
          <div class="trend-summary">
            <div class="trend-stats">
              <div class="stat-item">
                <span class="stat-label">平均情绪:</span>
                <span class="stat-value">{{ averageEmotion }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">波动程度:</span>
                <span class="stat-value">{{ emotionVolatility }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">学习效率:</span>
                <span class="stat-value">{{ learningEfficiency }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习状态监控 -->
    <div class="learning-status-card">
      <h3>⚡ 学习状态监控</h3>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-icon">🕐</div>
          <div class="status-info">
            <h4>学习时长</h4>
            <p>{{ learningStats.studyTime }}</p>
          </div>
        </div>
        
        <div class="status-item">
          <div class="status-icon">🎯</div>
          <div class="status-info">
            <h4>专注度</h4>
            <p>{{ learningStats.focusLevel }}%</p>
          </div>
        </div>
        
        <div class="status-item">
          <div class="status-icon">💪</div>
          <div class="status-info">
            <h4>疲劳度</h4>
            <p>{{ learningStats.fatigueLevel }}%</p>
          </div>
        </div>
        
        <div class="status-item">
          <div class="status-icon">📈</div>
          <div class="status-info">
            <h4>学习效率</h4>
            <p>{{ learningStats.efficiency }}%</p>
          </div>
        </div>
      </div>
      
      <div class="status-recommendations" v-if="statusRecommendations.length > 0">
        <h4>📋 状态建议</h4>
        <ul>
          <li v-for="rec in statusRecommendations" :key="rec">{{ rec }}</li>
        </ul>
      </div>
    </div>

    <!-- 干预历史记录 -->
    <div class="intervention-history" v-if="interventionHistory.length > 0">
      <h3>📋 干预记录</h3>
      <div class="intervention-list">
        <div v-for="intervention in interventionHistory" :key="intervention.id" class="intervention-item">
          <div class="intervention-info">
            <span class="intervention-time">{{ formatDate(intervention.interventionTime) }}</span>
            <span class="intervention-type">{{ intervention.interventionType }}</span>
          </div>
          <p class="intervention-content">{{ intervention.content }}</p>
          <div class="intervention-feedback" v-if="intervention.effectivenessScore">
            <span>效果评分: {{ intervention.effectivenessScore }}/5</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作按钮 -->
    <div class="quick-actions">
      <button @click="showRecordForm = !showRecordForm" class="btn btn-primary">
        📝 记录情绪
      </button>
      <button @click="startEmotionAnalysis" class="btn btn-secondary">
        🔍 分析当前状态
      </button>
      <button @click="exportEmotionData" class="btn btn-outline">
        📊 导出数据
      </button>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import intelligentApi from '@/api/intelligent'
import { ElMessage } from 'element-plus'

export default {
  name: 'EmotionAwareLearning',
  setup() {
    // 响应式数据
    const currentEmotion = ref({
      type: 'calm',
      intensity: 0.5,
      detectionTime: new Date()
    })
    
    const showRecordForm = ref(false)
    const suggestions = ref([])
    const trendData = ref([])
    const trendDays = ref(7)
    const interventionHistory = ref([])
    
    const emotionForm = reactive({
      emotionType: '',
      intensity: 0.5,
      context: ''
    })
    
    const learningStats = ref({
      studyTime: '0小时',
      focusLevel: 0,
      fatigueLevel: 0,
      efficiency: 0
    })
    
    const statusRecommendations = ref([])

    // 计算属性
    const averageEmotion = computed(() => {
      if (trendData.value.length === 0) return '暂无数据'
      const avg = trendData.value.reduce((sum, item) => sum + item.intensity, 0) / trendData.value.length
      return (avg * 100).toFixed(0) + '%'
    })
    
    const emotionVolatility = computed(() => {
      if (trendData.value.length < 2) return '稳定'
      const intensities = trendData.value.map(item => item.intensity)
      const avg = intensities.reduce((sum, val) => sum + val, 0) / intensities.length
      const variance = intensities.reduce((sum, val) => sum + Math.pow(val - avg, 2), 0) / intensities.length
      const volatility = Math.sqrt(variance)
      
      if (volatility < 0.1) return '稳定'
      if (volatility < 0.2) return '轻微波动'
      return '波动较大'
    })
    
    const learningEfficiency = computed(() => {
      return learningStats.value.efficiency
    })

    // 获取情绪图标
    const getEmotionIcon = (emotionType) => {
      const icons = {
        happy: '😊',
        focused: '🎯',
        confused: '😕',
        frustrated: '😤',
        bored: '😴',
        stressed: '😰',
        excited: '🤩',
        calm: '😌'
      }
      return icons[emotionType] || '😐'
    }
    
    // 获取情绪名称
    const getEmotionName = (emotionType) => {
      const names = {
        happy: '开心',
        focused: '专注',
        confused: '困惑',
        frustrated: '沮丧',
        bored: '无聊',
        stressed: '压力',
        excited: '兴奋',
        calm: '平静'
      }
      return names[emotionType] || '未知'
    }

    // 记录当前情绪
    const recordCurrentEmotion = async () => {
      try {
        const studentId = 17 // 实际应用中从用户状态获取
        const sessionId = 'session_' + Date.now()
        
        const result = await intelligentApi.recordEmotionState({
          studentId,
          sessionId,
          emotionType: currentEmotion.value.type,
          intensity: currentEmotion.value.intensity,
          context: '自动检测记录'
        })
        
        if (result.success) {
          ElMessage.success('情绪状态记录成功')
          await loadEmotionAnalysis()
        }
      } catch (error) {
        console.error('记录情绪状态失败:', error)
        ElMessage.error('记录失败，请稍后重试')
      }
    }

    // 提交情绪记录表单
    const submitEmotionRecord = async () => {
      try {
        const studentId = 17
        const sessionId = 'session_' + Date.now()
        
        const result = await intelligentApi.recordEmotionState({
          studentId,
          sessionId,
          emotionType: emotionForm.emotionType,
          intensity: emotionForm.intensity,
          context: emotionForm.context
        })
        
        if (result.success) {
          ElMessage.success('情绪记录提交成功')
          showRecordForm.value = false
          resetEmotionForm()
          await loadEmotionAnalysis()
        }
      } catch (error) {
        console.error('提交情绪记录失败:', error)
        ElMessage.error('提交失败，请稍后重试')
      }
    }

    // 重置表单
    const resetEmotionForm = () => {
      emotionForm.emotionType = ''
      emotionForm.intensity = 0.5
      emotionForm.context = ''
    }

    // 请求建议
    const requestSuggestions = async () => {
      try {
        const studentId = 17
        const sessionId = 'session_' + Date.now()
        
        const result = await intelligentApi.analyzeEmotionAndSuggest({
          studentId,
          sessionId
        })
        
        if (result.success && result.data) {
          suggestions.value = result.data.suggestions || []
          ElMessage.success('已获取个性化建议')
        }
      } catch (error) {
        console.error('获取建议失败:', error)
        ElMessage.error('获取建议失败，请稍后重试')
      }
    }

    // 加载情绪分析
    const loadEmotionAnalysis = async () => {
      try {
        const studentId = 17
        const sessionId = 'session_' + Date.now()
        
        const result = await intelligentApi.analyzeEmotionAndSuggest({
          studentId,
          sessionId
        })
        
        if (result.success && result.data) {
          currentEmotion.value = {
            type: result.data.currentEmotion || 'calm',
            intensity: result.data.intensity || 0.5,
            detectionTime: new Date(result.data.detectionTime || new Date())
          }
          suggestions.value = result.data.suggestions || []
        }
      } catch (error) {
        console.error('加载情绪分析失败:', error)
        // 使用模拟数据作为后备
        currentEmotion.value = {
          type: 'focused',
          intensity: 0.75,
          detectionTime: new Date()
        }
        suggestions.value = [
          {
            id: 1,
            suggestionType: '专注度提升',
            title: '保持专注状态',
            content: '您当前的专注度很好，建议继续保持当前的学习节奏。',
            priority: 'medium',
            category: 'focus'
          }
        ]
      }
    }

    // 加载趋势数据
    const loadTrendData = async (days) => {
      try {
        trendDays.value = days
        const studentId = 17
        
        const result = await intelligentApi.getEmotionTrend(studentId, days)
        
        if (result.success && result.data) {
          trendData.value = result.data.trends || []
        }
      } catch (error) {
        console.error('加载趋势数据失败:', error)
        // 使用模拟数据作为后备
        const mockTrends = []
        for (let i = days - 1; i >= 0; i--) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          mockTrends.push({
            date: date.toISOString().split('T')[0],
            emotion_type: ['happy', 'focused', 'calm', 'confused'][Math.floor(Math.random() * 4)],
            intensity: 0.3 + Math.random() * 0.7,
            avg_intensity: 0.4 + Math.random() * 0.4,
            count: Math.floor(Math.random() * 10) + 1
          })
        }
        trendData.value = mockTrends
      }
    }

    // 加载学习状态统计
    const loadLearningStats = async () => {
      try {
        const studentId = 17
        const hours = 24
        
        const result = await intelligentApi.getLearningStatusStats(studentId, hours)
        
        if (result.success && result.data) {
          learningStats.value = {
            studyTime: result.data.studyTime || '0小时',
            focusLevel: result.data.focusLevel || 0,
            fatigueLevel: result.data.fatigueLevel || 0,
            efficiency: result.data.efficiency || 0
          }
          
          // 生成状态建议
          generateStatusRecommendations()
        }
      } catch (error) {
        console.error('加载学习状态失败:', error)
        // 使用模拟数据作为后备
        learningStats.value = {
          studyTime: '3.5小时',
          focusLevel: 75,
          fatigueLevel: 35,
          efficiency: 82
        }
        
        // 生成状态建议
        generateStatusRecommendations()
      }
    }

    // 生成状态建议
    const generateStatusRecommendations = () => {
      const recommendations = []
      
      if (learningStats.value.fatigueLevel > 70) {
        recommendations.push('🛌 建议休息15-20分钟，恢复精力')
      }
      
      if (learningStats.value.focusLevel < 60) {
        recommendations.push('🎯 尝试番茄工作法，提高专注度')
      }
      
      if (learningStats.value.efficiency < 50) {
        recommendations.push('📚 调整学习方法，寻找更适合的学习策略')
      }
      
      statusRecommendations.value = recommendations
    }

    // 采纳建议
    const applySuggestion = (suggestion) => {
      ElMessage.success(`已采纳建议: ${suggestion.suggestionType}`)
      // 这里可以添加具体的建议应用逻辑
    }

    // 忽略建议
    const dismissSuggestion = (suggestion) => {
      const index = suggestions.value.findIndex(s => s.id === suggestion.id)
      if (index > -1) {
        suggestions.value.splice(index, 1)
      }
    }

    // 开始情绪分析
    const startEmotionAnalysis = async () => {
      ElMessage.info('开始分析情绪状态...')
      await loadEmotionAnalysis()
      await loadLearningStats()
    }

    // 导出情绪数据
    const exportEmotionData = () => {
      ElMessage.info('情绪数据导出功能开发中...')
    }

    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }

    // 组件挂载时初始化
    onMounted(() => {
      loadEmotionAnalysis()
      loadTrendData(7)
      loadLearningStats()
    })

    return {
      currentEmotion,
      showRecordForm,
      suggestions,
      trendData,
      trendDays,
      interventionHistory,
      emotionForm,
      learningStats,
      statusRecommendations,
      averageEmotion,
      emotionVolatility,
      learningEfficiency,
      getEmotionIcon,
      getEmotionName,
      recordCurrentEmotion,
      submitEmotionRecord,
      requestSuggestions,
      loadTrendData,
      applySuggestion,
      dismissSuggestion,
      startEmotionAnalysis,
      exportEmotionData,
      formatDate
    }
  }
}
</script>

<style scoped>
.emotion-aware-learning {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-header p {
  color: #7f8c8d;
  font-size: 16px;
}

/* 情绪监控卡片 */
.emotion-monitor-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.current-emotion {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.emotion-display {
  display: flex;
  align-items: center;
  gap: 20px;
}

.emotion-icon {
  font-size: 4rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.emotion-info h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.intensity-bar {
  width: 200px;
  height: 10px;
  background: #ecf0f1;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 8px;
}

.intensity-fill {
  height: 100%;
  background: linear-gradient(90deg, #2ecc71, #f39c12, #e74c3c);
  transition: width 0.3s ease;
}

.intensity-text {
  color: #7f8c8d;
  font-size: 14px;
}

.emotion-actions {
  display: flex;
  gap: 15px;
}

/* 表单样式 */
.emotion-record-form {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
}

.form-group select,
.form-group textarea,
.form-group input {
  width: 100%;
  padding: 10px;
  border: 2px solid #ecf0f1;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group select:focus,
.form-group textarea:focus,
.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

/* 建议卡片 */
.suggestions-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.suggestions-list {
  display: grid;
  gap: 15px;
}

.suggestion-item {
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  padding: 15px;
  transition: border-color 0.3s ease;
}

.suggestion-item:hover {
  border-color: #3498db;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.suggestion-type {
  font-weight: 600;
  color: #2c3e50;
}

.suggestion-priority {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  text-transform: uppercase;
}

.suggestion-priority.high {
  background: #e74c3c;
  color: white;
}

.suggestion-priority.medium {
  background: #f39c12;
  color: white;
}

.suggestion-priority.low {
  background: #2ecc71;
  color: white;
}

.suggestion-content {
  color: #34495e;
  margin-bottom: 15px;
  line-height: 1.6;
}

.suggestion-actions {
  display: flex;
  gap: 10px;
}

/* 趋势分析 */
.emotion-trend-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.trend-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.trend-controls .btn.active {
  background: #3498db;
  color: white;
}

.chart-placeholder {
  text-align: center;
  padding: 40px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #dee2e6;
}

.trend-summary {
  margin-top: 20px;
}

.trend-stats {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

/* 学习状态监控 */
.learning-status-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 25px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.status-icon {
  font-size: 2rem;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
}

.status-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.status-info p {
  margin: 0;
  color: #7f8c8d;
  font-size: 18px;
  font-weight: 600;
}

.status-recommendations h4 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.status-recommendations ul {
  list-style: none;
  padding: 0;
}

.status-recommendations li {
  padding: 8px 0;
  color: #34495e;
  border-bottom: 1px solid #ecf0f1;
}

/* 干预历史 */
.intervention-history {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.intervention-list {
  display: grid;
  gap: 15px;
}

.intervention-item {
  border-left: 4px solid #3498db;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 0 8px 8px 0;
}

.intervention-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.intervention-time {
  color: #7f8c8d;
  font-size: 14px;
}

.intervention-type {
  background: #3498db;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.intervention-content {
  color: #34495e;
  margin-bottom: 10px;
  line-height: 1.6;
}

.intervention-feedback {
  color: #f39c12;
  font-size: 14px;
  font-weight: 600;
}

/* 快速操作 */
.quick-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
}

/* 按钮样式 */
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
  transform: translateY(-2px);
}

.btn-outline {
  background: transparent;
  border: 2px solid #3498db;
  color: #3498db;
}

.btn-outline:hover {
  background: #3498db;
  color: white;
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .emotion-aware-learning {
    padding: 15px;
  }
  
  .current-emotion {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .trend-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style> 