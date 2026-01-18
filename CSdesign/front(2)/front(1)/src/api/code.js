import request from '@/utils/request'

/**
 * 运行代码
 */
export function runCode(data) {
  return request({
    url: '/api/code/run',
    method: 'post',
    data
  })
}

/**
 * 提交代码
 */
export function submitCode(data) {
  return request({
    url: '/api/code/submit',
    method: 'post',
    data
  })
}

/**
 * 批量提交测试用例
 */
export function submitBatch(data) {
  return request({
    url: '/api/code/submit-batch',
    method: 'post',
    data
  })
}

/**
 * 获取提交结果
 */
export function getSubmissionResult(token) {
  return request({
    url: `/api/code/result/${token}`,
    method: 'get'
  })
}

/**
 * 获取支持的编程语言
 */
export function getSupportedLanguages() {
  return request({
    url: '/api/code/languages',
    method: 'get'
  })
}

/**
 * 测试 Judge0 连接
 */
export function testJudge0Connection() {
  return request({
    url: '/api/code/test-connection',
    method: 'get'
  })
}
