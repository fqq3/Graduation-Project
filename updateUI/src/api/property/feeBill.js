import request from '@/utils/request'

export function listFeeBill(query) {
  return request({
    url: '/community/feeBill/list',
    method: 'get',
    params: query
  })
}

export function getFeeBill(billId) {
  return request({
    url: `/community/feeBill/${billId}`,
    method: 'get'
  })
}

export function addFeeBill(data) {
  return request({
    url: '/community/feeBill',
    method: 'post',
    data
  })
}

export function updateFeeBill(data) {
  return request({
    url: '/community/feeBill',
    method: 'put',
    data
  })
}

export function delFeeBill(billId) {
  return request({
    url: `/community/feeBill/${billId}`,
    method: 'delete'
  })
}
