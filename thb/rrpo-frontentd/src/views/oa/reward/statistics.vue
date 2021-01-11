<template>
  <div style="width: 100%">
    <a-form>
      <a-row>
        <a-col :span="15">
          <h4 style="text-align: center;font-size: 28px;font-weight: bold;color: #0d487b">
            {{thisStartYear}}年{{thisStartMonth}}月{{thisStartDate}}日至{{thisEndYear}}年{{thisEndMonth}}月{{thisEndDate}}日发布情况统计
          </h4>
        </a-col>
        <a-col :span="6">
          <a-form-item label="时间查询" :labelCol="{span: 5}" :wrapperCol="{span: 13, offset: 1}">
            <range-date @change="handleDateChange" ref="createTime"></range-date>
          </a-form-item>
        </a-col>
        <a-col :span="3">
          <div style="float: right">
            <a-button type="primary" @click="primary">查询</a-button>
            <a-button @click="rest">重置</a-button>
          </div>
        </a-col>
      </a-row>
    </a-form>
    <a-row>
      <a-col :span="18" class="demo-infinite-container">
        <div ref="anyMoneys" :style="{width: '100%', height: '100%'}"></div>
      </a-col>
<!--      <a-col :span="6">-->
<!--        <div ref="map1" :style="{width: '100%', height: '400px'}"></div>-->
<!--        <p style="position: relative;top: -50px;text-align: center">发布类型</p>-->
<!--      </a-col>-->
      <a-col :span="6">
        <div ref="map2" :style="{width: '100%', height: '400px'}"></div>
      </a-col>
    </a-row>
    <div ref="cityState" :style="{width: '100%', height: '350px'}"></div>
  </div>
