<template>
  <el-main>
    <!-- On-chain summary card -->
    <div class="onchain-card-container">
      <el-card class="summary-card light farms-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper info-bg">
            <el-icon size="32"><Van /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Total Logistics Company</div>
            <div class="summary-value">{{ onChainStats.total }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Search Form -->
    <el-form :inline="true" :model="searchParams" size="small">
      <el-form-item class="searchField">
        <el-input prefix-icon="Edit" v-model="searchParams.companyId" placeholder="Enter Company ID" clearable />
      </el-form-item>
      <el-form-item class="searchField">
        <el-input prefix-icon="User" v-model="searchParams.companyName" placeholder="Enter Company Name" clearable />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleSearch">Search</el-button>
        <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
        <el-button icon="Plus" type="primary" @click="showAddDialog = true">Add</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :data="tableList" :height="`calc(100vh - 330px)`" style="width: 100%;" size="medium">
      <el-table-column prop="companyId" label="Company ID" sortable />
      <el-table-column prop="companyName" label="Company Name" sortable />
      <el-table-column prop="ownerAddress" label="Owner Address" sortable />
      <el-table-column prop="txHash" label="Tx Hash">
        <template #default="{ row }">
          {{ row.txHash || '-' }}
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
        size="small"
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="searchParams.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="searchParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background
    />

    <!-- Dialog -->
    <AddLogistics
        v-model:visible="showAddDialog"
        mode="create"
        @submit="handleCreate"
    />
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import message from '@/utils/message';
import { useUserStore } from '@/store/user';
import { getLogisticsCompanyPage, createLogisticsCompany } from '@/api/logistics/logisticsCompany';
import { getMyLogisticsCompanyIds } from '@/contracts/logistics/logisticsCompanyContract';
import AddLogistics from '@/views/durianchain/logistics/logisticsCompany/addLogistics.vue';

const showAddDialog = ref(false);
const userStore = useUserStore();
const tableList = ref<any[]>([]);
const total = ref(0);
const onChainStats = reactive({ total: 0 });

const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  companyId: '',
  companyName: '',
  ownerAddress: userStore.getWalletAddress,
});

function handleSearch() {
  searchParams.pageNum = 1;
  fetchLogisticsCompanies();
}
function handleReset() {
  Object.assign(searchParams, {
    companyId: '',
    companyName: '',
    pageNum: 1,
    pageSize: 10,
  });
  fetchLogisticsCompanies();
}
function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchLogisticsCompanies();
}
function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchLogisticsCompanies();
}

async function fetchLogisticsCompanies() {
  try {
    const res = await getLogisticsCompanyPage({ ...searchParams });
    tableList.value = res.data.page.records;
    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load logistics companies');
  }
}

async function fetchOnChainStats() {
  try {
    const ids = await getMyLogisticsCompanyIds();
    onChainStats.total = ids.length;
  } catch {
    onChainStats.total = 0;
  }
}

async function handleCreate(data: any) {
  try {
    await createLogisticsCompany(data);
    message.success('Logistics company added successfully!');
    fetchLogisticsCompanies();
  } catch (err: any) {
    message.error(err.message || 'Failed to save logistics company');
  }
}

onMounted(async () => {
  await fetchOnChainStats();
  await fetchLogisticsCompanies();
});
</script>

<style scoped>
.searchField {
  width: 100%;
  max-width: 240px;
}
.onchain-card-container {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.summary-card {
  flex: 1;
  min-width: 250px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
}
.summary-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.icon-wrapper {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}
.info-bg {
  background-color: rgba(64, 158, 255, 0.15);
  color: #74c365;
}
.summary-info {
  display: flex;
  flex-direction: column;
}
.summary-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}
.summary-value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 4px;
  color: #303133;
}
</style>