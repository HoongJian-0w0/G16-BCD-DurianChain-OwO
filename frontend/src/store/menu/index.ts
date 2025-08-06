import { defineStore } from 'pinia'

export const useMenuStore = defineStore('menuStore', {
  state: (): { collapse: boolean } => ({
    collapse: false,
  }),

  getters: {
    getCollapse(state): boolean {
      return state.collapse
    },
  },

  actions: {
    setCollapse(collapse: boolean): void {
      this.collapse = collapse
    },
  },
  persist: true,
})
