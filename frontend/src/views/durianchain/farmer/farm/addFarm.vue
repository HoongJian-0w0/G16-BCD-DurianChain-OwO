<template>
  <el-dialog :model-value="visible" @update:modelValue="updateVisible" :title="dialogTitle" width="500px">
    <el-form :model="form" label-width="120px">
      <el-form-item label="Farm ID">
        <el-input v-model="form.farmId" :disabled="isUpdateMode" placeholder="e.g., FARM001" />
      </el-form-item>

      <el-form-item label="Location">
        <el-input v-model="form.location" :disabled="isUpdateMode" placeholder="Farm Location" />
      </el-form-item>

      <el-form-item label="Certificate CID">
        <el-input v-model="form.certificateCID" placeholder="IPFS CID" />
      </el-form-item>

      <el-form-item label="Certificate Expiry">
        <el-date-picker
            v-model="form.certificateExpiry"
            type="datetime"
            placeholder="Select expiry date"
            format="YYYY-MM-DD HH:mm"
            value-format="x"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="updateVisible(false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, watch, computed } from 'vue';
import message from '@/utils/message';
import {toSolidityTimestamp} from "@/utils/time";

const props = defineProps<{
  visible: boolean;
  mode?: 'create' | 'update';
  initialData?: {
    farmId: string;
    location: string;
    certificateCID: string;
    certificateExpiry: number | string;
  };
}>();

const emit = defineEmits(['update:visible', 'submit']);

const isUpdateMode = computed(() => props.mode === 'update');

const form = reactive({
  farmId: '',
  location: '',
  certificateCID: '',
  certificateExpiry: '', // value-format="x" gives seconds string
});

const dialogTitle = computed(() =>
    isUpdateMode.value ? 'Update Farm Certificate' : 'Register New Farm'
);

watch(
    () => props.visible,
    (val) => {
      if (val && props.initialData && isUpdateMode.value) {
        Object.assign(form, {
          farmId: props.initialData.farmId,
          location: props.initialData.location,
          certificateCID: props.initialData.certificateCID,
          certificateExpiry: props.initialData.certificateExpiry,
        });
      } else if (val) {
        Object.assign(form, {
          farmId: '',
          location: '',
          certificateCID: '',
          certificateExpiry: '',
        });
      }
    },
    { immediate: true }
);

function updateVisible(val: boolean) {
  emit('update:visible', val);
}

function handleSubmit() {
  const hasCID = form.certificateCID.trim() !== '';
  const hasExpiry = form.certificateExpiry !== '' && Number(form.certificateExpiry) > 0;

  if ((hasCID && !hasExpiry) || (!hasCID && hasExpiry)) {
    return message.error('Both Certificate CID and Expiry must be filled together.');
  }

  let expiry = 0;

  // If both are filled, validate expiry
  if (hasCID && hasExpiry) {
    try {
      expiry = toSolidityTimestamp(
          typeof form.certificateExpiry === 'string'
              ? parseInt(form.certificateExpiry)
              : form.certificateExpiry
      );
    } catch (e: any) {
      return message.error(e.message || 'Invalid expiry date');
    }

    const now = Math.floor(Date.now() / 1000);
    if (expiry <= now) {
      return message.error('Expiry must be in the future');
    }
  }

  emit('submit', {
    ...form,
    certificateCID: hasCID ? form.certificateCID : '',
    certificateExpiry: expiry, // 0 if not provided
  });

  updateVisible(false);
}
</script>
