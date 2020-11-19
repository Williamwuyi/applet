<template>
  <div style="width: 100%;min-height: 780px">
    <a-form layout="horizontal">
      <a-row>
        <a-col :md="4" :sm="20">
          <a-form-item
            label="标题:"
            :labelCol="{span: 4}"
            :wrapperCol="{span: 18, offset: -2}">
            <a-input v-model="character.title"/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="20" >
          <a-form-item
            label="接收时间:"
            :labelCol="{span: 5}"
            :wrapperCol="{span: 18, offset: 0}">
            <a-month-picker :locale="locale" placeholder="选择查询时间" rel="releaseTime" @change="onChange" v-model="monthPicker" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24" style="margin-left: 10px;margin-top: 3px">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </a-col>
      </a-row>
    </a-form>
    <a-row>
      <a-col :span="10">
        <a-table
          :data-source="dataSource"
          :columns="columns"
          :pagination="pagination"
          @change="handleTableChange"
        >
          <a slot="titles" slot-scope="text,record" style="color:#6290FF" @click="look(record)" title="text">{{ text }}</a>
        </a-table>
      </a-col>
      <a-col :span="14">
        <div ref="statistic" :style="{width: '100%', height: '720px'}"></div>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import echarts from 'echarts'
// 简报统计
const Statistics = {
  title: {
    text: ''
  },
  tooltip: {
    trigger: 'axis'
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
      name: '数量',
      type: 'line',
      data: [],
      itemStyle: {
        color: '#100000'
      }
    }
  ]
}
export default {
  name: 'Index',
  data () {
    return {
      titleText: '',
      character: {
        title: ''
      },
      characters: {},
      loading: false,
      locale,
      dataSource: [],
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      Statistics,
      monthPicker: null
    }
  },
  computed: {
    columns () {
      return [
        {
          title: '标题',
          dataIndex: 'title',
          ellipsis: true,
          scopedSlots: { customRender: 'titles' }
        },
        {
          title: '接收时间',
          dataIndex: 'releaseTime',
          width: '200px',
          align: 'center'
        }
      ]
    }
  },
  mounted () {
    let date = new Date()
    this.characters.startTime = date.getFullYear() + '-' + (date.getMonth() + 1) + '-01 00:00'
    this.characters.endTime = date.getFullYear() + '-' + (date.getMonth() + 1) + '-31 23:59'
    this.titleText = date.getFullYear() + '年' + (date.getMonth() + 1) + '月份简报统计'
    console.log(this.characters)
    this.fetch()
    this.query()
  },
  methods: {
    query () {
      this.loading = true
      this.$get('/briefing/countCity', this.characters).then((e) => {
        this.Statistics.xAxis.data = []
        this.Statistics.series[0].data = []
        console.log('护路简报统计数据', e)
        let moneyData = e.data
        for (let i in moneyData) {
          this.Statistics.xAxis.data.push(moneyData[i].city)
          this.Statistics.series[0].data.push(moneyData[i].count)
        }
        this.Statistics.title.text = this.titleText
        this.statistic()
      })
      this.loading = false
    },
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/briefing/countList', params).then(res => {
        let newData = res.data.data
        console.log(newData)
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    statistic () {
      let myChart = echarts.init(this.$refs.statistic)
      myChart.setOption(Statistics, true)
    },
    look (e) {
      let ids = {briefingId: e.id}
      this.loading = true
      this.titleText = e.title + '简报统计'
      this.$get('/briefing/countCityById', ids).then((e) => {
        this.Statistics.xAxis.data = []
        this.Statistics.series[0].data = []
        console.log('护路简报统计数据', e)
        let moneyData = e.data
        for (let i in moneyData) {
          this.Statistics.xAxis.data.push(moneyData[i].city)
          this.Statistics.series[0].data.push(moneyData[i].count)
        }
        this.Statistics.title.text = this.titleText
        this.statistic()
      })
      this.loading = false
    },
    // 分页点击
    handleTableChange (pagination) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fetch({
        ...this.character
      })
    },
    onChange (date, dateString) {
      this.character.startTime = dateString + '-01 00:00'
      this.character.endTime = dateString + '-31 23:59'
      this.characters.startTime = dateString + '-01 00:00'
      this.characters.endTime = dateString + '-31 23:59'
    },
    search () {
      if (this.character.startTime !== undefined) {
        this.titleText = this.character.startTime.slice(0, 4) + '年' + this.character.startTime.slice(5, 7) + '月份简报统计'
      }
      this.fetch({
        ...this.character
      })
      this.query({
        ...this.characters
      })
    },
    reset () {
      this.loading = true
      let date = new Date()
      this.titleText = date.getFullYear() + '年' + (date.getMonth() + 1) + '月份简报统计'
      this.character = []
      this.monthPicker = null
      this.fetch()
      this.query()
      this.loading = false
    }
  }
}
</script>

<style scoped>
</style>
