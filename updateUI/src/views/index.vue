<template>
  <div class="home-map-page">
    <section class="map-hero">
      <div>
        <h1>{{ selectedOverview ? selectedOverview.communityName : '社区地图总览' }}</h1>
        <div class="hero-meta">
          <template v-if="selectedOverview">
            <span>{{ selectedOverview.communityCode || '暂无小区编码' }}</span>
            <span>{{ selectedOverview.address || '暂无详细地址' }}</span>
          </template>
          <span v-else>点击小区查看运行详情</span>
        </div>
      </div>
      <el-button v-if="selectedOverview" plain class="overview-button" @click="backToOverview">总览</el-button>
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

    <section class="map-stage" :class="{ 'has-card': cardVisible && selectedOverview }">
      <div ref="mapContainer" class="map-container">
        <div v-if="showMapPlaceholder" class="map-placeholder">
          <strong>{{ mapPlaceholderTitle }}</strong>
          <span>{{ mapPlaceholderText }}</span>
        </div>
      </div>

      <aside v-if="cardVisible && selectedOverview" class="overview-card" :class="'status-' + selectedOverallStatus">
        <div class="card-head">
          <div>
            <p>运行概览</p>
            <h2>{{ selectedOverview.statusText || '正常' }}</h2>
          </div>
          <button type="button" class="card-close" @click="hideCard">收起</button>
        </div>

        <div class="metric-grid">
          <div class="metric-item">
            <span>楼栋数</span>
            <strong>{{ selectedOverview.buildingCount || 0 }}</strong>
          </div>
          <div class="metric-item">
            <span>房屋数</span>
            <strong>{{ selectedOverview.roomCount || 0 }}</strong>
          </div>
          <div class="metric-item">
            <span>车位数</span>
            <strong>{{ selectedOverview.parkingCount || 0 }}</strong>
          </div>
          <button class="metric-item metric-action" :class="'metric-' + deviceStatus" type="button" @click="goDevice">
            <span>设备</span>
            <strong>{{ selectedOverview.deviceTotal || 0 }}</strong>
            <em>异常 {{ selectedOverview.deviceError || 0 }}</em>
          </button>
          <button class="metric-item metric-action" :class="'metric-' + repairStatus" type="button" @click="goRepair">
            <span>报修</span>
            <strong>{{ selectedOverview.repairCount || 0 }}</strong>
            <em>待处理 {{ selectedOverview.repairPending || 0 }}</em>
          </button>
          <button class="metric-item metric-action" :class="'metric-' + complaintStatus" type="button" @click="goSuggest">
            <span>投诉建议/天</span>
            <strong>{{ selectedOverview.complaintCount || 0 }}</strong>
            <em>今日新增</em>
          </button>
        </div>

        <div class="rate-row">
          <div>
            <span>入住率</span>
            <strong>{{ formatRate(selectedOverview.occupancyRate) }}</strong>
          </div>
          <button class="rate-action" :class="'rate-' + paymentStatus" type="button" @click="goFeeBill">
            <span>缴费率</span>
            <strong>{{ formatRate(selectedOverview.paymentRate, '暂无账单') }}</strong>
          </button>
          <div>
            <span>今日访客</span>
            <strong>{{ selectedOverview.visitorCount || 0 }}</strong>
          </div>
        </div>

        <div class="alerts">
          <p>状态提示</p>
          <ul v-if="statusAlerts.length">
            <li v-for="item in statusAlerts" :key="item">{{ item }}</li>
          </ul>
          <span v-else>所有指标正常</span>
        </div>
      </aside>
    </section>

    <section class="ranking-card home-ranking-section">
      <div class="ranking-header">
        <div>
          <h3>小区运行综合评价排名</h3>
          <p>基于入住率、缴费率、设备健康度、报修健康度、投诉健康度和停车健康度进行 TOPSIS 综合评价。</p>
        </div>
        <strong class="ranking-header-badge">TOPSIS</strong>
      </div>

      <el-form :inline="true" :model="rankingQueryParams" class="analysis-filter ranking-filter">
        <el-form-item label="小区">
          <el-select v-model="rankingQueryParams.communityId" clearable filterable placeholder="全部小区" style="width: 220px">
            <el-option
              v-for="item in communityOptions"
              :key="String(item.communityId)"
              :label="item.communityName"
              :value="item.communityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="统计周期">
          <el-select v-model="rankingQueryParams.days" style="width: 150px">
            <el-option label="近 7 天" :value="7" />
            <el-option label="近 30 天" :value="30" />
            <el-option label="近 90 天" :value="90" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button class="el-button--cyan" @click="handleRankingQuery">查询</el-button>
          <el-button @click="resetRankingQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="rankingLoadError"
        :title="rankingLoadError"
        type="error"
        show-icon
        :closable="false"
        class="analysis-alert"
      />

      <el-skeleton v-if="rankingLoading" :rows="10" animated />
      <el-empty v-else-if="rankingLoaded && !communityRankings.length" description="当前筛选条件下暂无小区评价数据" />

      <template v-else-if="rankingLoaded">
        <section class="summary-grid">
          <article class="summary-card">
            <span>参与评价小区</span>
            <strong>{{ rankingSummary.total }}</strong>
          </article>
          <article class="summary-card">
            <span>平均得分</span>
            <strong>{{ rankingSummary.averageScore }}</strong>
          </article>
          <article class="summary-card status-good">
            <span>运行良好</span>
            <strong>{{ rankingSummary.good }}</strong>
          </article>
          <article class="summary-card status-warning">
            <span>轻度预警</span>
            <strong>{{ rankingSummary.warning }}</strong>
          </article>
          <article class="summary-card status-danger">
            <span>重点关注</span>
            <strong>{{ rankingSummary.danger }}</strong>
          </article>
        </section>

        <section class="chart-card">
          <div class="card-title">
            <span>TOPSIS 综合得分柱状图（最高 3 个 / 最低 3 个）</span>
            <strong>{{ rankingChartRows.length }}</strong>
          </div>
          <div v-if="rankingChartRows.length" class="topsis-bar-chart">
            <article
              v-for="item in rankingChartRows"
              :key="item._chartId"
              class="topsis-bar-item"
            >
              <span class="topsis-bar-value">{{ formatRankingScore(item.scorePercent) }}</span>
              <div class="topsis-bar-track">
                <div class="topsis-bar-fill" :style="{ height: getRankingBarHeight(item.scorePercent) }"></div>
              </div>
              <span class="topsis-bar-name" :title="item.communityName || '未命名小区'">{{ item.communityName || '未命名小区' }}</span>
              <span class="topsis-bar-rank">第 {{ item.rank || item._displayRank }} 名</span>
            </article>
          </div>
          <el-empty v-else description="当前暂无可展示的综合评分数据" />
        </section>

        <section class="table-card">
          <div class="card-title">
            <span>评价明细</span>
            <strong>{{ communityRankings.length }}</strong>
          </div>
          <el-table :data="communityRankings" class="ranking-table" height="520">
            <el-table-column prop="rank" label="排名" width="70" align="center" />
            <el-table-column label="小区" min-width="150" show-overflow-tooltip>
              <template #default="scope">
                <el-button link class="ranking-community-link" @click="goCommunityRankingMap(scope.row)">
                  {{ scope.row.communityName || '未命名小区' }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="综合得分" width="110" align="center">
              <template #default="scope">{{ formatRankingScore(scope.row.scorePercent) }}</template>
            </el-table-column>
            <el-table-column label="等级" width="110" align="center">
              <template #default="scope">
                <el-tag :type="getRankingLevelTag(scope.row.level)" effect="dark">{{ scope.row.levelName || '暂无' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="入住率" width="90" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.occupancyRate) }}</template>
            </el-table-column>
            <el-table-column label="缴费率" width="90" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.paymentRate) }}</template>
            </el-table-column>
            <el-table-column label="设备健康" width="100" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.deviceHealth) }}</template>
            </el-table-column>
            <el-table-column label="报修健康" width="100" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.repairHealth) }}</template>
            </el-table-column>
            <el-table-column label="投诉健康" width="100" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.complaintHealth) }}</template>
            </el-table-column>
            <el-table-column label="停车健康" width="100" align="center">
              <template #default="scope">{{ formatRankingRate(scope.row.parkingHealth) }}</template>
            </el-table-column>
            <el-table-column label="主要风险" min-width="210" show-overflow-tooltip>
              <template #default="scope">{{ formatRankingRisks(scope.row.mainRisks) }}</template>
            </el-table-column>
          </el-table>
        </section>
      </template>
    </section>

  </div>
