<template>
  <a-drawer
    title="查看信息"
    :maskClosable="false"
    width=700
    placement="right"
    :closable="true"
    @close="close"
    :visible="viewVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <div style="font-size: 17px">
      <p><a-icon type="file-word" style="font-size: 17px;margin-right: 5px" /><b>标题：</b>{{title}}</p>
      <p><a-icon type="idcard" style="font-size: 17px;margin-right: 5px"/><b>发送人：</b>{{creatUser}}</p>
      <p><a-icon type="hourglass" style="font-size: 17px;margin-right: 5px"/><b>接收时间：</b>{{releaseTime}}</p>
      <div style="width: 630px;height: 32px;">
        <a-icon type="contacts" style="font-size: 17px;margin-right: 5px"/><b>接收内容：</b>
        <a-button @click="lookFileButton" style="float:right;">
          <a-icon type="cloud-download" />查看附件
        </a-button>
      </div>
      <div style="border:1px solid #d9d9d9;
            width: 630px;
            padding: 10px 15px;
            line-height:35px;
            text-indent:30px;
            height: 250px;
            overflow: auto;">
        <span v-html="contentReceive" style="white-space: normal;"></span>
      </div>
    </div>
    <look-file
      :lookFileVisiable="lookFileVisiable"
      :exchangeId="exchangeId"
      ref="oldLook"
      @close="lookFileClose"
    />
  </a-drawer>
</template>

<script>
import lookFile from './lookFile'
export default {
  name: 'InRoadView',
  components: {lookFile}, // 局部注册
  props: {
    viewVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      lookFileVisiable: false,
      opinion: '',
      contentReceive: '',
      releaseTime: '',
      claimTime: '',
      fileIds: [],
      mustTime: '',
      creatUser: '',
      exchangeId: '',
      title: ''
    }
  },
  methods: {
    close () {
      this.$emit('close')
    },
    setFormValues (user) {
      console.log('获取收件信息', user)
      this.title = user.title
      this.contentReceive = user.content
      this.exchangeId = user.id
      this.releaseTime = user.releaseTime
      this.creatUser = user.creatUser
      this.opinion = user.opinion
      this.claimTime = user.claimTime
    },
    lookFileButton () {
      this.lookFileVisiable = true
      this.$refs.oldLook.setId(this.exchangeId)
    },
    lookFileClose () {
      this.lookFileVisiable = false
    }
  }
}
</script>
<style scoped>
</style>
