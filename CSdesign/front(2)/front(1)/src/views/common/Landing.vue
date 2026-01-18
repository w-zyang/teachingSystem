<template>
  <div class="landing-container">
    <!-- 粒子背景 -->
    <div class="particles-container">
      <div class="particle" v-for="n in 50" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
      <div class="floating-shape shape-5"></div>
      <div class="floating-shape shape-6"></div>
      <div class="floating-shape shape-7"></div>
    </div>
    
    <div class="landing-content">
      <header class="landing-header">
        <div class="logo-section">
          <div class="logo-container">
            <img src="@/assets/logo.png" alt="Logo" class="logo">
            <div class="logo-glow"></div>
          </div>
          <div class="brand-info">
            <h1 class="brand-name">智能教学管理系统</h1>
            <p class="brand-tagline">让学习更智能，让教育更高效</p>
          </div>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/login" class="nav-link login-link">
            <i class="fas fa-sign-in-alt"></i>
            <span>登录</span>
          </router-link>
          <router-link to="/register" class="nav-link register-link">
            <i class="fas fa-user-plus"></i>
            <span>注册</span>
          </router-link>
        </nav>
      </header>
      
      <main class="landing-main">
        <section class="hero-section">
          <div class="hero-content">
            <h2 class="hero-title">
              面向大学生的
              <span class="highlight">智能教学管理平台</span>
            </h2>
            <div class="hero-badge">
              <i class="fas fa-star"></i>
              <span>AI驱动的智能教学平台</span>
            </div>
            <p class="hero-subtitle">
              提供多专业课程学习，智能教学管理服务，让学习更高效、更智能
            </p>
            <div class="hero-stats">
              <div class="stat-item">
                <div class="stat-number">1000+</div>
                <div class="stat-label">活跃用户</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">50+</div>
                <div class="stat-label">专业课程</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">99%</div>
                <div class="stat-label">满意度</div>
              </div>
            </div>
            <div class="hero-actions">
              <router-link to="/register" class="btn btn-primary hero-btn">
                <i class="fas fa-rocket"></i>
                <span>立即开始</span>
                <div class="btn-glow"></div>
              </router-link>
              <router-link to="/test" class="btn btn-secondary hero-btn">
                <i class="fas fa-play"></i>
                <span>免费体验</span>
              </router-link>
            </div>
          </div>
          
          <div class="hero-visual">
            <div class="feature-cards">
              <div class="feature-card card-1">
                <div class="card-icon">
                  <i class="fas fa-graduation-cap"></i>
                </div>
                <h3>多专业课程</h3>
                <p>覆盖计算机、软件、网络等专业</p>
                <div class="card-hover-effect"></div>
              </div>
              <div class="feature-card card-2">
                <div class="card-icon">
                  <i class="fas fa-brain"></i>
                </div>
                <h3>智能管理</h3>
                <p>AI驱动的学习分析和推荐</p>
                <div class="card-hover-effect"></div>
              </div>
              <div class="feature-card card-3">
                <div class="card-icon">
                  <i class="fas fa-users"></i>
                </div>
                <h3>多角色支持</h3>
                <p>教师、学生、管理员一体化</p>
                <div class="card-hover-effect"></div>
              </div>
            </div>
          </div>
        </section>
        
        <section class="features-section">
          <div class="section-header">
            <h2 class="section-title">核心功能</h2>
            <p class="section-subtitle">为您提供全方位的智能教学解决方案</p>
          </div>
          <div class="features-grid">
            <div class="feature-item" v-for="(feature, index) in features" :key="index" @mouseenter="animateFeature" @mouseleave="resetFeature">
              <div class="feature-icon-wrapper">
                <i :class="feature.icon"></i>
              </div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
              <div class="feature-hover-effect"></div>
            </div>
          </div>
        </section>
        
        <section class="cta-section">
          <div class="cta-content">
            <div class="cta-badge">
              <i class="fas fa-gift"></i>
              <span>限时优惠</span>
            </div>
            <h2>准备好开始您的学习之旅了吗？</h2>
            <p>加入我们，体验智能教学管理带来的便利</p>
            <router-link to="/register" class="btn btn-primary cta-btn">
              <i class="fas fa-rocket"></i>
              <span>立即注册</span>
              <div class="btn-glow"></div>
            </router-link>
          </div>
        </section>
      </main>
      
      <footer class="landing-footer">
        <div class="footer-content">
          <div class="footer-section">
            <h4>关于我们</h4>
            <p>智能教学管理系统致力于为大学生提供优质的在线学习体验</p>
            <div class="social-links">
              <a href="#" class="social-link"><i class="fab fa-weixin"></i></a>
              <a href="#" class="social-link"><i class="fab fa-qq"></i></a>
              <a href="#" class="social-link"><i class="fab fa-weibo"></i></a>
            </div>
          </div>
          <div class="footer-section">
            <h4>联系方式</h4>
            <div class="contact-info">
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <span>support@example.com</span>
              </div>
              <div class="contact-item">
                <i class="fas fa-phone"></i>
                <span>400-123-4567</span>
              </div>
              <div class="contact-item">
                <i class="fas fa-map-marker-alt"></i>
                <span>北京市海淀区</span>
              </div>
            </div>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <div class="footer-links">
              <router-link to="/login">登录</router-link>
              <router-link to="/register">注册</router-link>
              <router-link to="/test">体验</router-link>
              <router-link to="/help">帮助中心</router-link>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 智能教学管理系统. 保留所有权利.</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const features = ref([
  {
    icon: 'fas fa-book',
    title: '课程管理',
    description: '智能课程设计、内容管理和学习进度跟踪'
  },
  {
    icon: 'fas fa-chart-line',
    title: '练习评估',
    description: '多样化练习题型，实时评估和成绩分析'
  },
  {
    icon: 'fas fa-user-cog',
    title: '用户管理',
    description: '完善的用户权限管理和个人信息维护'
  },
  {
    icon: 'fas fa-robot',
    title: '学习助手',
    description: 'AI智能问答，个性化学习建议和指导'
  },
  {
    icon: 'fas fa-chart-bar',
    title: '数据分析',
    description: '学习数据可视化，教学效果分析报告'
  },
  {
    icon: 'fas fa-eye',
    title: '实时监控',
    description: '学习行为监控，教学质量实时反馈'
  }
])

