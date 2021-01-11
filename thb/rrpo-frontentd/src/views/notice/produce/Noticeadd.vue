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
    :visible="NoticeaddVisiable"
    >
    <a-form :form="form">
<!--      v-model="newNoticea.title"-->
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
      </a-form-item>
        <a-form-item label='是否同步' v-bind="formItemLayout" v-hasPermission="'notice:synchronize'">
          <a-radio-group :disabled="isRadio" v-model="portal" @change="radioChange">
            <a-radio value="1">
              门户网站
            </a-radio>
            <a-radio value="2">
              其他
            </a-radio>
          </a-radio-group>
        </a-form-item>
<!--        <a-form-item v-if="isSX" label='来源' v-bind="formItemLayout">-->
<!--          <a-input v-decorator="['source']" />-->
<!--        </a-form-item>-->
        <a-form-item v-if="isSX" label='时间' v-bind="formItemLayout">
          <a-date-picker  @change="onTimeChange" :default-value="defaultTime" />
        </a-form-item>
        <a-form-item v-if="isSX" label='栏目' v-bind="formItemLayout">
          <a-select mode="multiple" ref="lanmu" show-search style="width: 100%" placeholder="选择栏目" @change="handleChange">
            <a-select-option v-for="i in programa" :key="i.id" :value="i.id">
              {{ i.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
       <a-form-item label='发布内容' v-bind="formItemLayout">
      <editor height="400px" :readonly=false ref="editor" refType="3" ></editor>
      </a-form-item>
      <a-form-item label='附件上传' v-bind="formItemLayout">
        <appendix refId="-1" refType="3" :unique="this.randomId" :is-upload="true"></appendix>
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
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'Noticeadd',
  components: {Editor, Appendix}, // 局部注册
  props: {
    NoticeaddVisiable: {
      default: false
    },
    randomId: {
      default: '-1'
    }
  },
  data () {
    return {
      loading: false,
      dabuclick: true,
      isRadio: false,
      isSX: false,
      defaultTime: '', // 默认当前时间
      portal: '2', // 是否同步门户网
      programa: [], // 栏目数据
      targetsId: '', // 栏目id
      formItemLayout,
      // 编辑器
      content: '<strong>初始化编辑器值</strong>',
      form: this.$form.createForm(this),
      newNoticea: {},
      value: []
    }
  },
  methods: {
    reset () {
      this.loading = false
      // 清空文本域
      this.newNoticea.content = ''
      // 清空表单
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.newNoticea = {}
    },
    // 富文本编辑
    get () {
      // 读取编辑器HTML内容
      // this.$refs.editor.content
      this.newNoticea.content = this.$refs.editor.content
    },
    set () {
      // 设置编辑器内容
      // this.$refs.editor.content = value
      this.$refs.editor.content = this.content
    },
    // TODO 附件文件列表
    getAppendixFileList () {
      console.log(this.$store.state.file.appendixList)
    },
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.dabuclick) {
            let atTime = null
            if (this.portal === '1') {
              if (this.dateTime !== undefined) {
                atTime = this.dateTime
              } else {
                atTime = new Date()
              }
            } else {
              this.targetsId = null
              values.source = null
            }
            this.dabuclick = false
            this.get()
            this.loading = true
            let newadd = {...this.form.getFieldsValue(), ...this.newNoticea}
            newadd.time = atTime
            newadd.targetsId = this.targetsId
            newadd.synchronizeWeb = this.portal
            let file = this.$store.state.file.appendixList
            newadd.fileIds = []
            file.forEach(t => {
              newadd.fileIds.push(t.fileId)
            })
            this.$post('/notice', newadd).then(() => {
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        }
      })
    },
    // 是否同步
    radioChange (e) {
      // 获取当前默认时间展示
      let date = new Date()
      let y = date.getFullYear()
      let m = date.getMonth() + 1
      m = m < 10 ? ('0' + m) : m
      var d = date.getDate()
      d = d < 10 ? ('0' + d) : d
      this.defaultTime = y + '-' + m + '-' + d
      // 获取栏目数据
      this.$get('/dict/getTargets').then((res) => {
        this.programa = res.data.data.targets
      }).catch(() => {
        this.$message.error('未获取到栏目信息请联系管理员')
      })
      // 判断是否同步 显示
      if (e.target.value === '1') {
        this.isSX = e.target.checked
      } else {
        this.isSX = false
      }
      // 是否同步，是否选中门户网
      this.portal = e.target.value
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.dateTime = date
    },
    handleChange (value) {
      this.targetsId = value.toString()
    }
  }
}
</script>
<style>
</style>
