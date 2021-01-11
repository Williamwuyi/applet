<template>
  <a-modal
    :visible="prooflookVisibt"
    :width= "1200"
    title="选择你需要的佐证材料"
    @ok="handleSubmit"
    :maskClosable="false"
    @cancel="() => { onClose() }"
  >
    <div style="min-height: 500px">
      <a-tabs  @change="callback" style="text-align: center;">
        <a-tab-pane key="1" tab="自定义佐证材料详细">
          <div>
            <a-upload-dragger
              v-hasPermission="'year:add'"
              name="file"
              :multiple="false"
              accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z"
              :action="action"
              :headers="headers"
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
          </div>
          <a-divider orientation="left">已上传文件</a-divider>
          <a-table
            :data-source="dataSource"
            :columns="columns"
            :loading="loading"
            :pagination="false">
            <template slot="status" slot-scope="text, record">
              <a-tag v-if="record.file.zzStatus === 0" color="#87d068">已通过</a-tag>
              <a-tag v-else-if="record.file.zzStatus === null" color="#DEE1E6" >未审核</a-tag>
              <a-tag v-else-if="record.file.zzStatus === 1" color="#f50">未通过</a-tag>
              <a-tag v-else-if="record.file.zzStatus === 2" color="#f50">未审核完</a-tag>
            </template>
            <template slot="operations" slot-scope="text, record">
              <a  style="color: #722ed1" @click="repulseFile(record)" v-hasPermission="'year:repulse'">返回</a>
              <a  style="color: #52c41a; margin: 0 5px"  @click="passFile(record)" v-hasPermission="'year:pass'" >通过</a>
              <a  :style="{ color: 'blue' }" v-hasPermission="'year:download'" @click="download(record)">下载</a>
              <a  style="color: red"  @click="adeleteFile(record)" v-hasPermission="'year:remove'" >删除</a>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="2" tab="站内佐证材料详细" force-render>
              <a-table
                :data-source="dataSource1"
                :columns="columns1"
                :rowKey="(record)=> record.fileId"
                :pagination="pagination">
                <template slot="operation" slot-scope="text, record">
                  <a style="color: #52c41a; margin: 0 5px"  @click="glzz(record)">添加佐证</a>
                </template>
              </a-table>
        </a-tab-pane>
      </a-tabs>
      <!-- 查看 -->
      <noticelook
        :NoticelookVisiable="NoticelookVisiable"
        @close="hanleNoticelookclose"
        ref="nolook"
      />
    </div>
  </a-modal>
</template>

