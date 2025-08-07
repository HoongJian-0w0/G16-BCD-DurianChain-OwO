<template>
  <el-main>
    <!-- Summary Card -->
    <div class="onchain-card-container">
      <el-card class="summary-card light farms-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper info-bg">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Total Varieties</div>
            <div class="summary-value">{{ total }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Search Form -->
    <el-form :inline="true" :model="searchParams" size="small">
      <el-form-item class="searchField">
        <el-input prefix-icon="Search" v-model="searchParams.varietyId" placeholder="Enter Variety ID" clearable />
      </el-form-item>
      <el-form-item class="searchField">
        <el-input prefix-icon="Edit" v-model="searchParams.name" placeholder="Enter Name" clearable />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleSearch">Search</el-button>
        <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
        <el-button icon="Plus" type="primary" @click="showAddDialog">Add</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :height="`calc(100vh - 330px)`" :data="tableList" style="width: 100%;" size="medium">
      <el-table-column prop="varietyId" label="Variety ID" sortable />
      <el-table-column prop="name" label="Name" sortable />
      <el-table-column prop="originRegion" label="Origin Region" />
      <el-table-column label="Description">
        <template #default="{ row }">
          {{ row.description || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="220px">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="Edit" size="small" plain @click="showUpdateDialog(scope.row)">Edit</el-button>
            <el-button icon="Delete" type="danger" size="small" plain @click="handleDelete(scope.row)">Delete</el-button>
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
    <AddVariety
        v-model:visible="dialogVisible"
        :mode="dialogMode"
        :initialData="dialogFormData"
        @submit="handleDialogSubmit"
    />
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import message from '@/utils/message';
import AddVariety from '@/views/durianchain/admin/variety/addVariety.vue';
import dialogConfirm from '@/utils/dialogConfirm';
import {
  getVarietyPage,
  createVariety,
  updateVariety,
  deleteVarietyById
} from '@/api/admin/variety';
import type { VarietyModel } from '@/api/admin/variety/VarietyModels';

const tableList = ref<VarietyModel[]>([]);
const total = ref(0);

// Updated search params with originRegion support
const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  varietyId: '',
  name: '',
  originRegion: ''
});

const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'update'>('create');
const dialogFormData = ref<VarietyModel | null>(null);

async function fetchVarieties() {
  try {
    const res = await getVarietyPage({ ...searchParams });
    tableList.value = res.data.page.records;
    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load durian varieties');
  }
}

function handleSearch() {
  searchParams.pageNum = 1;
  fetchVarieties();
}

function handleReset() {
  Object.assign(searchParams, {
    varietyId: '',
    name: '',
    originRegion: '',
    pageNum: 1,
    pageSize: 10
  });
  fetchVarieties();
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchVarieties();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchVarieties();
}

function showAddDialog() {
  dialogMode.value = 'create';
  dialogFormData.value = null;
  dialogVisible.value = true;
}

function showUpdateDialog(row: VarietyModel) {
  dialogMode.value = 'update';
  dialogFormData.value = { ...row };
  dialogVisible.value = true;
}

async function handleDialogSubmit(data: VarietyModel) {
  try {
    if (dialogMode.value === 'create') {
      await createVariety(data);
      message.success('Variety added successfully');
    } else {
      await updateVariety(data);
      message.success('Variety updated successfully');
    }
    dialogVisible.value = false;
    fetchVarieties();
  } catch (err: any) {
    message.error(err.message || 'Operation failed');
  }
}

const handleDelete = async (row: any) => {
  const confirmed = await dialogConfirm(`Are you sure you want to delete variety ${row.varietyId}?`);
  if (!confirmed) return;

  try {
    await deleteVarietyById(row.id);
    message.success('Deleted successfully!');
    fetchVarieties();
  } catch (err) {
    message.error('Failed to delete!');
  }
};

onMounted(fetchVarieties);
</script>

<style scoped>
.searchField { width: 100%; max-width: 240px; margin-right: 8px; }
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
.summary-info { display: flex; flex-direction: column; }
.summary-title { font-size: 14px; font-weight: 500; color: #606266; }
.summary-value { font-size: 24px; font-weight: bold; margin-top: 4px; color: #303133; }
.summary-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }
</style>
