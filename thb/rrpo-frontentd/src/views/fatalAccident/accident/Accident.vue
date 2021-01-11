<template>
  <div>
    <a-row style="margin: 10px 0">
      <a-col :span="4" >
       单位 ：
        <a-select @change="citySChange" v-model="unitG" v-has-any-permission="'communication:fatalitiesUnit'" style="width: 150px;">
          <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.id" >
            {{n.dept.deptName}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
          市级单位 ：
        <a-select @change="cityQChange" v-model="unitQ" v-has-any-permission="'communication:fatalitiesUnit'" style="width: 150px;">
          <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.dept.deptId">
            {{n.dept.deptName}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
        区县单位 ：
        <a-select @change="cityXChange" v-model="unitX" v-has-any-permission="'communication:fatalitiesUnit'" style="width: 150px;">
          <a-select-option v-for="(n,index) in cityX" :key="index" :value="n.deptId" >
            {{n.deptName}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
          发生时间 ：
        <a-month-picker placeholder="开始时间" @change="onStartChange" v-model="restTime" style="width: 150px;" />
      </a-col>
      <a-col :span="4">
          结束时间 ：
        <a-month-picker placeholder="开始时间" @change="onEndChange" v-model="restTime" style="width: 150px;" />
      </a-col>
      <a-col :span="3">
        <a-button type="primary" @click="search">查询</a-button>
        <a-button style="margin-left: 8px" @click="reset">重置</a-button>
      </a-col>
    </a-row>
    <div style="height:750px;overflow: auto">
    <a-row style="text-align: center;">
      <a-col span="8">
        <div ref="sgxz" style="height:400px;width:500px;border:1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="znqj" style="height:400px;width:500px;border:1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="hlms" style="height:400px;width:500px;border:1px dashed #E8E8E8"></div>
      </a-col>
    </a-row>
    <a-row style="text-align: center;">
      <a-col span="8">
        <div ref="nld" style="height:400px;width:500px;border: 1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="fbcd" style="height:400px;width:500px;;border: 1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="jzd" style="height:400px;width:500px;;border: 1px dashed #E8E8E8"></div>
      </a-col>
    </a-row>
    <a-row style="text-align: center;">
      <a-col span="8">
        <div ref="ltlyj" style="height:400px;width:500px;border: 1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="sf" style="height:400px;width:500px;border: 1px dashed #E8E8E8"></div>
      </a-col>
      <a-col span="8">
        <div ref="sgqx" style="height:400px;width:500px;border: 1px dashed #E8E8E8"></div>
      </a-col>
    </a-row>
  </div>
  </div>
</template>
<script>
import echarts from 'echarts'
import RangeDate from '@/components/datetime/RangeDate'
const sgxzOption = {
  title: {
    text: '事故性质',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const znqjOption = {
  title: {
    text: '站内区间',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#3CB371', '#CDAD00'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const hlmsOption = {
  title: {
    text: '护路模式',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#53868B', '#BDB76B', '#8B658B', '#D02090'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
let nldOption = {
  title: {
    text: '年龄段',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#5F9EA0', '#CD5C5C', '#EE7942', '#8B658B', '#B03060'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const fbcdOption = {
  title: {
    text: '封闭程度',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#8A2BE2', '#CD7054', '#27408B', '#5D478B'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const jzdOption = {
  title: {
    text: '居住地',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#00008B', '#008B8B', '#90EE90', '#8B0000'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const ltlyjOption = {
  title: {
    text: '离铁路远近',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#9370DB', '#EE7621', '#D2B48C', '#8B658B'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const sfOption = {
  title: {
    text: '身份',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#228B22', '#00CDCD', '#FF7F50', '#D02090'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const sgqxOption = {
  title: {
    text: '事故情形',
    x: 'center',
    y: 'bottom'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  color: ['#8A2BE2', '#EE4000', '#8B658B', '#D02090', '#00EE00'],
  series: [
    {
      name: '描述',
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}

export default {
  name: 'Accident',
  components: {RangeDate},
  data () {
    return {
      sgxzOption,
      nldOption,
      znqjOption,
      hlmsOption,
      fbcdOption,
      jzdOption,
      ltlyjOption,
      sfOption,
      sgqxOption,
      unitG: '',
      unitQ: '',
      unitX: '',
      cityS: [],
      cityQ: [],
      cityX: [],
      restTime: null,
      startTime: null,
      endTime: null,
      cityCsId: null, // 县区单位条件查询
      cityXsId: null, // 单位条件查询
      cityQsId: null // 单位条件查询
    }
  },
  // 钩子函数  不了解的话 建议看看 vue的生命周期
  mounted () {
    this.fetch()

    // 单位公安处
    this.$get('/accident/dept/list').then(res => {
      this.cityS = res.data.data
    })
  },
  methods: {
    fetch (param = {}) {
      this.$get('/accident/accident/map', param).then(res => {
        // nature事故性质，instation_section站内区间，read护路模式，age年龄段，
        // closed封闭程度，jzd居住地，distance离铁路远近，identity身份，conditions事故情形
        console.log('图形数据：', res.data)
        let ageData = res.data.age
        let fbcdData = res.data.closed
        let sgqxData = res.data.conditions
        let ltlyjData = res.data.distance
        let jzdData = res.data.jzd
        let sfData = res.data.identity
        let znxzData = res.data.instation_section
        let sgxzData = res.data.nature
        let hlmsData = res.data.road

        // 事故性质
        let sgxzArr = []
        if (sgxzData.length !== 0) {
          for (let i = 0; sgxzData.length > i; i++) {
            if (sgxzData[i].subscript === 'A') {
              let Obj = {}
              Obj.name = '一般铁路事故'
              Obj.value = sgxzData[i].number
              sgxzArr.push(Obj)
            }
            if (sgxzData[i].subscript === 'B') {
              let Obj = {}
              Obj.name = '以外事故'
              Obj.value = sgxzData[i].number
              sgxzArr.push(Obj)
            }
            if (sgxzData[i].subscript === 'C') {
              let Obj = {}
              Obj.name = '自杀事故'
              Obj.value = sgxzData[i].number
              sgxzArr.push(Obj)
            }
          }
        }
        this.sgxzOption.series[0].data = sgxzArr
        // 站内区间
        let znqjArr = []
        for (let i = 0; znxzData.length > i; i++) {
          if (znxzData[i].subscript === 'A') {
            let obj = {}
            obj.name = '站内'
            obj.value = znxzData[i].number
            znqjArr.push(obj)
          }
          if (znxzData[i].subscript === 'B') {
            let obj = {}
            obj.name = '区间'
            obj.value = znxzData[i].number
            znqjArr.push(obj)
          }
        }
        this.znqjOption.series[0].data = znqjArr
        // 护路模式
        let hlmsArr = []
        for (let i = 0; hlmsData.length > i; i++) {
          if (hlmsData[i].subscript === 'A') {
            let obj = {}
            obj.name = '专业护理区间'
            obj.value = hlmsData[i].number
            hlmsArr.push(obj)
          }
          if (hlmsData[i].subscript === 'B') {
            let obj = {}
            obj.name = '有奖义务护理区间'
            obj.value = hlmsData[i].number
            hlmsArr.push(obj)
          }
          if (hlmsData[i].subscript === 'C') {
            let obj = {}
            obj.name = '其他'
            obj.value = hlmsData[i].number
            hlmsArr.push(obj)
          }
        }
        this.hlmsOption.series[0].data = hlmsArr
        // 年龄段
        let ageArr = []
        for (let i = 0; ageData.length > i; i++) {
          if (ageData[i].subscript === 'A') {
            let ageObj = {}
            ageObj.name = '0~6岁'
            ageObj.value = ageData[i].number
            ageArr.push(ageObj)
          }
          if (ageData[i].subscript === 'B') {
            let ageObj = {}
            ageObj.name = '7~18岁'
            ageObj.value = ageData[i].number
            ageArr.push(ageObj)
          }
          if (ageData[i].subscript === 'C') {
            let ageObj = {}
            ageObj.name = '19~64岁'
            ageObj.value = ageData[i].number
            ageArr.push(ageObj)
          }
          if (ageData[i].subscript === 'D') {
            let ageObj = {}
            ageObj.name = '64岁以上'
            ageObj.value = ageData[i].number
            ageArr.push(ageObj)
          }
          if (ageData[i].subscript === 'E') {
            let ageObj = {}
            ageObj.name = '不详'
            ageObj.value = ageData[i].number
            ageArr.push(ageObj)
          }
        }
        this.nldOption.series[0].data = ageArr

        // 封闭模式
        let fbcdArr = []
        for (let i = 0; fbcdData.length > i; i++) {
          if (fbcdData[i].subscript === 'A') {
            let obj = {}
            obj.name = '全封闭'
            obj.value = fbcdData[i].number
            fbcdArr.push(obj)
          }
          if (fbcdData[i].subscript === 'B') {
            let obj = {}
            obj.name = '因社会管理原因造栅栏开口'
            obj.value = fbcdData[i].number
            fbcdArr.push(obj)
          }
          if (fbcdData[i].subscript === 'C') {
            let obj = {}
            obj.name = '因铁路原因造成栅栏开口'
            obj.value = fbcdData[i].number
            fbcdArr.push(obj)
          }
          if (fbcdData[i].subscript === 'D') {
            let obj = {}
            obj.name = '未封闭'
            obj.value = fbcdData[i].number
            fbcdArr.push(obj)
          }
        }
        this.fbcdOption.series[0].data = fbcdArr
        // 居住地
        let jzdArr = []
        for (let i = 0; jzdData.length > i; i++) {
          if (jzdData[i].subscript === 'A') {
            let obj = {}
            obj.name = '本村'
            obj.value = jzdData[i].number
            jzdArr.push(obj)
          }
          if (jzdData[i].subscript === 'B') {
            let obj = {}
            obj.name = '本乡'
            obj.value = jzdData[i].number
            jzdArr.push(obj)
          }
          if (jzdData[i].subscript === 'C') {
            let obj = {}
            obj.name = '本县'
            obj.value = jzdData[i].number
            jzdArr.push(obj)
          }
          if (jzdData[i].subscript === 'D') {
            let obj = {}
            obj.name = '其他'
            obj.value = jzdData[i].number
            jzdArr.push(obj)
          }
        }
        this.jzdOption.series[0].data = jzdArr
        // 离铁路远近
        let ltlyjArr = []
        for (let i = 0; ltlyjData.length > i; i++) {
          if (ltlyjData[i].subscript === 'A') {
            let obj = {}
            obj.name = '500米以内'
            obj.value = ltlyjData[i].number
            ltlyjArr.push(obj)
          }
          if (ltlyjData[i].subscript === 'B') {
            let obj = {}
            obj.name = '500米到2.5公里'
            obj.value = ltlyjData[i].number
            ltlyjArr.push(obj)
          }
          if (ltlyjData[i].subscript === 'C') {
            let obj = {}
            obj.name = '2.5公里以上'
            obj.value = ltlyjData[i].number
            ltlyjArr.push(obj)
          }
          if (ltlyjData[i].subscript === 'D') {
            let obj = {}
            obj.name = '不详'
            obj.value = ltlyjData[i].number
            ltlyjArr.push(obj)
          }
        }
        this.ltlyjOption.series[0].data = ltlyjArr
        // 身份
        let sfArr = []
        for (let i = 0; sfData.length > i; i++) {
          if (sfData[i].subscript === 'A') {
            let obj = {}
            obj.name = '中小学生'
            obj.value = sfData[i].number
            sfArr.push(obj)
          }
          if (sfData[i].subscript === 'B') {
            let obj = {}
            obj.name = '精神病患者'
            obj.value = sfData[i].number
            sfArr.push(obj)
          }
          if (sfData[i].subscript === 'C') {
            let obj = {}
            obj.name = '其他残障人员'
            obj.value = sfData[i].number
            sfArr.push(obj)
          }
          if (sfData[i].subscript === 'D') {
            let obj = {}
            obj.name = '其他'
            obj.value = sfData[i].number
            sfArr.push(obj)
          }
          if (sfData[i].subscript === 'E') {
            let obj = {}
            obj.name = '不详'
            obj.value = sfData[i].number
            sfArr.push(obj)
          }
        }
        this.sfOption.series[0].data = sfArr
        // 事故情形
        let sgqxArr = []
        for (let i = 0; sgqxData.length > i; i++) {
          if (sgqxData[i].subscript === 'A') {
            let obj = {}
            obj.name = '横穿铁路'
            obj.value = sgqxData[i].number
            sgqxArr.push(obj)
          }
          if (sgqxData[i].subscript === 'B') {
            let obj = {}
            obj.name = '沿铁路纵向行走'
            obj.value = sgqxData[i].number
            sgqxArr.push(obj)
          }
          if (sgqxData[i].subscript === 'C') {
            let obj = {}
            obj.name = '钻车底'
            obj.value = sgqxData[i].number
            sgqxArr.push(obj)
          }
          if (sgqxData[i].subscript === 'D') {
            let obj = {}
            obj.name = '线路上玩耍'
            obj.value = sgqxData[i].number
            sgqxArr.push(obj)
          }
          if (sgqxData[i].subscript === 'E') {
            let obj = {}
            obj.name = '其他情形'
            obj.value = sgqxData[i].number
            sgqxArr.push(obj)
          }
        }
        this.sgqxOption.series[0].data = sgqxArr
        this.mapEnld()
        this.mapEsgxz()
        this.mapEznqj()
        this.mapEhlms()
        this.mapEfbcd()
        this.mapEjzd()
        this.mapEltlyj()
        this.mapEsf()
        this.mapEsgqx()
      })
    },
    // 事故情形
    mapEsgxz () {
      var myChart = echarts.init(this.$refs.sgxz)
      myChart.setOption(sgxzOption, true)
    },
    // 站内区间
    mapEznqj () {
      var myChart = echarts.init(this.$refs.znqj)
      myChart.setOption(znqjOption, true)
    },
    // 护路模式
    mapEhlms () {
      var myChart = echarts.init(this.$refs.hlms)
      myChart.setOption(hlmsOption, true)
    },
    // 年龄段
    mapEnld () {
      this.$nextTick(function () {
        // 此时已经渲染完成
        var myChart = echarts.init(this.$refs.nld)
        myChart.setOption(this.nldOption, true)
      })
    },
    // 封闭程度
    mapEfbcd () {
      var myChart = echarts.init(this.$refs.fbcd)
      myChart.setOption(fbcdOption, true)
    },
    // 居住地
    mapEjzd () {
      var myChart = echarts.init(this.$refs.jzd)
      myChart.setOption(jzdOption, true)
    },
    // 离铁路远近
    mapEltlyj () {
      var myChart = echarts.init(this.$refs.ltlyj)
      myChart.setOption(ltlyjOption, true)
    },
    // 身份
    mapEsf () {
      var myChart = echarts.init(this.$refs.sf)
      myChart.setOption(sfOption, true)
    },
    // 事故情形
    mapEsgqx () {
      var myChart = echarts.init(this.$refs.sgqx)
      myChart.setOption(sgqxOption, true)
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.loading = true
      this.$get('/accident/dept/list', {prentId: key}).then(res => {
        this.cityQ = res.data.data
        this.loading = false
      })
      this.cityCsId = key
    },
    cityQChange (key) {
      this.loading = true
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityX = res.data
        this.loading = false
      })
      this.cityQsId = key
    },
    cityXChange (key) {
      this.cityXsId = key
    },
    // 条件查询
    search () {
      this.fetch({
        cityLevelId: this.cityQsId,
        quDeptId: this.cityXsId,
        policeId: this.cityCsId,
        startDate: this.startTime,
        endDate: this.endTime
      })
    },
    // 重置
    reset () {
      // 清空显示
      this.unitQ = ''
      this.unitG = ''
      this.unitX = ''
      // 清空值
      this.cityQsId = null
      this.cityCsId = null
      this.cityXsId = null
      this.restTime = null
      this.fetch()
    },
    // 选择开始时间
    onStartChange (value, date) {
      this.startTime = date.toString() + '-00'
    },
    // 选择结束时间
    onEndChange (value, date) {
      let year = date.toString().substring(0, 4)
      let months = date.toString().substring(5, 7)
      let month = parseInt(months) + 1
      let dateTime = year + '-' + month + '-00'
      this.endTime = dateTime
    }
  }
}
</script>
