<template>
<div>
  <a-modal
    :visible="folderVisible"
    title="传阅人员"
    :afterClose="()=>isEdit=false"
    width="1600px"
    okText="确定"
    cancelText="取消"
    :destroyOnClose="true"
    @cancel="modalclosel"
    @ok="modalsubmit"
  >
    <a-row style="">
      <a-col :span="3" class="tableStyle">
        <a-table style="text-align: center"
                 :data-source="dataProvince"
                 :columns="columnProvince"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :scroll="{ y: 450 }"
                 :rowSelection="{selectedRowKeys: selectedIdP, onChange: onChangeCityP}"
        >
        </a-table>
      </a-col>
      <a-col :span="3" class="tableStyle">
        <a-table style="text-align: center"
                 :data-source="cityS"
                 :columns="columnsCityS"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :scroll="{ y: 450 }"
                 :rowSelection="{selectedRowKeys: selectedIdS, onChange: onChangeCityS}"
                 >
        </a-table>
      </a-col>
      <a-col :span="3" class="tableStyle">
        <a-table style="text-align: center;"
                 :data-source="cityQ"
                 :columns="columnsCityQ"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :loading="loading"
                 :scroll="{ y: 450 }"
                 :rowSelection="{selectedRowKeys: selectedIdQ, onChange: onChangeCityQ}"
                 >
        </a-table>
      </a-col>
      <a-col :span="3" class="tableStyle">
        <a-table style="text-align: center;"
                 :data-source="cityX"
                 :columns="columnsCityX"
                 :rowKey="(record)=> record.deptId"
                 :pagination="false"
                 :loading="loading"
                 :scroll="{ y: 450 }"
                 :rowSelection="{selectedRowKeys: selectedIdX, onChange: onChangeCityX}"
                 >
        </a-table>
      </a-col>
