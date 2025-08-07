<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="updateVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="userRules" label-width="120px">
      <el-form-item label="Wallet Address" prop="walletAddress">
        <el-input v-model="form.walletAddress" placeholder="0x..." :disabled="isUpdateMode" />
      </el-form-item>

      <el-form-item label="Username" prop="username">
        <el-input v-model="form.username" placeholder="e.g., johndoe" />
      </el-form-item>

      <el-form-item label="Full Name" prop="name">
        <el-input v-model="form.name" placeholder="e.g., John Tan Zhi Wei" />
      </el-form-item>

      <el-form-item label="Email" prop="email">
        <el-input v-model="form.email" placeholder="e.g., john@example.com" />
      </el-form-item>

      <el-form-item label="Phone" prop="phone">
        <el-input v-model="form.phone" placeholder="e.g., 0123456789" />
      </el-form-item>

      <el-form-item label="Role" prop="role">
        <el-select v-model="form.role" placeholder="Select role">
          <el-option label="None" value="unknown" />
          <el-option label="Admin" value="admin" />
          <el-option label="Farmer" value="farmer" />
          <el-option label="Logistics" value="logistics" />
          <el-option label="Trader" value="trader" />
        </el-select>
      </el-form-item>


      <el-form-item label="Approved" prop="isApproved">
        <el-switch v-model="form.isApproved" active-text="Yes" inactive-text="No" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="updateVisible(false)">Cancel</el-button>
      <el-button type="primary" @click="submitForm">{{ actionButton }}</el-button>
    </template>
  </el-dialog>
</template>

<<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import type { FormInstance } from 'element-plus';
import type { UserModel } from '@/api/admin/user/UserModels';
import { userRules } from '@/api/admin/user/UserModels';
import {assignUserRole, getUserRole, Role} from "@/contracts/userContract";
import message from "@/utils/message";

// Props and Emits
const props = defineProps<{
  visible: boolean;
  mode: 'create' | 'update';
  initialData: UserModel | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'update:visible', value: boolean): void;
  (e: 'submit', form: UserModel): void;
}>();

const updateVisible = (val: boolean) => emit('update:visible', val);

// Form state
const formRef = ref<FormInstance>();
const form = ref<UserModel>({
  username: '',
  name: '',
  email: '',
  phone: '',
  role: 'unknown',
  walletAddress: '',
  isApproved: false
});

const isUpdateMode = computed(() => props.mode === 'update');

// Sync initialData when modal opens
watch(
    () => props.initialData,
    (data) => {
      form.value = data
          ? { ...data }
          : {
            username: '',
            name: '',
            email: '',
            phone: '',
            role: 'unknown',
            walletAddress: '',
            isApproved: false
          };
    },
    { immediate: true }
);

const roleMap = {
  unknown: Role.Unknown,
  admin: Role.Admin,
  farmer: Role.Farmer,
  logistics: Role.Logistics,
  trader: Role.Trader
};

const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    const { walletAddress, role } = form.value;

    try {
      const currentRole = await getUserRole(walletAddress);
      const newRole = roleMap[role]; // roleMap should map 'admin' -> Role.Admin, etc.

      if (currentRole !== newRole) {
        const txHash = await assignUserRole(walletAddress, newRole);
        message.success(`Blockchain role updated. Tx: ${txHash.slice(0, 10)}...`);
      } else {
        message.warning('User already has the same role on-chain. Skipping update.');
      }
    } catch (err: any) {
      message.error('Failed to assign role on blockchain: ' + err.message);
      return;
    }
    emit('submit', form.value);
  });
};

const dialogTitle = computed(() => (props.mode === 'create' ? 'Add New User' : 'Edit User'));
const actionButton = computed(() => (props.mode === 'create' ? 'Save' : 'Edit'));
</script>
