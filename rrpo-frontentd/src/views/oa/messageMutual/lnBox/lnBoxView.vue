<template>
  <a-modal
    title="信息详情"
    :maskClosable="false"
    width=45%
    :closable="true"
    :footer="null"
    @cancel="close"
    @ok="modelSubmit"
    :visible="boxViewVisiable"
    >
    <div style="height: 500px;overflow: auto">
    <div style="font-size: 17px;">
      <p style="border-bottom:1px solid #d9d9d9;padding-bottom: 10px;padding-bottom: 10px">
        <a-icon type="file-word" style="font-size: 17px;margin-right: 5px" />
        <b>标题：</b>{{title}}
      </p>
      <p style="border-bottom:1px solid #d9d9d9;padding-bottom: 10px">
        <a-icon type="idcard" style="font-size: 17px;margin-right: 5px"/>
        <b>发送人：</b>{{creatUser}}
      </p>
      <p style="border-bottom:1px solid #d9d9d9;padding-bottom: 10px">
        <a-icon type="hourglass" style="font-size: 17px;margin-right: 5px"/>
        <b>接收时间：</b>{{releaseTime}}
      </p>
      <p style="border-bottom:1px solid #d9d9d9;padding-bottom: 10px">
        <a-icon type="tag" style="font-size: 17px;margin-right: 5px"/>
        <b>接收附件：</b>
        <a-button @click="lookFileButton">
          <a-icon type="cloud" />查看附件
        </a-button>
      </p>
      <!--      <div style="width: 630px;height: 32px;">-->
      <!--        <a-button @click="lookFileButton">-->
      <!--          <a-icon type="cloud-download" />查看附件-->
      <!--        </a-button>-->
      <!--      </div>-->
      <a-icon type="notification" style="font-size: 17px;margin-right: 5px"/>
      <b>接收内容：</b>
      <div style="border-top:1px solid #d9d9d9;
            padding: 10px 15px;
            line-height:35px;
            text-indent:30px;
            height: 250px;">
        <span v-html="contentReceive" style="white-space: normal;"></span>
      </div>
    </div>
    <div v-if="isReads !== 2">
      <a-icon type="sound" style="font-size: 17px;margin-right: 5px"/><b>回复内容:</b>
      <span style="color: #E21918;margin-left: 20px">{{mustTime}}</span>
      <editor height="400px" :readonly=false ref="editor" refType="7"></editor>
      <a-icon type="tags" style="font-size: 17px;margin-right: 5px"/><b style="font-size: 17px;">回复附件:</b>
      <appendix refId="-1" refType="7" :is-upload="true" unique="this.randomId"></appendix>
    </div>
    </div>
    <div v-if="isReads !== 2" style="text-align: right;margin-top: 10px;padding-top: 10px;border-top: 1px solid #c3c3c3">
      <a-button @click="close">取消</a-button>
      <a-button @click="modelSubmit" type="primary">回复</a-button>
    </div>
    <look-file
      :lookFileVisiable="lookFileVisiable"
      :exchangeId="exchangeId"
      ref="oldLook"
      @close="lookFileClose"
    />
  </a-modal>
</template>

<script>
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
import lookFile from './lookFile'
export default {
  name: 'lnBoxView',
  components: {Editor, lookFile, Appendix}, // 局部注册
  props: {
    boxViewVisiable: {
      default: false
    },
    randomId: {
      default: '-1'
    }
  },
  data () {
    return {
      content: '<strong>初始化编辑器值</strong>',
      loading: false,
      lookFileVisiable: false,
      isReads: 0,
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
  component: {
    editor () {
      return this.$refs.myQuillEditor.quill
    }
  },
  methods: {
    close () {
      this.$emit('close')
    },
    setFormValues (user) {
      console.log('获取收件信息', user)
      this.title = user.title
      this.isReads = user.isRead
      this.contentReceive = user.content
      this.exchangeId = user.id
      this.releaseTime = user.releaseTime
      this.creatUser = user.creatUser
      this.opinion = user.opinion
      this.claimTime = user.claimTime
      let date = new Date().valueOf()
      let time = new Date(user.claimTime).valueOf()
      if (user.isTime === 'true') {
        if (date > time) {
          this.mustTime = '(请在' + user.claimTime + '之前务必回复信息) * 超时回复'
        } else {
          this.mustTime = '(请在' + user.claimTime + '之前务必回复信息)'
        }
      } else {
        this.mustTime = ''
      }
      // this.set()
    },
    lookFileButton () {
      this.lookFileVisiable = true
      this.$refs.oldLook.setId(this.exchangeId)
    },
    lookFileClose () {
      this.lookFileVisiable = false
    },
    get () {
      // 读取编辑器HTML内容
      this.opinion = this.$refs.editor.content
    },
    set () {
      // 设置编辑器内容
      this.$nextTick(function () {
        // 此时已经渲染完成
        this.$refs.editor.content = this.content
      })
    },
    // 获取上传文件id
    getFileList () {
      let filesList = this.$store.state.file.appendixList
      let fileIds = []
      if (filesList.length !== 0 || filesList !== null) {
        for (let i = 0; filesList.length > i; i++) {
          fileIds.push(filesList[i].fileId)
        }
        console.log('获取到文件id', fileIds)
      }
      this.fileIds = fileIds
    },
    modelSubmit () {
      if (this.isReads !== 2) {
        this.handleSubmit()
      } else {
        this.$emit('close')
      }
    },
    handleSubmit () {
      this.get() // 获取文本内容
      this.getFileList() // 获取上传文件id
      const params = {
        exchangeId: this.exchangeId,
        fileIds: this.fileIds,
        isRead: 2,
        opinion: this.opinion
      }
      this.$put('/exchange/receive', params).then(res => {
        if (res.status === 200) {
          this.$emit('success')
        }
      })
    }
  }
}
</script>
<style scoped>
</style>