</template>

<script>
import { getCommunityOverview, getCommunityTree, getNearbyCommunityOverviews } from '@/api/property/community'
import { getCommunityRanking } from '@/api/property/analysis'
import { setPropertyId } from '@/utils/auth'

const AMAP_SCRIPT_ID = 'amap-js-api'
const AMAP_LOAD_TIMEOUT = 12000
const RADIUS_KM = 5
const FIRST_COMMUNITY_ZOOM = 13
const MARKER_COLORS = {
  normal: '#F9CADA',
  warning: '#F59E0B',
  danger: '#F56C6C'
}
const MARKER_SIZE = 24
const ACTIVE_MARKER_SIZE = Math.round(MARKER_SIZE * 1.2)

function normalizeRankingRows(payload) {
  if (Array.isArray(payload)) {
    return payload
  }
  if (Array.isArray(payload?.rows)) {
    return payload.rows
  }
  if (Array.isArray(payload?.list)) {
    return payload.list
  }
  if (Array.isArray(payload?.records)) {
    return payload.records
  }
  if (Array.isArray(payload?.data)) {
    return payload.data
  }
  return []
}

function normalizeRankingScore(value) {
  const score = Number(value)
  if (!Number.isFinite(score)) {
    return 0
  }
  return Math.max(0, Math.min(100, score))
}

