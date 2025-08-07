<template>
  <div class="header">
    <div class="left-header">
      <Collapse />
      <Breadcrumb />
    </div>
    <div class="right-header">
      <span class="wlc-name">
        <span class="gradient-text">Welcome,</span>{{ displayName }}
      </span>
      <Login />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Collapse from '@/layout/header/Collapse.vue'
import Breadcrumb from '@/layout/header/Breadcrumb.vue'
import Login from '@/layout/header/Login.vue'
import { useUserStore } from '@/store/user/index'

const userStore = useUserStore()

const displayName = computed(() => userStore.name || 'Guest')
</script>

<style lang="scss" scoped>
.header {
  justify-content: space-between;
  width: 100%;

  .left-header {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .right-header {
    display: flex;
    justify-content: center;
    align-items: center;

    .wlc-name {
      margin-right: 8px;
      font-size: 13px;
      font-weight: 500;
    }

    .gradient-text {
      font-weight: 700;
      background: linear-gradient(90deg, #74c365, #d6df72);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      color: transparent;
      font-size: 14px;
      margin-right: 4px;
    }
  }

}

</style>
