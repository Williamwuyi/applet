<template>
  <a-modal
    :visible="GroupAddVisiable"
    :confirm-loading="confirmLoading"
    title='创建模块'
    okText='确定创建'
    loading
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
  >
    <a-form :form="form">
      <a-form-item label='模块名称' v-bind="formItemLayout">
        <a-input v-decorator="['name',
                   {rules: [{ required: true, message: '模块名称不能为空'}
                  ]}]"/>
      </a-form-item>
<!--      <a-form-item label='分数' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['num',-->
<!--                   {rules: [{ required: false, message: '分数不能为空'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
    </a-form>
  </a-modal>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'YearGroupAdd',
  props: {
    GroupAddVisiable: {
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
          this.confirmLoading = true
          this.loading = true
          let newadd = {...this.form.getFieldsValue()}
          this.$post('/check/menus/addOrUpdate', newadd).then(() => {
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
