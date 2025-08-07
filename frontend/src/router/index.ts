import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/login.vue'),
    meta: { breadcrumb: 'Login' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/register.vue'),
    meta: { breadcrumb: 'Register' },
  },
  {
    path: '/durianchain',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: '',
        name: 'DashboardHome',
        component: () => import('@/views/dashboard.vue'),
        meta: {
          title: 'Dashboard',
          icon: 'HomeFilled',
          roles: [''],
        },
      },

      // Admin Routes
      {
        path: 'admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/durianchain/admin/dashboard.vue'),
        meta: { title: 'Admin Panel', icon: 'Setting', roles: ['admin'] },
      },
      {
        path: 'admin/user-list',
        name: 'AdminUserList',
        component: () => import('@/views/durianchain/admin/user/userList.vue'),
        meta: { title: 'User Management', roles: ['admin'] },
      },
      {
        path: 'admin/variety-list',
        name: 'VarietyList',
        component: () => import('@/views/durianchain/admin/variety/varietyList.vue'),
        meta: { title: 'Variety List', roles: ['admin'] },
      },

      // Farmer Routes
      {
        path: 'farmer/dashboard',
        name: 'FarmerDashboard',
        component: () => import('@/views/durianchain/farmer/dashboard.vue'),
        meta: { title: 'Farmer Panel', icon: 'Apple', roles: ['farmer'] },
      },
      {
        path: 'farmer/batch-list',
        name: 'BatchList',
        component: () => import('@/views/durianchain/farmer/batch/batchList.vue'),
        meta: { title: 'Batch List', roles: ['farmer'] },
      },
      {
        path: 'farmer/durian-list',
        name: 'DurianList',
        component: () => import('@/views/durianchain/farmer/durian/durianList.vue'),
        meta: { title: 'Durian List', roles: ['farmer'] },
      },
      {
        path: 'farmer/farm-list',
        name: 'FarmList',
        component: () => import('@/views/durianchain/farmer/farm/farmList.vue'),
        meta: { title: 'Farm List', roles: ['farmer'] },
      },

      // Logistics Routes
      {
        path: 'logistics/dashboard',
        name: 'LogisticsDashboard',
        component: () => import('@/views/durianchain/logistics/dashboard.vue'),
        meta: { title: 'Logistics Panel', icon: 'Van', roles: ['logistics'] },
      },
      {
        path: 'logistics/logistics-list',
        name: 'LogisticsList',
        component: () => import('@/views/durianchain/logistics/logisticsCompany/logisticsList.vue'),
        meta: { title: 'Logistics List', roles: ['logistics'] },
      },
      {
        path: 'logistics/parcel-list',
        name: 'ParcelList',
        component: () => import('@/views/durianchain/logistics/parcelList.vue'),
        meta: { title: 'Parcel List', roles: ['logistics'] },
      },

      // Trader Routes
      {
        path: 'trader/dashboard',
        name: 'TraderDashboard',
        component: () => import('@/views/durianchain/trader/dashboard.vue'),
        meta: { title: 'Trader Panel', icon: 'ShoppingCart', roles: ['trader'] },
      },
      {
        path: 'trader/agency-list',
        name: 'AgencyList',
        component: () => import('@/views/durianchain/trader/agency/agencyList.vue'),
        meta: { title: 'Agency List', roles: ['trader'] },
      },
      {
        path: 'trader/order-list',
        name: 'OrderList',
        component: () => import('@/views/durianchain/trader/orderList.vue'),
        meta: { title: 'Order List', roles: ['trader'] },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
