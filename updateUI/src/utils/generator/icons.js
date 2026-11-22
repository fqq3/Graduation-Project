import { defineComponent, h } from 'vue'
import LegacyElIcon from '@/components/LegacyElIcon/index.vue'

export const generatorLegacyIconPresets = Object.freeze({
  pictureCardAdd: 'plus',
  uploadTrigger: 'upload'
})

export const generatorBuilderIcons = Object.freeze({
  link: 'link',
  search: 'search',
  choose: 'thumb',
  drag: 's-operation',
  remove: 'remove-outline',
  addOutline: 'circle-plus-outline',
  close: 'close',
  treeAppend: 'plus',
  treeDelete: 'delete',
  copyDocument: 'copy-document',
  delete: 'delete'
})

export const generatorLegacyIconFields = Object.freeze([
  'icon',
  'prefix-icon',
  'suffix-icon'
])

export function toGeneratorLegacyIcon(name) {
  const normalizedName = String(name || '').trim()
  if (!normalizedName) {
    return ''
  }
  if (normalizedName.startsWith('el-icon-')) {
    return normalizedName
  }
  return `el-icon-${normalizedName}`
}

export function stripGeneratorLegacyIcon(name) {
  return String(name || '').trim().replace(/^el-icon-/, '')
}

export function normalizeGeneratorIconList(iconList = []) {
  return iconList
    .map((name) => toGeneratorLegacyIcon(name))
    .filter(Boolean)
}

export function normalizeGeneratorIconFields(config = {}) {
  if (!config || typeof config !== 'object') {
    return config
  }

  const normalizedConfig = { ...config }
  generatorLegacyIconFields.forEach((field) => {
    if (field in normalizedConfig) {
      normalizedConfig[field] = toGeneratorLegacyIcon(normalizedConfig[field])
    }
  })

  if (Array.isArray(normalizedConfig.children)) {
    normalizedConfig.children = normalizedConfig.children.map((child) => normalizeGeneratorIconFields(child))
  }

  return normalizedConfig
}

export function getGeneratorLegacyIconProps(name, attrs = {}) {
  const normalizedName = toGeneratorLegacyIcon(name)
  if (!normalizedName) {
    return null
  }
  return {
    name: normalizedName,
    ...attrs
  }
}

export const GeneratorLegacyIcon = defineComponent({
  name: 'GeneratorLegacyIcon',
  inheritAttrs: false,
  props: {
    name: {
      type: String,
      default: ''
    }
  },
  setup(props, { attrs }) {
    return () => {
      const iconProps = getGeneratorLegacyIconProps(props.name, attrs)
      return iconProps ? h(LegacyElIcon, iconProps) : null
    }
  }
})

export function buildGeneratorLegacyIconVNode(name, attrs = {}) {
  const normalizedName = toGeneratorLegacyIcon(name)
  return normalizedName
    ? h(GeneratorLegacyIcon, {
      name: normalizedName,
      ...attrs
    })
    : null
}

export function buildGeneratorLegacyIconMarkup(name, className = '') {
  const normalizedName = toGeneratorLegacyIcon(name)
  if (!normalizedName) {
    return ''
  }
  const iconClass = className ? ` class="${className}"` : ''
  return `<LegacyElIcon name="${normalizedName}"${iconClass} />`
}

export function buildGeneratorLegacyIconCode(name) {
  const normalizedName = toGeneratorLegacyIcon(name)
  if (!normalizedName) {
    return ''
  }
  return `<legacy-el-icon name="${normalizedName}" />`
}

export function filterGeneratorIcons(iconList, keyword) {
  const normalizedKeyword = String(keyword || '').trim().toLowerCase()
  if (!normalizedKeyword) {
    return iconList
  }

  return iconList.filter((iconName) => {
    const legacyName = toGeneratorLegacyIcon(iconName).toLowerCase()
    const rawName = stripGeneratorLegacyIcon(iconName).toLowerCase()
    return legacyName.includes(normalizedKeyword) || rawName.includes(normalizedKeyword)
  })
}
