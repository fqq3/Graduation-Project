<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index === levelList.length - 1"
          class="no-redirect"
        >
          {{ item.meta.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import { compile } from 'path-to-regexp'

export default {
  name: 'Breadcrumb',
  data() {
    return {
      levelList: []
    }
  },
  watch: {
    $route(route) {
      if (route.path.startsWith('/redirect/')) {
        return
      }
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {
      let matched = this.$route.matched.filter((item) => item.meta && item.meta.title)
      const first = matched[0]

      if (!this.isDashboard(first)) {
        matched = [{ path: '/index', meta: { title: '首页' } }].concat(matched)
      }

      this.levelList = matched.filter((item) => item.meta && item.meta.breadcrumb !== false)
    },
    isDashboard(route) {
      const name = route?.name
      return typeof name === 'string' && ['首页', 'Index'].includes(name.trim())
    },
    pathCompile(path) {
      const toPath = compile(path)
      return toPath(this.$route.params)
    },
    handleLink(item) {
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
        return
      }
      this.$router.push(this.pathCompile(path))
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color:#FBE4DE;
    cursor: text;
  }

   .el-breadcrumb__inner {
    color: rgba(251, 228, 222, 0.72) !important;
    font-weight: 500;
  }

  .el-breadcrumb__separator {
    color: rgba(251, 228, 222, 0.38) !important;
  }

  .no-redirect {
    color: #FBE4DE !important;
  }

  a {
    color: rgba(251, 228, 222, 0.72) !important;

    &:hover {
      color: #EC797B !important;
    }
  }
}
</style>
