<template>
  <div class="logo">
    <transition name="fade" mode="out-in">
      <img
        v-if="isCollapse"
        key="favicon"
        src="@/assets/image/DurianChain-WebLogo-02.svg"
      />
      <img v-else key="black" src="@/assets/image/DurianChain-WebLogo-01.svg" />
    </transition>
  </div>
</template>

<script lang="ts" setup>
import { useMenuStore } from '@/store/menu'
import { computed } from 'vue'

const store = useMenuStore()
const isCollapse = computed(() => store.getCollapse)
</script>

<style lang="scss" scoped>
.logo {
  display: flex;
  width: 100%;
  height: 60px;
  text-align: center;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  img {
    height: 50px;
  }

  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.6s ease;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
}
</style>
