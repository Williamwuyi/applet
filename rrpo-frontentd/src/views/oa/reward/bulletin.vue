<template>
    <a-row style="width: 100%">
      <a-col style="padding: 10px 0px">
        <a-button @click="reportformone" v-hasPermission="'reward:statistics'">生成报表</a-button>
      </a-col>
      <a-col span="24">
        <a-table
          :columns="bucolumns"
          :data-source="briefData"
          :pagination="pagination"
        >
          <template slot="operation" slot-scope="text, record">
            <a-icon type="cloud-download" style="color:#0000FF; margin-right: 3px" @click="download(record)" twoToneColor="#4a9ff5" title="修改"></a-icon>
            <a-icon type="delete" theme="twoTone" twoToneColor="#FF0000" @click="deletes(record)" style="color:#FF0000;margin:0;"  title="删除" />
          </template>
        </a-table>
        <!-- 生成报表-->
        <reportForm
          :reportform="reportform"
          @close="hanleedRepclose"
        ></reportForm>
      </a-col>
    </a-row>
</template>
<script>
import axios from 'axios'
import store from '../../../store'
import {notification} from 'ant-design-vue'
import reportForm from './reportForm'
export default {
  name: 'bulletin',
  components: {reportForm},
  data () {
    return {
      msg: 'hello world!',
      reportform: false,
      bulletin: [],
      briefData: [],
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  computed: {
    bucolumns () {
      return [
        {
          title: '期数',
          dataIndex: 'perNum',
          width: '10%',
          align: 'center'
        },
        {
          title: '文件名',
          dataIndex: 'oldName',
          align: 'center'
        },
        {
          title: '创建时间',
          dataIndex: 'time',
          align: 'center'
        },
        {
          title: '文档类型',
          dataIndex: 'fileId',
          align: 'center'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: '5%',
          scopedSlots: { customRender: 'operation' },
          align: 'center'
        }
      ]
    }
  },
  mounted () {
    this.fach()
  },
  methods: {
    // 渲染表格
    fach () {
      this.loading = true
      this.$get('/file/getPrizeList').then(res => {
        this.briefData = res.data.data.rows
        console.log(this.briefData)
        this.pagination.total = res.data.data.total
        console.log(this.pagination)
        this.briefData.forEach((key, index) => {
          this.briefData[index].perNum = key.oldName.match(/简报(\S*)\(/)[1]
        })
      })
    },
    clone () {
      this.$get('/file/getPrizeList').then(res => {
        console.log(res)
      })
    },
    // 生成报表
    reportformone () {
      this.reportform = true
    },
    deletes (record) {
      this.$post('/file/deleteFile', record.fileId).then(res => {
        console.log(res)
      })
    },
    // 关闭报表页
    hanleedRepclose () {
      this.reportform = false
      this.fach()
    },
    download (record) {
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
<style scoped></style>
