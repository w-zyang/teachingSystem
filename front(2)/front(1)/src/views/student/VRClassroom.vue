<template>
  <div class="vr-lab" ref="containerEl">
    <!-- ── Three.js 全屏画布 ── -->
    <canvas ref="threeCanvas" class="three-canvas"></canvas>

    <!-- ── 顶部状态栏 ── -->
    <div class="top-bar">
      <div class="brand">
        <span class="brand-hex">⬡</span>
        <span class="brand-name">CPU 3D 拆解实验室</span>
      </div>

      <!-- 层级选择（硬件拆解 Tab 下可见） -->
      <div class="level-tabs" v-if="activeTab === 'hardware'">
        <button
          v-for="lv in levels"
          :key="lv.id"
          :class="['level-btn', { active: currentLevel === lv.id }]"
          @click="switchLevel(lv.id)"
        >
          <span class="lv-badge">L{{ lv.id }}</span>
          <span class="lv-name">{{ lv.name }}</span>
        </button>
      </div>

      <div class="top-actions">
        <button class="icon-btn" @click="resetCamera" title="重置视角">↺</button>
      </div>
    </div>

    <!-- ── 左侧导航 ── -->
    <nav class="left-nav">
      <button
        v-for="tab in mainTabs"
        :key="tab.id"
        :class="['nav-tab', { active: activeTab === tab.id }]"
        @click="switchTab(tab.id)"
        :title="tab.name"
      >
        <span class="nav-icon">{{ tab.icon }}</span>
        <span class="nav-label">{{ tab.name }}</span>
      </button>
    </nav>

    <!-- ── 右侧工具栏（硬件 Tab） ── -->
    <transition name="slide-r">
      <div class="right-tools" v-if="activeTab === 'hardware'">
        <div class="tools-title">⚙ 工具栏</div>

        <button :class="['tool-btn', { active: disassembleMode }]" @click="toggleDisassemble">
          <span class="t-ico">{{ disassembleMode ? '🔧' : '🔩' }}</span>
          <div class="t-info">
            <span class="t-name">{{ disassembleMode ? '组装模式' : '拆卸模式' }}</span>
            <span class="t-badge" :class="disassembleMode ? 'on' : 'off'">
              {{ disassembleMode ? 'ON' : 'OFF' }}
            </span>
          </div>
        </button>

        <button :class="['tool-btn', { active: xrayMode }]" @click="toggleXray">
          <span class="t-ico">⚡</span>
          <div class="t-info">
            <span class="t-name">透视扫描仪</span>
            <span class="t-badge" :class="xrayMode ? 'on' : 'off'">
              {{ xrayMode ? 'ON' : 'OFF' }}
            </span>
          </div>
        </button>

        <button :class="['tool-btn', { active: explodeMode }]" @click="toggleExplode">
          <span class="t-ico">💥</span>
          <div class="t-info">
            <span class="t-name">爆炸图视角</span>
            <span class="t-badge" :class="explodeMode ? 'on' : 'off'">
              {{ explodeMode ? 'ON' : 'OFF' }}
            </span>
          </div>
        </button>

        <button :class="['tool-btn', { active: showMonitor }]" @click="showMonitor = !showMonitor">
          <span class="t-ico">📊</span>
          <div class="t-info">
            <span class="t-name">运行监控</span>
            <span class="t-badge" :class="showMonitor ? 'on' : 'off'">
              {{ showMonitor ? 'ON' : 'OFF' }}
            </span>
          </div>
        </button>

        <!-- 拆卸步骤指引 -->
        <div class="disassemble-steps" v-if="disassembleMode">
          <div class="steps-title">📋 拆卸顺序</div>
          <div
            v-for="(step, i) in disassembleSteps[currentLevel]"
            :key="i"
            :class="['step-item', { current: disassembleStep === i, done: disassembleStep > i }]"
            @click="gotoDisassembleStep(i)"
          >
            <span class="step-num">{{ i + 1 }}</span>
            <span class="step-text">{{ step }}</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 数据流面板（数据流 Tab） ── -->
    <transition name="slide-r">
      <div class="df-panel" v-if="activeTab === 'dataflow'">
        <div class="df-title">🔁 指令执行周期演示</div>
        <p class="df-sub">
          以 <code>ADD R1, [0x1000]</code> 为例：假设内存地址 <code>[0x1000]</code> 里的数先被装入 <code>R2</code>，再执行
          <code>R1 = R1 + R2</code>，用“工厂机组流水线”的方式展示从取指到写回的全过程
        </p>

        <button :class="['df-run-btn', { running: dataFlowRunning }]" @click="toggleDataFlow">
          {{ dataFlowRunning ? '⏸ 暂停演示' : '▶ 开始演示' }}
        </button>

        <div class="df-stages">
          <div
            v-for="(s, i) in flowStages"
            :key="i"
            :class="['df-stage', { active: currentFlowStage === i, done: currentFlowStage > i }]"
          >
            <div class="df-stage-dot"></div>
            <div class="df-stage-body">
              <div class="df-stage-name">{{ s.name }}</div>
              <div class="df-stage-desc">{{ s.desc }}</div>
            </div>
          </div>
        </div>

        <div class="df-legend">
          <div v-for="s in flowStages" :key="s.name" class="df-legend-item">
            <span class="df-legend-dot" :style="{ background: s.hex }"></span>
            <span>{{ s.name }}</span>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 运行监控浮层 ── -->
    <transition name="fade-up">
      <div class="monitor-panel" v-if="showMonitor">
        <div class="mon-header">
          <span>📊 CPU 运行监控</span>
          <button @click="showMonitor = false" class="mon-close">✕</button>
        </div>
        <div class="mon-freq">
          <span class="freq-val">{{ clockFreq.toFixed(2) }} GHz</span>
          <span class="freq-info">{{ coreCount }}核 · {{ threadCount }}线程</span>
        </div>
        <div class="mon-temp-row">
          <span class="temp-label">CPU 温度</span>
          <div class="temp-bar-bg">
            <div class="temp-bar-fill" :style="{ width: Math.min(cpuTemp, 100) + '%', background: tempColor }"></div>
          </div>
          <span class="temp-val" :style="{ color: tempColor }">{{ cpuTemp }}°C</span>
        </div>
        <div v-for="m in cpuMetrics" :key="m.name" class="mon-metric">
          <div class="mon-m-hd">
            <span>{{ m.name }}</span>
            <span class="mon-m-val">{{ m.display }}</span>
          </div>
          <div class="mon-bar-bg">
            <div class="mon-bar-fill" :style="{ width: m.value + '%', background: m.color }"></div>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 组件属性面板（点击选中时） ── -->
    <transition name="slide-panel">
      <div class="comp-panel" v-if="selectedComp">
        <div class="cp-header">
          <div>
            <div class="cp-name">{{ selectedComp.name }}</div>
            <div class="cp-cat">{{ selectedComp.category }}</div>
          </div>
          <button class="cp-close" @click="deselectComp">✕</button>
        </div>
        <div class="cp-desc">{{ selectedComp.description }}</div>
        <div class="cp-specs">
          <div v-for="s in selectedComp.specs" :key="s.k" class="cp-spec">
            <span class="sk">{{ s.k }}</span>
            <span class="sv">{{ s.v }}</span>
          </div>
        </div>
        <!-- AI 问答 -->
        <div class="cp-qa">
          <div class="qa-title">🤖 向 AI 提问</div>
          <div class="qa-msgs" ref="qaMsgsEl">
            <div v-if="!qaMessages.length" class="qa-empty">
              对 <b>{{ selectedComp.name }}</b> 有什么疑问？直接问我吧。
            </div>
            <div
              v-for="(msg, i) in qaMessages"
              :key="i"
              :class="['qa-bubble', msg.role]"
            >
              <b>{{ msg.role === 'user' ? '我' : 'AI' }}：</b>
              <span>{{ msg.text }}</span>
            </div>
            <div v-if="qaLoading" class="qa-thinking">
              <span></span><span></span><span></span>
            </div>
          </div>
          <div class="qa-input-row">
            <input
              v-model="qaInput"
              :placeholder="'问问 ' + selectedComp.name + ' 的问题…'"
              @keyup.enter="sendQA"
              class="qa-input"
              :disabled="qaLoading"
            />
            <button @click="sendQA" :disabled="qaLoading" class="qa-send">↑</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- ── 提示栏 ── -->
    <div class="hint-bar" v-if="!selectedComp">
      <span>🖱 拖拽旋转 · 滚轮缩放 · 点击组件查看详情与 AI 讲解</span>
    </div>

    <!-- ── 悬浮 AI 助手 ── -->
    <div class="ai-float" :class="{ expanded: aiExpanded }" @click="aiExpanded = !aiExpanded">
      <div class="ai-ring">
        <div class="ai-waveform">
          <i v-for="n in 7" :key="n" :style="{ animationDelay: n * 0.08 + 's' }"></i>
        </div>
        <span class="ai-label">AI</span>
      </div>
      <transition name="fade">
        <div class="ai-speech" v-if="aiExpanded">{{ aiSpeech }}</div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import { ElMessage } from 'element-plus'
import { aiAPI } from '@/api/ai'

// ================================================================
// State
// ================================================================
const containerEl = ref(null)
const threeCanvas = ref(null)
const qaMsgsEl = ref(null)

