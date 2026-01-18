<template>
  <div class="vr-demo">
    <div class="demo-header">
      <h2><i class="fas fa-vr-cardboard"></i> VR虚拟教室演示</h2>
      <p>体验沉浸式学习的未来</p>
    </div>
    
    <div class="demo-features">
      <div class="feature-card" v-for="feature in features" :key="feature.id">
        <div class="feature-icon">
          <i :class="feature.icon"></i>
        </div>
        <h3>{{ feature.title }}</h3>
        <p>{{ feature.description }}</p>
        <button @click="demoFeature(feature)" class="demo-btn">
          {{ feature.buttonText }}
        </button>
      </div>
    </div>
    
    <div class="demo-stats">
      <h3>VR学习数据</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.courseCompleted }}</div>
          <div class="stat-label">已完成课程</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.averageScore }}%</div>
          <div class="stat-label">平均分数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.learningHours }}h</div>
          <div class="stat-label">学习时长</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const features = ref([
  {
    id: 1,
    title: '3D虚拟环境',
    description: '沉浸式的3D教室环境，仿真真实课堂体验',
    icon: 'fas fa-cube',
    buttonText: '体验3D环境'
  },
  {
    id: 2,
    title: '实时互动',
    description: '与老师和同学实时交流，举手发言，互动讨论',
    icon: 'fas fa-users',
    buttonText: '开始互动'
  },
  {
    id: 3,
    title: '虚拟实验',
    description: '在虚拟实验室中进行安全的实验操作',
    icon: 'fas fa-flask',
    buttonText: '进入实验室'
  },
  {
    id: 4,
    title: '空间学习',
    description: '利用空间记忆提升学习效果',
    icon: 'fas fa-brain',
    buttonText: '体验空间学习'
  }
])

const stats = ref({
  totalUsers: 1250,
  courseCompleted: 3280,
  averageScore: 87,
  learningHours: 15600
})

const demoFeature = (feature) => {
  ElMessage.success(`正在演示：${feature.title}`)
  
  switch (feature.id) {
    case 1:
      ElMessage.info('3D环境加载中...您将看到立体的教室场景')
      break
    case 2:
      ElMessage.info('连接到虚拟教室，准备与其他学习者互动')
      break
    case 3:
      ElMessage.info('虚拟实验室启动中，安全第一！')
      break
    case 4:
      ElMessage.info('激活空间记忆模式，增强学习体验')
      break
  }
}

onMounted(() => {
  // 动画效果
  ElMessage.info('VR演示模块已加载，点击按钮体验各项功能')
})
</script>

<style scoped>
.vr-demo {
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: white;
}

.demo-header {
  text-align: center;
  margin-bottom: 50px;
}

.demo-header h2 {
  font-size: 2.5rem;
  margin: 0 0 15px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.demo-header p {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0;
}

.demo-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-card:hover {
  transform: translateY(-10px);
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px auto;
  font-size: 2rem;
  color: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.feature-card h3 {
  font-size: 1.5rem;
  margin: 0 0 15px 0;
  color: white;
}

.feature-card p {
  font-size: 1rem;
  line-height: 1.6;
  margin: 0 0 20px 0;
  opacity: 0.9;
}

.demo-btn {
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.demo-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.demo-stats {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.demo-stats h3 {
  font-size: 2rem;
  margin: 0 0 30px 0;
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #f39c12;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.stat-label {
  font-size: 1rem;
  opacity: 0.9;
  color: white;
}

@media (max-width: 768px) {
  .vr-demo {
    padding: 20px;
  }
  
  .demo-header h2 {
    font-size: 2rem;
  }
  
  .demo-features {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 20px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .stat-value {
    font-size: 2rem;
  }
}
</style> 