# VR教室AI提问功能集成文档

## 功能概述

学生端VR教室中的"向AI提问"功能已成功接入AI服务，学生可以在3D拆解实验室中点击任意硬件组件，通过AI助手获取专业的硬件知识讲解。

## 技术架构

### 前端实现

**文件位置**: `front(2)/front(1)/src/views/student/VRClassroom.vue`

**核心功能**:
1. 3D场景交互 - 使用Three.js渲染CPU硬件组件
2. 组件选择 - 点击组件显示详细信息面板
3. AI问答界面 - 实时对话框，支持多轮对话
4. 上下文感知 - AI根据当前选中的组件提供针对性回答

**关键代码片段**:

```javascript
// AI Q&A 函数
async function sendQA() {
  const q = qaInput.value.trim()
  if (!q || !selectedComp.value) return
  
  qaMessages.value.push({ role: 'user', text: q })
  qaInput.value = ''
  qaLoading.value = true
  await nextTick()
  scrollQA()

  try {
    // 构建系统提示词，包含组件信息
    const systemPrompt = `你是一位计算机体系结构专家和硬件教师，回答风格简洁专业。学生当前正在 3D 拆解实验室查看组件：${selectedComp.value.name}（${selectedComp.value.category}）。该组件说明：${selectedComp.value.description}。规格参数：${selectedComp.value.specs.map(s => s.k + '=' + s.v).join('，')}。请用中文简洁回答，控制在120字以内。`
    
    // 调用AI API
    const resp = await aiAPI.chatWithSystem(systemPrompt, q)
    const d = resp?.data
    const text = (typeof d === 'string' ? d : d?.response || d?.content || d?.message || d?.data) || '我暂时无法回答，请稍后重试。'
    
    qaMessages.value.push({ role: 'ai', text })
  } catch {
    qaMessages.value.push({ role: 'ai', text: '连接 AI 服务失败，请检查网络后重试。' })
  } finally {
    qaLoading.value = false
    await nextTick()
    scrollQA()
  }
}
```

### API层

**文件位置**: `front(2)/front(1)/src/api/ai.js`

**接口定义**:
```javascript
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
}
```

### 后端实现

#### 控制器层

**文件位置**: `back/src/main/java/com/experiment/controller/AIAssistantController.java`

**新增接口**:
```java
/**
 * 带系统提示的AI对话（用于VR教室等场景）
 */
@PostMapping("/chat-with-system")
public Result<String> chatWithSystem(@RequestParam String systemPrompt, @RequestParam String userMessage) {
    log.info("收到带系统提示的AI对话请求");
    try {
        String response = assistantService.chatWithSystemPrompt(systemPrompt, userMessage);
        return Result.success("AI回复成功", response);
    } catch (Exception e) {
        log.error("AI对话处理失败", e);
        return Result.error("AI对话处理失败: " + e.getMessage());
    }
}
```

#### 服务层

**文件位置**: `back/src/main/java/com/experiment/service/AIAssistantService.java`

**接口定义**:
```java
/**
 * 带系统提示的AI对话
 * @param systemPrompt 系统提示
 * @param userMessage 用户消息
 * @return AI回复
 */
String chatWithSystemPrompt(String systemPrompt, String userMessage);
```

**文件位置**: `back/src/main/java/com/experiment/service/Impl/AIAssistantServiceImpl.java`

**实现代码**:
```java
@Override
public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
    log.info("收到带系统提示的AI对话请求");
    log.debug("系统提示: {}", systemPrompt);
    log.debug("用户消息: {}", userMessage);
    
    try {
        String response = aiService.chatWithSystem(systemPrompt, userMessage);
        log.info("AI对话完成，响应长度: {}", response.length());
        return response;
    } catch (Exception e) {
        log.error("AI对话处理失败", e);
        return "抱歉，AI服务暂时无法回答您的问题，请稍后重试。错误信息：" + e.getMessage();
    }
}
```

## 功能特点

