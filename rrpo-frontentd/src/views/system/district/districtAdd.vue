<template>
  <a-drawer
    title="新增地区"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="districtAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='地区/街道' v-bind="formItemLayout">
        <a-input v-model="region.areaName"
                 v-decorator="['districtName',
                   {rules: [
                    { required: true, message: '地区不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='邮编' v-bind="formItemLayout">
        <a-input v-model="region.areaCode"
                 v-decorator="['areaCode',
                   {rules: [
                   { required: false,
                   pattern: new RegExp(/^[0-9]{6}$/),
                   message: '请输入正确的邮编'
                   },
                   ]}]"/>
      </a-form-item>
      <a-form-item label='直属地区/街道'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :checkStrictly="true"
          :replaceFields="replaceFields"
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
  labelCol: { span: 5 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'districtAdd',
  props: {
    districtAddVisiable: {
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
      deptTreeData: [],
      replaceFields: {},
      region: {}
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
      console.log(checkedKeys)
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    // 提交
    handleSubmit () {
      let checkedArr = Object.is(this.checkedKeys.checked, undefined) ? this.checkedKeys : this.checkedKeys.checked
      if (checkedArr.length > 1) {
        this.$message.error('最多只能选择一个上级部门，请修改')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (checkedArr.length) {
            this.region.parentId = checkedArr[0]
          } else {
            // 没选默认为一级地区
            this.region.parentId = 0
          }
          this.$post('/area', this.region).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    districtAddVisiable () {
      if (this.districtAddVisiable) {
        this.$get('/area').then((r) => {
          // 替换后台获取的字段
          this.replaceFields = { title: 'areaName', key: 'id' }
          this.deptTreeData = r.data.rows.children
        })
      }
    }
  }
}
</script>
