<template>
  <div style="width: 100%; min-height: 650px">
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
                :wrapperCol="{span: 18, offset: 0}">
                <range-date @change="handleDateChange" ref="createTime"></range-date>
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
    <a-table
    :data-source="dataSource"
    :columns="columns"
    :loading="loading"
    :scroll="{ y: 550 }"
    :pagination="pagination"
    :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
    @change="handleTableChange"
    >
        <a slot="name" slot-scope="text,record" style="color:#4A9FF5" @click="look(record)">{{ text }}</a>
    </a-table>
  </div>
<!-- 查看 -->
<noticelook
  :NoticelookVisiable="NoticelookVisiable"
  @close="hanleNoticelookclose"
  ref="nolook"
/>
    </div>
</template>
<script>
import RangeDate from '@/components/datetime/RangeDate'
import Noticelook from '../../common/Noticelook'
export default {
  name: 'Notice',
  components: {Noticelook, RangeDate},
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
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '标题',
        dataIndex: 'title',
        width: '50%',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '发布时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '发布人',
        dataIndex: 'createUser'
      }]
    }
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/notice/inbox', parmse).then(res => {
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
      // 重置页码
      this.character.pageNum = 1
      this.character.pageSize = 10
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
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
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
    // 查看
    hanleNoticelookclose () {
      this.NoticelookVisiable = false
    },
    look (look) {
      this.NoticelookVisiable = true
      this.$refs.nolook.setFormValues(look)
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
</style>
