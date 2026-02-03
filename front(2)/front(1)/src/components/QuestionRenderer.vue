<template>
  <div class="question-renderer" :class="`question-type-${question.type}`">
    <!-- 题目头部 -->
    <div class="question-header">
      <div class="question-type-badge" :style="{ backgroundColor: typeDisplay.color }">
        <el-icon><component :is="typeDisplay.icon" /></el-icon>
        <span>{{ typeDisplay.label }}</span>
      </div>
      <div class="question-score">{{ question.score }}分</div>
    </div>

    <!-- 题目内容 -->
    <div class="question-content">
      <div class="question-title">
        <span class="question-number">{{ questionNumber }}.</span>
        <span v-html="questionContent"></span>
      </div>
    </div>

    <!-- 单选题 -->
    <div v-if="question.type === 'choice'" class="question-options">
      <el-radio-group v-model="localAnswer" :disabled="readonly">
        <el-radio 
          v-for="(option, index) in question.options" 
          :key="index"
          :label="getOptionKey(option)"
          class="option-item"
        >
          {{ option }}
        </el-radio>
      </el-radio-group>
    </div>

    <!-- 多选题 -->
    <div v-if="question.type === 'multiple'" class="question-options">
      <el-checkbox-group v-model="localAnswer" :disabled="readonly">
        <el-checkbox 
          v-for="(option, index) in question.options" 
          :key="index"
          :label="getOptionKey(option)"
          class="option-item"
        >
          {{ option }}
        </el-checkbox>
      </el-checkbox-group>
      <div class="multiple-hint">（多选题，请选择所有正确答案）</div>
    </div>

    <!-- 判断题 -->
    <div v-if="question.type === 'true_false'" class="question-options true-false-options">
      <el-radio-group v-model="localAnswer" :disabled="readonly">
        <el-radio :label="true" class="option-item">
          <el-icon color="#67C23A"><Select /></el-icon>
          <span>正确</span>
        </el-radio>
        <el-radio :label="false" class="option-item">
          <el-icon color="#F56C6C"><Close /></el-icon>
          <span>错误</span>
        </el-radio>
      </el-radio-group>
    </div>

    <!-- 填空题 -->
    <div v-if="question.type === 'fill'" class="question-answer-input">
      <el-input
        v-model="localAnswer"
        :disabled="readonly"
        placeholder="请输入答案"
        clearable
      />
    </div>

    <!-- 简答题 -->
    <div v-if="question.type === 'short'" class="question-answer-input">
      <el-input
        v-model="localAnswer"
        type="textarea"
        :rows="5"
        :disabled="readonly"
        placeholder="请输入你的答案"
      />
    </div>

    <!-- 编程题 -->
    <div v-if="question.type === 'coding'" class="coding-question">
      <div class="coding-section" v-if="question.requirements">
        <h4>编程要求</h4>
        <p>{{ question.requirements }}</p>
      </div>

      <div class="coding-io-format">
        <div class="coding-section" v-if="question.inputFormat">
          <h4>输入格式</h4>
          <p>{{ question.inputFormat }}</p>
        </div>
        <div class="coding-section" v-if="question.outputFormat">
          <h4>输出格式</h4>
          <p>{{ question.outputFormat }}</p>
        </div>
      </div>

      <div class="coding-section" v-if="question.examples && question.examples.length > 0">
        <h4>示例</h4>
        <div v-for="(example, index) in question.examples" :key="index" class="example-item">
          <div class="example-io">
            <div class="example-input">
              <strong>输入：</strong>
              <pre>{{ example.input }}</pre>
            </div>
            <div class="example-output">
              <strong>输出：</strong>
              <pre>{{ example.output }}</pre>
            </div>
          </div>
          <div class="example-explanation" v-if="example.explanation">
            <strong>说明：</strong>{{ example.explanation }}
          </div>
        </div>
      </div>

      <div class="coding-section" v-if="question.hints">
        <h4>💡 提示</h4>
        <p>{{ question.hints }}</p>
      </div>

      <div class="coding-editor">
        <h4>代码编辑器</h4>
        <el-input
          v-model="localAnswer"
          type="textarea"
          :rows="15"
          :disabled="readonly"
          placeholder="请在此处编写代码..."
          class="code-textarea"
        />
      </div>
    </div>

    <!-- 论述题 -->
    <div v-if="question.type === 'essay'" class="question-answer-input">
      <el-input
        v-model="localAnswer"
        type="textarea"
        :rows="8"
        :disabled="readonly"
        placeholder="请详细论述你的观点"
      />
    </div>

    <!-- 答案和解析（仅在查看模式显示） -->
    <div v-if="showAnswer" class="question-answer-section">
      <div class="correct-answer">
        <el-icon color="#67C23A"><CircleCheck /></el-icon>
        <strong>参考答案：</strong>
        <span class="answer-content">{{ formattedAnswer }}</span>
      </div>
      <div v-if="question.explanation" class="answer-explanation">
        <el-icon color="#409EFF"><InfoFilled /></el-icon>
        <strong>解析：</strong>
        <span>{{ question.explanation }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { 
  CircleCheck, 
  Select, 
  Close, 
  InfoFilled 
} from '@element-plus/icons-vue'
import { 
  getTypeDisplay, 
  getOptionKey, 
  formatQuestionAnswer,
  getQuestionContent 
} from '@/utils/questionHelper'

const props = defineProps({
  question: {
    type: Object,
    required: true
  },
  questionNumber: {
    type: Number,
    default: 1
  },
  modelValue: {
    type: [String, Array, Boolean, Number],
    default: null
  },
  readonly: {
    type: Boolean,
    default: false
  },
  showAnswer: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

// 本地答案
const localAnswer = ref(props.modelValue)

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  localAnswer.value = newVal
})

// 监听本地值变化，向外发送
watch(localAnswer, (newVal) => {
  emit('update:modelValue', newVal)
})

// 题型显示信息
const typeDisplay = computed(() => getTypeDisplay(props.question.type))

// 题目内容
const questionContent = computed(() => getQuestionContent(props.question))

// 格式化的答案
const formattedAnswer = computed(() => formatQuestionAnswer(props.question))
</script>

<style scoped>
/* 题目卡片 - 美化版 */
.question-renderer {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out;
}

.question-renderer:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 题目头部 */
.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

/* 题型标签 - 渐变效果 */
.question-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 24px;
  color: white;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 分数标签 */
.question-score {
  font-size: 24px;
  font-weight: 700;
  color: #F56C6C;
  text-shadow: 0 2px 4px rgba(245, 108, 108, 0.2);
}

/* 题目内容 */
.question-content {
  margin-bottom: 24px;
}

.question-title {
  font-size: 18px;
  line-height: 1.8;
  color: #333;
  font-weight: 500;
}

.question-number {
  display: inline-block;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  margin-right: 12px;
  font-weight: 700;
  padding: 0 8px;
}

/* 选项容器 */
.question-options {
  margin: 24px 0;
}

/* 选项样式 - 美化版 */
.option-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.option-item:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
  transform: translateX(4px);
}

