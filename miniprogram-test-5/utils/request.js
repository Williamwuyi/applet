// 统一发送接口
function postData(domain, port, contentType, data, needToken, tokenName) {
  let SUCCESS = 200;
  let token;
  tokenName == 'resignId' ? (tokenName = 'resignId') : (tokenName = 'sessionId')
  needToken ? (token = wx.getStorageSync(tokenName)) : ''
  console.log('token', token)
  return new Promise((resolve, reject) => {
    wx.request({
      url: domain + port,
      method: "POST",
      header: {
        'content-type': contentType,
        token: token
      },
      data: data,
      success(e) {
        if (e.statusCode == 200) {
          console.log('看一下token过期之后打印的数据', e)
          if (e.data.resp_code == 401) {
            // token 过期
            wx.clearStorageSync('sessionId')
            wx.showModal({
              title: '',
              content: '登陆已过期',
              success: function (res) {
                if (res.confirm) {
                  wx.redirectTo({
                    url: '/pages/login/login',
                  })
                }
              }
            })
          } else {
            resolve(e.data)
            return
          }
        }

      },
      fail(e) {
        wx.showModal({
          title: '提示',
          content: '请求失败',
          showCancel: false
        })
        reject(e)
      }
    })
  })
}
// 统一接收接口
function getData(domain, port, contentType, data, needToken, tokenName) {
  let token;
  tokenName == 'resignId' ? (tokenName = 'resignId') : (tokenName = 'sessionId')
  needToken ? (token = wx.getStorageSync(tokenName)) : ''
  console.log('token', token)
  return new Promise((resolve, reject) => {
    wx.request({
      url: domain + port,
      method: "GET",
      header: {
        token: token,
        'content-type': contentType
      },
      data: data,
      success(e) {
        if (e.statusCode == 200) {
          if (e.data.resp_code == 401) {
            // token 过期
            wx.clearStorageSync('sessionId')
            wx.showModal({
              title: '',
              content: '登陆已过期',
              success: function (res) {
                if (res.confirm) {
                  wx.redirectTo({
                    url: '/pages/login/login',
                  })
                }
              }
            })
          } else {
            resolve(e.data)
            return
          }
        }
      },
      fail(e) {
        console.log(e)
        wx.showModal({
          title: '提示',
          content: '请求失败',
          showCancel: false
        })
        reject(e)
      },
      complete(e) {
      }
    })
  })
}

function test() {
  console.log("test")
};
module.exports = {
  test,
  getData,
  postData,
  // postApi,
  // getApi,
  // postForm,
  // postCompanyNameApi,
  // postInterview,
  // postRequest,
};












// 统一接口前的备份
// 封装一下request请求
// function getApi(url, data = {}, needToken = true) {
//   let SUCCESS = 200;
//   let token;
//   needToken ? (token = wx.getStorageSync('sessionId')) : ''
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.baseurl_1 + url,
//       method: "GET",
//       header: {
//         'content-type': 'application/json',
//         // 'content-type': 'application/x-www-form-urlencoded',
//         token: token
//       },
//       data: data,
//       success(e) {
//         // if (e.data.resp_code == 0) {
//         resolve(e.data)
//         return
//         // }

//       },
//       fail(e) {
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       }
//     })
//   })
// };

