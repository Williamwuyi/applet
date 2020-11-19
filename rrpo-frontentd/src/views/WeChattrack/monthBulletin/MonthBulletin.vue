<template>
  <div style="width: 100%;min-height: 690px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="期数查询"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 15, offset: 0}">
                <a-input />
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
      <a-button class="editable-add-btn" @click="handleHz" >
        生成简报
      </a-button>
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :scroll="{ y: 450 }"
        :pagination="pagination"
        :rowKey="record => {record.wxMonthId}"
      >
      </a-table>
    </div>
    <!--  生成简报  -->
    <b-modal
      :BModalVisiable="BModalVisiable"
      @close="DMonClose"
    />
  </div>
</template>
<script>
import BModal from './BModal'
export default {
  name: 'MonthBulletin',
  components: {BModal},
  data () {
    return {
      advanced: false,
      loading: false,
      BModalVisiable: false,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      // 表格
      dataSource: []
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
      return [{
        title: '期数',
        dataIndex: 'deptJc.deptName'
      }, {
        title: '名称',
        dataIndex: 'qun.wxName'
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
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.character.month = dateTime
    },
    // 查询
    search () {
      this.fach()
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空时间
      this.timeValue = undefined
      // 重置列排序规则
      this.sortedInfo = null
      this.refId = ''
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
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
    //  汇总表
    handleHz () {
      this.BModalVisiable = true
    },
    DMonClose () {
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
