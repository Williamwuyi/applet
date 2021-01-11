//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    list: [{
        "text": "首页",
        "iconPath": "../../images/home.png",
        "selectedIconPath": "../../images/home_checked.png",
        dot: true
      },
      {
        "text": "我的",
        "iconPath": "../../images/my.png",
        "selectedIconPath": "../../images/my_checked.png",
        // badge: 'New'
      }
    ],
    currentTabbar:0,

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
  // 底部tab变化,切换页面
  bottomTabChange:function(e){
    // console.log("变化了",e.detail.index)
    let tabindex = e.detail.index;
    this.setData({
      currentTabbar: tabindex

    })
  }
})