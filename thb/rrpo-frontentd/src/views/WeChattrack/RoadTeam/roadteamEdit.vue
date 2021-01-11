<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    :zIndex="100"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="NoticeEditVisiable"
    >
    <a-form :form="form">
      <a-form-item label='姓  名' v-bind="formItemLayout">
        <a-input
          v-decorator="['name',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
      <a-form-item label='组织机构' v-bind="formItemLayout">
        <a-tree-select
          :allowClear="true"
          tree-data-simple-mode
          :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
          :treeData="deptTreeData"
          :load-data="getDept"
          :placeholder="defailtMsg"
          @change="onDeptChange"
          v-decorator="['deptId']"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label='家庭地址' v-bind="formItemLayout">
        <a-input
          v-decorator="['address',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
      <a-form-item label='身份证号码' v-bind="formItemLayout">
        <a-input
          v-decorator="['idCard',
                   {rules: [{ required: true, message: '身份证号码不能为空'},
                   { max: 18, message: '长度不能超过18个字符'}
                  ]}]" />
      </a-form-item>
      <a-form-item label='电话号码' v-bind="formItemLayout">
        <a-input
          v-decorator="['phone',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
      <a-form-item label='政治面貌' v-bind="formItemLayout">
        <a-cascader  change-on-select
                     style="width: 105px;"
                     @change="onChangecascader"
                     :options="options"
                     placeholder="未选择"
                     ref="cascader"
        />
      </a-form-item>
      <a-form-item label='管辖区派出所' v-bind="formItemLayout">
        <a-input
          v-decorator="['police',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
      <a-form-item label='线路' v-bind="formItemLayout">
        <a-select v-model="line"  @change="onChansgeSelect">
          <a-select-option v-for="(n,index) in BdLine" :key="index" :value="n.lineId">
            {{n.lineName}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='护路里程' v-bind="formItemLayout">
        <a-input
          v-decorator="['km',
                   {rules: [
                    { required: true, message: '护路里程'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='责任路段' v-bind="formItemLayout">
        <a-row>
          <a-col :span="4">
            <a-input
              v-decorator="['lu1',
                   {rules: [
                    { required: false, message: '线路'}
                  ]}]"/>
          </a-col>
          <a-col :span="4" offset="1">
            <a-input-number
              v-decorator="['lu2',
                   {rules: [
                    { required: false, message: '线路'}
                  ]}]"/>
          </a-col>
          <a-col :span="4" offset="1">
            <a-input-number
              v-decorator="['lu3',
                   {rules: [
                    { required: false, message: '线路'}
                  ]}]"/>
          </a-col>
          <a-col :span="4" offset="1">
            <a-input-number
              v-decorator="['lu4',
                   {rules: [
                    { required: false, message: '线路'}
                  ]}]"/>
          </a-col>
          <a-col :span="4" offset="1">
            <a-input-number
              v-decorator="['lu5',
                   {rules: [
                    { required: false, message: '线路'}
                  ]}]"/>
          </a-col>
        </a-row>
        <a-row><a-icon type="info-circle" style="color: #2eabff;margin-right: 3px"/>当前责任路段为：{{zrluduan}}</a-row>
      </a-form-item>
      <a-form-item label='是否使用微信' v-bind="formItemLayout">
        <a-radio-group v-decorator="['isWx']">
          <a-radio value="是">
            是
          </a-radio>
          <a-radio value="否">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item>
        <a-input hidden v-decorator="['hlId']"/>
      </a-form-item>
      <a-form-item label='是否加入群' v-bind="formItemLayout">
        <a-radio-group v-decorator="['isQun']">
          <a-radio value="是">
            是
          </a-radio>
          <a-radio value="否">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
       <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'NoticeEdit',
  props: {
    NoticeEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      line: '',
      zrluduan: '',
      deptTreeData: [],
      BdLine: [],
      userDept: [],
      user: '',
      defailtMsg: '',
      dept: '',
      formItemLayout,
      newedite: {},
      form: this.$form.createForm(this),
      //  政治面貌
      options: [
        {
          value: '群众',
          label: '群众'
        },
        {
          value: '党员',
          label: '党员'
        }, {
          value: '专业护路队员',
          label: '专业护路队员'
        }
      ]
    }
  },
  mounted () {
    this.getDept()
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
      this.$refs.cascader.sValue = []
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    onDeptChange (value) {
      if (value !== undefined) {
        this.defailtMsg = value
        this.newedite.deptId = value
        this.getdeptId(value)
      } else {
        this.getDeptId()
        this.BdLine = []
        this.newedite.line = undefined
      }
    },
    getDeptId () {
      //  等于最后一级
      this.defailtMsg = undefined
      if (this.user.xiang !== null) {
        this.newedite.deptId = this.user.xiang
      } else if (this.user.quXian !== null) {
        this.newedite.deptId = this.user.quXian
      } else if (this.user.shi !== null) {
        this.newedite.deptId = this.user.shi
      }
      this.getdeptId(this.newedite.deptId)
    },
    // 组织机构查询线路
    getdeptId (deptId) {
      this.$get('/wx/dept-line/childGetLine', {deptId: deptId}).then(res => {
        this.BdLine = res.data.data
      })
    },
    onChansgeSelect (value) {
      this.BdLine.forEach(t => {
        if (t.lineId === value) {
          // 身份
          this.line = value
          this.newedite.line = value
          if (t.nature) {
            this.newedite.proper = t.nature
          }
        }
      })
    },
    // 分页获取
    getDept (treeNode) {
      if (treeNode) {
        return new Promise(resolve => {
          setTimeout(() => {
            this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.id}).then((r) => {
              let newData = r.data.data
              newData.forEach(t => {
                this.deptTreeData.push(
                  { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
                )
              })
            })
            resolve()
          }, 300)
        })
      } else {
        this.$get('/dept/queryDeptChile').then((r) => {
          let newData = r.data.data
          newData.forEach(t => {
            this.deptTreeData.push(
              { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
            )
          })
        })
      }
    },
    // 下拉的值
    onChangecascader (key) {
      this.newedite.face = key[0]
    },
    setFormValues (user) {
      this.user = user
      this.defailtMsg = ''
      this.zrluduan = user.luDuan
      this.userDept = []
      this.getDeptId()
      if (user.shi !== null) {
        this.defailtMsg = user.shiName
      }
      if (user.quXian !== null) {
        this.defailtMsg = user.shiName + '/' + user.quXianName
      }
      if (user.xiang !== null) {
        this.defailtMsg = user.shiName + '/' + user.quXianName + '/' + user.xiangName
      }
      let fields = ['name', 'address', 'idCard', 'phone', 'police', 'luDuan', 'isWx', 'isQun', 'hlId', 'lu1', 'lu2', 'lu3', 'lu4', 'lu5', 'km']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      if (user.line) {
        this.line = user.line
        this.newedite.line = user.line
      }
      this.$nextTick(function () {
        this.$refs.cascader.sValue = [user.face]
      })
    },
    // 修改
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let neweRdite = {...this.form.getFieldsValue(), ...this.newedite}
          this.$post('/wx/people/saveOrUpdate', neweRdite).then(res => {
            if (res.data.status !== 0) {
              this.loading = false
              this.reset()
              this.$emit('success')
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
              this.loading = false
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
