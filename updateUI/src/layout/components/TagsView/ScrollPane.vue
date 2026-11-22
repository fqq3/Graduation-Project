<template>
  <el-scrollbar
    ref="scrollContainer"
    :vertical="false"
    class="scroll-container"
    @wheel.prevent="handleScroll"
  >
    <slot />
  </el-scrollbar>
</template>

<script>
const tagAndTagSpacing = 4

export default {
  name: 'ScrollPane',
  computed: {
    scrollWrapper() {
      return this.$refs.scrollContainer?.wrapRef || this.$refs.scrollContainer?.$refs?.wrap
    }
  },
  mounted() {
    this.scrollWrapper?.addEventListener('scroll', this.emitScroll, true)
  },
  beforeUnmount() {
    this.scrollWrapper?.removeEventListener('scroll', this.emitScroll, true)
  },
  methods: {
    handleScroll(event) {
      const scrollWrapper = this.scrollWrapper
      if (!scrollWrapper) {
        return
      }

      const eventDelta = event.wheelDelta || -event.deltaY * 40
      scrollWrapper.scrollLeft += eventDelta / 4
    },
    emitScroll() {
      this.$emit('scroll')
    },
    moveToTarget(currentTag) {
      const scrollbar = this.$refs.scrollContainer
      const container = scrollbar?.$el || scrollbar?.wrapRef?.parentElement
      const scrollWrapper = this.scrollWrapper
      const tagList = this.$parent?.$refs?.tag || []

      if (!container || !scrollWrapper || tagList.length === 0) {
        return
      }

      const containerWidth = container.offsetWidth
      const firstTag = tagList[0]
      const lastTag = tagList[tagList.length - 1]

      if (firstTag === currentTag) {
        scrollWrapper.scrollLeft = 0
        return
      }

      if (lastTag === currentTag) {
        scrollWrapper.scrollLeft = scrollWrapper.scrollWidth - containerWidth
        return
      }

      const currentIndex = tagList.findIndex((item) => item === currentTag)
      const prevTag = tagList[currentIndex - 1]
      const nextTag = tagList[currentIndex + 1]
      const nextTagEl = nextTag?.$el || nextTag
      const prevTagEl = prevTag?.$el || prevTag

      if (!nextTagEl || !prevTagEl) {
        return
      }

      const afterNextTagOffsetLeft = nextTagEl.offsetLeft + nextTagEl.offsetWidth + tagAndTagSpacing
      const beforePrevTagOffsetLeft = prevTagEl.offsetLeft - tagAndTagSpacing

      if (afterNextTagOffsetLeft > scrollWrapper.scrollLeft + containerWidth) {
        scrollWrapper.scrollLeft = afterNextTagOffsetLeft - containerWidth
      } else if (beforePrevTagOffsetLeft < scrollWrapper.scrollLeft) {
        scrollWrapper.scrollLeft = beforePrevTagOffsetLeft
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.scroll-container :deep(.el-scrollbar__bar) {
  bottom: 0;
}

.scroll-container :deep(.el-scrollbar__wrap) {
  height: 49px;
}
</style>
