<template>
  <a-drawer
    title="编辑"
    :maskClosable="false"
    :zIndex="100"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="NoticeEditVisiable"
    >
    <a-form :form="form">
      <a-row>
        <a-col :span="8"><a-form-item label='编码' :labelCol="{span: 5}"
                            :wrapperCol="{span: 7, offset: 0}">
          <a-input-number v-decorator="['bm',
                   {rules: [{ required: true}
                  ]}]"/>
        </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='公安处' :labelCol="{span: 5}"
                       :wrapperCol="{span: 15, offset: 1}">
            <a-select  v-decorator="['ga']">
              <a-select-option v-for="(n) in cityG" :key="n.dept.deptName" :value="n.dept.deptName" >
                {{n.dept.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item>
            <a-input hidden v-decorator="['id']"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8"><a-form-item label='地州市' :labelCol="{span: 5}"
                                      :wrapperCol="{span: 15, offset: 1}">
          <a-select @change="citySChange" ref="dzs" v-model="shi">
            <a-select-option v-for="(n) in cityS" :key="n.deptName" :value="n.deptId" >
              {{n.deptName}}
            </a-select-option>
          </a-select>
        </a-form-item>
        </a-col>
        <a-col :span="8"><a-form-item label='县市区' :labelCol="{span: 5}"
                                      :wrapperCol="{span: 15, offset: 1}">
          <a-select @change="cityQChange" ref="xsq" v-model="Smsg">
            <a-select-option v-for="(n) in cityQ" :key="n.deptName" :value="n.deptId" >
              {{n.deptName}}
            </a-select-option>
          </a-select>
        </a-form-item>
        </a-col>
        <a-col :span="8"><a-form-item label='乡镇街道' :labelCol="{span: 7}"
                                      :wrapperCol="{span: 15, offset: 1}">
          <a-select  v-model="Qmsg" ref="xz">
            <a-select-option v-for="(n) in cityX" :key="n.deptName" :value="n.deptName" >
              {{n.deptName}}
            </a-select-option>
          </a-select>
        </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12">
          <a-form-item label='座落村组' v-bind="formItemLayout">
            <a-input v-decorator="['zl',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='派出所' v-bind="formItemLayout">
            <a-input v-decorator="['pcs',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12">
          <a-form-item label='警务区' :labelCol="{span: 4}"
                       :wrapperCol="{span: 18, offset: 1}">
            <a-input v-decorator="['jwq',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='工务段' v-bind="formItemLayout">
            <a-select v-decorator="['gwd']">
              <a-select-option v-for="(n,index) in trackData" :key="index" :value="n.fieldName">
                {{n.fieldName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12">
          <a-form-item label='公里数' :labelCol="{span: 4}"
                       :wrapperCol="{span: 18, offset: 1}">
            <a-input v-decorator="['gls',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='修建方位' v-bind="formItemLayout">
            <a-input v-decorator="['fw',
                   {rules: [{ required: true, message: '修建方位'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col>
          <a-form-item label='修建长度'>
            <a-input v-decorator="['cd',
                   {rules: [{ required: true, message: '修建长度'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col>
          <a-form-item label='交通事故情况'>
            <a-textarea
              v-decorator="['jtsgqk',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
              placeholder="请输入事故情况" allow-clear />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col>
          <a-form-item label='修建原因及周边社情'>
            <a-textarea
              v-decorator="['yy',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
              placeholder="请输入周边社情情况" allow-clear />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col>
          <a-form-item label='路地已开展工作'>
            <a-textarea
              v-decorator="['kzqk',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"
              placeholder="请输入开展工作情况" allow-clear />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col>
          <a-form-item label='现场图' :labelCol="{span: 2}"
                       :wrapperCol="{span: 18, offset: 1}">
            <div class="clearfix">
              <a-upload
                list-type="picture-card"
                name="file"
                :multiple="false"
                accept="image/*"
                :action="action"
                :headers="headers"
                :data="datas"
                :remove="remove"
                :file-list="fileList"
                @preview="handlePreview"
                @change="handleChange"
              >
                <div v-if="fileList.length < 5">
                  <a-icon type="plus" />
                  <div class="ant-upload-text">
                    点击上传
                  </div>
                </div>
              </a-upload>
              <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                <img alt="example" style="width: 100%" :src="previewImage" />
              </a-modal>
            </div>
          </a-form-item>
        </a-col>
      </a-row>
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
import {notification} from 'ant-design-vue'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'BriggesEdit',
  props: {
    NoticeEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      previewVisible: false,
      previewImage: '',
      fileList: [],
      // 自定义文件上传
      action: this.$constURL + 'file/uploadImage',
      headers: {
        Authentication: this.$store.state.account.token
      },
      datas: {
        refId: '-1'
      },
      fileids: [],
      loading: false,
      isRadio: false,
      cityS: [],
      cityQ: [],
      cityX: [],
      Smsg: '',
      Qmsg: '',
      cityG: [],
      shi: '',
      trackData: [], // 工务段
      formItemLayout,
      newedite: {},
      form: this.$form.createForm(this)
    }
  },
  mounted () {
    this.$get('/dept/queryDeptChileNotshiro', {prentId: '0'}).then(res => {
      this.cityS = res.data.data
    })
    // 公安处
    this.$get('/accident/dept/list').then(res => {
      this.cityG = res.data.data
    })
    // 公务的
    this.$get('/dict/getListTable', {parentId: '521c7e776dbad0ee1fe118e9aee63121', pageSize: 50}).then(res => {
      this.trackData = res.data.data.records
    })
  },
  methods: {
    reset () {
      this.loading = false
      this.form.resetFields()
      this.fileList = this.fileids = []
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    // 图片上传
    // 自定义文件上传
    handleChange (record) {
      this.fileList = record.fileList
      if (record.file.status === 'done') {
        if (record.file.response.status) {
          notification.success({
            message: '系统提示',
            description: '文件上传成功！',
            duration: 4
          })
        } else {
          notification.error({
            message: '系统提示',
            description: record.file.response.message,
            duration: 4
          })
        }
      } else if (record.file.status === 'error') {
        this.$message.error(`${record.file.name} file upload failed.`)
      }
    },
    // 文件删除
    remove (info1) {
      // if (info1.response.status) {
      this.fileList = this.fileList.filter(res => {
        if (res.response.file.fileId === info1.response.file.fileId) {
          this.$post('/file/deleteFile', {fileId: res.response.file.fileId}).then(res => {
          })
        }
        return res.response.file.fileId !== info1.response.file.fileId
      })
      // }
    },
    setFormValues (user) {
      this.shi = user.dzs
      this.Smsg = user.xsq
      this.Qmsg = user.xz
      this.datas.refId = user.id
      let arr = user.tpFile
      arr.forEach(t => {
        this.fileList.push({
          url: t.accessUrl,
          uid: t.fileId,
          name: t.oldName,
          status: t.status,
          response: {
            file: {
              fileId: t.fileId,
              name: t.oldName,
              status: t.status,
              url: t.accessUrl
            }
          }})
      })
      let fields = ['bm', 'zl', 'pcs', 'jwq', 'gls', 'fw', 'cd', 'jtsgqk', 'yy', 'kzqk', 'id', 'gwd', 'ga']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.Smsg = ''
      this.Qmsg = ''
      this.$get('/dept/queryDeptChileNotshiro', {prentId: key}).then(res => {
        this.cityQ = res.data.data
      })
      this.cityQChange(key)
    },
    cityQChange (record) {
      this.Qmsg = ''
      this.$get('/dept/queryDeptChileNotshiro', {prentId: record}).then(res => {
        this.cityX = res.data.data
      })
    },
    // 修改
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          // this.loading = true
          let newadd = {...this.form.getFieldsValue()}
          newadd.dzs = this.$refs.dzs.$el.innerText
          newadd.xsq = this.$refs.xsq.$el.innerText
          newadd.xz = this.$refs.xz.$el.innerText
          // newadd.tp = []
          // this.fileList.forEach(t => {
          //   newadd.tp.push(t.response.file.fileId)
          // })
          this.$post('/accident/ql/saveOrUpdate', newadd).then(() => {
            this.loading = false
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
