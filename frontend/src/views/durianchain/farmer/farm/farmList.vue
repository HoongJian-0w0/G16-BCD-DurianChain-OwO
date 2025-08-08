<template>
  <el-main>
    <!-- On-chain summary card -->
    <div class="onchain-card-container">
      <el-card class="summary-card light farms-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper info-bg">
            <el-icon size="32"><Watermelon /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">On-Chain Farms</div>
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
        <el-input prefix-icon="Place" v-model="searchParams.farmId" placeholder="Enter Farm ID" clearable />
      </el-form-item>
      <el-form-item class="searchField">
        <el-input prefix-icon="Location" v-model="searchParams.location" placeholder="Enter Location" clearable />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleSearch">Search</el-button>
        <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
        <el-button icon="Plus" type="primary" @click="showAddDialog">Add</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :height="`calc(100vh - 330px)`"   :data="tableList" style="width: 100%;" size="medium">
      <el-table-column prop="farmId" label="Farm ID" sortable />
      <el-table-column prop="location" label="Location" sortable />
      <el-table-column label="Certificate CID">
        <template #default="{ row }">
          {{ formatCid(row.certificateCid) }}
        </template>
      </el-table-column>
      <el-table-column label="Certificate Expiry">
        <template #default="{ row }">
          {{ formatDate(row.certificateExpiry) }}
        </template>
      </el-table-column>
      <el-table-column label="Actions">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="View" size="small" plain @click="viewFarm(scope.row)">View</el-button>
            <el-button icon="Edit" type="success" size="small" plain @click="showUpdateDialog(scope.row)">Update</el-button>
            <el-button icon="Document" type="warning" size="small" plain @click="viewHistory(scope.row.farmId)">History</el-button>
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

    <!-- Add / Update Dialog -->
    <AddFarm v-model:visible="dialogVisible" :mode="dialogMode" :initialData="dialogFormData" @submit="handleDialogSubmit" />

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


    <!-- View Farm Modal -->
    <el-dialog v-model="viewFarmVisible" title="Farm Details" width="800px" destroy-on-close>
      <div class="farm-compare-grid">
        <div class="farm-section">
          <h4>📁 Database Record</h4>
          <div class="farm-item"><b>Farm ID:</b> {{ viewFarmData.db.farmId }}</div>
          <div class="farm-item"><b>Location:</b> {{ viewFarmData.db.location }}</div>
          <div class="farm-item"><b>Latitude:</b> {{ viewFarmData.db.latitude }}</div>
          <div class="farm-item"><b>Longitude:</b> {{ viewFarmData.db.longitude }}</div>
          <div class="farm-item"><b>Certificate CID:</b> {{ viewFarmData.db.certificateCid }}</div>
          <div class="farm-item"><b>Certificate Expiry:</b> {{ viewFarmData.db.certificateExpiry }}</div>
          <div class="farm-item"><b>Owner Address:</b> {{ viewFarmData.db.ownerAddress }}</div>
          <div class="farm-item"><b>Tx Hash:</b> {{ viewFarmData.db.txHash }}</div>
        </div>

        <div class="farm-section">
          <h4>🔗 On-Chain Record</h4>
          <div class="farm-item"><b>Farm ID:</b> {{ viewFarmData.chain.farmId }}</div>
          <div class="farm-item"><b>Location:</b> {{ viewFarmData.chain.location }}</div>
          <div class="farm-item farm-item-row">
            <div>
              <b>Certificate CID:</b> {{ viewFarmData.chain.certificateCID || '-' }}
            </div>
            <div v-if="viewFarmData.chain?.certificateCID?.trim()">
              <el-image
                  :src="`https://ipfs.io/ipfs/${viewFarmData.chain.certificateCID}`"
                  style="width: 100px; height: auto; border: 1px solid #ccc; border-radius: 4px;"
                  fit="contain"
                  :preview-src-list="[ `https://ipfs.io/ipfs/${viewFarmData.chain.certificateCID}` ]"
                  preview-teleported
              />
            </div>
          </div>
          <div class="farm-item">
            <b>Certificate Expiry:</b>
            {{
              Number(viewFarmData.chain?.certificateExpiry) > 0
                  ? new Date(Number(viewFarmData.chain.certificateExpiry) * 1000).toLocaleString()
                  : '-'
            }}
          </div>
          <div class="farm-item"><b>Owner Address:</b> {{ viewFarmData.chain.owner }}</div>
        </div>
      </div>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import dayjs from 'dayjs';
