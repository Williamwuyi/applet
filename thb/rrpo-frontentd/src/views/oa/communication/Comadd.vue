<template>
  <a-drawer
    title="新增联系人"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="comAddVisiable"
    >
    <a-form :form="form">
      <a-form-item label='姓名' v-bind="formItemLayout">
        <a-input v-decorator="['userName',{rules: [{ required: true, message: '姓名不能为空'},{ max: 20, message: '长度不能超过20个字符'} ]}]" placeholder = "请输入姓名"/>
      </a-form-item>
<!--      <a-form-item label='单位' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['unit', {rules: [{ required: true, message: '单位不能为空'}]}]"/>-->
<!--      </a-form-item>-->
      <a-form-item label='单位'
                   :required="true"
                   style="margin-bottom: 2rem;overflow: auto"
                   v-bind="formItemLayout">
        <a-tree-select
          @change="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :replaceFields="replaceFields"
          :showSearch="false"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :load-data="onLoadData"
          :treeData="deptTreeData"
          v-decorator="['deptId',{rules: [{ required: true, message: '请选择单位!' }]}]" placeholder = "请选择单位"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label='护路职务' v-bind="formItemLayout">
        <a-select
          mode="multiple"
          :allowClear="true"
          style="width: 100%"
          v-decorator="['position',{rules: [{ required: true, message: '请选择职务' }]}]" placeholder = "请选择护路职务">
          <a-select-option v-for="(n,index) in job" :key="index" :value="n.dictId">{{n.fieldName}}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label='职务' v-bind="formItemLayout">
        <a-input v-decorator="['newPosition', {rules: [{ required: true, message: '请输入你的职务'}]}]" placeholder = "请输入职务"/>
      </a-form-item>
      <a-form-item label='手机号' v-bind="formItemLayout">
        <a-input v-decorator="['telPhone', {rules: [{ required: true, message: '手机号码不能为空'},
            { pattern: '^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$', message: '请输入正确的手机号'}
          ]}]" placeholder = "请输入正确的手机号码"/>
      </a-form-item>
      <a-form-item label='电话' v-bind="formItemLayout">
        <a-input v-decorator="['phone', {rules: [
            {min:7, max: 50, message: '请输入正确电话号码'}
          ]}]" placeholder = "请输入正确的电话号码"/>
      </a-form-item>
      <a-form-item label='邮箱' v-bind="formItemLayout">
        <a-input v-decorator="['email',{rules: [
            { type: 'email', message: '请输入正确的邮箱' },
            { max: 50, message: '长度不能超过50个字符'}
          ]}]" placeholder = "请输入正确的邮箱"/>
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
  name: 'ComAdd',
  props: {
    comAddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      job: [],
      unit: '',
      formItemLayout,
      form: this.$form.createForm(this),
      deptTreeKeys: +new Date(),
      replaceFields: {},
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
      this.unit = ''
    },
    onClose () {
      // this.reset()
      this.form.resetFields()
      this.unit = ''
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    onLoadData (treeNode) {
      return new Promise(resolve => {
        if (treeNode.dataRef.children) {
          resolve()
          return
        }
        setTimeout(() => {
          this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.deptId}).then((r) => {
            treeNode.dataRef.children = r.data.data
            this.deptTreeData = [...this.deptTreeData]
            resolve()
          })
        }, 500)
      })
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (this.checkedKeys.length !== 0) {
            // values.deptId = this.checkedKeys
          } else {
            this.$notification.warning({message: '系统提示', description: '请选择单位！', duration: 4})
            this.loading = false
            return
          }
          if (!(/^1[3456789]\d{9}$/.test(values.telPhone))) {
            this.$notification.warning({message: '系统提示', description: '请输入正确的手机号码！', duration: 4})
            this.loading = false
            return
          }
          console.log(values.position)
          values.position = ',' + values.position.join(',') + ','
          console.log('传值：', values)
          this.$post('address', values).then((res) => {
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
              this.loading = false
            } else {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
              this.loading = false
            }
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
        this.$get('/dept/queryDeptChile').then((r) => {
          this.replaceFields = { key: 'deptId', title: 'deptName', value: 'deptId' }
          this.deptTreeData = r.data.data
          this.loading = false
        })
        // 加载基础管职务
        this.$get('/dict/getListTable', {parentId: '2a6c549aa14e2f77837b1a11f6e7ad0f', pageSize: 50}).then(res => {
          this.job = res.data.data.records
        })
      }
    }
  }
}
</script>
