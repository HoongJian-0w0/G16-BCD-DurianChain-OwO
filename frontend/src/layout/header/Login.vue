<template>
  <el-dropdown size="small">
    <span class="el-dropdown-link">
      <div class="icon-wrapper">
        <img src="@/assets/image/user.svg" alt="User Icon" class="icon" />
      </div>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="handleLogout">
          <el-icon class="login-label-icon"><Remove /></el-icon>
          Logout
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import router from '@/router'
import message from '@/utils/message'
import { useUserStore } from '@/store/user/index'

const userStore = useUserStore()

async function handleLogout() {
  try {
    userStore.clearUserInfo()
    localStorage.removeItem('token')
    message.warning('You have been logged out.')
    await router.push('/login')
  } catch (err) {
    message.error('Logout failed.')
  }
}
</script>

<style scoped>
.icon-wrapper {
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(90deg, #74c365, #d6df72);
  display: inline-block;
  cursor: pointer;
}

.icon {
  padding: 2px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: block;
  background-color: white;
}

.el-dropdown-link {
  outline: none;
  border: none;
}

.el-dropdown-link:focus,
.el-dropdown-link:hover {
  outline: none;
  border: none;
  box-shadow: none;
}
</style>
