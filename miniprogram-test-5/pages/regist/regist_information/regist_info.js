
const uploadPic = require('../../../utils/uploadPic.js')
const uploadImg = getApp().globalData.uploadImg
const formsubmit = require('../../../utils/formsubmit.js')
const request = require('../../../utils/request.js')
const app = getApp();
import WxValidate from '../../../utils/WxValidate.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    personName: '',
    // position: '',
    cellphoneNumber: '',
    telephoneNumber: '',
    address: '',
    // updateInfo: false,
    imgList: '',
    service_imgurl: '',
    showPersonPic: true,
    // picker性别选择
    picker_index: 0,
    sex: ['男', '女'],
    passwd_check: '',
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    registButton: false,
    canIUseName: false,
    usernameMessage:'用户名为必填项',
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 初始化表单校验函数
    const _this = this
    this.initValidate()
    const eventChannel = this.getOpenerEventChannel()
    // 直接调试此页面是请将这个监听注释掉
    eventChannel.on('acceptDataFromOpenerPage', function (data) {
      console.log('接收到的数据', data.data)
      _this.setData({
        companyId: data.data
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
  setPasswd(e) {
    const pwd = e.detail.value
    this.setData({
      passwd_check: pwd
    })
    this.initValidate()
  },
  showModal(error) {
    wx.showModal({
      content: error.msg,
      showCancel: false,
    })
  },
  submit: function (e) {
    // console.log(e.detail.value)
    let domain = this.data.domainList[0];
    let port = this.data.portList[12];
    let contentType = this.data.contentType[1];
    request.postData(domain, port, contentType, e.detail.value, false)
  },
  // 性别选择
  bindPickerChange: function (e) {
    this.setData({
      picker_index: e.detail.value
    })

  },
  // 从本地选择图片,将其保存到缓存中,并返回一个地址
  ChooseImage() {
    wx.chooseImage({
      count: 1,//上传数量,默认9
      sizeType: ['compressed'],//原始还是压缩格式
      sourceType: ['album', 'camera'],//从相册选择
      success: (res) => {
        if (this.data.imgList.length == 0) {
          this.setData({
            imgList: res.tempFilePaths
          })
          const url = this.data.imgList[0]
          let domain = this.data.domainList[0];
          let port = this.data.portList[8];
          // console.log('url',url)
          // 使用异步,将服务器上图片位置赋值给imgList 暂时注释掉
          uploadPic.uploadPic(domain + port, url, 'file').then(data => {
            // this.data.head_img_url = data
            this.setData({
              service_imgurl: data
            })
            // console.log('service_url', this.data.service_imgurl)
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
            imgList: ''
          })
          // console.log(this.data)
        }
      }
    })
  },
  showpersonpic() {
    if (imgList == '') {
      this.data.showPersonPic = false;
    } else {
      this.data.showPersonPic = true;
    }
  },
  // 验证函数
  initValidate: function () {
    // 规则
    const rules = {
      username: {
        required: true,
        minlength: 5,
        checkUserName: true,
      },
      // imgPaths:{
      //   required:true,
      // },
      password: {
        required: true,
        minlength: 5,
      },
      checkPassword: {
        required: true,
        minlength: 5,
        equalTo: 'password'
      },
      personPhone: {
        required: true,
        tel: true
      },
      personName: {
        required: true,
      }
    }
    // 返回信息
    const messages = {
      username: {
        required: "请输入用户名",
        minlength: "用户名长度不匹配"
      },
      // imgPaths: {
      //   required:"请上传人脸照片"
      // }, 
      password: {
        required: "请输入密码",
        minlength: "密码太短",
      },
      checkPassword: {
        required: "请再次输入密码",
        minlength: "密码太短",
        equalTo: '两次密码不一致'
      },
      personPhone: {
        required: "请输入手机号码",
        tel: "手机号码不合法"
      },
      personName: {
        required: "请输入姓名",
      }
    }
    this.WxValidate = new WxValidate(rules, messages)
    let _this = this
    this.WxValidate.addMethod('checkUserName', (value, param) => {
      // console.log('打印了个什么东西',_this.data.canIUseName)
      return (_this.data.canIUseName)
    }, '用户名已被注册')

  },
  // 调用验证函数
  formSubmit: function (e) {
    // console.log(this.WxValidate.checkForm(params))
    console.log("form发生了submit事件,携带的数据为: ", e.detail.value)
    const params = e.detail.value
    // 校验表单
    if (!this.WxValidate.checkForm(params)) {
      const error = this.WxValidate.errorList[0]
      this.showModal(error)
      return false
    } else {
      // 提交函数
      let domain = this.data.domainList[0];
      let port = this.data.portList[12];
      let contentType = this.data.contentType[1];
      this.setData({ registButton: true })
      request.postData(domain, port, contentType, e.detail.value, true, 'resignId').then(res => {
        console.log('注册之后返回的值', res)
        if (res.resp_code == 0) {
          if (res.datas.status == 0) {
            wx.showModal({
              title: '',
              content: '注册成功',
              showCancel: false,
              success: function (res) {
                // console.log(res.confirm)
                if (res.confirm) {
                  wx.clearStorage()
                  wx.redirectTo({
                    url: '../../login/login',
                  })
                }
              }
            })
          } else if (res.datas.status == -1) {
            wx.showModal({
              title: '',
              content: '注册成功,请修改个人信息',
              showCancel: false,
              success: function (res) {
                // console.log(res.confirm)
                if (res.confirm) {
                  wx.clearStorage()
                  wx.redirectTo({
                    url: '../../login/login',
                  })
                }
              }
            })
          }
        } else {
          wx.showModal({
            title: '',
            content: '注册失败,请联系管理员',
          })
          this.setData({ registButton: false })
        }
      }
      )
    }
  },
  // 失去焦点时对输入的用户名进行校验
  checkUserName: function (e) {
    const value = e.detail.value
    console.log('打印失去焦点之后的值', value)
    let domain = this.data.domainList[0];
    let port = this.data.portList[14];
    let contentType = this.data.contentType[0];
    request.postData(domain, port, contentType, { username: value }, false).then(res => {
      console.log('检测数据', res)
      if (res.resp_code == 0) {
        this.setData({
          canIUseName: true,
          usernameMessage: '用户名可使用',
        })
      } else {
        this.setData({ 
          canIUseName: false,
          usernameMessage: '用户名不可用',
         })
      }
    })
  }
})
