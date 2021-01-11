<template>
    <div style="width: 690px; text-align: center;position: absolute;top: 30px; z-index: 9999">
      <div style="opacity: 0.9;padding: 10px">
        <a-row style="margin-bottom: 10px">
          <a-col :span="4">基础工作加分项</a-col>
          <a-col :span="2"><a-tag color="#f50">
            {{msg.jcgzX}}
          </a-tag>
          </a-col>
          <a-col :span="4">基础工作减分项</a-col>
          <a-col :span="2"><a-tag color="#2db7f5">
            {{jf}}
          </a-tag></a-col>
          <a-col :span="4">基础工作折算项</a-col>
          <a-col :span="2"><a-tag color="#87d068">
            {{zs}}
          </a-tag></a-col>
          <a-col :span="4">工作效果得分项</a-col>
          <a-col :span="2"><a-tag color="#108ee9">
            {{gzxg}}
          </a-tag></a-col>
        </a-row>
    </div>
    </div>
</template>

<script>
export default {
  name: 'fuchuang',
  props: ['numArr'],
  data () {
    return {
      msg: this.numArr
    }
  },
  computed: {
    // 工作效果
    gzxg () {
      if (this.msg.gzxg > 0) {
        if (this.msg.gzxg < 40) {
          return this.msg.gzxg
        } else {
          return 40
        }
      } else {
        return 0
      }
    },
    // 工作项减分
    jf () {
      if (this.msg.jcgzY < 0) {
        return this.msg.jcgzY
      } else {
        return 0
      }
    },
    // 折算分
    zs () {
      console.log(this.msg.jcgzX)
      if (this.msg.jcgzX > 40) {
        return ((this.msg.jcgzX - 40) / 15).toFixed(2)
      } else {
        return 0
      }
    }
  }
}
</script>

<style scoped>

</style>
