<template>
  <a-card :bordered="false" class="card-area">
    <!-- 表格区域 -->
    <div>
      <a-button class="editable-add-btn" @click="handleAdd" v-hasPermission="'notice:add'">
        新建
      </a-button>
      <a-upload
        name="file"
        :multiple="false"
        accept="image/*, .doc, .docx, .xls, .xlsx, .pdf, .rar, .zip, .7z"
        :action="action"
        :headers="headers"
        :data="datar"
        :showUploadList="false"
        @change="handleChange"
      ><a-button @click="uploadClick"> <a-icon type="upload" /> 上传 </a-button></a-upload>
      <!--面包屑-->
      <a-breadcrumb style="cursor: pointer; margin: 20px 0; color: #4a9ff5; font-size: 14px">
        <a-breadcrumb-item><span @click="onClick(0,-1)">当前位置：>>首页</span></a-breadcrumb-item>
        <a-breadcrumb-item v-for="(item, index) in Breadcrumb" :key="item.key"><span @click="onClick(item, index)">{{item.title}}</span></a-breadcrumb-item>
      </a-breadcrumb>
      <hr />
      </div>
<!-- 文件区域   -->
    <div class="floot" ref="sildtree">
      <div class="Share" v-for="(item,index) in arr" :key="index" @click.right="rightClick(item, $event)">
<!--     文件夹显示   -->
        <div v-if="item.mark == 1" class="bgColor" style="position: relative">
<!--     有子集文件夹显示     -->
          <a href="#" v-if="!item.hasChildren" @dblclick="ClickShare(item)">
            <img src="static/img/share.png"/>
            <p :title="`创建时间： ${item.creatTime}`" style="text-align: center;color: #000c17;">{{item.name}}</p>
          </a>
<!--     无子集文件夹显示     -->
          <a href="#" v-else @dblclick="ClickShare(item)">
            <img style="width: 55px;height: 52px;margin-left: 12px;margin-top: 7px;" src="static/img/share_active.png"/>
            <p :title="`创建时间： ${item.creatTime}`" style="margin-top: 9px;text-align: center;color: #000c17;">{{item.name}}</p>
          </a>
        </div>
<!--     文件显示   -->
        <div v-else class="bgColor" style="padding-top: 13px;position: relative">
          <a href="#"  @dblclick="ClickShare(item)">
            <img style="width: 55px;height: 52px;margin-left: 12px;" src="static/img/wps.png"/>
            <p :title="`创建时间: ${item.time} \n文件名: ${item.oldName}`" class="fileName">{{item.oldName}}</p>
          </a>
        </div>
      </div>
      <!--    空文件提示    -->
      <div style="font-size: 16px; text-align: center; margin: 145px auto" v-if="NullText">
        <a-empty />
      </div>
      <!--    右键    -->
      <ul ref="actions" class="deptclass" v-if="isshow" :style="acStyle">
        <li class="flag" @click="getShareClick">打开</li>
        <li class="flag" v-if="downloads" @click="DpwnFile">下载</li>
        <li class="flag" v-if="EditFile" @click="EditShare">重命名</li>
        <li class="flag" @click="handleDliele">删除</li>
      </ul>
    </div>
 <!-- 新建 -->
    <shared-content-add
      ref="collectionForm"
      :visible="SharedisShow"
      :parentId="parentId"
      @cancel="handleCancel"
      @create="handleCreate"
    />
    <!-- 修改 -->
    <shared-content-edit
      ref="edit"
      :visible="ShareEdisShow"
      @cancel="handleEditCancel"
      @create="handleEditCreate"
    />
  </a-card>
