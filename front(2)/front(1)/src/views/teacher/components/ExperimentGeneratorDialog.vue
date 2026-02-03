<template>
  <el-dialog
    v-model="visible"
    title="生成实验指导书"
    width="700px"
    @close="handleClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="选择课程">
        <el-select 
          v-model="form.courseId" 
          placeholder="请选择课程" 
          style="width: 100%"
          :teleported="false"
          @change="onCourseChange"
        >
          <el-option
            v-for="course in courseList"
            :key="course.id"
            :label="course.name"
            :value="course.id"
          >
            <span style="float: left">{{ course.name }}</span>
            <span style="float: right; color: #8cc5ff; font-size: 13px">{{ course.subject }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择章节">
        <el-select 
          v-model="form.chapterId" 
          placeholder="请先选择课程" 
          style="width: 100%"
          :teleported="false"
          :disabled="!form.courseId || chapterList.length === 0"
          @change="onChapterChange"
        >
          <el-option
            v-for="chapter in chapterList"
            :key="chapter.id"
            :label="chapter.title"
            :value="chapter.id"
          />
        </el-select>
        <div class="tips" style="margin-top: 8px;" v-if="form.courseId && chapterList.length === 0">
          <el-text type="warning" size="small">
            ⚠️ 该课程暂无章节，请先在课程管理中添加章节
          </el-text>
        </div>
      </el-form-item>
      <el-form-item label="知识点选择">
        <div v-if="knowledgePointsList.length === 0" class="tips">
          <el-text type="warning" size="small">
            ⚠️ 请先选择章节以加载知识点
          </el-text>
        </div>
        <div v-else class="knowledge-points-grid">
          <el-card
            v-for="point in knowledgePointsList"
            :key="point.id"
            :class="{ 'is-selected': point.selected }"
            class="knowledge-point-card"
            @click="toggleKnowledgePoint(point.id)"
            shadow="hover"
          >
            <div class="point-content">
              <div class="point-header">
                <span class="point-name">{{ point.name }}</span>
                <el-icon v-if="point.selected" color="#67C23A" :size="20">
                  <CircleCheck />
                </el-icon>
              </div>
              <div class="point-description">{{ point.description }}</div>
            </div>
          </el-card>
        </div>
        <div class="tips" style="margin-top: 12px;">
          <el-text type="info" size="small">
            💡 已选择 {{ selectedKnowledgePointsCount }} 个知识点
          </el-text>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">生成</el-button>
    </template>
  </el-dialog>

  <!-- AI生成进度条 -->
  <AIGenerationProgress
    :visible="showProgress"
    title="AI 正在生成实验指导书"
    :progress="progressValue"
    :current-step="currentStep"
    :steps="progressSteps"
    :message="progressMessage"
    tip="💡 提示：生成时间取决于实验复杂度，通常需要1-2分钟"
  />
</template>

<script setup>
import { ref, onMounted, defineEmits, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import * as smartTeachingApi from '@/api/smartTeaching'
import { getCoursesByTeacherId } from '@/api/course'
import AIGenerationProgress from '@/components/AIGenerationProgress.vue'

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const submitting = ref(false)
const courseList = ref([])
const chapterList = ref([])
const knowledgePointsList = ref([])
const form = ref({
  courseId: null,
  chapterId: null,
  chapterName: '',
  knowledgePoints: []
})

// 进度条相关
const showProgress = ref(false)
const progressValue = ref(0)
const currentStep = ref(0)
const progressMessage = ref('')
const progressSteps = ref([
  { title: '准备数据', desc: '正在准备实验生成所需的数据...' },
  { title: '调用AI服务', desc: '正在连接AI服务并发送请求...' },
  { title: '生成实验内容', desc: 'AI正在生成实验指导书，请耐心等待...' },
  { title: '完成', desc: '实验指导书生成完成！' }
])

// 科目-章节-知识库数据结构（和考核生成页面完全一致）
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
  ],
  '软件工程实践': [
    { value: 'ch01', label: '第一章 线性表', knowledgeBase: '数据结构基础' },
    { value: 'ch02', label: '第二章 栈和队列', knowledgeBase: '栈队列原理' },
    { value: 'ch03', label: '第三章 树和二叉树', knowledgeBase: '树结构应用' },
    { value: 'ch04', label: '第四章 图', knowledgeBase: '图算法练习' },
    { value: 'ch05', label: '第五章 查找', knowledgeBase: '查找算法' },
    { value: 'ch06', label: '第六章 排序', knowledgeBase: '排序算法' }
  ]
}

