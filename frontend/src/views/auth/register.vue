<template>
  <div class="register-container">
    <div class="register-card-container">

      <!-- 📝 LEFT FORM SECTION -->
      <div class="register-card-left">
        <h2 class="register-head-title">Register</h2>

        <el-form
            :model="registerForm"
            :rules="registerRules"
            ref="formRef"
            label-position="top"
            @submit.prevent
            @keydown.enter.prevent="handleRegister"
        >
          <div class="form-columns">
            <!-- LEFT COLUMN -->
            <div class="form-column">
              <el-form-item label="Full Name" prop="name">
                <el-input v-model="registerForm.name" placeholder="e.g., John Tan Zhi Wei" />
              </el-form-item>

              <el-form-item label="Email" prop="email">
                <el-input v-model="registerForm.email" placeholder="e.g., user@email.com" />
              </el-form-item>

              <el-form-item label="Password" prop="password">
                <el-input
                    v-model="registerForm.password"
                    type="password"
                    show-password
                    placeholder="Minimum 6 characters"
                />
              </el-form-item>
            </div>

            <!-- RIGHT COLUMN -->
            <div class="form-column">
              <el-form-item label="Username" prop="username">
                <el-input v-model="registerForm.username" placeholder="Choose your username" />
              </el-form-item>

              <el-form-item label="Phone Number" prop="phone">
                <el-input v-model="registerForm.phone" placeholder="e.g., 0123456789" />
              </el-form-item>

              <el-form-item label="Role" prop="role">
                <el-select v-model="registerForm.role" placeholder="Select a role">
                  <el-option label="Farmer" value="farmer" />
                  <el-option label="Trader" value="trader" />
                  <el-option label="Logistics" value="logistics" />
                  <el-option label="Admin" value="admin" />
                </el-select>
              </el-form-item>
            </div>
          </div>

          <!-- WALLET BUTTON -->
          <el-form-item label="MetaMask Wallet" style="margin-top: 20px;" prop="walletAddress">
            <el-button
                type="default"
                @click="handleConnectWallet"
                class="wallet-button"
                :class="{ 'wallet-connected': walletAddress }"
                style="width: 100%;"
            >
              {{
                walletAddress
                    ? `Connected: ${walletAddress.slice(0, 6)}...${walletAddress.slice(-4)} (Click to switch)`
                    : 'Connect MetaMask Wallet'
              }}
            </el-button>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleRegister" style="width: 100%">Register</el-button>
          </el-form-item>

          <a class="register-register" @click="router.push('/login')">
            Already have an account? Login here
          </a>
        </el-form>
      </div>

      <!-- 🎨 RIGHT IMAGE SECTION -->
      <div class="register-card-right"></div>

    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import router from '@/router'
import message from '@/utils/message'
import { registerRules, type RegisterDTO } from '@/api/auth/AuthModels'
import { register } from '@/api/auth/index'
import type { FormInstance } from 'element-plus'

// Form + validation
const formRef = ref<FormInstance | null>(null)

const registerForm = reactive<RegisterDTO>({
  username: '',
  password: '',
  email: '',
  phone: '',
  name: '',
  role: '',
  walletAddress: ''
})

const walletAddress = ref<string | null>(null)

let walletListenerAdded = false

const handleConnectWallet = async () => {
  try {
    if (!window.ethereum) {
      message.error('MetaMask is not installed.')
      return
    }

    const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' })
    if (accounts.length > 0) {
      walletAddress.value = accounts[0]
      registerForm.walletAddress = accounts[0]
      message.success(`Connected: ${accounts[0].slice(0, 6)}...${accounts[0].slice(-4)}`)
    }
  } catch (err: any) {
    if (err.code === 4001) {
      message.error('Connection request rejected.')
    } else if (err.code === -32002) {
      message.warning('Connection already pending. Open MetaMask.')
    } else {
      message.error('MetaMask connection failed.')
    }
  }
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return message.error('Please fill in all required fields.')

    await formRef.value.validate(async (valid: boolean) => {
      if (!valid) return message.error('Please fill in all required fields.');

      try {
        const res = await register(registerForm);

        if (res.success) {
          message.success('Registration successful!');
          router.push('/login');
        } else {
          message.error(res.message);
        }
      } catch (err: any) {
      }
    });
  })
}
</script>

<style lang="scss" scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-image:
      linear-gradient(90deg, rgba(116, 195, 101, 0.1), rgba(163, 217, 119, 0.1)),
      url('@/assets/svg/star-bg.svg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  .register-card-container {
    display: flex;
    width: 1000px;
    height: 590px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    background-color: #ffffff;

    .register-card-left {
      flex: 1;
      padding: 40px 50px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .el-form-item {
        margin-bottom: 0px;
      }

      .register-head-title {
        font-size: 2rem;
        padding-bottom: 20px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      }

      .register-head-description {
        padding-top: 20px;
        font-size: 1.2rem;
      }

      .register-head-sub-description {
        font-size: 1.2rem;
        padding-bottom: 20px;
        background: linear-gradient(90deg, #74c365, #d6df72);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }

      .form-columns {
        display: flex;
        gap: 20px;

        .form-column {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 20px;
        }
      }

      .wallet-button {
        margin-top: 0px !important;
        width: 100%;
        &.wallet-connected {
          background-color: #f5f5f5;
          border: 1px solid #dcdfe6;
          color: #67c23a;
        }
      }

      .el-button {
        width: 100%;
        margin-top: 20px;
      }

      .register-register {
        justify-content: center;
        display: flex;
        font-size: 0.8rem;
        color: grey;
        margin-top: 10px;
        cursor: pointer;
      }
    }

    .register-card-right {
      width: 460px;
      background-image: url('@/assets/image/Durian-Banner-Login.jpg');
      background-size: cover;
      background-position: center;
    }

    @media (max-width: 768px) {
      flex-direction: column;
      height: auto;

      .register-card-right {
        width: 100%;
        height: 200px;
      }

      .register-card-left {
        padding: 20px;

        .form-columns {
          flex-direction: column;

          .form-column {
            width: 100%;
          }
        }

        .register-head-title {
          font-size: 1.5rem;
        }

        .register-head-description,
        .register-head-sub-description {
          font-size: 1rem;
        }
      }
    }
  }
}
</style>
