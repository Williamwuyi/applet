// components/menu_manage/menu-manage.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {

  },

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    navigateTo: function (e) {
      const target = e.currentTarget.dataset.target
      var toUrl = ''
      switch (target) {
        case '0':
          toUrl = '../guestManagement/guestManagement'
          break;
        case '1':
          toUrl = '../eventManagement/eventManagement'
          break;
        case '2':
          toUrl = '../workAttendance/workAttendance'
          break;
        case '3':
          toUrl = '../others/others'
          break;
      }
      console.log('点击')
      wx.navigateTo({
        url: toUrl,
      })
    },
  }
})
