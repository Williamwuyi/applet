<template>
  <div style="width: 100%; min-height: 690px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="月份查询"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-month-picker @change="handleYearChange" v-model="Timer">
                </a-month-picker>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label='状态查询' :labelCol="{span: 5}" :wrapperCol="{span: 15, offset: 1}">
                <a-cascader  change-on-select
                             style="width: 105px;"
                             @change="onChangecascader"
                             :options="options"
                             placeholder="未选择"
                             ref="cascader"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
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
      <a-button type="primary" @click="handleAdd" v-hasPermission="'monthKey:add'" >
        添加
      </a-button>
      <a-button class="editable-add-btn" @click="handleHz" v-hasPermission="'monthKey:proposal'" >
        生成汇总表
      </a-button>
      <a-button class="editable-add-btn" @click="handleHzBM" v-hasPermission="'monthKey:proposal'">
        生成简报
      </a-button>
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ y: 450 }"
        :rowKey="record => {record.wxMonthId}"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <template slot="status" slot-scope="text, record">
          <a-tag v-if="record.status === 1" color="#87d068">{{record.deptSh.deptName}}已通过</a-tag>
          <a-tag v-if="record.status === null" color="#DEE1E6" >{{record.deptSh.deptName}}未上报</a-tag>
          <a-tag v-if="record.status === 2" color="#2db7f5" >上报{{record.deptSh.deptName}}中</a-tag>
          <a-tag v-if="record.status === 0" color="#f50">{{record.deptSh.deptName}}未通过</a-tag>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a   style="margin-right: 3px;color:#4a9ff5;"   @click="edit(record)" v-hasPermission="'monthKey:edit'" title="修改">修改</a>
          <a    style="color:#0FB71F;margin:3px" v-hasPermission="'monthKey:top'" @click="soundClick(record)" title="上报" >上报</a>
          <a    style="color:#00CD00;margin:3px"  @click="look(record)" title="审核"  v-hasPermission="'monthKey:audit'" >审核</a>
        </template>
      </a-table>
    </div>
    <!-- 修改 -->
    <notice-edit
      @success="handleEdit"
      @close="hanleeditclose"
      :DailyEditVisiable="DailyEditVisiable"
      ref="oldedit"
    />
    <!-- 添加 -->
    <noticeadd
      @success="handlecomAdd"
      @close="hanlecomclose"
      :randomId="randomId"
      :addVisiable="NoticeaddVisiable"
    />
    <!-- 查看 -->
    <dmlook
      :NoticelookVisiable="NoticelookVisiable"
      @close="hanleNoticelookclose"
      ref="nolook"
    />
<!--  汇总表  -->
    <d-modal
    :DModalVisiable="DModalVisiable"
    @onClose="DMonClose"
    />
    <!--  生成简报  -->
    <b-modal
      :BModalVisiable="BModalVisiable"
      @close="DMonCloseBM"
    />
  </div>
