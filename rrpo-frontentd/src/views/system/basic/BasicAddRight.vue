<template>
  <a-drawer
    title="添加字典项"
    width=650
    placement="right"
    :maskClosable="false"
    :closable="false"
    :visible="basicAddRightVisible"
    @close="onClose"
  >
    <a-form :form="form" v-bind="formItemLayout">
<!--      <a-form-item-->
<!--        label="字典名称"-->
<!--        width=650>-->
<!--        <a-select  style="width: 100%" placeholder="请选择字典名称" :allowClear="true"-->
<!--                   v-decorator="['msgbasic',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'}-->
<!--                  ]}]">-->
<!--          <a-select-option  v-for="(item, index) in arr" :value="item.dictId" :key="index">-->
<!--            {{item.tableName}}-->
<!--          </a-select-option>-->
<!--        </a-select>-->
<!--      </a-form-item>-->
      <a-form-item label='字典项名称' v-bind="formItemLayout">
        <a-input style="width: 100%" placeholder="请输入字典项名称"
                        v-decorator="['fieldName',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
<!--      <a-form-item label='系数占比' v-bind="formItemLayout" v-if="count">-->
<!--        <a-input style="width: 100%" placeholder="此项为‘封闭程度’字典项内容"-->
<!--                 v-decorator="['coefficient',-->
<!--                   {rules: [-->
<!--                    { required: false, message: '不能为空'}-->
<!--                  ]}]"/>-->
<!--      </a-form-item>-->
      <a-form-item label="线路性质" width=650 v-bind="formItemLayout" v-if="route">
        <a-select  style="width: 100%" placeholder="请选择线路性质" :allowClear="true"
                   v-decorator="['nature',
                   {rules: [
                    { required: false, message: '不能为空'}
                  ]}]">
          <a-select-option value="义务护路队员">
            义务护路队员
          </a-select-option>
          <a-select-option value="高铁志愿者">
            高铁志愿者
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='公里数' v-bind="formItemLayout" v-if="route">
        <a-input style="width: 100%" placeholder="请输入公里数"
                 v-decorator="['revenue',
                   {rules: [
                    { required: false, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='值' v-bind="formItemLayout" v-if="show">
        <a-input-number style="width: 100%" placeholder="请输入值"
                 v-decorator="['value',
                   {rules: [
                    { required: false, message: '不能为空'}
                  ]}]"/>
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
  labelCol: { span: 4 },
  wrapperCol: { span: 17 }
}
export default {
  name: 'basicAddRight',
  props: {
    basicAddRightVisible: {
      default: false
    }
  },
  data () {
    return {
      count: false,
      route: false,
      show: false,
      loading: false,
      arr: [],
      formItemLayout,
      form: this.$form.createForm(this),
      msgbasic: ''
    }
  },
  mounted () {
    // this.searchSelect()
  },
  watch: {
    basicAddRightVisible () {
      this.searchSelect()
    }
  },
  methods: {
    searchSelect () {
      this.$get('/dict/getListTable', {
        parentId: '0'
      }).then((res) => {
        // console.log(res)
        this.arr = res.data.data.records
        this.loading = false
      })
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    // 判断左侧是点击哪个字典
    searchId (key) {
      console.log(key)
      this.msgbasic = key
      if (key === '96b76029ac74b1ad1c73642b6ddb9dab') {
        this.count = true
      } else {
        this.count = false
      }
      if (key === '1867989d0aaaf82f79b34070cc77d766') {
        this.route = true
      } else {
        this.route = false
      }
      if (key === '873293cce2e47b64bf6ab3b6b007f436' || key === '96b76029ac74b1ad1c73642b6ddb9dab' || key === 'dc5b3760be9b7a9cd3497f38abc2fc12' || key === '3c539bb9577d64f75d5e2039268037ea' || key === '7db3ce75ef3dd8b92830c325a81e10b4' || key === 'b730c43d0c0723636433a8f325650632') {
        this.show = true
      } else {
        this.show = false
      }
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          const param = {
            parentId: this.msgbasic,
            fieldName: values.fieldName,
            // coefficient: values.coefficient,
            nature: values.nature,
            revenue: values.revenue,
            value: values.value
          }
          this.$post('/dict/addTable', param).then((res) => {
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

<style>

</style>
