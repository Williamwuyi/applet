<template>
  <a-modal
    :visible="proofVisibt"
    :width= "1200"
    title="选择你需要的佐证材料"
    @ok="handleSubmit"
    :maskClosable="false"
    class=""
    @cancel="() => { onClose() }"
  >
    <a-tabs default-active-key="1" @change="callback" style="text-align: center">
          <a-tab-pane key="1" tab="自定义佐证材料上传">
            <div >
            <a-upload-dragger
              name="file"
              :multiple="false"
              style="height: 40%"
              accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z"
              :action="action"
              :headers="headers"
              :remove="remove"
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
              <template slot="operations" slot-scope="text, record">
                <a  style="color: red"  @click="adeleteFile(record)" >删除</a>
              </template>
            </a-table>
      </a-tab-pane>
      <a-tab-pane key="2" tab="站内佐证材料上传" force-render>
            <a-table
              :data-source="dataSource1"
              :columns="columns1"
              :rowKey="(record)=> record.id"
              :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
              :pagination="pagination">
              <template slot="operationr" slot-scope="text, record">
                <a @click="glzz(record)">关联本条规则</a>
              </template>
            </a-table>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script>
import {notification} from 'ant-design-vue'
import store from '@/store'
export default {
  name: 'yearConditionProof',
  props: ['proofVisibt'],
  data () {
    return {
      // 自定义文件上传
      action: this.$constURL + 'file/uploadFile',
      headers: {
        Authentication: this.$store.state.account.token
      },
      fileList: [],
      // 文件列表
      dataSource: [],
      asde: '',
      msgtext: '切换到其他',
      msgtextflag: true,
      gradeId: '',
      // 上传
      prmas: {
        yearId: '',
        menusId: '',
        deptId: store.state.account.user.deptId
      },
      // 详细
      dataSource1: [],
      selectedRowKeys1: [],
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
    // 信息互递
    // this.fetch()
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
        title: '操作',
        dataIndex: 'operations',
        scopedSlots: { customRender: 'operations' }
      }]
    },
    //  详细
    columns1 () {
      return [
        {
          title: '文件名',
          dataIndex: 'oldName'
        }, {
          title: '上传时间',
          dataIndex: 'time'
        }, {
          title: '操作',
          dataIndex: 'operationr',
          scopedSlots: { customRender: 'operationr' }
        }
      ]
    }
  },
  methods: {
    onClose () {
      this.asde = ''
      this.fileList = []
      this.$emit('close')
    },
    handleSubmit () {
      this.$emit('success')
      this.fileList = []
    },
    handleSubmit1 () {
      this.prmas.xXhd = this.selectedRowKeys1
      this.prmas.filedS = this.fileList
      this.$post('/check/grade/addZz', this.prmas).then(res => {
        this.getFile()
      })
    },
    getyearId (item) {
      this.prmas.gradeId = item.gradeId
      this.gradeId = item.gradeId
      this.getFile()
      this.fetch({deptId: item.deptId, yearId: item.yearId})
    },
    glzz (record) {
      this.$post('/check/grade/addZz', {filedS: [record.fileId], gradeId: this.gradeId}).then(res => {
        this.$message.success(`${res.data.message}`)
        this.getFile()
      })
    },
    callback (key) {
    },
    //  请求绑定的文件
    getFile () {
      this.$get('/asYear/list', {gradeId: this.gradeId}).then(res => {
        this.dataSource = res.data.data
      })
    },
    // 删除
    adeleteFile (record) {
      let that = this
      that.$confirm({
        title: '是否删除，一经删除永远不会恢复?',
        centered: true,
        onOk () {
          that.$delete('/asYear/delete', {ids: record.id}).then(() => {
            that.$message.success('删除成功')
            that.getFile()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    // 自定义文件上传
    handleChange (info) {
      const status = info.file.status
      if (status === 'done') {
        if (info.file.response.status) {
          let uploadFile = info.file.response.file.fileId
          this.fileList.push(uploadFile)
          notification.success({
            message: '系统提示',
            description: '文件上传成功！',
            duration: 4
          })
          this.handleSubmit1()
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
    // 文件删除
    remove (info1) {
      if (info1.response.status) {
        this.fileList = this.fileList.filter(res => {
          return res !== info1.response.file.fileId
        })
      }
    },
    // 详细选择
    onSelectChange1 (selectedRowKeys) {
      this.selectedRowKeys1 = selectedRowKeys
    },
    //  请求
    fetch (pamase) {
      this.$get('/asYear/fileList', pamase).then(res => {
        this.dataSource1 = res.data.data
      })
    }
  }
}
</script>

<style scoped>

</style>
