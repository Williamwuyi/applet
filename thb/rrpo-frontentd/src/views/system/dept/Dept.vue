<template>
  <div style="width: 100%">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="名称"
                :labelCol="{span: 1}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.deptName"/>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24" >
              <a-form-item
                label="创建时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
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
        <a-button v-hasPermission="'dept:add'" type="primary"  @click="add">新增</a-button>
        <a-button class="btnHoven" style="background-color: #FF4D4F; color: white; border-radius: 4px!important;" v-hasPermission="'dept:delete'" @click="batchDelete">删除</a-button>
        <a-dropdown v-hasPermission="'dept:export'">
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
               :scroll="{ x: 900}"
               :rowKey="(record)=> record.deptId"
               @expand="expand"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a v-hasPermission="'dept:update'" style="color: #4a9ff5" @click="edit(record)" title="修改">修改</a>
          <a-badge v-hasNoPermission="'dept:update'" status="warning" text="无权限"></a-badge>
        </template>
      </a-table>
    </div>
    <!-- 新增部门 -->
    <dept-add
      @success="handleDeptAddSuccess"
      @close="handleDeptAddClose"
      :deptAddVisiable="deptAddVisiable">
    </dept-add>
    <!-- 修改部门 -->
    <dept-edit
      ref="deptEdit"
      @success="handleDeptEditSuccess"
      @close="handleDeptEditClose"
      :deptEditVisiable="deptEditVisiable">
    </dept-edit>
  </div>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import DeptAdd from './DeptAdd'
import DeptEdit from './DeptEdit'

export default {
  name: 'Dept',
  components: {DeptAdd, DeptEdit, RangeDate},
  data () {
    return {
      arr: [],
      advanced: false,
      dataSource: [],
      selectedRowKeys: [],
      queryParams: {},
      sortedInfo: null,
      pagination: {
        defaultPageSize: 10000000,
        hideOnSinglePage: true,
        indentSize: 100
      },
      children: [],
      grandson: [],
      grandsonSon: [],
      loading: false,
      deptAddVisiable: false,
      deptEditVisiable: false
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '名称',
        dataIndex: 'deptName'
      }, {
        title: '排序',
        dataIndex: 'orderNum'
      }, {
        title: '创建时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '修改时间',
        dataIndex: 'modifyTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'modifyTime' && sortedInfo.order
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
    this.fetch({deptId: -1})
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    handleDeptAddClose () {
      this.deptAddVisiable = false
    },
    handleDeptAddSuccess () {
      this.deptAddVisiable = false
      this.$message.success('新增部门成功')
      this.reload()
    },
    add () {
      this.deptAddVisiable = true
    },
    handleDeptEditClose () {
      this.deptEditVisiable = false
    },
    handleDeptEditSuccess () {
      this.deptEditVisiable = false
      this.$message.success('修改部门成功')
      this.reload()
    },
    edit (record) {
      this.deptEditVisiable = true
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
        content: '当您点击确定按钮后，这些记录将会被彻底删除，如果其包含子记录，也将一并删除！且该部门的通讯录以及文件都会移动至公共区域',
        centered: true,
        onOk () {
          that.$delete('dept/' + that.selectedRowKeys.join(',')).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.reload()
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
      this.$export('dept/excel', {
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
      // console.log(sortedInfo)
      // console.log(JSON.stringify(this.queryParams) === '{}')
      if (sortedInfo === null && JSON.stringify(this.queryParams) === '{}') {
        this.fetch({deptId: -1})
      } else {
        this.fetch({
          sortField: sortField,
          sortOrder: sortOrder,
          ...this.queryParams
        })
      }
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      // 清空时间选择
      this.$refs.createTime.reset()
      this.reload()
    },
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // this.loading = true
      this.$get('/dept/list', {
        ...params
      }).then((r) => {
        this.arr = r.data
        this.dataSource = this.arr
        this.loading = false
      })
    },
    expand (expanded, record) {
      if (expanded) {
        this.$get('/dept/list', {deptId: record.deptId}).then((r) => {
          if (r.data.length > 0) {
            if (record.rank === 0) {
              this.arr.forEach(t => {
                if (record.deptId === t.deptId) {
                  t.children = r.data
                  this.children = r.data
                }
              })
            } else if (record.rank === 1 || record.rank === 4) {
              this.children.forEach(t => {
                if (record.deptId === t.deptId) {
                  t.children = r.data
                  this.grandson = r.data
                }
              })
            } else if (record.rank === 2) {
              this.grandson.forEach(t => {
                if (record.deptId === t.deptId) {
                  t.children = r.data
                  this.grandsonSon = r.data
                }
              })
            }
          } else {
            this.$message.error('没有下一级')
          }
        })
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "../../../../static/less/Common";
.btnHoven:hover{
  border-color: #FF4D4F;;
}
</style>
