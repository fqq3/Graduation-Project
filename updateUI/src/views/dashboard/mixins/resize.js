import { debounce } from '@/utils'

export default {
  data() {
    return {
      $_sidebarElm: null,
      $_resizeHandler: null
    }
  },
  mounted() {
    this.initListener()
  },
  activated() {
    if (!this.$_resizeHandler) {
      this.initListener()
    }

    this.resize()
  },
  beforeUnmount() {
    this.destroyListener()
  },
  deactivated() {
    this.destroyListener()
  },
  methods: {
    $_sidebarResizeHandler(event) {
      if (event.propertyName === 'width') {
        this.$_resizeHandler()
      }
    },
    initListener() {
      this.$_resizeHandler = debounce(() => {
        this.resize()
      }, 100)
      window.addEventListener('resize', this.$_resizeHandler)

      this.$_sidebarElm = document.getElementsByClassName('sidebar-container')[0]
      this.$_sidebarElm?.addEventListener('transitionend', this.$_sidebarResizeHandler)
    },
    destroyListener() {
      window.removeEventListener('resize', this.$_resizeHandler)
      this.$_resizeHandler = null
      this.$_sidebarElm?.removeEventListener('transitionend', this.$_sidebarResizeHandler)
    },
    resize() {
      this.chart?.resize()
    }
  }
}
