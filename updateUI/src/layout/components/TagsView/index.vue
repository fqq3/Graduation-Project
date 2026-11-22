<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll">
      <span
        v-for="tag in visitedViews"
        :key="tag.path"
        ref="tag"
        :data-path="tag.path"
        :data-full-path="tag.fullPath"
        :class="isActive(tag) ? 'active' : ''"
        :style="activeStyle(tag)"
        class="tags-view-item"
        @click="goTag(tag)"
        @mouseup="handleMouseUp(tag, $event)"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        {{ tag.title }}
        <el-icon
          v-if="!isAffix(tag)"
          class="tags-view-close el-icon-close"
          @click.prevent.stop="closeSelectedTag(tag)"
        >
          <Close />
        </el-icon>
      </span>
    </scroll-pane>
    <ul v-show="visible" :style="{ left: `${left}px`, top: `${top}px` }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">刷新页面</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">关闭当前</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags(selectedTag)">关闭所有</li>
    </ul>
  </div>
</template>

<script>
import { Close } from '@element-plus/icons-vue'
import path from 'path-browserify'
import ScrollPane from './ScrollPane.vue'
import { usePermissionStore } from '@/stores/permission'
import { useSettingsStore } from '@/stores/settings'
import { useTagsViewStore } from '@/stores/tags-view'

export default {
  name: 'TagsView',
  components: { Close, ScrollPane },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: []
    }
  },
  computed: {
    visitedViews() {
      return useTagsViewStore().visitedViews
    },
    routes() {
      return usePermissionStore().routes
    },
    theme() {
      return useSettingsStore().theme
    }
  },
  watch: {
    $route() {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible(value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
      }
    }
  },
  mounted() {
    this.initTags()
    this.addTags()
  },
  methods: {
    goTag(tag) {
      this.$router.push({ path: tag.path, query: tag.query, fullPath: tag.fullPath })
    },
    isActive(route) {
      return route.path === this.$route.path
    },
    activeStyle(tag) {
      if (!this.isActive(tag)) return {}
      return {
        backgroundColor: '#EC797B',
        borderColor: '#EC797B'
      }
    },
    isAffix(tag) {
      return tag.meta && tag.meta.affix
    },
    filterAffixTags(routes, basePath = '/') {
      let tags = []
      routes.forEach((route) => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          tags = tags.concat(this.filterAffixTags(route.children, route.path))
        }
      })
      return tags
    },
    initTags() {
      this.affixTags = this.filterAffixTags(this.routes)
      this.affixTags.forEach((tag) => {
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      })
    },
    addTags() {
      if (this.$route.name) {
        this.$store.dispatch('tagsView/addView', this.$route)
      }
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag || []
      this.$nextTick(() => {
        const current =
          tags.find?.(
            (tag) =>
              tag?.dataset?.path === this.$route.path ||
              tag?.dataset?.fullPath === this.$route.fullPath
          ) || tags[0]
        if (current) {
          this.$refs.scrollPane?.moveToTarget?.(current)
          if (current.dataset?.fullPath !== this.$route.fullPath) {
            this.$store.dispatch('tagsView/updateVisitedView', this.$route)
          }
        }
      })
    },
    handleMouseUp(tag, event) {
      if (event.button === 1 && !this.isAffix(tag)) {
        this.closeSelectedTag(tag)
      }
    },
    refreshSelectedTag(view) {
      this.$store.dispatch('tagsView/delCachedView', view).then(() => {
        this.$router.replace({ path: `/redirect${view.fullPath}` })
      })
    },
    closeSelectedTag(view) {
      this.$store.dispatch('tagsView/delView', view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view)
        }
      })
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag)
      this.$store.dispatch('tagsView/delOthersViews', this.selectedTag).then(() => {
        this.moveToCurrentTag()
      })
    },
    closeAllTags(view) {
      this.$store.dispatch('tagsView/delAllViews').then(({ visitedViews }) => {
        if (this.affixTags.some((tag) => tag.path === this.$route.path)) return
        this.toLastView(visitedViews, view)
      })
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else if (['Dashboard', 'Index', '首页'].includes(view?.name)) {
        this.$router.replace({ path: `/redirect${view.fullPath}` })
      } else {
        this.$router.push('/')
      }
    },
    openMenu(tag, event) {
      const menuMinWidth = 105
      const offsetLeft = this.$el.getBoundingClientRect().left
      const offsetWidth = this.$el.offsetWidth
      const maxLeft = offsetWidth - menuMinWidth
      const left = event.clientX - offsetLeft + 15

      this.left = left > maxLeft ? maxLeft : left
      this.top = event.clientY
      this.visible = true
      this.selectedTag = tag
    },
    closeMenu() {
      this.visible = false
    },
    handleScroll() {
      this.closeMenu()
    }
  }
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: rgba(49, 37, 32, 0.64);
  border-bottom: 1px solid rgba(251, 228, 222, 0.10);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(16px) saturate(125%);
  -webkit-backdrop-filter: blur(16px) saturate(125%);

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid rgba(251, 228, 222, 0.12);
      border-radius: 999px;
      color: rgba(255, 255, 255, 0.64);
      background: rgba(255, 255, 255, 0.045);
      padding: 0 10px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      transition: background 0.24s ease, color 0.24s ease, border-color 0.24s ease, box-shadow 0.24s ease;

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &:hover {
        color: #ffffff;
        border-color: rgba(236, 121, 123, 0.32);
        background: rgba(236, 121, 123, 0.10);
      }

      &.active {
        color: #fff;
        box-shadow: 0 8px 18px rgba(236, 121, 123, 0.22);

        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 7px;
          height: 7px;
          border-radius: 50%;
          position: relative;
          margin-right: 4px;
          box-shadow: 0 0 10px rgba(251, 228, 222, 0.72);
        }
      }
    }
  }

  .contextmenu {
    margin: 0;
    background: rgba(49, 37, 32, 0.94);
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 6px 0;
    border: 1px solid rgba(251, 228, 222, 0.12);
    border-radius: 12px;
    font-size: 12px;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.74);
    box-shadow: 0 18px 44px rgba(0, 0, 0, 0.34);
    backdrop-filter: blur(18px) saturate(125%);
    -webkit-backdrop-filter: blur(18px) saturate(125%);

    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;

      &:hover {
        color: #ffffff;
        background: rgba(236, 121, 123, 0.16);
      }
    }
  }
}
</style>

<style lang="scss">
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close,
    .tags-view-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;

      svg {
        transform: scale(.6);
        display: inline-block;
      }

      &:hover {
        background-color: rgba(236, 121, 123, 0.55);
        color: #fff;
      }
    }
  }
}
</style>
