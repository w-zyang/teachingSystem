<template>
  <div class="study-notes-container">
    <!-- 头部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <h2><i class="fas fa-sticky-note"></i> 学习笔记与标注</h2>
        <span class="resource-title">{{ resourceTitle }}</span>
      </div>
      <div class="toolbar-right">
        <button @click="showNoteModal = true" class="btn btn-primary">
          <i class="fas fa-plus"></i> 新建笔记
        </button>
        <div class="filter-group">
          <select v-model="selectedFilter" @change="applyFilter" class="filter-select">
            <option value="all">所有项目</option>
            <option value="notes">仅笔记</option>
            <option value="highlights">仅高亮</option>
            <option value="bookmarks">仅书签</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <i class="fas fa-sticky-note"></i>
        <span>笔记: {{ notesCount }}</span>
      </div>
      <div class="stat-item">
        <i class="fas fa-highlighter"></i>
        <span>标注: {{ annotationsCount }}</span>
      </div>
      <div class="stat-item">
        <i class="fas fa-star"></i>
        <span>重要标记: {{ importantCount }}</span>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 笔记列表 -->
      <div class="notes-section">
        <h3><i class="fas fa-edit"></i> 我的笔记</h3>
        <div v-if="filteredNotes.length === 0" class="empty-state">
          <i class="fas fa-file-alt"></i>
          <p>还没有笔记，点击"新建笔记"开始记录学习心得吧！</p>
        </div>
        <div v-else class="notes-list">
          <div 
            v-for="note in filteredNotes" 
            :key="note.id"
            class="note-card"
            :style="{ borderLeftColor: note.color }"
          >
            <div class="note-header">
              <h4>{{ note.title }}</h4>
              <div class="note-meta">
                <span class="page-info">第{{ note.pageNumber }}页</span>
                <span class="date-info">{{ formatDate(note.createTime) }}</span>
                <div class="note-actions">
                  <button @click="editNote(note)" class="btn-icon" title="编辑">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteNote(note.id)" class="btn-icon" title="删除">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
            <div class="note-content">{{ note.content }}</div>
            <div v-if="note.tags" class="note-tags">
              <span 
                v-for="tag in note.tags.split(',')" 
                :key="tag"
                class="tag"
              >
                {{ tag.trim() }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 标注列表 -->
      <div class="annotations-section">
        <h3><i class="fas fa-highlighter"></i> 我的标注</h3>
        <div v-if="filteredAnnotations.length === 0" class="empty-state">
          <i class="fas fa-marker"></i>
          <p>还没有标注，在阅读资料时选择文本即可创建标注！</p>
        </div>
        <div v-else class="annotations-list">
          <div 
            v-for="annotation in filteredAnnotations" 
            :key="annotation.id"
            class="annotation-card"
            :class="annotation.type"
          >
            <div class="annotation-header">
              <div class="annotation-type">
                <i :class="getAnnotationIcon(annotation.type)"></i>
                <span>{{ getAnnotationTypeName(annotation.type) }}</span>
              </div>
              <div class="annotation-meta">
                <span class="page-info">第{{ annotation.pageNumber }}页</span>
                <div class="importance-stars">
                  <i 
                    v-for="i in 5" 
                    :key="i"
                    :class="i <= annotation.importance ? 'fas fa-star' : 'far fa-star'"
                  ></i>
                </div>
                <div class="annotation-actions">
                  <button @click="editAnnotation(annotation)" class="btn-icon" title="编辑">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteAnnotation(annotation.id)" class="btn-icon" title="删除">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
            <div v-if="annotation.selectedText" class="annotation-text">
              "{{ annotation.selectedText }}"
            </div>
            <div v-if="annotation.comment" class="annotation-comment">
              <i class="fas fa-comment"></i>
              {{ annotation.comment }}
            </div>
            <div class="annotation-date">{{ formatDate(annotation.createTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建/编辑笔记模态框 -->
    <div v-if="showNoteModal" class="modal" @click.self="closeNoteModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingNote ? '编辑笔记' : '新建笔记' }}</h3>
          <button @click="closeNoteModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveNote">
            <div class="form-group">
              <label>笔记标题 *</label>
              <input 
                v-model="noteForm.title" 
                type="text" 
                required 
                placeholder="输入笔记标题"
                maxlength="100"
              />
            </div>
            <div class="form-group">
              <label>笔记内容 *</label>
              <textarea 
                v-model="noteForm.content" 
                required 
                placeholder="记录您的学习心得、重点总结等..."
                rows="8"
              ></textarea>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>页码</label>
                <input 
                  v-model.number="noteForm.pageNumber" 
                  type="number" 
                  min="1"
                  placeholder="1"
                />
              </div>
              <div class="form-group">
                <label>颜色标记</label>
                <div class="color-options">
                  <button 
                    v-for="color in colorOptions" 
                    :key="color.value"
                    type="button"
                    @click="noteForm.color = color.value"
                    class="color-btn"
                    :class="{ active: noteForm.color === color.value }"
                    :style="{ backgroundColor: color.value }"
                    :title="color.name"
                  ></button>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label>标签</label>
              <input 
                v-model="noteForm.tags" 
                type="text" 
                placeholder="用逗号分隔多个标签，如：重点,概念,公式"
              />
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input 
                  v-model="noteForm.isPublic" 
                  type="checkbox"
                />
                <span class="checkmark"></span>
                公开此笔记（其他同学可见）
              </label>
            </div>
            <div class="modal-actions">
              <button type="button" @click="closeNoteModal" class="btn btn-secondary">
                取消
              </button>
              <button type="submit" class="btn btn-primary">
                {{ editingNote ? '更新' : '创建' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 标注编辑模态框 -->
    <div v-if="showAnnotationModal" class="modal" @click.self="closeAnnotationModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑标注</h3>
          <button @click="closeAnnotationModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveAnnotation">
            <div class="form-group">
              <label>标注类型</label>
              <select v-model="annotationForm.type" required>
                <option value="highlight">高亮标记</option>
                <option value="underline">下划线</option>
                <option value="comment">批注</option>
                <option value="bookmark">书签</option>
              </select>
            </div>
            <div class="form-group">
              <label>重要程度</label>
              <div class="importance-selector">
                <button 
                  v-for="i in 5" 
                  :key="i"
                  type="button"
                  @click="annotationForm.importance = i"
                  class="star-btn"
                  :class="{ active: i <= annotationForm.importance }"
                >
                  <i :class="i <= annotationForm.importance ? 'fas fa-star' : 'far fa-star'"></i>
                </button>
              </div>
            </div>
            <div class="form-group">
              <label>颜色</label>
              <div class="color-options">
                <button 
                  v-for="color in annotationColors" 
                  :key="color.value"
                  type="button"
                  @click="annotationForm.color = color.value"
                  class="color-btn"
                  :class="{ active: annotationForm.color === color.value }"
                  :style="{ backgroundColor: color.value }"
                  :title="color.name"
                ></button>
              </div>
            </div>
            <div class="form-group">
              <label>备注说明</label>
              <textarea 
                v-model="annotationForm.comment" 
                placeholder="添加您的注释或说明..."
                rows="4"
              ></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" @click="closeAnnotationModal" class="btn btn-secondary">
                取消
              </button>
              <button type="submit" class="btn btn-primary">
                更新标注
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getResourceNotes,
  getResourceAnnotations,
  createNote,
  updateNote,
  deleteNote as deleteNoteApi,
  updateAnnotation,
  deleteAnnotation as deleteAnnotationApi
} from '@/api/notes'

// Props
const props = defineProps({
  resourceId: {
    type: Number,
    required: true
  },
  resourceTitle: {
    type: String,
    default: '课程资源'
  }
})

// 响应式数据
const notes = ref([])
const annotations = ref([])
const selectedFilter = ref('all')
const showNoteModal = ref(false)
const showAnnotationModal = ref(false)
const editingNote = ref(null)
const editingAnnotation = ref(null)

// 获取学生ID
const studentId = ref(localStorage.getItem('userId') || 17)

// 表单数据
const noteForm = reactive({
  title: '',
  content: '',
  pageNumber: 1,
  color: '#FFD700',
  tags: '',
  isPublic: false
})

const annotationForm = reactive({
  type: 'highlight',
  importance: 3,
  color: '#FFFF00',
  comment: ''
})

// 颜色选项
const colorOptions = [
  { name: '金黄色', value: '#FFD700' },
  { name: '天蓝色', value: '#87CEEB' },
  { name: '淡绿色', value: '#98FB98' },
  { name: '粉红色', value: '#FFB6C1' },
  { name: '淡紫色', value: '#DDA0DD' },
  { name: '橙色', value: '#FFA500' }
]

const annotationColors = [
  { name: '黄色高亮', value: '#FFFF00' },
  { name: '绿色高亮', value: '#90EE90' },
  { name: '蓝色高亮', value: '#87CEEB' },
  { name: '粉色高亮', value: '#FFB6C1' },
  { name: '橙色高亮', value: '#FFA500' }
]

// 计算属性
const notesCount = computed(() => notes.value.length)
const annotationsCount = computed(() => annotations.value.length)
const importantCount = computed(() => 
  annotations.value.filter(a => a.importance >= 4).length
)

const filteredNotes = computed(() => {
  if (selectedFilter.value === 'all' || selectedFilter.value === 'notes') {
    return notes.value
  }
  return []
})

const filteredAnnotations = computed(() => {
  if (selectedFilter.value === 'all') {
    return annotations.value
  } else if (selectedFilter.value === 'highlights') {
    return annotations.value.filter(a => a.type === 'highlight')
  } else if (selectedFilter.value === 'bookmarks') {
    return annotations.value.filter(a => a.type === 'bookmark')
  }
  return []
})

// 方法
const loadNotes = async () => {
  try {
    const response = await getResourceNotes(studentId.value, props.resourceId)
    if (response.success) {
      notes.value = response.data
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
  }
}

const loadAnnotations = async () => {
  try {
    const response = await getResourceAnnotations(studentId.value, props.resourceId)
    if (response.success) {
      annotations.value = response.data
    }
  } catch (error) {
    console.error('加载标注失败:', error)
  }
}

const applyFilter = () => {
  // 过滤逻辑已在计算属性中实现
}

const editNote = (note) => {
  editingNote.value = note
  noteForm.title = note.title
  noteForm.content = note.content
  noteForm.pageNumber = note.pageNumber
  noteForm.color = note.color
  noteForm.tags = note.tags || ''
  noteForm.isPublic = note.isPublic
  showNoteModal.value = true
}

const closeNoteModal = () => {
  showNoteModal.value = false
  editingNote.value = null
  resetNoteForm()
}

const resetNoteForm = () => {
  noteForm.title = ''
  noteForm.content = ''
  noteForm.pageNumber = 1
  noteForm.color = '#FFD700'
  noteForm.tags = ''
  noteForm.isPublic = false
}

const saveNote = async () => {
  try {
    const noteData = {
      studentId: parseInt(studentId.value),
      resourceId: props.resourceId,
      title: noteForm.title,
      content: noteForm.content,
      pageNumber: noteForm.pageNumber,
      color: noteForm.color,
      tags: noteForm.tags,
      isPublic: noteForm.isPublic
    }

    if (editingNote.value) {
      await updateNote(editingNote.value.id, noteData)
      ElMessage.success('笔记更新成功')
    } else {
      await createNote(noteData)
      ElMessage.success('笔记创建成功')
    }

    closeNoteModal()
    loadNotes()
  } catch (error) {
    console.error('保存笔记失败:', error)
    ElMessage.error('保存笔记失败')
  }
}

const deleteNote = async (noteId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '确认删除', {
      type: 'warning'
    })
    
    await deleteNoteApi(noteId)
    ElMessage.success('笔记删除成功')
    loadNotes()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除笔记失败:', error)
      ElMessage.error('删除笔记失败')
    }
  }
}

const editAnnotation = (annotation) => {
  editingAnnotation.value = annotation
  annotationForm.type = annotation.type
  annotationForm.importance = annotation.importance
  annotationForm.color = annotation.color
  annotationForm.comment = annotation.comment || ''
  showAnnotationModal.value = true
}

const closeAnnotationModal = () => {
  showAnnotationModal.value = false
  editingAnnotation.value = null
}

const saveAnnotation = async () => {
  try {
    const annotationData = {
      type: annotationForm.type,
      importance: annotationForm.importance,
      color: annotationForm.color,
      comment: annotationForm.comment
    }

    await updateAnnotation(editingAnnotation.value.id, annotationData)
    ElMessage.success('标注更新成功')
    
    closeAnnotationModal()
    loadAnnotations()
  } catch (error) {
    console.error('保存标注失败:', error)
    ElMessage.error('保存标注失败')
  }
}

const deleteAnnotation = async (annotationId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个标注吗？', '确认删除', {
      type: 'warning'
    })
    
    await deleteAnnotationApi(annotationId)
    ElMessage.success('标注删除成功')
    loadAnnotations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除标注失败:', error)
      ElMessage.error('删除标注失败')
    }
  }
}

