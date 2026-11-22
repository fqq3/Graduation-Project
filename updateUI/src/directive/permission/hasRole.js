import legacyStore from '@/stores/legacy-store'

function validateRole(value) {
  const roles = legacyStore.getters.roles || []
  const superAdmin = 'admin'
  return roles.some((role) => role === superAdmin || value.includes(role))
}

export default {
  mounted(el, binding) {
    const { value } = binding
    if (Array.isArray(value) && value.length > 0) {
      if (!validateRole(value)) {
        el.parentNode?.removeChild(el)
      }
      return
    }
    throw new Error('请设置角色权限标签值')
  }
}
