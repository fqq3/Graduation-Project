<template>
  <div class="community-map-page">
    <section class="map-hero">
      <div>
        <p class="eyebrow">Community Map</p>
        <h1>{{ overview.communityName || '小区地图' }}</h1>
        <div class="hero-meta">
          <span>{{ overview.address || '暂无详细地址' }}</span>
          <span>{{ overview.communityCode || '暂无小区编码' }}</span>
          <span>已录入小区：{{ nearbyOverviews.length }} 个</span>
        </div>
      </div>
      <el-button plain @click="$router.back()">返回</el-button>
    </section>

    <el-alert
      v-if="mapMessage"
      :title="mapMessage"
      type="warning"
      show-icon
      :closable="false"
      class="map-alert"
    />
    <el-alert
      v-if="overviewErrorMessage"
      :title="overviewErrorMessage"
      type="error"
      show-icon
      :closable="false"
      class="map-alert"
    />

    <section class="map-stage">
      <div ref="mapContainer" class="map-container">
        <div v-if="showMapPlaceholder" class="map-placeholder">
          <strong>{{ mapPlaceholderTitle }}</strong>
          <span>{{ mapPlaceholderText }}</span>
        </div>
      </div>

      <aside v-if="cardVisible" class="overview-card" :class="['status-' + overallStatus, { 'is-active': markerActive }]">
        <div class="card-head">
          <div>
            <p>运行概览</p>
            <h2>当前小区状态</h2>
          </div>
          <span class="status-pill">{{ overview.statusText || '正常' }}</span>
        </div>

        <div class="metric-grid">
          <div class="metric-item">
            <span>楼栋数</span>
            <strong>{{ overview.buildingCount || 0 }}</strong>
          </div>
          <div class="metric-item">
            <span>房屋数</span>
            <strong>{{ overview.roomCount || 0 }}</strong>
          </div>
          <div class="metric-item">
            <span>车位数</span>
            <strong>{{ overview.parkingCount || 0 }}</strong>
          </div>
          <button class="metric-item metric-action" :class="'metric-' + deviceStatus" type="button" :disabled="!canGoDevice" @click="goDevice">
            <span>设备</span>
            <strong>{{ overview.deviceTotal || 0 }}</strong>
            <em>异常 {{ overview.deviceError || 0 }}</em>
          </button>
          <button class="metric-item metric-action" :class="'metric-' + repairStatus" type="button" :disabled="!canGoRepair" @click="goRepair">
            <span>报修</span>
            <strong>{{ overview.repairCount || 0 }}</strong>
            <em>待处理 {{ overview.repairPending || 0 }}</em>
          </button>
          <button class="metric-item metric-action" :class="'metric-' + complaintStatus" type="button" :disabled="!canGoSuggest" @click="goSuggest">
            <span>投诉建议</span>
            <strong>{{ overview.complaintCount || 0 }}</strong>
            <em>查看明细</em>
          </button>
        </div>

        <div class="rate-row">
          <div>
            <span>入住率</span>
            <strong>{{ formatRate(overview.occupancyRate) }}</strong>
          </div>
          <div :class="'rate-' + paymentStatus">
            <span>缴费率</span>
            <strong>{{ formatRate(overview.paymentRate, '暂无账单') }}</strong>
          </div>
          <div>
            <span>今日访客</span>
            <strong>{{ overview.visitorCount || 0 }}</strong>
          </div>
        </div>

        <div class="alerts">
          <p>异常提示</p>
          <ul v-if="statusAlerts.length">
            <li v-for="item in statusAlerts" :key="item">{{ item }}</li>
          </ul>
          <span v-else>暂无异常提示</span>
        </div>
      </aside>
    </section>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getCommunityOverview, getNearbyCommunityOverviews } from '@/api/property/community'
import legacyStore from '@/stores/legacy-store'

