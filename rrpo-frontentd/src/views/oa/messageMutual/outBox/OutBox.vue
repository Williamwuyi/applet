<template>
    <div style="width: 100%;min-height: 780px">
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form>
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="4" :sm="20">
                <a-form-item
                  label="标题:"
                  :labelCol="{span: 4}"
                  :wrapperCol="{span: 17, offset: -2}">
                  <a-input v-model="character.title"/>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="20" >
                <a-form-item
                  label="创建时间:"
                  :labelCol="{span: 5}"
                  :wrapperCol="{span: 18, offset: 0}">
                  <range-date @change="onTimeChange" ref="creatTime" :allowClear="false"></range-date>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="16" >
                <a-form-item
                  label="发送状态:"
                  :labelCol="{span: 8}"
                  style="margin-left: 10px"
                  :wrapperCol="{span: 15, offset: 0}">
                  <a-cascader  change-on-select
                               @change="onChangecascader"
                               :options="options"
                               placeholder="未选择"
                               ref="cascader"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24" style="margin-left: 10px;margin-top: 3px">
                <a-button type="primary" @click="search">查询</a-button>
                <a-button style="margin-left: 8px" @click="reset">重置</a-button>
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
      <a-button @click="batchRelease" style="margin-bottom: 5px;background-color: #87D068;color: white">
      发布
      </a-button>
    <a-button @click="batchRemove" style="background-color: #FF4040; color: white;margin-bottom: 5px;margin-left: 10px">
      删除
    </a-button>
    <a-table
      :data-source="dataSource"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      :columns="columns"
      :loading="loading"
      :scroll="{ y: 580 }"
      @change="handleTableChange"
      :rowKey="(record)=> record.id"
      :pagination="pagination">
      <a slot="titlename" slot-scope="text,record" style="color:#6290FF" title="查看/发送状态" @click="lookStatus(record)">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === '3'" color="#87d068">已发送</a-tag>
        <a-tag v-else-if="record.status === '1'" color="#DEE1E6" >未发送</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
<!--        <a-icon  type="setting" theme="twoTone" twoToneColor="#4a9ff5" style="margin-left:8px" @click="outBoxedit(record)" title="修改"></a-icon>-->
        <a @click="outBoxedit(record)" style="color: #4a9ff5">修改</a>
      </template>
    </a-table>
    <!-- 修改 -->
    <out-box-edit
      @success="handleEdit"
      @close="hanleeditclose"
      :boxEditVisiable="boxEditVisiable"
      ref="oldedit"
    />
      <!-- 查看状态 -->
      <look-status
        :lookStatusVisiable="lookStatusVisiable"
        @close="lookStatusClose"
        ref="nolookstatus"
      />
  </div>
</template>
<script>
import OutBoxEdit from './OutBoxEdit'
import LookStatus from './LookStatus'
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'Outbox',
  components: {OutBoxEdit, RangeDate, LookStatus},
  data () {
    return {
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      lookStatusVisiable: false,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      //  状态下拉
      options: [
        {
          value: '1',
          label: '未发送'
        },
        {
          value: '3',
          label: '已发送'
        }
      ],
      character: {},
      sortedInfo: null,
      count: 2,
      comAddVisiable: false,
      boxEditVisiable: false
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [
        {
          title: '标题',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titlename' }
        },
        // {
        //   title: '发布人',
        //   width: '10%',
        //   dataIndex: 'creatUser'
        // },
        {
          title: '发送状态',
          dataIndex: 'status',
          width: '10%',
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '创建时间',
          dataIndex: 'creatTime',
          sorter: true,
          sortOrder: sortedInfo.columnKey === 'creatTime' && sortedInfo.order
        },
        {
          title: '发送时间',
          sorter: true,
          sortOrder: sortedInfo.columnKey === 'releaseTime' && sortedInfo.order,
          dataIndex: 'releaseTime'

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
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('exchange', params).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    // 选中时间
    onTimeChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
    },
    // 状态查询
    onChangecascader (value) {
      this.character.status = value
    },
    // 条件查询
    search () {
      // 通过this.character来进行查询
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.character
      })
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空表格选择框
      this.selectedRowKeys = []
      // 清空状态选择
      this.$refs.cascader.sValue = []
      // 清空时间选择
      this.$refs.creatTime.reset()
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    },
    // 批量发布
    batchRelease () {
      this.soundClick()
    },
    // 批量删除
    batchRemove () {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (!this.selectedRowKeys.length) {
        this.$notification.warning({message: '系统提示', description: '请选择需要删除的记录！', duration: 4})
        return
      }
      let that = this
      this.$confirm({
        content: '确认删除数据',
        centered: true,
        onOk () {
          let Remove = that.selectedRowKeys
          that.$delete('/exchange/' + Remove.join(',')).then(() => {
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            that.selectedRowKeys = []
            that.fetch()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    // 添加功能
    handleAdd () {
      this.comAddVisiable = true
    },
    handlecomAdd () {
      this.comAddVisiable = false
      this.fetch()
    },
    hanlecomclose () {
      this.comAddVisiable = false
    },
    // 修改功能
    outBoxedit (record) {
      if (record.status === '1') {
        this.boxEditVisiable = true
        // 掉用子组件方法加传值
        this.$refs.oldedit.setFormValues(record)
      } else {
        this.$notification.warning({message: '系统提示', description: '发送状态不能修改！', duration: 4})
      }
    },
    handleEdit () {
      this.boxEditVisiable = false
      this.fetch()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 批量发布
    soundClick () {
      if (!this.selectedRowKeys.length) {
        this.$notification.warning({message: '系统提示', description: '请选择需要发送的数据！', duration: 4})
        return
      }
      let that = this
      let batch = that.selectedRowKeys
      this.$confirm({
        content: '是否确认发布',
        centered: true,
        onOk () {
          that.$get('/exchange/release/' + batch.join(',')).then(res => {
            if (res.status === 200) {
              that.$notification.success({message: '系统提示', description: '发布成功！', duration: 4})
              that.fetch()
            }
          }).catch(err => {
            that.fetch()
            that.$message.error(err)
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    hanleeditclose () {
      this.boxEditVisiable = false
    },
    lookStatusClose () {
      this.lookStatusVisiable = false
    },
    // 查看状态
    lookStatus (info) {
      this.lookStatusVisiable = true
      // 传给子组件才数据 注意nolookstatus对应
      this.$refs.nolookstatus.setFormValues(info)
    }
  }
}
</script>
<style lang="less" scoped>
  .ant-table-placeholder{
    margin: 200px;
  }
  .ant-empty .ant-empty-normal{
    margin: 200px;
  }
</style>
