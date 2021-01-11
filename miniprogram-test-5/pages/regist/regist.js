// pages/resign/resign.js
const request = require('../../utils/request.js')
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // login获取到code,用于提交表单
    // util.getCode().then(res => {
    //   this.data.wx_code = res.code
    // })
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
  company_check: function (e) {
    let domain = this.data.domainList[0];
    let port = this.data.portList[11];
    let contentType = this.data.contentType[0];
    request.postData(domain, port,contentType, e.detail.value, true).then( res =>{
      console.log(res)
      if (res.resp_code == 0) {
        const companyName = res.datas.comId
        wx.navigateTo({
          url: '../regist/regist_information/regist_info',
          events: {},
          success: function (res) {
            res.eventChannel.emit('acceptDataFromOpenerPage', { data: companyName})
          }
        })
      }else{
        wx.showModal({
          title: '',
          content: '没有这个公司',
        })
      }
    })

  },

})