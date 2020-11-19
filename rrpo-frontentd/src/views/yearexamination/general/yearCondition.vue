<template>
  <a-card :bordered="false" class="card-area">
    <!-- 表格区域 -->
    <div>
      <a-button class="editable-add-btn" @click="handleAdd" v-hasPermission="'notice:add'">
        添加
      </a-button>
      <a-button class="editable-add-btn" @click="onDelete" v-hasPermission="'notice:delete'">
        删除
      </a-button>
      <a-cascader  change-on-select
                   style="width: 105px;"
                   @change="onChangeyear"
                   :options="options"
                   :placeholder="DwpnTitle"
                   ref="yearCondition"
                   :fieldNames="{ label: 'content', value: 'menusId', children: 'children' }"
      />
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :rowKey="(record)=> record.menusId"
        :pagination="pagination"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <template slot="sory" slot-scope="text, record">
          <span v-if="record.accessory == 1">是</span>
          <span v-else>否</span>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon  type="setting" theme="twoTone"  twoToneColor="#4a9ff5"  @click="edit(record)" v-hasPermission="'notice:edit'" title="修改"></a-icon>
        </template>
      </a-table>
    </div>
<!--  新增  -->
    <yearCondition-add
    :yearCondVisiable="yearCondVisiable"
    @success="yearCondAdd"
    @close="yearClose"
    />
<!--  修改  -->
<year-condition-edit
  :yearEditVisiable="yearEditVisiable"
  @success="handleEdit"
  @close="hanleeditclose"
  ref="yearEd"
/>
  </a-card>
</template>
<script>
import yearConditionAdd from './yearConditionAdd'
import yearConditionEdit from './yearConditionEdit'
export default {
  name: 'yearCondition',
  components: {yearConditionAdd, yearConditionEdit},
  data () {
    return {
      data: [],
      yearCon: {},
      loading: false,
      selectedRowKeys: [],
      sortedInfo: null,
      sad: {id: 'menusId'},
      // 默认下拉
      DwpnTitle: '未选择',
      // 新增
      yearCondVisiable: false,
      // 修改
      yearEditVisiable: false,
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
          label: '工作效果'
        }
      ]
    }
  },
  mounted () {
    this.fach()
  },
  computed: {
    columns () {
      return [{
        title: '考核规则项',
        dataIndex: 'content',
        width: '70%'
      },
        // {
      //   title: '最低扣加分/次',
      //   dataIndex: 'minGrade'
      // }, {
      //   title: '最高扣加分/次',
      //   dataIndex: 'maxGrade'
      // }, {
      //   title: '总扣加分',
      //   dataIndex: 'grade'
      // },
      {
        title: '佐证材料',
        dataIndex: 'accessory',
        scopedSlots: { customRender: 'sory' }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    // 初始渲染
    fach () {
      this.loading = true
      this.$get('/check/menus/fatherList').then(res => {
        this.DwpnTitle = res.data.data[0].content
        this.options = res.data.data
        this.dwpnId = res.data.data[0].menusId
        this.faech()
        this.loading = false
      })
    },
    // 其他渲染
    faech (parmse = {pageNum: 1, pageSize: 10, menusId: this.dwpnId}) {
      this.loading = true
      this.$get('/check/menus/lists', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 查询
    search () {},
    // 重置
    reset () {
      // 清空查询参数
      this.yearCon = {}
      // 清空选择框
      this.$refs.yearCondition.sValue = []
      // 重置列排序规则
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    // 表格选择
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
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
            that.$delete('/check/menus/deleteListById/' + that.selectedRowKeys.join(',')).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.fach()
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$notification.warning({message: '系统提示', description: '选择不能为空！', duration: 4})
      }
    },
    // 添加功能
    handleAdd () {
      this.yearCondVisiable = true
    },
    yearCondAdd () {
      this.yearCondVisiable = false
      this.fach()
      this.$refs.yearCondition.sValue = []
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    yearClose () {
      this.yearCondVisiable = false
    },
    // 修改功能
    edit (key) {
      this.yearEditVisiable = true
      this.$refs.yearEd.setFormValues(key)
    },
    handleEdit () {
      this.yearEditVisiable = false
      this.fach()
      this.$refs.yearCondition.sValue = []
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleeditclose () {
      this.yearEditVisiable = false
    },
    // 分页
    handleTableChange (pagination, filters) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.yearCon.pageNum = pagination.current
      this.yearCon.pageSize = pagination.pageSize
      this.yearCon.menusId = this.dwpnId
      this.faech({
        ...this.yearCon,
        ...filters
      })
    },
    // 下拉选项的值
    onChangeyear (value) {
      if (value.length > 0) {
        this.dwpnId = value[0]
        this.faech()
      } else {
        this.fach()
      }
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
