<template>
  <MenuLogo />
  <div class="menubar">
    <el-menu
      :default-active="defaultActive"
      class="el-menu-vertical-demo"
      :collapse="isCollapse"
      unique-opened
      router
    >
      <MenuItem :menuList="menuList" />
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import MenuItem from '@/layout/menu/MenuItem.vue'
import MenuLogo from '@/layout/menu/MenuLogo.vue'
import { ref, reactive, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useMenuStore } from '@/store/menu'

const store = useMenuStore()
const route = useRoute()

const isCollapse = computed(() => store.getCollapse)

const defaultActive = computed(() => {
  const { path } = route
  return path
})

let menuList = reactive([
  {
    path: '/durianchain',
    component: '/views/dashboard',
    name: 'Dashboard',
    meta: {
      title: 'Dashboard',
      icon: 'HomeFilled',
      roles: [''],
    },
  },

  // Admin Panel
  {
    path: '/durianchain/admin/dashboard',
    component: 'Layout',
    name: 'AdminDashboard',
    meta: {
      title: 'System Management',
      icon: 'Setting',
      roles: ['admin'],
    },
    children: [
      {
        path: '/durianchain/admin/user-list',
        component: '/views/durianchain/admin/userList',
        name: 'AdminUserList',
        meta: {
          title: 'User Management',
          icon: 'User',
          roles: ['admin'],
        },
      },
      {
        path: '/durianchain/admin/variety-list',
        component: '/views/durianchain/admin/varietyList',
        name: 'VarietyList',
        meta: {
          title: 'Variety Management',
          icon: 'Grid',
          roles: ['admin'],
        },
      },
    ],
  },

  // Farmer Panel
  {
    path: '/durianchain/farmer/dashboard',
    component: 'Layout',
    name: 'FarmerDashboard',
    meta: {
      title: 'Farmer Management',
      icon: 'Watermelon',
      roles: ['farmer'],
    },
    children: [
      {
        path: '/durianchain/farmer/batch-list',
        component: '/views/durianchain/farmer/batch/batchList',
        name: 'BatchList',
        meta: {
          title: 'Batch List',
          icon: 'List',
          roles: ['farmer'],
        },
      },
      {
        path: '/durianchain/farmer/durian-list',
        component: '/views/durianchain/farmer/durian/durianList',
        name: 'DurianList',
        meta: {
          title: 'Durian List',
          icon: 'Orange',
          roles: ['farmer'],
        },
      },
      {
        path: '/durianchain/farmer/farm-list',
        component: '/views/durianchain/farmer/farm/farmList',
        name: 'FarmList',
        meta: {
          title: 'Farm List',
          icon: 'Location',
          roles: ['farmer'],
        },
      },
    ],
  },

  // Trader Panel
  {
    path: '/durianchain/trader/dashboard',
    component: 'Layout',
    name: 'TraderDashboard',
    meta: {
      title: 'Trader Management',
      icon: 'Briefcase',
      roles: ['trader'],
    },
    children: [
      {
        path: '/durianchain/trader/order-list',
        component: '/views/durianchain/trader/orderList',
        name: 'OrderList',
        meta: {
          title: 'Order List',
          icon: 'Document',
          roles: ['trader'],
        },
      },
      {
        path: '/durianchain/trader/agency-list',
        component: '/views/durianchain/trader/agencyList',
        name: 'AgencyList',
        meta: {
          title: 'Agency List',
          icon: 'OfficeBuilding',
          roles: ['trader'],
        },
      },
    ],
  },

  // Logistics Panel
  {
    path: '/durianchain/logistics/dashboard',
    component: 'Layout',
    name: 'LogisticsDashboard',
    meta: {
      title: 'Logistics Management',
      icon: 'Van',
      roles: ['logistics'],
    },
    children: [
      {
        path: '/durianchain/logistics/parcel-list',
        component: '/views/durianchain/logistics/parcelList',
        name: 'ParcelList',
        meta: {
          title: 'Parcel List',
          icon: 'Box',
          roles: ['logistics'],
        },
      },
      {
        path: '/durianchain/logistics/logistics-list',
        component: '/views/durianchain/logistics/logisticsList',
        name: 'LogisticsList',
        meta: {
          title: 'Logistics List',
          icon: 'Van',
          roles: ['logistics'],
        },
      }
    ],
  },
])
</script>

<style lang="scss" scoped>
.menubar {
  margin-top: 20px;
}

.menu-vertical {
  width: 230px;
  min-height: 400px;
  border-right: none;
  font-family: var(--font-family-base);
}

:deep(.el-menu) {
  border-right: none !important;
  transition: width 0.8s ease !important;
}

:deep(.el-sub-menu .el-sub-menu__title) {
  color: var(--el-text-color-primary) !important;
}

:deep(.el-menu-item) {
  color: var(--el-text-color-primary);
  font-weight: 400;
  .el-icon {
    margin-right: 8px;
  }
}

:deep(.el-menu-item.is-active) {
  color: #74c365 !important;
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

:deep(.is-opened .el-menu-item) {
  background-color: #fff !important;
}

:deep(.el-menu-item:hover) {
  background-color: #fff !important;
}
</style>
