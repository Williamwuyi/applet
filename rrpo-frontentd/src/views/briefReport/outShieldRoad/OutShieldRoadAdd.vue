<template>
  <a-drawer
    title="添加信息"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="true"
    @close="onClose"
    :visible="addVisiable"
    >
      <a-form :form="form">
        <a-form-item label='标题' v-bind="formItemLayout">
          <a-input
            v-decorator="['title',{rules: [{ required: true, message: '标题不能为空'}]}]" />
        </a-form-item>
<!--        <a-form-item label='是否同步' v-bind="formItemLayout" >-->
<!--          <a-radio-group :disabled="isRadio" @change="radioChange">-->
<!--            <a-radio value="1">-->
<!--              门户网站-->
<!--            </a-radio>-->
<!--            <a-radio value="2">-->
<!--              其他-->
<!--            </a-radio>-->
<!--          </a-radio-group>-->
<!--        </a-form-item>-->
<!--        <a-form-item v-if="isSX" label='来源' v-bind="formItemLayout">-->
<!--          <a-input v-decorator="['source']" />-->
<!--        </a-form-item>-->
<!--        <a-form-item v-if="isSX" label='时间' v-bind="formItemLayout">-->
<!--          <a-date-picker  @change="onTimeChange" :default-value="defaultTime" />-->
<!--        </a-form-item>-->
<!--        <a-form-item v-if="isSX" label='栏目' v-bind="formItemLayout">-->
<!--          <a-select-->
<!--            mode="multiple"-->
<!--            show-search style="width: 100%"-->
<!--            placeholder="选择栏目"-->
<!--            @change="handleChange"-->
<!--            v-decorator="['lanmu',{rules: [{ required: true, message: '栏目必选'}]}]"-->
<!--          >-->
<!--            <a-select-option v-for="i in programa" :key="i.id" :value="i.id">-->
<!--              {{ i.name }}-->
<!--            </a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->
        <a-form-item label='传阅人员' v-bind="formItemLayout">
          <a-button @click="selectSender" >选择传阅人</a-button>
          <a-collapse>
            <a-collapse-panel key="1" header="查看选中人员">
              <a-tag :style="'color:' + colorArr[Math.floor((Math.random() * colorArr.length))]" v-for="(value,index) in personnelName" :key="index">
                {{value}}
              </a-tag>
            </a-collapse-panel>
          </a-collapse>
        </a-form-item>
        <a-form-item label='发布内容' v-bind="formItemLayout">
          <editor height="500px" :readonly=false ref="editor" refType="1" style="overflow: auto"></editor>
        </a-form-item>
        <a-form-item label='附件上传' v-bind="formItemLayout">
          <appendix refId="-1" refType="8" :is-upload="true" :unique="this.shieldId"></appendix>
        </a-form-item>
        <div class="drawer-bootom-button">
          <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
            <a-button style="margin-right: .8rem">取消</a-button>
          </a-popconfirm>
          <a-button @click="submit" type="primary">提交</a-button>
        </div>
      </a-form>
    <personnel
      :folderVisible="folderVisible"
      @success="modalSubmit"
      @close="modalClose"
      ref="oldpersonnel"
    />
    <submit-info
      :subVisiable="subVisiable"
      @success="submintInfo"
      @close="subClose"
      ref="oldsub"
    />
    </a-drawer>
