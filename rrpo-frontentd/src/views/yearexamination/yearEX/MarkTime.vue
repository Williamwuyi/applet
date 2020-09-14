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
    <a-date-picker v-model="timeValue"   @change="onTimeChange" />
  </a-modal>
</template>

<script>
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
    rest () {
      // 清空时间
      this.timeValue = undefined
    },
    create () {
      this.confirmLoading = true
      console.log(this.dateTime)
      this.$post('/check/num/deadDate', {...this.markTime, date: this.dateTime}).then(res => {
        this.$emit('check', this.yearId)
        this.confirmLoading = false
        this.rest()
      })
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
