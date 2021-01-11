<template>
  <div>
  <a-modal
    :visible="lookStatusVisiable"
    title="信息详情"
    :width= "700"
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
                <a-table
                  :data-source="dataSource"
                  :columns="columns"
                  :scroll="{ y: 300 }"
                  :rowKey="record=>record.id"
                  :loading="loading"
                  :pagination="pagination">
                </a-table>
          </a-tab-pane>
            <a-tab-pane key="1" :tab="'已读' + statusTotal.one + '条'">
              用户名：<a-input style="width: 200px" v-model="sendUserName" placeholder="用户名搜索"/>
              <a-button @click="search(1)">查询</a-button>
              <div class="tableStyle">
                <a-table
                  :data-source="dataSource"
                  :columns="columns"
                  :scroll="{ y: 300 }"
                  :rowKey="record=>record.id"
                  :loading="loading"
                  :pagination="pagination">
                </a-table>
              </div>
          </a-tab-pane>
        </a-tabs>
      </div>
  </a-modal>
  </div>
</template>
<script>
export default {
  name: 'LookStatus',
  // 子组件接收初始属性值
  props: {
    lookStatusVisiable: {
      default: false
    }
  },
  data () {
    return {
      colorArr: ['red', 'green', 'darkmagenta', 'turquoise', 'orange', 'blue', 'blueviolet', 'coral', 'deeppink', 'midnightblue'],
      loading: false,
      advanced: false,
      statusTotal: {
        zero: 0,
        one: 0
      },
      sendUserName: null,
      releaseTime: '',
      content: '',
      creatUser: '',
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
      this.$get('/briefing/getUserInfo', params).then(res => {
        let newData = res.data.data
        console.log('查询列表信息：', newData)
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
      const item = {
        briefingId: user.id
      }
      this.$get('/briefing/getInfoById', item).then((r) => {
        this.userName = r.data.userName
      })
      this.titleId = user.id
      console.log('父组件传值：', user)
      this.title = user.title
      this.content = user.content
      this.releaseTime = user.releaseTime // 发布时间
      this.status = user.status
      this.creatUser = user.creatUser
      let param = {
        briefingId: this.titleId,
        pageNum: 1,
        isRead: 0,
        pageSize: 10
      }
      this.fetch(param)
    },
    search (data) {
      let param = {
        briefingId: this.titleId,
        isRead: data,
        pageNum: 1,
        sendUserName: this.sendUserName,
        pageSize: 10
      }
      this.fetch(param)
    },
    // 关闭模态框
    onClose () {
      this.$emit('close')
    },
    // 标签页面
    callback (key) {
      let param = {
        briefingId: this.titleId,
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
