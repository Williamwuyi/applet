<template>
  <a-drawer
    title="修改发送信息"
    :maskClosable="false"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="editVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input
          v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='传阅人员' v-bind="formItemLayout">
        <a-button @click="selectSender" ><a-icon type="usergroup-add" />重新选择人员</a-button>
      </a-form-item>

      <a-form-item label='发布内容' v-bind="formItemLayout">
        <div style="text-align: right"><a-button @click="updateFileButton"><a-icon type="cloud-upload" />修改附件</a-button></div>
<!--       v-if="isRouterAlive" -->
        <editor height="500px" :readonly=false ref="editor" refType="8"></editor>
<!--        <appendix :refId="this.exchangeId" refType="1" :is-upload="true" v-if="isRouterAlive"></appendix>-->
      </a-form-item>
    </a-form>
    <personnel
      :folderVisible="folderVisible"
      @success="modalSubmit"
      @close="modalClose"
      ref="oldpersonnel"
    />
    <update-file
      :updateFileVisiable="updateFileVisiable"
      :exchangeId="exchangeId"
      @close="updateFileClose"
    />
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃修改？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
// import {toolbarTips} from 'utils/quill-title'
import UpdateFile from './UpdateFile'
import Personnel from '../../oa/messageMutual/newFolder/Personnel'
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
  name: 'ComEdit',
  components: {Editor, Appendix, UpdateFile, Personnel}, // 局部注册
  props: {
    editVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false, // 加载状态
      updateFileVisiable: false,
      content: '<strong>初始化编辑器值</strong>',
      folderVisible: false, // 默认关闭模态框选择传阅人
      selectedRowKeys: [], // 模态框选中人员id
      formItemLayout,
      checkedKeys: [],
      isRouterAlive: true, // 刷新组件
      id: null,
      autoExpandParent: true,
      expandedKeys: [],
      exchangeId: '',
      fileIds: [],
      deptTreeData: [], // 模态框中的树形结构接收数据
      deptTreeKeys: +new Date(),
      dataSource: [], // 模态框中的表格接收数据
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
    this.$get('dept').then((r) => {
      this.deptTreeData = r.data.rows.children
    })
    this.reloada()
  },
  methods: {
    reloada () {
      this.isRouterAlive = false
      this.$nextTick(() => (this.isRouterAlive = true))
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    personnel (param) {
      this.$get('/user/getByDept/' + param).then((r) => {
        this.dataSource = r.data
      })
    },
    updateFileButton () {
      this.updateFileVisiable = true
    },
    updateFileClose () {
      this.updateFileVisiable = false
    },
    // 关闭模态框
    modalclosel () {
      this.folderVisible = false
      this.selectedRowKeys = []
      this.personnelId = []
    },
    selectSender () {
      let result = []
      for (let index in this.personnelId) {
        result.push({userId: this.personnelId[index], username: this.personnelName[index]})
      }
      this.$refs.oldpersonnel.getPersonnerl(result)
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
    // 表格选中
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 树形选中
    handleCheck (checkedKeys) {
      this.checkedKeys = checkedKeys
      this.personnel(checkedKeys)
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
      this.autoExpandParent = false
    },
    set () {
      // 设置编辑器内容
      this.$nextTick(function () {
        // 此时已经渲染完成
        this.$refs.editor.content = this.content
      })
    },
    get () {
      // 读取编辑器HTML内容
      this.newFolder.content = this.$refs.editor.content
    },
    // 获取上传文件id
    getFileList () {
      let filesList = this.$store.state.file.appendixList
      let fileIds = []
      if (filesList.length !== 0 || filesList !== null) {
        for (let i = 0; filesList.length > i; i++) {
          fileIds.push(filesList[i].fileId)
        }
      }
      this.fileIds = fileIds
    },
    reset () {
      this.loading = false
      // 清空文本域
      this.newFolder.content = ''
      this.deptTreeKeys = +new Date()
      // 清空表单
      this.form.resetFields()
      this.personnelId = []
    },
    handleSubmit () {
      this.get() // 获取文本内容
      this.getFileList() // 获取上传文件id
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.personnelId.length !== 0) {
            const param = {
              id: this.exchangeId,
              sendUserIds: this.personnelId,
              content: this.newFolder.content,
              typeId: this.id,
              fileIds: this.fileIds,
              status: 1,
              title: values.title
            }
            this.$post('/briefing/creat', param).then((r) => {
              if (r.status === 200) {
                this.reset()
                this.$emit('success')
              }
            }).catch(() => {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
            })
          } else {
            this.$notification.warning({message: '系统提示', description: '请选择传阅的人员！', duration: 4})
          }
        }
      })
    },
    // 修改设置值
    setFormValues (user) {
      const param = {
        briefingId: user.id
      }
      this.$get('/briefing/getInfoById/', param).then((r) => {
        setTimeout(() => {
          this.form.setFieldsValue({
            title: user.title,
            sender: r.data.userName
          })
        }, 5)
        this.exchangeId = user.id
        this.personnelId = r.data.userId
        this.personnelName = r.data.userName
        this.id = user.typeId
        this.content = user.content
        this.set()
      })
    }
  }
}
</script>
