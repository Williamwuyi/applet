<template>
  <a-drawer
    title="信息互递"
    :maskClosable="false"
    width=850
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="NoticeEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input
          v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='发布人' v-bind="formItemLayout">
        <a-input
          v-decorator="['createUser']"/>
      </a-form-item>
      <a-form-item label='发布内容' v-bind="formItemLayout">
        <quill-editor
          :id="2"
          v-model="content"
          ref="EditQuillEditor"
          :options="editorOption"
          @blur="onEditorBlur($event)" @focus="onEditorFocus($event)"
          @change="onEditorChange($event)">
        </quill-editor>
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
// import {toolbarTips} from 'utils/quill-title'
const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'ShareEdit',
  props: {
    NoticeEditVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      checkedKeys: [],
      editorOption: {
        placeholder: '请在这里输入'
      },
      expandedKeys: [],
      content: null,
      share: {}
    }
  },
  component: {
    editor () {
      return this.$refs.EditQuillEditor.quill
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.newNoticea.content = ''
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.form.resetFields()
      this.$emit('close')
    },
    setFormValues (user) {
      let fields = ['title', 'createUser', 'id']
      this.content = user.content
      Object.keys(user).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
    },
    addTitleq () {
      // setTimeout(res => {
      //   for (let item of toolbarTips) {
      //     let tipa = document.querySelector('.quill-editor ' + item.Choice)
      //     if (!tipa) continue
      //     this.$set(tipa, 'title', item.title)
      //   }
      // })
    },
    // 修改
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let newedite = this.form.getFieldsValue()
          newedite.content = this.content
          // this.$put('/inform', newedite).then(() => {
          //   this.loading = false
          //   this.reset()
          //   this.$emit('success')
          // }).catch(() => {
          //   this.loading = false
          // })
        }
      })
    }
  },
  watch: {
    NoticeEditVisiable () {
      if (this.NoticeEditVisiable) {
        this.addTitleq()
      }
    }
  }
}
</script>
