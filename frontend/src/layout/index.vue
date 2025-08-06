<template>
  <el-container class="layout">
    <el-aside class="sidebar">
      <MenuBar />
    </el-aside>
    <el-container>
      <el-header class="header">
        <Header />
      </el-header>
      <el-main class="main">
        <div class="tabs">
          <Tabs class="tab" />
          <CloseTabs />
        </div>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts" setup>
import Header from '@/layout/header/Header.vue'
import MenuBar from '@/layout/menu/MenuBar.vue'
import Tabs from '@/layout/tabs/Tabs.vue'
import CloseTabs from '@/layout/tabs/CloseTabs.vue'
</script>

<style lang="scss" scoped>
.layout {
  height: 100vh;

  .sidebar {
    width: auto;
    background-color: #fff;
    border-right: solid 1px rgba(0, 0, 0, 0.1);
  }

  .header {
    background-color: #fff;
    align-items: center;
    display: flex;
  }

  .el-main {
    padding: 12px !important;
  }

  .main {
    background-image:
        linear-gradient(90deg, rgba(116, 195, 101, 0.02), rgba(163, 217, 119, 0.1)),
        url('@/assets/svg/star-bg.svg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;

    padding: 0 !important;

    .tabs {
      .tab {
        padding-left: 10px;
        padding-right: 80px;
      }
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid var(--el-border-color-light);
    }
  }
}
</style>
