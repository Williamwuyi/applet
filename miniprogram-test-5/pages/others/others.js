// pages/others/others.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    elements: [{
      title: '我的成员',
      name: 'layout',
      color: 'cyan',
      icon: 'newsfill'
    },
    {
      title: '快捷报修',
      name: 'background',
      color: 'blue',
      icon: 'phone'
    },
    {
      title: '投诉建议',
      name: 'text',
      color: 'purple',
      icon: 'form'
    },
    {
      title: '联系我们 ',
      name: 'icon',
      color: 'mauve',
      icon: 'communityfill'
    }],
    isStuff:false,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const _this = this
    wx.getStorage({
      key: 'stuffCode',
      success:(res)=>{
        // console.log('index页面能否获取到缓存数据',res)
        if(res.data == 1){
          this.setData({isStuff:true})
        }else{
          this.setData({isStuff:false})
        }
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

  }
})