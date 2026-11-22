import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const PropertyKey = 'propertyId'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getPropertyId() {
  return window.localStorage.getItem(PropertyKey)
}

export function setPropertyId(propertyId) {
  window.localStorage.setItem(PropertyKey, propertyId)
}

export function removePropertyId() {
  window.localStorage.removeItem(PropertyKey)
}
