<template>
  <a-modal
    :visible="visible"
    title='新建年度考核'
    okText='确定创建'
    :width="700"
    @cancel="() => { onClose() }"
    @ok="() => { create() }"
  >
    <a-form :form="form">
      <a-form-item label='时间'
                   v-bind="formItemLayout"
                   class="tit">
        <a-date-picker format='YYYY' show-time v-model="timeValue" @change="onChangeTime"  @ok="onTimeChange" />
      </a-form-item>
      <a-form-item label='考核模块选择'  v-bind="formItemLayout">
        <a-checkbox-group  @change="onChange">
          <a-checkbox :value="item.standardId" v-for="item in CheckArr" :key="item.standardId">
            {{item.name}}
          </a-checkbox>
        </a-checkbox-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
// 表单
const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 }
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
      menusIdS: [],
      timeValue: undefined,
      CheckArr: []
    }
  },
  props: ['visible'],
  mounted () {
    this.getCheck()
  },
  methods: {
    rest () {
      this.timeValue = undefined
    },
    // 时间
    onChangeTime (value, dateString) {
      this.dateTime = dateString
    },
    onTimeChange (date) {
      var d = new Date(date)
      this.dateTime = d.getFullYear() + ''
    },
    // 多选框数据渲染
    getCheck () {
      this.$get('/check/menus/list').then(res => {
        this.CheckArr = res.data.data
      })
    },
    // 多选框
    onChange (even) {
      this.menusIdS = even
    },
    create () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.$post('/check/year/saveOrUpdate', {yearDate: this.dateTime, menusIdS: this.menusIdS}).then((res) => {
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
