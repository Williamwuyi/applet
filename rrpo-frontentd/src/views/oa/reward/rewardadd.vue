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
              <a-cascader
                :options="options"
                :fieldNames="fieldNames"
                expand-trigger="hover"
                @change="onChange2"
                placeholder=""
                v-decorator="[
                  'place',
                  { rules: [{ required: true, message: '请选择事发地点' }] }
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
      idenList: {}
    }
  },
  mounted () {
    // 获取地址
    this.$get('dept').then((r) => {
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
    reset () {
      this.form.resetFields()
      this.loading = false
      this.fileIds = []
    },
    onChange (date, dateString) {
      this.happenTime = dateString
    },
    onChange2 (value) {
      console.log(value)
    },
    check () {
      this.form.validateFields((err, values) => {
        if (!err) {
          // 附件信息
          let appendixs = this.$store.state.file.appendixList
          Object.keys(appendixs).forEach((key) => {
            console.log(key)
            this.fileIds.push(appendixs[key].fileId)
          })
          values.happenTime = new Date(this.happenTime)
          values.status = 1
          values.fileIds = this.fileIds
          this.loading = true
          console.log(values.fileIds)
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
