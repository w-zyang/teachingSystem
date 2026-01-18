import { createRouter, createWebHistory } from 'vue-router'

// 认证相关页面
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import ChangePwd from '@/views/auth/ChangePwd.vue'

// 封面页面
import Landing from '@/views/common/Landing.vue'
import TestPage from '@/views/common/TestPage.vue'

// 教师侧页面
import TeacherLayout from '@/views/teacher/TeacherLayout.vue'
import TeacherDashboard from '@/views/teacher/Dashboard.vue'
import CreateCourse from '@/views/teacher/CreateCourse.vue'
import AICourseDesign from '@/views/teacher/AICourseDesign.vue'
import ExamGeneration from '@/views/teacher/ExamGeneration.vue'
import StudentAnalysis from '@/views/teacher/StudentAnalysis.vue'
import ResourceManagement from '@/views/teacher/ResourceManagement.vue'
import ClassroomSummary from '@/views/teacher/ClassroomSummary.vue'
import TProfile from '@/views/teacher/TProfile.vue'
import TeacherStudentDashboard from '@/views/teacher/StudentDashboard.vue'

// 学生侧页面
import StudentLayout from '@/views/student/StudentLayout.vue'
import StudentDashboard from '@/views/student/Dashboard.vue'
import OnlineLearning from '@/views/student/OnlineLearning.vue'
import PracticeEvaluation from '@/views/student/PracticeEvaluation.vue'
import ExtendedLearning from '@/views/student/ExtendedLearning.vue'
import LearningAssistant from '@/views/student/LearningAssistant.vue'
import ErrorQuestionTraining from '@/views/student/ErrorQuestionTraining.vue'
import ClassSummaryViewer from '@/views/student/ClassSummaryViewer.vue'
import IntelligentLearning from '@/views/student/IntelligentLearning.vue'
import Profile from '@/views/student/Profile.vue'
import VRClassroom from '@/views/student/VRClassroom.vue'

// 管理侧页面
import AdminLayout from '@/views/admin/AdminLayout.vue'
import AdminDashboard from '@/views/admin/Dashboard.vue'
import UserManagement from '@/views/admin/UserManagement.vue'
import ResourceManagementAdmin from '@/views/admin/ResourceManagement.vue'
import AnalyticsOverview from '@/views/admin/AnalyticsOverview.vue'
import DataDashboard from '@/views/admin/DataDashboard.vue'

// 通用页面
import NotFound from '@/views/common/NotFound.vue'

