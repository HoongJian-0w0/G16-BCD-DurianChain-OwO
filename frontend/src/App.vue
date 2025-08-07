<template>
  <RouterView />
</template>

<script setup lang="ts">
import {onMounted, onUnmounted} from 'vue'
import {RouterView, useRoute} from 'vue-router'
import { ElConfigProvider } from 'element-plus'
import { useUserStore } from '@/store/user/index'
import message from "@/utils/message";
import router from "@/router";

const userStore = useUserStore()

async function handleLogout() {
  try {
    userStore.clearUserInfo()
    localStorage.removeItem('token')
    await router.push('/login')
  } catch (err) {
    message.error('Logout failed.')
  }
}

const route = useRoute();

const handleAccountsChanged = async (accounts: string[]) => {
  const currentWallet = userStore.walletAddress?.toLowerCase() || ''
  const isProtectedRoute = route.path.startsWith('/durianchain')

  if (!isProtectedRoute) return

  if (accounts.length === 0) {
    message.warning('Wallet disconnected — logging out...')
    await handleLogout()
  } else if (accounts[0].toLowerCase() !== currentWallet) {
    message.warning('Wallet changed — logging out...')
    await handleLogout()
  }
}

onMounted(() => {
  if (window.ethereum && !window._walletListenerAdded) {
    window.ethereum.on('accountsChanged', handleAccountsChanged)
    window._walletListenerAdded = true
  }
})

onUnmounted(() => {
  if (window.ethereum && window._walletListenerAdded) {
    window.ethereum.removeListener('accountsChanged', handleAccountsChanged)
    window._walletListenerAdded = false
  }
})
</script>

<style scoped></style>
