import request from '@/utils/request'

// 测试接口
export function testHello() {
  return request({
    url: '/api/test/hello',
    method: 'get'
  })
}

// 获取系统信息
export function getSystemInfo() {
  return request({
    url: '/api/test/info',
    method: 'get'
  })
}

// 模拟用户登录测试
export function mockLogin(data) {
  return request({
    url: '/api/test/mock-login',
    method: 'post',
    data
  })
} 