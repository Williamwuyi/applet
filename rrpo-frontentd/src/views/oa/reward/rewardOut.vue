<template>
  <div style="width: 100%">
    <a-row>
      <!-- 功能按钮 -->
      <a-col :span="6">
        <a-button @click="rejects" v-hasPermission="'reward:reject'">批量驳回</a-button>
        <a-button @click="batchAddrews" v-hasPermission="'reward:approval'">批量审批</a-button>
      </a-col>
      <a-col :span="18">
        <div>
          <!-- 搜索区域 -->
          <a-form layout="horizontal" :form="form">
            <div>
              <a-row >
                <a-col :span="2">
                  <a-form-item
                    label="编号"
                    :labelCol="{span: 8}"
                    :wrapperCol="{span: 14, offset: 1}">
                    <a-input v-model="character.number"/>
                  </a-form-item>
                </a-col>
                <a-col :span="6">
                  <a-form-item label="上报时间" :labelCol="{span:6}" :wrapperCol="{span: 17, offset: 1}">
                    <range-date @change="handleDateChange" ref="releaseTime"></range-date>
                  </a-form-item>
                </a-col>
                <a-col :span="4" >
                  <a-form-item
                    label="事件类型"
                    :labelCol="{span: 10}"
                    :wrapperCol="{span: 12}"
                  >
                    <a-select
                      @change="thingtype"
                      v-model="character.types"
                    >
                      <a-select-option v-for="n in fileListdata" :key="n.id" :value="n.name">{{n.name}}</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :span="5" >
                  <a-form-item
                    label="地区"
                    :labelCol="{span: 4}"
                    :wrapperCol="{span: 20}"
                  >
                    <a-cascader
                      :options="options"
                      :fieldNames="fieldNames"
                      expand-trigger="hover"
                      change-on-select
                      @change="onChange2"
                      placeholder=""
                      v-model="character.place"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="4" >
                  <a-form-item
                    label="状态查询"
                    :labelCol="{span: 10}"
                    :wrapperCol="{span: 12}"
                  >
                    <a-select
                      v-model="character.status"
                    >
                      <a-select-option v-if="ranks==4" value="5">
                        未审批
                      </a-select-option>
                      <a-select-option v-if="ranks==4" value="6">
                        已审批
                      </a-select-option>
                      <a-select-option v-if="ranks==1" value="5">
                        已审批
                      </a-select-option>
                      <a-select-option v-if="ranks==1" value="3">
                        未审批
                      </a-select-option>
                      <a-select-option v-if="ranks==0" value="6">
                        未审批
                      </a-select-option>
                      <a-select-option value="7">
                        已完结
                      </a-select-option>
                      <a-select-option value="2">
                        被驳回
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :span="3">
                  <span style="float: right; margin-top: 3px;">
                    <a-button @click="search" type="primary">查询</a-button>
                    <a-button @click="reset" style="margin-left: 4px">重置</a-button>
                  </span>
                </a-col>
              </a-row>
            </div>
          </a-form>
        </div>
      </a-col>
    </a-row>
    <a-table
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :rowKey='(record) => record.id'
      @change="handleTableChange"
    >
      <span slot="happenTime" slot-scope="text,record" title="text">{{ text.slice(0,10)}}</span>
      <a slot="content" slot-scope="text,record" style="color:#6290FF" @click="look(record)" title="text">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#DEE1E6">未审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===1" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 5 && ranks===4" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===4" color="#87d068" >已审批</a-tag>
        <a-tag v-else-if="record.status === 6 && ranks===0" color="#DEE1E6" >未审批</a-tag>
        <a-tag v-else-if="record.status === 7 && ranks===0" color="#87d068" >已完结</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033">被驳回</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
        <a-icon v-if="record.status === 3" type="audit" style="color:#4a9ff5;margin:0" v-hasPermission="'reward:approval'" @click="batchAddrew(record)" title="审批" />
        <a-icon v-else-if="record.status === 5 && ranks===4" type="audit" style="color:#4a9ff5;margin:0" v-hasPermission="'reward:approval'" @click="batchAddrew(record)" title="审批" />
        <a-icon v-else-if="record.status === 6 && ranks===0" type="audit" style="color:#4a9ff5;margin:0" v-hasPermission="'reward:approval'" @click="batchAddrew(record)" title="审批" />
        <a-icon v-if="record.status !== 4&&record.status === 3" type="rollback"  style="color:#4a9ff5;margin:0" @click="reject(record)" title="驳回" />
        <a-icon v-else-if="record.status !== 4&&record.status === 5 && ranks===4" type="rollback"  style="color:#4a9ff5;margin:0" @click="reject(record)" title="驳回" />
        <a-icon v-else-if="record.status !== 4&&record.status === 6 && ranks===0" type="rollback"  style="color:#4a9ff5;margin:0" @click="reject(record)" title="驳回" />
      </template>
    </a-table>
    <!--   查看-->
    <rewardLook
      :rewardlookVisiable="rewardlookVisiable"
      @close="hanleedLoclose"
      ref="nolook"
    ></rewardLook>
