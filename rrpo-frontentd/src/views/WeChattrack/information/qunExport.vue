<template>
  <a-modal
    :visible="exportVisiable"
    :confirm-loading="confirmLoading"
    title='导出合格群'
    okText='确定导出'
    loading
    @cancel="() => { onClose() }"
    @ok="() => { handleSubmit() }"
  >
    <a-form :form="form">
      <a-form-item label='组织机构' v-bind="formItemLayout" v-hasPermission="'export:dept'">
        <a-tree-select
          :allowClear="true"
          :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
          :treeData="deptTreeData"
          v-decorator="['deptId']">
        </a-tree-select>
      </a-form-item>
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input v-decorator="['workName']"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
import store from '@/store'
const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 13 }
}
export default {
  name: 'qunExport',
  props: {
    exportVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      deptId: store.state.account.user.deptId,
      deptTreeData: [],
      confirmLoading: false
    }
  },
  methods: {
    reset () {
      this.loading = false
      // 清空表单
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
    },
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          this.loading = true
          let newadd = {...this.form.getFieldsValue()}
          // 如果没选组织机构且不是省铁护板，那么传自身的组织机构
          if (this.deptId !== '0') {
            newadd.deptId = this.deptId
          }
          this.$export('/wx/qun/exPort', newadd).then(() => {
            this.reset()
            this.confirmLoading = false
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  },
  watch: {
    exportVisiable () {
      if (this.exportVisiable) {
        this.$get('dept').then((r) => {
          this.deptTreeData = r.data.rows.children
        })
      }
    }
  }
}
</script>
<style>
</style>
