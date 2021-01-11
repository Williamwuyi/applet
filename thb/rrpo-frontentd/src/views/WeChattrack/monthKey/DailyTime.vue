<template>
  <a-modal
    :visible="visible"
    :confirm-loading="confirmLoading"
    title='选择您要统计的年份'
    okText='确定'
    loading
    @cancel="() => { Onclose() }"
    @ok="() => { create() }"
    style="text-align: center"
  >
    <a-range-picker :key="id" @change="handleDateChange"/>
  </a-modal>
</template>

<script>
export default {
  name: 'DailyTime',
  props: ['visible'],
  data () {
    return {
      timeValue: undefined,
      id: +new Date(),
      yearId: '',
      confirmLoading: false
    }
  },
  methods: {
    rest () {
      this.timeValue = undefined
      // 清空时间选择
      this.id = +new Date()
    },
    create () {
      this.confirmLoading = true
      console.log(this.dateTime)
      this.$emit('check', this.dateTime)
      this.confirmLoading = false
      this.rest()
      this.Onclose()
    },
    Onclose () {
      this.$emit('cancel')
      this.rest()
    },
    // 时间查询
    handleDateChange (value) {
      this.dateTime = value
    }
  }
}
</script>

<style scoped>

</style>
