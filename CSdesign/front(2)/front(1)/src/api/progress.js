import request from '@/utils/request'

/**
 * 更新学习进度
 * @param {number} studentId 学生ID
 * @param {number} courseId 课程ID
 * @param {object} progressData 进度数据 {increment: 进度增量, duration: 学习时长}
 */
export function updateProgress(studentId, courseId, progressData) {
  return request({
    url: '/api/student-progress/update',
    method: 'post',
    params: { studentId, courseId },
    data: progressData
  })
}

/**
 * 记录资源学习
 * @param {number} studentId 学生ID
 * @param {number} courseId 课程ID
 * @param {number} resourceId 资源ID
 */
export function recordResourceStudy(studentId, courseId, resourceId) {
  return request({
    url: '/api/student-progress/record-resource',
    method: 'post',
    params: { studentId, courseId, resourceId }
  })
}

/**
 * 获取课程进度
 * @param {number} studentId 学生ID
 * @param {number} courseId 课程ID
 */
export function getCourseProgress(studentId, courseId) {
  return request({
    url: `/api/student-progress/course/${studentId}/${courseId}`,
    method: 'get'
  })
}

