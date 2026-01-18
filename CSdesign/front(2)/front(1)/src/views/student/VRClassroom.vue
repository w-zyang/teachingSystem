<template>
  <div class="vr-classroom">
    <!-- VR控制面板 -->
    <div class="vr-controls">
      <div class="vr-header">
        <h2><i class="fas fa-vr-cardboard"></i> VR 虚拟教室</h2>
        <div class="vr-status">
          <span :class="['status-indicator', { connected: vrConnected }]"></span>
          <span>{{ vrConnected ? 'VR设备已连接' : 'VR设备未连接' }}</span>
        </div>
      </div>
      
      <div class="control-buttons">
        <button 
          @click="connectVR" 
          :disabled="vrConnected"
          class="vr-btn connect-btn"
        >
          <i class="fas fa-link"></i>
          {{ vrConnected ? '已连接' : '连接VR设备' }}
        </button>
        
        <button 
          @click="enterVR" 
          :disabled="!vrConnected"
          class="vr-btn enter-btn"
        >
          <i class="fas fa-play"></i>
          进入VR教室
        </button>
        
        <button 
          @click="toggleFullscreen" 
          class="vr-btn fullscreen-btn"
        >
          <i class="fas fa-expand"></i>
          全屏模式
        </button>
      </div>
    </div>

    <!-- 3D虚拟环境 -->
    <div class="vr-environment" ref="vrContainer">
      <div class="classroom-3d">
        <!-- 虚拟教师 -->
        <div class="virtual-teacher" :class="{ active: teacherActive }">
          <div class="teacher-avatar">
            <div class="avatar-placeholder">
              <i class="fas fa-user-tie"></i>
              <span>虚拟教师</span>
            </div>
          </div>
          <div class="teacher-speech" v-if="teacherSpeech">
            <div class="speech-bubble">
              {{ teacherSpeech }}
            </div>
          </div>
        </div>

        <!-- 3D环境元素 -->
        <div class="environment-objects">
          <div class="podium">
            <div class="podium-base"></div>
            <div class="podium-top"></div>
          </div>
          
          <div class="classroom-seats">
            <div 
              v-for="seat in seats" 
              :key="seat.id"
              :class="['seat', { occupied: seat.occupied }]"
              @click="selectSeat(seat)"
            >
              <div class="seat-back"></div>
              <div class="seat-cushion"></div>
            </div>
          </div>
          
          <div class="interactive-objects">
            <div 
              v-for="object in interactiveObjects" 
              :key="object.id"
              :class="['interactive-object', object.type]"
              @click="interactWithObject(object)"
            >
              <i :class="object.icon"></i>
              <span>{{ object.name }}</span>
            </div>
          </div>
        </div>

        <!-- 虚拟黑板 -->
        <div class="virtual-blackboard">
          <div class="blackboard-surface">
            <div class="board-content">
              <h3>{{ currentLessonTitle }}</h3>
              <div class="lesson-content" v-html="currentLessonContent"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- VR交互面板 -->
    <div class="vr-interaction-panel">
      <div class="interaction-tabs">
        <button 
          v-for="tab in interactionTabs" 
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="['tab-btn', { active: activeTab === tab.id }]"
        >
          <i :class="tab.icon"></i>
          {{ tab.name }}
        </button>
      </div>
      
      <div class="interaction-content">
        <!-- 课程内容 -->
        <div v-show="activeTab === 'lesson'" class="lesson-panel">
          <h4>当前课程</h4>
          <div class="lesson-info">
            <div class="lesson-title">{{ currentLessonTitle }}</div>
            <div class="lesson-progress">
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ width: lessonProgress + '%' }"
                ></div>
              </div>
              <span>{{ lessonProgress }}% 完成</span>
            </div>
          </div>
          
          <div class="lesson-controls">
            <button @click="playLesson" class="control-btn">
              <i :class="lessonPlaying ? 'fas fa-pause' : 'fas fa-play'"></i>
              {{ lessonPlaying ? '暂停' : '播放' }}
            </button>
            <button @click="nextLesson" class="control-btn">
              <i class="fas fa-forward"></i>
              下一课
            </button>
          </div>
        </div>
        
        <!-- 互动功能 -->
        <div v-show="activeTab === 'interaction'" class="interaction-panel">
          <h4>互动功能</h4>
          <div class="interaction-options">
            <button @click="raiseHand" class="interaction-btn">
              <i class="fas fa-hand-paper"></i>
              举手发言
            </button>
            <button @click="openChat" class="interaction-btn">
              <i class="fas fa-comments"></i>
              聊天室
            </button>
            <button @click="takeNotes" class="interaction-btn">
              <i class="fas fa-sticky-note"></i>
              记笔记
            </button>
            <button @click="askQuestion" class="interaction-btn">
              <i class="fas fa-question-circle"></i>
              提问
            </button>
          </div>
        </div>
        
        <!-- 硬件拆解面板 -->
        <div v-show="activeTab === 'hardware'" class="hardware-panel">
          <h4>硬件拆解实验室</h4>
          <div class="hardware-selection">
            <div class="hardware-grid">
              <div 
                v-for="hardware in hardwareList" 
                :key="hardware.id"
                @click="selectHardware(hardware)"
                :class="['hardware-item', { active: selectedHardware?.id === hardware.id }]"
              >
                <div class="hardware-icon">
                  <i :class="hardware.icon"></i>
                </div>
                <div class="hardware-name">{{ hardware.name }}</div>
                <div class="hardware-description">{{ hardware.description }}</div>
              </div>
            </div>
          </div>
          
          <div v-if="selectedHardware" class="hardware-detail">
            <div class="hardware-3d-viewer">
              <div class="viewer-container" ref="hardwareViewer">
                <div class="hardware-model" :class="['model-' + selectedHardware.type]">
                  <div 
                    v-for="(component, index) in selectedHardware.components" 
                    :key="index"
                    @click="toggleComponent(component)"
                    :class="['component', 'component-' + component.type, { 
                      'exploded': component.exploded,
                      'selected': component.selected
                    }]"
                    :style="getComponentStyle(component)"
                  >
                    <div class="component-label" v-if="component.selected">
                      {{ component.name }}
                    </div>
                    <div class="component-info" v-if="component.showInfo">
                      <div class="info-title">{{ component.name }}</div>
                      <div class="info-specs">
                        <div v-for="spec in component.specs" :key="spec.name">
                          <strong>{{ spec.name }}:</strong> {{ spec.value }}
                        </div>
                      </div>
                      <div class="info-description">{{ component.description }}</div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="viewer-controls">
                <button @click="explodeAll" class="control-btn">
                  <i class="fas fa-expand-arrows-alt"></i>
                  {{ allExploded ? '组装' : '拆解' }}
                </button>
                <button @click="rotateModel" class="control-btn">
                  <i class="fas fa-sync-alt"></i>
                  旋转
                </button>
                <button @click="resetView" class="control-btn">
                  <i class="fas fa-undo"></i>
                  重置
                </button>
                <button @click="toggleXray" class="control-btn">
                  <i class="fas fa-search"></i>
                  {{ xrayMode ? '正常' : 'X光' }}
                </button>
              </div>
            </div>
            
            <div class="hardware-info-panel">
              <h5>{{ selectedHardware.name }} 详细信息</h5>
              <div class="info-tabs">
                <button 
                  v-for="tab in infoTabs" 
                  :key="tab.id"
                  @click="activeInfoTab = tab.id"
                  :class="['info-tab', { active: activeInfoTab === tab.id }]"
                >
                  {{ tab.name }}
                </button>
              </div>
              
              <div class="info-content">
                <div v-show="activeInfoTab === 'overview'" class="overview-info">
                  <div class="spec-grid">
                    <div v-for="spec in selectedHardware.specs" :key="spec.name" class="spec-item">
                      <div class="spec-name">{{ spec.name }}</div>
                      <div class="spec-value">{{ spec.value }}</div>
                    </div>
                  </div>
                </div>
                
                <div v-show="activeInfoTab === 'components'" class="components-info">
                  <div class="component-list">
                    <div 
                      v-for="component in selectedHardware.components" 
                      :key="component.id"
                      @click="focusComponent(component)"
                      :class="['component-item', { selected: component.selected }]"
                    >
                      <div class="component-icon">
                        <i :class="component.icon"></i>
                      </div>
                      <div class="component-details">
                        <div class="component-name">{{ component.name }}</div>
                        <div class="component-function">{{ component.function }}</div>
                      </div>
                      <div class="component-status">
                        <span :class="['status-badge', component.status]">
                          {{ component.status === 'active' ? '活跃' : '待机' }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div v-show="activeInfoTab === 'analysis'" class="analysis-info">
                  <div class="analysis-charts">
                    <div class="chart-item">
                      <div class="chart-title">性能分析</div>
                      <div class="chart-placeholder">
                        <div class="performance-bars">
                          <div v-for="metric in selectedHardware.performance" :key="metric.name" class="performance-bar">
                            <div class="metric-name">{{ metric.name }}</div>
                            <div class="metric-bar">
                              <div 
                                class="metric-fill" 
                                :style="{ width: metric.value + '%', backgroundColor: metric.color }"
                              ></div>
                            </div>
                            <div class="metric-value">{{ metric.value }}%</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 设置面板 -->
        <div v-show="activeTab === 'settings'" class="settings-panel">
          <h4>VR设置</h4>
          <div class="setting-items">
            <div class="setting-item">
              <label>音量</label>
              <input 
                type="range" 
                v-model="vrSettings.volume" 
                min="0" 
                max="100"
                class="volume-slider"
              />
              <span>{{ vrSettings.volume }}%</span>
            </div>
            
            <div class="setting-item">
              <label>视角高度</label>
              <input 
                type="range" 
                v-model="vrSettings.viewHeight" 
                min="150" 
                max="200"
                class="height-slider"
              />
              <span>{{ vrSettings.viewHeight }}cm</span>
            </div>
            
            <div class="setting-item">
              <label>移动速度</label>
              <select v-model="vrSettings.movementSpeed" class="speed-select">
                <option value="slow">慢速</option>
                <option value="normal">正常</option>
                <option value="fast">快速</option>
              </select>
            </div>
            
            <div class="setting-item">
              <label>
                <input 
                  type="checkbox" 
                  v-model="vrSettings.enableHandTracking"
                  class="checkbox"
                />
                启用手势追踪
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天室模态框 -->
    <div v-if="showChat" class="chat-modal" @click.self="closeChat">
      <div class="chat-container">
        <div class="chat-header">
          <h3>VR教室聊天</h3>
          <button @click="closeChat" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="chat-messages" ref="chatMessages">
          <div 
            v-for="message in chatMessages" 
            :key="message.id"
            :class="['message', { own: message.isOwn }]"
          >
            <div class="message-avatar">
              <img :src="message.avatar" :alt="message.username" />
            </div>
            <div class="message-content">
              <div class="message-username">{{ message.username }}</div>
              <div class="message-text">{{ message.text }}</div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <input 
            v-model="newMessage" 
            @keyup.enter="sendMessage"
            placeholder="输入消息..."
            class="message-input"
          />
          <button @click="sendMessage" class="send-btn">
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getVRCourses, 
  createVRSession, 
  joinVRClassroom, 
  sendVRMessage,
  getVRMessages,
  getVRDeviceInfo,
  getHardwareList,
  getHardwareDetail,
  explodeHardware
} from '@/api/vr'

