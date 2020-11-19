<template>
  <a-drawer
    title="上报"
    :maskClosable="false"
    width=40%
    placement="right"
    :closable="false"
    :visible="InformTopVisiable"
  >
    <a-form :form="form">
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='群  名' v-bind="formItemLayout">
        <a-input
          v-decorator="['wxName',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='建群时间' v-bind="formItemLayout">
        <a-input
          v-decorator="['date',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
          <a-form-item label='填报单位' v-bind="formItemLayout">
            <a-input
              v-decorator="['fillDeptId',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='群主姓名' v-bind="formItemLayout">
        <a-input
          v-decorator="['wxUserName',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
          <a-form-item label='群主职务' v-bind="formItemLayout">
            <a-input
              v-decorator="['wxUserZw',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='目前群员人数' v-bind="formItemLayout">
        <a-input
          v-decorator="['qunNumber',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
          <a-form-item label='线路' v-bind="formItemLayout">
            <a-select v-decorator="['qunLine']">
              <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label='护路里程' v-bind="formItemLayout">
            <a-input
              v-decorator="['qunKm',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='群活跃程度' v-bind="formItemLayout">
        <a-input
          v-decorator="['qunHyd',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label='奖励申请' v-bind="formItemLayout">
            <a-input
              v-decorator="['award',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='铁路公安是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['ga']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='市州级联络员是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['sz']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='铁路工务是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['gw']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='省考核乡镇专用微信是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['zy']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='铁路电务是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['dw']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
      <a-form-item label='路地临时施工单位是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['sg']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
      <a-form-item label='铁路车务（车站）是否加入' v-bind="formItemLayout">
        <a-radio-group v-decorator="['cw']">
          <a-radio :value="1">
            是
          </a-radio>
          <a-radio :value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <a-form-item label='县市区级联络员是否加入' v-bind="formItemLayout">
            <a-radio-group v-decorator="['xq']">
              <a-radio :value="1">
                是
              </a-radio>
              <a-radio :value="0">
                否
              </a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :md="12" :sm="24">
          <a-form-item label='铁路护路信息是否占90%以上' v-bind="formItemLayout">
            <a-radio-group v-decorator="['hlxx']">
              <a-radio :value="1">
                是
              </a-radio>
              <a-radio :value="0">
                否
              </a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item label='执行湘护路护线组[2016]2号文件情况'>
        <a-textarea
          :autoSize=true
          v-decorator="['file1',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入文件情况" allow-clear />
      </a-form-item>
      <a-form-item label='解决具体问题个数及解决主要问题简要情况'>
        <a-textarea
          :autoSize=true
          v-decorator="['file2',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入简要情况" allow-clear />
      </a-form-item>
      <a-form-item label='主要经验做法'>
        <a-textarea
          :autoSize=true
          v-decorator="['file3',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入经验做法" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-input hidden  v-decorator="['wxId']"/>
        <a-input hidden  v-decorator="['jsId']"/>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-button @click="onClose">取消</a-button>
      <a-popconfirm title="确定上报，一经上报不可修改？" @confirm="handleSubmit" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">上报</a-button>
      </a-popconfirm>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 13 },
  wrapperCol: { span: 11 }
}
export default {
  name: 'yearConditionEdit',
  props: {
    InformTopVisiable: {
      default: false
    },
    parentId: {
      default: ''
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      value: '',
      lineData: [], // 线路
      accessory: null,
      optionsr: []
    }
  },
  mounted () {
    this.$get('dept').then((r) => {
      this.optionsr = r.data.rows.children
    })
    this.getFach()
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    // 获取路段
    getFach () {
      this.$get('/dict/getListTable', {parentId: '1867989d0aaaf82f79b34070cc77d766', pageSize: 50}).then(res => {
        this.lineData = res.data.data.records
      })
    },
    // 获取表格信息
    setFormValues (value) {
      this.form.getFieldDecorator('wxName')
      this.form.setFieldsValue({'wxName': value.wxName})
      this.form.getFieldDecorator('wxUserName')
      this.form.setFieldsValue({'wxUserName': value.wxUserName})
      this.form.getFieldDecorator('qunNumber')
      this.form.setFieldsValue({'qunNumber': value.number})
      this.form.getFieldDecorator('date')
      this.form.setFieldsValue({'date': value.date})
      this.form.getFieldDecorator('wxId')
      this.form.setFieldsValue({'wxId': value.wxId})
      this.qunId = value.wxId
      let that = this
      this.$get('/wx/qun-js/selectById', {wXId: value.wxId}).then(res => {
        if (res.data.data !== null) {
          let fields = ['hlxx', 'xq', 'cw', 'file3', 'file2', 'file1', 'sg', 'dw', 'zy', 'gw', 'sz', 'ga', 'award', 'qunHyd', 'qunKm', 'qunLine', 'wxUserZw', 'fillDeptId', 'jsId']
          Object.keys(res.data.data).forEach((key) => {
            if (fields.indexOf(key) !== -1) {
              that.form.getFieldDecorator(key)
              let obj = {}
              obj[key] = res.data.data[key]
              that.form.setFieldsValue(obj)
            }
          })
        }
      })
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = {...this.form.getFieldsValue()}
          this.$post('/wx/qun-js/saveOrUpdate', newCond).then(res => {
            this.$post('/wx/sh/shWx', {qunId: this.qunId, massage: '发出审核申请', status: 0})
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
