import request from '@/utils/request'

// 获取教师统计信息
export function getTeacherStats(teacherId) {
  return request({
    url: `/api/stats/teacher/${teacherId}`,
    method: 'get'
  })
}

// 获取教师班级统计
export function getTeacherClassStats(teacherId) {
  return request({
    url: `/api/stats/teacher/${teacherId}/class`,
    method: 'get'
  })
}

// 获取学生统计信息
export function getStudentStats(studentId) {
  return request({
    url: `/api/stats/student/${studentId}`,
    method: 'get'
  })
}

// 获取系统统计信息
export function getSystemStats() {
  return request({
    url: '/api/stats/system',
    method: 'get'
  })
}

// 获取系统分析数据
export function getSystemAnalytics() {
  return request({
    url: '/api/stats/system',
    method: 'get'
  })
}

// 获取成绩分布
export function getGradeDistribution() {
  return request({
    url: '/api/stats/grade-distribution',
    method: 'get'
  })
}

// 获取错误知识点统计
export function getErrorTopics() {
  return request({
    url: '/api/stats/error-topics',
    method: 'get'
  })
}

// 获取学习趋势数据
export function getLearningTrend(studentId) {
  return request({
    url: `/api/stats/learning-trend/${studentId}`,
    method: 'get'
  })
}

// 获取分析数据
export function getAnalyticsData() {
  return request({
    url: '/api/stats/analytics',
    method: 'get'
  })
}

// 获取学习趋势
export function getLearningTrends() {
  return request({
    url: '/api/stats/learning-trends',
    method: 'get'
  })
}

// 获取性能指标
export function getPerformanceMetrics() {
  return request({
    url: '/api/stats/performance',
    method: 'get'
  })
}

// 获取使用统计
export function getUsageStats() {
  return request({
    url: '/api/stats/usage',
    method: 'get'
  })
}

// 获取效率指数
export function getEfficiencyIndex() {
  return request({
    url: '/api/stats/efficiency',
    method: 'get'
  })
}

// 获取系统状态
export function getSystemStatus() {
  return request({
    url: '/api/stats/status',
    method: 'get'
  })
}

// 获取系统信息
export function getSystemInfo() {
  return request({
    url: '/api/stats/info',
    method: 'get'
  })
}

// 获取课程统计
export function getCourseStats(courseId) {
  return request({
    url: `/api/stats/course/${courseId}`,
    method: 'get'
  })
}

// 获取资源列表
export function getResources(params) {
  return request({
    url: '/api/stats/resources',
    method: 'get',
    params
  })
}

// 上传资源
export function uploadResource(data) {
  return request({
    url: '/api/stats/resources/upload',
    method: 'post',
    data
  })
}

// 删除资源
export function deleteResource(id) {
  return request({
    url: `/api/stats/resources/${id}`,
    method: 'delete'
  })
}

// 更新资源
export function updateResource(data) {
  return request({
    url: '/api/stats/resources/update',
    method: 'put',
    data
  })
}

// 获取资源统计
export function getResourceStats() {
  return request({
    url: '/api/stats/resources/stats',
    method: 'get'
  })
}

// 获取教学效率趋势
export function getEfficiencyTrend(teacherId, type = 'month') {
  return request({
    url: `/api/stats/teacher/${teacherId}/efficiency-trend`,
    method: 'get',
    params: { type }
  })
} 