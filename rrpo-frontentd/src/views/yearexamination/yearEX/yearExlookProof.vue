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
            :pagination="false">
            <template slot="status" slot-scope="text, record">
              <a-tag v-if="record.zzStatus === 0" color="#87d068">已通过</a-tag>
              <a-tag v-else-if="record.zzStatus === null" color="#DEE1E6" >未审核</a-tag>
              <a-tag v-else-if="record.zzStatus === 1" color="#f50">未通过</a-tag>
              <a-tag v-else-if="record.zzStatus === 2" color="#f50">未审核完</a-tag>
            </template>
            <template slot="operations" slot-scope="text, record">
              <a-icon  type="import" style="color: #722ed1" @click="repulseFile(record)" v-hasPermission="'year:repulse'" title="打回"></a-icon>
              <a-icon  type="check-circle" style="color: #52c41a; margin: 0 5px"  @click="passFile(record)" v-hasPermission="'year:pass'" title="通过"></a-icon>
              <a-icon class="icon-pointer" type="cloud-download" :style="{ color: 'blue' }" v-hasPermission="'year:download'" @click="download(record)" title="下载"></a-icon>
              <a-icon  type="delete" style="color: red"  @click="adeleteFile(record)" v-hasPermission="'year:remove'" title="删除"></a-icon>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="2" tab="站内佐证材料详细" force-render>
          <!--   折叠框   -->
          <a-collapse v-model="asde" @change="collapChange" ref="zhedie"  :bordered="false">
            <a-collapse-panel key="1" header="信息互递">
              <div style="text-align: right;margin-bottom: 3px">
                <a-button v-if="!addIsShow" @click="QtClick">{{Qtmsg}}</a-button>
                <a-button v-if="addIsShow" @click="addClick" v-hasPermission="'year:add'">新增信息</a-button>
              </div>
              <a-table
                :data-source="dataSource1"
                :columns="columns1"
                :rowKey="(record)=> record.exchange.id"
                :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
                :pagination="pagination">
                <a slot="titlename" slot-scope="text,record" style="color:#6290FF" @click="lookStatus(record)">{{ text }}</a>
                <template slot="status" slot-scope="text, record">
                  <a-tag v-if="record.status === 0" color="#87d068">已通过</a-tag>
                  <a-tag v-else-if="record.status === null" color="#DEE1E6" >未审核</a-tag>
                  <a-tag v-else-if="record.status === 1" color="#f50">未通过</a-tag>
                </template>
                <template slot="operation" slot-scope="text, record">
                  <a-icon  type="import" style="color: #722ed1" @click="repulseXxhd(record)" v-hasPermission="'year:repulse'" title="打回"></a-icon>
                  <a-icon  type="check-circle" style="color: #52c41a; margin: 0 5px"  @click="passXxhd(record)" v-hasPermission="'year:pass'" title="通过"></a-icon>
                  <a-icon  type="delete" style="color: red" @click="adeleteXxhd(record)" v-hasPermission="'year:remove'" title="删除"></a-icon>
                </template>
              </a-table>
            </a-collapse-panel>
            <a-collapse-panel key="2" header="一事一奖" :disabled="false">
              <div>信息互递</div>
            </a-collapse-panel>
            <a-collapse-panel key="3" header="通知公告" :disabled="false">
              <div>信息互递</div>
            </a-collapse-panel>
            <a-collapse-panel key="4" header="公共信息" :disabled="false">
              <div>信息互递</div>
            </a-collapse-panel>
          </a-collapse>
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
      // 文件列表
      dataSource: [],
      // 文件上传
      action: this.$constURL + 'file/uploadFile',
      headers: {
        Authentication: this.$store.state.account.token
      },
      // 信息互递
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
        dataIndex: 'oldName'
      }, {
        title: '上传时间',
        dataIndex: 'time'
      }, {
        title: '状态',
        dataIndex: 'zz_status',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '操作',
        dataIndex: 'operations',
        width: 100,
        scopedSlots: { customRender: 'operations' }
      }]
    },
    //  信息互递
    columns1 () {
      return [
        {
          title: '标题',
          dataIndex: 'exchange.title',
          scopedSlots: { customRender: 'titlename' }
        },
        {
          title: '创建时间',
          dataIndex: 'exchange.creatTime'
        },
        {
          title: '状态',
          dataIndex: 'status',
          scopedSlots: { customRender: 'status' }
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
      this.parame.xXhd = this.selectedRowKeys1
      console.log(this.parame)
      this.$post('/check/grade/addZz', this.parame).then(res => {
        console.log(res)
        this.$emit('success')
        this.reset()
      })
    },
    callback (key) {
    },
    // 参数设置
    getyearDate (item) {
      console.log(item)
      this.gradeId = item.gradeId
      this.parame = {
        gradeId: item.gradeId
      }
      this.getFile()
    },
    // 折叠款变化
    collapChange (key) {
      // 1 查询信息互递文件
      if (key.length > 0) {
        key.forEach(re => {
          // 1 信息互递 2一事一奖 3通知公告 4公共信息
          this.fetch({type: re * 1, gradeId: this.gradeId})
        })
      }
    },
    //  请求绑定的文件
    getFile () {
      console.log(11)
      this.$post('/file/getAppendixList', {refId: this.gradeId}).then(res => {
        console.log(res.data.fileList)
        this.dataSource = res.data.fileList
      })
    },
    // 站内详细请求
    fetch (pamase) {
      this.$get('/check/grade-zz/list', pamase).then(res => {
        console.log(res)
        if (res.data.message !== '无对象') {
          let newData = res.data.data
          // 分页;
          // const pagination = { ...this.pagination }
          // pagination.total = newData.total
          this.dataSource1 = newData
          newData.forEach(t => {
            this.selectedRowKeys1.push(t.exchange.id)
          })
          // this.pagination = pagination
        } else {
          this.$message.error('当前未绑定任何项目')
        }
      })
    },
    // 信息互递的操作
    // 信息互递查看
    lookStatus (item) {
      console.log(item)
      this.NoticelookVisiable = true
      this.$refs.nolook.setFormValues(item.exchange)
    },
    // 信息互递通过
    passXxhd (record) {
      let pamase = {
        status: 0,
        gradeId: this.gradeId,
        zzId: record.gradeZzId
      }
      let that = this
      that.$confirm({
        title: `确定${record.exchange.title}通过?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatus', pamase).then(res => {
            that.fetch({type: 1, gradeId: that.gradeId})
          })
        }
      })
    },
    // 信息互递打回
    repulseXxhd (record) {
      let pamase = {
        status: 1,
        gradeId: this.gradeId,
        zzId: record.gradeZzId
      }
      let that = this
      that.$confirm({
        title: `确定${record.exchange.title}打回?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatus', pamase).then(res => {
            that.fetch({type: 1, gradeId: that.gradeId})
          })
        }
      })
    },
    // 信息互递新增
    addClick () {
      this.addIsShow = false
      this.getXxhd({status: 1, type: 1})
    },
    QtClick () {
      if (this.msgtextflag) {
        this.getXxhd({status: 2, type: 1})
        this.msgtextflag = false
        this.Qtmsg = '考核'
      } else {
        this.getXxhd({status: 1, type: 1})
        this.msgtextflag = true
        this.Qtmsg = '其他'
      }
    },
    // 信息互递选择
    onSelectChange1 (selectedRowKeys) {
      this.selectedRowKeys1 = selectedRowKeys
    },
    getXxhd (params) {
      // 重新请求
      this.$get('/check/grade-zz/getListCZ', params).then(res => {
        let params = []
        let newData = res.data.data
        // 改变数据结构
        newData.forEach(t => {
          params.push({exchange: t})
        })
        this.dataSource1 = params
      })
    },
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
    },
    // 信息互递的删除操作
    adeleteXxhd (record) {
      let params = {
        list: record.gradeZzId
      }
      let that = this
      that.$confirm({
        title: '是否删除?',
        centered: true,
        onOk () {
          that.$delete('/check/grade-zz/delete/', params).then(() => {
            that.$message.success('删除成功')
            that.reset()
            that.fetch({type: 1, gradeId: that.gradeId})
          })
        }
      })
    },
    // 文件操作
    // 文件通过
    passFile (record) {
      console.log(record)
      let pamase = {
        status: 0,
        gradeId: this.gradeId,
        fileId: record.fileId
      }
      let that = this
      that.$confirm({
        title: `确定${record.oldName}通过?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatus', pamase).then(res => {
            that.getFile()
          })
        }
      })
    },
    // 文件打回
    repulseFile (record) {
      let pamase = {
        status: 1,
        gradeId: this.gradeId,
        fileId: record.fileId
      }
      let that = this
      that.$confirm({
        title: `确定${record.oldName}未通过?`,
        centered: true,
        onOk () {
          that.$post('/check/grade/checkStatus', pamase).then(res => {
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
          that.$post('/file/deleteFile', {fileId: record.fileId}).then(res => {
            that.getFile()
          })
        }
      })
    },
    // 文件上传
    handleChange (info) {
      console.log(info)
      const status = info.file.status
      if (status === 'done') {
        // 文件id上传到后端
        this.parame.filedS = []
        this.parame.filedS.push(info.file.response.file.fileId)
        console.log(this.parame)
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

</style>
