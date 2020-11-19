<template>
  <a-drawer
    title="修改发送信息"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="true"
    @close="onClose"
    :visible="boxEditVisiable"
    >
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input v-decorator="['title',{rules: [{ required: true, message: '标题不能为空'}]}]"/>
        <a-input hidden v-decorator="['id']" />
      </a-form-item>
      <a-form-item label='类别' v-bind="formItemLayout" v-decorator="['typeId']">
        <a-select v-model="typeId">
          <a-select-option v-for="n in typeData" :key="n.dictId" :value="n.dictId">{{n.fieldName}}</a-select-option>
        </a-select>
      </a-form-item>
<!--      <a-form-item label='关联' v-bind="formItemLayout" v-has-any-permission="'newFolder:relevance'">-->
<!--        <a-radio-group  @change="radioChange" v-model="yearKH">-->
<!--          <a-radio :value="1">-->
<!--            年度考核-->
<!--          </a-radio>-->
<!--          <a-radio value="2">-->
<!--            其他-->
<!--          </a-radio>-->
<!--        </a-radio-group>-->
<!--      </a-form-item>-->
<!--      <a-form-item label='是否同步' v-bind="formItemLayout" >-->
<!--        <a-radio-group :disabled="isRadio" @change="tbChange" v-model="portal">-->
<!--          <a-radio :value="1">-->
<!--            门户网站-->
<!--          </a-radio>-->
<!--          <a-radio :value="2">-->
<!--            其他-->
<!--          </a-radio>-->
<!--        </a-radio-group>-->
<!--      </a-form-item>-->
<!--      <a-form-item v-if="isSX" label='来源' v-bind="formItemLayout">-->
<!--        <a-input v-decorator="['source']" />-->
<!--      </a-form-item>-->
<!--      <a-form-item v-if="isSX" label='时间' v-bind="formItemLayout">-->
<!--        <a-date-picker  @change="onTimeChange" :default-value="defaultTime" />-->
<!--      </a-form-item>-->
<!--      <a-form-item v-if="isSX" label='栏目' v-bind="formItemLayout">-->
<!--        <a-select mode="multiple" show-search style="width: 100%" placeholder="选择栏目" @change="handleChange" v-model="programaId">-->
<!--          <a-select-option v-for="i in programa" :key="i.id" :value="i.id">-->
<!--            {{ i.name }}-->
<!--          </a-select-option>-->
<!--        </a-select>-->
<!--      </a-form-item>-->
      <a-form-item label='传阅人员' v-bind="formItemLayout">
        <a-button @click="selectSender" ><a-icon type="usergroup-add" />重新选择人员</a-button>
        <a-icon style="font-size: 18px;margin-left: 12px" type="exclamation-circle" theme="twoTone" twoToneColor="#E21918" title="重新选择人员，将会把原有选中的人员清除"/>
        <a-collapse>
          <a-collapse-panel key="1" header="查看选中人员">
            <a-tag :style="'color:' + colorArr[Math.floor((Math.random() * colorArr.length))]" v-for="(value,index) in personnelName" :key="index">
              {{value}}
            </a-tag>
          </a-collapse-panel>
        </a-collapse>
      </a-form-item>

      <a-form-item label='发布内容' v-bind="formItemLayout">
<!--        <div style="text-align: right">-->
<!--          <a-button @click="updateFileButton"><a-icon type="cloud-upload" />修改附件</a-button></div>-->
<!--       v-if="isRouterAlive" -->
        <editor height="500px" :readonly=false ref="editor" refType="1"></editor>
<!--        <appendix :refId="this.exchangeId" refType="1" :is-upload="true" v-if="isRouterAlive"></appendix>-->
      </a-form-item>
      <a-form-item  label='附件上传' v-bind="formItemLayout">
        <appendix :refId="this.exchangeId" refType="1" :is-upload="true" :unique="this.exchangeId"></appendix>
      </a-form-item>
    </a-form>
    <update-file
      :updateFileVisiable="updateFileVisiable"
      :exchangeId="exchangeId"
      @close="updateFileClose"
    />
    <personnel
      :folderVisible="folderVisible"
      @success="modalSubmit"
      @close="modalClose"
      ref="oldpersonnel"
    />
    <div class="drawer-bootom-button">
      <a-popconfirm title="是否确认取消？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
