<template>
  <div class="dc-container">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-text">
        <h1 class="title">Welcome to <span class="brand">Durian Chain</span></h1>
        <p class="subtitle">
          <span class="typing">
            To resolve fraud in the durian industry—trace every batch, verify every certificate, and build trust from farm to fork.
          </span>
        </p>
      </div>
      <div class="hero-bg"></div>
    </section>

    <!-- KPI -->
    <section class="kpi-grid">
      <StatCard icon="Collection" label="Total Batches" :target="kpis.totalBatches"
                bg-color="#E3F2FD" icon-color="#1e88e5" />
      <StatCard icon="Grape" label="Total Durians" :target="kpis.totalDurians"
                bg-color="#E8F5E9" icon-color="#2e7d32" />
      <StatCard icon="User" label="Total Agencies" :target="kpis.totalAgencies"
                bg-color="#FFF8E1" icon-color="#f9a825" />
      <StatCard icon="Van" label="Logistics Companies" :target="kpis.totalLogistics"
                bg-color="#EDE7F6" icon-color="#7e57c2" />
      <StatCard icon="HomeFilled" label="Total Farms" :target="kpis.totalFarms"
                bg-color="#E0F2F1" icon-color="#00796b" />
      <StatCard icon="UserFilled" label="Total Users" :target="kpis.totalUsers"
                bg-color="#F3E5F5" icon-color="#8e24aa" />
    </section>

    <div class="action-row">
      <!-- Scan / Verify Durian card -->
      <el-card class="action-card" shadow="hover">
        <div class="action-content">
          <div class="action-image">
            <img src="@/assets/image/Durian-Banner-Login.jpg" alt="Verify Durian" />
          </div>
          <div class="action-text">
            <h3 class="action-title">Scan to Verify Durian / Batch</h3>
            <p class="action-subtitle">Upload or scan a QR to check on-chain authenticity.</p>
            <el-button type="primary" size="large" @click="router.push('verify')" round>
              <el-icon class="mr-6"><UploadFilled /></el-icon>
              Scan / Verify
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- Login card -->
      <el-card class="action-card" shadow="hover">
        <div class="action-content">
          <div class="action-image">
            <img src="@/assets/image/Durian-Banner-Login.jpg" alt="Login" />
          </div>
          <div class="action-text">
            <h3 class="action-title">Login to Manage</h3>
            <p class="action-subtitle">Access your dashboard and manage farms, batches, and agencies.</p>
            <el-button type="primary" size="large" @click="router.push('/login')" round>
              <el-icon class="mr-6"><UserFilled /></el-icon>
              Login
            </el-button>
          </div>
        </div>
      </el-card>
    </div>


    <!-- Charts -->
    <section class="charts-grid">
      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3 class="grad-text">User Roles Distribution</h3></div>
        <v-chart :key="chartKey" :option="userRolesPieOption" autoresize style="height:360px;" />
      </el-card>

      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3 class="grad-text">Durian Batch Status</h3></div>
        <v-chart :option="batchStatusOption" autoresize style="height:320px;" />
      </el-card>
    </section>

    <section class="variety-table">
      <el-card class="chart-card wide" shadow="hover">
        <div class="chart-header"><h3 class="grad-text">Durian Varieties</h3></div>

        <el-table
            :data="varietyState.list"
            v-loading="varietyState.loading"
            element-loading-text="Loading varieties…"
            border
            style="width: 100%;"
            :header-cell-style="{ background: '#fafafa', fontWeight: 600 }"
            :row-key="row => row.id"
        >
          <el-table-column prop="varietyId" label="ID" width="140" />
          <el-table-column prop="name" label="Name" min-width="220" />
          <el-table-column prop="originRegion" label="Origin" min-width="200" />
          <el-table-column
              prop="description"
              label="Description"
              min-width="360"
              show-overflow-tooltip
          />
        </el-table>

        <div class="table-footer">
          <el-pagination
              background
              layout="prev, pager, next, jumper"
              :page-size="varietyState.pageSize"
              :total="varietyState.total"
              :current-page="varietyState.pageNum"
              @current-change="loadVarieties"
          />
          <span class="page-hint">{{ varietyState.total }} total</span>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import { UploadFilled } from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import { getUserPage } from '@/api/admin/user'
import {getFarmPage} from "@/api/farmer/farm";
import {getAllBatches, getBatchPage} from "@/api/farmer/batch";
import {getDurianPage} from "@/api/farmer/durian";
import {getAgencyPage} from "@/api/trader/agency";
import {getLogisticsCompanyPage} from "@/api/logistics/logisticsCompany";
import {getVarietyPage} from "@/api/admin/variety";
import router from "@/router";

