<template>
  <a-drawer
    title="新增联系人"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="comAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='姓名' v-bind="formItemLayout">
        <a-input v-decorator="['userName',{rules: [{ required: true, message: '姓名不能为空'},{ max: 20, message: '长度不能超过20个字符'} ]}]"/>
      </a-form-item>
      <a-form-item label='单位' v-bind="formItemLayout">
        <a-input v-decorator="['unit']"/>
      </a-form-item>
      <a-form-item label='职务' v-bind="formItemLayout">
        <a-input v-decorator="['position']"/>
      </a-form-item>
      <a-form-item label='手机号' v-bind="formItemLayout">
        <a-input v-decorator="['telPhone',{rules: [{ required: true, message: '手机不能为空'},]}]"/>
      </a-form-item>
      <a-form-item label='电话' v-bind="formItemLayout">
        <a-input v-decorator="['phone',{rules: [{ required: false},]}]"/>
<!--        { pattern: new RegExp( /^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/ ), message: '请输入正确格式'}-->
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input v-decorator="['email',{rules: [{ required: false}]}]"/>
      </a-form-item>
      <a-form-item label='所属机构'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :checkStrictly="true"
          @check="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :treeData="deptTreeData">
        </a-tree>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
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
  name: 'ComAdd',
  props: {
    comAddVisiable: {
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
      expandedKeys: [],
      deptTreeData: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.deptTreeKeys = +new Date()
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
      console.log('选中的id ', checkedKeys)
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    // 提交
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length === 0) {
        this.$message.error('上级部门不能为空')
        return
      }
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级部门，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (checkedArr.length) {
            values.deptId = checkedArr[0]
          } else {
            values.deptId = ''
          }
          if (!(/^1[3456789]\d{9}$/.test(values.telPhone))) {
            this.$message.error('请输入正确的手机号码')
            return
          }
          console.log(values)
          this.$post('address', values).then(() => {
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
    comAddVisiable () {
      this.loading = true
      if (this.comAddVisiable) {
        this.$get('dept').then((r) => {
          this.deptTreeData = r.data.rows.children
          this.loading = false
        })
      }
    }
  }
}
</script>
