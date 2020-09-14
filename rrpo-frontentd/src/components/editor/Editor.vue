<template>
  <div class="el-tiptap-editor__wrapper">
    <el-tiptap
      v-model="content"
      :extensions="extensions"
      :spellcheck="spellcheck"
      :menu-bubble-options="{ 'keep-in-bounds': false }"
      :placeholder="placeholder"
      :readonly="readonly"
      :height="height"
      @onInit="onInit"
    >
    </el-tiptap>
  </div>
</template>

<script>
import {
  Doc, Text, Paragraph, Heading, Bold, Link, Image,
  Blockquote, TextAlign, Indent,
  HorizontalRule, CodeView, Fullscreen, History, Iframe, LineHeight, TrailingNode,
  Table, TableHeader, TableCell, TableRow, FormatClear, TextColor, Preview,
  Print, SelectAll
} from 'element-tiptap'
import codemirror from 'codemirror'
import 'codemirror/lib/codemirror.css' // import base style
import 'codemirror/mode/xml/xml.js' // language
import 'codemirror/addon/selection/active-line.js' // require active-line.js
import 'codemirror/addon/edit/closetag.js'
import {notification} from 'ant-design-vue'
import {mapMutations} from 'vuex'

export default {
  name: 'Editor',
  props: {
    placeholder: {
      type: String,
      default: '点击输入内容 ...'
    },
    spellcheck: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    height: {
      type: String,
      default: '300px'
    },
    refType: {
      required: true,
      type: String
    },
    refId: {
      type: String,
      default: '-1'
    }
  },
  data () {
    // editor extensions
    // they will be added to menubar and bubble menu by the order you declare.
    return {
      editor: null,
      content: '',
      extensions: [
        new Doc(),
        new Text(),
        new Paragraph(),
        new Heading({ level: 5 }),
        new Bold(),
        new LineHeight(),
        new TextColor(),
        new TextAlign(),
        new Indent(),
        new History(),
        new Blockquote(),
        new FormatClear(),
        new Link(),
        new Image({
          uploadRequest: (file) => {
            return this.imageUpload(file)
          }
        }),
        new Iframe(),
        new Table({ resizable: true }),
        new TableHeader(),
        new TableCell(),
        new TableRow(),
        new HorizontalRule(),
        new TrailingNode(),
        new SelectAll(),
        new CodeView({
          codemirror,
          codemirrorOptions: {
            styleActiveLine: true,
            autoCloseTags: true
          }
        }),
        new Print(),
        new Preview(),
        new Fullscreen()
      ]
    }
  },
  methods: {
    imageUpload (file) {
      return new Promise((resolve, reject) => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('refType', this.refType)
        formData.append('refId', this.refId === '-1' ? '' : this.refId)
        this.$upload('file/uploadImage', formData)
          .then((res) => {
            if (res.data.status === true) {
              resolve(res.data.file.accessUrl)
              this.checkRef(res.data.file)
            } else {
              notification.error({
                message: '系统提示',
                description: res.data.message,
                duration: 4
              })
              reject(res.data.message)
            }
          })
      })
    },
    onInit (instance) {
      this.editor = instance.editor
      // 初始化临时文件列表
      if (this.refId === '-1') {
        this.initEditorList()
      }
    },
    checkRef (file) {
      if (this.refId === '-1') {
        this.saveEditorFile(file)
      }
    },
    ...mapMutations({
      saveEditorFile: 'file/addEditorFile',
      initEditorList: 'file/initEditorList'
    })
  },
  beforeDestroy () {
    this.editor.destroy()
  }
}
</script>
<style lang="less">
  .v-modal {
    z-index: 500 !important;
  }
  .el-dialog__wrapper {
    z-index: 501 !important;
  }
  .el-tiptap-popper {
    z-index: 499 !important;
  }
  .el-tooltip__popper {
    z-index: 488 !important;
  }
</style>