function buildRankingChartRows(rows) {
  const sortedRows = normalizeRankingRows(rows)
    .map((item, index) => ({
      ...item,
      _chartId: String(item.communityId || item.communityName || `ranking-${index}`),
      _displayRank: item.rank || index + 1,
      _score: normalizeRankingScore(item.scorePercent)
    }))
    .sort((a, b) => b._score - a._score)

  if (sortedRows.length <= 6) {
    return sortedRows
  }

  const selectedMap = new Map()
  sortedRows.slice(0, 3).forEach((item) => selectedMap.set(item._chartId, item))
  sortedRows.slice(-3).forEach((item) => selectedMap.set(item._chartId, item))
  return Array.from(selectedMap.values()).sort((a, b) => b._score - a._score)
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
  name: 'Index',
  data() {
    return {
      centerCommunityId: '',
      centerOverview: {},
      selectedOverview: null,
      nearbyOverviews: [],
      map: null,
      markers: [],
      infoWindow: null,
      AMap: null,
      amapReady: false,
      loading: false,
      overviewLoaded: false,
      mapLoadError: '',
      overviewErrorMessage: '',
      cardVisible: false,
      defaultViewportApplied: false,
      firstCommunityFocusDone: false,
      rankingLoading: false,
      rankingLoaded: false,
      rankingLoadError: '',
      communityOptions: [],
      communityRankings: [],
      rankingQueryParams: {
        communityId: null,
        days: 30
      },
      pendingFocusCommunityId: ''
    }
  },
  computed: {
    centerPosition() {
      return this.getPosition(this.centerOverview)
    },
    selectedOverallStatus() {
      return this.getOverviewStatus(this.selectedOverview)
    },
    deviceStatus() {
      return this.selectedOverview?.deviceStatus || 'normal'
    },
    repairStatus() {
      return this.selectedOverview?.repairStatus || 'normal'
    },
    complaintStatus() {
      return this.selectedOverview?.complaintStatus || 'normal'
    },
    paymentStatus() {
      return this.selectedOverview?.paymentStatus || 'normal'
    },
    statusAlerts() {
      const overview = this.selectedOverview || {}
      const alerts = []
      if (this.deviceStatus === 'danger') {
        alerts.push(`异常设备达到 ${overview.deviceError || 0} 台`)
      } else if (this.deviceStatus === 'warning') {
        alerts.push(`存在 ${overview.deviceError || 0} 台异常设备`)
      }
      if (this.repairStatus === 'danger') {
        alerts.push(`待处理报修达到 ${overview.repairPending || 0} 单`)
      } else if (this.repairStatus === 'warning') {
        alerts.push(`存在 ${overview.repairPending || 0} 单待处理报修`)
      }
      if (this.complaintStatus === 'danger') {
        alerts.push(`今日投诉建议达到 ${overview.complaintCount || 0} 条`)
      } else if (this.complaintStatus === 'warning') {
        alerts.push(`今日存在 ${overview.complaintCount || 0} 条投诉建议`)
      }
      if (this.paymentStatus === 'danger') {
        alerts.push('缴费率低于 60%')
      } else if (this.paymentStatus === 'warning') {
        alerts.push('缴费率低于 80%')
      }
      return alerts
    },
    rankingSummary() {
      const total = this.communityRankings.length
      const sum = this.communityRankings.reduce((acc, item) => acc + Number(item.scorePercent || 0), 0)
      return {
        total,
        averageScore: total ? (sum / total).toFixed(2) : '0.00',
        good: this.communityRankings.filter((item) => item.level === 'good').length,
        warning: this.communityRankings.filter((item) => item.level === 'warning').length,
        danger: this.communityRankings.filter((item) => item.level === 'danger').length
      }
    },
    rankingChartRows() {
      return buildRankingChartRows(this.communityRankings)
    },
    mapMessage() {
      if (this.overviewErrorMessage) {
        return ''
      }
      if (this.overviewLoaded && !this.centerPosition) {
        return '当前中心小区缺少有效经纬度信息'
      }
      if (this.mapLoadError) {
        return this.mapLoadError
      }
      return ''
    },
    showMapPlaceholder() {
      return !this.centerPosition || !this.amapReady || Boolean(this.mapLoadError)
    },
    mapPlaceholderTitle() {
      if (this.loading || !this.overviewLoaded) {
        return '地图加载中'
      }
      if (!this.centerPosition) {
        return '缺少经纬度'
      }
      if (this.mapLoadError) {
        return '地图加载失败'
      }
      return '地图加载中'
    },
    mapPlaceholderText() {
      if (this.loading || !this.overviewLoaded) {
        return '正在加载小区地图数据。'
      }
      if (!this.centerPosition) {
        return '请先在小区信息中维护经度和纬度。'
      }
      if (this.mapLoadError) {
        return this.mapLoadError
      }
      return '正在加载在线地图与小区标志。'
    }
  },
  mounted() {
    window.addEventListener('community-map-change', this.handleCommunityMapChange)
    this.bootstrapMap()
    this.loadCommunityOptions()
    this.loadCommunityRanking()
  },
  beforeUnmount() {
    window.removeEventListener('community-map-change', this.handleCommunityMapChange)
    this.destroyMap()
  },
  methods: {
    syncGlobalCommunitySelector(communityId) {
      const nextCommunityId = String(communityId || '')
      setPropertyId(nextCommunityId)
      window.dispatchEvent(new CustomEvent('community-selector-sync', {
        detail: {
          communityId: nextCommunityId
        }
      }))
    },
    bootstrapMap() {
      const routeCommunityId = this.$route.query?.communityId
      if (routeCommunityId) {
        this.syncGlobalCommunitySelector(routeCommunityId)
        this.loadCommunityMap(routeCommunityId, { focusCenter: true })
        return
      }
      this.syncGlobalCommunitySelector('')
      getCommunityTree()
        .then((response) => {
          const firstCommunity = (response.data || [])[0]
          if (!firstCommunity?.communityId) {
            this.overviewLoaded = true
            this.overviewErrorMessage = '暂无可用小区数据'
            return
          }
          this.loadCommunityMap(firstCommunity.communityId)
        })
        .catch(() => {
          this.overviewLoaded = true
          this.overviewErrorMessage = '小区下拉数据加载失败，请确认登录状态和接口服务。'
        })
    },
    loadCommunityOptions() {
      getCommunityTree()
        .then((response) => {
          this.communityOptions = response.data || []
        })
        .catch(() => {
          this.communityOptions = []
        })
    },
    handleCommunityMapChange(event) {
      const communityId = event?.detail?.communityId
      if (!communityId) {
        this.hideCard()
        return
      }
      this.syncGlobalCommunitySelector(communityId)
      if (String(communityId) === String(this.centerCommunityId || '')) {
        const target = this.nearbyOverviews.find((item) => String(item.communityId) === String(communityId))
        if (target) {
          this.selectMarker(target)
          return
        }
        this.loadCommunityMap(communityId, { focusCenter: true })
        return
      }
      this.loadCommunityMap(communityId, { focusCenter: true })
    },
    loadCommunityMap(communityId, options = {}) {
      this.centerCommunityId = communityId
      this.pendingFocusCommunityId = options.focusCenter ? String(communityId) : ''
      this.cardVisible = false
      this.selectedOverview = null
      this.defaultViewportApplied = false
      this.firstCommunityFocusDone = false
      this.overviewLoaded = false
      this.overviewErrorMessage = ''
      this.mapLoadError = ''
      this.nearbyOverviews = []
      this.clearMarkers()
      this.loading = true

      getCommunityOverview(communityId)
        .then((response) => {
          if (!response.data || !response.data.communityId) {
            this.centerOverview = {}
            this.overviewLoaded = true
            this.overviewErrorMessage = '小区信息不存在'
            this.destroyMap()
            return null
          }
          this.centerOverview = response.data
          this.overviewLoaded = true
          return this.fetchNearbyOverviews(communityId)
        })
        .catch(() => {
          this.centerOverview = {}
          this.overviewLoaded = true
          this.overviewErrorMessage = '首页地图概览加载失败，请确认登录状态和接口服务。'
          this.destroyMap()
        })
        .finally(() => {
          this.loading = false
        })
    },
    fetchNearbyOverviews(communityId) {
      if (!this.centerPosition) {
        this.nearbyOverviews = []
        this.pendingFocusCommunityId = ''
        this.renderMap()
        return Promise.resolve()
      }
      return getNearbyCommunityOverviews(communityId, RADIUS_KM)
        .then((response) => {
          const rows = Array.isArray(response.data) ? response.data : []
          this.nearbyOverviews = this.normalizeNearbyRows(rows.length ? rows : [this.centerOverview])
          this.renderMap()
        })
        .catch(() => {
          this.nearbyOverviews = this.normalizeNearbyRows([this.centerOverview])
          this.renderMap()
        })
    },
    normalizeNearbyRows(rows) {
      const result = []
      const seen = new Set()
      ;(rows || []).forEach((row) => {
        if (!row?.communityId || !this.getPosition(row)) {
          return
        }
        const key = String(row.communityId)
        if (seen.has(key)) {
          return
        }
        seen.add(key)
        result.push({
          ...row,
          deviceStatus: row.deviceStatus || 'normal',
          repairStatus: row.repairStatus || 'normal',
          complaintStatus: row.complaintStatus || 'normal',
          paymentStatus: row.paymentStatus || 'normal'
        })
      })
      return result
    },
    renderMap() {
      if (!this.centerPosition) {
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
                  zoom: 11,
                  center: this.centerPosition,
                  viewMode: '2D'
                })
              }
              this.renderMarkers(AMap)
            } catch (error) {
              console.warn('[HomeMap] AMap initialize failed', error)
              this.destroyMap()
              this.mapLoadError = '地图加载失败，请稍后重试'
            }
          })
        })
        .catch((error) => {
          console.warn('[HomeMap] AMap SDK load failed', error)
          this.destroyMap()
          this.mapLoadError = '地图加载失败，请稍后重试'
        })
    },
    renderMarkers(AMap) {
      this.clearMarkers()
      this.markers = this.nearbyOverviews.map((overview) => {
        const active = this.selectedOverview && String(overview.communityId) === String(this.selectedOverview.communityId)
        const size = active ? ACTIVE_MARKER_SIZE : MARKER_SIZE
        const marker = new AMap.Marker({
          position: this.getPosition(overview),
          content: this.createMarkerContent(overview, active),
          offset: new AMap.Pixel(-size / 2, -size)
        })
        marker.setMap(this.map)
        marker.on('click', () => this.selectMarker(overview))
        return marker
      })

      if (this.markers.length > 1) {
        if (!this.defaultViewportApplied && !this.selectedOverview) {
          this.map.setFitView(this.markers, false, [80, 80, 80, 80])
          this.defaultViewportApplied = true
        }
        this.applyPendingFocus(AMap)
        return
      }
      if (!this.defaultViewportApplied && this.centerPosition) {
        this.map.setZoomAndCenter(11, this.centerPosition)
        this.defaultViewportApplied = true
      }
      this.applyPendingFocus(AMap)
    },
    applyPendingFocus(AMap) {
      if (!this.pendingFocusCommunityId) {
        return
      }
      const target = this.nearbyOverviews.find((item) => String(item.communityId) === String(this.pendingFocusCommunityId))
      this.pendingFocusCommunityId = ''
      if (!target) {
        return
      }
      this.selectedOverview = target
      this.cardVisible = true
      this.firstCommunityFocusDone = true
      this.$nextTick(() => {
        if (this.map?.resize) {
          this.map.resize()
        }
        this.renderMarkers(AMap)
        this.focusCommunity(target)
        this.openInfoWindow(target)
      })
    },
    createMarkerContent(overview, active) {
      const status = this.getOverviewStatus(overview)
      const color = MARKER_COLORS[status] || MARKER_COLORS.normal
      const size = active ? ACTIVE_MARKER_SIZE : MARKER_SIZE
      const innerSize = Math.round(size / 3)
      const innerMargin = Math.round((size - innerSize) / 2 - 2)
      return `<div style="width:${size}px;height:${size}px;border-radius:50% 50% 50% 0;transform:rotate(-45deg);background:${color};box-shadow:0 12px 24px rgba(15,23,42,.28);border:3px solid #fff;"><span style="display:block;width:${innerSize}px;height:${innerSize}px;border-radius:50%;background:#fff;margin:${innerMargin}px auto 0;opacity:.95;"></span></div>`
    },
    selectMarker(overview) {
      const shouldFocus = !this.firstCommunityFocusDone && !this.selectedOverview
      this.selectedOverview = overview
      this.cardVisible = true
      this.syncGlobalCommunitySelector(overview.communityId)
      this.$nextTick(() => {
        if (this.map?.resize) {
          this.map.resize()
        }
        this.renderMarkers(this.AMap)
        if (shouldFocus) {
          this.focusCommunity(overview)
          this.firstCommunityFocusDone = true
        }
        this.openInfoWindow(overview)
      })
    },
    backToOverview() {
      this.cardVisible = false
      this.selectedOverview = null
      this.firstCommunityFocusDone = false
      this.syncGlobalCommunitySelector('')
      if (this.infoWindow) {
        this.infoWindow.close()
      }
      this.$nextTick(() => {
        if (!this.map || !this.AMap) {
          return
        }
        if (this.map?.resize) {
          this.map.resize()
        }
        this.renderMarkers(this.AMap)
        if (this.markers.length > 1) {
          this.map.setFitView(this.markers, false, [80, 80, 80, 80])
        } else if (this.centerPosition) {
          this.map.setZoomAndCenter(11, this.centerPosition)
        }
      })
    },
    hideCard() {
      this.cardVisible = false
      this.selectedOverview = null
      this.syncGlobalCommunitySelector('')
      if (this.infoWindow) {
        this.infoWindow.close()
      }
      if (this.AMap && this.map) {
        this.renderMarkers(this.AMap)
      }
    },
    focusCommunity(overview) {
      const position = this.getPosition(overview)
      if (!this.map || !position) {
        return
      }
      if (this.map.resize) {
        this.map.resize()
      }
      this.map.setZoomAndCenter(FIRST_COMMUNITY_ZOOM, position)
      window.setTimeout(() => {
        if (this.map?.resize) {
          this.map.resize()
        }
        this.map?.setZoomAndCenter(FIRST_COMMUNITY_ZOOM, position)
      }, 80)
    },
    openInfoWindow(overview) {
      if (!this.map || !this.AMap) {
        return
      }
      const position = this.getPosition(overview)
      if (!position) {
        return
      }
      const content = `
        <div style="min-width:220px;padding:4px 2px;color:#0f172a;">
          <strong style="display:block;font-size:15px;margin-bottom:6px;">${this.escapeHtml(overview.communityName || '-')}</strong>
          <div style="font-size:12px;line-height:1.7;color:#475569;">${this.escapeHtml(overview.address || '-')}</div>
          <div style="margin-top:8px;font-size:12px;color:${MARKER_COLORS[this.getOverviewStatus(overview)] || MARKER_COLORS.normal};">状态：${this.escapeHtml(overview.statusText || '正常')}</div>
        </div>
      `
      if (!this.infoWindow) {
        this.infoWindow = new this.AMap.InfoWindow({
          offset: new this.AMap.Pixel(0, -28),
          autoMove: false
        })
      }
      this.infoWindow.setContent(content)
      this.infoWindow.open(this.map, position)
    },
    goDevice() {
      this.goBusiness('/communityAsset/device')
    },
    goRepair() {
      this.goBusiness(['/communityService/repair', '/serviceManage/repair'])
    },
    goFeeBill() {
      this.goBusiness('/financeManage/feeBill')
    },
    goSuggest() {
      this.goBusiness('/serviceManage/complaintWordCloud')
    },
    goBusiness(path) {
      if (!this.selectedOverview?.communityId) {
        return
      }
      const query = {
        communityId: this.selectedOverview.communityId
      }
      const targetPath = Array.isArray(path)
        ? (path.find((item) => this.$router.resolve({ path: item, query }).matched.length) || path[0])
        : path
      setPropertyId(this.selectedOverview.communityId)
      this.$router.push({
        path: targetPath,
        query
      })
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
    handleRankingQuery() {
      this.loadCommunityRanking()
    },
    resetRankingQuery() {
      this.rankingQueryParams = {
        communityId: null,
        days: 30
      }
      this.loadCommunityRanking()
    },
    async loadCommunityRanking() {
      this.rankingLoading = true
      this.rankingLoadError = ''
      try {
        const params = {
          ...this.rankingQueryParams,
          communityId: this.rankingQueryParams.communityId || null
        }
        const response = await getCommunityRanking(params)
        this.communityRankings = normalizeRankingRows(response.data)
        this.rankingLoaded = true
      } catch (error) {
        console.warn('[HomeMap] community ranking load failed', error)
        this.rankingLoadError = '小区运行综合评价数据加载失败，请重试'
        this.communityRankings = []
        this.rankingLoaded = true
      } finally {
        this.rankingLoading = false
      }
    },
    formatRankingScore(value) {
      const score = Number(value || 0)
      if (!Number.isFinite(score)) {
        return '0.00'
      }
      return score.toFixed(2)
    },
    formatRankingRate(value) {
      if (value === null || value === undefined) {
        return '暂无'
      }
      const rate = Number(value)
      if (!Number.isFinite(rate)) {
        return '暂无'
      }
      return `${(rate * 100).toFixed(0)}%`
    },
    formatRankingRisks(risks) {
      if (Array.isArray(risks) && risks.length) {
        return risks.join('、')
      }
      if (typeof risks === 'string' && risks.trim()) {
        return risks
      }
      return '无明显风险'
    },
    getRankingLevelTag(level) {
      if (level === 'good') {
        return 'success'
      }
      if (level === 'warning') {
        return 'warning'
      }
      if (level === 'danger') {
        return 'danger'
      }
      return 'info'
    },
    goCommunityRankingMap(row) {
      if (!row?.communityId) {
        return
      }
      const communityId = String(row.communityId)
      this.syncGlobalCommunitySelector(communityId)
      this.loadCommunityMap(communityId, { focusCenter: true })
    },
    getRankingBarHeight(value) {
      const score = normalizeRankingScore(value)
      return score <= 0 ? '6px' : `${score}%`
    },

    escapeHtml(value) {
      return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;')
    }
  }
}
</script>

