<template>
  <a-drawer
    title="添加信息"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="addVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
      <a-form :form="form">
        <a-form-item label='标题' v-bind="formItemLayout">
          <a-input
            v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
        </a-form-item>
        <a-form-item label='传阅人员' v-bind="formItemLayout">
          <a-button @click="selectSender" >选择传阅人</a-button>
        </a-form-item>
        <a-form-item label='发布内容' v-bind="formItemLayout">
          <editor height="500px" :readonly=false ref="editor" refType="1" style="overflow: auto"></editor>
          <appendix refId="-1" refType="8" :is-upload="true" :unique="this.shieldId"></appendix>
        </a-form-item>
        <a-form-item style="text-align: center">
        </a-form-item>
        <div class="drawer-bootom-button">
          <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
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
      folderVisible: false, // 默认关闭模态框选择传阅人
      content: '<strong>初始化编辑器值</strong>',
      formItemLayout,
      subVisiable: false,
      fileIds: [],
      cont: 1,
      personnelId: [], // 模态框提交的人员id
      newFolder: {},
      form: this.$form.createForm(this),
      columns
    }
  },
  inject: ['reload'],
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
    // 选传阅人的提交模态框
    modalSubmit (data) {
      this.personnelId = data
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
      } else {
        this.subVisiable = true
      }
    },
    // 提交后确认
    submintInfo (data) {
      console.log('提交的统计数据', data)
      this.form.validateFields((err, values) => {
        this.get() // 获取文本内容
        this.getFileList() // 获取上传文件id
        if (!err) {
          const param = {
            sendUserIds: this.personnelId,
            content: this.newFolder.content,
            status: 1,
            fileIds: this.fileIds,
            briefingCounts: data,
            title: values.title
          }
          if (this.cont === 1) {
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