const getParticleStyle = (index) => {
  const size = Math.random() * 4 + 2
  const x = Math.random() * 100
  const y = Math.random() * 100
  const delay = Math.random() * 20
  const duration = Math.random() * 10 + 10
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${x}%`,
    top: `${y}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}

const animateFeature = (event) => {
  const feature = event.currentTarget
  feature.style.transform = 'translateY(-10px)'
}

const resetFeature = (event) => {
  const feature = event.currentTarget
  feature.style.transform = 'translateY(0)'
}

onMounted(() => {
  console.log('Landing页面已加载')
})
</script>

<style lang="scss" scoped>
.landing-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
}

/* 粒子背景 */
.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float-particle linear infinite;
}

@keyframes float-particle {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
  
  &.shape-1 {
    width: 100px;
    height: 100px;
    top: 10%;
    left: 5%;
    animation-delay: 0s;
  }
  
  &.shape-2 {
    width: 150px;
    height: 150px;
    top: 20%;
    right: 10%;
    animation-delay: 2s;
  }
  
  &.shape-3 {
    width: 80px;
    height: 80px;
    bottom: 30%;
    left: 15%;
    animation-delay: 4s;
  }
  
  &.shape-4 {
    width: 120px;
    height: 120px;
    bottom: 20%;
    right: 20%;
    animation-delay: 1s;
  }
  
  &.shape-5 {
    width: 60px;
    height: 60px;
    top: 60%;
    left: 50%;
    animation-delay: 3s;
  }
  
  &.shape-6 {
    width: 90px;
    height: 90px;
    top: 40%;
    left: 80%;
    animation-delay: 5s;
  }
  
  &.shape-7 {
    width: 70px;
    height: 70px;
    bottom: 10%;
    left: 60%;
    animation-delay: 6s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.landing-content {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.landing-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px 0;
  
  .logo-section {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .logo-container {
      position: relative;
      display: inline-block;
    }
    
    .logo {
      width: 70px;
      height: 70px;
      border-radius: 20px;
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
      transition: transform 0.3s ease;
      position: relative;
      z-index: 2;
      
      &:hover {
        transform: rotate(5deg) scale(1.1);
      }
    }
    
    .logo-glow {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 90px;
      height: 90px;
      background: radial-gradient(circle, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
      border-radius: 50%;
      animation: pulse 2s ease-in-out infinite;
    }
    

    
    .brand-info {
      .brand-name {
        font-size: 28px;
        font-weight: 900;
        color: white;
        margin: 0 0 4px 0;
        background: linear-gradient(135deg, #ffffff, #f0f0f0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
      
      .brand-tagline {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        margin: 0;
        font-weight: 500;
      }
    }
  }
  
  .nav-menu {
    display: flex;
    gap: 16px;
    
    .nav-link {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 14px 28px;
      border-radius: 16px;
      text-decoration: none;
      font-weight: 600;
      font-size: 16px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      i {
        font-size: 16px;
      }
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
        transition: left 0.5s ease;
      }
      
      &:hover::before {
        left: 100%;
      }
      
      &.login-link {
        background: rgba(255, 255, 255, 0.15);
        color: white;
        backdrop-filter: blur(15px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateY(-3px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
        }
      }
      
      &.register-link {
        background: linear-gradient(135deg, #ffd700, #ffed4e);
        color: #2d3748;
        
        &:hover {
          background: linear-gradient(135deg, #ffed4e, #ffd700);
          transform: translateY(-3px);
          box-shadow: 0 12px 32px rgba(255, 215, 0, 0.4);
        }
      }
    }
  }
}

.landing-main {
  padding: 40px 0;
}

.hero-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
  margin-bottom: 120px;
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 60px;
  }
  
  .hero-content {
    .hero-title {
      font-size: 52px;
      font-weight: 900;
      color: white;
      margin-bottom: 24px;
      line-height: 1.1;
      
      .highlight {
        background: linear-gradient(135deg, #ffd700, #ffed4e);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 0;
          width: 100%;
          height: 3px;
          background: linear-gradient(135deg, #ffd700, #ffed4e);
          border-radius: 2px;
        }
      }
      
      @media (max-width: 768px) {
        font-size: 40px;
      }
    }
    
    .hero-subtitle {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 32px;
      line-height: 1.6;
      
      @media (max-width: 768px) {
        font-size: 18px;
      }
    }
    
    .hero-stats {
      display: flex;
      gap: 32px;
      margin-bottom: 40px;
      
      @media (max-width: 768px) {
        gap: 20px;
      }
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          font-size: 32px;
          font-weight: 900;
          color: #ffd700;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
          font-weight: 500;
        }
      }
    }
    
    .hero-actions {
      display: flex;
      gap: 20px;
      
      @media (max-width: 768px) {
        flex-direction: column;
        gap: 16px;
      }
      
      .hero-btn {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 18px 36px;
        border-radius: 16px;
        text-decoration: none;
        font-weight: 700;
        font-size: 16px;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        
        i {
          font-size: 18px;
        }
        
        .btn-glow {
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
          transition: left 0.5s ease;
        }
        
        &:hover .btn-glow {
          left: 100%;
        }
        
        &.btn-primary {
          background: linear-gradient(135deg, #ffd700, #ffed4e);
          color: #2d3748;
          
          &:hover {
            transform: translateY(-4px);
            box-shadow: 0 16px 40px rgba(255, 215, 0, 0.4);
          }
        }
        
        &.btn-secondary {
          background: rgba(255, 255, 255, 0.15);
          color: white;
          backdrop-filter: blur(15px);
          border: 1px solid rgba(255, 255, 255, 0.3);
          
          &:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-4px);
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
          }
        }
      }
    }
  }
  
  .hero-visual {
    .feature-cards {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 24px;
      
      .feature-card {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(20px);
        border-radius: 20px;
        padding: 32px 24px;
        text-align: center;
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
        transition: all 0.4s ease;
        border: 1px solid rgba(255, 255, 255, 0.2);
        position: relative;
        overflow: hidden;
        
        .card-hover-effect {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
          opacity: 0;
          transition: opacity 0.3s ease;
        }
        
        &:hover .card-hover-effect {
          opacity: 1;
        }
        
        &.card-1 {
          animation: slideInUp 0.6s ease-out;
        }
        
        &.card-2 {
          animation: slideInUp 0.6s ease-out 0.2s both;
        }
        
        &.card-3 {
          grid-column: 1 / -1;
          animation: slideInUp 0.6s ease-out 0.4s both;
        }
        
        .card-icon {
          width: 60px;
          height: 60px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 20px;
          
          i {
            font-size: 24px;
            color: white;
          }
        }
        
        h3 {
          font-size: 20px;
          font-weight: 700;
          color: #2d3748;
          margin-bottom: 12px;
        }
        
        p {
          font-size: 14px;
          color: #718096;
          line-height: 1.5;
          margin: 0;
        }
      }
    }
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}



.features-section {
  margin-bottom: 120px;
  
  .section-header {
    text-align: center;
    margin-bottom: 80px;
    
    .section-title {
      font-size: 42px;
      font-weight: 900;
      color: white;
      margin-bottom: 16px;
      
      @media (max-width: 768px) {
        font-size: 32px;
      }
    }
    
    .section-subtitle {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.8);
      max-width: 600px;
      margin: 0 auto;
      line-height: 1.6;
    }
  }
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 32px;
    
    .feature-item {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(20px);
      border-radius: 24px;
      padding: 48px 32px;
      text-align: center;
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
      transition: all 0.4s ease;
      border: 1px solid rgba(255, 255, 255, 0.2);
      position: relative;
      overflow: hidden;
      
      .feature-hover-effect {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover .feature-hover-effect {
        opacity: 1;
      }
      
              .feature-icon-wrapper {
          width: 90px;
          height: 90px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 28px;
          position: relative;
        
        i {
          font-size: 36px;
          color: white;
        }
      }
      
      h3 {
        font-size: 26px;
        font-weight: 700;
        color: #2d3748;
        margin-bottom: 16px;
      }
      
      p {
        font-size: 16px;
        color: #718096;
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}

.cta-section {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 32px;
  padding: 80px 40px;
  text-align: center;
  margin-bottom: 100px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.1), rgba(255, 237, 78, 0.1));
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  &:hover::before {
    opacity: 1;
  }
  
  .cta-content {
    position: relative;
    z-index: 2;
    
    .cta-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 8px 16px;
      background: rgba(255, 215, 0, 0.2);
      border: 1px solid rgba(255, 215, 0, 0.3);
      border-radius: 20px;
      color: #ffd700;
      font-size: 14px;
      font-weight: 600;
      margin-top: 24px;
      
      i {
        font-size: 12px;
      }
    }
    
    h2 {
      font-size: 42px;
      font-weight: 900;
      color: white;
      margin-bottom: 16px;
      
      @media (max-width: 768px) {
        font-size: 32px;
      }
    }
    
    p {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 40px;
      
      @media (max-width: 768px) {
        font-size: 18px;
      }
    }
    
    .cta-btn {
      display: inline-flex;
      align-items: center;
      gap: 12px;
      padding: 20px 48px;
      background: linear-gradient(135deg, #ffd700, #ffed4e);
      color: #2d3748;
      text-decoration: none;
      border-radius: 16px;
      font-weight: 700;
      font-size: 18px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      
      i {
        font-size: 20px;
      }
      
      .btn-glow {
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
        transition: left 0.5s ease;
      }
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 16px 48px rgba(255, 215, 0, 0.4);
        
        .btn-glow {
          left: 100%;
        }
      }
    }
  }
}

.landing-footer {
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(20px);
  border-radius: 32px 32px 0 0;
  padding: 80px 40px 30px;
  margin-top: 100px;
  
  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 48px;
    margin-bottom: 48px;
    
    .footer-section {
      h4 {
        font-size: 22px;
        font-weight: 700;
        color: white;
        margin-bottom: 20px;
      }
      
      p {
        color: rgba(255, 255, 255, 0.8);
        line-height: 1.6;
        margin: 0 0 20px 0;
      }
      
      .social-links {
        display: flex;
        gap: 16px;
        
        .social-link {
          width: 40px;
          height: 40px;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          text-decoration: none;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
          }
          
          i {
            font-size: 18px;
          }
        }
      }
      
      .contact-info {
        .contact-item {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;
          
          i {
            width: 20px;
            color: #ffd700;
            font-size: 16px;
          }
          
          span {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }
      
      .footer-links {
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        a {
          color: rgba(255, 255, 255, 0.8);
          text-decoration: none;
          transition: color 0.3s ease;
          font-weight: 500;
          
          &:hover {
            color: #ffd700;
          }
        }
      }
    }
  }
  
  .footer-bottom {
    text-align: center;
    padding-top: 30px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    
    p {
      color: rgba(255, 255, 255, 0.6);
      margin: 0;
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

@media (max-width: 768px) {
  .landing-header {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .hero-section {
    text-align: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-stats {
    justify-content: center;
  }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 28px;
  background: rgba(255, 255, 255, 0.18);
  border: 2px solid #bdbdbd;
  border-radius: 999px;
  color: #ffd700;
  font-size: 18px;
  font-weight: 700;
  margin-top: 24px;
  box-shadow: none;
  letter-spacing: 1px;
  transition: none;
  user-select: none;
}
.hero-badge i {
  color: #ffd700;
  font-size: 20px;
}
</style> 