// 响应式数据
const vrContainer = ref(null)
const chatMessages = ref([])
const vrConnected = ref(false)
const teacherActive = ref(true)
const teacherSpeech = ref('欢迎来到VR虚拟教室！今天我们将学习《数字信号处理》的基础知识。')
const currentLessonTitle = ref('数字信号处理基础')
const currentLessonContent = ref(`
  <h4>本节课程内容：</h4>
  <ul>
    <li>信号的基本概念</li>
    <li>数字信号的特点</li>
    <li>信号处理的基本流程</li>
    <li>常见的信号处理算法</li>
  </ul>
`)
const lessonProgress = ref(25)
const lessonPlaying = ref(false)
const activeTab = ref('lesson')
const showChat = ref(false)
const newMessage = ref('')

// VR设置
const vrSettings = reactive({
  volume: 75,
  viewHeight: 175,
  movementSpeed: 'normal',
  enableHandTracking: true
})

// 座位数据
const seats = ref([
  { id: 1, occupied: false, x: 100, y: 200 },
  { id: 2, occupied: true, x: 200, y: 200 },
  { id: 3, occupied: false, x: 300, y: 200 },
  { id: 4, occupied: false, x: 100, y: 300 },
  { id: 5, occupied: false, x: 200, y: 300 },
  { id: 6, occupied: false, x: 300, y: 300 }
])

