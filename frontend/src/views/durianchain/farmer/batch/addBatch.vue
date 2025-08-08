<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="emit('update:visible', $event)"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="batchRules" label-width="140px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Batch ID" prop="batchId">
            <el-input v-model="form.batchId" disabled placeholder="Auto-generated..." />
          </el-form-item>

          <el-form-item label="Durian Variety" prop="varietyId">
            <el-select
                v-model="form.varietyId"
                placeholder="Select a durian variety"
                filterable clearable
                @change="handleVarietyChange"
            >
              <el-option
                  v-for="variety in varietyOptions"
                  :key="variety.varietyId"
                  :label="variety.name"
                  :value="variety.varietyId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="Farm ID" prop="farmId">
            <el-select
                v-model="form.farmId"
                placeholder="Select a farm"
                filterable clearable
                @change="handleFarmChange"
            >
              <el-option
                  v-for="farm in farmOptions"
                  :key="farm.farmId"
                  :label="farm.farmId"
                  :value="farm.farmId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="Farm Location" prop="farmLocation">
            <el-input v-model="form.farmLocation" disabled placeholder="Auto-filled from selected farm" />
          </el-form-item>

          <el-form-item label="Quantity" prop="quantity">
            <el-input v-model="form.quantity" disabled placeholder="Based on selected durians" />
          </el-form-item>

          <el-form-item label="Batch Image" prop="batchImageCid">
            <el-upload
                class="upload-box"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :http-request="uploadImage"
                :disabled="!isDurianSectionEnabled"
            >
              <img v-if="form.batchImageCid" :src="getImageUrl(form.batchImageCid)" class="upload-preview" />
              <el-icon v-else class="upload-placeholder"><UploadFilled /></el-icon>
            </el-upload>
          </el-form-item>

          <el-form-item label="CID" prop="batchImageCid">
            <el-input v-model="form.batchImageCid" disabled />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="Select Durians" prop="durianIds">
            <el-select
                v-model="form.durianIds"
                multiple
                placeholder="Select durians from farm"
                filterable
                clearable
                :disabled="!isDurianSectionEnabled"
            >
              <el-option
                  v-for="d in duriansFromFarm"
                  :key="d.durianId"
                  :label="d.durianId"
                  :value="d.durianId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue';
import { UploadFilled } from '@element-plus/icons-vue';
import message from '@/utils/message';

import { batchRules } from '@/api/farmer/batch/BatchModels';
import type { BatchModel } from '@/api/farmer/batch/BatchModels';
import { generateId } from '@/api/common/IDGen/index';
import { getMyFarmIds, getFarmById } from '@/contracts/farmer/farmContract';
import { getAllVarieties } from '@/api/admin/variety/index';
import { uploadToIPFS } from '@/api/common/CIDGen/index';
import {getDuriansByFarmAndVariety, getDurianHashesByIds, DurianQueryDTO} from '@/api/farmer/durian/index';
import { createBatch, updateBatchInfo } from '@/contracts/farmer/batchContract';
import { useUserStore } from '@/store/user/index';

const props = defineProps<{ visible: boolean }>();
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'submit', form: BatchModel): void;
}>();

const formRef = ref();
const isEditMode = ref(false);
const userStore = useUserStore();

const dialogTitle = computed(() => isEditMode.value ? 'Edit Batch' : 'Add New Batch');

const form = ref<BatchModel>({
  batchId: '',
  foodName: '',
  farmId: '',
  farmLocation: '',
  quantity: 0,
  batchImageCid: '',
  deliveryDestination: '',
  status: 'Created',
  durianIds: [],
  farmerAddress: userStore.walletAddress || '',
  txHash: ''
});

const farmOptions = ref<{ farmId: string }[]>([]);
const varietyOptions = ref<{ varietyId: string; name: string }[]>([]);
const duriansFromFarm = ref<{ durianId: string }[]>([]);
const durianHashes = ref<{ durianId: string; imageHash: string }[]>([]);

const isDurianSectionEnabled = computed(() => !!form.value.varietyId && !!form.value.farmId);

watch(() => form.value.durianIds, (val) => {
  form.value.quantity = val.length;
});

watch(() => props.visible, (val) => {
  if (!val) {
    isEditMode.value = false;
    resetForm();
  }
});

function loadBatch(batch: BatchModel) {
  isEditMode.value = true;
  Object.assign(form.value, batch);
  form.value.batchId = batch.batchId;

  const selectedVariety = varietyOptions.value.find(v => v.name === batch.foodName);
  if (selectedVariety) {
    form.value.varietyId = selectedVariety.varietyId;
  }

  nextTick(() => {
    if (!form.value.batchId) {
      message.warning('[loadBatch] Missing batchId, fetch skipped');
      return;
    }

    fetchDuriansByFarmAndVariety();
  });
}