// 知识点数据映射（完整的六门课程数据，和考核生成页面完全一致）
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
  
  // Linux系统
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

// 计算已选择的知识点数量
const selectedKnowledgePointsCount = computed(() => {
  return knowledgePointsList.value.filter(p => p.selected).length
})

// 加载教师的课程列表
const loadCourses = async () => {
  try {
    const teacherId = localStorage.getItem('userId') || '2'
    console.log('正在加载课程，教师ID:', teacherId)
    const res = await getCoursesByTeacherId(teacherId)
    console.log('课程API响应:', res)
    console.log('res.data:', res.data)
    console.log('res.data.data:', res.data?.data)
    
    // 根据实际响应结构提取课程列表
    // 响应格式: {data: {success: true, msg: '...', data: Array(5)}}
    let courses = []
    if (res.data && res.data.data && Array.isArray(res.data.data)) {
      courses = res.data.data
      console.log('使用 res.data.data')
    } else if (res.data && Array.isArray(res.data)) {
      courses = res.data
      console.log('使用 res.data')
    }
    
    courseList.value = [...courses]
    console.log('最终课程列表:', courseList.value)
    console.log('课程数量:', courseList.value.length)
    
    await nextTick()
    
    if (courseList.value.length === 0) {
      ElMessage.warning('您还没有任何课程')
    } else {
      ElMessage.success(`已加载 ${courseList.value.length} 门课程`)
    }
  } catch (error) {
    console.error('加载课程列表失败', error)
    ElMessage.error('加载课程列表失败：' + error.message)
  }
}

// 课程改变时，加载章节列表
const onCourseChange = async (courseId) => {
  const course = courseList.value.find(c => c.id === courseId)
  if (course) {
    ElMessage.success(`已选择课程：${course.name}`)
    // 清空章节选择
    form.value.chapterId = null
    form.value.chapterName = ''
    chapterList.value = []
    
    // 加载章节列表
    try {
      const res = await smartTeachingApi.getChaptersByCourseId(courseId)
      if (res.code === 1 || res.success === true) {
        chapterList.value = res.data || []
        if (chapterList.value.length === 0) {
          ElMessage.warning('该课程暂无章节')
        }
      }
    } catch (error) {
      console.error('加载章节列表失败', error)
      ElMessage.error('加载章节列表失败')
    }
  }
}

// 章节改变时，加载对应的知识点
const onChapterChange = (chapterId) => {
  const chapter = chapterList.value.find(c => c.id === chapterId)
  if (chapter) {
    form.value.chapterName = chapter.title
    ElMessage.success(`已选择章节：${chapter.title}`)
    
    // 根据章节加载知识点
    loadKnowledgePoints(chapter)
  }
}

// 加载知识点列表
const loadKnowledgePoints = (chapter) => {
  const course = courseList.value.find(c => c.id === form.value.courseId)
  if (!course) return
  
  console.log('加载知识点 - 课程:', course.name, '科目:', course.subject, '章节:', chapter)
  
  // 从章节标题中提取章节号（例如："第一章 线性表" -> "ch01"）
  let chapterKey = ''
  const chapterTitle = chapter.title || ''
  
  // 尝试从标题中提取章节号
  const chapterMatch = chapterTitle.match(/第([一二三四五六七八九十]+)章/)
  if (chapterMatch) {
    const chineseNumbers = { '一': 1, '二': 2, '三': 3, '四': 4, '五': 5, '六': 6, '七': 7, '八': 8, '九': 9, '十': 10 }
    const chapterNum = chineseNumbers[chapterMatch[1]] || 1
    chapterKey = `ch${String(chapterNum).padStart(2, '0')}`
  } else if (chapter.chapterNumber) {
    // 如果有 chapterNumber 字段，使用它
    chapterKey = `ch${String(chapter.chapterNumber).padStart(2, '0')}`
  } else {
    // 默认使用 ch01
    chapterKey = 'ch01'
  }
  
  const subject = course.subject
  
  console.log('初始章节key:', chapterKey, '科目:', subject)
  
  // 为不同课程添加后缀
  if (subject.includes('Linux') || subject.includes('linux')) {
    chapterKey = chapterKey + '-linux'
  } else if (subject.includes('操作系统')) {
    chapterKey = chapterKey + '-os'
  } else if (subject.includes('网络') || subject.includes('计算机网络')) {
    chapterKey = chapterKey + '-network'
  } else if (subject.includes('Java') || subject.includes('java')) {
    chapterKey = chapterKey + '-java'
  } else if (subject.includes('机器学习')) {
    chapterKey = chapterKey + '-ml'
  } else if (subject.includes('软件工程') || subject.includes('软件')) {
    chapterKey = chapterKey + '-software'
  }
  // 数据结构不需要后缀，直接使用 ch01, ch02 等
  
  console.log('最终章节key:', chapterKey)
  
  // 获取知识点数据
  const points = chapterKnowledgePointsMap[chapterKey]
  if (points && points.length > 0) {
    knowledgePointsList.value = points.map(p => ({ ...p, selected: false }))
    console.log('加载知识点成功:', chapterKey, knowledgePointsList.value)
  } else {
    // 使用默认知识点
    knowledgePointsList.value = [
      { id: 1, name: '基础概念', description: '课程基础概念和定义', selected: false },
      { id: 2, name: '核心原理', description: '课程核心原理和理论', selected: false },
      { id: 3, name: '应用实践', description: '实际应用和案例分析', selected: false },
      { id: 4, name: '综合运用', description: '综合运用和问题解决', selected: false }
    ]
    console.log('使用默认知识点，章节key:', chapterKey, '未找到对应数据')
  }
}

