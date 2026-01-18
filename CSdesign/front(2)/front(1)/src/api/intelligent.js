import request from '@/utils/request'

const intelligentApi = {
  // ============ 智能学习路径规划 API ============
  
  // 生成个性化学习路径
  generateLearningPath(data) {
    return request({
      url: '/api/intelligent/learning-path/generate',
      method: 'post',
      data
    })
  },

  // 获取学生学习路径列表
  getStudentLearningPaths(studentId) {
    return request({
      url: `/api/intelligent/learning-path/student/${studentId}`,
      method: 'get'
    })
  },

  // 更新学习进度
  updateLearningProgress(data) {
    return request({
      url: '/api/intelligent/learning-path/progress',
      method: 'post',
      data
    })
  },

  // 获取学习路径详情
  getLearningPathDetails(pathId) {
    return request({
      url: `/api/intelligent/learning-path/${pathId}/details`,
      method: 'get'
    })
  },

  // 获取下一步学习建议
  getNextLearningStep(pathId, studentId) {
    return request({
      url: `/api/intelligent/learning-path/${pathId}/next-step`,
      method: 'get',
      params: { studentId }
    })
  },

  // ============ 情绪感知学习助手 API ============
  
  // 记录学生情绪状态
  recordEmotionState(data) {
    return request({
      url: '/api/intelligent/emotion/record',
      method: 'post',
      data
    })
  },

  // 分析情绪并获取干预建议
  analyzeEmotionAndSuggest(data) {
    return request({
      url: '/api/intelligent/emotion/analyze',
      method: 'post',
      data
    })
  },

  // 获取情绪趋势分析
  getEmotionTrend(studentId, days = 7) {
    return request({
      url: `/api/intelligent/emotion/trend/${studentId}`,
      method: 'get',
      params: { days }
    })
  },

  // 记录学习状态监控数据
  recordLearningStatus(data) {
    return request({
      url: '/api/intelligent/emotion/learning-status',
      method: 'post',
      data
    })
  },

  // 获取学习状态统计
  getLearningStatusStats(studentId, hours = 24) {
    return request({
      url: `/api/intelligent/emotion/status-stats/${studentId}`,
      method: 'get',
      params: { hours }
    })
  },

  // ============ 智能同伴学习 API ============
  
  // 智能匹配学习伙伴
  findLearningCompanions(studentId, limit = 10) {
    return request({
      url: `/api/intelligent/companion/find/${studentId}`,
      method: 'get',
      params: { limit }
    })
  },

  // 创建学习伙伴关系
  createCompanionRelationship(data) {
    return request({
      url: '/api/intelligent/companion/create',
      method: 'post',
      data
    })
  },

  // 获取我的学习伙伴
  getMyLearningCompanions(studentId) {
    return request({
      url: `/api/intelligent/companion/my/${studentId}`,
      method: 'get'
    })
  },

  // 更新伙伴关系状态
  updateCompanionStatus(data) {
    return request({
      url: '/api/intelligent/companion/status',
      method: 'post',
      data
    })
  },

  // 创建学习小组
  createStudyGroup(data) {
    return request({
      url: '/api/intelligent/study-group/create',
      method: 'post',
      data
    })
  },

  // 获取学习小组列表
  getStudyGroups(studentId, subject, limit = 20) {
    return request({
      url: `/api/intelligent/study-group/list/${studentId}`,
      method: 'get',
      params: { subject, limit }
    })
  },

  // 加入学习小组
  joinStudyGroup(data) {
    return request({
      url: '/api/intelligent/study-group/join',
      method: 'post',
      data
    })
  },

  // 获取小组成员
  getGroupMembers(groupId) {
    return request({
      url: `/api/intelligent/study-group/${groupId}/members`,
      method: 'get'
    })
  },

  // 创建协作学习会话
  createCollaborativeSession(data) {
    return request({
      url: '/api/intelligent/collaborative-session/create',
      method: 'post',
      data
    })
  },

  // 获取即将到来的协作会话
  getUpcomingSessions(limit = 10) {
    return request({
      url: '/api/intelligent/collaborative-session/upcoming',
      method: 'get',
      params: { limit }
    })
  },

  // 记录协作互动
  recordCollaborativeInteraction(data) {
    return request({
      url: '/api/intelligent/collaborative-session/interaction',
      method: 'post',
      data
    })
  },

  // 评价协作伙伴
  rateCollaborativePartner(data) {
    return request({
      url: '/api/intelligent/companion/rate',
      method: 'post',
      data
    })
  }
}

export default intelligentApi 