<!--      传阅人员选择-->
      <a-col :span="3" class="tableStyle" style="width: 240px">
        <div>
        <a-table style="text-align: center;"
                 :data-source="dataSource"
                 :columns="columns"
                 :loading="loading"
                 :rowKey="(record)=> record.userId"
                 :scroll="{ y: 450 }"
                 :pagination="false"
                 :rowSelection="{selectedRowKeys: selectedIdY, onChange: onSelectChange}"
                 >
        </a-table>
        </div>
        <div style="position: absolute;bottom: 0;">
          <a-input v-model="groupName" placeholder="请输入分组名称" style="width: 140px;margin-right: 5px"></a-input>
          <a-icon @click="addGroup" type="plus-square" twoToneColor="#00BFFF" theme="twoTone"
                  style="font-size: 25px;margin-right: 10px" title="添加分组"/>
          <a-icon @click="editGroups" type="setting" twoToneColor="#42b983" theme="twoTone"
                  style="font-size: 25px;" title="确认修改分组"/>
        </div>
      </a-col>
      <a-col :span="5" class="tableStyle">
        <a-table
                 :data-source="dataGroup"
                 :columns="columnsGroup"
                 :rowKey="(record)=> record.id"
                 :scroll="{ y: 450 }"
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
      mergeData: [],
      // 列表数据显示
      userIdArr: [],
      usernameArr: [],
      columns,
      columnProvince,
      columnsCityS,
      columnsCityQ,
      columnsCityX,
      columnsGroup,
      // 分组名称
      groupName: '',
      loading: false
    }
  },
  mounted () {
    // 获取模态框树形结构
    this.$get('/dept/list', {deptId: 0}).then(res => {
      this.cityS = res.data
    })
    this.$get('/dept/findProvince').then(res => {
      this.dataProvince = res.data.data
    })
    this.viewGroup()
  },
  methods: {
    // 获取分组
    viewGroup () {
      this.$get('/exchangeGroup/getInformListByMapper').then(res => {
        this.dataGroup = res.data
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
    personnel (param, city) {
      this.$get('/user/getDeptAndUser', param).then((r) => {
        let dataUser = r.data.data.user // 用户信息
        let dept = r.data.data.dept // 市级区县街道数据
        if (city === 1) {
          this.cityQ = dept
        } else if (city === 2) {
          this.cityX = dept
        }
        this.repetition(dataUser)
      })
    },
    // 去除重复
    repetition (dataUser) {
      this.dataSource = []
      for (let i in dataUser) {
        this.userIdArr.push(dataUser[i].userId)
        this.usernameArr.push(dataUser[i].username)
      }
      let hashId = []
      for (let i = 0; i < this.userIdArr.length; i++) {
        if (hashId.indexOf(this.userIdArr[i]) === -1) {
          hashId.push(this.userIdArr[i])
        }
      }
      let hashName = []
      for (let i = 0; i < this.usernameArr.length; i++) {
        if (hashName.indexOf(this.usernameArr[i]) === -1) {
          hashName.push(this.usernameArr[i])
        }
      }
      let result = []
      for (let index in hashId) {
        result.push({userId: hashId[index], username: hashName[index]})
      }
      this.dataSource = result// 获取所有组织机构id
      let userIds = []
      for (let key in this.dataSource) {
        userIds.push(this.dataSource[key].userId)
      }
      this.selectedIdY = userIds // 获取所有的用户的id显示全部勾上
      this.loading = false
    },
    // 省级
    onChangeCityP (p) {
      this.loading = true
      this.selectedIdP = p
      if (this.selectedIdP.length === 0) {
        this.mergeData = []
        this.selectedIdS = []
        this.selectedIdQ = []
        this.selectedIdY = []
        this.selectedIdX = []
        this.loading = false
      } else {
        const param = {
          deptIds: this.selectedIdP
        }
        this.personnel(param, 3)
      }
    },
    // 选择市级
    onChangeCityS (s) {
      this.selectedIdS = s
      this.loading = true
      console.log('市级选中id：', this.selectedIdS)
      if (this.selectedIdS.length === 0) {
        this.dataSource = []
        this.selectedIdS = []
        this.selectedIdQ = []
        this.selectedIdX = []
        this.selectedIdP = []
        this.cityQ = []
        this.cityX = []
        this.loading = false
      } else {
        const param = {
          deptIds: this.selectedIdS
        }
        this.personnel(param, 1)
      }
    },
    // 选择区县
    onChangeCityQ (q) {
      this.selectedIdQ = q
      this.loading = true
      console.log('区县选中id：', this.selectedIdQ)
      if (this.selectedIdQ.length === 0) {
        this.selectedIdS = []
        this.selectedIdQ = []
        this.selectedIdX = []
        this.selectedIdP = []
        this.dataSource = []
        this.cityX = []
        this.loading = false
      } else {
        const param = {
          deptIds: this.selectedIdQ
        }
        this.personnel(param, 2)
      }
    },
    // 选择乡道
    onChangeCityX (x) {
      this.selectedIdX = x
      this.loading = true
      console.log('乡道选中id：', this.selectedIdX)
      if (this.selectedIdX.length === 0) {
        this.selectedIdS = []
        this.selectedIdQ = []
        this.selectedIdP = []
        this.selectedIdX = []
        this.dataSource = []
        this.loading = false
      } else {
        const param = {
          deptIds: this.selectedIdX
        }
        this.personnel(param, 3)
      }
    },
    // 选择人员
    onSelectChange (y) {
      this.selectedIdY = y
      console.log('人员选中id：', this.selectedIdY)
    },
    // 选择分组
    onSelectGroup (g) {
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
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            that.loading = false
          })
        },
        onCancel () {
        }
      })
    },
    // 添加分组
    addEidt (param) {
      if (this.selectedIdY.length === 0) {
        this.$notification.warning({message: '系统提示', description: '请选择传阅人员！', duration: 4})
        return
      } else if (this.groupName === '') {
        this.$notification.warning({message: '系统提示', description: '请输入分组名称！', duration: 4})
        return
      }
      this.$post('/exchangeGroup', param).then(res => {
        if (res.status === 200) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.groupName = ''
          this.dataSource = []
          this.selectedIdS = []
          this.selectedIdQ = []
          this.selectedIdY = []
          this.selectedIdX = []
          this.selectedIdG = []
          this.selectedIdP = []
          this.viewGroup()
        }
      })
    },
    addGroup () {
      let param = {
        groupUserIds: this.selectedIdY,
        name: this.groupName
      }
      this.addEidt(param)
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
      this.$emit('success', this.selectedIdY)
    }
  }
}
</script>

<style scoped>
  .tableStyle{
    margin: 0 17px;
    border: 1px solid #e1e1e1;
    height: 505px;
  }
</style>