<style lang="scss" scoped>
.home-map-page {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background:
    radial-gradient(circle at 8% 0%, rgba(249, 202, 218, 0.46), transparent 32%),
    radial-gradient(circle at 92% 18%, rgba(245, 158, 11, 0.14), transparent 30%),
    linear-gradient(135deg, #fff7f5 0%, #f8fafc 48%, #eef7f4 100%);
}

.map-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 28px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);

  h1 {
    margin: 4px 0 10px;
    color: #0f172a;
    font-size: 34px;
    line-height: 1.12;
  }
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  span {
    padding: 6px 12px;
    border-radius: 999px;
    background: #fff1f5;
    color: #475569;
    font-size: 13px;
  }
}

.overview-button {
  flex: none;
  margin-left: auto;
  border-radius: 999px;
  --el-button-bg-color: #fff3d6;
  --el-button-border-color: rgba(190, 24, 93, 0.18);
  --el-button-text-color: #be185d;
  --el-button-hover-bg-color: #fdecc8;
  --el-button-hover-border-color: rgba(190, 24, 93, 0.28);
  --el-button-hover-text-color: #9d174d;
  --el-button-active-bg-color: #fbdca5;
  --el-button-active-border-color: rgba(190, 24, 93, 0.36);
  --el-button-active-text-color: #9d174d;
}

