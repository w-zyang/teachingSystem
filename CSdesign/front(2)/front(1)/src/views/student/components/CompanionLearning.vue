<template>
  <div class="companion-learning">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>👥 智能同伴学习</h2>
      <p>找到志同道合的学习伙伴，组建学习小组，开展协作学习</p>
    </div>

    <!-- 功能导航 -->
    <div class="feature-nav">
      <button 
        @click="activeTab = 'companions'" 
        :class="{ active: activeTab === 'companions' }"
        class="nav-btn"
      >
        🤝 学习伙伴
      </button>
      <button 
        @click="activeTab = 'groups'" 
        :class="{ active: activeTab === 'groups' }"
        class="nav-btn"
      >
        👥 学习小组
      </button>
      <button 
        @click="activeTab = 'sessions'" 
        :class="{ active: activeTab === 'sessions' }"
        class="nav-btn"
      >
        💡 协作会话
      </button>
    </div>

    <!-- 学习伙伴模块 -->
    <div v-if="activeTab === 'companions'" class="companions-section">
      <!-- 推荐伙伴 -->
      <div class="recommended-companions">
        <h3>🔍 推荐学习伙伴</h3>
        <div class="companion-filters">
          <select v-model="companionFilter.subject">
            <option value="">全部学科</option>
            <option value="计算机科学">计算机科学</option>
            <option value="数学">数学</option>
            <option value="英语">英语</option>
            <option value="物理">物理</option>
          </select>
          <button @click="findCompanions" class="btn btn-primary">🔍 智能匹配</button>
        </div>
        
        <div class="companions-grid" v-if="recommendedCompanions.length > 0">
          <div 
            v-for="companion in recommendedCompanions" 
            :key="companion.partner_id" 
            class="companion-card"
          >
            <div class="companion-avatar">
              {{ companion.real_name.charAt(0) }}
            </div>
            <div class="companion-info">
              <h4>{{ companion.real_name }}</h4>
              <p class="companion-major">{{ companion.major }}</p>
              <p class="companion-grade">{{ companion.grade }}年级</p>
              <div class="companion-stats">
                <span class="stat-item">
                  📊 平均分: {{ companion.avg_score }}
                </span>
                <span class="stat-item">
                  📚 共同课程: {{ companion.common_courses }}
                </span>
              </div>
              <div class="matching-score">
                匹配度: {{ (companion.matching_score * 100).toFixed(0) }}%
              </div>
            </div>
            <div class="companion-actions">
              <button 
                @click="sendCompanionRequest(companion.partner_id)"
                class="btn btn-sm btn-primary"
              >
                🤝 发送请求
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <p>📭 暂无推荐伙伴，点击智能匹配找到合适的学习伙伴</p>
        </div>
      </div>

      <!-- 我的学习伙伴 -->
      <div class="my-companions">
        <h3>👫 我的学习伙伴</h3>
        <div class="companions-list" v-if="myCompanions.length > 0">
          <div 
            v-for="companion in myCompanions" 
            :key="companion.id" 
            class="companion-item"
          >
            <div class="companion-basic">
              <div class="companion-avatar">{{ companion.student1_name.charAt(0) }}</div>
              <div class="companion-details">
                <h4>{{ companion.student1_name }}</h4>
                <p>{{ companion.student1_major }}</p>
                <span class="companion-status" :class="companion.status">
                  {{ getStatusText(companion.status) }}
                </span>
              </div>
            </div>
            <div class="companion-score">
              匹配度: {{ (companion.matching_score * 100).toFixed(0) }}%
            </div>
            <div class="companion-actions">
              <button 
                v-if="companion.status === 'pending'"
                @click="updateCompanionStatus(companion.id, 'active')"
                class="btn btn-sm btn-success"
              >
                ✅ 接受
              </button>
              <button 
                v-if="companion.status === 'active'"
                @click="startCollaboration(companion)"
                class="btn btn-sm btn-primary"
              >
                💬 开始协作
              </button>
              <button 
                @click="rateCompanion(companion.id)"
                class="btn btn-sm btn-secondary"
              >
                ⭐ 评价
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <p>🔍 还没有学习伙伴，去找找志同道合的同学吧！</p>
        </div>
      </div>
    </div>

    <!-- 学习小组模块 -->
    <div v-if="activeTab === 'groups'" class="groups-section">
      <!-- 创建小组 -->
      <div class="create-group-section">
        <h3>➕ 创建学习小组</h3>
        <button @click="showCreateGroupForm = !showCreateGroupForm" class="btn btn-primary">
          {{ showCreateGroupForm ? '取消创建' : '创建新小组' }}
        </button>
        
        <div v-if="showCreateGroupForm" class="create-group-form">
          <form @submit.prevent="createStudyGroup">
            <div class="form-row">
              <div class="form-group">
                <label>小组名称:</label>
                <input v-model="groupForm.groupName" type="text" required placeholder="输入小组名称">
              </div>
              <div class="form-group">
                <label>学科:</label>
                <select v-model="groupForm.subject" required>
                  <option value="">选择学科</option>
                  <option value="计算机科学">计算机科学</option>
                  <option value="数学">数学</option>
                  <option value="英语">英语</option>
                  <option value="物理">物理</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>最大成员数:</label>
                <select v-model="groupForm.maxMembers">
                  <option value="4">4人</option>
                  <option value="6" selected>6人</option>
                  <option value="8">8人</option>
                  <option value="10">10人</option>
                </select>
              </div>
              <div class="form-group">
                <label>隐私设置:</label>
                <select v-model="groupForm.privacyLevel">
                  <option value="public">公开</option>
                  <option value="private">私有</option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label>小组描述:</label>
              <textarea v-model="groupForm.groupDescription" rows="3" placeholder="描述小组的学习目标和规则"></textarea>
            </div>
            
            <div class="form-group">
              <label>学习目标:</label>
              <textarea v-model="groupForm.learningGoals" rows="2" placeholder="设定具体的学习目标"></textarea>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn btn-primary">创建小组</button>
              <button type="button" @click="resetGroupForm" class="btn btn-secondary">重置</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 推荐小组 -->
      <div class="recommended-groups">
        <h3>🎯 推荐学习小组</h3>
        <div class="group-filters">
          <select v-model="groupFilter.subject">
            <option value="">全部学科</option>
            <option value="计算机科学">计算机科学</option>
            <option value="数学">数学</option>
            <option value="英语">英语</option>
          </select>
          <button @click="loadStudyGroups" class="btn btn-secondary">🔄 刷新</button>
        </div>
        
        <div class="groups-grid" v-if="studyGroups.length > 0">
          <div 
            v-for="group in studyGroups" 
            :key="group.id" 
            class="group-card"
          >
            <div class="group-header">
              <h4>{{ group.group_name }}</h4>
              <span class="group-subject">{{ group.subject }}</span>
            </div>
            <div class="group-info">
              <p class="group-description">{{ group.group_description }}</p>
              <div class="group-stats">
                <span class="stat-item">
                  👥 成员: {{ group.member_count }}/{{ group.max_members }}
                </span>
                <span class="stat-item">
                  👤 组长: {{ group.leader_name }}
                </span>
              </div>
              <div class="group-goals">
                <strong>学习目标:</strong> {{ group.learning_goals }}
              </div>
            </div>
            <div class="group-actions">
              <button 
                @click="joinStudyGroup(group.id)"
                class="btn btn-sm btn-primary"
                :disabled="group.member_count >= group.max_members"
              >
                {{ group.member_count >= group.max_members ? '已满员' : '🚀 加入小组' }}
              </button>
              <button 
                @click="viewGroupDetails(group.id)"
                class="btn btn-sm btn-secondary"
              >
                👀 查看详情
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 协作会话模块 -->
    <div v-if="activeTab === 'sessions'" class="sessions-section">
      <!-- 创建会话 -->
      <div class="create-session-section">
        <h3>💡 创建协作会话</h3>
        <button @click="showCreateSessionForm = !showCreateSessionForm" class="btn btn-primary">
          {{ showCreateSessionForm ? '取消创建' : '创建新会话' }}
        </button>
        
        <div v-if="showCreateSessionForm" class="create-session-form">
          <form @submit.prevent="createCollaborativeSession">
            <div class="form-row">
              <div class="form-group">
                <label>会话名称:</label>
                <input v-model="sessionForm.sessionName" type="text" required placeholder="输入会话主题">
              </div>
              <div class="form-group">
                <label>会话类型:</label>
                <select v-model="sessionForm.sessionType" required>
                  <option value="study">学习讨论</option>
                  <option value="homework">作业互助</option>
                  <option value="project">项目合作</option>
                  <option value="exam">考试复习</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>学科:</label>
                <select v-model="sessionForm.subject" required>
                  <option value="">选择学科</option>
                  <option value="计算机科学">计算机科学</option>
                  <option value="数学">数学</option>
                  <option value="英语">英语</option>
                </select>
              </div>
              <div class="form-group">
                <label>最大参与者:</label>
                <select v-model="sessionForm.maxParticipants">
                  <option value="2">2人</option>
                  <option value="4" selected>4人</option>
                  <option value="6">6人</option>
                  <option value="8">8人</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>开始时间:</label>
                <input v-model="sessionForm.startTime" type="datetime-local" required>
              </div>
              <div class="form-group">
                <label>预计时长(分钟):</label>
                <input v-model="sessionForm.estimatedDuration" type="number" min="15" max="240" required>
              </div>
            </div>
            
            <div class="form-group">
              <label>会话描述:</label>
              <textarea v-model="sessionForm.sessionDescription" rows="3" placeholder="描述会话内容和目标"></textarea>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn btn-primary">创建会话</button>
              <button type="button" @click="resetSessionForm" class="btn btn-secondary">重置</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 即将开始的会话 -->
      <div class="upcoming-sessions">
        <h3>⏰ 即将开始的会话</h3>
        <div class="sessions-list" v-if="upcomingSessions.length > 0">
          <div 
            v-for="session in upcomingSessions" 
            :key="session.id" 
            class="session-card"
          >
            <div class="session-header">
              <h4>{{ session.session_name }}</h4>
              <span class="session-type" :class="session.session_type">
                {{ getSessionTypeText(session.session_type) }}
              </span>
            </div>
            <div class="session-info">
              <div class="session-details">
                <p><strong>学科:</strong> {{ session.subject }}</p>
                <p><strong>主题:</strong> {{ session.topic }}</p>
                <p><strong>创建者:</strong> {{ session.creator_name }}</p>
                <p><strong>开始时间:</strong> {{ formatDateTime(session.start_time) }}</p>
                <p><strong>预计时长:</strong> {{ session.estimated_duration }}分钟</p>
              </div>
              <div class="session-participants">
                <span class="participant-count">
                  👥 {{ session.participant_count }}/{{ session.max_participants }}
                </span>
              </div>
            </div>
            <div class="session-actions">
              <button 
                @click="joinSession(session.id)"
                class="btn btn-sm btn-primary"
                :disabled="session.participant_count >= session.max_participants"
              >
                {{ session.participant_count >= session.max_participants ? '已满员' : '🚀 加入会话' }}
              </button>
              <button 
                @click="setSessionReminder(session.id)"
                class="btn btn-sm btn-secondary"
              >
                ⏰ 设置提醒
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <p>📅 暂无即将开始的协作会话</p>
        </div>
      </div>
    </div>

    <!-- 评价对话框 -->
    <div v-if="showRatingDialog" class="rating-dialog-overlay">
      <div class="rating-dialog">
        <h3>⭐ 评价学习伙伴</h3>
        <div class="rating-section">
          <label>协作满意度:</label>
          <div class="star-rating">
            <span 
              v-for="star in 5" 
              :key="star"
              @click="setRating(star)"
              :class="{ active: star <= currentRating }"
              class="star"
            >
              ⭐
            </span>
          </div>
        </div>
        <div class="rating-actions">
          <button @click="submitRating" class="btn btn-primary">提交评价</button>
          <button @click="closeRatingDialog" class="btn btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import intelligentApi from '@/api/intelligent'

