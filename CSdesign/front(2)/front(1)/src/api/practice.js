import request from '@/utils/request'

// 获取练习类型列表
export function getPracticeTypes() {
  return request({
    url: '/api/practice/types',
    method: 'get'
  })
}

// 获取推荐练习
export function getRecommendedPractices() {
  return request({
    url: '/api/practice/recommended',
    method: 'get'
  })
}

// 获取练习历史
export function getPracticeHistory(studentId) {
  return request({
    url: `/api/practice/history/${studentId}`,
    method: 'get'
  })
}

// 开始练习
export function startPractice(data) {
  return request({
    url: '/api/practice/start',
    method: 'post',
    data
  })
}

// 提交练习答案
export function submitPractice(data) {
  return request({
    url: '/api/practice/submit',
    method: 'post',
    data
  })
}

// 提交练习答案（兼容旧格式）
export function submitPracticeLegacy(data) {
  return request({
    url: '/api/practice/submit/legacy',
    method: 'post',
    data
  })
}

// 根据ID查询练习
export function getPracticeById(id) {
  return request({
    url: `/api/practice/${id}`,
    method: 'get'
  })
}

// 根据课程ID查询练习列表
export function getPracticesByCourseId(courseId) {
  return request({
    url: `/api/practice/course/${courseId}`,
    method: 'get'
  })
}

// 根据类型查询练习列表
export function getPracticesByType(type) {
  return request({
    url: `/api/practice/type/${type}`,
    method: 'get'
  })
}

// 根据难度查询练习列表
export function getPracticesByDifficulty(difficulty) {
  return request({
    url: `/api/practice/difficulty/${difficulty}`,
    method: 'get'
  })
}

// 分页查询练习
export function getPracticesByPage(params) {
  return request({
    url: '/api/practice/page',
    method: 'get',
    params
  })
}

// 创建练习
export function createPractice(data) {
  return request({
    url: '/api/practice',
    method: 'post',
    data
  })
}

// 更新练习
export function updatePractice(id, data) {
  return request({
    url: `/api/practice/${id}`,
    method: 'put',
    data
  })
}

// 删除练习
export function deletePractice(id) {
  return request({
    url: `/api/practice/${id}`,
    method: 'delete'
  })
}

// 获取学生练习记录
export function getStudentPractice(studentId, practiceId) {
  return request({
    url: `/api/practice/student/${studentId}/practice/${practiceId}`,
    method: 'get'
  })
}

// 获取学生的所有练习记录
export function getStudentPracticeHistory(studentId) {
  return request({
    url: `/api/practice/student/${studentId}/history`,
    method: 'get'
  })
}

// 获取练习的平均分
export function getPracticeAverageScore(practiceId) {
  return request({
    url: `/api/practice/${practiceId}/average-score`,
    method: 'get'
  })
}

// 获取学生的平均分
export function getStudentAverageScore(studentId) {
  return request({
    url: `/api/practice/student/${studentId}/average-score`,
    method: 'get'
  })
}

// 获取练习统计信息
export function getPracticeStats(practiceId) {
  return request({
    url: `/api/practice/${practiceId}/stats`,
    method: 'get'
  })
} 