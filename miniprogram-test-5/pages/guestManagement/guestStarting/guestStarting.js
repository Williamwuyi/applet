// 此页面对应原型的将开始
// pages/guestManagement/guestStarting/guestStarting.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    minHour: 10,
    maxHour: 20,
    minDate: new Date(2018, 0, 1).getTime(),
    maxDate: new Date(2019, 10, 1).getTime(),
    currentDate1: new Date(2018, 2, 31).getTime(),
    currentDate2: null,
    currentDate3: new Date(2018, 0, 1),
    currentDate4: '12:00',
    loading: false,
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      } else if (type === 'month') {
        return `${value}月`;
      }
      return value;
    },
    show: false

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
  radioChange(e) {
    this.setData({
      radioCurrentValue: e.detail.value
    })
  },
  onInput(event) {
    const { detail, currentTarget } = event;
    console.log(event)
    console.log(detail)
    console.log(currentTarget.dataset.type)
    const result = this.getResult(detail, currentTarget.dataset.type);
    this.setData({
      changeDate: result
    })
  },

  getResult(time, type) {
    const date = new Date(time);
    console.log(date)
    switch (type) {
      case 'datetime':
        return date.toLocaleDateString().split('/').join('-') + " " + date.toTimeString().split("G")[0];
      case 'date':
        return date.toLocaleDateString();
      case 'year-month':
        return `${date.getFullYear()}/${date.getMonth() + 1}`;
      case 'time':
        return time;
      default:
        return '';
    }
  },
  onClose() {
    this.setData({
      show: false
    })
  },
  popTime() {
    this.setData({
      show: true
    })
  },
  onCancel() {
    this.setData({
      show: false
    })
  },
  onConfirm() {
    this.setData({
      show: false
    })
  }
})