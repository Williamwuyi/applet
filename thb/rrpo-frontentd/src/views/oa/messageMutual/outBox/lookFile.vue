<template>
  <a-modal
    :visible="lookFileVisiable"
    title="查看附件"
    :width= "650"
    :footer="null"
    @cancel="onClose"
    @ok="onOk"
  >
    <a-table :columns="columns"
             :dataSource="fileList"
             :pagination="false"
             :rowKey="record => record.fileId"
             :loading="loading" >
      <template slot="operation" slot-scope="text, record">
        <a-icon class="icon-pointer" type="cloud-download" :loading="loading" :style="{ color: 'blue' }" @click="download(record)" title="下载"></a-icon>
      </template>
    </a-table>
  </a-modal>
</template>

<script>
import axios from 'axios'
import store from '../../../../store'
import {notification} from 'ant-design-vue'
export default {
  name: 'lookFile',
  props: {
    lookFileVisiable: {
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
    setId (id) {
      this.$post('/file/getAppendixListForEX', {refId: id}).then(res => {
        if (res.data.status) {
          this.fileList = res.data.fileList
        }
      })
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
    onClick () {
      // this.$emit('click')
      this.visible = true
    },
    onOk () {
      this.onClose()
    }
  }
}
</script>

<style scoped>

</style>
