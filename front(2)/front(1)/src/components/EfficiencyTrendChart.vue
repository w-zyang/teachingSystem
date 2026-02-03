<template>
  <div class="efficiency-trend-chart">
    <!-- 视图切换和关键指标 -->
    <div class="chart-controls">
      <div class="view-toggle">
        <button 
          :class="['toggle-btn', { active: viewMode === 'week' }]"
          @click="viewMode = 'week'"
        >
          本周
        </button>
        <button 
          :class="['toggle-btn', { active: viewMode === 'month' }]"
          @click="viewMode = 'month'"
        >
          月度
        </button>
      </div>
    </div>

    <!-- 关键指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-label">当前效率</span>
          <div class="trend-indicator" :class="trendClass">
            <img :src="trendIcon" alt="趋势" class="trend-icon">
          </div>
        </div>
        <div class="metric-value">{{ currentEfficiency }}%</div>
        <div class="metric-change" :class="trendClass">
          {{ trendPercentage }}% 较上期
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-label">平均课时</span>
        </div>
        <div class="metric-value">
          {{ avgCourseHours }}
          <span class="metric-unit">{{ viewMode === 'month' ? '节/月' : '节/天' }}</span>
        </div>
        <div class="metric-change positive">+2 较上期</div>
      </div>

      <div class="metric-card">
        <div class="metric-header">
          <span class="metric-label">学生参与度</span>
        </div>
        <div class="metric-value">{{ studentEngagement }}%</div>
        <div class="metric-change positive">+3% 较上期</div>
      </div>
    </div>

    <!-- 主图表 -->
    <div class="chart-wrapper">
      <canvas ref="chartCanvas"></canvas>
    </div>

    <!-- 多维度对比图 -->
    <div v-if="viewMode === 'month'" class="comparison-section">
      <h4 class="section-title">多维度对比</h4>
      <div class="comparison-chart">
        <canvas ref="comparisonCanvas"></canvas>
      </div>
    </div>

    <!-- AI 洞察建议 -->
    <div class="ai-insight">
      <div class="insight-icon">
        <img src="@/assets/category.png" alt="AI洞察" class="icon-img">
      </div>
      <div class="insight-content">
        <h5 class="insight-title">AI 洞察建议</h5>
        <p class="insight-text">
          您的教学效率持续提升，本月达到 <span class="highlight">{{ currentEfficiency }}%</span>。
          建议继续保持周四的高效教学模式，可在周一和周日适当增加互动环节以提升学生参与度。
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import Chart from 'chart.js/auto'
import { getEfficiencyTrend } from '@/api/stats'

// 响应式数据
const viewMode = ref('month')
const chartCanvas = ref(null)
const comparisonCanvas = ref(null)
let mainChart = null
let comparisonChart = null

// 数据改为响应式
const monthlyData = ref([])
const weeklyData = ref([])
const isLoading = ref(false)

// 计算属性
const currentEfficiency = computed(() => {
  const data = viewMode.value === 'month' ? monthlyData.value : weeklyData.value
  if (data.length > 0) {
    return data[data.length - 1].efficiency || 57.33
  }
  return viewMode.value === 'month' ? 57.33 : 57
})

const previousEfficiency = computed(() => {
  const data = viewMode.value === 'month' ? monthlyData.value : weeklyData.value
  if (data.length > 1) {
    return data[data.length - 2].efficiency || 55
  }
  return 55
})

const trendPercentage = computed(() => {
  const trend = currentEfficiency.value - previousEfficiency.value
  return ((trend / previousEfficiency.value) * 100).toFixed(1)
})

const trendClass = computed(() => {
  return parseFloat(trendPercentage.value) > 0 ? 'positive' : 'negative'
})

const trendIcon = computed(() => {
  return parseFloat(trendPercentage.value) > 0 ? '/src/assets/add.png' : '/src/assets/time.png'
})

const avgCourseHours = computed(() => {
  return viewMode.value === 'month' ? 21 : 4.3
})

const studentEngagement = computed(() => {
  const data = viewMode.value === 'month' ? monthlyData.value : weeklyData.value
  if (data.length > 0) {
    return data[data.length - 1].studentEngagement || 75
  }
  return 75
})

