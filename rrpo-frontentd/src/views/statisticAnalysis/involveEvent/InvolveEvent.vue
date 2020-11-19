<template>
  <div style="width: 100%;min-height: 780px">
  <div>
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form>
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="5" :sm="10" v-has-any-permission="'communication:fatalitiesUnit'">
                <a-form-item
                  label="单位:"
                  :labelCol="{span: 4}"
                  :wrapperCol="{span: 18, offset: 1}">
                  <a-select @change="citySChange" v-model="unitG">
                    <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.id" >
                      {{n.dept.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20" v-has-any-permission="'communication:fatalitiesUnit'">
                <a-form-item
                  label="市级单位 :"
                  :labelCol="{span: 8}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select @change="cityQChange" v-model="unitQ">
                    <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.dept.deptId" >
                      {{n.dept.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20" v-has-any-permission="'communication:fatalitiesUnit'">
                <a-form-item
                  label="区县单位 :"
                  :labelCol="{span: 8}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select @change="cityXChange" v-model="unitX">
                    <a-select-option v-for="(n,index) in cityX" :key="index" :value="n.deptId" >
                      {{n.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20">
                <a-form-item
                  label="线路 :"
                  :labelCol="{span: 8}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select v-model="line" @change="lineChange">
                    <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
                      {{n.fieldName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
              <a-row>
              <a-col :md="5" :sm="20" >
                <a-form-item label="开始时间:" :labelCol="{span: 4}" :wrapperCol="{span: 18, offset: 1}">
                  <a-month-picker placeholder="开始时间" @change="onStartChange" v-model="restStartTime" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20" >
                <a-form-item label="结束时间:" :labelCol="{span: 8}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="结束时间" @change="onEndChange" v-model="resetTime" style="width: 100%" />
                </a-form-item>
              </a-col>

              <a-col :md="5" :sm="20">
                <a-form-item
                  label="事件类型 :"
                  :labelCol="{span: 8}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select v-model="genre" @change="genreChange">
                    <a-select-option value="0">击打列车</a-select-option>
                    <a-select-option value="1">摆放障碍</a-select-option>
                    <a-select-option value="2">提车钩</a-select-option>
                    <a-select-option value="3">关塞门</a-select-option>
                    <a-select-option value="4">拆盗掀盗</a-select-option>
                    <a-select-option value="5">耕牛肇事</a-select-option>
                    <a-select-option value="6">拦车断道</a-select-option>
                    <a-select-option value="7">爆炸破坏</a-select-option>
                    <a-select-option value="8">机动车肇事</a-select-option>
                    <a-select-option value="9">高铁护网进入</a-select-option>
                    <a-select-option value="10">高铁非正常停车</a-select-option>
                    <a-select-option value="11">其他</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20" style="margin-top: 5px;margin-left: 60px">
                  <a-button type="primary" @click="search">查询</a-button>
                  <a-button style="margin-left: 20px" @click="reset">重置</a-button>
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
      <a-button class="btn" @click="handleLock" style="background-color: #CDAD00;color: white">
        锁定
      </a-button>
      <a-button class="btn" @click="handleRelieve" style="background-color: #CD853F;color: white">
        解锁
      </a-button>
    <a-table style="overflow: auto;width: 100%"
      :data-source="dataSource"
      :scroll={x:100,y:580}
      :columns="columns"
      :rowKey="(record)=> record.caseId"
      :pagination="pagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      @change="handleTableChange"
    >
<!--      案件名称-->
      <template slot="natures" slot-scope="text, record">
        <span v-if="record.nature === '0' ">击打列车</span>
        <span v-else-if="record.nature === '1' ">摆放障碍</span>
        <span v-else-if="record.nature === '2' ">提车钩</span>
        <span v-else-if="record.nature === '3' ">关塞门</span>
        <span v-else-if="record.nature === '4' ">拆盗掀盗</span>
        <span v-else-if="record.nature === '5' ">耕牛肇事</span>
        <span v-else-if="record.nature === '6' ">拦车断道</span>
        <span v-else-if="record.nature === '7' ">爆炸破坏</span>
        <span v-else-if="record.nature === '8' ">机动车肇事</span>
        <span v-else-if="record.nature === '9' ">高铁护网进入</span>
        <span v-else-if="record.nature === '10' ">高铁非正常停车</span>
        <span v-else-if="record.nature === '11' ">其他</span>
      </template>
<!--      案件状态-->
      <template slot="statuss" slot-scope="text, record">
        <span v-if="record.status === '0' ">未破案</span>
        <span v-else-if="record.status === '1' ">已破案</span>
      </template>
<!--      封闭状态-->
      <template slot="fbStatuss" slot-scope="text, record">
        <span v-if="record.fbStatus === '0' ">全封闭</span>
        <span v-else-if="record.fbStatus === '1' ">因社会管理原因造栅栏开口</span>
        <span v-else-if="record.fbStatus === '2' ">因铁路原因造成栅栏开口</span>
        <span v-else-if="record.fbStatus === '3' ">未封闭</span>
      </template>
      <template slot="operation" slot-scope="text, record" >
<!--        <a-icon v-if="record.statu !== 1" type="setting" style="margin-left:8px;color:#158BD2" @click="edit(record)" title="修改"></a-icon>-->
        <a @click="edit(record)" style="color: #158BD2" v-if="record.statu !== 1">修改</a>
        <span v-else>锁定</span>
      </template>
    </a-table>
    <involve-event-add
      @success="handleFataAdd"
      @close="hanleaddclose"
      :AddVisiable="AddVisiable"
      ref="oldadd"
    />
    <involve-event-edit
      @success="handleEdit"
      @close="hanleaEditclose"
      :EditVisiable="EditVisiable"
      ref="oldedit"
    />
  </div>
  </div>
</template>
<script>
import InvolveEventAdd from './InvolveEventAdd'
import InvolveEventEdit from './InvolveEventEdit'
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'InvolveEvent',
  components: {InvolveEventAdd, InvolveEventEdit, RangeDate},
  data () {
    return {
      AddVisiable: false,
      EditVisiable: false,
      selectedRowKeys: [],
      advanced: false,
      editShow: null,
      cityCsId: null, // 县区单位条件查询
      cityQsId: null, // 单位条件查询
      cityXsId: null, // 单位条件查询
      cityS: [],
      cityQ: [],
      cityX: [],
      unitG: '',
      unitQ: '',
      unitX: '',
      line: '',
      genre: '',
      resetTime: null,
      restStartTime: null,
      linekey: null,
      genrekey: null,
      lineData: [],
      startTime: null,
      endTime: null,
      sortedInfo: null,
      character: {},
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      dataSource: [],
      count: 2,
      loading: false,
      columns: [
        {
          title: '发生月份',
          width: '100px',
          dataIndex: 'month'
        },
        {
          title: '发生时间',
          width: '120px',
          dataIndex: 'date'
        },
        {
          title: '发生地市',
          width: '100px',
          dataIndex: 'deptCs.deptName'
        },
        {
          title: '发生县区',
          width: '100px',
          dataIndex: 'deptQx.deptName'
        },
        {
          title: '发生乡镇村',
          width: '110px',
          dataIndex: 'deptXc.deptName'
        },
        {
          title: '派出所',
          width: '130px',
          dataIndex: 'deptPolice.deptName'
        },
        {
          title: '线路',
          width: '100px',
          dataIndex: 'dictXl.fieldName'
        },
        {
          title: '地点',
          width: '100px',
          dataIndex: 'address'
        },
        {
          title: '案件名称',
          width: '150px',
          dataIndex: 'nature',
          scopedSlots: {customRender: 'natures'}
        },
        {
          title: '停车时间',
          width: '150px',
          dataIndex: 'dateLength'
        },
        {
          title: '封闭状态',
          dataIndex: 'fbStatus',
          width: '150px',
          scopedSlots: {customRender: 'fbStatuss'}
        },
        {
          title: '案件状态',
          dataIndex: 'status',
          width: '150px',
          scopedSlots: {customRender: 'statuss'}
        },
        {
          title: '地方责任说明',
          dataIndex: 'content',
          width: '150px'
        },
        {
          title: '备注',
          dataIndex: 'remark',
          width: '120px'
        },
        {
          title: '案件情况描述',
          width: '120px',
          dataIndex: 'ms'
        },
        {
          title: '操作',
          width: '80px',
          fixed: 'right',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  mounted () {
    this.fetch()
    this.$get('/accident/dept/list').then(res => {
      this.cityS = res.data.data
    })
    this.$get('/dict/getListTable', {parentId: '1867989d0aaaf82f79b34070cc77d766', pageSize: 50}).then(res => {
      this.lineData = res.data.data.records
    })
  },
  methods: {
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/accident/case/listPage', params).then(res => {
        let newData = res.data.data
        console.log('加载列表数据', newData)
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.loading = true
      this.$get('/accident/dept/list', {prentId: key}).then(res => {
        console.log('查市级', res.data)
        this.cityQ = res.data.data
        this.loading = false
      })
      this.cityCsId = key
      this.unitQ = ''
      this.unitX = ''
    },
    cityQChange (key) {
      this.loading = true
      console.log('++++', key)
      this.$get('/dept/list', {deptId: key}).then(res => {
        console.log('查区县', res.data)
        this.cityX = res.data
        this.loading = false
      })
      this.cityQsId = key
      this.unitX = ''
    },
    cityXChange (key) {
      this.cityXsId = key
    },
    lineChange (key) {
      this.linekey = key
    },
    genreChange (key) {
      this.genrekey = key
    },
    // 条件查询
    search () {
      this.fetch({
        policeId: this.cityCsId,
        cityLevelId: this.cityQsId,
        quDeptId: this.cityXsId,
        lineId: this.linekey,
        nature: this.genrekey,
        startDate: this.startTime,
        endDate: this.endTime
      })
    },
    // 重置
    reset () {
      // 清空显示
      this.unitQ = ''
      this.unitG = ''
      this.unitX = ''
      this.genre = ''
      this.line = ''
      // 清空值
      this.cityQsId = null
      this.cityCsId = null
      this.cityXsId = null
      this.linekey = null
      this.genrekey = null
      // 清空时间选择
      this.resetTime = null
      this.restStartTime = null
      this.fetch()
    },
    // 选择开始时间
    onStartChange (value, date) {
      this.startTime = date.toString() + '-00'
      this.restTime = null // 结束时间
    },
    // 选择结束时间
    onEndChange (value, date) {
      console.log('结束时间', value, '--', date)
      let year = date.toString().substring(0, 4)
      let months = date.toString().substring(5, 7)
      let month = parseInt(months) + 1
      let dateTime = year + '-' + month + '-00'
      this.endTime = dateTime
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
      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    },
    // 添加功能
    handleAdd () {
      this.AddVisiable = true
    },
    handleFataAdd () {
      this.AddVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fetch()
    },
    hanleaddclose () {
      this.AddVisiable = false
    },
    // 修改功能
    edit (record) {
      this.EditVisiable = true
      // 掉用子组件方法加传值
      this.$refs.oldedit.setFormValues(record)
    },
    handleEdit () {
      this.EditVisiable = false
      this.fetch()
    },
    hanleaEditclose () {
      this.EditVisiable = false
    },
    // 锁定记录
    handleLock () {
      const param = {
        caseId: this.selectedRowKeys,
        status: 1
      }
      this.$post('/accident/case/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.fetch()
        }
      })
    },
    handleRelieve () {
      const param = {
        caseId: this.selectedRowKeys,
        status: 0
      }
      this.$post('/accident/case/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.fetch()
        }
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
      console.log(that.selectedRowKeys)
      this.$confirm({
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let rowd = that.selectedRowKeys
          that.$delete('/accident/case/deleteByLists/' + rowd.join(',')).then(res => {
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
<style lang="less" scoped>
  .btn{
    margin-bottom: 5px;
    margin-right: 10px;
  }
</style>