.map-alert {
  margin: 16px 0 0;
}

.map-stage {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.map-stage.has-card {
  grid-template-columns: minmax(0, 1fr) 420px;
}

.map-container {
  position: relative;
  min-height: 680px;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 30px;
  background:
    linear-gradient(135deg, rgba(249, 202, 218, 0.22), rgba(14, 165, 233, 0.08)),
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
  background: rgba(248, 250, 252, 0.7);

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
  border-top: 6px solid #f9cada;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
}

.overview-card.status-warning {
  border-top-color: #f4c87d;
}

.overview-card.status-danger {
  border-top-color: #f69a9b;
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

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

.card-close {
  flex: none;
  padding: 6px 12px;
  border: 0;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475569;
  cursor: pointer;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.metric-item {
  min-height: 92px;
  padding: 12px;
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
  border: 0;
  font: inherit;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
  }
}

.metric-normal,
.rate-normal {
  background: #e8f4ff;
  color: #1677ff;
}

.metric-warning,
.rate-warning {
  background: #fff3d6;
  color: #b45309;
}

.metric-danger,
.rate-danger {
  background: #f56c6c;
  color: #ffffff;
}

.rate-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 16px;

  div,
  button {
    padding: 12px;
    border: 0;
    border-radius: 18px;
    background: #f1f5f9;
    text-align: left;
    font: inherit;
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

.rate-action {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
  }
}

.rate-row .rate-normal {
  background: #e8f4ff;
}

.rate-row .rate-warning {
  background: #fff3d6;
}

.rate-row .rate-danger {
  background: #f56c6c;
}

.rate-row .rate-normal span,
.rate-row .rate-normal strong {
  color: #1677ff;
}

.rate-row .rate-warning span,
.rate-row .rate-warning strong {
  color: #b45309;
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

.ranking-card {
  margin-top: 20px;
  padding: 22px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);
}

.ranking-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    color: #0f172a;
    font-size: 18px;
    font-weight: 700;
  }

  p {
    margin: 6px 0 0;
    color: #64748b;
    font-size: 13px;
    line-height: 1.6;
  }
}

