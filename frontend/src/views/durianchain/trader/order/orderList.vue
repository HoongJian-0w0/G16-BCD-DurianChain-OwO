<template>
  <el-main>
    <!-- Summary Cards -->
    <div class="summary-header">
      <div class="onchain-card-container">
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #e3f2fd; color: #1e88e5;">
              <el-icon size="32"><Edit /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Pending Order</div>
              <div class="summary-value">{{ onChainStats.created }}</div>
            </div>
          </div>
        </el-card>

        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #fff8e1; color: #f9a825;">
              <el-icon size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Ordered</div>
              <div class="summary-value">{{ onChainStats.ordered }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-form :inline="true" :model="searchParams" size="small" class="filter-form">
        <el-form-item class="searchField">
          <el-select
              v-model="searchParams.traderAgencyId"
              placeholder="Select Trader Agency"
              clearable
              style="width: 220px"
          >
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
            <el-option label="None" :value="''" />
            <el-option
                v-for="agency in agencyOptions"
                :key="agency.value"
                :label="agency.label"
                :value="agency.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="searchField">
          <el-select
              v-model="searchParams.statuses"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="Select Status"
              clearable
              style="width: 160px"
              @change="handleStatusChange"
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
            <el-option label="Created" value="Created" />
            <el-option label="Ordered" value="Ordered" />
            <el-option label="Delivered" value="Delivered" />
          </el-select>
        </el-form-item>
        <el-form-item class="searchField">
          <el-input v-model="searchParams.batchId" placeholder="Enter Batch ID" clearable />
        </el-form-item>
        <el-form-item class="searchField">
          <el-input v-model="searchParams.farmId" placeholder="Enter Farm ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button icon="Search" @click="handleSearch">Search</el-button>
          <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
          <el-button icon="Camera" type="primary" plain @click="handleScanQR">Scan QR</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <el-table :data="tableList" :height="`calc(100vh - 350px)`" style="width: 100%;" size="medium">
      <el-table-column prop="batchId" label="Batch ID" />
      <el-table-column prop="foodName" label="Durian Variety" />
      <el-table-column prop="farmId" label="Farm ID" />
      <el-table-column prop="quantity" label="Quantity" />
      <el-table-column prop="farmLocation" label="Location" />
      <el-table-column prop="deliveryDestination" label="Delivery To" />
      <el-table-column prop="status" label="Status">
        <template #default="{ row }">
          <el-tag
              :type="row.status === 'Ordered' ? 'warning' :
                    row.status === 'Created' ? 'info' : 'default'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Action" width="260">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="View" size="small" plain @click="viewBatch(scope.row)">View</el-button>
            <el-button icon="DataAnalysis" size="small" plain type="info" @click="viewMilestone(scope.row)">
              Milestone
            </el-button>
            <el-button
                v-if="scope.row.status === 'Created'"
                icon="ShoppingCart"
                type="primary"
                size="small"
                plain
                @click="handleOrder(scope.row)"
            >Order</el-button>
            <el-button
                v-if="scope.row.status === 'Ordered' && scope.row.traderAddress === walletAddress"
                icon="CloseBold"
                type="danger"
                size="small"
                plain
                @click="handleCancel(scope.row)"
            >Cancel</el-button>
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
        :page-sizes="[5, 10, 20]"
        :page-size="searchParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background
    />

    <!-- View Drawer -->
    <el-drawer v-model="viewVisible" title="Batch Details" size="850px">
      <el-descriptions :column="1" border style="max-width: 800px;">
        <el-descriptions-item label="Batch ID">{{ viewData.batchId }}</el-descriptions-item>
        <el-descriptions-item label="Food Name">{{ viewData.foodName }}</el-descriptions-item>
        <el-descriptions-item label="Farm ID">{{ viewData.farmId }}</el-descriptions-item>
        <el-descriptions-item label="Quantity">{{ viewData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="Farm Location">{{ viewData.farmLocation }}</el-descriptions-item>
        <el-descriptions-item label="Delivery Destination">{{ viewData.deliveryDestination }}</el-descriptions-item>
        <el-descriptions-item label="Farmer Address">{{ viewData.farmerAddress }}</el-descriptions-item>
        <el-descriptions-item label="Trader Address">{{ viewData.traderAddress }}</el-descriptions-item>
        <el-descriptions-item label="Logistics Address">{{ viewData.logisticsAddress }}</el-descriptions-item>
        <el-descriptions-item label="Tx Hash">{{ viewData.txHash }}</el-descriptions-item>
        <el-descriptions-item label="Status">{{ viewData.status }}</el-descriptions-item>
        <el-descriptions-item label="Image">
          <el-image :src="viewData.batchImageUrl" style="width: 350px;" fit="contain" v-if="viewData.batchImageUrl" />
        </el-descriptions-item>

        <!-- New: Durians in Batch -->
        <el-descriptions-item label="Durians" :span="1">
          <div v-if="viewData.durians?.length" class="durian-table-wrapper">
            <el-table
                :data="viewData.durians"
                style="width: 100%; margin-top: 12px;"
                size="small"
                border
            >
              <el-table-column label="Durian ID" prop="durianId" />
              <el-table-column label="Image" width="120">
                <template #default="{ row }">
                  <el-image
                      :src="row.imageUrl"
                      fit="cover"
                      style="width: 80px; height: 80px; border-radius: 6px;"
                      :preview-src-list="[row.imageUrl]"
                      preview-teleported
                  />
                </template>
              </el-table-column>
              <el-table-column label="Image Hash" prop="imageHash" />
            </el-table>
          </div>
          <div v-else>
            <el-empty description="No durians found for this batch" />
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-drawer
        v-model="milestoneVisible"
        :title="`Milestones for ${selectedBatchId}`"
        size="500px"
        class="milestone-drawer"
    >
      <div class="timeline">
        <el-timeline>
          <el-timeline-item
              v-for="(m, index) in milestoneList"
              :key="index"
              :timestamp="new Date(parseInt(m.timestamp) * 1000).toLocaleString()"
              placement="top"
          >
            <p><strong>{{ m.action }}</strong> - {{ m.role }}</p>
            <p><i>{{ m.description }}</i></p>
            <p><small>Actor: {{ m.actor }}</small></p>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>

    <input
        type="file"
        id="qr-file-input"
        accept="image/*"
        style="display: none"
        @change="handleQrFile"
    />

    <el-dialog v-model="qrDialogVisible" title="Scan or Upload QR Code" width="500px" destroy-on-close>
      <div style="display: flex; flex-direction: column; align-items: center; gap: 16px; padding: 12px 0;">
        <div
            id="qr-reader"
            style="width: 100%; max-width: 450px; height: 300px; border: 1px solid #ccc; border-radius: 8px;"
        />
        <el-button type="primary" plain icon="Upload" @click="switchToFileScan">
          Upload Image
        </el-button>
      </div>
    </el-dialog>

    <!-- Add Order Dialog -->
    <AddOrder
        :visible="addOrderVisible"
        :batchData="selectedBatch"
        @update:visible="val => addOrderVisible = val"
        @submit="fetchTable"
    />
  </el-main>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted, watch, nextTick} from 'vue';
import { getBatchPage, getBatchesByStatus, updateBatch } from '@/api/farmer/batch/index';
import { cancelOrder } from '@/contracts/trader/orderContract';
import { getMyTraderAgencyIds } from '@/contracts/trader/agencyContract';
import message from '@/utils/message';
import dialogConfirm from '@/utils/dialogConfirm';
import type { BatchModel, BatchQueryParams } from '@/api/farmer/batch/BatchModels';
import { useUserStore } from '@/store/user';
import AddOrder from '@/views/durianchain/trader/order/addOrder.vue';
import {cancelDBOrder} from "@/api/trader/order";
import {getDuriansByBatchId} from "@/api/farmer/durian";
import {getBatchMilestone} from "@/contracts/farmer/batchContract";
import { Html5Qrcode } from 'html5-qrcode';
import { scanQrFromFile, startQrScanner, stopQrScanner } from '@/utils/qrscan';

const qrDialogVisible = ref(false);
let qrReader: Html5Qrcode | null = null;

const userStore = useUserStore();
const walletAddress = userStore.walletAddress;

const tableList = ref<BatchModel[]>([]);
const total = ref(0);
const viewVisible = ref(false);
const viewData = ref<BatchModel & { batchImageUrl?: string }>({} as any);

const milestoneVisible = ref(false);
const milestoneList = ref<any[]>([]);
const selectedBatchId = ref('');

const agencyOptions = ref<{ label: string; value: string }[]>([]);

const searchParams = reactive<BatchQueryParams>({
  pageNum: 1,
  pageSize: 10,
  statuses: ['Created', 'Ordered'],
  traderAddress: walletAddress,
  traderNullOrMe: true,
  traderAgencyId: ''
});

const onChainStats = reactive({
  created: 0,
  ordered: 0
});

const addOrderVisible = ref(false);
const selectedBatch = ref({
  batchId: '',
  farmId: '',
  foodName: '',
});

function handleOrder(row: BatchModel) {
  selectedBatch.value = {
    batchId: row.batchId,
    farmId: row.farmId,
    foodName: row.foodName
  };
  addOrderVisible.value = true;
}

async function fetchAgencies() {
  try {
    const ids = await getMyTraderAgencyIds();
    agencyOptions.value = ids.map((id: string) => ({
      label: id,
      value: id
    }));
  } catch (err: any) {
    console.error('[fetchAgencies Error]', err);
    message.error(err.message || 'Failed to load trader agencies');
  }
}

async function fetchBatchList() {
  try {
    const res = await getBatchPage({ ...searchParams });
    const records = res.data.page.records || [];

    tableList.value = records.map((b: BatchModel) => ({
      ...b,
      batchImageUrl: b.batchImageCid
          ? `https://ipfs.io/ipfs/${b.batchImageCid}`
          : ''
    }));

    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load batches');
  }
}

function fetchTable() {
  getBatchPage(searchParams)
      .then(res => {
        const records = res.data.page.records || [];

        tableList.value = records.map((b: BatchModel) => ({
          ...b,
          batchImageUrl: b.batchImageCid
              ? `https://ipfs.io/ipfs/${b.batchImageCid}`
              : ''
        }));

        total.value = res.data.page.total;
      })
      .catch(err => {
        message.error(err.message || 'Failed to fetch orders');
      });
}

function handleSearch() {
  searchParams.pageNum = 1;
  fetchTable();
}

function handleReset() {
  Object.assign(searchParams, {
    pageNum: 1,
    pageSize: 10,
    batchId: '',
    farmId: '',
    traderAgencyId: '',
    statuses: ['Created']
  });
  fetchTable();
}

async function viewMilestone(row: BatchModel) {
  try {
    selectedBatchId.value = row.batchId;
    milestoneList.value = await getBatchMilestone(row.batchId);
    milestoneVisible.value = true;
  } catch (err: any) {
    console.error('[viewMilestone Error]', err);
    message.error('Failed to load milestone data');
  }
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchTable();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchTable();
}

async function viewBatch(row: BatchModel & { batchImageUrl?: string }) {
  viewData.value = { ...row, durians: [], batchImageUrl: row.batchImageUrl };
  viewVisible.value = true;

  try {
    const res = await getDuriansByBatchId(row.batchId);
    const durians = res.data.durians || [];

    viewData.value.durians = durians.map((d: any) => ({
      durianId: d.durianId,
      imageUrl: d.imageUrl,
      imageHash: d.imageHash
    }));
  } catch (err: any) {
    console.error('[viewBatch Error - Durians]', err);
    message.error('Failed to load durians for this batch');
  }
}

async function handleCancel(row: BatchModel) {
  const confirmed = await dialogConfirm(`Cancel your order for batch "${row.batchId}"?`);
  if (!confirmed) return;

  try {
    const txHash = await cancelOrder(row.batchId);

    await cancelDBOrder({
      batchId: row.batchId,
      txHash,
    });

    message.success(`Order for batch ${row.batchId} cancelled`);

    // 3. Refresh data
    fetchTable();
    fetchOffChainStatusStats();
  } catch (err: any) {
    console.error('[Cancel Error]', err);
    message.error(err.message || 'Failed to cancel order');
  }
}

async function fetchOffChainStatusStats() {
  try {
    const res = await getBatchesByStatus({
      statuses: ['Created', 'Ordered']
    });
    const list = res.data.list || [];

    const created = list.filter(item => item.status === 'Created').length;

    const ordered = list.filter(
        item => item.status === 'Ordered' && item.traderAddress === walletAddress
    ).length;

    Object.assign(onChainStats, { created, ordered });
  } catch (err: any) {
    message.error(err.message || 'Failed to load batch summary stats');
  }
}

function handleStatusChange(val: string[]) {
  if (!val || val.length === 0) {
    searchParams.statuses = ['Created'];
  }
}

function handleScanQR() {
  qrDialogVisible.value = true;

  nextTick(() => {
    startQrScanner(
        'qr-reader',
        async (decodedText: string) => {
          qrDialogVisible.value = false;
          searchParams.batchId = decodedText; // ← Customize this part for each page
          await fetchTable();                 // ← Customize this part for each page
          message.success('QR Code Scanned: ' + decodedText);
        },
        (err) => {
          console.warn('QR Scan Error', err);
        }
    );
  });
}

async function handleQrFile(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  try {
    const decoded = await scanQrFromFile(file);
    message.success('QR Code Scanned: ' + decoded);
    searchParams.batchId = decoded; // ← Customize this part for each page
    qrDialogVisible.value = false;
    fetchTable();                  // ← Customize this part for each page
  } catch (err) {
    message.error('Image scan failed');
    console.error(err);
  } finally {
    target.value = '';
  }
}

function switchToFileScan() {
  const input = document.getElementById('qr-file-input') as HTMLInputElement;
  if (input) input.click();
}

onMounted(() => {
  fetchAgencies();
  fetchBatchList();
  fetchTable();
  fetchOffChainStatusStats();
});

watch(() => searchParams.statuses, (newVal) => {
  if (!newVal || newVal.length === 0) {
    searchParams.statuses = ['Created'];
  }
});
</script>

<style lang="scss" scoped>
.summary-header {
  margin-bottom: 20px;

  .onchain-card-container {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
    width: 100%;
    justify-content: space-between;
  }

  .summary-card {
    flex: 1 1 250px;
    max-width: 100%;
    border-radius: 12px;
    box-sizing: border-box;

    .summary-content {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
    }

    .icon-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 12px;
    }

    .summary-info {
      flex: 1;

      .summary-title {
        font-size: 14px;
        font-weight: 500;
        color: #555;
      }

      .summary-value {
        font-size: 24px;
        font-weight: bold;
        color: #222;
      }
    }
  }
}
.milestone-drawer {
  padding: 16px;

  .el-timeline {
    padding-left: 0 !important;
  }

  .timeline {
    margin-top: 12px;

    ::v-deep(.el-timeline-item__content) {
      background-color: #f9f9f9;
      padding: 12px 16px;
      border-radius: 8px;
      border: 1px solid #ebeef5;

      p {
        margin: 4px 0;
        font-size: 13px;

        strong {
          font-weight: 600;
          color: #303133;
        }

        i {
          color: #909399;
          font-style: italic;
        }

        small {
          font-size: 12px;
          color: #b0b0b0;
        }
      }
    }

    ::v-deep(.el-timeline-item__timestamp) {
      font-size: 12px;
      color: #409EFF;
    }

    ::v-deep(.el-timeline-item__node) {
      background-color: #409EFF;
    }
  }
}
</style>
