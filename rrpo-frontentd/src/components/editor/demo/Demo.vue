// Editor Demo, code by Sinliz

// -------------------------------------------------------props-------------------------------------------------------
// |  1.extensions：编辑器组件配置
// |  2.spellcheck：拼写检查 Boolean，true/false，默认false
// |  3.placeholder：提示文字 String，默认'Write something.'
// |  4.readonly：只读 Boolean，true/false，默认 false
// |  5.height：编辑器初始化高度 String， 默认'500px'
// |  6.refType：必填项!! 外部关联类型 String [1.信息互递 2.一事一奖 3.通知公告 4.公共信息-考核资料 5.公共信息-我的文件]
// |  7.refId：外部关联ID String，默认-1.表示无关联
// -------------------------------------------------------------------------------------------------------------------

<template>
  <div class="id">
    <editor height="500px" :readonly=false ref="editor" refType="5" refId="45692b4c36ac427ce34e59eae40ad4b7"></editor>
    <appendix refId="45692b4c36ac427ce34e59eae40ad4b7" refType="5"></appendix>
    <button @click="get">get</button>
    <button @click="set">set</button>
    <button @click="getFileList">获取文件列表</button>
  </div>
</template>
<script>
import Editor from '../../editor/Editor' // 导入编辑器组件
import Appendix from '../../appendix/Appendix'
export default {
  components: {Editor, Appendix}, // 局部注册
  data () {
    return {
      content: '<strong>初始化编辑器值</strong>'
    }
  },
  methods: {
    // ----------------------------编辑器----------------------
    get () {
      // 读取编辑器HTML内容
      // this.$refs.editor.content
      console.log(this.$refs.editor.content)
    },
    set () {
      // 设置编辑器内容
      // this.$refs.editor.content = value
      this.$refs.editor.content = this.content
    },
    getFileList () {
      // TODO 文件列表为：编辑器内上传图片 + 所有附件文件 (编辑器内图片上传后删除 无法检查，最终当编辑器能容保存时，需要对比编辑器内图片与此文件列表，进行后台处理)
      console.log(this.$store.state.file.editorList)
    },

    // ----------------------------附件-----------------------
    // TODO 附件文件列表
    getAppendixFileList () {
      console.log(this.$store.state.file.appendixList)
    }
  }
}
</script>
