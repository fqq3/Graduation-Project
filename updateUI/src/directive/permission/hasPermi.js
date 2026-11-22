import legacyStore from '@/stores/legacy-store'

function validatePermission(value) {
  const permissions = legacyStore.getters.permissions || []
  const allPermission = '*:*:*'
  return permissions.some((permission) => permission === allPermission || value.includes(permission))
}

export default {
  mounted(el, binding) {
    const { value } = binding
    if (Array.isArray(value) && value.length > 0) {
      if (!validatePermission(value)) {
        el.parentNode?.removeChild(el)
      }
      return
    }
    throw new Error('请设置操作权限标签值')
  }
}
