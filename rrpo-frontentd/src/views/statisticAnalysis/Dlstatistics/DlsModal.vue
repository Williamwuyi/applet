<template>
  <a-modal
    :visible="DlsModalVisiable"
    title='登陆统计'
    :width= "950"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
  >
    <a-row>
      <a-col :md="5" :sm="24" >
        <a-form-item
          label="选择年月"
          :labelCol="{span: 8}"
          :wrapperCol="{span: 20, offset: 0}">
          <a-month-picker @change="handleYearChange" v-model="Timer">
          </a-month-picker>
        </a-form-item>
      </a-col>
      <a-col :md="7" :sm="24" >
        <a-form-item
          label="选择周"
          :labelCol="{span: 5}"
          :wrapperCol="{span: 20, offset: 0}">
        <a-select  style="width: 230px" v-model="msgweek" placeholder="未选择" :allowClear="true" @change="handleWeekChange">
          <a-select-option  v-for="(item, index) in arr" :value="index" :key="index">
            第{{index + 1}}周（{{item.startDate}} - {{item.endDate}}）
          </a-select-option>
        </a-select>
        </a-form-item>
      </a-col>
      <a-col :md="7" :sm="24" >
        <a-form-item
          label="登陆时间"
          :labelCol="{span: 6}"
          :wrapperCol="{span: 20, offset: 0}">
          <range-date @change="handleDateChange" ref="createTime"></range-date>
        </a-form-item>
      </a-col>
      <a-col :md="5" :sm="24" style="margin-top: 40px;">
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
<!--                <a-button style="margin-left: 8px; background-color: #2f54eb; color: #fff" @click="players">导出</a-button>-->
        </span>
      </a-col>
    </a-row>
    <hr />
    <!--  表格区域  -->
    <a-table
      :data-source="dataSource"
      :columns="columns"
      :loading="loading"
      :scroll="{ y: 550 }"
      :rowKey='(record, index) => index'
      :pagination="false"
    >
      <span slot="xuhao" slot-scope="text,record, index">{{index + 1}}</span>
    </a-table>
  </a-modal>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'DlsModal',
  props: ['DlsModalVisiable'],
  components: {RangeDate},
  data () {
    return {
      deptId: '',
      Timer: undefined,
      msgweek: undefined,
      loading: false,
      dataSource: [],
      character: {},
      // 周
      arr: [],
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  computed: {
    columns () {
      return [{
        title: '序号',
        scopedSlots: { customRender: 'xuhao' },
        align: 'center'
      }, {
        title: '登陆人员',
        dataIndex: 'username'
      }, {
        title: '登陆时间',
        dataIndex: 'loginTime'
      }]
    }
  },
  methods: {
    onClose () {
      this.$emit('onClose')
      this.reset()
    },
    getById (id) {
      this.deptId = id
      this.character.deptId = id
      this.fach({deptId: id})
    },
    // 渲染
    fach (parmaes) {
      this.loading = true
      this.$get('/accident/login/userList', parmaes).then(res => {
        this.dataSource = res.data.data
        this.loading = false
      })
    },
    search () {
      this.character.deptId = this.deptId
      this.fach(this.character)
    },
    reset () {
      this.character = {}
      this.dataSource = []
      // 清空时间选择
      this.$refs.createTime.reset()
      // 清空年月
      this.Timer = undefined
      // 清空周
      this.arr = []
      this.msgweek = undefined
      // 重置列排序规则
      // this.pagination.current = 1
      // this.pagination.pageSize = 10
      // this.pagination.total = 0
      this.fach({deptId: this.deptId})
    },
    // 分页
    // handleTableChange (pagination) {
    //   // 通知界面
    //   this.pagination.current = pagination.current
    //   this.pagination.pageSize = pagination.pageSize
    //   // 通知后台
    //   this.character.pageNum = pagination.current
    //   this.character.pageSize = pagination.pageSize
    //   this.fach({
    //     ...this.character,
    //     month: this.month
    //   })
    // },
    // 打印
    players () {
    },
    // 选择年月
    handleYearChange (date, dateString) {
      // 清空时间选择
      this.$refs.createTime.reset()
      if (date !== null) {
        let newData = {
          year: dateString.split('-')[0],
          month: dateString.split('-')[1]
        }
        this.$get('/accident/login/week', newData).then(res => {
          this.arr = res.data.data
        })
      } else {
        this.arr = []
        this.msgweek = undefined
      }
    },
    // 选择周
    handleWeekChange (value) {
      // 清空时间选择
      this.$refs.createTime.reset()
      if (value !== undefined) {
        let start = this.arr[value].startDate + ` 00:00:00`
        let end = this.arr[value].endDate + ` 23:59:59`
        this.character.startDate = start
        this.character.endDate = end
        this.character.deptId = this.deptId
      } else {
        this.character = {}
        this.character.deptId = this.deptId
      }
    },
    // 选择时间
    handleDateChange (value) {
      // 清空周
      this.arr = []
      this.msgweek = undefined
      // 清空年月
      this.Timer = undefined
      if (value[0] !== '' && value[1] !== '') {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startDate = start
        this.character.endDate = end
        this.character.deptId = this.deptId
      } else {
        this.character = {}
        this.character.deptId = this.deptId
      }
    }
  }
}
</script>

<style scoped>
</style>
