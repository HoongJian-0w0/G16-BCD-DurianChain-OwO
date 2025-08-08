<template>
  <el-main>
    <!-- Summary Card -->
    <!-- Summary Cards by Role -->
    <div class="onchain-card-container">
      <el-card class="summary-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper admin-bg">
            <el-icon size="32"><UserFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Admins</div>
            <div class="summary-value">{{ roleStats.admin }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper farmer-bg">
            <el-icon size="32"><UserFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Farmers</div>
            <div class="summary-value">{{ roleStats.farmer }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper logistics-bg">
            <el-icon size="32"><UserFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Logistics</div>
            <div class="summary-value">{{ roleStats.logistics }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="summary-card" shadow="always">
        <div class="summary-content">
          <div class="icon-wrapper trader-bg">
            <el-icon size="32"><UserFilled /></el-icon>
          </div>
          <div class="summary-info">
            <div class="summary-title">Traders</div>
            <div class="summary-value">{{ roleStats.trader }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Search Form -->
    <el-form :inline="true" :model="searchParams" size="small">
      <el-form-item class="searchField">
        <el-input prefix-icon="User" v-model="searchParams.username" placeholder="Username" clearable />
      </el-form-item>
      <el-form-item class="searchField">
        <el-input prefix-icon="Message" v-model="searchParams.email" placeholder="Email" clearable />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleSearch">Search</el-button>
        <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :height="`calc(100vh - 330px)`" :data="tableList" style="width: 100%;" size="medium">
      <el-table-column prop="walletAddress" label="Wallet Address" />
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="phone" label="Phone" />
      <el-table-column prop="role" label="Role" />
      <el-table-column label="Approved">
        <template #default="{ row }">
          <el-tag :type="row.isApproved ? 'success' : 'warning'">
            {{ row.isApproved ? 'Yes' : 'No' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Updated Time">
        <template #default="{ row }">
          {{ formatDate(row.updatedTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Actions">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="Edit" size="small" plain @click="showUpdateDialog(scope.row)">Edit</el-button>
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
    <AddUser
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
import AddUser from '@/views/durianchain/admin/user/addUser.vue';
import {
  getUserPage,
  createUser,
  updateUser
} from '@/api/admin/user';
import type { UserModel } from '@/api/admin/user/UserModels';
import { formatDate } from '@/utils/time';

const roleStats = reactive({
  admin: 0,
  farmer: 0,
  logistics: 0,
  trader: 0
});

const tableList = ref<UserModel[]>([]);
const total = ref(0);

const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  email: ''
});

const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'update'>('create');
const dialogFormData = ref<UserModel | null>(null);

async function fetchUsers() {
  try {
    const res = await getUserPage({ ...searchParams });
    tableList.value = res.data.page.records;
    total.value = res.data.page.total;

    const stats = res.data.stats || {};
    roleStats.admin = stats.admin || 0;
    roleStats.farmer = stats.farmer || 0;
    roleStats.logistics = stats.logistics || 0;
    roleStats.trader = stats.trader || 0;
  } catch (err: any) {
    message.error(err.message || 'Failed to load user list');
  }
}
function handleSearch() {
  searchParams.pageNum = 1;
  fetchUsers();
}

function handleReset() {
  Object.assign(searchParams, {
    username: '',
    email: '',
    pageNum: 1,
    pageSize: 10
  });
  fetchUsers();
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchUsers();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchUsers();
}

function showAddDialog() {
  dialogMode.value = 'create';
  dialogFormData.value = null;
  dialogVisible.value = true;
}

function showUpdateDialog(row: UserModel) {
  dialogMode.value = 'update';
  dialogFormData.value = { ...row };
  dialogVisible.value = true;
}

async function handleDialogSubmit(data: UserModel) {
  try {
    if (dialogMode.value === 'create') {
      await createUser(data);
      message.success('User added successfully');
    } else {
      await updateUser(data);
      message.success('User updated successfully');
    }
    dialogVisible.value = false;
    fetchUsers();
  } catch (err: any) {
    message.error(err.message || 'Operation failed');
  }
}

onMounted(fetchUsers);
</script>

<style scoped>
.searchField {
  width: 100%;
  max-width: 240px;
  margin-right: 8px;
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
  transition: 0.3s ease;
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
.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.admin-bg {
  background-color: rgba(245, 108, 108, 0.15);
  color: #f56c6c;
}
.farmer-bg {
  background-color: rgba(103, 194, 58, 0.15);
  color: #67c23a;
}
.logistics-bg {
  background-color: rgba(230, 162, 60, 0.15);
  color: #e6a23c;
}
.trader-bg {
  background-color: rgba(144, 147, 153, 0.15);
  color: #909399;
}
</style>
