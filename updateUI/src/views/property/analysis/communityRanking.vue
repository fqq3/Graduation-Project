<template>
  <div class="app-container ranking-page">
    <section class="analysis-hero">
      <div>
        <h1>小区运行综合评价</h1>
        <span>基于多指标 TOPSIS 方法，对小区运行状态进行综合排序并识别主要风险。</span>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="analysis-filter">
      <el-form-item label="小区">
        <el-select v-model="queryParams.communityId" clearable filterable placeholder="全部小区" style="width: 220px">
          <el-option
            v-for="item in communityOptions"
            :key="String(item.communityId)"
            :label="item.communityName"
            :value="item.communityId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="统计周期">
        <el-select v-model="queryParams.days" style="width: 150px">
          <el-option label="近 7 天" :value="7" />
          <el-option label="近 30 天" :value="30" />
          <el-option label="近 90 天" :value="90" />
        </el-select>
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

    <el-skeleton v-if="loading" :rows="10" animated />
    <el-empty v-else-if="loaded && !rankingRows.length" description="当前筛选条件下暂无小区评价数据" />

    <template v-else-if="loaded">
      <section class="summary-grid">
        <article class="summary-card">
          <span>参与评价小区</span>
          <strong>{{ summary.total }}</strong>
        </article>
        <article class="summary-card">
          <span>平均得分</span>
          <strong>{{ summary.averageScore }}</strong>
        </article>
        <article class="summary-card status-good">
          <span>运行良好</span>
          <strong>{{ summary.good }}</strong>
        </article>
        <article class="summary-card status-warning">
          <span>轻度预警</span>
          <strong>{{ summary.warning }}</strong>
        </article>
        <article class="summary-card status-danger">
          <span>重点关注</span>
          <strong>{{ summary.danger }}</strong>
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
            <span class="topsis-bar-value">{{ formatScore(item.scorePercent) }}</span>
            <div class="topsis-bar-track">
              <div class="topsis-bar-fill" :style="{ height: getBarHeight(item.scorePercent) }"></div>
            </div>
            <span class="topsis-bar-name" :title="item.communityName || '未命名小区'">{{ item.communityName || '未命名小区' }}</span>
            <span class="topsis-bar-rank">第 {{ item.rank || item._displayRank }} 名</span>
          </article>
        </div>
        <el-empty v-else description="当前暂无可展示的综合评分数据" />
      </section>

      <section class="table-card indicator-card">
        <div class="card-title">
          <span>指标来源与权重说明</span>
          <strong>{{ indicatorRows.length }}</strong>
        </div>
        <el-table :data="indicatorRows" class="ranking-table">
          <el-table-column prop="name" label="指标名称" width="110" align="center" />
          <el-table-column label="权重" width="90" align="center">
            <template #default="scope">{{ formatWeight(scope.row.weight) }}</template>
          </el-table-column>
          <el-table-column prop="attribute" label="指标属性" width="100" align="center" />
          <el-table-column prop="dataSource" label="数据来源" min-width="180" show-overflow-tooltip />
          <el-table-column prop="calculation" label="计算口径" min-width="260" show-overflow-tooltip />
          <el-table-column prop="description" label="说明" min-width="280" show-overflow-tooltip />
        </el-table>
      </section>

      <section class="table-card">
        <div class="card-title">
          <span>评价明细</span>
          <strong>{{ rankingRows.length }}</strong>
        </div>
        <el-table :data="rankingRows" class="ranking-table" height="520">
          <el-table-column prop="rank" label="排名" width="70" align="center" />
          <el-table-column prop="communityName" label="小区" min-width="150" show-overflow-tooltip />
          <el-table-column label="综合得分" width="110" align="center">
            <template #default="scope">{{ formatScore(scope.row.scorePercent) }}</template>
          </el-table-column>
          <el-table-column label="等级" width="110" align="center">  
            <template #default="scope">
              <el-tag :type="getLevelTag(scope.row.level)" effect="dark">{{ scope.row.levelName || '暂无' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="入住率" width="90" align="center">
            <template #default="scope">{{ formatPercent(scope.row.occupancyRate) }}</template>
          </el-table-column>
          <el-table-column label="缴费率" width="90" align="center">
            <template #default="scope">{{ formatPercent(scope.row.paymentRate) }}</template>
          </el-table-column>
          <el-table-column label="设备健康" width="100" align="center">
            <template #default="scope">{{ formatPercent(scope.row.deviceHealth) }}</template>
          </el-table-column>
          <el-table-column label="报修健康" width="100" align="center">
            <template #default="scope">{{ formatPercent(scope.row.repairHealth) }}</template>
          </el-table-column>
          <el-table-column label="投诉健康" width="100" align="center">
            <template #default="scope">{{ formatPercent(scope.row.complaintHealth) }}</template>
          </el-table-column>
          <el-table-column label="停车健康" width="100" align="center">
            <template #default="scope">{{ formatPercent(scope.row.parkingHealth) }}</template>
          </el-table-column>
          <el-table-column label="主要风险" min-width="210" show-overflow-tooltip>
            <template #default="scope">{{ formatRisks(scope.row.mainRisks) }}</template>
          </el-table-column>
        </el-table>
      </section>
    </template>
  </div>
</template>

<script>
import { getCommunityRanking, getCommunityRankingIndicators } from '@/api/property/analysis'
import { getCommunityTree } from '@/api/property/community'
import { getPropertyId } from '@/utils/auth'

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

export default {
  name: 'CommunityRanking',
  data() {
    return {
      loading: false,
      loaded: false,
      loadError: '',
      communityOptions: [],
      queryParams: {
        communityId: null,
        days: 30
      },
      rankingRows: [],
      indicatorRows: []
    }
  },
  computed: {
    summary() {
      const total = this.rankingRows.length
      const sum = this.rankingRows.reduce((acc, item) => acc + Number(item.scorePercent || 0), 0)
      return {
        total,
        averageScore: total ? (sum / total).toFixed(2) : '0.00',
        good: this.rankingRows.filter((item) => item.level === 'good').length,
        warning: this.rankingRows.filter((item) => item.level === 'warning').length,
        danger: this.rankingRows.filter((item) => item.level === 'danger').length
      }
    },
    rankingChartRows() {
      return buildRankingChartRows(this.rankingRows)
    }
  },
  created() {
    this.syncCommunityId()
    this.loadCommunityOptions()
    this.loadIndicatorRows()
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
  methods: {
    syncCommunityId() {
      const communityId = this.$route.query.communityId || getPropertyId() || null
      this.queryParams.communityId = communityId
    },
    loadCommunityOptions() {
      getCommunityTree()
        .then((res) => {
          this.communityOptions = res.data || []
        })
        .catch(() => {
          this.communityOptions = []
        })
    },
    loadIndicatorRows() {
      getCommunityRankingIndicators()
        .then((res) => {
          this.indicatorRows = Array.isArray(res.data) ? res.data : []
        })
        .catch(() => {
          this.indicatorRows = []
        })
    },
    handleQuery() {
      this.loading = true
      this.loadError = ''
      const params = {
        ...this.queryParams,
        communityId: this.queryParams.communityId || null
      }
      if (params.communityId) {
        setPropertyId(params.communityId)
      }
      getCommunityRanking(params)
        .then((res) => {
          this.rankingRows = normalizeRankingRows(res.data)
          this.loaded = true
        })
        .catch(() => {
          this.loadError = '小区运行综合评价数据加载失败，请重试'
          this.rankingRows = []
          this.loaded = true
        })
        .finally(() => {
          this.loading = false
        })
    },
    resetQuery() {
      this.queryParams.days = 30
      this.syncCommunityId()
      this.handleQuery()
    },
    formatPercent(value) {
      const n = Number(value || 0)
      if (!Number.isFinite(n)) {
        return '0%'
      }
      return `${(n * 100).toFixed(0)}%`
    },
    formatWeight(value) {
      const n = Number(value || 0)
      if (!Number.isFinite(n)) {
        return '0%'
      }
      return `${(n * 100).toFixed(0)}%`
    },
    formatScore(value) {
      const n = Number(value || 0)
      if (!Number.isFinite(n)) {
        return '0.00'
      }
      return n.toFixed(2)
    },
    formatRisks(risks) {
      if (Array.isArray(risks)) {
        return risks.join('、')
      }
      return risks || '无明显风险'
    },
    getLevelTag(level) {
      if (level === 'good') return 'success'
      if (level === 'warning') return 'warning'
      if (level === 'danger') return 'danger'
      return 'info'
    },
    getBarHeight(value) {
      const score = normalizeRankingScore(value)
      return score <= 0 ? '6px' : `${score}%`
    }
  }
}
</script>

<style lang="scss" scoped>
.ranking-page {
  min-height: calc(100vh - 84px);
  padding-bottom: 32px;
  background:
    radial-gradient(circle at 0% 0%, rgba(251, 228, 222, 0.14), transparent 30%),
    radial-gradient(circle at 100% 16%, rgba(236, 121, 123, 0.11), transparent 32%),
    linear-gradient(135deg, rgba(49, 37, 32, 0.96) 0%, rgba(43, 33, 29, 0.92) 100%);
}

.analysis-hero,
.analysis-filter,
.summary-card,
.chart-card,
.table-card {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(251, 228, 222, 0.14);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.055);
  box-shadow: 0 22px 56px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(18px) saturate(125%);
  -webkit-backdrop-filter: blur(18px) saturate(125%);
}

.analysis-hero {
  margin-bottom: 16px;
  padding: 24px 28px;

  h1 {
    margin: 0 0 10px;
    color: rgba(255, 255, 255, 0.92);
    font-size: 30px;
    font-weight: 500;
  }

  span {
    color: rgba(255, 255, 255, 0.58);
  }
}

.analysis-filter {
  margin-bottom: 16px;
  padding: 16px 16px 6px;
  border-radius: 18px;
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

.status-good strong { color: #c6f7ae; }
.status-warning strong { color: #f4c87d; }
.status-danger strong { color: #f69a9b; }

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

:deep(.el-table) {
  --el-table-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-tr-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-header-bg-color: rgba(236, 121, 123, 0.10);
  --el-table-text-color: rgba(255, 255, 255, 0.72);
  --el-table-header-text-color: rgba(255, 255, 255, 0.82);
  --el-table-border-color: rgba(251, 228, 222, 0.10);
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

@media (max-width: 1180px) {
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
