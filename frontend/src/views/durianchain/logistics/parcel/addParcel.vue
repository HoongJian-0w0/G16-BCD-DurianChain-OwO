<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="emit('update:visible', $event)"
      title="Collect Parcel"
      width="600px"
      destroy-on-close
  >
    <el-form :model="form" ref="formRef" label-width="140px" :rules="rules">
      <!-- Batch ID -->
      <el-form-item label="Batch ID">
        <el-input v-model="form.batchId" disabled />
      </el-form-item>

      <!-- Durian Variety -->
      <el-form-item label="Durian Variety">
        <el-input v-model="form.foodName" disabled />
      </el-form-item>

      <!-- Logistics Company -->
      <el-form-item label="Logistics Company" prop="logisticsCompanyId">
        <el-select
            v-model="form.logisticsCompanyId"
            placeholder="Select company"
            filterable
            clearable
            @visible-change="(visible) => visible && loadCompanies()"
        >
          <el-option
              v-for="company in companyOptions"
              :key="company.companyId"
              :label="company.name + ' (' + company.companyId + ')'"
              :value="company.companyId"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" @click="submitCollect">Collect</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue';
import type { FormInstance } from 'element-plus';
import { ElMessage as message } from 'element-plus';
import { useUserStore } from '@/store/user';
import { getMyLogisticsCompanyIds, getLogisticsCompanyById } from '@/contracts/logistics/logisticsCompanyContract';
import { collectBatch } from '@/contracts/logistics/parcelContract';
import { collectParcel } from '@/api/logistics/parcel/index';
import type { ParcelDTO } from '@/api/logistics/parcel/ParcelModels';

const props = defineProps<{
  visible: boolean;
  batchData: {
    batchId: string;
    foodName: string;
  };
}>();

const emit = defineEmits(['update:visible', 'submit']);

const userStore = useUserStore();
const formRef = ref<FormInstance>();

const form = reactive({
  batchId: '',
  foodName: '',
  logisticsCompanyId: '',
});

const rules = {
  logisticsCompanyId: [{ required: true, message: 'Please select a logistics company', trigger: 'change' }],
};

const companyIds = ref<string[]>([]);
const companyOptions = ref<{ companyId: string; name: string }[]>([]);
const companyLoaded = ref(false);

async function loadCompanies() {
  if (companyLoaded.value || companyIds.value.length === 0) return;

  try {
    const results = await Promise.all(
        companyIds.value.map(async (id) => {
          try {
            const d = await getLogisticsCompanyById(id);
            return {
              companyId: d.companyId,
              name: d.companyName,
            };
          } catch (err) {
            console.warn(`Failed to fetch logistics company ${id}`, err);
            return null;
          }
        })
    );

    companyOptions.value = results.filter((x): x is NonNullable<typeof x> => x !== null);
    companyLoaded.value = true;
  } catch (err: any) {
    message.error('Failed to load logistics company details: ' + (err.message || 'Unknown error'));
  }
}

async function submitCollect() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch {
    message.error('Please correct the errors before submitting.');
    return;
  }

  try {
    // 1. Call smart contract to collect batch
    const txHash = await collectBatch(form.batchId, form.logisticsCompanyId);
    message.success('Parcel collected on-chain successfully!');

    // 2. Send to backend for persistence
    const payload: ParcelDTO = {
      batchId: form.batchId,
      logisticsCompanyId: form.logisticsCompanyId,
      logisticsAddress: userStore.walletAddress,
      txHash,
    };

    await collectParcel(payload);
    message.success('Parcel recorded in DB successfully.');

    // 3. Emit success and close modal
    emit('submit', { ...form, txHash });
    emit('update:visible', false);
  } catch (err: any) {
    console.error('[Collect Error]', err);
    message.error('Failed to collect parcel: ' + (err.message || 'Unexpected error'));
  }
}

watch(
    () => props.visible,
    async (val) => {
      if (val && props.batchData) {
        Object.assign(form, {
          batchId: props.batchData.batchId,
          foodName: props.batchData.foodName,
          logisticsCompanyId: '',
        });

        try {
          companyIds.value = await getMyLogisticsCompanyIds();
          companyOptions.value = [];
          companyLoaded.value = false;
        } catch (err: any) {
          message.error('Failed to fetch logistics company IDs: ' + (err.message || 'Unknown'));
        }
      }
    },
    { immediate: true }
);
</script>
