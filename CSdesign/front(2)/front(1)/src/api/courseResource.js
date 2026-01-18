import request from '@/utils/request'

// 课程资源相关API

/**
 * 获取课程资源列表
 * @param {number} courseId 课程ID
 */
export function getCourseResources(courseId) {
  return request({
    url: `/api/course-resources/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 获取课程资源列表（分页查询）
 * @param {object} params 查询参数
 */
export function getCourseResourceList(params) {
  return request({
    url: '/api/course-resources/list',
    method: 'get',
    params
  })
}

/**
 * 获取教师的资源列表
 * @param {number} teacherId 教师ID
 */
export function getTeacherResources(teacherId) {
  return request({
    url: `/api/course-resources/teacher/${teacherId}`,
    method: 'get'
  })
}

/**
 * 获取资源详情
 * @param {number} resourceId 资源ID
 */
export function getResourceById(resourceId) {
  return request({
    url: `/api/course-resources/${resourceId}`,
    method: 'get'
  })
}

/**
 * 上传课程资源
 * @param {FormData} formData 表单数据
 */
export function uploadResource(formData) {
  return request({
    url: '/api/course-resources/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传课程资源（别名）
 * @param {FormData} formData 表单数据
 */
export function uploadCourseResource(formData) {
  return uploadResource(formData)
}

/**
 * 更新资源信息
 * @param {number} resourceId 资源ID
 * @param {object} data 更新数据
 */
export function updateResource(resourceId, data) {
  return request({
    url: `/api/course-resources/${resourceId}`,
    method: 'put',
    data
  })
}

/**
 * 删除资源
 * @param {number} resourceId 资源ID
 */
export function deleteResource(resourceId) {
  return request({
    url: `/api/course-resources/${resourceId}`,
    method: 'delete'
  })
}

/**
 * 删除课程资源（别名）
 * @param {number} resourceId 资源ID
 */
export function deleteCourseResource(resourceId) {
  return deleteResource(resourceId)
}

/**
 * 记录资源下载
 * @param {number} resourceId 资源ID
 */
export function downloadResource(resourceId) {
  return request({
    url: `/api/course-resources/${resourceId}/download`,
    method: 'post'
  })
}

/**
 * 搜索资源
 * @param {object} params 搜索参数
 */
export function searchResources(params) {
  return request({
    url: '/api/course-resources/search',
    method: 'get',
    params
  })
}

/**
 * 获取所有资源（管理员用）
 */
export function getAllResources() {
  return request({
    url: '/api/course-resources/admin/all',
    method: 'get'
  })
}

/**
 * 获取资源文件内容（用于文档预览）
 * @param {number} resourceId 资源ID
 */
export function getResourceContent(resourceId) {
  return request({
    url: `/api/course-resources/${resourceId}/content`,
    method: 'get'
  })
} 