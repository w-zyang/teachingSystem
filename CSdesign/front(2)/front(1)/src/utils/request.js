import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080', // 默认后端地址
  timeout: 120000 // 请求超时时间，AI生成可能需要更长时间（2分钟）
})

// 创建AI专用的axios实例，更长超时时间
const aiService = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 300000 // AI请求超时时间：5分钟
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 添加调试信息
    console.log('发送请求:', {
      url: config.url,
      method: config.method,
      data: config.data,
      params: config.params,
      hasToken: !!token
    })
    
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 添加调试信息
    console.log('收到响应:', {
      url: response.config.url,
      status: response.status,
      data: res
    })
    
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    if (response.status === 200) {
      return res
    }
    
    // 否则的话抛出错误
    const errorMsg = res.message || res.msg || '请求失败'
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(new Error(errorMsg))
  },
  error => {
    console.error('响应错误:', error)
    
    let errorMsg = '网络错误'
    
    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response
      console.error('服务器错误:', { status, data })
      
      switch (status) {
        case 401:
          errorMsg = '未授权，请重新登录'
          // 清除本地存储的token
          localStorage.removeItem('token')
          localStorage.removeItem('userId')
          localStorage.removeItem('userRole')
          localStorage.removeItem('username')
          localStorage.removeItem('realName')
          // 跳转到登录页
          window.location.href = '/login'
          break
        case 403:
          errorMsg = '权限不足'
          break
        case 404:
          errorMsg = '请求的资源不存在'
          break
        case 500:
          errorMsg = '服务器内部错误'
          break
        default:
          errorMsg = data?.message || data?.msg || `请求失败 (${status})`
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络错误:', error.request)
      errorMsg = '网络连接失败，请检查网络连接'
    } else {
      // 其他错误
      console.error('其他错误:', error.message)
      errorMsg = error.message || '未知错误'
    }
    
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

// 为AI服务添加相同的拦截器
aiService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    console.log('发送AI请求:', {
      url: config.url,
      method: config.method,
      data: config.data,
      params: config.params,
      hasToken: !!token
    })
    
    return config
  },
  error => {
    console.error('AI请求错误:', error)
    return Promise.reject(error)
  }
)

aiService.interceptors.response.use(
  response => {
    const res = response.data
    
    console.log('收到AI响应:', {
      url: response.config.url,
      status: response.status,
      data: res
    })
    
    if (response.status === 200) {
      return res
    }
    
    const errorMsg = res.message || res.msg || '请求失败'
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(new Error(errorMsg))
  },
  error => {
    console.error('AI响应错误:', error)
    
    let errorMsg = 'AI服务网络错误'
    
    if (error.response) {
      const { status, data } = error.response
      console.error('AI服务器错误:', { status, data })
      
      switch (status) {
        case 401:
          errorMsg = '未授权，请重新登录'
          localStorage.removeItem('token')
          localStorage.removeItem('userId')
          localStorage.removeItem('userRole')
          localStorage.removeItem('username')
          localStorage.removeItem('realName')
          window.location.href = '/login'
          break
        case 403:
          errorMsg = '权限不足'
          break
        case 404:
          errorMsg = '请求的资源不存在'
          break
        case 500:
          errorMsg = 'AI服务器内部错误'
          break
        default:
          errorMsg = data?.message || data?.msg || `AI请求失败 (${status})`
      }
    } else if (error.request) {
      console.error('AI网络错误:', error.request)
      errorMsg = 'AI服务连接失败，请检查网络连接或稍后重试'
    } else {
      console.error('AI其他错误:', error.message)
      errorMsg = error.message || 'AI服务未知错误'
    }
    
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
export { aiService } 