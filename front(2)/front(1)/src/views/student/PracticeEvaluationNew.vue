<template>
  <div class="practice-evaluation-new">
    <!-- 顶部标题 -->
    <div class="page-header">
      <h1>练习评估中心</h1>
      <p>自主练习，智能评测，全面提升</p>
    </div>

    <!-- 自主练习大厅 -->
    <div class="practice-hall">
      <h2 class="section-title">
        <el-icon><Edit /></el-icon>
        自主练习大厅
      </h2>
      <div class="action-cards">
        <div class="action-card" @click="openKnowledgePractice">
          <div class="card-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <h3>知识点专练</h3>
          <p>针对特定知识点进行专项训练</p>
          <div class="card-hover-effect"></div>
        </div>

        <div class="action-card" @click="openAIGeneration">
          <div class="card-icon">
            <el-icon><MagicStick /></el-icon>
          </div>
          <h3>AI 智能组卷</h3>
          <p>AI 根据你的学习情况智能出题</p>
          <div class="card-hover-effect"></div>
        </div>

        <div class="action-card" @click="openPaperPractice">
          <div class="card-icon">
            <el-icon><Document /></el-icon>
          </div>
          <h3>套卷演练</h3>
          <p>完整试卷模拟，检验综合能力</p>
          <div class="card-hover-effect"></div>
        </div>
      </div>
    </div>

    <!-- 全局测评历史 -->
    <div class="history-section">
      <h2 class="section-title">
        <el-icon><Clock /></el-icon>
        全局测评历史
      </h2>

      <!-- 筛选器 -->
      <div class="filter-bar">
        <el-select 
          v-model="filterType" 
          placeholder="练习类型" 
          clearable
          style="width: 200px"
        >
          <el-option label="全部类型" value="" />
          <el-option label="课程测验" value="course" />
          <el-option label="知识点专练" value="knowledge" />
          <el-option label="AI 智能组卷" value="ai" />
          <el-option label="套卷演练" value="paper" />
        </el-select>

        <el-date-picker
          v-model="filterDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 320px"
        />

        <el-button type="primary" @click="applyFilter">
          <el-icon><Search /></el-icon>
          筛选
        </el-button>

        <el-button @click="resetFilter">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 历史记录表格 -->
      <el-table 
        :data="filteredHistory" 
        stripe 
        style="width: 100%"
        :default-sort="{ prop: 'completeTime', order: 'descending' }"
      >
        <el-table-column prop="name" label="练习名称" min-width="200">
          <template #default="{ row }">
            <div class="practice-name">
              <el-icon v-if="row.type === 'course'"><Notebook /></el-icon>
              <el-icon v-else-if="row.type === 'knowledge'"><Reading /></el-icon>
              <el-icon v-else-if="row.type === 'ai'"><MagicStick /></el-icon>
              <el-icon v-else><Document /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="courseName" label="关联课程" width="180">
          <template #default="{ row }">
            <span v-if="row.courseName">{{ row.courseName }}</span>
            <span v-else style="color: #9ca3af">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="completeTime" label="完成时间" width="180" sortable />

        <el-table-column prop="score" label="得分" width="120" sortable>
          <template #default="{ row }">
            <span class="score-text" :class="getScoreClass(row.score, row.totalScore)">
              {{ row.score }} / {{ row.totalScore }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="duration" label="耗时" width="100" sortable>
          <template #default="{ row }">
            {{ row.duration }} 分钟
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button type="danger" link @click="deleteRecord(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalRecords"
        />
      </div>
    </div>

    <!-- 知识点专练弹窗 -->
    <el-dialog 
      v-model="showKnowledgeDialog" 
      title="知识点专练 - 精准突破" 
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="knowledge-dialog-content">
        <el-row :gutter="20">
          <!-- 左侧：知识树 -->
          <el-col :span="14">
            <div class="knowledge-tree-section">
              <h4>选择要练习的知识点</h4>
              <el-tabs v-model="selectedSubject" @tab-change="loadKnowledgeTree">
                <el-tab-pane label="网络安全" name="network-security">
                  <el-tree
                    ref="networkTree"
                    :data="knowledgeTreeData.networkSecurity"
                    show-checkbox
                    node-key="id"
                    :props="treeProps"
                    @check-change="handleTreeCheck"
                  />
                </el-tab-pane>
                <el-tab-pane label="计算机组成原理" name="computer-organization">
                  <el-tree
                    ref="computerTree"
                    :data="knowledgeTreeData.computerOrganization"
                    show-checkbox
                    node-key="id"
                    :props="treeProps"
                    @check-change="handleTreeCheck"
                  />
                </el-tab-pane>
                <el-tab-pane label="数据结构" name="data-structure">
                  <el-tree
                    ref="dataTree"
                    :data="knowledgeTreeData.dataStructure"
                    show-checkbox
                    node-key="id"
                    :props="treeProps"
                    @check-change="handleTreeCheck"
                  />
                </el-tab-pane>
                <el-tab-pane label="Java程序设计" name="java">
                  <el-tree
                    ref="javaTree"
                    :data="knowledgeTreeData.java"
                    show-checkbox
                    node-key="id"
                    :props="treeProps"
                    @check-change="handleTreeCheck"
                  />
                </el-tab-pane>
                <el-tab-pane label="Linux系统" name="linux">
                  <el-tree
                    ref="linuxTree"
                    :data="knowledgeTreeData.linux"
                    show-checkbox
                    node-key="id"
                    :props="treeProps"
                    @check-change="handleTreeCheck"
                  />
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-col>

          <!-- 右侧：配置 -->
          <el-col :span="10">
            <div class="knowledge-config-section">
              <h4>练习配置</h4>
              
              <div class="config-item">
                <label>已选知识点</label>
                <div class="selected-points">
                  <el-tag 
                    v-for="point in selectedKnowledgePoints" 
                    :key="point"
                    closable
                    @close="removeKnowledgePoint(point)"
                    style="margin: 4px;"
                  >
                    {{ point }}
                  </el-tag>
                  <span v-if="selectedKnowledgePoints.length === 0" class="empty-hint">
                    请在左侧勾选知识点
                  </span>
                </div>
              </div>

              <div class="config-item">
                <label>题型选择</label>
                <el-checkbox-group v-model="knowledgeForm.questionTypes">
                  <el-checkbox label="choice">选择题</el-checkbox>
                  <el-checkbox label="fill">填空题</el-checkbox>
                  <el-checkbox label="short">简答题</el-checkbox>
                  <el-checkbox label="coding">编程题</el-checkbox>
                </el-checkbox-group>
              </div>

              <div class="config-item">
                <label>题目数量</label>
                <el-radio-group v-model="knowledgeForm.questionCount">
                  <el-radio-button :label="10">10题</el-radio-button>
                  <el-radio-button :label="20">20题</el-radio-button>
                  <el-radio-button :label="30">30题</el-radio-button>
                </el-radio-group>
              </div>

              <div class="config-item">
                <label>难度等级</label>
                <el-radio-group v-model="knowledgeForm.difficulty">
                  <el-radio-button label="easy">简单</el-radio-button>
                  <el-radio-button label="medium">中等</el-radio-button>
                  <el-radio-button label="hard">困难</el-radio-button>
                </el-radio-group>
              </div>

              <el-alert type="info" :closable="false" style="margin-top: 20px;">
                <template #title>
                  <div style="font-size: 13px;">
                    💡 刷题模式：做完一题立即查看解析，适合碎片化学习
                  </div>
                </template>
              </el-alert>
            </div>
          </el-col>
        </el-row>
      </div>

      <template #footer>
        <el-button @click="showKnowledgeDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="startKnowledgePractice"
          :loading="generating"
          :disabled="selectedKnowledgePoints.length === 0"
        >
          <el-icon v-if="!generating"><CaretRight /></el-icon>
          {{ generating ? '生成中...' : '开始刷题' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- AI 智能组卷弹窗 -->
    <el-dialog 
      v-model="showAIDialog" 
      title="AI 智能组卷 - 千人千面" 
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="ai-dialog-content-new">
        <div class="ai-header">
          <div class="ai-icon">
            <el-icon><MagicStick /></el-icon>
          </div>
          <p class="ai-description">
            AI 将根据您的学习情况和历史错题，为您智能生成个性化练习
          </p>
        </div>

        <el-form :model="aiForm" label-width="100px" class="ai-form">
          <el-form-item label="选择学科">
            <el-select 
              v-model="aiForm.subject" 
              placeholder="请选择学科"
              style="width: 100%"
              size="large"
              clearable
            >
              <el-option label="网络安全" value="network-security" />
              <el-option label="计算机组成原理" value="computer-organization" />
              <el-option label="数据结构" value="data-structure" />
              <el-option label="Java程序设计" value="java" />
              <el-option label="Linux系统" value="linux" />
            </el-select>
          </el-form-item>

          <el-form-item label="预期难度">
            <div class="difficulty-cards">
              <div 
                class="difficulty-card"
                :class="{ active: aiForm.difficulty === 'basic' }"
                @click="aiForm.difficulty = 'basic'"
              >
                <div class="card-icon">📚</div>
                <div class="card-title">基础巩固</div>
                <div class="card-desc">夯实基础知识</div>
              </div>
              <div 
                class="difficulty-card"
                :class="{ active: aiForm.difficulty === 'improve' }"
                @click="aiForm.difficulty = 'improve'"
              >
                <div class="card-icon">🎯</div>
                <div class="card-title">综合提升</div>
                <div class="card-desc">查漏补缺</div>
              </div>
              <div 
                class="difficulty-card"
                :class="{ active: aiForm.difficulty === 'advanced' }"
                @click="aiForm.difficulty = 'advanced'"
              >
                <div class="card-icon">🚀</div>
                <div class="card-title">拔高冲刺</div>
                <div class="card-desc">挑战高难度</div>
              </div>
            </div>
          </el-form-item>

          <el-form-item label="期望耗时">
            <el-slider 
              v-model="aiForm.duration" 
              :marks="durationMarks"
              :min="15"
              :max="60"
              :step="15"
              show-stops
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="showAIDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="startAIGeneration"
          :loading="generating"
          :disabled="!aiForm.subject"
        >
          <el-icon v-if="!generating"><MagicStick /></el-icon>
          {{ generating ? 'AI 分析中...' : '智能生成' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 套卷演练弹窗 -->
    <el-dialog 
      v-model="showPaperDialog" 
      title="套卷演练 - 全真模拟" 
      width="1000px"
      :close-on-click-modal="false"
    >
      <div class="paper-dialog-content-new">
        <el-tabs v-model="selectedPaperSubject" @tab-change="loadPapersBySubject">
          <el-tab-pane label="网络安全" name="network-security" />
          <el-tab-pane label="计算机组成原理" name="computer-organization" />
          <el-tab-pane label="数据结构" name="data-structure" />
          <el-tab-pane label="Java程序设计" name="java" />
          <el-tab-pane label="Linux系统" name="linux" />
        </el-tabs>

        <div class="paper-shelf">
          <div v-if="currentPapers.length === 0" class="empty-papers">
            <el-empty description="该学科暂无试卷" />
          </div>
          <div 
            v-for="paper in currentPapers" 
            :key="paper.id"
            class="paper-card-new"
            @click="confirmStartPaper(paper)"
          >
            <div class="paper-badge" :class="'badge-' + paper.category">
              {{ paper.categoryName }}
            </div>
            <h3 class="paper-title">{{ paper.name }}</h3>
            <div class="paper-meta">
              <div class="meta-row">
                <span class="meta-label">满分</span>
                <span class="meta-value">{{ paper.totalScore }} 分</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">时长</span>
                <span class="meta-value">{{ paper.duration }} 分钟</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">题量</span>
                <span class="meta-value">{{ paper.questionCount }} 题</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">难度</span>
                <el-tag 
                  :type="paper.difficulty === 'hard' ? 'danger' : paper.difficulty === 'medium' ? 'warning' : 'success'"
                  size="small"
                >
                  {{ getDifficultyText(paper.difficulty) }}
                </el-tag>
              </div>
            </div>
            <div class="paper-stats">
              <span>{{ paper.attemptCount }} 人已练习</span>
              <span>平均分 {{ paper.averageScore }}</span>
            </div>
            <div class="paper-action">
              <el-button type="primary" size="large">
                <el-icon><CaretRight /></el-icon>
                开始考试
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- AI 扫描动画弹窗 -->
    <el-dialog 
      v-model="showAIScanDialog" 
      title="" 
      width="500px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="ai-scan-content">
        <div class="scan-animation">
          <div class="scan-circle"></div>
          <div class="scan-line"></div>
          <el-icon class="scan-icon"><MagicStick /></el-icon>
        </div>
        <h3 class="scan-title">{{ aiScanText }}</h3>
        <div class="scan-steps">
          <div 
            v-for="(step, index) in aiScanSteps" 
            :key="index"
            class="scan-step"
            :class="{ active: currentAIScanStep >= index, completed: currentAIScanStep > index }"
          >
            <el-icon v-if="currentAIScanStep > index"><CircleCheck /></el-icon>
            <el-icon v-else-if="currentAIScanStep === index"><Loading /></el-icon>
            <el-icon v-else><Clock /></el-icon>
            <span>{{ step }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Edit, Reading, MagicStick, Document, Clock, Search, RefreshRight,
  Notebook, View, Delete, CaretRight, Star, CircleCheck, Loading
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoursesByStudentId } from '@/api/course'
import { aiAPI } from '@/api/ai'
import { getExamsByPage } from '@/api/exam'
import { getStudentPracticeHistory } from '@/api/practice'

const router = useRouter()

// 获取学生ID
const studentId = ref(localStorage.getItem('userId') || '17')

// 筛选条件
const filterType = ref('')
const filterDateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 弹窗控制
const showKnowledgeDialog = ref(false)
const showAIDialog = ref(false)
const showPaperDialog = ref(false)
const showProgressDialog = ref(false)
const showAIScanDialog = ref(false)

// 生成状态
const generating = ref(false)
const progressPercentage = ref(0)
const progressStatus = ref('')
const progressText = ref('')
const currentStep = ref(0)
const progressSteps = ['分析需求', '检索题库', '生成题目', '质量检查', '完成']

// AI 扫描动画
const aiScanText = ref('正在分析您的历史薄弱点...')
const currentAIScanStep = ref(0)
const aiScanSteps = ['分析学习数据', '定位薄弱环节', '抽取同类变式题', '智能组卷完成']

// 学生课程列表
const studentCourses = ref([])

// 知识树相关
const selectedSubject = ref('data-structure')
const selectedKnowledgePoints = ref([])
const treeProps = {
  children: 'children',
  label: 'label'
}

// Tree refs
const networkTree = ref(null)
const computerTree = ref(null)
const dataTree = ref(null)
const javaTree = ref(null)
const linuxTree = ref(null)

// 知识树数据
const knowledgeTreeData = {
  networkSecurity: [
    {
      id: 'ns-1',
      label: '第一章：网络安全概述',
      children: [
        { id: 'ns-1-1', label: '网络安全基本概念' },
        { id: 'ns-1-2', label: '网络安全威胁' },
        { id: 'ns-1-3', label: '网络安全防护体系' }
      ]
    },
    {
      id: 'ns-2',
      label: '第二章：密码学基础',
      children: [
        { id: 'ns-2-1', label: '对称加密' },
        { id: 'ns-2-2', label: '非对称加密' },
        { id: 'ns-2-3', label: '哈希函数' }
      ]
    },
    {
      id: 'ns-3',
      label: '第三章：网络攻击与防御',
      children: [
        { id: 'ns-3-1', label: 'SQL注入' },
        { id: 'ns-3-2', label: 'XSS攻击' },
        { id: 'ns-3-3', label: 'CSRF攻击' },
        { id: 'ns-3-4', label: 'DDoS攻击' }
      ]
    }
  ],
  computerOrganization: [
    {
      id: 'co-1',
      label: '第一章：计算机系统概述',
      children: [
        { id: 'co-1-1', label: '计算机发展历程' },
        { id: 'co-1-2', label: '计算机系统层次结构' },
        { id: 'co-1-3', label: '计算机性能指标' }
      ]
    },
    {
      id: 'co-2',
      label: '第二章：数据的表示和运算',
      children: [
        { id: 'co-2-1', label: '数制与编码' },
        { id: 'co-2-2', label: '定点数与浮点数' },
        { id: 'co-2-3', label: 'ALU设计' }
      ]
    },
    {
      id: 'co-3',
      label: '第三章：存储器层次结构',
      children: [
        { id: 'co-3-1', label: '存储器概述' },
        { id: 'co-3-2', label: 'Cache存储器' },
        { id: 'co-3-3', label: '虚拟存储器' }
      ]
    }
  ],
  dataStructure: [
    {
      id: 'ds-1',
      label: '第一章：绪论',
      children: [
        { id: 'ds-1-1', label: '数据结构基本概念' },
        { id: 'ds-1-2', label: '算法复杂度分析' }
      ]
    },
    {
      id: 'ds-2',
      label: '第二章：线性表',
      children: [
        { id: 'ds-2-1', label: '顺序表' },
        { id: 'ds-2-2', label: '链表' },
        { id: 'ds-2-3', label: '双向链表' },
        { id: 'ds-2-4', label: '循环链表' }
      ]
    },
    {
      id: 'ds-3',
      label: '第三章：栈和队列',
      children: [
        { id: 'ds-3-1', label: '栈的基本操作' },
        { id: 'ds-3-2', label: '栈的应用' },
        { id: 'ds-3-3', label: '队列的基本操作' },
        { id: 'ds-3-4', label: '循环队列' }
      ]
    },
    {
      id: 'ds-4',
      label: '第四章：树与二叉树',
      children: [
        { id: 'ds-4-1', label: '二叉树的定义' },
        { id: 'ds-4-2', label: '二叉树的遍历' },
        { id: 'ds-4-3', label: '二叉搜索树' },
        { id: 'ds-4-4', label: '平衡二叉树' }
      ]
    },
    {
      id: 'ds-5',
      label: '第五章：图',
      children: [
        { id: 'ds-5-1', label: '图的存储结构' },
        { id: 'ds-5-2', label: '图的遍历' },
        { id: 'ds-5-3', label: '最短路径' },
        { id: 'ds-5-4', label: '最小生成树' }
      ]
    },
    {
      id: 'ds-6',
      label: '第六章：查找',
      children: [
        { id: 'ds-6-1', label: '顺序查找' },
        { id: 'ds-6-2', label: '二分查找' },
        { id: 'ds-6-3', label: '哈希表' }
      ]
    },
    {
      id: 'ds-7',
      label: '第七章：排序',
      children: [
        { id: 'ds-7-1', label: '冒泡排序' },
        { id: 'ds-7-2', label: '快速排序' },
        { id: 'ds-7-3', label: '归并排序' },
        { id: 'ds-7-4', label: '堆排序' }
      ]
    }
  ],
  java: [
    {
      id: 'java-1',
      label: '第一章：Java基础',
      children: [
        { id: 'java-1-1', label: 'Java语言特点' },
        { id: 'java-1-2', label: '基本数据类型' },
        { id: 'java-1-3', label: '运算符与表达式' }
      ]
    },
    {
      id: 'java-2',
      label: '第二章：面向对象编程',
      children: [
        { id: 'java-2-1', label: '类与对象' },
        { id: 'java-2-2', label: '封装' },
        { id: 'java-2-3', label: '继承' },
        { id: 'java-2-4', label: '多态' }
      ]
    },
    {
      id: 'java-3',
      label: '第三章：异常处理',
      children: [
        { id: 'java-3-1', label: '异常类层次结构' },
        { id: 'java-3-2', label: 'try-catch-finally' },
        { id: 'java-3-3', label: '自定义异常' }
      ]
    },
    {
      id: 'java-4',
      label: '第四章：集合框架',
      children: [
        { id: 'java-4-1', label: 'List接口' },
        { id: 'java-4-2', label: 'Set接口' },
        { id: 'java-4-3', label: 'Map接口' }
      ]
    }
  ],
  linux: [
    {
      id: 'linux-1',
      label: '第一章：Linux基础',
      children: [
        { id: 'linux-1-1', label: 'Linux系统概述' },
        { id: 'linux-1-2', label: '文件系统结构' },
        { id: 'linux-1-3', label: '基本命令' }
      ]
    },
    {
      id: 'linux-2',
      label: '第二章：用户与权限管理',
      children: [
        { id: 'linux-2-1', label: '用户管理' },
        { id: 'linux-2-2', label: '组管理' },
        { id: 'linux-2-3', label: '文件权限' }
      ]
    },
    {
      id: 'linux-3',
      label: '第三章：进程管理',
      children: [
        { id: 'linux-3-1', label: '进程概念' },
        { id: 'linux-3-2', label: '进程控制' },
        { id: 'linux-3-3', label: '进程通信' }
      ]
    }
  ]
}

// 知识点专练表单
const knowledgeForm = ref({
  courseId: '',
  knowledgePoint: '',
  difficulty: 'medium',
  questionCount: 10,
  questionTypes: ['choice', 'fill']
})

// AI 智能组卷表单（使用 reactive，避免整体替换对象导致表单绑定异常）
const aiForm = reactive({
  subject: '',
  difficulty: 'improve',
  duration: 30
})

const durationMarks = {
  15: '15分钟',
  30: '30分钟',
  45: '45分钟',
  60: '60分钟'
}

// 套卷演练
const selectedPaperSubject = ref('data-structure')
const currentPapers = ref([])

// 历史数据
const historyData = ref([])

const totalRecords = computed(() => filteredHistory.value.length)

const filteredHistory = computed(() => {
  let result = historyData.value

  // 类型筛选
  if (filterType.value) {
    result = result.filter(item => item.type === filterType.value)
  }

  // 日期筛选
  if (filterDateRange.value && filterDateRange.value.length === 2) {
    const [start, end] = filterDateRange.value
    result = result.filter(item => {
      const itemDate = new Date(item.completeTime)
      return itemDate >= start && itemDate <= end
    })
  }

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return result.slice(startIndex, endIndex)
})

const getTypeName = (type) => {
  const map = {
    course: '课程测验',
    knowledge: '知识点专练',
    ai: 'AI 智能组卷',
    paper: '套卷演练'
  }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = {
    course: 'primary',
    knowledge: 'success',
    ai: 'warning',
    paper: 'danger'
  }
  return map[type] || 'info'
}

const getScoreClass = (score, total) => {
  const percentage = (score / total) * 100
  if (percentage >= 90) return 'score-excellent'
  if (percentage >= 80) return 'score-good'
  if (percentage >= 60) return 'score-pass'
  return 'score-fail'
}

const applyFilter = () => {
  currentPage.value = 1
  ElMessage.success('筛选已应用')
}

const resetFilter = () => {
  filterType.value = ''
  filterDateRange.value = []
  currentPage.value = 1
  ElMessage.info('筛选已重置')
}

const openKnowledgePractice = () => {
  selectedKnowledgePoints.value = []
  selectedSubject.value = 'data-structure'
  showKnowledgeDialog.value = true
  console.log('打开知识点专练弹窗')
}

const openAIGeneration = () => {
  // 重置表单字段（保持 reactive 引用不变，保证 el-form / el-select 绑定正常工作）
  aiForm.subject = ''
  aiForm.difficulty = 'improve'
  aiForm.duration = 30
  showAIDialog.value = true
  console.log('打开 AI 智能组卷弹窗', aiForm)
}

const openPaperPractice = () => {
  selectedPaperSubject.value = 'data-structure'
  showPaperDialog.value = true
  loadPapersBySubject(selectedPaperSubject.value)
  console.log('打开套卷演练弹窗')
}

// 加载知识树
const loadKnowledgeTree = (subject) => {
  console.log('切换学科:', subject)
  selectedKnowledgePoints.value = []
}

// 处理知识树勾选
const handleTreeCheck = () => {
  const treeRefMap = {
    'network-security': networkTree,
    'computer-organization': computerTree,
    'data-structure': dataTree,
    'java': javaTree,
    'linux': linuxTree
  }
  
  const currentTreeRef = treeRefMap[selectedSubject.value]
  
  if (currentTreeRef && currentTreeRef.value) {
    const checkedNodes = currentTreeRef.value.getCheckedNodes()
    selectedKnowledgePoints.value = checkedNodes
      .filter(node => !node.children || node.children.length === 0)
      .map(node => node.label)
  }
}

// 移除知识点
const removeKnowledgePoint = (point) => {
  const index = selectedKnowledgePoints.value.indexOf(point)
  if (index > -1) {
    selectedKnowledgePoints.value.splice(index, 1)
  }
}

// 开始知识点专练
const startKnowledgePractice = async () => {
  try {
    generating.value = true
    
    showKnowledgeDialog.value = false
    
    // 跳转到练习页面（刷题模式）
    router.push({
      path: '/student/practice-session',
      query: {
        subject: selectedSubject.value,
        knowledgePoints: JSON.stringify(selectedKnowledgePoints.value),
        difficulty: knowledgeForm.value.difficulty,
        questionCount: knowledgeForm.value.questionCount,
        questionTypes: JSON.stringify(knowledgeForm.value.questionTypes),
        type: 'knowledge',
        mode: 'practice' // 刷题模式：做一题看一题
      }
    })
    
  } catch (error) {
    console.error('生成练习失败:', error)
    ElMessage.error('生成练习失败，请重试')
  } finally {
    generating.value = false
  }
}

// 开始 AI 智能组卷
const startAIGeneration = async () => {
  try {
    generating.value = true
    showAIScanDialog.value = true
    currentAIScanStep.value = 0
    
    // 模拟 AI 扫描过程
    const scanSteps = [
      { text: '正在分析您的学习数据...', duration: 800 },
      { text: '正在定位薄弱环节...', duration: 1000 },
      { text: '正在抽取同类变式题...', duration: 1200 },
      { text: '智能组卷完成！', duration: 500 }
    ]
    
    for (let i = 0; i < scanSteps.length; i++) {
      aiScanText.value = scanSteps[i].text
      currentAIScanStep.value = i
      await new Promise(resolve => setTimeout(resolve, scanSteps[i].duration))
    }
    
    // 调用 AI 生成练习
    const response = await aiAPI.generatePractice({
      subject: aiForm.subject,
      difficulty: aiForm.difficulty,
      duration: aiForm.duration,
      type: 'ai',
      studentId: studentId.value,
      adaptive: true // 自适应难度
    })
    
    showAIScanDialog.value = false
    showAIDialog.value = false
    
    if (response && response.success && response.data) {
      // 跳转到练习页面
      router.push({
        path: '/student/practice-evaluation',
        query: {
          practiceId: response.data.id,
          type: 'ai',
          aiMessage: response.data.aiMessage || '本次组卷已根据您的学习情况进行优化'
        }
      })
    } else {
      ElMessage.success('智能组卷成功！')
    }
    
  } catch (error) {
    console.error('AI 组卷失败:', error)
    ElMessage.error('AI 组卷失败，请重试')
    showAIScanDialog.value = false
  } finally {
    generating.value = false
  }
}

// 按学科加载试卷
const loadPapersBySubject = (subject) => {
  const papersData = {
    'network-security': [
      {
        id: 'ns-p1',
        name: '2024年网络安全期末考试',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 100,
        duration: 120,
        questionCount: 30,
        difficulty: 'hard',
        attemptCount: 156,
        averageScore: 78
      },
      {
        id: 'ns-p2',
        name: '网络安全综合模拟卷A',
        category: 'mock',
        categoryName: '模拟试卷',
        totalScore: 100,
        duration: 90,
        questionCount: 25,
        difficulty: 'medium',
        attemptCount: 89,
        averageScore: 82
      }
    ],
    'computer-organization': [
      {
        id: 'co-p1',
        name: '2024年计算机组成原理期末真题',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 150,
        duration: 120,
        questionCount: 35,
        difficulty: 'hard',
        attemptCount: 203,
        averageScore: 85
      }
    ],
    'data-structure': [
      {
        id: 'ds-p1',
        name: '2024年数据结构期末考试',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 100,
        duration: 120,
        questionCount: 30,
        difficulty: 'hard',
        attemptCount: 245,
        averageScore: 76
      },
      {
        id: 'ds-p2',
        name: '2023年数据结构期末考试',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 100,
        duration: 120,
        questionCount: 28,
        difficulty: 'hard',
        attemptCount: 198,
        averageScore: 74
      },
      {
        id: 'ds-p3',
        name: '数据结构期中模拟卷',
        category: 'mock',
        categoryName: '模拟试卷',
        totalScore: 100,
        duration: 90,
        questionCount: 25,
        difficulty: 'medium',
        attemptCount: 167,
        averageScore: 81
      }
    ],
    'java': [
      {
        id: 'java-p1',
        name: '2024年Java程序设计期末真题',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 100,
        duration: 120,
        questionCount: 32,
        difficulty: 'hard',
        attemptCount: 189,
        averageScore: 79
      }
    ],
    'linux': [
      {
        id: 'linux-p1',
        name: '2024年Linux系统期末考试',
        category: 'real',
        categoryName: '历年真题',
        totalScore: 100,
        duration: 90,
        questionCount: 28,
        difficulty: 'medium',
        attemptCount: 134,
        averageScore: 83
      }
    ]
  }
  
  currentPapers.value = papersData[subject] || []
}

// 确认开始试卷
const confirmStartPaper = (paper) => {
  ElMessageBox.confirm(
    `
    <div style="text-align: left;">
      <h3 style="margin: 0 0 16px 0; color: #374151;">${paper.name}</h3>
      <div style="line-height: 2; color: #6b7280;">
        <p style="margin: 8px 0;"><strong>满分：</strong>${paper.totalScore} 分</p>
        <p style="margin: 8px 0;"><strong>时长：</strong>${paper.duration} 分钟</p>
        <p style="margin: 8px 0;"><strong>题量：</strong>${paper.questionCount} 题</p>
        <p style="margin: 8px 0; color: #ef4444;"><strong>⚠️ 注意：</strong>开始后将进入正式考试模式，全程计时，不可中途退出</p>
      </div>
    </div>
    `,
    '开始考试',
    {
      confirmButtonText: '开始考试',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      confirmButtonClass: 'confirm-exam-btn'
    }
  ).then(() => {
    showPaperDialog.value = false
    // 跳转到练习页面（暂时复用刷题模式承载套卷题目）
    router.push({
      path: '/student/practice-session',
      query: {
        subject: selectedPaperSubject.value,
        questionCount: paper.questionCount,
        difficulty: paper.difficulty || 'medium',
        type: 'paper',
        mode: 'practice' // 后续可以扩展为真正的考试模式
      }
    })
  }).catch(() => {})
}

// 加载学生课程
const loadStudentCourses = async () => {
  try {
    const response = await getCoursesByStudentId(studentId.value)
    if (response && response.success && response.data) {
      studentCourses.value = response.data
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const map = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return map[difficulty] || '中等'
}

// 加载练习历史
const loadPracticeHistory = async () => {
  try {
    const response = await getStudentPracticeHistory(studentId.value)
    if (response && response.success && response.data) {
      historyData.value = response.data.map(item => ({
        id: item.id,
        name: item.name || item.title,
        type: item.type || 'course',
        courseName: item.courseName,
        completeTime: item.completeTime || item.submitTime,
        score: item.score || 0,
        totalScore: item.totalScore || 100,
        duration: item.duration || 0
      }))
    }
  } catch (error) {
    console.error('加载练习历史失败:', error)
  }
}

const viewDetail = (row) => {
  ElMessage.info(`查看 ${row.name} 的详细报告`)
}

const deleteRecord = (row) => {
  ElMessageBox.confirm(
    `确定要删除练习记录"${row.name}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = historyData.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      historyData.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

onMounted(() => {
  console.log('练习评估页面加载完成')
  loadStudentCourses()
  loadPracticeHistory()
})
</script>

<style lang="scss" scoped>
.practice-evaluation-new {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    margin: 0 0 12px 0;
    font-size: 36px;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  p {
    margin: 0;
    font-size: 16px;
    color: #6b7280;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 24px;

  .el-icon {
    font-size: 28px;
    color: #667eea;
  }
}

.practice-hall {
  background: white;
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 40px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

  .action-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;

    .action-card {
      position: relative;
      background: linear-gradient(135deg, #f8fafc, #ffffff);
      border: 2px solid #e5e7eb;
      border-radius: 20px;
      padding: 40px 32px;
      text-align: center;
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      overflow: hidden;

      &:hover {
        transform: translateY(-12px);
        border-color: #667eea;
        box-shadow: 0 20px 40px rgba(102, 126, 234, 0.25);

        .card-icon {
          transform: scale(1.1) rotate(5deg);
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: white;
        }

        .card-hover-effect {
          opacity: 1;
          transform: scale(1);
        }
      }

      .card-hover-effect {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%) scale(0);
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.1), transparent);
        opacity: 0;
        transition: all 0.6s;
        pointer-events: none;
      }

      .card-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 20px;
        background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 40px;
        color: #667eea;
        transition: all 0.4s;
      }

      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
        font-weight: 700;
        color: #374151;
      }

      p {
        margin: 0;
        font-size: 14px;
        color: #6b7280;
        line-height: 1.6;
      }
    }
  }
}

.history-section {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

  .filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;
  }

  .practice-name {
    display: flex;
    align-items: center;
    gap: 8px;

    .el-icon {
      font-size: 18px;
      color: #667eea;
    }
  }

  .score-text {
    font-weight: 700;
    font-size: 15px;

    &.score-excellent {
      color: #10b981;
    }

    &.score-good {
      color: #3b82f6;
    }

    &.score-pass {
      color: #f59e0b;
    }

    &.score-fail {
      color: #ef4444;
    }
  }

  .pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .practice-hall .action-cards {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;

    .el-select,
    .el-date-picker {
      width: 100% !important;
    }
  }
}

/* 弹窗样式 */
.knowledge-dialog-content {
  .knowledge-tree-section {
    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      color: #374151;
    }

    :deep(.el-tree) {
      max-height: 400px;
      overflow-y: auto;
    }
  }

  .knowledge-config-section {
    background: #f8fafc;
    border-radius: 12px;
    padding: 20px;
    height: 100%;

    h4 {
      margin: 0 0 20px 0;
      font-size: 16px;
      color: #374151;
    }

    .config-item {
      margin-bottom: 24px;

      label {
        display: block;
        margin-bottom: 12px;
        font-size: 14px;
        font-weight: 600;
        color: #374151;
      }

      .selected-points {
        min-height: 60px;
        padding: 12px;
        background: white;
        border-radius: 8px;
        border: 1px solid #e5e7eb;

        .empty-hint {
          color: #9ca3af;
          font-size: 13px;
        }
      }
    }
  }
}

.ai-dialog-content-new {
  .ai-header {
    text-align: center;
    margin-bottom: 32px;

    .ai-icon {
      width: 80px;
      height: 80px;
      margin: 0 auto 16px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 40px;
      color: white;
      animation: pulse 2s infinite;
    }

    .ai-description {
      font-size: 15px;
      color: #6b7280;
      line-height: 1.6;
      margin: 0;
    }
  }

  .ai-form {
    .difficulty-cards {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;

      .difficulty-card {
        background: white;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        padding: 20px 12px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: #667eea;
          transform: translateY(-4px);
        }

        &.active {
          border-color: #667eea;
          background: linear-gradient(135deg, #f0f4ff, #ffffff);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
        }

        .card-icon {
          font-size: 32px;
          margin-bottom: 8px;
        }

        .card-title {
          font-size: 15px;
          font-weight: 600;
          color: #374151;
          margin-bottom: 4px;
        }

        .card-desc {
          font-size: 12px;
          color: #9ca3af;
        }
      }
    }
  }
}

.paper-dialog-content-new {
  .paper-shelf {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    max-height: 500px;
    overflow-y: auto;
    padding: 10px;

    .empty-papers {
      grid-column: 1 / -1;
      padding: 60px;
    }

    .paper-card-new {
      position: relative;
      background: white;
      border: 2px solid #e5e7eb;
      border-radius: 16px;
      padding: 24px;
      cursor: pointer;
      transition: all 0.3s;
      overflow: hidden;

      &:hover {
        border-color: #667eea;
        box-shadow: 0 12px 28px rgba(102, 126, 234, 0.25);
        transform: translateY(-8px);

        .paper-action {
          opacity: 1;
          transform: translateY(0);
        }
      }

      .paper-badge {
        position: absolute;
        top: 16px;
        right: 16px;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        color: white;

        &.badge-real {
          background: linear-gradient(135deg, #ef4444, #dc2626);
        }

        &.badge-mock {
          background: linear-gradient(135deg, #3b82f6, #2563eb);
        }
      }

      .paper-title {
        margin: 0 0 20px 0;
        font-size: 17px;
        font-weight: 700;
        color: #374151;
        line-height: 1.4;
        padding-right: 80px;
      }

      .paper-meta {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
        margin-bottom: 16px;

        .meta-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 12px;
          background: #f8fafc;
          border-radius: 8px;

          .meta-label {
            font-size: 13px;
            color: #6b7280;
          }

          .meta-value {
            font-size: 14px;
            font-weight: 600;
            color: #374151;
          }
        }
      }

      .paper-stats {
        display: flex;
        justify-content: space-between;
        padding-top: 16px;
        border-top: 1px solid #e5e7eb;
        font-size: 12px;
        color: #9ca3af;
        margin-bottom: 16px;
      }

      .paper-action {
        opacity: 0;
        transform: translateY(10px);
        transition: all 0.3s;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}

.ai-scan-content {
  text-align: center;
  padding: 40px 20px;

  .scan-animation {
    position: relative;
    width: 120px;
    height: 120px;
    margin: 0 auto 32px;

    .scan-circle {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border: 4px solid #e5e7eb;
      border-radius: 50%;
      border-top-color: #667eea;
      animation: rotate 1.5s linear infinite;
    }

    .scan-line {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 80%;
      height: 2px;
      background: linear-gradient(90deg, transparent, #667eea, transparent);
      animation: scan 2s ease-in-out infinite;
    }

    .scan-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-size: 48px;
      color: #667eea;
    }
  }

  .scan-title {
    margin: 0 0 24px 0;
    font-size: 18px;
    font-weight: 600;
    color: #374151;
  }

  .scan-steps {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .scan-step {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      background: #f8fafc;
      border-radius: 8px;
      transition: all 0.3s;

      .el-icon {
        font-size: 20px;
        color: #9ca3af;
      }

      span {
        font-size: 14px;
        color: #6b7280;
      }

      &.active {
        background: #e0e7ff;

        .el-icon {
          color: #667eea;
          animation: spin 1s linear infinite;
        }

        span {
          color: #667eea;
          font-weight: 600;
        }
      }

      &.completed {
        background: #dcfce7;

        .el-icon {
          color: #10b981;
        }

        span {
          color: #10b981;
        }
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes scan {
  0%, 100% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  50% {
    transform: translate(-50%, -50%) rotate(180deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 修复 el-select 下拉菜单问题 */
:deep(.el-select-dropdown) {
  z-index: 9999 !important;
}

:deep(.el-popper) {
  z-index: 9999 !important;
}
</style>

