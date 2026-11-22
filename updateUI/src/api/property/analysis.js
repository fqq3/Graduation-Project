import request from '@/utils/request'

export function getComplaintWordCloud(query) {
  return request({
    url: '/community/analysis/complaint/wordcloud',
    method: 'get',
    params: query
  })
}

export function getParkingPeak(query) {
  return request({
    url: '/community/analysis/parking/peak',
    method: 'get',
    params: query,
    timeout: 30000
  })
}

export function getCommunityRanking(query) {
  return request({
    url: '/community/analysis/community/ranking',
    method: 'get',
    params: query,
    timeout: 30000
  })
}

export function getComplaintTfidf(query) {
  return request({
    url: '/community/analysis/complaint/tfidf',
    method: 'get',
    params: query,
    timeout: 30000
  })
}


export function getCommunityRankingIndicators() {
  return request({
    url: '/community/analysis/community/ranking/indicators',
    method: 'get',
    timeout: 30000
  })
}