// 获取教学效率趋势数据
const fetchEfficiencyData = async () => {
  try {
    isLoading.value = true
    const teacherId = localStorage.getItem('userId') || 2
    
    console.log('获取教学效率趋势数据，教师ID:', teacherId, '类型:', viewMode.value)
    
    const res = await getEfficiencyTrend(teacherId, viewMode.value)
    
    console.log('API响应:', res)
    
    if (res && res.data) {
      if (viewMode.value === 'month' && res.data.monthly) {
        monthlyData.value = res.data.monthly
        console.log('月度数据:', monthlyData.value)
      } else if (viewMode.value === 'week' && res.data.weekly) {
        weeklyData.value = res.data.weekly
        console.log('周度数据:', weeklyData.value)
      }
      
      // 重新创建图表
      await nextTick()
      createMainChart()
      if (viewMode.value === 'month') {
        await nextTick()
        createComparisonChart()
      }
    }
  } catch (error) {
    console.error('获取教学效率趋势失败:', error)
    // 失败时使用默认数据
    useDefaultData()
  } finally {
    isLoading.value = false
  }
}

// 使用默认数据
const useDefaultData = async () => {
  if (viewMode.value === 'month') {
    monthlyData.value = [
      { month: '1月', efficiency: 52, courseCount: 18, studentEngagement: 68 },
      { month: '2月', efficiency: 54, courseCount: 20, studentEngagement: 70 },
      { month: '3月', efficiency: 53, courseCount: 19, studentEngagement: 69 },
      { month: '4月', efficiency: 56, courseCount: 22, studentEngagement: 73 },
      { month: '5月', efficiency: 55, courseCount: 21, studentEngagement: 72 },
      { month: '6月', efficiency: 57.33, courseCount: 24, studentEngagement: 75 },
    ]
  } else {
    weeklyData.value = [
      { day: '周一', efficiency: 55, hours: 4 },
      { day: '周二', efficiency: 58, hours: 5 },
      { day: '周三', efficiency: 56, hours: 4.5 },
      { day: '周四', efficiency: 59, hours: 5.5 },
      { day: '周五', efficiency: 57, hours: 5 },
      { day: '周六', efficiency: 54, hours: 3 },
      { day: '周日', efficiency: 52, hours: 2 },
    ]
  }
  
  // 等待 DOM 更新
  await nextTick()
  
  // 创建图表
  createMainChart()
  if (viewMode.value === 'month') {
    await nextTick()
    createComparisonChart()
  }
}

// 创建主图表
const createMainChart = () => {
  console.log('开始创建主图表...')
  console.log('chartCanvas.value:', chartCanvas.value)
  
  if (!chartCanvas.value) {
    console.error('Canvas 元素不存在！')
    return
  }
  
  // 销毁旧图表
  if (mainChart) {
    console.log('销毁旧图表')
    mainChart.destroy()
  }

  const data = viewMode.value === 'month' ? monthlyData.value : weeklyData.value
  console.log('图表数据:', data)
  
  if (!data || data.length === 0) {
    console.warn('没有数据可显示')
    return
  }
  
  const labels = data.map(item => viewMode.value === 'month' ? item.month : item.day)
  const values = data.map(item => item.efficiency)
  
  console.log('标签:', labels)
  console.log('数值:', values)

  try {
    const ctx = chartCanvas.value.getContext('2d')
    
    // 创建渐变
    const gradient = ctx.createLinearGradient(0, 0, 0, 300)
    gradient.addColorStop(0, 'rgba(102, 126, 234, 0.3)')
    gradient.addColorStop(1, 'rgba(102, 126, 234, 0)')

    mainChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: '教学效率',
          data: values,
          borderColor: '#667eea',
          backgroundColor: gradient,
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointRadius: 5,
          pointBackgroundColor: '#667eea',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          pointHoverRadius: 7,
          pointHoverBackgroundColor: '#667eea',
          pointHoverBorderColor: '#fff',
          pointHoverBorderWidth: 3,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            titleColor: '#2d3748',
            bodyColor: '#4a5568',
            borderColor: '#e2e8f0',
            borderWidth: 1,
            padding: 12,
            displayColors: false,
            callbacks: {
              label: function(context) {
                return `效率: ${context.parsed.y}%`
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            max: 100,
            ticks: {
              callback: function(value) {
                return value + '%'
              },
              color: '#9ca3af',
              font: {
                size: 11
              }
            },
            grid: {
              color: '#f0f0f0',
              drawBorder: false
            }
          },
          x: {
            ticks: {
              color: '#9ca3af',
              font: {
                size: 11
              }
            },
            grid: {
              display: false
            }
          }
        }
      }
    })
    
    console.log('主图表创建成功！', mainChart)
  } catch (error) {
    console.error('创建主图表失败:', error)
  }
}

