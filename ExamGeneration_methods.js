// ========================================
// 添加到 ExamGeneration.vue 的方法部分
// ========================================

// 1. 类型切换方法
const onTypeChange = (type) => {
  console.log('考核类型切换为:', type)
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

// 2. 生成实验指导书方法
const generateExperimentGuide = async () => {
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

// 3. 检查实验指导书生成状态
const checkExperimentStatus = async (guideId) => {
  let attempts = 0
  const maxAttempts = 20
  
  const check = async () => {
    try {
      const res = await smartTeachingApi.getExperimentGuide(guideId)
      if (res.code === 1 && res.data.status === 'completed') {
        ElMessage.success('实验指导书生成完成！')
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

// 4. 生成大作业方法
const generateAssignment = async () => {
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
    
    const res = await smartTeachingApi.submitHomework({
      teacherId: parseInt(teacherId),
      studentId: parseInt(teacherId),
      courseId: selectedCourseId.value,
      homeworkTitle: assignmentConfig.title,
      fileUrl: '',
      fileName: '',
      content: assignmentConfig.description
    })

    if (res.code === 1 || res.success === true) {
      ElMessage.success('大作业创建成功！')
      goToHistoryView()
    } else {
      ElMessage.error('创建失败：' + (res.msg || '未知错误'))
    }
  } catch (error) {
    console.error('创建失败', error)
    ElMessage.error('创建失败：' + (error.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

// 5. 修改现有的 goToStep 方法，在方法开头添加以下判断：
/*
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
*/









