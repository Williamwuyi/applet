// pages/guestManagement/guestComfirm/guestDetails/guestDetails.js
// 准备调用两个接口,一个是获取本人的信息,另外一个是获取到拜访的信息
const request = require('../../../../utils/request.js')
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    details: {},
    imagesUrl: app.globalData.imagesUrl,
    personImage: '',
    status: '返回',
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
      const visitorId = data
      console.log('接收到的数据', visitorId)
      _this.getDetailsById(visitorId)
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
  getDetailsById: function (visitorId) {
    // console.log(visitorId.data)
    let domain = this.data.domainList[0];
    let port = this.data.portList[3];
    let contentType = this.data.contentType[1];
    request.getData(domain, port + visitorId.data, contentType,  '', true).then(res => {
      console.log(res)
      if (res.resp_code == 0) {
        // console.log(this)
        this.setData({
          details: { ...res.datas }
        })
        this.statusMessage(res.datas.status)
        // 获取到发起人到头像
        this.getVisitorImg(this.data.details.visitorUnikey)

      }
    })
    // console.log("函数运行")
  },
  statusMessage: function (status) {
    switch (status) {
      case -1:
        this.setData({ status: '未通过' })
        break;
      case 0:
        this.setData({ status: '未审核' })
        break;
      case 1:
        this.setData({ status: '已通过' })
        break;
      case 2:
        this.setData({ status: '已完成' })
        break;
    }
  },
  // 根据"personunikey"获取到人员头像
  getVisitorImg: function (e) {
    let domain = this.data.domainList[0];
    let port = this.data.portList[1];
    let contentType = this.data.contentType[1];
    request.getData(domain, port, contentType, { personUnikey: e }, true).then(res => {
      // console.log("打印一下参数", res)
      if (res.resp_code == 0) {
        this.setData({
          personImage: res.datas.imgPaths
        })
        // console.log('图片地址', this.data.personImage)
      }
    })
  }
})