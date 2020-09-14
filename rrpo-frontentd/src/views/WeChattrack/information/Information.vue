<template>
  <a-card :bordered="false" class="card-area">
<!--  通知  -->
    <div style="margin: -10px 0 10px 0; border: 1px solid #fff">
      <a-icon type="plus-square" style="color: #4CB5FC;font-size: 18px; margin-top: -10px;" @click="addTzgg"/>
      <a-icon type="sound" style="color: #4CB5FC;font-size: 18px;vertical-align: middle; margin-top: -10px;"/>
      <span class="microsoft marquee">{{tzgg}}</span>
    </div>
<!--  表单  -->
    <a-form layout="horizontal">
        <a-row >
          <a-col :md="6" :sm="24" >
            <a-form-item
              label="群名称"
              :labelCol="{span: 5}"
              :wrapperCol="{span: 18, offset: 1}">
              <a-input v-model="character.name"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24" >
            <a-form-item
              label="建群人"
              :labelCol="{span: 5}"
              :wrapperCol="{span: 15, offset: 1}">
              <a-input v-model="character.userName"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24" >
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
          </a-col>
        </a-row>
    </a-form>
    <!-- 表格区域 -->
    <div>
      <a-button class="editable-add-btn" @click="handleAdd" v-hasPermission="'Information:add'">
        添加
      </a-button>
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :rowKey="record => {record.wxId}"
        :pagination="pagination"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <template slot="Jd" slot-scope="text, record">
          <a-button @click="look(record)">查看当前进度</a-button>
        </template>
        <template slot="status" slot-scope="text, record">
          <a-tag v-if="record.shStatus === 3" color="#87d068">已通过</a-tag>
          <a-tag v-else-if="record.shStatus === 0" color="#DEE1E6" >未审核</a-tag>
          <a-tag v-else-if="record.shStatus === 1" color="#108ee9">审核中</a-tag>
          <a-tag v-else-if="record.shStatus === 2" color="#f50">未通过</a-tag>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon  type="setting" style="margin-right: 3px" theme="twoTone"  twoToneColor="#4a9ff5"  @click="edit(record)" v-hasPermission="'Information:edit'" title="修改"></a-icon>
          <a-icon  type="highlight" v-hasPermission="'Information:audit'"  style="color:#00CD00;margin:0"  @click="soundClick(record)" title="审核" />
          <a-icon  type="diff" v-hasPermission="'Information:bind'" style="color:#4a9ff5;"   @click="bind(record)" title="绑定" />
          <a-icon type="upload"  v-hasPermission="'Information:bind'" style="color: red" @click="TopClick(record)"   title="上报"/>
        </template>
      </a-table>
    </div>
    <!-- 绑定 -->
    <information-bind
      @success="handlebind"
      @close="hanlebindclose"
      :informationbindVisiable="informationbindVisiable"
      ref="oldbind"
    />
    <!-- 添加 -->
    <information-add
      @success="handlecomAdd"
      @close="hanlecomclose"
      :informationAddVisiable="informationAddVisiable"
    />
    <!-- 修改-->
    <information-edit
      @success="handlecomEdit"
      @close="hanleEditclose"
      :informationEditVisiable="informationEditVisiable"
      ref="oldedit"
    />
<!--  进度查看  -->
    <jd-modal
    @error="jderror"
    :JdModalVisiable="JdModalVisiable"
    ref="nolook"
    />
<!-- 审核   -->
    <information-audit
      @success="handleauditi"
      @close="hanleauditiclose"
      :informationauditiVisiable="informationauditiVisiable"
      ref="oldauditi"
    />
<!--  上报  -->
    <information-top
      @close="TopClose"
      @success="Topsuccess"
    :InformTopVisiable="InformTopVisiable"
      ref="oldTop"
    />
<!-- 通知公告   -->
    <information-tzgg-add
    :InformationTzggVisiable="InformationTzggVisiable"
    @close="TzggClose"
    @success="TzggSuccesse"
    />
  </a-card>