// 切换知识点选择
const toggleKnowledgePoint = (id) => {
  const point = knowledgePointsList.value.find(p => p.id === id)
  if (point) {
    point.selected = !point.selected
  }
}

const handleClose = () => {
  emit('close')
}

const handleSubmit = async () => {
  if (!form.value.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!form.value.chapterId) {
    ElMessage.warning('请选择章节')
    return
  }
  
  // 获取选中的知识点
  const selectedPoints = knowledgePointsList.value.filter(p => p.selected).map(p => p.name)
  if (selectedPoints.length === 0) {
    ElMessage.warning('请至少选择一个知识点')
    return
  }

  submitting.value = true
  showProgress.value = true
  progressValue.value = 0
  currentStep.value = 0
  progressMessage.value = '正在准备数据...'
  
  try {
    // 步骤1: 准备数据
    progressValue.value = 10
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const teacherId = localStorage.getItem('userId') || '2'
    const course = courseList.value.find(c => c.id === form.value.courseId)
    const chapterName = `${course.name} - ${form.value.chapterName}`
    
    // 步骤2: 调用AI服务
    currentStep.value = 1
    progressValue.value = 25
    progressMessage.value = '正在连接AI服务...'
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 步骤3: 生成内容
    currentStep.value = 2
    progressValue.value = 40
    progressMessage.value = 'AI正在生成实验指导书，请耐心等待...'
    
    // 提交生成请求
    const submitRes = await smartTeachingApi.generateExperimentGuide({
      teacherId,
      courseId: form.value.courseId,
      chapterName: chapterName,
      knowledgePoints: selectedPoints,
      difficultyLevel: 'medium'
    })
    
    console.log('提交响应:', submitRes)
    
    // 检查响应格式：可能是 {code: 1, data: xxx} 或 {success: true, data: xxx}
    const isSuccess = (submitRes.code === 1) || (submitRes.success === true)
    
    if (!isSuccess || !submitRes.data) {
      throw new Error('提交失败')
    }
    
    // 后端直接返回 guideId
    const guideId = submitRes.data
    console.log('获取到 guideId:', guideId)
    
    // 轮询检查生成状态
    let checkCount = 0
    const maxChecks = 60 // 最多检查60次（2分钟）
    
    const checkStatus = async () => {
      checkCount++
      
      console.log(`检查状态 - 第${checkCount}次，guideId:`, guideId)
      
      if (checkCount > maxChecks) {
        throw new Error('生成超时，请稍后查看')
      }
      
      const statusRes = await smartTeachingApi.getExperimentGuide(guideId)
      console.log('状态查询响应:', statusRes)
      
      // 检查响应格式：可能是 {code: 1, data: xxx} 或 {success: true, data: xxx}
      const isSuccess = (statusRes.code === 1) || (statusRes.success === true)
      
      if (isSuccess && statusRes.data) {
        const status = statusRes.data.status
        console.log('当前状态:', status)
        
        if (status === 'completed') {
          // 生成完成
          progressValue.value = 95
          console.log('生成完成，准备下载')
          return statusRes.data
        } else if (status === 'failed') {
          throw new Error('生成失败')
        } else {
          // 还在生成中，继续等待
          progressValue.value = Math.min(40 + checkCount * 0.7, 90)
          progressMessage.value = `AI正在生成中...`
          await new Promise(resolve => setTimeout(resolve, 2000))
          return await checkStatus()
        }
      } else {
        throw new Error('查询状态失败')
      }
    }
    
    const guideData = await checkStatus()
    
    // 步骤4: 完成并下载
    currentStep.value = 3
    progressValue.value = 100
    progressMessage.value = '生成完成，正在准备下载...'
    
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 下载Word文档
    downloadWordDocument(guideData)
    
    showProgress.value = false
    
    ElMessage.success('实验指导书生成成功！')
    emit('success')
  } catch (error) {
    showProgress.value = false
    console.error('生成失败，错误详情:', error)
    console.error('错误消息:', error.message)
    console.error('错误响应:', error.response)
    ElMessage.error('生成失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 下载Word文档
const downloadWordDocument = (guideData) => {
  try {
    console.log('开始生成文档，原始数据:', guideData)
    
    // 解析所有数据
    const objectives = parseJSON(guideData.objectives)
    const environment = parseJSON(guideData.environment)
    const steps = parseJSON(guideData.steps)
    const expectedResults = parseJSON(guideData.expectedResults)
    const questions = parseJSON(guideData.questions)
    const answers = parseJSON(guideData.answers)
    
    console.log('解析后的objectives:', objectives)
    console.log('解析后的environment:', environment, '类型:', typeof environment)
    console.log('解析后的steps:', steps)
    console.log('解析后的expectedResults:', expectedResults)
    console.log('解析后的questions:', questions)
    console.log('解析后的answers:', answers)
    
    // 构建Word文档内容
    let content = `====================================\n`
    content += `           实验指导书\n`
    content += `====================================\n\n`
    content += `标题：${guideData.title || guideData.chapterName}\n\n`
    content += `------------------------------------\n\n`
    
    // 实验目的
    content += `一、实验目的\n\n`
    objectives.forEach((obj, index) => {
      const cleanObj = cleanText(obj)
      content += `  ${index + 1}. ${cleanObj}\n\n`
    })
    
    // 实验环境
    content += `二、实验环境\n\n`
    
    if (typeof environment === 'object' && !Array.isArray(environment)) {
      // 处理对象格式的环境信息
      Object.keys(environment).forEach(key => {
        const value = environment[key]
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
          content += `  ${keyName}：\n`
          value.forEach(item => {
            content += `    • ${cleanText(item)}\n`
          })
          content += `\n`
        } else {
          content += `  ${keyName}：${cleanText(value)}\n\n`
        }
      })
    } else if (Array.isArray(environment)) {
      environment.forEach(env => {
        content += `  • ${cleanText(env)}\n`
      })
      content += `\n`
    } else if (guideData.environment) {
      content += `  ${cleanText(guideData.environment)}\n\n`
    }
    
    // 实验步骤
    content += `三、实验步骤\n\n`
    steps.forEach((step, index) => {
      let cleanStep = cleanText(step)
      // 移除XML代码和draw.io代码
      cleanStep = cleanStep.replace(/<mxGraphModel[\s\S]*?<\/mxGraphModel>/g, '[此处应包含用例图，请使用draw.io等工具绘制]')
      cleanStep = cleanStep.replace(/<mx[^>]*>/g, '')
      cleanStep = cleanStep.replace(/<\/mx[^>]*>/g, '')
      content += `  步骤 ${index + 1}：${cleanStep}\n\n`
    })
    
    // 预期结果
    if (guideData.expectedResults) {
      content += `四、预期结果\n\n`
      if (Array.isArray(expectedResults)) {
        expectedResults.forEach((result, index) => {
          content += `  ${index + 1}. ${cleanText(result)}\n\n`
        })
      } else {
        content += `  ${cleanText(guideData.expectedResults)}\n\n`
      }
    }
    
    // 思考题
    content += `五、思考题\n\n`
    questions.forEach((q, index) => {
      content += `  ${index + 1}. ${cleanText(q)}\n\n`
    })
    
    // 参考答案
    content += `六、参考答案\n\n`
    answers.forEach((a, index) => {
      content += `  ${index + 1}. ${cleanText(a)}\n\n`
    })
    
    content += `\n====================================\n`
    content += `            实验指导书结束\n`
    content += `====================================\n`
    
    console.log('生成的文档内容长度:', content.length)
    
    // 创建Word文档（使用HTML格式，Word可以打开）
    const htmlContent = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <style>
    body { font-family: "Microsoft YaHei", "SimSun", sans-serif; line-height: 1.8; padding: 40px; }
    h1 { text-align: center; color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
    h2 { color: #34495e; margin-top: 30px; border-left: 5px solid #3498db; padding-left: 10px; }
    h3 { color: #7f8c8d; margin-top: 20px; }
    p { margin: 10px 0; text-indent: 2em; }
    ul { margin: 10px 0; padding-left: 40px; }
    li { margin: 5px 0; }
    .header { text-align: center; border: 2px solid #3498db; padding: 20px; margin-bottom: 30px; }
    .section { margin: 20px 0; }
  </style>
</head>
<body>
  <div class="header">
    <h1>实验指导书</h1>
    <h3>${guideData.title || guideData.chapterName}</h3>
  </div>
  
  <div class="section">
    <h2>一、实验目的</h2>
    <ul>
      ${objectives.map((obj, i) => `<li>${cleanText(obj)}</li>`).join('\n      ')}
    </ul>
  </div>
  
  <div class="section">
    <h2>二、实验环境</h2>
    ${typeof environment === 'object' && !Array.isArray(environment) ? 
      Object.keys(environment).map(key => {
        const value = environment[key]
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
          return `<p><strong>${keyName}：</strong></p><ul>${value.map(item => `<li>${cleanText(item)}</li>`).join('')}</ul>`
        } else {
          return `<p><strong>${keyName}：</strong>${cleanText(value)}</p>`
        }
      }).join('\n    ') :
      Array.isArray(environment) ? 
        `<ul>${environment.map(env => `<li>${cleanText(env)}</li>`).join('')}</ul>` :
        `<p>${cleanText(guideData.environment)}</p>`
    }
  </div>
  
  <div class="section">
    <h2>三、实验步骤</h2>
    <ol>
      ${steps.map(step => {
        let cleanStep = cleanText(step)
        cleanStep = cleanStep.replace(/<mxGraphModel[\s\S]*?<\/mxGraphModel>/g, '[此处应包含用例图，请使用draw.io等工具绘制]')
        cleanStep = cleanStep.replace(/<mx[^>]*>/g, '').replace(/<\/mx[^>]*>/g, '')
        return `<li>${cleanStep.replace(/\n/g, '<br>')}</li>`
      }).join('\n      ')}
    </ol>
  </div>
  
  <div class="section">
    <h2>四、预期结果</h2>
    <ol>
      ${Array.isArray(expectedResults) ? 
        expectedResults.map(result => `<li>${cleanText(result).replace(/\n/g, '<br>')}</li>`).join('\n      ') :
        `<li>${cleanText(guideData.expectedResults)}</li>`
      }
    </ol>
  </div>
  
  <div class="section">
    <h2>五、思考题</h2>
    <ol>
      ${questions.map(q => `<li>${cleanText(q)}</li>`).join('\n      ')}
    </ol>
  </div>
  
  <div class="section">
    <h2>六、参考答案</h2>
    <ol>
      ${answers.map(a => `<li>${cleanText(a).replace(/\n/g, '<br>')}</li>`).join('\n      ')}
    </ol>
  </div>
</body>
</html>
`
    
    // 创建Word格式的Blob（使用HTML，Word可以打开）
    const blob = new Blob([htmlContent], { type: 'application/msword;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    const fileName = `${guideData.title || guideData.chapterName}_实验指导书.doc`
    link.download = fileName.replace(/[<>:"/\\|?*]/g, '_')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    console.log('文档下载成功:', fileName)
  } catch (error) {
    console.error('下载失败', error)
    ElMessage.error('下载失败：' + error.message)
  }
}

// 清理文本，移除转义字符和多余空格
const cleanText = (text) => {
  if (!text) return ''
  return text
    .replace(/\\n/g, '\n')  // 将 \n 转换为真正的换行
    .replace(/\\t/g, '  ')  // 将 \t 转换为空格
    .replace(/\n\s+/g, '\n  ')  // 规范化缩进
    .replace(/\n{3,}/g, '\n\n')  // 移除多余的空行
    .trim()
}

// 解析JSON字符串
const parseJSON = (str) => {
  if (!str) return []
  try {
    return JSON.parse(str)
  } catch {
    return [str]
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.tips {
  margin-top: 8px;
}

.knowledge-points-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.knowledge-point-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e2e8f0;
}

.knowledge-point-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.knowledge-point-card.is-selected {
  border-color: #67C23A;
  background: #f0f9ff;
}

.point-content {
  padding: 4px;
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.point-name {
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
}

.point-description {
  font-size: 12px;
  color: #718096;
  line-height: 1.5;
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>

