/**
 * 题目渲染辅助函数
 * 用于在ExamGeneration.vue中渲染不同类型的题目
 */

import { getQuestionTypeConfig, getQuestionTypeLabel, getQuestionTypeIcon, getQuestionTypeColor, formatAnswer } from '@/config/questionTypes'

/**
 * 获取选项的键（A, B, C, D）
 * @param {string} option - 选项字符串，如 "A. 选项内容"
 * @returns {string} 选项键，如 "A"
 */
export function getOptionKey(option) {
  if (typeof option !== 'string') return ''
  const match = option.match(/^([A-Z])\.\s*/)
  return match ? match[1] : ''
}

/**
 * 获取选项的内容（去除A. B.等前缀）
 * @param {string} option - 选项字符串
 * @returns {string} 选项内容
 */
export function getOptionContent(option) {
  if (typeof option !== 'string') return option
  return option.replace(/^[A-Z]\.\s*/, '')
}

/**
 * 渲染填空题的内容（将____替换为输入框）
 * @param {string} content - 题目内容
 * @returns {string} HTML字符串
 */
export function renderFillBlanks(content) {
  if (!content) return ''
  return content.replace(/____/g, '<input type="text" class="fill-blank-input" placeholder="请填写答案" />')
}

/**
 * 检查题目是否有选项
 * @param {object} question - 题目对象
 * @returns {boolean}
 */
export function hasOptions(question) {
  return question.options && Array.isArray(question.options) && question.options.length > 0
}

/**
 * 获取题目类型的显示信息
 * @param {string} type - 题型key
 * @returns {object} { label, icon, color }
 */
export function getTypeDisplay(type) {
  return {
    label: getQuestionTypeLabel(type),
    icon: getQuestionTypeIcon(type),
    color: getQuestionTypeColor(type)
  }
}

/**
 * 格式化题目答案用于显示
 * @param {object} question - 题目对象
 * @returns {string} 格式化后的答案
 */
export function formatQuestionAnswer(question) {
  if (!question || question.answer === undefined) return ''
  return formatAnswer(question.type, question.answer)
}

/**
 * 判断是否为多选题
 * @param {object} question - 题目对象
 * @returns {boolean}
 */
export function isMultipleChoice(question) {
  return question.type === 'multiple'
}

/**
 * 判断是否为判断题
 * @param {object} question - 题目对象
 * @returns {boolean}
 */
export function isTrueFalse(question) {
  return question.type === 'true_false'
}

/**
 * 判断是否为编程题
 * @param {object} question - 题目对象
 * @returns {boolean}
 */
export function isCoding(question) {
  return question.type === 'coding'
}

/**
 * 判断是否为填空题
 * @param {object} question - 题目对象
 * @returns {boolean}
 */
export function isFill(question) {
  return question.type === 'fill'
}

/**
 * 获取题目内容（兼容多种字段名）
 * @param {object} question - 题目对象
 * @returns {string}
 */
export function getQuestionContent(question) {
  return question.content || question.title || question.question || ''
}

/**
 * 验证用户答案格式
 * @param {object} question - 题目对象
 * @param {any} userAnswer - 用户答案
 * @returns {object} { valid, message }
 */
export function validateUserAnswer(question, userAnswer) {
  const config = getQuestionTypeConfig(question.type)
  
  switch (config.answerType) {
    case 'string':
      if (!userAnswer || userAnswer.trim() === '') {
        return { valid: false, message: '请输入答案' }
      }
      return { valid: true, message: '' }
      
    case 'array':
      if (!Array.isArray(userAnswer) || userAnswer.length === 0) {
        return { valid: false, message: '请至少选择一个选项' }
      }
      if (config.minAnswers && userAnswer.length < config.minAnswers) {
        return { valid: false, message: `请至少选择${config.minAnswers}个选项` }
      }
      return { valid: true, message: '' }
      
    case 'boolean':
      if (userAnswer === null || userAnswer === undefined) {
        return { valid: false, message: '请选择正确或错误' }
      }
      return { valid: true, message: '' }
      
    default:
      return { valid: true, message: '' }
  }
}

/**
 * 初始化用户答案
 * @param {object} question - 题目对象
 * @returns {any} 初始答案值
 */
export function initUserAnswer(question) {
  const config = getQuestionTypeConfig(question.type)
  
  switch (config.answerType) {
    case 'array':
      return []
    case 'boolean':
      return null
    case 'string':
    case 'code':
    default:
      return ''
  }
}

export default {
  getOptionKey,
  getOptionContent,
  renderFillBlanks,
  hasOptions,
  getTypeDisplay,
  formatQuestionAnswer,
  isMultipleChoice,
  isTrueFalse,
  isCoding,
  isFill,
  getQuestionContent,
  validateUserAnswer,
  initUserAnswer
}