// 硬件拆解相关数据
const hardwareViewer = ref(null)
const selectedHardware = ref(null)
const allExploded = ref(false)
const xrayMode = ref(false)
const activeInfoTab = ref('overview')
const modelRotation = ref(0)

// 硬件列表
const hardwareList = ref([
  {
    id: 1,
    name: '服务器机箱',
    type: 'server',
    description: '高性能服务器主机',
    icon: 'fas fa-server',
    specs: [
      { name: '型号', value: 'Dell PowerEdge R720' },
      { name: '处理器', value: 'Intel Xeon E5-2680 v2' },
      { name: '内存', value: '64GB DDR3' },
      { name: '存储', value: '2TB SAS HDD' },
      { name: '电源', value: '750W 冗余电源' }
    ],
    performance: [
      { name: 'CPU使用率', value: 65, color: '#3498db' },
      { name: '内存使用率', value: 45, color: '#e74c3c' },
      { name: '存储使用率', value: 78, color: '#f39c12' },
      { name: '网络负载', value: 32, color: '#2ecc71' }
    ],
    components: [
      {
        id: 1,
        name: '主板',
        type: 'motherboard',
        function: '连接所有硬件组件的主要电路板',
        icon: 'fas fa-microchip',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: 0, z: 0 },
        explodedPosition: { x: 0, y: -50, z: 0 },
        specs: [
          { name: '芯片组', value: 'Intel C602' },
          { name: '插槽', value: 'LGA 2011' },
          { name: '内存插槽', value: '16个DDR3' },
          { name: 'PCIe插槽', value: '8个PCIe 3.0' }
        ],
        description: '主板是计算机的核心组件，负责连接CPU、内存、存储设备和扩展卡。'
      },
      {
        id: 2,
        name: 'CPU处理器',
        type: 'cpu',
        function: '执行计算任务的核心处理单元',
        icon: 'fas fa-microchip',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 20, y: 10, z: 5 },
        explodedPosition: { x: 80, y: -30, z: 20 },
        specs: [
          { name: '核心数', value: '10核心' },
          { name: '线程数', value: '20线程' },
          { name: '基准频率', value: '2.8GHz' },
          { name: '加速频率', value: '3.6GHz' },
          { name: '缓存', value: '25MB L3' }
        ],
        description: 'Intel Xeon E5-2680 v2是一款高性能服务器处理器，适用于数据中心和高性能计算。'
      },
      {
        id: 3,
        name: '内存条',
        type: 'ram',
        function: '临时存储数据和程序的高速存储器',
        icon: 'fas fa-memory',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: -30, y: 15, z: 2 },
        explodedPosition: { x: -120, y: -20, z: 10 },
        specs: [
          { name: '容量', value: '64GB (8x8GB)' },
          { name: '类型', value: 'DDR3' },
          { name: '频率', value: '1600MHz' },
          { name: '延迟', value: 'CL11' }
        ],
        description: 'DDR3内存条提供高速数据访问，确保系统流畅运行。'
      },
      {
        id: 4,
        name: '显卡',
        type: 'gpu',
        function: '处理图形渲染和并行计算任务',
        icon: 'fas fa-tv',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 30, y: 40, z: 8 },
        explodedPosition: { x: 150, y: 60, z: 40 },
        specs: [
          { name: '型号', value: 'NVIDIA Tesla K80' },
          { name: '显存', value: '24GB GDDR5' },
          { name: 'CUDA核心', value: '4992个' },
          { name: '内存带宽', value: '480GB/s' }
        ],
        description: 'NVIDIA Tesla K80专为高性能计算和深度学习设计。'
      },
      {
        id: 5,
        name: '硬盘',
        type: 'storage',
        function: '永久存储数据和程序的存储设备',
        icon: 'fas fa-hdd',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: -40, y: 50, z: 10 },
        explodedPosition: { x: -180, y: 80, z: 50 },
        specs: [
          { name: '容量', value: '2TB' },
          { name: '类型', value: 'SAS 15K RPM' },
          { name: '接口', value: 'SAS 6Gb/s' },
          { name: '缓存', value: '128MB' }
        ],
        description: 'SAS硬盘提供高性能和可靠性，适用于企业级应用。'
      },
      {
        id: 6,
        name: '电源',
        type: 'power',
        function: '为整个系统提供稳定的电力供应',
        icon: 'fas fa-plug',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: 70, z: 15 },
        explodedPosition: { x: 0, y: 140, z: 80 },
        specs: [
          { name: '功率', value: '750W' },
          { name: '效率', value: '80+ Gold' },
          { name: '冗余', value: '1+1冗余' },
          { name: '输入电压', value: '100-240V' }
        ],
        description: '冗余电源确保系统在单个电源故障时仍能正常运行。'
      }
    ]
  },
  {
    id: 2,
    name: 'CPU处理器',
    type: 'cpu',
    description: '深度拆解CPU内部结构',
    icon: 'fas fa-microchip',
    specs: [
      { name: '制程工艺', value: '22nm' },
      { name: '晶体管数', value: '26.4亿' },
      { name: '核心面积', value: '315mm²' },
      { name: '工作温度', value: '0-85°C' }
    ],
    performance: [
      { name: '计算性能', value: 85, color: '#3498db' },
      { name: '缓存效率', value: 92, color: '#e74c3c' },
      { name: '能效比', value: 78, color: '#f39c12' },
      { name: '热管理', value: 88, color: '#2ecc71' }
    ],
    components: [
      {
        id: 1,
        name: '硅芯片',
        type: 'die',
        function: '包含所有计算核心的硅基芯片',
        icon: 'fas fa-microchip',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: 0, z: 0 },
        explodedPosition: { x: 0, y: -40, z: 0 },
        specs: [
          { name: '面积', value: '315mm²' },
          { name: '厚度', value: '0.7mm' },
          { name: '晶体管', value: '26.4亿' }
        ],
        description: '硅芯片是CPU的核心，包含所有计算单元和缓存。'
      },
      {
        id: 2,
        name: '散热器',
        type: 'cooler',
        function: '散发CPU产生的热量',
        icon: 'fas fa-fan',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: -20, z: 5 },
        explodedPosition: { x: 0, y: -80, z: 20 },
        specs: [
          { name: '材质', value: '铝合金+铜' },
          { name: '风扇转速', value: '1200-2400 RPM' },
          { name: '散热功率', value: '150W TDP' }
        ],
        description: '散热器通过风扇和散热片将CPU产生的热量散发到空气中。'
      },
      {
        id: 3,
        name: '金属触点',
        type: 'pins',
        function: '连接CPU与主板的电气接口',
        icon: 'fas fa-plug',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: 15, z: -2 },
        explodedPosition: { x: 0, y: 60, z: -20 },
        specs: [
          { name: '触点数', value: '2011个' },
          { name: '材质', value: '镀金铜' },
          { name: '间距', value: '1.016mm' }
        ],
        description: '金属触点负责CPU与主板之间的电气连接。'
      }
    ]
  },
  {
    id: 3,
    name: '显卡GPU',
    type: 'gpu',
    description: '图形处理器内部结构',
    icon: 'fas fa-tv',
    specs: [
      { name: '架构', value: 'Kepler' },
      { name: '制程', value: '28nm' },
      { name: '显存带宽', value: '480GB/s' },
      { name: '功耗', value: '300W' }
    ],
    performance: [
      { name: '渲染性能', value: 90, color: '#3498db' },
      { name: '计算性能', value: 95, color: '#e74c3c' },
      { name: '内存带宽', value: 88, color: '#f39c12' },
      { name: '能效', value: 75, color: '#2ecc71' }
    ],
    components: [
      {
        id: 1,
        name: 'GPU芯片',
        type: 'gpu_core',
        function: '图形和并行计算处理核心',
        icon: 'fas fa-microchip',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: 0, z: 0 },
        explodedPosition: { x: 0, y: -50, z: 0 },
        specs: [
          { name: 'CUDA核心', value: '4992个' },
          { name: '时钟频率', value: '562MHz' },
          { name: '加速频率', value: '875MHz' }
        ],
        description: 'GPU芯片包含数千个计算核心，专为并行计算设计。'
      },
      {
        id: 2,
        name: '显存',
        type: 'vram',
        function: '存储图形数据和纹理的高速内存',
        icon: 'fas fa-memory',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: -30, y: 10, z: 2 },
        explodedPosition: { x: -100, y: -30, z: 10 },
        specs: [
          { name: '容量', value: '24GB' },
          { name: '类型', value: 'GDDR5' },
          { name: '位宽', value: '384-bit' }
        ],
        description: 'GDDR5显存提供高带宽，满足图形渲染和计算需求。'
      },
      {
        id: 3,
        name: '散热系统',
        type: 'cooling',
        function: '冷却GPU芯片和显存',
        icon: 'fas fa-fan',
        status: 'active',
        exploded: false,
        selected: false,
        showInfo: false,
        position: { x: 0, y: -25, z: 8 },
        explodedPosition: { x: 0, y: -100, z: 40 },
        specs: [
          { name: '散热片', value: '铝合金+热管' },
          { name: '风扇', value: '双风扇' },
          { name: '散热功率', value: '300W' }
        ],
        description: '双风扇散热系统确保GPU在高负载下保持适当温度。'
      }
    ]
  }
])

