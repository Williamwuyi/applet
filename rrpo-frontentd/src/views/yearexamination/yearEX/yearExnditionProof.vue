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
              :showUploadList="true"
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
      </a-tab-pane>
      <a-tab-pane key="2" tab="站内佐证材料上传" force-render>
        <!--   折叠框   -->
        <a-collapse v-model="asde" @change="collapChange"  :bordered="false">
          <a-collapse-panel key="1"  header="信息互递">
            <div style="text-align: right; margin-bottom: 8px"><a-button @click="Xxcut">{{msgtext}}</a-button></div>
            <a-table
              :data-source="dataSource1"
              :columns="columns1"
              :rowKey="(record)=> record.id"
              :rowSelection="{selectedRowKeys: selectedRowKeys1, onChange: onSelectChange1}"
              :pagination="pagination">
              <template slot="operation" slot-scope="text, record">
              </template>
            </a-table>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="一事一奖" :disabled="false">
            <div style="text-align: right; margin-bottom: 8px"><a-button >切换到其他</a-button></div>
            <div>信息互递</div>
          </a-collapse-panel>
          <a-collapse-panel key="3" header="通知公告" :disabled="false">
            <div style="text-align: right; margin-bottom: 8px"><a-button>切换到其他</a-button></div>
            <div>信息互递</div>
          </a-collapse-panel>
          <a-collapse-panel key="4" header="公共信息" :disabled="false">
            <div style="text-align: right; margin-bottom: 8px"><a-button>切换到其他</a-button></div>
            <div>信息互递</div>
          </a-collapse-panel>
        </a-collapse>
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
      asde: '',
      msgtext: '切换到其他',
      msgtextflag: true,
      // 上传
      prmas: {
        yearId: '',
        menusId: '',
        deptId: store.state.account.user.deptId
      },
      // 信息互递
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
        dataIndex: 'oldName'
      }, {
        title: '上传时间',
        dataIndex: 'time'
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    },
    //  信息互递
    columns1 () {
      return [
        {
          title: '标题',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titlename' }
        },
        {
          title: '创建时间',
          dataIndex: 'creatTime'
        },
        {
          title: '发送时间',
          dataIndex: 'releaseTime'

        }
      ]
    }
  },
  methods: {
    onClose () {
      this.asde = ''
      this.$emit('close')
    },
    handleSubmit () {
      this.prmas.xXhd = this.selectedRowKeys1
      this.prmas.filedS = this.fileList
      this.$post('/check/grade/addZz', this.prmas).then(res => {
        this.$emit('success')
      })
    },
    getyearId (item) {
      console.log(item)
      this.prmas.yearId = item.yearId
      this.prmas.menusId = item.menusId
    },
    callback (key) {
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
    // 折叠框
    collapChange (key) {
      // 查询信息互递文件
      if (key.length > 0) {
        key.forEach(re => {
          // 1 信息互递 2一事一奖 3通知公告 4公共信息
          this.fetch({status: 1, type: re * 1})
        })
      }
    },
    // 信息互递其他
    Xxcut () {
      if (this.msgtextflag) {
        this.fetch({status: 2, type: 1})
        this.msgtextflag = false
        this.msgtext = '切换到考核'
      } else {
        this.fetch({status: 1, type: 1})
        this.msgtextflag = true
        this.msgtext = '切换到其他'
      }
    },
    // 信息互递选择
    onSelectChange1 (selectedRowKeys) {
      console.log(selectedRowKeys)
      this.selectedRowKeys1 = selectedRowKeys
    },
    //  请求
    fetch (params) {
      this.$get('/check/grade-zz/getListCZ', params).then(res => {
        console.log(res)
        this.dataSource1 = res.data.data
        // 分页;
        // const pagination = { ...this.pagination }
        // pagination.total = newData.total
        // this.dataSource1 = newData.rows
        // this.pagination = pagination
      })
    }
  }
}
</script>

<style scoped>

</style>