defineExpose({ loadBatch });

async function handleSubmit() {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    try {
      const res = await getDurianHashesByIds({ durianIds: form.value.durianIds });
      durianHashes.value = res.data.hashes || [];

      const durianIds = durianHashes.value.map(h => h.durianId);
      const imageHashes = durianHashes.value.map(h => h.imageHash);

      let txHash: string;

      if (!isEditMode.value) {
        txHash = await createBatch(
            form.value.batchId,
            form.value.foodName,
            form.value.quantity,
            form.value.farmId,
            form.value.batchImageCid,
            durianIds,
            imageHashes
        );
        message.success(`Batch created on-chain: ${txHash}`);
      } else {
        txHash = await updateBatchInfo(
            form.value.batchId,
            form.value.quantity,
            form.value.batchImageCid,
            durianIds,
            imageHashes
        );
        message.success(`Batch updated on-chain: ${txHash}`);
      }

      form.value.txHash = txHash;

      emit('submit', {
        ...form.value,
        imageHashes
      });

      emit('update:visible', false);

    } catch (err) {
      console.error('[handleSubmit Error]', err);
      message.error('Failed to process batch');
    }
  });
}

function resetForm() {
  form.value = {
    batchId: '',
    foodName: '',
    farmId: '',
    farmLocation: '',
    quantity: 0,
    batchImageCid: '',
    deliveryDestination: '',
    status: 'Created',
    durianIds: [],
    farmerAddress: userStore.walletAddress || '',
    txHash: ''
  };
  duriansFromFarm.value = [];
  fetchBatchId();
}

async function fetchFarms() {
  try {
    const ids = await getMyFarmIds();
    const farms = await Promise.all(ids.map(id => getFarmById(id)));
    farmOptions.value = farms.map(f => ({ farmId: f.farmId }));
  } catch {
    message.error('Failed to fetch farms');
  }
}

async function fetchVarieties() {
  try {
    const res = await getAllVarieties();
    varietyOptions.value = res.data.list;
  } catch {
    message.error('Failed to fetch durian varieties');
  }
}

async function fetchBatchId() {
  if (isEditMode.value) return;
  try {
    const res = await generateId('batch');
    form.value.batchId = res.data.generatedId;
  } catch {
    message.error('Failed to generate Batch ID');
  }
}

async function handleVarietyChange() {
  const selected = varietyOptions.value.find(v => v.varietyId === form.value.varietyId);
  form.value.foodName = selected?.name || '';

  if (form.value.farmId) {
    await fetchDuriansByFarmAndVariety();
  }
}

async function handleFarmChange() {
  try {
    const farm = await getFarmById(form.value.farmId);
    form.value.farmLocation = farm.location || '';

    if (form.value.varietyId) {
      await fetchDuriansByFarmAndVariety();
    }
  } catch {
    message.error('Failed to fetch farm details');
  }
}

async function fetchDuriansByFarmAndVariety() {
  try {
    const statuses = isEditMode.value ? ['Assigned', 'Pending'] : ['Pending'];
    const batchId = isEditMode.value ? form.value.batchId : undefined;

    const dto: DurianQueryDTO = {
      farmId: form.value.farmId,
      varietyId: form.value.varietyId,
      status: statuses,
      ...(batchId ? { batchId } : {})
    };

    const res = await getDuriansByFarmAndVariety(dto);
    duriansFromFarm.value = res.data.durians || [];
  } catch (err: any) {
    console.error('[fetchDuriansByFarmAndVariety Error]', err);
    message.error('Failed to fetch durians');
  }
}

function beforeUpload(file: File) {
  const valid = ['image/jpeg', 'image/png'].includes(file.type);
  if (!valid) message.error('Only JPG/PNG images are allowed!');
  return valid;
}

async function uploadImage(req: any) {
  try {
    const res = await uploadToIPFS(req.file);
    form.value.batchImageCid = res.data.cid;
    message.success('Image uploaded to IPFS');
  } catch (err) {
    console.error('[Upload Image Error]', err);
    message.error('Upload failed');
  }
}

function getImageUrl(cid: string) {
  return `https://ipfs.io/ipfs/${cid}`;
}

onMounted(() => {
  fetchFarms();
  fetchVarieties();
  fetchBatchId();
});
</script>

<style scoped>
.upload-box {
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
.upload-placeholder {
  font-size: 32px;
  color: #bbb;
}
</style>
