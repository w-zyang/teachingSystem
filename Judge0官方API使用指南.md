# Judge0 官方 API 使用指南

## 🎯 功能说明

Judge0 官方 API 完全支持代码评测的所有功能：

✅ **基础功能：**
- 代码执行
- 多种编程语言支持（71 种）
- 标准输入/输出
- 编译错误检测
- 运行时错误检测

✅ **判题功能：**
- 预期输出对比（自动判断对错）
- 时间限制检测
- 内存限制检测
- 多测试用例批量评测

✅ **状态码说明：**
- `statusId = 3`: **通过** ✅
- `statusId = 4`: **错误答案** ❌
- `statusId = 5`: **超时** ⏱️
- `statusId = 6`: **编译错误** 🔧
- `statusId = 7-12`: **运行时错误** 💥

---

## 📝 步骤 1：注册并获取 API 密钥

### 1.1 访问 RapidAPI

打开浏览器，访问：
```
https://rapidapi.com/judge0-official/api/judge0-ce
```

### 1.2 注册账号

1. 点击右上角 **"Sign Up"** 按钮
2. 使用 Google/GitHub 账号登录，或者邮箱注册
3. 验证邮箱（如果使用邮箱注册）

### 1.3 订阅免费计划

1. 在 Judge0 CE API 页面，点击 **"Subscribe to Test"**
2. 选择 **"Basic"** 计划（免费）
   - 每天 50 次请求
   - 每月 1,500 次请求
   - 完全免费，无需信用卡
3. 点击 **"Subscribe"** 确认

### 1.4 获取 API 密钥

1. 订阅成功后，页面会显示 **"X-RapidAPI-Key"**
2. 复制这个密钥，格式类似：
   ```
   1234567890abcdef1234567890abcdef12
   ```
3. 保存好这个密钥，后面会用到

---

## 🔧 步骤 2：配置后端

### 2.1 修改配置文件

打开文件：`back/src/main/resources/application-judge0.properties`

将 `YOUR_RAPIDAPI_KEY_HERE` 替换为你的实际 API 密钥：

```properties
# Judge0 配置
judge0.url=https://judge0-ce.p.rapidapi.com
judge0.api.key=你的实际API密钥

# 例如：
# judge0.api.key=1234567890abcdef1234567890abcdef12
```

### 2.2 重启后端服务

保存配置文件后，重启 Spring Boot 应用。

---

## 🧪 步骤 3：测试 API

### 3.1 使用测试页面

打开 `judge0-test.html` 文件，点击 **"测试运行代码"** 按钮。

### 3.2 测试判题功能

#### 示例 1：简单的输入输出测试

**代码（Python）：**
```python
n = int(input())
print(n * 2)
```

**输入：**
```
5
```

**预期输出：**
```
10
```

**结果：** statusId = 3（通过）✅

---

#### 示例 2：两数之和（判断对错）

**代码（Python）：**
```python
n = int(input())
nums = list(map(int, input().split()))
target = int(input())

for i in range(n):
    for j in range(i + 1, n):
        if nums[i] + nums[j] == target:
            print(i, j)
            break
```

**输入：**
```
4
2 7 11 15
9
```

**预期输出：**
```
0 1
```

**结果：**
- 如果输出是 `0 1`：statusId = 3（通过）✅
- 如果输出不是 `0 1`：statusId = 4（错误答案）❌

---

#### 示例 3：编译错误

**代码（Python）：**
```python
print("Hello World"
# 缺少右括号
```

**结果：** statusId = 6（编译错误）🔧

---

#### 示例 4：运行时错误

**代码（Python）：**
```python
x = 10 / 0  # 除以零
```

**结果：** statusId = 11（运行时错误 NZEC）💥

---

#### 示例 5：超时

**代码（Python）：**
```python
while True:
    pass  # 无限循环
```

**结果：** statusId = 5（超时）⏱️

---

## 📊 完整的状态码列表

| 状态码 | 描述 | 中文 | 说明 |
|--------|------|------|------|
| 1 | In Queue | 队列中 | 等待执行 |
| 2 | Processing | 处理中 | 正在执行 |
| 3 | Accepted | 通过 | ✅ 答案正确 |
| 4 | Wrong Answer | 错误答案 | ❌ 输出不匹配 |
| 5 | Time Limit Exceeded | 超时 | ⏱️ 超过时间限制 |
| 6 | Compilation Error | 编译错误 | 🔧 代码无法编译 |
| 7 | Runtime Error (SIGSEGV) | 运行时错误 | 💥 段错误 |
| 8 | Runtime Error (SIGXFSZ) | 运行时错误 | 💥 文件过大 |
| 9 | Runtime Error (SIGFPE) | 运行时错误 | 💥 浮点异常 |
| 10 | Runtime Error (SIGABRT) | 运行时错误 | 💥 程序中止 |
| 11 | Runtime Error (NZEC) | 运行时错误 | 💥 非零退出码 |
| 12 | Runtime Error (Other) | 运行时错误 | 💥 其他错误 |
| 13 | Internal Error | 内部错误 | ⚠️ Judge0 内部错误 |
| 14 | Exec Format Error | 执行格式错误 | ⚠️ 可执行文件格式错误 |

---

## 🎓 支持的编程语言

### 常用语言及其 ID

| 语言 | ID | 示例 |
|------|----|----|
| Python 3 | 71 | `print("Hello")` |
| Java | 62 | `System.out.println("Hello");` |
| C++ | 54 | `cout << "Hello";` |
| C | 50 | `printf("Hello");` |
| JavaScript | 63 | `console.log("Hello");` |
| TypeScript | 74 | `console.log("Hello");` |
| Go | 60 | `fmt.Println("Hello")` |
| Rust | 73 | `println!("Hello");` |
| Ruby | 72 | `puts "Hello"` |
| PHP | 68 | `echo "Hello";` |
| C# | 51 | `Console.WriteLine("Hello");` |
| Kotlin | 78 | `println("Hello")` |
| Swift | 83 | `print("Hello")` |

