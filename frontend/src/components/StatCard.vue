<template>
  <el-card class="summary-card light" shadow="always">
    <div class="summary-content">
      <div class="icon-wrapper" :style="{ backgroundColor: bgColor, color: iconColor }">
        <el-icon size="32">
          <component :is="iconComp" />
        </el-icon>
      </div>
      <div class="summary-info">
        <div class="summary-title">{{ label }}</div>
        <div class="summary-value">{{ displayValue.toLocaleString() }}</div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import * as Icons from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  label: string
  target: number
  icon?: keyof typeof Icons | string
  bgColor?: string
  iconColor?: string
}>(), {
  icon: 'Collection',
  bgColor: 'rgba(17, 24, 39, 0.06)',
  iconColor: '#374151'
})

const iconComp = computed(() => (Icons as any)[props.icon] ?? (Icons as any).Collection)

const displayValue = ref(0)
let raf = 0

onMounted(() => {
  const duration = 1200
  const start = performance.now()
  const tick = (t: number) => {
    const p = Math.min(1, (t - start) / duration)
    const eased = 1 - Math.pow(1 - p, 3)
    displayValue.value = Math.round((props.target || 0) * eased)
    if (p < 1) raf = requestAnimationFrame(tick)
  }
  raf = requestAnimationFrame(tick)
})

onBeforeUnmount(() => cancelAnimationFrame(raf))
</script>

<style scoped>
.summary-card {
  border: none;
  border-radius: 16px;
  overflow: hidden;
}
.summary-content {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
}
.icon-wrapper {
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border-radius: 14px;
}
.summary-info {
  display: grid;
  gap: 2px;
}
.summary-title {
  font-size: 13px;
  color: #455a64;
}
.summary-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.1;
}
</style>