<!--    审批-->
    <rewardApvals
      :rewardApvals="rewardApvalVisiable"
      @close="hanleedApclose"
      @success="handleApv"
      ref="noApval"
    ></rewardApvals>
<!-- 生成报表-->
    <reportForm
      :reportform="reportform"
      @close="hanleedRepclose"
    ></reportForm>
<!--    提交驳回意见-->
    <rewardReject
      :rewardReject="rewardReject"
      @close="hanleedRejclose"
      @success="handleRej"
      ref="noReject"
    ></rewardReject>
  </div>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
import rewardLook from './rewardLook'
import rewardApvals from './rewardApvals'
import reportForm from './reportForm'
import rewardReject from './rewardReject'
export default {
  name: 'rewardout',
  components: {rewardLook, rewardApvals, RangeDate, reportForm, rewardReject},
  data () {
    return {
      character: {},
      loading: false,
      sortedInfo: null,
      rewardlookVisiable: false,
      rewardApvalVisiable: false,
      reportform: false,
      rewardReject: false,
      form: this.$form.createForm(this),
      dataSource: [],
      selectedRowKeys: [],
      //  分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      prizeIds: [],
      selectedRows: [],
      ranks: 0,
      options: [],
      fieldNames: {label: 'title', value: 'text', children: 'children'},
      fileListdata: []
    }
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [
        {
          title: '编号',
          dataIndex: 'number',
          width: '5%',
          scopedSlots: { customRender: 'number' },
          align: 'center'
        },
        {
          title: '事迹简介',
          dataIndex: 'content',
          width: '30%',
          ellipsis: true,
          scopedSlots: { customRender: 'content' }
        },
        {
          title: '上报时间',
          dataIndex: 'releaseTime',
          sorter: true,
          sortOrder: sortedInfo.columnKey === 'creatTime' && sortedInfo.order,
          align: 'center'
        },
        {
          title: '当前状态',
          dataIndex: 'state',
          scopedSlots: { customRender: 'status' },
          align: 'center'
        },
        {
          title: '事发地点',
          dataIndex: 'place'
        },
        {
          title: '事发时间',
          dataIndex: 'happenTime',
          scopedSlots: { customRender: 'happenTime' },
          align: 'center'
        },
        {
          title: '事件类型',
          dataIndex: 'types',
          align: 'center'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    // 获取地址
    this.$get('dept').then((r) => {
      this.options = r.data.rows.children
    })
    this.fach()
    // /dept/findRank
    // 获取当前用户的rank
    this.$get('/dept/findRank').then(res => {
      console.log(res)
      this.ranks = res.data.data.rank
      console.log(this.ranks)
    })
    // 获取事件类型
    this.$get('prizeTypes/getTypesList').then((r) => {
      this.fileListdata = r.data.data
    })
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/prize/inbox', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    thingtype (value) {
      console.log(value)
    },
    onChange (date, dateString) {},
    // 重置表格参数
    reset () {
      this.fach()
      this.loading = false
      // 重置查询参数
      this.character = {}
      // 清空时间选择
      this.$refs.releaseTime.reset()
      // 清空表格选择框
      this.selectedRowKeys = []
      // 重置列排序规则
      this.sortedInfo = null
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
    onChange2 (value) {
      console.log(value)
    },
    // 查询
    search () {
      // 通过this.character来进行查询
      let {sortedInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fach({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.character
      })
    },
    // 获取表格勾选项信息
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
      console.log(this.selectedRows)
    },
    // 打开查看页面
    showLook () {
      this.rewardlookVisiable = true
    },
    // 单条审批
    batchAddrew (record) {
      this.rewardApvalVisiable = true
      this.$refs.noApval.setTableValues(record)
    },
    // 批量审批
    batchAddrews () {
      if (this.selectedRowKeys.length > 0) {
        this.rewardApvalVisiable = true
        this.$refs.noApval.setTableValues(this.selectedRows)
        this.selectedRowKeys = []
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 单条驳回
    reject (record) {
      this.rewardReject = true
      this.$refs.noReject.setTableValues(record)
    },
    // 批量驳回
    rejects () {
      if (this.selectedRowKeys.length > 0) {
        this.rewardReject = true
        this.$refs.noReject.setTableValues(this.selectedRows)
        this.selectedRowKeys = []
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 生成报表
    reportformone () {
      this.reportform = true
    },
    // 返回审批成功状态
    handleApv () {
      this.rewardApvalVisiable = false
      this.fach(this.character)
      this.$message.success('审批完成')
    },
    // 返回驳回成功状态
    handleRej () {
      this.rewardReject = false
      this.fach(this.character)
      this.$message.success('驳回成功')
    },
    // 关闭查看页面
    hanleedLoclose () {
      this.rewardlookVisiable = false
    },
    // 关闭审批页面
    hanleedApclose () {
      this.rewardApvalVisiable = false
    },
    // 关闭驳回页面
    hanleedRejclose () {
      this.rewardReject = false
    },
    // 关闭报表页
    hanleedRepclose () {
      this.reportform = false
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
    },
    look (look) {
      this.rewardlookVisiable = true
      this.$refs.nolook.setFormValues(look)
    }
  }
}
</script>
<style>
</style>