export default {
  name: 'CompanionLearning',
  setup() {
    // 响应式状态
    const activeTab = ref('companions')
    const showCreateGroupForm = ref(false)
    const showCreateSessionForm = ref(false)
    const showRatingDialog = ref(false)
    const currentRating = ref(0)
    const ratingCompanionId = ref(null)

    // 数据状态
    const recommendedCompanions = ref([])
    const myCompanions = ref([])
    const studyGroups = ref([])
    const upcomingSessions = ref([])

    // 过滤器
    const companionFilter = reactive({
      subject: ''
    })
    
    const groupFilter = reactive({
      subject: ''
    })

    // 表单数据
    const groupForm = reactive({
      groupName: '',
      subject: '',
      maxMembers: 6,
      privacyLevel: 'public',
      groupDescription: '',
      learningGoals: '',
      groupRules: ''
    })

    const sessionForm = reactive({
      sessionName: '',
      sessionType: 'study',
      subject: '',
      topic: '',
      maxParticipants: 4,
      sessionDescription: '',
      startTime: '',
      estimatedDuration: 60,
      learningGoals: ''
    })

    // 工具方法
    const getStatusText = (status) => {
      const statusMap = {
        pending: '待确认',
        active: '已激活',
        inactive: '已暂停'
      }
      return statusMap[status] || status
    }

    const getSessionTypeText = (type) => {
      const typeMap = {
        study: '学习讨论',
        homework: '作业互助',
        project: '项目合作',
        exam: '考试复习'
      }
      return typeMap[type] || type
    }

    const formatDateTime = (dateTimeString) => {
      const date = new Date(dateTimeString)
      return date.toLocaleString('zh-CN')
    }

    // API调用方法
    const findCompanions = async () => {
      try {
        const studentId = 17 // 实际应用中从用户状态获取
        const result = await intelligentApi.findLearningCompanions(studentId, 10)
        
        if (result.success) {
          recommendedCompanions.value = result.data || []
        }
      } catch (error) {
        console.error('查找学习伙伴失败:', error)
        // 使用模拟数据作为后备
        recommendedCompanions.value = [
          {
            id: 18,
            realName: '张同学',
            major: '计算机科学',
            grade: '大二',
            matchingScore: 0.85,
            commonSubjects: ['数据结构', '算法设计'],
            avgScore: 88,
            strengthAreas: ['编程', '数学']
          },
          {
            id: 19,
            realName: '王同学',
            major: '软件工程',
            grade: '大二',
            matchingScore: 0.78,
            commonSubjects: ['软件工程', '数据库'],
            avgScore: 85,
            strengthAreas: ['项目管理', '测试']
          }
        ]
      }
    }

    const sendCompanionRequest = async (partnerId) => {
      try {
        const studentId = 17
        const result = await intelligentApi.createCompanionRelationship({
          studentId,
          partnerId
        })
        
        if (result.success) {
          console.log('伙伴请求发送成功')
          await loadMyCompanions()
        }
      } catch (error) {
        console.error('发送伙伴请求失败:', error)
      }
    }

    const loadMyCompanions = async () => {
      try {
        const studentId = 17
        const result = await intelligentApi.getMyLearningCompanions(studentId)
        
        if (result.success) {
          myCompanions.value = result.data || []
        }
      } catch (error) {
        console.error('加载我的学习伙伴失败:', error)
      }
    }

    const updateCompanionStatus = async (matchId, status) => {
      try {
        const result = await intelligentApi.updateCompanionStatus({
          matchId,
          status
        })
        
        if (result.success) {
          console.log('伙伴状态更新成功')
          await loadMyCompanions()
        }
      } catch (error) {
        console.error('更新伙伴状态失败:', error)
      }
    }

    const createStudyGroup = async () => {
      try {
        const studentId = 17
        const groupData = {
          ...groupForm,
          leaderId: studentId
        }
        
        const result = await intelligentApi.createStudyGroup(groupData)
        
        if (result.success) {
          console.log('学习小组创建成功')
          showCreateGroupForm.value = false
          resetGroupForm()
          await loadStudyGroups()
        }
      } catch (error) {
        console.error('创建学习小组失败:', error)
      }
    }

    const loadStudyGroups = async () => {
      try {
        const studentId = 17
        const result = await intelligentApi.getStudyGroups(studentId, groupFilter.subject, 20)
        
        if (result.success) {
          studyGroups.value = result.data || []
        }
      } catch (error) {
        console.error('加载学习小组失败:', error)
        // 使用模拟数据作为后备
        studyGroups.value = [
          {
            id: 1,
            groupName: '算法学习小组',
            groupDescription: '一起学习数据结构与算法',
            subject: '计算机科学',
            leaderName: '李老师',
            memberCount: 8,
            maxMembers: 12,
            activityLevel: 95,
            groupType: 'study',
            privacyLevel: 'public'
          },
          {
            id: 2,
            groupName: '前端开发交流群',
            groupDescription: '分享前端技术，一起做项目',
            subject: '软件工程',
            leaderName: '陈同学',
            memberCount: 6,
            maxMembers: 10,
            activityLevel: 88,
            groupType: 'project',
            privacyLevel: 'public'
          }
        ]
      }
    }

    const joinStudyGroup = async (groupId) => {
      try {
        const studentId = 17
        const result = await intelligentApi.joinStudyGroup({
          groupId,
          studentId
        })
        
        if (result.success) {
          console.log('加入学习小组成功')
          await loadStudyGroups()
        }
      } catch (error) {
        console.error('加入学习小组失败:', error)
      }
    }

    const createCollaborativeSession = async () => {
      try {
        const studentId = 17
        const sessionData = {
          ...sessionForm,
          creatorId: studentId
        }
        
        const result = await intelligentApi.createCollaborativeSession(sessionData)
        
        if (result.success) {
          console.log('协作会话创建成功')
          showCreateSessionForm.value = false
          resetSessionForm()
          await loadUpcomingSessions()
        }
      } catch (error) {
        console.error('创建协作会话失败:', error)
      }
    }

    const loadUpcomingSessions = async () => {
      try {
        const result = await intelligentApi.getUpcomingSessions(20)
        
        if (result.success) {
          upcomingSessions.value = result.data || []
        }
      } catch (error) {
        console.error('加载即将开始的会话失败:', error)
      }
    }

    const rateCompanion = (companionId) => {
      ratingCompanionId.value = companionId
      showRatingDialog.value = true
      currentRating.value = 0
    }

    const setRating = (rating) => {
      currentRating.value = rating
    }

    const submitRating = async () => {
      try {
        const result = await intelligentApi.rateCollaborativePartner({
          companionId: ratingCompanionId.value,
          rating: currentRating.value
        })
        
        if (result.success) {
          console.log('评价提交成功')
          closeRatingDialog()
        }
      } catch (error) {
        console.error('提交评价失败:', error)
      }
    }

    const closeRatingDialog = () => {
      showRatingDialog.value = false
      ratingCompanionId.value = null
      currentRating.value = 0
    }

    // 表单重置方法
    const resetGroupForm = () => {
      Object.keys(groupForm).forEach(key => {
        if (key === 'maxMembers') {
          groupForm[key] = 6
        } else if (key === 'privacyLevel') {
          groupForm[key] = 'public'
        } else {
          groupForm[key] = ''
        }
      })
    }

    const resetSessionForm = () => {
      Object.keys(sessionForm).forEach(key => {
        if (key === 'sessionType') {
          sessionForm[key] = 'study'
        } else if (key === 'maxParticipants') {
          sessionForm[key] = 4
        } else if (key === 'estimatedDuration') {
          sessionForm[key] = 60
        } else {
          sessionForm[key] = ''
        }
      })
    }

    // 其他功能方法
    const startCollaboration = (companion) => {
      console.log('开始与伙伴协作:', companion)
      // 这里可以跳转到协作界面或打开聊天窗口
    }

    const viewGroupDetails = (groupId) => {
      console.log('查看小组详情:', groupId)
      // 这里可以打开小组详情页面
    }

    const joinSession = (sessionId) => {
      console.log('加入会话:', sessionId)
      // 这里可以跳转到会话页面
    }

    const setSessionReminder = (sessionId) => {
      console.log('设置会话提醒:', sessionId)
      // 这里可以设置浏览器通知或其他提醒
    }

    // 组件挂载时初始化
    onMounted(() => {
      loadMyCompanions()
      loadStudyGroups()
      loadUpcomingSessions()
    })

    return {
      activeTab,
      showCreateGroupForm,
      showCreateSessionForm,
      showRatingDialog,
      currentRating,
      recommendedCompanions,
      myCompanions,
      studyGroups,
      upcomingSessions,
      companionFilter,
      groupFilter,
      groupForm,
      sessionForm,
      getStatusText,
      getSessionTypeText,
      formatDateTime,
      findCompanions,
      sendCompanionRequest,
      updateCompanionStatus,
      createStudyGroup,
      loadStudyGroups,
      joinStudyGroup,
      createCollaborativeSession,
      loadUpcomingSessions,
      rateCompanion,
      setRating,
      submitRating,
      closeRatingDialog,
      resetGroupForm,
      resetSessionForm,
      startCollaboration,
      viewGroupDetails,
      joinSession,
      setSessionReminder
    }
  }
}
</script>

