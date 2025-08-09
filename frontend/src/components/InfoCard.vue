<template>
  <el-card class="info-card" shadow="hover">
    <div class="card-content">
      <div class="icon-box" :style="{ backgroundColor: bg, color }">
        <el-icon size="28"><component :is="icon" /></el-icon>
      </div>
      <div class="text-box">
        <div class="label">{{ title }}</div>
        <div class="value">{{ value }}</div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  value: number
  icon: string
  color: string
  bg: string
}>()
</script>

<style scoped>
.info-card {
  border-radius: 16px;
  padding: 16px;
  transition: all 0.3s ease;
  min-width: 240px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-box {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.text-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.label {
  font-size: 15px;
  color: #909399;
  font-weight: 500;
  line-height: 1.4;
}

.value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}
</style>
