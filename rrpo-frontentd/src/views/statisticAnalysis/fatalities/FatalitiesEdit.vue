<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="fatalitiesEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-row>
        <a-col :span="12">
          <a-form-item label='发生地市' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="citySChange" v-decorator="['cityCsId']">
              <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.deptId" >
                {{n.deptName}}
              </a-select-option>
            </a-select>
            <a-input hidden v-decorator="['accidentId']"/>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='发生县区' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select @change="cityQonChange" v-decorator="['cityQxId']">
              <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.deptId" >
                  {{n.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12">
          <a-form-item label='派出所' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['policeId']">
              <a-select-option v-for="(n,index) in cityJ" :key="index" :value="n.deptId">
                {{n.deptName}}
              </a-select-option>
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
        <a-col :span="12">
          <a-form-item label='车务段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['trainId']">
              <a-select-option v-for="(n,index) in trainData" :key="index" :value="n.dictId">
                {{n.valuee}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='工务段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['trackId']">
              <a-select-option v-for="(n,index) in trackData" :key="index" :value="n.dictId">
                {{n.valuee}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='事故性质' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['nature']">
              <a-select-option value="0">一般铁路事故</a-select-option>
              <a-select-option value="1">以外事故</a-select-option>
              <a-select-option value="2">自杀事故</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='事故情形' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['conditions']">
              <a-select-option value="0">横穿铁路</a-select-option>
              <a-select-option value="1">沿铁路纵向行走</a-select-option>
              <a-select-option value="2">钻车底</a-select-option>
              <a-select-option value="3">线路上玩耍</a-select-option>
              <a-select-option value="4">其他情形</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='站内区间' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['instationSection']">
              <a-select-option value="0">站内</a-select-option>
              <a-select-option value="1">区间</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='护路模式' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['road']">
              <a-select-option value="0">专业护理区间</a-select-option>
              <a-select-option value="1">有奖义务护理区间</a-select-option>
              <a-select-option value="2">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='年龄段' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['age']">
              <a-select-option value="0">0~6岁</a-select-option>
              <a-select-option value="1">7~18岁</a-select-option>
              <a-select-option value="2">19~64岁</a-select-option>
              <a-select-option value="3">64岁以上</a-select-option>
              <a-select-option value="4">不详</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='身份判断' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['identity']">
              <a-select-option value="0">中小学生</a-select-option>
              <a-select-option value="1">精神病患者</a-select-option>
              <a-select-option value="2">其他残障人员</a-select-option>
              <a-select-option value="3">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='性别' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['sex']">
              <a-select-option v-for="(n,index) in sexData" :key="index" :value="n.dictId">
                {{n.valuee}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='居住地' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['jzd']">
              <a-select-option value="0">本村</a-select-option>
              <a-select-option value="1">本乡</a-select-option>
              <a-select-option value="2">本县</a-select-option>
              <a-select-option value="3">其他</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='封闭程度' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['closed']">
              <a-select-option value="0">全封闭</a-select-option>
              <a-select-option value="1">因社会管理原因造栅栏开口</a-select-option>
              <a-select-option value="2">因铁路原因造成栅栏开口</a-select-option>
              <a-select-option value="3">未封闭</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='离铁路远近' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['distance']">
              <a-select-option value="0">500米以内</a-select-option>
              <a-select-option value="1">500米到2.5公里</a-select-option>
              <a-select-option value="2">2.5公里以上</a-select-option>
              <a-select-option value="3">不详</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='电脑分析系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['dnxs']">
              <a-select-option value="0.1">0.1</a-select-option>
              <a-select-option value="0.2">0.2</a-select-option>
              <a-select-option value="0.3">0.3</a-select-option>
              <a-select-option value="0.4">0.4</a-select-option>
              <a-select-option value="0.5">0.5</a-select-option>
              <a-select-option value="0.6">0.6</a-select-option>
              <a-select-option value="0.7">0.7</a-select-option>
              <a-select-option value="0.8">0.8</a-select-option>
              <a-select-option value="0.9">0.9</a-select-option>
              <a-select-option value="1.0">1.0</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='公安分析系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-select v-decorator="['gaxs']">
              <a-select-option value="0.1">0.1</a-select-option>
              <a-select-option value="0.2">0.2</a-select-option>
              <a-select-option value="0.3">0.3</a-select-option>
              <a-select-option value="0.4">0.4</a-select-option>
              <a-select-option value="0.5">0.5</a-select-option>
              <a-select-option value="0.6">0.6</a-select-option>
              <a-select-option value="0.7">0.7</a-select-option>
              <a-select-option value="0.8">0.8</a-select-option>
              <a-select-option value="0.9">0.9</a-select-option>
              <a-select-option value="1.0">1.0</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='电脑调节系数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-decorator="['dntjxs']"></a-input>
          </a-form-item>
        </a-col>
        <a-col span="12">
          <a-form-item label='属地管理死亡数' :labelCol="{span:8}" :wrapperCol="{span:16}">
            <a-input v-decorator="['deathToll']"></a-input>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃修改？" @confirm="onClose" okText="确定" cancelText="取消">
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
  name: 'FatalitiesEdit',
  props: {
    fatalitiesEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      cityS: [], // 市级
      cityQ: [], // 县级
      cityJ: [], // 街道
      sexData: [], // 性别
      lineData: [], // 线路
      trainData: [], // 车务段
      trackData: [], // 工务段
      dateTimes: '',
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
      this.$notification.warning({message: '系统提示', description: '县区已被清空,请重新选择！', duration: 1})
      // 选中后清空，以下联级
      this.form.setFieldsValue({
        cityQxId: '',
        policeId: ''
      })
    },
    // 选择县级市，加载派出所（街道）
    cityQonChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityJ = res.data
      })
      this.$notification.warning({message: '系统提示', description: '下级被清空！', duration: 1})
      // 清空下关联
      this.form.setFieldsValue({
        policeId: ''
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
    setFormValues (user) {
      console.log('编辑;', user)
      // 实现拉取表单数据并且赋值 areaId
      let fields = ['accidentId', 'date', 'cityCsId', 'cityQxId', 'policeId', 'lineId',
        'address', 'trainId', 'trackId', 'nature', 'conditions',
        'instationSection', 'road', 'closed', 'sex', 'age', 'identity', 'jzd', 'distance', 'dnxs', 'dntjxs', 'gaxs', 'deathToll']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      setTimeout(() => {
        this.form.setFieldsValue({
          cityCsId: user.deptCs.deptId, // 市级
          cityQxId: user.deptQx.deptId, //  区县
          // policeId: user.deptPolice.deptId, //  派出所
          lineId: user.dictXl.dictId, // 线路
          trainId: user.dictCwd.dictId, //  车务段
          trackId: user.dictGwd.dictId //   工务段
        })
        if (user.deptCs.deptId !== '') {
          this.$get('/dept/list', {deptId: user.deptCs.deptId}).then(res => {
            this.cityQ = res.data
          })
        }
        if (user.deptQx.deptId !== '') {
          this.$get('/dept/list', {deptId: user.deptQx.deptId}).then(res => {
            this.cityJ = res.data
          })
        }
      }, 5)
    },
    // 修改后保存
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          if (this.dateTimes !== null) {
            values.date = this.dateTimes
          }
          this.$post('/accident/accident/saveOrUpdate', values).then(res => {
            console.log(res)
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
              this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
            } else {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
              this.loading = false
              this.$emit('close')
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
