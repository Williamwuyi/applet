<template>
  <a-card :bordered="false" class="card-area">
      <a-form :form="form">
        <a-form-item label='标题' v-bind="formItemLayout">
          <a-input
            v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
        </a-form-item>
        <a-form-item label='类别' v-bind="formItemLayout">
          <a-select v-model="id">
            <a-select-option v-for="n in typeData" :key="n.id" :value="n.id">{{n.typeName}}</a-select-option>
          </a-select>
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
          <a-form-item label='限时回复' v-bind="formItemLayout">
          &nbsp;&nbsp;<a-checkbox :disabled="isEdit" @change="checkboxChange">
            <a-date-picker v-if="isSX" @change="onTimeChange" />
          </a-checkbox>
        </a-form-item>
        <a-form-item label='传阅人员' v-bind="formItemLayout">
<!--          <a-select style="font-weight: bold;"-->
<!--            mode="tags"-->
<!--            disabled-->
<!--            :token-separators="[',']"-->
<!--            v-decorator="['sender',{rules:[{required: true, message: '请选择传阅人员'}]}]" placeholder="请点击选择传阅人员">-->
<!--          </a-select>-->
          <a-button @click="selectSender" >选择传阅人</a-button>
        </a-form-item>
        <a-form-item label='发布内容' v-bind="formItemLayout">
          <editor height="700px" :readonly=false ref="editor" refType="1"></editor>
          <appendix refId="-1" refType="1" :is-upload="true" unique="UUID"></appendix>
        </a-form-item>
        <a-form-item style="text-align: center">
<!--          getFileList  reset-->
        <a-button class="editable-add-btn" @click="getFileList" style="margin-right: 25px">
          重置
        </a-button>
        <a-button class="editable-add-btn" @click="submit (1)" style="margin-right: 25px" :loading="loading">
          保存
        </a-button>
          <a-button class="editable-add-btn" @click="submit (3)" :loading="loading">
            保存并发送
          </a-button>
        </a-form-item>
      </a-form>
        <personnel
          :folderVisible="folderVisible"
          @success="modalSubmit"
          @close="modalClose"
          ref="oldpersonnel"
        />
    </a-card>
</template>
<script>
// import {toolbarTips} from 'utils/quill-title'
import Personnel from './Personnel'
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
const columns = [{
  title: '传阅人员',
  dataIndex: 'username',
  key: '1'
}]
export default {
  name: 'newFolder',
  components: {Editor, Appendix, Personnel}, // 局部注册
  data () {
    return {
      loading: false, // 加载状态
      folderVisible: false, // 默认关闭模态框选择传阅人
      // 编辑器
      content: '<strong>初始化编辑器值</strong>',
      formItemLayout,
      isEdit: false, // 必须选择时间
      redioData: 0,
      isSX: false, // 是否显示时间
      dateTime: null,
      id: null,
      fileIds: [],
      typeData: [], // 下拉数据接收
      personnelId: [], // 模态框提交的人员id
      newFolder: {},
      form: this.$form.createForm(this),
      columns,
      value: [],
      editorOption: {
        placeholder: '请在这里输入'
      }
    }
  },
  inject: ['reload'],
  component: {
    editor () {
      return this.$refs.myQuillEditor.quill
    }
  },
  mounted () {
    // 获取模态框树形结构
    this.loading = true
    this.$get('/exchangeType/getInformListByMapper').then((r) => {
      this.typeData = r.data
      this.loading = false
    })
  },
  methods: {
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.personnelId = []
      this.newFolder = {}
    },
    radioChange (e) {
      this.redioData = e.target.value
    },
    // 勾选时间
    checkboxChange (e) {
      this.isSX = e.target.checked
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.dateTime = date
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
    },
    // 提交
    submit (statusId) {
      this.loading = true
      this.form.validateFields((err, values) => {
        this.get() // 获取文本内容
        this.getFileList() // 获取上传文件id
        if (!err) {
          if (this.personnelId.length === 0) {
            this.$notification.warning({message: '系统提示', description: '请选择传阅的人员！', duration: 4})
            this.loading = false
          } else {
            const param = {
              sendUserIds: this.personnelId,
              content: this.newFolder.content,
              typeId: this.id,
              isCheck: this.redioData,
              status: statusId,
              claimTime: this.dateTime,
              isTime: this.isSX,
              fileIds: this.fileIds,
              title: values.title
            }
            this.$post('/exchange/creat', param).then((r) => {
              if (r.status === 200) {
                this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
                this.reset()
                this.loading = false
                this.$router.push('/oa/messageMutual/outBox')
              }
            })
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
