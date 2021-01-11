<template>
  <a-drawer
   title="修改字典"
   width=650
   plscement="right"
   :maskClosable="false"
   :closeable="false"
   :visible="basicEditLeftVisible"
   @close="onClose"
  >
    <a-form :form="form">
      <a-form-item label="字典名称" v-bind="formItemLayout">
        <a-input style="width: 100%" placeholder="请输入字典名称"
                        v-decorator="['tableName',
                        { rules: [
                          { required: true, message: '不能为空' },
                          { max: 20, message: '长度不能超过20个字符'}
                        ] }]"/>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确认放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :lading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 17 }
}
export default {
  name: 'basicEditLeft',
  props: {
    basicEditLeftVisible: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      basic: {}
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.basic = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues (basic) {
      // console.log('1', basic)
      let fields = ['tableName']
      Object.keys(basic).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = basic[key]
          this.form.setFieldsValue(obj)
        }
      })
      this.basic.dictId = basic.dictId
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          const param = {
            tableName: values.tableName,
            dictId: this.basic.dictId
          }
          this.$post('/dict/updateTable', param).then((res) => {
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
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