import message from '@/utils/message';
import AddFarm from '@/views/durianchain/farmer/farm/addFarm.vue';
import { getMyFarmIds, getFarmById, getFarmMilestone } from '@/contracts/farmer/farmContract';
import { fromSolidityTimestamp, toSolidityTimestamp, formatDate } from '@/utils/time';
import { createFarm, getFarmPage, fetchFarmById, updateFarm } from '@/api/farmer/farm';
import { useUserStore } from '@/store/user/index'

const userStore = useUserStore();

const tableHeight = ref(400);
const tableList = ref<any[]>([]);
const total = ref(0);
const onChainStats = reactive({ total: 0, active: 0, expired: 0 });
const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  owner_address: userStore.getWalletAddress,
  farmId: '',
  location: ''
})
const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'update'>('create');
const dialogFormData = ref<any>(null);

const historyDrawerVisible = ref(false);
const historyTitle = ref('');
const historyItems = ref<any[]>([]);

const viewFarmVisible = ref(false);
const viewFarmData = ref({
  db: {
    farmId: '',
    location: '',
    latitude: '',
    longitude: '',
    certificateCid: '',
    certificateExpiry: '',
    ownerAddress: '',
    txHash: '',
  },
  chain: {
    farmId: '',
    location: '',
    certificateCID: '',
    certificateExpiry: '',
    owner: '',
  },
});

async function viewFarm(row: any) {
  try {
    // Load DB data
    viewFarmData.value.db = {
      farmId: row.farmId,
      location: row.location,
      latitude: row.latitude,
      longitude: row.longitude,
      certificateCid: row.certificateCid,
      certificateExpiry: row.certificateExpiry,
      ownerAddress: row.ownerAddress,
      txHash: row.txHash || '-',
    };

    // Load Chain data
    const onchain = await getFarmById(row.farmId);
    viewFarmData.value.chain = {
      farmId: row.farmId,
      location: onchain.location,
      certificateCID: onchain.certificateCID,
      certificateExpiry: dayjs.unix(Number(onchain.certificateExpiry)).format('YYYY-MM-DD HH:mm'),
      owner: onchain.owner,
    };

    viewFarmVisible.value = true;
  } catch (err: any) {
    message.error(err.message || 'Failed to load farm data');
  }
}

async function fetchFarms() {
  try {
    const res = await getFarmPage({ ...searchParams });
    const records = res.data.page.records;
    const totalCount = res.data.page.total;
    tableList.value = records.map((item: any) => ({
      ...item,
      certificateExpiry: item.certificateExpiry || '-',
    }));
    total.value = totalCount;
  } catch (err: any) {
    message.error(err.message || 'Failed to fetch farms from DB');
  }
}

async function fetchOnChainStats() {
  try {
    const ids = await getMyFarmIds();
    if (!ids || ids.length === 0) {
      Object.assign(onChainStats, { total: 0, active: 0, expired: 0 });
      return;
    }

    const farms = await Promise.all(ids.map(id => getFarmById(id)));
    const now = dayjs().unix();

    let active = 0;
    let expired = 0;
    let totalWithCid = 0;

    farms.forEach(f => {
      try {
        const cid = f.certificateCID?.trim();
        if (!cid) return; // Skip farms without certificate CID

        totalWithCid++;

        const expiry = toSolidityTimestamp(f.certificateExpiry);
        if (!expiry || isNaN(expiry)) return;

        if (expiry > now) {
          active++;
        } else {
          expired++;
        }
      } catch {
        // Optional: silently skip
      }
    });

    onChainStats.total = totalWithCid;
    onChainStats.active = active;
    onChainStats.expired = expired;
  } catch (err: any) {
    message.error(err.message || 'Failed to load on-chain stats');
  }
}


function formatCid(cid: string | null | undefined) {
  return cid && cid.trim() !== '' ? cid : '-';
}

