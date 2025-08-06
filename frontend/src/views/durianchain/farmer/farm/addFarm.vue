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
            <el-input v-model="form.farmId" readonly placeholder="e.g., FARM001" />
          </el-form-item>

          <!-- Owner Address -->
          <el-form-item label="Owner Address" prop="ownerAddress">
            <el-input v-model="form.ownerAddress" :disabled="isUpdateMode" placeholder="Wallet address" />
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
                :on-remove="handleFileRemove"
                :file-list="fileList"
            >
              <el-icon><upload-filled /></el-icon>
              <div class="el-upload__text">Drop or click to upload</div>
              <template #tip>
                <div class="el-upload__tip">Only PNG file allowed</div>
              </template>
            </el-upload>
          </el-form-item>

          <!-- Certificate Expiry -->
          <el-form-item label="MyGAP Expiry" prop="certificateExpiry">
            <el-date-picker
                v-model="form.certificateExpiry"
                type="datetime"
                placeholder="Select expiry date"
                format="YYYY-MM-DD HH:mm"
                value-format="x"
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
import type { FarmModel } from '@/api/farmer/FarmModels';
import { farmRules } from '@/api/farmer/FarmModels';
import { registerFarm, updateFarmCertificate } from "@/contracts/farmContract";
import { generateId } from '@/api/common/IDGen';

const props = defineProps<{
  visible: boolean;
  mode?: 'create' | 'update';
  initialData?: Partial<FarmModel>;
}>();

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

// Handle PNG file upload and preview
function handleFileChange(file: any, fileListArr: any[]) {
  if (file.raw && file.raw.type !== 'image/png') {
    message.error('Only PNG files are allowed.');
    return;
  }

  certificateFile.value = file.raw;
  fileList.value = [file];
  previewUrl.value = URL.createObjectURL(file.raw);
}

function handleFileRemove(file: any, fileListArr: any[]) {
  certificateFile.value = null;
  fileList.value = [];
  previewUrl.value = '';
}

async function handleSubmit() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch {
    return message.error('Please fix the form validation errors.');
  }

  // Step 1: Validate expiry format
  const rawExpiry = Number(form.certificateExpiry);
  if (!rawExpiry || isNaN(rawExpiry)) {
    return message.error('Please select a valid expiry date');
  }

  const expiry = toSolidityTimestamp(rawExpiry);

  // Step 2: Optional check – expiry must be in the future
  const now = Math.floor(Date.now() / 1000);
  if (expiry <= now) {
    return message.error('Expiry must be in the future');
  }

  // Step 3: Upload to IPFS (skipped)
  let certificateCID = 'CID1'; // Placeholder for IPFS CID

  try {
    let txHash = '';

    if (isUpdateMode.value) {
      // Update Mode
      txHash = await updateFarmCertificate(form.farmId, certificateCID, expiry);
      message.success('Certificate updated on-chain');
    } else {
      // Create Mode
      txHash = await registerFarm(form.farmId, form.location, certificateCID, expiry);
      message.success('Farm registered on-chain');
    }

    // Emit final result to parent for DB saving
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

watch(
    () => props.visible,
    async (val) => {
      if (val) {
        nextTick(async () => {
          if (props.initialData) {
            // Update mode – fill from existing data
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
          } else {
            try {
              const res = await generateId('farm');
              form.farmId = res.data.generatedId;
            } catch (err: any) {
              message.error('Failed to generate farm ID: ' + (err.message || 'Unknown error'));
              form.farmId = '';
            }

            form.ownerAddress = '';
            form.location = '';
            form.latitude = '';
            form.longitude = '';
            form.certificateCid = '';
            form.certificateExpiry = '';
            form.txHash = '';
          }

          // Always reset upload state
          previewUrl.value = '';
          fileList.value = [];
          certificateFile.value = null;
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
</style>
