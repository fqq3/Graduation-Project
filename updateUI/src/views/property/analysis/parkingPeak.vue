<template>
  <div class="app-container parking-analysis-page">
    <section class="analysis-hero">
      <div>
        <h1>停车高峰分析</h1>
        <span>按小时统计车辆实时在场数量，并给出近 7 日 24 小时停车量填充折线对比图。</span>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="analysis-filter">
      <el-form-item label="日期">
        <el-date-picker
          v-model="queryParams.date"
          type="date"
          placeholder="请选择日期"
          value-format="YYYYMMDD"
        />
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert
      v-if="loadError"
      :title="loadError"
      type="error"
      show-icon
      :closable="false"
      class="analysis-alert"
    />
    <el-alert
      v-if="loaded && !loading && noParkingSpace"
      title="暂无车位数据，无法判断停车压力"
      type="warning"
      show-icon
      :closable="false"
      class="analysis-alert"
    />
    <el-alert
      v-if="loaded && !loading && noParkingRecord"
      title="当前日期暂无停车记录"
      type="info"
      show-icon
      :closable="false"
      class="analysis-alert"
    />

    <el-skeleton v-if="loading" :rows="10" animated />

    <template v-else-if="loaded">
      <section class="summary-grid">
        <article class="summary-card">
          <span>高峰时段</span>
          <strong>{{ result.peakHour || '暂无' }}</strong>
        </article>
        <article class="summary-card">
          <span>高峰在场车辆数</span>
          <strong>{{ result.peakCount || 0 }}</strong>
        </article>
        <article class="summary-card">
          <span>车位总数</span>
          <strong>{{ result.parkingCount || 0 }}</strong>
        </article>
        <article class="summary-card">
          <span>固定车占比</span>
          <strong>{{ formatRate(result.fixedCarRate) }}</strong>
          <em>{{ result.fixedCarCount || 0 }} 辆</em>
        </article>
        <article class="summary-card">
          <span>临停车占比</span>
          <strong>{{ formatRate(result.tempCarRate) }}</strong>
          <em>{{ result.tempCarCount || 0 }} 辆</em>
        </article>
        <article class="summary-card" :class="'pressure-' + (result.pressureLevel || 'unknown')">
          <span>停车压力</span>
          <strong>{{ formatRate(result.pressureRate) }}</strong>
          <em>{{ result.pressureText || '暂无车位数据，无法判断停车压力' }}</em>
        </article>
      </section>

      <section class="chart-grid">
        <article class="chart-card chart-card-large">
          <div class="card-title">
            <span>24 小时在场车辆统计</span>
            <strong>{{ result.date || queryParams.date }}</strong>
          </div>
          <div ref="hourlyChart" class="chart chart-hourly"></div>
        </article>

        <article class="chart-card chart-card-large">
          <div class="card-title">
            <span>近 7 日在场车辆趋势</span>
            <strong>{{ (result.last7Days || []).length }} 天</strong>
          </div>
          <el-alert
            v-if="trendEmpty"
            title="近 7 日暂无在场车辆趋势数据"
            type="info"
            show-icon
            :closable="false"
            class="trend-alert"
          />
          <div ref="trendChart" class="chart chart-trend"></div>
        </article>
      </section>
    </template>
  </div>
</template>

<script>
import { markRaw } from 'vue'
import * as echarts from 'echarts'
import 'echarts-gl'
import { getParkingPeak } from '@/api/property/analysis'
import { getPropertyId, setPropertyId } from '@/utils/auth'

