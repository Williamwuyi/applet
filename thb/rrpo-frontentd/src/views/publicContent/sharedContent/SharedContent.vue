<template>
  <a-card :bordered="false" class="card-area">
    <!-- 表格区域 -->
    <div>
      <a-button class="editable-add-btn" @click="handleAdd" v-hasPermission="'Scn:add'">
        新建
      </a-button>
      <a-button class="editable-add-btn" @click="handleDelete" v-hasPermission="'Scn:delect'">
        删除
      </a-button>
<!--      :action= "constURL + '/address/importAddress'"-->
      <a-upload
              name="file"
              :multiple="true"
              :file-list="fileList"
              action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
              @change="handleChange"
       ><a-button v-hasPermission="'Scn:upload'"> <a-icon type="upload" /> 上传 </a-button></a-upload>
<!--面包屑-->
      <a-breadcrumb style="cursor: pointer; margin: 20px 0">
        <a-breadcrumb-item><span @click="fach()">当前位置：>>首页</span></a-breadcrumb-item>
        <a-breadcrumb-item v-for="item in Breadcrumb" :key="item.key"><span @click="onClick(item.id)">{{item.title}}</span></a-breadcrumb-item>
      </a-breadcrumb>
<!-- 表格-->
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :pagination="false"
        :row-selection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
      >
        <template slot="name" slot-scope="text, record">
          <div style="cursor: pointer"> <a-icon v-if="record.cms == 1" type="folder" style="color: #FFD955; font-size: 24px;margin-right: 5px"/>
            <a-icon v-if="record.cms == 0" type="snippets" style="color: #0089FF;font-size: 20px;margin-right: 5px"/>
            <span onselectstart="return false" @click="SharedClick(record)">{{text}}</span></div>
        </template>
        <template slot="size" slot-scope="text, record">
            <a-tag v-if="record.size > 500" color="red">{{text}}MB</a-tag>
            <a-tag v-else-if="record.size > 300" color="cyan" >{{text}}MB</a-tag>
            <a-tag v-else color="green">{{text}}MB</a-tag>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon  type="setting" v-if="record.cms !== 0" theme="twoTone"  twoToneColor="#4a9ff5" style="margin-right: 5px"  @click="edit(record)" v-hasPermission="'Scn:edit'" title="修改"></a-icon>
          <a-icon type="vertical-align-bottom" v-hasPermission="'Scn:download'" @click="download(record)" style="color: #4a9ff5" title="下载"/>
        </template>
      </a-table>
    </div>
<!-- 新建 -->
    <shared-content-add
      ref="collectionForm"
      :visible="SharedisShow"
      @cancel="handleCancel"
      @create="handleCreate"
    />
  </a-card>
</template>
<script>
import SharedContentAdd from './SharedContentAdd'
var arr = [{
  title: '最新文件',
  createTime: '----',
  size: '----',
  createUser: '----',
  cms: 1
}, {
  title: '铁护组文件',
  createTime: '----',
  size: '----',
  createUser: '----',
  cms: 1
}]
var arr1 = [{
  title: 'XX文件',
  createTime: '2020-2-9',
  size: 67,
  createUser: 'wherePI',
  cms: 0
},
{
  title: 'XX文件',
  createTime: '2020-2-9',
  size: 367,
  createUser: 'wherePI',
  cms: 0
},
{
  title: 'XX文件',
  createTime: '2020-2-9',
  size: 567,
  createUser: 'wherePI',
  cms: 0
}]
export default {
  name: 'SharedContent',
  components: { SharedContentAdd },
  data () {
    return {
      data: [],
      loading: false,
      sortedInfo: null,
      // 新建
      SharedisShow: false,
      // 表格
      dataSource: [],
      // 表格选中
      selectedRowKeys: [],
      fileList: [],
      count: 2,
      constURL: this.$constURL,
      Breadcrumb: []
    }
  },
  mounted () {
    this.fach()
    this.dataSource = arr
  },
  computed: {
    columns () {
      let { sortedInfo } = this
      sortedInfo = sortedInfo || {}
      return [{
        title: '名称',
        dataIndex: 'title',
        width: '30%',
        scopedSlots: { customRender: 'name' }
      }, {
        title: '发布时间',
        dataIndex: 'createTime',
        sorter: true,
        sortOrder: sortedInfo.columnKey === 'createTime' && sortedInfo.order
      }, {
        title: '大小',
        dataIndex: 'size',
        scopedSlots: { customRender: 'size' }
      }, {
        title: '发布人',
        dataIndex: 'createUser'
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    // 渲染
    fach () {
      console.log('111111')
    },
    // 跳转上级
    onClick (key) {
      console.log(key)
    },
    // 表格前面选中
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 新建
    handleAdd () {
      this.SharedisShow = true
    },
    // 上传文件夹
    handleUpload () {},
    // 上传文件
    handleChange (info) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (info.file.status === 'done') {
        this.$message.success(`${info.file.name} file uploaded successfully`)
      } else if (info.file.status === 'error') {
        this.$message.error(`${info.file.name} file upload failed.`)
      }
    },
    // 删除
    handleDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          // that.$delete('dept/' + that.selectedRowKeys.join(',')).then(() => {
          //   that.$message.success('删除成功')
          //   that.selectedRowKeys = []
          //   that.fetch()
          // })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    // close
    handleCancel () {
      this.SharedisShow = false
    },
    // success
    handleCreate (key) {
      this.SharedisShow = false
      this.dataSource.push({
        title: key,
        cms: 1,
        createTime: '----',
        size: '----',
        createUser: '----'
      })
      this.$message.success('新建成功')
    },
    // name点击
    SharedClick (record) {
      this.selectedRowKeys = []
      if (record.cms === 1) {
        this.Breadcrumb.push({title: record.title, id: record.id})
        this.dataSource = arr1
      } else if (record.cms === 0) {
        this.$message.success('你执行了预览操作')
      }
    },
    // 修改
    edit (record) {},
    //  下载
    download (record) {}
  }
}
</script>
<style lang="less" scoped>
  .editable-cell {
    position: relative;
  }

  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }

  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }

  .editable-cell-icon,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 20px;
    cursor: pointer;
  }

  .editable-cell-icon {
    line-height: 18px;
    display: none;
  }

  .editable-cell-icon-check {
    line-height: 28px;
  }

  .editable-cell:hover .editable-cell-icon {
    display: inline-block;
  }

  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }

  .editable-add-btn {
    margin-bottom: 8px;
  }
</style>
