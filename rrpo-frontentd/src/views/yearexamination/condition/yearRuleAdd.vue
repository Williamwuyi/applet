<template>
  <a-modal
    :visible="visible"
    title='新建年度考核'
    okText='确定创建'
    @cancel="() => { onClose() }"
    @ok="() => { create() }"
  >
    <a-form :form="form">
      <a-form-item label='时间'
                   v-bind="formItemLayout"
                   class="tit">
        <a-date-picker format='YYYY' v-model="timeValue" @change="onTimeChange" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
// 表单
const formItemLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 12 }
}
export default {
  name: 'yearRuleAdd',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  data () {
    return {
      formItemLayout,
      dateTime: '',
      timeValue: undefined
    }
  },
  props: ['visible'],
  methods: {
    rest () {
      this.timeValue = undefined
    },
    // 时间
    onTimeChange (date, dateTime) {
      this.dateTime = dateTime
    },
    create () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.$post('/check/year/saveOrUpdate', {yearDate: this.dateTime}).then((res) => {
            if (res.data.message === '操作成功') {
              this.$emit('sucusee')
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message + '操作失败！', duration: 4})
              this.$emit('close')
            }
            this.rest()
          }).catch((err) => {
            console.log(err)
          })
        }
      })
    },
    onClose () {
      this.$emit('close')
      this.rest()
    }
  }
}
</script>

<style scoped>

</style>