<style scoped>
.companion-learning {
  padding: 20px;
  max-width: 1400px;
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

/* 功能导航 */
.feature-nav {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  justify-content: center;
}

.nav-btn {
  padding: 12px 24px;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn:hover {
  border-color: #3498db;
  background: #f8f9fa;
}

.nav-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

/* 通用卡片样式 */
.recommended-companions,
.my-companions,
.create-group-section,
.recommended-groups,
.create-session-section,
.upcoming-sessions {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 伙伴卡片 */
.companions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.companion-card {
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.companion-card:hover {
  border-color: #3498db;
  transform: translateY(-2px);
}

.companion-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.companion-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.companion-major {
  color: #7f8c8d;
  margin: 0 0 5px 0;
}

.companion-grade {
  color: #95a5a6;
  margin: 0 0 10px 0;
  font-size: 14px;
}

.companion-stats {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 10px;
}

.stat-item {
  font-size: 14px;
  color: #34495e;
}

.matching-score {
  background: #e8f4fd;
  color: #2980b9;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 15px;
}

.companion-actions {
  display: flex;
  gap: 10px;
}

/* 伙伴列表 */
.companions-list {
  display: grid;
  gap: 15px;
}

.companion-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  transition: border-color 0.3s ease;
}

.companion-item:hover {
  border-color: #3498db;
}

.companion-basic {
  display: flex;
  align-items: center;
  gap: 15px;
}

.companion-basic .companion-avatar {
  width: 50px;
  height: 50px;
  font-size: 18px;
  margin-bottom: 0;
}

.companion-details h4 {
  margin: 0 0 5px 0;
}

.companion-details p {
  margin: 0 0 5px 0;
  color: #7f8c8d;
  font-size: 14px;
}

.companion-status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.companion-status.pending {
  background: #fff3cd;
  color: #856404;
}

.companion-status.active {
  background: #d4edda;
  color: #155724;
}

/* 过滤器 */
.companion-filters,
.group-filters {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
}

.companion-filters select,
.group-filters select {
  padding: 8px 12px;
  border: 2px solid #ecf0f1;
  border-radius: 6px;
  font-size: 14px;
}

/* 表单样式 */
.create-group-form,
.create-session-form {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
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

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 2px solid #ecf0f1;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3498db;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

/* 小组和会话卡片 */
.groups-grid,
.sessions-list {
  display: grid;
  gap: 20px;
  margin-top: 20px;
}

.group-card,
.session-card {
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.group-card:hover,
.session-card:hover {
  border-color: #3498db;
  transform: translateY(-2px);
}

.group-header,
.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.group-header h4,
.session-header h4 {
  margin: 0;
  color: #2c3e50;
}

.group-subject,
.session-type {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.group-subject {
  background: #e8f4fd;
  color: #2980b9;
}

.session-type.study {
  background: #d4edda;
  color: #155724;
}

.session-type.homework {
  background: #fff3cd;
  color: #856404;
}

.session-type.project {
  background: #e2e3e5;
  color: #383d41;
}

.session-type.exam {
  background: #f8d7da;
  color: #721c24;
}

.group-description,
.group-goals {
  margin-bottom: 10px;
  color: #34495e;
  line-height: 1.5;
}

.group-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.group-actions,
.session-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

/* 评价对话框 */
.rating-dialog-overlay {
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
}

.rating-dialog {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
}

.rating-dialog h3 {
  margin: 0 0 20px 0;
  text-align: center;
  color: #2c3e50;
}

.rating-section {
  margin-bottom: 25px;
}

.rating-section label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #2c3e50;
}

.star-rating {
  display: flex;
  gap: 5px;
  justify-content: center;
}

.star {
  font-size: 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.3;
}

.star.active {
  opacity: 1;
  transform: scale(1.1);
}

.rating-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
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
}

.btn-success {
  background: #2ecc71;
  color: white;
}

.btn-success:hover {
  background: #27ae60;
}

.btn-sm {
  padding: 8px 16px;
  font-size: 13px;
}

.btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #7f8c8d;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .companion-learning {
    padding: 15px;
  }
  
  .feature-nav {
    flex-direction: column;
    gap: 10px;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .companions-grid {
    grid-template-columns: 1fr;
  }
  
  .companion-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .companion-actions,
  .group-actions,
  .session-actions {
    width: 100%;
    justify-content: center;
  }
}
</style> 