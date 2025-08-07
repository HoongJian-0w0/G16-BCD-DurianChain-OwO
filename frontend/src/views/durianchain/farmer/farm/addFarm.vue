<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="updateVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
  >
    <el-form :model="form" :rules="farmRules" ref="formRef" label-width="130px">
      <div class="form-with-preview">
        <!-- LEFT: FORM FIELDS -->
        <div class="form-fields">
          <!-- Farm ID -->
          <el-form-item label="Farm ID" prop="farmId">
            <el-input v-model="form.farmId" disabled placeholder="e.g., FARM001" />
          </el-form-item>

          <!-- Owner Address -->
          <el-form-item label="Owner Address" prop="ownerAddress">
            <el-input v-model="form.ownerAddress" disabled placeholder="Wallet address" />
          </el-form-item>

          <!-- Address -->
          <el-form-item label="Address" prop="location">
            <el-input v-model="form.location" :disabled="isUpdateMode"  placeholder="Farm address" />
          </el-form-item>

          <!-- Latitude -->
          <el-form-item label="Latitude" prop="latitude">
            <el-input v-model="form.latitude" :disabled="isUpdateMode" placeholder="e.g., 3.139" />
          </el-form-item>

          <!-- Longitude -->
          <el-form-item label="Longitude" prop="longitude">
            <el-input v-model="form.longitude" :disabled="isUpdateMode" placeholder="e.g., 101.6869" />
          </el-form-item>

          <!-- Certificate Upload -->
          <el-form-item label="MyGAP Cert (.png)">
            <el-upload
                class="upload-demo"
                drag
                accept=".png"
                :auto-upload="false"
                :limit="1"
                :on-change="handleFileChange"
                :file-list="fileList"
                :show-file-list="false"
            >
              <el-icon><upload-filled /></el-icon>
              <div class="el-upload__text">Drop or click to upload</div>
              <template #tip>
                <div class="el-upload__tip">Only PNG file allowed</div>
              </template>
            </el-upload>
          </el-form-item>

          <!-- Certificate CID Field -->
          <el-form-item label="CID" prop="certificateCid">
            <el-input
                v-model="form.certificateCid"
                placeholder="Auto-filled after upload"
                readonly
            >
              <template #append>
                <el-button
                    type="danger"
                    icon="Delete"
                    @click="handleCIDRemove"
                    :disabled="!form.certificateCid"
                />
              </template>
            </el-input>
          </el-form-item>

          <!-- Certificate Expiry -->
          <el-form-item label="MyGAP Expiry" prop="certificateExpiry">
            <el-date-picker
                v-model="form.certificateExpiry"
                type="datetime"
                placeholder="Select expiry date"
                format="YYYY-MM-DD HH:mm"
                value-format="x"
                style="width: 100%"
            />
          </el-form-item>

          <!-- Tx Hash -->
          <el-form-item label="Tx Hash" v-if="isUpdateMode">
            <el-input v-model="form.txHash" readonly>
              <template #append>
                <el-button type="primary" icon="CopyDocument" @click="copyTxHash" />
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- RIGHT: IMAGE PREVIEW -->
        <div class="preview-box">
          <h4 style="margin-bottom: 8px;">📄 Certificate Preview</h4>
          <div class="preview-container">
            <template v-if="previewUrl">
              <img :src="previewUrl" alt="Certificate Preview" class="preview-image" />
            </template>
            <template v-else>
              <div class="no-preview">No Preview</div>
            </template>
          </div>
        </div>
      </div>
    </el-form>

    <template #footer>
      <el-button @click="updateVisible(false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">
        {{ isUpdateMode ? 'Update' : 'Submit' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch, computed, ref, nextTick } from 'vue';
import { ElMessage as message } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';

import { toSolidityTimestamp } from '@/utils/time';
import type { FarmModel } from '@/api/farmer/farm/FarmModels';
import { farmRules } from '@/api/farmer/farm/FarmModels';
import {getFarmById, registerFarm, updateFarmCertificate} from "@/contracts/farmContract";
import { generateId } from '@/api/common/IDGen';
import {useUserStore} from "@/store/user";
import { uploadToIPFS } from '@/api/common/CIDGen';

const props = defineProps<{
  visible: boolean;
  mode?: 'create' | 'update';
  initialData?: Partial<FarmModel>;
}>();

const userStore = useUserStore()

const emit = defineEmits(['update:visible', 'submit']);

const isUpdateMode = computed(() => props.mode === 'update');
const dialogTitle = computed(() =>
    isUpdateMode.value ? 'Update Farm' : 'Register New Farm'
);

const formRef = ref<FormInstance>();

const form = reactive<FarmModel>({
  id: 0,
  farmId: '',
  ownerAddress: '',
  location: '',
  latitude: '',
  longitude: '',
  certificateCid: '',
  certificateExpiry: '',
  txHash: '',
});

const fileList = ref<any[]>([]);
const certificateFile = ref<File | null>(null);
const previewUrl = ref<string>('');

function copyTxHash() {
  if (!form.txHash) return;
  navigator.clipboard.writeText(form.txHash).then(() => {
    message.success('Tx Hash copied to clipboard!');
  }).catch(() => {
    message.error('Failed to copy Tx Hash');
  });
}

async function handleSubmit() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch {
    return message.error('Please fix the form validation errors.');
  }

  try {
    let certificateCID = '';
    let expiry = 0;
    let txHash = '';

    if (isUpdateMode.value) {
      const rawExpiry = Number(form.certificateExpiry);
      if (!rawExpiry || isNaN(rawExpiry)) {
        return message.error('Please select a valid expiry date');
      }

      expiry = toSolidityTimestamp(rawExpiry);
      const now = Math.floor(Date.now() / 1000);
      if (expiry <= now) {
        return message.error('Expiry must be in the future');
      }

      certificateCID = form.certificateCid?.trim();
      if (!certificateCID) {
        return message.error('Missing CID. Please upload a certificate.');
      }

      const currentOnChainData = await getFarmById(form.farmId);
      const currentCID = currentOnChainData.certificateCID || '';
      const currentExpiry = Number(currentOnChainData.certificateExpiry || 0);

      if (currentCID === certificateCID && currentExpiry === expiry) {
        return message.warning('No changes detected. Please update the CID or expiry.');
      }

      txHash = await updateFarmCertificate(form.farmId, certificateCID, expiry);
      message.success('Certificate updated on-chain');
    } else {
      const rawExpiry = Number(form.certificateExpiry);

      if (rawExpiry && !form.certificateCid?.trim()) {
        return message.error('Certificate expiry is provided, but CID is missing. Please upload a certificate.');
      }

      if (form.certificateCid) {
        certificateCID = form.certificateCid.trim();

        if (!rawExpiry || isNaN(rawExpiry)) {
          return message.error('Please select a valid expiry date');
        }

        expiry = toSolidityTimestamp(rawExpiry);
        const now = Math.floor(Date.now() / 1000);
        if (expiry <= now) {
          return message.error('Expiry must be in the future');
        }
      }

      txHash = await registerFarm(form.farmId, form.location, certificateCID, expiry);
      message.success('Farm registered on-chain');
    }

    emit('submit', {
      ...form,
      certificateExpiry: expiry,
      certificateCid: certificateCID,
      certificateFile: certificateFile.value || undefined,
      txHash,
    });

    updateVisible(false);
  } catch (err: any) {
    console.error('[Blockchain Error]', err);
    message.error('Blockchain submission failed: ' + (err.message || err));
  }
}

