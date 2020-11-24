<template>
  <div style="height: 780px;overflow: auto;width: 100%">
      <a-form :form="form">
        <a-form-item label='标题' v-bind="formItemLayout">
          <a-input
            v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
        </a-form-item>
<!--        <a-form-item label='类别' v-bind="formItemLayout">-->
<!--          <a-select v-model="id">-->
<!--            <a-select-option v-for="n in typeData" :key="n.dictId" :value="n.dictId">{{n.fieldName}}</a-select-option>-->
<!--          </a-select>-->
<!--        </a-form-item>-->
        <a-form-item label='关联' v-bind="formItemLayout" v-has-any-permission="'newFolder:relevance'">
            <a-radio-group  @change="radioChange" v-model="defaultValue">
              <a-radio value="0" hidden>
              </a-radio>
              <a-radio value="1">
                年度考核
              </a-radio>
              <a-radio value="2">
                其他
              </a-radio>
            </a-radio-group>
        </a-form-item>
        <a-form-item label='是否同步' v-bind="formItemLayout" v-has-any-permission="'newFolder:synchroniz'">
          <a-checkbox :disabled="isRadio" @change="tbChange">门户网站</a-checkbox>
        </a-form-item>
<!--        <a-form-item v-if="isTb" label='来源' v-bind="formItemLayout">-->
<!--          <a-input v-decorator="['source']" disabled />-->
<!--        </a-form-item>-->
        <a-form-item v-if="isTb" label='时间' v-bind="formItemLayout">
          <a-date-picker  @change="tbTimeChange" :default-value="tbDefaultTime" />
        </a-form-item>
        <a-form-item v-if="isTb" label='栏目' v-bind="formItemLayout">
          <a-select mode="multiple" ref="lanmu" show-search style="width: 100%" placeholder="选择栏目" @change="handleChange">
            <a-select-option v-for="i in programa" :key="i.id" :value="i.id">
              {{ i.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
          <a-form-item label='限时回复' v-bind="formItemLayout">
          &nbsp;&nbsp;<a-checkbox :disabled="isEdit" @change="checkboxChange">
            <a-date-picker
              v-if="isSX"
              @change="onTimeChange"
              format="YYYY-MM-DD"
              :disabled-date="disabledDate"
            />
          </a-checkbox>
        </a-form-item>
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
          <editor height="350" :readonly=false ref="editor" refType="1"></editor>
        </a-form-item>
        <a-form-item label='附件上传' v-bind="formItemLayout">
          <appendix refId="-1" refType="1" :is-upload="true" unique="UUID"></appendix>
        </a-form-item>
        <a-form-item style="text-align: center">
        <a-button class="editable-add-btn" @click="reset">
          重置
        </a-button>
        <a-button class="editable-add-btn" type="primary" @click="submit (1)" style="margin-left: 20px" :loading="loading">
          保存
        </a-button>
          <a-button class="editable-add-btn" @click="submit (3)" style="margin-left: 20px;background-color: #87D068;color: white" :loading="loading">
            保存并发送
          </a-button>
        </a-form-item>
      </a-form>
    <a-modal
      :visible="yearVisible"
      title="年度考核"
      :afterClose="()=>isEdit=false"
      width="600px"
      okText="确定"
      cancelText="取消"
      :destroyOnClose="true"
      @cancel="yearClose"
      @ok="yearSubmit"
    >
      <a-select @change="onYearTime" style="width: 110px;height: 20px;margin-right: 20px;margin-bottom: 10px" placeholder="考核时间">
        <a-select-option v-for="(n,index) in yearTimeData" :key="index" :value="n.yearId" placeholder="考核时间">
          {{n.yearDate}}
        </a-select-option>
      </a-select>
      <a-checkbox @change="KHChange">
        年度考核
      </a-checkbox>
      <a-collapse v-if="this.selectedRowKeys[0] !== 0" accordion :bordered="false">
        <a-collapse-panel v-for="item in dataSource" :key="item.id" :header= "item.name">
          <a-table
            :data-source="item.list"
            :columns="columns"
            :loading="loading"
            :rowKey="(record)=> record.gradeId"
            :pagination="pagination"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
          >
          </a-table>
        </a-collapse-panel>
      </a-collapse>
    </a-modal>
        <personnel
          :folderVisible="folderVisible"
          @success="modalSubmit"
          @close="modalClose"
          ref="oldpersonnel"
        />
  </div>
</template>
<script>
import Personnel from './Personnel'
import Editor from '@/components/editor/Editor' // 导入编辑器组件
import Appendix from '@/components/appendix/Appendix'
import {mapState} from 'vuex'
import moment from 'moment'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'newFolder',
  components: {Editor, Appendix, Personnel}, // 局部注册
  data () {
    return {
      moment,
      loading: false, // 加载状态
      defaultValue: '0', // 关联点选默认值
      folderVisible: false, // 默认关闭模态框选择传阅人
      // 编辑器
      content: '<strong>初始化编辑器值</strong>',
      userName: '',
      colorArr: ['red', 'green', 'darkmagenta', 'turquoise', 'orange', 'blue', 'blueviolet', 'coral', 'deeppink', 'midnightblue'],
      personnelName: [],
      dataSource: [],
      selectedRowKeys: [],
      columns: [
        {
          title: '摘要',
          width: '100px',
          dataIndex: 'menusYear.summary'
        }],
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      yearVisible: false,
      formItemLayout,
      isEdit: false, // 选择时间
      redioData: 0,
      isSX: false, // 是否显示时间
      dateTime: null,
      isTb: false,
      isRadio: false,
      yearTimeData: [],
      yearTime: '',
      tbTime: '', // 选择时间
      tbDefaultTime: '', // 默认当前时间
      portal: false, // 是否同步门户网
      programa: [], // 栏目数据
      targetsId: '', // 栏目id
      // id: null,
      fileIds: [],
      typeData: [], // 下拉数据接收
      personnelId: [], // 模态框提交的人员id
      newFolder: {},
      form: this.$form.createForm(this),
      value: []
    }
  },
  computed: {
    ...mapState({
      user: state => state.account.user
    })
  },
  inject: ['reload'],
  component: {
    editor () {
      return this.$refs.myQuillEditor.quill
    }
  },
  mounted () {
    this.loading = true
    // 类别数据
    this.$get('/dict/getListTable', {parentId: '6c8bd4ecf92c36748f8c679a181d1221', pageSize: 50}).then(res => {
      this.typeData = res.data.data.records
      this.loading = false
    })
    this.yearK()
  },
  methods: {
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.personnelId = []
      this.newFolder = {}
    },
    yearK () {
      this.$get('/check/grade/selectByUserIdOrDateYear').then(res => {
        this.dataSource = res.data.data
      })
    },
    // 是否关联年度考核
    radioChange (e) {
      this.redioData = e.target.value
      if (e.target.value === '1') {
        this.$get('/check/year/list').then(res => {
          this.yearTimeData = res.data.data
        })
        this.yearVisible = true
      }
    },
    // 年度考核选择时间
    onYearTime (value) {
      this.yearTime = value
      this.$get('/check/grade/selectByUserIdOrDateYear', {yearId: value}).then(res => {
        this.dataSource = res.data.data
      })
    },
    yearClose () {
      this.yearVisible = false
      this.defaultValue = '0'
    },
    yearSubmit () {
      this.yearVisible = false
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
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
      this.tbDefaultTime = y + '-' + m + '-' + d
      // 获取栏目数据
      this.$get('/dict/getTargets').then((res) => {
        this.programa = res.data.data.targets
      })
      this.isTb = e.target.checked
      // 是否同步，是否选中门户网
      this.portal = e.target.checked
    },
    // 选择时间
    tbTimeChange (date, dateTime) {
      this.tbTime = date
    },
    handleChange (value) {
      this.targetsId = value.toString()
    },
    // 选择年度考核
    KHChange (e) {
      this.selectedRowKeys = []
      if (e.target.checked) {
        this.selectedRowKeysselectedRowKeys.push(0)
      }
    },
    // 勾选时间
    checkboxChange (e) {
      this.isSX = e.target.checked
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      this.dateTime = date
    },
    disabledDate (current) {
      return current && current < moment().endOf('day')
    },
    selectSender () {
      this.folderVisible = true
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
      // 清空表单
      this.form.resetFields()
      this.reload()
    },
    // 提交
    submit (statusId) {
      this.loading = true
      this.form.validateFields((err, values) => {
        this.get() // 获取文本内容
        this.getFileList() // 获取上传文件id
        if (!err) {
          // 判断是否勾选时间
          if (this.isSX) {
            if (this.dateTime == null) {
              this.$notification.warning({message: '系统提示', description: '请选择限制时间！', duration: 4})
              this.loading = false
              return
            }
          }
          // 是否选中关联
          if (this.redioData === '1') {
            if (this.selectedRowKeys.length === 0) {
              this.$notification.warning({message: '系统提示', description: '选中关联，并未选取摘要！', duration: 4})
              this.loading = false
              return
            }
          }
          // 判断是否选中摘要，选中则不为0，
          if (this.selectedRowKeys.length !== 0) { // 选中摘要，则必须选文件
            if (this.fileIds.length === 0) { // 如果没选文件，则弹出提示信息
              this.$notification.warning({message: '系统提示', description: '勾选年度考核摘要，必须上传文件！', duration: 4})
              this.loading = false
              return
            }
          }
          if (this.personnelId.length === 0) {
            this.$notification.warning({message: '系统提示', description: '请选择传阅的人员！', duration: 4})
            this.loading = false
          } else {
            // 判断是否同步且时间
            let atTime = ''
            let param = {}
            if (this.portal) {
              if (this.tbTime !== '') {
                atTime = this.tbTime
              } else {
                atTime = new Date()
              }
              param.synchronizeWeb = 1 // 是否同步门户网
              // param.source = values.source
              param.time = atTime
              param.targetsId = this.targetsId // 栏目id
            } else {
              this.targetsId = ''
              param.synchronizeWeb = 0
              // values.source = ''
            }
            // 关联年度考核要传个后台参数
            if (this.redioData === '1') {
              param.menusYearIds = this.selectedRowKeys // 年度考核摘要
              param.isCheck = this.redioData // 是否选中关联
              param.yearTime = this.yearTime
            }
            param.sendUserIds = this.personnelId // 选择的传阅人员
            param.content = this.newFolder.content // 内容
            // param.typeId = this.id // 类型选择
            param.status = statusId // 保存或者提交状态
            param.claimTime = this.dateTime // 限时回复时间
            param.isTime = this.isSX // 是否有回复时间
            param.fileIds = this.fileIds // 文件的id
            param.title = values.title // 标题
            this.$post('/exchange/creat', param).then((r) => {
              if (r.status === 200) {
                this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
                this.loading = false
                this.$router.push('/oa/messageMutual/outBox')
                this.reload()
              }
            })
          }
        } else {
          this.loading = false
          this.$notification.warning({message: '系统提示', description: '请输入有效填写数据！', duration: 4})
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