const routes = [
    { path: '/', redirect: '/landing' },
    { path: '/landing', name: 'landing', component: Landing },
    { path: '/test', name: 'test', component: TestPage },
    
    // 认证路由
    { path: '/login', name: 'login', component: Login },
    { path: '/register', name: 'register', component: Register },
    { path: '/change-pwd', name: 'change-pwd', component: ChangePwd },
    
    // 教师侧路由
    {
        path: '/teacher',
        component: TeacherLayout,
        children: [
            { path: '', redirect: '/teacher/dashboard' },
            { path: 'dashboard', name: 'teacher-dashboard', component: TeacherDashboard },
            { path: 'create-course', name: 'create-course', component: CreateCourse },
            { path: 'ai-course-design', name: 'ai-course-design', component: AICourseDesign },
            { path: 'exam-generation', name: 'exam-generation', component: ExamGeneration },
            { path: 'student-analysis', name: 'student-analysis', component: StudentAnalysis },
            { path: 'student-dashboard', name: 'teacher-student-dashboard', component: TeacherStudentDashboard },
            { path: 'resource-management', name: 'teacher-resource', component: ResourceManagement },
            { path: 'classroom-summary', name: 'classroom-summary', component: ClassroomSummary },
            { path: 'tprofile',name: 'TProfile',component: TProfile}
        ]
    },
    
    // 学生侧路由
    {
        path: '/student',
        component: StudentLayout,
        children: [
            { path: '', redirect: '/student/dashboard' },
            { path: 'dashboard', name: 'student-dashboard', component: StudentDashboard },
            { path: 'online-learning', name: 'online-learning', component: OnlineLearning },
            { path: 'practice-evaluation', name: 'practice-evaluation', component: PracticeEvaluation },
            { path: 'extended-learning', name: 'extended-learning', component: ExtendedLearning },
            { path: 'learning-assistant', name: 'learning-assistant', component: LearningAssistant },
            { path: 'intelligent-learning', name: 'intelligent-learning', component: IntelligentLearning },
            { path: 'error-question-training', name: 'error-question-training', component: ErrorQuestionTraining },
            { path: 'class-summary-viewer', name: 'class-summary-viewer', component: ClassSummaryViewer },
            { path: 'vr-classroom', name: 'vr-classroom', component: VRClassroom },
            { path: 'profile',name: 'Profile',component: Profile}
        ]
    },
    
    // 管理侧路由
    {
        path: '/admin',
        component: AdminLayout,
        children: [
            { path: '', redirect: '/admin/dashboard' },
            { path: 'dashboard', name: 'admin-dashboard', component: AdminDashboard },
            { path: 'users', name: 'user-management', component: UserManagement },
            { path: 'user-management', name: 'user-management-alt', component: UserManagement },
            { path: 'resources', name: 'admin-resource', component: ResourceManagementAdmin },
            { path: 'resource-management', name: 'admin-resource-alt', component: ResourceManagementAdmin },
            { path: 'analytics', name: 'analytics', component: AnalyticsOverview },
            { path: 'analytics-overview', name: 'analytics-overview', component: AnalyticsOverview },
            { path: 'data-dashboard', name: 'admin-data-dashboard', component: DataDashboard },
            { path: 'settings', name: 'admin-settings', component: AdminDashboard },
            { path: 'profile', name: 'admin-profile', component: AdminDashboard }
        ]
    },
    
    // 404页面
    { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFound }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userRole = localStorage.getItem('userRole')
    
    // 不需要认证的页面
    const publicPages = ['/login', '/register', '/landing', '/test']
    
    // 调试信息
    console.log('路由跳转:', {
        to: to.path,
        from: from.path,
        hasToken: !!token,
        userRole: userRole
    })
    
    // 如果访问的是公开页面，直接允许
    if (publicPages.includes(to.path)) {
        console.log('访问公开页面，允许访问:', to.path)
        next()
        return
    }
    
    // 如果没有token，重定向到登录页
    if (!token) {
        console.log('未登录，重定向到登录页')
        next('/login')
        return
    }
    
    // 已登录用户访问登录页，重定向到对应角色的首页
    if (to.path === '/login') {
        console.log('已登录用户访问登录页，重定向到对应角色首页')
        switch (userRole) {
            case 'teacher':
                next('/teacher/dashboard')
                break
            case 'student':
                next('/student/dashboard')
                break
            case 'admin':
                next('/admin/dashboard')
                break
            default:
                console.log('未知角色，重定向到登录页')
                next('/login')
        }
        return
    }
    
    // 访问根路径，重定向到对应角色的首页
    if (to.path === '/') {
        console.log('访问根路径，重定向到对应角色首页')
        switch (userRole) {
            case 'teacher':
                next('/teacher/dashboard')
                break
            case 'student':
                next('/student/dashboard')
                break
            case 'admin':
                next('/admin/dashboard')
                break
            default:
                next('/landing')
        }
        return
    }
    
    // 检查角色权限
    if (userRole) {
        const rolePaths = {
            teacher: ['/teacher'],
            student: ['/student'],
            admin: ['/admin']
        }
        
        const allowedPaths = rolePaths[userRole] || []
        const isAllowedPath = allowedPaths.some(path => to.path.startsWith(path))
        
        if (!isAllowedPath) {
            console.log('权限不足，重定向到对应角色首页')
            switch (userRole) {
                case 'teacher':
                    next('/teacher/dashboard')
                    break
                case 'student':
                    next('/student/dashboard')
                    break
                case 'admin':
                    next('/admin/dashboard')
                    break
                default:
                    next('/landing')
            }
            return
        }
    }
    
    console.log('路由验证通过，允许访问:', to.path)
    next()
})

export default router