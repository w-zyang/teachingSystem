<template>
  <div class="extended-learning">
    <!-- 页面头部介绍 -->
    <div class="page-header">
      <div class="header-content">
        <h1>扩展学习资源</h1>
        <p>精选优质在线学习平台，助力您的知识拓展与技能提升</p>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">500+</span>
            <span class="stat-label">优质课程</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">50+</span>
            <span class="stat-label">知名讲师</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">10+</span>
            <span class="stat-label">学习平台</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习平台推荐 -->
    <section class="platforms-section">
      <h2 class="section-title">优质学习平台</h2>
      <div class="platforms-grid">
        <div 
          v-for="platform in learningPlatforms" 
          :key="platform.id"
          class="platform-card"
          @click="openPlatform(platform.url)"
        >
          <div class="platform-header">
            <div class="platform-logo">
              <img :src="platform.logo" :alt="platform.name">
            </div>
            <div class="platform-info">
              <h3>{{ platform.name }}</h3>
              <p class="platform-desc">{{ platform.description }}</p>
              <div class="platform-tags">
                <span 
                  v-for="tag in platform.tags" 
                  :key="tag"
                  class="platform-tag"
                >{{ tag }}</span>
              </div>
            </div>
          </div>
          <div class="platform-stats">
            <div class="stat">
              <span class="stat-value">{{ platform.courseCount }}</span>
              <span class="stat-name">课程数</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ platform.rating }}</span>
              <span class="stat-name">评分</span>
            </div>
          </div>
          <div class="platform-footer">
            <button class="access-btn">
              <i class="el-icon-link"></i>
              立即访问
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 推荐课程专区 -->
    <section class="courses-section">
      <h2 class="section-title">精选课程推荐</h2>
      
      <!-- 课程分类标签 -->
      <div class="course-categories">
        <button 
          v-for="category in courseCategories"
          :key="category.id"
          :class="['category-btn', { active: selectedCategory === category.id }]"
          @click="selectCategory(category.id)"
        >
          <i :class="category.icon"></i>
          {{ category.name }}
        </button>
      </div>

      <!-- 课程列表 -->
      <div class="courses-grid">
        <div 
          v-for="course in filteredCourses" 
          :key="course.id"
          class="course-card"
          @click="openCourse(course.url)"
        >
          <div class="course-image">
            <img :src="course.thumbnail" :alt="course.title">
            <div class="course-overlay">
              <div class="play-btn">
                <i class="el-icon-video-play"></i>
              </div>
            </div>
            <div class="course-badges">
              <span v-if="course.isFree" class="badge free">免费</span>
              <span v-if="course.isHot" class="badge hot">热门</span>
              <span v-if="course.isNew" class="badge new">最新</span>
            </div>
          </div>
          <div class="course-content">
            <h4 class="course-title">{{ course.title }}</h4>
            <p class="course-instructor">{{ course.instructor }}</p>
            <div class="course-meta">
              <span class="course-platform">{{ course.platform }}</span>
              <span class="course-duration">{{ course.duration }}</span>
            </div>
            <div class="course-rating">
              <div class="stars">
                <i 
                  v-for="i in 5" 
                  :key="i"
                  :class="['star', i <= course.rating ? 'filled' : '']"
                  class="el-icon-star-on"
                ></i>
              </div>
              <span class="rating-text">{{ course.rating }}/5</span>
              <span class="student-count">({{ course.studentCount }}人学习)</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 学习工具推荐 -->
    <section class="tools-section">
      <h2 class="section-title">学习工具推荐</h2>
      <div class="tools-grid">
        <div 
          v-for="tool in learningTools" 
          :key="tool.id"
          class="tool-card"
          @click="openTool(tool.url)"
        >
          <div class="tool-icon">
            <img :src="tool.icon" :alt="tool.name">
          </div>
          <h4>{{ tool.name }}</h4>
          <p>{{ tool.description }}</p>
          <div class="tool-features">
            <span 
              v-for="feature in tool.features" 
              :key="feature"
              class="feature-tag"
            >{{ feature }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const selectedCategory = ref('all')

// 学习平台数据
const learningPlatforms = ref([
  {
    id: 1,
    name: 'B站 (bilibili)',
    description: '国内最大的视频学习平台，海量编程、设计、技术课程',
    logo: '/src/assets/logo.png',
    url: 'https://www.bilibili.com',
    tags: ['视频课程', '免费', '互动性强'],
    courseCount: '10万+',
    rating: '4.8'
  },
  {
    id: 2,
    name: '中国大学MOOC',
    description: '汇集清华、北大等名校课程，权威性高',
    logo: '/src/assets/author.png',
    url: 'https://www.icourse163.org/',
    tags: ['名校课程', '学分认证', '权威'],
    courseCount: '5000+',
    rating: '4.7'
  },
  {
    id: 3,
    name: '慕课网',
    description: '专业的IT技能学习平台，实战项目丰富',
    logo: '/src/assets/category.png',
    url: 'https://www.imooc.com',
    tags: ['IT技能', '实战项目', '就业导向'],
    courseCount: '3000+',
    rating: '4.6'
  },
  {
    id: 4,
    name: '网易云课堂',
    description: '综合性在线学习平台，涵盖多个领域',
    logo: '/src/assets/balance.png',
    url: 'https://study.163.com',
    tags: ['综合性', '职业技能', '证书'],
    courseCount: '8000+',
    rating: '4.5'
  },
  {
    id: 5,
    name: '腾讯课堂',
    description: '腾讯旗下在线教育平台，职业培训为主',
    logo: '/src/assets/search.png',
    url: 'https://ke.qq.com',
    tags: ['职业培训', '直播教学', '互动'],
    courseCount: '12000+',
    rating: '4.4'
  },
  {
    id: 6,
    name: 'Coursera',
    description: '全球顶尖大学和公司的在线课程平台',
    logo: '/src/assets/time.png',
    url: 'https://www.coursera.org',
    tags: ['国际化', '证书', '英文'],
    courseCount: '4000+',
    rating: '4.9'
  }
])

// 课程分类
const courseCategories = ref([
  { id: 'all', name: '全部', icon: 'el-icon-menu' },
  { id: 'programming', name: '编程开发', icon: 'el-icon-cpu' },
  { id: 'design', name: '设计创意', icon: 'el-icon-brush' },
  { id: 'data', name: '数据分析', icon: 'el-icon-pie-chart' },
  { id: 'ai', name: '人工智能', icon: 'el-icon-magic-stick' },
  { id: 'business', name: '商业管理', icon: 'el-icon-suitcase' }
])

// 推荐课程数据
const recommendedCourses = ref([
  {
    id: 1,
    title: 'Java零基础入门到精通',
    instructor: '黑马程序员',
    platform: 'B站',
    duration: '120小时',
    rating: 5,
    studentCount: '85万',
    thumbnail: '/src/assets/logo.png',
    url: 'https://www.bilibili.com/video/BV1Cv411372m',
    category: 'programming',
    isFree: true,
    isHot: true,
    isNew: false
  },
  {
    id: 2,
    title: 'Python数据分析与机器学习',
    instructor: '北京大学',
    platform: '中国大学MOOC',
    duration: '80小时',
    rating: 5,
    studentCount: '12万',
    thumbnail: '/src/assets/author.png',
    url: 'https://www.icourse163.org/',
    category: 'ai',
    isFree: true,
    isHot: false,
    isNew: true
  },
  {
    id: 3,
    title: 'Vue.js框架从入门到实战',
    instructor: '慕课网',
    platform: '慕课网',
    duration: '60小时',
    rating: 4,
    studentCount: '5万',
    thumbnail: '/src/assets/category.png',
    url: 'https://www.imooc.com/learn/694',
    category: 'programming',
    isFree: false,
    isHot: true,
    isNew: false
  },
  {
    id: 4,
    title: 'UI设计系统化学习',
    instructor: '站酷高高手',
    platform: '网易云课堂',
    duration: '100小时',
    rating: 4,
    studentCount: '8万',
    thumbnail: '/src/assets/balance.png',
    url: 'https://study.163.com/course/courseMain.htm?courseId=1006304024',
    category: 'design',
    isFree: false,
    isHot: false,
    isNew: true
  },
  {
    id: 5,
    title: 'SQL数据库从零到高级',
    instructor: '尚硅谷',
    platform: 'B站',
    duration: '90小时',
    rating: 5,
    studentCount: '35万',
    thumbnail: '/src/assets/search.png',
    url: 'https://www.bilibili.com/video/BV1iq4y1u7vj',
    category: 'data',
    isFree: true,
    isHot: true,
    isNew: false
  },
  {
    id: 6,
    title: '商业分析与决策',
    instructor: '清华大学',
    platform: '中国大学MOOC',
    duration: '50小时',
    rating: 4,
    studentCount: '6万',
    thumbnail: '/src/assets/time.png',
    url: 'https://www.icourse163.org/',
    category: 'business',
    isFree: true,
    isHot: false,
    isNew: false
  }
])

// 学习工具数据
const learningTools = ref([
  {
    id: 1,
    name: 'GitHub',
    description: '代码托管和版本控制平台',
    icon: '/src/assets/logo.png',
    url: 'https://github.com',
    features: ['代码管理', '协作开发', '开源项目']
  },
  {
    id: 2,
    name: 'CodePen',
    description: '在线代码编辑器和前端展示平台',
    icon: '/src/assets/author.png',
    url: 'https://codepen.io',
    features: ['在线编程', '代码分享', '前端展示']
  },
  {
    id: 3,
    name: 'Figma',
    description: '在线协作式界面设计工具',
    icon: '/src/assets/category.png',
    url: 'https://www.figma.com',
    features: ['UI设计', '团队协作', '原型制作']
  },
  {
    id: 4,
    name: 'Jupyter Notebook',
    description: '数据科学和机器学习开发环境',
    icon: '/src/assets/balance.png',
    url: 'https://jupyter.org',
    features: ['数据分析', '可视化', '交互式编程']
  }
])

// 计算属性：过滤课程
const filteredCourses = computed(() => {
  if (selectedCategory.value === 'all') {
    return recommendedCourses.value
  }
  return recommendedCourses.value.filter(course => course.category === selectedCategory.value)
})

// 方法
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
}