/* 多选题提示 */
.multiple-hint {
  margin-top: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #fff7e6 0%, #fff3d9 100%);
  border-radius: 8px;
  border-left: 4px solid #E6A23C;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.multiple-hint::before {
  content: "💡";
  font-size: 18px;
}

/* 判断题特殊样式 */
.true-false-options {
  display: flex;
  gap: 20px;
}

.true-false-options .option-item {
  flex: 1;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  padding: 20px;
}

/* 输入框样式 */
.question-answer-input {
  margin: 24px 0;
}

.question-answer-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 2px #e8e8e8 inset;
  transition: all 0.3s ease;
}

.question-answer-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px #667eea inset;
}

.question-answer-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea inset, 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.question-answer-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s ease;
  min-height: 120px;
}

.question-answer-input :deep(.el-textarea__inner:hover) {
  border-color: #667eea;
}

.question-answer-input :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

/* 编程题样式 */
.coding-question {
  margin: 24px 0;
}

.coding-section {
  margin: 20px 0;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.coding-section h4 {
  color: #667eea;
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
}

.coding-section p {
  margin: 0;
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
}

.coding-io-format {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.example-item {
  background: white;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.example-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.example-io {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 12px;
}

.example-input pre,
.example-output pre {
  margin: 4px 0 0 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  border: 1px solid #e8e8e8;
  overflow-x: auto;
}

.example-explanation {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.coding-editor {
  margin-top: 20px;
}

.coding-editor h4 {
  margin-bottom: 12px;
  color: #333;
  font-size: 16px;
}

.code-textarea :deep(.el-textarea__inner) {
  font-family: 'Courier New', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  min-height: 200px;
}

/* 答案区域 */
.question-answer-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px dashed #e8e8e8;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.correct-answer,
.answer-explanation {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px 20px;
  border-radius: 12px;
  line-height: 1.8;
}

.correct-answer {
  background: linear-gradient(135deg, #f0f9ff 0%, #e8f5e9 100%);
  border-left: 4px solid #67C23A;
}

.answer-explanation {
  background: linear-gradient(135deg, #fff7e6 0%, #fff3d9 100%);
  border-left: 4px solid #409EFF;
}

.correct-answer strong,
.answer-explanation strong {
  font-weight: 700;
  font-size: 16px;
}

.answer-content {
  color: #67C23A;
  font-weight: 700;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-renderer {
    padding: 20px;
  }
  
  .coding-io-format,
  .example-io {
    grid-template-columns: 1fr;
  }
  
  .true-false-options {
    flex-direction: column;
  }
  
  .question-number {
    min-width: 28px;
    height: 28px;
    line-height: 28px;
    font-size: 14px;
  }
  
  .question-title {
    font-size: 16px;
  }
}
</style>
