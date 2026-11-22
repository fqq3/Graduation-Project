<template>
  <el-icon v-if="iconComponent" v-bind="iconAttrs">
    <component :is="iconComponent" />
  </el-icon>
</template>

<script>
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { legacyIcons } from '@/plugins/legacy-el-icons'

const legacyIconAliases = {
  'caret-bottom': ['ArrowDown'],
  'caret-top': ['ArrowUp'],
  'caret-left': ['ArrowLeft'],
  'caret-right': ['ArrowRight'],
  'circle-plus': ['CirclePlusFilled', 'CirclePlus'],
  'circle-plus-outline': ['CirclePlus'],
  'circle-check': ['CircleCheckFilled', 'CircleCheck'],
  'circle-close': ['CircleCloseFilled', 'CircleClose'],
  'circle-close-filled': ['CircleCloseFilled'],
  'question': ['QuestionFilled'],
  'info': ['InfoFilled'],
  'success': ['SuccessFilled'],
  'error': ['CircleCloseFilled', 'CircleClose'],
  'warning': ['WarningFilled', 'Warning'],
  'warning-outline': ['WarnTriangleFilled', 'WarningFilled', 'Warning'],
  'phone-outline': ['Phone'],
  'picture-outline': ['Picture'],
  'user-solid': ['UserFilled', 'User'],
  'camera-solid': ['CameraFilled', 'Camera'],
  'video-camera-solid': ['VideoCameraFilled', 'VideoCamera'],
  'more-outline': ['MoreFilled', 'More'],
  'star-on': ['StarFilled', 'Star'],
  'star-off': ['Star'],
  'copy-document': ['CopyDocument', 'DocumentCopy'],
  'document-copy': ['DocumentCopy'],
  'delete-solid': ['DeleteFilled', 'Delete'],
  'mobile': ['Cellphone', 'Phone'],
  'time': ['Timer', 'Clock'],
  'thumb': ['Pointer'],
  's-fold': ['Fold'],
  's-operation': ['Operation'],
  's-tools': ['Tools'],
  'upload2': ['Upload']
}

function toPascalCase(value) {
  return String(value || '')
    .split('-')
    .filter(Boolean)
    .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
    .join('')
}

function resolveByName(name) {
  const normalizedName = String(name || '')
  const baseName = normalizedName.replace(/^el-icon-/, '')
  const candidates = [
    ...(legacyIconAliases[baseName] || []),
    toPascalCase(baseName)
  ]

  if (baseName.startsWith('s-')) {
    candidates.push(toPascalCase(baseName.slice(2)))
  }

  if (baseName.endsWith('-solid')) {
    candidates.push(`${toPascalCase(baseName.replace(/-solid$/, ''))}Filled`)
  }

  if (baseName.endsWith('-outline')) {
    const strippedName = toPascalCase(baseName.replace(/-outline$/, ''))
    candidates.push(strippedName)
    candidates.push(`${strippedName}Filled`)
  }

  const resolvedName = [...new Set(candidates)].find((candidate) => ElementPlusIconsVue[candidate])
  return resolvedName ? ElementPlusIconsVue[resolvedName] : ElementPlusIconsVue.QuestionFilled
}

function toKebabCase(value) {
  return String(value || '')
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/_/g, '-')
    .toLowerCase()
}

function toLegacyClassName(name) {
  const normalizedName = String(name || '').trim()
  if (!normalizedName) {
    return ''
  }
  if (normalizedName.startsWith('el-icon-')) {
    return normalizedName
  }
  return `el-icon-${toKebabCase(normalizedName)}`
}

export default {
  name: 'LegacyElIcon',
  inheritAttrs: false,
  props: {
    name: {
      type: String,
      required: true
    }
  },
  computed: {
    iconAttrs() {
      const attrs = { ...this.$attrs }
      const classes = [attrs.class, 'legacy-el-icon', toLegacyClassName(this.name)].filter(Boolean)
      delete attrs.class
      return {
        ...attrs,
        class: classes
      }
    },
    iconComponent() {
      if (legacyIcons[this.name]) {
        return legacyIcons[this.name]
      }
      if (legacyIcons[`el-icon-${this.name}`]) {
        return legacyIcons[`el-icon-${this.name}`]
      }
      return resolveByName(this.name)
    }
  }
}
</script>
