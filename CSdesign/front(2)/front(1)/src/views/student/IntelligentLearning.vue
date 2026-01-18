<template>
  <div class="intelligent-learning">
    <div class="header">
      <h2>🧠 智能学习中心</h2>
      <p>AI驱动的个性化学习体验</p>
    </div>

    <!-- 功能导航 -->
    <div class="feature-nav">
      <div class="nav-item" 
           :class="{ active: activeTab === 'path' }" 
           @click="activeTab = 'path'">
        <div class="nav-icon">🗺️</div>
        <div class="nav-title">学习路径规划</div>
        <div class="nav-desc">AI定制专属学习路线</div>
      </div>
      
      <div class="nav-item" 
           :class="{ active: activeTab === 'emotion' }" 
           @click="activeTab = 'emotion'">
        <div class="nav-icon">😊</div>
        <div class="nav-title">情绪感知助手</div>
        <div class="nav-desc">智能情绪监测与调节</div>
      </div>
      
      <div class="nav-item" 
           :class="{ active: activeTab === 'companion' }" 
           @click="activeTab = 'companion'">
        <div class="nav-icon">👥</div>
        <div class="nav-title">智能同伴学习</div>
        <div class="nav-desc">AI匹配最佳学习伙伴</div>
      </div>
    </div>

    <!-- 功能内容区 -->
    <div class="content-area">
      <!-- 智能学习路径规划 -->
      <div v-if="activeTab === 'path'" class="feature-content">
        <LearningPathPlanning :student-id="currentUserId" />
      </div>

      <!-- 情绪感知学习助手 -->
      <div v-if="activeTab === 'emotion'" class="feature-content">
        <EmotionAwareLearning :student-id="currentUserId" />
      </div>

      <!-- 智能同伴学习 -->
      <div v-if="activeTab === 'companion'" class="feature-content">
        <CompanionLearning :student-id="currentUserId" />
      </div>
    </div>
  </div>
</template>

<script>
import LearningPathPlanning from './components/LearningPathPlanning.vue'
import EmotionAwareLearning from './components/EmotionAwareLearning.vue'
import CompanionLearning from './components/CompanionLearning.vue'

export default {
  name: 'IntelligentLearning',
  components: {
    LearningPathPlanning,
    EmotionAwareLearning,
    CompanionLearning
  },
  data() {
    return {
      activeTab: 'path',
      currentUserId: 17 // 默认使用学生ID 17
    }
  },
  mounted() {
    // 从localStorage获取当前用户ID
    const userId = localStorage.getItem('userId')
    if (userId) {
      this.currentUserId = parseInt(userId)
    }
    
    console.log('智能学习页面加载，当前用户ID:', this.currentUserId)
  }
}
</script>

<style scoped>
.intelligent-learning {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.header h2 {
  font-size: 2.5rem;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.header p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.feature-nav {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.nav-item {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px 25px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  color: white;
  min-width: 200px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.3);
}

.nav-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.nav-title {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.nav-desc {
  font-size: 0.9rem;
  opacity: 0.8;
  line-height: 1.4;
}

.content-area {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  margin: 0 auto;
  max-width: 1400px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.feature-content {
  min-height: 600px;
}

@media (max-width: 768px) {
  .feature-nav {
    flex-direction: column;
    align-items: center;
  }
  
  .nav-item {
    width: 100%;
    max-width: 300px;
  }
  
  .content-area {
    margin: 0 10px;
    padding: 20px;
  }
  
  .header h2 {
    font-size: 2rem;
  }
}
</style> 