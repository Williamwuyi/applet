// pages/guestManagement/guestManagement.js
const request = require('../../utils/request.js');
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    TabCur: 0,
    refused: [],
    authorizing: [],
    authorized: [],
    visited: [],
    summary: [],
    haveData: false,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    // 加载状态还是没有数据
    isLoad: true
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getVisitorList(-1)
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
    this.getVisitorList(this.data.TabCur - 1)
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
    console.log('不出意外的e', e.detail.data)
    // e.currentTarget.dataset.id
    this.setData({ TabCur: e.detail.data , });
    this.getVisitorList(this.data.TabCur - 1);
  },
  // 获取访客列表信息
  getVisitorList: function (status) {
    let domain = this.data.domainList[0];
    let port = this.data.portList[2];
    let contentType = this.data.contentType[0];
    // console.log("打印一个数据", status)
    // status表示审核状态,searchType表示请求者类型,size每条显示的条目数量,current指的是当前页面
    const data = {
      status: status,
      searchType: 1,
      current: 1,
      size: 8
    }
    request.postData(domain, port, contentType, data, true).then(res => {
      let domain = this.data.domainList[0];
      let port = this.data.portList[2];
      let contentType = this.data.contentType[0];
      console.log('获取访客列表打印出来的信息', res)
      if (res.resp_code == 0) {
        if (data.status == 0) {
          this.setData({
            authorizing: [...res.datas.records],
            // 有数据就不显示加载条
            hasData: res.datas.total == 0 ? false : true,
            // 无数据,加载完成,就显示无数据
            isLoad: res.datas.total == 0 ? false : true,
          })
          this.makeSummary(this.data.authorizing);
        } else if (data.status == -1) {
          this.setData({
            refused: [...res.datas.records],
            // 有数据就不显示加载条
            hasData: res.datas.total == 0 ? false : true,
            // 无数据,加载完成,就显示无数据
            isLoad: res.datas.total == 0 ? false : true,
          })
          this.makeSummary(this.data.refused);
        } else if (data.status == 1) {
          this.setData({
            authorized: [...res.datas.records],
            // 有数据就不显示加载条
            hasData: res.datas.total == 0 ? false : true,
            // 无数据,加载完成,就显示无数据
            isLoad: res.datas.total == 0 ? false : true,
          })
          this.makeSummary(this.data.authorized);
        } else {
          this.setData({
            visited: [...res.datas.records],
            // 有数据就不显示加载条
            hasData: res.datas.total == 0 ? false : true,
            // 无数据,加载完成,就显示无数据
            isLoad: res.datas.total == 0 ? false : true,
          })
          this.makeSummary(this.data.visited);
        }
      }
    }
    )
  },
  makeSummary: function (array) {
    const currentSummary = []
    for (var i = 0; i < array.length; i++) {
      currentSummary[i] = array[i].visitorName + ' ' + array[i].startTime + "来拜访"
    }
    this.setData({
      summary: [...currentSummary]
    })
  },
  navigateToDetailPage: function (e) {
    const arrayIndex = e.currentTarget.dataset.target;
    // console.log("第几个", e.currentTarget.dataset.target)
    var pickedVisitorId = '';
    if (this.data.TabCur == 0) {
      const pickedVisitorId = this.data.refused[arrayIndex].visitorId
      this.takeValue(pickedVisitorId, 0)
    } else if (this.data.TabCur == 1) {
      console.log(this.data.authorizing[arrayIndex].visitorId)
      const pickedVisitorId = this.data.authorizing[arrayIndex].visitorId
      this.takeValue(pickedVisitorId, 1)
    } else if (this.data.TabCur == 2) {
      const pickedVisitorId = this.data.authorized[arrayIndex].visitorId
      this.takeValue(pickedVisitorId, 2)
    } else {
      const pickedVisitorId = this.data.visited[arrayIndex].visitorId
      this.takeValue(pickedVisitorId, 3)
    }
  },
  takeValue: function (visitorId, index) {
    const urlList = [
      '../guestManagement/guestConfirm/guestDetails/guestDetails',
      '../guestManagement/guestConfirm/guestConfirm',
      '../guestManagement/guestConfirm/guestDetails/guestDetails',
      '../guestManagement/guestConfirm/guestDetails/guestDetails'
    ]
    if (visitorId) {
      wx.navigateTo({
        url: urlList[index],
        success: function (res) {
          // console.log("跳转成功", res)
          res.eventChannel.emit('acceptDataFromOpenerPage', { data: visitorId })
        }
      })
    }
  }
})