function updateVisible(val: boolean) {
  emit('update:visible', val);
}

async function handleFileChange(file: any, fileListArr: any[]) {
  if (file.raw && file.raw.type !== 'image/png') {
    message.error('Only PNG files are allowed.');
    return;
  }

  certificateFile.value = file.raw;
  fileList.value = [file];
  previewUrl.value = URL.createObjectURL(file.raw);

  try {
    const res = await uploadToIPFS(file.raw);
    form.certificateCid = res.data.cid;
    message.success('CID uploaded to IPFS');
  } catch (err: any) {
    message.error('Failed to upload to IPFS: ' + (err.message || err));
    form.certificateCid = '';
  }
}

function handleCIDRemove() {
  certificateFile.value = null;
  fileList.value = [];
  previewUrl.value = '';
  form.certificateCid = '';
}

watch(
    () => props.visible,
    async (val) => {
      if (val) {
        nextTick(async () => {
          if (props.initialData) {
            // Update mode
            Object.assign(form, {
              id: props.initialData.id || 0,
              farmId: props.initialData.farmId || '',
              ownerAddress: props.initialData.ownerAddress || '',
              location: props.initialData.location || '',
              latitude: props.initialData.latitude || 0,
              longitude: props.initialData.longitude || 0,
              certificateCid: props.initialData.certificateCid || '',
              certificateExpiry: props.initialData.certificateExpiry || '',
              txHash: props.initialData.txHash || '',
            });

            if (form.certificateCid && !previewUrl.value) {
              previewUrl.value = `https://ipfs.io/ipfs/${form.certificateCid}`;
            } else {
              handleCIDRemove();
            }
          } else {
            // Create mode
            try {
              const res = await generateId('farm');
              form.farmId = res.data.generatedId;
            } catch (err: any) {
              message.error('Failed to generate farm ID: ' + (err.message || 'Unknown error'));
              form.farmId = '';
            }

            handleCIDRemove();

            form.ownerAddress = userStore.getWalletAddress || '';
            form.location = '';
            form.latitude = '';
            form.longitude = '';
            form.certificateCid = '';
            form.certificateExpiry = '';
            form.txHash = '';
          }
        });
      }
    },
    { immediate: true }
);
</script>

<style scoped>
.upload-demo {
  width: 100%;
}

.form-with-preview {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.form-fields {
  flex: 1;
}

.preview-box {
  flex: 0 0 230px;
  padding: 10px;
  background-color: #f9f9f9;
  border-left: 1px solid #dcdfe6;
  border-radius: 6px;
  text-align: center;
}

.preview-container {
  width: 100%;
  height: 240px;
  border: 1px dashed #c0c4cc;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-preview {
  color: #999;
  font-size: 14px;
  font-style: italic;
}

.upload-demo {
  width: 100%;
  font-size: 13px;
  --el-upload-dragger-padding: 10px 0;
}

.upload-demo .el-upload__text {
  font-size: 13px;
}

.upload-demo .el-icon {
  font-size: 24px;
}
</style>
