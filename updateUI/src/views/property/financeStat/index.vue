<template>
  <div class="app-container finance-stat-page">
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="统计年份" prop="billYear">
        <el-input-number v-model="queryParams.billYear" :min="2000" :max="2100" controls-position="right" style="width: 160px" />
      </el-form-item>
      <el-form-item label="项目名称" prop="itemName">
        <el-input v-model="queryParams.itemName" placeholder="请输入项目名称" clearable size="small" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button class="el-button--cyan" size="small" @click="handleQuery"><el-icon><Search /></el-icon><span>搜索</span></el-button>
        <el-button size="small" @click="resetQuery"><el-icon><Refresh /></el-icon><span>重置</span></el-button>
      </el-form-item>
    </el-form>

    <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div class="stat-title">应收总额</div>
          <div class="stat-value">{{ formatAmount(overview.totalReceivable) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div class="stat-title">实收总额</div>
          <div class="stat-value">{{ formatAmount(overview.totalReceived) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div class="stat-title">未收总额</div>
          <div class="stat-value stat-danger">{{ formatAmount(overview.totalUnpaid) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover">
          <div class="stat-title">账单总数</div>
          <div class="stat-value">{{ overview.billCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="mini-title">已缴清账单</div>
          <div class="mini-value">{{ overview.paidBillCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="mini-title">缴费率</div>
          <div class="mini-value">{{ formatRate(overview.paymentRate) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="mini-title">部分缴费账单</div>
          <div class="mini-value">{{ overview.partialBillCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="mini-title">待缴费账单</div>
          <div class="mini-value">{{ overview.unpaidBillCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-title">月度收缴趋势</div>
          </template>
          <div ref="monthChart" class="finance-chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-title">费用项目统计</div>
          </template>
          <div ref="itemChart" class="finance-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="mb16">
      <template #header>
        <div class="card-title">月度统计</div>
      </template>
      <el-table v-loading="loading" :data="monthTableData">
        <el-table-column label="月份" align="center" prop="statMonth" min-width="120" />
        <el-table-column label="账单数" align="center" prop="billCount" />
        <el-table-column label="应收总额" align="center" prop="receivableAmount" />
        <el-table-column label="实收总额" align="center" prop="receivedAmount" />
        <el-table-column label="未收总额" align="center" prop="unpaidAmount" />
      </el-table>
      <pagination v-show="monthList.length > 0" :total="monthList.length" v-model:page="monthPageNum" v-model:limit="monthPageSize" @pagination="noop" />
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-title">项目统计</div>
      </template>
      <el-table v-loading="loading" :data="itemTableData">
        <el-table-column label="项目名称" align="center" prop="itemName" min-width="140" />
        <el-table-column label="账单数" align="center" prop="billCount" />
        <el-table-column label="应收总额" align="center" prop="receivableAmount" />
        <el-table-column label="实收总额" align="center" prop="receivedAmount" />
        <el-table-column label="未收总额" align="center" prop="unpaidAmount" />
      </el-table>
      <pagination v-show="itemList.length > 0" :total="itemList.length" v-model:page="itemPageNum" v-model:limit="itemPageSize" @pagination="noop" />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getFinanceAll } from '@/api/property/financeStat'
import { getPropertyId } from '@/utils/auth'

export default {
  name: 'FinanceStat',
  components: { Refresh, Search },
  data() {
    return {
      loading: false,
      showSearch: true,
      overview: {},
      monthList: [],
      itemList: [],
      monthPageNum: 1,
      monthPageSize: 10,
      itemPageNum: 1,
      itemPageSize: 10,
      monthChart: null,
      itemChart: null,
      queryParams: {
        communityId: null,
        billYear: new Date().getFullYear(),
        itemName: null
      }
    }
  },
  computed: {
    monthTableData() {
      const start = (this.monthPageNum - 1) * this.monthPageSize
      return this.monthList.slice(start, start + this.monthPageSize)
    },
    itemTableData() {
      const start = (this.itemPageNum - 1) * this.itemPageSize
      return this.itemList.slice(start, start + this.itemPageSize)
    }
  },
  created() {
    this.syncCommunityId()
    this.getList()
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
    this.disposeCharts()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (newValue === oldValue) {
        return
      }
      this.syncCommunityId()
      this.getList()
    }
  },
  methods: {
    getCurrentPropertyId() {
      const routeCommunityId = this.$route?.query?.communityId
      return routeCommunityId || getPropertyId() || null
    },
    syncCommunityId() {
      this.queryParams.communityId = this.getCurrentPropertyId()
    },
    getList() {
      this.loading = true
      this.syncCommunityId()
      getFinanceAll(this.queryParams).then((response) => {
        const data = response.data || {}
        this.overview = data.overview || {}
        this.monthList = data.month || []
        this.itemList = data.item || []
        this.monthPageNum = 1
        this.itemPageNum = 1
        this.$nextTick(() => this.renderCharts())
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.billYear = new Date().getFullYear()
      this.syncCommunityId()
      this.handleQuery()
    },
    formatAmount(value) {
      const amount = Number(value || 0)
      return amount.toFixed(2)
    },
    formatRate(value) {
      const rate = Number(value || 0)
      if (!Number.isFinite(rate)) {
        return '0%'
      }
      return `${(rate * 100).toFixed(0)}%`
    },
    renderCharts() {
      this.renderMonthChart()
      this.renderItemChart()
    },
    renderMonthChart() {
      if (!this.$refs.monthChart) return
      if (!this.monthChart) {
        this.monthChart = echarts.init(this.$refs.monthChart)
      }
      const rows = this.monthList || []
      this.monthChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis' },
        legend: { textStyle: { color: 'rgba(255,255,255,0.70)' } },
        grid: { left: 48, right: 24, top: 48, bottom: 36, containLabel: true },
        xAxis: {
          type: 'category',
          data: rows.map((item) => item.statMonth),
          axisLine: { lineStyle: { color: 'rgba(251,228,222,0.22)' } },
          axisTick: { show: false },
          axisLabel: { color: 'rgba(255,255,255,0.58)' }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: 'rgba(255,255,255,0.58)' },
          splitLine: { lineStyle: { color: 'rgba(251,228,222,0.10)' } }
        },
        series: [
          { name: '应收', type: 'bar', data: rows.map((item) => Number(item.receivableAmount || 0)), itemStyle: { color: '#FBE4DE' } },
          { name: '实收', type: 'line', smooth: true, data: rows.map((item) => Number(item.receivedAmount || 0)), lineStyle: { color: '#EC797B', width: 3 }, itemStyle: { color: '#EC797B' } },
          { name: '未收', type: 'line', smooth: true, data: rows.map((item) => Number(item.unpaidAmount || 0)), lineStyle: { color: '#F4C87D', width: 3 }, itemStyle: { color: '#F4C87D' } }
        ]
      })
    },
    renderItemChart() {
      if (!this.$refs.itemChart) return
      if (!this.itemChart) {
        this.itemChart = echarts.init(this.$refs.itemChart)
      }
      const rows = this.itemList || []
      this.itemChart.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis' },
        grid: { left: 48, right: 24, top: 36, bottom: 46, containLabel: true },
        xAxis: {
          type: 'category',
          data: rows.map((item) => item.itemName || '未命名'),
          axisLine: { lineStyle: { color: 'rgba(251,228,222,0.22)' } },
          axisTick: { show: false },
          axisLabel: { color: 'rgba(255,255,255,0.58)', interval: 0, rotate: 24 }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: 'rgba(255,255,255,0.58)' },
          splitLine: { lineStyle: { color: 'rgba(251,228,222,0.10)' } }
        },
        series: [
          {
            name: '实收金额',
            type: 'bar',
            data: rows.map((item) => Number(item.receivedAmount || 0)),
            itemStyle: { color: '#EC797B', borderRadius: [8, 8, 0, 0] }
          }
        ]
      })
    },
    resizeCharts() {
      this.monthChart?.resize()
      this.itemChart?.resize()
    },
    disposeCharts() {
      if (this.monthChart) {
        this.monthChart.dispose()
        this.monthChart = null
      }
      if (this.itemChart) {
        this.itemChart.dispose()
        this.itemChart = null
      }
    },
    noop() {}
  }
}
</script>

<style scoped>
.finance-stat-page {
  overflow: visible;
}

.finance-stat-page::before {
  display: none;
}

.mb16 {
  margin-bottom: 16px;
}

.stat-title {
  color: rgba(255, 255, 255, 0.70);
  font-size: 14px;
  margin-bottom: 12px;
}

.stat-value {
  color: #fbe4de;
  font-size: 28px;
  font-weight: 600;
  line-height: 1.2;
  text-shadow: 0 0 14px rgba(236, 121, 123, 0.14);
}

.stat-danger {
  color: #ec797b;
}

.mini-title {
  color: rgba(255, 255, 255, 0.62);
  font-size: 13px;
  margin-bottom: 8px;
}

.mini-value {
  color: #fbe4de;
  font-size: 24px;
  font-weight: 600;
}

.card-title {
  color: rgba(255, 255, 255, 0.82);
  font-weight: 600;
}

.chart-card {
  background: rgba(255, 255, 255, 0.045);
}

.finance-chart {
  width: 100%;
  height: 320px;
  border-radius: 16px;
  background: rgba(0, 0, 0, 0.10);
}
</style>
