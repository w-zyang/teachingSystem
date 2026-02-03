<template>
  <div class="dialog-overlay" @click.self="$emit('close')">
    <div class="dialog-content large">
      <div class="dialog-header">
        <h2>📦 教学资源包详情</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="dialog-body" v-if="resource">
        <div class="resource-section" v-if="resource.quizContent">
          <h3>📝 课堂测验</h3>
          <pre class="content-box">{{ resource.quizContent }}</pre>
        </div>
        <div class="resource-section" v-if="resource.homeworkContent">
          <h3>📚 课后作业</h3>
          <pre class="content-box">{{ resource.homeworkContent }}</pre>
        </div>
        <div class="resource-section" v-if="resource.experimentContent">
          <h3>🔬 实验指导书</h3>
          <pre class="content-box">{{ resource.experimentContent }}</pre>
        </div>
        <div class="resource-section" v-if="resource.summaryPpt">
          <h3>📊 知识点总结</h3>
          <pre class="content-box">{{ resource.summaryPpt }}</pre>
        </div>
      </div>
      <div class="dialog-body" v-else>
        <p class="loading">加载中...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import * as smartTeachingApi from '@/api/smartTeaching'

export default {
  name: 'ResourceViewDialog',
  props: ['packageId'],
  emits: ['close'],
  setup(props) {
    const resource = ref(null)

    onMounted(async () => {
      try {
        const res = await smartTeachingApi.getResourcePackage(props.packageId)
        if (res.code === 1) {
          resource.value = res.data
        }
      } catch (error) {
        console.error('加载失败', error)
      }
    })

    return { resource }
  }
}
</script>

<style scoped>
.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.dialog-content.large { background: white; border-radius: 16px; width: 90%; max-width: 900px; max-height: 90vh; overflow-y: auto; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 24px; border-bottom: 1px solid #e9ecef; }
.dialog-header h2 { margin: 0; font-size: 24px; color: #333; }
.close-btn { background: none; border: none; font-size: 32px; color: #999; cursor: pointer; }
.dialog-body { padding: 24px; }
.resource-section { margin-bottom: 32px; }
.resource-section h3 { margin: 0 0 16px 0; font-size: 18px; color: #333; }
.content-box { background: #f8f9fa; padding: 16px; border-radius: 8px; white-space: pre-wrap; word-wrap: break-word; font-size: 14px; line-height: 1.6; }
.loading { text-align: center; padding: 40px; color: #999; }
</style>

