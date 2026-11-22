import request from '@/utils/request'

// 查询小区信息列表
export function listCommunity(query) {
  return request({
    url: '/community/list',
    method: 'get',
    params: query
  })
}

// 查询小区信息详细
export function getCommunity(communityId) {
  return request({
    url: '/community/' + communityId,
    method: 'get'
  })
}

// 查询小区地图概览
export function getCommunityOverview(communityId) {
  return request({
    url: `/community/overview/${encodeURIComponent(communityId)}`,
    method: 'get'
  })
}

export function getNearbyCommunityOverviews(communityId, radiusKm = 5) {
  return request({
    url: `/community/overview/nearby/${encodeURIComponent(communityId)}`,
    method: 'get',
    params: {
      radiusKm
    }
  })
}


// 新增小区信息
export function addCommunity(data) {
  return request({
    url: '/community',
    method: 'post',
    data: data
  })
}

// 修改小区信息
export function updateCommunity(data) {
  return request({
    url: '/community',
    method: 'put',
    data: data
  })
}

// 删除小区信息
export function delCommunity(communityId) {
  return request({
    url: '/community/' + communityId,
    method: 'delete'
  })
}

// 导出小区信息
export function exportCommunity(query) {
  return request({
    url: '/exportExcel/exportCommunityExcel',
    method: 'get',
    params: query
  })
}

// 获取区划信息
export function getAreaTree() {
  return request({
    url: '/system/area/tree',
    method: 'get'
  })
}

// 获取小区树
export function getCommunityTree() {
  return request({
    url: '/community/queryPullDown',
    method: 'get'
  })
}

// 小区下拉
export function queryPullDown(query) {
  return request({
    url: '/community/queryPullDown',
    method: 'get',
    params: query
  })
}
