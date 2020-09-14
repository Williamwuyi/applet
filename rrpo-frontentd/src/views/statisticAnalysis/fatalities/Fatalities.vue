<template>
  <a-card :bordered="false" class="card-area">
    <div>
      <div :class="advanced ? 'search' : null">
        <!-- 搜索区域 -->
        <a-form layout="horizontal">
          <div :class="advanced ? null: 'fold'">
            <a-row >
              <a-col :md="5" :sm="20">
                <a-form-item
                  label="单位:"
                  :labelCol="{span: 5}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select v-model="character.cityCsId" @change="citySChange">
                    <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.deptId" >
                      {{n.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20">
                <a-form-item
                  label="区县单位 :"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <a-select v-model="character.cityQxId">
                    <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.deptId" >
                      {{n.deptName}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="21" >
                <a-form-item
                  label="发生时间:"
                  :labelCol="{span: 6}"
                  :wrapperCol="{span: 15, offset: 1}">
                  <range-date @change="onTimeChange" ref="creatTime" :allowClear="false"></range-date>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="20">
                <span style="margin-left: 20px">
                  <a-button type="primary" @click="search">查询</a-button>
                  <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                </span>
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
    <a-button class="editable-add-btn" @click="handleAdd" >
      添加
    </a-button>
    <a-button class="editable-add-btn" @click="handleDel">
      删除
    </a-button>
      <a-button class="editable-add-btn" @click="handleLock">
        锁定记录
      </a-button>
      <a-button class="editable-add-btn" @click="handleRelieve">
        解锁记录
       </a-button>
    <a-table style="overflow: auto;width: 100%"
      :data-source="dataSource"
      :scroll={x:100}
      :columns="columns"
      :rowKey="(record)=> record.accidentId"
      :pagination="pagination"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      @change="handleTableChange"
    >
      <template slot="nature" slot-scope="text, record">
        <span v-if="record.nature === '0' ">一般铁路事故</span>
        <span v-else-if="record.nature === '1' ">以外事故</span>
        <span v-else-if="record.nature === '2' ">自杀事故</span>
      </template>
      <template slot="conditions" slot-scope="text, record">
        <span v-if="record.conditions === '0' ">横穿铁路</span>
        <span v-else-if="record.conditions === '1' ">沿铁路纵向行走</span>
        <span v-else-if="record.conditions === '2' ">钻车底</span>
        <span v-else-if="record.conditions === '3' ">线路上玩耍</span>
        <span v-else-if="record.conditions === '4' ">其他情形</span>
      </template>
      <template slot="instationSection" slot-scope="text, record">
        <span v-if="record.instationSection === '0' ">站内</span>
        <span v-else-if="record.instationSection === '1' ">区间</span>
      </template>
      <template slot="road" slot-scope="text, record">
      <span v-if="record.road === '0' ">专业护理区间</span>
      <span v-else-if="record.road === '1' ">有奖义务护理区间</span>
      <span v-else-if="record.road === '2' ">其他</span>
    </template>
      <template slot="age" slot-scope="text, record">
        <span v-if="record.road === '0' ">0~6岁</span>
        <span v-else-if="record.road === '1' ">7~18岁</span>
        <span v-else-if="record.road === '2' ">19~64岁</span>
        <span v-else-if="record.road === '3' ">64岁以上</span>
        <span v-else-if="record.road === '4' ">不详</span>
      </template>
      <template slot="identity" slot-scope="text, record">
        <span v-if="record.road === '0' ">中小学生</span>
        <span v-else-if="record.road === '1' ">精神病患者</span>
        <span v-else-if="record.road === '2' ">其他残障人员</span>
        <span v-else-if="record.road === '3' ">其他</span>
        <span v-else-if="record.road === '4' ">不详</span>
      </template>
      <template slot="jzd" slot-scope="text, record">
        <span v-if="record.road === '0' ">本村</span>
        <span v-else-if="record.road === '1' ">本乡</span>
        <span v-else-if="record.road === '2' ">本县</span>
        <span v-else-if="record.road === '3' ">其他</span>
      </template>
      <template slot="closed" slot-scope="text, record">
        <span v-if="record.road === '0' ">全封闭</span>
        <span v-else-if="record.road === '1' ">因社会管理原因造栅栏开口</span>
        <span v-else-if="record.road === '2' ">因铁路原因造成栅栏开口</span>
        <span v-else-if="record.road === '3' ">未封闭</span>
      </template>
      <template slot="sex" slot-scope="text, record">
<!--        <span v-if="record.sex === '0' ">全封闭</span>-->
        <span v-if="record.sex === '1' ">男</span>
        <span v-else-if="record.sex === '2' ">女</span>
<!--        <span v-else-if="record.sex === '3' ">未封闭</span>-->
      </template>
      <template slot="distance" slot-scope="text, record">
        <span v-if="record.road === '0' ">500米以内</span>
        <span v-else-if="record.road === '1' ">500米到2.5公里</span>
        <span v-else-if="record.road === '2' ">2.5公里以上</span>
        <span v-else-if="record.road === '3' ">不详</span>
      </template>
      <template slot="operation" slot-scope="text, record" v-if="editShow">
        <a-icon  type="setting" style="margin-left:8px;color:#158BD2" @click="edit(record)" title="修改"></a-icon>
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
  </a-card>
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
      selectedRowKeys: [],
      advanced: false,
      lineData: [], // 线路
      trainData: [], // 车务段
      trackData: [], // 工务段
      character: {},
      cityCsId: '', // 县区单位条件查询
      cityQsId: '', // 单位条件查询
      cityS: [],
      cityQ: [],
      editShow: true,
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
          width: '100px',
          dataIndex: 'dictXl.valuee'
        },
        {
          title: '地点',
          width: '100px',
          dataIndex: 'address'
        },
        {
          title: '车务段',
          width: '150px',
          dataIndex: 'dictCwd.valuee'
        },
        {
          title: '工务段',
          width: '150px',
          dataIndex: 'dictGwd.valuee'
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
          dataIndex: 'sex',
          width: '100px',
          scopedSlots: {customRender: 'sex'}
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
          width: '150px',
          scopedSlots: {customRender: 'dnxs'}
        },
        {
          title: '公安分析系数',
          width: '150px',
          dataIndex: 'gaxs'
        },
        {
          title: '电脑调节系数',
          dataIndex: 'dntjxs',
          width: '150px',
          scopedSlots: {customRender: 'dntjxs'}
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
  inject: ['reload'],
  mounted () {
    this.fetch()
    this.$get('/dept/list', {deptId: 0}).then(res => {
      this.cityS = res.data
    })
  },
  methods: {
    fetch (params = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/accident/accident/listPage', params).then(res => {
        let newData = res.data.data
        console.log('加载列表数据', newData)
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
        for (let key in this.dataSource) {
          let solck = this.dataSource[key].statu // 1为锁定，0为解锁
          if (solck === 1) {
            this.editShow = false
          } else if (solck === 0) {
            this.editShow = true
          }
        }
      })
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityQ = res.data
      })
      // 选中后清空，以下联级
      this.cityQxId = ''
    },
    // 条件查询
    search () {
      this.fetch({
        ...this.character
      })
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空时间选择
      this.$refs.creatTime.reset()
      this.fetch()
    },
    // 状态查询
    onChangecascader (value) {
      this.character.status = value
    },
    // 选中时间
    onTimeChange (value) {
      if (value) {
        let start = value[0] + ` 00:00:00`
        let end = value[1] + ` 23:59:59`
        this.character.startTime = start
        this.character.endTime = end
      }
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
      this.reload()
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
      this.reload()
    },
    hanleaEditclose () {
      this.fatalitiesEditVisiable = false
    },
    // 锁定记录
    handleLock () {
      const param = {
        accidentId: this.selectedRowKeys,
        status: 1
      }
      this.$post('/accident/accident/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.reload()
        }
      })
    },
    handleRelieve () {
      const param = {
        accidentId: this.selectedRowKeys,
        status: 0
      }
      this.$post('/accident/accident/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.reload()
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
            that.reload()
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
@import "../../../../static/less/Common";
.ant-table-body {  //样式穿透
    overflow-x: auto !important;
  }
</style>