const activeTab = ref('hardware')
const currentLevel = ref(1)
const disassembleMode = ref(false)
const disassembleStep = ref(-1)
const xrayMode = ref(false)
const explodeMode = ref(false)
const showMonitor = ref(false)
const selectedComp = ref(null)
const aiExpanded = ref(false)
const aiSpeech = ref('👋 你好！点击 3D 场景中的任意组件，我来帮你深度讲解它的原理与作用。')
const qaInput = ref('')
const qaMessages = ref([])
const qaLoading = ref(false)
const dataFlowRunning = ref(false)
const currentFlowStage = ref(-1)

const clockFreq = ref(3.6)
const coreCount = ref(12)
const threadCount = ref(24)
const cpuTemp = ref(68)
const tempColor = computed(() => {
  if (cpuTemp.value < 60) return '#00ff88'
  if (cpuTemp.value < 80) return '#f59e0b'
  return '#ef4444'
})
const cpuMetrics = reactive([
  { name: '计算核心利用率', value: 67, display: '67%', color: '#00d4ff' },
  { name: '缓存命中率', value: 89, display: '89%', color: '#00ff88' },
  { name: '内存带宽使用', value: 54, display: '54%', color: '#ff6b35' },
  { name: '分支预测准确率', value: 92, display: '92%', color: '#a78bfa' },
])

const mainTabs = [
  { id: 'hardware', name: '硬件拆解', icon: '🔬' },
  { id: 'dataflow', name: '数据流', icon: '⚡' },
  { id: 'overview', name: '概览', icon: '🖥' },
]

const levels = [
  { id: 1, name: '机箱级', shortName: '机箱' },
  { id: 2, name: '封装结构', shortName: '封装' },
  { id: 3, name: '逻辑结构', shortName: '逻辑' },
]

const disassembleSteps = {
  1: ['拔下 CPU 供电线 (8-pin ATX12V)', '松开水冷/风冷固定螺丝', '取下散热器', '扳动主板 CPU 底座拉杆', '垂直取出 CPU'],
  2: ['使用工具撬开 IHS 顶盖', '清除表面硅脂 (TIM)', '暴露裸露 Die 芯片', '检查基板 (Substrate) 完整性', '观察 LGA 针脚矩阵'],
  3: ['进入 Die 内部结构', '识别 L3 缓存区块', '定位 L2/L1 私有缓存', '找到 ALU 计算单元阵列', '分析控制单元 (CU) 与流水线'],
}

const flowStages = [
  {
    name: '取指 Fetch',
    desc: '像工厂“进料机”：从 L1 指令缓存中把整条 ADD R1, [0x1000] 指令字节取出，PC 指向下一条指令地址。',
    hex: '#ff66aa',
  },
  {
    name: '译码 Decode',
    desc: '像“分拣/拆箱工序”：把 ADD R1, [0x1000] 拆成简单 μOps，例如先把 [0x1000] 装入 R2，再执行 R1 = R1 + R2。',
    hex: '#ff9966',
  },
  {
    name: '执行 Execute',
    desc: '相当于“加工机组”：寄存器堆提供当前的 R1，访存通路把 [0x1000] 的数据送入 R2，ALU 计算出 R1 + R2 的结果。',
    hex: '#ff6b35',
  },
  {
    name: '写回 Writeback',
    desc: '类似“成品入库”：把运算结果写回目标寄存器 R1，并更新标志位，流水线释放，准备处理下一条指令。',
    hex: '#66ffcc',
  },
]

// ================================================================
// Three.js internal variables (not reactive for performance)
// ================================================================
let scene, camera, renderer, controls
let animFrameId = null
const threeClk = new THREE.Clock()
const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()

let currentGroup = null
const allSelectables = [] // meshes that respond to clicks
const explodables = []    // { mesh, base, offset }
const xrayMats = new Map() // mesh → original material
let fanRef = null           // fan mesh for rotation
let flowParticle = null
let flowLight = null
let flowCurve = null
let r1Block = null
let r2Block = null
let memBlock = null
let flowT = 0
let flowTimer = null
let monTimer = null