const openPlatform = (url) => {
  window.open(url, '_blank')
  ElMessage.success('正在打开学习平台...')
}

const openCourse = (url) => {
  window.open(url, '_blank')
  ElMessage.success('正在打开课程页面...')
}

const openTool = (url) => {
  window.open(url, '_blank')
  ElMessage.success('正在打开学习工具...')
}

// 页面加载
onMounted(() => {
  console.log('扩展学习页面已加载')
})
</script>

<style scoped>
.extended-learning {
  padding: 0;
  max-width: 100%;
  margin: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="20" r="2" fill="rgba(255,255,255,0.1)"/></svg>');
  pointer-events: none;
}

.header-content {
  position: relative;
  z-index: 1;
}

.page-header h1 {
  font-size: 3rem;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.page-header p {
  font-size: 1.2rem;
  margin: 0 0 40px 0;
  opacity: 0.9;
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 1rem;
  opacity: 0.8;
}

/* 通用样式 */
.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 30px 0;
  text-align: center;
  position: relative;
}

.section-title::after {
  content: '';
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: block;
  margin: 15px auto 0;
  border-radius: 2px;
}

/* 学习平台部分 */
.platforms-section {
  padding: 80px 40px;
  background: #f8f9fa;
}

.platforms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.platform-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.platform-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.platform-header {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
}

