import { defineStore } from 'pinia'

export type Tab = {
  title: string
  path: string
}

export type TabState = {
  tabList: Tab[]
}

export const useTabStore = defineStore('TabStore', {
  state: (): TabState => {
    return {
      tabList: [],
    }
  },
  getters: {
    getTab(state): Tab[] {
      return state.tabList
    },
  },
  actions: {
    addTab(tab: Tab) {
      if (this.tabList.some((item) => item.path === tab.path)) return
      if (tab.path === '/durianchain') {
        this.tabList.unshift(tab)
      } else {
        this.tabList.push(tab)
      }
    },
  },
  persist: true,
})
