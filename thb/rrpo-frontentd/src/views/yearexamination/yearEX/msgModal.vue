<template>
  <a-modal
    :visible="visible"
    title='填写描述'
    okText='确定'
    @cancel="() => { Onclose() }"
    @ok="() => { create() }"
  >
    <a-form layout='vertical' :form="form">
      <a-form-item label='文件夹名称'>
        <a-input
          v-decorator="[
              'name',
              {
                rules: [{ required: true, message: '文件夹名称不能未空' }]
              }
            ]"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
export default {
  name: 'msgModal',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  props: ['visible', 'parentId'],
  data () {
    return {
      confirmLoading: false
    }
  },
  methods: {
    create () {
      let newShare = {
        ...this.form.getFieldsValue(),
        parentId: this.parentId,
        mark: 1
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          this.$post('/privateFile', newShare).then(res => {
            // 清空表单
            this.form.resetFields()
            this.confirmLoading = false
            this.$emit('create')
          })
        }
      })
    },
    Onclose () {
      // 清空表单
      this.form.resetFields()
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>

</style>
