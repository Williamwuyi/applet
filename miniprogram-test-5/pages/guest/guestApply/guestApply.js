// pages/guest/guestApply/guestApply.js
const app = getApp();
const request = require('../../../utils/request.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imagesUrl: app.globalData.imagesUrl,
    person: {},
    interviewee: {},
    show: false,
    currentEndDate: '2019-10-01',
    currentEndTime: '12:00',
    currentVisiteDate: '2019-10-01',
    currentVisiteTime: '06:00',
    visitDate: '',
    endDate: '',
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const _this = this
    const eventChannel = this.getOpenerEventChannel()
    // 直接调试此页面是请将这个监听注释掉
    eventChannel.on('acceptDataFromOpenerPage', function (data) {
      // console.log('接收到的数据',data)
      _this.setData({
        interviewee: { ...data.data }
      })
    })
    console.log('受访者信息', this.data.interviewee)
    // 获取发起人的个人信息
    let domain = this.data.domainList[0];
    let port = this.data.portList[0];
    let contentType = this.data.contentType[1];
    request.getData(domain, port, contentType, '', true).then(data => {
      if (data.resp_code == 0) {
        this.setData({
          person: { ...data.datas }
        })
        console.log("获取到的个人信息", this.data.person)
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  bindDateChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.currentTarget.dataset.target)
    const dataObj = e.currentTarget.dataset.target
    if (dataObj == 'currentVisiteDate') {
      this.setData({
        currentVisiteDate: e.detail.value
      })
    } else if (dataObj == 'currentEndDate') {
      this.setData({
        currentEndDate: e.detail.value
      })
    }
  },
  bindTimeChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.currentTarget.dataset.target)
    const dataObj = e.currentTarget.dataset.target
    if (dataObj == 'currentVisiteTime') {
      this.setData({
        currentVisiteTime: e.detail.value
      })
    } else if (dataObj == 'currentEndTime') {
      this.setData({
        currentEndTime: e.detail.value
      })
    }
  },
  formSubmit(e) {
    // console.log("提交的信息",e.detail.value)
    let domain = this.data.domainList[0];
    let port = this.data.portList[10];
    let contentType = this.data.contentType[1];
    request.postData(domain, port, contentType, e.detail.value, true).then(res => {
      if (res.resp_code == 0) {
        wx.showModal({
          title: '',
          content: res.resp_msg,
          showCancel: false,
          confirmText: "确定",
          success(res) {
            if (res.confirm) {
              wx.navigateTo({
                url: '../../index/index.wxml',
              })
            }
          }
        })

      }
    })

  }
})