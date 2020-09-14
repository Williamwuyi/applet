<template>
  <a-drawer
    :zIndex="100"
    title="添加"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="addVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item
        label="月份查询"
        v-bind="formItemLayout">
        <a-date-picker
          format = 'YYYY-MM' @change="onTimeChange" />
      </a-form-item>
      <a-form-item v-bind="formItemLayout" label="选择群">
        <a-auto-complete v-model="hdd" style="width: 260px" @search="search" placeholder="输入群名称看看">
          <template slot="dataSource">
            <a-select-option v-for="email in autoCompleteResult" @click="clickSelect(email.wxId)" :key="email.wxId">{{email.wxName}}</a-select-option>
          </template>
        </a-auto-complete>
      </a-form-item>
      <a-form-item label='关联' v-bind="formItemLayout">
        <a-radio-group  @change="radioChange">
          <a-radio value="1">
            年度考核
          </a-radio>
          <a-radio value="2">
            其他
          </a-radio>
        </a-radio-group>
      </a-form-item>
       <a-form-item label='精品内容' v-bind="formItemLayout">
      <editor height="400px" :readonly=false ref="editor" refType="9" ></editor>
      </a-form-item>
      <a-form-item label='附件上传' v-bind="formItemLayout">
        <appendix refId="-1" refType="9" unique="UUID" :is-upload="true"></appendix>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="确定放弃修改？" @confirm="onClose"  okText="确定" cancelText="取消">
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
  wrapperCol: { span: 15 }
}
export default {
  name: 'DailyManagementAdd',
  components: {Editor, Appendix}, // 局部注册
  props: {
    addVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      // 编辑器
      content: '<strong>初始化编辑器值</strong>',
      form: this.$form.createForm(this),
      qunId: '',
      value: [],
      dateTime: '',
      newNoticea: {},
      // 选择群
      autoCompleteResult: [],
      hdd: ''
    }
  },
  component: {
  },
  methods: {
    reset () {
      this.loading = false
      // 清空表单
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.newNoticea = {}
    },
    // 关联
    radioChange (value) {
      console.log(value)
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.dateTime = dateTime
    },
    // 选择群
    search (value) {
      if (!value) {
        this.autoCompleteResult = []
      } else {
        this.$get('/wx/qun/list', {name: value}).then(res => {
          this.autoCompleteResult = res.data.data.records
        })
      }
    },
    clickSelect (key) {
      this.qunId = key
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
          this.get()
          if (this.dateTime === '') {
            this.$message.error('月份不能为空')
            return
          }
          if (this.hdd === '') {
            this.$message.error('选择群不能为空')
            return
          } else {
            this.$get('/wx/qun/list', {name: this.hdd}).then(res => {
              let arr = res.data.data.records
              arr.forEach(t => {
                if (t.wxName === this.hdd) {
                  this.qunId = t.qunId
                } else {
                  this.$message.error('没有对应的群信息')
                }
              })
            })
          }
          this.newNoticea.month = this.dateTime
          this.newNoticea.qunId = this.qunId
          this.loading = true
          let newadd = {...this.newNoticea}
          let file = this.$store.state.file.appendixList
          newadd.fileIds = []
          file.forEach(t => {
            newadd.fileIds.push(t.fileId)
          })
          console.log(newadd)
          this.$post('/wx/month/saveorUpdate', newadd).then(() => {
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
    addVisiable () {
      let file = this.$store.state.file.appendixList
      file.forEach(t => {
        t.fileId = []
      })
    }
  }
}
</script>
<style>
</style>
