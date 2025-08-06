<template>
  <el-main>
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

    <el-table :height="tableHeight" :data="tableList" style="width: 100%" size="medium">
      <el-table-column prop="farmId" label="Farm ID" sortable />
      <el-table-column prop="location" label="Location" sortable />
      <el-table-column prop="certificateCID" label="Certificate CID" />
      <el-table-column prop="certificateExpiry" label="Certificate Expiry" />
      <el-table-column label="Actions" width="240px">
        <template #default="scope">
          <el-button icon="View" size="small" plain @click="viewFarm(scope.row)">View</el-button>
          <el-button icon="Edit" type="success" size="small" plain @click="showUpdateDialog(scope.row)">Update</el-button>
          <el-button icon="Document" type="warning" size="small" plain @click="viewHistory(scope.row.farmId)">History</el-button>
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

    <AddFarm
        v-model:visible="dialogVisible"
        :mode="dialogMode"
        :initialData="dialogFormData"
        @submit="handleDialogSubmit"
    />

  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import AddFarm from '@/views/durianchain/farmer/farm/addFarm.vue';
import { getMyFarmIds, getFarmById, registerFarm, updateFarmCertificate, getFarmMilestone } from '@/contracts/farmContract';
import message from '@/utils/message';
import dayjs from 'dayjs';

const tableHeight = ref(400);
const tableList = ref<any[]>([]);
const allFarms = ref<any[]>([]);
const total = ref(0);

const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  farmId: '',
  location: '',
});

const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'update'>('create');
const dialogFormData = ref<any>(null);

async function fetchFarms() {
  try {
    const ids = await getMyFarmIds();
    const farms = await Promise.all(ids.map(id => getFarmById(id)));
    allFarms.value = farms.map((f, index) => ({
      farmId: ids[index],
      location: f.location,
      certificateCID: f.certificateCID,
      certificateExpiry: dayjs.unix(Number(f.certificateExpiry)).format('YYYY-MM-DD HH:mm'),
      owner: f.owner,
    }));
    applyFilter();
  } catch (err: any) {
    message.error(err.message || 'Failed to load farms');
  }
}

function applyFilter() {
  let filtered = allFarms.value;
  if (searchParams.farmId) {
    filtered = filtered.filter(f => f.farmId.includes(searchParams.farmId));
  }
  if (searchParams.location) {
    filtered = filtered.filter(f => f.location.toLowerCase().includes(searchParams.location.toLowerCase()));
  }
  total.value = filtered.length;
  const start = (searchParams.pageNum - 1) * searchParams.pageSize;
  const end = start + searchParams.pageSize;
  tableList.value = filtered.slice(start, end);
}

function handleSearch() {
  searchParams.pageNum = 1;
  applyFilter();
}

function handleReset() {
  searchParams.farmId = '';
  searchParams.location = '';
  searchParams.pageNum = 1;
  searchParams.pageSize = 10;
  applyFilter();
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  applyFilter();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  applyFilter();
}

function showAddDialog() {
  dialogMode.value = 'create';
  dialogFormData.value = null;
  dialogVisible.value = true;
}

function showUpdateDialog(row: any) {
  dialogMode.value = 'update';
  dialogFormData.value = {
    farmId: row.farmId,
    location: row.location,
    certificateCID: row.certificateCID,
    certificateExpiry: dayjs(row.certificateExpiry).valueOf(), // convert back to timestamp (ms)
  };
  dialogVisible.value = true;
}

async function handleDialogSubmit(data: any) {
  try {
    if (dialogMode.value === 'create') {
      await registerFarm(data.farmId, data.location, data.certificateCID, Number(data.certificateExpiry));
      message.success('Farm registered successfully');
    } else {
      await updateFarmCertificate(data.farmId, data.certificateCID, Number(data.certificateExpiry));
      message.success('Certificate updated successfully');
    }
    dialogVisible.value = false;
    await fetchFarms();
  } catch (err: any) {
    message.error(err.message || 'Operation failed');
  }
}

async function viewFarm(row: any) {
  message.success(`📄 Viewing farm: ${row.farmId}\nLocation: ${row.location}`);
}

async function viewHistory(farmId: string) {
  try {
    const milestone = await getFarmMilestone(farmId);

    if (!milestone || milestone.length === 0) {
      return message.warning(`❌ No milestone history found for farm: ${farmId}`);
    }

    const historyLog = milestone
        .map((entry) =>
            `${entry.action} (${entry.role} @ ${dayjs.unix(Number(entry.timestamp)).format('YYYY-MM-DD HH:mm')} by ${entry.actor}):\n  ${entry.description}`
        )
        .join('\n\n');

    message.success(`📜 History for ${farmId}:\n\n${historyLog}`);
  } catch (err: any) {
    message.error(err.message || 'Failed to load history');
  }
}
onMounted(async () => {
  await nextTick(() => {
    tableHeight.value = window.innerHeight - 250;
  });
  await fetchFarms();
});
</script>

<style scoped>
.searchField {
  width: 100%;
  max-width: 240px;
}
</style>
