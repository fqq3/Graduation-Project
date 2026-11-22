<template>
  <component :is="tag" ref="containerRef" class="compat-draggable" v-bind="$attrs">
    <slot />
  </component>
</template>

<script>
import Sortable from 'sortablejs'

const elementContextMap = new WeakMap()
const dragState = {
  source: null,
  sourceItem: null,
  movedItem: null,
  clonedItem: null,
  isClone: false
}

function resetDragState() {
  dragState.source = null
  dragState.sourceItem = null
  dragState.movedItem = null
  dragState.clonedItem = null
  dragState.isClone = false
}

export default {
  name: 'CompatDraggable',
  inheritAttrs: false,
  props: {
    tag: {
      type: String,
      default: 'div'
    },
    list: {
      type: Array,
      default: null
    },
    value: {
      type: Array,
      default: null
    },
    modelValue: {
      type: Array,
      default: null
    },
    group: {
      type: [String, Object],
      default: null
    },
    animation: {
      type: Number,
      default: 0
    },
    itemKey: {
      type: [String, Function],
      default: null
    },
    clone: {
      type: Function,
      default(item) {
        return item
      }
    },
    sort: {
      type: Boolean,
      default: true
    },
    handle: {
      type: String,
      default: ''
    },
    draggable: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['input', 'update:modelValue', 'start', 'add', 'remove', 'update', 'change', 'end'],
  mounted() {
    this.initSortable()
  },
  beforeUnmount() {
    this.destroySortable()
  },
  methods: {
    getContainerElement() {
      return this.$refs.containerRef?.$el || this.$refs.containerRef || null
    },
    getList() {
      if (Array.isArray(this.list)) {
        return this.list
      }
      if (Array.isArray(this.modelValue)) {
        return this.modelValue
      }
      if (Array.isArray(this.value)) {
        return this.value
      }
      return null
    },
    syncList(list) {
      if (!Array.isArray(list)) {
        return
      }
      const snapshot = list.slice()
      this.$emit('input', snapshot)
      this.$emit('update:modelValue', snapshot)
    },
    resolvePullMode() {
      if (typeof this.group === 'string') {
        return true
      }
      return this.group?.pull ?? true
    },
    cloneItem(item) {
      return this.clone ? this.clone(item) : item
    },
    initSortable() {
      const container = this.getContainerElement()
      if (!container) {
        return
      }

      elementContextMap.set(container, this)

      this.sortable = Sortable.create(container, {
        group: this.group,
        animation: this.animation,
        sort: this.sort,
        handle: this.handle || undefined,
        draggable: this.draggable || undefined,
        disabled: this.disabled,
        onStart: (event) => this.handleStart(event),
        onAdd: (event) => this.handleAdd(event),
        onRemove: (event) => this.handleRemove(event),
        onUpdate: (event) => this.handleUpdate(event),
        onEnd: (event) => this.handleEnd(event)
      })
    },
    destroySortable() {
      const container = this.getContainerElement()
      if (container) {
        elementContextMap.delete(container)
      }
      if (this.sortable) {
        this.sortable.destroy()
        this.sortable = null
      }
    },
    handleStart(event) {
      const list = this.getList()
      dragState.source = this
      dragState.sourceItem = Array.isArray(list) ? list[event.oldIndex] : null
      dragState.isClone = this.resolvePullMode() === 'clone'
      dragState.movedItem = null
      dragState.clonedItem = dragState.isClone ? this.cloneItem(dragState.sourceItem) : null
      this.$emit('start', event)
    },
    handleAdd(event) {
      const list = this.getList()
      if (!Array.isArray(list)) {
        return
      }

      const source = dragState.source || elementContextMap.get(event.from)
      let element = dragState.isClone ? dragState.clonedItem : dragState.movedItem

      if (element === null || element === undefined) {
        const sourceList = source?.getList?.()
        const sourceItem = Array.isArray(sourceList) ? sourceList[event.oldIndex] : undefined
        element = dragState.isClone ? source?.cloneItem?.(sourceItem) ?? sourceItem : sourceItem
      }

      list.splice(event.newIndex, 0, element)
      this.syncList(list)
      this.$emit('add', event)
      this.$emit('change', {
        added: {
          element,
          newIndex: event.newIndex
        }
      })
    },
    handleRemove(event) {
      const list = this.getList()
      if (!Array.isArray(list) || dragState.isClone) {
        return
      }

      const removed = list.splice(event.oldIndex, 1)[0]
      dragState.movedItem = removed
      this.syncList(list)
      this.$emit('remove', event)
      this.$emit('change', {
        removed: {
          element: removed,
          oldIndex: event.oldIndex
        }
      })
    },
    handleUpdate(event) {
      const list = this.getList()
      if (!Array.isArray(list)) {
        return
      }

      const moved = list.splice(event.oldIndex, 1)[0]
      list.splice(event.newIndex, 0, moved)
      this.syncList(list)
      this.$emit('update', event)
      this.$emit('change', {
        moved: {
          element: moved,
          oldIndex: event.oldIndex,
          newIndex: event.newIndex
        }
      })
    },
    handleEnd(event) {
      this.$emit('end', event)
      window.setTimeout(resetDragState, 0)
    }
  }
}
</script>
