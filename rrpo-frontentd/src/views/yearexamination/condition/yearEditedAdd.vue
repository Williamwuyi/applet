<template>
  <a-modal
    title="新增考核规则"
    :maskClosable="false"
    placement="right"
    :closable="false"
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
    :visible="yearCondVisiable"
    >
    <a-form :form="form">
      <a-form-item label='考核规则' v-bind="formItemLayout">
        <a-textarea
          :autoSize=true
          v-decorator="['content',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入规则" allow-clear />
      </a-form-item>
      <a-form-item label='规则摘要' v-bind="formItemLayout">
        <a-textarea
          :autoSize=true
          v-decorator="['summary',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入摘要" allow-clear />
      </a-form-item>
      <a-form-item label='规则属性' v-bind="formItemLayout">
      <a-radio-group v-decorator="['saveOrDelete',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" >
        <a-radio :value="'加分项'">
          加分项
        </a-radio>
        <a-radio :value="'减分项'">
          减分项
        </a-radio>
      </a-radio-group>
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
      value: '',
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
        console.log(this.value)
        if (!err) {
          this.loading = true
          let newCond = {...this.cond, ...this.form.getFieldsValue()}
          this.$post('/check/menus-year/add', newCond).then(res => {
            if (res.data.status !== 0) {
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
