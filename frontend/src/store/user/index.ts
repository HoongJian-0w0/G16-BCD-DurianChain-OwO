import { defineStore } from 'pinia'

export const useUserStore = defineStore('UserStore', {
  state: () => ({
    userId: '',
    username: '',
    firstName: '',
    lastName: '',
    nickName: '',
    avatar: '',
    walletAddress: '',
    role: '',
  }),

  getters: {
    getUserId: state => state.userId,
    getUsername: state => state.username,
    getFirstName: state => state.firstName,
    getLastName: state => state.lastName,
    getFullName: state => `${state.firstName} ${state.lastName}`.trim(),
    getNickName: state => state.nickName,
    getAvatar: state => state.avatar,
    getWalletAddress: state => state.walletAddress,
    getRole: state => state.role,
  },

  actions: {
    setUserInfo(user: {
      id: number
      username: string
      firstName: string
      lastName: string
      nickName: string
      avatar: string
      walletAddress: string
      role: string
    }) {
      this.userId = String(user.id)
      this.username = user.username
      this.firstName = user.firstName
      this.lastName = user.lastName
      this.nickName = user.nickName
      this.avatar = user.avatar
      this.walletAddress = user.walletAddress
      this.role = user.role
    },

    clearUserInfo() {
      this.userId = ''
      this.username = ''
      this.firstName = ''
      this.lastName = ''
      this.nickName = ''
      this.avatar = ''
      this.walletAddress = ''
      this.role = ''
    },

    updateRole(newRole: string) {
      this.role = newRole
    },
  },

  persist: true,
})
