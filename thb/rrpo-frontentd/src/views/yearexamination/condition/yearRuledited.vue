<template>
    <a-modal
      :visible="yearCondVisiable"
      title="需要定制的规则"
      width="1200px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="onClose"
      @ok="handleSubmit"
    >
      <div style="max-height: 650px; overflow: auto">
<!--    表单  -->
      <a-form :form="form">
        <a-form-item label='时间'
                     v-bind="formItemLayout"
                     >
          <a-date-picker
            show-time
            :default-value="moment(dateTime, 'YYYY')"
            format = 'YYYY' @change="onChangeTime" @ok="onTimeChange" />
        </a-form-item>
        <a-form-item label='工作项得绑定'  v-bind="formItemLayout">
          <a-checkbox-group :default-value="shArr" >
            <a-checkbox :value="item.standardId" @change="onChange" v-for="item in CheckArr" :key="item.standardId">
              {{item.name}}
            </a-checkbox>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
<!--   折叠框   -->
      <a-collapse accordion  @change="changeActivekey"  :bordered="false">
        <a-collapse-panel v-for="item in arr" :key="item.id" :header= "item.name">
          <a-button @click="add">增加</a-button>
          <a-button @click="deleteS(item)">删除</a-button>
          <a-table
                       :data-source="item.list"
                       :columns="columns"
                       :loading="loading"
                       :scroll="{ y: 550 }"
                       :rowKey="(record)=> record.menusYearId"
                       :pagination="false"
                       :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
                     >
            <template slot="operation" slot-scope="text, record">
              <a-icon  type="setting" theme="twoTone"  twoToneColor="#4a9ff5"  @click="edit(record)"  title="绑定或修改"></a-icon>
            </template>
                     </a-table>
        </a-collapse-panel>
      </a-collapse>
      <!-- 增加 -->
      <year-edited-add
        :yearCondVisiable="yeareditdVisiable"
        @success="addSuccess"
        @close="addClose"
        ref="oldadd"
      />
      <!-- 修改 -->
      <yearedit-edit
        :yearEditVisiable="yearEditVisiable"
      @close="Editonclose"
        @success="successEdit"
        ref="oldEdit"
      />
      </div>
    </a-modal>
</template>
<script>
import yearEditedAdd from './yearEditedAdd'
import yeareditEdit from './yeareditEdit'
import moment from 'moment'
// 表单
const formItemLayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 20 }
}
export default {
  name: 'yearRuledited',
  components: {yearEditedAdd, yeareditEdit},
  props: {
    yearCondVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      selectedRowKeys: [],
      selectedRowKeys1: [],
      yeareditdVisiable: false,
      yearEditVisiable: false,
      yearId: '',
      dwpnId: '',
      arr: [],
      CheckArr: [],
      shArr: [],
      newData: [],
      //  工作
      dataSource: [],
      dataSource1: [],
      // 时间
      dateTime: '',
      // 分页
      pagination: {
        total: 0,
        pageSize: 8, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['8', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  mounted () {
    this.getCheck()
  },
  computed: {
    columns () {
      return [{
        title: '规则',
        dataIndex: 'content',
        width: '80%'
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    // 时间
    moment,
    // 渲染规则
    fach () {
      this.loading = true
      this.$get('/check/menus-year/list', {yearId: this.yearId}).then(res => {
        this.arr = res.data.data
        this.loading = false
      })
    },
    reset () {
      this.loading = false
      this.form.resetFields()
      this.shArr = []
      this.selectedRowKeys = this.selectedRowKeys1 = []
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    // 时间
    onTimeChange (date) {
      var d = new Date(date)
      this.dateTime = d.getFullYear() + ''
    },
    onChangeTime (value, dateString) {
      this.dateTime = dateString
    },
    // 多选框数据渲染
    getCheck () {
      this.$get('/check/menus/list').then(res => {
        this.CheckArr = res.data.data
      })
    },
    // 多选框
    onChange (even) {
      let neatr = even.target.value
      if (even.target.checked) {
        this.checkAdd(neatr)
      } else {
        this.newData.forEach(t => {
          if (neatr === t.menusId) {
            this.deldeCheck(t.bindId)
          }
        })
      }
    },
    // 添加考核项
    checkAdd (checkedValues) {
      this.$post('/check/year-bind-menus/addOrUpdate', {menusId: checkedValues, yearId: this.yearId}).then(res => {
        this.fach()
        this.checkEdit()
      })
    },
    // 删除考核项
    deldeCheck (checkedValues) {
      this.$delete('/check/year-bind-menus/delete', {id: checkedValues}).then(res => {
        this.fach()
        this.checkEdit()
      })
    },
    // 修改考核项
    checkEdit (checkedValues) {
      this.$get('/check/year-bind-menus/list', {yearId: this.yearId}).then(res => {
        if (res.data.data !== []) {
          this.newData = res.data.data
          this.newData.forEach(t => {
            this.shArr.push(t.menusId)
          })
        }
      })
    },
    // 获取 请求详情绑定
    getYear (item) {
      this.reset()
      this.yearId = item.yearId
      this.dateTime = item.yearDate
      this.fach()
      this.checkEdit()
    },
    // 折叠框
    changeActivekey (key) {
      this.dwpnId = key
      // this.faech()
    },
    // 表格选择
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    deleteS (key) {
      let that = this
      that.$confirm({
        title: `是否删除考核规则，一经删除永远不会恢复?`,
        centered: true,
        onOk () {
          that.$delete('/check/menus-year/deleteByLists', {listS: that.selectedRowKeys}).then(() => {
            that.$message.success('删除成功')
            that.fach()
          })
        },
        onCancel () {
          that.fach()
        }
      })
    },
    // 表格添加
    add () {
      this.yeareditdVisiable = true
      let item = {
        parentId: this.dwpnId,
        yearId: this.yearId
      }
      this.$refs.oldadd.get(item)
    },
    addSuccess () {
      this.yeareditdVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fach()
    },
    addClose () {
      this.yeareditdVisiable = false
    },
    // 表格修改
    edit (res) {
      this.yearEditVisiable = true
      this.$refs.oldEdit.setFormValues(res)
    },
    Editonclose () {
      this.yearEditVisiable = false
    },
    successEdit () {
      this.yearEditVisiable = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
      this.fach()
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          // 修改标题
          var fromdata = {yearDate: this.dateTime}
          fromdata.yearId = this.yearId
          this.$post('/check/year/saveOrUpdate', fromdata).then(res => {
            if (res.data.message === '操作成功') {
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
            }
          })
          // 确定考核项接口
          // this.$get('/check/year/ok', {yearId: this.yearId})
          // 绑定规则
          let pramase = {}
          pramase.menusId = [...this.selectedRowKeys]
          pramase.yearID = this.yearId
          this.$post('/check/menus-year/add', pramase).then(() => {
            this.reset()
            this.$emit('success')
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
<style scoped>
  .tit {
    width: 400px;
  }
</style>
