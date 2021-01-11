<template>
  <a-drawer
    title="审核"
    :maskClosable="false"
    width=40%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="informationauditiVisiable"
  >
    <a-form :form="form">
      <a-form-item label='所属街道' v-bind="formItemLayout">
        <a-input v-model="deptName" :read-only="true"/>
      </a-form-item>
      <a-form-item label='群主' v-bind="formItemLayout">
        <a-input v-model="wxUserName" :read-only="true" />
      </a-form-item>
      <a-form-item label='联系方式' v-bind="formItemLayout">
        <a-input v-model="phone" :read-only="true"/>
      </a-form-item>
      <a-form-item label='群名称' v-bind="formItemLayout">
        <a-input v-model="wxName" :read-only="true"/>
      </a-form-item>
      <a-form-item label='建群时间' v-bind="formItemLayout">
        <a-input v-model="dater" :read-only="true"/>
      </a-form-item>
      <a-form-item label='奖励申请表' v-bind="formItemLayout">
        <a-button @click="Shmodal">点击查看</a-button>
      </a-form-item>
    </a-form>
    <a-divider orientation="left">已绑定的群成员详细信息</a-divider>
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
      <template slot="idcard" slot-scope="text, record">
        <a-icon type="idcard" style="color: #2eabff"/>
      </template>
    </a-table>
    <a-divider orientation="left">审核</a-divider>
    <a-form-item
      label="填写您的理由"
      :labelCol="{span: 4}"
      :wrapperCol="{span: 18, offset: 0}">
      <a-input type="textarea" v-model="message"/>
    </a-form-item>
    <div class="drawer-bootom-button">
      <a-button @click="onClose">取消</a-button>
      <a-button style="color: white; background-color: red" @click="handleSubmit(1)" >返回</a-button>
      <a-button @click="handleSubmit(0)" type="primary" :loading="loading">通过</a-button>
    </div>
<!--  表格  -->
    <sh-modal
    :ShModalVisiable="ShModalVisiable"
    @error="errorModal"
    ref="oldSh"
    />
  </a-drawer>
</template>
<script>
import ShModal from './ShModal'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'InformationAudit',
  components: {ShModal},
  props: {
    informationauditiVisiable: {
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
      ShModalVisiable: false,
      qunId: '',
      // 输入框
      deptName: '',
      wxUserName: '',
      phone: '',
      wxName: '',
      dater: '',
      message: '',
      // 搜索
      htt: {},
      // 分页
      pagination: {
        total: 0,
        pageSize: 4, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['4', '10', '20', '50'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      columns: [
        {
          title: '',
          dataIndex: 'idcard',
          scopedSlots: { customRender: 'idcard' }
        },
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
        }
      ]
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.htt = {}
      this.message = ''
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
    getItem (key) {
      this.qunId = key.wxId
      this.deptName = key.deptJc.deptName
      this.wxUserName = key.wxUserName
      this.phone = key.phone
      this.wxName = key.wxName
      this.dater = key.date
      this.getQun()
    },
    Shmodal () {
      this.ShModalVisiable = true
      this.$refs.oldSh.getSh(this.qunId)
    },
    errorModal () {
      this.ShModalVisiable = false
    },
    // 查询群成员
    getQun (parmse = {pageNum: 1, pageSize: 4}) {
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
    // 通过 打回
    handleSubmit (index) {
      if (this.message !== '') {
        this.$post('/wx/sh/shWx', {status: index, massage: this.message, qunId: this.qunId}).then(res => {
          if (res.data.status !== 0) {
            this.reset()
            this.$emit('success')
          } else {
            this.$message.error(res.data.message)
            this.reset()
            this.$emit('close')
          }
        })
      } else {
        this.$notification.error({message: '系统提示理由不能为空', description: '操作失败！', duration: 4})
      }
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
