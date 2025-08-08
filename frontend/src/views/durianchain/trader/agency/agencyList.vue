<template>
  <el-main>
    <!-- On-chain summary card -->
    <div class="onchain-card-container">
      <el-card class="summary-card light farms-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper info-bg">
            <el-icon size="32"><OfficeBuilding /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">On-Chain Agencies</div>
            <div class="summary-value">{{ onChainStats.total }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card light active-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper success-bg">
            <el-icon size="32"><CircleCheck /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Active Certificates</div>
            <div class="summary-value">{{ onChainStats.active }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card light expired-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper danger-bg">
            <el-icon size="32"><CircleClose /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Expired Certificates</div>
            <div class="summary-value">{{ onChainStats.expired }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Search Form -->
    <el-form :inline="true" :model="searchParams" size="small">
      <el-form-item class="searchField">
        <el-input prefix-icon="Edit" v-model="searchParams.agencyId" placeholder="Enter Agency ID" clearable />
      </el-form-item>
      <el-form-item class="searchField">
        <el-input prefix-icon="User" v-model="searchParams.agencyName" placeholder="Enter Agency Name" clearable />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleSearch">Search</el-button>
        <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
        <el-button icon="Plus" type="primary" @click="showAddDialog">Add</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :data="tableList" :height="`calc(100vh - 330px)`" style="width: 100%;" size="medium">
      <el-table-column prop="agencyId" label="Agency ID" sortable />
      <el-table-column prop="agencyName" label="Agency Name" sortable />
      <el-table-column prop="exportLicenseCid" label="Export CID">
        <template #default="{ row }">
          {{ formatCid(row.exportLicenseCid) }}
        </template>
      </el-table-column>
      <el-table-column prop="exportLicenseExpiry" label="Expiry">
        <template #default="{ row }">
          {{ formatDate(row.exportLicenseExpiry) }}
        </template>
      </el-table-column>
      <el-table-column label="Actions">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="View" size="small" plain @click="viewAgency(scope.row)">View</el-button>
            <el-button icon="Edit" type="success" size="small" plain @click="showUpdateDialog(scope.row)">Update</el-button>
            <el-button icon="Document" type="warning" size="small" plain @click="viewHistory(scope.row.agencyId)">History</el-button>
          </el-space>
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

    <!-- History Drawer -->
    <el-drawer v-model="historyDrawerVisible" :title="historyTitle" size="40%" direction="rtl">
      <div class="history-scroll">
        <div v-for="(item, index) in historyItems" :key="index" class="history-block">
          <div class="history-header">
            <strong>{{ item.action }}</strong>
            <span class="timestamp">{{ item.formattedTime }}</span>
          </div>
          <div class="history-meta">
            <span><b>By:</b> {{ item.actor }}</span>
            <span><b>Role:</b> {{ item.role }}</span>
          </div>
          <div class="history-desc">{{ item.description }}</div>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="viewAgencyVisible" title="Agency Details" width="800px" destroy-on-close>
      <div class="agency-compare-grid">
        <div class="agency-section">
          <h4>📁 Database Record</h4>
          <div class="agency-item"><b>Agency ID:</b> {{ viewAgencyData.db.agencyId }}</div>
          <div class="agency-item"><b>Name:</b> {{ viewAgencyData.db.agencyName }}</div>
          <div class="agency-item"><b>Owner:</b> {{ viewAgencyData.db.ownerAddress }}</div>
          <div class="agency-item"><b>Export CID:</b> {{ viewAgencyData.db.exportLicenseCid }}</div>
          <div class="agency-item"><b>Expiry:</b> {{ viewAgencyData.db.exportLicenseExpiry }}</div>
          <div class="agency-item"><b>Tx Hash:</b> {{ viewAgencyData.db.txHash }}</div>
        </div>

        <div class="agency-section">
          <h4>🔗 On-Chain Record</h4>
          <div class="agency-item"><b>Agency ID:</b> {{ viewAgencyData.chain.agencyId }}</div>
          <div class="agency-item"><b>Name:</b> {{ viewAgencyData.chain.agencyName }}</div>
          <div class="agency-item"><b>Owner:</b> {{ viewAgencyData.chain.owner }}</div>
          <div class="agency-item"><b>Export CID:</b> {{ viewAgencyData.chain.exportLicenseCID }}</div>
          <div class="agency-item"><b>Expiry:</b> {{ viewAgencyData.chain.exportLicenseExpiry }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- Add / Update Dialog -->
    <AddAgency v-model:visible="dialogVisible" :mode="dialogMode" :initialData="dialogFormData" @submit="handleDialogSubmit" />
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import dayjs from 'dayjs';
import message from '@/utils/message';
import { formatDate, toSolidityTimestamp } from '@/utils/time';
import {createAgency, getAgencyPage, updateAgency} from '@/api/trader/agency/index';
import { getTraderAgencyById, getTraderAgencyMilestone, getMyTraderAgencyIds } from '@/contracts/trader/agencyContract';
import { useUserStore } from '@/store/user';
import AddAgency from '@/views/durianchain/trader/agency/addAgency.vue';

const userStore = useUserStore();
const tableList = ref<any[]>([]);
const total = ref(0);
const onChainStats = reactive({ total: 0, active: 0, expired: 0 });

const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  agencyId: '',
  agencyName: '',
  ownerAddress: userStore.getWalletAddress,
});

const historyDrawerVisible = ref(false);
const historyTitle = ref('');
const historyItems = ref<any[]>([]);

const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'update'>('create');
const dialogFormData = ref<any>(null);

const viewAgencyVisible = ref(false);
const viewAgencyData = ref({
  db: {
    agencyId: '',
    agencyName: '',
    ownerAddress: '',
    exportLicenseCid: '',
    exportLicenseExpiry: '',
    txHash: '',
  },
  chain: {
    agencyId: '',
    agencyName: '',
    owner: '',
    exportLicenseCID: '',
    exportLicenseExpiry: '',
  },
});

function showAddDialog() {
  dialogMode.value = 'create';
  dialogFormData.value = null;
  dialogVisible.value = true;
}

function showUpdateDialog(row: any) {
  dialogMode.value = 'update';
  dialogFormData.value = { ...row };
  dialogVisible.value = true;
}

async function handleDialogSubmit(data: any) {
  try {
    const payload: any = {
      agencyId: data.agencyId,
      ownerAddress: data.ownerAddress,
      agencyName: data.agencyName,
      txHash: data.txHash,
    };

    const expiryUnix = Number(data.exportLicenseExpiry);
    if (!isNaN(expiryUnix) && expiryUnix > 0) {
      payload.exportLicenseExpiry = dayjs(expiryUnix * 1000).format('YYYY-MM-DDTHH:mm:ss');
      payload.exportLicenseCid = data.exportLicenseCid;
    }

    if (dialogMode.value === 'create') {
      await createAgency(payload);
      message.success('Agency registered and saved to DB');
    } else {
      await updateAgency({ id: data.id, ...payload });
      message.success('Agency updated in DB');
    }

    dialogVisible.value = false;
    await fetchAgencies();
    await fetchOnChainStats();
  } catch (err: any) {
    message.error(err.message || 'Operation failed');
  }
}

function formatCid(cid: string | null | undefined) {
  return cid && cid.trim() !== '' ? cid : '-';
}

function handleSearch() {
  searchParams.pageNum = 1;
  fetchAgencies();
}
function handleReset() {
  Object.assign(searchParams, { agencyId: '', agencyName: '', pageNum: 1, pageSize: 10 });
  fetchAgencies();
}
function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchAgencies();
}
function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchAgencies();
}

