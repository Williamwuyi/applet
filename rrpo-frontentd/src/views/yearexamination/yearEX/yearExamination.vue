<template>
  <div style="width: 100%">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="3" :sm="24" v-hasPermission="'year:assess'">
              <a-button class="editable-add-btn"  @click="yearCheckClick">
                开始考核
              </a-button>
            </a-col>
            <a-col :md="4" :sm="24" >
              <a-form-item
                label="所属单位"
                :labelCol="{span: 7}"
                :wrapperCol="{span: 15, offset: 1}">
                <span>{{dape}}</span>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24" v-hasPermission="'year:demand'">
              <a-form-item
                label="考核单位"
                :labelCol="{span: 7}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-cascader  change-on-select
                             style="width: 145px;"
                             @change="onChangedanwei"
                             :options="optionsr"
                             :fieldNames="{ label: 'deptName', value: 'deptId', children: 'children2' }"
                             placeholder="未选择"
                             ref="danwei"
                />
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="24" v-hasPermission="'year:demand'">
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <!-- 表格区域 -->
    <div>
      <!-- 表格-->
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :rowKey="(record)=> record.numId"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template slot="status" slot-scope="text, record">
          <a-tag v-if="record.status === 3" color="#87d068">审核完</a-tag>
          <a-tag v-else-if="record.status === 2" color="#2db7f5" >重新提交</a-tag>
          <a-tag v-else-if="record.status === 0" color="#DEE1E6" >未审核</a-tag>
          <a-tag v-else-if="record.status === 1" color="#f50">未通过</a-tag>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a href="#"  v-hasPermission="'year:details'" @click="particular(record)">详细</a>
          <a href="#" style="color: red; margin-left: 4px"  v-hasPermission="'year:mark'" @click="Setmark(record)">评分</a>
        </template>
      </a-table>
    </div>
<!--  详细  -->
    <year-examinationlook
    :yearExamVisible="yearExamVisible"
    @success="LookSuccess"
    @close="close"
    ref="examina"
    />
    <!--  打分  -->
    <year-examination-mark
      :yearMarkVisible="yearMarkVisible"
      @success="MarkSuccess"
      @close="markClose"
      ref="Mark"
    />
<!--  日期选择  -->
    <year-choose
    @cancel="chooseClose"
    @check="chooseCreat"
    :visible="VisibleClose"
    />
<!--    考核-->
    <year-examination-check
      :deptId="dapeId"
      :CheckVisible="CheckVisible"
      @success="yearexSuccess"
      @close="closeCheck"
      ref="concekd"
    />
  </div>
</template>

<script>
import yearExaminationlook from './yearExaminationlook'
import yearExaminationCheck from './yearExaminationCheck'
import yearExaminationMark from './yearExaminationMark'
import yearChoose from './yearChoose'
import store from '@/store'
export default {
  name: 'yearExamination',
  components: {yearExaminationlook, yearExaminationCheck, yearExaminationMark, yearChoose},
  data () {
    return {
      dataSource: [],
      key: +new Date(),
      dape: store.state.account.user.deptName,
      // 考核单位
      dapeId: store.state.account.user.deptId,
      // 查询参数
      yearex: {},
      loading: false,
      advanced: false,
      // 日期
      VisibleClose: false,
      // 考核时间
      examTime: '',
      // 详细的显示隐藏
      yearExamVisible: false,
      // 考核的显示隐藏
      CheckVisible: false,
      // 打分
      yearMarkVisible: false,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      //  状态下拉
      options: [],
      optionsr: []
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '组织机构',
        dataIndex: 'dept.deptName',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '时间',
        dataIndex: 'yearDate',
        // sorter: true,
        sortOrder: sortedInfo.columnKey === 'dateYear' && sortedInfo.order
      }, {
        title: '基础得分',
        dataIndex: 'jcWork'
      }, {
        title: '工作得分',
        dataIndex: 'xgWork'
      }, {
        title: '初评总分',
        dataIndex: 'number'
      }, {
        title: '终评基础得分',
        dataIndex: 'fpJcWork'
      }, {
        title: '终评工作得分',
        dataIndex: 'fpXgWork'
      }, {
        title: '终评总分',
        dataIndex: 'fpNumber'
      }, {
        title: '限定重提时间',
        dataIndex: 'deadDate'
      }, {
        title: '状态',
        dataIndex: 'status',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
    this.$get('/dept/list', {deptId: 0}).then((r) => {
      this.optionsr = r.data.filter(t => {
        return t.rank !== 4
      })
    })
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.$get('/check/num/num', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        // 判断每条消息得时间是否超过，超过发送改变状态请求
        // this.dataSource.forEach(t => {
        //   let TimeStatus = new Date().getTime() > new Date(t.deadDate).getTime()
        //   if (TimeStatus && t.status !== 3) {
        //     console.log(TimeStatus)
        //     this.getStatus(t.numId, 3)
        //   }
        // })
        this.pagination = pagination
        this.loading = false
      })
    },
    // 改变状态
    // getStatus (numId, status) {
    //   this.$get('/check/num/changeStatus', {numId: numId, status: status})
    // },
    search () {
      this.fach(this.yearex)
    },
    reset () {
      // 重置查询参数
      this.yearex = {}
      // 清空机构选择
      this.$refs.danwei.sValue = []
      // 重置考核按钮
      // this.newTdanwei = true
      this.options = []
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    onChangedanwei (value) {
      if (value.length > 0) {
        this.yearex.deptId = value[0]
      } else {
        this.yearex.deptId = ''
      }
    },
    //  详细
    particular (record) {
      this.yearExamVisible = true
      this.$refs.examina.fach(record)
    },
    LookSuccess () {
      this.yearExamVisible = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    close () {
      this.yearExamVisible = false
      this.fach()
    },
    //  考核
    yearCheckClick () {
      this.VisibleClose = true
    },
    yearexSuccess () {
      this.CheckVisible = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    closeCheck () {
      this.fach()
      this.CheckVisible = false
    },
    // 日期选择
    chooseCreat (key) {
      console.log(key)
      this.CheckVisible = true
      this.$refs.concekd.fach(key)
    },
    chooseClose () {
      this.VisibleClose = false
    },
    // 分页
    handleTableChange (pagination, filters) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.yearex.pageNum = pagination.current
      this.yearex.pageSize = pagination.pageSize
      this.fach({
        ...this.yearex,
        deptId: this.dapeId,
        ...filters
      })
    },
    // 打分
    Setmark (record) {
      this.yearMarkVisible = true
      this.$refs.Mark.fach(record)
    },
    MarkSuccess () {
      this.yearMarkVisible = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    markClose () {
      this.yearMarkVisible = false
      this.fach()
    }
  }
}
</script>

<style scoped>
.yearEx {
  width: 100%;
  height: 100%;
  background-color: #fff;
  padding: 16px 32px;
  border-left: 1px solid #e8e8e8;
  border-right: 1px solid #e8e8e8;
  border-bottom: 1px solid #e8e8e8;
}
  h2 {
    text-align: center;
  }
  .editable-add-btn {
    margin-bottom: 7px;
  }
</style>
