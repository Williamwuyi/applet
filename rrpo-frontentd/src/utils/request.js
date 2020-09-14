import axios from 'axios'
import {message, Modal, notification} from 'ant-design-vue'
import moment from 'moment'
import store from '../store'
import db from './localstorage'
moment.locale('zh-cn')

// const constURL = 'http://127.0.0.1:9527/'
const constURL = 'http://192.168.10.104:9527/'
// const constURL = 'http://114.115.147.159:9527/'
// 统一配置
let JIEBAO_REQUEST = axios.create({
  baseURL: constURL,
  responseType: 'json',
  validateStatus (status) {
    // 200 外的状态码都认定为失败
    return status === 200
  }
})
// 401防止重复弹窗
let unLogin = false
// 上传文件头部路径
// 拦截请求
JIEBAO_REQUEST.interceptors.request.use((config) => {
  // let expireTime = store.state.account.expireTime
  // let now = moment().format('YYYYMMDDHHmmss')
  // 让token早10秒种过期，提升“请重新登录”弹窗体验
  // if (now - expireTime >= -10) {
  //   Modal.error({
  //     title: '登录已过期',
  //     content: '很抱歉，登录已过期，请重新登录',
  //     okText: '重新登录',
  //     mask: false,
  //     onOk: () => {
  //       return new Promise((resolve, reject) => {
  //         db.clear()
  //         location.reload()
  //       })
  //     }
  //   })
  // }
  // 有 token就带上
  if (store.state.account.token) {
    config.headers.Authentication = store.state.account.token
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// 拦截响应
JIEBAO_REQUEST.interceptors.response.use((config) => {
  return config
}, (error) => {
  if (error.response) {
    let errorMessage = error.response.data === null ? '系统内部异常，请联系网站管理员' : error.response.data.message
    switch (error.response.status) {
      case 404:
        notification.error({
          message: '系统提示',
          description: '很抱歉，资源未找到',
          duration: 4
        })
        break
      case 403:
      case 401:
        if (!unLogin) {
          notification.warn({
            message: '系统提示',
            description: '很抱歉，您无法访问系统资源，可能是因为没有相应权限或者登录已失效',
            duration: 4
          })
          let secondsToGo = 5
          let modal = Modal.error({
            title: '系统提示',
            content: `很抱歉，您无法访问系统资源，可能是因为没有相应权限或者登录已失效, 将在 ${secondsToGo} 秒后自动跳转登录页面.`,
            okText: '重新登录',
            mask: false,
            onOk: () => {
              return new Promise((resolve, reject) => {
                db.clear()
                location.reload()
              })
            }
          })
          const interval = setInterval(() => {
            secondsToGo -= 1
            modal.update({
              content: `很抱歉，您无法访问系统资源，可能是因为没有相应权限或者登录已失效, 将在 ${secondsToGo} 秒后自动跳转登录页面.`
            })
          }, 1000)
          setTimeout(() => {
            clearInterval(interval)
            modal.destroy()
            return new Promise((resolve, reject) => {
              db.clear()
              location.reload()
            })
          }, secondsToGo * 1000)
          unLogin = true
        }
        break
      default:
        notification.error({
          message: '系统提示',
          description: errorMessage,
          duration: 4
        })
        break
    }
  }
  return Promise.reject(error)
})

const request = {
  constURL: constURL,
  post (url, params) {
    return JIEBAO_REQUEST.post(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },
  put (url, params) {
    return JIEBAO_REQUEST.put(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },
  get (url, params) {
    let _params
    if (Object.is(params, undefined)) {
      _params = ''
    } else {
      _params = '?'
      for (let key in params) {
        if (params.hasOwnProperty(key) && params[key] !== null) {
          _params += `${key}=${params[key]}&`
        }
      }
    }
    return JIEBAO_REQUEST.get(`${url}${_params}`)
  },
  delete (url, params) {
    let _params
    if (Object.is(params, undefined)) {
      _params = ''
    } else {
      _params = '?'
      for (let key in params) {
        if (params.hasOwnProperty(key) && params[key] !== null) {
          _params += `${key}=${params[key]}&`
        }
      }
    }
    return JIEBAO_REQUEST.delete(`${url}${_params}`)
  },
  export (url, params = {}) {
    message.loading('导出数据中')
    return JIEBAO_REQUEST.post(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      responseType: 'blob'
    }).then((r) => {
      const content = r.data
      const blob = new Blob([content])
      const fileName = `${new Date().getTime()}_导出结果.xlsx`
      if ('download' in document.createElement('a')) {
        const elink = document.createElement('a')
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href)
        document.body.removeChild(elink)
      } else {
        navigator.msSaveBlob(blob, fileName)
      }
    }).catch((r) => {
      console.error(r)
      message.error('导出失败')
    })
  },
  download (url, params, filename) {
    message.loading('文件传输中')
    return JIEBAO_REQUEST.post(url, params, {
      transformRequest: [(params) => {
        let result = ''
        Object.keys(params).forEach((key) => {
          if (!Object.is(params[key], undefined) && !Object.is(params[key], null)) {
            result += encodeURIComponent(key) + '=' + encodeURIComponent(params[key]) + '&'
          }
        })
        return result
      }],
      responseType: 'blob'
    }).then((r) => {
      const content = r.data
      const blob = new Blob([content])
      if ('download' in document.createElement('a')) {
        const elink = document.createElement('a')
        elink.download = filename
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href)
        document.body.removeChild(elink)
      } else {
        navigator.msSaveBlob(blob, filename)
      }
    }).catch((r) => {
      console.error(r)
      message.error('下载失败')
    })
  },
  upload (url, params) {
    return JIEBAO_REQUEST.post(url, params, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default request
