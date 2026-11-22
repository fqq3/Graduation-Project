<script>
export default {
  name: 'RightToolbar',
  inheritAttrs: false,
  props: {
    showSearch: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:showSearch', 'queryTable'],
  render() {
    return null
  }
}
</script>
