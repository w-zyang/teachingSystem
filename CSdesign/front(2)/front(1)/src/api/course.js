import request from '@/utils/request'

// 分页查询课程列表
export function getCourseList(params) {
  return request({
    url: '/api/course/list',
    method: 'get',
    params
  })
}

// 根据ID查询课程
export function getCourseById(id) {
  return request({
    url: `/api/course/${id}`,
    method: 'get'
  })
}

// 添加课程
export function addCourse(data) {
  return request({
    url: '/api/course/add',
    method: 'post',
    data
  })
}

// 更新课程
export function updateCourse(data) {
  return request({
    url: '/api/course/update',
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/api/course/${id}`,
    method: 'delete'
  })
}

// 更新课程状态
export function updateCourseStatus(id, status) {
  return request({
    url: `/api/course/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 根据教师ID查询课程列表
export function getCoursesByTeacherId(teacherId) {
  return request({
    url: `/api/course/teacher/${teacherId}`,
    method: 'get'
  })
}

// 根据学生ID查询课程列表
export function getCoursesByStudentId(studentId) {
  return request({
    url: `/api/course/student/${studentId}`,
    method: 'get'
  })
}

// AI生成课程大纲
export function generateCourseOutline(data) {
  return request({
    url: '/api/course/generate-outline',
    method: 'post',
    data
  })
}

// AI生成教学内容
export function generateCourseContent(chapterId) {
  return request({
    url: `/api/course/generate-content/${chapterId}`,
    method: 'post'
  })
}

// 获取课程统计信息
export function getCourseStats() {
  return request({
    url: '/api/course/stats',
    method: 'get'
  })
} 