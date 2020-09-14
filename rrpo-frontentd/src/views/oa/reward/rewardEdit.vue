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
                <a-select-option v-for="n in idenList" :key="n.id" :value="n.id">{{n.identity}}</a-select-option>
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
                <a-select-option v-for="n in fileListdata" :key="n.id" :value="n.name">{{n.name}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item
              :label-col="{span: 4}"
              :wrapper-col="{span:18}"
              label="事发地点"
            >
              <a-input
                 v-decorator="[
                  'place',
                  { rules: [{ required: true, message: '请输入事发地点' }] },
                ]"
              />
            </a-form-item>
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
      idenList: {}
    }
  },
  mounted () {
    // 获取地址
    this.$get('dept').then((r) => {
      this.fieldNames = {label: 'title', value: 'id', children: 'children'}
      this.options = r.data.rows.children
    })
    // 获取事件类型
    this.$get('prizeTypes/getTypesList').then((r) => {
      this.fileListdata = r.data.data
    })
    //  获取身份信息
    this.$get('/prizeIdentity/getInformListByMapper').then((r) => {
      this.idenList = r.data
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
        'place', 'releaseWho', 'types',
        'ownOpinion', 'ownMoney'
      ]
      this.content = user.content
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
    },
    onChange2 (value) {},
    displayRender ({ labels }) {
      return labels[labels.length - 1]
    },
    onChange (date, dateString) {
      this.happenTimes = dateString
    },
    check () {
      console.log(this.happenTimes)
      this.form.validateFields((err, values) => {
        if (!err) {
          values.happenTime = new Date(this.happenTimes)
          this.loading = true
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
