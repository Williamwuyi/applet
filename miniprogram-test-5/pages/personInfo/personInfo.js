// pages/personInfo/personInfo.js
const request = require('../../utils/request');
const app = getApp();
Component({

  /**
   * 页面的初始数据
   */
  data: {
    currentTabbar: 1,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    imagesUrl: app.globalData.imagesUrl,
    // 人员信息
    person:{
      headImage:'',
      name:'',
      phoneNumber:''
    }
  },
  lifetimes: {
    /**
     * 生命周期函数--监听页面加载
     */
    attached: function (options) {
      this.getPersonInfo();
    },
  },
  methods: {
    // 获取个人信息
    getPersonInfo: function () {
      let domain = this.data.domainList[0];
      let port = this.data.portList[0];
      let contentType = this.data.contentType[1];
      let imagesUrl = this.data.imagesUrl
      let _this = this
      request.getData(domain, port, contentType, '', true).then(data => {
        console.log('获取个人信息', data.datas)
        _this.setData({
          'person.headImage':imagesUrl+data.datas.imgPaths,
          'person.name':data.datas.personName,
          'person.phoneNumber':data.datas.personPhone
        })
        // console.log('头像地址',_this.data.person.headImage)
      })
    },
    // jump to the target page 
    jumpTo:function(e){
      let target = e.currentTarget.dataset.target;
      switch(target){
        case '1':{
          wx.navigateTo({
            url: '../changePersonInfo/personInfo',
          })
          break;
        };
        case '4':{
          wx.showModal({
            content:'已更新至最新版本\nV3.0.2',
            showCancel:false,
          })
          break;
        }
      }
    }
  }
})