// ================================================================
// Component info data
// ================================================================
const CI = {
  // Level 1
  motherboard: {
    name: '主板 (Motherboard)',
    category: 'Level 1 · 基础架构',
    description: '主板是计算机的核心电路板，承载所有关键组件并提供相互之间的电气连接。板上集成芯片组、电源管理、PCIe 通道和 BIOS/UEFI 固件。',
    specs: [
      { k: '芯片组', v: 'Intel Z790' },
      { k: 'CPU 插槽', v: 'LGA 1700' },
      { k: '内存规格', v: 'DDR5-5600 x4' },
      { k: 'PCIe', v: '5.0 x16 + 4.0 x4' },
      { k: 'M.2 插槽', v: '5 个 (PCIe 5.0/4.0)' },
    ],
  },
  cpu_chip: {
    name: 'Intel Core i9-13900K',
    category: 'Level 1 · 中央处理器',
    description: '第13代 Intel Raptor Lake 架构旗舰处理器，采用 P+E 混合大小核设计，8 个 P核 (高性能) + 16 个 E核 (能效)，单线程最高睿频 5.8 GHz。',
    specs: [
      { k: '架构', v: 'Raptor Lake (Intel 7)' },
      { k: '核心配置', v: '8P + 16E = 24 核 32 线程' },
      { k: '基础 / 最高频率', v: '3.0 / 5.8 GHz' },
      { k: 'L3 缓存', v: '36 MB 共享' },
      { k: 'TDP / PBP', v: '125W / 253W' },
    ],
  },
  ram: {
    name: 'DDR5 内存 ×2',
    category: 'Level 1 · 系统内存',
    description: 'DDR5 SDRAM 是第五代双倍数据率同步动态随机存取内存，每个 DIMM 内置电源管理芯片 (PMIC)，支持片上 ECC，工作电压降至 1.1V。',
    specs: [
      { k: '单条容量', v: '32 GB' },
      { k: '有效频率', v: 'DDR5-5600' },
      { k: '峰值带宽', v: '89.6 GB/s (双通道)' },
      { k: '延迟时序', v: 'CL40-40-40-77' },
      { k: '工作电压', v: '1.1 V' },
    ],
  },
  cooler: {
    name: 'CPU 塔式风冷散热器',
    category: 'Level 1 · 散热系统',
    description: '通过导热铜底座将 CPU 热量传至热管，再经铝合金散热鳍片散发。高端风冷可应对 250W+ TDP，配合直接接触热管技术最大化导热效率。',
    specs: [
      { k: '热管数量', v: '6 根铜热管' },
      { k: '风扇规格', v: '140mm PWM × 2' },
      { k: '最大散热量', v: '280W TDP' },
      { k: '噪音', v: '< 25 dB(A) @ 1000rpm' },
    ],
  },
  // Level 2
  ihs: {
    name: 'IHS (集成散热顶盖)',
    category: 'Level 2 · 封装结构',
    description: 'Integrated Heat Spreader，用镀镍铜制成的金属保护盖。作用是将裸 Die 产生的热量均匀分散至更大面积，再传递给散热器，同时保护易碎的硅 Die 不受机械损伤。',
    specs: [
      { k: '材料', v: '镀镍铜' },
      { k: '功能', v: '均热 + 机械保护' },
      { k: '导热系数', v: ~400 + ' W/(m·K)' },
      { k: '厚度', v: '约 0.5 mm' },
    ],
  },
  tim: {
    name: '热界面材料 (TIM)',
    category: 'Level 2 · 导热层',
    description: 'Thermal Interface Material，填充 IHS 与 Die 之间的微观缝隙，消除空气热阻。高端 CPU 使用液态金属 TIM (导热系数 73 W/mK)，消费级常见硅脂 TIM (~10 W/mK)。',
    specs: [
      { k: 'Die 侧材料', v: '液态金属 (In/Ga 合金)' },
      { k: '导热系数', v: '10~73 W/(m·K)' },
      { k: '层厚度', v: '0.05~0.1 mm' },
    ],
  },
  die: {
    name: 'Silicon Die (硅裸片)',
    category: 'Level 2 · 核心芯片',
    description: '真正的硅芯片，所有晶体管和功能单元均以 Intel 7 工艺 (10nm ESF) 蚀刻其上，包含约 258 亿个晶体管。Die 通过焊料凸块 (Solder Bumps) 连接到有机基板。',
    specs: [
      { k: '制造工艺', v: 'Intel 7 (10nm ESF)' },
      { k: '晶体管数', v: '约 258 亿' },
      { k: 'Die 尺寸', v: '257 mm²' },
      { k: '连接方式', v: 'Flip-Chip Solder Bumps' },
    ],
  },
  substrate: {
    name: '有机基板 (Substrate)',
    category: 'Level 2 · 封装基板',
    description: '多层有机高分子载体，内含密集的铜走线，负责转换 Die 侧极细间距的凸块到主板 LGA 接触垫所需的较大间距，同时提供电源分配网络 (PDN)。',
    specs: [
      { k: '层数', v: '8~12 层铜走线' },
      { k: '材料', v: 'BT 树脂 + 玻璃纤维' },
      { k: '功能', v: '信号路由 + 供电分配' },
      { k: '线宽/间距', v: '约 10~15 μm' },
    ],
  },
  pins: {
    name: 'LGA 1700 针脚矩阵',
    category: 'Level 2 · 接口结构',
    description: 'Land Grid Array，主板一侧提供 1700 个弹性弹针，对应封装底部的金属触垫，传输供电、数据、时钟和控制信号。与 PGA 不同，针脚在主板而非 CPU 上。',
    specs: [
      { k: '针脚总数', v: 'LGA 1700' },
      { k: '针脚间距', v: '1.0 mm (部分 0.65 mm)' },
      { k: '最大电流', v: '约 400 A (全核负载)' },
      { k: '信号线路', v: 'DMI 4.0 x8 / PCIe / 内存' },
    ],
  },
  // Level 3
  alu: {
    name: 'ALU (算术逻辑单元)',
    category: 'Level 3 · 执行引擎',
    description: '执行所有整数加减乘除与位运算 (AND/OR/NOT/XOR/SHL/SHR) 的核心电路。现代超标量 CPU 内每个核心含 4 个整数 ALU，可并行执行多条独立指令 (乱序执行 OoOE)。',
    specs: [
      { k: '运算类型', v: '整数 · 逻辑 · 移位' },
      { k: '数据宽度', v: '64-bit (整数) / 512-bit (AVX-512)' },
      { k: '每周期并发', v: '最多 4 个 ALU op' },
      { k: '延迟 (ADD)', v: '1 cycle' },
    ],
  },
  cu: {
    name: '控制单元 (Control Unit)',
    category: 'Level 3 · 调度引擎',
    description: '指挥 CPU 各部件协同工作的"大脑"。解码指令流，产生控制信号驱动数据路径；负责分支预测 (Tournament Predictor)、指令调度与流水线冲突检测。',
    specs: [
      { k: '主要功能', v: '指令译码 + 微操作调度' },
      { k: '流水线深度', v: '14~19 级 (Intel Raptor)' },
      { k: '分支预测', v: 'Tournament Predictor (>97% 准确率)' },
      { k: '重排序缓冲', v: 'ROB 512 项 (i9-13900K)' },
    ],
  },
  l1: {
    name: 'L1 指令/数据缓存',
    category: 'Level 3 · 高速缓存',
    description: '最靠近执行单元的缓存，访问延迟仅 4~5 个周期。P 核配置 48KB 指令缓存 + 32KB 数据缓存，采用 8 路组相联结构，是整个存储层次中速度最快的组件。',
    specs: [
      { k: '容量 (P核)', v: '48KB I$ + 32KB D$' },
      { k: '访问延迟', v: '4~5 cycles' },
      { k: '相联度', v: '8-way set associative' },
      { k: '带宽', v: '2 × 32B/cycle (读+写)' },
    ],
  },
  l2: {
    name: 'L2 统一缓存',
    category: 'Level 3 · 高速缓存',
    description: '每个 P 核独享 2MB L2 缓存 (E 核 4MB 共享 Cluster)，作为 L1 与共享 L3 的缓冲层，访问延迟约 14 个周期，在容量与速度间取得良好平衡。',
    specs: [
      { k: '容量 (P核)', v: '2 MB per core' },
      { k: '访问延迟', v: '~14 cycles' },
      { k: '归属', v: '核心私有 (Core-private)' },
      { k: '替换策略', v: 'Adaptive Replacement' },
    ],
  },
  l3: {
    name: 'L3 末级缓存 (LLC)',
    category: 'Level 3 · 高速缓存',
    description: '36MB 全核共享末级缓存，是 CPU 与高延迟 DRAM 主内存之间的最后防线。命中 L3 避免约 100ns 的 DRAM 访问，通过 Mesh 互连网络被所有 P 核和 E 核共享访问。',
    specs: [
      { k: '总容量', v: '36 MB (P核 3MB×8 + E核 3MB×4)' },
      { k: '访问延迟', v: '~40 cycles' },
      { k: '互连方式', v: 'Ring Bus → Mesh' },
      { k: '带宽', v: '~1.5 TB/s (内部)' },
    ],
  },
  reg: {
    name: '寄存器组 (Register File)',
    category: 'Level 3 · 超高速存储',
    description: 'CPU 内部最快的存储单元，直接连接 ALU 输入输出。x86-64 架构拥有 16 个 64-bit 通用寄存器 (RAX~R15)，还包括程序计数器 RIP、标志寄存器 RFLAGS 等。',
    specs: [
      { k: '通用寄存器', v: '16 × 64-bit (x86-64)' },
      { k: '访问延迟', v: '0 cycle (组合逻辑直接连接)' },
      { k: 'PC 寄存器', v: 'RIP：指向下一条指令地址' },
      { k: '物理寄存器', v: '约 280 个物理寄存器 (重命名后)' },
    ],
  },
  fetch: {
    name: '取指单元 (Fetch Unit)',
    category: 'Level 3 · 流水线前端',
    description: '从 L1 指令缓存读取下一条 (或多条) 指令的字节流，填入指令队列 (IQ)。借助分支预测器预判跳转目标，维持流水线满负荷运转，是流水线的入口。',
    specs: [
      { k: '取指宽度', v: '最多 6 条指令/周期' },
      { k: '指令预取', v: '启用硬件预取' },
      { k: '分支预测辅助', v: 'BTB + BHT' },
    ],
  },
  decode: {
    name: '译码单元 (Decode Unit)',
    category: 'Level 3 · 流水线前端',
    description: '将 CISC 的 x86 变长指令翻译成 RISC 风格的微操作 (μOps)，每周期可译码最多 5 条复杂指令。超复杂指令通过 Microcode ROM 辅助展开为多个 μOps。',
    specs: [
      { k: '译码宽度', v: '5 条指令/周期 (前端)' },
      { k: '输出', v: 'μOps (微操作)' },
      { k: 'Microcode ROM', v: '处理超复杂 x86 指令' },
      { k: 'μOp Cache', v: 'DSB: 4096 μOps 缓冲' },
    ],
  },
  writeback: {
    name: '写回单元 (Writeback Unit)',
    category: 'Level 3 · 流水线末端',
    description: '将 ALU 或内存操作的执行结果写回目标寄存器或缓存，是经典 5 级流水线的最后一关，完成一个完整指令执行周期，同时释放执行资源以供后续指令使用。',
    specs: [
      { k: '写回目标', v: '物理寄存器文件 / L1D$' },
      { k: '时序阶段', v: 'WB Stage (流水线末端)' },
      { k: '并行写回', v: '最多 4 个结果/周期' },
    ],
  },
}

// ================================================================
// Three.js Scene Init
// ================================================================
function initScene() {
  const canvas = threeCanvas.value
  const w = canvas.clientWidth
  const h = canvas.clientHeight

  // Scene
  scene = new THREE.Scene()
  // 整体色调调亮一些：更偏蓝紫的夜景背景与更浅的雾效
  scene.background = new THREE.Color(0x10162a)
  scene.fog = new THREE.FogExp2(0x10162a, 0.028)

  // Camera
  camera = new THREE.PerspectiveCamera(60, w / h, 0.01, 200)
  camera.position.set(8, 6, 8)

  // Renderer
  renderer = new THREE.WebGLRenderer({ canvas, antialias: true, powerPreference: 'high-performance' })
  renderer.setSize(w, h)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  // 提高曝光，让场景整体更明亮
  renderer.toneMappingExposure = 1.45

  // Lights
  // 环境光稍微偏蓝、强度更高一些
  const ambient = new THREE.AmbientLight(0x5a7ab0, 0.9)
  scene.add(ambient)

  const dirLight = new THREE.DirectionalLight(0xffffff, 1.4)
  dirLight.position.set(10, 20, 10)
  dirLight.castShadow = true
  dirLight.shadow.mapSize.setScalar(2048)
  dirLight.shadow.camera.near = 0.1
  dirLight.shadow.camera.far = 50
  dirLight.shadow.camera.left = -15
  dirLight.shadow.camera.right = 15
  dirLight.shadow.camera.top = 15
  dirLight.shadow.camera.bottom = -15
  scene.add(dirLight)

  const rimLight = new THREE.DirectionalLight(0x00d4ff, 0.55)
  rimLight.position.set(-8, 5, -5)
  scene.add(rimLight)

  // Grid floor：网格颜色提亮
  const grid = new THREE.GridHelper(40, 40, 0x255a7a, 0x17233c)
  grid.position.y = -3
  scene.add(grid)

  // Stars particle field：星点颜色更接近白色且更细腻
  const starGeo = new THREE.BufferGeometry()
  const starPos = new Float32Array(3000)
  for (let i = 0; i < 3000; i++) {
    starPos[i] = (Math.random() - 0.5) * 100
  }
  starGeo.setAttribute('position', new THREE.BufferAttribute(starPos, 3))
  const starMat = new THREE.PointsMaterial({ color: 0xf2f4ff, size: 0.06 })
  scene.add(new THREE.Points(starGeo, starMat))

  // Controls
  controls = new OrbitControls(camera, canvas)
  controls.enableDamping = true
  controls.dampingFactor = 0.05
  controls.minDistance = 2
  controls.maxDistance = 40
  controls.maxPolarAngle = Math.PI * 0.85

  // Raycaster mouse
  canvas.addEventListener('click', onCanvasClick)
  canvas.addEventListener('mousemove', onMouseMove)
}

