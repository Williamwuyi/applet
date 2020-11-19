<template>
  <div style="width: 100%; height: 100%">
    <div>
      <a-row>
        <a-col :span="2" style="width: 70px; margin-top: 5px;">
          安装年份：
        </a-col>
        <a-col :span="2">
          <a-input v-model="year"/>
        </a-col>
        <a-col :span="2" style="margin-left: 20px">
          <a-button type="primary" @click="search">查询</a-button>
        </a-col>
      </a-row>
    </div>
    <div ref="statistic" style="height:600px;width:100%;" ></div>
    <div style="text-align: center; font-size: 22px;">监控安装统计图</div>
  </div>
</template>

<script>
import echarts from 'echarts'
const Statistics = {
  color: ['#3398DB'],
  tooltip: {
    trigger: 'axis',
    axisPointer: { // 坐标轴指示器，坐标轴触发有效
      type: 'shadow'// 默认为直线，可选为：'line' | 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [
    {
      type: 'category',
      data: [],
      axisTick: {
        alignWithLabel: true
      },
      // x轴字体竖着排
      axisLabel: {
        interval: 0,
        rotate: 0,
        formatter: function (val) {
          return val.split('').join('\n')
        }
      }
    }
  ],
  yAxis: [
    {
      type: 'value'
    }
  ],
  series: [
    {
      name: '安装个数',
      type: 'bar',
      barWidth: '60%',
      data: []
    }
  ]
}
export default {
  name: 'InstallStatistic',
  data () {
    return {
      Statistics,
      year: ''
    }
  },
  mounted () {
    this.query()
  },
  methods: {
    query () {
      this.$get('/accident/jk/countTable', {year: this.year}).then(res => {
        let data = res.data.data
        console.log('监控安装统计数据', data)
        let dataName = []
        let dataCount = []
        for (let i = 0; data.length > i; i++) {
          dataName.push(data[i].name)
          dataCount.push(data[i].count)
        }
        this.Statistics.xAxis[0].data = dataName
        this.Statistics.series[0].data = dataCount
        console.log(dataName)
        console.log('----', dataCount)
        this.statistic()
      })
    },
    search () {
      this.year = this.year
      this.query()
    },
    statistic () {
      let myChart = echarts.init(this.$refs.statistic)
      myChart.setOption(Statistics, true)
    }
  }
}
</script>

<style scoped>

</style>
