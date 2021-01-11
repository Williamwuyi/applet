const request = require("./request.js");
/**
 *微信登录 获取code值,并将code传递给服务器
 *getCode获取到code 
 * @returns
 */
function getCode() {
  return new Promise(resolve => {
    wx.login({
      success(res) {
        // console.log('res', res)
        if (res.errMsg === 'login:ok') {
          //将code存储到缓存中去 
          // wx.setStorageSync('code', res.code)
          resolve(res)
        } else {
          wx.showToast({
            title: '微信登录失败',
            icon: 'none',
            duration: 1200
          })
        }
      },
      fail() {
        wx.showToast({
          title: '微信登录接口调用失败',
          icon: 'none',
          duration: 1200
        })
      }
    })
  })
};
/**
 *doLogin(),调用getCode(),获取到将登陆态存储到本地
 * 
 * 这里存储的是登陆态和token!!!!
 */
function doLogin(domain, port, contentType) {
  getCode().then(res => {
      // console.log('code值',res.code)
      request.postData(domain, port, contentType, {
        code: res.code
      }, false).then(data => {
          console.log('登陆状态信息', data)
          // resp_code 0  status 0未注册,1已注册
          if (data.resp_code == 0 && data.datas.status == 1) {
            // 将员工信息和登录态保存到缓存,后续跳转的页面要根据获取的值显示对应的组件
            wx.setStorageSync('sessionId', data.datas.token)
            wx.setStorageSync('stuffCode', data.datas.isStaff)
            const stuffCode = wx.getStorageSync('stuffCode')
            // console.log(data.datas.isStaff)
            wx.redirectTo({
              url: '../index/index'
            })
        } else if (data.resp_code == 0 && data.datas.status == 0) {
          // 注册时使用的,区别于sessionId
          // status为0,没有注册,保存注册码
          wx.setStorageSync('resignId', data.datas.resignToken)
          // console.log('resignId', data.datas.resignToken)
          wx.showModal({
            title: '',
            content: '尚未注册',
            showCancel: false,
            success: function (res) {
              if (res.confirm) {
                wx.redirectTo({
                  url: '../regist/regist',
                })
              }
            }
          })
        }
      })
  })
};
/**
 *loginApi()登陆函数,调用dologin()
 *
 * 
 */
function loginApi(domain, port, contentType) {
  // 登陆
  var loginFlag = wx.getStorageSync('sessionId')
  console.log('loginFlag', loginFlag)
  // var that = this
  if (loginFlag) {
    // 检查 session_key 是否过期
    wx.checkSession({
      // session_key 有效(未过期)
      success: function () {
        // console.log('session_key未过期')
        // const stuffCode = wx.getStorageSync('stuffCode')
        // console.log('是否为员工', stuffCode)
          wx.navigateTo({
            url: '../index/index'
          })
      },
      // session_key 过期
      fail: function () {
        // session_key过期，重新登录
        console.log('session_key过期')
        doLogin(domain, port, contentType)
      }
    })
  } else {
    // 无skey，作为首次登录
    console.log('首次登录')
    doLogin(domain, port, contentType)
  }
};
module.exports = {
  loginApi,
  getCode
};