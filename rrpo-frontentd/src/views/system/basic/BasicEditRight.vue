<template>
  <a-drawer
   title="修改字典项"
   width=650
   plscement="right"
   :maskClosable="false"
   :closeable="false"
   :visible="basicEditRightVisible"
   @close="onClose"
  >
    <a-form :form="form">
      <a-form-item label='字典项名称' v-bind="formItemLayout">
        <a-input style="width: 100%" placeholder="请输入字典项名称"
                 v-decorator="['fieldName',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='系数占比' v-bind="formItemLayout" v-if="count">
        <a-input style="width: 100%" placeholder="请输入系数占比名称"
                 v-decorator="['coefficient',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label="线路性质" width=650 v-bind="formItemLayout" v-if="route">
        <a-select  style="width: 100%" placeholder="请选择线路性质" :allowClear="true"
                   v-decorator="['nature',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]">
          <a-select-option value="义务护路队员">
            义务护路队员
          </a-select-option>
          <a-select-option value="高铁志愿者">
            高铁志愿者
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='公里数' v-bind="formItemLayout" v-if="route">
        <a-input style="width: 100%" placeholder="请输入公里数"
                 v-decorator="['revenue',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='值' v-bind="formItemLayout">
        <a-input-number style="width: 100%" placeholder="请输入值"
                 v-decorator="['value',
                   {rules: [
                    { required: false, message: '不能为空'}
                  ]}]"/>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确认放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :lading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 17 }
}
export default {
  name: 'basicEditRight',
  props: {
    basicEditRightVisible: {
      default: false
    }
  },
  data () {
    return {
      count: false,
      route: false,
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      basic: {}
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.basic = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues ({...basic}) {
      // console.log(basic)
      let fields = ['fieldName', 'coefficient', 'nature', 'revenue', 'value']
      Object.keys(basic).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = basic[key]
          this.form.setFieldsValue(obj)
        }
      })
      this.basic.dictId = basic.dictId
      if (basic.parentId === '96b76029ac74b1ad1c73642b6ddb9dab') {
        this.count = true
      } else {
        this.count = false
      }
      if (basic.parentId === '1867989d0aaaf82f79b34070cc77d766') {
        this.route = true
      } else {
        this.route = false
      }
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          let basic = {...this.basic, ...this.form.getFieldsValue()}
          // console.log(basic)
          this.$post('/dict/updateTable', basic).then((res) => {
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
