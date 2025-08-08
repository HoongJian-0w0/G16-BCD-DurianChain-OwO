<template>
  <template v-for="menu in menuList" :key="menu.path">
    <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
      <template #title>
        <div
            class="submenu-title"
            :class="{ active: isActive(menu.path) }"
        >
          <el-icon>
            <component :is="menu.meta.icon" />
          </el-icon>
          <span class="menu-title" v-if="!isCollapse">{{ menu.meta.title }}</span>
        </div>
      </template>
      <menu-item :menuList="menu.children" />
    </el-sub-menu>

    <el-menu-item v-else :index="menu.path" @click="handleClick(menu)">
      <el-icon>
        <component :is="menu.meta.icon" />
      </el-icon>
      <template #title>{{ menu.meta.title }}</template>
    </el-menu-item>
  </template>
</template>

<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'
import { useMenuStore } from '@/store/menu'

defineProps<{ menuList: any[] }>()

const router = useRouter()
const route = useRoute()
const menuStore = useMenuStore()

const isCollapse = computed(() => menuStore.getCollapse)

function handleClick(menu: any) {
  if (menu.path) {
    router.push(menu.path)
  }
}

function isActive(path: string): boolean {
  return route.path === path
}
</script>

<style scoped>
.submenu-title {
  display: flex;
  align-items: center;
  height: 100%;
  cursor: pointer;
}
.submenu-title.active {
  color: #74c365 !important;
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
</style>
