<template>
    <div :class="{'has-logo':showLogo}">
        <logo v-if="showLogo" :collapse="isCollapse" />
        <el-scrollbar wrap-class="scrollbar-wrapper">
            <el-menu
                :default-active="activeMenu"
                :collapse="isCollapse"
                :background-color="variables.menuBg"
                :text-color="variables.menuText"
                :unique-opened="true"
                active-text-color="#FBE4DE"
                :collapse-transition="false"
                mode="vertical"
            >
                <sidebar-item
                    v-for="(route, index) in permission_routes"
                    :key="route.path  + index"
                    :item="route"
                    :base-path="route.path"
                />
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script>
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'
import { constantRoutes } from '@/router'
import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'
import { useSettingsStore } from '@/stores/settings'

const styleVariables = {
  menuBg: '#312520',
  menuText: 'rgba(255, 255, 255, 0.68)'
}

export default {
  components: { SidebarItem, Logo },
  computed: {
    settings() {
      return useSettingsStore()
    },
    permission_routes() {
      const permissionStore = usePermissionStore()
      const routes = permissionStore.routes
      const addRoutes = permissionStore.addRoutes
      const cachedRawRoutes = JSON.parse(window.sessionStorage.getItem('async-routes') || '[]')

      if (Array.isArray(routes) && routes.length > constantRoutes.length) {
        return routes
      }

      if (Array.isArray(addRoutes) && addRoutes.length > 0) {
        return constantRoutes.concat(addRoutes)
      }

      if (Array.isArray(cachedRawRoutes) && cachedRawRoutes.length > 0) {
        return constantRoutes.concat(cachedRawRoutes)
      }

      if (Array.isArray(routes) && routes.length > 0) {
        return routes
      }

      return constantRoutes
    },
    sidebar() {
      return useAppStore().sidebar
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return useSettingsStore().sidebarLogo
    },
    variables() {
      return styleVariables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
};
</script>