### 1. 上下文感知
- AI根据学生当前选中的硬件组件提供针对性回答
- 系统提示词包含组件名称、类别、描述和规格参数
- 确保回答与学习内容高度相关

### 2. 专业性
- AI扮演计算机体系结构专家和硬件教师角色
- 回答风格简洁专业，适合教学场景
- 控制回答长度在120字以内，便于快速理解

### 3. 用户体验
- 实时对话界面，支持多轮问答
- 加载状态提示（思考动画）
- 自动滚动到最新消息
- 错误处理友好，网络异常时给出明确提示

### 4. 可扩展性
- 支持不同场景的系统提示词定制
- 可轻松扩展到其他教学模块
- API设计通用，便于复用

## 使用流程

1. **进入VR教室**: 学生登录后进入VR教室页面
2. **选择组件**: 在3D场景中点击任意硬件组件（如CPU、内存、缓存等）
3. **查看详情**: 右侧弹出组件详情面板，显示名称、类别、描述和规格
4. **提问**: 在"向AI提问"输入框中输入问题
5. **获取回答**: AI基于组件上下文给出专业回答
6. **多轮对话**: 可继续追问，形成完整的学习对话

## 支持的硬件组件

### Level 1 - 机箱级
- 主板 (Motherboard)
- CPU芯片 (Intel Core i9-13900K)
- DDR5内存
- CPU散热器

### Level 2 - 封装结构
- IHS (集成散热顶盖)
- 热界面材料 (TIM)
- Silicon Die (硅裸片)
- 有机基板 (Substrate)
- LGA 1700针脚矩阵

### Level 3 - 逻辑结构
- ALU (算术逻辑单元)
- 控制单元 (Control Unit)
- L1/L2/L3缓存
- 寄存器组
- 取指/译码/执行/写回单元

## 示例对话

**场景**: 学生选中了"ALU (算术逻辑单元)"

**学生**: 这个ALU是怎么工作的？

**AI**: ALU是CPU的核心计算单元，负责执行所有整数运算（加减乘除）和逻辑运算（AND/OR/NOT/XOR）。现代CPU每个核心有4个ALU，可并行处理多条指令。当指令到达时，ALU从寄存器读取操作数，执行运算，然后将结果写回寄存器，整个过程仅需1个时钟周期。

**学生**: 为什么需要4个ALU？

**AI**: 4个ALU支持超标量架构和乱序执行。当程序中有多条独立的运算指令时，CPU可以同时将它们分配给不同的ALU并行执行，大幅提升性能。例如计算a+b和c+d可以同时进行，而不必等待前一个完成。这是现代CPU实现高性能的关键技术之一。

## 技术优势

1. **智能化教学**: AI实时解答学生疑问，提供个性化学习支持
2. **沉浸式体验**: 3D可视化 + AI讲解，增强学习效果
3. **降低教师负担**: AI助手可处理大量重复性问题
4. **数据积累**: 可收集学生提问数据，优化教学内容

## 后续优化方向

1. **知识库集成**: 接入专业硬件知识库，提高回答准确性
2. **多模态交互**: 支持语音提问和回答
3. **学习路径推荐**: 根据提问历史推荐相关学习内容
4. **问题统计分析**: 分析高频问题，优化课程设计
5. **多语言支持**: 支持英文等其他语言的问答

## 配置说明

### 前端配置
无需额外配置，已集成在VRClassroom.vue中

### 后端配置
确保`application.yml`中配置了阿里云百炼API密钥：

```yaml
dashscope:
  api-key: your-api-key-here
  model: qwen-plus
  app-id: your-app-id-here
```

## 测试建议

1. 测试不同组件的问答效果
2. 测试多轮对话的上下文保持
3. 测试网络异常情况的错误处理
4. 测试并发请求的性能表现
5. 收集真实学生使用反馈

## 总结

VR教室的AI提问功能已完整实现并集成到系统中。该功能通过上下文感知的AI对话，为学生提供了智能化的学习辅助，显著提升了VR教室的教学价值和用户体验。













