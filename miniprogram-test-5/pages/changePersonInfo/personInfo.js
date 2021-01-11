// pages/personInfo.js
const request = require('../../utils/request');
const uploadPic = require('../../utils/uploadPic.js')
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    person: {},
    sex: ['男', '女'],
    picker_index: 0,
    updateInfo: false,
    imgList: [],
    showPersonPic: true,
    whoUseThis: 'manager',
    imagesUrl: app.globalData.imagesUrl,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    // 修改之后加载
    loadModal: false,
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
    }],
    currentTabbar:1
  },

  /**
   * 生命周期函数--监听页面加载
   * 监听上一个页面传来的参数,guest or manager
   */
  onLoad: function (options) {
    this.getPersonInfo()
    const _this = this
    const eventChannel = this.getOpenerEventChannel()
    // 直接调试此页面是请将这个监听注释掉
    eventChannel.on('acceptDataFromOpenerPage', function (data) {
      _this.setData({
        whoUseThis: data.data
      })
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
  updateInfo: function () {
    this.setData({
      updateInfo: !this.data.updateInfo
    })
    if (this.data.updateInfo == false) {
      this.onLoad()
    }
  },
  ChooseImage: function () {
    console.log("图片点击事件")
    wx.chooseImage({
      count: 1,//上传数量,默认9
      sizeType: ['compressed'],//原始还是压缩格式
      sourceType: ['album', 'camera'],//从相册选择
      success: (res) => {
        if (this.data.imgList.length == 0) {
          this.setData({
            'imgList[0]': res.tempFilePaths
          })
          let domain = this.data.domainList[0];
          let port = this.data.portList[8];
          console.log('上传图片', this.data.imgList[0].toString())
          const url = this.data.imgList[0].toString()
          uploadPic.uploadPic(domain + port, url, 'file').then(data => {
            const newPath = data
            console.log('上传图片的返回值', data)
            // this.data.imgList[0] = data
            this.setData({
              'imgList[1]': newPath
            })
            // 我不明白为什么要加一个toString()
            console.log('service_url', this.data.imgList[1].toString())
          })
        }
      },
    })
  },
  DelImg(e) {
    wx.showModal({
      title: '警告⚠️',
      content: '确定要删除照片？',
      cancelText: '取消',
      confirmText: '确定',
      success: res => {
        if (res.confirm) {
          this.setData({
            imgList: []
          })
          console.log('this.data.imgList[0]', this.data.imgList[0])
        }
      }
    })
  },
  showpersonpic() {
    if (this.data.imgList) {
      this.data.showPersonPic = true;
    } else {
      this.data.showPersonPic = false;
    }
    // console.log("1")
  },
  bindPickerChange(e) {
    console.log(e.detail.value)
    this.setData({
      picker_index: e.detail.value
    })
  },
  getPersonInfo: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[0];
    let contentType = this.data.contentType[1];
    request.getData(domain, port, contentType, '', true).then(data => {
      console.log('获取个人信息', data.datas)
      let personSex = data.datas.personSex == '0' ? '男' : '女'
      console.log('人的性别是', personSex)
      if (data.resp_code == 0) {
        this.setData({
          person: { ...data.datas, },
          'imgList[0]': this.data.imagesUrl + data.datas.imgPaths,
          picker_index: data.datas.personSex
        })
        // data.datas.imgPaths 将头像写进缓存里面,在修改个人信息的时候如果没有传新的,则使用旧的上传
        wx.setStorageSync('personImgPaths', data.datas.imgPaths)
        console.log(this.data.person)
      }
    })
  },
  updateInfomation: function (e) {
    console.log('打印提交的数据', e.detail.value)
    const newData = e.detail.value
    const _this = this
    console.log('图片', newData.imgPaths)
    if (newData.imgPaths) {
      let domain = this.data.domainList[0];
      let port = this.data.portList[7];
      let contentType = this.data.contentType[1];
      // 加载状态
      this.setData({ loadModal: true })
      request.postData(domain, port, contentType, newData, true).then(res => {
        console.log("打印返回值更新人员数据", res)
        if (res.resp_code == 0) {
          this.getPersonInfo()
          this.setData({
            updateInfo: false,
            // 取消加载状态
            loadModal: false
          })
        } else {
          this.setData({
            // 取消加载状态
            loadModal: false
          })
          wx.showModal({
            title: '',
            content: '修改失败',
            success: function (res) {
              if (res) {
                _this.getPersonInfo()
                _this.setData({
                  updateInfo: false,
                  loadModal: false
                })
              }
            }
          })
        }
      })
    } else {
      // 如果修改时图片没有上传,就使用原来的图片
      let domain = this.data.domainList[0];
      let port = this.data.portList[7];
      let contentType = this.data.contentType[1];
      // 加载中
      this.setData({ loadModal: true })
      const personImgPaths = wx.getStorageSync('personImgPaths')
      newData.imgPaths = personImgPaths
      request.postData(domain, port, contentType, newData, true).then(res => {
        console.log("打印返回值更新人员数据", res)
        if (res.resp_code == 0) {
          this.getPersonInfo()
          this.setData({
            updateInfo: false,
            // 取消加载中
            loadModal: false
          })
        } else {
          // 取消加载中
          this.setData({ loadModal: false })
          wx.showModal({
            title: '',
            content: '修改失败',
            success: function (res) {
              if (res) {
                _this.getPersonInfo()
                _this.setData({
                  updateInfo: false
                })
              }
            }
          })
        }
      })
    }
  }
})
