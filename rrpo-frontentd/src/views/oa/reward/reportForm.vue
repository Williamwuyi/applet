<template>
  <a-modal
    :visible="reportform"
    title="报表"
    :width= "794"
    :footer="null"
    @cancel="() => { onClose() }"
  >
    <div>
      <range-date @change="handleDateChange" ref="releaseTime" style="width:300px"></range-date>
      <div class="floatR" style="width:400px">
        <a-button @click="fach">查询</a-button>
        <a-button @click="Ymoney">不带金额报表</a-button>
        <a-button @click="reset">重置</a-button>
      </div>
    </div>
    <div class="rewardForm">
        <h2>湖南铁路护路联防</h2>
        <h1>简报</h1>
        <p class="repNumber">
          第 <input type="number" class="periods periodsNumber" v-model="period"> 期
        </p>
        <div class="repcode clearboth">
          <p class="floatL">湖南省铁路护路联防工作办公室编</p>
          <p class="floatR">
            <input type="number" class="periods2 periodsNumber" v-model="year" oninput="if(value.length>4)value=value.slice(0,4)">
            年<input type="number" class="periods3 periodsNumber" v-model="month" oninput="if(value.length>2)value=value.slice(0,2)">
            月<input type="number" class="periods3 periodsNumber" v-model="date"  oninput="if(value.length>2)value=value.slice(0,2)">
            日
          </p>
        </div>
    </div>
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
    ></a-table>
  </a-modal>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'reportform',
  components: {
    RangeDate
  },
  data () {
    return {
      loading: false,
      columns: [
        {
          title: '编号',
          dataIndex: 'number',
          width: '10%',
          align: 'center'
        },
        {
          title: '单位',
          dataIndex: 'place',
          width: '20%',
          scopedSlots: { customRender: 'places' },
          align: 'center'
        },
        {
          title: '事迹内容',
          dataIndex: 'content',
          scopedSlots: { customRender: 'content' }
        },
        {
          title: '金额',
          dataIndex: 'briefingMoney',
          width: '10%'
        }
      ],
      dataSource: [],
      character: {},
      //  分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      moneyData: [],
      year: '',
      month: '',
      date: '',
      period: ''
    }
  },
  props: {
    reportform: {
      default: false
    }
  },
  methods: {
    // 时间查询
    handleDateChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
    },
    // 渲染
    fach () {
      this.loading = true
      this.$get('/prize/briefing', this.character).then(res => {
        let newData = res.data.data.rows
        let pagination = {}
        pagination.total = res.data.data.total
        this.dataSource = newData.rows
        this.pagination = pagination
        // 分页;
        this.dataSource = newData
        this.loading = false
      })
    },
    // 重置
    reset () {
      this.loading = false
      this.character = {}
      this.$refs.releaseTime.reset()
      this.dataSource = []
      this.pagination = {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
      this.year = ''
      this.month = ''
      this.date = ''
      this.period = ''
    },
    // 关闭
    onClose () {
      this.reset()
      this.$emit('close')
    },
    Ymoney () {
      let years = this.year
      let months = this.month
      let days = this.date
      let periods = this.period
      this.moneyData = {
        year: years,
        month: months,
        day: days,
        period: periods,
        startTime: this.character.startTime,
        endTime: this.character.endTime
      }
      this.$post('/prize/briefingWord', this.moneyData).then(res => {
        this.loading = true
        this.onClose()
      })
    }
  }
}
</script>
<style scoped>
  .floatL{float: left}
  .floatR{float: right}
  .clearboth .floatL, clearboth .floatR{display: block;content: '';clear: both}
  .rewardForm{width:688px;height: 320px;margin: 0 auto;border-bottom:2px solid red;position: relative;}
  .rewardForm h2,.rewardForm h1{color: red;text-align: center;margin: 0}
  .rewardForm h2{font-size: 50px;padding-top: 10px}
  .rewardForm h1{font-size: 70px}
  .rewardForm .repNumber{text-align: center;margin-top: 30px}
  .rewardForm .repcode{width:688px;position: absolute;bottom: 0;left: 0}
  .rewardForm .periods{width: 60px;border: transparent;border-bottom: 1px dotted #000c17;text-align: center}
  .rewardForm .periods2{width: 40px;border: transparent;border-bottom: 1px dotted #000c17}
  .rewardForm .periods3{width: 30px;border: transparent;border-bottom: 1px dotted #000c17}
  .periodsNumber::-webkit-outer-spin-button,
  .periodsNumber::-webkit-inner-spin-button{
    -webkit-appearance: none !important;
    margin: 0;
  }
</style>
