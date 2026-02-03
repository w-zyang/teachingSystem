/**
 * 题目数据转换工具
 * 用于将后端返回的数据格式转换为前端期望的格式
 */

/**
 * 转换选项格式：将字符串数组转换为对象数组
 * 
 * 后端格式: ["A. 选项1", "B. 选项2", "C. 选项3", "D. 选项4"]
 * 前端格式: [{key: "A", content: "选项1"}, {key: "B", content: "选项2"}, ...]
 * 
 * @param {Array} options - 选项数组
 * @returns {Array} 转换后的选项数组
 */
export function convertOptions(options) {
  // 如果没有选项或不是数组，返回空数组
  if (!options || !Array.isArray(options)) {
    return []
  }
  
  return options.map((option, index) => {
    // 如果已经是对象格式 {key: "A", content: "选项内容"}，直接返回
    if (typeof option === 'object' && option !== null && option.key && option.content) {
      return {
        key: option.key,
        content: option.content
      }
    }
    
    // 如果是字符串格式，需要解析
    if (typeof option === 'string') {
      // 格式1: "A. 选项内容"
      const match1 = option.match(/^([A-Z])\.\s*(.+)$/)
      if (match1) {
        return {
          key: match1[1],
          content: match1[2].trim()
        }
      }
      
      // 格式2: "A: 选项内容" 或 "A：选项内容"
      const match2 = option.match(/^([A-Z])[:：]\s*(.+)$/)
      if (match2) {
        return {
          key: match2[1],
          content: match2[2].trim()
        }
      }
      
      // 格式3: "A) 选项内容"
      const match3 = option.match(/^([A-Z])\)\s*(.+)$/)
      if (match3) {
        return {
          key: match3[1],
          content: match3[2].trim()
        }
      }
      
      // 格式4: "(A) 选项内容"
      const match4 = option.match(/^\(([A-Z])\)\s*(.+)$/)
      if (match4) {
        return {
          key: match4[1],
          content: match4[2].trim()
        }
      }
      
      // 如果都不匹配，尝试自动生成key
      const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
      return {
        key: letters[index] || String(index + 1),
        content: option.trim()
      }
    }
    
    // 其他情况，返回默认格式
    return {
      key: String.fromCharCode(65 + index), // A, B, C, D...
      content: String(option)
    }
  })
}

/**
 * 标准化题目类型
 * 将各种可能的题型格式统一转换为标准格式
 * 
 * @param {string} type - 原始题型
 * @returns {string} 标准化后的题型
 */
export function normalizeQuestionType(type) {
  if (!type) return 'choice'
  
  // 转换为小写进行匹配
  const lowerType = String(type).toLowerCase().trim()
  
  // 题型映射表
  const typeMap = {
    // 单选题的各种格式
    'choice': 'choice',
    'single_choice': 'choice',
    'multiple_choice': 'choice',
    'singlechoice': 'choice',
    'multiplechoice': 'choice',
    '选择题': 'choice',
    '单选题': 'choice',
    '单选': 'choice',
    
    // 多选题
    'multiple': 'multiple',
    'multi': 'multiple',
    '多选题': 'multiple',
    '多选': 'multiple',
    
    // 判断题
    'true_false': 'true_false',
    'truefalse': 'true_false',
    'judge': 'true_false',
    'tf': 'true_false',
    '判断题': 'true_false',
    '判断': 'true_false',
    
    // 填空题
    'fill': 'fill',
    'fill_in_the_blank': 'fill',
    'fill_in_blank': 'fill',
    'fill_blank': 'fill',
    'blank': 'fill',
    'fillintheblank': 'fill',
    'fillinblank': 'fill',
    '填空题': 'fill',
    '填空': 'fill',
    
    // 简答题
    'short': 'short',
    'short_answer': 'short',
    'shortanswer': 'short',
    'essay': 'short',
    '简答题': 'short',
    '简答': 'short',
    '问答题': 'short',
    '问答': 'short',
    
    // 编程题
    'coding': 'coding',
    'programming': 'coding',
    'code': 'coding',
    'program': 'coding',
    '编程题': 'coding',
    '编程': 'coding',
    '代码题': 'coding',
    
    // 命令题
    'command': 'coding',
    'cmd': 'coding',
    '命令题': 'coding',
    '命令': 'coding',
    
    // 论述题
    'essay': 'short',
    '论述题': 'short',
    '论述': 'short'
  }
  
  return typeMap[lowerType] || type
}

/**
 * 转换题目数据
 * 将后端返回的题目数据转换为前端期望的格式
 * 
 * @param {object} question - 原始题目对象
 * @returns {object} 转换后的题目对象
 */
