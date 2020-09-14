<template>
  <a-drawer
    title="信息互递"
    :maskClosable="false"
    width=850
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="NoticeaddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='标题' v-bind="formItemLayout">
        <a-input v-model="newNoticea.title"
                 v-decorator="['title',
                   {rules: [{ required: true, message: '标题不能为空'},
                   { max: 30, message: '长度不能超过30个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='发布内容' v-bind="formItemLayout">
        <quill-editor
          :id="1"
          v-model="newNoticea.content"
          ref="myQuillEditor"
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
  name: 'Shareadd',
  props: {
    NoticeaddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      newNoticea: {},
      editorOption: {
        placeholder: '请在这里输入'
      },
      form: this.$form.createForm(this),
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
      this.share = {}
      this.newNoticea.content = ''
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
      this.form.resetFields()
    },
    addTitle () {
      // setTimeout(res => {
      //   for (let item of toolbarTips) {
      //     let tip = document.querySelector('.quill-editor ' + item.Choice)
      //     if (!tip) continue
      //     this.$set(tip, 'title', item.title)
      //   }
      // })
    },
    // 提交
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          // this.$post('/inform', this.newNoticea).then(() => {
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
    NoticeaddVisiable () {
      if (this.NoticeaddVisiable) {
        this.addTitle()
      }
    }
    // NoticeaddVisiable () {
    //   if (this.NoticeaddVisiable) {
    //     this.$get('dept').then((r) => {
    //       this.deptTreeData = r.data.rows.children
    //     })
    //   }
    // }
  }
}
</script>