</template>
<script>
import JdModal from './JdModal'
import InformationTzggAdd from './InformationTzggAdd'
import InformationAudit from './InformationAudit'
import InformationAdd from './InformationAdd'
import InformationEdit from './InformationEdit'
import InformationBind from './Informationbind'
import InformationTop from './InformationTop'
export default {
  name: 'Information',
  components: {InformationBind, InformationAdd, InformationEdit, JdModal, InformationAudit, InformationTop, InformationTzggAdd},
  data () {
    return {
      character: {},
      data: [],
      loading: false,
      selectedRowKeys: [],
      informationAddVisiable: false,
      informationEditVisiable: false,
      NoticelookVisiable: false,
      informationbindVisiable: false,
      JdModalVisiable: false,
      informationauditiVisiable: false,
      InformTopVisiable: false,
      InformationTzggVisiable: false,
      sortedInfo: null,
      tzgg: '没有任何通知',
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      // 表格
      dataSource: [],
      count: 2
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
  },
  computed: {
    columns () {
      // let { sortedInfo } = this
      // sortedInfo = sortedInfo || {}
      return [{
        title: '组织机构',
        dataIndex: 'deptJc.deptName'
      }, {
        title: '群名称',
        dataIndex: 'wxName'
      }, {
        title: '建群时间',
        dataIndex: 'date'
      }, {
        title: '建群人',
        dataIndex: 'wxUserName'
      }, {
        title: '联系方式',
        dataIndex: 'phone'
      }, {
        title: '群成员数',
        dataIndex: 'number'
      }, {
        title: '审核进度',
        dataIndex: 'Jd',
        scopedSlots: { customRender: 'Jd' }
      }, {
        title: '审核状态',
        dataIndex: 'status',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/wx/qun/list', parmse).then(res => {
        console.log(res)
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 查询
    search () {
      // 通过this.character来进行查询
      // let {sortedInfo} = this
      // let sortField, sortOrder
      // // 获取当前列的排序和列的过滤规则
      // if (sortedInfo) {
      //   sortField = sortedInfo.field
      //   sortOrder = sortedInfo.order
      // }
      // sortField: sortField,
      //   sortOrder: sortOrder,
      this.fach({
        ...this.character
      })
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空表格选择框
      this.selectedRowKeys = []
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    // 分页
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
    },
    // 删除
    onDelete () {
    //   // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
    //   if (this.selectedRowKeys.length > 0) {
    //     let that = this
    //     that.$confirm({
    //       title: '是否删除，一经删除永远不会恢复?',
    //       centered: true,
    //       onOk () {
    //         that.$delete('/inform/' + that.selectedRowKeys.join(',')).then(() => {
    //           that.$message.success('删除成功')
    //           that.selectedRowKeys = []
    //           that.fach(that.character)
    //         })
    //       },
    //       onCancel () {
    //         that.selectedRowKeys = []
    //       }
    //     })
    //   } else {
    //     this.$message.error('选择不能为空')
    //   }
    },
    // 添加功能
    handleAdd () {
      this.informationAddVisiable = true
    },
    handlecomAdd () {
      this.informationAddVisiable = false
      this.fach(this.character)
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.reload()
    },
    hanlecomclose () {
      this.informationAddVisiable = false
      this.reload()
    },
    // 修改
    edit (item) {
      this.informationEditVisiable = true
      this.$refs.oldedit.getName(item)
    },
    handlecomEdit () {
      this.informationEditVisiable = false
      this.fach(this.character)
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.reload()
    },
    hanleEditclose () {
      this.informationEditVisiable = false
      this.reload()
    },
    // 绑定功能
    bind (key) {
      this.informationbindVisiable = true
      this.$refs.oldbind.getQunId(key)
    },
    handlebind () {
      this.informationbindVisiable = false
      this.fach(this.character)
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanlebindclose () {
      this.informationbindVisiable = false
      this.fach()
    },
    // 审核
    soundClick (record) {
      this.informationauditiVisiable = true
      this.$refs.oldauditi.getItem(record)
    },
    handleauditi () {
      this.informationauditiVisiable = false
      this.fach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    hanleauditiclose () {
      this.informationauditiVisiable = false
      this.fach()
    },
    // 查看
    jderror () {
      this.JdModalVisiable = false
    },
    look (look) {
      this.JdModalVisiable = true
      this.$refs.nolook.getJd(look)
    },
    //  上报
    TopClick (record) {
      this.InformTopVisiable = true
      this.$refs.oldTop.setFormValues(record)
    },
    TopClose () {
      this.InformTopVisiable = false
    },
    Topsuccess () {
      this.InformTopVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 通知公告
    addTzgg () {
      this.InformationTzggVisiable = true
    },
    TzggClose () {
      this.InformationTzggVisiable = false
    },
    TzggSuccesse () {
      this.InformationTzggVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.reload()
    }
  }
}
</script>
<style lang="less" scoped>
  .editable-cell {
    position: relative;
  }

  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }

  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }

  .editable-cell-icon,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 20px;
    cursor: pointer;
  }

  .editable-cell-icon {
    line-height: 18px;
    display: none;
  }

  .editable-cell-icon-check {
    line-height: 28px;
  }

  .editable-cell:hover .editable-cell-icon {
    display: inline-block;
  }

  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }

  .editable-add-btn {
    margin-bottom: 8px;
  }
  .marquee {
    width: 1170px;
    margin: 0 auto;
    overflow: hidden;
    white-space: nowrap;
    box-sizing: border-box;
    animation: marquee 50s linear infinite;
  }

  .marquee:hover {
    animation-play-state: paused
  }
  /* Make it move */

  @keyframes marquee {
    0% {
      text-indent: 27.5em
    }
    100% {
      text-indent: -105em
    }
  }
  .microsoft {
    display: inline-block;
    padding-left: 1.5em;
    position: relative;
    font: 16px 'Segoe UI', Tahoma, Helvetica, Sans-Serif;
    color: rgb(102, 102, 102);
    background: rgb(255, 248, 233) none repeat scroll 0% 0%;
  }

  .microsoft:before,
  .microsoft::before {
    z-index: 2;
    content: '';
    position: absolute;
    top: -1em;
    left: -1em;
    width: .5em;
    height: .5em;

  }

  .microsoft:after,
  .microsoft::after {
    z-index: 1;
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 1.4em;
    height: 1.4em;
    background-size:18px 18px ;
  }
</style>