function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}${month}${day}`
}

function normalizeHourLabel(value) {
  if (value === null || value === undefined) {
    return '00:00'
  }
  const text = String(value)
  if (text.includes(':')) {
    const hour = parseInt(text.split(':')[0], 10)
    return `${String(Number.isFinite(hour) ? hour : 0).padStart(2, '0')}:00`
  }
  const hour = parseInt(text, 10)
  return `${String(Number.isFinite(hour) ? hour : 0).padStart(2, '0')}:00`
}

function normalizeHourlyRows(rows) {
  const map = new Map()
  ;(rows || []).forEach((item) => {
    map.set(normalizeHourLabel(item.hour), Number(item.count || 0))
  })
  return Array.from({ length: 24 }, (_, index) => {
    const hour = `${String(index).padStart(2, '0')}:00`
    return {
      hour,
      count: map.get(hour) || 0
    }
  })
}

function createEmptyResult() {
  return {
    parkingCount: 0,
    peakHour: '',
    peakCount: 0,
    pressureRate: 0,
    pressureLevel: '',
    pressureText: '',
    hourlyData: [],
    last7Days: [],
    fixedCarCount: 0,
    tempCarCount: 0,
    fixedCarRate: 0,
    tempCarRate: 0
  }
}

export default {
  name: 'ParkingPeak',
  data() {
    return {
      loading: false,
      loaded: false,
      loadError: '',
      renderTimer: null,
      hourlyChart: null,
      trendChart: null,
      queryParams: {
        communityId: null,
        date: ''
      },
      result: createEmptyResult()
    }
  },
  computed: {
    noParkingSpace() {
      return Number(this.result.parkingCount || 0) <= 0
    },
    noParkingRecord() {
      return Number(this.result.peakCount || 0) <= 0
    },
    trendEmpty() {
      const days = this.result.last7Days || []
      if (!days.length) {
        return true
      }
      return days.every((item) => {
        const hourly = item.hourlyData || []
        return hourly.every((hour) => Number(hour.count || 0) <= 0)
      })
    }
  },
  created() {
    this.initDefaultDate()
    this.syncCommunityId()
    this.handleQuery()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncCommunityId()
      this.handleQuery()
    }
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.renderTimer) {
      window.clearTimeout(this.renderTimer)
      this.renderTimer = null
    }
    this.disposeCharts()
  },
  methods: {
    initDefaultDate() {
      this.queryParams.date = formatDate(new Date())
    },
    syncCommunityId() {
      const communityId = this.$route.query.communityId || getPropertyId() || null
      this.queryParams.communityId = communityId
      if (communityId) {
        setPropertyId(communityId)
      }
    },
    handleQuery() {
      this.loading = true
      this.loadError = ''
      this.disposeCharts()
      if (this.renderTimer) {
        window.clearTimeout(this.renderTimer)
        this.renderTimer = null
      }
      if (this.queryParams.communityId) {
        setPropertyId(this.queryParams.communityId)
      }

      getParkingPeak(this.queryParams)
        .then((res) => {
          this.result = {
            ...createEmptyResult(),
            ...(res.data || {})
          }
          this.loaded = true
          this.loading = false
          this.scheduleRenderCharts()
        })
        .catch(() => {
          this.loadError = '停车高峰数据加载失败，请重试'
          this.result = createEmptyResult()
          this.loaded = true
          this.loading = false
          this.disposeCharts()
        })
    },
    resetQuery() {
      this.initDefaultDate()
      this.syncCommunityId()
      this.handleQuery()
    },
    formatRate(value) {
      const raw = Number(value || 0)
      if (!Number.isFinite(raw)) {
        return '0%'
      }
      const rate = raw > 1 ? raw : raw * 100
      return `${rate.toFixed(0)}%`
    },
    scheduleRenderCharts() {
      if (this.renderTimer) {
        window.clearTimeout(this.renderTimer)
      }
      this.$nextTick(() => {
        window.requestAnimationFrame(() => {
          window.requestAnimationFrame(() => {
            this.renderTimer = window.setTimeout(() => this.renderCharts(), 80)
          })
        })
      })
    },
    renderCharts() {
      this.renderHourlyChart()
      this.renderTrendChart()
    },
    isChartDomReady(dom) {
      if (!dom) {
        return false
      }
      const rect = dom.getBoundingClientRect()
      return rect.width > 0 && rect.height > 0
    },
    retryRenderCharts() {
      if (this.renderTimer) {
        window.clearTimeout(this.renderTimer)
      }
      this.renderTimer = window.setTimeout(() => {
        this.$nextTick(() => this.renderCharts())
      }, 160)
    },
    renderHourlyChart() {
      const dom = this.$refs.hourlyChart
      if (!dom) {
        return
      }
      if (!this.isChartDomReady(dom)) {
        this.retryRenderCharts()
        return
      }
      if (this.hourlyChart) {
        this.hourlyChart.dispose()
        this.hourlyChart = null
      }
      this.hourlyChart = markRaw(echarts.init(dom, null, { renderer: 'svg' }))

      const rows = normalizeHourlyRows(this.result.hourlyData || [])
      const hours = rows.map((item) => item.hour)
      const counts = rows.map((item) => Number(item.count || 0))

      this.hourlyChart.setOption({
        backgroundColor: 'transparent',
        color: ['#f57c6e'],
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(49, 37, 32, 0.94)',
          borderColor: 'rgba(245, 124, 110, 0.48)',
          textStyle: { color: 'rgba(255, 255, 255, 0.88)' },
          axisPointer: {
            type: 'line',
            lineStyle: { color: 'rgba(245, 124, 110, 0.52)' }
          }
        },
        grid: [
          {
            left: 46,
            right: 28,
            top: 38,
            bottom: 42,
            containLabel: true
          }
        ],
        xAxis: [
          {
            type: 'category',
            gridIndex: 0,
            boundaryGap: false,
            data: hours,
            axisLine: { lineStyle: { color: 'rgba(251, 228, 222, 0.22)' } },
            axisTick: { show: false },
            axisLabel: { color: 'rgba(255, 255, 255, 0.58)' }
          }
        ],
        yAxis: [
          {
            type: 'value',
            gridIndex: 0,
            minInterval: 1,
            axisLabel: { color: 'rgba(255, 255, 255, 0.58)' },
            splitLine: { lineStyle: { color: 'rgba(251, 228, 222, 0.10)' } }
          }
        ],
        series: [
          {
            name: '在场车辆数',
            type: 'line',
            xAxisIndex: 0,
            yAxisIndex: 0,
            smooth: true,
            symbol: 'circle',
            symbolSize: 7,
            data: counts,
            lineStyle: {
              color: '#f57c6e',
              width: 3
            },
            itemStyle: {
              color: '#f57c6e',
              borderColor: '#fae69e',
              borderWidth: 2
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(245, 124, 110, 0.32)' },
                { offset: 0.55, color: 'rgba(242, 181, 111, 0.14)' },
                { offset: 1, color: 'rgba(245, 124, 110, 0.02)' }
              ])
            }
          }
        ]
      }, true)

      this.hourlyChart.resize()
    },
    renderTrendChart() {
      const dom = this.$refs.trendChart
      if (!dom) {
        return
      }
      if (!this.isChartDomReady(dom)) {
        this.retryRenderCharts()
        return
      }
      if (this.trendChart) {
        this.trendChart.dispose()
        this.trendChart = null
      }
      this.trendChart = markRaw(echarts.init(dom, null, { renderer: 'canvas' }))

      const rows = this.result.last7Days || []
      const hours = Array.from({ length: 24 }, (_, index) => `${String(index).padStart(2, '0')}:00`)
      const days = rows.map((item) => item.date)

      const series = rows.map((day, index) => {
        const hourly = normalizeHourlyRows(day.hourlyData || [])
        const countMap = new Map(hourly.map((item) => [item.hour, Number(item.count || 0)]))

        return {
          name: day.date,
          type: 'line3D',
          coordinateSystem: 'cartesian3D',
          smooth: true,
          lineStyle: {
            width: 5,
            opacity: 0.86
          },
          data: hours.map((hour, hourIndex) => [hourIndex, index, countMap.get(hour) || 0])
        }
      })

      const surfaceData = []
      rows.forEach((day, dayIndex) => {
        const hourly = normalizeHourlyRows(day.hourlyData || [])
        const countMap = new Map(hourly.map((item) => [item.hour, Number(item.count || 0)]))
        hours.forEach((hour, hourIndex) => {
          surfaceData.push([hourIndex, dayIndex, countMap.get(hour) || 0])
        })
      })
      const maxCount = Math.max(1, ...surfaceData.map((item) => Number(item[2] || 0)))

      if (rows.length && hours.length) {
        series.push({
          name: '趋势面',
          type: 'surface',
          coordinateSystem: 'cartesian3D',
          wireframe: {
            show: false
          },
          lineStyle: {
            width: 1,
            opacity: 0.16
          },
          itemStyle: {
            opacity: 0.24
          },
          silent: true,
          data: surfaceData
        })
      }

      this.trendChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          backgroundColor: 'rgba(49, 37, 32, 0.94)',
          borderColor: 'rgba(245, 124, 110, 0.48)',
          textStyle: {
            color: 'rgba(255, 255, 255, 0.88)'
          },
          formatter(params) {
            const value = params.value || []
            const hour = hours[value[0]] || ''
            const date = days[value[1]] || params.seriesName || ''
            return `${date}<br/>${hour}<br/>车辆数：${value[2] || 0}`
          }
        },
        visualMap: {
          show: true,
          dimension: 2,
          min: 0,
          max: maxCount,
          right: 8,
          top: 42,
          calculable: true,
          inRange: {
            color: ['#6c3b8f', '#71b7ed', '#84c3b7', '#fae69e', '#f2b56f', '#f57c6e']
          },
          textStyle: {
            color: 'rgba(255, 255, 255, 0.62)'
          }
        },
        grid3D: {
          left: 0,
          right: 88,
          top: 8,
          bottom: 8,
          boxWidth: 180,
          boxDepth: 74,
          boxHeight: 82,
          viewControl: {
            projection: 'perspective',
            alpha: 28,
            beta: -42,
            distance: 232,
            rotateSensitivity: 1,
            zoomSensitivity: 1,
            panSensitivity: 1
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(251, 228, 222, 0.28)'
            }
          },
          axisPointer: {
            lineStyle: {
              color: 'rgba(245, 124, 110, 0.72)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(251, 228, 222, 0.10)'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['rgba(255, 255, 255, 0.02)', 'rgba(255, 255, 255, 0.04)']
            }
          }
        },
        xAxis3D: {
          type: 'category',
          name: '小时',
          data: hours,
          axisLabel: {
            interval: 2,
            color: 'rgba(255, 255, 255, 0.62)'
          },
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.72)'
          }
        },
        yAxis3D: {
          type: 'category',
          name: '日期',
          data: days,
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.62)'
          },
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.72)'
          }
        },
        zAxis3D: {
          type: 'value',
          name: '车辆数',
          minInterval: 1,
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.62)'
          },
          nameTextStyle: {
            color: 'rgba(255, 255, 255, 0.72)'
          }
        },
        series
      }, true)

      this.trendChart.resize()
    },
    resizeCharts() {
      this.hourlyChart?.resize()
      this.trendChart?.resize()
    },
    disposeCharts() {
      if (this.hourlyChart) {
        this.hourlyChart.dispose()
        this.hourlyChart = null
      }
      if (this.trendChart) {
        this.trendChart.dispose()
        this.trendChart = null
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.parking-analysis-page {
  min-height: calc(100vh - 84px);
  padding-bottom: 32px;
  overflow: visible;
  background:
    radial-gradient(circle at 8% 0%, rgba(251, 228, 222, 0.14), transparent 32%),
    radial-gradient(circle at 92% 12%, rgba(236, 121, 123, 0.10), transparent 30%),
    linear-gradient(135deg, rgba(49, 37, 32, 0.96) 0%, rgba(43, 33, 29, 0.92) 100%);
}

.analysis-hero,
.summary-card,
.chart-card {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(251, 228, 222, 0.14);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.055);
  box-shadow: 0 22px 56px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(18px) saturate(125%);
  -webkit-backdrop-filter: blur(18px) saturate(125%);
}

.analysis-hero::before,
.summary-card::before,
.chart-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    radial-gradient(circle at 10% 0%, rgba(251, 228, 222, 0.12), transparent 36%),
    radial-gradient(circle at 96% 12%, rgba(236, 121, 123, 0.10), transparent 30%);
  pointer-events: none;
}

.analysis-hero > *,
.summary-card > *,
.chart-card > * {
  position: relative;
  z-index: 1;
}

.analysis-hero {
  margin-bottom: 16px;
  padding: 24px 28px;

  h1 {
    margin: 0 0 10px;
    color: rgba(255, 255, 255, 0.92);
    font-size: 30px;
    font-weight: 500;
    text-shadow: 0 10px 28px rgba(0, 0, 0, 0.28);
  }

  span {
    color: rgba(255, 255, 255, 0.58);
  }
}

.analysis-filter {
  margin-bottom: 16px;
  padding: 16px 16px 6px;
  border: 1px solid rgba(251, 228, 222, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.045);
  box-shadow: 0 16px 38px rgba(0, 0, 0, 0.20);
  backdrop-filter: blur(16px) saturate(120%);
  -webkit-backdrop-filter: blur(16px) saturate(120%);
}

.analysis-alert,
.trend-alert {
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.summary-card {
  padding: 20px;

  span,
  em {
    display: block;
    color: rgba(255, 255, 255, 0.56);
    font-size: 13px;
    font-style: normal;
  }

  strong {
    display: block;
    margin: 10px 0 6px;
    color: #fbe4de;
    font-size: 24px;
    font-weight: 700;
    text-shadow: 0 0 18px rgba(236, 121, 123, 0.18);
  }
}

.pressure-normal {
  background:
    radial-gradient(circle at 92% 12%, rgba(251, 228, 222, 0.18), transparent 40%),
    rgba(255, 255, 255, 0.06);
}

.pressure-warning {
  background:
    radial-gradient(circle at 92% 12%, rgba(236, 121, 123, 0.20), transparent 40%),
    rgba(255, 255, 255, 0.06);
}

.pressure-danger {
  border-color: rgba(236, 121, 123, 0.34);
  background:
    radial-gradient(circle at 92% 12%, rgba(236, 121, 123, 0.28), transparent 44%),
    rgba(255, 255, 255, 0.065);
}

.pressure-unknown {
  background: rgba(255, 255, 255, 0.045);
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 18px;
}

.chart-card {
  padding: 22px;
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  span {
    color: rgba(255, 255, 255, 0.76);
    font-weight: 700;
  }

  strong {
    color: #ec797b;
  }
}

.chart {
  width: 100%;
  min-height: 420px;
  border-radius: 18px;
  background:
    radial-gradient(circle at 50% 100%, rgba(251, 228, 222, 0.07), transparent 46%),
    rgba(0, 0, 0, 0.10);
}

.chart-hourly {
  height: 420px;
}

.chart-trend {
  height: 520px;
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
  box-shadow: 0 12px 26px rgba(236, 121, 123, 0.30);
}

:deep(.el-skeleton__item) {
  background: linear-gradient(90deg, rgba(255,255,255,0.06) 25%, rgba(251,228,222,0.12) 37%, rgba(255,255,255,0.06) 63%);
}

@media (max-width: 1300px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .chart-hourly,
  .chart-trend {
    height: 380px;
  }
}
</style>
