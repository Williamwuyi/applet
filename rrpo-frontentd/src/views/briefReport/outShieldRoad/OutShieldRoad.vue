<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form layout="horizontal">
          <div :class="advanced ? null: 'fold'">
            <a-row >
              <a-col :md="5" :sm="20">
                <a-form-item
                  label="标题:"
                  :labelCol="{span: 5}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-input v-model="character.title"/>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24" >
                <a-form-item
                  label="创建时间:"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <range-date @change="onTimeChange" ref="creatTime" :allowClear="false"></range-date>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20" >
                <a-form-item style="margin-left: 30px"
                  label="发送状态:"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-cascader  change-on-select
                               @change="onChangecascader"
                               :options="options"
                               placeholder="未选择"
                               ref="cascader"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20">
                <span style="margin-left: 20px">
                  <a-button type="primary" @click="search">查询</a-button>
                  <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                </span>
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
      <a-button class="editable-add-btn" @click="addition">
      添加
      </a-button>
      <a-button class="editable-add-btn" @click="batchRelease">
      批量发布
      </a-button>
    <a-button class="editable-add-btn" @click="batchRemove">
      批量删除
    </a-button>
    <a-table
      :data-source="dataSource"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      :columns="columns"
      @change="handleTableChange"
      :rowKey="(record)=> record.id"
      :pagination="pagination">
      <a slot="titlename" slot-scope="text,record" style="color:#6290FF" title="查看/发送状态" @click="lookStatus(record)">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3 " color="#87d068">已发送</a-tag>
        <a-tag v-else-if="record.status === 1" color="#DEE1E6" >未发送</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
        <a-icon  type="setting" theme="twoTone" twoToneColor="#4a9ff5" style="margin-left:8px" @click="outBoxedit(record)" title="修改"></a-icon>
      </template>
    </a-table>
    <!-- 修改 -->
    <out-shield-road-edit
      @success="handleEdit"
      @close="hanleeditclose"
      :editVisiable="editVisiable"
      ref="oldedit"
    />
      <out-shield-road-add
        @success="handleAdd"
        :shieldId="shieldId"
        @close="hanleaddclose"
        :addVisiable="addVisiable"
        ref="oldadd"
      />
      <!-- 查看状态 -->
      <look-status
        :lookStatusVisiable="lookStatusVisiable"
        @close="lookStatusClose"
        ref="nolookstatus"
      />
  </div>
  </a-card>
</template>
<script>
import OutShieldRoadEdit from './OutShieldRoadEdit'
import OutShieldRoadAdd from './OutShieldRoadAdd'
import uuidv1 from 'uuid/v1'
import LookStatus from './LookStatus'
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'OutShieldRoad',
  components: {OutShieldRoadEdit, RangeDate, OutShieldRoadAdd, LookStatus},
  data () {
    return {
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      lookStatusVisiable: false,
      shieldId: '-1',
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
      addVisiable: false,
      editVisiable: false
    }
  },
  inject: ['reload'],
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
      this.$get('/briefing', params).then(res => {
        let newData = res.data.data
        console.log('查询列表信息：', newData)
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
      if (!this.selectedRowKeys.length) {
        this.$notification.warning({message: '系统提示', description: '请选择需要发送的数据！', duration: 4})
        return
      }
      let that = this
      let batch = that.selectedRowKeys
      that.$get('/briefing/release/' + batch.join(',')).then(res => {
        that.reload()
        this.$notification.success({message: '系统提示', description: '发布成功！', duration: 4})
      }).catch(err => {
        that.reload()
        that.$message.error(err)
      })
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
          that.$delete('/briefing/' + Remove.join(',')).then(() => {
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            that.selectedRowKeys = []
            that.reload()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    // 添加功能完成后
    handleAdd () {
      this.addVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.reload()
    },
    // 触发添加按钮
    addition () {
      this.shieldId = uuidv1()
      this.addVisiable = true
    },
    // 添加时 取消或者关闭
    hanleaddclose () {
      this.addVisiable = false
    },
    // 修改触发按钮
    outBoxedit (record) {
      if (record.status === 1) {
        this.editVisiable = true
        // 掉用子组件方法加传值
        this.$refs.oldedit.setFormValues(record)
      } else {
        this.$notification.warning({message: '系统提示', description: '发送状态不能修改！', duration: 4})
      }
    },
    handleEdit () {
      this.editVisiable = false
      this.reload()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleeditclose () {
      this.editVisiable = false
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
</style>
