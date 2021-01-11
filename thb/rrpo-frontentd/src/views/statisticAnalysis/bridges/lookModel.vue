<template>
  <a-modal
    :visible="visible"
    title='查看'
    :width="1200"
    :footer="null"
    @cancel="() => { Onclose() }"
  >
    <a-descriptions  bordered>
      <a-descriptions-item label="编号">
        {{newData.bm}}
      </a-descriptions-item>
      <a-descriptions-item label="地州市">
        {{newData.dzs}}
      </a-descriptions-item>
      <a-descriptions-item label="县市区">
        {{newData.xsq}}
      </a-descriptions-item>
      <a-descriptions-item label="乡镇街道">
        {{newData.xz}}
      </a-descriptions-item>
      <a-descriptions-item label="座落村组" :span="2">
        {{newData.cd}}
      </a-descriptions-item>
      <a-descriptions-item label="公安处">
        {{newData.ga}}
      </a-descriptions-item>
      <a-descriptions-item label="派出所">
        {{newData.pcs}}
      </a-descriptions-item>
      <a-descriptions-item label="警务区">
        {{newData.jwq}}
      </a-descriptions-item>
      <a-descriptions-item label="工务段">
        {{newData.gwd}}
      </a-descriptions-item>
      <a-descriptions-item label="公里数">
        {{newData.gls}}
      </a-descriptions-item>
      <a-descriptions-item label="修建方位">
        {{newData.cd}}
      </a-descriptions-item>
      <a-descriptions-item label="修建长度" :span="3">
        {{newData.cd}}
      </a-descriptions-item>
      <a-descriptions-item label="修建原因及周边社情" :span="3">
        {{newData.yy}}
      </a-descriptions-item>
      <a-descriptions-item label="交通事故情况" :span="3">
        {{newData.jtsgqk}}
      </a-descriptions-item>
      <a-descriptions-item label="路地已开展工作">
        {{newData.kzqk}}
      </a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script>
export default {
  name: 'lookModel',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  props: ['visible'],
  data () {
    return {
      confirmLoading: false,
      newData: {}
    }
  },
  methods: {
    setLook (record) {
      this.newData = record
      console.log(record)
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
