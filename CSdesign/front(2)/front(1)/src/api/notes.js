import request from '@/utils/request'

// =============================================
// 学生笔记相关API
// =============================================

/**
 * 获取学生的所有笔记
 * @param {number} studentId 学生ID
 */
export function getStudentNotes(studentId) {
  return request({
    url: `/api/student-notes/student/${studentId}`,
    method: 'get'
  })
}

/**
 * 获取特定资源的学生笔记
 * @param {number} studentId 学生ID
 * @param {number} resourceId 资源ID
 */
export function getResourceNotes(studentId, resourceId) {
  return request({
    url: `/api/student-notes/student/${studentId}/resource/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取资源的公开笔记
 * @param {number} resourceId 资源ID
 */
export function getPublicNotes(resourceId) {
  return request({
    url: `/api/student-notes/public/resource/${resourceId}`,
    method: 'get'
  })
}

/**
 * 根据ID获取笔记详情
 * @param {number} id 笔记ID
 */
export function getNoteById(id) {
  return request({
    url: `/api/student-notes/${id}`,
    method: 'get'
  })
}

/**
 * 创建新笔记
 * @param {object} note 笔记数据
 */
export function createNote(note) {
  return request({
    url: '/api/student-notes',
    method: 'post',
    data: note
  })
}

/**
 * 更新笔记
 * @param {number} id 笔记ID
 * @param {object} note 笔记数据
 */
export function updateNote(id, note) {
  return request({
    url: `/api/student-notes/${id}`,
    method: 'put',
    data: note
  })
}

/**
 * 删除笔记
 * @param {number} id 笔记ID
 */
export function deleteNote(id) {
  return request({
    url: `/api/student-notes/${id}`,
    method: 'delete'
  })
}

/**
 * 搜索笔记
 * @param {number} studentId 学生ID
 * @param {string} keyword 关键词
 * @param {number} resourceId 资源ID
 */
export function searchNotes(studentId, keyword = '', resourceId = null) {
  return request({
    url: '/api/student-notes/search',
    method: 'get',
    params: {
      studentId,
      keyword,
      resourceId
    }
  })
}

/**
 * 获取学生笔记统计
 * @param {number} studentId 学生ID
 */
export function getNoteStats(studentId) {
  return request({
    url: `/api/student-notes/stats/${studentId}`,
    method: 'get'
  })
}

// =============================================
// 学生标注相关API
// =============================================

/**
 * 获取学生的所有标注
 * @param {number} studentId 学生ID
 */
export function getStudentAnnotations(studentId) {
  return request({
    url: `/api/student-annotations/student/${studentId}`,
    method: 'get'
  })
}

/**
 * 获取特定资源的学生标注
 * @param {number} studentId 学生ID
 * @param {number} resourceId 资源ID
 */
export function getResourceAnnotations(studentId, resourceId) {
  return request({
    url: `/api/student-annotations/student/${studentId}/resource/${resourceId}`,
    method: 'get'
  })
}

/**
 * 获取特定页面的标注
 * @param {number} resourceId 资源ID
 * @param {number} pageNumber 页码
 */
export function getPageAnnotations(resourceId, pageNumber) {
  return request({
    url: `/api/student-annotations/resource/${resourceId}/page/${pageNumber}`,
    method: 'get'
  })
}

/**
 * 根据类型获取标注
 * @param {number} studentId 学生ID
 * @param {string} type 标注类型
 */
export function getAnnotationsByType(studentId, type) {
  return request({
    url: `/api/student-annotations/student/${studentId}/type/${type}`,
    method: 'get'
  })
}

/**
 * 根据重要程度获取标注
 * @param {number} studentId 学生ID
 * @param {number} importance 重要程度
 */
export function getAnnotationsByImportance(studentId, importance) {
  return request({
    url: `/api/student-annotations/student/${studentId}/importance/${importance}`,
    method: 'get'
  })
}

/**
 * 根据ID获取标注详情
 * @param {number} id 标注ID
 */
export function getAnnotationById(id) {
  return request({
    url: `/api/student-annotations/${id}`,
    method: 'get'
  })
}

/**
 * 创建新标注
 * @param {object} annotation 标注数据
 */
export function createAnnotation(annotation) {
  return request({
    url: '/api/student-annotations',
    method: 'post',
    data: annotation
  })
}

/**
 * 更新标注
 * @param {number} id 标注ID
 * @param {object} annotation 标注数据
 */
export function updateAnnotation(id, annotation) {
  return request({
    url: `/api/student-annotations/${id}`,
    method: 'put',
    data: annotation
  })
}

/**
 * 删除标注
 * @param {number} id 标注ID
 */
export function deleteAnnotation(id) {
  return request({
    url: `/api/student-annotations/${id}`,
    method: 'delete'
  })
}

/**
 * 搜索标注
 * @param {number} studentId 学生ID
 * @param {string} keyword 关键词
 * @param {string} type 标注类型
 * @param {number} resourceId 资源ID
 */
export function searchAnnotations(studentId, keyword = '', type = '', resourceId = null) {
  return request({
    url: '/api/student-annotations/search',
    method: 'get',
    params: {
      studentId,
      keyword,
      type,
      resourceId
    }
  })
}

/**
 * 获取学生标注统计
 * @param {number} studentId 学生ID
 */
export function getAnnotationStats(studentId) {
  return request({
    url: `/api/student-annotations/stats/${studentId}`,
    method: 'get'
  })
} 