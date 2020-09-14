<template>
  <div>
    <a-modal
      :visible="visible"
      title='文件导入'
      okText='确定'
      :footer="null"
      @cancel="() => {onClose() }"
      @ok="() => { onImport() }"
    >
      <a-form layout='vertical' :form="form">
        <div>
        <a-form-item label='' style="margin-bottom: 2rem;height:300px;overflow:auto;" >
          <a-tree
            :key="deptTreeKeys"
            ref="tree"
            :checkable="true"
            :checkStrictly="true"
            @check="handleCheck"
            @expand="handleExpand"
            :expandedKeys="expandedKeys"
            :treeData="deptTreeData">
            </a-tree>
        </a-form-item>
        </div>
        <a-form-item>
<!--          <a-upload-->
<!--           name="file"-->
<!--           :multiple="false"-->
<!--           @change="handleChange"-->
<!--           :customRequest="customRequest"-->
<!--           :file-list="fileList"-->
<!--          >-->
<!--            <a-button> <a-icon type="upload" /> 上传文件 </a-button>-->
<!--          </a-upload>-->
          <a-upload
            name="file"
            :multiple="false"
            accept=".xls, .xlsx"
            :action="action"
            :headers="headers"
            :data="data"
            :showUploadList="false"
            :loading="loading"
            @change="handleChange"
          >
            <a-button> <a-icon type="upload" /> 上传文件 </a-button>
          </a-upload>
        </a-form-item>
        <a-form-item>
          <span class="uploadHint">注意！只支持 xls或者xlsx格式的文件</span>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import axios from 'axios'
import store from '../../../store'
import {notification} from 'ant-design-vue'
let checkedKeysVlue = {
  deptId: ''
}
export default {
  name: 'ComImport',
  // props: ['visible'],
  props: {
    visible: {
      default: false
    }
  },
  data () {
    return {
      deptTreeKeys: +new Date(),
      deptTreeData: [],
      expandedKeys: [],
      loading: false,
      fileList: [],
      checkedKeys: [],
      replaceFields: {},
      action: this.$constURL + '/address/importAddress',
      headers: {
        Authentication: this.$store.state.account.token
      },
      data: checkedKeysVlue,
      form: this.$form.createForm(this),
      formData: {}
    }
  },
  methods: {
    reset () {
      this.expandedKeys = this.checkedKeys = []
      this.defaultCheckedKeys = []
      this.loading = false
      this.deptTreeKeys = +new Date()
    },
    onClose () {
      this.reset()
      this.$emit('cancel')
    },
    handleCheck (checkedKeys) {
      if (checkedKeys.checked.length >= 1) {
        if (checkedKeys.checked.length > 1 || checkedKeys.checked.length === 0) {
          this.$notification.warning({message: '系统提示', description: '未选中或者多选项！', duration: 4})
        } else {
          checkedKeysVlue.deptId = checkedKeys.checked[0]
        }
      } else {
        this.$notification.warning({message: '系统提示', description: '不能不选哟！', duration: 4})
      }
    },
    handleChange (info) {
      this.loading = true
      if (checkedKeysVlue.deptId === '') {
        this.$notification.warning({message: '系统提示', description: '至少选中一个！', duration: 4})
        return
      }
      if (info.file.status) {
        const status = info.file.status
        if (status === 'done') {
          if (info.file.status) {
            // 显示效果
            // let uploadFile = info.file.response.file
            // this.fileList.push(uploadFile)
            notification.success({
              message: '系统提示',
              description: '文件上传成功！',
              duration: 4
            })
            this.loading = false
          } else {
            notification.error({
              message: '系统提示',
              description: info.file.response.message,
              duration: 4
            })
          }
          this.$emit('success')
        } else if (status === 'error') {
          this.$message.error(`${info.file.name} file upload failed.`)
        }
      }
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    // 自定义文件上传
    customRequest1 (file) {
      const formData = new FormData()
      formData.append('file', file.file)
      console.log(this.checkedKeys)
      if (this.checkedKeys === undefined) {
        this.$notification.warning({message: '系统提示', description: '请至少选中一个选项！', duration: 4})
        return
      }
      if (this.checkedKeys.length > 1 || this.checkedKeys.length === 0) {
        this.$notification.warning({message: '系统提示', description: '未选中或者多选项！', duration: 4})
      } else {
        axios({
          method: 'post',
          url: this.$constURL + '/address/importAddress',
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authentication': store.state.account.token
            // 'Content-Type': 'appliction/octet-stream'
          },
          data: formData
        }).then((res) => {
          this.$emit('success')
          this.reset()
        }).catch(function (error) {
          console.log(error)
        })
      }
    }
  },
  watch: {
    visible () {
      this.loading = true
      if (this.visible) {
        // /area
        this.$get('/dept').then((r) => {
          // this.replaceFields = {title: 'areaName', key: 'id'}
          this.deptTreeData = r.data.rows.children
          this.loading = false
        })
      }
    }
  }
}
</script>

<style scoped>
.uploadHint {
    color: red!important;
}
</style>