const STATUS_ORDER = ['Created', 'Ordered', 'Collected', 'Delivered', 'Deleted'] as const
type Status = (typeof STATUS_ORDER)[number]

const kpis = reactive({
  totalBatches: 0,
  totalDurians: 0,
  totalAgencies: 0,
  totalLogistics: 0,
  totalFarms: 0,
  totalUsers: 0
})

const chartKey = ref(0)

/* -------------------------- Gradient helpers & palettes -------------------------- */
const grad = (c1: string, c2: string) =>
    new echarts.graphic.LinearGradient(0, 0, 1, 1, [
      { offset: 0, color: c1 },
      { offset: 1, color: c2 },
    ])

const gradV = (c1: string, c2: string) =>
    new echarts.graphic.LinearGradient(0, 0, 0, 1, [
      { offset: 0, color: c1 },
      { offset: 1, color: c2 },
    ])

const rolePalette = {
  admin:     grad('#4f46e5', '#60a5fa'), // indigo → sky
  farmer:    grad('#16a34a', '#bbf7d0'), // emerald → mint
  logistics: grad('#f59e0b', '#fde68a'), // amber  → lemon
  trader:    grad('#ef4444', '#fda4af'), // red    → rose
}

const statusPalette = {
  Created:   gradV('#3b82f6', '#93c5fd'), // blue
  Ordered:   gradV('#f59e0b', '#fcd34d'), // amber
  Collected: gradV('#10b981', '#a7f3d0'), // green
  Delivered: gradV('#8b5cf6', '#c4b5fd'), // violet
  Deleted:   gradV('#ef4444', '#fecaca'), // red
}

const axisStyle = {
  axisTick: { show: false },
  axisLine: { show: false },
  axisLabel: { color: '#374151' },
}
const splitLineStyle = { lineStyle: { type: 'dashed', color: 'rgba(55,65,81,0.25)' } }

/* ----------------------------------- Charts ----------------------------------- */
// PIE (colorful gradients per role; data filled after API)
const userRolesPieOption = ref({
  tooltip: { trigger: 'item' },
  legend: { top: 'bottom', textStyle: { color: '#374151' } },
  series: [{
    name: 'User Roles',
    type: 'pie',
    radius: ['35%', '65%'],
    itemStyle: { borderRadius: 8 },
    label: { formatter: '{b}: {c}', color: '#1f2937', fontWeight: 600 },
    data: [
      { name: 'Admin', value: 0, itemStyle: { color: rolePalette.admin } },
      { name: 'Farmer', value: 0, itemStyle: { color: rolePalette.farmer } },
      { name: 'Logistics', value: 0, itemStyle: { color: rolePalette.logistics } },
      { name: 'Trader', value: 0, itemStyle: { color: rolePalette.trader } },
    ]
  }]
})

// BAR (status → gradient) — still dummy counts until you wire a status API
const batchStatusOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: 12, right: 12, top: 32, bottom: 12, containLabel: true },
  xAxis: { type: 'category', data: ['Created', 'Ordered', 'Collected', 'Delivered', 'Deleted'], ...axisStyle },
  yAxis: { type: 'value', splitLine: splitLineStyle, axisLine: { show: false }, axisLabel: { color: '#374151' } },
  series: [{
    type: 'bar',
    data: [120, 98, 76, 112, 22],
    barWidth: 18,
    itemStyle: {
      borderRadius: [8, 8, 0, 0],
      color: (p: any) => statusPalette[p.name] ?? gradV('#9ca3af', '#e5e7eb'),
    },
  }]
})

/* ---------------------------- Data: KPIs & user roles ---------------------------- */
const pageTotal = (res: any) =>
    Number(res?.data?.page?.total ?? res?.data?.data?.page?.total ?? 0)

async function loadKpis() {
  try {
    // Only batches uses fetch-all; others use page (pageNum=1,pageSize=1 just to read total)
    const [
      farmsRes,
      batchesRes,
      duriansRes,
      agenciesRes,
      logisticsRes,
    ] = await Promise.all([
      getFarmPage({ pageNum: 1, pageSize: 1 }),
      getBatchPage({ pageNum: 1, pageSize: 1 }),
      getDurianPage({ pageNum: 1, pageSize: 1 }),
      getAgencyPage({ pageNum: 1, pageSize: 1 }),
      getLogisticsCompanyPage({ pageNum: 1, pageSize: 1 }),
    ])

    kpis.totalFarms = pageTotal(farmsRes)
    kpis.totalBatches = pageTotal(batchesRes)           // fetchAll -> list length
    kpis.totalDurians = pageTotal(duriansRes)
    kpis.totalAgencies = pageTotal(agenciesRes)
    kpis.totalLogistics = pageTotal(logisticsRes)
  } catch (e) {
    console.error('Failed to load KPI totals', e)
  }
}

