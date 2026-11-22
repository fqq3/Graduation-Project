import { useAppStore } from './app'
import { useUserStore } from './user'
import { useTagsViewStore } from './tags-view'
import { usePermissionStore } from './permission'
import { useSettingsStore } from './settings'

const rootActionNamespaces = {
  Login: 'user',
  GetInfo: 'user',
  LogOut: 'user',
  FedLogOut: 'user',
  ResetToken: 'user',
  resetToken: 'user',
  GenerateRoutes: 'permission'
}

const storeRegistry = {
  app: useAppStore,
  user: useUserStore,
  tagsView: useTagsViewStore,
  permission: usePermissionStore,
  settings: useSettingsStore
}

function getStores() {
  return Object.fromEntries(
    Object.entries(storeRegistry).map(([key, useStore]) => [key, useStore()])
  )
}

function buildGetters() {
  const stores = getStores()
  return {
    sidebar: stores.app.sidebar,
    size: stores.app.size,
    device: stores.app.device,
    visitedViews: stores.tagsView.visitedViews,
    cachedViews: stores.tagsView.cachedViews,
    token: stores.user.token,
    avatar: stores.user.avatar,
    name: stores.user.name,
    introduction: stores.user.introduction,
    roles: stores.user.roles,
    permissions: stores.user.permissions,
    permission_routes: stores.permission.routes,
    theme: stores.settings.theme,
    showSettings: stores.settings.showSettings,
    tagsView: stores.settings.tagsView,
    fixedHeader: stores.settings.fixedHeader,
    sidebarLogo: stores.settings.sidebarLogo,
    sideTheme: stores.settings.sideTheme
  }
}

const legacyStore = {
  get state() {
    return getStores()
  },
  get getters() {
    return buildGetters()
  },
  dispatch(type, payload) {
    const stores = getStores()
    const [namespace, action] = type.includes('/')
      ? type.split('/')
      : [rootActionNamespaces[type] || 'user', type]
    const store = stores[namespace]
    if (!store || typeof store[action] !== 'function') {
      return Promise.reject(new Error(`Unknown store action: ${type}`))
    }
    return Promise.resolve(store[action](payload))
  }
}

export default legacyStore
