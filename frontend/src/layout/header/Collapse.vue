<template>
  <el-icon size="25" class="collapse" @click="handleCollapse">
    <component :is="isCollapse ? Fold : Expand" />
  </el-icon>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useMenuStore } from '@/store/menu'
import { Fold, Expand } from '@element-plus/icons-vue'

const store = useMenuStore()
const isCollapse = computed(() => store.getCollapse)

async function handleCollapse() {
  store.setCollapse(!isCollapse.value)
}
</script>

<style lang="scss" scoped>
.collapse {
  cursor: pointer;
}
</style>
