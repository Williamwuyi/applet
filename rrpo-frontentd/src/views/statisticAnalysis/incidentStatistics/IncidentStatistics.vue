<template>
  <div ref="map" style="height:720px;width:800px;margin: 0 auto" ></div>
</template>
<script>
import echarts from 'echarts'
import 'echarts/map/js/province/hunan.js' // 引入湖南地图
import option from './map-option.js'
export default {
  name: 'IncidentStatistics',
  data () {
    return {
    }
  },
  // 钩子函数  不了解的话 建议看看 vue的生命周期
  mounted () {
    this.mapEchartsInit()
  },
  methods: {
    mapEchartsInit () {
      var myChart = echarts.init(this.$refs.map)
      myChart.setOption(option, true)
    }
  }
}
</script>
