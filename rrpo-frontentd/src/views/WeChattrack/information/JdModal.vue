<template>
  <a-modal
    :visible="JdModalVisiable"
    title='当前进度'
    :width= "950"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
  >
<!--    :status="state == 0 ? 'finish': 'error'"-->
    <a-steps :direction="num > 5 ? 'vertical': 'horizontal'">
      <a-step :status="item.status === 0 ? 'finish': 'error'"  v-for="(item, index) in arr" :title="item.dept.deptName" :description="item.massage" :key="index"/>
    </a-steps>
  </a-modal>
</template>
<script>
export default {
  name: 'JdModal',
  props: ['JdModalVisiable'],
  data () {
    return {
      jiedao: '',
      state: '',
      num: 0,
      arr: []
    }
  },
  methods: {
    onClose () {
      this.$emit('error')
      this.arr = []
    },
    getJd (key) {
      this.$get('/wx/sh/list', {qunid: key.wxId}).then(res => {
        if (res.data.data.records.length > 0) {
          let newArr = res.data.data.records
          this.arr = newArr
          this.num = newArr.length - 1
          this.state = this.arr[this.num].status
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
