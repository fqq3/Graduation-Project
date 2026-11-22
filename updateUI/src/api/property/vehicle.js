import request from '@/utils/request'

export function listVehicle(query) {
  return request({
    url: '/community/vehicle/list',
    method: 'get',
    params: query
  })
}

export function getVehicle(vehicleId) {
  return request({
    url: '/community/vehicle/' + vehicleId,
    method: 'get'
  })
}

export function addVehicle(data) {
  return request({
    url: '/community/vehicle',
    method: 'post',
    data
  })
}

export function updateVehicle(data) {
  return request({
    url: '/community/vehicle',
    method: 'put',
    data
  })
}

export function delVehicle(vehicleId) {
  return request({
    url: '/community/vehicle/' + vehicleId,
    method: 'delete'
  })
}
