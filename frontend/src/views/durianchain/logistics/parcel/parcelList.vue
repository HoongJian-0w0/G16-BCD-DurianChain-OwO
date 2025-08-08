<template>
  <el-main>
    <!-- Summary Cards -->
    <div class="summary-header">
      <div class="onchain-card-container">

        <!-- Ordered -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #e3f2fd; color: #1e88e5;">
              <el-icon size="32"><Edit /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Pending Collect</div>
              <div class="summary-value">{{ onChainStats.ordered }}</div>
            </div>
          </div>
        </el-card>

        <!-- Collected -->
        <el-card class="summary-card light" shadow="always">
          <div class="summary-content">
            <div class="icon-wrapper" style="background-color: #fff3e0; color: #fb8c00;">
              <el-icon size="32"><TakeawayBox /></el-icon>
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
            <div class="icon-wrapper" style="background-color: #e8f5e9; color: #43a047;">
              <el-icon size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="summary-info">
              <div class="summary-title">Status: Delivered</div>
              <div class="summary-value">{{ onChainStats.delivered }}</div>
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
              v-model="searchParams.logisticsCompanyId"
              placeholder="Select Logistics Company"
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
            <el-option label="Ordered" value="Ordered" />
            <el-option label="Collected" value="Collected" />
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
                    row.status === 'Collected' ? 'info' :
                    row.status === 'Delivered' ? 'success' : 'default'">
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
                v-if="scope.row.status === 'Ordered'"
                icon="ShoppingCart"
                type="primary"
                size="small"
                plain
                @click="handleCollect(scope.row)"
            >Collect</el-button>

            <el-button
                v-if="scope.row.status === 'Collected'"
                icon="CircleCheck"
                type="success"
                size="small"
                plain
                @click="handleComplete(scope.row)"
            >Complete</el-button>
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

    <!-- View Drawer (same) -->
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
        <el-descriptions-item label="Durians" :span="1">
          <div v-if="viewData.durians?.length" class="durian-table-wrapper">
            <el-table :data="viewData.durians" style="width: 100%; margin-top: 12px;" size="small" border>
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

    <!-- Milestone Drawer -->
    <el-drawer v-model="milestoneVisible" :title="`Milestones for ${selectedBatchId}`" size="500px" class="milestone-drawer">
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

    <!-- Hidden file input -->
    <input
        type="file"
        id="qr-file-input"
        accept="image/*"
        style="display: none"
        @change="handleQrFile"
    />

    <el-dialog v-model="qrDialogVisible" title="Scan or Upload QR Code" width="500px" destroy-on-close>
      <!-- Wrapper with column layout -->
      <div
          style="
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
      padding: 12px 0;
    "
      >
        <!-- Camera Preview -->
        <div
            id="qr-reader"
            style="width: 100%; max-width: 450px; height: 300px; border: 1px solid #ccc; border-radius: 8px;"
        />

        <!-- Upload Button -->
        <el-button type="primary" plain icon="Upload" @click="switchToFileScan">
          Upload Image
        </el-button>
      </div>
    </el-dialog>

    <AddParcel
        v-model:visible="addParcelVisible"
        :batch-data="selectedParcelBatch"
        @submit="handleCollectSuccess"
    />
  </el-main>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted, watch, nextTick} from 'vue';
import { getBatchPage, getBatchesByStatus } from '@/api/farmer/batch/index';
import { getMyLogisticsCompanyIds } from '@/contracts/logistics/logisticsCompanyContract';
import message from '@/utils/message';
import type { BatchModel, BatchQueryParams } from '@/api/farmer/batch/BatchModels';
import { useUserStore } from '@/store/user';
import { getDuriansByBatchId } from '@/api/farmer/durian';
import { getBatchMilestone } from '@/contracts/farmer/batchContract';
import AddParcel from './addParcel.vue';
import {confirmDelivery} from "@/contracts/logistics/parcelContract";
import {confirmDBDelivery} from "@/api/logistics/parcel";
import {Html5Qrcode} from "html5-qrcode";
import {scanQrFromFile, startQrScanner, stopQrScanner} from "@/utils/qrscan";

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
  statuses: ['Ordered'],
  logisticsAddress: walletAddress,
  logisticsCompanyId: '',
  logisticsNullOrMe: true,
});

const onChainStats = reactive({
  ordered: 0,
  collected: 0,
  delivered: 0
});

const addParcelVisible = ref(false);
const selectedParcelBatch = ref({ batchId: '', foodName: '' });

function handleCollect(row: BatchModel) {
  selectedParcelBatch.value = {
    batchId: row.batchId,
    foodName: row.foodName
  };
  addParcelVisible.value = true;
}

function handleCollectSuccess() {
  fetchTable();
  fetchOffChainStatusStats();
}

async function fetchAgencies() {
  try {
    const ids = await getMyLogisticsCompanyIds();
    agencyOptions.value = ids.map((id: string) => ({
      label: id,
      value: id
    }));
  } catch (err: any) {
    console.error('[fetchAgencies Error]', err);
    message.error(err.message || 'Failed to load logistics companies');
  }
}

async function fetchTable() {
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
    logisticsCompanyId: '',
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


async function handleComplete(row: BatchModel) {
  try {
    const txHash = await confirmDelivery(row.batchId);

    await confirmDBDelivery({
      batchId: row.batchId,
      txHash: txHash
    });

    message.success(`Batch ${row.batchId} marked as delivered.`);
    fetchTable();
    fetchOffChainStatusStats();
  } catch (err: any) {
    console.error('[Complete Error]', err);
    message.error(err.message || 'Failed to complete batch');
  }
}

async function fetchOffChainStatusStats() {
  try {
    const res = await getBatchesByStatus({
      statuses: ['Ordered', 'Collected', 'Delivered']
    });

    const list = res.data.list || [];

    const ordered = list.filter(
        item => item.status?.toLowerCase() === 'ordered'
    ).length;

    const collected = list.filter(
        item => item.status?.toLowerCase() === 'collected' &&
            item.logisticsAddress === walletAddress
    ).length;

    const delivered = list.filter(
        item => item.status?.toLowerCase() === 'delivered' &&
            item.logisticsAddress === walletAddress
    ).length;

    Object.assign(onChainStats, { ordered, collected, delivered });
  } catch (err: any) {
    message.error(err.message || 'Failed to load batch summary stats');
  }
}

function handleScanQR() {
  qrDialogVisible.value = true;

  nextTick(() => {
    startQrScanner(
        'qr-reader',
        async (decodedText: string) => {
          qrDialogVisible.value = false;
          searchParams.batchId = decodedText;
          await fetchTable();
          message.success('QR Code Scanned: ' + decodedText);
        },
        (err) => {
          console.warn('QR Scan Error', err);
        }
    );
  });
}

function handleStatusChange(val: string[]) {
  if (!val || val.length === 0) {
    searchParams.statuses = ['Ordered'];
  }
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
    searchParams.batchId = decoded;
    qrDialogVisible.value = false;
    fetchTable();
  } catch (err) {
    message.error('Image scan failed');
    console.error(err);
  } finally {
    target.value = '';
  }
}

onMounted(() => {
  fetchAgencies();
  fetchTable();
  fetchOffChainStatusStats();
});

watch(() => searchParams.statuses, (newVal) => {
  if (!newVal || newVal.length === 0) {
    searchParams.statuses = ['Ordered'];
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