async function loadBatchStatus() {
  const res = await getAllBatches()
  // support either {data:{list}} or {list}
  const list = res?.data?.data?.list ?? res?.data?.list ?? []

  // init counters
  const counts: Record<(typeof STATUS_ORDER)[number], number> = {
    Created: 0, Ordered: 0, Collected: 0, Delivered: 0, Deleted: 0,
  }

  // count by status (case-insensitive, trims)
  for (const b of list) {
    const s = String(b?.status ?? '').trim().toLowerCase()
    if (s === 'created') counts.Created++
    else if (s === 'ordered') counts.Ordered++
    else if (s === 'collected') counts.Collected++
    else if (s === 'delivered') counts.Delivered++
    else if (s === 'deleted') counts.Deleted++
  }

  // rebuild option so echarts definitely refreshes
  batchStatusOption.value = {
    tooltip: { trigger: 'axis' },
    grid: { left: 12, right: 12, top: 32, bottom: 12, containLabel: true },
    xAxis: {
      type: 'category',
      data: [...STATUS_ORDER],
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: '#374151' },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: 'rgba(55,65,81,0.25)' } },
      axisLine: { show: false },
      axisLabel: { color: '#374151' },
    },
    series: [{
      type: 'bar',
      data: STATUS_ORDER.map(k => counts[k]),
      barWidth: 18,
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        // keep your colorful gradients per category
        color: (p: any) => statusPalette[p.name] ?? gradV('#9ca3af', '#e5e7eb'),
      },
    }],
  }
}

async function loadUserStats() {
  const resp = await getUserPage({ pageNum: 1, pageSize: 1 })

  // normalize payload
  const d = resp?.data ?? {}
  const payload = d.data && (d.data.stats || d.data.page) ? d.data : d
  const statsFromApi = payload?.stats
  const page = payload?.page ?? {}
  const total = Number(page?.total ?? 0)
  kpis.totalUsers = total

  let roleCounts = {
    admin: Number(statsFromApi?.admin ?? 0),
    farmer: Number(statsFromApi?.farmer ?? 0),
    logistics: Number(statsFromApi?.logistics ?? 0),
    trader: Number(statsFromApi?.trader ?? 0),
  }

  // fallback: compute by fetching all users if stats missing
  const statsMissing = Object.values(roleCounts).every(v => v === 0)
  if (statsMissing && total > 0) {
    try {
      const allResp = await getUserPage({ pageNum: 1, pageSize: total })
      const d2 = allResp?.data ?? {}
      const payload2 = d2.data && (d2.data.records || d2.data.page) ? d2.data : d2
      const records = payload2?.page?.records ?? payload2?.records ?? []
      roleCounts = { admin: 0, farmer: 0, logistics: 0, trader: 0 }
      for (const u of records) {
        const r = String(u.role || '').toLowerCase()
        if (r in roleCounts) (roleCounts as any)[r]++
      }
    } catch (e) {
      console.error('Failed to compute role stats', e)
    }
  }

  // set colorful gradients per slice
  userRolesPieOption.value = {
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom', textStyle: { color: '#374151' } },
    series: [{
      name: 'User Roles',
      type: 'pie',
      radius: ['35%', '65%'],
      itemStyle: { borderRadius: 8 },
      label: { formatter: '{b}: {c}', color: '#1f2937', fontWeight: 600 },
      data: [
        { name: 'Admin',     value: roleCounts.admin,     itemStyle: { color: rolePalette.admin } },
        { name: 'Farmer',    value: roleCounts.farmer,    itemStyle: { color: rolePalette.farmer } },
        { name: 'Logistics', value: roleCounts.logistics, itemStyle: { color: rolePalette.logistics } },
        { name: 'Trader',    value: roleCounts.trader,    itemStyle: { color: rolePalette.trader } },
      ],
    }],
  }

  chartKey.value++
}

type Variety = {
  id: number
  varietyId: string
  name: string
  description: string
  originRegion: string
  createTime: string
  updateTime: string
}

const varietyState = reactive({
  list: [] as Variety[],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  loading: false,
})

const normalizeVarietyPayload = (res: any) => {
  const p = res?.data?.page ?? res?.data?.data?.page ?? {}
  return {
    records: Array.isArray(p?.records) ? p.records : [],
    total: Number(p?.total ?? 0),
  }
}

