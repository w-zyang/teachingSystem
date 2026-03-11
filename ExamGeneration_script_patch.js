// ========================================
// 在 ExamGeneration.vue 的 script 部分添加以下代码
// ========================================

// 1. 在 import 部分添加
import * as smartTeachingApi from '@/api/smartTeaching'

// 2. 在 data() 或 setup() 中添加以下配置对象

// 实验指导书配置
const experimentConfig = reactive({
  chapterName: '',
  knowledgePoints: [],
  difficultyLevel: 'intermediate'
})

// 大作业配置
const assignmentConfig = reactive({
  title: '',
  description: '',
  submitTypes: ['file'],
  totalScore: 100
})

// 3. 修改 examConfig 的 type 默认值
// 将 type: 'choice' 改为 type: 'exam'

// 4. 添加类型切换方法
const onTypeChange = (type) => {
  console.log('考核类型切换为:', type)
  // 重置配置
  if (type === 'experiment') {
    experimentConfig.chapterName = ''
    experimentConfig.knowledgePoints = []
    experimentConfig.difficultyLevel = 'intermediate'
  } else if (type === 'assignment') {
    assignmentConfig.title = ''
    assignmentConfig.description = ''
    assignmentConfig.submitTypes = ['file']
    assignmentConfig.totalScore = 100
  }
}

// 5. 修改 goToStep 方法，处理不同类型
const goToStep = async (step) => {
  if (step === 2) {
    // 如果是实验指导书类型，直接生成
    if (examConfig.type === 'experiment') {
      await generateExperimentGuide()
      return
    }
    // 如果是大作业类型，直接生成
    if (examConfig.type === 'assignment') {
      await generateAssignment()
      return
    }
    // 如果是考试类型，进入下一步配置题目
    wizardStep.value = step
  } else {
    wizardStep.value = step
  }
}

// 6. 添加生成实验指导书方法
const generateExperimentGuide = async () => {
  // 验证必填项
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!experimentConfig.chapterName) {
    ElMessage.warning('请输入章节名称')
    return
  }
  if (experimentConfig.knowledgePoints.length === 0) {
    ElMessage.warning('请选择至少一个知识点')
    return
  }

  try {
    generating.value = true
    const teacherId = localStorage.getItem('userId') || '2'
    
    const res = await smartTeachingApi.generateExperimentGuide({
      teacherId: parseInt(teacherId),
      courseId: selectedCourseId.value,
      chapterName: experimentConfig.chapterName,
      knowledgePoints: experimentConfig.knowledgePoints,
      difficultyLevel: experimentConfig.difficultyLevel
    })

    if (res.code === 1 || res.success === true) {
      ElMessage.success('实验指导书生成中，请稍候...')
      
      // 轮询检查状态
      const guideId = res.data
      checkExperimentStatus(guideId)
    } else {
      ElMessage.error('生成失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('生成失败', error)
    ElMessage.error('生成失败：' + (error.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

// 7. 添加检查实验指导书生成状态方法
const checkExperimentStatus = async (guideId) => {
  let attempts = 0
  const maxAttempts = 20
  
  const check = async () => {
    try {
      const res = await smartTeachingApi.getExperimentGuide(guideId)
      if (res.code === 1 && res.data.status === 'completed') {
        ElMessage.success('实验指导书生成完成！')
        // 可以跳转到历史记录或显示详情
        goToHistoryView()
        return
      }
      
      attempts++
      if (attempts < maxAttempts) {
        setTimeout(check, 3000)
      } else {
        ElMessage.warning('生成超时，请稍后在历史记录中查看')
        goToHistoryView()
      }
    } catch (error) {
      console.error('检查状态失败', error)
    }
  }
  
  setTimeout(check, 3000)
}

// 8. 添加生成大作业方法
const generateAssignment = async () => {
  // 验证必填项
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!assignmentConfig.title) {
    ElMessage.warning('请输入作业标题')
    return
  }
  if (!assignmentConfig.description) {
    ElMessage.warning('请输入作业描述')
    return
  }

  try {
    generating.value = true
    const teacherId = localStorage.getItem('userId') || '2'
    
    // TODO: 调用大作业生成API（需要后端实现）
    // const res = await assignmentApi.generateAssignment({
    //   teacherId: parseInt(teacherId),
    //   courseId: selectedCourseId.value,
    //   title: assignmentConfig.title,
    //   description: assignmentConfig.description,
    //   submitTypes: assignmentConfig.submitTypes,
    //   totalScore: assignmentConfig.totalScore
    // })

    // 临时提示
    ElMessage.success('大作业生成功能开发中...')
    
  } catch (error) {
    console.error('生成失败', error)
    ElMessage.error('生成失败：' + (error.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

// 9. 在 return 或 export 中添加这些变量和方法
// return {
//   experimentConfig,
//   assignmentConfig,
//   onTypeChange,
//   generateExperimentGuide,
//   generateAssignment,
//   checkExperimentStatus,
//   // ... 其他已有的变量和方法
// }









