<template>
  <div>
  <a-modal
    :visible="lookStatusVisiable"
    title="信息详情"
    :width= "850"
    :footer="null"
    @cancel="() => { onClose() }"
  >
    <div style="font-size: 17px;text-align: center">
      <p><b style="font-size: 28px">{{title}}</b></p>
      <p>
        <span style="font-size: 15px;">发送时间：{{releaseTime}}</span>
      </p>
      <div style="text-align:left;margin-top: 20px">
        接收人员：
        <a-tag :style="'color:' + colorArr[Math.floor((Math.random() * colorArr.length))]" v-for="(value,index) in userName" :key="index">
          {{value}}
        </a-tag>
      </div>
    </div>
<!--    下部列表-->
      <div style="margin-top: 20px">
        <a-tabs type="card" @change="callback">
            <a-tab-pane key="0" :tab="'未读' + statusTotal.zero + '条'">
              用户名：<a-input style="width: 200px" v-model="sendUserName" placeholder="用户名搜索"/>
              <a-button @click="search(0)">查询</a-button>
              <a-button @click="exprotExccel(1)" title="默认打印本页，或者勾选打印">导出打印</a-button>
                <a-table
                  :data-source="dataSource"
                  :columns="columnsd"
                  :rowKey="record=>record.id"
                  :loading="loading"
                  :scroll="{ y: 300 }"
                  :rowSelection="{selectedRowKeys: selectedIdW, onChange: onWChange}"
                  :pagination="pagination">
                </a-table>
          </a-tab-pane>
            <a-tab-pane key="1" :tab="'已读' + statusTotal.one + '条'">
              用户名：<a-input style="width: 200px" v-model="sendUserName" placeholder="用户名搜索"/>
              <a-button @click="search(1)">查询</a-button>
              <a-button @click="exprotExccel(2)" title="默认打印本页，或者勾选打印">导出打印</a-button>
                <a-table
                  :data-source="dataSource"
                  :columns="columnsd"
                  :scroll="{ y: 300 }"
                  :rowKey="record=>record.id"
                  :loading="loading"
                  :rowSelection="{selectedRowKeys: selectedIdY, onChange: onYChange}"
                  :pagination="pagination">
                </a-table>
          </a-tab-pane>
          <a-tab-pane key="2" :tab="'回复' + statusTotal.two + '条'">
            用户名：<a-input style="width: 200px" v-model="sendUserName" placeholder="用户名搜索"/>
            <a-button @click="search(2)">查询</a-button>
            <a-button @click="exprotExccel(3)" title="默认打印本页，或者勾选打印">导出打印</a-button>
            <a-table
              :data-source="dataSource"
              :columns="columns"
              :scroll="{ y: 300 }"
              :rowKey="record=>record.id"
              :loading="loading"
              :rowSelection="{selectedRowKeys: selectedIdH, onChange: onHChange}"
              :pagination="pagination">
              <a slot="details" slot-scope="text,record" style="color:#6290FF" v-if="text !== null" @click="lookReply(record)">
                <div v-html="text" style="width: 100px; white-space: nowrap;overflow: hidden;text-overflow:ellipsis;"></div>
              </a>
              <template slot="operation">
                <a-button style="padding: 1px 5px" @click="lookFileButton"><a-icon type="cloud-download" />查看附件</a-button>
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </div>
  </a-modal>
    <collection-create-form
      ref="collectionForm"
      :details="details"
      :visible="visible"
      @cancel="handleCancel"
    />
    <look-file
      :lookFileVisiable="lookFileVisiable"
      ref="oldLook"
      @close="lookFileClose"
    />
  </div>
