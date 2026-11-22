import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from '@/router'
import { getToken } from '@/utils/auth'
import { isExternal } from '@/utils/validate'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
//隐藏顶部进度条右侧的旋转加载圈。
NProgress.configure({ showSpinner: false })
// 常量 白名单 = ['/登录', '/认证重定向', '/绑定', '/注册']
const whiteList = ['/login', '/auth-redirect', '/bind', '/register']
const dynamicRouteRetryKey = 'dynamic-route-retry-path'

function isDynamicNotFound(to) {
  return to.name === 'DynamicNotFound' || to.matched.some((route) => route.name === 'DynamicNotFound')
}

function getDynamicRouteRefreshTarget(to) {
  if (to.matched.length === 0) {
    return to
  }
  if (isDynamicNotFound(to)) {
    return to
  }
  if (to.path === '/404' && to.redirectedFrom) {
    return to.redirectedFrom
  }
  return null
}

function shouldRefreshDynamicRoutes(target) {
  if (!target) {
    return false
  }
  const retryPath = window.sessionStorage.getItem(dynamicRouteRetryKey)
  if (retryPath === target.fullPath) {
    return false
  }
  window.sessionStorage.setItem(dynamicRouteRetryKey, target.fullPath)
  return true
}

function toRefreshLocation(target) {
  return {
    path: target.path,
    query: target.query,
    hash: target.hash,
    replace: true
  }
}

function clearDynamicRouteRetry() {
  window.sessionStorage.removeItem(dynamicRouteRetryKey)
}

// 递归过滤路由配置，剔除外部链接路由，返回仅包含内部路由的路由树。
function normalizeRouteForRegister(route) {
  if (isExternal(route.path)) {
    return null
  }
    // 浅拷贝当前路由对象，避免修改原对象
  const currentRoute = { ...route }
  if (currentRoute.children?.length) {
    currentRoute.children = currentRoute.children
      .map((child) => normalizeRouteForRegister(child))
      .filter(Boolean)
  }
  return currentRoute
}
// 动态添加路由
function addDynamicRoutes(routes) {
  routes
    .map((route) => normalizeRouteForRegister(route))
    .filter(Boolean)
    .forEach((route) => {
      router.addRoute(route)
    })

  if (!router.hasRoute('DynamicNotFound')) {
    router.addRoute({
      path: '/:pathMatch(.*)*',
      name: 'DynamicNotFound',
      redirect: '/404',
      hidden: true
    })
  }
}
// 根据是否登录（token 存在）及角色权限，决定用户能访问哪些页面
router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  if (getToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
      return
    }
    if (userStore.roles.length === 0) {
      try {
        await userStore.GetInfo()
        const routes = await permissionStore.GenerateRoutes()
        addDynamicRoutes(routes)
        next({ ...to, replace: true })
      } catch (error) {
        await userStore.FedLogOut()
        next(`/login?redirect=${to.fullPath}`)
        NProgress.done()
      }
      return
    }
    const dynamicRouteRefreshTarget = getDynamicRouteRefreshTarget(to)
    if (shouldRefreshDynamicRoutes(dynamicRouteRefreshTarget)) {
      try {
        const routes = await permissionStore.GenerateRoutes()
        addDynamicRoutes(routes)
        next(toRefreshLocation(dynamicRouteRefreshTarget))
      } catch (error) {
        await userStore.FedLogOut()
        next(`/login?redirect=${to.fullPath}`)
        NProgress.done()
      }
      return
    }
    clearDynamicRouteRetry()
    next()
    return
  }

  if (whiteList.includes(to.path)) {
    next()
  } else {
    next(`/login?redirect=${to.fullPath}`)
    NProgress.done()
  }
})
// 无论守卫成功或失败，每次路由跳转完成后调用 NProgress.done() 关闭顶部进度条
router.afterEach(() => {
  NProgress.done()
})
