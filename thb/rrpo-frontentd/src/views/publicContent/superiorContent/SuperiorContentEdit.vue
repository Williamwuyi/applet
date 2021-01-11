<template>
  <a-modal
    :visible="visible"
    title='修改'
    okText='确定修改'
    @cancel="() => { $emit('cancel') }"
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
  name: 'SharedContentEdit',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  props: ['visible'],
  data () {
    return {
      id: ''
    }
  },
  methods: {
    getName (item) {
      this.form.getFieldDecorator('name')
      this.form.setFieldsValue({'name': item.name})
      this.id = item.id
    },
    create () {
      let newShare = {
        ...this.form.getFieldsValue(),
        id: this.id,
        mark: 1
      }
      this.$put('/manageFile', newShare).then(res => {
        this.$emit('create')
      })
    }
  }
}
</script>

<style scoped>

</style>
