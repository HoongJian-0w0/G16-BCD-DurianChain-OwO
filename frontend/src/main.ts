import './assets/style/reset.scss'
import './assets/style/global.scss'
import './assets/style/main.scss'

import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router'
import dialogConfirm from '@/utils/dialogConfirm'
import VChart from 'vue-echarts'

import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'

import en from 'element-plus/es/locale/lang/en'
import 'virtual:svg-icons-register'

// --- ECharts (vue-echarts) imports ---
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
} from 'echarts/components'

// Register only the parts you need for performance
use([
    CanvasRenderer,
    BarChart,
    LineChart,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
])
// -------------------------------------

const pinia = createPinia()
pinia.use(piniaPersist)

const app = createApp(App)

app.use(ElementPlus, { locale: en })
app.use(router)
app.use(pinia)

// Register Element Plus icons globally
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// Register VChart globally for all charts
app.component('v-chart', VChart)

app.config.globalProperties.$dialogConfirm = dialogConfirm

app.mount('#app')
