<template>
  <a-drawer
    title="修改地区"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="districtEdit"
    >
    <a-form :form="form">
      <a-form-item label='地区/街道' v-bind="formItemLayout">
        <a-input v-model="region.areaName"
                 v-decorator="['areaName',
                   {rules: [
                    { required: true, message: '地名不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item>
        <a-input type="hidden" v-decorator="['id']"  v-bind="formItemLayout" />
      </a-form-item>
      <a-form-item label='直属地区/街道'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :checkStrictly="true"
          :replaceFields="replaceFields"
          :defaultCheckedKeys="defaultCheckedKeys"
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
  name: 'districtEdit',
  props: {
    districtEdit: {
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
      defaultCheckedKeys: [],
      replaceFields: {},
      region: {}
    }
  },
  methods: {
    reset () {
      this.expandedKeys = this.checkedKeys = []
      this.loading = false
      this.deptTreeKeys = +new Date()
      this.defaultCheckedKeys = []
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
    // 获取
    setFormValues (district) {
      // 实现拉取表单数据并且赋值
      let fields = ['areaName', 'areaCode', 'rank', 'id']
      Object.keys(district).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = district[key]
          this.form.setFieldsValue(obj)
        }
      })
      // 实现自动选定上级菜单并展开
      if (district.id !== '0') {
        this.defaultCheckedKeys = []
        this.defaultCheckedKeys.push(district.id)
        // 默认展开树节点
        console.log(district.parentId)
        console.log(this.defaultCheckedKeys)
        this.checkedKeys = this.defaultCheckedKeys
        this.expandedKeys = this.checkedKeys
      }
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
          this.region = this.form.getFieldsValue()
          console.log(this.region)
          if (checkedArr.length) {
            console.log(checkedArr[0])
            this.region.parentId = checkedArr[0]
          } else {
            this.region.parentId = 0
          }
          this.$put('/area', this.region).then(() => {
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
    districtEdit () {
      if (this.districtEdit) {
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
