// pages/login.js
const app = getApp();
var util = require("../../utils/util.js");
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
  login_information:function (){
    let domain = this.data.domainList[0];
    let port = this.data.portList[13];
    let contentType = this.data.contentType[0];
    // let domain = 'https://rlsb.youhuan.net:8003'
    // let port = '/auth/wx7690b7064df0e5fb/login'
    // let contentType = 'application/x-www-form-urlencoded'
    util.loginApi(domain, port, contentType)
  }
})