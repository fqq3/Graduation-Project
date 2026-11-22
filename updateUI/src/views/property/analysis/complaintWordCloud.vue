<template>
  <div class="app-container analysis-page">
    <section class="analysis-hero">
      <div>
        <h1>投诉建议词云</h1>
        <span>按当前小区统计投诉与建议高频关键词，点击词项可下钻到投诉建议列表。</span>
      </div>
    </section>

    <el-form :inline="true" :model="queryParams" class="analysis-filter">
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
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

    <el-skeleton v-if="loading" :rows="8" animated />
    <el-empty v-else-if="loaded && !hasData" description="当前筛选条件下暂无投诉建议" />

    <template v-else-if="loaded">
      <section class="cloud-grid">
        <article class="cloud-card">
          <div class="card-title">
            <span>投诉词云</span>
            <strong>{{ complaintTotal }}</strong>
          </div>
          <div v-if="complaintCloudWords.length" class="word-cloud" aria-label="投诉词云">
            <button
              v-for="word in complaintCloudWords"
              :key="'complaint-' + word.name"
              type="button"
              class="cloud-word"
              :style="word.cloudStyle"
              @click="handleWordClick(word)"
            >
              {{ word.name }}
            </button>
          </div>
          <el-empty v-else description="当前投诉建议无可分析文本" />
        </article>

        <article class="cloud-card">
          <div class="card-title">
            <span>建议词云</span>
            <strong>{{ suggestTotal }}</strong>
          </div>
          <div v-if="suggestCloudWords.length" class="word-cloud word-cloud-suggest" aria-label="建议词云">
            <button
              v-for="word in suggestCloudWords"
              :key="'suggest-' + word.name"
              type="button"
              class="cloud-word"
              :style="word.cloudStyle"
              @click="handleWordClick(word)"
            >
              {{ word.name }}
            </button>
          </div>
          <el-empty v-else description="当前投诉建议无可分析文本" />
        </article>
      </section>

      <!-- <section class="top-card">
        <div class="card-title">
          <span>Top10 高频词对比</span>
          <strong>{{ topWords.length }}</strong>
        </div>
        <div v-if="topWords.length" class="top-list">
          <div v-for="item in topWords" :key="item.name" class="top-row" @click="handleTopWordClick(item)">
            <span class="top-word">{{ item.name }}</span>
            <div class="bar-wrap">
              <span class="bar complaint" :style="{ width: getPercent(item.complaintValue) }"></span>
              <span class="bar suggest" :style="{ width: getPercent(item.suggestValue) }"></span>
            </div>
            <span class="top-count">投诉 {{ item.complaintValue }} / 建议 {{ item.suggestValue }}</span>
          </div>
        </div>
        <el-empty v-else description="当前投诉建议无可分析文本" />
      </section> -->
<!-- 相关样式：top-list 、top-card、top-word、bar-wrap、top-count-->
      <section class="tfidf-card">
        <div class="card-title tfidf-title">
          <div>
            <span>TF-IDF 特征关键词</span>
            <p>突出当前筛选范围内更具有区分度的投诉建议问题词。</p>
          </div>
          <el-radio-group v-model="tfidfType" size="small" @change="loadTfidf">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="Complaint">投诉</el-radio-button>
            <el-radio-button label="Suggest">建议</el-radio-button>
          </el-radio-group>
        </div>
        <el-skeleton v-if="tfidfLoading" :rows="5" animated />
        <el-empty v-else-if="!tfidfKeywords.length" description="当前筛选条件下暂无特征关键词" />
        <template v-else>
          <div class="tfidf-bars">
            <div v-for="item in topTfidfKeywords" :key="'tfidf-bar-' + item.word" class="tfidf-bar-row">
              <span>{{ item.word }}</span>
              <div class="tfidf-bar-wrap">
                <i :style="{ width: formatWeightPercent(item.weight) }"></i>
              </div>
              <strong>{{ formatWeight(item.weight) }}</strong>
            </div>
          </div>
          <el-table :data="tfidfKeywords" class="tfidf-table" max-height="360">
            <el-table-column prop="word" label="关键词" min-width="120" />
            <el-table-column prop="tf" label="TF" width="90" align="center" />
            <el-table-column prop="df" label="DF" width="90" align="center" />
            <el-table-column label="IDF" width="110" align="center">
              <template #default="scope">{{ formatNumber(scope.row.idf) }}</template>
            </el-table-column>
            <el-table-column label="TF-IDF" width="120" align="center">
              <template #default="scope">{{ formatNumber(scope.row.tfidf) }}</template>
            </el-table-column>
            <el-table-column label="权重" width="100" align="center">
              <template #default="scope">{{ formatWeight(scope.row.weight) }}</template>
            </el-table-column>
          </el-table>
        </template>
      </section>

      <section class="detail-actions">
        <el-button type="primary" @click="goDetail">查看详情</el-button>
      </section>
    </template>
  </div>
