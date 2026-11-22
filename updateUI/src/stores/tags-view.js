import { defineStore } from 'pinia'

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: []
  }),
  actions: {
    addVisitedView(view) {
      if (this.visitedViews.some((item) => item.path === view.path)) return
      this.visitedViews.push({
        ...view,
        title: view.meta?.title || 'no-name'
      })
    },
    addCachedView(view) {
      if (this.cachedViews.includes(view.name)) return
      if (!view.meta?.noCache) {
        this.cachedViews.push(view.name)
      }
    },
    addView(view) {
      this.addVisitedView(view)
      this.addCachedView(view)
    },
    delVisitedView(view) {
      this.visitedViews = this.visitedViews.filter((item) => item.path !== view.path)
      return [...this.visitedViews]
    },
    delCachedView(view) {
      this.cachedViews = this.cachedViews.filter((item) => item !== view.name)
      return [...this.cachedViews]
    },
    async delView(view) {
      await this.delVisitedView(view)
      await this.delCachedView(view)
      return {
        visitedViews: [...this.visitedViews],
        cachedViews: [...this.cachedViews]
      }
    },
    delOthersVisitedViews(view) {
      this.visitedViews = this.visitedViews.filter(
        (item) => item.meta?.affix || item.path === view.path
      )
      return [...this.visitedViews]
    },
    delOthersCachedViews(view) {
      const index = this.cachedViews.indexOf(view.name)
      this.cachedViews = index > -1 ? this.cachedViews.slice(index, index + 1) : []
      return [...this.cachedViews]
    },
    async delOthersViews(view) {
      await this.delOthersVisitedViews(view)
      await this.delOthersCachedViews(view)
      return {
        visitedViews: [...this.visitedViews],
        cachedViews: [...this.cachedViews]
      }
    },
    delAllVisitedViews() {
      this.visitedViews = this.visitedViews.filter((item) => item.meta?.affix)
      return [...this.visitedViews]
    },
    delAllCachedViews() {
      this.cachedViews = []
      return [...this.cachedViews]
    },
    async delAllViews() {
      await this.delAllVisitedViews()
      await this.delAllCachedViews()
      return {
        visitedViews: [...this.visitedViews],
        cachedViews: [...this.cachedViews]
      }
    },
    updateVisitedView(view) {
      const match = this.visitedViews.find((item) => item.path === view.path)
      if (match) {
        Object.assign(match, view)
      }
    }
  }
})