// ================================================================
// Build Level Scenes
// ================================================================
function buildScene(lv) {
  // Clear previous
  if (currentGroup) {
    scene.remove(currentGroup)
    currentGroup.traverse(child => {
      if (child.isMesh) {
        child.geometry.dispose()
        if (Array.isArray(child.material)) child.material.forEach(m => m.dispose())
        else child.material.dispose()
      }
    })
  }
  allSelectables.length = 0
  explodables.length = 0
  xrayMats.clear()
  fanRef = null
  flowCurve = null
  r1Block = null
  r2Block = null
  memBlock = null
  destroyFlowParticle()

  const group = new THREE.Group()

  if (lv === 1) buildLevel1(group)
  else if (lv === 2) buildLevel2(group)
  else buildLevel3(group)

  scene.add(group)
  currentGroup = group

  // Reset explode state
  explodeMode.value = false
  xrayMode.value = false
}

// ── helpers ──
function M(color, opts = {}) {
  return new THREE.MeshStandardMaterial({
    color,
    metalness: opts.m ?? 0.3,
    roughness: opts.r ?? 0.7,
    transparent: !!opts.transparent,
    opacity: opts.opacity ?? 1,
    wireframe: opts.wireframe ?? false,
    emissive: opts.emissive != null ? new THREE.Color(opts.emissive) : undefined,
    emissiveIntensity: opts.ei ?? 0,
  })
}

function addComp(group, geo, mat, pos, compInfo, expOffset) {
  const mesh = new THREE.Mesh(geo, mat)
  mesh.position.set(...pos)
  mesh.castShadow = true
  mesh.receiveShadow = true
  if (compInfo) {
    mesh.userData.ci = compInfo
    allSelectables.push(mesh)
  }
  if (expOffset) {
    explodables.push({
      mesh,
      base: new THREE.Vector3(...pos),
      off: new THREE.Vector3(...expOffset),
    })
  }
  group.add(mesh)
  return mesh
}

// ── Level 1: Chassis ──
function buildLevel1(g) {
  // Motherboard：颜色提亮一些，方便看清轮廓
  addComp(g, new THREE.BoxGeometry(8, 0.18, 5.5), M(0x2e7c4a, { m: 0.08, r: 0.88 }),
    [0, 0, 0], CI.motherboard, [0, -1.5, 0])

  // PCB circuit lines overlay
  const cmat = new THREE.LineBasicMaterial({ color: 0x00d4ff, transparent: true, opacity: 0.25 })
  const pts = []
  for (let i = 0; i < 30; i++) {
    const x1 = (Math.random() - 0.5) * 7.5
    const z1 = (Math.random() - 0.5) * 5
    const x2 = x1 + (Math.random() - 0.5) * 2
    const z2 = z1 + (Math.random() - 0.5) * 1
    pts.push(new THREE.Vector3(x1, 0.1, z1))
    pts.push(new THREE.Vector3(x2, 0.1, z2))
  }
  const cGeo = new THREE.BufferGeometry().setFromPoints(pts)
  g.add(new THREE.LineSegments(cGeo, cmat))

  // CPU socket pad：底座颜色也稍微提亮
  const socketPad = new THREE.Mesh(new THREE.BoxGeometry(1.6, 0.02, 1.6), M(0x1b6a4a, { m: 0.05, r: 0.8 }))
  socketPad.position.set(0, 0.1, 0)
  g.add(socketPad)

  // CPU chip
  addComp(g, new THREE.BoxGeometry(1.3, 0.14, 1.3), M(0xb8b8b8, { m: 0.75, r: 0.25 }),
    [0, 0.16, 0], CI.cpu_chip, [0, 2.5, 0])

  // CPU IHS top reflection lines
  for (let i = -2; i <= 2; i++) {
    const line = new THREE.Mesh(new THREE.BoxGeometry(0.02, 0.01, 1.25), M(0xdddddd, { m: 0.9 }))
    line.position.set(i * 0.25, 0.235, 0)
    g.add(line)
  }

  // RAM sticks ×2：内存条主体颜色偏亮绿
  for (let s = -1; s <= 1; s += 2) {
    const xPos = s * 1.7
    addComp(g, new THREE.BoxGeometry(0.2, 1.4, 3.2), M(0x2e7f3a, { m: 0.08, r: 0.8 }),
      [xPos, 0.8, 0], CI.ram, [s * 4, 0, 0])
    // chips on stick：小颗粒芯片略提亮
    for (let c = -2; c <= 2; c++) {
      const ch = new THREE.Mesh(new THREE.BoxGeometry(0.16, 0.06, 0.28), M(0x26263a))
      ch.position.set(xPos, 0.72, c * 0.62)
      g.add(ch)
    }
    // notch：卡槽也稍微亮一点
    const notch = new THREE.Mesh(new THREE.BoxGeometry(0.22, 0.25, 0.08), M(0x202040))
    notch.position.set(xPos, 0.12, 0.5)
    g.add(notch)
  }

  // Cooler base
  const coolerBase = addComp(g, new THREE.BoxGeometry(1.8, 0.1, 1.8), M(0x888888, { m: 0.8, r: 0.2 }),
    [0, 0.37, 0], CI.cooler, [0, 5, 0])

  // Heatsink fins
  for (let f = -5; f <= 5; f++) {
    const fin = new THREE.Mesh(new THREE.BoxGeometry(1.7, 0.85, 0.07), M(0xaaaaaa, { m: 0.85, r: 0.3 }))
    fin.position.set(0, 0.9, f * 0.17)
    fin.castShadow = true
    g.add(fin)
    explodables.push({ mesh: fin, base: fin.position.clone(), off: new THREE.Vector3(0, 5, 0) })
  }

  // Heat pipes (copper colored)
  for (let p = -1; p <= 1; p++) {
    const pipe = new THREE.Mesh(
      new THREE.CylinderGeometry(0.045, 0.045, 2.2, 12),
      M(0xb87333, { m: 0.9, r: 0.2 })
    )
    pipe.position.set(p * 0.35, 1.0, 0)
    pipe.castShadow = true
    g.add(pipe)
    explodables.push({ mesh: pipe, base: pipe.position.clone(), off: new THREE.Vector3(0, 5, 0) })
  }

  // Fan (circle)
  const fanBase = new THREE.Mesh(new THREE.CylinderGeometry(0.8, 0.8, 0.09, 32), M(0x222222, { m: 0.5, r: 0.5 }))
  fanBase.position.set(0, 1.42, 0)
  g.add(fanBase)
  explodables.push({ mesh: fanBase, base: fanBase.position.clone(), off: new THREE.Vector3(0, 5, 0) })

  // Fan blades group
  const fanGroup = new THREE.Group()
  fanGroup.position.set(0, 1.44, 0)
  for (let b = 0; b < 7; b++) {
    const blade = new THREE.Mesh(new THREE.BoxGeometry(0.1, 0.02, 0.55), M(0x444444, { m: 0.3 }))
    blade.rotation.y = (b / 7) * Math.PI * 2
    blade.translateZ(0.32)
    fanGroup.add(blade)
  }
  g.add(fanGroup)
  fanRef = fanGroup
  explodables.push({ mesh: fanGroup, base: fanGroup.position.clone(), off: new THREE.Vector3(0, 5, 0) })

  // 为机箱级单独加一束柔和的聚光灯，突出 CPU 区域
  const cpuSpot = new THREE.SpotLight(0xffffff, 1.4, 25, Math.PI / 5, 0.4, 1)
  cpuSpot.position.set(4, 10, 6)
  cpuSpot.target = coolerBase
  cpuSpot.castShadow = true
  g.add(cpuSpot)
  g.add(cpuSpot.target)
}

