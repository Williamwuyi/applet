<template>
  <div style="width: 100%;min-height: 770px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form style="">
        <div :class="advanced ? null: 'fold'">
          <a-row>
            <a-col :md="4" :sm="10">
              <a-form-item
                label="公安处"
                :labelCol="{span: 6}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-select v-model="unitG" ref="gacs">
                  <a-select-option v-for="(n,index) in cityG" :key="index" :value="n.id" >
                    {{n.dept.deptName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="10">
              <a-form-item
                label="地洲市"
                :labelCol="{span: 6}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-select @change="cityQChange" v-model="unitS" ref="dzss">
                  <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.deptId" >
                    {{n.deptName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="10">
              <a-form-item
                label="区县单位 :"
                :labelCol="{span: 8}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-select @change="cityXChange" v-model="unitX" ref="xsqs">
                  <a-select-option v-for="(n,index) in cityX" :key="index" :value="n.deptId" >
                    {{n.deptName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="10">
              <a-form-item label="线路名称" :labelCol="{span: 8}" :wrapperCol="{span: 15, offset: 1}">
                <a-select @change="cityXChange" v-model="lineName" ref="xlmcs">
                  <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.lineId" >
                    {{n.lineName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="20" >
              <a-form-item label="安装年月:" :labelCol="{span: 8}" :wrapperCol="{span: 15, offset: 1}">
<!--                <a-date-picker type="year" format="yyyy" value-format="yyyy" @change="onTimeChange" v-model="installTime" >-->
<!--                </a-date-picker>-->
                <a-input v-model="year" placeholder="年份格式：2020"/>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="20" style="margin-top: 3px; margin-left: 10px">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <a-button class="btn" @click="handleAdd" type="primary">
      添加
    </a-button>
    <a-button class="btn" @click="handleDel" style="background-color: #FF4040;color: white">
      删除
    </a-button>
    <a-table style="overflow: auto;width: 100%"
             :data-source="dataSource"
             :scroll={x:100,y:580}
             :columns="columns"
             :rowKey="(record)=> record.id"
             :pagination="pagination"
             :loading="loading"
             :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
             @change="handleTableChange"
    >
      <template slot="operation" slot-scope="text, record">
        <a @click="edit(record)" style="color: #158BD2">修改</a>
      </template>
    </a-table>
    <add-edit
      ref="oldAddEdit"
      :title="title"
      @success="addEditSuccess"
      @close="addEditClose"
      :addEditVisiable="addEditVisiable"
    />
  </div>
</template>

<script>
import AddEdit from './addEdit'
export default {
  name: 'Monitor',
  components: {AddEdit},
  data () {
    return {
      advanced: false,
      addEditVisiable: false,
      installTime: null,
      unitG: '',
      unitS: '',
      unitX: '',
      lineName: '',
      lineData: [],
      year: null,
      cityG: [],
      cityS: [],
      cityX: [],
      title: '',
      loading: false,
      character: {},
      sortedInfo: null,
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      columns: [
        {
          title: '公安处',
          width: '100px',
          dataIndex: 'gac'
        },
        {
          title: '地州市',
          width: '120px',
          dataIndex: 'dzs'
        },
        {
          title: '县市区',
          width: '120px',
          dataIndex: 'xsq'
        },
        {
          title: '乡镇街道',
          width: '120px',
          dataIndex: 'xzjd'
        },
        {
          title: '线路名称',
          width: '120px',
          dataIndex: 'xlmc'
        },
        {
          title: '铁路里程',
          width: '120px',
          dataIndex: 'tllc'
        },
        {
          title: '对应方位',
          width: '120px',
          dataIndex: 'azfw'
        },
        {
          title: '铁路距离',
          width: '120px',
          dataIndex: 'tljl'
        },
        {
          title: '道路名称',
          width: '120px',
          dataIndex: 'dlmc'
        },
        {
          title: '道路里程',
          width: '120px',
          dataIndex: 'dllc'
        },
        {
          title: '安装部位',
          width: '120px',
          dataIndex: 'azbw'
        },
        {
          title: '视频个数',
          width: '120px',
          dataIndex: 'sl'
        },
        {
          title: '视频类型',
          width: '120px',
          dataIndex: 'jkqlx'
        },
        {
          title: '监控范围',
          width: '120px',
          dataIndex: 'jkfw'
        },
        {
          title: '设计类型',
          width: '120px',
          dataIndex: 'jksjzl'
        },
        {
          title: '夜视功能',
          width: '120px',
          dataIndex: 'jkysgn'
        },
        {
          title: '保留天数',
          width: '120px',
          dataIndex: 'blts'
        },
        {
          title: '联网单位',
          width: '120px',
          dataIndex: 'lwdw'
        },
        {
          title: '建设单位',
          width: '120px',
          dataIndex: 'jsdw'
        },
        {
          title: '安装时间',
          width: '120px',
          dataIndex: 'azsj'
        },
        {
          title: '管理单位',
          width: '120px',
          dataIndex: 'gldw'
        },
        {
          title: '使用单位',
          width: '120px',
          dataIndex: 'sydw'
        },
        {
          title: '派出所',
          width: '120px',
          dataIndex: 'pcs'
        },
        {
          title: '属地派出所',
          width: '120px',
          dataIndex: 'sdpcs'
        },
        {
          title: '录入修改人',
          width: '120px',
          dataIndex: 'llxgr'
        },
        {
          title: '操作',
          width: '120px',
          fixed: 'right',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }],
      selectedRowKeys: [],
      dataSource: []
    }
  },
  mounted () {
    this.citySChange()
    // this.year = new Date().getFullYear()
    this.fetch({year: this.year})
    // 公安处
    this.$get('/accident/dept/list').then(res => {
      this.cityG = res.data.data
    })
  },
  methods: {
    fetch (param = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/accident/jk/list', param).then(res => {
        let newData = res.data.data
        console.log('加载列表数据', res.data.data)
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    citySChange () {
      this.$get('/dept/list', {deptId: 0}).then(res => {
        this.cityS = res.data
      })
    },
    // 点击公安处查市级
    // cityGChange (key) {
    //   this.$get('/accident/dept/list', {prentId: key}).then(res => {
    //     this.cityS = res.data.data
    //   })
    // this.unitS = ''
    // this.unitX = ''
    // },
    cityQChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityX = res.data
      })
      this.$get('/wx/dept-line/getLine', {deptId: key}).then(res => {
        this.lineData = res.data.data
      })
    },
    cityXChange (key) {
      this.cityXsId = key
    },
    onTimeChange (date, string) {
      this.year = date
    },
    // 条件查询
    search () {
      this.pagination.current = 1
      this.pagination.pageSize = 10
      let param = {}
      if (this.year !== '') {
        param.year = this.year
      }
      if (this.$refs.gacs.$el.innerText !== '') {
        param.gac = this.$refs.gacs.$el.innerText
      }
      if (this.$refs.dzss.$el.innerText !== '') {
        param.dzs = this.$refs.dzss.$el.innerText
      }
      if (this.$refs.xsqs.$el.innerText !== '') {
        param.xsq = this.$refs.xsqs.$el.innerText
      }
      if (this.$refs.xlmcs.$el.innerText !== '') {
        param.lineName = this.$refs.xlmcs.$el.innerText
      }
      this.fetch(param)
    },
    // 重置
    reset () {
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      // 清空显示
      this.lineName = ''
      this.lineData = []
      // this.year = ''
      this.unitG = ''
      this.unitS = ''
      this.cityX = []
      this.unitX = ''
      this.fetch()
    },
    // 添加功能
    handleAdd () {
      this.addEditVisiable = true
      this.title = '添加'
    },
    addEditSuccess () {
      this.addEditVisiable = false
      this.fetch()
    },
    addEditClose () {
      this.addEditVisiable = false
    },
    edit (record) {
      this.addEditVisiable = true
      this.title = '修改'
      this.$refs.oldAddEdit.setFormValues(record)
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      if (this.year !== '') {
        this.character.year = this.year
      }
      if (this.$refs.gacs.$el.innerText !== '') {
        this.character.gac = this.$refs.gacs.$el.innerText
      }
      if (this.$refs.dzss.$el.innerText !== '') {
        this.character.dzs = this.$refs.dzss.$el.innerText
      }
      if (this.$refs.xsqs.$el.innerText !== '') {
        this.character.xsq = this.$refs.xsqs.$el.innerText
      }
      if (this.$refs.xlmcs.$el.innerText !== '') {
        this.character.lineName = this.$refs.xlmcs.$el.innerText
      }
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    },
    // 删除
    handleDel () {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (!this.selectedRowKeys.length) {
        this.$notification.warning({message: '系统提示', description: '请选择需要删除的记录！', duration: 4})
        return
      }
      let that = this
      this.$confirm({
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          that.$delete('/accident/jk/delete', {idList: that.selectedRowKeys}).then(res => {
            that.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            that.selectedRowKeys = []
            that.fetch()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
