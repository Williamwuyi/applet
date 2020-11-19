<template>
  <div>
    <a-drawer
      :visible="rewardEditVisiable"
      width=40%
      title="修改湖南省铁路护路联防工作’一事一奖‘审批表"
      @close="rewclone"
      :maskClosable="false"
    >
      <a-form :form="form">
        <a-row>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="姓名"
            >
              <a-input
                v-decorator="[
                'name',
                { rules: [{ required: true, message: '请输入姓名' }] },
              ]"
              />
              <a-input
                v-decorator="[
                'id',
                { rules: [{ required: true, message: '请输入姓名' }] },
              ]"
                style="display: none"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="年龄"
            >
              <a-input
                type="number"
                :max="99"
                :min="0"
                v-decorator="[
                'age',
                { rules: [{ required: true, message: '请输入年龄' }] },
              ]"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="性别"
            >
              <a-select
                v-decorator="[
                  'sex',
                  { rules: [{ required: true, message: '请选择性别' }] },
                ]"
              >
                <a-select-option value="男">
                  男
                </a-select-option>
                <a-select-option value="女">
                  女
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="身份"
            >
              <a-select
                v-decorator="[
                'identity',
                { rules: [{ required: true, message: '请输入身份' }] },
              ]"
              >
                <a-select-option v-for="n in idenList" :key="n.dictId" :value="n.fieldName">{{n.fieldName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="事发日期"
            >
              <a-date-picker
                v-decorator="[
                  'happenTime',
                  { rules: [{ required: true, message: '请输入事发时间' }] },
                ]"
                @change="onChange"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="事件类型"
            >
              <a-select
                v-decorator="[
                    'types',
                    { rules: [{ required: true, message: '请选择事件类型' }] }
                  ]"
              >
                <a-select-option v-for="n in fileListdata" :key="n.dictId" :value="n.fieldName">{{n.fieldName}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-row>
              <a-col :span="8">
                <a-form-item
                  :label-col="{span: 12}"
                  :wrapper-col="{span:12}"
                  label="事发地点"
                >
                  <a-select @change="handleProv" placeholder="请选择市" v-decorator="['provincePlace',{rules:[{required: true, message: '请先选择地市'}]}]">
                    <a-select-option v-if="userRank <= 1" v-for="province in provinceData" :value='province.deptName' :key="province.deptId">
                      {{ province.deptName}}
                    </a-select-option>
                    <a-select-option v-else-if="userRank > 1" v-for="province in provinceData" :value='province.parentDeptName' :key="province.deptId">
                      {{ province.parentDeptName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="5">
                <a-form-item
                  :label-col="{span: 1}"
                  :wrapper-col="{span:23}"
                  label=""
                >
                  <a-select @change="handleCitie" placeholder="请选择区县" v-decorator="['citiePlace',{rules:[{required: true, message: '请先选择区县'}]}]">
                    <a-select-option v-for="city in cities" :value="city.deptName" :key="city.deptId">
                      {{ city.deptName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item
                  :label-col="{span: 4}"
                  :wrapper-col="{span:20}"
                  label=""
                >
                  <a-select placeholder="请选择乡镇街道" @change="handleStreet" v-decorator="['streetPlace',{rules:[{required: true, message: '请先选择乡镇街道'}]}]">
                    <a-select-option v-for="street in streets" :value="street.deptName" :key="street.deptId">
                      {{ street.deptName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="上传人"
            >
              <a-select
                v-decorator="[
                  'releaseWho',
                  { rules: [{ required: true, message: '请选择本人/他人' }] }
                ]"
              >
                <a-select-option value="他人">他人</a-select-option>
                <a-select-option value="本人">本人</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item
          :label-col="{span:4}"
          :wrapper-col="{span:18}"
          label="事迹简要描述:"
        >
          <a-textarea allow-clear
                      v-decorator="[
            'content',
            { rules: [{ required: true, message: '请输入事迹描述' }] },
          ]"
          />
        </a-form-item>
        <a-row>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="推荐单位意见:"
            >
              <a-textarea
                allow-clear
                v-decorator="[
                  'ownOpinion',
                  { rules: [{ required: true, message: '请输入推荐单位意见' }] },
                ]"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              :label-col="formItemLayout.labelCol"
              :wrapper-col="formItemLayout.wrapperCol"
              label="建议奖励金额"
            >
              <a-input
                type="number"
                :min="0"
                v-decorator="[
                'ownMoney',
                { rules: [{ required: true, message: '请输入奖励金额' }] },
              ]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <!--        附件上传-->
        <a-form-item>
          <appendix :refId="this.ids" refType="2" :is-upload="true" :unique="this.ids"></appendix>
        </a-form-item>
      </a-form>
      <div class="drawer-bootom-button">
        <a-popconfirm title="确定放弃修改？" @confirm="rewclone"  okText="确定" cancelText="取消">
          <a-button style="margin-right: .8rem">取消</a-button>
        </a-popconfirm>
        <a-button @click="check" type="primary" :loading="loading">提交</a-button>
      </div>
    </a-drawer>
  </div>
</template>
<script>
import Appendix from '../../../components/appendix/Appendix'
export default {
  name: 'rewardEdit',
  components: {
    Appendix
  },
  props: {
    rewardEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      happenTime: null,
      loading: false,
      form: this.$form.createForm(this, { name: 'dynamic_rule' }),
      formItemLayout: {
        labelCol: { span: 8 },
        wrapperCol: { span: 12 }
      },
      fileListdata: [],
      options: [],
      fieldNames: {},
      ids: '',
      happenTimes: '',
      idenList: {},
      provinceData: [{parentDeptName: '', deptId: 1}],
      cities: [{deptName: '', deptId: 1}],
      streets: [{deptName: '', deptId: 1}],
      secondCity: [],
      secondStreet: [],
      userRank: 0,
      parentDeptName: '',
      provincePlace: '',
      citiePlace: '',
      streetPlace: ''
    }
  },
  mounted () {
    // 根据角色获取地址
    this.$get('/dept/findRank').then(res => {
      this.userRank = res.data.data.rank // 获取当前用户的rank
      // 如果是市级
      if (this.userRank === 1) {
        this.provinceData[0].parentDeptName = this.provinceData[0].deptName
        this.$get('/dept/queryDeptChile').then((r) => {
          console.log(r)
          this.provinceData = r.data.data
          let rank = {prentId: r.data.data[0].deptId}
          this.$get('/dept/queryDeptChile', rank).then((e) => {
            this.cities = e.data.data
            this.streets = [{deptName: '请先选择区县', deptId: 1}]
          })
        })
      } else if (this.userRank === 0) { // 如果是省级或管理员
        this.$get('/dept/queryDeptChile').then((r) => {
          let rank = {prentId: r.data.data[0].deptId}
          // 查询市级
          this.$get('/dept/queryDeptChile', rank).then((e) => {
            let province = e.data.data
            for (let i = 0; i < 14; i++) {
              this.provinceData[i] = province[i]
            }
          })
        })
      } else if (this.userRank === 2) {
        // 如果是区县用户
        this.$get('/dept/queryDeptChile').then((r) => {
          this.provinceData = r.data.data
          this.cities = this.provinceData
        })
      }
    })
    // 获取事件类型
    this.$get('/dict/getListTable', { parentId: 'fd14fb748b74e6bedddd14dd81a3be3e' }).then((r) => {
      this.fileListdata = r.data.data.records
      console.log('事件类型', r)
    })
    //  获取身份信息
    this.$get('/dict/getListTable', { parentId: '873293cce2e47b64bf6ab3b6b007f436' }).then((r) => {
      this.idenList = r.data.data.records
    })
  },
  methods: {
    // 页面刷新重置
    reset () {
      this.form.resetFields()
      this.loading = false
    },
    setFormValues (user) {
      this.ids = user.id
      this.happenTimes = user.happenTime
      let fields = [
        'content', 'name', 'id', 'age',
        'sex', 'identity', 'happenTime',
        'releaseWho', 'types',
        'ownOpinion', 'ownMoney', 'provincePlace', 'citiePlace', 'streetPlace'
      ]
      this.content = user.content
      user.provincePlace = user.place.split(',')[0]
      this.provincePlace = user.provincePlace
      user.citiePlace = user.place.split(',')[1]
      this.citiePlace = user.citiePlace
      user.streetPlace = user.place.split(',')[2]
      this.streetPlace = user.streetPlace
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
      let rank = {prentId: user.placeIdq}
      this.$get('/dept/queryDeptChile', rank).then((r) => {
        this.streets = r.data.data
        console.log(this.streets)
      })
    },
    onChange2 (value) {},
    onChange (date, dateString) {
      this.happenTimes = dateString
    },
    // 选择市级事件
    handleProv (value, Array) {
      this.provincePlace = value
      console.log(value, Array)
      console.log(1)
      let rank = {prentId: Array.key}
      // 如果是省级或市级用户
      if (this.userRank <= 1) {
        // 查询区县
        this.$get('/dept/queryDeptChile', rank).then((r) => {
          this.cities = r.data.data
        })
      }
      // 选中后清空地区+乡镇街道
      this.form.setFieldsValue({
        citiePlace: '',
        streetPlace: ''
      })
    },
    handleCitie (value, Array) {
      console.log(1)
      this.citiePlace = value
      // 查询乡镇
      let rank = {prentId: Array.key}
      this.$get('/dept/queryDeptChile', rank).then((r) => {
        this.streets = r.data.data
      })
      // 选中后乡镇街道
      this.form.setFieldsValue({
        streetPlace: ''
      })
    },
    handleStreet (value) {
      console.log(value)
      this.streetPlace = value
    },
    check () {
      console.log(this.happenTimes)
      this.form.validateFields((err, values) => {
        console.log(values)
        if (!err) {
          values.happenTime = new Date(this.happenTimes)
          this.loading = true
          values.place = this.provincePlace + ',' + this.citiePlace + ',' + this.streetPlace
          this.$put('prize/update', values).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    rewclone () {
      this.$emit('close')
      this.form.resetFields()
    }
  }
}
</script>