.ranking-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.78);
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: rgba(236, 121, 123, 0.36);
    background: rgba(255, 241, 245, 0.9);
    box-shadow: 0 14px 30px rgba(15, 23, 42, 0.10);
  }

  &.is-good .ranking-no {
    background: rgba(82, 196, 26, 0.14);
    color: #389e0d;
  }

  &.is-warning .ranking-no {
    background: rgba(245, 158, 11, 0.18);
    color: #b45309;
  }

  &.is-danger .ranking-no {
    background: rgba(245, 108, 108, 0.18);
    color: #d9363e;
  }
}

.ranking-no {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: rgba(236, 121, 123, 0.12);
  color: #be185d;
  font-size: 18px;
  font-weight: 800;
}

.ranking-main {
  flex: 1;
  min-width: 0;
}

.ranking-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;

  span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  em {
    flex-shrink: 0;
    padding: 2px 8px;
    border-radius: 999px;
    background: rgba(236, 121, 123, 0.10);
    color: #be185d;
    font-style: normal;
    font-size: 12px;
    font-weight: 500;
  }
}

.ranking-risk {
  margin-top: 6px;
  color: #64748b;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ranking-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;

  span {
    color: #64748b;
    font-size: 12px;
  }
}

.ranking-score {
  min-width: 72px;
  text-align: right;
  color: #ec797b;

  strong {
    font-size: 24px;
    font-weight: 800;
  }

  span {
    margin-left: 2px;
    color: #64748b;
    font-size: 12px;
  }
}


