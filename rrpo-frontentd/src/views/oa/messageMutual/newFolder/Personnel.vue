<template>
<div>
  <a-modal
    :visible="folderVisible"
    title="传阅人员"
    :afterClose="()=>isEdit=false"
    width="100%"
    okText="确定"
    cancelText="取消"
    :destroyOnClose="true"
    @cancel="modalclosel"
    @ok="modalsubmit"
  >
    <a-row style="display: flex;justify-content: center;">
      <!--    省级-->
      <a-row style="border: 1px solid #e1e1e1;display: flex;justify-content: center;">
      <a-col :span="6" style="height: 580px;">
        <a-table style="text-align: center;margin-left: 10px"
                 :data-source="dataProvince"
                 :columns="columnProvince"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :scroll="{ y: 500 }"
                 :rowSelection="{selectedRowKeys: selectedIdP, onChange: onChangeCityP}"
        >
        </a-table>
      </a-col>
<!--      市级-->
      <a-col :span="6" style="height: 580px;">
        <a-table style="text-align: center;margin-left: 10px"
                 :data-source="cityS"
                 :columns="columnsCityS"
                 :rowKey="(record)=> record.deptId"
                 :childrenColumnName="'childrenColumnName'"
                 :pagination="false"
                 :scroll="{ y: 500 }"
                 :rowSelection="{selectedRowKeys: selectedIdS, onChange: onChangeCityS}"
                 >
        </a-table>
      </a-col>
<!--      县级-->
      <a-col :span="6" style="height: 580px;">
        <a-table style="text-align: center;margin-left: 10px"
                 :data-source="cityQ"
                 :columns="columnsCityQ"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :loading="loading"
                 :scroll="{ y: 500 }"
                 :rowSelection="{selectedRowKeys: selectedIdQ, onChange: onChangeCityQ}"
                 >
        </a-table>
      </a-col>
<!--      乡道-->
      <a-col :span="6" style="height: 580px;">
        <a-table style="text-align: center;margin-left: 10px;margin-right: 10px"
                 :data-source="cityX"
                 :columns="columnsCityX"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :loading="loading"
                 :scroll="{ y: 500 }"
                 :rowSelection="{selectedRowKeys: selectedIdX, onChange: onChangeCityX}"
                 >
        </a-table>
      </a-col>
      </a-row>
<!--      传阅人员选择-->
      <a-col :span="3" style="height: 580px;width: 240px;border: 1px solid #e1e1e1;margin-left: 20px">
        <div>
        <a-table style="text-align: center;"
                 :data-source="dataSource"
                 :columns="columns"
                 :loading="loading"
                 :rowKey="(record)=> record.userId"
                 :scroll="{ y: 490 }"
                 :pagination="false"
                 :rowSelection="{selectedRowKeys: selectedIdY, onChange: onSelectChange}"
                 >
        </a-table>
        </div>
        <div style="position: absolute;bottom: 0;">
          <a-input v-model="groupName" placeholder="请输入分组名称" style="width: 140px;margin-right: 5px"></a-input>
          <a-icon @click="addGroup"
                  type="plus-square"
                  twoToneColor="#00BFFF"
                  theme="twoTone"
                  :loading="loading"
                  style="font-size: 25px;margin-right: 10px"
                  title="添加分组"/>
          <a-icon @click="editGroups"
                  type="setting"
                  twoToneColor="#42b983"
                  theme="twoTone"
                  :loading="loading"
                  style="font-size: 25px;"
                  title="确认修改分组"/>
        </div>
      </a-col>
      <a-col :span="5" style="height: 580px;border: 1px solid #e1e1e1;margin-left: 20px">
        <a-table
                 :data-source="dataGroup"
                 :columns="columnsGroup"
                 :rowKey="(record)=> record.id"
                 :scroll="{ y: 500 }"
                 :pagination="false"
                 :rowSelection="{selectedRowKeys: selectedIdG, onChange: onSelectGroup}"
                 >
          <template slot="operationG" slot-scope="text, record">
            <a-icon @click="editGroup(record)" type="setting" twoToneColor="#42b983" theme="twoTone" title="修改分组"/>
            <a-icon  type="delete" theme="twoTone" twoToneColor="#E21918" @click="deleteGroup(record)" title="删除分组"></a-icon>
          </template>
        </a-table>
      </a-col>
    </a-row>
  </a-modal>