<script>
import Noticelook from '../../common/Noticelook'
import {notification} from 'ant-design-vue'
import axios from 'axios'
import store from '../../../store'
export default {
  name: 'yearExlookProof',
  props: ['prooflookVisibt'],
  components: {Noticelook},
  data () {
    return {
      // 折叠框
      asde: '',
      loading: false,
      yearId: '',
      deptId: '',
      // 文件列表
      dataSource: [],
      // 文件上传
      action: this.$constURL + 'file/uploadFile',
      headers: {
        Authentication: this.$store.state.account.token
      },
      // 详细
      dataSource1: [],
      selectedRowKeys1: [],
      msgtextflag: true,
      Qtmsg: '其他',
      addIsShow: true,
      // 穿进来参数
      parame: {},
      // look
      NoticelookVisiable: false,
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  mounted () {
  },
  computed: {
    // 自定义文件上传
    columns () {
      return [{
        title: '文件名',
        dataIndex: 'file.oldName'
      }, {
        title: '上传时间',
        dataIndex: 'file.time'
      }, {
        title: '状态',
        dataIndex: 'file.zzStatus',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '操作',
        dataIndex: 'operations',
        scopedSlots: { customRender: 'operations' }
      }]
    },
    //  所有材料
    columns1 () {
      return [
        {
          title: '文件名',
          dataIndex: 'oldName',
          scopedSlots: { customRender: 'titlename' }
        },
        {
          title: '上传时间',
          dataIndex: 'time'
        }, {
          title: '操作',
          dataIndex: 'operation',
          width: 100,
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  methods: {
    onClose () {
      this.asde = ''
      this.reset()
      this.$emit('close')
    },
    // 重置
    reset () {
      this.addIsShow = true
      this.msgtextflag = true
      this.dataSource1 = []
      this.dataSource = []
      this.selectedRowKeys1 = []
    },
    // 提交
    handleSubmit () {
      // 信息互递
      this.$emit('success')
      // this.parame.xXhd = this.selectedRowKeys1
      this.reset()
      // this.$post('/check/grade/addZz', this.parame).then(res => {
      //   console.log(res)
      // })
    },
    callback (key) {
    },
    glzz (record) {
      this.$post('/check/grade/addZz', {filedS: [record.fileId], gradeId: this.gradeId}).then(res => {
        this.$message.success(`${res.data.message}`)
        this.getFile()
      })
    },
    // 参数设置
    getyearDate (item) {
      this.gradeId = item.gradeId
      this.deptId = item.deptId
      this.yearId = item.yearId
      this.fetch({deptId: item.deptId, yearId: item.yearId})
      this.parame = {
        gradeId: item.gradeId
      }
      this.getFile()
    },
    //  请求绑定的文件
    getFile () {
      this.$get('/asYear/list', {gradeId: this.gradeId}).then(res => {
        this.dataSource = res.data.data
      })
    },
    // 站内详细请求
    fetch (pamase) {
      this.$get('/asYear/fileList', pamase).then(res => {
        this.dataSource1 = res.data.data
      })
    },
    // 信息互递的操作
    // 信息互递查看
    lookStatus (item) {
      this.NoticelookVisiable = true
      this.$refs.nolook.setFormValues(item.exchange)
    },
    // 信息互递选择
    onSelectChange1 (selectedRowKeys) {
      this.selectedRowKeys1 = selectedRowKeys
    },
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
    },
    // 文件操作
    // 文件通过
    passFile (record) {
      let pamase = {
        status: 0,
        // gradeId: this.gradeId,
        zzId: record.file.fileId
      }
      let that = this
      that.$confirm({
        title: `确定${record.file.oldName}通过?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatusZZ', pamase).then(res => {
            that.getFile()
          })
        }
      })
    },
    // 文件打回
    repulseFile (record) {
      let pamase = {
        status: 1,
        // gradeId: this.gradeId,
        zzId: record.file.fileId
      }
      let that = this
      that.$confirm({
        title: `确定${record.file.oldName}未通过?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatusZZ', pamase).then(res => {
            that.getFile()
          })
        }
      })
    },
    // 文件列表的删除操作
    adeleteFile (record) {
      let that = this
      that.$confirm({
        title: '是否删除?',
        centered: true,
        onOk () {
          that.$delete('/asYear/delete', {ids: record.id}).then(res => {
            that.getFile()
          })
        }
      })
    },
    // 文件上传
    handleChange (info) {
      const status = info.file.status
      if (status === 'done') {
        // 文件id上传到后端
        this.parame.filedS = []
        this.parame.filedS.push(info.file.response.file.fileId)
        this.$post('/check/grade/addZz', this.parame).then(res => {
          this.getFile()
        })
        if (info.file.response.status) {
          notification.success({
            message: '系统提示',
            description: '文件上传成功！',
            duration: 4
          })
          this.getFile()
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
    },
    // 文件下载
    download (record) {
      this.loading = true
      axios({
        method: 'get',
        url: this.$constURL + 'file/downloadFile',
        headers: {
          'Authentication': store.state.account.token
        },
        params: {
          fileId: record.file.fileId
        },
        responseType: 'blob'
      }).then(res => {
        this.loading = false
        const content = res.data
        const blob = new Blob([content])
        if ('download' in document.createElement('a')) {
          const link = document.createElement('a')
          link.download = record.file.oldName
          link.style.display = 'none'
          link.href = URL.createObjectURL(blob)
          document.body.appendChild(link)
          link.click()
          URL.revokeObjectURL(link.href)
          document.body.removeChild(link)
        } else {
          navigator.msSaveBlob(blob, record.file.oldName)
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

</style>
