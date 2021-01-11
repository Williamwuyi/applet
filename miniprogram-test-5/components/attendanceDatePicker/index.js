// components/attendacneDatePicker/index.js
const date = new Date()
const years = []
const months = []
const days = []
Component({
  options: {
    styleIsolation: "isolated"
  },
  properties: {
    showmodal: {
      type: Boolean,
      value: true,
    },
    // 和table共用一个属性,当这个只发生变化的时候,改变value的属性
    attendanceDate: {
      type: String,
      value: '',
      observer:function(newVal,oldVal){
        console.log('newVal', newVal)
        console.log('oldVal', oldVal)
        console.log('value has been changed')
      }
    }
  },
  data: {
    years: years,
    year: date.getFullYear(),
    months: months,
    month: date.getMonth() + 1,
    value: [0, 0],
  },
  externalClasses: [],
  lifetimes: {
    attached: function () {
      // 在组件实例进入页面节点树时执行
      for (let i = 2010; i <= date.getFullYear(); i++) {
        years.push(i)
      }

      for (let i = 1; i <= 12; i++) {
        months.push(i)
      }
      this.setData({
        years: years,
        months: months,
      })
      console.log('长度', this.data.years.length - 1)
      console.log('长度', this.data.month)
      this.setData({
        value: [this.data.years.length - 1, this.data.month - 1]
      })
    },
    detached: function () {
      // 在组件实例被从页面节点树移除时执行
    }
  },

  methods: {
    bindChange: function (e) {
      const val = e.detail.value
      // console.log('发生改变',e)
      // console.log('发生改变', this.data.years[val[0]])
      // console.log('发生改变', this.data.months[val[1]])
      this.setData({
        year: this.data.years[val[0]],
        month: this.data.months[val[1]],
        valueCache: [val[0], val[1]]
      })
      // console.log('发生改变', this.data.year)
      // console.log('发生改变', this.data.month)
    },
    _hidemodal: function () {
      this.triggerEvent('hidemodal')
    },
    _confirmDate: function () {
      this.triggerEvent('confirmDate', { date: this.data.year + '-' + this.data.month })
      if (this.data.valueCache) {
        this.setData({
          value: [... this.data.valueCache]
        })
      }
    }
  },
})
