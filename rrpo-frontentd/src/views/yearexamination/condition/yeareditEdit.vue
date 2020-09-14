<template>
  <a-modal
    title="修改考核规则"
    :maskClosable="false"
    placement="right"
    :closable="false"
    :width="800"
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
    :visible="yearEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='考核规则' v-bind="formItemLayout">
        <a-textarea
          :autosize=true
          v-decorator="['content',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入规则" allow-clear />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'yearConditionEdit',
  props: {
    yearEditVisiable: {
      default: false
    },
    parentId: {
      default: ''
    }
  },
  data () {
    return {
      loading: false,
      newData: '',
      formItemLayout,
      form: this.$form.createForm(this),
      value: '',
      accessory: null
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
    // 获取表格信息
    setFormValues (value) {
      this.form.getFieldDecorator('content')
      this.form.setFieldsValue({'content': value.content})
      this.newData = {
        menusYearId: value.menusYearId,
        parentId: value.parentId,
        yearId: value.yearId
      }
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = {...this.form.getFieldsValue(), ...this.newData}
          console.log(newCond)
          this.$post('/check/menus-year/add', newCond).then(() => {
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
