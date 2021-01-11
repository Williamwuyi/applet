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
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input
          ref="aab"
                 v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
      </a-form-item>
        <a-form-item>
          <a-input hidden v-decorator="['id']"/>
        </a-form-item>
      <a-form-item label='通知方式' v-bind="formItemLayout" id="hchecked">
        <a-checkbox :checked="Achecked" v-model="Achecked">
          站内通知
        </a-checkbox>
        <a-checkbox :checked="Bchecked" v-model="Bchecked" >
          微信小程序通知
        </a-checkbox>
      </a-form-item>
<!--      <a-checkbox :checked="Cchecked" v-model="Cchecked">-->
<!--        是否同步到门户网站-->
<!--      </a-checkbox>-->
         <a-form-item label='内容' v-bind="formItemLayout">
           <editor height="600px" :readonly=false ref="editor" refType="3" ></editor>
         </a-form-item>
      <a-form-item label='附件' v-bind="formItemLayout">
        <appendix :refId="refId" :unique="refId" refType="3" :is-upload="true"></appendix>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="确定放弃修改？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
       <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>
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
    NoticeEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      dabuclick: true,
      formItemLayout,
      Achecked: false,
      Bchecked: false,
      Cchecked: false,
      newedite: {},
      refId: '',
      content: undefined,
      form: this.$form.createForm(this)
    }
  },
  methods: {
    reset () {
      this.dabuclick = true
      this.loading = false
      this.form.resetFields()
      this.Achecked = this.Bchecked = this.Cchecked = false
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues (user) {
      if (user.station === 1) {
        this.Achecked = true
      }
      if (user.applets === 1) {
        this.Bchecked = true
      }
      if (user.synchronizeWeb === 1) {
        this.Cchecked = true
      }
      this.refId = user.id
      let fields = ['title', 'createUser', 'id']
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
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
    getFileList () {
      // TODO 文件列表为：编辑器内上传图片 + 所有附件文件 (编辑器内图片上传后删除 无法检查，最终当编辑器能容保存时，需要对比编辑器内图片与此文件列表，进行后台处理)
      console.log(this.$store.state.editor.editorList)
    },
    // TODO 附件文件列表
    getAppendixFileList () {
      console.log(this.$store.state.file.appendixList)
    },
    // 修改
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.dabuclick) {
            this.dabuclick = false
            this.loading = true
            this.newedite = this.form.getFieldsValue()
            this.newedite.content = this.$refs.editor.content
            // 判断通知状态
            if (this.Achecked) {
              this.newedite.station = 1
            } else {
              this.newedite.station = 0
            }
            if (this.Bchecked) {
              this.newedite.applets = 1
            } else {
              this.newedite.applets = 0
            }
            if (this.Cchecked) {
              this.newedite.synchronizeWeb = 1
            } else {
              this.newedite.synchronizeWeb = 0
            }
            this.$put('/inform', this.newedite).then(() => {
              this.loading = false
              this.reset()
              this.$emit('success')
            }).catch(() => {
              this.loading = false
            })
          }
        }
      })
    }
  }
}
</script>
