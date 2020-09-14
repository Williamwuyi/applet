<template>
  <a-modal
  :visible="NoticelookVisiable"
  title="公告"
  :width= "1200"
  :footer="null"
  style="height: 800px; overflow: auto"
  @cancel="() => { onClose() }"
  >
  <div class="modalShow">
    <div class="lookHeader">
      <h2>{{title}}</h2>
      <div class="msegsize">调整字体:
        <span @click="bingClick(0)">大</span>
        <span @click="bingClick(1)">中</span>
        <span @click="bingClick(2)">小</span>
        <div id="issuer">
          <span style="color:#000;">发布人: {{createUser}}</span>
<!--          <span style="color:#000;">发布时间{{createTime}}</span>-->
        </div>
        </div>
    </div>
  <hr />
  <div class="middle" v-html="createneirong" style="white-space: normal"></div>
  <hr />
  </div>
  </a-modal>
</template>
<script>
export default {
  name: 'Noticelook',
  props: {
    NoticelookVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      createTime: '',
      createUser: '',
      title: '',
      createneirong: ''
    }
  },
  methods: {
    reset () {
      this.loading = false
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues (user) {
      this.createTime = user.createTime
      this.createUser = user.creatUser
      this.title = user.title
      this.createneirong = user.content
    },
    bingClick (num) {
      var middle = document.querySelector('.middle')
      switch (num) {
        case 0: middle.style.fontSize = '36px'
          break
        case 1: middle.style.fontSize = '24px'
          break
        case 2: middle.style.fontSize = '16px'
          break
      }
    }
  }
}
</script>
<style scoped>
.modalShow {
  background-color: #fff;
  min-height: 500px;
}
h2 {
  text-align: center;
  color: #149EC5;
  font-size: 28px;
}
.middle {
  width: 80%;
  margin: 20px auto;
}
.floot {
  height: 100px;
  width: 100%;
  text-align: right;
}
.msegsize {
text-align: center;
font-size: 16px;
background-color: #F7F7F7;
  position: relative;
}
.msegsize span {
  margin-top: 10px;
  margin-left: 10px;
  color: #249DD8;
  cursor:pointer
}
#issuer {
  position: absolute;
  right: 0;
  top: 0;
}
</style>
