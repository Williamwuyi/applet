<template>
    <div style="width: 100%;min-height: 780px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
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
                <range-date @change="onTimeChange" ref="releaseTime"></range-date>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24" style="margin-left: 10px;margin-top: 3px">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <a-button style="margin-right: 10px;margin-bottom: 5px;background-color: #FF4040;color: white" @click="handleDel">
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
      <template slot="isRead" slot-scope="text, record">
        <a-tag v-if="record.isRead === 0 " color="#DEE1E6">未阅读</a-tag>
        <a-tag v-else-if="record.isRead === 1 " color="#87d068" >已阅读</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
<!--        <a-icon type="eye" theme="twoTone" twoToneColor="#42b983" @click="viewLook(record)" title="查看"></a-icon>-->
        <a @click="viewLook(record)" style="color: #42b983">查看</a>
      </template>
    </a-table>
      <in-road-view
        @success="handleView"
        @close="hanleClose"
        :viewVisiable="viewVisiable"
        ref="oldeview"
      />
    </div>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
import InRoadView from './InRoadView'
export default {
  name: 'InShieldRoad',
  components: {RangeDate, InRoadView},
  data () {
    return {
      advanced: false,
      character: {},
      dataSource: [],
      selectedRowKeys: [],
      sortedInfo: null,
      loading: false,
      isRead: 0,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      viewVisiable: false
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [
        {
          title: '标题',
          dataIndex: 'title'
        },
        {
          title: '发送人',
          dataIndex: 'creatUser'
        },
        {
          title: '接收时间',
          dataIndex: 'releaseTime',
          sorter: true,
          sortOrder: sortedInfo.columnKey === 'releaseTime' && sortedInfo.order
        },
        {
          title: '查收状态',
          dataIndex: 'isRead',
          scopedSlots: {customRender: 'isRead'}
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
    handleView () {
      this.viewVisiable = false
    },
    hanleClose () {
      this.viewVisiable = false
    },
    // 查看信息
    viewLook (record) {
      const param = {
        briefingId: record.id
      }
      this.$get('/briefing/getView', param).then(res => {
        console.log('点查看', res)
        if (res.status === 200) {
          this.viewVisiable = true
          this.$refs.oldeview.setFormValues(record)
        }
      })
    },
    // 选择时间
    onTimeChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
    },
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/briefing/inbox', params).then(res => {
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
      // 清空时间选择
      // 清空表格选择框
      this.selectedRowKeys = []
      // 清空状态选择
      this.$refs.releaseTime.reset()
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

    // 删除
    handleDel () {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (!this.selectedRowKeys.length) {
        this.$notification.warning({message: '系统提示', description: '请选择需要删除的记录！', duration: 4})
        return
      }
      let that = this
      console.log(that.selectedRowKeys)
      this.$confirm({
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let rowd = that.selectedRowKeys
          console.log(rowd)
          that.$delete('/briefing/inbox/' + rowd.join(',')).then(() => {
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            that.selectedRowKeys = []
            that.fetch()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
