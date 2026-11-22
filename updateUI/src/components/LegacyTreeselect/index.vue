<template>
  <el-tree-select
    :model-value="modelValue"
    :data="normalizedOptions"
    :props="treeProps"
    :placeholder="placeholder"
    :disabled="disabled"
    :multiple="multiple"
    :clearable="clearable"
    check-strictly
    default-expand-all
    node-key="id"
    value-key="id"
    @update:modelValue="handleUpdate"
    @change="handleChange"
  />
</template>

<script>
function normalizeNodes(nodes = [], normalizer) {
  return nodes.map((node) => {
    const normalized = typeof normalizer === 'function' ? normalizer({ ...node }) : { ...node }
    const children = normalizeNodes(normalized.children || [], normalizer)
    const result = { ...normalized }

    if (children.length) {
      result.children = children
    } else {
      delete result.children
    }

    return result
  })
}

export default {
  name: 'LegacyTreeselect',
  props: {
    modelValue: {
      type: [String, Number, Array, Object],
      default: null
    },
    options: {
      type: Array,
      default: () => []
    },
    normalizer: {
      type: Function,
      default: null
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    multiple: {
      type: Boolean,
      default: false
    },
    clearable: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:modelValue', 'change', 'input'],
  computed: {
    normalizedOptions() {
      return normalizeNodes(this.options, this.normalizer)
    },
    treeProps() {
      return {
        value: 'id',
        label: 'label',
        children: 'children'
      }
    }
  },
  methods: {
    handleUpdate(value) {
      this.$emit('update:modelValue', value)
      this.$emit('input', value)
    },
    handleChange(value) {
      this.$emit('change', value)
    }
  }
}
</script>
