<template>
  <a-drawer
    title="绑定群成员"
    :maskClosable="false"
    width=40%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="informationbindVisiable"
    >
    <a-form :form="form">
      <!--      v-model="newNoticea.title"-->
      <a-form-item label='成员名称' v-bind="formItemLayout">
        <a-input v-decorator="['name',
                   {rules: [{ required: true, message: '成员名称不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='联系方式' v-bind="formItemLayout">
        <a-input v-decorator="['phone',
                   {rules: [{ required: true, message: '联系方式不能为空'},
                   { pattern: new RegExp( /^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/ ), message: '请输入正确格式'},
                   { max: 11, message: '长度不能超过16个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='身份证号码' v-bind="formItemLayout">
        <a-input v-decorator="['idCar',
                   {rules: [{ required: true, message: '身份证号码不能为空'},
                   { max: 18, message: '长度不能超过18个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='身份' v-bind="formItemLayout">
        <a-input v-decorator="['sf',  {rules: [{ required: true, message: '身份不能为空'}]}]"/>
      </a-form-item>
    </a-form>
    <a-divider orientation="left">已绑定的成员</a-divider>
<!--    查询-->
    <a-row >
      <a-col :md="12" :sm="24" >
        <a-form-item
          label="成员名"
          :labelCol="{span: 5}"
          :wrapperCol="{span: 18, offset: 0}">
          <a-input v-model="htt.name"/>
        </a-form-item>
      </a-col>
      <a-col :md="12" :sm="24" >
              <span style="margin-top: 8px; display: block">
              <a-button size="small" type="primary" @click="search">查询</a-button>
              <a-button size="small" style="margin-left: 8px" @click="resetTable">重置</a-button>
        </span>
      </a-col>
    </a-row>
<!--  表格  -->
    <a-table
      :data-source="dataSource"
      :columns="columns"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template slot="operation" slot-scope="text, record">
        <a-icon  type="setting" style="margin-right: 3px" theme="twoTone"  twoToneColor="#4a9ff5"  @click="aedit(record)"  title="修改"></a-icon>
        <a-icon  type="delete" style="color: red"  @click="adeleteQun(record)"  title="删除"></a-icon>
      </template>
    </a-table>
    <div class="drawer-bootom-button">
        <a-button @click="onClose">取消</a-button>
       <a-button @click="handleSubmit" type="primary"  :loading="loading">提交</a-button>
    </div>
    <!-- 修改-->
    <qun-edit
      @success="handlecomEdit"
      @close="hanleEditclose"
      :informationEditVisiable="informationEditVisiable"
      ref="newedit"
    />
  </a-drawer>
</template>
<script>
import qunEdit from './qunEdit'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'informationbind',
  components: {qunEdit},
  props: {
    informationbindVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      form: this.$form.createForm(this),
      formItemLayout,
      dataSource: [],
      informationEditVisiable: false,
      qunId: '',
      // 搜索
      htt: {},
      // 分页
      pagination: {
        total: 0,
        pageSize: 6, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['6', '10', '20', '50'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      columns: [
        {
          title: '成员名称',
          dataIndex: 'name'
        }, {
          title: '联系方式',
          dataIndex: 'phone'
        }, {
          title: '身份证号码',
          dataIndex: 'idCar'
        }, {
          title: '身份',
          dataIndex: 'sf'
        }, {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.htt = {}
      // 清空表单
      this.form.resetFields()
    },
    resetTable () {
      this.htt = {}
      this.getQun()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    // 查询
    search () {
      this.htt.qunId = this.qunId
      this.getQun(this.htt)
    },
    getQunId (key) {
      console.log(key)
      this.qunId = key.wxId
      this.getQun()
    },
    // 删除
    adeleteQun (record) {
      let that = this
      that.$confirm({
        title: '是否删除，一经删除永远不会恢复?',
        centered: true,
        onOk () {
          that.$delete('/wx/user/deleteS', {wxUserIdS: record.wxUserId, qunId: record.qunId}).then(() => {
            that.$message.success('删除成功')
            that.getQun()
          })
        }
      })
    },
    // 修改群成员
    aedit (record) {
      this.informationEditVisiable = true
      this.$refs.newedit.getData(record)
    },
    handlecomEdit () {
      this.informationEditVisiable = false
      this.getQun()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleEditclose () {
      this.informationEditVisiable = false
    },
    // 查询群成员
    getQun (parmse = {pageNum: 1, pageSize: 6}) {
      this.loading = true
      parmse.qunId = this.qunId
      this.$get('/wx/user/List', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 绑定群成员
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newadd = {...this.form.getFieldsValue()}
          newadd.qunId = this.qunId
          console.log(newadd)
          this.$post('/wx/user/addOrUpdate', newadd).then(() => {
            this.reset()
            this.getQun()
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    // 分页
    handleTableChange (pagination, filters, sorter) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.htt.pageNum = pagination.current
      this.htt.pageSize = pagination.pageSize
      this.getQun({
        ...this.htt,
        ...filters
      })
    }
  }
}
</script>
<style  lang="less" scoped>
</style>
