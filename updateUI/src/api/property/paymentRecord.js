import request from '@/utils/request'

export function listPaymentRecord(query) {
  return request({
    url: '/community/paymentRecord/list',
    method: 'get',
    params: query
  })
}

export function getPaymentRecord(paymentId) {
  return request({
    url: `/community/paymentRecord/${paymentId}`,
    method: 'get'
  })
}

export function addPaymentRecord(data) {
  return request({
    url: '/community/paymentRecord',
    method: 'post',
    data
  })
}

export function updatePaymentRecord(data) {
  return request({
    url: '/community/paymentRecord',
    method: 'put',
    data
  })
}

export function delPaymentRecord(paymentId) {
  return request({
    url: `/community/paymentRecord/${paymentId}`,
    method: 'delete'
  })
}
