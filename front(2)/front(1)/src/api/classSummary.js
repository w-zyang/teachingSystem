import request from '../utils/request'

const BASE_URL = '/api/class-summary'

/**
 * 课堂重点整理API接口
 */
export default {
  /**
   * 创建课堂总结
   */
  createClassSummary(data) {
    return request({
      url: `${BASE_URL}/create`,
      method: 'post',
      data
    })
  },

  /**
   * 上传录音文件
   */
  uploadAudioFile(audioFile, courseId, summaryId, audioDuration) {
    const formData = new FormData()
    formData.append('audioFile', audioFile)
    formData.append('courseId', courseId)
    if (summaryId) {
      formData.append('summaryId', summaryId)
    }
    if (audioDuration !== undefined && audioDuration !== null) {
      formData.append('audioDuration', audioDuration)
    }
    
    // 验证FormData内容
    console.log('FormData内容:', {
      hasAudioFile: formData.has('audioFile'),
      courseId: formData.get('courseId'),
      summaryId: formData.get('summaryId'),
      audioDuration: formData.get('audioDuration'),
      audioFileSize: audioFile ? audioFile.size : 0,
      audioFileName: audioFile ? audioFile.name : 'N/A',
      audioFileType: audioFile ? audioFile.type : 'N/A'
    })
    
    return request({
      url: `${BASE_URL}/upload-audio`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 300000 // 5分钟超时，适应大文件上传
    })
  },

  /**
   * 处理录音（语音转文字）- 使用本地文件路径
   * @param {number} summaryId 总结ID
   * @param {string} audioFilePath 音频文件本地路径
   */
  processAudioToText(summaryId, audioFilePath) {
    return request({
      url: `${BASE_URL}/process-audio`,
      method: 'post',
      data: {
        summaryId,
        audioFilePath: audioFilePath // 使用 audioFilePath 参数名
      },
      timeout: 300000 // 5分钟超时，Whisper 处理可能需要较长时间
    })
  },

  /**
   * 处理录音（语音转文字）- 旧版本，兼容 audioFilePath
   * @deprecated 请使用 processAudioToText(summaryId, audioUrl)
   */
  processAudioToTextByPath(summaryId, audioFilePath) {
    // 如果是本地路径，转换为可访问的URL
    let audioUrl = audioFilePath
    if (audioFilePath && !audioFilePath.startsWith('http://') && !audioFilePath.startsWith('https://')) {
      // 构建完整的访问URL
      const baseUrl = window.location.origin.replace(/:\d+$/, ':8080')
      audioUrl = baseUrl + (audioFilePath.startsWith('/') ? audioFilePath : '/' + audioFilePath)
    }
    
    return request({
      url: `${BASE_URL}/process-audio`,
      method: 'post',
      data: {
        summaryId,
        audioUrl
      },
      timeout: 180000 // 3分钟超时，语音识别需要时间
    })
  },

  /**
   * 直接处理音频文件（简化流程：上传+ASR一步完成）
   * 这个方法可以替代：上传文件 → 保存 → 再调用处理 → 读取文件 → 上传OSS → 调用AI 的复杂流程
   * 直接：上传文件 → ASR处理 → 返回文字
   * @param {File} audioFile 音频文件
   * @param {number} summaryId 总结ID（可选）
   * @returns {Promise} 返回转录文本
   */
  processAudioFileDirect(audioFile, summaryId) {
    const formData = new FormData()
    formData.append('audioFile', audioFile)
    if (summaryId) {
      formData.append('summaryId', summaryId)
    }
    
    return request({
      url: `${BASE_URL}/process-audio-direct`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 180000 // 3分钟超时，语音识别需要时间
    })
  },

  /**
   * 处理录音（语音转文字，使用SSE流式传输）
   * @deprecated 该接口已删除，请使用 processAudioToText
   * @param {number} summaryId 总结ID
   * @param {string} audioFilePath 音频文件路径
   * @param {function} onProgress 进度回调函数，接收实时识别结果片段 (textFragment, fullText)
   * @returns {Promise} 返回完整的转录文本
   */
  processAudioToTextWithSSE(summaryId, audioFilePath, onProgress) {
    // SSE接口已删除，回退到普通接口
    console.warn('processAudioToTextWithSSE 接口已删除，使用普通接口替代')
    
    // 如果是本地路径，转换为可访问的URL
    let audioUrl = audioFilePath
    if (audioFilePath && !audioFilePath.startsWith('http://') && !audioFilePath.startsWith('https://')) {
      // 构建完整的访问URL
      const baseUrl = window.location.origin.replace(/:\d+$/, ':8080')
      audioUrl = baseUrl + (audioFilePath.startsWith('/') ? audioFilePath : '/' + audioFilePath)
    }
    
    return this.processAudioToText(summaryId, audioUrl)
  },

  /**
   * 生成AI重点整理
   */
  generateSummaryWithAI(summaryId, transcriptText, coursewareContent = '') {
    console.log('========== API: generateSummaryWithAI ==========')
    console.log('请求参数:', {
      summaryId,
      transcriptTextLength: transcriptText ? transcriptText.length : 0,
      coursewareContentLength: coursewareContent ? coursewareContent.length : 0,
      hasCourseware: coursewareContent && coursewareContent.trim().length > 0
    })
    
    return request({
      url: `${BASE_URL}/generate-summary`,
      method: 'post',
      data: {
        summaryId,
        transcriptText,
        coursewareContent
      },
      timeout: 120000 // 2分钟超时，AI生成需要时间
    }).then(response => {
      console.log('API 响应:', response)
      return response
    }).catch(error => {
      console.error('API 错误:', error)
      throw error
    })
  },

  /**
   * 更新最终文档内容
   */
  updateFinalContent(summaryId, finalContent) {
    return request({
      url: `${BASE_URL}/update-content/${summaryId}`,
      method: 'put',
      data: {
        finalContent
      }
    })
  },

  /**
   * 发布课堂总结
   */
  publishClassSummary(summaryId) {
    return request({
      url: `${BASE_URL}/publish/${summaryId}`,
      method: 'put'
    })
  },

  /**
   * 获取教师的课堂总结列表
   */
  getTeacherSummaries(teacherId) {
    return request({
      url: `${BASE_URL}/teacher/${teacherId}`,
      method: 'get'
    })
  },

  /**
   * 获取课程的已发布总结列表
   */
  getPublishedSummaries(courseId) {
    return request({
      url: `${BASE_URL}/course/${courseId}`,
      method: 'get'
    })
  },

  /**
   * 获取所有已发布的总结（学生端）
   */
  getAllPublishedSummaries() {
    return request({
      url: `${BASE_URL}/published`,
      method: 'get'
    })
  },

  /**
   * 获取课堂总结详情
   */
  getSummaryDetail(summaryId) {
    return request({
      url: `${BASE_URL}/detail/${summaryId}`,
      method: 'get'
    })
  },

  /**
   * 搜索已发布的课堂总结
   */
  searchPublishedSummaries(keyword) {
    return request({
      url: `${BASE_URL}/search`,
      method: 'get',
      params: {
        keyword
      }
    })
  },

  /**
   * 删除课堂总结
   */
  deleteClassSummary(summaryId, teacherId) {
    return request({
      url: `${BASE_URL}/delete/${summaryId}`,
      method: 'delete',
      params: {
        teacherId
      }
    })
  },

  /**
   * 获取课堂总结统计信息
   */
  getSummaryStatistics(teacherId) {
    return request({
      url: `${BASE_URL}/statistics/${teacherId}`,
      method: 'get'
    })
  },

  /**
   * 删除录音文件
   */
  deleteAudioFile(summaryId, teacherId) {
    return request({
      url: `${BASE_URL}/delete-audio/${summaryId}`,
      method: 'delete',
      params: {
        teacherId
      }
    })
  }
} 