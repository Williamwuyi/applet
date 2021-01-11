<template>
  <a-drawer
    title="新增组织机构"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="deptAddVisiable"
    >
    <a-form :form="form">
      <a-form-item label='机构名称' v-bind="formItemLayout">
        <a-input v-model="dept.deptName"
                 v-decorator="['deptName',
                   {rules: [
                    { required: true, message: '部门名称不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='机构等级' v-bind="formItemLayout">
        <a-cascader  change-on-select
                     style="width: 105px;"
                     @change="onChangecascader"
                     :options="options"
                     placeholder="未选择"
                     ref="cascader"
        />
      </a-form-item>
      <a-form-item label='机构排序' v-bind="formItemLayout">
        <a-input-number v-model="dept.orderNum" style="width: 100%"/>
      </a-form-item>
      <a-form-item label='所属机构'
                   style="margin-bottom: 2rem"
                   v-bind="formItemLayout">
<!--        <a-tree-->
<!--          :key="deptTreeKeys"-->
<!--          :checkable="true"-->
<!--          :checkStrictly="true"-->
<!--          @check="handleCheck"-->
<!--          @expand="handleExpand"-->
<!--          :expandedKeys="expandedKeys"-->
<!--          :treeData="deptTreeData">-->
<!--        </a-tree>-->
        <a-tree
          :key="deptTreeKeys"
          :checkable="true"
          :checkStrictly="true"
          @check="handleCheck"
          @expand="handleExpand"
          :expandedKeys="expandedKeys"
          :replaceFields="replaceFields"
          :load-data="onLoadData"
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
  name: 'DeptAdd',
  props: {
    deptAddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      deptTreeKeys: +new Date(),
      form: this.$form.createForm(this),
      dept: {},
      replaceFields: {},
      checkedKeys: [],
      expandedKeys: [],
      deptTreeData: [],
      //  状态下拉
      options: [
        {
          value: '0',
          label: '省级'
        },
        {
          value: '1',
          label: '市级'
        },
        {
          value: '2',
          label: '区级'
        },
        {
          value: '3',
          label: '街道'
        },
        {
          value: '4',
          label: '公安处'
        }
      ]
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.deptTreeKeys = +new Date()
      this.expandedKeys = this.checkedKeys = []
      this.$refs.cascader.sValue = []
      this.dept = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    // 下拉的值
    onChangecascader (key) {
      this.dept.rank = key[0]
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
            this.dept.parentId = checkedArr[0]
          } else {
            this.dept.parentId = ''
          }
          if (this.dept.rank === '0') {
            this.dept.parentId = '-1'
          }
          this.$post('dept', {
            ...this.dept
          }).then(() => {
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
    deptAddVisiable () {
      if (this.deptAddVisiable) {
        // this.$get('dept').then((r) => {
        //   this.deptTreeData = r.data.rows.children
        //   console.log(r.data.rows.children)
        // })
        this.$get('/dept/queryDeptChile').then((r) => {
          this.replaceFields = { title: 'deptName', key: 'deptId' }
          this.deptTreeData = r.data.data
          this.loading = false
        })
      }
    }
  }
}
</script>
