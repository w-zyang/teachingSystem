import request, { aiService } from '@/utils/request'

/**
 * AI聊天相关API
 */
export const aiAPI = {
  /**
   * 发送聊天请求
   * @param {Object} data - 聊天请求数据
   * @returns {Promise}
   */
  chat(data) {
    return aiService({
      url: '/api/ai/chat',
      method: 'post',
      data
    })
  },

  /**
   * 简单文本对话
   * @param {string} message - 用户消息
   * @returns {Promise}
   */
  simpleChat(message) {
    return aiService({
      url: '/api/ai/simple-chat',
      method: 'post',
      params: { message }
    })
  },

  /**
   * 带系统提示的对话
   * @param {string} systemPrompt - 系统提示
   * @param {string} userMessage - 用户消息
   * @returns {Promise}
   */
  chatWithSystem(systemPrompt, userMessage) {
    return aiService({
      url: '/api/ai/chat-with-system',
      method: 'post',
      params: { systemPrompt, userMessage }
    })
  },

  /**
   * 学习助手
   * @param {string} message - 学习问题
   * @returns {Promise}
   */
  learningAssistant(message) {
    return aiService({
      url: '/api/ai/learning-assistant',
      method: 'post',
      data: { message }
    })
  },

  /**
   * 代码助手
   * @param {string} question - 编程问题
   * @returns {Promise}
   */
  codeAssistant(question) {
    return aiService({
      url: '/api/ai/code-assistant',
      method: 'post',
      params: { question }
    })
  },

  /**
   * 写作助手
   * @param {string} request - 写作请求
   * @returns {Promise}
   */
  writingAssistant(request) {
    return aiService({
      url: '/api/ai/writing-assistant',
      method: 'post',
      params: { request }
    })
  },

  /**
   * 教师备课助手 - 根据课程大纲设计教学内容
   * @param {Object} data - 备课数据
   * @returns {Promise}
   */
  courseDesign(data) {
    return aiService({
      url: '/api/ai/course-design',
      method: 'post',
      data
    })
  },

  /**
   * 生成考核题目
   * @param {Object} data - 考核生成数据
   * @returns {Promise}
   */
  generateExam(data) {
    return aiService({
      url: '/api/ai/generate-exam',
      method: 'post',
      data
    })
  },

  /**
   * 学情数据分析
   * @param {Object} data - 学生答题数据
   * @returns {Promise}
   */
  analyzeStudentData(data) {
    return aiService({
      url: '/api/ai/analyze-student-data',
      method: 'post',
      data
    })
  },

  /**
   * 实时练习评测
   * @param {Object} data - 练习数据
   * @returns {Promise}
   */
  practiceEvaluation(data) {
    return aiService({
      url: '/api/ai/practice-evaluation',
      method: 'post',
      data
    })
  },

  /**
   * 检查AI分析状态
   * @param {Object} data - 分析数据
   * @returns {Promise}
   */
  checkAnalysisStatus(data) {
    return aiService({
      url: '/api/ai/check-analysis-status',
      method: 'post',
      data
    })
  },

  /**
   * 手动触发AI分析
   * @param {Object} data - 分析数据
   * @returns {Promise}
   */
  triggerAIAnalysis(data) {
    return aiService({
      url: '/api/ai/trigger-ai-analysis',
      method: 'post',
      data
    })
  },

  /**
   * 生成练习题目
   * @param {Object} data - 练习生成数据
   * @returns {Promise}
   */
  generatePractice(data) {
    return aiService({
      url: '/api/ai/generate-practice',
      method: 'post',
      data
    })
  },

  /**
   * 文件上传
   * @param {FormData} formData - 文件数据
   * @returns {Promise}
   */
  uploadFile(formData) {
    return request({
      url: '/api/ai/upload/file',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 文件下载
   * @param {string} fileName - 文件名
   * @returns {Promise}
   */
  downloadFile(fileName) {
    return request({
      url: `/api/ai/download/${fileName}`,
      method: 'get',
      responseType: 'blob'
    })
  }
}

// 知识库查询（可能涉及AI处理）
export function queryKnowledgeBase(query) {
  return aiService({
    url: '/api/ai/knowledge-query',
    method: 'post',
    data: { query }
  })
}

// 获取知识库分类（快速查询，使用普通服务）
export function getKnowledgeCategories() {
  return request({
    url: '/api/ai/knowledge-categories',
    method: 'get'
  })
}

// 获取知识库内容（快速查询，使用普通服务）
export function getKnowledgeContent(category, topic) {
  return request({
    url: '/api/ai/knowledge-content',
    method: 'get',
    params: { category, topic }
  })
}

// 搜索知识库（可能涉及AI处理）
export function searchKnowledgeBase(keyword) {
  return aiService({
    url: '/api/ai/knowledge-search',
    method: 'post',
    data: { keyword }
  })
}

/**
 * 根据文档内容进行对话
 * @param {Object} data - 对话数据
 * @returns {Promise}
 */
export function chatWithDocument(data) {
  return aiService({
    url: '/api/ai/chat-with-document',
    method: 'post',
    data
  })
}

/**
 * 生成思维导图
 * @param {Object} data - 思维导图生成数据
 * @returns {Promise}
 */
export function generateMindmap(data) {
  return aiService({
    url: '/api/ai/generate-mindmap',
    method: 'post',
    data
  })
}

export default aiAPI 