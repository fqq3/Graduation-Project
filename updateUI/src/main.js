import { createApp } from 'vue'
import ElementPlus, { ElLoading, ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import Cookies from 'js-cookie'
import 'element-plus/dist/index.css'
import 'normalize.css/normalize.css'
import 'virtual:svg-icons-register'
import '@/assets/styles/element-variables.scss'
import '@/assets/styles/index.scss'
import '@/assets/styles/jeethink.scss'
import '@/assets/styles/digital-twin-theme.scss'
import App from './App.vue'
import router from './router'
import pinia from './stores'
import legacyStore from './stores/legacy-store'
import permission from './directive/permission'
import './permission'
import { registerSvgIcons } from './plugins/svg-icons'
import { registerLegacyElIcons } from './plugins/legacy-el-icons'
import { getDicts } from '@/api/system/dict/data'
import { getConfigKey } from '@/api/system/config'
import {
  parseTime,
  resetForm,
  addDateRange,
  selectDictLabel,
  selectDictLabels,
  download,
  handleTree
} from '@/utils/jeethink'
import { handleTreeAll } from '@/utils/mashibing'
import LegacyElIcon from '@/components/LegacyElIcon/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import { elementPlusConfig, syncLegacySize } from '@/utils/element-plus-size'

const app = createApp(App)
// 从 Cookie 读取上次保存的组件尺寸，默认为 'medium'
const initialSize = Cookies.get('size') || 'medium'

// 调用同步尺寸方法，确保旧版组件也使用相同的尺寸
syncLegacySize(initialSize)

//.use(plugin)：安装插件（如 Vue Router、Pinia）。
app.use(pinia)
app.use(router)
app.use(permission)
app.use(ElementPlus, elementPlusConfig)

registerSvgIcons(app)         // 注册所有 SVG 图标为全局组件
registerLegacyElIcons(app)    // 注册旧版 Element UI 图标为全局组件

// .component(name, component)：注册全局组件。
app.component('Pagination', Pagination)
app.component('LegacyElIcon', LegacyElIcon)
app.component('RightToolbar', RightToolbar)
// .directive(name, directive)：注册全局指令。

app.config.globalProperties.$store = legacyStore
app.config.globalProperties.getDicts = getDicts
app.config.globalProperties.getConfigKey = getConfigKey
app.config.globalProperties.parseTime = parseTime
app.config.globalProperties.resetForm = resetForm
app.config.globalProperties.addDateRange = addDateRange
app.config.globalProperties.selectDictLabel = selectDictLabel
app.config.globalProperties.selectDictLabels = selectDictLabels
app.config.globalProperties.download = download
app.config.globalProperties.handleTree = handleTree
app.config.globalProperties.handleTreeAll = handleTreeAll
// 封装成功提示消息（自动关闭 + 成功类型）
app.config.globalProperties.msgSuccess = (msg) =>
  ElMessage({ showClose: true, message: msg, type: 'success' })
// 封装错误提示消息
app.config.globalProperties.msgError = (msg) =>
  ElMessage({ showClose: true, message: msg, type: 'error' })
// 封装普通信息提示
app.config.globalProperties.msgInfo = (msg) => ElMessage.info(msg)
app.config.globalProperties.$message = ElMessage
app.config.globalProperties.$confirm = ElMessageBox.confirm
app.config.globalProperties.$alert = ElMessageBox.alert
app.config.globalProperties.$notify = ElNotification
app.config.globalProperties.$loading = ElLoading.service
// 将 Vue 应用渲染到 index.html 中 id="app" 的容器内
app.mount('#app')