// function postApi(url, data = {}, needToken = ture) {
//   let SUCCESS = 200
//   // code 0成功,code 1用户未注册,此处建议增加一个状态量表示用户已经填写注册信息,未审核
//   let code_1 = 0
//   let code_2 = 1
//   needToken ? (data.token = wx.getStorageSync('sessionId')) : ''
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.baseurl + url,
//       // url:'http://192.168.10.131:7777/wx/wx4cb48a5fb606cf5b/login?',
//       method: "POST",
//       //此处可以根据接口文档设置header头,发送建议设置成表单形式,这样以请求体发送的数据会自动拼接成URL
//       header: {
//         'content-type': 'application/x-www-form-urlencoded'
//       },
//       data: data,
//       success(e) {
//         console.log('success_e', e)
//         if (e.statusCode == SUCCESS) {
//           if (e.data.resp_code == code_1) {
//             resolve(e.data)
//           } else if (e.data.resp_code == code_2) {
//             wx.showModal({
//               title: '提示',
//               content: e.data.resp_msg,
//               confirmText: '去注册',
//               success: function (res) {
//                 if (res.confirm) {
//                   wx.navigateTo({
//                     url: '../resign/resign',
//                   })
//                 }
//               }
//             })
//           }
//         } else {
//           wx.showModal({
//             title: '提示',
//             content: e.data.error,
//             showCancel: false
//           })
//           reject(e)
//         }
//       },
//       fail(e) {
//         console.log(e)
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       },
//       complete(e) {
//       }
//     })
//   })
// };
// // 表单提交post 的Api
// function postForm(url, data = {}, needToken = ture) {
//   let token;
//   needToken ? (token = wx.getStorageSync('sessionId')) : '';
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.regist + url,
//       method: "POST",
//       //此处可以根据接口文档设置header头,发送建议设置成表单形式,这样以请求体发送的数据会自动拼接成URL
//       header: {
//         token: token,
//         'content-type': 'application/json',

//       },
//       data: data,
//       success(e) {
//         console.log(e)
//         if (e.data.resp_code == 0) {
//           resolve(e.data)
//           return
//         }
//       },
//       // 
//       fail(e) {
//         console.log(e)
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       },
//       complete(e) {
//       }
//     })
//   })
// };
// // 公司名称校验
// function postCompanyNameApi(url, data = {}, needToken) {
//   let token;
//   needToken ? (token = wx.getStorageSync('sessionId')) : '';
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.checkCompany,
//       method: "POST",
//       //此处可以根据接口文档设置header头,发送建议设置成表单形式,这样以请求体发送的数据会自动拼接成URL
//       header: {
//         'content-type': 'application/x-www-form-urlencoded',
//         token: token
//       },
//       data: data,
//       success(e) {
//         console.log('公司名称校验结果', e.data.resp_code)
//         if (e.statusCode == 200) {
//           // console.log(e)
//           resolve(e.data)
//           return
//         }
//       },
//       fail(e) {
//         console.log(e)
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       },
//       complete(e) {
//       }
//     })
//   })
// };
// // 提交预约
// function postInterview(url, data = {}, needToken = ture) {
//   let token;
//   needToken ? (token = wx.getStorageSync('sessionId')) : '';
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.baseurl_1 + url,
//       method: "POST",
//       //此处可以根据接口文档设置header头,发送建议设置成表单形式,这样以请求体发送的数据会自动拼接成URL
//       header: {
//         token: token,
//         'content-type': 'application/json',
//         // 'content-type': 'application/x-www-form-urlencoded'
//       },
//       data: data,
//       success(e) {
//         if (e.statusCode == 200) {
//           console.log(e)
//           resolve(e.data)
//           return
//         }
//       },
//       fail(e) {
//         console.log(e)
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       },
//       complete(e) {
//       }
//     })
//   })
// };
// // 表单类型提交
// function postRequest(url, data = {}, needToken = ture) {
//   let token;
//   needToken ? (token = wx.getStorageSync('sessionId')) : '';
//   return new Promise((resolve, reject) => {
//     wx.request({
//       url: getApp().globalData.baseurl_1 + url,
//       method: "POST",
//       //此处可以根据接口文档设置header头,发送建议设置成表单形式,这样以请求体发送的数据会自动拼接成URL
//       header: {
//         token: token,
//         // 'content-type': 'application/json',
//         'content-type': 'application/x-www-form-urlencoded'
//       },
//       data: data,
//       success(e) {
//         if (e.statusCode == 200) {
//           // console.log(e)
//           resolve(e.data)
//           return
//         }
//       },
//       fail(e) {
//         console.log(e)
//         wx.showModal({
//           title: '提示',
//           content: '请求失败',
//           showCancel: false
//         })
//         reject(e)
//       },
//       complete(e) {
//       }
//     })
//   })
// };
