<template>
  <div style="width: 100%">
    <a-row>
      <!-- 功能按钮 -->
      <a-col :span="10">
        <a-button @click="showAddrew" v-hasPermission="'reward:add'">新增</a-button>
        <a-button @click="soundClickm" v-hasPermission="'reward:report'">批量上报</a-button>
        <a-button @click="onDelete" v-hasPermission="'reward:delete'">批量删除</a-button>
      </a-col>
      <a-col :span="14">
          <!-- 搜索区域 -->
          <a-form layout="horizontal" :form="form">
            <div>
              <a-row >
                <a-col :span="4">
                  <a-form-item
                    label="编号"
                    :labelCol="{span: 5}"
                    :wrapperCol="{span: 18, offset: 1}">
                    <a-input v-model="character.number"/>
                  </a-form-item>
                </a-col>
                <a-col :span="10">
                  <a-form-item label="创建时间" :labelCol="{span: 5}" :wrapperCol="{span: 13, offset: 1}">
                    <range-date @change="handleDateChange" ref="createTime"></range-date>
                  </a-form-item>
                </a-col>
                <a-col :span="6">
                  <a-form-item
                    label="状态查询"
                    :labelCol="{span: 10}"
                    :wrapperCol="{span: 12}"
                  >
                    <a-select
                      v-model="character.status"
                    >
                      <a-select-option value=1>
                        未上报
                      </a-select-option>
                      <a-select-option value=3>
                        已上报
                      </a-select-option>
                      <a-select-option value=2>
                        被驳回
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :span="4">
                  <span style="float: right; margin-top: 3px;">
                    <a-button @click="search" type="primary">查询</a-button>
                    <a-button @click="reset" style="margin-left: 4px">重置</a-button>
                  </span>
                </a-col>
              </a-row>
            </div>
          </a-form>
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
      <span slot="happenTime" slot-scope="text,record">{{ text.slice(0,10)}}</span>
      <a slot="content" slot-scope="text,record" style="color:#6290FF" @click="look(record)">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === 3" color="#87d068">已上报</a-tag>
        <a-tag v-else-if="record.status === 1" color="#DEE1E6" >未上报</a-tag>
        <a-tag v-else-if="record.status === 2" color="#FF0033" >被驳回</a-tag>
        <a-tag v-else-if="record.status === 5" color="#87d068" >市州已审批</a-tag>
        <a-tag v-else-if="record.status === 6" color="#87d068" >铁护办已审批</a-tag>
        <a-tag v-else-if="record.status === 7" color="#87d068" >省级已审批</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
        <a-icon v-if="record.status <2" type="setting" style="margin-right: 3px" theme="twoTone"  twoToneColor="#4a9ff5"  @click="edit(record)" v-hasPermission="'reward:modify'" title="修改"></a-icon>
        <a-icon v-if="record.status <2" type="file-add" theme="twoTone"  style="color:#4a9ff5;margin:0" v-hasPermission="'reward:report'" @click="soundClick(record)" title="上报" />
      </template>
    </a-table>
    <!--    添加-->
    <rewardadd
      :rewardaddVisiable="rewardaddVisiable"
      :randomId="randomId"
      @success="handlecomAdd"
      @close="hanlecomclose"
      ref="oldadd"
    >
    </rewardadd>
    <!--    修改-->
    <rewardEdit
      :rewardEditVisiable="rewardEditVisiable"
      @close="hanleeditclose"
      @success="handleEdit"
      ref="oldedit"
    >
    </rewardEdit>
    <!--    查看-->
    <rewardLook
      :rewardlookVisiable="rewardlookVisiable"
      @close="hanleedLoclose"
      ref="nolook"
    ></rewardLook>
  </div>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
import rewardadd from './rewardadd'
import rewardEdit from './rewardEdit'
import rewardLook from './rewardLook'
import uuidv1 from 'uuid/v1'
import {mapMutations} from 'vuex'
export default {
  name: 'reward',
  components: {rewardadd, rewardEdit, rewardLook, RangeDate},
  data () {
    return {
      character: {},
      loading: false,
      sortedInfo: null,
      rewardaddVisiable: false,
      rewardEditVisiable: false,
      rewardlookVisiable: false,
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
      ranks: 0,
      randomId: '-1'
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
          width: '35%',
          ellipsis: true,
          scopedSlots: { customRender: 'content' }
        },
        {
          title: '创建时间',
          dataIndex: 'creatTime',
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
          title: '操作',
          dataIndex: 'operation',
          width: '5%',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/prize', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
        this.pagination = pagination
        this.loading = false
      })
    },
    onChange (date, dateString) {},
    // 重置表格参数
    reset () {
      this.fach()
      this.loading = false
      // 重置查询参数
      this.character = {}
      // 清空时间选择
      this.$refs.createTime.reset()
      // 清空表格选择框
      this.selectedRowKeys = []
      // // 重置列排序规则
      this.sortedInfo = null
    },
    // 单条上报
    soundClick (record) {
      if (record.status !== 3) {
        let that = this
        that.$confirm({
          title: '确定上报?一经上报不可修改！',
          centered: true,
          onOk () {
            that.$get('/prize/' + record.id).then(res => {
              that.fach(that.character)
              that.$message.success('上报成功')
            }).catch(() => {
              that.fach(that.character)
              that.$message.error('上报失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于上报状态了')
      }
    },
    // 批量上报
    soundClickm (record) {
      // 先通过key之传递到数据库，表明上报这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        that.$confirm({
          title: '是否上报？一经上报不可修改！',
          centered: true,
          onOk () {
            that.$get('prize/' + that.selectedRowKeys.join(',')).then(() => {
              that.$message.success('上报成功')
              that.selectedRowKeys = []
              that.fach(that.character)
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 删除
    onDelete () {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        console.log(that.selectedRowKeys)
        that.$confirm({
          title: '已上报或驳回数据无法删除，一经删除无法恢复',
          centered: true,
          onOk () {
            that.$delete('/prize/' + that.selectedRowKeys.join(',')).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.fach(that.character)
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 修改
    edit (key) {
      this.rewardEditVisiable = true
      this.$refs.oldedit.setFormValues(key)
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
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 打开新增页面
    showAddrew (randomId) {
      this.randomId = uuidv1() // 获取随机ID
      this.rewardaddVisiable = true
      this.initAppendixList()
    },
    ...mapMutations({
      initAppendixList: 'file/initAppendixList'
    }),
    // 查看详情
    showLook () {
      this.rewardlookVisiable = true
    },
    handlecomAdd () {
      this.rewardaddVisiable = false
      this.fach(this.character)
      this.$message.success('新增一事一奖成功')
      this.$store.commit('file/initAppendixList')
      // this.reload()
    },
    handleEdit () {
      this.rewardEditVisiable = false
      this.fach(this.character)
      this.$message.success('修改成功')
    },
    hanleeditclose () {
      this.rewardEditVisiable = false
    },
    hanlecomclose () {
      this.rewardaddVisiable = false
      // this.reload()
    },
    hanleedLoclose () {
      this.rewardlookVisiable = false
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
