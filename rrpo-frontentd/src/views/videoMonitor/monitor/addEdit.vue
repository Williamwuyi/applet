<template>
  <a-drawer
    :title="title"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="true"
    @close="onClose"
    :visible="addEditVisiable"
  >
    <a-form :form="form">
      <a-row>
        <a-col :span="8">
          <a-form-item label='公安处' v-bind="formItemLayout">
            <a-select @change="cityGChange" ref="gacs" v-decorator="['gac',{rules:[{required: true, message: '请选择公安处'}]}]">
              <a-select-option v-for="(n,index) in cityG" :key="index" :value="n.id" >
                {{n.dept.deptName}}
              </a-select-option>
            </a-select>
            <a-input hidden v-decorator="['id']"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='地州市' v-bind="formItemLayout">
            <a-select @change="citySChange" ref="dzss" v-decorator="['dzs',{rules:[{required: true, message: '请先选择公安处，再选地州市'}]}]">
              <a-select-option v-for="(n,index) in cityS" :key="index" :value="n.dept.deptId" >
                {{n.dept.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='县市区' v-bind="formItemLayout">
            <a-select @change="cityQChange" ref="xsqs" v-decorator="['xsq',{rules:[{required: true, message: '请先选择地州市，再选县区'}]}]">
              <a-select-option v-for="(n,index) in cityQ" :key="index" :value="n.deptId" >
                {{n.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='乡镇街道' v-bind="formItemLayout">
            <a-select ref="xzjds" v-decorator="['xzjd',{rules:[{required: true, message: '请选择乡镇街道'}]}]">
              <a-select-option v-for="(n,index) in cityX" :key="index" :value="n.deptId" >
                {{n.deptName}}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='线路名称' v-bind="formItemLayout">
            <a-input v-decorator="['xlmc', {rules: [{required: true, message: '请输入线路名称'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='铁路里程' v-bind="formItemLayout">
            <a-input v-decorator="['tllc', {rules: [{required: true, message: '请输入铁路里程'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='铁路距离' v-bind="formItemLayout">
            <a-input v-decorator="['tljl', {rules: [{required: true, message: '请输入离铁路距离'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='对应方位' v-bind="formItemLayout">
            <a-input v-decorator="['azfw', {rules: [{required: true, message: '请输入铁路方位'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='道路名称' v-bind="formItemLayout">
            <a-input v-decorator="['dlmc', {rules: [{required: true, message: '请输入道路名称'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='道路里程' v-bind="formItemLayout">
            <a-input v-decorator="['dllc', {rules: [{required: true, message: '请输入道路里程'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='安装部位' v-bind="formItemLayout">
            <a-input v-decorator="['azbw', {rules: [{required: true, message: '请输入安装部位'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='视屏探头数' v-bind="formItemLayout">
            <a-input v-decorator="['sl', {rules: [{required: true, message: '请输入视频探头数'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='视屏器类型' v-bind="formItemLayout">
            <a-select v-decorator="['jkqlx',{rules:[{required: true, message: '请选择视频器类型'}]}]">
              <a-select-option value="枪机">枪机</a-select-option>
              <a-select-option value="球机">球机</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='监控范围' v-bind="formItemLayout">
            <a-input v-decorator="['jkfw', {rules: [{required: true, message: '请输入监控范围'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='监控设计种类' :labelCol="{span:10}" :wrapperCol="{span:14}">
            <a-input v-decorator="['jksjzl', {rules: [{required: true, message: '请输入监控设计种类'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='夜视功能' v-bind="formItemLayout">
            <a-select v-decorator="['jkysgn',{rules:[{required: true, message: '请选择夜视功能'}]}]">
              <a-select-option value="有">有</a-select-option>
              <a-select-option value="无">无</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='监控高度' v-bind="formItemLayout">
            <a-input v-decorator="['jkgd', {rules: [{required: true, message: '请输入监控高度'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='保留天数' v-bind="formItemLayout">
            <a-input v-decorator="['blts', {rules: [{required: true, message: '请输入线路名称'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='联网单位' v-bind="formItemLayout">
            <a-select v-decorator="['lwdw',{rules:[{required: true, message: '请选择联网单位'}]}]">
              <a-select-option value="铁路">铁路</a-select-option>
              <a-select-option value="地方">地方</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='建设单位' v-bind="formItemLayout">
            <a-input v-decorator="['jsdw', {rules: [{required: true, message: '请输入建设单位'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='管理单位' v-bind="formItemLayout">
            <a-input v-decorator="['gldw', {rules: [{required: true, message: '请输入管理单位'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label='使用单位' v-bind="formItemLayout" >
            <a-input v-decorator="['sydw', {rules: [{required: true, message: '请输入使用单位'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='派出所' v-bind="formItemLayout">
            <a-input v-decorator="['pcs', {rules: [{required: true, message: '请输入派出所'}]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='属地派出所' v-bind="formItemLayout">
            <a-input v-decorator="['sdpcs', {rules: [{required: true, message: '请输入属地派出所'}]}]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="8">
          <a-form-item label="安装时间:" v-bind="formItemLayout" required>
            <a-month-picker placeholder="安装时间" @change="onChange" v-model="restInstallTime" >
            </a-month-picker>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='备注' v-bind="formItemLayout">
            <a-input v-decorator="['bz']" type="textarea" />
          </a-form-item>
        </a-col>
        </a-row>
<!--      <a-row>-->
<!--        <a-col :span="20">-->
<!--          <a-form-item label='上传文件' :labelCol="{span:3}" :wrapperCol="{span:21}">-->
<!--            <a-upload-->
<!--              list-type="picture-card"-->
<!--              name="file"-->
<!--              :multiple="false"-->
<!--              accept="image/*"-->
<!--              :action="action"-->
<!--              :headers="headers"-->
<!--              :remove="remove"-->
<!--              :file-list="fileList"-->
<!--              @preview="handlePreview"-->
<!--              @change="handleChange"-->
<!--            >-->
<!--              <div v-if="fileList.length < 5">-->
<!--                <a-icon type="plus" />-->
<!--                <div class="ant-upload-text">-->
<!--                  点击上传-->
<!--                </div>-->
<!--              </div>-->
<!--            </a-upload>-->
<!--            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">-->
<!--              <img alt="example" style="width: 100%" :src="previewImage" />-->
<!--            </a-modal>-->
<!--          </a-form-item>-->
<!--        </a-col>-->
<!--      </a-row>-->
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

const formItemLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 }
}
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'addEdit',
  props: {
    addEditVisiable: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      formItemLayout,
      id: '',
      cityG: [],
      cityS: [],
      cityQ: [],
      cityX: [],
      restInstallTime: null, // 重置时间
      installTime: null, // 安装时间
      loading: false,
      previewVisible: false,
      previewImage: '',
      action: this.$constURL + '/file/uploadImage',
      headers: {
        Authentication: this.$store.state.account.token
      },
      fileList: [],
      fileids: [],
      form: this.$form.createForm(this)
    }
  },
  mounted () {
    // 公安处
    this.$get('/accident/dept/list').then(res => {
      this.cityG = res.data.data
    })
  },
  methods: {
    // 点击公安处查市级
    cityGChange (key) {
      this.$get('/accident/dept/list', {prentId: key}).then(res => {
        this.cityS = res.data.data
      })
      this.form.setFieldsValue({
        dzs: '',
        xsq: '',
        xzjd: ''
      })
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityQ = res.data
      })
      this.form.setFieldsValue({
        xsq: '',
        xzjd: ''
      })
    },
    cityQChange (key) {
      this.$get('/dept/list', {deptId: key}).then(res => {
        this.cityX = res.data
      })
      this.form.setFieldsValue({
        xzjd: ''
      })
    },
    // 选择开始时间
    onChange (value, date) {
      this.installTime = date
    },
    // 文件删除
    remove (info1) {
      // 当上传成功的图片时删除一样id
      if (info1.response.status) {
        this.fileList = this.fileList.filter(res => {
          return res.response.file.fileId !== info1.response.file.fileId
        })
        // 当上传不成功时去除不成功状态
      } else if (info1.status === 'error') {
        this.fileList = this.fileList.filter(res => {
          return res.status !== 'error'
        })
      }
    },
    // 自定义文件上传
    handleChange (record) {
      this.fileList = record.fileList
      if (record.file.status === 'done') {
        if (record.file.response.status) {
          // 后台
          let uploadFile = record.file.response.file.fileId
          this.fileids.push(uploadFile)
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
    handleCancel () {
      this.previewVisible = false
    },
    // 点击图片回调
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    setFormValues (data) {
      console.log('获取编辑数据', data)
      let arr = data.tpFile
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
      // 实现拉取表单数据并且赋值 areaId
      let fields = ['id', 'gac', 'dzs', 'xsq', 'xzjd', 'xlmc', 'tllc',
        'azfw', 'tljl', 'dlmc', 'dllc', 'azbw',
        'sl', 'jkqlx', 'jkfw', 'jksjzl', 'jkysgn', 'jkgd', 'blts', 'lwdw', 'jsdw', 'gldw', 'sydw', 'pcs', 'sdpcs', 'azsj', 'bz']
      Object.keys(data).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = data[key]
          this.form.setFieldsValue(obj)
        }
      })
      this.restInstallTime = data.azsj
    },
    handleSubmit () {
      this.loading = true
      this.form.validateFields((err, values) => {
        if (!err) {
          values.gac = this.$refs.gacs.$el.innerText
          values.dzs = this.$refs.dzss.$el.innerText
          values.xsq = this.$refs.xsqs.$el.innerText
          values.xzjd = this.$refs.xzjds.$el.innerText
          values.azsj = this.installTime
          values.tp = []
          this.fileList.forEach(t => {
            values.tp.push(t.response.file.fileId)
          })
          this.$post('/accident/jk/saveOrUpdate', values).then(res => {
            if (res.data.status === 1) {
              this.reset()
              this.$emit('success')
              this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
              this.loading = false
            } else {
              this.$notification.warning({message: '系统提示', description: '操作失败！', duration: 4})
              this.loading = false
              this.$emit('close')
            }
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    reset () {
      this.form.resetFields()
      this.fileList = []
    },
    // 取消
    onClose () {
      // this.reset()
      this.form.resetFields()
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>
