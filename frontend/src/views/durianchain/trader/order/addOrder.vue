<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="emit('update:visible', $event)"
      title="Place New Order"
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

      <!-- Trader Agency -->
      <el-form-item label="Select Agency" prop="traderAgencyId">
        <el-select
            v-model="form.traderAgencyId"
            placeholder="Select agency"
            filterable
            clearable
            @change="handleAgencyChange"
            @visible-change="(visible) => visible && loadAgencies()"
        >
          <el-option
              v-for="agency in agencyOptions"
              :key="agency.agencyId"
              :label="agency.name + ' (' + agency.agencyId + ')'"
              :value="agency.agencyId"
          />
        </el-select>
      </el-form-item>

      <!-- Delivery Location -->
      <el-form-item label="Delivery Location" prop="deliveryDestination">
        <el-input v-model="form.deliveryDestination" placeholder="Enter delivery destination" />
      </el-form-item>

      <!-- Export Switch -->
      <el-form-item label="Export Order">
        <el-switch
            v-model="form.isExport"
            :disabled="!isExportSwitchEnabled"
            active-text="Yes"
            inactive-text="No"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" @click="submitOrder">Place Order</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage as message } from 'element-plus';
import { useUserStore } from '@/store/user';
import { orderBatch } from '@/contracts/trader/orderContract';
import {
  getMyTraderAgencyIds,
  getTraderAgencyById
} from '@/contracts/trader/agencyContract';
import { getFarmById } from '@/contracts/farmer/farmContract';
import type { FormInstance } from 'element-plus';
import { OrderDTO } from "@/api/trader/order/OrderModels";
import { placeOrder } from "@/api/trader/order";

const props = defineProps<{
  visible: boolean;
  batchData: {
    batchId: string;
    foodName: string;
    farmId: string;
  };
}>();

const emit = defineEmits(['update:visible', 'submit']);

const formRef = ref<FormInstance>();
const userStore = useUserStore();

const form = reactive({
  batchId: '',
  foodName: '',
  traderAgencyId: '',
  deliveryDestination: '',
  isExport: false,
});

const rules = {
  traderAgencyId: [{ required: true, message: 'Please select an agency', trigger: 'change' }],
  deliveryDestination: [{ required: true, message: 'Please enter delivery location', trigger: 'blur' }],
};

const myAgencyIds = ref<string[]>([]);
const agencyOptions = ref<{ agencyId: string; name: string; exportLicenseExpiry: string }[]>([]);
const agencyLoaded = ref(false);
const isExportSwitchEnabled = ref(false);

const farmCertValid = ref(false);
const selectedAgencyCertValid = ref(false);

async function loadAgencies() {
  if (agencyLoaded.value || myAgencyIds.value.length === 0) return;

  try {
    const results = await Promise.all(
        myAgencyIds.value.map(async (id) => {
          try {
            const d = await getTraderAgencyById(id);
            return {
              agencyId: d.agencyId,
              name: d.agencyName,
              exportLicenseExpiry: d.exportLicenseExpiry,
            };
          } catch (err) {
            console.warn(`Failed to fetch agency ${id}`, err);
            return null;
          }
        })
    );

    agencyOptions.value = results.filter((x): x is NonNullable<typeof x> => x !== null);
    agencyLoaded.value = true;
  } catch (err: any) {
    message.error('Failed to load agency details: ' + (err.message || 'Unknown error'));
  }
}

async function handleAgencyChange(agencyId: string) {
  const selected = agencyOptions.value.find(a => a.agencyId === agencyId);
  if (!selected) return;

  const expiry = Number(selected.exportLicenseExpiry || 0) * 1000;
  const isAgencyCertValid = expiry > Date.now();

  selectedAgencyCertValid.value = isAgencyCertValid;

  const enableExport = farmCertValid.value && selectedAgencyCertValid.value;
  isExportSwitchEnabled.value = enableExport;

  if (!enableExport) {
    form.isExport = false;

    const messages = [];
    if (!farmCertValid.value) {
      messages.push('Farm certificate is missing or expired.');
    }
    if (!isAgencyCertValid) {
      messages.push('Agency does not have a valid export license.');
    }

    if (messages.length > 0) {
      message.warning(`Export Disabled. ${messages.join(' ')}`);
    }
  }
}

async function submitOrder() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
  } catch {
    return message.error('Please correct the errors before submitting.');
  }

  try {
    const txHash = await orderBatch(
        form.batchId,
        form.traderAgencyId,
        form.deliveryDestination,
        form.isExport
    );

    message.success('Order placed on-chain successfully!');

    const orderPayload: OrderDTO = {
      batchId: form.batchId,
      traderAgencyId: form.traderAgencyId,
      traderAddress: userStore.walletAddress,
      deliveryDestination: form.deliveryDestination,
      txHash: txHash
    };

    await placeOrder(orderPayload);

    emit('submit', { ...form, txHash });
    emit('update:visible', false);
  } catch (err: any) {
    console.error('[Order Error]', err);
    message.error('Failed to place order: ' + (err.message || 'Blockchain error'));
  }
}

watch(
    () => props.visible,
    async (val) => {
      if (val && props.batchData) {
        Object.assign(form, {
          batchId: props.batchData.batchId,
          foodName: props.batchData.foodName,
          traderAgencyId: '',
          deliveryDestination: '',
          isExport: false,
        });

        isExportSwitchEnabled.value = false;
        farmCertValid.value = false;
        selectedAgencyCertValid.value = false;

        try {
          myAgencyIds.value = await getMyTraderAgencyIds();
          agencyOptions.value = [];
          agencyLoaded.value = false;
        } catch (err: any) {
          message.error('Failed to fetch agency IDs: ' + (err.message || 'Unknown'));
        }

        try {
          const farm = await getFarmById(props.batchData.farmId);
          const expiry = Number(farm.certificateExpiry || 0) * 1000;

          const isValid = expiry > Date.now();
          farmCertValid.value = isValid;
        } catch (err) {
          console.error('[Farm Cert Error]', err);
          message.error('Failed to validate farm certificate.');
          farmCertValid.value = false;
        }
      }
    },
    { immediate: true }
);
</script>
