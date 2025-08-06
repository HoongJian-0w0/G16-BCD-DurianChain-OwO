<template>
  <div class="close-tabs">
    <el-dropdown size="small">
      <span class="el-dropdown-link">
        <el-icon class="el-icon--right">
          <Close />
        </el-icon>
        Close
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="closeAllTabs">Close All</el-dropdown-item>
          <el-dropdown-item @click="closeLeftTab">Close Left</el-dropdown-item>
          <el-dropdown-item @click="closeRightTab">Close Right</el-dropdown-item>
          <el-dropdown-item @click="closeCurrentTab">Close Current</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { type Tab, useTabStore } from '@/store/tabs'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const store = useTabStore()

function closeCurrentTab() {
  const targetName = route.path
  if (targetName === '/durianchain') return
  const tabs = store.getTab
  let activeName = route.path
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
  store.tabList = tabs.filter((tab) => tab.path !== targetName)
  router.push({ path: activeName })
}

function closeRightTab() {
  const path = route.path
  let index = store.tabList.findIndex((item) => item.path === path)
  store.tabList.splice(index + 1)
}

function closeLeftTab() {
  const path = route.path
  let index = store.tabList.findIndex((item) => item.path === path)
  store.tabList.splice(1, index - 1)
}

function closeAllTabs() {
  store.tabList.splice(1, store.tabList.length)
  router.push({ path: '/durianchain' })
}
</script>

<style lang="scss" scoped>
.el-dropdown-link {
  cursor: pointer;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
}

.el-dropdown-link:focus {
  outline: none;
}

.close-tabs {
  width: 80px;
  height: 40px;
  position: fixed;
  right: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  border-left: 1px solid var(--el-border-color-light);
  padding-right: 10px;
  justify-content: center;
}
</style>
