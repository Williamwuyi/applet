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
<!--                  <a-cascader  change-on-select-->
<!--                               @change="onChangecascader"-->
<!--                               :options="options"-->
<!--                               placeholder="未选择"-->
<!--                               ref="cascader"-->
<!--                  />-->
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
      :rowKey="(record)=> record.caseId"
      :pagination="pagination"
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
      <template slot="operation" slot-scope="text, record" v-if="editShow">
        <a-icon  type="setting" style="margin-left:8px;color:#158BD2" @click="edit(record)" title="修改"></a-icon>
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
  </a-card>
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
      editShow: true,
      character: {},
      cityCsId: '', // 县区单位条件查询
      cityQsId: '', // 单位条件查询
      cityS: [],
      cityQ: [],
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
          title: '发生乡镇村',
          width: '110px',
          dataIndex: 'cityXc'
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
      this.$get('/accident/case/listPage', params).then(res => {
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
      this.AddVisiable = true
    },
    handleFataAdd () {
      this.AddVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.reload()
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
      this.reload()
    },
    hanleaEditclose () {
      this.EditVisiable = false
    },
    // 锁定记录
    handleLock () {
      // this.editShow = false
      const param = {
        accidentId: this.selectedRowKeys,
        status: 1
      }
      this.$post('/accident/case/lock', param).then(res => {
        if (res.data.status === 1) {
          this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
          this.reload()
        }
      })
    },
    handleRelieve () {
      // this.editShow = true
      const param = {
        accidentId: this.selectedRowKeys,
        status: 0
      }
      this.$post('/accident/case/lock', param).then(res => {
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
          console.log('----------------', rowd)
          that.$delete('/accident/case/deleteByLists/' + rowd.join(',')).then(res => {
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
