<template>
  <el-dialog
      :model-value="visible"
      @update:modelValue="emit('update:visible', $event)"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="varietyRules" label-width="140px">
      <el-form-item label="Variety ID" prop="varietyId">
        <el-input v-model="form.varietyId" placeholder="e.g., D197" readonly disabled />
      </el-form-item>

      <el-form-item label="Name" prop="name">
        <el-input v-model="form.name" placeholder="e.g., Musang King" />
      </el-form-item>

      <el-form-item label="Origin Region" prop="originRegion">
        <el-select v-model="form.originRegion" placeholder="Select a state" filterable clearable>
          <el-option
              v-for="state in states"
              :key="state"
              :label="state"
              :value="state"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
            type="textarea"
            v-model="form.description"
            placeholder="Short description about the variety"
            :rows="3"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import type { FormInstance } from 'element-plus';
import { defaultVarietyForm, varietyRules, type VarietyModel } from '@/api/admin/variety/VarietyModels';
import { generateId } from '@/api/common/IDGen';

const states = [
  'Johor',
  'Kedah',
  'Kelantan',
  'Melaka',
  'Negeri Sembilan',
  'Pahang',
  'Perak',
  'Perlis',
  'Pulau Pinang',
  'Sabah',
  'Sarawak',
  'Selangor',
  // optionally add: 'Terengganu', 'Wilayah Persekutuan'
];

const props = defineProps<{
  visible: boolean;
  mode: 'create' | 'update';
  initialData?: VarietyModel | null;
}>();

const emit = defineEmits(['update:visible', 'submit']);

const formRef = ref<FormInstance>();
const form = ref<VarietyModel>({ ...defaultVarietyForm });

const isUpdateMode = computed(() => props.mode === 'update');
const dialogTitle = computed(() => (isUpdateMode.value ? 'Update Variety' : 'Add New Variety'));

watch(
    () => props.visible,
    async (val) => {
      if (val) {
        if (props.mode === 'create') {
          try {
            const res = await generateId('variety');
            const generatedId = res.data?.generatedId || '';
            form.value = {
              ...defaultVarietyForm,
              varietyId: generatedId,
            };
          } catch (err) {
            console.error('Failed to generate variety ID:', err);
            form.value = { ...defaultVarietyForm };
          }
        } else {
          form.value = props.initialData ? { ...props.initialData } : { ...defaultVarietyForm };
        }
      }
    },
    { immediate: true }
);

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', form.value);
    }
  });
};
</script>