</template>
<script>
import echarts from 'echarts'
import RangeDate from '@/components/datetime/RangeDate'
// 各市州上报情况统计
const cityState = {
  title: {
    text: '上报情况统计'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      crossStyle: {
        color: '#999'
      }
    }
  },
  legend: {
    data: ['上报数', '通过数']
  },
  xAxis: [
    {
      type: 'category',
      data: [],
      axisPointer: {
        type: 'shadow'
      }
    }
  ],
  grid: {
    left: '1%',
    right: '1%',
    bottom: '3%',
    containLabel: true
  },
  yAxis: [
    {
      type: 'value',
      name: '数量',
      interval: 50,
      axisLabel: {
        formatter: '{value}'
      }
    }
  ],
  series: [
    {
      name: '上报数',
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#0000FF'
      }
    },
    {
      name: '通过数',
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#00FF00'
      }
    }
  ]
}
// 全省总发布情况分析
const option1 = {
  tooltip: {
    trigger: 'item',
    formatter: '{b} : {c} ({d}%)'
  },
  series: [
    {
      type: 'pie',
      radius: '55%',
      center: ['50%', '50%'],
      data: [
        {value: 335, name: '总通过数'},
        {value: 310, name: '总驳回数'},
        {value: 234, name: '未审核数'}
      ],
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
// 全省事件类型分析
const eventType = {
  title: {
    text: '事件类型统计'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{b} : {c} ({d}%)'
  },
  series: [
    {
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
// 各市州审批通过金额统计
const anyMoney = {
  title: {
    text: '审批通过金额统计'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['总通过金额']
  },
  grid: {
    left: '1%',
    right: '1%',
    bottom: '3%',
    containLabel: true
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: ['20%', '20%'],
    data: [],
    axisLabel: {
      interval: 0,
      rotate: 0,
      formatter: function (val) {
        return val.split('').join('\n')
      }
    }
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '金额',
      type: 'line',
      data: [],
      itemStyle: {
        color: '#100000'
      }
    }
  ]
}
export default {
  name: 'statistics',
  components: {RangeDate},
  data () {
    return {
      form: this.$form.createForm(this),
      cityState, // 市州详情统计
      anyMoney, // 市州金额详情统计
      option1, // 全省统计
      eventType, // 全省事件类型统计
      character: {}, // 事件查询
      thisStartYear: '',
      thisStartMonth: '01',
      thisStartDate: '01',
      thisEndYear: '',
      thisEndMonth: '12',
      thisEndDate: '31'
    }
  },
  mounted () {
    this.thisStartYear = new Date().getFullYear()
    this.thisEndYear = new Date().getFullYear()
    let start = this.thisStartYear + '-01-01' + ` 00:00:00`
    let end = this.thisEndYear + '-12-31' + ` 23:59:59`
    this.character.startTime = start
    this.character.endTime = end
    this.initialize()
  },
  methods: {
    initialize () {
      // 初始化发件情况统计
      this.$get('/prize/countRelease', this.character).then((r) => {
        let passNum = []
        let totals = []
        let ciTys = []
        // 查询是否是省办
        this.$get('/dept/findRank').then(res => {
          let userRank = res.data.data.rank
          if (userRank === 0) {
            let totalAndPass = JSON.parse(r.data.data)
            for (let i in totalAndPass) {
              ciTys.push(totalAndPass[i].name)
              totals.push(JSON.parse(totalAndPass[i].not)[0])
              passNum.push(JSON.parse(totalAndPass[i].pass)[0])
            }
          } else {
            let all = r.data.data.all
            let done = r.data.data.done
            for (let i in all) {
              ciTys.push(all[i].dept_name)
              totals.push(all[i].count)
            }
            for (let i in done) {
              passNum.push(done[i].count)
            }
          }
          this.cityState.xAxis[0].data = ciTys
          this.cityState.series[0].data = totals
          this.cityState.series[1].data = passNum
          this.statistic()
        })
      })
      // 初始化金额统计
      this.$get('/prize/countMoney', this.character).then((r) => {
        let moneyData = r.data.data
        this.anyMoney.xAxis.data = []
        this.anyMoney.series[0].data = []
        for (let i in moneyData) {
          this.anyMoney.xAxis.data.push(moneyData[i].dept_name)
          this.anyMoney.series[0].data.push(moneyData[i].money)
        }
        this.statistic1()
      })
      // 初始化事件类型统计
      this.$get('/prize/countType', this.character).then((e) => {
        let eList = e.data.data
        if (eList[0] === undefined) {
          this.eventType.series[0].data = [{value: 1, name: '该时间段暂无数据。。。'}]
        } else {
          this.tipsMessage = false
          this.eventType.series[0].data = []
          let dataList = this.eventType.series[0].data
          for (let i in eList) {
            dataList.push({value: eList[i].count, name: eList[i].types})
          }
        }
        this.statistic2()
      })
    },
    // 时间查询
    handleDateChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
    },
    statistic () {
      let myChart = echarts.init(this.$refs.cityState)
      myChart.setOption(cityState, true)
    },
    statistic1 () {
      let myChart = echarts.init(this.$refs.anyMoneys)
      myChart.setOption(anyMoney, true)
    },
    statistic2 () {
      let myChart = echarts.init(this.$refs.map2)
      myChart.setOption(eventType, true)
    },
    primary () {
      this.loading = true
      // 通过this.character来进行查询
      // 查询各市州发布情况
      this.thisStartYear = this.character.startTime.slice(0, 4)
      this.thisStartMonth = this.character.startTime.slice(5, 7)
      this.thisStartDate = this.character.startTime.slice(8, 10)
      this.thisEndYear = this.character.endTime.slice(0, 4)
      this.thisEndMonth = this.character.endTime.slice(5, 7)
      this.thisEndDate = this.character.endTime.slice(8, 10)
      this.cityState.xAxis[0].data = []
      this.cityState.series[0].data = []
      this.cityState.series[1].data = []
      this.anyMoney.xAxis.data = []
      this.anyMoney.series[0].data = []
      this.eventType.series[0].data = []
      this.initialize({
        ...this.character
      })
      this.loading = false
    },
    // 重置
    rest () {
      this.loading = true
      this.thisStartYear = new Date().getFullYear()
      this.thisEndYear = new Date().getFullYear()
      let start = this.thisStartYear + '-01-01' + ` 00:00:00`
      let end = this.thisEndYear + '-12-31' + ` 23:59:59`
      this.character.startTime = start
      this.character.endTime = end
      this.initialize({
        ...this.character
      })
      // 清空时间选择
      this.$refs.createTime.reset()
      this.loading = false
    }
  }
}
</script>
<style scoped>
  .tipsMessage{
    width: 100%;
    height: 100%;
    line-height: 400px;
    color: #FF0000;
    text-align: center;
    font-size: 28px;
    font-weight: bold;
  }
  .demo-infinite-container {
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    overflow: auto;
    padding: 8px 24px;
    height: 300px;
  }
  .demo-loading-container {
    position: absolute;
    bottom: 40px;
    width: 100%;
    text-align: center;
  }
</style>
