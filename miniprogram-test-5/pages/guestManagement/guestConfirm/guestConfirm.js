const request = require('../../../utils/request.js')
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    modalName: '',
    details: {},
    imagesUrl: app.globalData.imagesUrl,
    visitorId: '',
    confirmButton: false,
    refusedButton: false,
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
      _this.getDetailsById(data)
      _this.setData({ visitorId: data })
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
  showModal(e) {
    this.setData({
      modalName: e.currentTarget.dataset.target,
    })
  },
  hideModal() {
    this.setData({
      modalName: '',
      refusedReason: ''
    })
  },
  ifchecked(e) {
    let checkedValue = e.detail.value
    if (checkedValue == "others") {
      this.setData({
        hasBeenSelected: true
      })
    } else {
      this.setData({
        hasBeenSelected: false
      })
    }
  },
  getDetailsById: function (visitorId) {
    let domain = this.data.domainList[0];
    let port = this.data.portList[3];
    let contentType = this.data.contentType[1];
    request.getData(domain, port + visitorId.data, contentType,  '', true).then(res => {
      // console.log('接收的数据',res.datas)
      if (res.resp_code == 0) {
        // console.log(this)
        this.setData({
          details: { ...res.datas }
        })
        // 获取到发起人到头像
        this.getVisitorImg(this.data.details.visitorUnikey)
      }
    })
  },
  reasonInput: function (e) {
    console.log(e.detail.value)
    this.setData({
      refusedReason: e.detail.value
    })
  },
  refuseApply: function (e) {
    if (this.data.refusedReason.length != 0) {
      this.setData({
        modalName: '',
      })
      // console.log('visitorId', typeof(this.data.visitorId.data))
      const data = { refusal: this.data.refusedReason, visitorId: (this.data.visitorId.data) }
      let domain = this.data.domainList[0];
      let port = this.data.portList[4];
      let contentType = this.data.contentType[0];
      request.postData(domain, port, contentType, data, true).then(res => {
        console.log(res)
        this.setData({
          confirmButton: true,
          refusedButton: true,
        })
      })
    }
  },
  jumpToDetail: function () {
    const data = { personUnikey: this.data.details.person.personUnikey, visitorId: this.data.details.visitorId }
    wx.navigateTo({
      url: './detailsCheck/detailsCheck',
      success: function (res) {
        console.log("跳转成功1", res)
        console.log("跳转成功2", data)
        res.eventChannel.emit('acceptDataFromOpenerPage', { data: data })
      }
    })
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