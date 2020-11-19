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
      this.form.getFieldDecorator('saveOrDelete')
      this.form.setFieldsValue({'saveOrDelete': value.saveOrDelete})
      this.form.getFieldDecorator('content')
      this.form.setFieldsValue({'content': value.content})
      this.form.getFieldDecorator('summary')
      this.form.setFieldsValue({'summary': value.summary})
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
