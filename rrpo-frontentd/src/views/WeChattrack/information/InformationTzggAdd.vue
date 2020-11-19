<template>
  <a-modal
    title="新增公告"
    :maskClosable="false"
    placement="right"
    :closable="false"
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
    :visible="InformationTzggVisiable"
    >
    <a-form :form="form">
      <a-form-item label='公告内容' v-bind="formItemLayout">
        <a-textarea
          :autoSize={maxRows:6}
          v-decorator="['content',
                   {rules: [
                    { required: true, max: 160, message: '值不能为空且最大值不能超过160字符'}
                  ]}]"
          placeholder="请输入公告" allow-clear />
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
  name: 'InformationTzggAdd',
  props: {
    InformationTzggVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this)
    }
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
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = {...this.form.getFieldsValue()}
          this.$post('/wx/notice/save', newCond).then(() => {
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
