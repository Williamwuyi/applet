<template>
  <div style="width: 100%">
    <a-row style="padding: 10px 0px">
      <a-col span="8">
        <a-button @click="reportformone"  v-hasPermission="'reward:generate'">生成报表</a-button>
      </a-col>
      <a-col span="16">
        <a-form layout="horizontal" :form="form">
          <a-row>
            <a-col span="3">
              <a-form-item label="期数" :labelCol="{span: 5}" :wrapperCol="{span: 13, offset: 1}">
                <a-input type="number" v-model="perNums"/>
              </a-form-item>
            </a-col>
            <a-col span="8">
              <a-form-item label="创建时间" :labelCol="{span: 5}" :wrapperCol="{span: 13, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
              </a-form-item>
            </a-col>
            <a-col span="6">
              <div style="float: right">
                <a-button type="primary" @click="search">查询</a-button>
                <a-button @click="reset">重置</a-button>
              </div>
            </a-col>
          </a-row>
        </a-form>
      </a-col>
    </a-row>
    <a-table
      :columns="bucolumns"
      :data-source="briefData"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <span slot="perNums" slot-scope="text">{{text}}期</span>
      <template slot="operation" slot-scope="text, record">
        <a-icon type="cloud-download" style="color:#0000FF; margin-right: 3px" @click="download(record)" twoToneColor="#4a9ff5" title="下载"></a-icon>
        <a-icon type="delete" theme="twoTone" twoToneColor="#FF0000" @click="deletes(record)" style="color:#FF0000;margin:0;" v-hasPermission="'reward:dlgenerate'"  title="删除" />
      </template>
    </a-table>
    <!-- 生成报表-->
    <reportForm
      :reportform="reportform"
      @close="hanleedRepclose"
    ></reportForm>
  </div>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
import axios from 'axios'
import store from '../../../store'
import {notification} from 'ant-design-vue'
import reportForm from './reportForm'
export default {
  name: 'bulletin',
  components: {reportForm, RangeDate},
  data () {
    return {
      msg: 'hello world!',
      reportform: false,
      bulletin: [],
      briefData: [],
      form: this.$form.createForm(this),
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      character: {},
      perNums: ''
    }
  },
  computed: {
    bucolumns () {
      return [
        {
          title: '期数',
          dataIndex: 'perNum',
          width: '10%',
          scopedSlots: { customRender: 'perNums' }
        },
        {
          title: '文件名',
          dataIndex: 'oldName'
        },
        {
          title: '创建时间',
          dataIndex: 'time',
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
      this.$get('/file/getPrizeList', this.character).then(res => {
        this.briefData = res.data.data.rows
        this.pagination.total = res.data.data.total
        this.briefData.forEach((key, index) => {
          this.briefData[index].perNum = (key.oldName.match(/简报(\S*)期/)[1])
        })
      })
    },
    // 时间查询
    handleDateChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
    },
    // 生成报表
    reportformone () {
      this.reportform = true
    },
    // 关闭报表页
    hanleedRepclose () {
      this.reportform = false
      this.fach()
    },
    reset () {
      this.loading = true
      // 重置查询参数
      this.character = {}
      // 重新渲染表格
      this.fach()
      // 清空期数选择
      this.perNums = ''
      // 清空时间选择
      this.$refs.createTime.reset()
    },
    // 查询
    search () {
      if (this.perNums === '') {
        this.character.oldName = ''
      } else {
        this.character.oldName = '第' + this.perNums + '期'
      }
      // 通过this.character来进行查询
      this.fach({
        ...this.character
      })
    },
    // 下载
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
    },
    // 删除
    deletes (record) {
      let fileId = record.fileId
      this.$post('/prize/deleteFile', {
        fileId: fileId
      }).then(res => {
        this.loading = true
        this.$message.success('删除成功')
        this.loading = false
        this.reset()
      })
    },
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fach({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    }
  }
}
</script>
<style scoped></style>
