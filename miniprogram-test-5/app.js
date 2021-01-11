//app.js
var dayjs = require("./utils/dayjs.min.js");
var Promise = require("./utils/Promise.js");
var request = require("./utils/request.js");
var util = require("./utils/util.js");
var test = require("./utils/test.js");
// 登陆
const NOLOGINCODE = 404 //未登录
const SUCCESS = 200 //成功
// 清除token
var tokenClear

App({
  onLaunch: function () {

    var that = this
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    //获取系统状态栏信息，影响状态栏高度等参数
    wx.getSystemInfo({
      success: e => {
        console.log('全局变量,初始数据',e)
        this.globalData.StatusBar = e.statusBarHeight;
        let custom = wx.getMenuButtonBoundingClientRect();
        this.globalData.Custom = custom;
        this.globalData.CustomBar = custom.bottom + custom.top - e.statusBarHeight;
        this.globalData.CuscreenHeight = e.screenHeight;
        this.globalData.CuscreenWidth = e.screenWidth;
      }
    })
    // 登陆
    // let domain = 'https://192.168.10.146:8003'
    let domain = 'https://rlsb.youhuan.net:8003'
    let port = '/small/auth/wx7690b7064df0e5fb/login'
    let contentType = 'application/x-www-form-urlencoded'
    // util.loginApi(domain, port, contentType);
    // 时间
    this.globalData.time_today = dayjs().format('YYYY年MM月DD日');
    this.globalData.attendanceDate = dayjs().format('YYYY-MM');
  },
  onHide: function () {
    // 退到后台,清除保存token的缓存
    // wx.clearStorageSync('sessionId')
     tokenClear = setTimeout(function () { 
      wx.clearStorageSync('sessionId')
    //  console.log("清理token")
    }, 120000)
    
  },
  onShow(){
    // console.log('显示')
    if (tokenClear) { clearTimeout(tokenClear)}
    console.log('tokenClear', tokenClear)
  },
  globalData: {
    userInfo: null,
    // 图片显示前缀
    imagesUrl: 'https://rlsb.youhuan.net:8003/small/person/show?fileName=',
    // domainList: ['https://192.168.10.146:8003'],
    domainList: ['https://rlsb.youhuan.net:8003'],
    portList: [
      // 0-4
      '/small/person/getPersonInfo',
      '/small/person/getPersonByUnikey',
      '/small/visitor/getOwnListByStatus',
      '/small/visitor/getById/',
      '/small/visitor/refuseVisitor',
      // 5-9
      '/small/visitor/getDoorruleList',
      '/small/visitor/agreeVisitor',
      '/small/person/updatePersonInfo',
      '/small/person/imgUpload',
      '/small/person/getPersonByName',
      // 10-14
      '/small/visitor/save',
      '/small/auth/getCopanyByComName',
      '/small/auth/registry',
      '/small/auth/wx7690b7064df0e5fb/login',
      '/small/auth/checkUsername',
      // 15-29
      '/small/att/getDayStatis',
      '/small/att/getTodayStatis',
      '/small/notice/getOwnList'
    ],
    contentType: ['application/x-www-form-urlencoded', 'application/json']
  },
})
