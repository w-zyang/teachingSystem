import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/user/change-password',
    method: 'post',
    data
  })
}

// 分页查询用户列表
export function getUserList(params) {
  return request({
    url: '/api/user/list',
    method: 'get',
    params
  })
}

// 根据ID查询用户
export function getUserById(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'get'
  })
}

// 添加用户
export function addUser(data) {
  return request({
    url: '/api/user/add',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/api/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'delete'
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/api/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取用户统计信息
export function getUserStats() {
  return request({
    url: '/api/user/stats',
    method: 'get'
  })
}

// 批量删除用户
export function batchDeleteUsers(ids) {
  return request({
    url: '/api/user/batch',
    method: 'delete',
    data: ids
  })
}

// 头像上传
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/user/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 根据用户名查询用户信息
export function getUserByUsername(username) {
  return request({
    url: '/api/user/get-by-username',
    method: 'get',
    params: { username }
  })
}

// 获取用户列表（别名）
export function getUsers(params) {
  return getUserList(params)
}

// 创建用户（别名）
export function createUser(data) {
  return addUser(data)
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
} 