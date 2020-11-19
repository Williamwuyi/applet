<template>
<!--  <a-card :bordered="false" class="card-area">-->
  <div style="background-color: white;">
    <a-row>
      <div style="margin:15px 10px">
      <a-breadcrumb>
        <a-breadcrumb-item>
          <a @click="home">首页</a>
        </a-breadcrumb-item>
        <a-breadcrumb-item>通讯录</a-breadcrumb-item>
      </a-breadcrumb>
      </div>
    <div>
      <div class="container">
        <com-tree
          @reloadStaffTable="findDeptStaff"
          @clearSelectrows="cleardRowKeys"
          :loading="loading"
          @getMannerableDept="getMannerableDept"
        />
      </div>
      <div class="sildtable">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="8" :sm="20">
              <a-form-item
                label="姓名"
                :labelCol="{span: 2}"
                :wrapperCol="{span: 14, offset:1}">
                <a-input v-model="Comserach.CommunideName"/>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="20" >
              <a-form-item
                label="手机号"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 14, offset:0}">
                <a-input v-model="Comserach.telphone"/>
              </a-form-item>
            </a-col>
            <a-col style="margin-top: 5px; display: inline-block">
              <span style="margin: 3px;">
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
    <a-button class="btn" type="primary" @click="handleAdd" v-hasPermission="'communication:add'">
      添加
    </a-button>
    <a-button class="btn" @click="handleDel" style="background-color: #FF4D4F;color:white;" v-hasPermission="'communication:delete'">
      删除
    </a-button>
    <a-button class="btn" @click="exprotExccel" style="background-color: #CDAD00;color: white" v-hasPermission="'communication:export'">
      导出全部
    </a-button>
    <a-button class="btn" @click="ImportExccel" style="background-color: #CD853F;color: white" v-hasPermission="'communication:import'">
      导入Excel
    </a-button>
    <a-table
      :data-source="dataSource"
      :columns="columns"
      :pagination="pagination"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      :scroll="{ y: 540 }"
      @change="handleTableChange"
      :rowKey="record=>record.id"
      :loading="loading">
      <template slot="operation" slot-scope="text, record">
<!--        <a-icon  type="setting" theme="twoTone" twoToneColor="#4a9ff5" style="margin-left:8px" @click="edit(record)" title="修改" v-hasPermission="'communication:edit'"></a-icon>-->
        <a @click="edit(record)" style="color: #4a9ff5">修改</a>
      </template>
    </a-table>
  </div>
      </div>
<!-- 修改 -->
<com-edit
  @success="handleEdit"
  @close="hanleeditclose"
  :comEditVisiable="comEditVisiable"
  ref="oldedit"
/>
<!-- 添加 -->
<comadd
  @success="handlecomAdd"
  @close="hanlecomclose"
  :comAddVisiable="comAddVisiable"
/>
<!-- 导入 -->
<collection-create-form
        ref="collectionForm"
        :visible="visible"
        @success="handleCancel"
        @cancel="handleCancel"
        @create="handleCreate"
/>
    </div>
    </a-row>
  </div>
<!--    </a-card>-->
</template>
<script>
import ComEdit from './ComEdit'
import Comadd from './Comadd'
import ComTree from './comTree'
import CollectionCreateForm from './ComImport'
export default{
  name: 'Communication',
  components: {ComEdit, Comadd, ComTree, CollectionCreateForm},
  data () {
    return {
      Comserach: {},
      advanced: false,
      visible: false,
      deptStaff: '',
      comAddVisiable: false,
      comEditVisiable: false,
      loading: false,
      dataSource: [],
      deptID: '',
      sortedInfo: null,
      selectedRowKeys: [],
      character: {},
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      count: 2
    }
  },
  computed: {
    columns () {
      return [
        {
          title: '姓名',
          dataIndex: 'userName',
          key: '1'
        },
        {
          title: '单位',
          dataIndex: 'unit',
          key: '2'
        },
        {
          title: '职务',
          dataIndex: 'position',
          key: '3'
        },
        {
          title: '手机号',
          dataIndex: 'telPhone',
          key: '4'
        },
        {
          title: '电话',
          dataIndex: 'phone',
          key: '5'
        },
        {
          title: '邮箱',
          dataIndex: 'email',
          key: '6'
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
    this.fetch()
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    home () {
      this.$router.push('/home')
    },
    // 根据选择的树结构的id查询数据
    fetch (params = {pageNum: 1, pageSize: 10, deptId: this.deptID}) {
      this.loading = true
      this.$get('/address/iPage', params).then(res => {
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
    // 查询
    search () {
      if (this.Comserach.CommunideName === undefined) {
        this.Comserach.CommunideName = ''
      }
      if (this.Comserach.telphone === undefined) {
        this.Comserach.telphone = ''
      }
      // 获取当前列的排序和列的过滤规则
      const param = {
        deptId: this.deptID,
        telPhone: this.Comserach.telphone,
        userName: this.Comserach.CommunideName
      }
      this.fetch(param)
    },
    // 重置
    reset () {
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.Comserach = ''
      this.fetch()
    },
    handleTableChange (pagination) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.character.deptId = this.deptID
      this.fetch(this.character)
    },
    // 更多操作
    // 导出
    exprotExccel () {
      this.$export('/address/excel')
      this.$notification.success({message: '系统提示', description: '操作成功，数据正在下载中......', duration: 20})
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
      this.$notification.success({message: '系统提示', description: '导入成功！', duration: 4})
      this.fetch()
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
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let comId = that.selectedRowKeys
          that.$delete('/address/' + comId.join(',')).then((res) => {
            if (res.data.status === 1) {
              that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
              that.selectedRowKeys = []
              that.fetch()
            } else if (res.data.status === 0) {
              that.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
              that.selectedRowKeys = []
            }
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
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fetch()
    },
    hanlecomclose () {
      this.comAddVisiable = false
    },
    // 修改功能
    edit (record) {
      this.comEditVisiable = true
      // 掉用子组件方法加传值
      this.$refs.oldedit.setFormValues(record)
    },
    handleEdit () {
      this.comEditVisiable = false
      this.fetch()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleeditclose () {
      this.comEditVisiable = false
    },
    // 根据点击的部门节点，展示对应部门下的员工
    findDeptStaff (res) {
      this.deptID = res
      const param = {
        deptId: res
      }
      this.fetch(param)
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
  .btn{
    margin-bottom: 5px;
    margin-right: 10px;
  }
</style>
