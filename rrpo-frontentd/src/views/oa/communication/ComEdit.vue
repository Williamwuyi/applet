<template>
  <a-drawer
    title="新增联系人"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="comEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='姓名' v-bind="formItemLayout">
        <a-input v-decorator="['userName',{rules: [{ required: true, message: '姓名不能为空'},{ max: 20, message: '长度不能超过20个字符'}]}]"/>
        <a-input v-decorator="['id']" type="hidden"></a-input>
      </a-form-item>
      <a-form-item label='单位' v-bind="formItemLayout">
        <a-input v-decorator="['unit']"/>
      </a-form-item>
      <a-form-item label='职务' v-bind="formItemLayout">
        <a-input v-decorator="['position']"/>
      </a-form-item>
      <a-form-item label='手机号' v-bind="formItemLayout">
        <a-input v-decorator="['telPhone',{initialValue:''},{rules: [{ required: true, message: '不能为空'},
                    { pattern: new RegExp( /^1(3|4|5|6|7|8|9)\d{9}$/ ), message: '请输入正确格式'}]}]"/>
      </a-form-item>
      <a-form-item label='电话' v-bind="formItemLayout">
        <a-input v-decorator="['phone']"/>
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input v-decorator="['email',{rules: [{ required: false},{ max: 20, message: '长度不能超过20个字符'}]}]"/>
      </a-form-item>
      <a-form-item label='所属机构'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :replaceFields="replaceFields"
          :checkStrictly="true"
          @check="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :defaultCheckedKeys="defaultCheckedKeys"
          :treeData="deptTreeData">
        </a-tree>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃修改？" @confirm="onClose" okText="确定" cancelText="取消">
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
  name: 'ComEdit',
  props: {
    comEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      deptTreeKeys: +new Date(),
      checkedKeys: [],
      replaceFields: {},
      expandedKeys: [],
      defaultCheckedKeys: [],
      deptTreeData: [],
      com: {}
    }
  },
  methods: {
    reset () {
      this.expandedKeys = this.checkedKeys = []
      this.defaultCheckedKeys = []
      this.loading = false
      this.menuTreeKey = +new Date()
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    setFormValues (user) {
      // 实现拉取表单数据并且赋值 areaId
      let fields = ['userName', 'phone', 'unit', 'position', 'telPhone', 'id', 'email', 'deptId']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      console.log(user)
      // 实现自动选定上级菜单并展开
      // 如果时公共文件夹则默认不选择树状图
      if (user.title !== '公共文件夹') {
        if (user.deptId !== '0') {
          this.defaultCheckedKeys = []
          this.defaultCheckedKeys.push(user.deptId)
          this.checkedKeys = this.defaultCheckedKeys
          this.expandedKeys = this.checkedKeys
        }
      }
    },
    // 修改
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length === 0) {
        this.$notification.warning({message: '系统提示', description: '所属机构不能为空！', duration: 4})
        return
      }
      if (checkedArr.length > 1) {
        this.$notification.warning({message: '系统提示', description: '最多只能选择一个所属机构！', duration: 4})
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (checkedArr.length) {
            console.log(checkedArr)
            this.com.deptId = checkedArr[0]
          } else {
            this.com.deptId = ''
          }
          let newcom = this.form.getFieldsValue()
          newcom.deptId = this.com.deptId
          this.$put('address', newcom).then(() => {
            this.reset()
            this.$emit('success')
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    comEditVisiable () {
      // 加载部门树状图area
      this.loading = true
      if (this.comEditVisiable) {
        this.$get('/dept').then((r) => {
          // this.replaceFields = {title: 'areaName', key: 'id'}
          this.deptTreeData = r.data.rows.children
          this.deptTreeKeys = +new Date()
          this.loading = false
        })
      }
    }
  }
}
</script>