// 创建对比图表
const createComparisonChart = () => {
  if (!comparisonCanvas.value || viewMode.value !== 'month') return
  
  // 销毁旧图表
  if (comparisonChart) {
    comparisonChart.destroy()
  }

  if (!monthlyData.value || monthlyData.value.length === 0) {
    console.warn('没有月度数据可显示')
    return
  }

  const labels = monthlyData.value.map(item => item.month)
  
  const ctx = comparisonCanvas.value.getContext('2d')
  
  comparisonChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [
        {
          label: '教学效率',
          data: monthlyData.value.map(item => item.efficiency),
          borderColor: '#667eea',
          backgroundColor: 'transparent',
          borderWidth: 2,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#667eea',
        },
        {
          label: '课程数量',
          data: monthlyData.value.map(item => item.courseCount),
          borderColor: '#f59e0b',
          backgroundColor: 'transparent',
          borderWidth: 2,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#f59e0b',
        },
        {
          label: '学生参与度',
          data: monthlyData.value.map(item => item.studentEngagement),
          borderColor: '#10b981',
          backgroundColor: 'transparent',
          borderWidth: 2,
          tension: 0.4,
          pointRadius: 4,
          pointBackgroundColor: '#10b981',
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 15,
            font: {
              size: 11
            }
          }
        },
        tooltip: {
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          titleColor: '#2d3748',
          bodyColor: '#4a5568',
          borderColor: '#e2e8f0',
          borderWidth: 1,
          padding: 12,
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: '#9ca3af',
            font: {
              size: 10
            }
          },
          grid: {
            color: '#f0f0f0',
            drawBorder: false
          }
        },
        x: {
          ticks: {
            color: '#9ca3af',
            font: {
              size: 10
            }
          },
          grid: {
            display: false
          }
        }
      }
    }
  })
}

// 监听视图模式变化
watch(viewMode, async () => {
  await fetchEfficiencyData()
})

// 组件挂载时创建图表
onMounted(async () => {
  // 确保 DOM 已经渲染
  await nextTick()
  
  // 先使用默认数据创建图表
  useDefaultData()
  
  // 然后尝试获取真实数据
  await fetchEfficiencyData()
})
</script>

<style lang="scss" scoped>
.efficiency-trend-chart {
  width: 100%;
}

.chart-controls {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
  
  .view-toggle {
    display: flex;
    gap: 4px;
    background: #f7fafc;
    padding: 4px;
    border-radius: 10px;
    
    .toggle-btn {
      padding: 8px 20px;
      border: none;
      background: transparent;
      color: #718096;
      font-size: 13px;
      font-weight: 600;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        color: #667eea;
      }
      
      &.active {
        background: white;
        color: #667eea;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  
  .metric-card {
    background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
    border-radius: 16px;
    padding: 16px;
    
    .metric-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .metric-label {
        font-size: 13px;
        color: #718096;
        font-weight: 500;
      }
      
      .trend-indicator {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &.positive {
          background: rgba(16, 185, 129, 0.1);
        }
        
        &.negative {
          background: rgba(239, 68, 68, 0.1);
        }
        
        .trend-icon {
          width: 12px;
          height: 12px;
        }
      }
    }
    
    .metric-value {
      font-size: 24px;
      font-weight: 800;
      color: #2d3748;
      margin-bottom: 4px;
      
      .metric-unit {
        font-size: 12px;
        color: #718096;
        font-weight: 500;
        margin-left: 4px;
      }
    }
    
    .metric-change {
      font-size: 11px;
      font-weight: 600;
      
      &.positive {
        color: #10b981;
      }
      
      &.negative {
        color: #ef4444;
      }
    }
  }
}

.chart-wrapper {
  height: 280px;
  margin-bottom: 24px;
  
  canvas {
    width: 100% !important;
    height: 100% !important;
  }
}

.comparison-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  
  .section-title {
    font-size: 14px;
    font-weight: 700;
    color: #2d3748;
    margin: 0 0 16px 0;
  }
  
  .comparison-chart {
    height: 200px;
    
    canvas {
      width: 100% !important;
      height: 100% !important;
    }
  }
}

.ai-insight {
  display: flex;
  gap: 12px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-radius: 16px;
  padding: 16px;
  margin-top: 24px;
  
  .insight-icon {
    width: 36px;
    height: 36px;
    background: #667eea;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    
    .icon-img {
      width: 20px;
      height: 20px;
      opacity: 0.9;
    }
  }
  
  .insight-content {
    flex: 1;
    
    .insight-title {
      font-size: 14px;
      font-weight: 700;
      color: #2d3748;
      margin: 0 0 6px 0;
    }
    
    .insight-text {
      font-size: 12px;
      color: #4a5568;
      line-height: 1.6;
      margin: 0;
      
      .highlight {
        font-weight: 700;
        color: #667eea;
      }
    }
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .chart-wrapper {
    height: 220px;
  }
  
  .comparison-chart {
    height: 180px;
  }
}
</style>

