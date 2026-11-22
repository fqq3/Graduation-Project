import { defineStore } from 'pinia'           // Pinia 状态管理库
import { constantRoutes } from '@/router'      // 固定路由（登录、首页等）
import { getRouters } from '@/api/menu'        // 从 src/api/menu 文件导入名为 getRouters 的命名导出函数
import Layout from '@/layout/index.vue'        // 布局组件
import ParentView from '@/components/ParentView/index.vue'  // 父级容器组件

const viewModules = import.meta.glob('../views/**/*.vue')
//批量匹配 views 目录及所有子目录的 .vue 文件返回对象的键是文件路径，值是懒加载函数
const notFoundView = viewModules['../views/error/404.vue']

function normalizeRouteRecords(payload) {
  if (Array.isArray(payload)) {
    return payload
  }

  if (!payload || typeof payload !== 'object') {
    return []
  }

  if (Array.isArray(payload.data)) {
    return payload.data
  }

  if (Array.isArray(payload.rows)) {
    return payload.rows
  }

  if (Array.isArray(payload.result)) {
    return payload.result
  }

  if (payload.data && typeof payload.data === 'object') {
    if (Array.isArray(payload.data.data)) {
      return payload.data.data
    }
    if (Array.isArray(payload.data.rows)) {
      return payload.data.rows
    }
    if (Array.isArray(payload.data.result)) {
      return payload.data.result
    }
  }

  return []
}

function loadView(view) {
  const normalizedView = String(view || '').replace(/^\/+/, '')
  const directKey = `../views/${normalizedView}.vue`
  const indexKey = `../views/${normalizedView}/index.vue`
  return viewModules[directKey] || viewModules[indexKey] || notFoundView
}

function filterAsyncRouter(asyncRouterMap) {
  return asyncRouterMap.map((route) => {
    const currentRoute = { ...route }
    if (currentRoute.component) {
      if (currentRoute.component === 'Layout') {
        currentRoute.component = Layout
      } else if (currentRoute.component === 'ParentView') {
        currentRoute.component = ParentView
      } else {
        currentRoute.component = loadView(currentRoute.component)
      }
    }
    if (currentRoute.children?.length) {
      currentRoute.children = filterAsyncRouter(currentRoute.children)
    }
    return currentRoute
  })
}

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: []
  }),
  actions: {
    async GenerateRoutes() {
      const res = await getRouters()
      const asyncRoutes = normalizeRouteRecords(res)
      window.sessionStorage.setItem('async-routes', JSON.stringify(asyncRoutes))
      const accessedRoutes = filterAsyncRouter(asyncRoutes)
      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)
      return accessedRoutes
    }
  }
})
