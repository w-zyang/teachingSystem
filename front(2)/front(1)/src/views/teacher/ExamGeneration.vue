<template>
  <div class="exam-generation">
    <!-- 🔧 第一步：考核设置 + 题目配置（合并到一个页面） -->
    <div v-if="!showHistoryView && wizardStep === 1" class="step-container step-1">
      <el-row :gutter="24">
        <!-- 左侧：考核设置 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="config-card">
            <template #header>
              <div class="header-left">
                <el-icon><User /></el-icon>
                <span style="font-weight:bold;font-size:18px;">考核设置</span>
              </div>
            </template>
            <el-form label-width="120px" label-position="left">
              <el-form-item label="所属课程" required>
                <el-select v-model="selectedCourseId" @change="onCourseChange" placeholder="请选择课程" style="width: 100%;">
                  <el-option 
                    v-for="course in teacherCourses" 
                    :key="course.id" 
                    :label="course.name" 
                    :value="course.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="考核名称">
                <el-input v-model="examConfig.name" placeholder="请输入考核名称" />
              </el-form-item>
              <el-form-item label="考核类型">
                <el-select v-model="examConfig.type" style="width: 100%;">
                  <el-option label="choice" value="choice" />
                </el-select>
              </el-form-item>
              <el-form-item label="考核时长(分钟)">
                <el-input-number v-model="examConfig.duration" :min="1" :max="300" class="centered-input-number" style="width: 100%;" />
              </el-form-item>
              <el-form-item label="总分">
                <el-input :value="calculatedTotalScore" readonly class="centered-input-with-padding" style="width: 100%;" />
              </el-form-item>
            </el-form>
            
            <!-- 当前选择信息展示 -->
            <div style="margin-top: 16px; padding: 12px; background: #f5f7fa; border-radius: 8px;">
              <div style="font-size: 14px; color: #606266; margin-bottom: 8px;">
                <strong>当前选择：</strong>
              </div>
              <div style="font-size: 13px; color: #909399; line-height: 1.6;">
                <div>📚 课程：{{ selectedCourseId ? teacherCourses.find(c => c.id === selectedCourseId)?.name || '软件工程实践' : '软件工程实践' }}</div>
                <div>📖 科目：{{ selectedCourseId ? teacherCourses.find(c => c.id === selectedCourseId)?.subject || '软件工程' : '软件工程' }}</div>
                <div>📄 章节：{{ selectedChapter ? availableChapters.find(c => c.value === selectedChapter)?.label || '第一章 基础概念' : '第一章 基础概念' }}</div>
                <div>🎯 知识库：{{ selectedKnowledgeBase || '软件工程实践基础' }}</div>
              </div>
            </div>
            
            <!-- 查看历史考核按钮 -->
            <div style="margin-top: 16px;">
              <el-button @click="goToHistoryView" icon="Document" style="width: 100%;">
                📋 查看历史考核
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 右侧：题目配置 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="config-card">
            <template #header>
              <div class="header-left">
                <el-icon><List /></el-icon>
                <span style="font-weight:bold;font-size:18px;">自题目配置</span>
              </div>
            </template>
            <el-row :gutter="12">
              <el-col :xs="24" :sm="12" :md="12" v-for="type in questionTypes" :key="type.key" style="margin-bottom: 12px;">
                <el-card class="type-card" shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-weight:bold; font-size: 16px; margin-bottom: 8px;">{{ type.name }}</div>
                    <div style="color:#888;font-size:13px; margin-bottom: 12px;">每题{{ type.scorePer }}分</div>
                    <div style="color:#888;font-size:13px; margin-bottom: 12px;">难度:{{ type.difficulty }}</div>
                    <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
                      <el-button size="small" @click.stop="type.count = Math.max(0, type.count - 1)" circle>
                        <span style="font-size: 18px; font-weight: bold;">−</span>
                      </el-button>
                      <el-input 
                        v-model.number="type.count" 
                        type="number" 
                        :min="0" 
                        :max="100" 
                        size="small" 
                        class="centered-input"
                        style="width: 80px;" 
                        @input="type.count = Math.max(0, Math.min(100, Number(type.count) || 0))"
                      />
                      <el-button size="small" @click.stop="type.count = Math.min(100, type.count + 1)" circle>
                        <span style="font-size: 18px; font-weight: bold;">+</span>
                      </el-button>
                      <span style="margin-left:4px;">题</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 下一步按钮 -->
            <div style="margin-top: 24px;">
              <el-button type="primary" @click="goToStep(2)" icon="Right" size="large" style="width: 100%;">
                下一步：配置题目
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 🔧 第二步：知识点选择 -->
    <div v-if="!showHistoryView && wizardStep === 2" class="step-container step-2">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="24" :md="16">
          <el-card class="config-card">
            <template #header>
              <div class="header-left">
                <el-icon><Collection /></el-icon>
                <span style="font-weight:bold;font-size:18px;">📚 知识点选择</span>
              </div>
            </template>
            <el-form inline style="margin-bottom: 20px;">
              <el-form-item label="章节选择">
                <el-select v-model="selectedChapter" @change="onChapterChange" style="width: 300px;" :disabled="!selectedCourseId">
                  <el-option v-for="chapter in availableChapters" :key="chapter.value" :label="chapter.label" :value="chapter.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="知识库">
                <el-tag type="success">{{ selectedKnowledgeBase || '请先选择课程和章节' }}</el-tag>
              </el-form-item>
            </el-form>
            
            <!-- 题目总数提示 -->
            <el-alert 
              :title="`请为 ${totalQuestionCount} 道题目分配知识点权重`" 
              type="info" 
              :closable="false"
              style="margin-bottom: 16px;">
              <template #default>
                <div style="font-size: 13px; color: #606266;">
                  根据您在上一步的配置，共需生成 <strong style="color: #667eea;">{{ totalQuestionCount }}</strong> 道题目。
                  请为每个知识点设置权重，AI将按权重比例分配题目数量。<br/>
                  <strong style="color: #e6a23c;">💡 提示：权重为0的知识点将不生成题目</strong>
                </div>
              </template>
            </el-alert>
            
            <el-row :gutter="16">
              <el-col :xs="24" :sm="12" :md="12" v-for="point in knowledgePoints" :key="point.id" style="margin-bottom: 12px;">
                <el-card 
                  class="knowledge-point-card"
                  shadow="hover">
                  <div style="margin-bottom: 12px;">
                    <div style="font-weight:bold; font-size: 15px; margin-bottom: 4px;">{{ point.name }}</div>
                    <div style="color:#888;font-size:13px;">{{ point.description }}</div>
                  </div>
                  <div style="display: flex; justify-content: space-between; align-items: center;" @click.stop>
                    <div style="display: flex; align-items: center; gap: 8px;">
                      <span style="font-size: 13px; color: #606266;">权重：</span>
                      <el-input-number v-model="point.weight" :min="0" :max="100" size="small" style="width: 120px;" />
                    </div>
                    <div style="font-size: 13px; color: #667eea; font-weight: 600;">
                      约 {{ calculateQuestionsByWeight(point.weight) }} 题
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="8">
          <el-card class="config-card">
            <template #header>
              <div class="header-left">
                <el-icon><MagicStick /></el-icon>
                <span style="font-weight:bold;font-size:18px;">✨ 智能生成考核</span>
              </div>
            </template>
            <div style="margin: 24px 0; text-align: center;">
              <el-button type="primary" size="large" :loading="generating" @click="generateExamHandler" style="width: 100%; font-size: 18px; padding: 16px;">
                <el-icon><MagicStick /></el-icon>
                <span v-if="generating">生成中...</span>
                <span v-else>智能生成考核</span>
              </el-button>
              <div style="margin-top: 16px; color: #888; font-size: 13px; line-height: 1.6;">
                AI将根据知识点权重和题目配置自动生成考核内容
              </div>
              
              <!-- 返回上一步按钮 -->
              <el-button @click="goToStep(1)" icon="Back" style="width: 100%; margin-top: 12px;">
                返回上一步
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 🔧 历史考核视图页面 -->
    <div v-if="showHistoryView" class="history-view-container">
      <div class="history-view-header">
        <el-button @click="backToExamGeneration" icon="Back" type="primary">
          返回考核生成
        </el-button>
        <div class="header-title">
          <img src="@/assets/time.png" alt="历史" class="history-icon">
          <h2>📋 历史考核管理</h2>
        </div>
        <div class="history-count">
          <el-tag type="info" size="large">{{ examList.length }} 个考试</el-tag>
        </div>
      </div>
      
      <div v-if="examList.length > 0" class="history-exams">
        <!-- 课程分类标签页 -->
        <div class="course-tabs">
        <el-tabs v-model="activeCourseTab" @tab-click="handleCourseTabClick">
          <el-tab-pane label="全部课程" name="all">
            <div class="exams-grid">
              <div v-for="exam in examList" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/tag.png" alt="课程" class="info-icon">
                    <span>{{ getCourseName(exam.courseId) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="数据结构" name="1">
            <div v-if="getExamsByCourse(1).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(1)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无数据结构课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="Linux系统" name="2">
            <div v-if="getExamsByCourse(2).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(2)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无Linux系统课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="计算机网络" name="3">
            <div v-if="getExamsByCourse(3).length > 0" class="exams-grid">
              <div v-for="exam in getExamsByCourse(3)" :key="exam.id" class="exam-item">
                <div class="exam-item-header">
                  <h4>{{ exam.name }}</h4>
                  <div class="exam-status">
                    <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
                  </div>
                </div>
                
                <div class="exam-item-info">
                  <div class="info-row">
                    <img src="@/assets/time.png" alt="时长" class="info-icon">
                    <span>{{ exam.duration }}分钟</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/balance.png" alt="总分" class="info-icon">
                    <span>{{ exam.totalScore }}分</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/category.png" alt="类型" class="info-icon">
                    <span>{{ getExamTypeName(exam.type) }}</span>
                  </div>
                  <div class="info-row">
                    <img src="@/assets/author.png" alt="创建时间" class="info-icon">
                    <span>{{ formatDate(exam.createTime) }}</span>
                  </div>
                </div>
                
                <div class="exam-item-actions">
                  <el-button size="small" @click="viewExamDetails(exam)">查看详情</el-button>
                  <el-button size="small" type="primary" @click="editExam(exam)" v-if="exam.status === 'draft'">编辑</el-button>
                  <el-button size="small" type="success" @click="publishExistingExam(exam)" v-if="exam.status === 'draft'">发布</el-button>
                  <el-button size="small" type="warning" @click="archiveExistingExam(exam)" v-if="exam.status === 'published'">归档</el-button>
                  <el-button size="small" type="danger" @click="deleteExistingExam(exam)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <img src="@/assets/category.png" alt="空状态" class="empty-icon">
              <div class="empty-text">暂无计算机网络课程的考核</div>
              <div class="empty-desc">点击上方"生成考核"按钮创建新的考核</div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      </div>
      
      <div v-else class="empty-state" style="text-align: center; padding: 40px;">
        <el-empty description="暂无历史考核">
          <el-button type="primary" @click="backToExamGeneration">返回考核生成</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 生成的考核内容 -->
    <!-- 🔧 第三步：题目预览与管理 -->
 
      
        
        <el-col :xs="24" :sm="24" :md="24">
          <el-card class="config-card" v-if="currentQuestion">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div class="header-left">
                  <el-icon><Document /></el-icon>
                  <span style="font-weight:bold;font-size:18px;">题目 {{ currentQuestionIndex + 1 }} / {{ generatedExam.questionCount }}</span>
                </div>
                <div>
                  <el-tag :type="getQuestionTypeColor(currentQuestion.type)">
                    {{ getQuestionTypeName(currentQuestion.type) }}
                  </el-tag>
                  <el-tag type="warning" style="margin-left: 8px;">{{ currentQuestion.score }}分</el-tag>
                </div>
              </div>
            </template>
            
            <!-- 考试信息卡片 -->
            <div class="exam-info-card">
              <div class="exam-info-header">
                <el-icon class="exam-info-icon"><Document /></el-icon>
                <span class="exam-info-title">{{ generatedExam.name || '题目列表' }}</span>
              </div>
              <div class="exam-info-details">
                <div class="exam-info-item">
                  <el-icon><Timer /></el-icon>
                  <span>考试时长：{{ generatedExam.duration }}分钟</span>
                </div>
                <div class="exam-info-item">
                  <el-icon><Document /></el-icon>
                  <span>题目数量：{{ generatedExam.questionCount }}道</span>
                </div>
                <div class="exam-info-item">
                  <el-icon><TrophyBase /></el-icon>
                  <span>总分：{{ generatedExam.totalScore }}分</span>
                </div>
              </div>
            </div>
            
            <!-- 题目内容 -->
            <div class="question-content-display">
              <div class="question-text-display">
                <strong style="font-size: 16px; color: #667eea;">{{ currentQuestionIndex + 1 }}.</strong>
                <span style="font-size: 16px; line-height: 1.8; margin-left: 8px;">
                  {{ currentQuestion.content || currentQuestion.title || '题目内容' }}
                </span>
              </div>
              
              <!-- 选择题/多选题选项 -->
              <div v-if="currentQuestion.type === 'choice' || currentQuestion.type === 'multiple'" 
                   class="options-display" style="margin-top: 20px;">
                <div v-for="option in currentQuestion.options" :key="option.key" 
                     class="option-item-display">
                  <span class="option-key-display">{{ option.key }}.</span>
                  <span class="option-content-display">{{ option.content }}</span>
                </div>
              </div>
              
              <!-- 判断题 -->
              <div v-else-if="currentQuestion.type === 'true_false'" 
                   class="judge-display" style="margin-top: 20px;">
                <div class="judge-options">
                  <div class="judge-option">✓ 正确</div>
                  <div class="judge-option">✗ 错误</div>
                </div>
              </div>
              
              <!-- 填空题 -->
              <div v-else-if="currentQuestion.type === 'fill'" 
                   class="fill-display" style="margin-top: 20px;">
                <div class="fill-placeholder">
                  <el-input placeholder="学生答题区域" disabled style="width: 100%;"></el-input>
                </div>
              </div>
              
              <!-- 简答题 -->
              <div v-else-if="currentQuestion.type === 'short'" 
                   class="short-display" style="margin-top: 20px;">
                <el-input 
                  type="textarea" 
                  :rows="6" 
                  placeholder="学生答题区域" 
                  disabled
                  style="width: 100%;">
                </el-input>
              </div>
              
              <!-- 编程题 -->
              <div v-else-if="currentQuestion.type === 'coding'" 
                   class="coding-display" style="margin-top: 20px;">
                
                <!-- 编程要求 -->
                <div class="coding-section" v-if="currentQuestion.requirements">
                  <div class="coding-section-header">
                    <span class="coding-icon">📝</span>
                    <span class="coding-section-title">编程要求</span>
                  </div>
                  <div class="coding-section-content">
                    {{ currentQuestion.requirements }}
                  </div>
                </div>
                
                <!-- 输入格式 -->
                <div class="coding-section" v-if="currentQuestion.inputFormat">
                  <div class="coding-section-header">
                    <span class="coding-icon">🔽</span>
                    <span class="coding-section-title">输入格式</span>
                  </div>
                  <div class="coding-section-content">
                    {{ currentQuestion.inputFormat }}
                  </div>
                </div>
                
                <!-- 输出格式 -->
                <div class="coding-section" v-if="currentQuestion.outputFormat">
                  <div class="coding-section-header">
                    <span class="coding-icon">🔼</span>
                    <span class="coding-section-title">输出格式</span>
                  </div>
                  <div class="coding-section-content">
                    {{ currentQuestion.outputFormat }}
                  </div>
                </div>
                
                <!-- 示例 -->
                <div class="coding-section" v-if="currentQuestion.examples && currentQuestion.examples.length > 0">
                  <div class="coding-section-header">
                    <span class="coding-icon">💡</span>
                    <span class="coding-section-title">示例</span>
                  </div>
                  <div v-for="(example, idx) in currentQuestion.examples" :key="idx" class="coding-example">
                    <div class="example-table">
                      <div class="example-row">
                        <div class="example-cell label-cell">输入：</div>
                        <div class="example-cell content-cell">
                          <pre class="example-code">{{ example.input }}</pre>
                        </div>
                      </div>
                      <div class="example-row">
                        <div class="example-cell label-cell">输出：</div>
                        <div class="example-cell content-cell">
                          <pre class="example-code">{{ example.output }}</pre>
                        </div>
                      </div>
                      <div v-if="example.explanation" class="example-row">
                        <div class="example-cell label-cell">说明：</div>
                        <div class="example-cell content-cell">{{ example.explanation }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 提示 -->
                <div class="coding-section" v-if="currentQuestion.hints">
                  <div class="coding-section-header">
                    <span class="coding-icon">💡</span>
                    <span class="coding-section-title">提示</span>
                  </div>
                  <div class="coding-section-content">
                    {{ currentQuestion.hints }}
                  </div>
                </div>
                
                <!-- 代码编辑器 -->
                <div class="coding-section">
                  <div class="coding-section-header">
                    <span class="coding-icon">💻</span>
                    <span class="coding-section-title">代码编辑器：</span>
                  </div>
                  <el-input 
                    type="textarea" 
                    :rows="12" 
                    placeholder="请在此处编写代码..."
                    disabled
                    style="width: 100%; font-family: 'Courier New', monospace; font-size: 14px;">
                  </el-input>
                  <div style="margin-top: 8px; color: #909399; font-size: 13px;">
                    学生需要在代码编辑器中完成编程任务
                  </div>
                </div>
              </div>
              
              <!-- 参考答案 -->
              <div v-if="showAnswers" class="answer-section" style="margin-top: 24px;">
                <el-divider content-position="left">
                  <el-icon><Check /></el-icon>
                  <span style="margin-left: 8px;">参考答案</span>
                </el-divider>
                <div class="answer-content">
                  <el-tag type="success" size="large">{{ currentQuestion.answer }}</el-tag>
                </div>
                <div v-if="currentQuestion.explanation" style="margin-top: 12px;">
                  <el-divider content-position="left">
                    <el-icon><InfoFilled /></el-icon>
                    <span style="margin-left: 8px;">解析</span>
                  </el-divider>
                  <div class="explanation-content">
                    {{ currentQuestion.explanation }}
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 题目操作按钮 -->
            <div style="margin-top: 24px; display: flex; gap: 12px; justify-content: space-between;">
              <div style="display: flex; gap: 12px;">
                <el-button 
                  @click="currentQuestionIndex = Math.max(0, currentQuestionIndex - 1)" 
                  :disabled="currentQuestionIndex === 0"
                  icon="ArrowLeft">
                  上一题
                </el-button>
                <el-button 
                  @click="currentQuestionIndex = Math.min(generatedExam.questionCount - 1, currentQuestionIndex + 1)" 
                  :disabled="currentQuestionIndex === generatedExam.questionCount - 1"
                  icon="ArrowRight">
                  下一题
                </el-button>
              </div>
              <el-button 
                @click="showAnswers = !showAnswers" 
                :type="showAnswers ? 'warning' : 'primary'">
                {{ showAnswers ? '隐藏答案' : '显示答案' }}
              </el-button>
            </div>
          </el-card>
          
          <!-- 操作按钮卡片 -->
          <el-card class="config-card" style="margin-top: 20px;" v-if="generatedExam && generatedExam.questions && generatedExam.questions.length > 0">
            <template #header>
              <div class="header-left">
                <el-icon><Operation /></el-icon>
                <span style="font-weight:bold;font-size:18px;">⚙️ 考试操作</span>
              </div>
            </template>
            
            <div style="display: flex; flex-wrap: wrap; gap: 12px;">
              <el-button @click="exitExamPreview" icon="Back" size="large">
                返回首页
              </el-button>
              <el-button @click="regenerateExam" type="warning" icon="Refresh" size="large">
                重新生成
              </el-button>
              <el-button @click="saveExam" type="success" icon="Document" size="large">
                保存考核
              </el-button>
              <el-button @click="publishExamHandler" type="danger" icon="Upload" size="large">
                发布到学生端
              </el-button>
            </div>
          </el-card>
        </el-col>

    </div>

    <!-- AI生成进度条 -->
    <AIGenerationProgress
      :visible="showProgress"
      title="AI 正在生成考试题目"
      :progress="progressValue"
      :current-step="currentStep"
      :steps="progressSteps"
      :message="progressMessage"
      tip="💡 提示：生成时间取决于题目数量和复杂度，通常需要1-3分钟"
    />

    <!-- 预览弹窗 -->
    <div v-if="showPreview" class="modal-overlay" @click="closePreview">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <div class="header-left">
            <img src="@/assets/search.png" alt="预览" class="modal-icon">
            <h3>考核预览</h3>
          </div>
          <button @click="closePreview" class="close-btn">
            <img src="@/assets/delete.png" alt="关闭" class="close-icon">
          </button>
        </div>
        <div class="modal-body">
          <div class="exam-preview">
            <div class="preview-header">
              <h2>{{ generatedExam.name }}</h2>
              <div class="preview-info">
                <div class="info-row">
                  <img src="@/assets/time.png" alt="时间" class="info-icon">
                  <p>考试时间：{{ generatedExam.duration }}分钟</p>
                </div>
                <div class="info-row">
                  <img src="@/assets/balance.png" alt="总分" class="info-icon">
                  <p>总分：{{ generatedExam.totalScore }}分</p>
                </div>
                <div class="info-row">
                  <img src="@/assets/author.png" alt="注意" class="info-icon">
                  <p>注意事项：请独立完成，不得抄袭</p>
                </div>
              </div>
            </div>
            <div class="preview-questions">
              <div v-for="(question, index) in generatedExam.questions" :key="index" class="preview-question">
                <div class="preview-question-header">
                  <span class="preview-number">{{ index + 1 }}.</span>
                  <span class="preview-type">{{ getQuestionTypeName(question.type) }}</span>
                  <span class="preview-score">({{ question.score }}分)</span>
                </div>
                
                <!-- 题目内容 -->
                <div class="preview-content" v-html="formatQuestionContent(question.content || question.title || '题目内容缺失')"></div>
                
                <!-- 选择题选项 -->
                <div v-if="(question.type === 'multiple_choice' || question.type === 'choice') && question.options" class="preview-options">
                  <div v-for="option in question.options" :key="option.key" class="preview-option">
                    <span class="preview-option-key">{{ option.key }}.</span>
                    <span class="preview-option-content">{{ option.content }}</span>
                  </div>
                </div>
                
                <!-- 判断题答题区域 -->
                <div v-else-if="question.type === 'true_false' || question.type === 'judge'" class="preview-judge-area">
                  <div class="judge-hint">✔️ 答题区域（判断题：正确/错误）</div>
                  <div class="answer-line"></div>
                </div>
                
                <!-- 填空题答题区域 -->
                <div v-else-if="question.type === 'fill_in_the_blank' || question.type === 'fill'" class="preview-fill-area">
                  <div class="fill-hint">📝 答题区域（填空题）</div>
                  <div class="answer-line"></div>
                </div>
                
                <!-- 简答题答题区域 -->
                <div v-else-if="question.type === 'short_answer' || question.type === 'short'" class="preview-short-area">
                  <div class="short-hint">✍️ 答题区域（简答题）</div>
                  <div class="answer-box"></div>
                </div>
                
                <!-- 编程题/命令题答题区域 -->
                <div v-else-if="question.type === 'command' || question.type === 'coding' || question.type === 'programming'" class="preview-coding-area">
                  <div class="coding-hint">💻 答题区域（{{ question.type === 'command' ? '命令题' : '编程题' }}）</div>
                  <div class="code-box"></div>
                </div>
                
                <!-- 通用答题区域 -->
                <div v-else class="preview-answer-space">
                  <div class="answer-line"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { getExamsByTeacherId, getExamById, createExam, updateExam, deleteExam, publishExam, archiveExam } from '@/api/exam'
import { getCoursesByTeacherId } from '@/api/course'
import { aiAPI } from '@/api/ai'
import { ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElInputNumber, ElButton, ElTag, ElRow, ElCol, ElIcon, ElMessage, ElMessageBox } from 'element-plus'
import { User, List, Collection, MagicStick, Document, Check, InfoFilled, Operation, ArrowLeft, ArrowRight, Back, Refresh, Upload, Timer, TrophyBase } from '@element-plus/icons-vue'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'
import { convertQuestionsData, debugQuestion } from '@/utils/questionConverter'

// 获取当前教师ID，假设是teacher1(ID=2)
const teacherId = localStorage.getItem('userId') || '2'

// 响应式变量
const examConfig = ref({
  name: '',
  type: 'choice',
  duration: 60,
  totalScore: 100
})

const questionTypes = ref([
  { key: 'choice', name: '单选题', count: 0, scorePer: 2, difficulty: '简单', iconSrc: '/src/assets/author.png' },
  { key: 'multiple', name: '多选题', count: 0, scorePer: 4, difficulty: '中等', iconSrc: '/src/assets/category.png' },
  { key: 'true_false', name: '判断题', count: 0, scorePer: 2, difficulty: '简单', iconSrc: '/src/assets/author.png' },  // ✅ 新增判断题
  { key: 'fill', name: '填空题', count: 0, scorePer: 3, difficulty: '中等', iconSrc: '/src/assets/balance.png' },
  { key: 'essay', name: '简答题', count: 0, scorePer: 10, difficulty: '困难', iconSrc: '/src/assets/time.png' },
  { key: 'programming', name: '编程题', count: 0, scorePer: 20, difficulty: '困难', iconSrc: '/src/assets/code.png' }
])

const knowledgePoints = ref([])
const generatedExam = ref(null)
const generating = ref(false)
const showPreview = ref(false)
const examList = ref([])

// 🔧 缓存管理
const EXAM_CACHE_KEY = 'temp_generated_exam'
const CACHE_DURATION = 30 * 60 * 1000 // 30分钟

// 保存考试到缓存
const saveExamToCache = () => {
  if (generatedExam.value) {
    const cacheData = {
      exam: generatedExam.value,
      timestamp: Date.now()
    }
    sessionStorage.setItem(EXAM_CACHE_KEY, JSON.stringify(cacheData))
    console.log('✅ 考试已保存到缓存')
  }
}

// 从缓存加载考试
const loadExamFromCache = () => {
  try {
    const cached = sessionStorage.getItem(EXAM_CACHE_KEY)
    if (cached) {
      const cacheData = JSON.parse(cached)
      const now = Date.now()
      const elapsed = now - cacheData.timestamp
      
      if (elapsed < CACHE_DURATION) {
        generatedExam.value = cacheData.exam
        wizardStep.value = 3
      
      // 🔧 保存到缓存
      saveExamToCache()
        console.log('✅ 从缓存加载考试成功')
        return true
      } else {
        clearExamCache()
        console.log('⏰ 缓存已过期')
      }
    }
  } catch (error) {
    console.error('❌ 加载缓存失败:', error)
  }
  return false
}

// 清除考试缓存
const clearExamCache = () => {
  sessionStorage.removeItem(EXAM_CACHE_KEY)
  console.log('🗑️ 缓存已清除')
}

// 退出题目预览
const exitExamPreview = () => {
  ElMessageBox.confirm(
    '退出后将清除当前生成的题目，是否确认退出？',
    '确认退出',
    {
      confirmButtonText: '确认退出',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    clearExamCache()
    generatedExam.value = null
    wizardStep.value = 1
    ElMessage.success('已退出题目预览')
  }).catch(() => {})
}

// 🔧 新增：递进式流程状态管理
const wizardStep = ref(1)  // 当前向导步骤（1: 考核设置, 2: 题目配置, 3: 题目展示）
const currentQuestionIndex = ref(0)  // 当前显示的题目索引（翻页用）
const showHistoryDialog = ref(false)  // 是否显示历史考核弹窗（已废弃）
const showHistoryView = ref(false)  // 是否显示历史考核视图页面
const showStep2Dialog = ref(false)  // 是否显示第二步弹窗

// 🔧 新增：计算题目总数
const totalQuestionCount = computed(() => {
  return questionTypes.value.reduce((sum, type) => sum + type.count, 0)
})

// 🔧 新增：计算总分（根据题型配置自动计算）
const calculatedTotalScore = computed(() => {
  return questionTypes.value.reduce((sum, type) => sum + (type.count * type.scorePer), 0)
})

// 🔧 监听计算出的总分，自动同步到 examConfig
watch(calculatedTotalScore, (newScore) => {
  examConfig.value.totalScore = newScore
})

// 🔧 新增：计算总权重
const totalWeight = computed(() => {
  return knowledgePoints.value.reduce((sum, point) => sum + (point.weight || 0), 0)
})

// 🔧 新增：根据权重计算题目数量
const calculateQuestionsByWeight = (weight) => {
  if (totalWeight.value === 0 || totalQuestionCount.value === 0) {
    return 0
  }
  return Math.round((weight / totalWeight.value) * totalQuestionCount.value)
}

// 🔧 新增：切换到历史考核视图
const goToHistoryView = () => {
  showHistoryView.value = true
}

// 🔧 新增：返回考核生成界面
const backToExamGeneration = () => {
  showHistoryView.value = false
}

// 🔧 新增：切换步骤
const goToStep = (step) => {
  wizardStep.value = step
}

// 进度条相关
const showProgress = ref(false)
const progressValue = ref(0)
const currentStep = ref(0)
const progressMessage = ref('')
const progressSteps = ref([
  { title: '准备数据', desc: '正在准备考试生成所需的数据...' },
  { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
  { title: '生成题目', desc: 'AI正在生成题目，请耐心等待...' },
  { title: '完成', desc: '题目生成完成！' }
])
const teacherCourses = ref([])
const selectedCourseId = ref(null)
const activeCourseTab = ref('all')
const showAnswers = ref(false)  // 🔧 新增：控制答案显示/隐藏

// 新增：科目-章节-知识库数据结构（完整的五门课程）
const subjectChapterMap = {
  '数据结构': [
    { value: 'ch01', label: '第一章 线性表', knowledgeBase: '数据结构基础' },
    { value: 'ch02', label: '第二章 栈和队列', knowledgeBase: '栈队列原理' },
    { value: 'ch03', label: '第三章 树和二叉树', knowledgeBase: '树结构应用' },
    { value: 'ch04', label: '第四章 图', knowledgeBase: '图算法练习' },
    { value: 'ch05', label: '第五章 查找', knowledgeBase: '查找算法' },
    { value: 'ch06', label: '第六章 排序', knowledgeBase: '排序算法' }
  ],
  'Java程序设计': [
    { value: 'ch01', label: '第一章 Java基础', knowledgeBase: 'Java基础语法' },
    { value: 'ch02', label: '第二章 面向对象', knowledgeBase: 'Java面向对象' },
    { value: 'ch03', label: '第三章 异常处理', knowledgeBase: 'Java异常' },
    { value: 'ch04', label: '第四章 集合框架', knowledgeBase: 'Java集合' },
    { value: 'ch05', label: '第五章 IO流', knowledgeBase: 'JavaIO' },
    { value: 'ch06', label: '第六章 多线程', knowledgeBase: 'Java并发' }
  ],
  'Linux系统': [
    { value: 'ch01', label: '第一章 Linux基础', knowledgeBase: 'Linux基础' },
    { value: 'ch02', label: '第二章 系统管理', knowledgeBase: 'Linux系统管理' },
    { value: 'ch03', label: '第三章 网络配置', knowledgeBase: 'Linux网络' },
    { value: 'ch04', label: '第四章 安全管理', knowledgeBase: 'Linux安全' },
    { value: 'ch05', label: '第五章 Shell编程', knowledgeBase: 'Shell脚本' },
    { value: 'ch06', label: '第六章 系统编程', knowledgeBase: 'Linux编程' }
  ],
  '计算机网络': [
    { value: 'ch01', label: '第一章 网络基础', knowledgeBase: '网络基础概念' },
    { value: 'ch02', label: '第二章 物理层', knowledgeBase: '物理层原理' },
    { value: 'ch03', label: '第三章 数据链路层', knowledgeBase: '数据链路应用' },
    { value: 'ch04', label: '第四章 网络层', knowledgeBase: '网络层练习' },
    { value: 'ch05', label: '第五章 传输层', knowledgeBase: '传输层协议' },
    { value: 'ch06', label: '第六章 应用层', knowledgeBase: '应用层协议' }
  ],
  '机器学习': [
    { value: 'ch01', label: '第一章 机器学习基础', knowledgeBase: '机器学习概论' },
    { value: 'ch02', label: '第二章 监督学习', knowledgeBase: '监督学习算法' },
    { value: 'ch03', label: '第三章 无监督学习', knowledgeBase: '无监督学习' },
    { value: 'ch04', label: '第四章 神经网络', knowledgeBase: '神经网络基础' },
    { value: 'ch05', label: '第五章 深度学习', knowledgeBase: '深度学习应用' },
    { value: 'ch06', label: '第六章 模型优化', knowledgeBase: '模型调优' }
  ],
  '操作系统': [
    { value: 'ch01', label: '第一章 操作系统概述', knowledgeBase: '操作系统基础' },
    { value: 'ch02', label: '第二章 进程管理', knowledgeBase: '进程与线程' },
    { value: 'ch03', label: '第三章 内存管理', knowledgeBase: '内存管理' },
    { value: 'ch04', label: '第四章 文件系统', knowledgeBase: '文件系统' },
    { value: 'ch05', label: '第五章 I/O管理', knowledgeBase: 'IO系统' },
    { value: 'ch06', label: '第六章 死锁', knowledgeBase: '死锁处理' }
  ]
}

// 章节对应的知识点映射（完整的五门课程）
const chapterKnowledgePointsMap = {
  // 数据结构
  'ch01': [
    { id: 101, name: '顺序表', description: '顺序表的定义、操作与实现', selected: false, weight: 6 },
    { id: 102, name: '链表', description: '单链表、双链表、循环链表', selected: false, weight: 8 },
    { id: 103, name: '线性表应用', description: '线性表的实际应用场景', selected: false, weight: 5 },
    { id: 104, name: '时间复杂度', description: '算法时间复杂度分析', selected: false, weight: 7 }
  ],
  'ch02': [
    { id: 201, name: '栈的基本操作', description: '栈的定义、入栈、出栈操作', selected: false, weight: 7 },
    { id: 202, name: '队列的基本操作', description: '队列的定义、入队、出队操作', selected: false, weight: 7 },
    { id: 203, name: '栈的应用', description: '表达式求值、括号匹配等', selected: false, weight: 8 },
    { id: 204, name: '队列的应用', description: '循环队列、优先队列等', selected: false, weight: 6 }
  ],
  'ch03': [
    { id: 301, name: '二叉树遍历', description: '前序、中序、后序、层序遍历', selected: false, weight: 9 },
    { id: 302, name: '二叉搜索树', description: 'BST的插入、删除、查找', selected: false, weight: 8 },
    { id: 303, name: '平衡二叉树', description: 'AVL树、红黑树原理', selected: false, weight: 7 },
    { id: 304, name: '树的应用', description: '哈夫曼树、表达式树等', selected: false, weight: 6 }
  ],
  'ch04': [
    { id: 401, name: '图的存储', description: '邻接矩阵、邻接表表示', selected: false, weight: 7 },
    { id: 402, name: '图的遍历', description: 'DFS深度优先、BFS广度优先', selected: false, weight: 9 },
    { id: 403, name: '最短路径', description: 'Dijkstra、Floyd算法', selected: false, weight: 8 },
    { id: 404, name: '最小生成树', description: 'Prim、Kruskal算法', selected: false, weight: 7 }
  ],
  'ch05': [
    { id: 501, name: '顺序查找', description: '线性查找算法及优化', selected: false, weight: 5 },
    { id: 502, name: '二分查找', description: '折半查找算法与应用', selected: false, weight: 7 },
    { id: 503, name: '哈希查找', description: '哈希表、冲突处理方法', selected: false, weight: 8 },
    { id: 504, name: '查找性能分析', description: '各种查找算法的比较', selected: false, weight: 6 }
  ],
  'ch06': [
    { id: 601, name: '简单排序', description: '冒泡、选择、插入排序', selected: false, weight: 6 },
    { id: 602, name: '高级排序', description: '快速、归并、堆排序', selected: false, weight: 9 },
    { id: 603, name: '排序算法比较', description: '时间复杂度、稳定性分析', selected: false, weight: 7 },
    { id: 604, name: '外部排序', description: '大数据量排序方法', selected: false, weight: 5 }
  ],
  
  // Java程序设计
  'ch01-java': [
    { id: 5001, name: 'Java语法基础', description: '变量、数据类型、运算符', selected: false, weight: 7 },
    { id: 5002, name: '流程控制', description: 'if、switch、for、while语句', selected: false, weight: 7 },
    { id: 5003, name: '数组', description: '一维数组、多维数组操作', selected: false, weight: 6 },
    { id: 5004, name: '方法定义', description: '方法声明、参数传递、返回值', selected: false, weight: 6 }
  ],
  'ch02-java': [
    { id: 5101, name: '类与对象', description: '类的定义、对象的创建与使用', selected: false, weight: 8 },
    { id: 5102, name: '封装', description: '访问修饰符、getter/setter方法', selected: false, weight: 7 },
    { id: 5103, name: '继承', description: '继承关系、方法重写、super关键字', selected: false, weight: 8 },
    { id: 5104, name: '多态', description: '方法重载、接口、抽象类', selected: false, weight: 9 }
  ],
  'ch03-java': [
    { id: 5201, name: '异常概念', description: '异常类层次结构、异常分类', selected: false, weight: 6 },
    { id: 5202, name: '异常处理', description: 'try-catch-finally语句', selected: false, weight: 8 },
    { id: 5203, name: '自定义异常', description: '创建和使用自定义异常类', selected: false, weight: 7 },
    { id: 5204, name: '异常链', description: '异常传播、异常链追踪', selected: false, weight: 6 }
  ],
  'ch04-java': [
    { id: 5301, name: 'List集合', description: 'ArrayList、LinkedList使用', selected: false, weight: 8 },
    { id: 5302, name: 'Set集合', description: 'HashSet、TreeSet特点', selected: false, weight: 7 },
    { id: 5303, name: 'Map集合', description: 'HashMap、TreeMap操作', selected: false, weight: 8 },
    { id: 5304, name: '集合遍历', description: 'Iterator、foreach、Stream', selected: false, weight: 7 }
  ],
  'ch05-java': [
    { id: 5401, name: '字节流', description: 'InputStream、OutputStream', selected: false, weight: 7 },
    { id: 5402, name: '字符流', description: 'Reader、Writer使用', selected: false, weight: 7 },
    { id: 5403, name: '文件操作', description: 'File类、文件读写操作', selected: false, weight: 8 },
    { id: 5404, name: '序列化', description: '对象序列化与反序列化', selected: false, weight: 6 }
  ],
  'ch06-java': [
    { id: 5501, name: '线程创建', description: 'Thread类、Runnable接口', selected: false, weight: 8 },
    { id: 5502, name: '线程同步', description: 'synchronized、Lock锁', selected: false, weight: 9 },
    { id: 5503, name: '线程通信', description: 'wait、notify、notifyAll', selected: false, weight: 8 },
    { id: 5504, name: '线程池', description: 'Executor框架、线程池使用', selected: false, weight: 7 }
  ],
  
  // 软件工程实践
  'ch01-software': [
    { id: 6001, name: '软件工程概述', description: '软件工程定义、发展历程', selected: false, weight: 6 },
    { id: 6002, name: '软件生命周期', description: '瀑布模型、迭代模型、敏捷开发', selected: false, weight: 8 },
    { id: 6003, name: '软件过程模型', description: '各种软件开发过程模型', selected: false, weight: 7 },
    { id: 6004, name: '项目管理基础', description: '项目计划、进度管理', selected: false, weight: 7 }
  ],
  'ch02-software': [
    { id: 6101, name: '需求获取', description: '需求调研、用户访谈技术', selected: false, weight: 8 },
    { id: 6102, name: '需求分析方法', description: '结构化分析、面向对象分析', selected: false, weight: 8 },
    { id: 6103, name: '用例建模', description: 'UML用例图、用例描述', selected: false, weight: 7 },
    { id: 6104, name: '需求文档编写', description: '需求规格说明书编写', selected: false, weight: 7 }
  ],
  'ch03-software': [
    { id: 6201, name: '系统架构设计', description: '分层架构、MVC模式', selected: false, weight: 9 },
    { id: 6202, name: '详细设计', description: '类图、时序图设计', selected: false, weight: 8 },
    { id: 6203, name: '设计模式', description: '常用设计模式应用', selected: false, weight: 8 },
    { id: 6204, name: '数据库设计', description: 'ER图、数据库范式', selected: false, weight: 7 }
  ],
  'ch04-software': [
    { id: 6301, name: '编码规范', description: '代码风格、命名规范', selected: false, weight: 7 },
    { id: 6302, name: '版本控制', description: 'Git使用、分支管理', selected: false, weight: 8 },
    { id: 6303, name: '代码审查', description: 'Code Review流程与方法', selected: false, weight: 7 },
    { id: 6304, name: '持续集成', description: 'CI/CD流程与工具', selected: false, weight: 8 }
  ],
  'ch05-software': [
    { id: 6401, name: '测试策略', description: '单元测试、集成测试、系统测试', selected: false, weight: 8 },
    { id: 6402, name: '测试用例设计', description: '黑盒测试、白盒测试方法', selected: false, weight: 8 },
    { id: 6403, name: '自动化测试', description: '测试框架、自动化工具', selected: false, weight: 7 },
    { id: 6404, name: '性能测试', description: '压力测试、负载测试', selected: false, weight: 7 }
  ],
  'ch06-software': [
    { id: 6501, name: '软件部署', description: '部署策略、环境配置', selected: false, weight: 7 },
    { id: 6502, name: '运维监控', description: '日志管理、性能监控', selected: false, weight: 7 },
    { id: 6503, name: '缺陷管理', description: 'Bug跟踪、问题修复流程', selected: false, weight: 8 },
    { id: 6504, name: '软件维护', description: '版本升级、系统优化', selected: false, weight: 7 }
  ],
  
  // Linux系统（使用不同的ID范围）
  'ch01-linux': [
    { id: 1001, name: 'Linux基本命令', description: 'ls、cd、pwd等基础命令', selected: false, weight: 7 },
    { id: 1002, name: '文件操作', description: '文件创建、删除、复制、移动', selected: false, weight: 8 },
    { id: 1003, name: '目录管理', description: '目录结构、权限管理', selected: false, weight: 6 },
    { id: 1004, name: '文本处理', description: 'grep、sed、awk工具', selected: false, weight: 7 }
  ],
  'ch02-linux': [
    { id: 1101, name: '用户管理', description: '用户创建、删除、权限设置', selected: false, weight: 7 },
    { id: 1102, name: '进程管理', description: 'ps、top、kill命令使用', selected: false, weight: 8 },
    { id: 1103, name: '服务管理', description: 'systemctl服务控制', selected: false, weight: 6 },
    { id: 1104, name: '系统监控', description: '系统资源监控与优化', selected: false, weight: 7 }
  ],
  'ch03-linux': [
    { id: 1201, name: '网络配置', description: 'IP地址、网关、DNS配置', selected: false, weight: 8 },
    { id: 1202, name: '网络工具', description: 'ping、netstat、ifconfig', selected: false, weight: 7 },
    { id: 1203, name: '防火墙配置', description: 'iptables、firewalld使用', selected: false, weight: 6 },
    { id: 1204, name: '远程连接', description: 'SSH、SCP、SFTP使用', selected: false, weight: 7 }
  ],
  'ch04-linux': [
    { id: 1301, name: '用户权限', description: '文件权限、ACL访问控制', selected: false, weight: 8 },
    { id: 1302, name: 'SELinux', description: 'SELinux安全策略', selected: false, weight: 6 },
    { id: 1303, name: '系统加固', description: '系统安全加固措施', selected: false, weight: 7 },
    { id: 1304, name: '日志审计', description: '系统日志分析与审计', selected: false, weight: 7 }
  ],
  'ch05-linux': [
    { id: 1401, name: 'Shell基础', description: 'Shell脚本基本语法', selected: false, weight: 7 },
    { id: 1402, name: '变量与运算', description: '变量定义、算术运算', selected: false, weight: 6 },
    { id: 1403, name: '流程控制', description: 'if、for、while、case语句', selected: false, weight: 8 },
    { id: 1404, name: '函数与模块', description: '函数定义、脚本模块化', selected: false, weight: 7 }
  ],
  'ch06-linux': [
    { id: 1501, name: '文件I/O', description: '文件读写、系统调用', selected: false, weight: 8 },
    { id: 1502, name: '进程控制', description: 'fork、exec、wait函数', selected: false, weight: 9 },
    { id: 1503, name: '进程通信', description: '管道、消息队列、共享内存', selected: false, weight: 8 },
    { id: 1504, name: '信号处理', description: '信号机制与信号处理', selected: false, weight: 7 }
  ],
  
  // 计算机网络
  'ch01-network': [
    { id: 2001, name: '网络体系结构', description: 'OSI七层模型、TCP/IP模型', selected: false, weight: 8 },
    { id: 2002, name: '网络性能指标', description: '带宽、时延、吞吐量', selected: false, weight: 6 },
    { id: 2003, name: '网络分类', description: 'LAN、WAN、MAN分类', selected: false, weight: 5 },
    { id: 2004, name: '网络拓扑', description: '总线、星型、环型拓扑', selected: false, weight: 6 }
  ],
  'ch02-network': [
    { id: 2101, name: '物理层功能', description: '数据编码、调制解调', selected: false, weight: 6 },
    { id: 2102, name: '传输介质', description: '双绞线、光纤、无线', selected: false, weight: 5 },
    { id: 2103, name: '信道复用', description: 'FDM、TDM、WDM技术', selected: false, weight: 7 },
    { id: 2104, name: '物理层设备', description: '中继器、集线器原理', selected: false, weight: 5 }
  ],
  'ch03-network': [
    { id: 2201, name: '数据链路层功能', description: '成帧、差错控制、流量控制', selected: false, weight: 8 },
    { id: 2202, name: 'MAC协议', description: 'CSMA/CD、CSMA/CA协议', selected: false, weight: 7 },
    { id: 2203, name: '以太网', description: '以太网帧格式、交换机', selected: false, weight: 8 },
    { id: 2204, name: 'VLAN技术', description: '虚拟局域网原理与配置', selected: false, weight: 6 }
  ],
  'ch04-network': [
    { id: 2301, name: 'IP协议', description: 'IPv4、IPv6地址与分组', selected: false, weight: 9 },
    { id: 2302, name: '路由算法', description: '距离向量、链路状态算法', selected: false, weight: 8 },
    { id: 2303, name: 'ICMP协议', description: 'ping、traceroute原理', selected: false, weight: 6 },
    { id: 2304, name: 'NAT与DHCP', description: '地址转换、动态分配', selected: false, weight: 7 }
  ],
  'ch05-network': [
    { id: 2401, name: 'TCP协议', description: 'TCP连接管理、流量控制', selected: false, weight: 9 },
    { id: 2402, name: 'UDP协议', description: 'UDP特点与应用场景', selected: false, weight: 7 },
    { id: 2403, name: '拥塞控制', description: 'TCP拥塞控制算法', selected: false, weight: 8 },
    { id: 2404, name: '可靠传输', description: '确认重传、滑动窗口', selected: false, weight: 8 }
  ],
  'ch06-network': [
    { id: 2501, name: 'HTTP协议', description: 'HTTP请求响应、状态码', selected: false, weight: 8 },
    { id: 2502, name: 'DNS协议', description: '域名解析原理与过程', selected: false, weight: 7 },
    { id: 2503, name: 'FTP与SMTP', description: '文件传输、邮件协议', selected: false, weight: 6 },
    { id: 2504, name: 'Socket编程', description: '网络编程接口与应用', selected: false, weight: 9 }
  ],
  
  // 机器学习
  'ch01-ml': [
    { id: 3001, name: '机器学习概念', description: '监督学习、无监督学习、强化学习', selected: false, weight: 7 },
    { id: 3002, name: '数据预处理', description: '数据清洗、特征工程', selected: false, weight: 8 },
    { id: 3003, name: '模型评估', description: '准确率、召回率、F1分数', selected: false, weight: 7 },
    { id: 3004, name: '过拟合与欠拟合', description: '模型复杂度与泛化能力', selected: false, weight: 8 }
  ],
  'ch02-ml': [
    { id: 3101, name: '线性回归', description: '最小二乘法、梯度下降', selected: false, weight: 8 },
    { id: 3102, name: '逻辑回归', description: '二分类、多分类问题', selected: false, weight: 8 },
    { id: 3103, name: '决策树', description: 'ID3、C4.5、CART算法', selected: false, weight: 7 },
    { id: 3104, name: 'SVM支持向量机', description: '核函数、软间隔', selected: false, weight: 9 }
  ],
  'ch03-ml': [
    { id: 3201, name: 'K-means聚类', description: 'K均值聚类算法', selected: false, weight: 7 },
    { id: 3202, name: '层次聚类', description: '凝聚、分裂聚类方法', selected: false, weight: 6 },
    { id: 3203, name: 'PCA降维', description: '主成分分析原理', selected: false, weight: 8 },
    { id: 3204, name: '关联规则', description: 'Apriori、FP-Growth算法', selected: false, weight: 6 }
  ],
  'ch04-ml': [
    { id: 3301, name: '感知机', description: '单层感知机、多层感知机', selected: false, weight: 7 },
    { id: 3302, name: '反向传播', description: 'BP算法原理与实现', selected: false, weight: 9 },
    { id: 3303, name: '激活函数', description: 'Sigmoid、ReLU、Tanh', selected: false, weight: 7 },
    { id: 3304, name: '神经网络优化', description: '学习率、批量大小调整', selected: false, weight: 8 }
  ],
  'ch05-ml': [
    { id: 3401, name: 'CNN卷积神经网络', description: '卷积层、池化层原理', selected: false, weight: 9 },
    { id: 3402, name: 'RNN循环神经网络', description: 'LSTM、GRU结构', selected: false, weight: 9 },
    { id: 3403, name: '迁移学习', description: '预训练模型、微调技术', selected: false, weight: 7 },
    { id: 3404, name: 'GAN生成对抗网络', description: '生成器、判别器原理', selected: false, weight: 8 }
  ],
  'ch06-ml': [
    { id: 3501, name: '超参数调优', description: '网格搜索、随机搜索', selected: false, weight: 7 },
    { id: 3502, name: '正则化技术', description: 'L1、L2正则化、Dropout', selected: false, weight: 8 },
    { id: 3503, name: '集成学习', description: 'Bagging、Boosting、Stacking', selected: false, weight: 8 },
    { id: 3504, name: '模型部署', description: '模型保存、在线预测', selected: false, weight: 7 }
  ],
  
  // 操作系统
  'ch01-os': [
    { id: 4001, name: '操作系统功能', description: '进程管理、内存管理、文件管理', selected: false, weight: 7 },
    { id: 4002, name: '操作系统结构', description: '单体、微内核、外核结构', selected: false, weight: 6 },
    { id: 4003, name: '系统调用', description: '系统调用接口与实现', selected: false, weight: 8 },
    { id: 4004, name: '中断机制', description: '中断处理过程', selected: false, weight: 7 }
  ],
  'ch02-os': [
    { id: 4101, name: '进程概念', description: '进程状态、进程控制块', selected: false, weight: 8 },
    { id: 4102, name: '进程调度', description: 'FCFS、SJF、优先级调度', selected: false, weight: 9 },
    { id: 4103, name: '进程同步', description: '信号量、管程、条件变量', selected: false, weight: 9 },
    { id: 4104, name: '线程管理', description: '用户线程、内核线程', selected: false, weight: 8 }
  ],
  'ch03-os': [
    { id: 4201, name: '内存分配', description: '连续分配、分页、分段', selected: false, weight: 8 },
    { id: 4202, name: '虚拟内存', description: '页面置换算法', selected: false, weight: 9 },
    { id: 4203, name: '页面置换', description: 'FIFO、LRU、Clock算法', selected: false, weight: 9 },
    { id: 4204, name: '内存保护', description: '地址空间、访问控制', selected: false, weight: 7 }
  ],
  'ch04-os': [
    { id: 4301, name: '文件系统结构', description: '文件组织、目录结构', selected: false, weight: 7 },
    { id: 4302, name: '文件操作', description: '创建、删除、读写操作', selected: false, weight: 7 },
    { id: 4303, name: '磁盘管理', description: '磁盘调度算法', selected: false, weight: 8 },
    { id: 4304, name: '文件保护', description: '访问控制、加密技术', selected: false, weight: 6 }
  ],
  'ch05-os': [
    { id: 4401, name: 'I/O设备', description: 'I/O设备分类与特点', selected: false, weight: 6 },
    { id: 4402, name: 'I/O控制方式', description: '程序控制、中断、DMA', selected: false, weight: 8 },
    { id: 4403, name: '缓冲技术', description: '单缓冲、双缓冲、缓冲池', selected: false, weight: 7 },
    { id: 4404, name: '设备驱动', description: '设备驱动程序结构', selected: false, weight: 7 }
  ],
  'ch06-os': [
    { id: 4501, name: '死锁概念', description: '死锁产生的四个条件', selected: false, weight: 8 },
    { id: 4502, name: '死锁预防', description: '破坏死锁必要条件', selected: false, weight: 7 },
    { id: 4503, name: '死锁避免', description: '银行家算法原理', selected: false, weight: 9 },
    { id: 4504, name: '死锁检测与恢复', description: '资源分配图、死锁恢复', selected: false, weight: 7 }
  ]
}
const selectedChapter = ref('')
const selectedKnowledgeBase = ref('')
const availableChapters = computed(() => {
  if (!selectedCourseId.value) return []
  const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
  return selectedCourse ? subjectChapterMap[selectedCourse.subject] || [] : []
})

// 课程变化处理
function onCourseChange() {
  selectedChapter.value = ''
  selectedKnowledgeBase.value = ''
  knowledgePoints.value = []
  
  // 如果选择了课程，自动选择第一个章节
  if (selectedCourseId.value && availableChapters.value.length > 0) {
    selectedChapter.value = availableChapters.value[0].value
    onChapterChange()
  }
}

// 🔧 计算属性：当前显示的题目
const currentQuestion = computed(() => {
  if (!generatedExam.value || !generatedExam.value.questions || generatedExam.value.questions.length === 0) {
    return null
  }
  return generatedExam.value.questions[currentQuestionIndex.value]
})

function onChapterChange() {
  if (!selectedCourseId.value) return
  
  const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
  if (!selectedCourse) return
  
  const chapter = (subjectChapterMap[selectedCourse.subject] || []).find(c => c.value === selectedChapter.value)
  selectedKnowledgeBase.value = chapter ? chapter.knowledgeBase : ''
  
  // 根据课程类型构建章节key
  let chapterKey = selectedChapter.value
  const subject = selectedCourse.subject
  
  // 为不同课程添加后缀以区分
  if (subject === 'Java程序设计' || subject.includes('Java')) {
    chapterKey = selectedChapter.value + '-java'
  } else if (subject === 'Linux系统' || subject.includes('Linux')) {
    chapterKey = selectedChapter.value + '-linux'
  } else if (subject === '计算机网络' || subject.includes('网络')) {
    chapterKey = selectedChapter.value + '-network'
  } else if (subject === '机器学习' || subject.includes('机器学习')) {
    chapterKey = selectedChapter.value + '-ml'
  } else if (subject === '操作系统' || subject.includes('操作系统')) {
    chapterKey = selectedChapter.value + '-os'
  }
  // 数据结构不需要后缀，直接使用 ch01, ch02 等
  
  // 切换知识点 - 深拷贝并重置选中状态
  const chapterPoints = chapterKnowledgePointsMap[chapterKey]
  if (chapterPoints && chapterPoints.length > 0) {
    knowledgePoints.value = chapterPoints.map(p => ({ 
      ...p, 
      selected: false,  // 重置选中状态
      weight: p.weight || 5  // 确保有默认权重
    }))
    console.log('章节切换 - 加载知识点:', chapterKey, knowledgePoints.value)
  } else {
    // 如果没有对应章节的知识点，使用默认知识点
    console.warn('章节没有对应知识点，使用默认知识点，章节key:', chapterKey)
    initKnowledgePoints()
  }
}

// 获取教师考试列表
const fetchTeacherExams = async () => {
  try {
    const res = await getExamsByTeacherId(teacherId)
    if (res && res.data) {
      examList.value = res.data
      console.log('教师考试列表:', res.data)
    }
  } catch (error) {
    console.error('获取教师考试列表失败:', error)
  }
}

// 获取教师课程列表
const fetchTeacherCourses = async () => {
  try {
    // 获取当前用户信息，这里假设是teacher1(ID=2)
    const currentTeacherId = localStorage.getItem('userId') || '2'
    const res = await getCoursesByTeacherId(currentTeacherId)
    
    if (res && (res.success === true || res.code === 200) && res.data && Array.isArray(res.data) && res.data.length > 0) {
      teacherCourses.value = res.data
      console.log('从API获取教师课程列表:', res.data)
      ElMessage.success(`成功加载 ${res.data.length} 门课程`)
      // 如果有课程，默认选择第一个
      selectedCourseId.value = res.data[0].id
      // 更新科目章节映射
      updateSubjectChapterMap(res.data)
    } else {
      console.log('没有找到课程数据')
      ElMessage.warning('您还没有任何课程，请先在系统中创建课程')
      teacherCourses.value = []
      selectedCourseId.value = null
    }
  } catch (error) {
    console.error('获取教师课程列表失败:', error)
    ElMessage.error('获取课程列表失败: ' + error.message)
    teacherCourses.value = []
    selectedCourseId.value = null
  }
}

// 根据实际课程更新科目章节映射
const updateSubjectChapterMap = (courses) => {
  // 不清空原有映射，保留预定义的五门课程数据
  
  // 根据实际课程补充映射（如果课程不在预定义列表中）
  courses.forEach(course => {
    // 如果该科目已经有预定义的章节，跳过
    if (subjectChapterMap[course.subject] && subjectChapterMap[course.subject].length > 0) {
      console.log(`课程 ${course.subject} 使用预定义章节`)
      return
    }
    
    // 如果没有预定义，创建默认章节
    console.log(`课程 ${course.subject} 创建默认章节`)
    subjectChapterMap[course.subject] = [
      { value: 'ch01', label: '第一章 基础概念', knowledgeBase: `${course.name}基础` },
      { value: 'ch02', label: '第二章 核心原理', knowledgeBase: `${course.name}原理` },
      { value: 'ch03', label: '第三章 实践应用', knowledgeBase: `${course.name}应用` },
      { value: 'ch04', label: '第四章 综合练习', knowledgeBase: `${course.name}练习` }
    ]
  })
  
  // 触发课程变化处理，自动选择第一个课程和章节
  if (courses.length > 0 && selectedCourseId.value) {
    onCourseChange()
  }
}

// 获取考试详情
const fetchExamById = async (id) => {
  try {
    const res = await getExamById(id)
    if (res && res.data) {
      console.log('考试详情:', res.data)
      return res.data
    }
  } catch (error) {
    console.error('获取考试详情失败:', error)
  }
}

// 创建考试
const createExamData = async (examData) => {
  try {
    console.log('准备发送的考试数据:', JSON.stringify(examData, null, 2))
    const res = await createExam(examData)
    console.log('后端响应原始数据:', res)
    console.log('后端响应结构详情:', {
      '响应类型': typeof res,
      '是否为空': res === null || res === undefined,
      '所有字段': res ? Object.keys(res) : '无字段',
      'success字段': res?.success,
      'success类型': typeof res?.success,
      'msg字段': res?.msg,
      'message字段': res?.message,
      'data字段': res?.data,
      'data类型': typeof res?.data
    })
    
    // 根据后端实际Result格式判断：success=true 或 消息包含"成功"
    const isSuccess = (res && res.success === true) || 
                     (res && res.msg && (res.msg.includes('成功') || res.msg.includes('success')))
    
    if (isSuccess) {
      console.log('考试创建成功 - success:', res.success, 'data:', res.data)
      return res.data || res
    } else {
      console.error('后端返回失败 - success:', res?.success, 'msg:', res?.msg)
      return null
    }
  } catch (error) {
    console.error('创建考试网络异常:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      statusText: error.response?.statusText
    })
    return null
  }
}

// 更新考试
const updateExamData = async (examData) => {
  try {
    const res = await updateExam(examData)
    if (res && res.success === true) {
      console.log('考试更新成功:', res.data)
      return res.data
    }
    return null
  } catch (error) {
    console.error('更新考试失败:', error)
    return null
  }
}

// 删除考试
const deleteExamData = async (id) => {
  try {
    const res = await deleteExam(id)
    if (res && res.success === true) {
      console.log('考试删除成功')
      return true
    }
    return false
  } catch (error) {
    console.error('删除考试失败:', error)
    return false
  }
}

// 发布考试
const publishExamData = async (id) => {
  try {
    console.log('准备发布考试，ID:', id)
    const res = await publishExam(id)
    console.log('发布考试响应:', res)
    console.log('发布考试响应详情:', {
      '响应类型': typeof res,
      '所有字段': res ? Object.keys(res) : '无字段',
      'success字段': res?.success,
      'success类型': typeof res?.success,
      'msg字段': res?.msg,
      'data字段': res?.data
    })
    
    if (res && res.success === true) {
      console.log('考试发布成功 - success:', res.success, 'msg:', res.msg)
      return true
    } else {
      console.error('考试发布失败 - success:', res?.success, 'msg:', res?.msg)
      return false
    }
  } catch (error) {
    console.error('发布考试网络异常:', error)
    console.error('发布考试错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      statusText: error.response?.statusText
    })
    return false
  }
}

// 归档考试
const archiveExamData = async (id) => {
  try {
    const res = await archiveExam(id)
    if (res && res.success === true) {
      console.log('考试归档成功')
      return true
    }
    return false
  } catch (error) {
    console.error('归档考试失败:', error)
    return false
  }
}

// 智能生成考试
const generateExamData = async (params) => {
  try {
    // 构建AI请求数据
    const aiRequestData = {
      subject: params.subject || '计算机科学',
      difficulty: params.difficulty || '中等',
      questionCount: params.questionCount,
      examType: params.examType,
      examName: params.examName,
      knowledgePoints: params.knowledgePoints,
      questionTypes: params.questionTypes
    }
    
    console.log('发送AI考试生成请求:', aiRequestData)
    const response = await aiAPI.generateExam(aiRequestData)
    console.log('AI考试生成响应完整结构:', response)
    console.log('响应字段详情 - success:', response?.success, 'typeof success:', typeof response?.success)
    console.log('响应字段详情 - msg:', response?.msg)
    console.log('响应字段详情 - data:', response?.data)
    console.log('响应所有键:', response ? Object.keys(response) : 'null')
    
    // 处理后端Result格式的响应
    // 注意：响应拦截器已经返回了response.data，所以这里的response就是后端的Result对象
    const isSuccess = (response && response.success === true) || 
                     (response && response.msg && response.msg.includes('生成成功'))
    
    if (isSuccess) {
      // 🔧 修复：后端返回的数据在 response.data.exam.questions
      let questionsData = null
      
      // 尝试多种可能的数据结构
      if (response.data && response.data.exam && response.data.exam.questions && response.data.exam.questions.length > 0) {
        console.log('✅ 在 response.data.exam.questions 找到题目 - 数量:', response.data.exam.questions.length)
        questionsData = {
          questions: response.data.exam.questions,
          exam_title: response.data.exam.title,
          total_score: response.data.exam.total_score,
          duration_minutes: parseInt(response.data.exam.duration) || 60
        }
      } else if (response.data && response.data.questions && response.data.questions.length > 0) {
        console.log('✅ 在 response.data.questions 找到题目 - 数量:', response.data.questions.length)
        questionsData = response.data
      } else if (response.questions && response.questions.length > 0) {
        console.log('✅ 在 response.questions 找到题目 - 数量:', response.questions.length)
        questionsData = { questions: response.questions }
      }
      
      if (questionsData) {
        return {
          success: true,
          data: questionsData
        }
      } else {
        console.warn('⚠️ AI生成成功但题目数据为空:', response.data)
        return {
          success: false,
          error: '生成的题目数据为空，请重试'
        }
      }
    } else {
      console.warn('AI生成考试响应失败 - success:', response?.success, 'msg:', response?.msg)
      return {
        success: false,
        error: response?.msg || '生成失败'
      }
    }
  } catch (error) {
    console.error('AI生成考试网络异常:', error)
    
    let errorMsg = '网络错误'
    if (error.response) {
      // 服务器响应错误
      errorMsg = error.response.data?.msg || error.response.statusText || '服务器错误'
    } else if (error.request) {
      // 网络连接错误
      errorMsg = '网络连接失败，请检查网络'
    } else {
      // 其他错误
      errorMsg = error.message || '未知错误'
    }
    
    return {
      success: false,
      error: errorMsg
    }
  }
}

// 切换知识点选择
const toggleKnowledgePoint = (id) => {
  const point = knowledgePoints.value.find(p => p.id === id)
  if (point) {
    point.selected = !point.selected
  }
}

// 生成考试
const generateExamHandler = async () => {
  generating.value = true
  
  // 验证课程选择
  if (!selectedCourseId.value) {
    ElMessage.warning('请先选择课程')
    generating.value = false
    return
  }
  
  // 计算总题目数
  const totalQuestions = questionTypes.value.reduce((sum, type) => sum + type.count, 0)
  if (totalQuestions === 0) {
    ElMessage.warning('请至少选择一道题目')
    generating.value = false
    return
  }
  
  // 计算总分
  const totalScore = questionTypes.value.reduce((sum, type) => sum + (type.count * type.scorePer), 0)
  
  // 获取选中的知识点
  // 获取权重大于0的知识点（不再需要手动选择）
  const selectedKnowledgePoints = knowledgePoints.value.filter(p => p.weight > 0)
  if (selectedKnowledgePoints.length === 0) {
    ElMessage.warning('请为至少一个知识点设置权重（权重大于0）')
    generating.value = false
    return
  }
  
  // 显示进度条
  showProgress.value = true
  progressValue.value = 0
  currentStep.value = 0
  progressMessage.value = '正在准备数据...'
  
  try {
    // 步骤1: 准备生成参数
    progressValue.value = 10
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const selectedCourse = teacherCourses.value.find(c => c.id === selectedCourseId.value)
    const params = {
      subject: selectedCourse?.subject || '',
      chapter: selectedChapter.value,
      knowledgeBase: selectedKnowledgeBase.value,
      difficulty: '中等', // 可根据需要扩展
      examName: examConfig.value.name,
      examType: examConfig.value.type,
      questionCount: totalQuestions,
      duration: examConfig.value.duration,
      totalScore: totalScore,
      knowledgePoints: selectedKnowledgePoints.map(p => ({
        id: p.id,
        name: p.name,
        description: p.description,
        weight: p.weight
      })),
      questionTypes: questionTypes.value.filter(type => type.count > 0).map(type => ({
        type: type.key,
        count: type.count,
        scorePer: type.scorePer,
        difficulty: type.difficulty
      }))
    }
    
    console.log('生成考试参数:', params)
    console.log('选中的知识点详情:', selectedKnowledgePoints)
    
    // 步骤2: 调用AI服务
    currentStep.value = 1
    progressValue.value = 25
    progressMessage.value = '正在连接AI服务...'
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 步骤3: 生成题目
    currentStep.value = 2
    progressValue.value = 40
    progressMessage.value = 'AI正在生成题目，请耐心等待...'
    
    // 模拟进度增长
    const progressInterval = setInterval(() => {
      if (progressValue.value < 85) {
        progressValue.value += Math.random() * 5
      }
    }, 800)
    
    // 调用AI API生成考试
    const result = await generateExamData(params)
    clearInterval(progressInterval)
    
    console.log('generateExamData返回结果 - success:', result.success, 'data:', result.data ? '有数据' : '无数据', 'questions:', result.data?.questions?.length || 0)
    
    progressValue.value = 90
    
    if (result.success && result.data && result.data.questions && result.data.questions.length > 0) {
      // 🔧 直接转换题目数据（简单可靠）
      console.log('📥 后端返回的原始数据:', result.data.questions)
      
      const convertedQuestions = result.data.questions.map((q, index) => {
        // 🔧 标准化题型：将各种格式统一转换（支持中英文）
        let standardType = q.type || 'choice'
        const typeMap = {
          // 英文题型映射
          'multiple_choice': 'choice',
          'single_choice': 'choice',
          'fill_in_blank': 'fill',
          'fill_blank': 'fill',
          'short_answer': 'short',
          'programming': 'coding',
          'true_false': 'true_false',
          'judge': 'true_false',
          // 🔧 中文题型映射
          '选择题': 'choice',
          '单选题': 'choice',
          '多选题': 'multiple',
          '填空题': 'fill',
          '判断题': 'true_false',
          '简答题': 'short',
          '案例分析题': 'short',
          '匹配题': 'short',
          '编程题': 'coding'
        }
        standardType = typeMap[standardType] || standardType
        
        // 基本字段转换
        const converted = {
          id: q.id || `q_${index + 1}`,
          type: standardType,
          content: q.question_text || q.content || q.text || q.title || q.question || '题目内容缺失',  // 🔧 添加 q.question_text 支持
          answer: q.correct_answer || q.answer || '',  // 🔧 优先使用 q.correct_answer
          score: Number(q.points || q.score) || 0,  // 🔧 添加 q.points 支持
          explanation: q.explanation || q.analysis || '',
          difficulty: q.difficulty || 'medium'
        }
        
        // 🔧 判断题特殊处理：确保 answer 是布尔值
        if (standardType === 'true_false') {
          const rawAnswer = q.correct_answer || q.answer
          if (typeof rawAnswer === 'string') {
            const lowerAnswer = rawAnswer.toLowerCase().trim()
            if (lowerAnswer === 'true' || lowerAnswer === '正确' || lowerAnswer === '对' || lowerAnswer === 't' || lowerAnswer === '1') {
              converted.answer = true
            } else if (lowerAnswer === 'false' || lowerAnswer === '错误' || lowerAnswer === '错' || lowerAnswer === 'f' || lowerAnswer === '0') {
              converted.answer = false
            } else {
              converted.answer = true  // 默认值
            }
          } else if (typeof rawAnswer === 'boolean') {
            converted.answer = rawAnswer
          } else {
            converted.answer = true  // 默认值
          }
          console.log(`🔍 判断题 #${index + 1} 答案转换: ${rawAnswer} -> ${converted.answer} (${typeof converted.answer})`)
        }
        
        // 🔧 填空题特殊处理：确保 answer 是数组或字符串
        if (standardType === 'fill') {
          const rawAnswer = q.correct_answer || q.answer
          // 如果是数组，保持数组格式
          if (Array.isArray(rawAnswer)) {
            converted.answer = rawAnswer.map(a => String(a))
          } else {
            converted.answer = String(rawAnswer || '')
          }
          console.log(`🔍 填空题 #${index + 1} 答案:`, converted.answer)
        }
        
        // 🔧 简答题特殊处理：确保有参考答案
        if (standardType === 'short') {
          const rawAnswer = q.sample_answer || q.correct_answer || q.answer
          converted.answer = String(rawAnswer || '请参考相关资料')
          console.log(`🔍 简答题 #${index + 1} 答案长度: ${converted.answer.length} 字符`)
        }
        
        // 转换选项（单选题和多选题）
        if (q.options && Array.isArray(q.options)) {
          converted.options = q.options.map((opt, optIndex) => {
            // 如果已经是对象格式 {key: "A", content: "选项内容"}
            if (typeof opt === 'object' && opt !== null && opt.key && opt.content) {
              return { key: opt.key, content: opt.content }
            }
            // 如果是字符串格式 "A. 选项内容"
            if (typeof opt === 'string') {
              const match = opt.match(/^([A-Z])\.\s*(.+)$/)
              if (match) {
                return { key: match[1], content: match[2].trim() }
              }
              // 没有匹配到格式，自动生成key
              return {
                key: String.fromCharCode(65 + optIndex), // A, B, C, D...
                content: opt.trim()
              }
            }
            // 其他情况
            return {
              key: String.fromCharCode(65 + optIndex),
              content: String(opt)
            }
          })
        }
        
        // 编程题的特殊字段
        if (standardType === 'coding') {
          // 编程题的描述字段（问题描述）
          converted.description = q.content || q.description || q.question_text || converted.content
          
          // 编程题的其他字段
          if (q.sample_solution) converted.answer = q.sample_solution  // 🔧 编程题的答案是示例代码
          if (q.evaluation_criteria) converted.evaluation_criteria = q.evaluation_criteria
          if (q.requirements) converted.requirements = q.requirements
          if (q.inputFormat) converted.inputFormat = q.inputFormat
          if (q.outputFormat) converted.outputFormat = q.outputFormat
          if (q.examples) converted.examples = q.examples
          if (q.testCases) converted.testCases = q.testCases
          if (q.hints) converted.hints = q.hints
          
          console.log(`🔍 编程题 #${index + 1} 字段:`, {
            description: !!converted.description,
            requirements: !!converted.requirements,
            inputFormat: !!converted.inputFormat,
            outputFormat: !!converted.outputFormat,
            examples: converted.examples?.length || 0,
            testCases: converted.testCases?.length || 0
          })
        }
        
        // 🔧 容错处理：如果选择题没有选项，生成默认选项
        if ((standardType === 'choice' || standardType === 'multiple') && 
            (!converted.options || converted.options.length === 0)) {
          console.warn(`⚠️ ${standardType} 题目缺少选项，生成默认选项`)
          converted.options = [
            { key: 'A', content: '选项A（后端未生成）' },
            { key: 'B', content: '选项B（后端未生成）' },
            { key: 'C', content: '选项C（后端未生成）' },
            { key: 'D', content: '选项D（后端未生成）' }
          ]
          if (!converted.answer || converted.answer === '请参考相关资料') {
            converted.answer = 'A'
          }
        }
        
        // 🔧 容错处理：判断题答案修复
        if (standardType === 'true_false') {
          if (converted.answer === '请参考相关资料' || !converted.answer || typeof converted.answer !== 'boolean') {
            console.warn(`⚠️ 判断题答案异常: ${converted.answer}，设置为默认值 true`)
            converted.answer = true
          }
        }
        
        // 🔧 容错处理：填空题答案修复
        if (standardType === 'fill') {
          if (converted.answer === '请参考相关资料' || !converted.answer) {
            console.warn(`⚠️ 填空题答案为空，设置为默认值`)
            converted.answer = '____'
          }
        }
        
        return converted
      })
      
      console.log('✅ 题目数据已转换，共', convertedQuestions.length, '道题')
      console.log('📤 转换后的第一题:', convertedQuestions[0])
      
      generatedExam.value = {
        name: result.data.exam_title || result.data.examTitle || examConfig.value.name,
        duration: result.data.duration_minutes || result.data.totalTime || examConfig.value.duration,
        totalScore: result.data.total_score || result.data.totalScore || totalScore,
        questionCount: convertedQuestions.length,
        questions: convertedQuestions
      }
      
      // 保存到 localStorage，防止刷新丢失
      localStorage.setItem('generatedExam', JSON.stringify(generatedExam.value))
      localStorage.setItem('examConfig', JSON.stringify(examConfig.value))
      
      // 步骤4: 完成
      currentStep.value = 3
      progressValue.value = 100
      progressMessage.value = '生成完成！'
      
      await new Promise(resolve => setTimeout(resolve, 800))
      showProgress.value = false
      
      ElMessage.success(`考核题目生成成功！共${result.data.questions.length}道题目`)
      
      // 🔧 新增：跳转到第三步（题目展示页面）
      goToStep(3)
      console.log('🔧 已跳转到第三步，wizardStep:', wizardStep.value)
      console.log('🔧 generatedExam:', generatedExam.value)
      console.log('🔧 showHistoryView:', showHistoryView.value)
      console.log('🔧 currentQuestion:', currentQuestion.value)
      
    } else {
      console.log('AI生成失败，使用模拟数据:', result.error)
      
      // 生成失败时使用模拟数据
      generatedExam.value = {
        name: examConfig.value.name,
        duration: examConfig.value.duration,
        totalScore: totalScore,
        questionCount: totalQuestions,
        questions: generateMockQuestions()
      }
      
      progressValue.value = 100
      await new Promise(resolve => setTimeout(resolve, 500))
      showProgress.value = false
      
      let errorMsg = 'AI生成失败，已使用模拟题目'
      
      // 针对不同错误类型提供更友好的提示
      if (result.error) {
        if (result.error.includes('timeout')) {
          errorMsg = 'AI生成超时，这通常是因为题目生成需要较长时间。已使用模拟题目，请稍后重试或尝试减少题目数量。'
        } else if (result.error.includes('繁忙') || result.error.includes('429')) {
          errorMsg = 'AI服务繁忙，已使用模拟题目，请稍后重试'
        } else if (result.error.includes('服务')) {
          errorMsg = 'AI服务暂时不可用，已使用模拟题目，请稍后重试'
        } else {
          errorMsg = `AI生成失败：${result.error}，已使用模拟题目`
        }
      }
      
      ElMessage.warning({
        message: errorMsg,
        duration: 8000,
        showClose: true
      })
    }
  } catch (error) {
    console.error('生成考试异常:', error)
    
    showProgress.value = false
    
    // 生成失败时使用模拟数据
    generatedExam.value = {
      name: examConfig.value.name,
      duration: examConfig.value.duration,
      totalScore: totalScore,
      questionCount: totalQuestions,
      questions: generateMockQuestions()
    }
    
    showProgress.value = false
    
    ElMessage.error({
      message: '生成考试时发生异常，已使用模拟题目',
      duration: 8000,
      showClose: true
    })
  } finally {
    generating.value = false
  }
}

// 生成模拟题目（当API失败时使用）
const generateMockQuestions = () => {
  const questions = []
  let questionIndex = 1
  
  questionTypes.value.forEach(type => {
    for (let i = 0; i < type.count; i++) {
      const question = {
        id: questionIndex,
        type: type.key,
        content: `这是第${questionIndex}题，类型为${type.name}`,
        score: type.scorePer,
        answer: `第${questionIndex}题的参考答案`
      }
      
      if (type.key === 'choice' || type.key === 'multiple') {
        question.options = [
          { key: 'A', content: '选项A' },
          { key: 'B', content: '选项B' },
          { key: 'C', content: '选项C' },
          { key: 'D', content: '选项D' }
        ]
      }
      
      questions.push(question)
      questionIndex++
    }
  })
  
  return questions
}

// 预览考试
const previewExam = () => {
  showPreview.value = true
}

// 关闭预览
const closePreview = () => {
  showPreview.value = false
}

// 🔧 新增：翻页函数
const nextQuestion = () => {
  if (generatedExam.value && currentQuestionIndex.value < generatedExam.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 🔧 新增：重新生成考试
const regenerateExam = () => {
  generatedExam.value = null
  goToStep(2)
}

// 导出PDF
const exportExam = () => {
  if (!generatedExam.value) return
  const examDom = document.querySelector('.exam-card')
  html2canvas(examDom).then(canvas => {
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const imgProps = pdf.getImageProperties(imgData)
    const pdfWidth = pageWidth
    const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width
    pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight)
    pdf.save(`${generatedExam.value.name || 'exam'}.pdf`)
  })
}

// 转换题目数据格式以匹配后端实体
const convertQuestionsForBackend = (questions) => {
  return questions.map((question, index) => {
    const convertedQuestion = {
      type: question.type,
      content: question.content,
      answer: Array.isArray(question.answer) ? question.answer.join(',') : String(question.answer),
      analysis: question.explanation || question.analysis || '', // 字段名转换
      score: Number(question.score) || 0,
      difficulty: question.difficulty || 'medium',
      knowledgePoint: question.knowledgePoint || '',
      sortOrder: index + 1
    }
    
    // 处理选项数据
    if (question.options && Array.isArray(question.options)) {
      convertedQuestion.options = question.options.map((option, optIndex) => ({
        key: option.key,
        content: option.content,
        isCorrect: question.answer === option.key || (Array.isArray(question.answer) && question.answer.includes(option.key)),
        sortOrder: optIndex + 1
      }))
    }
    
    return convertedQuestion
  })
}

// 保存考试
const saveExam = async () => {
  if (!generatedExam.value) {
    ElMessage.warning('请先生成考试')
    return
  }
  
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  
  try {
    const examData = {
      name: String(generatedExam.value.name || ''),
      type: String(examConfig.value.type || 'homework'),
      duration: Number(generatedExam.value.duration) || 60,
      totalScore: Number(generatedExam.value.totalScore) || 100,
      courseId: Number(selectedCourseId.value),
      teacherId: Number(teacherId),
      status: 'draft',
      questions: convertQuestionsForBackend(generatedExam.value.questions)
    }
    
    console.log('转换后的考试数据:', examData)
    
    const savedExam = await createExamData(examData)
    if (savedExam) {
      ElMessage.success('考试保存成功')
      console.log('考试保存成功')
      // 清除临时数据
      localStorage.removeItem('generatedExam')
      localStorage.removeItem('examConfig')
      // 重新获取考试列表
      await fetchTeacherExams()
    }
  } catch (error) {
    console.error('保存考试失败:', error)
    ElMessage.error('保存考试失败')
  }
}

// 发布考试
const publishExamHandler = async () => {
  if (!generatedExam.value) {
    ElMessage.warning('请先生成考试')
    return
  }
  
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  
  try {
    // 先保存考试
    const examData = {
      name: String(generatedExam.value.name || ''),
      type: String(examConfig.value.type || 'homework'),
      duration: Number(generatedExam.value.duration) || 60,
      totalScore: Number(generatedExam.value.totalScore) || 100,
      courseId: Number(selectedCourseId.value),
      teacherId: Number(teacherId),
      status: 'draft',
      questions: convertQuestionsForBackend(generatedExam.value.questions)
    }
    
    const savedExam = await createExamData(examData)
    if (savedExam && savedExam.id) {
      const result = await publishExamData(savedExam.id)
      if (result) {
        ElMessage.success('考试发布成功，学生端可见！')
        // 重新获取考试列表
        await fetchTeacherExams()
      } else {
        ElMessage.error('考试发布失败！')
      }
    } else {
      ElMessage.error('保存考试失败，无法发布！')
    }
  } catch (error) {
    console.error('发布考试失败:', error)
    ElMessage.error('发布考试失败！')
  }
}

// 获取题目类型名称
const getQuestionTypeName = (type) => {
  const typeMap = {
    choice: '单选题',
    multiple_choice: '单选题',  // AI返回的格式
    multiple: '多选题',
    fill: '填空题',
    fill_in_the_blank: '填空题',  // AI返回的格式
    fill_in_blank: '填空题',
    essay: '简答题',
    short: '简答题',  // 后端AI使用的简答题类型
    short_answer: '简答题',  // AI返回的格式
    programming: '编程题',
    coding: '编程题',  // 后端AI使用的编程题类型
    command: '命令题',  // AI返回的格式
    true_false: '判断题',  // ✅ 新增：判断题
    judge: '判断题',
    unknown: '综合题'  // 后端默认题目可能使用的类型
  }
  return typeMap[type] || '未知类型'
}

// 获取题目类型样式类
const getQuestionTypeClass = (type) => {
  const classMap = {
    choice: 'type-choice',
    multiple_choice: 'type-choice',
    multiple: 'type-multiple',
    fill: 'type-fill',
    fill_in_the_blank: 'type-fill',
    fill_in_blank: 'type-fill',
    essay: 'type-essay',
    short: 'type-essay',  // 简答题使用相同样式
    short_answer: 'type-essay',
    programming: 'type-programming',
    coding: 'type-programming',  // 编程题使用相同样式
    command: 'type-programming',  // 命令题使用编程题样式
    true_false: 'type-choice',  // ✅ 判断题使用选择题样式
    judge: 'type-choice',
    unknown: 'type-unknown'
  }
  return classMap[type] || 'type-unknown'
}

// 🔧 新增：获取题目类型颜色（用于Tag标签）
const getQuestionTypeColor = (type) => {
  const colorMap = {
    choice: 'primary',
    multiple_choice: 'primary',
    multiple: 'success',
    fill: 'warning',
    fill_in_the_blank: 'warning',
    fill_in_blank: 'warning',
    essay: 'danger',
    short: 'danger',
    short_answer: 'danger',
    programming: 'info',
    coding: 'info',
    command: 'info',
    true_false: 'primary',  // 🔧 修复：改为 primary
    judge: 'primary'  // 🔧 修复：改为 primary
  }
  return colorMap[type] || 'info'  // 🔧 修复：默认值改为 info
}

// 获取题目类型图标
const getQuestionTypeIcon = (type) => {
  const iconMap = {
    choice: '/src/assets/author.png',
    multiple_choice: '/src/assets/author.png',
    multiple: '/src/assets/category.png',
    fill: '/src/assets/balance.png',
    fill_in_the_blank: '/src/assets/balance.png',
    fill_in_blank: '/src/assets/balance.png',
    essay: '/src/assets/time.png',
    short: '/src/assets/time.png',  // 简答题使用相同图标
    short_answer: '/src/assets/time.png',
    programming: '/src/assets/code.png',
    coding: '/src/assets/code.png',  // 编程题使用相同图标
    command: '/src/assets/code.png',  // 命令题使用代码图标
    true_false: '/src/assets/author.png',  // ✅ 判断题使用选择题图标
    judge: '/src/assets/author.png',
    unknown: '/src/assets/category.png'
  }
  return iconMap[type] || '/src/assets/author.png'
}

// 获取考试类型名称
const getExamTypeName = (type) => {
  const typeMap = {
    quiz: '小测验',
    homework: '作业',
    midterm: '期中考试',
    final: '期末考试',
    practice: '练习'
  }
  return typeMap[type] || type
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    draft: 'info',
    published: 'success',
    archived: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    draft: '草稿',
    published: '已发布',
    archived: '已归档'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化题目内容，自动识别并美化排版（参考学生端ProgrammingPractice.vue）
const formatQuestionContent = (content) => {
  if (!content) return ''
  
  let html = '<div class="auto-formatted-content">'
  
  // 按行分割内容
  const lines = content.split('\n')
  let currentSection = ''
  let sectionContent = []
  
  const sectionIcons = {
    '【题目】': '📝',
    '题目描述': '📝',
    '编程要求': '📝',
    '题目要求': '📝',
    '问题描述': '📝',
    '输入格式': '📥',
    '输入': '📥',
    '输出格式': '📤',
    '输出': '📤',
    '示例': '💡',
    '样例': '💡',
    '示例输入': '💡',
    '示例输出': '💡',
    '数据范围': '⚠️',
    '注意': '⚠️',
    '说明': 'ℹ️',
    '提示': '💡',
    '解释': 'ℹ️',
    '要求': '📋'
  }
  
  const flushSection = () => {
    if (currentSection && sectionContent.length > 0) {
      const icon = sectionIcons[currentSection] || '▪️'
      html += `<div class="section">
        <h4 class="section-title">${icon} ${currentSection}</h4>
        <div class="section-content">${sectionContent.join('<br>')}</div>
      </div>`
      sectionContent = []
    }
  }
  
  for (let line of lines) {
    line = line.trim()
    
    if (!line) {
      continue
    }
    
    // 检查是否是章节标题
    let isSection = false
    for (let sectionName of Object.keys(sectionIcons)) {
      if (line.includes(sectionName + '：') || line.includes(sectionName + ':') || line === sectionName || line.startsWith(sectionName)) {
        flushSection()
        currentSection = sectionName.replace(/[【】]/g, '')
        isSection = true
        // 如果标题后面还有内容，添加到section中
        const afterColon = line.split(/[：:]/)[1]
        if (afterColon && afterColon.trim()) {
          sectionContent.push(escapeHtml(afterColon.trim()))
        }
        break
      }
    }
    
    if (!isSection && line) {
      // 处理代码块（以def、function、class等开头）
      if (line.match(/^(def|function|class|import|from|return|if|for|while|int|void|public|private|#include|\/\/|#|var|let|const|async|await)/)) {
        sectionContent.push(`<code class="inline-code">${escapeHtml(line)}</code>`)
      } else if (line.startsWith('输入：') || line.startsWith('输出：') || line.startsWith('输入:') || line.startsWith('输出:')) {
        sectionContent.push(`<div class="example-io">${escapeHtml(line)}</div>`)
      } else if (line.match(/^\d+[\.\、]/)) {
        // 数字列表项
        sectionContent.push(`<div class="list-item">${escapeHtml(line)}</div>`)
      } else {
        sectionContent.push(escapeHtml(line))
      }
    }
  }
  
  flushSection()
  html += '</div>'
  
  return html
}

// HTML转义函数
const escapeHtml = (text) => {
  const div = document.createElement('div')
  div.textContent = text
  return div.innerHTML
}

// 根据课程ID获取考试列表
const getExamsByCourse = (courseId) => {
  return examList.value.filter(exam => exam.courseId === courseId)
}

// 获取课程名称
const getCourseName = (courseId) => {
  const course = teacherCourses.value.find(c => c.id === courseId)
  return course ? course.name : '未知课程'
}

// 处理课程标签页点击
const handleCourseTabClick = (tab) => {
  console.log('切换到课程标签页:', tab.name)
  activeCourseTab.value = tab.name
}

// 查看考试详情
const viewExamDetails = async (exam) => {
  try {
    const examDetails = await fetchExamById(exam.id)
    if (examDetails) {
      console.log('考试详情数据:', examDetails)
      console.log('题目数据:', examDetails.questions)
      
      // 将考试详情转换为生成格式，用于预览
      generatedExam.value = {
        name: examDetails.name,
        duration: examDetails.duration,
        totalScore: examDetails.totalScore,
        questionCount: examDetails.questions?.length || 0,
        questions: examDetails.questions || []
      }
      
      // 确保每个题目都有选项数据
      if (generatedExam.value.questions) {
        generatedExam.value.questions.forEach((question, index) => {
          console.log(`题目${index + 1}:`, question)
          console.log(`题目${index + 1}选项:`, question.options)
        })
      }
      
      showPreview.value = true
      ElMessage.success(`查看考试：${exam.name}`)
    }
  } catch (error) {
    console.error('获取考试详情失败:', error)
    ElMessage.error('获取考试详情失败')
  }
}

// 编辑考试
const editExam = (exam) => {
  // 将现有考试加载到编辑表单中
  examConfig.value = {
    name: exam.name,
    type: exam.type,
    duration: exam.duration,
    totalScore: exam.totalScore
  }
  selectedCourseId.value = exam.courseId
  
  // 获取考试详情并转换为生成格式
  fetchExamById(exam.id).then(examDetails => {
    if (examDetails && examDetails.questions) {
      generatedExam.value = {
        name: examDetails.name,
        duration: examDetails.duration,
        totalScore: examDetails.totalScore,
        questionCount: examDetails.questions.length,
        questions: examDetails.questions
      }
      ElMessage.success(`已加载考试：${exam.name}`)
    }
  }).catch(error => {
    console.error('加载考试失败:', error)
    ElMessage.error('加载考试失败')
  })
}

// 发布现有考试
const publishExistingExam = async (exam) => {
  try {
    const result = await publishExamData(exam.id)
    if (result) {
      ElMessage.success(`考试"${exam.name}"发布成功，学生端可见！`)
      await fetchTeacherExams() // 重新获取列表
    } else {
      ElMessage.error('发布失败')
    }
  } catch (error) {
    console.error('发布考试失败:', error)
    ElMessage.error('发布考试失败')
  }
}

// 归档现有考试
const archiveExistingExam = async (exam) => {
  try {
    const result = await archiveExamData(exam.id)
    if (result) {
      ElMessage.success(`考试"${exam.name}"已归档`)
      await fetchTeacherExams() // 重新获取列表
    } else {
      ElMessage.error('归档失败')
    }
  } catch (error) {
    console.error('归档考试失败:', error)
    ElMessage.error('归档考试失败')
  }
}

// 删除现有考试
const deleteExistingExam = async (exam) => {
  try {
    const confirmed = await ElMessageBox.confirm(
      `确定要删除考试"${exam.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      const result = await deleteExamData(exam.id)
      if (result) {
        ElMessage.success(`考试"${exam.name}"已删除`)
        await fetchTeacherExams() // 重新获取列表
      } else {
        ElMessage.error('删除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除考试失败:', error)
      ElMessage.error('删除考试失败')
    }
  }
}

// 初始化知识点数据
const initKnowledgePoints = () => {
  knowledgePoints.value = [
    { id: 1, name: '基础概念', description: '课程基础概念和定义', selected: false, weight: 5 },
    { id: 2, name: '核心原理', description: '课程核心原理和理论', selected: false, weight: 8 },
    { id: 3, name: '应用实践', description: '实际应用和案例分析', selected: false, weight: 6 },
    { id: 4, name: '综合运用', description: '综合运用和问题解决', selected: false, weight: 7 }
  ]
}

// 课程选择变量已在上面定义

// 监听考试数据变化，自动保存到缓存
watch(generatedExam, () => {
  if (generatedExam.value && wizardStep.value === 3) {
    saveExamToCache()
  }
}, { deep: true })

// 监听当前题目索引变化
watch(currentQuestionIndex, () => {
  if (generatedExam.value) {
    saveExamToCache()
  }
})

// 🔧 新增：定时检查缓存过期
let cacheCheckInterval = null

// 页面卸载时清理
onUnmounted(() => {
  if (cacheCheckInterval) {
    clearInterval(cacheCheckInterval)
  }
})

// 页面加载时获取数据
onMounted(async () => {
  // 🔧 优先检查缓存
  const hasCachedExam = loadExamFromCache()
  
  if (!hasCachedExam) {
    // 没有缓存，正常加载数据
    await fetchTeacherCourses()
    await fetchExamList()
  }
  
  // 🔧 启动缓存过期检查（每分钟检查一次）
  cacheCheckInterval = setInterval(() => {
    const cached = sessionStorage.getItem(EXAM_CACHE_KEY)
    if (cached) {
      try {
        const cacheData = JSON.parse(cached)
        const elapsed = Date.now() - cacheData.timestamp
        const remaining = CACHE_DURATION - elapsed
        
        // 剩余5分钟提醒
        if (remaining > 0 && remaining < 5 * 60 * 1000 && remaining > 4 * 60 * 1000) {
          ElMessage.warning('题目缓存将在5分钟后过期，请及时保存')
        }
        
        // 过期清除
        if (remaining <= 0) {
          clearExamCache()
          generatedExam.value = null
          wizardStep.value = 1
          ElMessage.error('题目缓存已过期，已自动清除')
        }
      } catch (error) {
        console.error('检查缓存失败:', error)
      }
    }
  }, 60000)
  
  // 恢复生成的考试数据
  const savedExam = localStorage.getItem('generatedExam')
  if (savedExam) {
    try {
      generatedExam.value = JSON.parse(savedExam)
      console.log('恢复生成的考试数据:', generatedExam.value)
    } catch (e) {
      console.error('恢复考试数据失败:', e)
    }
  }
  
  // 恢复考试配置
  const savedConfig = localStorage.getItem('examConfig')
  if (savedConfig) {
    try {
      examConfig.value = JSON.parse(savedConfig)
      console.log('恢复考试配置:', examConfig.value)
    } catch (e) {
      console.error('恢复考试配置失败:', e)
    }
  }
  
  await fetchTeacherCourses()
  await fetchTeacherExams()
  
  // 如果没有知识点，则使用默认知识点
  if (knowledgePoints.value.length === 0) {
    initKnowledgePoints()
  }
  
  console.log('初始化完成:', {
    selectedCourse: selectedCourseId.value,
    selectedChapter: selectedChapter.value,
    selectedKnowledgeBase: selectedKnowledgeBase.value,
    knowledgePointsCount: knowledgePoints.value.length,
    hasGeneratedExam: !!generatedExam.value
  })
})

// 辅助函数：获取题型的key（用于CSS类名）
const getQuestionTypeKey = (type) => {
  const typeMap = {
    'choice': 'choice',
    'multiple_choice': 'choice',
    'single_choice': 'choice',
    'multiple': 'multiple',
    'true_false': 'judge',
    'judge': 'judge',
    'fill': 'fill',
    'fill_in_the_blank': 'fill',
    'short': 'short',
    'short_answer': 'short',
    'essay': 'short',
    'coding': 'coding',
    'programming': 'coding',
    'command': 'coding'
  }
  return typeMap[type] || 'choice'
}
</script>

<style lang="scss" scoped>
.exam-generation {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  
  .step-container {
    animation: fadeIn 0.5s ease-in-out;
    
    &.step-1 {
      .el-row {
        margin-bottom: 0;
      }
      
      .el-col {
        margin-bottom: 24px;
        
        @media (max-width: 768px) {
          margin-bottom: 16px;
        }
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 32px;
  border-radius: 24px;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
    pointer-events: none;
  }
  
  .header-content {
    position: relative;
    z-index: 2;
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .header-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
      
      h1 {
        margin: 0;
        font-size: 2.5rem;
        font-weight: 800;
        background: linear-gradient(135deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
    
    .header-subtitle {
      margin: 0;
      font-size: 1.1rem;
      opacity: 0.9;
      font-weight: 400;
    }
  }
  
  .header-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
      animation: float 6s ease-in-out infinite;
      
      &.shape-1 {
        width: 60px;
        height: 60px;
        top: 20px;
        right: 40px;
        animation-delay: 0s;
      }
      
      &.shape-2 {
        width: 40px;
        height: 40px;
        bottom: 30px;
        right: 80px;
        animation-delay: 2s;
      }
      
      &.shape-3 {
        width: 30px;
        height: 30px;
        top: 60px;
        right: 120px;
        animation-delay: 4s;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 配置卡片 */
.config-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  height: 100%;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 12px 40px rgba(102, 126, 234, 0.15);
    transform: translateY(-2px);
  }
  
  .card-header {
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .card-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    
    .form-item {
      label {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #718096;
        margin-bottom: 8px;
        
        .label-icon {
          width: 16px;
          height: 16px;
          opacity: 0.6;
        }
      }
      
      input, select {
        width: 100%;
        padding: 12px 16px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 14px;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        &::placeholder {
          color: #a0aec0;
        }
      }
    }
  }
}

/* 题目类型卡片 */
.type-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 2px solid #e9ecef;
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
  /* 🔧 移除 cursor: pointer，避免干扰按钮点击 */
  
  &:hover {
    background: linear-gradient(135deg, #ffffff 0%, #f0f4ff 100%);
    border-color: #667eea;
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

/* 居中输入框 */
.centered-input {
  :deep(.el-input__inner) {
    text-align: center;
  }
  
  /* 隐藏数字输入框的上下箭头 */
  :deep(input[type="number"]::-webkit-inner-spin-button),
  :deep(input[type="number"]::-webkit-outer-spin-button) {
    -webkit-appearance: none;
    margin: 0;
  }
  
  :deep(input[type="number"]) {
    -moz-appearance: textfield;
  }
}

/* 居中输入框（带padding，用于对齐） */
.centered-input-with-padding {
  :deep(.el-input__inner) {
    text-align: center;
    padding-left: 50px;
    padding-right: 50px;
  }
}

/* 居中数字输入框 */
.centered-input-number {
  :deep(.el-input__inner) {
    text-align: center;
  }
}

/* 知识点选择卡片 */
.knowledge-point-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s ease;
  
  &:hover {
    background: linear-gradient(135deg, #ffffff 0%, #f0f4ff 100%);
    border-color: #667eea;
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

/* 历史考核弹窗样式 */
.history-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
    max-height: 70vh;
    overflow-y: auto;
  }
}

/* 历史考核视图页面样式 */
.history-view-container {
  width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
  animation: fadeIn 0.5s ease-in-out;
  
  .history-view-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    
    .header-title {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;
      justify-content: center;
      
      .history-icon {
        width: 32px;
        height: 32px;
      }
      
      h2 {
        margin: 0;
        font-size: 24px;
        font-weight: 700;
        color: #2d3748;
      }
    }
    
    .history-count {
      min-width: 120px;
      text-align: right;
    }
  }
  
  .history-exams {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

/* 题目类型 */
.question-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .question-type {
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 16px;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .type-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .type-info {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .type-icon {
          width: 20px;
          height: 20px;
          opacity: 0.7;
        }
        
        .type-name {
          font-size: 16px;
          font-weight: 600;
          color: #2d3748;
        }
      }
      
      .type-controls {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .count-input {
          width: 60px;
          padding: 8px 12px;
          border: 1px solid #e2e8f0;
          border-radius: 6px;
          text-align: center;
          font-size: 14px;
          
          &:focus {
            outline: none;
            border-color: #667eea;
          }
        }
        
        .unit {
          font-size: 14px;
          color: #718096;
        }
      }
    }
    
    .type-details {
      display: flex;
      gap: 16px;
      
      .score-per {
        font-size: 12px;
        color: #10b981;
        font-weight: 600;
      }
      
      .difficulty {
        font-size: 12px;
        color: #718096;
      }
    }
  }
}

/* 知识点选择 */
.knowledge-points {
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  .knowledge-point {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: white;
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-color: #667eea;
      
      .point-info .point-content .point-name {
        color: white;
      }
      
      .point-info .point-content .point-desc {
        color: rgba(255, 255, 255, 0.8);
      }
    }
    
    .point-info {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;
      
      .point-icon {
        width: 20px;
        height: 20px;
        opacity: 0.7;
      }
      
      .point-content {
        .point-name {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 4px;
        }
        
        .point-desc {
          display: block;
          font-size: 12px;
          color: #718096;
        }
      }
    }
    
    .point-weight {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .weight-input {
        width: 50px;
        padding: 6px 8px;
        border: 1px solid #e2e8f0;
        border-radius: 6px;
        text-align: center;
        font-size: 12px;
        
        &:focus {
          outline: none;
          border-color: #667eea;
        }
      }
      
      .weight-label {
        font-size: 12px;
        color: #718096;
      }
    }
  }
}

/* 生成区域 */
.generate-section {
  text-align: center;
  
  .generate-btn {
    width: 100%;
    padding: 16px 32px;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
  }
  
  .generate-tip {
    font-size: 14px;
    color: #718096;
    margin: 0;
  }
}

/* 按钮样式 */
.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  
  .btn-icon {
    width: 18px;
    height: 18px;
    opacity: 0.8;
  }
  
  &.primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
    }
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }
  }
  
  &.secondary {
    background: #f7fafc;
    color: #718096;
    border: 1px solid #e2e8f0;
    
    &:hover {
      background: #edf2f7;
      transform: translateY(-2px);
    }
  }
  
  &.success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
    }
  }
}

/* 历史考试列表 */
.history-exams {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .history-icon {
        width: 24px;
        height: 24px;
        opacity: 0.7;
      }
      
      h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
        color: #2d3748;
      }
    }
    
    .history-count {
      .el-tag {
        font-size: 12px;
        padding: 4px 8px;
      }
    }
  }
  
  .exams-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    
    .exam-item {
      background: #f7fafc;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 20px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        border-color: #667eea;
      }
      
      .exam-item-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        h4 {
          margin: 0;
          font-size: 1.1rem;
          font-weight: 600;
          color: #2d3748;
          flex: 1;
          margin-right: 12px;
        }
        
        .exam-status {
          .el-tag {
            font-size: 11px;
            padding: 2px 6px;
          }
        }
      }
      
      .exam-item-info {
        margin-bottom: 16px;
        
        .info-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          font-size: 13px;
          color: #718096;
          
          .info-icon {
            width: 14px;
            height: 14px;
            opacity: 0.6;
          }
          
          span {
            flex: 1;
          }
        }
      }
      
      .exam-item-actions {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        
        .el-button {
          font-size: 12px;
          padding: 6px 12px;
        }
      }
    }
  }
}

/* 生成的考核 */
.generated-exam {
  margin-top: 32px;
  
  .exam-card {
    background: white;
    border-radius: 20px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .exam-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .exam-icon {
          width: 32px;
          height: 32px;
          opacity: 0.7;
        }
        
        h3 {
          margin: 0;
          font-size: 1.5rem;
          font-weight: 700;
          color: #2d3748;
        }
      }
      
      .exam-info {
        display: flex;
        gap: 16px;
        
        .info-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #718096;
          
          .info-icon {
            width: 16px;
            height: 16px;
            opacity: 0.6;
          }
        }
      }
    }
    
    .exam-actions {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;
    }
    
    .questions-preview {
      .question-item {
        background: #f7fafc;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        padding: 16px;
        margin-bottom: 12px;
        
        .question-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;
          
          .question-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .question-number {
              font-size: 16px;
              font-weight: 700;
              color: #667eea;
            }
            
            .question-type-badge {
              display: flex;
              align-items: center;
              gap: 4px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
              padding: 4px 8px;
              border-radius: 6px;
              font-size: 12px;
              font-weight: 600;
              
              .badge-icon {
                width: 12px;
                height: 12px;
                opacity: 0.8;
              }
            }
          }
          
          .question-score {
            background: #e6fffa;
            color: #319795;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
          }
        }
        
        .question-content {
          font-size: 13px;
          color: #2d3748;
          margin-bottom: 12px;
          line-height: 1.6;
          white-space: pre-wrap;
          word-wrap: break-word;
          background: #ffffff;
          padding: 12px;
          border-radius: 6px;
          border: 1px solid #e2e8f0;
        }
        
        .auto-formatted-content {
          line-height: 1.6;
        }
        
        .auto-formatted-content .section {
          margin-bottom: 12px;
        }
        
        .auto-formatted-content .section:last-child {
          margin-bottom: 0;
        }
        
        .auto-formatted-content .section-title {
          margin: 0 0 8px 0;
          color: #ffffff;
          font-size: 0.95rem;
          font-weight: 600;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          padding: 6px 12px;
          border-radius: 6px;
          display: flex;
          align-items: center;
          gap: 6px;
          box-shadow: 0 2px 6px rgba(102, 126, 234, 0.25);
        }
        
        .auto-formatted-content .section-content {
          padding: 10px 12px;
          background: #f8f9fa;
          border-radius: 6px;
          border-left: 3px solid #667eea;
          line-height: 1.6;
          color: #555;
          margin-bottom: 0;
          font-size: 13px;
        }
        
        .auto-formatted-content .section-content br {
          line-height: 1.4;
        }
        
        .auto-formatted-content .list-item {
          padding: 4px 0 4px 14px;
          color: #2d3748;
          line-height: 1.6;
          position: relative;
        }
        
        .auto-formatted-content .list-item::before {
          content: '•';
          position: absolute;
          left: 0;
          color: #667eea;
          font-weight: bold;
        }
        
        .auto-formatted-content .inline-code {
          display: block;
          padding: 8px 12px;
          background: #1e1e1e;
          color: #d4d4d4;
          border-radius: 4px;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 12px;
          line-height: 1.5;
          margin: 4px 0;
          overflow-x: auto;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
        }
        
        .auto-formatted-content .example-io {
          padding: 8px 12px;
          background: #e6f7ff;
          border-left: 3px solid #1890ff;
          border-radius: 4px;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 12px;
          line-height: 1.5;
          margin: 4px 0;
          color: #0050b3;
          box-shadow: 0 1px 3px rgba(24, 144, 255, 0.1);
        }
        
        /* 保留旧样式以兼容 */
        .content-section-title {
          font-weight: 600;
          color: #2d3748;
          margin: 12px 0 6px 0;
          padding: 6px 10px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-radius: 4px;
          font-size: 14px;
        }
        
        .content-line {
          margin: 4px 0;
          padding-left: 10px;
          color: #4a5568;
          line-height: 1.6;
        }
        
        .content-code {
          margin: 4px 0;
          padding: 6px 10px;
          background: #2d3748;
          color: #f8f9fa;
          border-radius: 4px;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 12px;
          overflow-x: auto;
        }
        
        .content-example {
          margin: 4px 0;
          padding: 6px 10px;
          background: #f0fff4;
          border-left: 3px solid #48bb78;
          border-radius: 4px;
          color: #2d3748;
          font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
          font-size: 12px;
        }
        
        .question-options {
          margin-bottom: 10px;
          
          .option {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 6px 0;
            
            .option-icon {
              width: 16px;
              height: 16px;
              opacity: 0.6;
            }
            
            .option-key {
              font-weight: 600;
              color: #667eea;
              min-width: 20px;
            }
            
            .option-content {
              font-size: 14px;
              color: #4a5568;
            }
          }
        }
        
        .question-answer {
          background: #f0fff4;
          border: 1px solid #c6f6d5;
          border-radius: 6px;
          padding: 10px;
          
          .answer-header {
            display: flex;
            align-items: center;
            gap: 6px;
            margin-bottom: 6px;
            
            .answer-icon {
              width: 14px;
              height: 14px;
              opacity: 0.7;
            }
            
            strong {
              font-size: 13px;
              color: #2d3748;
            }
          }
          
          span {
            font-size: 13px;
            color: #4a5568;
            line-height: 1.5;
          }
          
          .code-answer {
            margin-top: 6px;
            
            pre {
              margin: 0;
              padding: 10px;
              background: #1e1e1e;
              border-radius: 6px;
              overflow-x: auto;
              
              code {
                font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
                font-size: 12px;
                line-height: 1.6;
                color: #d4d4d4;
                white-space: pre;
              }
            }
          }
        }
      }
    }
  }
}

/* 模态框 */
.modal-overlay {
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
  
  .modal-content {
    background: white;
    border-radius: 20px;
    max-width: 800px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    
    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      border-bottom: 1px solid #e2e8f0;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .modal-icon {
          width: 24px;
          height: 24px;
          opacity: 0.7;
        }
        
        h3 {
          margin: 0;
          font-size: 1.3rem;
          font-weight: 700;
          color: #2d3748;
        }
      }
      
      .close-btn {
        background: none;
        border: none;
        cursor: pointer;
        padding: 8px;
        border-radius: 8px;
        transition: all 0.3s ease;
        
        &:hover {
          background: #f7fafc;
        }
        
        .close-icon {
          width: 20px;
          height: 20px;
          opacity: 0.6;
        }
      }
    }
    
    .modal-body {
      padding: 24px;
      
      .exam-preview {
        .preview-header {
          text-align: center;
          margin-bottom: 32px;
          
          h2 {
            margin: 0 0 16px 0;
            font-size: 1.8rem;
            font-weight: 700;
            color: #2d3748;
          }
          
          .preview-info {
            .info-row {
              display: flex;
              align-items: center;
              justify-content: center;
              gap: 8px;
              margin-bottom: 8px;
              
              .info-icon {
                width: 16px;
                height: 16px;
                opacity: 0.6;
              }
              
              p {
                margin: 0;
                font-size: 14px;
                color: #718096;
              }
            }
          }
        }
        
        .preview-questions {
          .preview-question {
            margin-bottom: 24px;
            padding: 20px;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            
            .preview-question-header {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 12px;
              
              .preview-number {
                font-weight: 700;
                color: #667eea;
              }
              
              .preview-type {
                background: #e6fffa;
                color: #319795;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 600;
              }
              
              .preview-score {
                color: #718096;
                font-size: 14px;
              }
            }
            
            .preview-content {
              font-size: 14px;
              color: #2d3748;
              margin-bottom: 12px;
              line-height: 1.8;
              white-space: pre-wrap;
              word-wrap: break-word;
            }
            
            .preview-content .auto-formatted-content {
              line-height: 1.8;
            }
            
            .preview-content .auto-formatted-content .section {
              margin-bottom: 20px;
            }
            
            .preview-content .auto-formatted-content .section:last-child {
              margin-bottom: 0;
            }
            
            .preview-content .auto-formatted-content .section-title {
              margin: 0 0 12px 0;
              color: #ffffff;
              font-size: 1.05rem;
              font-weight: 600;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              padding: 10px 16px;
              border-radius: 8px;
              display: flex;
              align-items: center;
              gap: 8px;
              box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
            }
            
            .preview-content .auto-formatted-content .section-content {
              padding: 16px;
              background: #f8f9fa;
              border-radius: 8px;
              border-left: 4px solid #667eea;
              line-height: 1.8;
              color: #555;
              margin-bottom: 16px;
            }
            
            .preview-content .auto-formatted-content .inline-code {
              display: block;
              padding: 12px 16px;
              background: #1e1e1e;
              color: #d4d4d4;
              border-radius: 6px;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
              line-height: 1.6;
              margin: 6px 0;
              overflow-x: auto;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }
            
            .preview-content .auto-formatted-content .example-io {
              padding: 12px 16px;
              background: #e6f7ff;
              border-left: 4px solid #1890ff;
              border-radius: 6px;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
              line-height: 1.6;
              margin: 6px 0;
              color: #0050b3;
              box-shadow: 0 2px 4px rgba(24, 144, 255, 0.1);
            }
            
            .preview-content .content-section-title {
              font-weight: 600;
              color: white;
              margin: 16px 0 8px 0;
              padding: 8px 12px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              border-radius: 6px;
              font-size: 15px;
            }
            
            .preview-content .content-line {
              margin: 6px 0;
              padding-left: 12px;
              color: #4a5568;
              line-height: 1.8;
            }
            
            .preview-content .content-code {
              margin: 6px 0;
              padding: 8px 12px;
              background: #2d3748;
              color: #f8f9fa;
              border-radius: 4px;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
              overflow-x: auto;
            }
            
            .preview-content .content-example {
              margin: 6px 0;
              padding: 8px 12px;
              background: #f0fff4;
              border-left: 4px solid #48bb78;
              border-radius: 4px;
              color: #2d3748;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
            }
            
            .preview-options {
              margin-bottom: 16px;
              
              .preview-option {
                display: flex;
                gap: 8px;
                padding: 4px 0;
                
                .preview-option-key {
                  font-weight: 600;
                  color: #667eea;
                  min-width: 20px;
                }
                
                .preview-option-content {
                  font-size: 14px;
                  color: #4a5568;
                }
              }
            }
            
            .preview-answer-space {
              .answer-line {
                height: 2px;
                background: #e2e8f0;
                margin: 16px 0;
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;
    
    .header-title h1 {
      font-size: 2rem;
    }
  }
  
  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .exam-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .exam-actions {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}

.exam-generation .el-card {
  margin-bottom: 24px;
  border-radius: 12px;
}
.exam-generation .el-card__header {
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}
.exam-generation .is-selected {
  border: 2px solid #409EFF;
  background: #f0faff;
}
.main-row {
  margin-bottom: 0 !important;
}

.type-programming {
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 8px;
  font-weight: bold;
}

.type-unknown {
  background: #f6f6f6;
  color: #666666;
  border-radius: 6px;
  padding: 2px 8px;
  margin-left: 8px;
  font-weight: bold;
}

/* 课程标签页样式 */
.course-tabs {
  margin-top: 20px;
  
  .el-tabs__header {
    margin-bottom: 24px;
  }
  
  .el-tabs__item {
    font-size: 16px;
    font-weight: 600;
    padding: 12px 24px;
    
    &.is-active {
      color: #667eea;
    }
  }
  
  .el-tabs__active-bar {
    background-color: #667eea;
    height: 3px;
  }
  
  .el-tab-pane {
    padding: 0;
  }
}

/* 空状态提示 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  
  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    opacity: 0.3;
  }
  
  .empty-text {
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  .empty-desc {
    font-size: 14px;
    opacity: 0.7;
  }
}

/* 🔧 新增：不同题型的特殊样式 */

/* 判断题提示样式 */
.question-judge-hint {
  margin: 12px 0;
}

/* 填空题提示样式 */
.question-fill-hint {
  margin: 12px 0;
}

/* 简答题提示样式 */
.question-short-hint {
  margin: 12px 0;
}

/* 编程题/命令题提示样式 */
.question-coding-hint {
  margin: 12px 0;
}

/* 文本答案样式（简答题） */
.text-answer {
  margin-top: 8px;
  
  p {
    margin: 0;
    padding: 12px;
    background: #f7fafc;
    border-radius: 6px;
    color: #2d3748;
    font-size: 14px;
  }
}

/* 代码答案样式（编程题/命令题） */
.code-answer {
  margin-top: 8px;
  
  pre {
    margin: 0;
    padding: 12px;
    background: #1a202c;
    border-radius: 6px;
    overflow-x: auto;
    
    code {
      color: #68d391;
      font-family: 'Courier New', Courier, monospace;
      font-size: 13px;
      line-height: 1.6;
    }
  }
}

/* 解释说明样式 */
.question-explanation {
  margin-top: 12px;
  padding: 12px;
  background: #fffaf0;
  border: 1px solid #fbd38d;
  border-radius: 6px;
  
  .explanation-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    
    .explanation-icon {
      width: 18px;
      height: 18px;
      opacity: 0.7;
    }
    
    strong {
      color: #744210;
      font-size: 14px;
    }
  }
  
  .explanation-content {
    color: #744210;
    font-size: 14px;
    line-height: 1.6;
  }
}

/* 预览弹窗中的题型样式 */
.preview-judge-area,
.preview-fill-area,
.preview-short-area,
.preview-coding-area {
  margin: 16px 0;
  
  .judge-hint,
  .fill-hint,
  .short-hint,
  .coding-hint {
    font-size: 13px;
    color: #718096;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .answer-line {
    border-bottom: 1px solid #cbd5e0;
    height: 40px;
  }
  
  .answer-box {
    border: 1px dashed #cbd5e0;
    border-radius: 6px;
    min-height: 120px;
    background: #f7fafc;
  }
  
  .code-box {
    border: 1px dashed #cbd5e0;
    border-radius: 6px;
    min-height: 150px;
    background: #1a202c;
  }
}

// 🔧 新增：翻页式题目显示样式
.question-display-area {
  margin-top: 24px;
  
  .current-question-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 32px;
    margin-bottom: 24px;
    box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
    color: white;
    
    .question-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 2px solid rgba(255, 255, 255, 0.2);
      
      .question-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .question-number {
          font-size: 28px;
          font-weight: 800;
          color: #ffd700;
          text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }
        
        .question-type-badge {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 16px;
          background: rgba(255, 255, 255, 0.2);
          backdrop-filter: blur(10px);
          border-radius: 20px;
          font-size: 14px;
          font-weight: 600;
          
          .badge-icon {
            width: 18px;
            height: 18px;
          }
        }
      }
      
      .question-score {
        font-size: 24px;
        font-weight: 700;
        color: #ffd700;
        background: rgba(255, 255, 255, 0.2);
        padding: 8px 20px;
        border-radius: 20px;
        backdrop-filter: blur(10px);
      }
    }
    
    .question-content {
      font-size: 18px;
      line-height: 1.8;
      margin-bottom: 24px;
      padding: 20px;
      background: rgba(255, 255, 255, 0.15);
      border-radius: 12px;
      backdrop-filter: blur(10px);
    }
    
    .question-options {
      margin-bottom: 24px;
      
      .option {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 16px;
        margin-bottom: 12px;
        background: rgba(255, 255, 255, 0.15);
        border-radius: 12px;
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateX(8px);
        }
        
        .option-icon {
          width: 20px;
          height: 20px;
          margin-top: 2px;
        }
        
        .option-key {
          font-weight: 700;
          font-size: 16px;
          color: #ffd700;
        }
        
        .option-content {
          flex: 1;
          font-size: 16px;
          line-height: 1.6;
        }
      }
    }
    
    .question-judge-hint,
    .question-fill-hint,
    .question-short-hint,
    .question-coding-hint {
      margin-bottom: 24px;
      
      :deep(.el-alert) {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        backdrop-filter: blur(10px);
        
        .el-alert__title {
          color: white !important;
        }
      }
    }
    
    .question-answer {
      margin-bottom: 24px;
      padding: 20px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      backdrop-filter: blur(10px);
      
      .answer-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        
        .answer-icon {
          width: 20px;
          height: 20px;
        }
        
        strong {
          font-size: 16px;
          color: #ffd700;
        }
      }
      
      .code-answer {
        pre {
          background: rgba(0, 0, 0, 0.3);
          padding: 16px;
          border-radius: 8px;
          overflow-x: auto;
          
          code {
            color: #a8dadc;
            font-family: 'Courier New', monospace;
            font-size: 14px;
            line-height: 1.6;
          }
        }
      }
      
      .text-answer {
        p {
          margin: 0;
          font-size: 15px;
        }
      }
    }
    
    .question-explanation {
      padding: 20px;
      background: rgba(255, 255, 255, 0.15);
      border-radius: 12px;
      backdrop-filter: blur(10px);
      border-left: 4px solid #ffd700;
      
      .explanation-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        
        .explanation-icon {
          width: 20px;
          height: 20px;
        }
        
        strong {
          font-size: 16px;
          color: #ffd700;
        }
      }
      
      .explanation-content {
        font-size: 15px;
        line-height: 1.8;
      }
    }
  }
  
  .pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(240, 147, 251, 0.3);
    
    .el-button {
      padding: 12px 32px;
      font-size: 16px;
      font-weight: 600;
      border-radius: 24px;
      background: white;
      color: #f5576c;
      border: none;
      transition: all 0.3s ease;
      
      &:hover:not(:disabled) {
        background: #ffd700;
        color: #764ba2;
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(255, 215, 0, 0.4);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    .page-indicator {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: rgba(255, 255, 255, 0.3);
      backdrop-filter: blur(10px);
      border-radius: 24px;
      
      .current-page {
        font-size: 24px;
        font-weight: 800;
        color: white;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
      }
      
      .separator {
        font-size: 20px;
        color: rgba(255, 255, 255, 0.7);
        margin: 0 4px;
      }
      
      .total-pages {
        font-size: 18px;
        font-weight: 600;
        color: rgba(255, 255, 255, 0.9);
      }
    }
  }
}

/* ==================== 翻页式题目显示样式 ==================== */

/* 考试显示容器 */
.exam-display-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  overflow-y: auto;
}

.exam-gradient-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.exam-content-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}

/* 考试头部信息 */
.exam-header-info {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  text-align: center;
  animation: fadeIn 0.3s;
}

.exam-title {
  color: #667eea;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
}

.exam-meta {
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
  color: #666;
  font-size: 16px;
}

/* 当前题目卡片 */
.current-question-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  animation: fadeIn 0.3s;
}

/* 题目头部 */
.question-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.type-badge-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  color: white;
  font-weight: 500;
  font-size: 14px;
}

.badge-choice {
  background: #409EFF;
}

.badge-multiple {
  background: #67C23A;
}

.badge-judge {
  background: #00D7FF;
}

.badge-fill {
  background: #E6A23C;
}

.badge-short {
  background: #909399;
}

.badge-coding {
  background: #F56C6C;
}

.score-badge {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

/* 题目内容 */
.question-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 20px;
}

/* 选项列表 */
.options-list {
  margin: 20px 0;
}

.option-box {
  padding: 15px;
  margin-bottom: 10px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.option-box:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.option-radio,
.option-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  flex-shrink: 0;
}

.option-checkbox {
  border-radius: 4px;
}

.option-key {
  font-weight: 600;
  color: #409EFF;
  margin-right: 8px;
}

.option-text {
  flex: 1;
  color: #333;
}

.hint-text {
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

/* 判断题选项 */
.true-false-options {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.tf-option {
  flex: 1;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 500;
}

.tf-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

/* 填空题输入 */
.fill-input-area {
  margin: 20px 0;
}

.fill-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.fill-input:focus {
  outline: none;
  border-color: #667eea;
}

/* 简答题文本域 */
.short-answer-area {
  margin: 20px 0;
}

.short-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: border-color 0.3s;
}

.short-textarea:focus {
  outline: none;
  border-color: #667eea;
}

/* 编程题区域 */
.coding-area {
  margin: 20px 0;
}

.coding-display {
  .coding-section {
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fc;
    border-radius: 8px;
    border: 1px solid #e8eaf0;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f3f4f8;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }
    
    .coding-section-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 16px;
      
      .coding-icon {
        font-size: 22px;
      }
      
      .coding-section-title {
        font-size: 16px;
        font-weight: 600;
        color: #5a67d8;
      }
    }
    
    .coding-section-content {
      color: #2d3748;
      line-height: 1.8;
      font-size: 15px;
      white-space: pre-wrap;
    }
  }
  
  .coding-example {
    margin-top: 16px;
    padding: 0;
    background: transparent;
    border: none;
    
    &:not(:last-child) {
      margin-bottom: 16px;
    }
    
    .example-table {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      overflow: hidden;
    }
    
    .example-row {
      display: flex;
      border-bottom: 1px solid #e2e8f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .example-cell {
        padding: 12px 16px;
        
        &.label-cell {
          font-weight: 600;
          color: #2d3748;
          font-size: 15px;
          min-width: 80px;
          background: #f7fafc;
          border-right: 1px solid #e2e8f0;
        }
        
        &.content-cell {
          flex: 1;
          color: #2d3748;
          font-size: 14px;
          line-height: 1.6;
          
          .example-code {
            background: transparent;
            padding: 0;
            border: none;
            font-family: 'Courier New', monospace;
            font-size: 14px;
            color: #2d3748;
            margin: 0;
            overflow-x: auto;
          }
        }
      }
    }
  }
}

.coding-section h4 {
  color: #667eea;
  margin-bottom: 10px;
  font-size: 16px;
}

.coding-editor {
  margin-top: 20px;
}

.coding-editor h4 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.code-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 13px;
  font-family: 'Courier New', monospace;
  resize: vertical;
  min-height: 200px;
  background: #f5f7fa;
  transition: border-color 0.3s;
}

.code-textarea:focus {
  outline: none;
  border-color: #667eea;
}

/* 考试信息卡片 */
.exam-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.exam-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  
  .exam-info-icon {
    font-size: 28px;
  }
  
  .exam-info-title {
    font-size: 22px;
    font-weight: 700;
  }
}

.exam-info-details {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  
  .exam-info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    
    .el-icon {
      font-size: 18px;
    }
  }
}

/* 答案显示区域 */
.answer-display {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 8px;
  border-left: 4px solid #67c23a;
}

.answer-title {
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 8px;
  font-size: 16px;
}

.answer-text {
  color: #333;
  line-height: 1.6;
  margin-bottom: 12px;
}

.explanation-text {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e0e0e0;
  color: #666;
  line-height: 1.6;
}

/* 翻页控制栏 */
.pagination-bar {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.nav-btn {
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
}

.page-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
}

.page-info .current {
  font-size: 24px;
  font-weight: 800;
  color: #667eea;
}

.page-info .divider {
  color: #909399;
}

.page-info .total {
  font-size: 18px;
  font-weight: 600;
  color: #909399;
}

/* 底部控制按钮 */
.bottom-controls {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.bottom-controls .el-button {
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-gradient-bg {
    padding: 12px;
  }
  
  .exam-header-info {
    padding: 20px;
  }
  
  .exam-title {
    font-size: 22px;
  }
  
  .exam-meta {
    gap: 16px;
    font-size: 14px;
  }
  
  .current-question-card {
    padding: 20px;
  }
  
  .pagination-bar {
    flex-direction: column;
    gap: 15px;
  }
  
  .bottom-controls {
    flex-direction: column;
  }
  
  .bottom-controls .el-button {
    width: 100%;
  }
  
  .true-false-options {
    flex-direction: column;
  }
}
</style> 