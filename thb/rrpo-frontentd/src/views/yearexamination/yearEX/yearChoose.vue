<template>
  <a-modal
    :visible="visible"
    :confirm-loading="confirmLoading"
    title='选择您要考核的年份'
    okText='确定考核'
    loading
    @cancel="() => { Onclose() }"
    @ok="() => { create() }"
    style="text-align: center"
  >
    <a-cascader  change-on-select
                 style="width: 105px;"
                 @change="onChangeTime"
                 :options="options"
                 placeholder="未选择"
                 ref="time"
    />
  </a-modal>
</template>

<script>
export default {
  name: 'yearChoose',
  props: ['visible'],
  data () {
    return {
      //  状态下拉
      options: [],
      yearId: '',
      confirmLoading: false
    }
  },
  methods: {
    rest () {
      // 清空时间选择
      this.$refs.time.sValue = []
    },
    create () {
      this.confirmLoading = true
      this.$emit('check', this.yearId)
      this.confirmLoading = false
      this.rest()
      this.Onclose()
    },
    Onclose () {
      this.$emit('cancel')
      this.rest()
    },
    // 下拉函数
    onChangeTime (value) {
      // this.yearex = value
      if (value.length > 0) {
        this.yearId = value[0]
      } else {
        this.yearId = ''
      }
    }
  },
  watch: {
    visible () {
      if (this.visible) {
        // 日期
        this.$get('/check/year/list').then(res => {
          let data = res.data.data
          this.options = []
          data.forEach(t => {
            this.options.push({value: t.yearId, label: t.yearDate})
          })
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
