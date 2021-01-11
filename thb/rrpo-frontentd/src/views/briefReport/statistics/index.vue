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
            <a-range-picker
              :placeholder="['开始时间', '结束时间']"
              format="YYYY-MM"
              :locale="locale"
              :value="monthValue"
              :mode="mode"
              :show-time="{ format: 'YYYY-MM' }"
              @panelChange="monthChange"
              @blur="cityBlur(monthValue, $event)"
              @change="onChange"
              @ok="onOk"
            />
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
          <template slot="operation" slot-scope="text, record">
            <a @click="details(record)" style="color: #4a9ff5">详情</a>
          </template>
        </a-table>
      </a-col>
<!--      <a-col :span="10"  v-else>-->
<!--        <a-table-->
<!--          :data-source="dataSource2"-->
<!--          :columns="columns2"-->
<!--          :pagination2="pagination2"-->
<!--          @change="handleTableChange2"-->
<!--        >-->
<!--          <a slot="titles2" slot-scope="text,record" style="color:#6290FF" @click="look(record)" title="text">{{ text }}</a>-->
<!--        </a-table>-->
<!--      </a-col>-->
      <a-col :span="14">
        <div ref="statistic" :style="{width: '100%', height: '720px'}"></div>
      </a-col>
    </a-row>
    <in-shield-road ref="oldRoad" hidden />
  </div>
</template>
<script>
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import InShieldRoad from '../inShieldRoad/InShieldRoad'
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
      type: 'bar',
      data: [],
      itemStyle: {
        color: '#42B983'
      }
    }
  ]
}
export default {
  components: {InShieldRoad},
  name: 'Index',
  data () {
    return {
      titleText: '护路简报统计',
      character: {
        title: ''
      },
      mode: ['month', 'month'],
      monthValue: [],
      ifOpen: true,
      characters: {},
      loading: false,
      locale,
      dataSource: [],
      dataSource2: [],
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      pagination2: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      Statistics
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
        },
        {
          title: '数量',
          dataIndex: 'counts',
          width: '200px',
          align: 'center'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: {customRender: 'operation'}
        }
      ]
    }
  },
  mounted () {
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
        console.log('简报：', res)
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    // 详情查看
    details (data) {
      let param = {
        id: data.id
      }
      this.$get('/briefing/inbox', param).then(res => {
        this.$refs.oldRoad.viewLook(res.data.data.rows[0])
      })
    },
    statistic () {
      let param = {}
      let myChart = echarts.init(this.$refs.statistic)
      myChart.setOption(Statistics, true)
      // 触发条形图点击事件
      myChart.on('click', params => {
        param.deptName = params.name
        if (this.character.title !== undefined || this.character.title !== null) {
          param.title = this.character.title
        }
        if (this.character.startTime !== undefined) {
          param.startTime = this.character.startTime
        }
        if (this.character.endTime !== undefined) {
          param.endTime = this.character.endTime
        }
        this.character.deptName = params.name
        param.pageNum = 1
        param.pageSize = 10
        this.getDqjb(param)
      })
    },
    getDqjb (param) {
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.$get('/briefing/countListByCity', param).then(res => {
        console.log('点击条形图:', res.data.data)
        let newData = res.data.data
        let pagination = { ...this.pagination2 }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
      })
    },
    look (e) {
      let ids = {briefingId: e.id}
      this.loading = true
      this.titleText = e.title + '统计'
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
      if (this.character.deptName !== undefined) {
        this.getDqjb({
          ...this.character
        })
      } else {
        this.fetch({
          ...this.character
        })
      }
    },
    monthChange (value, mode) {
      this.monthValue = value
      this.mode = [mode[0] === 'date' ? 'month' : mode[0], mode[1] === 'date' ? 'month' : mode[1]]
    },
    cityBlur (value, data) {
      if (value === undefined) {
        this.character.startTime = ''
        this.character.endTime = ''
        this.characters = []
        this.titleText = '护路简报统计'
      } else {
        if (value.length !== 0) {
          let start = new Date(value[0]._d)
          let end = new Date(value[1]._d)
          let startMonthData = ''
          let endMonthData = ''
          if (start.getMonth() < 9) {
            startMonthData = '0' + (start.getMonth() + 1)
          } else {
            startMonthData = (start.getMonth() + 1)
          }
          if (end.getMonth() < 9) {
            endMonthData = '0' + (end.getMonth() + 1)
          } else {
            endMonthData = (end.getMonth() + 1)
          }
          let ba = start.getFullYear() + '-' + startMonthData + '-01 00:00'
          let bb = end.getFullYear() + '-' + endMonthData + '-31 23:59'
          this.character.startTime = ba
          this.character.endTime = bb
          this.characters.startTime = ba
          this.characters.endTime = bb
        } else {
          this.character.startTime = ''
          this.character.endTime = ''
          this.characters = []
          this.titleText = '护路简报统计'
        }
      }
    },
    onChange (value) {
      this.monthValue = value[0]
      // var d = new Date('Fri Dec 12 2014 08:00:00 GMT+0800');
      // d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds();
      // this.character.startTime = dateString + '-01 00:00'
      // this.character.endTime = dateString + '-31 23:59'
      // this.characters.startTime = dateString + '-01 00:00'
      // this.characters.endTime = dateString + '-31 23:59'
    },
    onOk (value) {
      if (value.length !== 0) {
        let start = new Date(value[0]._d)
        let end = new Date(value[1]._d)
        let startMonthData = ''
        let endMonthData = ''
        if (start.getMonth() < 9) {
          startMonthData = '0' + (start.getMonth() + 1)
        } else {
          startMonthData = (start.getMonth() + 1)
        }
        if (end.getMonth() < 9) {
          endMonthData = '0' + (end.getMonth() + 1)
        } else {
          endMonthData = (end.getMonth() + 1)
        }
        let ba = start.getFullYear() + '-' + startMonthData + '-01 00:00'
        let bb = end.getFullYear() + '-' + endMonthData + '-31 23:59'
        this.character.startTime = ba
        this.character.endTime = bb
        this.characters.startTime = ba
        this.characters.endTime = bb
      } else {
        console.log('选择时间为空')
      }
    },
    search () {
      if (this.characters.startTime !== undefined) {
        this.titleText = this.character.startTime.slice(0, 4) + '年' + this.character.startTime.slice(5, 7) + '月至' + this.character.endTime.slice(0, 4) + '年' + this.character.endTime.slice(5, 7) + '月简报统计'
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
      this.monthValue = []
      this.titleText = '护路简报统计'
      this.character = []
      this.characters = []
      this.fetch()
      this.query()
      this.loading = false
    }
  }
}
</script>

<style scoped>
</style>
