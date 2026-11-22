<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />
    <div class="right-menu">
      <el-select
        v-if="showCommunitySelector"
        v-model="communityId"
        class="community-selector-container right-menu-item hover-effect"
        style="border: 0; position: relative"
        filterable
        clearable
        placeholder="请选择小区"
        @change="selectedCommunity"
      >
        <el-option
          v-for="item in options"
          :key="item.communityId"
          :label="item.communityName"
          :value="String(item.communityId)"
        />
      </el-select>

      <el-dropdown class="avatar-container user-avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar" />
          <el-icon class="caret-bottom el-icon-caret-bottom">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { ArrowDown } from '@element-plus/icons-vue'
import Breadcrumb from '@/components/Breadcrumb/index.vue'
import Hamburger from '@/components/Hamburger/index.vue'
import { getCommunityTree } from '@/api/property/community'
import { getToken, getPropertyId, setPropertyId } from '@/utils/auth'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import defaultAvatar from '@/assets/image/default.gif'

export default {
  name: 'Navbar',
  inject: ['reload'],
  components: {
    ArrowDown,
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      options: [],
      communityId: ''
    }
  },
  computed: {
    sidebar() {
      return useAppStore().sidebar
    },
    avatar() {
      return useUserStore().avatar || defaultAvatar
    },
    showCommunitySelector() {
      return !this.isSystemManagementRoute() && !this.isCommunityInfoRoute()
    }
  },
  created() {
    window.addEventListener('community-selector-sync', this.handleCommunitySelectorSync)
    if (!getToken()) {
      this.options = []
      this.communityId = ''
      return
    }

    getCommunityTree()
      .then((response) => {
        this.options = response.data || []
        this.syncCommunitySelector()
      })
      .catch(() => {
        this.options = []
      })
  },
  beforeUnmount() {
    window.removeEventListener('community-selector-sync', this.handleCommunitySelectorSync)
  },
  watch: {
    '$route.fullPath'() {
      this.syncCommunitySelector()
    }
  },
  methods: {
    normalizePath(path) {
      return String(path || '').replace(/\/+$/, '')
    },
    getRouteCommunityId() {
      return this.$route.query?.communityId || this.$route.params?.communityId || ''
    },
    syncCommunitySelector() {
      const routeCommunityId = this.getRouteCommunityId()
      if (this.isHomeRoute()) {
        this.communityId = routeCommunityId ? String(routeCommunityId) : ''
        return
      }
      const storedCommunityId = getPropertyId()
      const nextCommunityId = String(routeCommunityId || storedCommunityId || '')
      this.communityId = nextCommunityId
      if (nextCommunityId) {
        setPropertyId(nextCommunityId)
      }
    },
    isSystemManagementRoute() {
      const activeMenu = this.normalizePath(this.$route.meta?.activeMenu)
      const currentPath = this.normalizePath(this.$route.path)
      return [currentPath, activeMenu].some((path) => path === '/system' || path.startsWith('/system/') || path.startsWith('/dict/'))
    },
    isCommunityMapRoute() {
      const currentPath = this.normalizePath(this.$route.path)
      return currentPath.startsWith('/property/community/map/')
    },
    isHomeRoute() {
      const currentPath = this.normalizePath(this.$route.path)
      return currentPath === '/index' || currentPath === ''
    },
    isCommunityInfoRoute() {
      const activeMenu = this.normalizePath(this.$route.meta?.activeMenu)
      const currentPath = this.normalizePath(this.$route.path)
      const paths = [currentPath, activeMenu]
      // 小区信息列表页不需要全局小区下拉框。
      return paths.some((path) => path === '/communityAsset/community' || path === '/property/community' || /\/(communityAsset|property)\/community$/.test(path))
        && !this.isCommunityMapRoute()
    },
    handleCommunitySelectorSync(event) {
      const nextCommunityId = String(event?.detail?.communityId || '')
      setPropertyId(nextCommunityId)
      this.communityId = nextCommunityId
    },
    selectedCommunity(value) {
      const nextCommunityId = String(value || '')
      this.communityId = nextCommunityId
      if (!nextCommunityId) {
        setPropertyId('')
        if (this.isHomeRoute()) {
          window.dispatchEvent(new CustomEvent('community-map-change', {
            detail: {
              communityId: ''
            }
          }))
        }
        return
      }
      setPropertyId(nextCommunityId)
      if (this.isCommunityMapRoute()) {
        this.$router.push({
          path: `/property/community/map/${nextCommunityId}`
        })
        return
      }
      if (this.isHomeRoute()) {
        window.dispatchEvent(new CustomEvent('community-map-change', {
          detail: {
            communityId: nextCommunityId
          }
        }))
        return
      }
      const nextQuery = {
        ...this.$route.query,
        communityId: nextCommunityId
      }
      if (String(this.$route.query?.communityId || '') === nextCommunityId) {
        this.reload?.()
        return
      }
      this.$router.replace({
        path: this.$route.path,
        query: nextQuery
      }).then(() => {
        this.reload?.()
      })
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          window.localStorage.clear()
          location.href = '#/login'
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: rgba(49, 37, 32, 0.72);
  border-bottom: 1px solid rgba(251, 228, 222, 0.12);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.20);
  backdrop-filter: blur(18px) saturate(125%);
  -webkit-backdrop-filter: blur(18px) saturate(125%);

  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(236, 121, 123, 0.42), transparent);
    opacity: 0.75;
    pointer-events: none;
  }

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s, color 0.3s;
    -webkit-tap-highlight-color: transparent;
    color: rgba(255, 255, 255, 0.78);

    &:hover {
      background: rgba(236, 121, 123, 0.14);
      color: #ffffff;
    }
  }

  .breadcrumb-container {
    float: left;
  }

  :deep(.el-breadcrumb__inner),
  :deep(.el-breadcrumb__inner a) {
    color: rgba(251, 228, 222, 0.92) !important;
  }

  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: rgba(251, 228, 222, 0.92) !important;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: rgba(255, 255, 255, 0.72);
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s, color 0.3s;

        &:hover {
          background: rgba(236, 121, 123, 0.14);
          color: #ffffff;
        }
      }
    }

    .community-selector-container {
      width: 220px;
      margin-right: 12px;
      vertical-align: middle;

      :deep(.el-select__wrapper) {
        min-height: 34px;
        background: rgba(255, 255, 255, 0.055);
        border: 1px solid rgba(251, 228, 222, 0.12);
        box-shadow: none;
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 12px;
          border: 1px solid rgba(251, 228, 222, 0.22);
          box-shadow: 0 8px 18px rgba(0, 0, 0, 0.22);
        }

        .el-icon-caret-bottom,
        .caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
          color: rgba(251, 228, 222, 0.78);
        }
      }
    }
  }
}
</style>
