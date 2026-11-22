<template>
  <div :class="{ show: show }" class="header-search">
    <svg-icon class-name="search-icon" icon-class="search" @click.stop="click" />
    <el-select
      ref="headerSearchSelect"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="Search"
      class="header-search-select"
      @change="change"
    >
      <el-option v-for="item in options" :key="item.path" :value="item" :label="item.title.join(' > ')" />
    </el-select>
  </div>
</template>

<script>
import path from 'path-browserify'

function normalizeSearchText(value) {
  return String(value || '').trim().toLowerCase()
}

function getContainScore(text, keyword, weight) {
  const index = text.indexOf(keyword)
  if (index === -1) {
    return 0
  }
  return Math.max(weight - index, 1)
}

function getFuzzyScore(text, keyword, weight) {
  let cursor = -1
  let gapPenalty = 0

  for (const char of keyword) {
    const nextIndex = text.indexOf(char, cursor + 1)
    if (nextIndex === -1) {
      return 0
    }
    gapPenalty += nextIndex - cursor - 1
    cursor = nextIndex
  }

  return Math.max(weight - gapPenalty, 1)
}

function rankSearchItem(item, keyword) {
  const title = normalizeSearchText(item.title.join(' > '))
  const routePath = normalizeSearchText(item.path)

  const exactTitleScore = getContainScore(title, keyword, 120)
  const exactPathScore = getContainScore(routePath, keyword, 90)
  const fuzzyTitleScore = getFuzzyScore(title, keyword, 60)
  const fuzzyPathScore = getFuzzyScore(routePath, keyword, 40)

  return Math.max(exactTitleScore, exactPathScore, fuzzyTitleScore, fuzzyPathScore)
}

export default {
  name: 'HeaderSearch',
  data() {
    return {
      search: '',
      options: [],
      searchPool: [],
      show: false
    }
  },
  computed: {
    routes() {
      return this.$store.getters.permission_routes
    }
  },
  watch: {
    routes() {
      this.searchPool = this.generateRoutes(this.routes)
    },
    searchPool() {
      this.options = []
    },
    show(value) {
      if (value) {
        document.body.addEventListener('click', this.close)
      } else {
        document.body.removeEventListener('click', this.close)
      }
    }
  },
  mounted() {
    this.searchPool = this.generateRoutes(this.routes)
  },
  beforeUnmount() {
    document.body.removeEventListener('click', this.close)
  },
  methods: {
    click() {
      this.show = !this.show
      if (this.show) {
        this.$refs.headerSearchSelect?.focus()
      }
    },
    close() {
      this.$refs.headerSearchSelect?.blur()
      this.options = []
      this.show = false
    },
    change(val) {
      if (this.ishttp(val.path)) {
        window.open(val.path, '_blank')
      } else {
        this.$router.push(val.path)
      }
      this.search = ''
      this.options = []
      this.$nextTick(() => {
        this.show = false
      })
    },
    generateRoutes(routes, basePath = '/', prefixTitle = []) {
      let res = []

      for (const router of routes) {
        if (router.hidden) {
          continue
        }

        const data = {
          path: !this.ishttp(router.path) ? path.resolve(basePath, router.path) : router.path,
          title: [...prefixTitle]
        }

        if (router.meta && router.meta.title) {
          data.title = [...data.title, router.meta.title]
          if (router.redirect !== 'noRedirect') {
            res.push(data)
          }
        }

        if (router.children) {
          const tempRoutes = this.generateRoutes(router.children, data.path, data.title)
          if (tempRoutes.length >= 1) {
            res = [...res, ...tempRoutes]
          }
        }
      }
      return res
    },
    querySearch(query) {
      const keyword = normalizeSearchText(query)
      if (!keyword) {
        this.options = []
        return
      }
      this.options = this.searchPool
        .map((item) => ({
          item,
          score: rankSearchItem(item, keyword)
        }))
        .filter(({ score }) => score > 0)
        .sort((a, b) => b.score - a.score)
        .map(({ item }) => item)
    },
    ishttp(url) {
      return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
    }
  }
}
</script>

<style lang="scss" scoped>
.header-search {
  font-size: 0 !important;

  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;

    :deep(.el-input__inner) {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-left: 10px;
    }
  }
}
</style>
