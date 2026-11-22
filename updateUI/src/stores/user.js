import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import defaultAvatar from '@/assets/image/default.gif'

function clearUserState(store) {
  store.token = ''
  store.name = ''
  store.avatar = ''
  store.introduction = ''
  store.roles = []
  store.permissions = []
  window.sessionStorage.removeItem('async-routes')
  removeToken()
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    introduction: '',
    roles: [],
    permissions: []
  }),
  actions: {
    async Login(userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      const res = await login(username, password, code, uuid)
      const token = res.token || res.data?.token || res.data?.data?.token || ''
      if (!token) { 
        throw new Error(res.msg || res.message || '登录成功，但未获取到令牌')
      }
      setToken(token)
      this.token = token
    },
    async GetInfo() {
      const res = await getInfo()
      const user = res.user || {}
      this.avatar = user.avatar === '' || user.avatar == null ? defaultAvatar : user.avatar
      this.roles = res.roles && res.roles.length > 0 ? res.roles : ['ROLE_DEFAULT']
      this.permissions = res.permissions || []
      this.name = user.userName || ''
      this.introduction = user.nickName || user.userName || ''
      return res
    },
    async LogOut() {
      try {
        await logout()
      } finally {
        clearUserState(this)
      }
    },
    async FedLogOut() {
      clearUserState(this)
    },
    async ResetToken() {
      clearUserState(this)
    },
    async resetToken() {
      await this.ResetToken()
    }
  }
})
