import request from '@/utils/request'

/**
 * 智能备课助手API（精简版 - 避免功能冲突）
 * 
 * 只包含3个核心功能：
 * 1. 课件更新助手
 * 2. 实验指导书生成
 * 3. 主观题批阅助手
 */

// ==================== 1. 课件更新助手 ====================

// 更新课件案例
export const updateCourseware = (data) => {
  return request({
    url: '/api/smart-teaching/update-courseware',
    method: 'post',
    data
  })
}

// 获取课件更新建议
export const getCoursewareSuggestions = (updateId) => {
  return request({
    url: `/api/smart-teaching/courseware-suggestions/${updateId}`,
    method: 'get'
  })
}

// 获取课件更新记录
export const getCoursewareUpdates = (teacherId) => {
  return request({
    url: `/api/smart-teaching/courseware-updates/${teacherId}`,
    method: 'get'
  })
}

// 删除课件更新记录
export const deleteCoursewareUpdate = (updateId) => {
  return request({
    url: `/api/smart-teaching/courseware-update/${updateId}`,
    method: 'delete'
  })
}

// ==================== 2. 实验指导书生成 ====================

// 获取课程的章节列表
export const getChaptersByCourseId = (courseId) => {
  return request({
    url: `/api/smart-teaching/chapters/${courseId}`,
    method: 'get'
  })
}

// 生成实验指导书
export const generateExperimentGuide = (data) => {
  return request({
    url: '/api/smart-teaching/generate-experiment-guide',
    method: 'post',
    data
  })
}

// 获取实验指导书内容
export const getExperimentGuide = (guideId) => {
  return request({
    url: `/api/smart-teaching/experiment-guide/${guideId}`,
    method: 'get'
  })
}

// 获取实验指导书列表
export const getExperimentGuides = (teacherId) => {
  return request({
    url: `/api/smart-teaching/experiment-guides/${teacherId}`,
    method: 'get'
  })
}

// 删除实验指导书
export const deleteExperimentGuide = (guideId) => {
  return request({
    url: `/api/smart-teaching/experiment-guide/${guideId}`,
    method: 'delete'
  })
}

// ==================== 3. 主观题批阅助手 ====================

// 提交作业
export const submitHomework = (data) => {
  return request({
    url: '/api/smart-teaching/submit-homework',
    method: 'post',
    data
  })
}

// AI批阅作业
export const gradeHomework = (homeworkId) => {
  return request({
    url: `/api/smart-teaching/grade-homework/${homeworkId}`,
    method: 'post'
  })
}

// 获取作业批阅列表
export const getHomeworkList = (teacherId) => {
  return request({
    url: `/api/smart-teaching/homework-list/${teacherId}`,
    method: 'get'
  })
}

// 教师审阅作业
export const reviewHomework = (data) => {
  return request({
    url: '/api/smart-teaching/review-homework',
    method: 'post',
    data
  })
}

// 删除作业记录
export const deleteHomework = (homeworkId) => {
  return request({
    url: `/api/smart-teaching/homework/${homeworkId}`,
    method: 'delete'
  })
}
