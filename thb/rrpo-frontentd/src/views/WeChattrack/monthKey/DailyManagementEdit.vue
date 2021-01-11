<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    :zIndex="100"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="DailyEditVisiable"
    >
    <a-form :form="form">
      <a-form-item
        label="月份查询"
        v-bind="formItemLayout">
        <a-date-picker
          :default-value="moment(dateTime, 'YYYY-MM')"
          format = 'YYYY-MM' @change="onTimeChange" />
      </a-form-item>
      <a-form-item v-bind="formItemLayout" label="选择群">
        <a-auto-complete v-model="hdd" style="width: 260px" @search="search" placeholder="输入群名称看看">
          <template slot="dataSource">
            <a-select-option v-for="email in autoCompleteResult" @click="clickSelect(email.wxId)" :key="email.wxId">{{email.wxName}}</a-select-option>
          </template>
        </a-auto-complete>
        <a-icon  type="question-circle"  style="color:#61C2FE;margin-left: 10px; font-size: 18px"  title="本群只接受本级或本级以下合格群搜索"/>
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
         <a-form-item label='内容' v-bind="formItemLayout">
           <editor height="600px" :readonly=false ref="editor" refType="3" ></editor>
         </a-form-item>
      <a-form-item label='附件' v-bind="formItemLayout">
        <appendix :refId="monthId"  :unique="monthId" refType="9" :is-upload="true"></appendix>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
       <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
import moment from 'moment'
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'NoticeEdit',
  components: {Editor, Appendix}, // 局部注册
  props: {
    DailyEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      autoCompleteResult: [],
      hdd: '',
      monthId: '',
      dateTime: '',
      newedite: {},
      content: undefined,
      form: this.$form.createForm(this)
    }
  },
  methods: {
    moment,
    reset () {
      this.loading = false
      this.form.resetFields()
      this.dateTime = ''
      this.monthId = ''
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    // 关联
    radioChange (value) {
      console.log(value)
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
    // 选择时间
    onTimeChange (date, dateTime) {
      this.dateTime = dateTime
    },
    setFormValues (user) {
      this.monthId = ''
      this.dateTime = ''
      if (user.qun) {
        this.hdd = user.qun.wxName
      }
      this.dateTime = user.month
      this.monthId = user.wxMonthId
      // content
      this.content = user.content
      this.set()
    },
    // 富文本编辑
    set () {
      this.$nextTick(function () {
        // 此时已经渲染完成
        this.$refs.editor.content = this.content
      })
    },
    get () {
      // 读取编辑器HTML内容
      // this.$refs.editor.content
      this.newedite.content = this.$refs.editor.content
    },
    getFileList () {
      // TODO 文件列表为：编辑器内上传图片 + 所有附件文件 (编辑器内图片上传后删除 无法检查，最终当编辑器能容保存时，需要对比编辑器内图片与此文件列表，进行后台处理)
      console.log(this.$store.state.editor.editorList)
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
    // 修改
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.get()
          if (this.dateTime === '') {
            this.$message.error('月份不能为空')
            return
          }
          if (this.hdd !== '') {
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
          this.newedite.month = this.dateTime
          this.newedite.qunId = this.qunId
          this.newedite.wxMonthId = this.monthId
          console.log(this.newedite)
          this.loading = true
          let newadd = {...this.newedite}
          let file = this.$store.state.file.appendixList
          newadd.fileIdS = []
          file.forEach(t => {
            newadd.fileIdS.push(t.fileId)
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
  }
}
</script>
