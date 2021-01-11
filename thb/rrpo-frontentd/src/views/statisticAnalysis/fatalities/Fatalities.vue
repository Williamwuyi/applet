<template>
    <div style="width: 100%;min-height: 770px">
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form style="">
          <div :class="advanced ? null: 'fold'">
            <a-row>
              <a-col :md="3" :sm="10" v-has-any-permission="'communication:fatalitiesUnit'">
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
              <a-col :md="4" :sm="10" v-has-any-permission="'communication:fatalitiesUnit'">
                <a-form-item
                  label="市级单位"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select @change="cityQChange" v-model="unitQ">
                    <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.dept.deptId" >
                      {{n.dept.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="10" v-has-any-permission="'communication:fatalitiesUnit'">
                <a-form-item
                  label="区县单位 :"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select @change="cityXChange" v-model="unitX">
                    <a-select-option v-for="(n,index) in cityX" :key="index" :value="n.deptId" >
                      {{n.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" >
                <a-form-item label="开始时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="开始时间" @change="onStartChange" v-model="restStartTime">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" >
                <a-form-item label="结束时间:" :labelCol="{span: 6}" :wrapperCol="{span: 15, offset: 1}">
                  <a-month-picker placeholder="结束时间" @change="onEndChange" v-model="restTime">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="20" style="margin-top: 3px; margin-left: 10px">
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
             :rowKey="(record)=> record.accidentId"
             :pagination="pagination"
             :loading="loading"
             :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
             @change="handleTableChange"
    >
      <template slot="nature" slot-scope="text, record">
        <span v-if="record.nature === 'A' ">一般铁路事故</span>
        <span v-else-if="record.nature === 'B' ">意外事故</span>
        <span v-else-if="record.nature === 'C' ">自杀事故</span>
      </template>
      <template slot="conditions" slot-scope="text, record">
        <span v-if="record.conditions === 'A' ">横穿铁路</span>
        <span v-else-if="record.conditions === 'B' ">沿铁路纵向行走</span>
        <span v-else-if="record.conditions === 'C' ">钻车底</span>
        <span v-else-if="record.conditions === 'D' ">线路上玩耍</span>
        <span v-else-if="record.conditions === 'E' ">其他情形</span>
      </template>
      <template slot="instationSection" slot-scope="text, record">
        <span v-if="record.instationSection === 'A' ">站内</span>
        <span v-else-if="record.instationSection === 'B' ">区间</span>
      </template>
      <template slot="road" slot-scope="text, record">
      <span v-if="record.road === 'A' ">专业护理区间</span>
      <span v-else-if="record.road === 'B' ">有奖义务护理区间</span>
      <span v-else-if="record.road === 'C' ">其他</span>
    </template>
      <template slot="age" slot-scope="text, record">
        <span v-if="record.age === 'A' ">0~6岁</span>
        <span v-else-if="record.age === 'B' ">7~18岁</span>
        <span v-else-if="record.age === 'C' ">19~64岁</span>
        <span v-else-if="record.age === 'D' ">64岁以上</span>
        <span v-else-if="record.age === 'E' ">不详</span>
      </template>
      <template slot="identity" slot-scope="text, record">
        <span v-if="record.identity === 'A' ">中小学生</span>
        <span v-else-if="record.identity === 'B' ">精神病患者</span>
        <span v-else-if="record.identity === 'C' ">其他残障人员</span>
        <span v-else-if="record.identity === 'D' ">其他</span>
        <span v-else-if="record.identity === 'E' ">不详</span>
      </template>
      <template slot="jzd" slot-scope="text, record">
        <span v-if="record.jzd === 'A' ">本村</span>
        <span v-else-if="record.jzd === 'B' ">本乡</span>
        <span v-else-if="record.jzd === 'C' ">本县</span>
        <span v-else-if="record.jzd === 'D' ">其他</span>
      </template>
      <template slot="closed" slot-scope="text, record">
        <span v-if="record.closed === 'A' ">全封闭</span>
        <span v-else-if="record.closed === 'B' ">因社会管理原因造栅栏开口</span>
        <span v-else-if="record.closed === 'C' ">因铁路原因造成栅栏开口</span>
        <span v-else-if="record.closed === 'D' ">未封闭</span>
      </template>
      <template slot="sex" slot-scope="text, record">
        <span v-if="record.sex === '1' ">男</span>
        <span v-else-if="record.sex === '2' ">女</span>
      </template>
      <template slot="distance" slot-scope="text, record">
        <span v-if="record.distance === 'A' ">500米以内</span>
        <span v-else-if="record.distance === 'B' ">500米到2.5公里</span>
        <span v-else-if="record.distance === 'C' ">2.5公里以上</span>
        <span v-else-if="record.distance === 'D' ">不详</span>
      </template>
      <template slot="operation" slot-scope="text, record">
<!--        <a-icon v-if="record.statu !== 1" type="setting" style="margin-left:8px;color:#158BD2" @click="edit(record)" title="修改"></a-icon>-->
        <a @click="edit(record)" style="color: #158BD2" v-if="record.statu !== 1">修改</a>
        <span v-else>锁定</span>
      </template>
    </a-table>
    <fatalities-add
      @success="handleFataAdd"
      @close="hanleaddclose"
      :fatalitiesAddVisiable="fatalitiesAddVisiable"
      ref="oldadd"
    />
    <fatalities-edit
      @success="handleEdit"
      @close="hanleaEditclose"
      :fatalitiesEditVisiable="fatalitiesEditVisiable"
      ref="oldedit"
    />
    </div>
</template>
<script>
import FatalitiesAdd from './FatalitiesAdd'
import FatalitiesEdit from './FatalitiesEdit'
import RangeDate from '@/components/datetime/RangeDate'
export default {
  name: 'Fatalities',
  components: {FatalitiesAdd, FatalitiesEdit, RangeDate},
  data () {
    return {
      fatalitiesAddVisiable: false,
      fatalitiesEditVisiable: false,
      loading: false,
      selectedRowKeys: [],
      advanced: false,
      lineData: [], // 线路
      trainData: [], // 车务段
      trackData: [], // 工务段
      cityCsId: null, // 县区单位条件查询
      cityQsId: null, // 单位条件查询
      cityXsId: null, // 单位条件查询
      unitG: '',
      unitQ: '',
      unitX: '',
      cityS: [],
      cityQ: [],
      cityX: [],
      startTime: null,
      endTime: null,
      restStartTime: null,
      restTime: null,
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
          title: '派出所',
          width: '130px',
          dataIndex: 'deptPolice.deptName'
        },
        {
          title: '线路',
          width: '150px',
          dataIndex: 'dictXl.fieldName'
        },
        {
          title: '地点',
          width: '100px',
          dataIndex: 'address'
        },
        {
          title: '车务段',
          width: '150px',
          dataIndex: 'dictCwd.fieldName'
        },
        {
          title: '工务段',
          width: '150px',
          dataIndex: 'dictGwd.fieldName'
        },
        {
          title: '事故性质',
          dataIndex: 'nature',
          width: '150px',
          scopedSlots: {customRender: 'nature'}
        },
        {
          title: '事故情形',
          dataIndex: 'conditions',
          width: '150px',
          scopedSlots: {customRender: 'conditions'}
        },
        {
          title: '站区间',
          dataIndex: 'instationSection',
          width: '80px',
          scopedSlots: {customRender: 'instationSection'}
        },
        {
          title: '护路模式',
          dataIndex: 'road',
          width: '150px',
          scopedSlots: {customRender: 'road'}
        },
        {
          title: '封闭程度',
          dataIndex: 'closed',
          width: '150px',
          scopedSlots: {customRender: 'closed'}
        },
        {
          title: '性别',
          dataIndex: 'sexDict.fieldName',
          width: '100px'
        },
        {
          title: '年龄段',
          dataIndex: 'age',
          width: '120px',
          scopedSlots: {customRender: 'age'}
        },
        {
          title: '身份',
          width: '120px',
          dataIndex: 'identity',
          scopedSlots: {customRender: 'identity'}
        },
        {
          title: '居住地',
          dataIndex: 'jzd',
          width: '100px',
          scopedSlots: {customRender: 'jzd'}
        },
        {
          title: '离路情况',
          dataIndex: 'distance',
          width: '150px',
          scopedSlots: {customRender: 'distance'}
        },
        {
          title: '电脑分析系数',
          dataIndex: 'dnxs',
          width: '150px'
        },
        {
          title: '公安分析系数',
          width: '150px',
          dataIndex: 'gaxs'
        },
        {
          title: '电脑调节系数',
          dataIndex: 'dntjxs',
          width: '150px'
        },
        {
          title: '换算伤亡',
          width: '150px',
          dataIndex: 'deathToll'
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
    this.loading = true
    // 单位公安处
    this.$get('/accident/dept/list').then(res => {
      this.cityS = res.data.data
      this.loading = false
    })
  },
  methods: {
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/accident/accident/listPage', params).then(res => {
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
    // 条件查询
    search () {
      this.fetch({
        cityLevelId: this.cityQsId,
        quDeptId: this.cityXsId,
        policeId: this.cityCsId,
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
      // 清空值
      this.cityQsId = null
      this.cityCsId = null
      this.cityXsId = null
      this.restTime = null // 结束时间
      this.restStartTime = null // 开始时间
      this.fetch()
    },
    // 选择开始时间
    onStartChange (value, date) {
      this.startTime = date.toString()
      this.restTime = null // 结束时间
    },
    // 选择结束时间
    onEndChange (value, date) {
      console.log('结束时间', value, '--', date)
      let year = date.toString().substring(0, 4)
      let months = date.toString().substring(5, 7)
      let month = parseInt(months)
      let dateTime = year + '-' + month
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
      this.fatalitiesAddVisiable = true
    },
    handleFataAdd () {
      this.fatalitiesAddVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fetch()
    },
    hanleaddclose () {
      this.fatalitiesAddVisiable = false
    },
    // 修改功能
    edit (record) {
      this.fatalitiesEditVisiable = true
      // 掉用子组件方法加传值
      this.$refs.oldedit.setFormValues(record)
    },
    handleEdit () {
      this.fatalitiesEditVisiable = false
      this.fetch()
    },
    hanleaEditclose () {
      this.fatalitiesEditVisiable = false
    },
    // 锁定记录
    handleLock () {
      this.loading = true
      const param = {
        accidentId: this.selectedRowKeys,
        status: 1
      }
      this.$post('/accident/accident/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.fetch()
          this.loading = false
        }
      })
    },
    handleRelieve () {
      this.loading = true
      const param = {
        accidentId: this.selectedRowKeys,
        status: 0
      }
      this.$post('/accident/accident/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.fetch()
          this.loading = false
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
          that.$delete('/accident/accident/deleteByLists/' + rowd.join(',')).then(res => {
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