const AMAP_SCRIPT_ID = 'amap-js-api'
const MARKER_COLORS = {
  normal: '#F9CADA',
  warning: '#F59E0B',
  danger: '#F56C6C'
}
const MARKER_SIZE = 24
const ACTIVE_MARKER_SIZE = Math.round(MARKER_SIZE * 1.2)
const AMAP_LOAD_TIMEOUT = 12000
const BUSINESS_PERMISSIONS = {
  device: 'community:device:list',
  repair: 'community:repairOrder:list',
  suggest: 'community:analysis:complaintWordCloud'
}

function loadAmap(key, securityJsCode) {
  if (!key) {
    return Promise.reject(new Error('AMap key is empty'))
  }
  if (securityJsCode) {
    window._AMapSecurityConfig = {
      securityJsCode
    }
  }
  if (window.AMap) {
    return Promise.resolve(window.AMap)
  }
  if (window.__communityAmapLoader) {
    return window.__communityAmapLoader
  }

  window.__communityAmapLoader = new Promise((resolve, reject) => {
    let timer = null
    const cleanup = () => {
      if (timer) {
        window.clearTimeout(timer)
        timer = null
      }
    }
    const resolveAmap = () => {
      cleanup()
      if (window.AMap) {
        resolve(window.AMap)
        return
      }
      window.__communityAmapLoader = null
      reject(new Error('AMap script loaded without AMap object'))
    }
    const rejectAmap = (error) => {
      cleanup()
      window.__communityAmapLoader = null
      reject(error)
    }
    const existingScript = document.getElementById(AMAP_SCRIPT_ID)
    if (existingScript) {
      existingScript.addEventListener('load', resolveAmap, { once: true })
      existingScript.addEventListener('error', rejectAmap, { once: true })
      timer = window.setTimeout(() => rejectAmap(new Error('AMap script load timeout')), AMAP_LOAD_TIMEOUT)
      return
    }

    const script = document.createElement('script')
    script.id = AMAP_SCRIPT_ID
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${encodeURIComponent(key)}`
    script.async = true
    script.onload = resolveAmap
    script.onerror = (error) => {
      cleanup()
      window.__communityAmapLoader = null
      script.remove()
      reject(error)
    }
    document.head.appendChild(script)
    timer = window.setTimeout(() => {
      script.remove()
      rejectAmap(new Error('AMap script load timeout'))
    }, AMAP_LOAD_TIMEOUT)
  })

  return window.__communityAmapLoader
}

export default {
  name: 'CommunityMap',
  data() {
    return {
      loading: false,
      overviewLoaded: false,
      overview: {},
      nearbyOverviews: [],
      map: null,
      markers: [],
      infoWindow: null,
      AMap: null,
      amapReady: false,
      mapLoadError: '',
      overviewErrorMessage: '',
      cardVisible: true,
      markerActive: false
    }
  },
  computed: {
    communityId() {
      return this.$route.params.communityId
    },
    deviceStatus() {
      return this.overview.deviceStatus || 'normal'
    },
    repairStatus() {
      return this.overview.repairStatus || 'normal'
    },
    complaintStatus() {
      return this.overview.complaintStatus || 'normal'
    },
    paymentStatus() {
      return this.overview.paymentStatus || 'normal'
    },
    overallStatus() {
      const statuses = [
        this.deviceStatus,
        this.repairStatus,
        this.complaintStatus,
        this.paymentStatus
      ]
      if (statuses.includes('danger')) {
        return 'danger'
      }
      if (statuses.includes('warning')) {
        return 'warning'
      }
      return 'normal'
    },
    statusAlerts() {
      const alerts = []
      if (this.deviceStatus === 'danger') {
        alerts.push(`异常设备达到 ${this.overview.deviceError || 0} 台`)
      } else if (this.deviceStatus === 'warning') {
        alerts.push(`存在 ${this.overview.deviceError || 0} 台异常设备`)
      }
      if (this.repairStatus === 'danger') {
        alerts.push(`待处理报修达到 ${this.overview.repairPending || 0} 单`)
      } else if (this.repairStatus === 'warning') {
        alerts.push(`存在 ${this.overview.repairPending || 0} 单待处理报修`)
      }
      if (this.complaintStatus === 'danger') {
        alerts.push(`今日投诉建议达到 ${this.overview.complaintCount || 0} 条`)
      } else if (this.complaintStatus === 'warning') {
        alerts.push(`今日存在 ${this.overview.complaintCount || 0} 条投诉建议`)
      }
      if (this.paymentStatus === 'danger') {
        alerts.push('缴费率低于 60%')
      } else if (this.paymentStatus === 'warning') {
        alerts.push('缴费率低于 80%')
      }
      return alerts
    },
    canGoDevice() {
      return this.hasPermission(BUSINESS_PERMISSIONS.device)
    },
    canGoRepair() {
      return this.hasPermission(BUSINESS_PERMISSIONS.repair)
    },
    canGoSuggest() {
      return this.hasPermission(BUSINESS_PERMISSIONS.suggest)
    },
    coordinateIssue() {
      const rawLongitude = this.overview.longitude
      const rawLatitude = this.overview.latitude
      if (rawLongitude === null || rawLongitude === undefined || rawLatitude === null || rawLatitude === undefined) {
        return 'missing'
      }
      if (String(rawLongitude).trim() === '' || String(rawLatitude).trim() === '') {
        return 'missing'
      }
      const longitude = Number(rawLongitude)
      const latitude = Number(rawLatitude)
      if (!Number.isFinite(longitude) || !Number.isFinite(latitude)) {
        return 'invalid'
      }
      if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
        return 'invalid'
      }
      return null
    },
    position() {
      if (this.coordinateIssue !== null) {
        return null
      }
      return [Number(this.overview.longitude), Number(this.overview.latitude)]
    },
    coordinateText() {
      if (this.position) {
        return `${this.overview.longitude}, ${this.overview.latitude}`
      }
      if (this.coordinateIssue === 'invalid') {
        return '经纬度格式错误'
      }
      return '缺少经纬度'
    },
    mapMessage() {
      if (this.overviewErrorMessage) {
        return ''
      }
      if (this.overviewLoaded && this.coordinateIssue === 'missing') {
        return '该小区缺少经纬度信息'
      }
      if (this.overviewLoaded && this.coordinateIssue === 'invalid') {
        return '经纬度格式错误，无法加载地图'
      }
      if (this.mapLoadError) {
        return this.mapLoadError
      }
      return ''
    },
    showMapPlaceholder() {
      return !this.position || !this.amapReady || Boolean(this.mapLoadError)
    },
    mapPlaceholderTitle() {
      if (!this.overviewLoaded) {
        return '概览加载中'
      }
      if (!this.position) {
        return this.coordinateIssue === 'invalid' ? '经纬度格式错误' : '缺少经纬度'
      }
      if (this.mapLoadError) {
        return '地图加载失败'
      }
      return '地图加载中'
    },
    mapPlaceholderText() {
      if (!this.overviewLoaded) {
        return '正在加载小区概览数据。'
      }
      if (!this.position) {
        return this.coordinateIssue === 'invalid'
          ? '经纬度格式错误，无法加载地图'
          : '请先在小区信息中维护经度和纬度。'
      }
      if (this.mapLoadError) {
        return this.mapLoadError
      }
      return '正在加载在线地图与小区 Marker。'
    }
  },
  mounted() {
    this.syncCurrentCommunityId()
    this.fetchOverview()
  },
  watch: {
    communityId(newValue, oldValue) {
      if (newValue === oldValue) {
        return
      }
      this.syncCurrentCommunityId()
      this.overview = {}
      this.nearbyOverviews = []
      this.overviewLoaded = false
      this.overviewErrorMessage = ''
      this.mapLoadError = ''
      this.clearMarkers()
      this.fetchOverview()
    }
  },
  beforeUnmount() {
    this.destroyMap()
  },
  methods: {
    syncCurrentCommunityId() {
      if (!this.communityId) {
        return
      }
      window.localStorage.setItem('propertyId', this.communityId)
    },
    fetchOverview() {
      if (!this.communityId) {
        return
      }
      this.loading = true
      this.overviewErrorMessage = ''
      getCommunityOverview(this.communityId)
        .then((response) => {
          if (!response.data || !response.data.communityId) {
            this.overview = {}
            this.nearbyOverviews = []
            this.overviewLoaded = true
            this.overviewErrorMessage = '小区信息不存在'
            this.destroyMap()
            return
          }
          this.updateOverview(response.data)
          this.fetchNearbyOverviews()
        })
        .catch(() => {
          this.overviewLoaded = true
          this.overviewErrorMessage = '小区地图概览加载失败，请确认登录状态和接口服务。'
        })
        .finally(() => {
          this.loading = false
        })
    },
    updateOverview(data) {
      this.overview = data || {}
      this.overviewLoaded = true
      this.upsertNearbyOverview(this.overview)
      this.$nextTick(() => {
        this.renderMap()
      })
    },
    fetchNearbyOverviews() {
      if (!this.communityId || !this.position) {
        this.nearbyOverviews = [this.overview].filter((item) => item && item.communityId)
        this.$nextTick(() => {
          this.renderMap()
        })
        return
      }
      getNearbyCommunityOverviews(this.communityId, 5)
        .then((response) => {
          const rows = Array.isArray(response.data) ? response.data : []
          if (rows.length) {
            this.nearbyOverviews = this.normalizeNearbyRows(rows)
            this.$nextTick(() => {
              this.renderMap()
            })
            return
          }
          this.nearbyOverviews = [this.overview].filter((item) => item && item.communityId)
          this.$nextTick(() => {
            this.renderMap()
          })
        })
        .catch(() => {
          this.nearbyOverviews = [this.overview].filter((item) => item && item.communityId)
          this.$nextTick(() => {
            this.renderMap()
          })
        })
    },
    normalizeNearbyRows(rows) {
      const result = []
      const seen = new Set()
      ;(rows || []).forEach((row) => {
        const normalized = {
          ...row,
          statusText: row.statusText || '正常',
          deviceStatus: row.deviceStatus || 'normal',
          repairStatus: row.repairStatus || 'normal',
          complaintStatus: row.complaintStatus || 'normal',
          paymentStatus: row.paymentStatus || 'normal'
        }
        if (!normalized || !normalized.communityId || !this.getPosition(normalized)) {
          return
        }
        const key = String(normalized.communityId)
        if (seen.has(key)) {
          return
        }
        seen.add(key)
        result.push(normalized)
      })
      return result.length ? result : [this.overview].filter((item) => item && item.communityId)
    },
    upsertNearbyOverview(overview) {
      if (!overview || !overview.communityId) {
        return
      }
      const index = this.nearbyOverviews.findIndex((item) => String(item.communityId) === String(overview.communityId))
      if (index >= 0) {
        this.nearbyOverviews.splice(index, 1, { ...this.nearbyOverviews[index], ...overview })
        return
      }
      this.nearbyOverviews.push(overview)
    },
    renderMap() {
      if (!this.position) {
        this.destroyMap()
        return
      }

      const amapKey = import.meta.env.VITE_AMAP_KEY
      const amapSecurityJsCode = import.meta.env.VITE_AMAP_SECURITY_JS_CODE
      if (!amapKey && !window.AMap) {
        this.destroyMap()
        this.mapLoadError = '地图 Key 未配置，无法加载在线地图'
        return
      }

      loadAmap(amapKey, amapSecurityJsCode)
        .then((AMap) => {
          this.$nextTick(() => {
            try {
              this.AMap = AMap
              this.amapReady = true
              this.mapLoadError = ''
              if (!this.map) {
                this.map = new AMap.Map(this.$refs.mapContainer, {
                  zoom: 15,
                  center: this.position,
                  viewMode: '2D'
                })
              }
              this.renderMarkers(AMap)
            } catch (error) {
              console.warn('[CommunityMap] AMap initialize failed', error)
              this.destroyMap()
              this.mapLoadError = '地图加载失败，请稍后重试'
            }
          })
        })
        .catch((error) => {
          console.warn('[CommunityMap] AMap SDK load failed', error)
          this.destroyMap()
          this.mapLoadError = '地图加载失败，请稍后重试'
        })
    },
    renderMarkers(AMap) {
      this.clearMarkers()
      const markerData = this.nearbyOverviews.filter((item) => this.getPosition(item))
      this.markers = markerData.map((item) => {
        const active = String(item.communityId) === String(this.overview.communityId)
        const size = active ? ACTIVE_MARKER_SIZE : MARKER_SIZE
        const marker = new AMap.Marker({
          position: this.getPosition(item),
          content: this.createMarkerContent(item, active),
          offset: new AMap.Pixel(-size / 2, -size)
        })
        marker.setMap(this.map)
        marker.on('click', () => {
          this.selectCommunityMarker(item)
        })
        return marker
      })

      if (this.markers.length > 1) {
        this.map.setFitView(this.markers, false, [80, 80, 80, 80])
        return
      }
      if (this.position) {
        this.map.setCenter(this.position)
      }
    },
    createMarkerContent(overview, active) {
      const status = this.getOverviewStatus(overview)
      const color = MARKER_COLORS[status] || MARKER_COLORS.normal
      const size = active ? ACTIVE_MARKER_SIZE : MARKER_SIZE
      const innerSize = Math.round(size / 3)
      const innerMargin = Math.round((size - innerSize) / 2 - 2)
      return `<div style="width:${size}px;height:${size}px;border-radius:50% 50% 50% 0;transform:rotate(-45deg);background:${color};box-shadow:0 12px 24px rgba(15,23,42,.28);border:3px solid #fff;"><span style="display:block;width:${innerSize}px;height:${innerSize}px;border-radius:50%;background:#fff;margin:${innerMargin}px auto 0;opacity:.95;"></span></div>`
    },
    selectCommunityMarker(overview) {
      this.cardVisible = true
      this.markerActive = true
      if (String(overview.communityId) !== String(this.overview.communityId)) {
        window.localStorage.setItem('propertyId', overview.communityId)
        this.$router.push({
          path: `/property/community/map/${overview.communityId}`
        })
        return
      }
      this.openInfoWindow(this.AMap, overview)
      this.renderMarkers(this.AMap)
    },
    clearMarkers() {
      if (this.infoWindow) {
        this.infoWindow.close()
      }
      this.markers.forEach((marker) => marker.setMap(null))
      this.markers = []
    },
    destroyMap() {
      this.clearMarkers()
      if (this.map) {
        this.map.destroy()
        this.map = null
      }
      this.infoWindow = null
      this.AMap = null
      this.amapReady = false
    },
    openInfoWindow(AMap, targetOverview = this.overview) {
      if (!this.map || !this.position) {
        return
      }
      const targetPosition = this.getPosition(targetOverview) || this.position
      const content = `
        <div style="min-width:220px;padding:4px 2px;color:#0f172a;">
          <strong style="display:block;font-size:15px;margin-bottom:6px;">${this.escapeHtml(targetOverview.communityName || '-')}</strong>
          <div style="font-size:12px;line-height:1.7;color:#475569;">${this.escapeHtml(targetOverview.address || '-')}</div>
          <div style="margin-top:8px;font-size:12px;color:${MARKER_COLORS[this.getOverviewStatus(targetOverview)] || MARKER_COLORS.normal};">状态：${this.escapeHtml(targetOverview.statusText || '正常')}</div>
        </div>
      `
      if (!this.infoWindow) {
        this.infoWindow = new AMap.InfoWindow({
          offset: new AMap.Pixel(0, -28)
        })
      }
      this.infoWindow.setContent(content)
      this.infoWindow.open(this.map, targetPosition)
    },
    goDevice() {
      this.goBusiness('/communityAsset/device', BUSINESS_PERMISSIONS.device)
    },
    goRepair() {
      this.goBusiness('/serviceManage/repair', BUSINESS_PERMISSIONS.repair)
    },
    goSuggest() {
      if (!this.overview.communityId) {
        return
      }
      if (!this.hasPermission(BUSINESS_PERMISSIONS.suggest)) {
        ElMessage.warning('暂无投诉建议分析页面访问权限')
        return
      }
      this.$router.push({
        path: '/serviceManage/complaintWordCloud',
        query: {
          communityId: this.overview.communityId
        }
      })
    },
    goBusiness(path, permission) {
      if (!this.overview.communityId) {
        return
      }
      if (!this.hasPermission(permission)) {
        ElMessage.warning('暂无目标页面访问权限')
        return
      }
      const query = {
        communityId: this.overview.communityId
      }
      this.$router.push({
        path: this.resolveBusinessPath(path, query),
        query
      })
    },
    resolveBusinessPath(path, query) {
      if (!Array.isArray(path)) {
        return path
      }
      return path.find((item) => this.isRealRoute(item, query)) || path[0]
    },
    isRealRoute(path, query = {}) {
      const matched = this.$router.resolve({ path, query }).matched || []
      return matched.some((route) => {
        const routePath = String(route.path || '')
        return route.name !== 'DynamicNotFound' && routePath !== '/404' && !routePath.includes(':pathMatch')
      })
    },
    formatRate(value, fallback = '暂无') {
      if (value === null || value === undefined) {
        return fallback
      }
      const rate = Number(value)
      if (!Number.isFinite(rate)) {
        return fallback
      }
      return `${(rate * 100).toFixed(0)}%`
    },
    getPosition(overview) {
      if (!overview) {
        return null
      }
      const longitude = Number(overview.longitude)
      const latitude = Number(overview.latitude)
      if (!Number.isFinite(longitude) || !Number.isFinite(latitude)) {
        return null
      }
      if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
        return null
      }
      return [longitude, latitude]
    },
    getOverviewStatus(overview) {
      const statuses = [
        overview?.deviceStatus || 'normal',
        overview?.repairStatus || 'normal',
        overview?.complaintStatus || 'normal',
        overview?.paymentStatus || 'normal'
      ]
      if (statuses.includes('danger')) {
        return 'danger'
      }
      if (statuses.includes('warning')) {
        return 'warning'
      }
      return 'normal'
    },
    escapeHtml(value) {
      return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;')
    },
    hasPermission(permission) {
      const permissions = legacyStore.getters.permissions || []
      return permissions.includes('*:*:*') || permissions.includes(permission)
    }
  }
}
</script>

<style scoped lang="scss">
.community-map-page {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background:
    radial-gradient(circle at 8% 0%, rgba(14, 165, 233, 0.18), transparent 32%),
    linear-gradient(135deg, #eef7f4 0%, #f8fafc 46%, #f4efe6 100%);
}

.map-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 28px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);

  h1 {
    margin: 4px 0 10px;
    font-size: 32px;
    line-height: 1.15;
    color: #0f172a;
  }
}

.eyebrow {
  margin: 0;
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  span {
    padding: 6px 12px;
    border-radius: 999px;
    background: #eef6f2;
    color: #475569;
    font-size: 13px;
  }
}

.map-alert {
  margin: 16px 0 0;
}

.map-stage {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: 20px;
  margin-top: 20px;
}

.map-container {
  position: relative;
  min-height: 640px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 30px;
  background:
    linear-gradient(135deg, rgba(15, 118, 110, 0.12), rgba(14, 165, 233, 0.08)),
    repeating-linear-gradient(45deg, rgba(15, 23, 42, 0.04) 0 1px, transparent 1px 18px);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
}

.map-placeholder {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #334155;
  text-align: center;
  background: rgba(248, 250, 252, 0.68);

  strong {
    color: #0f172a;
    font-size: 24px;
  }

  span {
    max-width: 360px;
    line-height: 1.7;
  }
}

.overview-card {
  position: sticky;
  top: 16px;
  align-self: start;
  padding: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-top: 6px solid #409EFF;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.overview-card.status-warning {
  border-top-color: #FF7AA2;
}

.overview-card.status-danger {
  border-top-color: #F56C6C;
}

.overview-card.is-active {
  box-shadow: 0 28px 70px rgba(64, 158, 255, 0.18);
  transform: translateY(-2px);
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;

  p {
    margin: 0 0 6px;
    color: #64748b;
  }

  h2 {
    margin: 0;
    color: #0f172a;
    font-size: 24px;
  }
}

.status-pill {
  flex: none;
  padding: 6px 12px;
  border-radius: 999px;
  background: #ecfdf5;
  color: #047857;
  font-size: 13px;
  font-weight: 700;
}

.status-normal .status-pill {
  background: #E8F4FF;
  color: #409EFF;
}

.status-warning .status-pill {
  background: #FFE4EC;
  color: #d63384;
}

.status-danger .status-pill {
  background: #FEE2E2;
  color: #B91C1C;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.metric-item {
  min-height: 92px;
  padding: 12px;
  border: 0;
  border-radius: 18px;
  background: #eff6ff;
  color: #1e3a8a;
  text-align: left;

  span,
  em {
    display: block;
    font-size: 12px;
    font-style: normal;
    opacity: 0.78;
  }

  strong {
    display: block;
    margin: 8px 0 2px;
    font-size: 26px;
    line-height: 1;
  }
}

.metric-action {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 14px 28px rgba(37, 99, 235, 0.14);
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.48;
  }

  &:disabled:hover {
    transform: none;
    box-shadow: none;
  }
}

.metric-normal,
.rate-normal {
  background: #E8F4FF;
  color: #1677ff;
}

.metric-warning,
.rate-warning {
  background: #FFE4EC;
  color: #d63384;
}

.metric-danger,
.rate-danger {
  background: #F56C6C;
  color: #ffffff;
}

.rate-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 16px;

  div {
    padding: 12px;
    border-radius: 18px;
    background: #f1f5f9;
  }

  span {
    display: block;
    color: #64748b;
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 6px;
    color: #0f172a;
    font-size: 20px;
  }
}

.rate-row .rate-normal {
  background: #E8F4FF;
  color: #1677ff;
}

.rate-row .rate-warning {
  background: #FFE4EC;
  color: #d63384;
}

.rate-row .rate-danger {
  background: #F56C6C;
  color: #ffffff;
}

.rate-row .rate-normal span,
.rate-row .rate-normal strong {
  color: #1677ff;
}

.rate-row .rate-warning span,
.rate-row .rate-warning strong {
  color: #d63384;
}

.rate-row .rate-danger span,
.rate-row .rate-danger strong {
  color: #ffffff;
}

.alerts {
  margin-top: 18px;
  padding: 16px;
  border-radius: 20px;
  background: #fff7ed;
  color: #9a3412;

  p {
    margin: 0 0 10px;
    font-weight: 700;
  }

  ul {
    margin: 0;
    padding-left: 18px;
    line-height: 1.8;
  }

  span {
    color: #64748b;
  }
}

@media (max-width: 1180px) {
  .map-stage {
    grid-template-columns: 1fr;
  }

  .overview-card {
    position: static;
  }
}

@media (max-width: 720px) {
  .community-map-page {
    padding: 12px;
  }

  .map-hero {
    align-items: flex-start;
    flex-direction: column;
    padding: 20px;

    h1 {
      font-size: 26px;
    }
  }

  .map-container {
    min-height: 420px;
  }

  .overview-card {
    padding: 18px;
  }

  .metric-grid,
  .rate-row {
    grid-template-columns: 1fr;
  }
}
</style>
