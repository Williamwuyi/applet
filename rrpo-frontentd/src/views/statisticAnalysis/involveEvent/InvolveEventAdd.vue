<template>
  <a-drawer
    title="添加"
    :maskClosable="false"
    width=750
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="AddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-row>
        <a-col :span="12">
            <a-form-item label='发生地市' :labelCol="{span:8}" :wrapperCol="{span:16}">
              <a-select @change="citySChange" v-decorator="['cityCsId',{rules:[{required: true, message: '请先选择市级'}]}]">
                <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.deptId" >
                  {{n.deptName}}
                </a-select-option>
              </a-select>
            </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='发生县区' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="cityQonChange" v-decorator="['cityQxId',{rules:[{required: true, message: '请先选择市级，再选县区'}]}]">
              <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.deptId" >
                {{n.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='发生乡镇村' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-decorator="['cityXc']"></a-input>
<!--            <a-select v-decorator="['cityXcId',{rules:[{required: true, message: '请先选择上级'}]}]">-->
<!--              <a-select-option v-for="(n,index) in cityJ" :key="index" :value="n.deptId">-->
<!--                {{n.deptName}}-->
<!--              </a-select-option>-->
<!--            </a-select>-->
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='派出所' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['policeId']">
<!--            <a-select-option v-for="(n,index) in cityJ" :key="index" :value="n.deptId">-->
<!--              {{n.deptName}}-->
<!--            </a-select-option>-->
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='线路' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['lineId']">
              <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
                {{n.valuee}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='地点' :labelCol="{span:8}" :wrapperCol="{span:16}">
           <a-input v-decorator="['address']"></a-input>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item  label='时间' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-date-picker show-time v-decorator="['date']" @change="dateTime">
            </a-date-picker>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='案件性质分类' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['nature']">
              <a-select-option value="0">击打列车</a-select-option>
              <a-select-option value="1">摆放障碍</a-select-option>
              <a-select-option value="2">提车钩</a-select-option>
              <a-select-option value="3">关塞门</a-select-option>
              <a-select-option value="4">拆盗掀盗</a-select-option>
              <a-select-option value="5">耕牛肇事</a-select-option>
              <a-select-option value="6">拦车断道</a-select-option>
              <a-select-option value="7">爆炸破坏</a-select-option>
              <a-select-option value="8">机动车肇事</a-select-option>
              <a-select-option value="9">高铁护网进入</a-select-option>
              <a-select-option value="10">高铁非正常停车</a-select-option>
              <a-select-option value="11">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='停车时长(分钟)' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-decorator="['dateLength']"></a-input>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='案件状态' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['status']">
              <a-select-option value="0">未破案</a-select-option>
              <a-select-option value="1">已破案</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='封闭状态' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['fbStatus']">
              <a-select-option value="0">全封闭</a-select-option>
              <a-select-option value="1">因社会管理原因造栅栏开口</a-select-option>
              <a-select-option value="2">因铁路原因造成栅栏开口</a-select-option>
              <a-select-option value="3">未封闭</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='备注' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-textarea v-decorator="['remark']" :rows="4" />
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='属地管理责任说明' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-textarea v-decorator="['content']" :rows="4" />
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='案件情况描述' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-textarea v-decorator="['ms']" :rows="4" />
          </a-form-item>
        </a-col>
      </a-row>
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
  name: 'InvolveEventAdd',
  props: {
    AddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      cityS: [], // 市级
      cityQ: [], // 县级
      cityJ: [],
      lineData: [], // 线路
      dateTimes: '',
      formItemLayout,
      form: this.$form.createForm(this)
    }
  },
  mounted () {
    this.fetch()
    this.getDictionary()
  },
  methods: {
    // 加载市级
    fetch () {
      this.$get('/dept/list', {deptId: 0}).then(res => {
        this.cityS = res.data
      })
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    // 取消
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityQ = res.data
      })
      // 选中后清空，以下联级
      this.form.setFieldsValue({
        cityQxId: '',
        cityXcId: ''
      })
    },
    // 选择县级市，加载派出所（街道）
    cityQonChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityJ = res.data
      })
      // 清空下关联
      this.form.setFieldsValue({
        cityXcId: ''
      })
    },
    dateTime (data, string) {
      this.dateTimes = string
    },
    // 获取数据字典对应的数据显示
    getDictionary () {
      const field = ['t_user', 't_line', 't_train', 't_track']
      for (let i = 0; field.length > i; i++) {
        this.$get('dict', {tableName: field[i]}).then(res => {
          // console.log('获取数据字典', res)
          if (field[i] === 't_user') {
            this.sexData = res.data.rows
          } else if (field[i] === 't_line') {
            this.lineData = res.data.rows
          } else if (field[i] === 't_train') {
            this.trainData = res.data.rows
          } else if (field[i] === 't_track') {
            this.trackData = res.data.rows
          }
        })
      }
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log('传参数', values)
          values.date = this.dateTimes
          this.$post('/accident/case/saveOrUpdate', values).then(() => {
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