</template>
<script>
import SubmitInfo from './SubmitInfo'
import Personnel from '../../oa/messageMutual/newFolder/Personnel'
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
const formItemLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 }
}
const columns = [{
  title: '传阅人员',
  dataIndex: 'username',
  key: '1'
}]
export default {
  name: 'OutShieldRoadAdd',
  components: {Editor, Appendix, SubmitInfo, Personnel}, // 局部注册
  props: {
    addVisiable: {
      default: false
    },
    shieldId: {
      default: '-1'
    }
  },
  data () {
    return {
      loading: false, // 加载状态
      colorArr: ['red', 'green', 'darkmagenta', 'turquoise', 'orange', 'blue', 'blueviolet', 'coral', 'deeppink', 'midnightblue'],
      personnelName: [],
      folderVisible: false, // 默认关闭模态框选择传阅人
      content: '<strong>初始化编辑器值</strong>',
      formItemLayout,
      subVisiable: false,
      fileIds: [],
      isSX: false,
      isRadio: false,
      dateTime: null, // 选择时间
      defaultTime: null, // 默认当前时间
      portal: 0, // 是否同步门户网
      programa: [], // 栏目数据
      targetsId: '', // 栏目id
      cont: 1,
      personnelId: [], // 模态框提交的人员id
      newFolder: {},
      form: this.$form.createForm(this),
      columns
    }
  },
  component: {
    editor () {
      return this.$refs.myQuillEditor.quill
    }
  },
  mounted () {
  },
  methods: {
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.personnelId = []
      this.newFolder = {}
    },
    selectSender () {
      this.folderVisible = true
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
      // this.$get('/dict/getTargets').then((res) => {
      //   console.log('获取栏目信息', res.data.data.targets)
      //   this.programa = res.data.data.targets
      // })
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
      console.log('栏目id', this.targetsId)
    },
    // 选传阅人的提交模态框
    modalSubmit (dataId) {
      this.personnelId = dataId
      const param = {
        userIds: dataId
      }
      this.$get('/user/getUserInfo', param).then(res => {
        let pessonnel = res.data.data
        let name = []
        for (let key in pessonnel) {
          name.push(pessonnel[key].username)
        }
        this.personnelName = name
      })
      this.folderVisible = false
    },
    modalClose () {
      this.folderVisible = false
    },
    get () {
      // 读取编辑器HTML内容
      this.newFolder.content = this.$refs.editor.content
    },
    // 获取上传文件id
    getFileList () {
      let filesList = this.$store.state.file.appendixList
      console.log(filesList)
      let fileIds = []
      if (filesList.length !== 0 || filesList !== null) {
        for (let i = 0; filesList.length > i; i++) {
          fileIds.push(filesList[i].fileId)
        }
        console.log('获取到文件id', fileIds)
      }
      this.fileIds = fileIds
    },
    reset () {
      this.loading = false
      // 清空文本域
      this.newFolder.content = ''
      // 清空表单
      this.form.resetFields()
      this.personnelId = []
    },
    // 提交信息关闭
    subClose () {
      this.subVisiable = false
    },
    // 提交
    submit () {
      if (this.form.getFieldValue('title') === undefined) {
        this.$notification.warning({message: '系统提示', description: '请填写标题！', duration: 4})
      } else if (this.personnelId.length === 0) {
        this.$notification.warning({message: '系统提示', description: '请选择传阅的人员！', duration: 4})
      } else if (this.form.getFieldValue('title').indexOf('第') !== -1 && this.form.getFieldValue('title').indexOf('期') !== -1) {
        this.subVisiable = true
      } else {
        this.$notification.warning({message: '系统提示', description: '标题未填写期数！', duration: 4})
      }
    },
    // 提交后确认
    submintInfo (data) {
      console.log('提交的统计数据', data)
      this.form.validateFields((err, values) => {
        this.get() // 获取文本内容
        this.getFileList() // 获取上传文件id
        if (!err) {
          let param = {}
          let atTime = ''
          if (this.portal === '1') {
            if (this.dateTime !== null) {
              atTime = this.dateTime
            } else {
              atTime = new Date()
            }
            param.synchronizeWeb = this.portal // 是否同步门户网
            param.source = values.source // 来源
            param.time = atTime // 网页信息发布时间
            // param.targetsId = this.targetsId // 栏目id
          } else {
            this.targetsId = ''
            values.source = ''
          }
          param.sendUserIds = this.personnelId // 传阅人id
          param.content = this.newFolder.content // 文本内容
          param.status = 1 // 状态
          param.fileIds = this.fileIds // 文件id
          param.briefingCounts = data // 提交弹框输入数据
          param.title = values.title // 标题
          if (this.cont === 1) {
            console.log('添加提交参数：', param)
            this.$post('/briefing/creat', param).then((r) => {
              if (r.status === 200) {
                this.subVisiable = false
                this.$emit('success')
              }
            })
            this.cont = 2
          } else {
            this.$notification.warning({message: '系统提示', description: '数据加载中，请勿重复提交！', duration: 4})
          }
        }
      })
    }
  }
}
</script>
<style>
  .edit_container {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
  .ql-editor{
    min-height: 600px;
  }
</style>