// ── Level 2: Physical Structure ──
function buildLevel2(g) {
  // Substrate bottom
  addComp(g, new THREE.BoxGeometry(3.0, 0.35, 3.0), M(0x4a2a00, { m: 0.05, r: 0.95 }),
    [0, 0, 0], CI.substrate, [0, -2.5, 0])

  // Substrate surface lines
  const slines = new THREE.LineBasicMaterial({ color: 0xd4a017, transparent: true, opacity: 0.3 })
  const spts = []
  for (let i = -5; i <= 5; i++) {
    spts.push(new THREE.Vector3(-1.4, 0.18, i * 0.28))
    spts.push(new THREE.Vector3(1.4, 0.18, i * 0.28))
    spts.push(new THREE.Vector3(i * 0.28, 0.18, -1.4))
    spts.push(new THREE.Vector3(i * 0.28, 0.18, 1.4))
  }
  g.add(new THREE.LineSegments(new THREE.BufferGeometry().setFromPoints(spts), slines))

  // Pin grid (representative gold dots)
  const pinMat = M(0xd4a017, { m: 0.9, r: 0.1 })
  // Invisible click target for pins
  const pinTarget = addComp(g, new THREE.BoxGeometry(2.6, 0.02, 2.6), M(0xd4a017, { transparent: true, opacity: 0.01 }),
    [0, 0.18, 0], CI.pins, [0, -1, 0])
  for (let px = -4; px <= 4; px++) {
    for (let pz = -4; pz <= 4; pz++) {
      const pin = new THREE.Mesh(new THREE.CylinderGeometry(0.03, 0.03, 0.18, 6), pinMat)
      pin.position.set(px * 0.29, 0.27, pz * 0.29)
      g.add(pin)
      explodables.push({ mesh: pin, base: pin.position.clone(), off: new THREE.Vector3(0, -1, 0) })
    }
  }

  // Die
  addComp(g, new THREE.BoxGeometry(1.6, 0.1, 1.6), M(0x0d0d1a, { m: 0.3, r: 0.2 }),
    [0, 0.42, 0], CI.die, [0, 2.0, 0])

  // Die surface engraving
  const dl = new THREE.LineBasicMaterial({ color: 0x4444aa, transparent: true, opacity: 0.5 })
  const dpts = [
    new THREE.Vector3(-0.7, 0.48, -0.7), new THREE.Vector3(0.7, 0.48, -0.7),
    new THREE.Vector3(0.7, 0.48, 0.7), new THREE.Vector3(-0.7, 0.48, 0.7),
    new THREE.Vector3(-0.7, 0.48, -0.7),
    new THREE.Vector3(-0.3, 0.48, -0.7), new THREE.Vector3(-0.3, 0.48, 0.7),
    new THREE.Vector3(0.3, 0.48, -0.7), new THREE.Vector3(0.3, 0.48, 0.7),
  ]
  g.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints(dpts), dl))

  // TIM layer
  addComp(g, new THREE.BoxGeometry(1.6, 0.04, 1.6), M(0x666666, { transparent: true, opacity: 0.65 }),
    [0, 0.54, 0], CI.tim, [0, 3.5, 0])

  // IHS lid
  addComp(g, new THREE.BoxGeometry(2.6, 0.25, 2.6), M(0xcccccc, { m: 0.85, r: 0.15 }),
    [0, 0.74, 0], CI.ihs, [0, 5.5, 0])

  // IHS etched text decoration
  const iLines = new THREE.LineBasicMaterial({ color: 0x444444, opacity: 0.6 })
  const ipts = [
    new THREE.Vector3(-0.8, 0.87, -0.2), new THREE.Vector3(0.8, 0.87, -0.2),
    new THREE.Vector3(-0.4, 0.87, 0.0), new THREE.Vector3(0.4, 0.87, 0.0),
    new THREE.Vector3(-0.6, 0.87, 0.2), new THREE.Vector3(0.6, 0.87, 0.2),
  ]
  g.add(new THREE.LineSegments(new THREE.BufferGeometry().setFromPoints(ipts), iLines))
}

// ── Level 3: Logical Structure ──
function buildLevel3(g) {
  // Die base plate
  const dieBg = new THREE.Mesh(new THREE.BoxGeometry(6.2, 0.12, 6.2), M(0x0a0a18, { m: 0.1, r: 0.9 }))
  dieBg.receiveShadow = true
  g.add(dieBg)

  // Border glow lines
  const bg = new THREE.LineBasicMaterial({ color: 0x00d4ff, transparent: true, opacity: 0.4 })
  const bpts = [
    new THREE.Vector3(-3, 0.07, -3), new THREE.Vector3(3, 0.07, -3),
    new THREE.Vector3(3, 0.07, 3), new THREE.Vector3(-3, 0.07, 3),
    new THREE.Vector3(-3, 0.07, -3),
  ]
  g.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints(bpts), bg))

  function blk(ci, w, h, d, px, pz, expY) {
    const mat = M(ci.specs ? 0xffffff : 0xffffff)
    // Use the color from CI by finding its key in CI
    const color = getCompColor(ci)
    const m = new THREE.Mesh(
      new THREE.BoxGeometry(w, h, d),
      M(color, { m: 0.25, r: 0.6, emissive: color, ei: 0.08 })
    )
    m.position.set(px, h / 2 + 0.06, pz)
    m.castShadow = true
    m.userData.ci = ci
    allSelectables.push(m)
    explodables.push({ mesh: m, base: m.position.clone(), off: new THREE.Vector3(0, expY, 0) })
    g.add(m)
    return m
  }

  // L3 Cache (outer perimeter - dark blue)
  blk(CI.l3, 5.8, 0.2, 0.85, 0, -2.5, 3.5)
  blk(CI.l3, 5.8, 0.2, 0.85, 0, 2.5, 3.5)
  blk(CI.l3, 0.75, 0.2, 3.0, -2.7, 0, 3.5)
  blk(CI.l3, 0.75, 0.2, 3.0, 2.7, 0, 3.5)

  // L2 Cache (mid ring - blue)
  blk(CI.l2, 3.5, 0.25, 0.55, 0, -1.5, 4.0)
  blk(CI.l2, 3.5, 0.25, 0.55, 0, 1.5, 4.0)
  blk(CI.l2, 0.45, 0.25, 2.0, -1.6, 0, 4.0)
  blk(CI.l2, 0.45, 0.25, 2.0, 1.6, 0, 4.0)

  // L1 Cache strips (cyan, next to cores)
  blk(CI.l1, 0.7, 0.3, 0.35, -0.6, -0.75, 5.0)
  blk(CI.l1, 0.7, 0.3, 0.35, 0.6, -0.75, 5.0)
  blk(CI.l1, 0.7, 0.3, 0.35, -0.6, 0.75, 5.0)
  blk(CI.l1, 0.7, 0.3, 0.35, 0.6, 0.75, 5.0)

  // ALU blocks (orange, core center)
  blk(CI.alu, 0.85, 0.35, 0.55, -0.55, -0.1, 6.0)
  blk(CI.alu, 0.85, 0.35, 0.55, 0.55, -0.1, 6.0)
  blk(CI.alu, 0.85, 0.35, 0.55, -0.55, 0.2, 6.0)
  blk(CI.alu, 0.85, 0.35, 0.55, 0.55, 0.2, 6.0)

  // Control Unit (green, top center)
  blk(CI.cu, 1.1, 0.38, 0.75, 0, -0.55, 5.5)

  // Register File (gold, center)
  const regLeft = blk(CI.reg, 0.42, 0.4, 0.32, -0.35, 0.5, 6.5)
  const regRight = blk(CI.reg, 0.42, 0.4, 0.32, 0.35, 0.5, 6.5)

  // Visual R1 / R2 register blocks and memory cell [0x1000]
  const regVisMatR1 = M(0x00ff88, { m: 0.3, r: 0.35, emissive: 0x00ff88, ei: 0.15 })
  const regVisMatR2 = M(0xffc857, { m: 0.3, r: 0.35, emissive: 0xffc857, ei: 0.15 })
  const memVisMat = M(0x7f5af0, { m: 0.35, r: 0.4, emissive: 0x7f5af0, ei: 0.18 })

  r1Block = new THREE.Mesh(new THREE.BoxGeometry(0.28, 0.25, 0.24), regVisMatR1)
  r1Block.position.set(regLeft.position.x - 0.3, regLeft.position.y + 0.35, regLeft.position.z + 0.05)
  g.add(r1Block)

  r2Block = new THREE.Mesh(new THREE.BoxGeometry(0.28, 0.25, 0.24), regVisMatR2)
  r2Block.position.set(regLeft.position.x - 0.3, regLeft.position.y + 0.05, regLeft.position.z - 0.25)
  g.add(r2Block)

  memBlock = new THREE.Mesh(new THREE.BoxGeometry(0.5, 0.3, 0.5), memVisMat)
  memBlock.position.set(-2.6, 0.35, 0)
  g.add(memBlock)

  // Simple neon line from memory cell to register area to提示“数据搬运路径”
  const memLineMat = new THREE.LineBasicMaterial({ color: 0x7f5af0, transparent: true, opacity: 0.7 })
  const memLinePts = [
    new THREE.Vector3(memBlock.position.x + 0.25, memBlock.position.y + 0.2, memBlock.position.z),
    new THREE.Vector3(-1.3, 0.55, 0.0),
    new THREE.Vector3(regLeft.position.x - 0.25, regLeft.position.y + 0.1, regLeft.position.z),
  ]
  g.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints(memLinePts), memLineMat))

  // Pipeline column on the right side
  const pX = 2.2
  blk(CI.fetch, 0.65, 0.28, 0.55, pX, -1.1, 4.5)
  blk(CI.decode, 0.65, 0.28, 0.55, pX, -0.4, 5.0)
  blk(CI.alu, 0.65, 0.28, 0.55, pX, 0.3, 5.5)
  blk(CI.writeback, 0.65, 0.28, 0.55, pX, 1.0, 5.5)

  // Pipeline connector arrows
  const arrowMat = new THREE.LineBasicMaterial({ color: 0x00d4ff, transparent: true, opacity: 0.5 })
  const arrowPts = [
    new THREE.Vector3(pX, 0.2, -1.1), new THREE.Vector3(pX, 0.2, -0.65),
    new THREE.Vector3(pX, 0.2, -0.4), new THREE.Vector3(pX, 0.2, 0.05),
    new THREE.Vector3(pX, 0.2, 0.3), new THREE.Vector3(pX, 0.2, 0.75),
    new THREE.Vector3(pX, 0.2, 1.0), new THREE.Vector3(pX, 0.2, 1.4),
  ]
  g.add(new THREE.Line(new THREE.BufferGeometry().setFromPoints(arrowPts), arrowMat))

  // Setup data flow curve for level 3
  flowCurve = new THREE.CatmullRomCurve3([
    new THREE.Vector3(-5, 0.8, 0),       // Memory (off-screen)
    new THREE.Vector3(-2, 0.5, 0),        // L3 Cache
    new THREE.Vector3(0, 0.5, -2.5),      // L3 top
    new THREE.Vector3(0, 0.6, -0.55),     // Control Unit
    new THREE.Vector3(pX, 0.6, -1.1),     // Fetch
    new THREE.Vector3(pX, 0.6, -0.4),     // Decode
    new THREE.Vector3(-0.3, 0.7, -0.1),   // ALU
    new THREE.Vector3(-0.35, 0.8, 0.5),   // Register
  ])
}