// 信息标签页
const infoTabs = ref([
  { id: 'overview', name: '概述' },
  { id: 'components', name: '组件' },
  { id: 'analysis', name: '分析' }
])

// 交互对象
const interactiveObjects = ref([
  { id: 1, type: 'book', name: '教材', icon: 'fas fa-book' },
  { id: 2, type: 'calculator', name: '计算器', icon: 'fas fa-calculator' },
  { id: 3, type: 'whiteboard', name: '白板', icon: 'fas fa-chalkboard' },
  { id: 4, type: 'computer', name: '电脑', icon: 'fas fa-desktop' }
])

// 交互标签
const interactionTabs = [
  { id: 'lesson', name: '课程', icon: 'fas fa-play-circle' },
  { id: 'interaction', name: '互动', icon: 'fas fa-users' },
  { id: 'hardware', name: '硬件拆解', icon: 'fas fa-microchip' },
  { id: 'settings', name: '设置', icon: 'fas fa-cog' }
]

// 初始化聊天消息
chatMessages.value = [
  {
    id: 1,
    username: '系统',
    text: '欢迎进入VR虚拟教室！',
    timestamp: new Date(),
    avatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiMzNDk4ZGIiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4PSI4IiB5PSI4Ij4KPHBhdGggZD0iTTEyIDEyQzE0LjIwOTEgMTIgMTYgMTAuMjA5MSAxNiA4QzE2IDUuNzkwODYgMTQuMjA5MSA0IDEyIDRDOS43OTA4NiA0IDggNS43OTA4NiA4IDhDOCAxMC4yMDkxIDkuNzkwODYgMTIgMTIgMTJaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMTIgMTRDOC42ODYyOSAxNCA2IDE2LjY4NjMgNiAyMFYyMkgxOFYyMEMxOCAxNi42ODYzIDE1LjMxMzcgMTQgMTIgMTRaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4KPC9zdmc+',
    isOwn: false
  }
]

