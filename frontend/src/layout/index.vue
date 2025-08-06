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
        <div class="router-view-wrapper">
          <router-view />
        </div>
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
  display: flex;

  .sidebar {
    width: auto;
    background-color: #fff;
    border-right: solid 1px rgba(0, 0, 0, 0.1);
  }

  .header {
    background-color: #fff;
    align-items: center;
    display: flex;
    height: 60px;
    border-bottom: 1px solid #e5e5e5;
  }

  .el-main {
    padding: 0 !important;
  }

  .main {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 60px);
    background-image:
        linear-gradient(90deg, rgba(116, 195, 101, 0.02), rgba(163, 217, 119, 0.1)),
        url('@/assets/svg/star-bg.svg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    overflow: hidden;

    .tabs {
      position: sticky;
      top: 0;
      z-index: 10;
      background: #fff;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 10px;
      border-bottom: 1px solid var(--el-border-color-light);
      height: 40px;
    }

    .router-view-wrapper {
      flex: 1;
      overflow-y: auto;
      padding: 12px;
    }
  }
}
</style>
