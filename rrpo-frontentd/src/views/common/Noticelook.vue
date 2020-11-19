<template>
  <a-modal
  :visible="NoticelookVisiable"
  title="公告"
  :width= "1200"
  :footer="null"
  @cancel="() => { onClose() }"
  >
  <div class="modalShow" style="min-height: 600px; overflow: auto">
    <div class="lookHeader">
      <h2>{{title}}</h2>
      <div class="msegsize">调整字体:
        <span @click="bingClick(0)">大</span>
        <span @click="bingClick(1)">中</span>
        <span @click="bingClick(2)">小</span>
        </div>
    </div>
  <hr />
  <div class="middle" v-html="createneirong" style="white-space: normal"></div>
    <div><span style="font-weight: 800">相关附件:</span>
      <ul>
        <li v-for="item in arr" :key="item.fileId"><span style="margin-right: 20px">{{item.oldName}}</span><a @click="DpwnFile(item)">下载</a></li>
      </ul>
    </div>
    <hr />
  </div>
  </a-modal>
</template>
<script>
import axios from 'axios'
import store from '../../store'
import {notification} from 'ant-design-vue'

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
      arr: [],
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
      this.$post('/file/getAppendixList', {refId: user.id}).then(res => {
        this.arr = res.data.fileList
      })
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
    },
    // 下载
    DpwnFile (record) {
      axios({
        method: 'post',
        url: this.$constURL + 'file/downloadFile',
        headers: {
          'Authentication': store.state.account.token
        },
        params: {
          fileId: record.fileId
        },
        responseType: 'blob'
      }).then(res => {
        const content = res.data
        const blob = new Blob([content])
        if ('download' in document.createElement('a')) {
          const link = document.createElement('a')
          link.download = record.oldName
          link.style.display = 'none'
          link.href = URL.createObjectURL(blob)
          document.body.appendChild(link)
          link.click()
          URL.revokeObjectURL(link.href)
          document.body.removeChild(link)
        } else {
          navigator.msSaveBlob(blob, record.oldName)
        }
        notification.success({
          message: '系统提示',
          description: '文件下载成功！',
          duration: 4
        })
      }).catch(() => {
        notification.error({
          message: '系统提示',
          description: '文件传输失败！',
          duration: 4
        })
      })
    }
  }
}
</script>
<style scoped>
  li {
    list-style: none;
  }
.modalShow {
  background-color: #fff;
  min-height: 500px;
}
h2 {
  text-align: center;
  color: #149EC5;
  font-size: 28px;
  line-height: 50px;
}
.middle {
  width: 80%;
  margin: 20px auto;
  line-height: 200%;
}
.floot {
  height: 100px;
  width: 100%;
  text-align: right;
}
.msegsize {
text-align: center;
font-size: 16px;
  line-height: 35px;
  margin-bottom: 20px;
background-color: #F7F7F7;
  position: relative;
}
.msegsize span {
  margin-top: 10px;
  margin-left: 10px;
  color: #149EC5;
  cursor:pointer
}
#issuer {
  position: absolute;
  right: 0;
  top: 0;
}
</style>
