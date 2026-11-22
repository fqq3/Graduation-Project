import request from '@/utils/request'

export function listParkingSpace(query) {
  return request({
    url: '/community/parkingSpace/list',
    method: 'get',
    params: query
  })
}

export function getParkingSpace(spaceId) {
  return request({
    url: '/community/parkingSpace/' + spaceId,
    method: 'get'
  })
}

export function addParkingSpace(data) {
  return request({
    url: '/community/parkingSpace',
    method: 'post',
    data
  })
}

export function updateParkingSpace(data) {
  return request({
    url: '/community/parkingSpace',
    method: 'put',
    data
  })
}

export function delParkingSpace(spaceId) {
  return request({
    url: '/community/parkingSpace/' + spaceId,
    method: 'delete'
  })
}
