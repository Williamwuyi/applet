<template>
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
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="5" :sm="20" >
              <a-form-item
                label="单位"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 16, offset:0}">
                <a-tree-select
                  :allowClear="true"
                  tree-data-simple-mode
                  :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
                  :treeData="deptTreeData"
                  :load-data="getDept"
                  v-model="character.deptId">
                </a-tree-select>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="20" >
              <a-form-item
                label="护路职务"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 14, offset: 0}">
<!--                :style="{width:'150px'}"-->
                <a-select v-model="jobs" @change="jobChange">
                  <a-select-option v-for="(n,index) in job" :key="index" :value="n.dictId">
                    {{n.fieldName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="20">
              <a-form-item
                label="姓名"
                :labelCol="{span: 6}"
                :wrapperCol="{span: 18, offset: 0}">
                <a-input v-model="character.userName" placeholder="请输入姓名"/>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="20" style="margin-left: 17px">
              <a-form-item
                label="手机号"
                :labelCol="{span: 6}"
                :wrapperCol="{span: 18, offset:0}">
                <a-input v-model="character.telPhone" placeholder="请输入手机号"/>
              </a-form-item>
            </a-col>
            <a-col style="margin-left: 15px;margin-top: 4px;display: inline-block">
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
      <a-row>
        <a-col :span="4">
          <com-tree
            @reloadStaffTable="findDeptStaff"
            @clearSelectrows="cleardRowKeys"
            :loading="loading"
            @getMannerableDept="getMannerableDept"
          />
        </a-col>
        <a-col :span="20">
          <div style="margin-bottom: 15px">
          <a-button class="btn" type="primary" @click="handleAdd" v-hasPermission="'communication:add'">
            添加
          </a-button>
          <a-button class="btn" @click="handleDel" style="background-color: #FF4D4F;color:white;" v-hasPermission="'communication:delete'">
            删除
          </a-button>
          <a-button class="btn" @click="exprotExccel" style="background-color: #CDAD00;color: white" v-hasPermission="'communication:export'">
            导出Excel
          </a-button>
          <a-button class="btn" @click="ImportExccel" style="background-color: #CD853F;color: white" v-hasPermission="'communication:import'">
            导入Excel
          </a-button>
          </div>
          <a-table
            :data-source="dataSource"
            :columns="columns"
            :pagination="pagination"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
            :scroll="{ y: 540 }"
            @change="handleTableChange"
            :rowKey="record=>record.id"
            :loading="loading">
            <template slot="operation" slot-scope="text, record" v-hasPermission="'communication:edit'">
              <a @click="edit(record)" style="color: #4a9ff5">修改</a>
            </template>
          </a-table>
        </a-col>
      </a-row>
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
        @success="handleCreate"
        @cancel="handleCancel"
/>
    </a-row>
  </div>
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
      advanced: false,
      visible: false,
      deptStaff: '',
      deptTreeData: [],
      job: [],
      jobs: '',
      comAddVisiable: false,
      comEditVisiable: false,
      loading: false,
      dataSource: [],
      // deptID: '',
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
  inject: ['reload'],
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
          dataIndex: 'deptName',
          key: '2'
        },
        {
          title: '护路职位',
          dataIndex: 'position',
          key: '3'
        },
        {
          title: '职位',
          dataIndex: 'newPosition',
          key: '4'
        },
        {
          title: '手机号',
          dataIndex: 'telPhone',
          key: '5'
        },
        {
          title: '电话',
          dataIndex: 'phone',
          key: '6'
        },
        {
          title: '邮箱',
          dataIndex: 'email',
          key: '7'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  mounted () {
    this.fetch()
    // 加载单位
    this.getDept()
    // 加载基础管职务
    this.$get('/dict/getListTable', {parentId: '2a6c549aa14e2f77837b1a11f6e7ad0f', pageSize: 50}).then(res => {
      this.job = res.data.data.records
    })
  },
  methods: {
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    home () {
      this.$router.push('/home')
    },
    // 根据选择的树结构的id查询数据
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/address/iPage', params).then(res => {
        let newData = res.data.data
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
      this.pagination.current = 1
      this.pagination.pageSize = 10
      const param = {}
      if (this.character.userName !== undefined) {
        param.userName = this.character.userName
      }
      if (this.character.telPhone !== undefined) {
        param.telPhone = this.character.telPhone
      }
      if (this.character.position !== undefined) {
        param.position = this.character.position
      }
      if (this.character.deptId !== undefined) {
        param.deptId = this.character.deptId
      }
      this.fetch(param)
    },
    // 单位下拉树加载
    getDept (treeNode) {
      if (treeNode) {
        return new Promise(resolve => {
          setTimeout(() => {
            this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.id}).then((r) => {
              let newData = r.data.data
              newData.forEach(t => {
                this.deptTreeData.push(
                  { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
                )
              })
            })
            resolve()
          }, 300)
        })
      } else {
        this.$get('/dept/queryDeptChile').then((r) => {
          let newData = r.data.data
          newData.forEach(t => {
            this.deptTreeData.push(
              { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
            )
          })
        })
      }
    },
    jobChange (key) {
      this.character.position = key
    },
    // 重置
    reset () {
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.character = {}
      this.jobs = ''
      this.fetch()
    },
    handleTableChange (pagination) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fetch({
        ...this.character
      })
    },
    // 更多操作
    // 导出
    exprotExccel () {
      const param = {}
      if (this.character.userName !== undefined) {
        param.userName = this.character.userName
      }
      if (this.character.telPhone !== undefined) {
        param.telPhone = this.character.telPhone
      }
      if (this.character.position !== undefined) {
        param.position = this.character.position
      }
      if (this.character.deptId !== undefined) {
        param.deptId = this.character.deptId
      }
      this.$export('/address/excel', param)
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
      this.reload()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanlecomclose () {
      this.reload()
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
      this.reload()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleeditclose () {
      this.reload()
      this.comEditVisiable = false
    },
    // 根据点击的部门节点，展示对应部门下的员工
    findDeptStaff (res) {
      // this.deptID = res
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
