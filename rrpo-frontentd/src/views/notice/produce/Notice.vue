<template>
  <div style="width: 100%; min-height: 690px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="标题"
                :labelCol="{span: 3}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="character.title"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="创建时间"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="状态查询"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-cascader  change-on-select
                             style="width: 105px;"
                             @change="onChangecascader"
                             :options="options"
                             placeholder="未选择"
                             ref="cascader"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <!-- 表格区域 -->
    <div>
    <div style="margin-bottom: 5px">
      <a-button type="primary" @click="handleAdd" v-hasPermission="'notice:add'">
        添加
      </a-button>
      <a-button class="delet" @click="onDelete" v-hasPermission="'notice:delete'">
        删除
      </a-button>
    </div>
    <a-table
    :data-source="dataSource"
    :columns="columns"
    :loading="loading"
    :scroll="{ y: 450 }"
    :pagination="pagination"
    :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
    @change="handleTableChange"
    >
        <a slot="name" slot-scope="text,record" style="color:#4A9FF5" @click="look(record)">{{ text }}</a>
      <template slot="status" slot-scope="text, record">
        <a-tag v-if="record.status === '3'" color="#87d068">已发布</a-tag>
        <a-tag v-else-if="record.status === '1'" color="#DEE1E6" >未发布</a-tag>
        <a-tag v-else-if="record.status === '2'" color="#f50">已撤销</a-tag>
      </template>
      <template slot="operation" slot-scope="text, record">
        <a v-if="record.status !== '3'"  style="margin-right: 3px;color:#4a9ff5;"     @click="edit(record)" v-hasPermission="'notice:edit'" >修改</a>
        <a v-if="record.status !== '3'"   style="margin:0" v-hasPermission="'notice:sound'" @click="soundClick(record)" >发布</a>
        <a v-if="record.status == '3'"   style="color:#f50;"  v-hasPermission="'notice:repeal'" @click="repealClick(record)">撤销</a>
      </template>
    </a-table>
  </div>
<!-- 修改 -->
<notice-edit
  @success="handleEdit"
  @close="hanleeditclose"
  :NoticeEditVisiable="NoticeEditVisiable"
  ref="oldedit"
/>
<!-- 添加 -->
<noticeadd
  @success="handlecomAdd"
  @close="hanlecomclose"
  :NoticeaddVisiable="NoticeaddVisiable"
/>
<!-- 查看 -->
<noticelook
  :NoticelookVisiable="NoticelookVisiable"
  @close="hanleNoticelookclose"
  ref="nolook"
/>
    </div>
</template>
<script>
import uuidv1 from 'uuid/v1'
import RangeDate from '@/components/datetime/RangeDate'
import Noticeadd from './Noticeadd'
import NoticeEdit from './NoticeEdit'
import Noticelook from '../../common/Noticelook'
import {mapMutations} from 'vuex'
export default {
  name: 'Notice',
  components: {Noticelook, NoticeEdit, Noticeadd, RangeDate},
  data () {
    return {
      character: {},
      data: [],
      loading: false,
      advanced: false,
      selectedRowKeys: [],
      NoticeaddVisiable: false,
      NoticeEditVisiable: false,
      NoticelookVisiable: false,
      sortedInfo: null,
      randomId: '-1',
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
      count: 2,
      //  状态下拉
      options: [
        {
          value: '1',
          label: '未发布'
        },
        {
          value: '2',
          label: '已撤销'
        },
        {
          value: '3',
          label: '已发布'
        }
      ]
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '标题',
        dataIndex: 'title',
        width: '30%',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '发布时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '当前状态',
        dataIndex: 'state',
        scopedSlots: { customRender: 'status' }
      }, {
        title: '发布人',
        dataIndex: 'createUser'
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
      this.$get('/notice', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.rows
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
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空时间选择
      this.$refs.createTime.reset()
      // 清空表格选择框
      this.selectedRowKeys = []
      // 清空状态选择
      this.$refs.cascader.sValue = []
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    // // 更多操作
    // exprotExccel () {
    //   // 导出操作
    //   console.log('执行了打印操作')
    // },
    // 时间查询
    handleDateChange (value) {
      if (value[0] !== '' && value[1] !== '') {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      } else {
        this.character = {}
      }
    },
    // 状态查询
    onChangecascader (value) {
      this.character.status = value
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
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        that.$confirm({
          title: '是否删除，一经删除永远不会恢复?',
          centered: true,
          onOk () {
            that.$delete('/notice/' + that.selectedRowKeys.join(',')).then(res => {
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
    // 添加功能
    handleAdd () {
      this.randomId = uuidv1() // 获取随机ID
      this.NoticeaddVisiable = true
      this.initAppendixList()
    },
    ...mapMutations({
      initAppendixList: 'file/initAppendixList'
    }),
    handlecomAdd () {
      this.NoticeaddVisiable = false
      this.fach(this.character)
      this.$message.success('新增公告成功')
      this.$store.commit('file/initAppendixList')
      this.reload()
    },
    hanlecomclose () {
      this.NoticeaddVisiable = false
      this.reload()
    },
    // 修改功能
    edit (key) {
      this.NoticeEditVisiable = true
      this.$refs.oldedit.setFormValues(key)
    },
    handleEdit () {
      this.NoticeEditVisiable = false
      this.fach(this.character)
      this.$message.success('修改成功')
    },
    hanleeditclose () {
      this.NoticeEditVisiable = false
    },
    // 发布
    soundClick (record) {
      if (record.status !== 3) {
        let that = this
        that.$confirm({
          title: '确定发布《' + record.title + '》?',
          centered: true,
          onOk () {
            that.$get('/notice/release/' + record.id).then(res => {
              that.fach(that.character)
              that.$message.success('发布成功')
            }).catch(() => {
              that.fach(that.character)
              that.$message.error('发布失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于发布状态了')
      }
    },
    // 撤销
    repealClick (record) {
      if (record.status !== 2) {
        let that = this
        that.$confirm({
          title: '确定撤销《' + record.title + '》?',
          centered: true,
          onOk () {
            that.$get('/notice/revoke/' + record.id).then(res => {
              that.fach(that.character)
              that.$message.success('撤销成功')
            }).catch(() => {
              that.fach(that.character)
              that.$message.success('撤销失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于撤销状态了')
      }
    },
    // 查看
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
    },
    look (look) {
      this.NoticelookVisiable = true
      this.$refs.nolook.setFormValues(look)
    }
    // // 面包屑回到首页
    // onClickTop () {
    //   this.$router.push('/home')
    // }
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
.bre {
  cursor: pointer;
  position: absolute;
  width: 100%;
  height: 50px;
  left: 2px;
  padding-left: 20px;
  padding-top: 15px;
  background-color: rgb(255, 255, 255);
  top: -24px;
  font-size: 14px;
}
  .delet {
    background-color: #FF4D4F;
    color: white;
    border-radius: 4px!important;
  }
  .delet:hover {
    border-color: #FF4D4F;
    background-color: #FF4D4F;
    color: white;
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.035);
  }
</style>
