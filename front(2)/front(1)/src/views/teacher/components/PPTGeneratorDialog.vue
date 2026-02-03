<template>
  <div class="dialog-overlay" @click.self="$emit('close')">
    <div class="dialog-content">
      <div class="dialog-header">
        <h2>🎨 AI生成新技术PPT</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <div class="dialog-body">
        <div class="form-group">
          <label>PPT主题 *</label>
          <input 
            v-model="form.topic" 
            type="text" 
            placeholder="例如：MCP技术详解、2024年信息安全事件分析"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>关键词 *</label>
          <div class="keywords-input">
            <div class="keyword-tags">
              <span v-for="(keyword, index) in form.keywords" :key="index" class="keyword-tag">
                {{ keyword }}
                <button @click="removeKeyword(index)" class="remove-btn">×</button>
              </span>
            </div>
            <input 
              v-model="currentKeyword"
              @keyup.enter="addKeyword"
              type="text" 
              placeholder="输入关键词后按回车添加"
              class="form-input"
            />
          </div>
          <p class="form-hint">建议添加3-5个关键词</p>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>页数</label>
            <input 
              v-model.number="form.slideCount" 
              type="number" 
              min="5" 
              max="30"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label>风格</label>
            <select v-model="form.style" class="form-select">
              <option value="professional">专业风格</option>
              <option value="creative">创意风格</option>
              <option value="academic">学术风格</option>
            </select>
          </div>
        </div>

        <div class="preview-section">
          <h3>📋 生成预览</h3>
          <div class="preview-info">
            <p><strong>主题：</strong>{{ form.topic || '未设置' }}</p>
            <p><strong>关键词：</strong>{{ form.keywords.join('、') || '未设置' }}</p>
            <p><strong>页数：</strong>{{ form.slideCount }}页</p>
            <p><strong>风格：</strong>{{ getStyleText(form.style) }}</p>
          </div>
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
  name: 'PPTGeneratorDialog',
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const form = ref({
      topic: '',
      keywords: [],
      slideCount: 15,
      style: 'professional'
    })

    const currentKeyword = ref('')
    const loading = ref(false)

    const teacherId = localStorage.getItem('userId') || '2'

    const isValid = computed(() => {
      return form.value.topic && form.value.keywords.length > 0
    })

    const addKeyword = () => {
      const keyword = currentKeyword.value.trim()
      if (keyword && !form.value.keywords.includes(keyword)) {
        form.value.keywords.push(keyword)
        currentKeyword.value = ''
      }
    }

    const removeKeyword = (index) => {
      form.value.keywords.splice(index, 1)
    }

    const getStyleText = (style) => {
      const map = {
        'professional': '专业风格',
        'creative': '创意风格',
        'academic': '学术风格'
      }
      return map[style] || style
    }

    const handleSubmit = async () => {
      if (!isValid.value) {
        ElMessage.warning('请填写完整信息')
        return
      }

      loading.value = true
      try {
        const res = await smartTeachingApi.generatePPT({
          teacherId,
          ...form.value
        })

        if (res.code === 1) {
          ElMessage.success('PPT生成任务已提交，请稍后查看结果')
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
      currentKeyword,
      loading,
      isValid,
      addKeyword,
      removeKeyword,
      getStyleText,
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
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

.form-input,
.form-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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
  background: #667eea;
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

.preview-section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-top: 20px;
}

.preview-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.preview-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
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
  background: #667eea;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #5568d3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

