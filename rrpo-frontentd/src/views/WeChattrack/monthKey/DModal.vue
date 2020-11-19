<template>
  <a-modal
    :visible="DModalVisiable"
    title='预览'
    :width= "850"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
  >
    <div>
      <a-row>
        <a-col :md="12" :sm="24" >
          <a-form-item
            label="月份查询"
            :labelCol="{span: 5}"
            :wrapperCol="{span: 18, offset: 1}">
            <a-date-picker
              format = 'YYYY-MM' v-model="timeValue" @change="onTimeChange" />
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24" >
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                <a-button style="margin-left: 8px; background-color: #2f54eb; color: #fff" @click="players">导出</a-button>
        </span>
        </a-col>
      </a-row>
      <hr />
      <!--  表格区域  -->
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :scroll="{ y: 450 }"
        :pagination="pagination"
        :rowKey="record => {record.wxMonthId}"
        @change="handleTableChange"
      >
        <template slot="content" slot-scope="text, record">
          <div v-html="text"></div>
        </template>
        <template slot="preStatus" slot-scope="text, record">
          <div v-if="record.preStatus === 1" color="#DEE1E6" >可入</div>
          <div v-if="record.preStatus === 2 || record.preStatus === null" color="#f50">不可入</div>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>
<script>
export default {
  name: 'DModal',
  props: ['DModalVisiable'],
  data () {
    return {
      timeValue: undefined,
      arr: [],
      loading: false,
      dataSource: [],
      character: {},
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      month: ''
    }
  },
  computed: {
    columns () {
      return [{
        title: '序号',
        dataIndex: 'serial'
      }, {
        title: '推荐单位',
        dataIndex: 'szDeptName'
      }, {
        title: '乡镇街',
        dataIndex: 'deptJc.deptName'
      }, {
        title: '微信记录简要内容(详情内容登陆’hnzhb.com.cn-信息公开——月度最佳微信记录‘)',
        width: '80%',
        dataIndex: 'content',
        scopedSlots: { customRender: 'content' }
      }, {
        title: '初步意见',
        dataIndex: 'preStatus',
        scopedSlots: { customRender: 'preStatus' }
      }]
    }
  },
  methods: {
    onClose () {
      this.$emit('onClose')
      this.reset()
    },
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10, month: this.month}) {
      this.$get('/wx/month/monthDocxText', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    search () {
      if (this.timeValue !== undefined) {
        this.fach()
      } else {
        this.$message.error('请先选择月份')
      }
    },
    reset () {
      this.timeValue = undefined
      this.dataSource = []
      // 重置列排序规则
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.pagination.total = 0
    },
    // 分页
    handleTableChange (pagination) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fach({
        ...this.character,
        month: this.month
      })
    },
    // 打印
    players () {
      if (this.timeValue !== undefined) {
        this.$DocExport('/wx/month/downDocx', {month: this.month}).then(res => {
        })
      } else {
        this.$message.error('请先选择月份')
      }
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.month = dateTime
    }
  }
}
</script>

<style scoped>
</style>
