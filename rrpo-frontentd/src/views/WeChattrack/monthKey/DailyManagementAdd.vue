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
    >
    <a-form :form="form">
      <a-form-item
        label="月份查询"
        v-bind="formItemLayout">
        <a-date-picker
          format = 'YYYY-MM' @change="onTimeChange" v-model="Timer" />
      </a-form-item>
      <a-form-item v-bind="formItemLayout" label="选择群">
        <a-auto-complete v-model="hdd" style="width: 260px" @search="search" placeholder="输入群名称看看">
          <template slot="dataSource">
            <a-select-option v-for="email in autoCompleteResult" @click="clickSelect(email.wxId)" :key="email.wxId">{{email.wxName}}</a-select-option>
          </template>
        </a-auto-complete>
        <a-icon  type="question-circle"  style="color:#61C2FE;margin-left: 10px; font-size: 18px"  title="本群只接受本级或本级以下合格群搜索"/>
      </a-form-item>
       <a-form-item label='精品内容' v-bind="formItemLayout">
      <editor height="400px" :readonly=false ref="editor" refType="9" ></editor>
      </a-form-item>
      <a-form-item label='附件上传' v-bind="formItemLayout">
        <appendix refId="-1" refType="9" :unique="this.randomId" :is-upload="true"></appendix>
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
// 防抖
const delay = (function () {
  let timer = 0
  return function (callback, ms) {
    clearTimeout(timer)
    timer = setTimeout(callback, ms)
  }
})()
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
    },
    randomId: {
      default: '-1'
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
      Timer: undefined,
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
      this.Timer = undefined
      this.$refs.editor.content = ''
      this.autoCompleteResult = []
      this.hdd = ''
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
        delay(() => {
          // 执行部分
          this.getQQun(value)
        }, 500)
      }
    },
    // 查找群
    getQQun (value) {
      this.$get('/wx/qun/list', {name: value}).then(res => {
        if (res.data.data.records.length > 0) {
          this.autoCompleteResult = res.data.data.records
        } else {
          this.$message.error('未找到相关群信息')
        //  没找到从这里传群名称 this.hdd
        }
      })
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
    // 纯文本
    ToText (HTML) {
      var input = HTML
      return input.replace(/<(style|script|iframe)[^>]*?>[\s\S]+?<\/\1\s*>/gi, '').replace(/<[^>]+?>/g, '').replace(/\s+/g, ' ').replace(/ /g, ' ').replace(/>/g, ' ')
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
          this.newNoticea.month = this.dateTime
          this.newNoticea.qunId = this.qunId
          this.loading = true
          let newadd = {...this.newNoticea}
          let file = this.$store.state.file.appendixList
          console.log(file)
          newadd.fileIds = []
          file.forEach(t => {
            newadd.fileIds.push(t.fileId)
          })
          newadd.fuContent = this.ToText(this.$refs.editor.content)
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
