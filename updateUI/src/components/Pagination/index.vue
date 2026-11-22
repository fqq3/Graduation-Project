<template>
  <div :class="{ hidden }" class="pagination-container">
    <el-pagination
      v-bind="$attrs"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :background="background"
      :layout="layout"
      :page-sizes="pageSizes"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { scrollTo } from '@/utils/scroll-to'

export default {
  name: 'Pagination',
  inheritAttrs: false,
  props: {
    total: {
      type: Number,
      required: true
    },
    page: {
      type: Number,
      default: 1
    },
    limit: {
      type: Number,
      default: 20
    },
    pageSizes: {
      type: Array,
      default() {
        return [10, 20, 30, 50]
      }
    },
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper'
    },
    background: {
      type: Boolean,
      default: true
    },
    autoScroll: {
      type: Boolean,
      default: true
    },
    hidden: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:page', 'update:limit', 'pagination'],
  computed: {
    currentPage: {
      get() {
        return this.page
      },
      set(value) {
        this.$emit('update:page', value)
      }
    },
    pageSize: {
      get() {
        return this.limit
      },
      set(value) {
        this.$emit('update:limit', value)
      }
    }
  },
  methods: {
    handleSizeChange(value) {
      this.$emit('pagination', { page: this.currentPage, limit: value })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    },
    handleCurrentChange(value) {
      this.$emit('pagination', { page: value, limit: this.pageSize })
      if (this.autoScroll) {
        scrollTo(0, 800)
      }
    }
  }
}
</script>

<style scoped>
.pagination-container {
  background: transparent;
  padding: 26px 16px 18px;
}

.pagination-container.hidden {
  display: none;
}
</style>
