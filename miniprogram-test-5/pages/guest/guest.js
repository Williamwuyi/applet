// pages/guest/guest.js
// pages/manageStation/manageStation.js
const request = require('../../utils/request');
const app = getApp();
Page({

  /**
   * 页面的初始数据
   * custombar:bar的高度
   * 
   */
  data: {
    person: {},
    imagesUrl: app.globalData.imagesUrl,
    time_today: app.globalData.time_today,
    CustomBar: app.globalData.CustomBar,
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getPersonInfo()
    this.notification()
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
    this.getPersonInfo()
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
  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target
    })
  },
  hideModal(e) {
    let items = this.data.checkbox_door;
    for (let i = 0, lenI = items.length; i < lenI; ++i) {
      items[i].checked = false;
    }
    this.setData({
      modalName: null,
      checkbox_door: items,
    })
  },
  getUserInfo: function (e) {
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  },
  //触摸开始,记录触摸的pagex
  ListTouchStart(e) {
    this.setData({
      ListTouchStart: e.touches[0].pageX,
      ListTouchStartY: e.touches[0].pageY
    })
  },
  ListTouchMove(e) {
    let ifSwiper = Math.abs(e.touches[0].pageY - this.data.ListTouchStartY)
    // console.log('初始', this.data.ListTouchStartY)
    // console.log('结束', e.touches[0].pageX)
    if (ifSwiper < 20) {
      this.setData({
        ListTouchDirection: e.touches[0].pageX - this.data.ListTouchStart > 30 ? 'right' : 'left',
      })
    } else {
      this.setData({
        ListTouchDirection: null,
      })
    }
  },
  ListTouchEnd(e) {
    // console.log(this.data.ListTouchDirection)
    if (this.data.ListTouchDirection == 'left') {
      this.setData({
        modalName: e.currentTarget.dataset.target
      })
    } else {
      this.setData({
        modalName: null
      })
    }
    this.setData({
      ListTouchDirection: null
    })
  },
  ChooseCheckbox(e) {
    let items = this.data.checkbox_door;
    let values = e.currentTarget.dataset.value;
    for (let i = 0, lenI = items.length; i < lenI; ++i) {
      if (items[i].value == values) {
        items[i].checked = !items[i].checked;
      } else {
        items[i].checked = false;
      }
    }
    this.setData({
      checkbox_door: items
    })
  },
  // 开门
  openDoor() {
    let items = this.data.checkbox_door;
    for (let i = 0, lenI = items.length; i < lenI; ++i) {
      if (items[i].checked == true) {
        console.log(items[i].name + "已经打开了");
        items[i].checked = false;
      }
    }
    this.setData({
      modalName: null,
      checkbox_door: items,
    })
  },
  personCenter: function () {
    wx.navigateTo({
      url: '../changePersonInfo/personInfo',
      success: function (res) {
        // console.log("跳转成功", res)
        res.eventChannel.emit('acceptDataFromOpenerPage', { data: 'guest' })
      }
    })
  },
  getPersonInfo: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[0];
    let contentType = this.data.contentType[1];
    request.getData(domain, port,contentType, '', true).then(data => {
      console.log('获取到的信息',data)
    // request.getApi('/person/getPersonInfo', '', true).then(data => {

      let personSex = data.datas.personSex == 0 ? '男' : '女'
      console.log('人的性别是',personSex)
      if (data.resp_code == 0) {
        this.setData({
          person: { ...data.datas, sex: personSex }
        })
        wx.setStorage({
          key: "userImage",
          data: this.data.person.imgPaths
        })
      }
    })
  },
  navigateToOthers:function(){
    console.log('跳转到其他操作')
    wx.navigateTo({
      url: '../others/others',
      success: function (res) {
        // console.log("跳转成功", res)
        res.eventChannel.emit('acceptDataFromOpenerPage', { data: 'guest' })
      }
    })
  },
  // 通知消息
  notification: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[17];
    let contentType = this.data.contentType[0];
    const _this = this
    let oldSummary_1 = []
    request.getData(domain, port, contentType, { type: 1 }, true).then(res => {
      let oldSummary = [...res.datas.records]
      for (let i = 0; i < oldSummary.length; i++) {
        oldSummary_1[i] = oldSummary[i].title
        // console.log(oldSummary_1)
      }
      _this.setData({
        summary: [...oldSummary_1]
      })

    })
  }
})