async function loadVarieties(pageNum = varietyState.pageNum) {
  varietyState.loading = true
  try {
    const res = await getVarietyPage({ pageNum, pageSize: varietyState.pageSize })
    const { records, total } = normalizeVarietyPayload(res)
    varietyState.list = records
    varietyState.total = total
    varietyState.pageNum = pageNum
  } finally {
    varietyState.loading = false
  }
}

onMounted(async () => {
  await Promise.all([
    loadKpis(),
    loadUserStats(),
    loadBatchStatus(),
    loadVarieties(1),
  ])
})
</script>

<style scoped>
:root {
  --main-grad-start: #74c365;
  --main-grad-end:   #d6df72;
  --main-gradient: linear-gradient(90deg, var(--main-grad-start), var(--main-grad-end));
}

.el-main { padding: 24px !important; }
.dc-container {
  padding: 24px !important;
  display: flex;
  gap: 28px;
  flex-direction: column;
  background-image:
      linear-gradient(90deg, rgba(116, 195, 101, 0.02), rgba(163, 217, 119, 0.1)),
      url('@/assets/svg/star-bg.svg');
}

/* Hero */
.hero {
  position: relative; overflow: hidden; border-radius: 20px;
  padding: 40px 28px; background: linear-gradient(135deg, #0f172a, #111827);
  color: #e5e7eb; isolation: isolate;
}
.hero-bg::before {
  content: ""; position: absolute; inset: -40px -80px auto auto;
  width: 380px; height: 380px; border-radius: 50%; filter: blur(60px);
  background:
      radial-gradient(circle at 30% 30%, rgba(116,195,101,.35), transparent 60%),
      radial-gradient(circle at 70% 70%, rgba(214,223,114,.35), transparent 60%);
  opacity: .8; z-index: 0;
}
.hero-text { position: relative; z-index: 1; }
.title { font-size: 32px; margin: 0 0 8px; }
/* Brand green for text accents */
.brand {
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle { font-size: 16px; opacity: .92; margin: 0; }
.typing {
  display: inline-block; white-space: nowrap; overflow: hidden;
  border-right: .12em solid rgba(229,231,235,.65); width: 0;
  animation: typing 25s steps(60, end) 0s infinite, caret 700ms steps(1, end) infinite;
}
@keyframes typing { 0%{width:0} 32%{width:51%} 80%{width:51%} 100%{width:0} }
@keyframes caret { 50%{border-color:transparent} }

/* KPI */
.kpi-grid {
  display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px;
}

/* Charts */
.charts-grid {
  display: grid; grid-template-columns: repeat(2, minmax(280px, 1fr)); gap: 16px;
}
.chart-card { border: none; border-radius: 18px; overflow: hidden; }
.chart-card.wide { grid-column: 1 / -1; }
.chart-header { padding: 8px 8px 0 8px; }
.chart-header h3 { margin: 0; font-weight: 700; color: #111827; }

/* Gradient text utility for headings */
.grad-text {
  background: var(--main-gradient);
  -webkit-background-clip: text; background-clip: text; color: transparent;
}

.image-wrapper img { width: 100%; height: 100%; object-fit: cover; }

.variety-table .table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px 4px;
}
.variety-table .page-hint {
  font-size: 12px;
  color: #6b7280;
}

.action-card {
  border: none;
  border-radius: 18px;
  overflow: hidden;
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-image {
  width: 250px;
  height: 250px;
  border-radius: 14px;
  overflow: hidden;
  flex-shrink: 0;
}
.action-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.action-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.action-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  line-height: 1.2;
  background: var(--main-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.action-subtitle {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}
.mr-6 {
  margin-right: 6px;
}

/* Stack vertically on smaller screens */
@media (max-width: 900px) {
  .action-content {
    flex-direction: column;
    align-items: center;
  }
  .action-image {
    width: 200px;
    height: 200px;
  }
}

.action-row {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

/* Cards */
.action-card {
  flex: 1;
  border: none;
  border-radius: 18px;
  overflow: hidden;
}

/* Inside card layout */
.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-image {
  width: 150px;
  height: 150px;
  border-radius: 14px;
  overflow: hidden;
  flex-shrink: 0;
}
.action-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.action-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-text .el-button {
  width: 200px;
  justify-content: center;
}

.action-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  line-height: 1.2;
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  color: transparent;
}
.action-subtitle {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}
.mr-6 { margin-right: 6px; }

/* Mobile stack */
@media (max-width: 900px) {
  .action-row {
    flex-direction: column;
  }
  .action-content {
    flex-direction: column;
    align-items: center;
  }
  .action-image {
    width: 200px;
    height: 200px;
  }
}

</style>