import UpdateFile from './UpdateFile'
import Personnel from '../newFolder/Personnel'
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'ComEdit',
  components: {Editor, Appendix, UpdateFile, Personnel}, // 局部注册
  props: {
    boxEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false, // 加载状态
      updateFileVisiable: false,
      colorArr: ['red', 'green', 'darkmagenta', 'turquoise', 'orange', 'blue', 'blueviolet', 'coral', 'deeppink', 'midnightblue'],
      content: '<strong>初始化编辑器值</strong>',
      folderVisible: false, // 默认关闭模态框选择传阅人
      formItemLayout,
      checkedKeys: [],
      isRouterAlive: true, // 刷新组件
      typeId: null,
      typeData: [], // 下拉数据接收
      isSX: false,
      isRadio: false,
      dateTime: '', // 选择时间
      defaultTime: '', // 默认当前时间
      portal: 0, // 是否同步门户网
      programa: [], // 栏目数据
      targetsId: '', // 栏目id
      programaId: [],
      exchangeId: '',
      fileIds: [],
      personnelId: [], // 模态框提交的人员id
      personnelName: [],
      form: this.$form.createForm(this),
      editorOption: {
        placeholder: '请在这里输入'
      }
    }
  },
  component: {
    editor () {
      return this.$refs.myQuillEditor.quill
    }
  },
  mounted () {
    this.loading = true
    // 获取模态框树形结构
    this.$get('dept').then((r) => {
      this.deptTreeData = r.data.rows.children
      this.loading = false
    })
    // 类别数据
    this.$get('/dict/getListTable', {parentId: '6c8bd4ecf92c36748f8c679a181d1221', pageSize: 50}).then(res => {
      this.typeData = res.data.data.records
      this.loading = false
    })
  },
  methods: {
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    selectSender () {
      let result = []
      for (let index in this.personnelId) {
        result.push({userId: this.personnelId[index], username: this.personnelName[index]})
      }
      this.$refs.oldpersonnel.getPersonnerl(result)
      this.folderVisible = true
    },
    // 是否同步
    tbChange (e) {
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
    updateFileButton () {
      this.updateFileVisiable = true
    },
    updateFileClose () {
      this.updateFileVisiable = false
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
      this.content = this.$refs.editor.content
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
      this.content = ''
      // 清空表单
      this.form.resetFields()
      this.personnelId = []
    },
    handleSubmit () {
      this.get() // 获取文本内容
      this.getFileList() // 获取上传文件id
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.personnelId.length === 0) {
            this.$notification.warning({message: '系统提示', description: '请选择传阅的人员！', duration: 4})
          } else {
            // let atTime = ''
            // if (this.portal === 1) {
            //   if (this.dateTime !== '') {
            //     atTime = this.dateTime
            //   } else {
            //     atTime = new Date()
            //   }
            // } else {
            //   this.targetsId = ''
            //   values.source = ''
            // }
            const param = {
              id: this.exchangeId,
              sendUserIds: this.personnelId,
              content: this.content,
              typeId: this.typeId,
              // synchronizeWeb: this.portal, // 是否同步门户网
              // time: atTime,
              // targetsId: this.targetsId, // 栏目id
              // source: values.source,
              fileIds: this.fileIds,
              status: 1,
              title: values.title
            }
            this.$post('/exchange/creat', param).then((r) => {
              if (r.status === 200) {
                this.reset()
                this.$emit('success')
              }
            }).catch(() => {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
            })
          }
        }
      })
    },
    // 修改设置值
    setFormValues (user) {
      // 获取栏目数据
      // this.$get('/dict/getTargets').then((res) => {
      //   console.log('获取栏目信息', res.data.data.targets)
      //   this.programa = res.data.data.targets
      // })
      console.log('编辑数据', user)
      setTimeout(() => {
        this.form.setFieldsValue({
          id: user.id,
          title: user.title,
          source: user.source
        })
      }, 5)
      this.exchangeId = user.id
      this.typeId = user.typeId
      this.content = user.content
      this.$get('/exchange/getInfoById/' + user.id).then((r) => {
        console.log('获取用户：', r)
        this.personnelId = r.data.userId
        this.personnelName = r.data.userName
      })
      // this.portal = user.synchronizeWeb
      // this.programaId = user.targetsId.split(',')
      this.isSX = true
      // this.defaultTime = user.time
      this.set()
    }
  }
}
</script>
