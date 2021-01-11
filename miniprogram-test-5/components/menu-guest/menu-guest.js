// components/menu-guest/menu-guest.js
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
          toUrl = '../guest/postName/postName'
          break;
        case '1':
          toUrl = '../guest/applyList/applyList'
          break;
        case '2':
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
