<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="updateVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
  >
    <el-form :model="form" :rules="agencyRules" ref="formRef" label-width="130px">
      <div class="form-with-preview">
        <!-- LEFT FORM FIELDS -->
        <div class="form-fields">
          <el-form-item label="Agency ID" prop="agencyId">
            <el-input v-model="form.agencyId" disabled placeholder="e.g., AGENCY001" />
          </el-form-item>

          <el-form-item label="Owner Address" prop="ownerAddress">
            <el-input v-model="form.ownerAddress" disabled placeholder="Wallet address" />
          </el-form-item>

          <el-form-item label="Agency Name" prop="agencyName">
            <el-input v-model="form.agencyName" :disabled="isUpdateMode" placeholder="e.g., Durian Export Sdn Bhd" />
          </el-form-item>

          <!-- Certificate Upload -->
          <el-form-item label="Export License (.png)">
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

          <el-form-item label="CID" prop="exportLicenseCid">
            <el-input v-model="form.exportLicenseCid" placeholder="Auto-filled after upload" readonly>
              <template #append>
                <el-button
                    type="danger"
                    icon="Delete"
                    @click="handleCIDRemove"
                    :disabled="!form.exportLicenseCid"
                />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="License Expiry" prop="exportLicenseExpiry">
            <el-date-picker
                v-model="form.exportLicenseExpiry"
                type="datetime"
                placeholder="Select expiry date"
                format="YYYY-MM-DD HH:mm"
                value-format="x"
                style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="Tx Hash" v-if="isUpdateMode">
            <el-input v-model="form.txHash" readonly>
              <template #append>
                <el-button type="primary" icon="CopyDocument" @click="copyTxHash" />
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- RIGHT IMAGE PREVIEW -->
        <div class="preview-box">
          <h4>📄 License Preview</h4>
          <div class="preview-container">
            <template v-if="previewUrl">
              <img :src="previewUrl" class="preview-image" />
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

<script setup lang="ts">
import { reactive, computed, ref, watch, nextTick } from 'vue';
import { UploadFilled } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { ElMessage as message } from 'element-plus';
import { useUserStore } from '@/store/user';
import { generateId } from '@/api/common/IDGen';
import { uploadToIPFS } from '@/api/common/CIDGen';
import { type AgencyModel, agencyRules } from '@/api/trader/agency/AgencyModels';
import {
  createTraderAgency,
  updateTraderAgencyCertificate,
  getTraderAgencyById
} from '@/contracts/trader/agencyContract';
import { toSolidityTimestamp } from '@/utils/time';

const props = defineProps<{
  visible: boolean;
  mode?: 'create' | 'update';
  initialData?: Partial<AgencyModel>;
}>();

const emit = defineEmits(['update:visible', 'submit']);
const userStore = useUserStore();
const isUpdateMode = computed(() => props.mode === 'update');
const dialogTitle = computed(() => isUpdateMode.value ? 'Update Agency' : 'Register New Agency');

const formRef = ref<FormInstance>();
const fileList = ref<any[]>([]);
const certificateFile = ref<File | null>(null);
const previewUrl = ref<string>('');

const form = reactive<AgencyModel>({
  id: 0,
  agencyId: '',
  agencyName: '',
  ownerAddress: '',
  exportLicenseCid: '',
  exportLicenseExpiry: '',
  txHash: '',
});

function updateVisible(val: boolean) {
  emit('update:visible', val);
}

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
    let licenseCID = '';
    let expiry = 0;
    let txHash = '';

    const rawExpiry = Number(form.exportLicenseExpiry);
    if (rawExpiry && !form.exportLicenseCid?.trim()) {
      return message.error('Expiry is provided but CID is missing. Please upload the license.');
    }

    if (form.exportLicenseCid) {
      licenseCID = form.exportLicenseCid.trim();

      if (!rawExpiry || isNaN(rawExpiry)) {
        return message.error('Please select a valid expiry date');
      }

      expiry = toSolidityTimestamp(rawExpiry);
      const now = Math.floor(Date.now() / 1000);
      if (expiry <= now) {
        return message.error('Expiry must be in the future');
      }
    }

    if (isUpdateMode.value) {
      const currentOnChain = await getTraderAgencyById(form.agencyId);
      const currentCID = currentOnChain.exportLicenseCID || '';
      const currentExpiry = Number(currentOnChain.exportLicenseExpiry || 0);
      const newCID = licenseCID;

      if (!newCID) {
        return message.error('Missing CID. Please upload a new license.');
      }

      if (currentCID === newCID && currentExpiry === expiry) {
        return message.warning('No changes detected. Please update the CID or expiry.');
      }

      if (currentCID === newCID && currentExpiry !== expiry) {
        return message.warning('CID is same, only expiry changed.');
      }

      txHash = await updateTraderAgencyCertificate(form.agencyId, newCID, expiry);
      message.success('Export license updated on-chain');
    } else {
      txHash = await createTraderAgency(
          form.agencyId,
          form.agencyName,
          licenseCID,
          expiry
      );
      message.success('Agency registered on-chain');
    }

    emit('submit', {
      ...form,
      exportLicenseExpiry: expiry,
      exportLicenseCid: licenseCID,
      certificateFile: certificateFile.value || undefined,
      txHash,
    });

    updateVisible(false);
  } catch (err: any) {
    console.error('[Blockchain Error]', err);
    message.error('Blockchain submission failed: ' + (err.message || err));
  }
}

async function handleFileChange(file: any) {
  if (file.raw?.type !== 'image/png') {
    return message.error('Only PNG files are allowed');
  }

  certificateFile.value = file.raw;
  fileList.value = [file];
  previewUrl.value = URL.createObjectURL(file.raw);

  try {
    const res = await uploadToIPFS(file.raw);
    form.exportLicenseCid = res.data.cid;
    message.success('CID uploaded to IPFS');
  } catch (err: any) {
    message.error('Upload failed: ' + (err.message || 'Unknown error'));
    form.exportLicenseCid = '';
  }
}

function handleCIDRemove() {
  fileList.value = [];
  certificateFile.value = null;
  previewUrl.value = '';
  form.exportLicenseCid = '';
}

watch(() => props.visible, async (val) => {
  if (val) {
    await nextTick();

    if (props.initialData) {
      Object.assign(form, {
        id: props.initialData.id ?? 0,
        agencyId: props.initialData.agencyId ?? '',
        agencyName: props.initialData.agencyName ?? '',
        ownerAddress: props.initialData.ownerAddress ?? '',
        exportLicenseCid: props.initialData.exportLicenseCid ?? '',
        exportLicenseExpiry: props.initialData.exportLicenseExpiry ?? '',
        txHash: props.initialData.txHash ?? '',
      });

      if (form.exportLicenseCid) {
        previewUrl.value = `https://ipfs.io/ipfs/${form.exportLicenseCid}`;
      } else {
        handleCIDRemove();
      }
    } else {
      try {
        const res = await generateId('agency');
        form.agencyId = res.data.generatedId;
      } catch (err: any) {
        form.agencyId = '';
        message.error('Failed to generate Agency ID');
      }

      handleCIDRemove();

      Object.assign(form, {
        agencyName: '',
        exportLicenseCid: '',
        exportLicenseExpiry: '',
        txHash: '',
        ownerAddress: userStore.getWalletAddress,
      });
    }

    fileList.value = [];
    certificateFile.value = null;
  }
}, { immediate: true });
</script>

<style scoped>
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
