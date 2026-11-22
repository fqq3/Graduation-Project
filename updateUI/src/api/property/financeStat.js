import request from '@/utils/request'

export function getFinanceOverview(query) {
  return request({
    url: '/community/financeStat/overview',
    method: 'get',
    params: query
  })
}

export function getFinanceMonth(query) {
  return request({
    url: '/community/financeStat/month',
    method: 'get',
    params: query
  })
}

export function getFinanceItem(query) {
  return request({
    url: '/community/financeStat/item',
    method: 'get',
    params: query
  })
}

export function getFinanceAll(query) {
  return request({
    url: '/community/financeStat/all',
    method: 'get',
    params: query
  })
}
