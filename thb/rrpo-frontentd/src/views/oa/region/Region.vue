<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div class="container">
        <region-tree
          @reloadStaffTable="findDeptStaff"
          @clearSelectrows="cleardRowKeys"
          @getMannerableDept="getMannerableDept"
        />
      </div>
      <div class="sildtable">
        <div :class="advanced ? 'search' : null">
          <!-- 搜索区域 -->
          <a-form layout="horizontal">
            <div :class="advanced ? null: 'fold'">
              <a-row >
                <a-col :md="8" :sm="24" >
                  <a-form-item
                    label="地区"
                    :labelCol="{span: 5}"
                    :wrapperCol="{span: 18, offset: 1}">
                    <a-input v-model="Regionserach.district"/>
                  </a-form-item>
                </a-col>
                <a-col   :md="8" :sm="24">
                  <a-form-item
                    label="街道"
                    :labelCol="{span: 5}"
                    :wrapperCol="{span: 18, offset: 1}">
                    <a-input v-model="Regionserach.street"/>
                  </a-form-item>
                </a-col>
                <a-col :md="8" :sm="24">
              <span style="float: right; margin: 3px;">
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
          <a-button type="primary" v-hasPermission="'region:add'" @click="handleAdd" ghost>新增</a-button>
          <a-button class="editable-add-btn" @click="onDelete" v-hasPermission="'region:delete'">
            删除
          </a-button>
          <a-dropdown>
            <a-menu slot="overlay">
              <a-menu-item key="export-data" @click="exprotExccel">导出Excel</a-menu-item>
              <a-menu-item key="export-data1" @click="ImportExccel">导入Excel</a-menu-item>
            </a-menu>
            <a-button>
              更多操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-table bordered
            :data-source="dataSource"
                   :columns="columns"
                   :pagination="pagination"
                   :row-selection="rowSelection"
                   :loading="loading">
            <template slot="operation" slot-scope="text, record">
              <a-icon  type="setting" theme="twoTone" twoToneColor="#4a9ff5" style="margin-left:8px" @click="edit(record)" title="修改"></a-icon>
            </template>
          </a-table>
        </div>
      </div>
      <!-- 修改 -->
<!--      <region-edit-->
<!--        @success="handleEdit"-->
<!--        @close="hanleeditclose"-->
<!--        :regionEditVisiable="regionEditVisiable"-->
<!--        ref="oldedit"-->
<!--      />-->
<!--      &lt;!&ndash; 添加事件 &ndash;&gt;-->
<!--      <regionadd-->
<!--        @success="handlecomAdd"-->
<!--        @close="hanlecomclose"-->
<!--        :regionAddVisiable="regionAddVisiable"-->
<!--      />-->
    </div>
  </a-card>
</template>
<script>
// import RegionEdit from './RegionEdit'
// import Regionadd from './Regionadd'
import RegionTree from './RegionTree'
export default{
  name: 'Region',
  components: {RegionTree},
  data () {
    return {
      Regionserach: {},
      advanced: false,
      visible: false,
      deptStaff: '1',
      regionAddVisiable: false,
      regionEditVisiable: false,
      loading: false,
      dataSource: [],
      keyDelet: [],
      count: 2,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      columns: [
        {
          title: '地区名',
          dataIndex: 'userName'
        },
        {
          title: '街道',
          dataIndex: 'phone'
        },
        {
          title: '电话',
          dataIndex: 'telPhone'
        },
        {
          title: '邮箱',
          dataIndex: 'email'
        },
        {
          title: '人员',
          dataIndex: 'weiXin'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  mounted () {
    this.fach()
  },
  // 选择框
  computed: {
    rowSelection () {
      return {
        onChange: (selectedRowKeys, selectedRows) => {
          console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows)
          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.keyDelet.push(selectedRowKeys)
        }
      }
    }
  },
  methods: {
    // 渲染
    fach (params = {}) {
      // this.loading = true
      // this.$get('/address/' + this.deptStaff).then(res => {
      //   this.dataSource = []
      // 分页;
      // const pagination = { ...this.pagination }
      // pagination.total = newData.total
      // this.dataSource = newData.rows
      // this.pagination = pagination
      //   this.loading = false
      // })
    },
    // 查询
    search () {
      // 通过this.phone来进行查询
      this.fach(this.Regionserach)
    },
    // 重置
    reset () {
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.Regionserach = {}
      this.fach()
    },
    // 更多操作
    // 导出
    exprotExccel () {
      // this.$export('/address/excel', this.Regionserach.phone)
    },
    // 导入
    ImportExccel: function () {
      this.visible = true
    },
    // 取消导入
    handleCancel () {
      this.visible = false
    },
    // 导入成功
    handleCreate () {
      this.visible = false
      this.$message.success('导入成功！')
    },
    // 表格
    // 删除
    onDelete () {
      console.log(this.keyDelet)
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      // this.$delete('/address/' + this.keyDelet).then(() => {
      //   this.$message.success('删除成功')
      //   this.fach()
      // })
    },
    // 添加功能
    // 新增事件
    handleAdd () {
      this.regionAddVisiable = true
    },
    handlecomAdd () {
      this.regionAddVisiable = false
      this.fach()
      this.$message.success('新增事件成功')
    },
    hanlecomclose () {
      this.regionAddVisiable = false
    },
    // 修改功能
    edit (record) {
      this.regionEditVisiable = true
      // 掉用子组件方法加传值
      this.$refs.oldedit.setFormValues(record)
    },
    handleEdit () {
      this.regionEditVisiable = false
      this.fach()
      this.$message.success('修改成功')
    },
    hanleeditclose () {
      this.regionEditVisiable = false
    },
    // 根据点击的部门节点，展示对应部门下的员工
    findDeptStaff (res) {
      // this.keyDelet = []
      this.deptStaff = res
      this.fach()
    },
    // 清空selcetrowkeys.
    cleardRowKeys () {
      // this.selectedRowKeys = [];
    },
    // 新增人员时的可选择部门
    getMannerableDept (data) {
      this.mannerableDepts = data.newTreeNodes
      this.deptOptions = data.allDepts
    }
  }
}
</script>
<style lang="less" scoped>
  @import "../../../../static/less/Common";
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
  // 下拉
  .deptSelect {
    height: 32px;
    margin-left: 10px;
  }
  /*左边树*/
  div .container {
    /*width: 400px;*/
    float: left;
    position: relative;
    /*border:#f0f2f5 solid 1px;*/
    background: #ffffff;
  }
  div .sildtable {
    float: left;
    margin-left: 20px;
    width: calc(100% - 400px);
    height: auto;
  }
</style>
