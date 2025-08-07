import { defineStore } from 'pinia'

export const useUserStore = defineStore('UserStore', {
  state: () => ({
    userId: '',
    username: '',
    name: '',
    avatar: '',
    walletAddress: '',
    role: ''
  }),

  getters: {
    getUserId: state => state.userId,
    getUsername: state => state.username,
    getName: state => state.name,
    getAvatar: state => state.avatar,
    getWalletAddress: state => state.walletAddress,
    getRole: state => state.role
  },

  actions: {
    setUserInfo(user: {
      id: number
      username: string
      name: string
      avatar?: string
      walletAddress: string
      role: string
    }) {
      this.userId = String(user.id)
      this.username = user.username
      this.name = user.name
      this.avatar = user.avatar || ''
      this.walletAddress = user.walletAddress
      this.role = user.role
    },

    clearUserInfo() {
      this.userId = ''
      this.username = ''
      this.name = ''
      this.avatar = ''
      this.walletAddress = ''
      this.role = ''
    },

    updateRole(newRole: string) {
      this.role = newRole
    }
  },

  persist: true
})
