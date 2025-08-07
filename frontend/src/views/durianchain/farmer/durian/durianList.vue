<template>
  <el-main>
    <div class="onchain-card-container">
      <el-card class="summary-card light farms-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper info-bg">
            <el-icon size="32"><Watermelon /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">On-Chain Farms</div>
            <div class="summary-value">{{ farmOptions.length }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card light active-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper success-bg">
            <el-icon size="32"><CircleCheck /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Total Durians</div>
            <div class="summary-value">{{ total  }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-form :inline="true" :model="searchParams" size="small" class="filter-form">
        <div class="search-fields-group">
          <el-form-item label="Please select farm: " class="searchField" style="font-weight: bold;">
            <el-select
                v-model="searchParams.farmId"
                placeholder="Select a farm:"
                style="width: 220px"
                clearable
            >
              <el-option
                  v-for="farm in farmOptions"
                  :key="farm.farmId"
                  :label="farm.farmId"
                  :value="farm.farmId"
                  style="width: 200px"
              />
            </el-select>
          </el-form-item>

          <el-form-item class="searchField">
            <el-select v-model="searchParams.varietyId" placeholder="Select Variety" clearable style="width: 200px">
              <el-option
                  v-for="v in varietyOptions"
                  :key="v.varietyId"
                  :label="v.name"
                  :value="v.varietyId"
              />
            </el-select>
          </el-form-item>

          <el-form-item class="searchField">
            <el-input prefix-icon="Edit" v-model="searchParams.durianId" placeholder="Enter Durian ID" clearable />
          </el-form-item>

          <el-form-item class="searchField">
            <el-button icon="Search" @click="handleSearch">Search</el-button>
            <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
            <el-button type="primary" icon="Plus" @click="showAddDialog = true">Add</el-button>
          </el-form-item>

        </div>
      </el-form>
    </div>

    <!-- Table -->
    <el-table :data="tableList" :height="`calc(100vh - 350px)`" style="width: 100%;" size="medium">
      <el-table-column prop="durianId" label="Durian ID" sortable />
      <el-table-column label="Variety">
        <template #default="{ row }">
          {{ row.varietyName }}
        </template>
      </el-table-column>
      <el-table-column prop="imageUrl" label="Image">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" style="width: 50px; height: 50px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="imageHash" label="Image Hash" width="200px" />
      <el-table-column prop="scanCount" label="Scan Count" sortable />
      <el-table-column prop="onChain" label="On Chain">
        <template #default="{ row }">
          <el-tag :type="row.onChain ? 'success' : 'info'">
            {{ row.onChain ? 'Yes' : 'No' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : row.status === 'archived' ? 'warning' : 'info'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="200px">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button
                icon="View"
                size="small"
                plain
                @click="viewDurian(scope.row)"
            >View</el-button>

            <el-button
                icon="Delete"
                type="danger"
                size="small"
                plain
                @click="handleDeleteDurian(scope.row)"
                :disabled="scope.row.onChain"
            >Delete</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>

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

    <el-drawer v-model="viewVisible" title="Durian Details" size="640px">
    <el-descriptions :column="1" border style="max-width: 600px;">
      <el-descriptions-item label="Durian ID">{{ viewDurianData.durianId }}</el-descriptions-item>
      <el-descriptions-item label="Farm ID">{{ viewDurianData.farmId }}</el-descriptions-item>
      <el-descriptions-item label="Variety">{{ viewDurianData.varietyName }} ({{ viewDurianData.varietyId }})</el-descriptions-item>
      <el-descriptions-item label="Status">{{ viewDurianData.status }}</el-descriptions-item>
      <el-descriptions-item label="On Chain">{{ viewDurianData.onChain ? 'Yes' : 'No' }}</el-descriptions-item>
      <el-descriptions-item label="Scan Count">{{ viewDurianData.scanCount }}</el-descriptions-item>
      <el-descriptions-item label="Image Verification">
        <el-tag
            v-if="verifyStatus === 'pass'"
            type="success"
            effect="dark"
        >✔ Hash Verified</el-tag>
        <el-tag
            v-else-if="verifyStatus === 'fail'"
            type="danger"
            effect="dark"
        >❌ Mismatch</el-tag>
        <el-tag
            v-else-if="verifyStatus === 'verifying'"
            type="info"
            effect="plain"
        >Verifying...</el-tag>
        <el-tag
            v-else-if="verifyStatus === 'error'"
            type="warning"
            effect="plain"
        >⚠️ Error</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Image Hash">{{ viewDurianData.imageHash }}</el-descriptions-item>
      <el-descriptions-item label="Image">
        <el-image :src="viewDurianData.imageUrl" style="width: 350px;" fit="contain" />
      </el-descriptions-item>
    </el-descriptions>
  </el-drawer>

    <AddDurian v-model:visible="showAddDialog" @submit="handleCreateDurian" />
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import message from '@/utils/message';
import {getDurianPage, createDurian, deleteDurian} from '@/api/farmer/durian/index';
import { getAllVarieties } from '@/api/admin/variety';
import { getMyFarmIds, getFarmById } from '@/contracts/farmContract';
import { useUserStore } from '@/store/user';
import AddDurian from './addDurian.vue';
import { verifyImageHash } from '@/utils/cloudinaryUploader';

const userStore = useUserStore();
const walletAddress = userStore.getWalletAddress;

const tableList = ref<any[]>([]);
const total = ref(0);
const farmOptions = ref<{ farmId: string }[]>([]);
const varietyOptions = ref<{ varietyId: string, name: string }[]>([]);

const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  durianId: '',
  farmId: '',
  varietyId: '',
  walletAddress
});

const showAddDialog = ref(false);
const viewVisible = ref(false);
const viewDurianData = ref<any>({});
const verifyStatus = ref<'verifying' | 'pass' | 'fail' | 'error' | null>(null);

async function viewDurian(row: any) {
  viewDurianData.value = row;
  viewVisible.value = true;
  verifyStatus.value = 'verifying';

  try {
    const isValid = await verifyImageHash(row.imageUrl, row.imageHash);
    verifyStatus.value = isValid ? 'pass' : 'fail';
  } catch (err) {
    verifyStatus.value = 'error';
  }
}

function handleSearch() {
  searchParams.pageNum = 1;
  fetchDurianList();
}

function handleReset() {
  Object.assign(searchParams, {
    durianId: '',
    farmId: '',
    varietyId: '',
    pageNum: 1,
    pageSize: 10,
    walletAddress
  });
  fetchDurianList();
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchDurianList();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchDurianList();
}

async function fetchDurianList() {
  try {
    const res = await getDurianPage({ ...searchParams });
    const records = res.data.page.records;

    tableList.value = records.map((d: any) => {
      const varietyId = String(d.varietyId).trim();
      const variety = varietyOptions.value.find(v => String(v.varietyId).trim() === varietyId);

      console.log('Mapping variety:', {
        durianVarietyId: d.varietyId,
        matchedVariety: variety,
      });

      return {
        ...d,
        varietyName: variety ? variety.name : '-'
      };
    });

    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load durians');
  }
}

async function handleDeleteDurian(row: any) {
  try {
    await deleteDurian(row.id);
    message.success(`Durian ${row.durianId} deleted successfully`);
    fetchDurianList();
  } catch (err: any) {
    message.error(err.message || 'Failed to delete durian');
  }
}

async function fetchFarms() {
  try {
    const farmIds = await getMyFarmIds();
    const promises = farmIds.map(id => getFarmById(id));
    const farms = await Promise.all(promises);
    farmOptions.value = farms.map(farm => ({ farmId: farm.farmId }));
  } catch (err: any) {
    message.error(err.message || 'Failed to load farms from blockchain');
  }
}

let hasFetchedVarieties = false;
async function fetchVarieties() {
  if (hasFetchedVarieties) return;
  hasFetchedVarieties = true;
  try {
    const res = await getAllVarieties();
    const list = Array.isArray(res.data?.list) ? res.data.list : [];
    varietyOptions.value = list;
  } catch (err: any) {
    message.error('Failed to fetch varieties');
  }
}

async function handleCreateDurian(data: any) {
  try {
    data.walletAddress = walletAddress;
    await createDurian(data);
    message.success('Durian added successfully');
    showAddDialog.value = false;
    fetchDurianList();
  } catch (err: any) {
    message.error(err.message || 'Failed to save durian');
  }
}

onMounted(async () => {
  fetchFarms();
  await fetchVarieties();
  await fetchDurianList();
});
</script>

<style scoped>
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: nowrap;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 12px;
}

.searchField {
}

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


</style>