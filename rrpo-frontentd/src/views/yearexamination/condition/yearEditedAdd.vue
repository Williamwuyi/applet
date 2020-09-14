<template>
  <a-modal
    title="新增考核规则"
    :maskClosable="false"
    placement="right"
    :closable="false"
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
    :visible="yearCondVisiable"
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
  wrapperCol: { span: 15 }
}
export default {
  name: 'yearConditionAdd',
  props: {
    yearCondVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      cond: {}
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
      this.cond = {}
    },
    onClose () {
      this.reset()
      this.cond = {}
      this.form.resetFields()
      this.$emit('close')
    },
    // 获取yearID
    get (ket) {
      this.cond = ket
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = {...this.cond, ...this.form.getFieldsValue()}
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
