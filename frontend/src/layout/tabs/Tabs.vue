<template>
  <el-tabs v-model="activeTab" @tab-click="clickBtn" type="card" @tab-remove="removeTab">
    <el-tab-pane
      v-for="(item, index) in tabList"
      :key="item.path"
      :label="item.title"
      :name="item.path"
      :closable="index !== 0"
    />
  </el-tabs>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { type Tab, useTabStore } from '@/store/tabs'
import { useRoute, useRouter } from 'vue-router'
import type { TabsPaneContext } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = useTabStore()
const activeTab = ref('')

const tabList = computed(() => {
  return store.getTab
})

function clickBtn(pane: TabsPaneContext) {
  const { props } = pane
  router.push({ path: props.name as string })
}

function addTab() {
  const { path, meta } = route
  const tab: Tab = {
    path: path,
    title: meta.title as string,
  }
  store.addTab(tab)
}

function removeTab(targetName: string) {
  const tabs = tabList.value
  let activeName = activeTab.value
  if (activeName === targetName) {
    tabs.forEach((tab: Tab, index: number) => {
      if (tab.path === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName = nextTab.path
        }
      }
    })
  }
  activeTab.value = activeName
  store.tabList = tabs.filter((tab) => tab.path !== targetName)
  router.push({ path: activeName })
}

function setActiveTab() {
  activeTab.value = route.path
}

watch(
  () => route.path,
  () => {
    setActiveTab()
    addTab()
  },
)

onMounted(() => {
  setActiveTab()
  addTab()
})
</script>

<style lang="scss" scoped>
:deep(.el-tabs__header) {
  border-bottom: none;
  margin: 0px;
}

:deep(.el-tabs__item) {
  height: 26px !important;
  line-height: 26px !important;
  text-align: center !important;
  border: 1px solid #d8dce5 !important;
  margin: 0px 3px !important;
  color: #495060 !important;
  font-size: 12px !important;
  padding: 0px 10px !important;
}

:deep(.el-tabs__nav) {
  border: none !important;
}

:deep(.is-active) {
  border-bottom: 1px solid transparent !important;
  border: 1px solid #74c365 !important;
  background: linear-gradient(90deg, #74c365, #d6df72);
  color: #fff !important;
}

:deep(.el-tabs__item:hover) {
  color: #495060 !important;
}

:deep(.is-active:hover) {
  color: #fff !important;
}

:deep(.el-tabs__nav-next) {
  line-height: 26px !important;
}

:deep(.el-tabs__nav-prev) {
  line-height: 26px !important;
}

.el-tabs {
  width: 100%;
}
</style>