function getCompColor(ci) {
  for (const key of Object.keys(CI)) {
    if (CI[key] === ci) {
      const colorMap = {
        motherboard: 0x1a4a2e, cpu_chip: 0xb8b8b8, ram: 0x2d5a27, cooler: 0x888888,
        ihs: 0xcccccc, tim: 0x888888, die: 0x1a1a3e, substrate: 0x5c3a00, pins: 0xd4a017,
        alu: 0xff6b35, cu: 0x00ff88, l1: 0x00d4ff, l2: 0x0099cc, l3: 0x003d66,
        reg: 0xffd700, fetch: 0xff66aa, decode: 0xff9966, writeback: 0x66ffcc,
      }
      return colorMap[key] ?? 0x888888
    }
  }
  return 0x888888
}

// ================================================================
// Data Flow Animation
// ================================================================
function highlightFlowStage(stageIdx) {
  if (!currentGroup) return

  // 先清空之前的高亮
  currentGroup.traverse(child => {
    if (child.isMesh && child.userData?.ci && child.material) {
      child.material.emissive = new THREE.Color(0x000000)
      child.material.emissiveIntensity = 0
    }
  })

  // 重置可视 R1/R2/内存块的高亮与缩放
  const resetVisBlock = (b) => {
    if (!b || !b.material) return
    b.material.emissiveIntensity = 0.15
    b.scale.set(1, 1, 1)
  }
  resetVisBlock(r1Block)
  resetVisBlock(r2Block)
  resetVisBlock(memBlock)

  const glowList = []
  if (stageIdx === 0) {
    glowList.push(CI.fetch, CI.l1, CI.l3)
  } else if (stageIdx === 1) {
    glowList.push(CI.decode, CI.cu)
  } else if (stageIdx === 2) {
    glowList.push(CI.alu, CI.l1, CI.l2)
  } else if (stageIdx === 3) {
    glowList.push(CI.writeback, CI.reg)
  }

  if (!glowList.length) return

  const glowColor = new THREE.Color(0x00d4ff)
  currentGroup.traverse(child => {
    if (!child.isMesh || !child.userData?.ci || !child.material) return
    if (glowList.includes(child.userData.ci)) {
      child.material.emissive = glowColor
      child.material.emissiveIntensity = 0.6
    }
  })

  // 根据阶段增强 R1 / R2 / [0x1000] 的视觉提示
  const boostBlock = (b) => {
    if (!b || !b.material) return
    b.material.emissiveIntensity = 0.7
    b.scale.set(1.1, 1.1, 1.1)
  }
  if (stageIdx === 0) {
    // 取指阶段突出内存单元 [0x1000] 是即将被访问的数据源
    boostBlock(memBlock)
  } else if (stageIdx === 1) {
    // 译码阶段强调会产生“加载到 R2 再加到 R1”的计划
    boostBlock(memBlock)
    boostBlock(r2Block)
  } else if (stageIdx === 2) {
    // 执行阶段：R1 和 R2 一起送入 ALU
    boostBlock(r1Block)
    boostBlock(r2Block)
  } else if (stageIdx === 3) {
    // 写回阶段：结果回到 R1
    boostBlock(r1Block)
  }
}

function createFlowParticle() {
  if (!scene) return
  const pGeo = new THREE.SphereGeometry(0.15, 12, 12)
  const pMat = new THREE.MeshBasicMaterial({ color: 0x00d4ff })
  flowParticle = new THREE.Mesh(pGeo, pMat)
  scene.add(flowParticle)

  flowLight = new THREE.PointLight(0x00d4ff, 3, 4)
  scene.add(flowLight)
}

function destroyFlowParticle() {
  if (flowParticle) { scene.remove(flowParticle); flowParticle.geometry?.dispose(); flowParticle = null }
  if (flowLight) { scene.remove(flowLight); flowLight = null }
  if (flowTimer) { clearInterval(flowTimer); flowTimer = null }
}

function toggleDataFlow() {
  if (currentLevel.value !== 3) {
    switchLevel(3)
    setTimeout(() => {
      startDataFlow()
    }, 100)
    return
  }
  if (dataFlowRunning.value) {
    dataFlowRunning.value = false
    destroyFlowParticle()
    currentFlowStage.value = -1
    highlightFlowStage(-1)
  } else {
    startDataFlow()
  }
}

function startDataFlow() {
  dataFlowRunning.value = true
  flowT = 0
  currentFlowStage.value = 0
  highlightFlowStage(0)
  createFlowParticle()

  let stage = 0
  flowTimer = setInterval(() => {
    stage++
    if (stage >= flowStages.length) {
      currentFlowStage.value = -1
      dataFlowRunning.value = false
      destroyFlowParticle()
      highlightFlowStage(-1)
      clearInterval(flowTimer)
      flowTimer = null
    } else {
      currentFlowStage.value = stage
      highlightFlowStage(stage)
      // Update color
      const hex = parseInt(flowStages[stage].hex.replace('#', ''), 16)
      if (flowParticle) flowParticle.material.color.setHex(hex)
      if (flowLight) flowLight.color.setHex(hex)
    }
  }, 2300)
}

// ================================================================
// Explosion
// ================================================================
let expAnimating = false
let expProgress = 0
let expDir = 0 // +1 = explode, -1 = assemble

function toggleExplode() {
  explodeMode.value = !explodeMode.value
  expDir = explodeMode.value ? 1 : -1
  expAnimating = true
}

function updateExplosion(delta) {
  if (!expAnimating) return
  expProgress += delta * 1.8 * expDir
  expProgress = Math.max(0, Math.min(1, expProgress))
  const t = easeInOut(expProgress)
  for (const { mesh, base, off } of explodables) {
    mesh.position.lerpVectors(base, base.clone().add(off), t)
  }
  if (expProgress <= 0 || expProgress >= 1) expAnimating = false
}

function easeInOut(t) {
  return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t
}

// ================================================================
// X-ray Mode
// ================================================================
function toggleXray() {
  xrayMode.value = !xrayMode.value
  if (!currentGroup) return

  if (xrayMode.value) {
    currentGroup.traverse(child => {
      if (!child.isMesh) return
      const origMat = Array.isArray(child.material) ? child.material[0] : child.material
      xrayMats.set(child, origMat)
      child.material = origMat.clone()
      child.material.transparent = true
      child.material.opacity = 0.28
      child.material.wireframe = false
    })
  } else {
    currentGroup.traverse(child => {
      if (!child.isMesh) return
      const orig = xrayMats.get(child)
      if (orig) child.material = orig
    })
    xrayMats.clear()
  }
}

// ================================================================
// Disassemble Mode
// ================================================================
function toggleDisassemble() {
  disassembleMode.value = !disassembleMode.value
  if (disassembleMode.value) {
    disassembleStep.value = 0
    highlightDisassembleStep(0)
  } else {
    disassembleStep.value = -1
    resetHighlights()
  }
}

function gotoDisassembleStep(i) {
  disassembleStep.value = i
  highlightDisassembleStep(i)
}

function highlightDisassembleStep(stepIdx) {
  if (!currentGroup) return
  currentGroup.traverse(child => {
    if (child.isMesh && child.material) {
      child.material.emissive = new THREE.Color(0x000000)
      child.material.emissiveIntensity = 0
    }
  })
  // Highlight nth selectable (rough mapping)
  const mesh = allSelectables[stepIdx]
  if (mesh && mesh.material) {
    mesh.material.emissive = new THREE.Color(0x00d4ff)
    mesh.material.emissiveIntensity = 0.4
  }
}

function resetHighlights() {
  if (!currentGroup) return
  currentGroup.traverse(child => {
    if (child.isMesh && child.material) {
      child.material.emissive = new THREE.Color(0x000000)
      child.material.emissiveIntensity = 0
    }
  })
}

// ================================================================
// Raycasting
// ================================================================
let hoveredMesh = null
let hoverOutline = null

function onMouseMove(e) {
  const rect = threeCanvas.value.getBoundingClientRect()
  mouse.x = ((e.clientX - rect.left) / rect.width) * 2 - 1
  mouse.y = -((e.clientY - rect.top) / rect.height) * 2 + 1
  threeCanvas.value.style.cursor = getIntersected() ? 'pointer' : 'grab'
}

function getIntersected() {
  raycaster.setFromCamera(mouse, camera)
  const hits = raycaster.intersectObjects(allSelectables, false)
  return hits.length > 0 ? hits[0].object : null
}

function onCanvasClick(e) {
  const hit = getIntersected()
  if (hit && hit.userData.ci) {
    selectedComp.value = hit.userData.ci
    qaMessages.value = []
    qaInput.value = ''
    updateAISpeech(hit.userData.ci)

    // Pulse effect
    const origEI = hit.material.emissiveIntensity
    hit.material.emissive = new THREE.Color(0x00d4ff)
    hit.material.emissiveIntensity = 0.6
    setTimeout(() => {
      if (hit.material) {
        hit.material.emissiveIntensity = origEI
        hit.material.emissive = new THREE.Color(0x000000)
      }
    }, 500)
  }
}