async function fetchAgencies() {
  try {
    const res = await getAgencyPage({ ...searchParams });
    const records = res.data.page.records;
    tableList.value = records.map((item: any) => ({
      ...item,
      exportLicenseExpiry: item.exportLicenseExpiry || '-',
    }));
    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load agencies');
  }
}

async function fetchOnChainStats() {
  try {
    const ids = await getMyTraderAgencyIds();
    onChainStats.total = ids.length;

    const now = dayjs().unix();
    let active = 0;
    let expired = 0;

    for (const id of ids) {
      const agency = await getTraderAgencyById(id);
      const expiry = Number(agency.exportLicenseExpiry);

      if (expiry === 0) continue;

      if (expiry > now) active++;
      else expired++;
    }

    onChainStats.active = active;
    onChainStats.expired = expired;
  } catch {
    onChainStats.total = 0;
    onChainStats.active = 0;
    onChainStats.expired = 0;
  }
}

async function viewAgency(row: any) {
  try {
    const agencyOnChain = await getTraderAgencyById(row.agencyId);

    viewAgencyData.value.db = {
      agencyId: row.agencyId,
      agencyName: row.agencyName,
      ownerAddress: row.ownerAddress,
      exportLicenseCid: row.exportLicenseCid || '-',
      exportLicenseExpiry: row.exportLicenseExpiry || '-',
      txHash: row.txHash || '-',
    };

    viewAgencyData.value.chain = {
      agencyId: agencyOnChain.agencyId,
      agencyName: agencyOnChain.agencyName,
      owner: agencyOnChain.owner,
      exportLicenseCID: agencyOnChain.exportLicenseCID,
      exportLicenseExpiry: dayjs.unix(Number(agencyOnChain.exportLicenseExpiry)).format('YYYY-MM-DD HH:mm'),
    };

    viewAgencyVisible.value = true;
  } catch (err: any) {
    message.error(err.message || 'Failed to fetch agency details');
  }
}

