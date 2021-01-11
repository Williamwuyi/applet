<template>
    <div style="width: 100%">
      <div :class="advanced ? 'search' : null">
        <a-form>
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="4" :sm="20" >
                <a-form-item label="开始时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="开始时间" @change="onStartChange" v-model="startTime">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" >
                <a-form-item label="结束时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="结束时间" @change="onEndChange" v-model="endTime">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" style="margin-top: 3px; margin-left: 10px">
                <a-button type="primary" @click="search">查询</a-button>
<!--                <a-button style="margin-left: 8px" @click="reset">重置</a-button>-->
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
      <a-table style="overflow: auto;width: 100%"
               :data-source="dataSource"
               :columns="columns"
               bordered
               :pagination="false"
               :rowKey="(record)=> record.id"
               :loading="loading"
      >
<!--        同期比较判断显示  -->
        <template slot="bj" slot-scope="text, record">
          <span v-if="text<0 && text !== '-Infinity'" style="color: red">
            {{ parseFloat(text * 100).toFixed(2).substring(1) }}% ↑
          </span>
          <span v-else-if="text>0 && text !== 'Infinity'" style="color: green">
            {{ parseFloat(text * 100).toFixed(2) }}% ↓
          </span>
          <span v-else-if="record.upNumber === 0 || record.upDnxs === 0 || record.upDnTjxs === 0 || record.upDeathToll === 0 || text === '-Infinity'">
          </span>
          <span v-else>
          </span>
        </template>
<!--        电脑系数-->
        <template slot="point" slot-scope="text, record">
          <span v-if="text.toString().indexOf('.') !== -1" style="text-align: left">
            {{text.toFixed(2)}}
          </span>
          <span v-else-if="text === 0">
          </span>
          <span v-else>
            {{text}}
          </span>
        </template>
      </a-table>
    </div>
</template>

<script>
const columns = [
  {
    title: '单位',
    width: '150px',
    dataIndex: 'deptName'
  },
  {
    title: '事故死亡人数',
    children: [
      {
        title: '上期死亡人数',
        width: '120px',
        dataIndex: 'upNumber',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '本期死亡人数',
        dataIndex: 'number',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj1',
        align: 'center',
        scopedSlots: {customRender: 'bj'}
      }
    ]
  },
  {
    title: '电脑分析责任死亡',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDnxs',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '本期死亡人数',
        dataIndex: 'dnxs',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj2',
        align: 'center',
        scopedSlots: {customRender: 'bj'}
      }
    ]
  },
  {
    title: '电脑调节分析死亡人数',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDnTjxs',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '本期死亡人数',
        dataIndex: 'dntjxs',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj3',
        align: 'center',
        scopedSlots: {customRender: 'bj'}
      }
    ]
  },
  {
    title: '属地管理责任死亡人数',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDeathToll',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '本期死亡人数',
        dataIndex: 'deathToll',
        scopedSlots: {customRender: 'point'}
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj4',
        align: 'center',
        scopedSlots: {customRender: 'bj'}
      }
    ]
  }
]
export default {
  name: 'Period',
  data () {
    return {
      advanced: false,
      pagination: false,
      columns,
      startTime: null,
      endTime: null,
      startYear: null,
      endYear: null,
      dataSource: [],
      loading: false
    }
  },
  mounted () {
    this.defaultTime()
  },
  methods: {
    fetch (param = {}) {
      this.$get('/accident/accident/compareTable', param).then(res => {
        console.log('同期比较数据：', res.data)
        this.dataSource = res.data.data
      })
    },
    defaultTime () {
      let date = new Date()
      let year = date.getFullYear()
      let lastYear = date.getFullYear() - 1
      let m = date.getMonth() + 1
      let startYear
      let endYear
      if (m < 10) {
        startYear = lastYear + '-0' + m
        endYear = year + '-0' + m
      } else {
        startYear = lastYear + '-' + m
        endYear = year + '-' + m
      }
      this.startTime = startYear
      this.endTime = endYear
      const param = {
        startYear: this.startTime,
        endYear: this.endTime
      }
      this.fetch(param)
    },
    // 条件查询
    search () {
      this.fetch({
        startYear: this.startTime,
        endYear: this.endTime
      })
    },
    // 重置
    reset () {
      this.startTime = null // 结束时间
      this.endTime = null // 开始时间
      this.fetch()
    },
    // 选择开始时间
    onStartChange (value, date) {
      this.startTime = date
    },
    // 选择结束时间
    onEndChange (value, date) {
      this.endTime = date
    }
  }
}
</script>

<style scoped>
  /deep/ .ant-table-tbody > tr > td {
    padding: 5px 5px;
  }
  /deep/ .ant-table-thead > tr > th {
    text-align: center;
    padding: 5px 5px;
  }
</style>
