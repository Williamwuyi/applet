<template>
  <div style="width: 100%; height: 100%">
    <div>
      <a-row>
        <a-col :span="3" style="width: 70px; margin-top: 5px;">
          安装年份：
        </a-col>
        <a-col :span="2">
          <a-input v-model="year" placeholder="年份格式：2020"/>
        </a-col>
        <a-col :span="2" style="width: 50px; margin-top: 5px;margin-left: 20px">
          线路：
        </a-col>
        <a-col :span="3">
          <a-select style="width: 150px" ref="xlmc" v-model="xlmc">
            <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
              {{n.fieldName}}
            </a-select-option>
          </a-select>
        </a-col>
        <a-col :span="4" style="">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
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
      xlmc: '',
      line: '',
      lineData: [],
      year: ''
    }
  },
  mounted () {
    this.query()
    // 线路
    this.$get('/dict/getListTable', {parentId: '1867989d0aaaf82f79b34070cc77d766', pageSize: 50}).then(res => {
      this.lineData = res.data.data.records
    })
  },
  methods: {
    query (param = {}) {
      this.$get('/accident/jk/countTable', param).then(res => {
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
        this.statistic()
      })
    },
    search () {
      const param = {}
      if (this.year !== '') {
        param.year = this.year
      }
      if (this.xlmc !== '') {
        param.xlmc = this.$refs.xlmc.$el.innerText
      }
      this.query(param)
    },
    reset () {
      this.year = ''
      this.xlmc = ''
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