function handleSearch() {
  searchParams.pageNum = 1;
  fetchFarms();
}
function handleReset() {
  Object.assign(searchParams, { farmId: '', location: '', pageNum: 1, pageSize: 10 });
  fetchFarms();
}
function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchFarms();
}
function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchFarms();
}
function showAddDialog() {
  dialogMode.value = 'create';
  dialogFormData.value = null;
  dialogVisible.value = true;
}
async function showUpdateDialog(row: any) {
  dialogMode.value = 'update';
  try {
    const res = await fetchFarmById(row.id); // Assuming `row.id` is numeric DB ID
    const farm = res.data.item;

    dialogFormData.value = {
      id: farm.id,
      farmId: farm.farmId,
      ownerAddress: farm.ownerAddress,
      location: farm.location,
      latitude: farm.latitude,
      longitude: farm.longitude,
      certificateCid: farm.certificateCid,
      certificateExpiry: new Date(farm.certificateExpiry).getTime(), // convert ISO to timestamp
      txHash: farm.txHash,
    };
    dialogVisible.value = true;
  } catch (err: any) {
    message.error('Failed to fetch farm data for update');
  }
}

async function handleDialogSubmit(data: any) {
  try {
    const payload: any = {
      farmId: data.farmId,
      ownerAddress: data.ownerAddress,
      location: data.location,
      latitude: data.latitude,
      longitude: data.longitude,
      txHash: data.txHash,
    };

    const expiryUnix = Number(data.certificateExpiry);
    if (!isNaN(expiryUnix) && expiryUnix > 0) {
      payload.certificateExpiry = dayjs(expiryUnix * 1000).format('YYYY-MM-DDTHH:mm:ss');
      payload.certificateCid = data.certificateCid;
    }

    if (dialogMode.value === 'create') {
      await createFarm(payload);
      message.success('Farm registered and saved to DB');
    } else {
      await updateFarm({ id: data.id, ...payload });
      message.success('Farm updated in DB');
    }

    dialogVisible.value = false;
    await fetchFarms();
    await fetchOnChainStats();
  } catch (err: any) {
    message.error(err.message || 'Operation failed');
  }
}

async function viewHistory(farmId: string) {
  try {
    const milestone = await getFarmMilestone(farmId);
    if (!milestone || milestone.length === 0) return message.warning(`No milestone history found for farm: ${farmId}`);
    historyTitle.value = `Milestone History for ${farmId}`;
    historyItems.value = milestone.map(entry => ({
      ...entry,
      formattedTime: dayjs.unix(Number(entry.timestamp)).format('YYYY-MM-DD HH:mm'),
    }));
    historyDrawerVisible.value = true;
  } catch (err: any) {
    message.error(err.message || 'Failed to load history');
  }
}

onMounted(async () => {
  await nextTick(() => {
    tableHeight.value = window.innerHeight - 300;
  });
  await fetchOnChainStats();
  await fetchFarms();
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
  flex: 1; min-width: 250px; border-radius: 12px; border: 1px solid #ebeef5; transition: 0.3s ease;
}
.summary-content { display: flex; align-items: center; gap: 16px; }
.icon-wrapper {
  width: 52px; height: 52px; display: flex; align-items: center; justify-content: center; border-radius: 50%;
}
.info-bg { background-color: rgba(64, 158, 255, 0.15); color: #74c365; }
.success-bg { background-color: rgba(103, 194, 58, 0.15); color: #67C23A; }
.danger-bg { background-color: rgba(245, 108, 108, 0.15); color: #F56C6C; }
.summary-info { display: flex; flex-direction: column; }
.summary-title { font-size: 14px; font-weight: 500; color: #606266; }
.summary-value { font-size: 24px; font-weight: bold; margin-top: 4px; color: #303133; }
.summary-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }
.history-scroll { max-height: 80vh; overflow-y: auto; padding-right: 8px; }
.history-block {
  padding: 12px; border-left: 3px solid #74c365; background: #f9f9f9; margin-bottom: 12px;
  border-radius: 6px; transition: all 0.2s;
}
.history-header { display: flex; justify-content: space-between; font-weight: 600; margin-bottom: 6px; }
.history-meta {
  font-size: 13px; color: #666; display: flex; justify-content: space-between;
}
.history-desc { margin-top: 8px; font-size: 14px; white-space: pre-wrap; }
.farm-compare-grid {
  display: flex;
  gap: 24px;
  padding-top: 12px;
}
.farm-section {
  flex: 1;
  border: 1px solid #ebeef5;
  padding: 16px;
  border-radius: 8px;
  background: #f9f9f9;
}
.farm-item {
  margin-bottom: 10px;
  font-size: 14px;
  word-break: break-word;
}
.farm-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
</style>