</template>
<script>
import SharedContentAdd from './SharedContentAdd'
import SharedContentEdit from './SharedContentEdit'
import {notification} from 'ant-design-vue'
import axios from 'axios'
import store from '../../../store'
export default {
  name: 'Sharedminddle',
  components: {SharedContentAdd, SharedContentEdit},
  data () {
    return {
      Breadcrumb: [],
      constURL: this.$constURL,
      SharedisShow: false,
      ShareEdisShow: false,
      NullText: false,
      // 上传
      // 导入
      action: this.$constURL + 'file/uploadFile',
      headers: {
        Authentication: this.$store.state.account.token
      },
      datar: {
        refType: '4',
        refId: ''
      },
      // 右键的显示隐藏
      isshow: false,
      downloads: false,
      EditFile: true,
      // 当前实例
      current: {},
      parentId: '',
      acStyle: {},
      arr: []
    }
  },
  mounted () {
    this.fach()
    document.addEventListener('click', e => {
      // 鼠标点击任意非打开框的位置，隐藏操作
      if (e.target.className !== 'flag') {
        this.isshow = false
      }
    })
  },
  methods: {
    // 渲染
    fach () {
      this.$get('/publicFile').then(res => {
        console.log(res)
        this.NullText = false
        this.parentId = res.data.rows.id
        this.arr = res.data.rows.children
      })
    },
    // 右键模态框
    rightClick (item, event) {
      // 根据文件类型不同显示不同的右键菜单
      if (item.fileId) {
        // 下载
        this.downloads = true
        // 重命名
        this.EditFile = false
      } else {
        this.downloads = false
        this.EditFile = true
      }
      // 取消鼠标默认事件
      event.preventDefault()
      this.isshow = true
      let x0 = event.clientX
      let y0 = event.clientY
      let elem = this.$refs.sildtree
      let x1 = elem.offsetParent.offsetLeft + elem.offsetLeft
      let y1 = elem.offsetTop
      let left = x0 - x1
      let top = y0 - y1
      this.acStyle = { left: left + 'px', top: top + 'px' }
      this.current = item
    },
    // 标题
    onClick (key, index) {
      // 当点击首页时 数组为空 渲染页面
      if (index === -1) {
        this.fach()
        this.Breadcrumb = []
      } else {
        // 当数组不为首页时，根据id渲染数组，存储当前对象到parentId，然后根据下标截取数组
        this.parentId = key.id
        this.getFach({publicFileId: key.id})
        this.Breadcrumb = this.Breadcrumb.slice(0, index + 1)
      }
    },
    // 文件上传
    handleChange (info) {
      const status = info.file.status
      if (status === 'done') {
        // 文件id上传到后端
        let parame = {}
        parame.fileId = info.file.response.file.fileId
        parame.publicFileId = this.parentId
        this.$get('/publicFile/BindFile', parame).then(res => {
          this.getFach({publicFileId: this.parentId})
        })
        if (info.file.response.status) {
          notification.success({
            message: '系统提示',
            description: '文件上传成功！',
            duration: 4
          })
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
    },
    // 点击上传
    uploadClick () {
      this.datar.refId = this.parentId
    },
    // 右键打开
    getShareClick () {
      this.ClickShare(this.current)
      this.isshow = false
    },
    // 点击文件
    ClickShare (item) {
      if (item.mark === 1) {
        this.Breadcrumb.push({title: item.name, id: item.id})
        this.parentId = item.id
        let newD = {
          publicFileId: item.id
        }
        this.getFach(newD)
      } else {
        this.$message.success('你执行了预览操作')
      }
    },
    // 点击请求
    getFach (newD) {
      this.$get('/publicFile/getByIdList', newD).then(res => {
        console.log(res)
        if (res.data.length > 0) {
          this.NullText = false
          this.arr = res.data
        } else {
          this.arr = res.data
          this.NullText = true
        }
      })
    },
    // 修改文件名
    EditShare () {
      this.$refs.edit.getName(this.current)
      this.isshow = false
      this.ShareEdisShow = true
    },
    handleEditCreate () {
      this.ShareEdisShow = false
      this.getFach({publicFileId: this.parentId})
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    handleEditCancel () {
      this.ShareEdisShow = false
    },
    // 删除文件名
    handleDliele () {
      let that = this
      that.isshow = false
      if (!that.current.fileId) {
        let errorMsg = ''
        if (!that.current.hasChildren) {
          errorMsg = `${that.current.name}`
        } else {
          errorMsg = `${that.current.name},以及${that.current.children.length}个子文件`
        }
        that.$confirm({
          title: `是否删除${errorMsg}，一经删除永远不会恢复?`,
          centered: true,
          onOk () {
            that.$delete('/publicFile/' + that.current.id).then(() => {
              that.$message.success('删除成功')
              that.getFach({publicFileId: that.parentId})
            })
          }
        })
      } else {
        that.$confirm({
          title: `是否${that.current.oldName}删除?`,
          centered: true,
          onOk () {
            that.$post('/file/deleteFile', {fileId: that.current.fileId}).then(res => {
              that.getFach({publicFileId: that.parentId})
            })
          }
        })
      }
    },
    //  新建文件夹
    handleAdd () {
      this.SharedisShow = true
    },
    // close
    handleCancel () {
      this.SharedisShow = false
    },
    // success
    handleCreate () {
      this.SharedisShow = false
      this.getFach({publicFileId: this.parentId})
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 下载
    DpwnFile () {
      axios({
        method: 'post',
        url: this.$constURL + 'file/downloadFile',
        headers: {
          'Authentication': store.state.account.token
        },
        params: {
          fileId: this.current.fileId
        },
        responseType: 'blob'
      }).then(res => {
        const content = res.data
        const blob = new Blob([content])
        if ('download' in document.createElement('a')) {
          const link = document.createElement('a')
          link.download = this.current.oldName
          link.style.display = 'none'
          link.href = URL.createObjectURL(blob)
          document.body.appendChild(link)
          link.click()
          URL.revokeObjectURL(link.href)
          document.body.removeChild(link)
        } else {
          navigator.msSaveBlob(blob, this.current.oldName)
        }
        notification.success({
          message: '系统提示',
          description: '文件下载成功！',
          duration: 4
        })
      }).catch(() => {
        notification.error({
          message: '系统提示',
          description: '文件传输失败！',
          duration: 4
        })
      })
    }
  }
}
</script>

<style scoped>
.floot {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  min-height: 600px;
}
a {
  text-decoration: none;
}
.Share {
  width: 100px;
  height: 120px;
  padding: 8px;
  margin-left: 3px;
  overflow: hidden;
}
.Share a {
  display: block;
  width: 100%;
  height: 100%;
}
.bgColor:hover {
  background-color: #E5F3FF;
}
.Share a img{
  width: 70px;
  height: 70px;
  margin-left: 7px;
}
.deptclass {
  width: 160px;
  height: auto;
  list-style-type: none;
  padding: 5px 0;
  position: absolute;
  z-index: 999999;
  box-shadow: 2px 5px 2px rgba(10, 16, 20, .24);
  background-color: white;
}
.deptclass li {
  margin: 0 5px;
  height: 35px;
  line-height: 35px;
  border-bottom: 1px dashed #ddd;
  cursor: pointer;
}
.deptclass li:hover {
  color: lightseagreen;
}
  .fileName {
    margin-top: 4px;
    text-align: center;
    color: #000c17;
  }
</style>
