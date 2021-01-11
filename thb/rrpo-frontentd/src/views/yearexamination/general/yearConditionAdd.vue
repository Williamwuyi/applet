<template>
  <a-drawer
    title="新增考核规则"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="yearCondVisiable"
    >
    <a-form :form="form">
      <a-form-item label='考核规则' v-bind="formItemLayout">
        <a-textarea
          :autoSize=true
          v-decorator="['content',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
          placeholder="请输入规则" allow-clear />
      </a-form-item>
<!--      <a-form-item label='最低扣加分/次' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['minGrade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
<!--      <a-form-item label='最高扣加分/次' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['maxGrade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
<!--      <a-form-item label='总扣加分' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['grade',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'},-->
<!--                    { pattern: new RegExp( /^(\-|\+)?\d+(\.\d+)?$/ ), message: '请输入正确格式'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
      <a-form-item label='佐证材料' v-bind="formItemLayout">
        <a-radio-group  v-model="cond.accessory" @change="onChangeSory">
          <a-radio value = "1">
            是
          </a-radio>
          <a-radio value="0">
           否
          </a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label='主级选择' v-bind="formItemLayout">
        <a-radio-group v-model="cond.parentId" @change="onChange">
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
      <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
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
  name: 'yearConditionAdd',
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
      cond: {
        accessory: '1'
      }
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
      this.cond = {}
    },
    onClose () {
      this.reset()
      this.cond = {}
      this.form.resetFields()
      this.$emit('close')
    },
    // 单选框
    onChange (e) {
      this.cond.parentId = e.target.value
    },
    onChangeSory (e) {
      this.cond.accessory = e.target.value
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newCond = {...this.cond, ...this.form.getFieldsValue()}
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
