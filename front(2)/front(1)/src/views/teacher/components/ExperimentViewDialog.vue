<template>
  <el-dialog
    v-model="visible"
    title="实验指导书"
    width="900px"
    @close="handleClose"
  >
    <div v-loading="loading" class="guide-container">
      <div v-if="guide && guide.status === 'completed'">
        <!-- 标题 -->
        <div class="guide-header">
          <h2>{{ guide.title || guide.chapterName }}</h2>
          <el-tag type="success">已完成</el-tag>
        </div>

        <!-- 实验目的 -->
        <div class="guide-section">
          <h3>📌 实验目的</h3>
          <ul>
            <li v-for="(obj, index) in parseJSON(guide.objectives)" :key="index">
              <div v-html="formatText(obj)"></div>
            </li>
          </ul>
        </div>

        <!-- 实验环境 -->
        <div class="guide-section" v-if="guide.environment">
          <h3>💻 实验环境</h3>
          <div v-html="formatEnvironment(guide.environment)"></div>
        </div>

        <!-- 实验步骤 -->
        <div class="guide-section">
          <h3>📝 实验步骤</h3>
          <ol class="steps-list">
            <li v-for="(step, index) in parseJSON(guide.steps)" :key="index">
              <div v-html="formatStep(step)"></div>
            </li>
          </ol>
        </div>

        <!-- 示例代码 -->
        <div class="guide-section" v-if="guide.code">
          <h3>💡 示例代码</h3>
          <pre class="code-block">{{ guide.code }}</pre>
        </div>

        <!-- 预期结果 -->
        <div class="guide-section" v-if="guide.expectedResults">
          <h3>✅ 预期结果</h3>
          <div v-if="Array.isArray(parseJSON(guide.expectedResults))">
            <ol>
              <li v-for="(result, index) in parseJSON(guide.expectedResults)" :key="index">
                <div v-html="formatText(result)"></div>
              </li>
            </ol>
          </div>
          <div v-else v-html="formatText(guide.expectedResults)"></div>
        </div>

        <!-- 思考题 -->
        <div class="guide-section">
          <h3>🤔 思考题</h3>
          <ol>
            <li v-for="(q, index) in parseJSON(guide.questions)" :key="index">
              {{ q }}
            </li>
          </ol>
        </div>

        <!-- 参考答案 -->
        <div class="guide-section">
          <h3>📖 参考答案</h3>
          <ol>
            <li v-for="(a, index) in parseJSON(guide.answers)" :key="index">
              {{ a }}
            </li>
          </ol>
        </div>
      </div>

      <div v-else-if="guide && guide.status === 'generating'" class="status-message">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>AI正在生成实验指导书，请稍候...</p>
      </div>

      <div v-else-if="guide && guide.status === 'failed'" class="status-message error">
        <el-icon><CircleClose /></el-icon>
        <p>生成失败，请重试</p>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" v-if="guide && guide.status === 'completed'">
        导出PDF
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { Loading, CircleClose } from '@element-plus/icons-vue'
import * as smartTeachingApi from '@/api/smartTeaching'

const props = defineProps({
  guideId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close'])

const visible = ref(true)
const loading = ref(false)
const guide = ref(null)

const loadGuide = async () => {
  loading.value = true
  try {
    const res = await smartTeachingApi.getExperimentGuide(props.guideId)
    // 兼容两种响应格式
    if (res.code === 1 || res.success === true) {
      guide.value = res.data
    }
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const parseJSON = (str) => {
  if (!str) return []
  try {
    return JSON.parse(str)
  } catch {
    return [str]
  }
}

// 格式化文本，处理换行符和特殊字符
const formatText = (text) => {
  if (!text) return ''
  return text
    .replace(/\\n/g, '<br>')  // 将 \n 转换为 <br>
    .replace(/\n/g, '<br>')   // 将真实换行转换为 <br>
    .replace(/\\t/g, '&nbsp;&nbsp;')  // 将 \t 转换为空格
    .trim()
}

// 格式化环境信息
const formatEnvironment = (env) => {
  if (!env) return ''
  
  try {
    const envObj = JSON.parse(env)
    let html = ''
    
    // 处理对象格式的环境信息
    if (typeof envObj === 'object' && !Array.isArray(envObj)) {
      Object.keys(envObj).forEach(key => {
        const value = envObj[key]
        const keyName = key === 'os' ? '操作系统' : 
                       key === 'shell' ? 'Shell环境' :
                       key === 'tools' ? '工具软件' :
                       key === 'language' ? '编程语言' :
                       key === 'compiler' ? '编译器' :
                       key === 'ide' ? '开发环境' :
                       key === 'additional_tools' ? '辅助工具' :
                       key === 'hardware' ? '硬件要求' :
                       key === 'software' ? '软件要求' :
                       key === 'data' ? '数据准备' :
                       key === 'prerequisites' ? '前置条件' : key
        
        if (Array.isArray(value)) {
          html += `<div class="env-section"><strong>${keyName}：</strong><ul>`
          value.forEach(item => {
            html += `<li>${formatText(item)}</li>`
          })
          html += '</ul></div>'
        } else {
          html += `<div class="env-section"><strong>${keyName}：</strong>${formatText(value)}</div>`
        }
      })
    } else if (Array.isArray(envObj)) {
      html += '<ul>'
      envObj.forEach(item => {
        html += `<li>${formatText(item)}</li>`
      })
      html += '</ul>'
    } else {
      html = formatText(env)
    }
    
    return html
  } catch {
    return formatText(env)
  }
}

// 格式化步骤，移除XML代码
const formatStep = (step) => {
  if (!step) return ''
  
  let formatted = formatText(step)
  
  // 移除draw.io XML代码
  formatted = formatted.replace(/<mxGraphModel[\s\S]*?<\/mxGraphModel>/g, '<div class="xml-placeholder">📊 [此处应包含用例图，请使用draw.io等工具绘制]</div>')
  formatted = formatted.replace(/<mx[^>]*>/g, '')
  formatted = formatted.replace(/<\/mx[^>]*>/g, '')
  
  return formatted
}

const handleClose = () => {
  emit('close')
}

onMounted(() => {
  loadGuide()
})
</script>

<style scoped>
.guide-container {
  min-height: 400px;
}

.guide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e0e0e0;
}

.guide-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.guide-section {
  margin-bottom: 24px;
}

.guide-section h3 {
  color: #409eff;
  font-size: 18px;
  margin-bottom: 12px;
}

.guide-section ul,
.guide-section ol {
  padding-left: 24px;
  line-height: 1.8;
}

.guide-section li {
  margin-bottom: 8px;
  color: #555;
}

.steps-list {
  counter-reset: step-counter;
}

.steps-list li {
  position: relative;
  padding-left: 8px;
}

.code-block {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  line-height: 1.6;
  font-family: 'Courier New', monospace;
  border: 1px solid #e0e0e0;
}

.env-section {
  margin-bottom: 16px;
}

.env-section strong {
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.env-section ul {
  margin: 0;
  padding-left: 24px;
}

.env-section li {
  margin-bottom: 6px;
  line-height: 1.6;
}

.xml-placeholder {
  background: #e3f2fd;
  border: 2px dashed #2196f3;
  border-radius: 8px;
  padding: 16px;
  margin: 12px 0;
  text-align: center;
  color: #1976d2;
  font-weight: 500;
}

.status-message {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.status-message .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.status-message.error {
  color: #f56c6c;
}

.status-message p {
  font-size: 16px;
  margin: 0;
}
</style>