.platform-logo {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.platform-logo img {
  width: 32px;
  height: 32px;
  filter: brightness(0) invert(1);
}

.platform-info h3 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.platform-desc {
  color: #6c757d;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.platform-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.platform-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.platform-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 25px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 15px;
}

.stat {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 1.8rem;
  font-weight: 700;
  color: #667eea;
}

.stat-name {
  font-size: 0.9rem;
  color: #6c757d;
}

.access-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 15px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.access-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 课程推荐部分 */
.courses-section {
  padding: 80px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.course-categories {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 50px;
  flex-wrap: wrap;
}

.category-btn {
  background: white;
  border: 2px solid #e9ecef;
  color: #6c757d;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.category-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
}

.course-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.05);
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.play-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #667eea;
}

.course-badges {
  position: absolute;
  top: 15px;
  right: 15px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
  text-align: center;
}

.badge.free {
  background: #4caf50;
  color: white;
}

.badge.hot {
  background: #ff5722;
  color: white;
}

.badge.new {
  background: #2196f3;
  color: white;
}

.course-content {
  padding: 25px;
}

.course-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.course-instructor {
  color: #6c757d;
  margin: 0 0 15px 0;
  font-weight: 500;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 0.9rem;
}

.course-platform {
  color: #667eea;
  font-weight: 600;
}

.course-duration {
  color: #6c757d;
}

.course-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #ddd;
  font-size: 16px;
}

.star.filled {
  color: #ffc107;
}

.rating-text {
  font-weight: 600;
  color: #2c3e50;
}

.student-count {
  color: #6c757d;
}

/* 学习工具部分 */
.tools-section {
  padding: 80px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.tools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.tool-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.tool-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.tool-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  padding: 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tool-icon img {
  width: 40px;
  height: 40px;
  filter: brightness(0) invert(1);
}

.tool-card h4 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 15px 0;
}

.tool-card p {
  color: #6c757d;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.tool-features {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.feature-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 40px 20px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .header-stats {
    gap: 30px;
  }
  
  .platforms-section,
  .courses-section,
  .tools-section {
    padding: 40px 20px;
  }
  
  .platforms-grid,
  .courses-grid,
  .tools-grid {
    grid-template-columns: 1fr;
  }
  
  .course-categories {
    gap: 10px;
  }
  
  .category-btn {
    padding: 10px 16px;
    font-size: 0.9rem;
  }
  
  .platform-header {
    flex-direction: column;
    text-align: center;
  }
  
  .platform-stats {
    justify-content: space-around;
  }
}
</style> 