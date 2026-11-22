import request from '@/utils/request'

export function listRepair(query) {
  return request({
    url: '/community/repairOrder/list',
    method: 'get',
    params: query
  })
}

export function getRepair(repairId) {
  return request({
    url: '/community/repairOrder/' + repairId,
    method: 'get'
  })
}

export function addRepair(data) {
  return request({
    url: '/community/repairOrder',
    method: 'post',
    data
  })
}

export function updateRepair(data) {
  return request({
    url: '/community/repairOrder',
    method: 'put',
    data
  })
}

export function delRepair(repairId) {
  return request({
    url: '/community/repairOrder/' + repairId,
    method: 'delete'
  })
}