async function viewHistory(agencyId: string) {
  try {
    const logs = await getTraderAgencyMilestone(agencyId);
    if (!logs.length) return message.warning('No history found');

    historyTitle.value = `Milestone History for ${agencyId}`;
    historyItems.value = logs.map(item => ({
      ...item,
      formattedTime: dayjs.unix(Number(item.timestamp)).format('YYYY-MM-DD HH:mm'),
    }));
    historyDrawerVisible.value = true;
  } catch (err: any) {
    message.error(err.message || 'Failed to load history');
  }
}

onMounted(async () => {
  await fetchOnChainStats();
  await fetchAgencies();
});
</script>

<style scoped>
.searchField { width: 100%; max-width: 240px; }
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
.summary-content { display: flex; align-items: center; gap: 16px; }
.icon-wrapper {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}
.info-bg { background-color: rgba(64, 158, 255, 0.15); color: #74c365; }
.success-bg { background-color: rgba(103, 194, 58, 0.15); color: #67C23A; }
.danger-bg { background-color: rgba(245, 108, 108, 0.15); color: #F56C6C; }
.summary-info { display: flex; flex-direction: column; }
.summary-title { font-size: 14px; font-weight: 500; color: #606266; }
.summary-value { font-size: 24px; font-weight: bold; margin-top: 4px; color: #303133; }

.history-scroll { max-height: 80vh; overflow-y: auto; padding-right: 8px; }
.history-block {
  padding: 12px;
  border-left: 3px solid #74c365;
  background: #f9f9f9;
  margin-bottom: 12px;
  border-radius: 6px;
}
.history-header { display: flex; justify-content: space-between; font-weight: 600; margin-bottom: 6px; }
.history-meta {
  font-size: 13px; color: #666;
  display: flex; justify-content: space-between;
}
.history-desc { margin-top: 8px; font-size: 14px; white-space: pre-wrap; }
.agency-compare-grid {
  display: flex;
  gap: 24px;
  padding-top: 12px;
}
.agency-section {
  flex: 1;
  border: 1px solid #ebeef5;
  padding: 16px;
  border-radius: 8px;
  background: #f9f9f9;
}
.agency-item {
  margin-bottom: 10px;
  font-size: 14px;
  word-break: break-word;
}

</style>
