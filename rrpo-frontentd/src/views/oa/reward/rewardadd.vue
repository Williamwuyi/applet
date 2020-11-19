<template>
  <div>
    <a-drawer
      :visible="rewardaddVisiable"
      width=40%
      title="新建湖南省铁路护路联防工作’一事一奖‘审批表"
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
                <a-select-option value="他人">
                  他人
                </a-select-option>
                <a-select-option value="本人">
                  本人
                </a-select-option>
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
          <appendix refId="-1" refType="2" :is-upload="true" ref="appendix" :unique="this.randomId"></appendix>
        </a-form-item>
      </a-form>
      <div class="drawer-bootom-button">
        <a-popconfirm title="确定放弃新建？" @confirm="rewclone"  okText="确定" cancelText="取消">
          <a-button style="margin-right: .8rem">取消</a-button>
        </a-popconfirm>
        <a-button @click="check" type="primary" :loading="loading">创建</a-button>
<!--        <a-button @click="checkup" type="primary" :loading="loading">创建并提交</a-button>-->
      </div>
    </a-drawer>
  </div>
</template>
<script>
export default {
  name: 'rewardadd',
  props: {
    rewardaddVisiable: {
      default: false
    },
    randomId: {
      default: '-1'
    }
  },
  components: {
    Appendix: () => import('../../../components/appendix/Appendix')
  },
  data () {
    return {
      happenTime: null,
      fieldNames: {label: 'title', value: 'text', children: 'children'},
      options: [],
      loading: false,
      form: this.$form.createForm(this, { name: 'dynamic_rule' }),
      formItemLayout: {
        labelCol: { span: 8 },
        wrapperCol: { span: 12 }
      },
      fileListdata: [],
      status: null,
      deptData: [],
      arrdata: [],
      action: this.$constURL + '/file/uploadFile',
      fileIds: [],
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
      streetPlace: '',
      placeIds: 0,
      placeIdq: 0,
      placeIdj: 0
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
          this.provinceData = r.data.data
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
      } else {
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
    })
    //  获取身份信息
    this.$get('/dict/getListTable', { parentId: '873293cce2e47b64bf6ab3b6b007f436' }).then((r) => {
      this.idenList = r.data.data.records
    })
  },
  methods: {
    reset () {
      this.form.resetFields()
      this.loading = false
      this.fileIds = []
    },
    onChange (date, dateString) {
      this.happenTime = dateString
    },
    onChange2 (value) {
    },
    // 选择市级事件
    handleProv (value, Array) {
      this.provincePlace = value
      let rank = {prentId: Array.key}
      this.placeIds = Array.key
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
      console.log(value, Array)
      this.citiePlace = value
      this.placeIdq = Array.key
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
    handleStreet (value, Array) {
      this.streetPlace = value
      this.placeIdj = Array.key
    },
    check () {
      this.form.validateFields((err, values) => {
        if (!err) {
          // 附件信息
          let appendixs = this.$store.state.file.appendixList
          Object.keys(appendixs).forEach((key) => {
            this.fileIds.push(appendixs[key].fileId)
          })
          values.happenTime = new Date(this.happenTime)
          values.status = 1
          values.placeIds = this.placeIds
          values.placeIdq = this.placeIdq
          values.placeIdj = this.placeIdj
          values.fileIds = this.fileIds
          values.place = this.provincePlace + ',' + this.citiePlace + ',' + this.streetPlace
          this.loading = true
          this.$post('prize/save', values).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    rewclone () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
    }
  }
}
</script>
<style lang="less" scoped>
</style>