// 方法
const connectVR = async () => {
  try {
    // 检查WebXR支持
    if (!navigator.xr) {
      ElMessage.warning('您的浏览器不支持WebXR，请使用最新版Chrome或Edge浏览器')
      return
    }

    // 请求VR会话
    const session = await navigator.xr.requestSession('immersive-vr', {
      requiredFeatures: ['local-floor'],
      optionalFeatures: ['hand-tracking']
    })
    
    vrConnected.value = true
    ElMessage.success('VR设备连接成功！')
    
    // 设置VR会话监听器
    session.addEventListener('end', () => {
      vrConnected.value = false
      ElMessage.info('VR会话已结束')
    })
    
  } catch (error) {
    console.error('VR连接失败:', error)
    
    // 模拟连接成功（用于演示）
    vrConnected.value = true
    ElMessage.success('VR设备连接成功！（模拟模式）')
  }
}

const enterVR = () => {
  if (!vrConnected.value) {
    ElMessage.warning('请先连接VR设备')
    return
  }
  
  // 进入全屏模式
  if (vrContainer.value.requestFullscreen) {
    vrContainer.value.requestFullscreen()
  }
  
  // 启动VR体验
  startVRExperience()
  ElMessage.success('正在进入VR虚拟教室...')
}

const startVRExperience = () => {
  // 启动虚拟教师
  teacherActive.value = true
  
  // 开始课程
  setTimeout(() => {
    teacherSpeech.value = '让我们开始今天的学习吧！'
    playLesson()
  }, 2000)
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    vrContainer.value.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const selectSeat = (seat) => {
  if (seat.occupied) {
    ElMessage.warning('该座位已被占用')
    return
  }
  
  seat.occupied = true
  ElMessage.success('已选择座位' + seat.id)
}

const interactWithObject = (object) => {
  ElMessage.info(`正在与${object.name}互动...`)
  
  switch (object.type) {
    case 'book':
      ElMessage.success('已打开电子教材')
      break
    case 'calculator':
      ElMessage.success('已启动计算器')
      break
    case 'whiteboard':
      ElMessage.success('已打开虚拟白板')
      break
    case 'computer':
      ElMessage.success('已启动虚拟电脑')
      break
  }
}

const playLesson = () => {
  lessonPlaying.value = !lessonPlaying.value
  
  if (lessonPlaying.value) {
    // 模拟课程进度
    const progressInterval = setInterval(() => {
      if (lessonProgress.value < 100) {
        lessonProgress.value += 1
      } else {
        clearInterval(progressInterval)
        lessonPlaying.value = false
        ElMessage.success('课程播放完成！')
      }
    }, 1000)
  }
}

const nextLesson = () => {
  lessonProgress.value = 0
  currentLessonTitle.value = '数字信号处理进阶'
  currentLessonContent.value = `
    <h4>进阶课程内容：</h4>
    <ul>
      <li>傅里叶变换</li>
      <li>数字滤波器设计</li>
      <li>频域分析</li>
      <li>实际应用案例</li>
    </ul>
  `
  teacherSpeech.value = '现在我们进入进阶课程的学习！'
  ElMessage.success('已切换到下一课程')
}

const raiseHand = () => {
  ElMessage.success('您已举手，等待老师回应...')
  teacherSpeech.value = '看到你的举手了，请说出你的问题。'
}

const openChat = () => {
  showChat.value = true
}

const closeChat = () => {
  showChat.value = false
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return
  
  const message = {
    id: chatMessages.value.length + 1,
    username: '我',
    text: newMessage.value,
    timestamp: new Date(),
          avatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiMzNDk4ZGIiLz4KPHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4PSI4IiB5PSI4Ij4KPHBhdGggZD0iTTEyIDEyQzE0LjIwOTEgMTIgMTYgMTAuMjA5MSAxNiA4QzE2IDUuNzkwODYgMTQuMjA5MSA0IDEyIDRDOS43OTA4NiA0IDggNS43OTA4NiA4IDhDOCAxMC4yMDkxIDkuNzkwODYgMTIgMTIgMTJaIiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMTIgMTRDOC42ODYyOSAxNCA2IDE2LjY4NjMgNiAyMFYyMkgxOFYyMEMxOCAxNi42ODYzIDE1LjMxMzcgMTQgMTIgMTRaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4KPC9zdmc+',
    isOwn: true
  }
  
  chatMessages.value.push(message)
  newMessage.value = ''
  
  // 滚动到底部
  nextTick(() => {
    const messagesEl = document.querySelector('.chat-messages')
    if (messagesEl) {
      messagesEl.scrollTop = messagesEl.scrollHeight
    }
  })
}

const takeNotes = () => {
  ElMessage.success('已打开虚拟笔记本')
}

const askQuestion = () => {
  ElMessageBox.prompt('请输入您的问题', '提问', {
    confirmButtonText: '提交',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    if (value) {
      ElMessage.success('问题已提交，老师会尽快回答')
      teacherSpeech.value = `关于"${value}"这个问题，让我来为大家解答...`
    }
  }).catch(() => {
    // 用户取消
  })
}

// 硬件拆解相关函数
const selectHardware = (hardware) => {
  selectedHardware.value = hardware
  // 重置所有组件状态
  hardware.components.forEach(component => {
    component.exploded = false
    component.selected = false
    component.showInfo = false
  })
  allExploded.value = false
  xrayMode.value = false
  ElMessage.success(`已选择 ${hardware.name}`)
}

const toggleComponent = (component) => {
  // 取消其他组件的选择
  if (selectedHardware.value) {
    selectedHardware.value.components.forEach(comp => {
      if (comp.id !== component.id) {
        comp.selected = false
        comp.showInfo = false
      }
    })
  }
  
  component.selected = !component.selected
  component.showInfo = component.selected
  
  if (component.selected) {
    ElMessage.success(`已选择组件: ${component.name}`)
  }
}

const focusComponent = (component) => {
  // 取消其他组件的选择
  if (selectedHardware.value) {
    selectedHardware.value.components.forEach(comp => {
      comp.selected = false
      comp.showInfo = false
    })
  }
  
  component.selected = true
  component.showInfo = true
  
  // 如果组件已拆解，则聚焦到拆解位置
  if (component.exploded) {
    component.selected = true
  }
  
  ElMessage.success(`正在查看组件: ${component.name}`)
}

const explodeAll = () => {
  if (!selectedHardware.value) return
  
  allExploded.value = !allExploded.value
  
  selectedHardware.value.components.forEach(component => {
    component.exploded = allExploded.value
  })
  
  ElMessage.success(allExploded.value ? '硬件已拆解' : '硬件已组装')
}

const rotateModel = () => {
  modelRotation.value = (modelRotation.value + 45) % 360
  
  if (hardwareViewer.value) {
    hardwareViewer.value.style.transform = `rotateY(${modelRotation.value}deg)`
  }
  
  ElMessage.success('模型已旋转')
}

const resetView = () => {
  if (!selectedHardware.value) return
  
  // 重置所有组件状态
  selectedHardware.value.components.forEach(component => {
    component.exploded = false
    component.selected = false
    component.showInfo = false
  })
  
  allExploded.value = false
  xrayMode.value = false
  modelRotation.value = 0
  
  if (hardwareViewer.value) {
    hardwareViewer.value.style.transform = 'rotateY(0deg)'
  }
  
  ElMessage.success('视图已重置')
}

const toggleXray = () => {
  xrayMode.value = !xrayMode.value
  
  if (hardwareViewer.value) {
    if (xrayMode.value) {
      hardwareViewer.value.classList.add('xray-mode')
    } else {
      hardwareViewer.value.classList.remove('xray-mode')
    }
  }
  
  ElMessage.success(xrayMode.value ? '已开启X光模式' : '已关闭X光模式')
}

const getComponentStyle = (component) => {
  const position = component.exploded ? component.explodedPosition : component.position
  
  return {
    transform: `translate3d(${position.x}px, ${position.y}px, ${position.z}px)`,
    transition: 'transform 0.5s ease',
    opacity: xrayMode.value ? 0.7 : 1
  }
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(() => {
  // 初始化VR环境
  ElMessage.info('VR虚拟教室已加载，点击连接VR设备开始体验')
  
  // 模拟教师定时发言
  setInterval(() => {
    if (teacherActive.value && Math.random() > 0.7) {
      const speeches = [
        '大家有什么问题吗？',
        '这个知识点需要重点掌握',
        '我们来看一个实际的例子',
        '接下来我们进行互动练习'
      ]
      teacherSpeech.value = speeches[Math.floor(Math.random() * speeches.length)]
    }
  }, 10000)
})

onUnmounted(() => {
  // 清理VR资源
  if (vrConnected.value) {
    vrConnected.value = false
  }
})
</script>

<style scoped>
.vr-classroom {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* VR控制面板 */
.vr-controls {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 0 0 15px 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.vr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.vr-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.8rem;
}

.vr-status {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #e74c3c;
  transition: all 0.3s ease;
}

.status-indicator.connected {
  background: #27ae60;
  box-shadow: 0 0 10px rgba(39, 174, 96, 0.5);
}

.control-buttons {
  display: flex;
  gap: 15px;
}

.vr-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.connect-btn {
  background: #3498db;
  color: white;
}

.connect-btn:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-2px);
}

.connect-btn:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.enter-btn {
  background: #e74c3c;
  color: white;
}

.enter-btn:hover:not(:disabled) {
  background: #c0392b;
  transform: translateY(-2px);
}

.enter-btn:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.fullscreen-btn {
  background: #9b59b6;
  color: white;
}

.fullscreen-btn:hover {
  background: #8e44ad;
  transform: translateY(-2px);
}

/* 3D虚拟环境 */
.vr-environment {
  flex: 1;
  position: relative;
  overflow: hidden;
  background: linear-gradient(to bottom, #87CEEB 0%, #98FB98 100%);
}

.classroom-3d {
  width: 100%;
  height: 100%;
  position: relative;
  perspective: 1000px;
  transform-style: preserve-3d;
}

/* 虚拟教师 */
.virtual-teacher {
  position: absolute;
  top: 20%;
  left: 300px;
  transform: none;
  z-index: 10;
  transition: all 0.3s ease;
}

.virtual-teacher.active {
  animation: teacherMove 2s ease-in-out infinite alternate;
}

@keyframes teacherMove {
  0% { transform: translateX(-50%) translateY(0px); }
  100% { transform: translateX(-50%) translateY(-5px); }
}

.teacher-avatar {
  width: 200px;
  height: 300px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.teacher-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.teacher-avatar .avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  text-align: center;
  
  i {
    font-size: 64px;
    margin-bottom: 15px;
  }
}

.teacher-speech {
  position: absolute;
  top: -60px;
  left: 50%;
  transform: translateX(-50%);
  width: 300px;
}

.speech-bubble {
  background: white;
  padding: 15px;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  font-size: 0.9rem;
  line-height: 1.4;
  text-align: center;
}

.speech-bubble::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-top: 10px solid white;
}

/* 环境对象 */
.environment-objects {
  position: absolute;
  width: 100%;
  height: 100%;
}

.podium {
  position: absolute;
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 80px;
}

.podium-base {
  width: 100%;
  height: 60px;
  background: linear-gradient(45deg, #8B4513, #A0522D);
  border-radius: 8px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.podium-top {
  width: 120px;
  height: 20px;
  background: linear-gradient(45deg, #654321, #8B4513);
  border-radius: 10px;
  position: absolute;
  top: -10px;
  left: -10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.classroom-seats {
  position: absolute;
  bottom: 30%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  width: 400px;
  justify-content: center;
}

.seat {
  width: 60px;
  height: 60px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.seat:hover {
  transform: scale(1.1);
}

.seat.occupied {
  opacity: 0.5;
  cursor: not-allowed;
}

.seat-back {
  width: 100%;
  height: 70%;
  background: linear-gradient(45deg, #2c3e50, #3498db);
  border-radius: 8px 8px 0 0;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.seat-cushion {
  width: 100%;
  height: 30%;
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  border-radius: 0 0 8px 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.interactive-objects {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.interactive-object {
  background: rgba(255, 255, 255, 0.9);
  padding: 15px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.interactive-object:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* 虚拟黑板 */
.virtual-blackboard {
  position: absolute;
  top: 10%;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 250px;
  background: #2c3e50;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.blackboard-surface {
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #34495e, #2c3e50);
  padding: 20px;
  box-sizing: border-box;
}

.board-content {
  color: white;
  font-family: 'Comic Sans MS', cursive;
}

.board-content h3 {
  margin: 0 0 15px 0;
  color: #f39c12;
  text-align: center;
  font-size: 1.5rem;
}

.board-content ul {
  list-style: none;
  padding: 0;
}

.board-content li {
  margin: 8px 0;
  padding-left: 20px;
  position: relative;
  color: #ecf0f1;
}

.board-content li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #e74c3c;
  font-size: 1.2rem;
}

/* VR交互面板 */
.vr-interaction-panel {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 15px 15px 0 0;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.interaction-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ecf0f1;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-btn.active {
  background: #3498db;
  color: white;
}

.tab-btn:hover {
  transform: translateY(-2px);
}

.interaction-content {
  min-height: 150px;
}

.lesson-panel h4,
.interaction-panel h4,
.settings-panel h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.lesson-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.lesson-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.lesson-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(45deg, #3498db, #2980b9);
  transition: width 0.3s ease;
}

.lesson-controls {
  display: flex;
  gap: 10px;
}

.control-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #27ae60;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
}

.control-btn:hover {
  background: #229954;
  transform: translateY(-2px);
}

.interaction-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.interaction-btn {
  padding: 15px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
  color: #2c3e50;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
}

.interaction-btn:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.interaction-btn i {
  font-size: 1.5rem;
}

.setting-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.setting-item label {
  min-width: 80px;
  color: #2c3e50;
  font-weight: 500;
}

.volume-slider,
.height-slider {
  flex: 1;
  height: 6px;
  background: #ecf0f1;
  border-radius: 3px;
  outline: none;
  -webkit-appearance: none;
}

.volume-slider::-webkit-slider-thumb,
.height-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #3498db;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.speed-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  background: white;
}

.checkbox {
  width: 18px;
  height: 18px;
  margin-right: 8px;
}

/* 聊天室模态框 */
.chat-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.chat-container {
  background: white;
  border-radius: 15px;
  width: 400px;
  height: 500px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.chat-header {
  background: #3498db;
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  display: flex;
  gap: 10px;
}

.message.own {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message.own .message-content {
  text-align: right;
}

.message-username {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 4px;
}

.message-text {
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 18px;
  font-size: 0.9rem;
  line-height: 1.4;
}

.message.own .message-text {
  background: #3498db;
  color: white;
}

.message-time {
  font-size: 0.75rem;
  color: #aaa;
  margin-top: 4px;
}

.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  align-items: center;
}

.message-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  font-size: 0.9rem;
}

.send-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: #3498db;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.send-btn:hover {
  background: #2980b9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .vr-controls {
    padding: 15px;
  }
  
  .control-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .vr-btn {
    width: 100%;
    justify-content: center;
  }
  
  .virtual-blackboard {
    width: 300px;
    height: 200px;
  }
  
  .teacher-avatar {
    width: 150px;
    height: 225px;
  }
  
  .classroom-seats {
    width: 300px;
  }
  
  .interaction-options {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chat-container {
    width: 90%;
    height: 80%;
  }
}

/* 硬件拆解样式 */
.hardware-panel {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.hardware-selection {
  margin-bottom: 20px;
}

.hardware-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.hardware-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  backdrop-filter: blur(5px);
}

.hardware-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.hardware-item.active {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.1);
}

.hardware-icon {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #3498db;
}

.hardware-name {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 5px;
  color: #2c3e50;
}

.hardware-description {
  font-size: 0.9rem;
  color: #666;
}

.hardware-detail {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  height: 500px;
}

.hardware-3d-viewer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.viewer-container {
  flex: 1;
  position: relative;
  overflow: hidden;
  background: linear-gradient(to bottom, #2c3e50, #3498db);
  perspective: 1000px;
  transform-style: preserve-3d;
}

.hardware-model {
  width: 100%;
  height: 100%;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s ease;
}

.component {
  position: absolute;
  cursor: pointer;
  transform-style: preserve-3d;
  transition: all 0.5s ease;
}

.component-cpu {
  width: 60px;
  height: 60px;
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  border-radius: 8px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.component-motherboard {
  width: 200px;
  height: 150px;
  background: linear-gradient(45deg, #27ae60, #2ecc71);
  border-radius: 10px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.component-ram {
  width: 80px;
  height: 20px;
  background: linear-gradient(45deg, #f39c12, #e67e22);
  border-radius: 4px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
}

.component-gpu {
  width: 100px;
  height: 60px;
  background: linear-gradient(45deg, #9b59b6, #8e44ad);
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.component-storage {
  width: 80px;
  height: 40px;
  background: linear-gradient(45deg, #34495e, #2c3e50);
  border-radius: 6px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
}

.component-power {
  width: 60px;
  height: 80px;
  background: linear-gradient(45deg, #e67e22, #d35400);
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.component.selected {
  outline: 3px solid #f1c40f;
  outline-offset: 2px;
  z-index: 10;
}

.component.exploded {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.component-label {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.8rem;
  white-space: nowrap;
  z-index: 100;
}

.component-info {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 250px;
  z-index: 100;
  margin-top: 10px;
}

.info-title {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 8px;
  color: #2c3e50;
}

.info-specs {
  margin-bottom: 10px;
}

.info-specs div {
  margin-bottom: 4px;
  font-size: 0.85rem;
}

.info-description {
  font-size: 0.85rem;
  color: #666;
  line-height: 1.4;
}

.viewer-controls {
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  display: flex;
  gap: 10px;
  justify-content: center;
}

.control-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.hardware-info-panel {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 20px;
  overflow-y: auto;
  backdrop-filter: blur(10px);
}

.hardware-info-panel h5 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.info-tabs {
  display: flex;
  gap: 5px;
  margin-bottom: 15px;
}

.info-tab {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: #ecf0f1;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.info-tab.active {
  background: #3498db;
  color: white;
}

.info-tab:hover {
  background: #3498db;
  color: white;
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
}

.spec-item {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 8px;
  border-left: 4px solid #3498db;
}

.spec-name {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 4px;
}

.spec-value {
  font-weight: bold;
  color: #2c3e50;
}

.component-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.component-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.component-item:hover {
  background: #e9ecef;
}

.component-item.selected {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.1);
}

.component-icon {
  font-size: 1.5rem;
  color: #3498db;
  width: 30px;
  text-align: center;
}

.component-details {
  flex: 1;
}

.component-name {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 2px;
}

.component-function {
  font-size: 0.85rem;
  color: #666;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.performance-bars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.performance-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.metric-name {
  font-size: 0.85rem;
  color: #666;
  min-width: 80px;
}

.metric-bar {
  flex: 1;
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.metric-value {
  font-size: 0.85rem;
  font-weight: bold;
  color: #2c3e50;
  min-width: 40px;
}

.xray-mode .component {
  opacity: 0.5;
  border: 2px solid #f1c40f;
}

.xray-mode .component.selected {
  opacity: 1;
  border: 3px solid #e74c3c;
}

/* 硬件拆解动画 */
@keyframes explode {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.component.exploded {
  animation: explode 0.5s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hardware-detail {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .hardware-grid {
    grid-template-columns: 1fr;
  }
  
  .component-info {
    width: 200px;
  }
  
  .viewer-controls {
    flex-wrap: wrap;
  }
  
  .control-btn {
    font-size: 0.8rem;
    padding: 6px 12px;
  }
}
</style> 