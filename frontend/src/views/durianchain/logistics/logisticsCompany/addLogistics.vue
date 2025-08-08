<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="updateVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
  >
    <el-form :model="form" :rules="logisticsCompanyRules" ref="formRef" label-width="140px">
      <!-- Company ID -->
      <el-form-item label="Company ID" prop="companyId">
        <el-input v-model="form.companyId" disabled placeholder="e.g., LOGI001" />
      </el-form-item>

      <!-- Company Name -->
      <el-form-item label="Company Name" prop="companyName">
        <el-input v-model="form.companyName" placeholder="Enter logistics company name" />
      </el-form-item>

      <!-- Wallet Address -->
      <el-form-item label="Owner Address" prop="ownerAddress">
        <el-input v-model="form.ownerAddress" disabled placeholder="Wallet address" />
      </el-form-item>

      <!-- Tx Hash (read-only for update mode) -->
      <el-form-item label="Tx Hash" v-if="isUpdateMode">
        <el-input v-model="form.txHash" readonly>
          <template #append>
            <el-button type="primary" icon="CopyDocument" @click="copyTxHash" />
          </template>
        </el-input>
      </el-form-item>
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
import { ref, reactive, watch, computed, nextTick } from 'vue';
import type { FormInstance } from 'element-plus';
import { ElMessage as message } from 'element-plus';
import { useUserStore } from '@/store/user';
import { generateId } from '@/api/common/IDGen';
import { registerLogisticsCompany } from '@/contracts/logistics/logisticsCompanyContract';
import { type LogisticsCompany, logisticsCompanyRules } from '@/api/logistics/logisticsCompany/logisticsCompanyModels';

const props = defineProps<{
  visible: boolean;
  mode?: 'create' | 'update';
  initialData?: Partial<LogisticsCompany>;
}>();

const emit = defineEmits(['update:visible', 'submit']);

const userStore = useUserStore();
const formRef = ref<FormInstance>();
const isUpdateMode = computed(() => props.mode === 'update');
const dialogTitle = computed(() => isUpdateMode.value ? 'Update Logistics Company' : 'Register Logistics Company');

const form = reactive<LogisticsCompany>({
  companyId: '',
  companyName: '',
  ownerAddress: '',
  txHash: '',
});

function updateVisible(val: boolean) {
  emit('update:visible', val);
}

function copyTxHash() {
  if (!form.txHash) return;
  navigator.clipboard.writeText(form.txHash).then(() => {
    message.success('Tx Hash copied!');
  }).catch(() => {
    message.error('Failed to copy Tx Hash.');
  });
}

async function handleSubmit() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch {
    return message.error('Please complete the form correctly.');
  }

  try {
    if (!form.companyName.trim()) {
      return message.error('Company name cannot be empty.');
    }

    const txHash = await registerLogisticsCompany(form.companyId, form.companyName.trim());
    message.success('Company registered on-chain');

    emit('submit', {
      ...form,
      txHash,
    });

    updateVisible(false);
  } catch (err: any) {
    console.error('[Blockchain Error]', err);
    message.error('Registration failed: ' + (err.message || err));
  }
}

watch(() => props.visible, async (val) => {
  if (val) {
    nextTick(async () => {
      if (props.initialData && isUpdateMode.value) {
        Object.assign(form, {
          companyId: props.initialData.companyId || '',
          companyName: props.initialData.companyName || '',
          ownerAddress: props.initialData.ownerAddress || '',
          txHash: props.initialData.txHash || '',
        });
      } else {
        try {
          const res = await generateId('logistics_company');
          form.companyId = res.data.generatedId;
        } catch (err: any) {
          message.error('Failed to generate company ID: ' + (err.message || 'Unknown error'));
          form.companyId = '';
        }

        form.companyName = '';
        form.ownerAddress = userStore.getWalletAddress || '';
        form.txHash = '';
      }
    });
  }
}, { immediate: true });
</script>

<style scoped>
</style>
