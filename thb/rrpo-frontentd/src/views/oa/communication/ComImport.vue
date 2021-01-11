<template>
  <div>
    <a-modal
      :visible="visible"
      title='文件导入'
      okText='确定'
      :footer="null"
      :destroyOnClose="true"
      @cancel="() => {onClose() }"
    >
      <a-form layout='vertical' :form="form">
        <div>
        <a-form-item label='单位机构:' style="margin-bottom: 2rem;" >
          <a-tree-select
            placeholde="请选择单位机构"
            @change="handleCheck"
            @expand="handleExpand"
            :expandedKeys="expandedKeys"
            :replaceFields="replaceFields"
            :showSearch="false"
            :dropdown-style="{ maxHeight: '400px',width: '200px', overflow: 'auto' }"
            :load-data="onLoadData"
            :treeData="deptTreeData">
          </a-tree-select>
        </a-form-item>
        </div>
        <a-form-item>
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
          <a-button  @click="downloadM" style="margin-left: 20px"> <a-icon type="download"/> 下载模板 </a-button>
        </a-form-item>
        <a-form-item>
          <span class="uploadHint">注意！只能上传xls/xlsx格式的文件; 或者下载模板格式进行上传</span>
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
      this.$emit('cancel')
    },
    handleCheck (checkedKeys) {
      checkedKeysVlue.deptId = checkedKeys
    },
    handleExpand (expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    handleChange (info) {
      if (checkedKeysVlue.deptId === '') {
        notification.error({message: '系统提示', description: '请选择所属机构！', duration: 2})
        return
      }
      this.loading = true
      if (info.file.status) {
        const status = info.file.status
        if (status === 'done') {
          if (info.file.status) {
            // 显示效果
            // let uploadFile = info.file.response.file
            // this.fileList.push(uploadFile)
            notification.success({
              message: '系统提示',
              description: info.file.response.message,
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
    onLoadData (treeNode) {
      return new Promise(resolve => {
        if (treeNode.dataRef.children) {
          resolve()
          return
        }
        setTimeout(() => {
          this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.deptId}).then((r) => {
            treeNode.dataRef.children = r.data.data
            this.deptTreeData = [...this.deptTreeData]
            resolve()
          })
        }, 500)
      })
    },
    // 下载模板
    downloadM () {
      console.log('----')
      this.$export('address/downloadFile')
      console.log('++++')
    },
    // 自定义文件上传
    customRequest1 (file) {
      const formData = new FormData()
      formData.append('file', file.file)
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
  },
  watch: {
    visible () {
      this.loading = true
      if (this.visible) {
        // /area
        this.$get('/dept/queryDeptChile').then((r) => {
          this.replaceFields = { key: 'deptId', title: 'deptName', value: 'deptId' }
          this.deptTreeData = r.data.data
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