</div>
</template>

<script>
const columnProvince = [{
  title: '省级全选',
  dataIndex: 'deptName',
  key: '6'
}]
const columnsCityS = [{
  title: '市级全选',
  dataIndex: 'deptName',
  key: '5'
}]
const columnsCityQ = [{
  title: '区县全选',
  dataIndex: 'deptName',
  key: '4'
}]
const columnsCityX = [{
  title: '乡道全选',
  dataIndex: 'deptName',
  key: '3'
}]
const columns = [{
  title: '传阅人员',
  dataIndex: 'username',
  key: '2'
}]
const columnsGroup = [{
  title: '人员分组',
  dataIndex: 'name',
  width: '100px',
  key: '1'
},
{
  title: '操作',
  dataIndex: 'operationG',
  width: '70px',
  scopedSlots: {customRender: 'operationG'}
}]

export default {
  name: 'Personnel',
  props: {
    folderVisible: {
      default: false
    }
  },
  data () {
    return {
      // 人员选择
      dataSource: [],
      // 分组数据
      dataGroup: [],
      groupId: '',
      // 省级，市级，区县，乡道接收数据
      dataProvince: [],
      cityS: [],
      cityQ: [],
      cityX: [],
      // 选中的id
      selectedIdP: [],
      selectedIdY: [],
      selectedIdQ: [],
      selectedIdS: [],
      selectedIdX: [],
      selectedIdG: [],
      // 列表数据显示
      columns,
      columnProvince,
      columnsCityS,
      columnsCityQ,
      columnsCityX,
      columnsGroup,
      // 分组名称
      groupName: '',
      cont: 1,
      loading: false
    }
  },
  mounted () {
    // 获取模态框树形结构
    this.loading = true
    this.$get('/dept/list', {deptId: 0}).then(res => {
      this.cityS = res.data
      this.loading = false
    })
    // 省级数据
    this.$get('/dept/findProvince').then(res => {
      this.dataProvince = res.data.data
      this.loading = false
    })
    this.viewGroup()
  },
  methods: {
    // 获取分组
    viewGroup () {
      this.loading = true
      this.$get('/exchangeGroup/getInformListByMapper').then(res => {
        this.dataGroup = res.data
        this.loading = false
        console.log('获取分组信息', this.dataGroup)
      })
    },
    // 接收人员
    getPersonnerl (result) {
      this.dataSource = result // 获取所有的乡道id显示全部勾上
      let userIds = []
      for (let key in this.dataSource) {
        userIds.push(this.dataSource[key].userId)
      }
      this.selectedIdY = userIds // 获取所有的用户的id显示全部勾上
    },
    // 根据机构组织id查询人员
    personnel () {
      this.loading = true
      let arr = [...this.selectedIdS, ...this.selectedIdQ, ...this.selectedIdX, ...this.selectedIdP]
      console.log(arr)
      const param = {
        deptIds: arr
      }
      this.$get('/user/getDeptAndUser', param).then((r) => {
        let dataUser = r.data.data.user // 用户信息
        this.dataSource = dataUser
        let userIds = []
        for (let key in this.dataSource) {
          userIds.push(this.dataSource[key].userId)
        }
        this.selectedIdY = userIds // 获取所有的用户的id显示全部勾上
        this.loading = false
      })
    },
    // 省级
    onChangeCityP (p) {
      this.loading = true
      this.selectedIdP = p
      this.dataSource = []
      this.loading = false
      this.personnel()
    },
    // 选择市级
    onChangeCityS (s) {
      this.selectedIdS = s
      this.loading = true
      this.dataSource = []
      this.cityQ = []
      this.cityX = []
      this.$get('/user/getDeptAndUser', {deptIds: s}).then((r) => {
        this.cityQ = r.data.data.dept
        this.loading = false
      })
      this.personnel()
      this.selectedIdQ = []
      this.selectedIdX = []
    },
    // 选择区县
    onChangeCityQ (q) {
      this.selectedIdQ = q
      this.loading = true
      this.dataSource = []
      this.cityX = []
      this.$get('/user/getDeptAndUser', {deptIds: q}).then((r) => {
        this.cityX = r.data.data.dept // 市级区县街道数据
        this.loading = false
      })
      this.personnel()
      this.selectedIdX = []
    },
    // 选择乡道
    onChangeCityX (x) {
      this.selectedIdX = x
      this.dataSource = []
      this.personnel()
    },
    // 选择人员
    onSelectChange (y) {
      this.selectedIdY = y
      console.log('人员选中id：', this.selectedIdY)
    },
    // 选择分组
    onSelectGroup (g) {
      this.loading = true
      this.selectedIdG = g
      const param = {
        groupIds: this.selectedIdG
      }
      this.dataSource = []
      this.groupName = ''
      this.$get('/exchangeGroup/getGroupUserList', param).then(res => {
        console.log('获取分组人员数据', res.data)
        if (res.data.status === 1) {
          this.dataSource = res.data.data
          this.selectedIdS = []
          this.selectedIdQ = []
          this.selectedIdX = []
          this.selectedIdP = []
          // 获取所有组织机构id
          let userIds = []
          for (let key in this.dataSource) {
            userIds.push(this.dataSource[key].userId)
          }
          this.selectedIdY = userIds // 获取所有的用户的id显示全部勾上
          this.loading = false
        }
      })
    },
    // 修改分组
    editGroup (record) {
      this.groupId = record.id
      this.onSelectGroup(record.id.split(','))
      this.groupName = record.name
    },
    // 删除人员分组
    deleteGroup (record) {
      console.log(record)
      let groupId = record.id
      let that = this
      this.$confirm({
        title: '确定删除分组?',
        content: '当您点击确定按钮后，分组将会被彻底删除',
        centered: true,
        onOk () {
          that.loading = true
          that.$delete('/exchangeGroup/' + groupId).then(res => {
            that.viewGroup()
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 1})
            that.loading = false
          })
        },
        onCancel () {
        }
      })
    },
    // 添加分组
    addEidt (param) {
      this.loading = true
      if (this.selectedIdY.length === 0) {
        this.$notification.warning({message: '系统提示', description: '请选择传阅人员！', duration: 2})
        this.loading = false
        return
      } else if (this.groupName === '') {
        this.loading = false
        this.$notification.warning({message: '系统提示', description: '请输入分组名称！', duration: 2})
        return
      }
      this.$post('/exchangeGroup', param).then(res => {
        this.loading = false
        if (res.status === 200) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 2})
          this.groupName = ''
          this.dataSource = []
          this.selectedIdS = []
          this.selectedIdQ = []
          this.selectedIdY = []
          this.selectedIdX = []
          this.selectedIdG = []
          this.selectedIdP = []
          this.cont = 1
          this.viewGroup()
        }
      })
      this.cont = 2
    },
    addGroup () {
      let param = {
        groupUserIds: this.selectedIdY,
        name: this.groupName
      }
      if (this.cont === 1) {
        this.addEidt(param)
      }
    },
    editGroups () {
      let param = {
        id: this.groupId,
        groupUserIds: this.selectedIdY,
        name: this.groupName
      }
      this.addEidt(param)
    },
    // 关闭模态框
    modalclosel () {
      this.$emit('close')
    },
    // 选传阅人的提交模态框
    modalsubmit () {
      console.log(this.selectedIdY)
      if (this.selectedIdY.length === 0) {
        this.$notification.warning({message: '系统提示', description: '至少选择一个传阅人！', duration: 4})
        return
      }
      this.$emit('success', this.selectedIdY)
    }
  }
}
</script>

<style scoped>

</style>
