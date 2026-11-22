import { defineStore } from 'pinia'
import defaultSettings from '@/settings'

const { showSettings, tagsView, fixedHeader, sidebarLogo, sideTheme } = defaultSettings

function resolveTheme() {
  if (typeof window === 'undefined') {
    return '#EC797B'
  }

  const theme = window
    .getComputedStyle(document.documentElement)
    .getPropertyValue('--el-color-primary')
    .trim()

  return theme || '#EC797B'
}

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    theme: resolveTheme(),
    sideTheme,
    showSettings,
    tagsView,
    fixedHeader,
    sidebarLogo
  }),
  actions: {
    changeSetting({ key, value }) {
      if (Object.prototype.hasOwnProperty.call(this, key)) {
        this[key] = value
      }
    }
  }
})
