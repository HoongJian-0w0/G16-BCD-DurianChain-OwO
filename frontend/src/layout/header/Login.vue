<template>
  <el-dropdown size="small">
    <span class="el-dropdown-link">
      <div class="icon-wrapper">
        <img :src="avatarSrc" alt="User Icon" class="icon" />
      </div>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="router.push('/user/profile')">
          <el-icon class="login-label-icon"><User /></el-icon>
          Profile</el-dropdown-item
        >
        <el-dropdown-item @click="handleLogout">
          <el-icon class="login-label-icon"><Remove /></el-icon>
          Logout</el-dropdown-item
        >
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import userIcon from '@/assets/image/user.svg'
import router from '@/router'
import message from '@/utils/message'
import { User, Remove } from '@element-plus/icons-vue'

// import { logout } from '@/api/auth'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const avatarSrc = userStore.getAvatar || userIcon

async function handleLogout() {
  /*const res = await logout()
  if (res.success) {
    userStore.clearUserInfo()
    localStorage.clear()
    await router.push('/login')
  } else {
    message.error(res.message)
  }*/
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