</template>

<script>
import { getComplaintTfidf, getComplaintWordCloud } from '@/api/property/analysis'
import { getPropertyId, setPropertyId } from '@/utils/auth'

function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}${month}${day}`
}

const CLOUD_SIZE = 520
const CLOUD_CENTER = CLOUD_SIZE / 2
const CLOUD_RADIUS = CLOUD_SIZE * 0.47

function estimateTextWidth(text, fontSize) {
  const chars = String(text || '').split('')
  const weight = chars.reduce((total, char) => {
    return total + (/^[\x00-\xff]$/.test(char) ? 0.56 : 1.02)
  }, 0)
  return Math.max(fontSize, weight * fontSize) + 12
}

function isRectInsideCircle(rect) {
  const points = [
    [rect.left, rect.top],
    [rect.right, rect.top],
    [rect.left, rect.bottom],
    [rect.right, rect.bottom]
  ]
  return points.every(([x, y]) => {
    const dx = x - CLOUD_CENTER
    const dy = y - CLOUD_CENTER
    return Math.sqrt(dx * dx + dy * dy) <= CLOUD_RADIUS
  })
}

function hasCollision(rect, placed) {
  return placed.some((item) => {
    return !(
      rect.right < item.left ||
      rect.left > item.right ||
      rect.bottom < item.top ||
      rect.top > item.bottom
    )
  })
}

function createRect(x, y, text, fontSize) {
  const width = estimateTextWidth(text, fontSize)
  const height = Math.max(18, fontSize * 1.18)
  const padding = 4
  return {
    left: x - width / 2 - padding,
    right: x + width / 2 + padding,
    top: y - height / 2 - padding,
    bottom: y + height / 2 + padding
  }
}

function findCloudPosition(word, fontSize, placed, angleOffset) {
  const centerRect = createRect(CLOUD_CENTER, CLOUD_CENTER, word.name, fontSize)
  if (!placed.length && isRectInsideCircle(centerRect)) {
    return {
      x: CLOUD_CENTER,
      y: CLOUD_CENTER,
      rect: centerRect
    }
  }

  const maxSteps = 3600
  for (let step = 0; step < maxSteps; step += 1) {
    const angle = step * 0.34 + angleOffset
    const radius = 2.9 * Math.sqrt(step)
    if (radius > CLOUD_RADIUS) {
      break
    }
    const x = CLOUD_CENTER + Math.cos(angle) * radius
    const y = CLOUD_CENTER + Math.sin(angle) * radius
    const rect = createRect(x, y, word.name, fontSize)
    if (isRectInsideCircle(rect) && !hasCollision(rect, placed)) {
      return { x, y, rect }
    }
  }

  return null
}

function buildCircularCloudWords(list, palette, angleOffset) {
  const sorted = (list || [])
    .map((item) => ({
      ...item,
      value: Number(item.value || 0)
    }))
    .filter((item) => item.name)
    .sort((a, b) => b.value - a.value)

  if (!sorted.length) {
    return []
  }

  const max = Math.max(1, ...sorted.map((item) => item.value))
  const placedRects = []
  const result = []

  sorted.forEach((word, index) => {
    const ratio = word.value / max
    const lengthPenalty = Math.min(7, Math.max(0, String(word.name).length - 4))
    const baseSize = 13 + Math.round(Math.pow(ratio, 0.72) * 25) - lengthPenalty
    let fontSize = Math.max(11, baseSize)
    let position = null

    while (fontSize >= 10 && !position) {
      position = findCloudPosition(word, fontSize, placedRects, angleOffset + index * 0.17)
      if (!position) {
        fontSize -= 1
      }
    }

    if (!position) {
      return
    }

    placedRects.push(position.rect)
    result.push({
      ...word,
      cloudStyle: {
        left: `${((position.x / CLOUD_SIZE) * 100).toFixed(2)}%`,
        top: `${((position.y / CLOUD_SIZE) * 100).toFixed(2)}%`,
        fontSize: `${fontSize}px`,
        color: palette[index % palette.length],
        opacity: `${Math.min(1, 0.72 + ratio * 0.32)}`,
        zIndex: `${Math.max(1, Math.round(ratio * 10) + 1)}`,
        fontWeight: index === 0 ? 800 : 700,
        textShadow: index === 0 ? '0 0 24px rgba(236, 121, 123, 0.28)' : '0 0 16px rgba(236, 121, 123, 0.18)'
      }
    })
  })

  return result
}

export default {
  name: 'ComplaintWordCloud',
  data() {
    return {
      loading: false,
      loaded: false,
      loadError: '',
      dateRange: [],
      queryParams: {
        communityId: null,
        beginTime: '',
        endTime: ''
      },
      complaintWords: [],
      suggestWords: [],
      tfidfLoading: false,
      tfidfType: '',
      tfidfKeywords: []
    }
  },
  computed: {
    hasData() {
      return this.complaintWords.length > 0 || this.suggestWords.length > 0 || this.tfidfKeywords.length > 0
    },
    complaintTotal() {
      return this.complaintWords.reduce((total, item) => total + Number(item.value || 0), 0)
    },
    suggestTotal() {
      return this.suggestWords.reduce((total, item) => total + Number(item.value || 0), 0)
    },
    complaintCloudWords() {
      return buildCircularCloudWords(this.complaintWords, ['#EC797B', '#FBE4DE', '#F3B5B6', '#F1CDC6', '#E89C9E'], -0.18)
    },
    suggestCloudWords() {
      return buildCircularCloudWords(this.suggestWords, ['#FBE4DE', '#EC797B', '#F2C8C1', '#F0A9AB', '#E8B9AE'], 0.22)
    },
    maxTopValue() {
      return Math.max(1, ...this.topWords.map((item) => Math.max(item.complaintValue, item.suggestValue)))
    },
    topWords() {
      const map = new Map()
      this.complaintWords.forEach((item) => {
        map.set(item.name, {
          name: item.name,
          complaintValue: Number(item.value || 0),
          suggestValue: 0
        })
      })
      this.suggestWords.forEach((item) => {
        const target = map.get(item.name) || { name: item.name, complaintValue: 0, suggestValue: 0 }
        target.suggestValue = Number(item.value || 0)
        map.set(item.name, target)
      })
      return Array.from(map.values())
        .sort((a, b) => (b.complaintValue + b.suggestValue) - (a.complaintValue + a.suggestValue))
        .slice(0, 10)
    },
    topTfidfKeywords() {
      return (this.tfidfKeywords || []).slice(0, 10)
    }
  },
  created() {
    this.initDefaultRange()
    this.syncRouteCommunityId()
    this.handleQuery()
  },
  watch: {
    '$route.query.communityId'(newValue, oldValue) {
      if (String(newValue || '') === String(oldValue || '')) {
        return
      }
      this.syncRouteCommunityId()
      this.handleQuery()
    }
  },
  methods: {
    initDefaultRange() {
      const end = new Date()
      const begin = new Date(end)
      begin.setFullYear(end.getFullYear() - 1)
      this.queryParams.beginTime = formatDate(begin)
      this.queryParams.endTime = formatDate(end)
      this.dateRange = [this.queryParams.beginTime, this.queryParams.endTime]
    },
    syncRouteCommunityId() {
      const communityId = this.$route.query.communityId || getPropertyId() || null
      this.queryParams.communityId = communityId
      if (communityId) {
        setPropertyId(communityId)
      }
    },
    syncDateRange() {
      const [beginTime, endTime] = this.dateRange || []
      this.queryParams.beginTime = beginTime || ''
      this.queryParams.endTime = endTime || ''
    },
    handleQuery() {
      this.syncDateRange()
      this.loading = true
      this.loadError = ''
      this.loadTfidf()
      getComplaintWordCloud(this.queryParams)
        .then((wordCloudRes) => {
          const data = wordCloudRes.data || {}
          this.complaintWords = data.complaintWords || []
          this.suggestWords = data.suggestWords || []
        })
        .catch(() => {
          this.loadError = '投诉建议词云数据加载失败，请重试'
          this.complaintWords = []
          this.suggestWords = []
        })
        .finally(() => {
          this.loaded = true
          this.loading = false
        })
    },
    resetQuery() {
      this.initDefaultRange()
      this.syncRouteCommunityId()
      this.handleQuery()
    },
    handleWordClick(word) {
      if (!word || !word.name) {
        return
      }
      this.syncDateRange()
      this.$router.push({
        path: '/communityService/suggest',
        query: {
          communityId: this.queryParams.communityId,
          keyword: word.name,
          complaintSuggestContent: word.name
        }
      })
    },
    goDetail() {
      this.$router.push({
        path: '/communityService/suggest',
        query: {
          communityId: this.queryParams.communityId
        }
      })
    },
    handleTopWordClick(item) {
      this.handleWordClick(item)
    },
    loadTfidf() {
      this.syncDateRange()
      this.tfidfLoading = true
      getComplaintTfidf({
        ...this.queryParams,
        type: this.tfidfType,
        topN: 30
      })
        .then((res) => {
          this.tfidfKeywords = res.data?.keywords || []
        })
        .catch(() => {
          this.tfidfKeywords = []
        })
        .finally(() => {
          this.tfidfLoading = false
        })
    },
    formatNumber(value) {
      const number = Number(value || 0)
      return Number.isFinite(number) ? number.toFixed(4) : '0.0000'
    },
    formatWeight(value) {
      const number = Number(value || 0)
      return Number.isFinite(number) ? `${(number * 100).toFixed(0)}%` : '0%'
    },
    formatWeightPercent(value) {
      const number = Number(value || 0)
      if (!Number.isFinite(number)) {
        return '0%'
      }
      return `${Math.max(4, Math.round(number * 100))}%`
    },
    getPercent(value) {
      return `${Math.max(4, Math.round((Number(value || 0) / this.maxTopValue) * 100))}%`
    }
  }
}
</script>

<style lang="scss" scoped>
.analysis-page {
  min-height: calc(100vh - 84px);
  padding-bottom: 32px;
  overflow: visible;
  background:
    radial-gradient(circle at 0% 0%, rgba(251, 228, 222, 0.14), transparent 30%),
    radial-gradient(circle at 100% 16%, rgba(236, 121, 123, 0.11), transparent 32%),
    linear-gradient(135deg, rgba(49, 37, 32, 0.96) 0%, rgba(43, 33, 29, 0.92) 100%);
}

.analysis-hero,
.cloud-card,
// .top-card,
.tfidf-card {
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
.cloud-card::before,
// .top-card::before,
.tfidf-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    radial-gradient(circle at 10% 0%, rgba(251, 228, 222, 0.12), transparent 36%),
    radial-gradient(circle at 96% 14%, rgba(236, 121, 123, 0.10), transparent 30%);
  pointer-events: none;
}

.analysis-hero > *,
.cloud-card > *,
// .top-card > *,
.tfidf-card > * {
  position: relative;
  z-index: 1;
}

.analysis-hero {
  margin-bottom: 16px;
  padding: 24px 28px;

  p {
    margin: 0 0 8px;
    color: #ec797b;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.18em;
    text-transform: uppercase;
  }

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

.analysis-alert {
  margin-bottom: 16px;
}

.cloud-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.cloud-card,
// .top-card,
.tfidf-card {
  padding: 22px;
}

// .top-card,
.tfidf-card {
  margin-top: 16px;
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
    font-size: 24px;
    text-shadow: 0 0 18px rgba(236, 121, 123, 0.18);
  }
}

.word-cloud {
  position: relative;
  width: min(100%, 520px);
  margin: 0 auto;
  aspect-ratio: 1 / 1;
  min-height: 420px;
  border: 1px solid rgba(251, 228, 222, 0.10);
  border-radius: 50%;
  overflow: hidden;
  background:
    radial-gradient(circle at 50% 50%, rgba(251, 228, 222, 0.11), rgba(0, 0, 0, 0.04) 42%, rgba(0, 0, 0, 0.18) 78%),
    radial-gradient(circle at 20% 18%, rgba(251, 228, 222, 0.13), transparent 34%),
    radial-gradient(circle at 78% 72%, rgba(236, 121, 123, 0.14), transparent 36%),
    rgba(0, 0, 0, 0.10);
  box-shadow: inset 0 0 0 1px rgba(251, 228, 222, 0.06), inset 0 18px 42px rgba(0, 0, 0, 0.16);
}

.word-cloud::before,
.word-cloud::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.word-cloud::before {
  inset: 8%;
  border: 1px dashed rgba(251, 228, 222, 0.09);
}

.word-cloud::after {
  inset: 18%;
  border: 1px solid rgba(236, 121, 123, 0.08);
}

.word-cloud-suggest {
  background:
    radial-gradient(circle at 50% 50%, rgba(236, 121, 123, 0.10), rgba(0, 0, 0, 0.04) 42%, rgba(0, 0, 0, 0.18) 78%),
    radial-gradient(circle at 22% 20%, rgba(251, 228, 222, 0.12), transparent 34%),
    radial-gradient(circle at 80% 74%, rgba(236, 121, 123, 0.11), transparent 38%),
    rgba(0, 0, 0, 0.10);
}

.cloud-word {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  padding: 2px 6px;
  border: 0;
  background: transparent;
  cursor: pointer;
  line-height: 1.15;
  white-space: nowrap;
  transition: transform 0.18s ease, filter 0.18s ease, text-shadow 0.18s ease;
}

.cloud-word:hover {
  transform: translate(-50%, -50%) scale(1.08);
  filter: brightness(1.08);
  text-shadow: 0 0 22px rgba(236, 121, 123, 0.40);
}

// .top-list {
//   display: grid;
//   gap: 12px;
// }

.top-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) 180px;
  gap: 14px;
  align-items: center;
  padding: 12px;
  border: 1px solid rgba(251, 228, 222, 0.10);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.045);
  cursor: pointer;
  transition: transform 0.18s ease, border-color 0.18s ease, background 0.18s ease, box-shadow 0.18s ease;
}

.top-row:hover {
  transform: translateY(-1px);
  border-color: rgba(236, 121, 123, 0.34);
  background: rgba(236, 121, 123, 0.08);
  box-shadow: 0 12px 26px rgba(236, 121, 123, 0.12);
}

// .top-word {
//   color: #fbe4de;
//   font-weight: 700;
// }

// .bar-wrap {
//   display: grid;
//   gap: 5px;
// }

.bar {
  display: block;
  height: 8px;
  border-radius: 999px;
}

.bar.complaint {
  background: linear-gradient(90deg, rgba(236, 121, 123, 0.36), #ec797b);
  box-shadow: 0 0 12px rgba(236, 121, 123, 0.26);
}

.bar.suggest {
  background: linear-gradient(90deg, rgba(251, 228, 222, 0.30), #fbe4de);
  box-shadow: 0 0 12px rgba(251, 228, 222, 0.18);
}

// .top-count {
//   color: rgba(255, 255, 255, 0.56);
//   font-size: 13px;
//   text-align: right;
// }


.tfidf-title {
  align-items: flex-start;

  p {
    margin: 6px 0 0;
    color: rgba(255, 255, 255, 0.48);
    font-size: 13px;
  }
}

.tfidf-bars {
  display: grid;
  gap: 10px;
  margin-bottom: 18px;
}

.tfidf-bar-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) 64px;
  gap: 12px;
  align-items: center;
  color: rgba(255, 255, 255, 0.72);

  span {
    color: #fbe4de;
    font-weight: 700;
  }

  strong {
    color: rgba(255, 255, 255, 0.56);
    text-align: right;
  }
}

.tfidf-bar-wrap {
  height: 9px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(251, 228, 222, 0.10);

  i {
    display: block;
    height: 100%;
    border-radius: inherit;
    background: linear-gradient(90deg, rgba(236, 121, 123, 0.32), #ec797b);
    box-shadow: 0 0 12px rgba(236, 121, 123, 0.24);
  }
}

.tfidf-table {
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

.detail-actions {
  display: flex;
  justify-content: center;
  margin-top: 18px;
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

:deep(.el-empty__description p) {
  color: rgba(255, 255, 255, 0.50);
}

:deep(.el-skeleton__item) {
  background: linear-gradient(90deg, rgba(255,255,255,0.06) 25%, rgba(251,228,222,0.12) 37%, rgba(255,255,255,0.06) 63%);
}

@media (max-width: 960px) {
  .cloud-grid,
  .top-row {
    grid-template-columns: 1fr;
  }

  // .top-count {
  //   text-align: left;
  // }

  .word-cloud {
    width: min(100%, 420px);
  }
}

@media (max-width: 560px) {
  .word-cloud {
    width: min(100%, 340px);
    min-height: 320px;
  }

  .cloud-word {
    max-width: 34%;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
