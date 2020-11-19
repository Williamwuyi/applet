<template>
    <div>
        <div :class="advanced ? 'search' : null">
          <!-- 搜索区域 -->
          <a-form layout="horizontal">
            <div :class="advanced ? null: 'fold'">
              <a-row >
                <a-col :md="6" :sm="24">
                  <a-form-item label='组织机构查询' :labelCol="{span: 6}" :wrapperCol="{span: 16, offset: 1}">
                    <a-cascader  change-on-select
                                 style="width: 270px;"
                                 @change="onChangedanwei"
                                 :options="optionsr"
                                 :fieldNames="{ label: 'text', value: 'id', children: 'children' }"
                                 placeholder="未选择"
                                 ref="danwei"
                    />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24" >
                  <a-form-item
                    label="选择群"
                    :labelCol="{span: 5}"
                    :wrapperCol="{span: 18, offset: 0}">
                    <a-select  style="width: 230px" v-model="msgweek" placeholder="先选择地区" :allowClear="true" @change="handleWeekChange">
                      <a-select-option  v-for="(item, index) in arr" :value="item.wxId" :key="index">
                        {{item.wxName}}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24" >
                  <a-form-item
                    label="用户"
                    :labelCol="{span: 5}"
                    :wrapperCol="{span: 15, offset: 1}">
                    <a-input v-model="character.name"/>
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
          <a-table
            :data-source="dataSource"
            :columns="columns"
            :loading="loading"
            :pagination="pagination"
            :scroll="{ y: 450 }"
            :rowKey="record => {record.wxUserId}"
            @change="handleTableChange"
          >
            <template slot="dept" slot-scope="text, record">
              <span v-if="record.deptSzO !== null">{{record.deptSzO.deptName}}</span>
              <span v-if="record.deptQxO.deptName">/</span>
              <span v-if="record.deptQxO !== null">{{record.deptQxO.deptName}}</span>
              <span v-if="record.deptXzO.deptName">/</span>
              <span v-if="record.deptXzO !== null">{{record.deptXzO.deptName}}</span>
            </template>
          </a-table>
    </div>
</template>

<script>
export default {
  name: 'Assessment',
  data () {
    return {
      advanced: false,
      arr: [],
      msgweek: undefined,
      character: {},
      dataSource: [],
      optionsr: [],
      loading: false,
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
    this.fach()
    this.$get('dept').then((r) => {
      this.optionsr = r.data.rows.children
    })
  },
  computed: {
    columns () {
      // let { sortedInfo } = this
      // sortedInfo = sortedInfo || {}
      // {
      //   title: '组织机构',
      //     dataIndex: 'deptJc.deptName'
      // }, {
      //   title: '群名称',
      //     dataIndex: 'wxName'
      // },
      return [{
        title: '组织机构',
        dataIndex: 'dept',
        scopedSlots: { customRender: 'dept' }
      }, {
        title: '所在群',
        dataIndex: 'wxName'
      }, {
        title: '姓名',
        dataIndex: 'name'
      }, {
        title: '身份',
        dataIndex: 'sf'
      }, {
        title: '联系方式',
        dataIndex: 'phone'
      }]
    }
  },
  methods: {
    // 渲染
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/wx/user/List', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 查询
    search () {
      // 通过this.character来进行查询
      this.fach({
        ...this.character
      })
    },
    // 重置
    reset () {
      this.arr = []
      this.msgweek = undefined
      // 重置查询参数
      this.character = {}
      // 清空表格选择框
      this.selectedRowKeys = []
      // 清空机构选择
      this.$refs.danwei.sValue = []
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
    onChangedanwei (value) {
      if (value !== undefined) {
        let id = value[value.length - 1]
        this.$get('/wx/qun/ListByDeptId', {deptId: id}).then(res => {
          this.arr = res.data.data
        })
      } else {
        this.arr = []
        this.msgweek = undefined
      }
    },
    handleWeekChange (value) {
      if (value !== undefined) {
        this.character.qunId = value
      } else {
        this.character.qunId = value
      }
    }
  }
}
</script>

<style scoped>

</style>
