<template>
  <a-modal
    :visible="visible"
    :confirm-loading="confirmLoading"
    :width= "850"
    loading
    :footer="null"
    style="text-align: center"
  >
    <div class="banner">
    <div style="height: 200px">
      <h3 style="text-align: center">导入模板图片<span style="color: red">(注释：目前只支持XLSX后缀文件导入)</span></h3>
      <img src="static/img/shili.png">
    </div>
    <hr />
    <div class="footer">
      <a-button @click="Onclose">取消</a-button>
                    <a-upload
                      name="multipartFile"
                      :multiple="false"
                      :showUploadList="false"
                      accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z"
                      :action="action"
                      :data="datar"
                      :headers="headers"
                      @change="handleChange"
                    >
                      <a-button @click="starClick">开始导入</a-button>
                    </a-upload>
    </div>
    </div>
  </a-modal>
</template>

<script>
import {notification} from 'ant-design-vue'
export default {
  name: 'yearChoose',
  props: ['visible'],
  data () {
    return {
      //  状态下拉
      options: [],
      yearId: '',
      confirmLoading: false,
      // 导入
      action: this.$constURL + 'check/menus-year/excel',
      headers: {
        Authentication: this.$store.state.account.token
      },
      datar: {
        yearId: ''
      }
    }
  },
  methods: {
    rest () {
    },
    Onclose () {
      this.$emit('cancel')
      this.rest()
    },
    getId (res) {
      this.datar.yearId = res
    },
    starClick () {
      this.$emit('check')
    },
    // 下拉函数
    onChangeTime (value) {
      // this.yearex = value
      if (value.length > 0) {
        this.yearId = value[0]
      } else {
        this.yearId = ''
      }
    },
    handleChange (info) {
      const status = info.file.status
      if (status === 'done') {
        if (info.file.response.status) {
          notification.success({
            message: '系统提示',
            description: '文件导入成功！',
            duration: 4
          })
          this.$emit('check')
        } else {
          notification.error({
            message: '系统提示',
            description: info.file.response.message,
            duration: 4
          })
        }
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} file upload failed.`)
      }
    }
  }
}
</script>

<style scoped>
.banner {
  position: relative;
}
.footer {
  text-align: right;
}
</style>
