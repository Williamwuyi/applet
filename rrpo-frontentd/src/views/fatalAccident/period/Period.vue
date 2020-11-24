<template>
    <div style="width: 100%">
      <div :class="advanced ? 'search' : null">
        <a-form>
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="4" :sm="20" >
                <a-form-item label="开始时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="开始时间" @change="onStartChange" v-model="restStartTime">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" >
                <a-form-item label="结束时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="结束时间" @change="onEndChange" v-model="restTime">
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
               :scroll={x:100,y:580}
               :columns="columns"
               :rowKey="(record)=> record.accidentId"
               :loading="loading"
      >
        <template slot="bj1" slot-scope="text, record">
          <span v-if="text === 'Infinity'">
            ∞
          </span>
          <span v-else>
            <span v-if="text>0" style="color: red">
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
            <span v-else>
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
          </span>
        </template>
        <template slot="bj2" slot-scope="text, record">
         <span v-if="text === 'Infinity'">
            ∞
          </span>
          <span v-else>
            <span v-if="text>0" style="color: red">
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
            <span v-else>
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
          </span>
        </template>
        <template slot="bj3" slot-scope="text, record">
          <span v-if="text === 'Infinity'">
            ∞
          </span>
          <span v-else>
            <span v-if="text>0" style="color: red">
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
            <span v-else>
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
          </span>
        </template>
        <template slot="bj4" slot-scope="text, record">
          <span v-if="text === 'Infinity'">
            ∞
          </span>
          <span v-else>
            <span v-if="text>0" style="color: red">
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
            <span v-else>
              {{ parseFloat(text * 100).toFixed(2) }}%
            </span>
          </span>
        </template>
      </a-table>
    </div>
</template>

<script>
const columns = [
  {
    title: '单位',
    dataIndex: 'deptName'
  },
  {
    title: '事故死亡人数',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upNumber'
      },
      {
        title: '本期死亡人数',
        dataIndex: 'number'
      },
      {
        title: '同期比较',
        dataIndex: 'bj1',
        scopedSlots: {customRender: 'bj1'}
      }
    ]
  },
  {
    title: '电脑分析责任死亡',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDnxs'
      },
      {
        title: '本期死亡人数',
        dataIndex: 'dnxs'
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj2',
        scopedSlots: {customRender: 'bj2'}
      }
    ]
  },
  {
    title: '电脑调节分析死亡人数',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDnTjxs'
      },
      {
        title: '本期死亡人数',
        dataIndex: 'dntjxs'
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj3',
        scopedSlots: {customRender: 'bj3'}
      }
    ]
  },
  {
    title: '属地管理责任死亡人数',
    children: [
      {
        title: '上期死亡人数',
        dataIndex: 'upDeathToll'
      },
      {
        title: '本期死亡人数',
        dataIndex: 'deathToll'
      },
      {
        title: '同期比较(%)',
        dataIndex: 'bj4',
        scopedSlots: {customRender: 'bj4'}
      }
    ]
  }
]
export default {
  name: 'Period',
  data () {
    return {
      restTime: null,
      advanced: false,
      columns,
      restStartTime: null,
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
      let month = date.getMonth() + 1
      let lastMonth = date.getMonth()
      this.startYear = lastYear + '-' + lastMonth
      this.endYear = year + '-' + month
      this.restStartTime = this.startYear
      this.restTime = this.endYear
      const param = {
        startYear: this.startYear,
        endYear: this.endYear
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
      this.restTime = null // 结束时间
      this.restStartTime = null // 开始时间
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

</style>