function deselectComp() {
  selectedComp.value = null
}

function updateAISpeech(ci) {
  aiSpeech.value = `📌 你选中了 ${ci.name}。${ci.description.slice(0, 60)}… 展开右侧面板查看详情，或直接向我提问！`
  aiExpanded.value = true
  setTimeout(() => { aiExpanded.value = false }, 5000)
}

// ================================================================
// AI Q&A
// ================================================================
async function sendQA() {
  const q = qaInput.value.trim()
  if (!q || !selectedComp.value) return
  qaMessages.value.push({ role: 'user', text: q })
  qaInput.value = ''
  qaLoading.value = true
  await nextTick()
  scrollQA()

  try {
    const systemPrompt = `你是一位计算机体系结构专家和硬件教师，回答风格简洁专业。学生当前正在 3D 拆解实验室查看组件：${selectedComp.value.name}（${selectedComp.value.category}）。该组件说明：${selectedComp.value.description}。规格参数：${selectedComp.value.specs.map(s => s.k + '=' + s.v).join('，')}。请用中文简洁回答，控制在120字以内。`
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

function scrollQA() {
  if (qaMsgsEl.value) qaMsgsEl.value.scrollTop = qaMsgsEl.value.scrollHeight
}

// ================================================================
// Monitor fluctuation
// ================================================================
function startMonitorFluctuation() {
  monTimer = setInterval(() => {
    cpuTemp.value = Math.round(65 + Math.random() * 20)
    clockFreq.value = parseFloat((3.2 + Math.random() * 2.6).toFixed(2))
    for (const m of cpuMetrics) {
      const base = { '计算核心利用率': 67, '缓存命中率': 88, '内存带宽使用': 52, '分支预测准确率': 92 }
      const v = Math.round(Math.min(99, Math.max(10, base[m.name] + (Math.random() - 0.5) * 20)))
      m.value = v
      m.display = v + '%'
    }
  }, 2500)
}

// ================================================================
// Tabs & Level Switch
// ================================================================
function switchTab(id) {
  activeTab.value = id
  if (id === 'hardware') {
    buildScene(currentLevel.value)
  } else if (id === 'dataflow') {
    if (currentLevel.value !== 3) {
      currentLevel.value = 3
      buildScene(3)
    }
    resetCamera()
  } else if (id === 'overview') {
    buildScene(1)
    currentLevel.value = 1
  }
}

function switchLevel(lv) {
  currentLevel.value = lv
  buildScene(lv)
  resetCamera()
  if (dataFlowRunning.value) {
    dataFlowRunning.value = false
    destroyFlowParticle()
  }
}

function resetCamera() {
  if (!controls || !camera) return
  const targets = { 1: [8, 6, 8], 2: [5, 4, 5], 3: [6, 8, 6] }
  const t = targets[currentLevel.value] || [8, 6, 8]
  camera.position.set(...t)
  controls.target.set(0, 0, 0)
  controls.update()
}

// ================================================================
// Resize
// ================================================================
function onResize() {
  if (!renderer || !camera || !containerEl.value) return
  const w = containerEl.value.clientWidth
  const h = containerEl.value.clientHeight
  camera.aspect = w / h
  camera.updateProjectionMatrix()
  renderer.setSize(w, h)
}

// ================================================================
// Main animation loop
// ================================================================
function animate() {
  animFrameId = requestAnimationFrame(animate)
  const delta = Math.min(threeClk.getDelta(), 0.05)
  const elapsed = threeClk.getElapsedTime()

  controls?.update()

  // Fan spin
  if (fanRef) fanRef.rotation.y += delta * 6

  // Explosion animation
  updateExplosion(delta)

  // Data flow particle
  if (dataFlowRunning.value && flowCurve && flowParticle) {
    flowT += delta * 0.12
    if (flowT > 1) flowT = 0
    const pos = flowCurve.getPoint(flowT)
    flowParticle.position.copy(pos)
    if (flowLight) flowLight.position.copy(pos)
    // Pulse size
    const scale = 1 + Math.sin(elapsed * 8) * 0.3
    flowParticle.scale.setScalar(scale)
  }

  renderer.render(scene, camera)
}

// ================================================================
// Lifecycle
// ================================================================
onMounted(() => {
  initScene()
  buildScene(1)
  animate()
  startMonitorFluctuation()
  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  if (animFrameId) cancelAnimationFrame(animFrameId)
  if (monTimer) clearInterval(monTimer)
  destroyFlowParticle()
  window.removeEventListener('resize', onResize)
  if (renderer) {
    renderer.dispose()
    threeCanvas.value?.removeEventListener('click', onCanvasClick)
    threeCanvas.value?.removeEventListener('mousemove', onMouseMove)
  }
})

// Watch level change to rebuild disassemble steps display
watch(currentLevel, () => {
  disassembleStep.value = 0
})
</script>

<style scoped lang="scss">
/* ── Layout ── */
.vr-lab {
  position: relative;
  width: 100%;
  height: calc(100vh - 60px);
  min-height: 600px;
  overflow: hidden;
  background: #050510;
  font-family: 'Segoe UI', system-ui, sans-serif;
  color: #e0e8ff;
}

.three-canvas {
  position: absolute;
  inset: 0;
  width: 100% !important;
  height: 100% !important;
  display: block;
}

/* ── Glass panel base ── */
%glass {
  background: rgba(5, 10, 30, 0.82);
  backdrop-filter: blur(14px);
  border: 1px solid rgba(0, 212, 255, 0.18);
  border-radius: 12px;
}

/* ── Top bar ── */
.top-bar {
  @extend %glass;
  position: absolute;
  top: 12px;
  left: 80px;
  right: 12px;
  height: 52px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 20px;
  z-index: 20;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 15px;
  white-space: nowrap;
  .brand-hex { font-size: 20px; color: #00d4ff; }
  .brand-name { background: linear-gradient(135deg, #00d4ff, #a78bfa); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
}

.level-tabs {
  display: flex;
  gap: 6px;
  flex: 1;
  justify-content: center;
}

.level-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  background: rgba(0, 212, 255, 0.08);
  border: 1px solid rgba(0, 212, 255, 0.25);
  border-radius: 20px;
  color: #88bbcc;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  .lv-badge {
    background: rgba(0, 212, 255, 0.2);
    border-radius: 10px;
    padding: 1px 7px;
    font-weight: 700;
    font-size: 11px;
  }
  &.active {
    background: rgba(0, 212, 255, 0.2);
    border-color: #00d4ff;
    color: #00d4ff;
    .lv-badge { background: #00d4ff; color: #050510; }
  }
  &:hover:not(.active) {
    border-color: rgba(0, 212, 255, 0.5);
    color: #aaddef;
  }
}

.top-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}
.icon-btn {
  width: 32px; height: 32px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.25);
  border-radius: 8px;
  color: #00d4ff;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
  &:hover { background: rgba(0, 212, 255, 0.25); }
}

/* ── Left nav ── */
.left-nav {
  position: absolute;
  left: 12px;
  top: 12px;
  bottom: 40px;
  width: 58px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 20;
}
.nav-tab {
  @extend %glass;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 4px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 10px;
  .nav-icon { font-size: 18px; }
  .nav-label { font-size: 10px; color: #6688aa; white-space: nowrap; }
  &.active {
    border-color: #00d4ff;
    background: rgba(0, 212, 255, 0.18);
    .nav-label { color: #00d4ff; }
  }
  &:hover:not(.active) { border-color: rgba(0, 212, 255, 0.4); }
}

/* ── Right tools ── */
.right-tools {
  @extend %glass;
  position: absolute;
  right: 12px;
  top: 76px;
  width: 220px;
  padding: 14px;
  z-index: 20;
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: calc(100vh - 180px);
  overflow-y: auto;
}
.tools-title {
  font-size: 12px;
  font-weight: 700;
  color: #00d4ff;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 4px;
}
.tool-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #99aabb;
  .t-ico { font-size: 18px; flex-shrink: 0; }
  .t-info { display: flex; flex-direction: column; gap: 2px; }
  .t-name { font-size: 12px; font-weight: 600; }
  .t-badge { font-size: 10px; padding: 1px 6px; border-radius: 8px; width: fit-content;
    &.on { background: rgba(0, 255, 136, 0.2); color: #00ff88; }
    &.off { background: rgba(255, 255, 255, 0.1); color: #556677; }
  }
  &.active { border-color: #00d4ff; background: rgba(0, 212, 255, 0.12); color: #00d4ff; }
  &:hover:not(.active) { border-color: rgba(0, 212, 255, 0.3); color: #aaccdd; }
}

.disassemble-steps {
  margin-top: 8px;
  border-top: 1px solid rgba(0, 212, 255, 0.15);
  padding-top: 10px;
  .steps-title { font-size: 11px; color: #667788; margin-bottom: 6px; }
}
.step-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 11px;
  color: #556677;
  transition: all 0.15s;
  .step-num {
    flex-shrink: 0;
    width: 18px; height: 18px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    display: flex; align-items: center; justify-content: center;
    font-size: 10px; font-weight: 700;
  }
  &.current {
    background: rgba(0, 212, 255, 0.12);
    color: #00d4ff;
    .step-num { background: #00d4ff; color: #050510; }
  }
  &.done { color: #00ff88;
    .step-num { background: rgba(0, 255, 136, 0.2); color: #00ff88; text-decoration: line-through; }
  }
  &:hover { background: rgba(255, 255, 255, 0.06); }
}

/* ── Data Flow Panel ── */
.df-panel {
  @extend %glass;
  position: absolute;
  right: 12px;
  top: 76px;
  width: 260px;
  padding: 18px;
  z-index: 20;
}
.df-title { font-size: 13px; font-weight: 700; color: #00d4ff; margin-bottom: 4px; }
.df-sub { font-size: 11px; color: #667788; margin-bottom: 14px; code { background: rgba(0,212,255,0.15); padding: 1px 5px; border-radius: 4px; } }
.df-run-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(167, 139, 250, 0.2));
  border: 1px solid #00d4ff;
  border-radius: 8px;
  color: #00d4ff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 16px;
  &.running { background: rgba(255, 100, 100, 0.15); border-color: #ff6464; color: #ff6464; }
  &:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(0, 212, 255, 0.3); }
}
.df-stages { display: flex; flex-direction: column; gap: 10px; }
.df-stage {
  display: flex;
  gap: 10px;
  opacity: 0.4;
  transition: all 0.3s;
  .df-stage-dot {
    flex-shrink: 0;
    width: 12px; height: 12px;
    border-radius: 50%;
    background: #334455;
    margin-top: 4px;
    border: 2px solid #334455;
  }
  .df-stage-name { font-size: 12px; font-weight: 700; color: #aabbcc; }
  .df-stage-desc { font-size: 11px; color: #556677; line-height: 1.4; }
  &.active {
    opacity: 1;
    .df-stage-dot { background: #00d4ff; border-color: #00d4ff; box-shadow: 0 0 8px #00d4ff; animation: pulse 1s infinite; }
    .df-stage-name { color: #00d4ff; }
    .df-stage-desc { color: #99bbcc; }
  }
  &.done {
    opacity: 0.7;
    .df-stage-dot { background: #00ff88; border-color: #00ff88; }
    .df-stage-name { color: #00ff88; }
  }
}
.df-legend {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  border-top: 1px solid rgba(255,255,255,0.08);
  padding-top: 10px;
}
.df-legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 10px;
  color: #667788;
  .df-legend-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
}

/* ── Monitor Panel ── */
.monitor-panel {
  @extend %glass;
  position: absolute;
  bottom: 50px;
  left: 80px;
  width: 260px;
  padding: 14px;
  z-index: 20;
}
.mon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 12px;
  font-weight: 700;
  color: #00d4ff;
  .mon-close { background: none; border: none; color: #556677; cursor: pointer; font-size: 14px; &:hover { color: #fff; } }
}
.mon-freq {
  text-align: center;
  margin-bottom: 12px;
  .freq-val { font-size: 24px; font-weight: 800; color: #00d4ff; display: block; }
  .freq-info { font-size: 11px; color: #556677; }
}
.mon-temp-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 11px;
  .temp-label { color: #667788; white-space: nowrap; }
  .temp-bar-bg { flex: 1; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden; }
  .temp-bar-fill { height: 100%; border-radius: 3px; transition: all 0.5s; }
  .temp-val { font-weight: 700; font-size: 12px; white-space: nowrap; }
}
.mon-metric {
  margin-bottom: 8px;
  .mon-m-hd { display: flex; justify-content: space-between; font-size: 11px; color: #667788; margin-bottom: 3px; }
  .mon-m-val { font-weight: 600; color: #aabbcc; }
  .mon-bar-bg { height: 5px; background: rgba(255,255,255,0.06); border-radius: 3px; overflow: hidden; }
  .mon-bar-fill { height: 100%; border-radius: 3px; transition: width 0.8s ease; }
}

/* ── Component Info Panel ── */
.comp-panel {
  @extend %glass;
  position: absolute;
  top: 76px;
  left: 80px;
  width: 320px;
  max-height: calc(100vh - 160px);
  overflow-y: auto;
  padding: 0;
  z-index: 25;
  display: flex;
  flex-direction: column;
}
.cp-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
  background: rgba(0, 212, 255, 0.06);
  .cp-name { font-size: 14px; font-weight: 700; color: #ffffff; margin-bottom: 3px; }
  .cp-cat { font-size: 11px; color: #00d4ff; }
  .cp-close { background: none; border: none; color: #556677; cursor: pointer; font-size: 16px; padding: 0; &:hover { color: #fff; } }
}
.cp-desc {
  padding: 12px 16px;
  font-size: 12px;
  color: #99aabb;
  line-height: 1.6;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.cp-specs {
  padding: 10px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.cp-spec {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  gap: 8px;
  .sk { color: #556677; flex-shrink: 0; }
  .sv { color: #ccd6ee; font-weight: 600; text-align: right; }
}
.cp-qa {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 180px;
}
.qa-title {
  padding: 10px 16px;
  font-size: 11px;
  font-weight: 700;
  color: #a78bfa;
  border-bottom: 1px solid rgba(167, 139, 250, 0.2);
}
.qa-msgs {
  flex: 1;
  max-height: 200px;
  overflow-y: auto;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.qa-empty { font-size: 11px; color: #445566; font-style: italic; text-align: center; padding: 12px 0; }
.qa-bubble {
  font-size: 12px;
  line-height: 1.5;
  padding: 8px 10px;
  border-radius: 8px;
  b { font-weight: 700; margin-right: 4px; }
  &.user { background: rgba(167, 139, 250, 0.12); border: 1px solid rgba(167, 139, 250, 0.2); color: #c4b5fd; b { color: #a78bfa; } }
  &.ai { background: rgba(0, 212, 255, 0.08); border: 1px solid rgba(0, 212, 255, 0.15); color: #aaccdd; b { color: #00d4ff; } }
}
.qa-thinking {
  display: flex;
  gap: 4px;
  padding: 6px 10px;
  span {
    width: 7px; height: 7px;
    background: #00d4ff;
    border-radius: 50%;
    animation: qa-dot 1.2s infinite;
    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}
.qa-input-row {
  display: flex;
  gap: 6px;
  padding: 10px 12px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}
.qa-input {
  flex: 1;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 8px;
  padding: 7px 10px;
  font-size: 12px;
  color: #ccd6ee;
  outline: none;
  &:focus { border-color: #00d4ff; }
  &::placeholder { color: #334455; }
}
.qa-send {
  width: 34px; height: 34px;
  background: #00d4ff;
  border: none;
  border-radius: 8px;
  color: #050510;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { background: #33ddff; transform: translateY(-1px); }
  &:disabled { opacity: 0.4; cursor: not-allowed; }
}

/* ── Hint bar ── */
.hint-bar {
  position: absolute;
  bottom: 14px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(5, 10, 30, 0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 20px;
  padding: 6px 20px;
  font-size: 12px;
  color: #445566;
  z-index: 15;
  white-space: nowrap;
  pointer-events: none;
}

/* ── Floating AI ── */
.ai-float {
  position: absolute;
  bottom: 14px;
  right: 14px;
  z-index: 30;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  cursor: pointer;
}
.ai-ring {
  width: 54px; height: 54px;
  border-radius: 50%;
  background: rgba(5, 10, 30, 0.88);
  border: 2px solid rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  transition: all 0.3s;
  .ai-label { font-size: 9px; font-weight: 700; color: #00d4ff; letter-spacing: 1px; }
  &:hover { box-shadow: 0 0 30px rgba(0, 212, 255, 0.5); border-color: #00d4ff; }
}
.ai-waveform {
  display: flex;
  gap: 2px;
  align-items: center;
  height: 16px;
  i {
    display: block;
    width: 3px;
    background: #00d4ff;
    border-radius: 2px;
    animation: waveform 0.9s ease-in-out infinite alternate;
    height: 8px;
  }
}
.ai-speech {
  max-width: 220px;
  background: rgba(5, 10, 30, 0.9);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 12px;
  color: #99bbcc;
  line-height: 1.5;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

/* ── Transitions ── */
.slide-r-enter-active, .slide-r-leave-active { transition: all 0.3s ease; }
.slide-r-enter-from, .slide-r-leave-to { opacity: 0; transform: translateX(20px); }

.slide-panel-enter-active, .slide-panel-leave-active { transition: all 0.3s ease; }
.slide-panel-enter-from, .slide-panel-leave-to { opacity: 0; transform: translateX(-20px); }

.fade-up-enter-active, .fade-up-leave-active { transition: all 0.3s ease; }
.fade-up-enter-from, .fade-up-leave-to { opacity: 0; transform: translateY(10px); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ── Keyframes ── */
@keyframes pulse {
  0%, 100% { box-shadow: 0 0 6px #00d4ff; }
  50% { box-shadow: 0 0 14px #00d4ff, 0 0 24px rgba(0, 212, 255, 0.4); }
}

@keyframes waveform {
  0% { height: 4px; }
  100% { height: 16px; }
}

@keyframes qa-dot {
  0%, 80%, 100% { transform: scale(0.5); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* ── Scrollbar ── */
::-webkit-scrollbar { width: 4px; }
::-webkit-scrollbar-track { background: rgba(255,255,255,0.04); }
::-webkit-scrollbar-thumb { background: rgba(0, 212, 255, 0.3); border-radius: 2px; }
</style>
