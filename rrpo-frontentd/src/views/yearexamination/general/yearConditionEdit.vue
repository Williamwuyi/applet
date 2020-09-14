<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="yearEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='考核规则' v-bind="formItemLayout">
        <a-textarea
          :autosize=true
          v-decorator="['content',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入规则" allow-clear />
      </a-form-item>
<!--      <a-form-item label='最低扣加分/次' v-bind="formItemLayout">-->
<!--        <a-input-->
<!--                 v-decorator="['minGrade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
<!--      <a-form-item label='最高扣加分/次' v-bind="formItemLayout">-->
<!--        <a-input-->
<!--                 v-decorator="['maxGrade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
<!--      <a-form-item label='总扣加分' v-bind="formItemLayout">-->
<!--        <a-input-->
<!--                 v-decorator="['grade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
      <a-form-item>
        <a-input hidden v-decorator="['menusId']"/>
      </a-form-item>
      <a-input v-decorator="['parentId']" v-bind="formItemLayout" type="hidden"></a-input>
      <a-form-item label='佐证材料' v-bind="formItemLayout">
        <a-radio-group v-model="accessory" @change="onChangeSory">
          <a-radio value = "1">
            是
          </a-radio>
          <a-radio value="0">
            否
          </a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='主级选择' v-bind="formItemLayout">
        <a-radio-group v-model="value" @change="onChange">
          <a-radio value = "9c07dd761b6eec3d00bcc04cb1712ed7">
            基础工作
          </a-radio>
          <a-radio value="d4a47260da921f217ab847d1a75054d5">
            工作效果
          </a-radio>
        </a-radio-group>
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
  labelCol: { span: 6 },
  wrapperCol: { span: 15 }
}
export default {
  name: 'yearConditionEdit',
  props: {
    yearEditVisiable: {
      default: false
    },
    parentId: {
      default: ''
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      value: '',
      accessory: null
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    // 单选框
    onChange (e) {
      this.value = e.target.value
    },
    onChangeSory (e) {
      this.accessory = e.target.value
    },
    // 获取表格信息
    setFormValues (value) {
      this.value = value.parentId
      this.accessory = value.accessory + ''
      let fields = ['content', 'grade', 'maxGrade', 'menusId', 'minGrade', 'parentId']
      Object.keys(value).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = value[key]
          this.form.setFieldsValue(obj)
        }
      })
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = this.form.getFieldsValue()
          newCond.parentId = this.value
          newCond.accessory = this.accessory
          this.$post('/check/menus/addOrUpdate', newCond).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
