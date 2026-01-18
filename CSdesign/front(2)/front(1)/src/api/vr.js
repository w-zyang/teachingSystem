import request from '@/utils/request'

/**
 * VR教室相关API
 */

/**
 * 获取VR课程列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getVRCourses(params = {}) {
  return request({
    url: '/api/vr/courses',
    method: 'get',
    params
  })
}

/**
 * 获取VR课程详情
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export function getVRCourseDetail(courseId) {
  return request({
    url: `/api/vr/courses/${courseId}`,
    method: 'get'
  })
}

/**
 * 创建VR学习会话
 * @param {Object} data - 会话数据
 * @returns {Promise}
 */
export function createVRSession(data) {
  return request({
    url: '/api/vr/sessions',
    method: 'post',
    data
  })
}

/**
 * 加入VR教室
 * @param {number} sessionId - 会话ID
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export function joinVRClassroom(sessionId, data) {
  return request({
    url: `/api/vr/sessions/${sessionId}/join`,
    method: 'post',
    data
  })
}

/**
 * 离开VR教室
 * @param {number} sessionId - 会话ID
 * @returns {Promise}
 */
export function leaveVRClassroom(sessionId) {
  return request({
    url: `/api/vr/sessions/${sessionId}/leave`,
    method: 'post'
  })
}

/**
 * 发送VR聊天消息
 * @param {number} sessionId - 会话ID
 * @param {Object} message - 消息内容
 * @returns {Promise}
 */
export function sendVRMessage(sessionId, message) {
  return request({
    url: `/api/vr/sessions/${sessionId}/messages`,
    method: 'post',
    data: message
  })
}

/**
 * 获取VR聊天历史
 * @param {number} sessionId - 会话ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getVRMessages(sessionId, params = {}) {
  return request({
    url: `/api/vr/sessions/${sessionId}/messages`,
    method: 'get',
    params
  })
}

/**
 * 更新VR学习进度
 * @param {number} sessionId - 会话ID
 * @param {Object} progress - 进度数据
 * @returns {Promise}
 */
export function updateVRProgress(sessionId, progress) {
  return request({
    url: `/api/vr/sessions/${sessionId}/progress`,
    method: 'put',
    data: progress
  })
}

/**
 * 获取VR设备信息
 * @returns {Promise}
 */
export function getVRDeviceInfo() {
  return request({
    url: '/api/vr/device-info',
    method: 'get'
  })
}

/**
 * 检查WebXR支持
 * @returns {Promise}
 */
export function checkWebXRSupport() {
  return request({
    url: '/api/vr/webxr-support',
    method: 'get'
  })
}

/**
 * 获取VR教室状态
 * @param {number} classroomId - 教室ID
 * @returns {Promise}
 */
export function getVRClassroomStatus(classroomId) {
  return request({
    url: `/api/vr/classrooms/${classroomId}/status`,
    method: 'get'
  })
}

/**
 * 举手发言
 * @param {number} sessionId - 会话ID
 * @returns {Promise}
 */
export function raiseHandInVR(sessionId) {
  return request({
    url: `/api/vr/sessions/${sessionId}/raise-hand`,
    method: 'post'
  })
}

/**
 * 提交VR问题
 * @param {number} sessionId - 会话ID
 * @param {Object} question - 问题内容
 * @returns {Promise}
 */
export function submitVRQuestion(sessionId, question) {
  return request({
    url: `/api/vr/sessions/${sessionId}/questions`,
    method: 'post',
    data: question
  })
}

/**
 * 保存VR笔记
 * @param {number} sessionId - 会话ID
 * @param {Object} note - 笔记内容
 * @returns {Promise}
 */
export function saveVRNote(sessionId, note) {
  return request({
    url: `/api/vr/sessions/${sessionId}/notes`,
    method: 'post',
    data: note
  })
}

/**
 * 获取VR学习数据
 * @param {number} userId - 用户ID
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getVRLearningData(userId, params = {}) {
  return request({
    url: `/api/vr/users/${userId}/learning-data`,
    method: 'get',
    params
  })
}

/**
 * 硬件拆解相关API
 */

/**
 * 获取硬件拆解列表
 * @returns {Promise}
 */
export function getHardwareList() {
  return request({
    url: '/api/vr/hardware',
    method: 'get'
  })
}

/**
 * 获取硬件详细信息
 * @param {number} hardwareId - 硬件ID
 * @returns {Promise}
 */
export function getHardwareDetail(hardwareId) {
  return request({
    url: `/api/vr/hardware/${hardwareId}`,
    method: 'get'
  })
}

/**
 * 更新硬件拆解状态
 * @param {number} hardwareId - 硬件ID
 * @param {Object} explodeData - 拆解数据
 * @returns {Promise}
 */
export function explodeHardware(hardwareId, explodeData) {
  return request({
    url: `/api/vr/hardware/${hardwareId}/explode`,
    method: 'post',
    data: explodeData
  })
}

export default {
  getVRCourses,
  getVRCourseDetail,
  createVRSession,
  joinVRClassroom,
  leaveVRClassroom,
  sendVRMessage,
  getVRMessages,
  updateVRProgress,
  getVRDeviceInfo,
  checkWebXRSupport,
  getVRClassroomStatus,
  raiseHandInVR,
  submitVRQuestion,
  saveVRNote,
  getVRLearningData
} 