const getAnnotationIcon = (type) => {
  const icons = {
    highlight: 'fas fa-highlighter',
    underline: 'fas fa-underline',
    comment: 'fas fa-comment',
    bookmark: 'fas fa-bookmark'
  }
  return icons[type] || 'fas fa-marker'
}

const getAnnotationTypeName = (type) => {
  const names = {
    highlight: '高亮',
    underline: '下划线',
    comment: '批注',
    bookmark: '书签'
  }
  return names[type] || type
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 监听资源ID变化
watch(() => props.resourceId, () => {
  if (props.resourceId) {
    loadNotes()
    loadAnnotations()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  if (props.resourceId) {
    loadNotes()
    loadAnnotations()
  }
})
</script>

<style scoped>
.study-notes-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.toolbar-left h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.toolbar-left h2 i {
  color: #3498db;
  margin-right: 10px;
}

.resource-title {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-left: 10px;
}

.toolbar-right {
  display: flex;
  gap: 15px;
  align-items: center;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
}

.stats-bar {
  display: flex;
  gap: 20px;
  background: white;
  padding: 15px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
}

.stat-item i {
  color: #3498db;
}

.content-area {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.notes-section,
.annotations-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.notes-section h3,
.annotations-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.notes-section h3 i,
.annotations-section h3 i {
  color: #3498db;
  margin-right: 10px;
}

.empty-state {
  text-align: center;
  color: #95a5a6;
  padding: 40px 20px;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 15px;
  opacity: 0.5;
}

.note-card,
.annotation-card {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  border-left: 4px solid #3498db;
  transition: all 0.3s ease;
}

.note-card:hover,
.annotation-card:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.note-header,
.annotation-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.note-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.note-meta,
.annotation-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 0.85rem;
  color: #7f8c8d;
}

.note-actions,
.annotation-actions {
  display: flex;
  gap: 5px;
}

.btn-icon {
  background: none;
  border: none;
  color: #7f8c8d;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-icon:hover {
  background: #f8f9fa;
  color: #3498db;
}

.note-content {
  color: #34495e;
  line-height: 1.6;
  margin-bottom: 10px;
}

.note-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  background: #ecf0f1;
  color: #2c3e50;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

.annotation-type {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.annotation-text {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 6px;
  font-style: italic;
  color: #34495e;
  margin-bottom: 10px;
}

.annotation-comment {
  color: #7f8c8d;
  margin-bottom: 5px;
}

.annotation-comment i {
  margin-right: 5px;
}

.annotation-date {
  font-size: 0.8rem;
  color: #95a5a6;
}

.importance-stars {
  display: flex;
  gap: 2px;
}

.importance-stars .fa-star {
  color: #f39c12;
}

.importance-stars .far.fa-star {
  color: #bdc3c7;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #7f8c8d;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.color-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.color-btn {
  width: 30px;
  height: 30px;
  border: 3px solid transparent;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.color-btn.active {
  border-color: #2c3e50;
  transform: scale(1.2);
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
}

.importance-selector {
  display: flex;
  gap: 5px;
}

.star-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #bdc3c7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.star-btn.active {
  color: #f39c12;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-area {
    grid-template-columns: 1fr;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .toolbar-right {
    justify-content: space-between;
  }
}
</style> 