@media (max-width: 1180px) {
  .map-stage.has-card {
    grid-template-columns: 1fr;
  }

  .overview-card {
    position: static;
  }
}

@media (max-width: 720px) {
  .home-map-page {
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
    min-height: 460px;
  }

  .overview-card {
    padding: 18px;
  }

  .metric-grid,
  .rate-row {
    grid-template-columns: 1fr;
  }
}

/* 与登录页统一的深色玻璃拟态首页主题，仅覆盖视觉样式，不改交互和数据。 */
.home-map-page {
  background:
    radial-gradient(circle at 78% 6%, rgba(236, 121, 123, 0.13), transparent 30%),
    radial-gradient(circle at 24% 100%, rgba(251, 228, 222, 0.12), transparent 36%),
    linear-gradient(180deg, #312520 0%, #2b211d 100%);
  color: rgba(255, 255, 255, 0.84);
}

.map-hero,
.map-container,
.overview-card {
  border-color: rgba(251, 228, 222, 0.14);
  background: rgba(255, 255, 255, 0.055);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.30);
  backdrop-filter: blur(18px) saturate(125%);
  -webkit-backdrop-filter: blur(18px) saturate(125%);
}

.map-hero {
  background:
    radial-gradient(circle at 0% 0%, rgba(251, 228, 222, 0.10), transparent 34%),
    rgba(255, 255, 255, 0.055);

  h1 {
    color: rgba(251, 228, 222, 0.94);
    font-weight: 500;
    letter-spacing: 1px;
    text-shadow: 0 0 22px rgba(236, 121, 123, 0.20);
  }
}

.hero-meta span {
  color: rgba(255, 255, 255, 0.68);
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(251, 228, 222, 0.10);
}

.overview-button {
  --el-button-bg-color: rgba(255, 255, 255, 0.055);
  --el-button-border-color: rgba(251, 228, 222, 0.18);
  --el-button-text-color: rgba(251, 228, 222, 0.92);
  --el-button-hover-bg-color: rgba(236, 121, 123, 0.16);
  --el-button-hover-border-color: rgba(236, 121, 123, 0.36);
  --el-button-hover-text-color: #ffffff;
  --el-button-active-bg-color: rgba(236, 121, 123, 0.22);
  --el-button-active-border-color: rgba(236, 121, 123, 0.46);
  --el-button-active-text-color: #ffffff;
}

.map-container {
  background:
    radial-gradient(circle at 12% 8%, rgba(251, 228, 222, 0.12), transparent 34%),
    radial-gradient(circle at 86% 18%, rgba(236, 121, 123, 0.10), transparent 30%),
    repeating-linear-gradient(45deg, rgba(251, 228, 222, 0.05) 0 1px, transparent 1px 18px),
    rgba(255, 255, 255, 0.035);
}

.map-placeholder {
  color: rgba(255, 255, 255, 0.66);
  background: rgba(49, 37, 32, 0.62);

  strong {
    color: rgba(251, 228, 222, 0.92);
  }
}

.overview-card {
  border-top-color: #ec797b;
  background:
    radial-gradient(circle at 0% 0%, rgba(251, 228, 222, 0.10), transparent 34%),
    rgba(255, 255, 255, 0.06);
}

.overview-card.status-warning {
  border-top-color: #e6a23c;
}

.overview-card.status-danger {
  border-top-color: #f56c6c;
}

.card-head {
  p {
    color: rgba(255, 255, 255, 0.54);
  }

  h2 {
    color: rgba(251, 228, 222, 0.94);
  }
}

.card-close {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(251, 228, 222, 0.12);

  &:hover {
    color: #ffffff;
    background: rgba(236, 121, 123, 0.16);
  }
}

.metric-item,
.rate-row div,
.rate-row button,
.alerts {
  background: rgba(255, 255, 255, 0.055);
  border: 1px solid rgba(251, 228, 222, 0.10);
  color: rgba(255, 255, 255, 0.78);
}

.metric-item span,
.metric-item em,
.rate-row span,
.alerts span {
  color: rgba(255, 255, 255, 0.56);
}

.metric-item strong,
.rate-row strong {
  color: rgba(251, 228, 222, 0.92);
}

.metric-normal,
.rate-row .rate-normal {
  background: rgba(236, 121, 123, 0.12);
  color: rgba(251, 228, 222, 0.92);
}

.metric-warning,
.rate-row .rate-warning {
  background: rgba(230, 162, 60, 0.16);
  color: #ffd79b;
}

.metric-danger,
.rate-row .rate-danger {
  background: rgba(245, 108, 108, 0.28);
  color: #ffffff;
}

.rate-row .rate-normal span,
.rate-row .rate-normal strong,
.rate-row .rate-warning span,
.rate-row .rate-warning strong,
.rate-row .rate-danger span,
.rate-row .rate-danger strong {
  color: inherit;
}

.metric-action:hover,
.rate-action:hover {
  box-shadow: 0 14px 28px rgba(236, 121, 123, 0.16);
}

.alerts {
  color: rgba(251, 228, 222, 0.82);
}

.ranking-card {
  border-color: rgba(251, 228, 222, 0.14);
  background:
    radial-gradient(circle at 0% 0%, rgba(251, 228, 222, 0.10), transparent 34%),
    rgba(255, 255, 255, 0.055);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.30);
  backdrop-filter: blur(18px) saturate(125%);
  -webkit-backdrop-filter: blur(18px) saturate(125%);
}

