// pages/manageStation/manageStation.js
const request = require('../../utils/request');
const app = getApp();
Component({

  /**
   * 页面的初始数据
   * custombar:bar的高度
   * 
   */
  data: {
    person: {},
    imagesUrl: app.globalData.imagesUrl,
    time_today: app.globalData.time_today,
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    checkbox_door: [{
      value: 0,
      name: '大门1号',
      checked: false,
    }, {
      value: 1,
      name: '大门2号',
      checked: false,
    }, {
      value: 2,
      name: '大门3号',
      checked: false,
    }],
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    // test1
    swiperList: [{
      id: 0,
      type: 'image',
      url: 'https://rlsb.youhuan.net:8003/small/person/show?fileName=/images/swiper1.png'
    }, {
      id: 1,
      type: 'image',
      url: 'https://rlsb.youhuan.net:8003/small/person/show?fileName=/images/swiper2.png',
    }, {
      id: 2,
      type: 'image',
      url: 'https://rlsb.youhuan.net:8003/small/person/show?fileName=/images/swiper3.png'
    }],
    // test2
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
    calculateScrollViewHeight: 100,
    currentTabbar: 0,
    // 根据缓存中存储的code判定是员工还是访客以显示不同的权限
    isStuff:false,
  },

  //  组件生命周期
  lifetimes:{
  /**
   * 生命周期函数--监听页面加载
   */
  attached: function (options) {
    // getPersonInfo will save a unikey into the storage,it will be used in [attandance]
    this.getPersonInfo()
    this.notification()
    // this.calculateScrollViewHeight()
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
  ready: function () {
    let that = this;
    setTimeout(function () {
      that.calculateScrollViewHeight()
    }, 100)
  },
},
  methods:{
    showModal(e) {
        this.setData({
          modalName: e.currentTarget.dataset.target
        })
      },
      hideModal(e) {
        let items = this.data.checkbox_door;
        for (let i = 0, lenI = items.length; i < lenI; ++i) {
          items[i].checked = false;
        }
        this.setData({
          modalName: null,
          checkbox_door: items,
        })
      },
      getUserInfo: function (e) {
        app.globalData.userInfo = e.detail.userInfo
        this.setData({
          userInfo: e.detail.userInfo,
          hasUserInfo: true
        })
      },
      //触摸开始,记录触摸的pagex
      ListTouchStart(e) {
        this.setData({
          ListTouchStart: e.touches[0].pageX,
          ListTouchStartY: e.touches[0].pageY
        })
      },
      ListTouchMove(e) {
        let ifSwiper = Math.abs(e.touches[0].pageY - this.data.ListTouchStartY)
        // console.log('初始', this.data.ListTouchStartY)
        // console.log('结束', e.touches[0].pageX)
        if (ifSwiper < 20) {
          this.setData({
            ListTouchDirection: e.touches[0].pageX - this.data.ListTouchStart > 30 ? 'right' : 'left',
          })
        } else {
          this.setData({
            ListTouchDirection: null,
          })
        }
      },
      ListTouchEnd(e) {
        // console.log(this.data.ListTouchDirection)
        if (this.data.ListTouchDirection == 'left') {
          this.setData({
            modalName: e.currentTarget.dataset.target
          })
        } else {
          this.setData({
            modalName: null
          })
        }
        this.setData({
          ListTouchDirection: null
        })
      },
      ChooseCheckbox(e) {
        let items = this.data.checkbox_door;
        let values = e.currentTarget.dataset.value;
        for (let i = 0, lenI = items.length; i < lenI; ++i) {
          if (items[i].value == values) {
            items[i].checked = !items[i].checked;
          } else {
            items[i].checked = false;
          }
        }
        this.setData({
          checkbox_door: items
        })
      },
      hellotest: function () {
        wx.navigateTo({
          url: '../changePersonInfo/personInfo',
        })
      },
      getPersonInfo: function () {
        let domain = this.data.domainList[0];
        let port = this.data.portList[0];
        let contentType = this.data.contentType[1];
        request.getData(domain, port, contentType, '', true).then(data => {
          // console.log('打印一下本人数据', data.datas.personUnikey)
          // 员工版把unikey放到缓存里,以后要用
          wx.setStorageSync('personUnikey', data.datas.personUnikey)
          wx.getStorage({
            key: 'personUnikey',
            success: function (res) {
              console.log('成功获取到数据', res)
            },
          })
        })
      },
      newsDetail: function () {

      },
      // 通知消息
      notification: function () {
        let domain = this.data.domainList[0];
        let port = this.data.portList[17];
        let contentType = this.data.contentType[0];
        const _this = this
        let oldSummary_1 = []
        request.getData(domain, port, contentType, {
          type: 1
        }, true).then(res => {
          let oldSummary = [...res.datas.records]
          for (let i = 0; i < oldSummary.length; i++) {
            oldSummary_1[i] = oldSummary[i].title
            // console.log(oldSummary_1)
          }
          _this.setData({
            summary: [...oldSummary_1]
          })

        })
      },
      // 计算一下滚动条的高度
      calculateScrollViewHeight() {
        let that = this;
        // console.log(systemInfo)
        let query = wx.createSelectorQuery().in(this)
        //根据类名查询节点部分的高度（px单位）
        query.select('.latestNews').boundingClientRect();
        query.exec((res) => {
          // console.log('打印一下获取的数据',res[0].bottom)
          // console.log('打印一下获取的数据', res[0])
          let height = that.data.CuscreenHeight - res[0].bottom - 60
          that.setData({
            calculateScrollViewHeight: height
          })
          // console.log('打印一下获取的数据', that.data.calculateScrollViewHeight)
        })
      }
  }
})