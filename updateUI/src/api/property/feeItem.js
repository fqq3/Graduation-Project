import request from '@/utils/request'

export function listFeeItem(query) {
  return request({
    url: '/community/feeItem/list',
    method: 'get',
    params: query
  })
}

export function getFeeItem(itemId) {
  return request({
    url: `/community/feeItem/${itemId}`,
    method: 'get'
  })
}

export function addFeeItem(data) {
  return request({
    url: '/community/feeItem',
    method: 'post',
    data
  })
}

export function updateFeeItem(data) {
  return request({
    url: '/community/feeItem',
    method: 'put',
    data
  })
}

export function delFeeItem(itemId) {
  return request({
    url: `/community/feeItem/${itemId}`,
    method: 'delete'
  })
}
