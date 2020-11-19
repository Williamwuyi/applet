<template>
  <a-modal
    :visible="markIsshow"
    :confirm-loading="confirmLoading"
    title='选择限定时间'
    okText='确定'
    loading
    @cancel="() => { Onclose() }"
    @ok="() => { create() }"
    style="text-align: center"
  >
    <a-date-picker v-model="timeValue"
                   format="YYYY-MM-DD"
                   :disabled-date="disabledDate"
                   @change="onTimeChange" />
  </a-modal>
</template>

<script>
import moment from 'moment'
export default {
  name: 'yearChoose',
  props: ['markIsshow'],
  data () {
    return {
      confirmLoading: false,
      timeValue: undefined,
      markTime: {},
      dateTime: null
    }
  },
  methods: {
    moment,
    rest () {
      // 清空时间
      this.timeValue = undefined
    },
    create () {
      this.confirmLoading = true
      this.$post('/check/num/deadDate', {...this.markTime, date: this.dateTime}).then(res => {
        this.$emit('check', this.yearId)
        this.confirmLoading = false
        this.rest()
      })
    },
    // 禁用时间
    disabledDate (current) {
      // Can not select days before today and today
      return current && current < moment().endOf('day')
    },
    getnumId (key) {
      this.markTime.numId = key
    },
    Onclose () {
      this.$emit('cancel')
      this.rest()
    },
    // 时间
    onTimeChange (date, shting) {
      this.dateTime = date
    }
  }
}
</script>

<style scoped>

</style>