完整列表：https://github.com/judge0/judge0/blob/master/CHANGELOG.md#languages

---

## 💡 API 使用示例

### 提交代码（带预期输出）

```json
POST https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=true&wait=false

Headers:
  Content-Type: application/json
  X-RapidAPI-Key: 你的API密钥
  X-RapidAPI-Host: judge0-ce.p.rapidapi.com

Body:
{
  "source_code": "cHJpbnQoaW50KGlucHV0KCkpICogMik=",  // Base64: print(int(input()) * 2)
  "language_id": 71,
  "stdin": "NQ==",  // Base64: 5
  "expected_output": "MTA="  // Base64: 10
}

Response:
{
  "token": "abc123..."
}
```

### 获取结果

```json
GET https://judge0-ce.p.rapidapi.com/submissions/abc123?base64_encoded=true

Headers:
  X-RapidAPI-Key: 你的API密钥
  X-RapidAPI-Host: judge0-ce.p.rapidapi.com

Response:
{
  "status": {
    "id": 3,
    "description": "Accepted"
  },
  "stdout": "MTA=",  // Base64: 10
  "time": "0.001",
  "memory": 3456
}
```

---

## 🚀 在你的应用中使用

### 前端调用示例

```javascript
// 提交代码
async function submitCode(code, languageId, input, expectedOutput) {
  const response = await fetch('http://localhost:8080/api/code/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      sourceCode: code,
      languageId: languageId,
      stdin: input,
      expectedOutput: expectedOutput
    })
  });
  
  const result = await response.json();
  return result.data.token;
}

// 获取结果
async function getResult(token) {
  const response = await fetch(`http://localhost:8080/api/code/result/${token}`);
  const result = await response.json();
  
  if (result.data.statusId === 3) {
    console.log('✅ 通过！');
  } else if (result.data.statusId === 4) {
    console.log('❌ 错误答案');
  } else {
    console.log('⚠️ 其他错误:', result.data.status);
  }
  
  return result.data;
}
```

---

## ⚠️ 注意事项

### 1. 速率限制

免费计划限制：
- 每天 50 次请求
- 每月 1,500 次请求
- 超过限制会返回 429 错误

**建议：**
- 开发测试时使用免费计划
- 生产环境考虑升级到付费计划或自建 Judge0

### 2. 超时设置

默认超时时间：
- CPU 时间：5 秒
- 墙钟时间：10 秒

可以在提交时自定义：
```json
{
  "cpu_time_limit": 2.0,
  "wall_time_limit": 5.0
}
```

### 3. 内存限制

默认内存限制：128 MB

可以自定义：
```json
{
  "memory_limit": 256000  // 单位：KB
}
```

### 4. Base64 编码

Judge0 API 使用 Base64 编码传输代码和输入输出，避免特殊字符问题。

我们的后端已经自动处理了编码/解码，你不需要手动处理。

---

## 🔄 从本地 Judge0 切换到官方 API

如果你之前配置了本地 Judge0，现在想切换到官方 API：

1. 修改 `application-judge0.properties`：
   ```properties
   judge0.url=https://judge0-ce.p.rapidapi.com
   judge0.api.key=你的API密钥
   ```

2. 重启后端服务

3. 测试是否正常工作

就这么简单！✨

---

## 📈 升级到付费计划

如果免费计划不够用，可以升级：

| 计划 | 价格 | 请求数 |
|------|------|--------|
| Basic | 免费 | 50/天 |
| Pro | $10/月 | 10,000/月 |
| Ultra | $50/月 | 100,000/月 |
| Mega | $200/月 | 500,000/月 |

访问 RapidAPI 页面查看最新价格。

---

## 🎯 总结

✅ **优点：**
- 无需部署，开箱即用
- 稳定可靠，官方维护
- 支持所有 Judge0 功能
- 免费计划足够开发测试

❌ **缺点：**
- 有速率限制
- 依赖外部服务
- 需要网络连接

🎓 **推荐场景：**
- 快速原型开发
- 小规模应用
- 学习和测试
- 不想维护服务器

---

## 🆘 常见问题

### Q1: 如何判断答案是否正确？

A: 检查 `statusId`：
- `statusId === 3`：答案正确 ✅
- `statusId === 4`：答案错误 ❌
- 其他：编译错误、运行时错误等

### Q2: 如何设置多个测试用例？

A: 使用批量提交 API：
```javascript
POST /api/code/submit-batch
{
  "sourceCode": "...",
  "languageId": 71,
  "testCases": [
    {"input": "5", "expectedOutput": "10"},
    {"input": "3", "expectedOutput": "6"}
  ]
}
```

### Q3: 超过免费限额怎么办？

A: 三个选择：
1. 升级到付费计划
2. 部署本地 Judge0（WSL2）
3. 等待第二天重置限额

### Q4: API 密钥泄露了怎么办？

A: 在 RapidAPI 控制台重新生成密钥。

---

## 📚 相关资源

- Judge0 官方文档：https://ce.judge0.com/
- RapidAPI 页面：https://rapidapi.com/judge0-official/api/judge0-ce
- GitHub 仓库：https://github.com/judge0/judge0
- 支持的语言列表：https://github.com/judge0/judge0/blob/master/CHANGELOG.md#languages

---

## 🎉 开始使用

1. ✅ 注册 RapidAPI 账号
2. ✅ 获取 API 密钥
3. ✅ 配置后端
4. ✅ 测试代码执行
5. ✅ 开始构建你的 OJ 系统！

祝你使用愉快！🚀