</template>
<script>
import lookFile from './lookFile'
const CollectionCreateForm = {
  props: ['visible', 'details'],
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  template: `

    <a-modal
      :visible="visible"
      title='查看内容'
      :footer="null"
      @cancel="() => { $emit('cancel') }"
      @ok="() => { $emit('create') }"
    >
      <div style="min-height: 500px;line-height:35px;text-indent:30px">
      <div v-html="details.opinion"></div>
    </div>
    </a-modal>

  `
}
export default {
  components: {CollectionCreateForm, lookFile},
  name: 'LookStatus',
  // 子组件接收初始属性值
  props: {
    lookStatusVisiable: {
      default: false
    }
  },
  data () {
    return {
      visible: false,
      colorArr: ['red', 'green', 'darkmagenta', 'turquoise', 'orange', 'blue', 'blueviolet', 'coral', 'deeppink', 'midnightblue'],
      loading: false,
      color: 'turquoise ',
      selectedIdW: [],
      selectedIdY: [],
      selectedIdH: [],
      lookReplyVisiable: false,
      details: {},
      advanced: false,
      statusTotal: {zero: 0, one: 0, two: 0},
      sendUserName: null,
      releaseTime: '',
      content: '',
      creatUser: '',
      lookFileVisiable: false, // 查看附件
      title: '',
      userName: '',
      status: '',
      dataSource: [],
      titleId: '',
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      columns: [
        {
          title: '机构组织',
          dataIndex: 'deptName'
        },
        {
          title: '用户名',
          dataIndex: 'sendUserName'
        },
        {
          title: '时间',
          dataIndex: 'receiveTime',
          width: '20%'
        },
        {
          title: '内容',
          dataIndex: 'opinion',
          scopedSlots: { customRender: 'details' }
        },
        {
          title: '操作',
          scopedSlots: { customRender: 'operation' }
        }
      ],
      columnsd: [
        {
          title: '机构组织',
          dataIndex: 'deptName'
        },
        {
          title: '用户名',
          dataIndex: 'sendUserName'
        },
        {
          title: '时间',
          dataIndex: 'receiveTime',
          width: '20%'
        }
      ]
    }
  },
  // 初始渲染
  mounted () {
  },
  methods: {
    fetch (params = {}) {
      this.loading = true
      this.$get('/exchange/getUserInfo/', params).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.statusTotal = newData.isRead
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    // 父组件传过来的数据
    setFormValues (user) {
      this.$get('/exchange/getInfoById/' + user.id).then((r) => {
        this.userName = r.data.userName
      })
      this.titleId = user.id
      this.title = user.title
      this.content = user.content
      this.releaseTime = user.releaseTime // 发布时间
      this.status = user.status
      this.creatUser = user.creatUser
      let param = {
        exchangeId: this.titleId,
        pageNum: 1,
        isRead: 0,
        pageSize: 10
      }
      this.fetch(param)
    },
    lookReply (record) {
      this.visible = true
      this.details = record
    },
    handleCancel () {
      this.visible = false
    },
    search (data) {
      let param = {
        exchangeId: this.titleId,
        isRead: data,
        pageNum: 1,
        sendUserName: this.sendUserName,
        pageSize: 10
      }
      this.fetch(param)
    },
    // 查看附件
    lookFileButton () {
      this.lookFileVisiable = true
      this.$refs.oldLook.setId(this.titleId)
    },
    lookFileClose () {
      this.lookFileVisiable = false
    },
    // 关闭模态框
    onClose () {
      this.$emit('close')
    },
    onWChange (selectedIdW) {
      this.selectedIdW = selectedIdW
    },
    onYChange (selectedIdY) {
      this.selectedIdY = selectedIdY
    },
    onHChange (selectedIdH) {
      this.selectedIdH = selectedIdH
    },
    exprotExccel (data) {
      let id
      if (data === 1) {
        id = this.selectedIdW
      } else if (data === 2) {
        id = this.selectedIdY
      } else if (data === 3) {
        id = this.selectedIdH
      }
      if (id.length !== 0) {
        const param = {
          exchangeIds: id
        }
        this.$export('/exchange/excel', param)
      } else {
        this.$notification.warning({message: '系统提示', description: '请选中导出！', duration: 4})
      }
    },
    // 标签页面
    callback (key) {
      let param = {
        exchangeId: this.titleId,
        pageNum: 1,
        isRead: key,
        pageSize: 10
      }
      this.sendUserName = null
      this.fetch(param)
    }
  }
}
</script>
<style lang="less" scoped>
</style>
