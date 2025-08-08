<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="emit('update:visible', $event)"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="durianRules"
        label-width="140px"
    >
      <el-form-item label="Durian ID" prop="durianId">
        <el-input v-model="form.durianId" disabled placeholder="Auto-generated..." />
      </el-form-item>

      <el-form-item label="Durian Image" prop="imageUrl">
        <el-upload
            class="upload-box"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="uploadImage"
        >
          <img v-if="form.imageUrl" :src="form.imageUrl" class="upload-preview" />
          <el-icon v-else class="upload-placeholder"><UploadFilled /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="Image Hash" prop="imageHash">
        <el-input v-model="form.imageHash" disabled placeholder="SHA256 hash of image" />
      </el-form-item>

      <el-form-item label="Farm ID" prop="farmId">
        <el-select v-model="form.farmId" placeholder="Select a farm" filterable clearable>
          <el-option
              v-for="farm in farmOptions"
              :key="farm.farmId"
              :label="farm.farmId"
              :value="farm.farmId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Variety" prop="varietyId">
        <el-select v-model="form.varietyId" placeholder="Select a variety" filterable clearable>
          <el-option
              v-for="v in varietyOptions"
              :key="v.varietyId"
              :label="v.name"
              :value="v.varietyId"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { Durian } from '@/api/farmer/durian/DurianModels';
import { durianRules } from '@/api/farmer/durian/DurianModels';
import { useUserStore } from '@/store/user';
import { generateId } from '@/api/common/IDGen/index';
import { getMyFarmIds, getFarmById } from '@/contracts/farmer/farmContract';
import { getAllVarieties } from '@/api/admin/variety';
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { uploadImageToCloudinary } from '@/utils/cloudinaryUploader';

const props = defineProps<{ visible: boolean }>();
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'submit', form: Durian): void;
}>();

const userStore = useUserStore();
const formRef = ref();

const form = ref<Durian>({
  durianId: '',
  imageUrl: '',
  imageHash: '',
  farmId: '',
  varietyId: '',
  walletAddress: '',
  status: 'pending',
});

const farmOptions = ref<{ farmId: string }[]>([]);
const varietyOptions = ref<{ varietyId: string; name: string }[]>([]);

const dialogTitle = 'Add New Durian';

async function handleSubmit() {
  if (!formRef.value) return;
  form.value.walletAddress = userStore.getWalletAddress;

  await formRef.value.validate((valid: boolean) => {
    if (!valid) return;
    emit('submit', form.value);
    emit('update:visible', false);
  });

  resetForm();
}

function resetForm() {
  form.value = {
    durianId: '',
    imageUrl: '',
    imageHash: '',
    farmId: '',
    varietyId: '',
    walletAddress: '',
    status: 'pending',
  };
  fetchDurianId();
}

async function fetchFarms() {
  try {
    const ids = await getMyFarmIds();
    const farms = await Promise.all(ids.map(id => getFarmById(id)));
    farmOptions.value = farms.map(f => ({ farmId: f.farmId }));
  } catch (err) {
    ElMessage.error('Failed to fetch farms');
  }
}

async function fetchVarieties() {
  try {
    const res = await getAllVarieties();
    varietyOptions.value = res.data.list || [];
  } catch (err) {
    ElMessage.error('Failed to fetch varieties');
  }
}

async function fetchDurianId() {
  try {
    const res = await generateId('durian');
    form.value.durianId = res.data.generatedId;
  } catch (err) {
    ElMessage.error('Failed to generate Durian ID');
  }
}

function beforeUpload(file: File) {
  const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
  const isValid = validTypes.includes(file.type);
  if (!isValid) {
    ElMessage.error('Only JPG/PNG images are allowed!');
  }
  return isValid;
}

async function uploadImage(req: any) {
  try {
    const file = req.file as File;
    const { imageUrl, imageHash } = await uploadImageToCloudinary(file);
    form.value.imageUrl = imageUrl;
    form.value.imageHash = imageHash;
    ElMessage.success('Image uploaded to Cloudinary');
  } catch {
    ElMessage.error('Image upload failed');
  }
}

onMounted(() => {
  fetchFarms();
  fetchVarieties();
  fetchDurianId();
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
