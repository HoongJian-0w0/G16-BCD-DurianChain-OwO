<template>
  <el-breadcrumb separator="/" class="breadcrumb">
    <el-breadcrumb-item v-for="item in tabs">{{ item.meta.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script lang="ts" setup>
import { ref, watch, type Ref } from 'vue'
import { useRoute, type RouteLocationMatched } from 'vue-router'

const route = useRoute()
const tabs: Ref<RouteLocationMatched[]> = ref([])

const getBreadcrumb = () => {
  let matchedRoute = route.matched.filter((item) => item.meta && item.meta.title)
  const first = matchedRoute[0]
  if (first.path !== '/dashboard') {
    matchedRoute = [{ path: '/dashboard', meta: { title: 'Dashboard' } } as any].concat(
      matchedRoute,
    )
  }
  tabs.value = matchedRoute
}
getBreadcrumb()

watch(
  () => route.path,
  () => {
    getBreadcrumb()
  },
)
</script>

<style lang="scss" scoped>
.breadcrumb {
  margin-left: 20px;
}
</style>
