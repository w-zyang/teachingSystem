import request from '@/utils/request'

// 教师端 - 获取学生列表
export const getStudentsForTeacher = (teacherId) => {
  return request({
    url: `/api/teacher/${teacherId}/students`,
    method: 'get'
  })
}

// 教师端 - 获取学生详细数据
export const getStudentDetailData = (studentId) => {
  return request({
    url: `/api/student/${studentId}/dashboard-data`,
    method: 'get'
  })
}

// 管理员端 - 获取教师列表
export const getTeachersForAdmin = () => {
  return request({
    url: '/api/admin/teachers',
    method: 'get'
  })
}

// 管理员端 - 获取教师详细数据
export const getTeacherDetailData = (teacherId) => {
  return request({
    url: `/api/admin/teacher/${teacherId}/dashboard-data`,
    method: 'get'
  })
}

// 管理员端 - 获取学生列表
export const getAllStudentsForAdmin = () => {
  return request({
    url: '/api/admin/students',
    method: 'get'
  })
}

// 管理员端 - 获取学生详细数据
export const getStudentDetailDataForAdmin = (studentId) => {
  return request({
    url: `/api/admin/student/${studentId}/dashboard-data`,
    method: 'get'
  })
}

// 管理员端 - 获取系统整体数据
export const getSystemAnalytics = () => {
  return request({
    url: '/api/admin/system/analytics',
    method: 'get'
  })
}

// 获取智能学习功能使用统计
export const getIntelligentLearningStats = (userId, userType = 'student') => {
  return request({
    url: `/api/intelligent/stats/${userType}/${userId}`,
    method: 'get'
  })
}

// 获取情绪分析数据
export const getEmotionAnalysisData = (studentId) => {
  return request({
    url: `/api/emotion/analysis/${studentId}`,
    method: 'get'
  })
}

// 获取学习路径分析数据
export const getLearningPathAnalysis = (studentId) => {
  return request({
    url: `/api/learning-path/analysis/${studentId}`,
    method: 'get'
  })
}

// 获取同伴学习分析数据
export const getCompanionLearningAnalysis = (studentId) => {
  return request({
    url: `/api/companion/analysis/${studentId}`,
    method: 'get'
  })
}

export default {
  getStudentsForTeacher,
  getStudentDetailData,
  getTeachersForAdmin,
  getTeacherDetailData,
  getAllStudentsForAdmin,
  getStudentDetailDataForAdmin,
  getSystemAnalytics,
  getIntelligentLearningStats,
  getEmotionAnalysisData,
  getLearningPathAnalysis,
  getCompanionLearningAnalysis
} 