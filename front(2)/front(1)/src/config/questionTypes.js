/**
 * 题型配置文件
 * 定义所有支持的题型及其属性
 */

export const QUESTION_TYPES = {
  choice: {
    key: 'choice',
    label: '单选题',
    icon: 'CircleCheck',
    color: '#409EFF',
    description: '从多个选项中选择一个正确答案',
    answerType: 'string',
    hasOptions: true,
    component: 'ChoiceQuestion'
  },
  
  multiple: {
    key: 'multiple',
    label: '多选题',
    icon: 'Checked',
    color: '#67C23A',
    description: '从多个选项中选择多个正确答案',
    answerType: 'array',
    hasOptions: true,
    minAnswers: 2,
    component: 'MultipleQuestion'
  },
  
  fill: {
    key: 'fill',
    label: '填空题',
    icon: 'Edit',
    color: '#E6A23C',
    description: '在空白处填写正确答案',
    answerType: 'string',
    hasOptions: false,
    hasBlank: true,
    component: 'FillQuestion'
  },
  
  short: {
    key: 'short',
    label: '简答题',
    icon: 'Document',
    color: '#909399',
    description: '用简洁的语言回答问题',
    answerType: 'string',
    hasOptions: false,
    isLongAnswer: true,
    component: 'ShortQuestion'
  },
  
  coding: {
    key: 'coding',
    label: '编程题',
    icon: 'Code',
    color: '#F56C6C',
    description: '编写代码解决问题',
    answerType: 'code',
    hasOptions: false,
    hasTestCases: true,
    hasExamples: true,
    component: 'CodingQuestion',
    fields: {
      title: '题目标题',
      content: '问题描述',
      requirements: '编程要求',
      inputFormat: '输入格式',
      outputFormat: '输出格式',
      examples: '示例',
      testCases: '测试用例',
      hints: '提示',
      answer: '参考代码'
    }
  },
  
  essay: {
    key: 'essay',
    label: '论述题',
    icon: 'Reading',
    color: '#606266',
    description: '深入分析和论证',
    answerType: 'string',
    hasOptions: false,
    isLongAnswer: true,
    component: 'EssayQuestion'
  },
  
  true_false: {
    key: 'true_false',
    label: '判断题',
    icon: 'Select',
    color: '#00D7FF',
    description: '判断陈述是否正确',
    answerType: 'boolean',
    hasOptions: false,
    options: [
      { value: true, label: '正确', icon: 'Select' },
      { value: false, label: '错误', icon: 'Close' }
    ],
    component: 'TrueFalseQuestion'
  },
  
  command: {
    key: 'command',
    label: '命令题',
    icon: 'Monitor',
    color: '#C71585',
    description: '执行命令或操作',
    answerType: 'string',
    hasOptions: false,
    component: 'CommandQuestion'
  }
}

/**
 * 获取题型配置
 * @param {string} type - 题型key
 * @returns {object} 题型配置对象
 */
export function getQuestionTypeConfig(type) {
  return QUESTION_TYPES[type] || {
    key: type,
    label: '未知类型',
    icon: 'QuestionFilled',
    color: '#909399',
    description: '未知题型',
    answerType: 'string',
    hasOptions: false,
    component: 'UnknownQuestion'
  }
}

/**
 * 获取题型标签
 * @param {string} type - 题型key
 * @returns {string} 题型标签
 */
export function getQuestionTypeLabel(type) {
  const config = getQuestionTypeConfig(type)
  return config.label
}

/**
 * 获取题型图标
 * @param {string} type - 题型key
 * @returns {string} 题型图标名称
 */
export function getQuestionTypeIcon(type) {
  const config = getQuestionTypeConfig(type)
  return config.icon
}

/**
 * 获取题型颜色
 * @param {string} type - 题型key
 * @returns {string} 题型颜色
 */
export function getQuestionTypeColor(type) {
  const config = getQuestionTypeConfig(type)
  return config.color
}

/**
 * 获取所有支持的题型列表
 * @returns {array} 题型配置数组
 */
export function getAllQuestionTypes() {
  return Object.values(QUESTION_TYPES)
}

/**
 * 验证答案格式是否正确
 * @param {string} type - 题型key
 * @param {any} answer - 答案
 * @returns {boolean} 是否有效
 */
export function validateAnswerFormat(type, answer) {
  const config = getQuestionTypeConfig(type)
  
  switch (config.answerType) {
    case 'string':
      return typeof answer === 'string' && answer.length > 0
    case 'array':
      return Array.isArray(answer) && answer.length >= (config.minAnswers || 1)
    case 'boolean':
      return typeof answer === 'boolean'
    case 'code':
      return typeof answer === 'string'
    default:
      return true
  }
}

/**
 * 检查答案是否正确
 * @param {object} question - 题目对象
 * @param {any} userAnswer - 用户答案
 * @returns {boolean|null} 是否正确，null表示需要人工评分
 */
export function checkAnswer(question, userAnswer) {
  const type = question.type
  const correctAnswer = question.answer
  
  switch (type) {
    case 'choice':
      // 单选题：字符串比较
      return userAnswer === correctAnswer
      
    case 'multiple':
      // 多选题：数组比较
      if (!Array.isArray(userAnswer) || !Array.isArray(correctAnswer)) {
        return false
      }
      if (userAnswer.length !== correctAnswer.length) {
        return false
      }
      const sortedUser = [...userAnswer].sort()
      const sortedCorrect = [...correctAnswer].sort()
      return sortedUser.every((ans, index) => ans === sortedCorrect[index])
      
    case 'true_false':
      // 判断题：布尔值比较
      return userAnswer === correctAnswer
      
    case 'fill':
      // 填空题：字符串比较（去除首尾空格）
      if (typeof userAnswer !== 'string' || typeof correctAnswer !== 'string') {
        return false
      }
      return userAnswer.trim().toLowerCase() === correctAnswer.trim().toLowerCase()
      
    case 'short':
    case 'essay':
      // 简答题/论述题：需要人工评分
      return null
      
    case 'coding':
      // 编程题：需要运行测试用例
      return null
      
    default:
      return false
  }
}

/**
 * 计算得分
 * @param {object} question - 题目对象
 * @param {any} userAnswer - 用户答案
 * @returns {object} 得分结果 { needManualGrading, score, isCorrect }
 */
export function calculateScore(question, userAnswer) {
  const isCorrect = checkAnswer(question, userAnswer)
  
  if (isCorrect === null) {
    // 需要人工评分
    return { 
      needManualGrading: true, 
      score: 0, 
      isCorrect: null 
    }
  }
  
  if (isCorrect) {
    return { 
      needManualGrading: false, 
      score: question.score || 0, 
      isCorrect: true 
    }
  } else {
    return { 
      needManualGrading: false, 
      score: 0, 
      isCorrect: false 
    }
  }
}

/**
 * 格式化答案显示
 * @param {string} type - 题型key
 * @param {any} answer - 答案
 * @returns {string} 格式化后的答案字符串
 */
export function formatAnswer(type, answer) {
  const config = getQuestionTypeConfig(type)
  
  switch (config.answerType) {
    case 'array':
      return Array.isArray(answer) ? answer.join(', ') : String(answer)
    case 'boolean':
      return answer ? '正确' : '错误'
    case 'string':
    case 'code':
    default:
      return String(answer)
  }
}

export default QUESTION_TYPES
