/* eslint-disable no-undef */
<template>
  <a-drawer
    :zIndex="100"
    title="添加"
    :maskClosable="false"
    width=45%
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="NoticeaddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
<!--      v-model="newNoticea.title"-->
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='通知方式' v-bind="formItemLayout">
        <a-checkbox-group @change="onChange">
          <a-checkbox value="A">
            站内通知
          </a-checkbox>
          <a-checkbox value="B">
            微信小程序通知
          </a-checkbox>
        </a-checkbox-group>
<!--        <a-checkbox value="C">-->
<!--          是否同步到门户网站-->
<!--        </a-checkbox>-->
      </a-form-item>
       <a-form-item label='发布内容' v-bind="formItemLayout">
      <editor height="400px" :readonly=false ref="editor" refType="3" ></editor>
      </a-form-item>
      <a-form-item label='附件上传' v-bind="formItemLayout">
        <appendix refId="-1" unique="UUID" refType="3" :is-upload="true"></appendix>
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
         <a-popconfirm title="确定放弃修改？" @confirm="onClose"  okText="确定" cancelText="取消">
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
  name: 'Noticeadd',
  components: {Editor, Appendix}, // 局部注册
  props: {
    NoticeaddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      dabuclick: true,
      formItemLayout,
      // 编辑器
      content: '<strong>初始化编辑器值</strong>',
      form: this.$form.createForm(this),
      newNoticea: {
        // 站内通知
        station: 0,
        // 微信小程序通知
        applets: 0,
        // 是否同步到门户网站
        synchronizeWeb: 0
      },
      value: []
    }
  },
  component: {
  },
  methods: {
    reset () {
      this.loading = false
      // 清空文本域
      this.newNoticea.content = ''
      // 清空表单
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
      this.newNoticea = {}
    },
    // 富文本编辑
    get () {
      // 读取编辑器HTML内容
      // this.$refs.editor.content
      this.newNoticea.content = this.$refs.editor.content
    },
    set () {
      // 设置编辑器内容
      // this.$refs.editor.content = value
      this.$refs.editor.content = this.content
    },
    // TODO 附件文件列表
    getAppendixFileList () {
      console.log(this.$store.state.file.appendixList)
    },
    // 通知
    onChange (e) {
      for (var i = 0; i < e.length; i++) {
        // 判断数组是否有ABC，没有赋值为0，防止先点击后取消的状态
        if (e.indexOf('A') === -1) {
          this.newNoticea.station = 0
        }
        if (e.indexOf('B') === -1) {
          this.newNoticea.applets = 0
        }
        if (e.indexOf('C') === -1) {
          this.newNoticea.synchronizeWeb = 0
        }
        switch (e[i]) {
          case 'A' : this.newNoticea.station = 1
            break
          case 'B' : this.newNoticea.applets = 1
            break
          case 'C' : this.newNoticea.synchronizeWeb = 1
            break
        }
      }
    },
    // 添加
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.dabuclick) {
            this.dabuclick = false
            this.get()
            this.loading = true
            let newadd = {...this.form.getFieldsValue(), ...this.newNoticea}
            let file = this.$store.state.file.appendixList
            newadd.fileIds = []
            file.forEach(t => {
              newadd.fileIds.push(t.fileId)
            })
            console.log(newadd)
            this.$post('/inform', newadd).then(() => {
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
<style>
</style>
