import { computed, ref } from 'vue'

const DEFAULT_LEGACY_SIZE = 'medium'
const LEGACY_TO_ELEMENT_PLUS_SIZE_MAP = Object.freeze({
  medium: 'default',
  mini: 'small'
})

const legacySize = ref(DEFAULT_LEGACY_SIZE)

export function normalizeLegacySize(size) {
  return size || DEFAULT_LEGACY_SIZE
}

export function toElementPlusSize(size) {
  const normalizedSize = normalizeLegacySize(size)
  return LEGACY_TO_ELEMENT_PLUS_SIZE_MAP[normalizedSize] || normalizedSize || 'default'
}

export function syncLegacySize(size) {
  legacySize.value = normalizeLegacySize(size)
}

export const elementPlusConfig = computed(() => ({
  size: toElementPlusSize(legacySize.value)
}))
