<template>
  <div style="width: 100%; height: auto">
    <div>
<!--      <a-cascader  change-on-select-->
<!--                   style="width: 105px;"-->
<!--                   @change="onChangeTime"-->
<!--                   :options="options"-->
<!--                   placeholder="未选择"-->
<!--                   ref="time"-->
<!--      />-->
      <a-select v-model="mr" @change="onChangeTime" style="width: 100px">
        <a-select-option v-for="(n,index) in options" :key="index" :value="n.yearId">
          {{n.yearDate}}
        </a-select-option>
      </a-select>
    </div>
    <div id="myChart" style="height:600px;width:100%;" ></div>
    <div style="text-align: center; font-size: 22px;">湖南省本年度考核统计表</div>
  </div>
</template>

<script>
import echarts from 'echarts'
export default {
  name: 'Organization',
  data () {
    return {
      abc: [],
      jcgz: [],
      count: [],
      gzxg: [],
      mr: [],
      options: []
    }
  },
  mounted () {
    this.getDate()
  },
  methods: {
    getdept (yearId) {
      this.$get('/check/num/map', {yearId: yearId}).then((r) => {
        let newData = r.data.data
        this.abc = newData.dept
        this.gzxg = newData.gzXg
        this.jcgz = newData.jcGz
        this.count = newData.count
        this.$nextTick(function () {
          // 此时已经渲染完成
          this.drawLine()
        })
      })
    },
    // 日期
    getDate () {
      this.$get('/check/year/list').then(res => {
        let data = res.data.data
        this.options = data
        this.getdept(data[0].yearId)
        this.mr = data[0].yearId
        // data.forEach(t => {
        //   this.options.push({value: t.yearId, label: t.yearDate})
        // })
      })
    },
    // 下拉函数
    onChangeTime (value) {
      this.getdept(value)
    },
    drawLine () {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('myChart'))
      // 绘制图表
      myChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        toolbox: {
          feature: {
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },
        legend: {
          data: ['基本工作', '工作效果', '总共得分']
        },
        xAxis: [
          {
            type: 'category',
            data: this.abc,
            axisPointer: {
              type: 'shadow'
            },
            // 解决树状图名字太长显示不了的情况
            axisLabel: {
              interval: 0
            }
          }
        ],
        grid: {
          left: '2%',
          right: '2%',
          bottom: '10%',
          top: '65',
          containLabel: true
        },
        yAxis: [
          {
            type: 'value',
            name: '工作效果',
            min: 0,
            max: 60,
            interval: 20,
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '总得分',
            min: 0,
            max: 100,
            interval: 5,
            axisLabel: {
              formatter: '{value}'
            }
          }
        ],
        series: [
          {
            name: '基本工作',
            type: 'bar',
            data: this.jcgz
          },
          {
            name: '工作效果',
            type: 'bar',
            data: this.gzxg,
            itemStyle: {
              normal: {
                color: '#42B983'
              }
            }
          },
          {
            name: '总共得分',
            type: 'line',
            yAxisIndex: 1,
            data: this.count
          }
        ]
      }, true)
    }
  }
}
</script>

<style scoped>

</style>
