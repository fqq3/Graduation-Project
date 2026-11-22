import axios from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getPropertyId, getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

async function getLegacyStore() {
  const module = await import('@/stores/legacy-store')
  return module.default
}

service.interceptors.request.use(
  (config) => {
    const isToken = (config.headers || {}).isToken === false
    if (getToken() && !isToken) {
      config.headers.Authorization = `Bearer ${getToken()}`
    }
    const propertyId = getPropertyId()
    if (propertyId) {
      config.headers.propertyId = propertyId
    } else if (config.headers && Object.prototype.hasOwnProperty.call(config.headers, 'propertyId')) {
      delete config.headers.propertyId
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  async (res) => {
    const rawCode = res.data?.code
    const code = Number.isFinite(Number(rawCode)) ? Number(rawCode) : 200
    const msg = errorCode[String(code)] || res.data.msg || res.data.message || errorCode.default

    if (code === 401) {
      try {
        await ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (error) {
        return Promise.reject(new Error(msg))
      }

      const legacyStore = await getLegacyStore()
      try {
        await legacyStore.dispatch('LogOut')
      } catch (error) {
        await legacyStore.dispatch('FedLogOut')
      }
      window.location.href = '#/login'
      return Promise.reject(new Error(msg))
    }

    if (code === 500) {
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }

    if (code !== 200) {
      ElNotification.error({ title: msg })
      return Promise.reject(new Error(msg))
    }

    return res.data
  },
  (error) => {
    let { message } = error
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = `系统接口${message.slice(-3)}异常`
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
