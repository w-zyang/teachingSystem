# 测试文件说明

本文件夹包含用于测试后端 API 的 HTML 测试页面。

## 测试文件列表

### 1. test_ai_summary.html
- **功能**: 测试 AI 课堂总结生成功能
- **测试内容**: 
  - 带课件内容的 AI 分析
  - 不带课件内容的 AI 分析
- **访问地址**: http://localhost:8080/test_ai_summary.html

### 2. test_class_summary_audio.html
- **功能**: 测试课堂总结音频上传功能
- **测试内容**: 音频文件上传和管理
- **访问地址**: http://localhost:8080/test_class_summary_audio.html

### 3. test_class_summary_api.html
- **功能**: 测试课堂总结相关 API
- **测试内容**: 课堂总结的增删改查操作
- **访问地址**: http://localhost:8080/test_class_summary_api.html

### 4. test_stats_api.html
- **功能**: 测试统计数据 API
- **测试内容**: 各类统计数据的获取
- **访问地址**: http://localhost:8080/test_stats_api.html

### 5. test_class_stats.html
- **功能**: 测试课堂统计功能
- **测试内容**: 课堂相关的统计信息
- **访问地址**: http://localhost:8080/test_class_stats.html

### 6. test_error_question_api.html
- **功能**: 测试错题相关 API
- **测试内容**: 错题的管理和查询
- **访问地址**: http://localhost:8080/test_error_question_api.html

### 7. test_admin_apis.html
- **功能**: 测试管理员相关 API
- **测试内容**: 管理员功能的各项操作
- **访问地址**: http://localhost:8080/test_admin_apis.html

## 使用说明

1. 确保后端服务已启动（默认端口 8080）
2. 在浏览器中访问对应的测试页面 URL
3. 按照页面提示进行测试操作
4. 查看浏览器控制台（F12）获取详细的请求和响应信息

## 注意事项

- 这些文件仅用于开发和测试，不应部署到生产环境
- 测试前请确保数据库连接正常
- 某些测试可能需要先登录或设置相应的权限
