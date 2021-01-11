// pages/workAttendance/attendanceTable/attendanceTable.js
// pages/workAttendance/workAttendance.js
const request = require('../../../utils/request.js');
const app = getApp();
const date = new Date()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    CustomBar: app.globalData.CustomBar,
    CuscreenHeight: app.globalData.CuscreenHeight,
    CuscreenWidth: app.globalData.CuscreenWidth,
    // 加载状态还是没有数据
    headers: [{ text: 'one', display: '日期' }, { text: 'two', display: '状态' }, { text: 'three', display: '上班时间' }, { text: 'four', display: '下班时间' },],
    row: [],
    hasdata: true,
    // 考勤报表
    // attendanceHeight: 0,
    attendanceTdWidth:app.globalData.CuscreenWidth/4,
    attendanceDate: app.globalData.attendanceDate,
    showmodal: false,
    this_year: date.getFullYear(),
    // 地址栏
    contentType: app.globalData.contentType,
    domainList: app.globalData.domainList,
    portList: app.globalData.portList,
    personUnikey: '',
    hasdata: true,
    workTimeBegin: '暂无记录',
    workTimeEnd: '暂无记录'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function () {
    this.getDayStatis()
  },

  lastMonth() {
    const date = this.data.attendanceDate.split('-')
    const year = Number(date[0])
    const month = Number(date[1])
    var new_year;
    var new_month;
    if (month > 1) {
      new_year = year
      new_month = month - 1
    } else {
      if (year > 2010) {
        new_year = year - 1;
        new_month = 12;
      } else {
        new_year = this.data.this_year;
        new_month = 12;
      }
    }
    this.setData({
      attendanceDate: new_year + '-' + new_month
    })
    this.getDayStatis()
  },
  nextMonth() {
    const date = this.data.attendanceDate.split('-')
    const year = Number(date[0])
    const month = Number(date[1])
    var new_year;
    var new_month;
    if (month < 12) {
      new_month = month + 1;
      new_year = year;
    } else {
      if (year < this.data.this_year) {
        new_year = year + 1;
        new_month = 1;
      } else {
        new_year = 2010;
        new_month = 1;
      }
    }
    this.setData({
      attendanceDate: new_year + '-' + new_month
    })
    this.getDayStatis()
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
  // 表格组件,点击时间弹出时间选择器
  changeDate: function () {
    this.setData({
      showmodal: true
    })
  },
  hidemodal: function () {
    this.setData({
      showmodal: false
    })
  },
  confirmDate: function (e) {
    console.log(e.detail.date)
    this.setData({
      showmodal: false,
      attendanceDate: e.detail.date
    })
    this.getDayStatis()
  },
  // 获取考勤数据
  getDayStatis: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[15];
    let contentType = this.data.contentType[0];
    let month = Number(this.data.attendanceDate.split('-')[1]);
    let year = this.data.attendanceDate.split('-')[0]
    console.log('启动了')
    // 放在缓存里面的personUnikey
    const _this = this
    wx.getStorage({
      key: 'personUnikey',
      success: function (res) {
        console.log('成功获取到数据', res.data)
        _this.setData({ personUnikey: res.data})
        if (month < 10) {
          month = '0' + month;
          _this.setData({
            attendanceDate: year + '-' + month
          })
          console.log('日期', _this.data.attendanceDate)
        }
        request.getData(domain, port, contentType, { dataTime: _this.data.attendanceDate, personUnikey: _this.data.personUnikey, type: 1 }, true).then(res => {
          // res.datas.length 数据长度
          let data = res.datas
          let length = data.length;
          if (length > 0) {
            _this.setData({ hasdata: true })
          } else {
            _this.setData({ hasdata: false })
          }
          let tempData = [];
          console.log('返回的考勤数据', data[0].dateTime)
          for (let i = 0; i < length; i++) {
            let tempObj = { one: '' + data[i].dateTime, two: data[i].shiftName, three: '' + data[i].range1OnTime, four: '' + data[i].range1OffTime };
            tempData.push(tempObj);
            // console.log('测试用数据', tempData)
            tempObj = {};
          }
          _this.setData({
            row: tempData,
          })
        })
      },
    })
  },
  // 获取今日考勤数据
  getTodayStatis: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[16];
    let contentType = this.data.contentType[0];
    // 放在缓存里面的personUnikey
    const _this = this
    wx.getStorage({
      key: 'personUnikey',
      success: function (res) {
        request.getData(domain, port, contentType, { personUnikey: _this.data.personUnikey }, true).then(res => {
          // console.log('打印数据', res)
          const data = res.datas;
          if (data.staTisOnTime) {
            let staTisOnTime = data.staTisOnTime.split(' ')[1];
            // console.log('上班时间', staTisOnTime)
            _this.setData({
              workTimeBegin: staTisOnTime
            })
          }
          if (data.staTisOffTime) {
            let staTisOffTime = data.staTisOffTime.split(' ')[1];
            // console.log('下班时间', staTisOffTime)
            _this.setData({
              workTimeEnd: staTisOffTime
            })
          }
        })
      }
    })
  },
  // 通知消息
  notification: function () {
    let domain = this.data.domainList[0];
    let port = this.data.portList[17];
    let contentType = this.data.contentType[0];
    const _this = this
    let oldSummary_1 = []
    request.getData(domain, port, contentType, { type: 1 }, true).then(res => {
      let oldSummary = [...res.datas.records]
      for (let i = 0; i < oldSummary.length; i++) {
        oldSummary_1[i] = oldSummary[i].title
      }
      _this.setData({
        summary: [...oldSummary_1]
      })

    })
  }
})
