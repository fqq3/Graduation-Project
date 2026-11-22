import request from '@/utils/request'

export function listOwnerRoom(query) {
  return request({
    url: '/system/ownerRoom/list',
    method: 'get',
    params: query
  })
}

export function getOwnerRoom(ownerRoomId) {
  return request({
    url: `/system/ownerRoom/${ownerRoomId}`,
    method: 'get'
  })
}

export function addOwnerRoom(data) {
  return request({
    url: '/system/ownerRoom',
    method: 'post',
    data
  })
}

export function updateOwnerRoom(data) {
  return request({
    url: '/system/ownerRoom',
    method: 'put',
    data
  })
}

export function delOwnerRoom(ownerRoomId) {
  return request({
    url: `/system/ownerRoom/${ownerRoomId}`,
    method: 'delete'
  })
}

export function exportOwnerRoom(query) {
  return request({
    url: '/system/ownerRoom/export',
    method: 'get',
    params: query
  })
}

export function listOwnerRoomRecord(query) {
  return request({
    url: '/system/ownerRoomRecord/list',
    method: 'get',
    params: query
  })
}

export function getOwnerRoomRecord(recordId) {
  return request({
    url: `/system/ownerRoomRecord/${recordId}`,
    method: 'get'
  })
}

export function addOwnerRoomRecord(data) {
  return request({
    url: '/system/ownerRoomRecord',
    method: 'post',
    data
  })
}

export function updateOwnerRoomRecord(data) {
  return request({
    url: '/system/ownerRoomRecord',
    method: 'put',
    data
  })
}

export function delOwnerRoomRecord(recordId) {
  return request({
    url: `/system/ownerRoomRecord/${recordId}`,
    method: 'delete'
  })
}

// Backward-compatible aliases for the current page usage.
export const listRoom = listOwnerRoom
export const getRoom = getOwnerRoom
export const addRoom = addOwnerRoom
export const updateRoom = updateOwnerRoom
export const delRoom = delOwnerRoom
export const exportRoom = exportOwnerRoom
