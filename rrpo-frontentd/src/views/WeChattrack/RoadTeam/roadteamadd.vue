/* eslint-disable no-undef */
<template>
  <a-drawer
    :zIndex="100"
    title="添加"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="roadteamaddVisiable"
    >
    <a-form :form="form">
          <a-form-item label='姓  名' v-bind="formItemLayout">
            <a-input
              v-decorator="['name',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
<!--          <a-form-item label='组织机构' v-bind="formItemLayout">-->
<!--            <a-tree-select-->
<!--              :allowClear="true"-->
<!--              tree-data-simple-mode-->
<!--              :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"-->
<!--              :treeData="deptTreeData"-->
<!--              :load-data="getDept"-->
<!--              @change="onChansgeThree"-->
<!--              v-decorator="['deptId',-->
<!--                   {rules: [-->
<!--                    { required: true, message: '不能为空'}-->
<!--                  ]}]">-->
<!--            </a-tree-select>-->
<!--          </a-form-item>-->
          <a-form-item label='家庭地址' v-bind="formItemLayout">
            <a-input
              v-decorator="['address',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
      <a-form-item label='所在群聊' v-bind="formItemLayout">
        <a-input v-model="qunName" placeholder="该街道下未搜索到合格群" :read-only="true"/>
      </a-form-item>
          <a-form-item label='身份证号码' v-bind="formItemLayout">
            <a-input
              placeholder="请正确填写身份证号码，否则将添加失败"
              v-decorator="['idCard',
                   {rules: [{ required: true, message: '身份证号码不能为空'},
                   { pattern: new RegExp( /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/ ), message: '请输入正确格式'},
                   { max: 18, message: '长度不能超过18个字符'}
                  ]}]" />
          </a-form-item>
          <a-form-item label='电话号码' v-bind="formItemLayout">
            <a-input
              v-decorator="['phone',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
          </a-form-item>
          <a-form-item label='政治面貌' v-bind="formItemLayout">
            <a-cascader  change-on-select
                         style="width: 105px;"
                         @change="onChangecascader"
                         :options="options"
                         placeholder="未选择"
                         ref="cascader"
            />
          </a-form-item>
      <a-form-item label='管辖区派出所' v-bind="formItemLayout">
        <a-input
          v-decorator="['police',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]" />
      </a-form-item>
<!--          <a-form-item label='线路' v-bind="formItemLayout">-->
<!--            <a-select  v-model="newNoticea.line"  @change="onChansgeSelect">-->
<!--              <a-select-option v-for="(n,index) in BdLine" :key="index" :value="n.lineId">-->
<!--                {{n.lineName}}-->
<!--              </a-select-option>-->
<!--            </a-select>-->
<!--          </a-form-item>-->
      <a-form-item label='护路里程' v-bind="formItemLayout">
        <a-input
          v-decorator="['km',
                   {rules: [
                    { required: true, message: '护路里程'}
                  ]}]"/>
      </a-form-item>
          <a-form-item label='责任路段' :labelCol="{span: 4}"
                       :wrapperCol="{span: 18, offset: 1}">
            <a-row>
              <a-col :span="3">
                <a-input
                  v-decorator="['lu1',
                   {rules: [
                    { required: true, message: '线路'}
                  ]}]"/>
              </a-col>
              <a-col :span="3" offset="1">
                <a-input-number
                  v-decorator="['lu2',
                   {rules: [
                    { required: true, message: '线路'}
                  ]}]"/>
              </a-col>
              <a-col :span="3" offset="1">
                <a-input-number
                  v-decorator="['lu3',
                   {rules: [
                    { required: true, message: '线路'}
                  ]}]"/>
              </a-col>
              <a-col :span="3" offset="1">
                <a-input-number
                  v-decorator="['lu4',
                   {rules: [
                    { required: true, message: '线路'}
                  ]}]"/>
              </a-col>
              <a-col :span="3" offset="1">
                <a-input-number
                  v-decorator="['lu5',
                   {rules: [
                    { required: true, message: '线路'}
                  ]}]"/>
              </a-col>
            </a-row>
            <a-row><a-icon type="info-circle" style="color: #2eabff;margin-right: 3px"/>实例： k 100 100 100 100</a-row>
          </a-form-item>
          <a-form-item label='是否使用微信' v-bind="formItemLayout">
            <a-radio-group v-decorator="['isWx']">
              <a-radio value="是">
                是
              </a-radio>
              <a-radio value="否">
                否
              </a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item label='是否加入群' v-bind="formItemLayout">
            <a-radio-group v-decorator="['isQun']">
              <a-radio value="是">
                是
              </a-radio>
              <a-radio value="否">
                否
              </a-radio>
            </a-radio-group>
          </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="是否确认取消？" @confirm="onClose"  okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
       <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 11 }
}
export default {
  name: 'roadteamadd',
  props: {
    roadteamaddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      deptTreeData: [],
      qunName: '',
      BdLine: [],
      form: this.$form.createForm(this),
      newNoticea: {},
      //  政治面貌
      options: [
        {
          value: '群众',
          label: '群众'
        },
        {
          value: '党员',
          label: '党员'
        }
      ],
      //  属性
      optionsr: [
        {
          value: '义务护路队员',
          label: '义务护路队员'
        },
        {
          value: '志愿者',
          label: '志愿者'
        }, {
          value: '高铁志愿者',
          label: '高铁志愿者'
        }, {
          value: '专业护路队员',
          label: '专业护路队员'
        }
      ],
      value: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      // 清空
      this.newNoticea = {}
      // 清空表单
      this.form.resetFields()
      this.$refs.cascader.sValue = []
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.newNoticea = {}
    },
    getQun (record) {
      this.$get('/wx/qun/getQunByDept', {deptId: record}).then(res => {
        if (res.data.data !== null) {
          this.qunName = res.data.data.wxName
          this.newNoticea.qunId = res.data.data.wxId
        }
      })
    },
    // 组织机构下拉变化
    onChansgeThree (change) {
      if (change !== undefined) {
        this.getdeptId(change)
      } else {
        this.BdLine = []
        this.newNoticea.line = undefined
      }
    },
    // 组织机构查询线路
    getdeptId (deptId) {
      this.$get('/wx/dept-line/childGetLine', {deptId: deptId}).then(res => {
        this.BdLine = res.data.data
      })
    },
    onChansgeSelect (value) {
      this.BdLine.forEach(t => {
        if (t.lineId === value) {
          // 身份
          if (t.nature) {
            this.newNoticea.line = value
            this.newNoticea.proper = t.nature
          }
        }
      })
    },
    // 下拉的值
    onChangecascader (key) {
      this.newNoticea.face = key[0]
    },
    // 分页获取
    getDept (treeNode) {
      if (treeNode) {
        return new Promise(resolve => {
          setTimeout(() => {
            this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.id}).then((r) => {
              let newData = r.data.data
              newData.forEach(t => {
                this.deptTreeData.push(
                  { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
                )
              })
            })
            resolve()
          }, 300)
        })
      } else {
        this.$get('/dept/queryDeptChile').then((r) => {
          let newData = r.data.data
          newData.forEach(t => {
            this.deptTreeData.push(
              { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
            )
          })
        })
      }
    },
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newadd = {...this.form.getFieldsValue(), ...this.newNoticea}
          this.$post('/wx/people/saveOrUpdate', newadd).then(res => {
            if (res.data.status !== 0) {
              this.reset()
              this.$emit('success')
            } else {
              this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
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
    roadteamaddVisiable () {
      this.getDept()
    }
  }
}
</script>
<style>
</style>