.ranking-header {
  h3 {
    color: rgba(251, 228, 222, 0.94);
    font-weight: 600;
  }

  p {
    color: rgba(255, 255, 255, 0.58);
  }
}

.ranking-item {
  border-color: rgba(251, 228, 222, 0.10);
  background: rgba(255, 255, 255, 0.055);

  &:hover {
    border-color: rgba(236, 121, 123, 0.42);
    background: rgba(236, 121, 123, 0.12);
    box-shadow: 0 14px 28px rgba(236, 121, 123, 0.14);
  }

  &.is-good .ranking-no {
    background: rgba(82, 196, 26, 0.18);
    color: #95de64;
  }

  &.is-warning .ranking-no {
    background: rgba(230, 162, 60, 0.18);
    color: #ffd79b;
  }

  &.is-danger .ranking-no {
    background: rgba(245, 108, 108, 0.24);
    color: #ffb4b4;
  }
}

.ranking-no {
  background: rgba(236, 121, 123, 0.16);
  color: rgba(251, 228, 222, 0.94);
}

.ranking-title {
  color: rgba(251, 228, 222, 0.94);

  em {
    background: rgba(251, 228, 222, 0.10);
    color: rgba(251, 228, 222, 0.86);
  }
}

.ranking-risk,
.ranking-metrics span,
.ranking-score span {
  color: rgba(255, 255, 255, 0.56);
}

.ranking-score {
  color: #ec797b;
}

.ranking-header-badge {
  flex-shrink: 0;
  color: #ec797b;
  font-size: 20px;
  font-weight: 800;
}

.analysis-filter {
  margin-bottom: 16px;
  padding: 16px 16px 6px;
  border: 1px solid rgba(251, 228, 222, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.045);
}

.analysis-alert {
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.summary-card,
.chart-card,
.table-card {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(251, 228, 222, 0.12);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.045);
}

.summary-card {
  padding: 20px;

  span {
    display: block;
    margin-bottom: 8px;
    color: rgba(255, 255, 255, 0.55);
  }

  strong {
    color: #fbe4de;
    font-size: 28px;
  }
}

.status-good strong {
  color: #c6f7ae;
}

.status-warning strong {
  color: #e6a23c;
}

.status-danger strong {
  color: #ec797b;
}

.chart-card,
.table-card {
  padding: 22px;
  margin-bottom: 16px;
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  span {
    color: rgba(255, 255, 255, 0.76);
    font-weight: 700;
  }

  strong {
    color: #ec797b;
    font-size: 20px;
  }
}

.topsis-bar-chart {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(96px, 1fr));
  gap: 18px;
  min-height: 360px;
  padding: 8px 4px 4px;
}

.topsis-bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  min-width: 0;
}

.topsis-bar-value {
  margin-bottom: 8px;
  color: #fbe4de;
  font-size: 15px;
  font-weight: 700;
}

.topsis-bar-track {
  position: relative;
  display: flex;
  align-items: flex-end;
  width: 42px;
  height: 260px;
  padding: 0 7px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
  box-shadow: inset 0 0 0 1px rgba(251, 228, 222, 0.10);
}

.topsis-bar-fill {
  width: 100%;
  min-height: 6px;
  border-radius: 999px 999px 8px 8px;
  background: linear-gradient(180deg, #fbe4de 0%, #ec797b 100%);
  box-shadow: 0 10px 24px rgba(236, 121, 123, 0.30);
  transition: height 0.25s ease;
}

.topsis-bar-name {
  width: 100%;
  margin-top: 12px;
  overflow: hidden;
  color: rgba(255, 255, 255, 0.76);
  font-size: 13px;
  text-align: center;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topsis-bar-rank {
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.48);
  font-size: 12px;
}

.ranking-table {
  border-radius: 16px;
  overflow: hidden;
}

.ranking-community-link {
  padding: 0;
  color: rgba(251, 228, 222, 0.94);
}

:deep(.ranking-filter .el-form-item__label) {
  color: rgba(255, 255, 255, 0.72);
}

:deep(.ranking-filter .el-select__wrapper) {
  min-height: 34px;
  background: rgba(255, 255, 255, 0.055);
  border: 1px solid rgba(251, 228, 222, 0.12);
  box-shadow: none;
}

:deep(.ranking-filter .el-select__placeholder),
:deep(.ranking-filter .el-select__selected-item) {
  color: rgba(255, 255, 255, 0.76);
}

:deep(.ranking-table.el-table) {
  --el-table-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-tr-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-header-bg-color: rgba(236, 121, 123, 0.10);
  --el-table-text-color: rgba(255, 255, 255, 0.72);
  --el-table-header-text-color: rgba(255, 255, 255, 0.82);
  --el-table-border-color: rgba(251, 228, 222, 0.10);
}

:deep(.ranking-table .el-table__row:hover > td.el-table__cell) {
  background: rgba(236, 121, 123, 0.10);
}

:deep(.el-button--cyan) {
  color: #ffffff;
  background: #ec797b;
  border-color: #ec797b;
  box-shadow: 0 8px 18px rgba(236, 121, 123, 0.22);
}

:deep(.el-button--cyan:hover),
:deep(.el-button--cyan:focus) {
  color: #ffffff;
  background: #f08b8d;
  border-color: #f08b8d;
}

:deep(.el-empty__description p) {
  color: rgba(255, 255, 255, 0.50);
}


@media (max-width: 960px) {
  .ranking-header {
    flex-direction: column;
  }

  .ranking-list {
    grid-template-columns: 1fr;
  }

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}


</style>
