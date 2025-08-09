<template>
  <div style="margin-top: 20px;">
    <v-chart
        :option="userRolesPieOption"
        :key="chartKey"
        style="width: 100%; height: 350px;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import VChart from 'vue-echarts'
import {getUserPage} from "@/api/admin/user/index.ts";

const chartKey = ref(0)
const userRolesPieOption = ref({})

async function loadUserStats() {
  const resp = await getUserPage({ pageNum: 1, pageSize: 1 })
  const stats = resp?.data?.data?.stats ?? {}

  userRolesPieOption.value = {
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      name: 'User Roles',
      type: 'pie',
      radius: ['35%', '65%'],
      itemStyle: { borderRadius: 8 },
      label: { formatter: '{b}: {c}' },
      data: [
        {
          name: 'Admin',
          value: stats.admin ?? 0,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#60a5fa' }
            ]) }
        },
        {
          name: 'Farmer',
          value: stats.farmer ?? 0,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
              { offset: 0, color: '#22c55e' },
              { offset: 1, color: '#86efac' }
            ]) }
        },
        {
          name: 'Logistics',
          value: stats.logistics ?? 0,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
              { offset: 0, color: '#f59e0b' },
              { offset: 1, color: '#fcd34d' }
            ]) }
        },
        {
          name: 'Trader',
          value: stats.trader ?? 0,
          itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
              { offset: 0, color: '#ef4444' },
              { offset: 1, color: '#fca5a5' }
            ]) }
        }
      ]
    }]
  }

  chartKey.value++
}

onMounted(loadUserStats)
</script>
