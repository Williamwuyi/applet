// pages/guest/postName/postName.js
const request = require('../../../utils/request.js')
import WxValidate from '../../../utils/WxValidate.js'
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    imagesUrl: app.globalData.imagesUrl,
    // 显示加载还是接口返回的数据
    sendName: false,
    receiveResult: false,
    // 数据对象,用来接收接口返回的数据
    person: [],
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.initValidate()
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
  test1: function () {
    this.setData({
      sendName: !this.data.sendName
    })
  },
  test2: function () {
    this.setData({
      receiveResult: !this.data.receiveResult
    })
  },
  showYourData: function (e) {
    const visitIndex = e.currentTarget.dataset.target
    const currentData = this.data.person[visitIndex]
    // console.log('这里是拜访对象的数据', visitIndex)
    // console.log('即将要被传递的数据', this.data.person[visitIndex])
    // console.log('即将要被传递的数据2', currentData)
    wx.navigateTo({
      url: '../guestApply/guestApply',
      success: function (res) {
        // console.log("跳转成功", res)
        res.eventChannel.emit('acceptDataFromOpenerPage', { data: currentData })
      }
    })
  },
  getPersonByName: function (e) {
    // 校验表单
    const params = e.detail.value
    if (!this.WxValidate.checkForm(params)) {
      const error = this.WxValidate.errorList[0]
      this.showModal(error)
      return false
    } else {

      this.setData({
        sendName: true
      })
      let domain = this.data.domainList[0];
      let port = this.data.portList[9];
      let contentType = this.data.contentType[1];
      request.getData(domain, port,contentType, e.detail.value, true).then(res => {
        // console.log('查询人员返回的数据值', res.datas)
        if (res.datas) {
          this.setData({
            person: [...res.datas],
            receiveResult: true
          })
        } else {
          wx.showModal({
            title: '',
            content: '没有找到匹配的受访者',
          })
          this.setData({
            sendName: false,
            receiveResult: false
          })
        }
        // console.log('拷贝人员返回的数据值', this.data.person)
      })
    }
  },
  initValidate: function () {
    // 规则
    const rules = {
      personName: {
        required: true,
      }
    }
    // 返回信息
    const messages = {
      personName: {
        required: "请输入受访者全名",
      }
    }
    this.WxValidate = new WxValidate(rules, messages)
  },
  showModal(error) {
    wx.showModal({
      content: error.msg,
      showCancel: false,
    })
  },
})