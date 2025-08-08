<template>
  <el-main>
    <!-- Summary Cards + Add Button -->
    <div class="summary-header">
      <div class="onchain-card-container">
        <!-- On-chain Total -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #e8f5e9; color: #43a047;">
              <el-icon size="32"><CircleCheck /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">On-Chain Batches</div>
              <div class="summary-value">{{ onChainStats.total }}</div>
            </div>
          </div>
        </el-card>

        <!-- Created -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #e3f2fd; color: #1e88e5;">
              <el-icon size="32"><Edit /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Created</div>
              <div class="summary-value">{{ onChainStats.created }}</div>
            </div>
          </div>
        </el-card>

        <!-- Ordered -->
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

        <!-- Collected -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #f1f8e9; color: #7cb342;">
              <el-icon size="32"><Van /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Collected</div>
              <div class="summary-value">{{ onChainStats.collected }}</div>
            </div>
          </div>
        </el-card>

        <!-- Delivered -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #ede7f6; color: #673ab7;">
              <el-icon size="32"><CircleCheck /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Delivered</div>
              <div class="summary-value">{{ onChainStats.delivered }}</div>
            </div>
          </div>
        </el-card>

        <!-- Deleted -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #ffebee; color: #e53935;">
              <el-icon size="32"><Delete /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Deleted</div>
              <div class="summary-value">{{ onChainStats.deleted }}</div>
            </div>
          </div>
        </el-card>

      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-form :inline="true" :model="searchParams" size="small" class="filter-form">
        <el-form-item class="searchField">
          <el-input v-model="searchParams.batchId" placeholder="Enter Batch ID" clearable />
        </el-form-item>
        <el-form-item class="searchField">
          <el-input v-model="searchParams.farmId" placeholder="Enter Farm ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button icon="Search" @click="handleSearch">Search</el-button>
          <el-button icon="Close" type="danger" plain @click="handleReset">Clear</el-button>
          <el-button type="primary" @click="addVisible = true">Add Batch</el-button>
          <el-button icon="Camera" type="primary" plain @click="handleScanQR">Scan QR</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Table -->
    <el-table :data="tableList" :height="`calc(100vh - 350px)`" style="width: 100%;" size="medium">
      <el-table-column label="Batch ID" sortable>
        <template #default="{ row }">
          <el-link type="primary" @click="showBatchQr(row.batchId)">
            {{ row.batchId }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="foodName" label="Food Name" />
      <el-table-column prop="farmId" label="Farm ID" />
      <el-table-column prop="quantity" label="Quantity" />
      <el-table-column prop="farmLocation" label="Farm Location" />
      <el-table-column prop="deliveryDestination" label="Delivery Destination" />
      <el-table-column prop="status" label="Status">
        <template #default="{ row }">
          <el-tag
              :type="row.status === 'Delivered' ? 'success' :
                    row.status === 'Ordered' ? 'warning' :
                    row.status === 'Collected' ? 'info' :
                    row.status === 'Deleted' ? 'danger' : 'default'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="220px">
        <template #default="scope">
          <el-space wrap size="small">
            <el-button icon="View" size="small" plain @click="viewBatch(scope.row)">
              View
            </el-button>
            <template v-if="scope.row.status === 'Created'">
              <el-button icon="Edit" size="small" type="primary" plain @click="editBatch(scope.row)">
                Edit
              </el-button>
              <el-button icon="Delete" size="small" type="danger" plain @click="deleteBatch(scope.row)">
                Delete
              </el-button>
            </template>
            <el-button icon="DataAnalysis" size="small" plain type="info" @click="viewMilestone(scope.row)">
              Milestone
            </el-button>
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

    <el-dialog
        v-model="qrVisible"
        :title="`QR Code for Batch: ${selectedQrBatchId}`"
        width="300px"
        center
    >
      <div style="text-align: center;">
        <el-image
            :src="qrImageUrl"
            style="width: 200px; height: 200px;"
            fit="contain"
        />
        <p style="margin-top: 12px; font-size: 14px; color: #606266;">
          {{ selectedQrBatchId }}
        </p>
        <el-button type="primary" size="small" @click="downloadQrCode">
          Download PNG
        </el-button>
      </div>
    </el-dialog>

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

    <!-- Add Batch Dialog -->
    <AddBatch
        ref="addBatchRef"
        v-model:visible="addVisible"
        @submit="handleAddSubmit"
    />
  </el-main>
</template>


<script setup lang="ts">
import {ref, reactive, onMounted, nextTick} from 'vue';
import {getBatchPage, saveBatchOffChain, updateBatch} from '@/api/farmer/batch/index';
import message from '@/utils/message';
import type { BatchModel, BatchQueryParams } from '@/api/farmer/batch/BatchModels';
import AddBatch from '@/views/durianchain/farmer/batch/addBatch.vue';
import {getMyBatchIds, getBatchById, getBatchMilestone, removeBatch} from '@/contracts/farmer/batchContract';
import {getDuriansByBatchId, releaseDurians} from "@/api/farmer/durian";
import dialogConfirm from "@/utils/dialogConfirm";
import {useUserStore} from "@/store/user";
import QRCode from 'qrcode';
import { Html5Qrcode } from 'html5-qrcode';
import { scanQrFromFile, startQrScanner, stopQrScanner } from '@/utils/qrscan';

const qrDialogVisible = ref(false);
let qrReader: Html5Qrcode | null = null;

const qrVisible = ref(false);
const qrImageUrl = ref('');
const selectedQrBatchId = ref('');

const addBatchRef = ref<InstanceType<typeof AddBatch>>();

const tableList = ref<BatchModel[]>([]);
const total = ref(0);
const viewVisible = ref(false);
const addVisible = ref(false);
const viewData = ref<BatchModel & { batchImageUrl?: string }>({} as any);

const milestoneVisible = ref(false);
const milestoneList = ref<any[]>([]);
const selectedBatchId = ref('');

const userStore = useUserStore();

const onChainStats = reactive({
  total: 0,
  created: 0,
  ordered: 0,
  collected: 0,
  delivered: 0,
  deleted: 0
});

const searchParams = reactive<BatchQueryParams>({
  pageNum: 1,
  pageSize: 10,
  batchId: '',
  farmId: '',
  farmerAddress: userStore.walletAddress,
});

function handleSearch() {
  searchParams.pageNum = 1;
  fetchBatchList();
}

function handleReset() {
  Object.assign(searchParams, {
    batchId: '',
    farmId: '',
    pageNum: 1,
    pageSize: 10
  });
  fetchBatchList();
}

function sizeChange(size: number) {
  searchParams.pageSize = size;
  fetchBatchList();
}

function currentChange(page: number) {
  searchParams.pageNum = page;
  fetchBatchList();
}

async function fetchBatchList() {
  try {
    const res = await getBatchPage({ ...searchParams });
    const records = res.data.page.records || [];
    tableList.value = records.map((b: BatchModel) => ({
      ...b,
      batchImageUrl: b.batchImageCid ? `https://ipfs.io/ipfs/${b.batchImageCid}` : ''
    }));
    total.value = res.data.page.total;
  } catch (err: any) {
    message.error(err.message || 'Failed to load batches');
  }
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

async function showBatchQr(batchId: string) {
  try {
    selectedQrBatchId.value = batchId;
    const url = await QRCode.toDataURL(batchId);
    qrImageUrl.value = url;
    qrVisible.value = true;
  } catch (err) {
    console.error('[QR Error]', err);
    message.error('Failed to generate QR code');
  }
}

async function handleAddSubmit(batch: BatchModel & { imageHashes: string[] }) {
  try {
    const payload = {
      batchId: batch.batchId,
      variety: batch.foodName,
      quantity: batch.quantity,
      farmId: batch.farmId,
      batchImageCID: batch.batchImageCid!,
      durianIds: batch.durianIds!,
      imageHashes: batch.imageHashes!,
      farmerAddress: batch.farmerAddress!,
      farmLocation: batch.farmLocation!,
      txHash: batch.txHash!,
    };

    const isEdit = tableList.value.some(b => b.batchId === batch.batchId);

    await saveBatchOffChain(payload);

    message.success(`Batch ${isEdit ? 'Updated' : 'Created'} in DB Successfully`);

    fetchBatchList();
    fetchOnChainStats();
  } catch (err: any) {
    console.error('[handleAddSubmit Error]', err);
    message.error(err.message || 'Failed to save batch');
  }
}

async function fetchOnChainStats() {
  try {
    const batchIds = await getMyBatchIds();
    onChainStats.total = batchIds.length;

    // Reset all counts
    onChainStats.created = 0;
    onChainStats.ordered = 0;
    onChainStats.collected = 0;
    onChainStats.delivered = 0;
    onChainStats.deleted = 0;

    for (const batchId of batchIds) {
      const b = await getBatchById(batchId);
      switch (b.status) {
        case 0: onChainStats.created++; break;
        case 1: onChainStats.ordered++; break;
        case 2: onChainStats.collected++; break;
        case 3: onChainStats.delivered++; break;
        case 4: onChainStats.deleted++; break;
      }
    }
  } catch (err: any) {
    console.error('[fetchOnChainStats Error]', err);
    message.error('Failed to fetch on-chain batch stats');
  }
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

function editBatch(row: BatchModel) {
  addVisible.value = true;
  nextTick(() => {
    addBatchRef.value?.loadBatch(row);
  });
}

function downloadQrCode() {
  const link = document.createElement('a');
  link.href = qrImageUrl.value;
  link.download = `${selectedQrBatchId.value}_qrcode.png`;
  link.click();
}

async function deleteBatch(row: BatchModel) {
  const confirmed = await dialogConfirm(`Are you sure you want to delete batch "${row.batchId}"?`);
  if (!confirmed) return;

  try {
    // 1. Remove from blockchain
    const txHash = await removeBatch(row.batchId);
    message.success(`Batch ${row.batchId} deleted on-chain\nTx: ${txHash}`);

    // 2. Update batch status to 'Deleted' in DB
    await updateBatch({
      ...row,
      txHash,
      status: 'Deleted'
    });
    message.success(`Batch ${row.batchId} marked as Deleted in DB`);

    // 3. Release durians assigned to the batch
    await releaseDurians(row.batchId);
    message.success(`Durians released from batch ${row.batchId}`);

    // 4. Refresh data
    fetchBatchList();
    fetchOnChainStats();
  } catch (err: any) {
    console.error('[deleteBatch Error]', err);
    message.error(err.message || 'Failed to delete batch');
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
          await fetchBatchList();                 // ← Customize this part for each page
          message.success('QR Code Scanned: ' + decodedText);
        },
        (err) => {
          console.warn('QR Scan Error', err);
        }
    );
  });
}

function switchToFileScan() {
  const input = document.getElementById('qr-file-input') as HTMLInputElement;
  if (input) input.click();
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
    fetchBatchList();                  // ← Customize this part for each page
  } catch (err) {
    message.error('Image scan failed');
    console.error(err);
  } finally {
    target.value = '';
  }
}


onMounted(() => {
  fetchBatchList();
  fetchOnChainStats();
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
  color: #409EFF;
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

.durian-table-wrapper {
  margin-top: 12px;
}
.durian-table-wrapper .el-table {
  font-size: 12px;
}
.durian-table-wrapper .el-image {
  border: 1px solid #ebeef5;
}
</style>
