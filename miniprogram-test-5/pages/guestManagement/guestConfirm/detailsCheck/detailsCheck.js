// 同意申请
import Toast from '../../../../dist/toast/toast';
const request = require('../../../../utils/request.js')
const app = getApp();
Page({
  data: {
    doorRule:[],
    ruleList:[],
    currentDate: "2019-10-01",
    currentTime:"12:00",
    show: false,
    loading: false,
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
  },

  onLoad: function (options) {
    const _this = this
    const eventChannel = this.getOpenerEventChannel()
    let domain = this.data.domainList[0];
    let port = this.data.portList[5];
    let contentType = this.data.contentType[1];
    // 直接调试此页面是请将这个监听注释掉
    eventChannel.on('acceptDataFromOpenerPage', function (res) {
      console.log('接收到的数据123',res.data)
      _this.setData({
        dataFromLast: { ...res.data }
      })
      request.getData(domain, port, contentType, { personUnikey: _this.data.dataFromLast.personUnikey }, true).then(data => {
        if (data.resp_code == 0) {
          _this.setData({
            doorRule: [...data.datas]
          })
          // console.log("门禁的信息", this.data.doorRule)
        }
      })
    })
    // console.log('受访者信息', this.data.dataFromLast.personUnikey)
    // 获取门禁规则
  },
  checkboxChange(e){
    this.setData({
      ruleList:e.detail.value
    })
    // console.log("显示选择的值", this.data.ruleList)
  },
  accept(e){
    console.log("提交的值", e.detail.value.failureTime)
    const visitorId = this.data.dataFromLast.visitorId
    const ruleIdList = this.data.ruleList
    const times = Number(e.detail.value.times)
    let domain = this.data.domainList[0];
    let port = this.data.portList[6];
    let contentType = this.data.contentType[0];
    // console.log(e)
    this.setData({
      acceptData: { times: times, failureTime: e.detail.value.failureTime, visitorId: visitorId, ruleIdList: ruleIdList}
    })
    console.log(this.data.acceptData)
    request.postData(domain, port, contentType,  this.data.acceptData,true).then(res =>{
      console.log('返回值', res)
      if (res.resp_code ==0 ) {
        wx.redirectTo({
          url: '../../../guestManagement/guestManagement',
        })
      }
    }
    )
  },
  bindDateChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      currentDate: e.detail.value
    })
  },
  bindTimeChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      currentTime: e.detail.value
    })
  },
});
