<template>
  <el-color-picker
    v-model="theme"
    :predefine="predefineColors"
    class="theme-picker"
    popper-class="theme-picker-dropdown"
    @change="handleChange"
  />
</template>

<script>
export default {
  emits: ['change'],
  data() {
    return {
      theme: '',
      predefineColors: ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d']
    }
  },
  computed: {
    defaultTheme() {
      return this.$store.state.settings.theme
    }
  },
  watch: {
    defaultTheme: {
      handler(val) {
        this.theme = val
      },
      immediate: true
    }
  },

  methods: {
    handleChange(val) {
      if (typeof val !== 'string' || !val) {
        return
      }
      this.$emit('change', val)
    }
  }
}
</script>

<style scoped>
.theme-picker-dropdown {
  z-index: 99999 !important;
}

.theme-picker :deep(.el-color-picker__trigger) {
  height: 26px;
  width: 26px;
  padding: 2px;
}
</style>
