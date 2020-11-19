<template>
  <a-modal
    :visible="informationAddVisiable"
    :confirm-loading="confirmLoading"
    title='创建群聊'
    okText='确定创建'
    loading
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
  >
    <a-form :form="form">
      <a-form-item label='群名称' v-bind="formItemLayout">
        <a-input v-decorator="['wxName',
                   {rules: [{ required: true, message: '群名称不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='创建人' v-bind="formItemLayout">
        <a-input v-decorator="['wxUserName',
                   {rules: [{ required: true, message: '创建人不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='创建人联系方式' v-bind="formItemLayout">
        <a-input v-decorator="['phone',
                   {rules: [{ required: true, message: '联系方式不能为空'}
                  ]}]"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'InformtionAdd',
  props: {
    informationAddVisiable: {
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
          this.$post('/wx/qun/addOrUpdate', newadd).then(res => {
            if (res.data.status !== 0) {
              this.reset()
              this.confirmLoading = false
              this.$emit('success')
            } else {
              this.confirmLoading = false
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
<style>
</style>
