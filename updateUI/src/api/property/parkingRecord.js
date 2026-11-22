import request from '@/utils/request'

export function listParkingRecord(query) {
  return request({
    url: '/community/parkingRecord/list',
    method: 'get',
    params: query
  })
}

export function getParkingRecord(recordId) {
  return request({
    url: '/community/parkingRecord/' + recordId,
    method: 'get'
  })
}

export function addParkingRecord(data) {
  return request({
    url: '/community/parkingRecord',
    method: 'post',
    data
  })
}

export function updateParkingRecord(data) {
  return request({
    url: '/community/parkingRecord',
    method: 'put',
    data
  })
}

export function delParkingRecord(recordId) {
  return request({
    url: '/community/parkingRecord/' + recordId,
    method: 'delete'
  })
}
