import request from '@/utils/request'

// 创建考试
export function createExam(data) {
  return request({
    url: '/api/exam',
    method: 'post',
    data
  })
}

// 根据ID查询考试
export function getExamById(id) {
  return request({
    url: `/api/exam/${id}`,
    method: 'get'
  })
}

// 根据教师ID查询考试列表
export function getExamsByTeacherId(teacherId) {
  return request({
    url: `/api/exam/teacher/${teacherId}`,
    method: 'get'
  })
}

// 根据课程ID查询考试列表
export function getExamsByCourseId(courseId) {
  return request({
    url: `/api/exam/course/${courseId}`,
    method: 'get'
  })
}

// 根据课程ID查询已发布的考试列表（学生端使用）
export function getPublishedExamsByCourseId(courseId) {
  return request({
    url: `/api/exam/course/${courseId}/published`,
    method: 'get'
  })
}

// 分页查询考试
export function getExamsByPage(params) {
  return request({
    url: '/api/exam/page',
    method: 'get',
    params
  })
}

// 更新考试
export function updateExam(data) {
  return request({
    url: '/api/exam',
    method: 'put',
    data
  })
}

// 删除考试
export function deleteExam(id) {
  return request({
    url: `/api/exam/${id}`,
    method: 'delete'
  })
}

// 发布考试
export function publishExam(id) {
  return request({
    url: `/api/exam/${id}/publish`,
    method: 'put'
  })
}

// 归档考试
export function archiveExam(id) {
  return request({
    url: `/api/exam/${id}/archive`,
    method: 'put'
  })
}

// 智能生成考试
export function generateExam(data) {
  return request({
    url: '/api/exam/generate',
    method: 'post',
    data
  })
} 