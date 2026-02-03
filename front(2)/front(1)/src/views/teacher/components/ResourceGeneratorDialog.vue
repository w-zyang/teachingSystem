<template>
  <div class="dialog-overlay" @click.self="$emit('close')">
    <div class="dialog-content">
      <div class="dialog-header">
        <h2>📦 生成教学资源包</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <div class="dialog-body">
        <div class="form-group">
          <label>章节名称 *</label>
          <input 
            v-model="form.chapterName" 
            type="text" 
            placeholder="例如：数据结构 - 第三章：栈和队列"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>知识点 *</label>
          <div class="keywords-input">
            <div class="keyword-tags">
              <span v-for="(point, index) in form.knowledgePoints" :key="index" class="keyword-tag">
                {{ point }}
                <button @click="removePoint(index)" class="remove-btn">×</button>
              </span>
            </div>
            <input 
              v-model="currentPoint"
              @keyup.enter="addPoint"
              type="text" 
              placeholder="输入知识点后按回车添加"
              class="form-input"
            />
          </div>
          <p class="form-hint">添加本章节的主要知识点</p>
        </div>

        <div class="form-group">
          <label>难度等级</label>
          <div class="difficulty-options">
            <label class="radio-option">
              <input type="radio" v-model="form.difficultyLevel" value="easy" />
              <span>简单</span>
            </label>
            <label class="radio-option">
              <input type="radio" v-model="form.difficultyLevel" value="medium" />
              <span>中等</span>
            </label>
            <label class="radio-option">
              <input type="radio" v-model="form.difficultyLevel" value="hard" />
              <span>困难</span>
            </label>
          </div>
        </div>

        <div class="resource-preview">
          <h3>📋 将生成以下资源：</h3>
          <ul>
            <li>✅ 课堂测验（10题：5选择 + 3填空 + 2简答）</li>
            <li>✅ 课后作业（5题：2计算 + 2编程 + 1分析）</li>
            <li>✅ 实验指导书（含步骤、代码、思考题）</li>
            <li>✅ 知识点总结PPT（5-8页）</li>
          </ul>
          <p class="estimate-time">预计生成时间：1-2分钟</p>
        </div>
      </div>

      <div class="dialog-footer">
        <button class="btn-cancel" @click="$emit('close')">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="!isValid || loading">
          {{ loading ? '生成中...' : '开始生成' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as smartTeachingApi from '@/api/smartTeaching'

export default {
  name: 'ResourceGeneratorDialog',
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const form = ref({
      chapterName: '',
      knowledgePoints: [],
      difficultyLevel: 'medium'
    })

    const currentPoint = ref('')
    const loading = ref(false)

    const teacherId = localStorage.getItem('userId') || '2'

    const isValid = computed(() => {
      return form.value.chapterName && form.value.knowledgePoints.length > 0
    })

    const addPoint = () => {
      const point = currentPoint.value.trim()
      if (point && !form.value.knowledgePoints.includes(point)) {
        form.value.knowledgePoints.push(point)
        currentPoint.value = ''
      }
    }

    const removePoint = (index) => {
      form.value.knowledgePoints.splice(index, 1)
    }

    const handleSubmit = async () => {
      if (!isValid.value) {
        ElMessage.warning('请填写完整信息')
        return
      }

      loading.value = true
      try {
        const res = await smartTeachingApi.generateResourcePackage({
          teacherId,
          courseId: null,
          ...form.value
        })

        if (res.code === 1) {
          ElMessage.success('资源包生成任务已提交')
          emit('success')
        } else {
          ElMessage.error(res.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交失败', error)
        ElMessage.error('提交失败，请重试')
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      currentPoint,
      loading,
      isValid,
      addPoint,
      removePoint,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

.dialog-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 32px;
  color: #999;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.dialog-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.keywords-input {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 8px;
  min-height: 80px;
}

.keyword-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.keyword-tag {
  background: #51cf66;
  color: white;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.keyword-tag .remove-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  padding: 0;
}

.keywords-input .form-input {
  border: none;
  padding: 8px;
}

.form-hint {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #999;
}

.difficulty-options {
  display: flex;
  gap: 16px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 10px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  transition: all 0.3s;
}

.radio-option:hover {
  border-color: #667eea;
  background: #f8f9fa;
}

.radio-option input[type="radio"] {
  cursor: pointer;
}

.radio-option input[type="radio"]:checked + span {
  color: #667eea;
  font-weight: 600;
}

.resource-preview {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
}

.resource-preview h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.resource-preview ul {
  list-style: none;
  padding: 0;
  margin: 0 0 12px 0;
}

.resource-preview li {
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}

.estimate-time {
  margin: 0;
  font-size: 13px;
  color: #999;
  font-style: italic;
}

.dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-submit {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #e9ecef;
  color: #666;
}

.btn-cancel:hover {
  background: #dee2e6;
}

.btn-submit {
  background: #51cf66;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #40c057;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(81, 207, 102, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>

