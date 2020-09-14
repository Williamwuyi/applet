<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="接口名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.dataName"/>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="更新时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="updateTime"></range-date>
              </a-form-item>
            </a-col>
          </a-row>
        </div>
        <span style="float: right; margin-top: 3px;">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button v-hasPermission="'basic:add'" type="primary" ghost @click="add">新增</a-button>
        <a-button v-hasPermission="'basic:delete'" @click="batchDelete">删除</a-button>
        <a-dropdown v-hasPermission="'basic:export'">
          <a-menu slot="overlay">
            <a-menu-item key="export-data" @click="exportExcel">导出Excel</a-menu-item>
          </a-menu>
          <a-button>
            更多操作 <a-icon type="down" />
          </a-button>
        </a-dropdown>
      </div>
      <!-- 表格区域 -->
      <a-table :columns="columns"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :scroll="{ x: 900 }"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :rowKey="record => record.id"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a-icon v-hasPermission="'basic:update'" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修改"></a-icon>
          <a-badge v-hasNoPermission="'basic:update'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 操作
    <basic-add
      @success="handleBasicAddSuccess"
      @close="handleBasicAddClose"
      :basicAddVisiable="basicAddVisiable">
    </basic-add>
    <basic-edit
      ref="basicEdit"
      @success="handleBasicEditSuccess"
      @close="handleBasicEditClose"
      :basicEditVisiable="basicEditVisiable">
    </basic-edit>
    -->
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BasicAdd from './BasicAdd'
import BasicEdit from './BasicEdit'

export default {
  name: 'Basic',
  components: {BasicAdd, BasicEdit, RangeDate},
  data () {
    return {
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      queryParams: {},
      filteredInfo: null,
      paginationInfo: null,
      sortedInfo: null,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      loading: false,
      basicAddVisiable: false,
      basicEditVisiable: false
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '接口ID',
        dataIndex: 'dataTypeId'
      }, {
        title: '接口名称',
        dataIndex: 'dataName'
      }, {
        title: '对应实体',
        dataIndex: 'bean'
      }, {
        title: '是否需要更新',
        dataIndex: 'isUpdate',
        customRender: (text) => {
          if (text) {
            return <a-tag color="red">是</a-tag>
          } else {
            return <a-tag color="cyan">否</a-tag>
          }
        }
      }, {
        title: '上次更新时间',
        dataIndex: 'updateTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'updateTime' && sortedInfo.order
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' },
        fixed: 'right',
        width: 120
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    handleBasicAddClose () {
      this.BasicAddVisiable = false
    },
    handleBasicAddSuccess () {
      this.BasicAddVisiable = false
      this.$message.success('新增接口Basic成功')
      this.fetch()
    },
    add () {
      this.basicEditVisiable = true
    },
    handleBasicEditClose () {
      this.basicEditVisiable = false
    },
    handleBasicEditSuccess () {
      this.basicEditVisiable = false
      this.$message.success('修改接口成功')
      this.fetch()
    },
    edit (record) {
      this.basicEditVisiable = true
      this.$refs.deptEdit.setFormValues(record)
    },
    handleDateChange (value) {
      if (value) {
        this.queryParams.createTimeFrom = value[0]
        this.queryParams.createTimeTo = value[1]
      }
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除，如果其包含子记录，也将一并删除！',
        centered: true,
        onOk () {
          that.$delete('crm/data/' + that.selectedRowKeys.join(',')).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.fetch()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    exportExcel () {
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.$export('crm/data/excel', {
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams
      })
    },
    search () {
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
        ...this.queryParams
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      // 清空时间选择
      this.$refs.updateTime.reset()
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.pagination.current = this.paginationInfo.current
        this.pagination.pageSize = this.paginationInfo.pageSize
        params.pageSize = this.paginationInfo.pageSize
        params.pageNum = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.pageSize = this.pagination.defaultPageSize
        params.pageNum = this.pagination.defaultCurrent
      }
      this.$get('crm/data/crmDataList', {
        ...params
      }).then((r) => {
        let data = r.data
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.dataSource = data.rows
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  }
}
</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";
</style>