export function convertQuestionData(question) {
  if (!question || typeof question !== 'object') {
    return null
  }
  
  // 创建转换后的对象
  const converted = { ...question }
  
  // 1. 标准化题型
  converted.type = normalizeQuestionType(question.type)
  
  // 2. 确保 content 字段存在
  if (!converted.content) {
    converted.content = question.title || question.question || question.stem || '题目内容缺失'
  }
  
  // 3. 转换选项格式（单选题和多选题）
  if ((converted.type === 'choice' || converted.type === 'multiple') && question.options) {
    converted.options = convertOptions(question.options)
  }
  
  // 4. 标准化答案格式
  if (converted.type === 'multiple' && converted.answer) {
    // 多选题答案应该是数组
    if (typeof converted.answer === 'string') {
      // 如果是字符串，尝试转换为数组
      if (converted.answer.includes(',')) {
        converted.answer = converted.answer.split(',').map(a => a.trim())
      } else if (converted.answer.includes('、')) {
        converted.answer = converted.answer.split('、').map(a => a.trim())
      } else {
        converted.answer = [converted.answer]
      }
    }
  } else if (converted.type === 'true_false' && converted.answer) {
    // 判断题答案应该是布尔值
    if (typeof converted.answer === 'string') {
      const lowerAnswer = converted.answer.toLowerCase().trim()
      if (lowerAnswer === 'true' || lowerAnswer === '正确' || lowerAnswer === '对' || lowerAnswer === 't' || lowerAnswer === '1') {
        converted.answer = true
      } else if (lowerAnswer === 'false' || lowerAnswer === '错误' || lowerAnswer === '错' || lowerAnswer === 'f' || lowerAnswer === '0') {
        converted.answer = false
      }
    }
  }
  
  // 5. 确保 explanation 字段（解析）
  if (!converted.explanation && (question.analysis || question.解析 || question.explain)) {
    converted.explanation = question.analysis || question.解析 || question.explain
  }
  
  // 6. 确保 score 字段是数字
  if (converted.score) {
    converted.score = Number(converted.score) || 0
  }
  
  // 7. 编程题的特殊字段
  if (converted.type === 'coding') {
    // 确保编程题有必要的字段
    if (!converted.title && converted.content) {
      converted.title = converted.content.substring(0, 50) + '...'
    }
    
    // 确保有 examples 和 testCases
    if (!converted.examples) {
      converted.examples = []
    }
    if (!converted.testCases) {
      converted.testCases = []
    }
  }
  
  return converted
}

/**
 * 批量转换题目数据
 * 
 * @param {Array} questions - 题目数组
 * @returns {Array} 转换后的题目数组
 */
export function convertQuestionsData(questions) {
  if (!Array.isArray(questions)) {
    return []
  }
  
  return questions.map((question, index) => {
    const converted = convertQuestionData(question)
    
    // 确保每个题目都有 id
    if (!converted.id) {
      converted.id = question.id || `q_${index + 1}`
    }
    
    return converted
  }).filter(q => q !== null)
}

/**
 * 验证题目数据是否完整
 * 
 * @param {object} question - 题目对象
 * @returns {object} 验证结果 { valid: boolean, errors: string[] }
 */
export function validateQuestionData(question) {
  const errors = []
  
  if (!question) {
    return { valid: false, errors: ['题目对象为空'] }
  }
  
  // 检查必填字段
  if (!question.type) {
    errors.push('缺少题型字段 (type)')
  }
  
  if (!question.content && !question.title) {
    errors.push('缺少题目内容 (content 或 title)')
  }
  
  if (question.answer === undefined || question.answer === null || question.answer === '') {
    errors.push('缺少答案 (answer)')
  }
  
  // 检查选择题的选项
  if ((question.type === 'choice' || question.type === 'multiple') && 
      (!question.options || !Array.isArray(question.options) || question.options.length === 0)) {
    errors.push('选择题缺少选项 (options)')
  }
  
  // 检查编程题的必要字段
  if (question.type === 'coding') {
    if (!question.requirements && !question.description) {
      errors.push('编程题缺少要求说明 (requirements 或 description)')
    }
  }
  
  return {
    valid: errors.length === 0,
    errors
  }
}

/**
 * 调试输出：打印题目数据
 * 
 * @param {object} question - 题目对象
 * @param {string} label - 标签
 */
export function debugQuestion(question, label = '题目数据') {
  console.group(`🔍 ${label}`)
  console.log('题型:', question.type)
  console.log('内容:', question.content)
  console.log('答案:', question.answer)
  
  if (question.options) {
    console.log('选项:', question.options)
  }
  
  if (question.explanation) {
    console.log('解析:', question.explanation)
  }
  
  const validation = validateQuestionData(question)
  if (!validation.valid) {
    console.warn('⚠️ 数据验证失败:', validation.errors)
  } else {
    console.log('✅ 数据验证通过')
  }
  
  console.groupEnd()
}

export default {
  convertOptions,
  normalizeQuestionType,
  convertQuestionData,
  convertQuestionsData,
  validateQuestionData,
  debugQuestion
}
