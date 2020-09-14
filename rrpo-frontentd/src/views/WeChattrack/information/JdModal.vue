<template>
  <a-modal
    :visible="JdModalVisiable"
    title='当前进度'
    :width= "850"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
  >
<!--    wait process finish error-->
    <a-steps :current="num" :status="state == 0 ? 'finish': 'error'">
      <a-step :title="jiedao" description="发出审核申请"/>
      <a-step v-for="(item, index) in arr" :title="item.dept.deptName" :description="item.massage" :key="index"/>
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
    },
    getJd (key) {
      console.log(key)
      this.jiedao = key.deptJc.deptName
      this.$get('/wx/sh/list', {qunid: key.wxId}).then(res => {
        console.log(res)
        let newArr = res.data.data.records
        this.arr = newArr
        this.num = newArr.length
        this.state = this.arr[this.num - 1].status
      })
    }
  }
}
</script>

<style scoped>
</style>
