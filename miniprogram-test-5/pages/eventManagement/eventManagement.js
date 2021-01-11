// pages/eventManagement/eventManagement.js
const request = require('../../utils/request.js');
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    TabCur: 0,
    deal: [],
    dealing: [],
    dealed: [],
    visited: [],
    summary: [
      '2019年11月1日,10点30分,大门报警事件,请处理!',
      '2019年11月11日,10点30分,大门报警事件,请处理!',
      '2019年11月12日,10点30分,大门报警事件,请处理!',
      '2019年12月1日,10点30分,大门报警事件,请处理!',
      '2019年12月9日,10点30分,大门报警事件,请处理!',
    ],
    hasData: true,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    // 加载状态还是没有数据
    isLoad: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // this.getVisitorList(-1)
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
    // this.getVisitorList(this.data.TabCur - 1)
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
  tabSelect(e) {
    console.log('tab has been changed',e.detail.data)
    this.setData({ TabCur: e.detail.data });
    // this.getVisitorList(this.data.TabCur - 1);
    let n = this.data.TabCur
    console.log("TabCur的值", n)
    this.changeContent(n)
  },
  changeContent(n) {
    switch (n) {
      case 0:
        console.log('运行n', n)
        this.setData({
          summary: [
            '2019年11月1日,10点30分,大门报警事件,请处理!',
            '2019年11月11日,10点30分,大门报警事件,请处理!',
            '2019年11月12日,10点30分,大门报警事件,请处理!',
            '2019年12月1日,10点30分,大门报警事件,请处理!',
            '2019年12月9日,10点30分,大门报警事件,请处理!',
          ],
        })
        break;
      case 1:
        this.setData({
          summary: [
            '2019年11月1日,10点30分,大门报警事件,正在处理!',
          ],
        })
        break;
      case 2:
        this.setData({
          summary: [
            '2019年11月1日,10点30分,大门报警事件,已处理!',
            '2019年12月1日,10点30分,大门报警事件,已处理!',
            '2019年12月9日,10点30分,大门报警事件,已处理!',
          ],
        })
        break;
    }
  },
  navigateToDetailPage: function (e) {
    const arrayIndex = e.currentTarget.dataset.target;
    const TabCur = this.data.TabCur
    console.log('Tabcur的值', TabCur)
    console.log('点击的数值', arrayIndex)
    if (TabCur == 0) {
      wx.navigateTo({
        url: './eventDeal/deal',
      })
      console.log('1')
    } else if (TabCur == 1) {
      wx.navigateTo({
        url: './eventDealing/dealing',
      })
    } else if (TabCur == 2) {
      wx.navigateTo({
        url: './eventDealed/dealed',
      })

    }
  }
})
