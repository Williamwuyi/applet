<template>
<div>
  <a-modal
    :visible="updateFileVisiable"
    title="上传文件"
    :width= "650"
    :footer="null"
    @cancel="onClose"
    @ok="onOk"
  >
    <a-upload-dragger
      name="file"
      :multiple="false"
      accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z"
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
        单击选择文件或将文件拖到该区域以上传
      </p>
      <p class="ant-upload-hint">
        当前仅支持单文件上传
      </p>
    </a-upload-dragger>
    <a-divider orientation="left">已上传文件</a-divider>
    <a-table :columns="columns"
             :dataSource="fileList"
             :pagination="false"
             :rowKey="record => record.fileId"
             :loading="loading" >
      <template slot="operation" slot-scope="text, record">
        <a-icon class="icon-pointer" type="cloud-download" :style="{ color: 'blue' }" @click="download(record)" title="下载"></a-icon>
        &nbsp;&nbsp;
        <a-popconfirm title="确定删除此附件吗？" okText="确定" cancelText="取消" @confirm="deleteFile(record)">
          <a-icon class="icon-pointer" type="delete" :style="{ color: 'red' }" title="删除"></a-icon>
        </a-popconfirm>
      </template>
    </a-table>
  </a-modal>
</div>
</template>

<script>
import axios from 'axios'
import {notification} from 'ant-design-vue'
import store from '../../../../store'
import {mapMutations} from 'vuex'
export default {
  name: 'UpdateFile',
  props: {
    updateFileVisiable: {
      default: false
    },
    exchangeId: {
      type: String,
      default: ''
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
        refId: '',
        refType: '1'
      },
      visible: false,
      fileList: [],
      loading: false
    }
  },
  mounted () {
  },
  methods: {
    onClose () {
      this.$emit('close')
    },
    appList (exchangeId) {
      this.$post('/file/getAppendixListReply', {refId: exchangeId}).then(res => {
        if (res.data.status) {
          this.fileList = res.data.fileList
        }
      })
    },
    download (record) {
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
    // 删除
    deleteFile (record) {
      this.$post('file/deleteFile', {fileId: record.fileId}).then(res => {
        if (res.data.status) {
          notification.success({
            message: '系统提示',
            description: '文件删除成功！',
            duration: 4
          })
          this.deleteEditorFile(record)
          this.appList(record.refId)
        } else {
          notification.error({
            message: '系统提示',
            description: res.data.message,
            duration: 4
          })
        }
      })
    },
    onClick () {
      // this.$emit('click')
      this.visible = true
    },
    onOk () {
      this.onClose()
    },
    ...mapMutations({
      saveEditorFile: 'file/addAppendixFile',
      deleteEditorFile: 'editor/deleteEditorFile'
    }),
    handleChange (info) {
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
        } else {
          notification.error({
            message: '系统提示',
            description: info.file.response.message,
            duration: 4
          })
        }
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} file upload failed.`)
      }
    }
  },
  // 属性监听
  watch: {
    exchangeId (exchangeId, n) {
      this.data.refId = exchangeId
      this.appList(exchangeId)
    }
  }
}
</script>

<style scoped>

</style>
