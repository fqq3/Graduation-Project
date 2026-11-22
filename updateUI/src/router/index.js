import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:pathMatch(.*)*',
        component: () => import('@/views/redirect.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login.vue'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: 'index',
        name: 'Index',
        component: () => import('@/views/index.vue'),
        meta: { title: '\u9996\u9875', icon: 'dashboard', noCache: true, affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: '/user/profile',
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/system/user/profile/index.vue'),
        meta: { title: '\u4e2a\u4eba\u4e2d\u5fc3', icon: 'user' }
      }
    ]
  },
  {
    path: '/serviceManage/complaintWordCloud',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        name: 'ComplaintWordCloud',
        component: () => import('@/views/property/analysis/complaintWordCloud.vue'),
        meta: { title: '投诉建议分析', activeMenu: '/communityService/suggest' }
      }
    ]
  },

  {
    path: '/property/community/map/:communityId',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        name: 'CommunityMap',
        component: () => import('@/views/property/community/map.vue'),
        meta: { title: '小区地图', activeMenu: '/communityAsset/community' }
      }
    ]
  },
  {
    path: '/dict',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'type/data/:dictId(\\d+)',
        name: 'DictData',
        component: () => import('@/views/system/dict/data.vue'),
        meta: { title: '\u5b57\u5178\u6570\u636e' }
      }
    ]
  },
  {
    path: '/job',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'log',
        name: 'JobLog',
        component: () => import('@/views/monitor/job/log.vue'),
        meta: { title: '\u8c03\u5ea6\u65e5\u5fd7', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/gen',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'edit/:tableId(\\d+)',
        name: 'GenEdit',
        component: () => import('@/views/tool/gen/editTable.vue'),
        meta: { title: '\u4fee\u6539\u751f\u6210\u914d\u7f6e', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: constantRoutes
})

export default router
