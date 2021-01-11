<template>
  <div id="app">
    <a-spin :spinning="uploadSpinning" tip="文件上传中...">
      <div v-if="isUpload">
        <a-upload-dragger
          name="file"
          :multiple="false"
          accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z, .mp4"
          :action="action"
          :headers="headers"
          :data="data"
          :showUploadList="false"
          @change="handleChange"
        >
          <p class="ant-upload-drag-icon">
            <a-icon type="inbox" />
          </p>
          <p class="ant-upload-text">
            单击选择文件或将文件拖到该区域上传
          </p>
          <p class="ant-upload-hint">
            单个上传文件大小限制为100M
          </p>
        </a-upload-dragger>
      </div>
      <a-divider orientation="left">已上传附件列表</a-divider>
      <a-table id="appendix-table" :columns="columns"
               :dataSource="fileList"
               :pagination="false"
               :rowKey="record => record.fileId"
               :loading="loading" >
        <template slot="operation" slot-scope="text, record" style="text-align: left">
          <a-icon class="icon-pointer" type="cloud-download" :loading="loading" :style="{ color: 'blue' }" @click="download(record)" title="下载"></a-icon>
          <span v-if="preview(record, true)">
          <a-icon class="icon-pointer" type="eye" :style="{ color: 'green' }" @click="preview(record, false)" title="预览"/>
        </span>
          <span v-if="isUpload">
          <a-popconfirm title="确定删除此附件吗？" okText="确定" cancelText="取消" @confirm="deleteFile(record)">
            <a-icon class="icon-pointer" type="delete" :style="{ color: 'red' }" title="删除"></a-icon>
          </a-popconfirm>
        </span>
        </template>
      </a-table>
    </a-spin>
    <a-modal v-model="visible" :width="700" :footer="null" :centered="true" @cancel="cancel">
      <div class="previewContent">
        <div v-if="previewType === 'video'">
          <a-spin :spinning="spinning">
            <video id="video" controls="controls"
                   controlslist = "nodownload"
                   width="100%" height="100%"
                   :src="previewURL" style="margin-bottom: -5px;">
            </video>
          </a-spin>
        </div>
        <div v-else-if="previewType === 'image'">
          <a-spin :spinning="spinning">
            <img :src="previewURL"/>
          </a-spin>
        </div>
        <div v-else>
          暂不支持其他文件预览
        </div>
      </div>
    </a-modal>
  </div>
</template>
<script>
import axios from 'axios'
import store from '../../store'
import {notification} from 'ant-design-vue'
import {mapMutations} from 'vuex'
import fileSuffixTypeUtil from '../../utils/fileSuffixTypeUtil'

export default {
  name: 'Appendix',
  props: {
    refType: {
      required: true,
      type: String
    },
    refId: {
      type: String,
      default: '-1'
    },
    isUpload: {
      type: Boolean,
      default: true
    },
    unique: {
      required: true,
      type: String
    }
  },
  computed: {
    columns () {
      return [{
        title: '文件名',
        dataIndex: 'oldName'
      }, {
        title: '上传时间',
        dataIndex: 'time'
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  data () {
    return {
      action: this.$constURL + 'file/uploadFile',
      headers: {
        Authentication: this.$store.state.account.token
      },
      data: {
        refId: this.refId === '-1' ? '' : this.refId,
        refType: this.refType
      },
      fileList: [],
      previewFile: null,
      loading: false,
      visible: false,
      previewType: null,
      previewURL: null,
      spinning: true,
      uploadSpinning: false
    }
  },
  created () {
    this.fetch()
  },
  methods: {
    cancel () {
      let type = fileSuffixTypeUtil.matchFileSuffixType(this.previewFile.oldName)
      if (type === 'video') {
        let video = document.getElementById('video')
        video.pause()
      }
    },
    preview (record, isTypeValidate) {
      let type = fileSuffixTypeUtil.matchFileSuffixType(record.oldName)
      if (isTypeValidate) {
        return type === 'video' || type === 'image'
      } else {
        this.previewFile = record
        switch (type) {
          case 'video':
            this.previewType = type
            break
          case 'image':
            this.previewType = type
            break
        }
        this.visible = true
        if (record.accessUrl === this.previewURL) {

        } else {
          axios({
            method: 'get',
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
            this.previewURL = window.URL.createObjectURL(blob)
            this.spinning = false
          })
        }
      }
    },
    download (record) {
      this.loading = true
      axios({
        method: 'get',
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
        this.loading = false
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
    },
    deleteFile (record) {
      this.$post('file/deleteFile', {fileId: record.fileId}).then(res => {
        if (res.data.status) {
          notification.success({
            message: '系统提示',
            description: '文件删除成功！',
            duration: 4
          })
          if (this.refId === '-1') {
            this.deleteEditorFile(record)
            this.fileList = this.fileList.filter(item => item.fileId !== record.fileId)
          } else {
            this.fetch()
          }
        } else {
          notification.error({
            message: '系统提示',
            description: res.data.message,
            duration: 4
          })
        }
      })
    },
    handleChange (info) {
      this.uploadSpinning = true
      const status = info.file.status
      if (status === 'done') {
        if (info.file.response.status) {
          let uploadFile = info.file.response.file
          this.fileList.push(uploadFile)
          notification.success({
            message: '系统提示',
            description: '文件上传成功！',
            duration: 4
          })
          this.checkRef(info.file.response.file)
          this.uploadSpinning = false
        } else {
          notification.error({
            message: '系统提示',
            description: info.file.response.message,
            duration: 4
          })
          this.uploadSpinning = false
        }
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} 文件上传失败，请重试！.`)
        this.uploadSpinning = false
      }
    },
    checkRef (file) {
      if (this.refId === '-1') {
        this.saveEditorFile(file)
      }
    },
    ...mapMutations({
      saveEditorFile: 'file/addAppendixFile',
      deleteEditorFile: 'file/deleteAppendixFile'
    }),
    fetch () {
      if (this.refId !== '-1') {
        this.$post('file/getAppendixList', {refId: this.refId}).then(res => {
          if (res.data.status) {
            this.fileList = res.data.fileList
          }
        })
      } else {
        this.fileList = []
      }
    }
  },
  watch: {
    unique: {
      handler (n, o) {
        if (n !== o) {
          this.fetch()
        }
      }
    }
  }
}
</script>
<style lang="less" scoped>
  #appendix-table {
    z-index: 0;
  }
  .icon-pointer {
    cursor: pointer;
    margin: 5px;
  }
  .previewContent {
    text-align: center;
    max-width: 700px;
    margin-top: 15px;
  }
  img {
    width:100%;
    height:100%;
    background-size: cover;
  }
  /deep/ .ant-table-placeholder {
    z-index: 0;
  }
</style>
