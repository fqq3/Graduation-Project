import axios from 'axios'
import { getToken } from '@/utils/auth'

const mimeMap = {
  xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  zip: 'application/zip'
}

const baseUrl = import.meta.env.VITE_APP_BASE_API

export function downLoadZip(str, filename) {
  const url = baseUrl + str
  axios({
    method: 'get',
    url,
    responseType: 'blob',
    headers: { Authorization: `Bearer ${getToken()}` }
  }).then((res) => {
    resolveBlob(res, mimeMap.zip, filename)
  })
}

function resolveFileName(headers, fallbackFileName = 'download') {
  const contentDisposition = headers?.['content-disposition']
  if (contentDisposition) {
    const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
    if (utf8Match?.[1]) {
      return decodeURIComponent(utf8Match[1])
    }

    const plainMatch = contentDisposition.match(/filename=([^;]+)/i)
    if (plainMatch?.[1]) {
      return decodeURIComponent(plainMatch[1]).replace(/\"/g, '')
    }
  }

  return fallbackFileName.endsWith('.zip') ? fallbackFileName : `${fallbackFileName}.zip`
}

/**
 * 解析 blob 响应内容并下载
 * @param {*} res blob 响应内容
 * @param {String} mimeType MIME 类型
 * @param {String} fallbackFileName 后备文件名
 */
export function resolveBlob(res, mimeType, fallbackFileName) {
  const aLink = document.createElement('a')
  const blob = new Blob([res.data], { type: mimeType })
  const fileName = resolveFileName(res.headers, fallbackFileName)
  const objectUrl = URL.createObjectURL(blob)

  aLink.href = objectUrl
  aLink.setAttribute('download', fileName)
  document.body.appendChild(aLink)
  aLink.click()
  document.body.removeChild(aLink)

  window.setTimeout(() => URL.revokeObjectURL(objectUrl), 0)
}
