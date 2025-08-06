import './assets/style/reset.scss'
import './assets/style/global.scss'
import './assets/style/main.scss'

import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import dialogConfirm from '@/utils/dialogConfirm'

import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'

import en from 'element-plus/es/locale/lang/en'
import 'virtual:svg-icons-register';

const pinia = createPinia()
pinia.use(piniaPersist)

const app = createApp(App)

app.use(ElementPlus, {
    locale: en,
})

app.use(router)
app.use(pinia)

app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.config.globalProperties.$dialogConfirm = dialogConfirm;