</template>
<script>
import Noticeadd from './DailyManagementAdd'
import NoticeEdit from './DailyManagementEdit'
import Dmlook from './Dmlook'
import DModal from './DModal'
import uuidv1 from 'uuid/v1'
import BModal from '../monthBulletin/BModal'
import {mapMutations} from 'vuex'
export default {
  name: 'Notice',
  components: {Dmlook, NoticeEdit, Noticeadd, DModal, BModal},
  data () {
    return {
      character: {},
      data: [],
      loading: false,
      advanced: false,
      selectedRowKeys: [],
      timeValue: undefined,
      randomId: '-1',
      NoticeaddVisiable: false,
      DailyEditVisiable: false,
      NoticelookVisiable: false,
      DModalVisiable: false,
      BModalVisiable: false,
      // 时间
      Timer: undefined,
      refId: '',
      sortedInfo: null,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      // 表格
      dataSource: [],
      count: 2,
      //  状态下拉
      options: [
        {
          value: '1',
          label: '上报中'
        },
        {
          value: '2',
          label: '未审核'
        },
        {
          value: '3',
          label: '已通过'
        },
        {
          value: '4',
          label: '未上报'
        }
      ]
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
  },
  activated () {
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '组织机构',
        dataIndex: 'deptJc.deptName'
      }, {
        title: '群名称',
        dataIndex: 'qun.wxName'
      }, {
        title: '创建时间',
        dataIndex: 'date',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'date' && sortedInfo.order
      }, {
        title: '上报月份',
        dataIndex: 'month',
        scopedSlots: { customRender: 'month' }
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
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/wx/month/list', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 选择时间
    handleYearChange (date, dateTime) {
      console.log(dateTime)
      this.character.month = dateTime
    },
    // 查询
    search () {
      // 通过this.character来进行查询
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fach({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.character
      })
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空年月
      this.Timer = undefined
      // 清空表格选择框
      this.selectedRowKeys = []
      // 重置列排序规则

      this.sortedInfo = null
      this.$refs.cascader.sValue = []
      this.refId = ''
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    // 状态查询
    onChangecascader (value) {
      this.character.status = value[0]
    },
    // 分页
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fach({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    },
    // 删除
    onDelete () {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        that.$confirm({
          title: '是否删除，一经删除永远不会恢复?',
          centered: true,
          onOk () {
            that.$delete('/inform/' + that.selectedRowKeys.join(',')).then(() => {
              that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
              that.selectedRowKeys = []
              that.fach(that.character)
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 添加功能
    handleAdd () {
      this.randomId = uuidv1() // 获取随机ID
      this.NoticeaddVisiable = true
      this.initAppendixList()
    },
    ...mapMutations({
      initAppendixList: 'file/initAppendixList'
    }),
    handlecomAdd () {
      this.NoticeaddVisiable = false
      this.fach(this.character)
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.$store.commit('file/initAppendixList')
    },
    hanlecomclose () {
      this.NoticeaddVisiable = false
      this.fach(this.character)
    },
    // 修改功能
    edit (key) {
      this.DailyEditVisiable = true
      this.$refs.oldedit.setFormValues(key)
    },
    handleEdit () {
      this.DailyEditVisiable = false
      this.fach(this.character)
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fach(this.character)
    },
    hanleeditclose () {
      this.DailyEditVisiable = false
      this.fach(this.character)
    },
    // 上报
    soundClick (record) {
      if (record.status !== 3) {
        let that = this
        that.$confirm({
          title: '确定上报?',
          centered: true,
          onOk () {
            that.$get('/wx/month/appear', {monthId: record.wxMonthId, status: 1}).then(res => {
              if (res.data.status !== 0) {
                that.fach(that.character)
                that.$message.success(res.data.message)
              } else {
                that.$message.error(res.data.message)
              }
            }).catch(() => {
              that.fach(that.character)
              that.$message.error('上报失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于发布状态了')
      }
    },
    // 查看
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
      this.fach(this.character)
    },
    look (look) {
      this.NoticelookVisiable = true
      this.$refs.nolook.setFormValues(look)
    },
    //  汇总表
    handleHz () {
      this.DModalVisiable = true
    },
    DMonClose () {
      this.DModalVisiable = false
      this.fach(this.character)
    },
    // 简报
    //  汇总表
    handleHzBM () {
      this.BModalVisiable = true
    },
    DMonCloseBM () {
      this.BModalVisiable = false
    }
  }
}
</script>
<style lang="less" scoped>
  .editable-cell {
    position: relative;
  }

  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }

  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }

  .editable-cell-icon,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 20px;
    cursor: pointer;
  }

  .editable-cell-icon {
    line-height: 18px;
    display: none;
  }

  .editable-cell-icon-check {
    line-height: 28px;
  }

  .editable-cell:hover .editable-cell-icon {
    display: inline-block;
  }

  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }

  .editable-add-btn {
    margin-bottom: 8px;
  }
</style>
