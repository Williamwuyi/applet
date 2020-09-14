<template>
  <a-modal
    :visible="informationEditVisiable"
    :confirm-loading="confirmLoading"
    title='修改群聊'
    okText='确定修改'
    loading
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
  >
    <a-form :form="form">
      <a-form-item label='群成员名' v-bind="formItemLayout">
        <a-input v-decorator="['name',
                   {rules: [{ required: true, message: '群名称不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='联系方式' v-bind="formItemLayout">
        <a-input v-decorator="['phone',
                   {rules: [{ required: true, message: '创建人不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='身份证号码' v-bind="formItemLayout">
        <a-input v-decorator="['idCar',
                   {rules: [{ required: true, message: '联系方式不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='身份' v-bind="formItemLayout">
        <a-input v-decorator="['sf',
                   {rules: [{ required: true, message: '联系方式不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-input v-decorator="['qunId']" hidden />
      <a-input v-decorator="['wxUserId']" hidden />
    </a-form>
  </a-modal>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'InformationEdit',
  props: {
    informationEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      confirmLoading: false
    }
  },
  methods: {
    reset () {
      this.loading = false
      // 清空表单
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
    },
    getData (item) {
      console.log(item)
      this.form.getFieldDecorator('phone')
      this.form.setFieldsValue({'phone': item.phone})
      this.form.getFieldDecorator('name')
      this.form.setFieldsValue({'name': item.name})
      this.form.getFieldDecorator('idCar')
      this.form.setFieldsValue({'idCar': item.idCar})
      this.form.getFieldDecorator('qunId')
      this.form.setFieldsValue({'qunId': item.qunId})
      this.form.getFieldDecorator('wxUserId')
      this.form.setFieldsValue({'wxUserId': item.wxUserId})
      this.form.getFieldDecorator('sf')
      this.form.setFieldsValue({'sf': item.sf})
    },
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (!(/^1[3456789]\d{9}$/.test(values.phone))) {
            this.$message.error('请输入正确的手机号码')
            return
          }
          this.confirmLoading = true
          this.loading = true
          let newadd = {...this.form.getFieldsValue()}
          console.log(newadd)
          this.$post('/wx/user/addOrUpdate', newadd).then(() => {
            this.reset()
            this.confirmLoading = false
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
<style>
</style>
