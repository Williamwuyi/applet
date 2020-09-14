<template>
  <a-card :bordered="false" class="card-area">
    <!-- 表格区域 -->
    <div>
    <div class="container">
      <ul class="leftul">
        <li> <a-icon  type="download" style="margin-right: 10px"></a-icon> 收件箱</li>
        <li> <a-icon  type="upload" style="margin-right: 10px"></a-icon>发件箱</li>
        <li> <a-icon  type="delete" style="margin-right: 10px"></a-icon>回收箱</li>
        <li> <a-icon  type="select" style="margin-right: 10px"></a-icon>接发信息汇总</li>
        <li> <a-icon  type="question-circle" style="margin-right: 10px"></a-icon>未浏览信息汇总</li>
      </ul>
<!--      <share-tree-->
<!--        @reloadStaffTable="findDeptStaff"-->
<!--        @clearSelectrows="cleardRowKeys"-->
<!--        @getMannerableDept="getMannerableDept"-->
<!--      />-->
    </div>
    <div class="sildtable">
    <a-button class="editable-add-btn" @click="handleAdd" v-hasPermission="'share:add'">
      新建
    </a-button>
      <a-button class="editable-add-btn" @click="handleDel" v-hasPermission="'share:delete'">
        删除
      </a-button>
     <a-dropdown>
          <a-menu slot="overlay">
            <a-menu-item key="export-data" @click="addFolder">新建文件夹</a-menu-item>
          </a-menu>
          <a-button>
            更多操作<a-icon type="down" />
          </a-button>
        </a-dropdown>
    <a-table :data-source="dataSource" :columns="columns" :pagination="false">
      <template slot="operation" slot-scope="text, record">
        <a-popconfirm
          cancelText="取消"
          okText="确定"
          v-if="dataSource.length"
          title="是否删除，一经删除永远不会恢复?"
          @confirm="() => onDelete(record.key)"
        >
        <a-icon  type="close" style="color:#E21918" title="删除"></a-icon>
        </a-popconfirm>
<!--        <a-icon  type="download" style="margin-left:8px;color:#1AA034" @click="open(record)" title="下载"></a-icon>-->
        <a-icon  type="edit" style="margin-left:8px;color:#158BD2" @click="edit(record)" title="修改"></a-icon>
      </template>
    </a-table>
  </div>
<!-- 上传 -->
<shareadd
:NoticeaddVisiable="NoticeaddVisiable"
@close="ShareClose"
@success="handleShareAdd"
/>
<!-- 修改 -->
<share-edit
:NoticeEditVisiable="NoticeEditVisiable"
@close="ShareeditClose"
@success="handleEdit"
ref="oldedit"
/>
<!-- 新增文件夹 -->
<!-- <folder /> -->
    </div>
    </a-card>
</template>
<script>
import Shareadd from './Shareadd'
import ShareEdit from './ShareEdit'
import ShareTree from './shareTree'
// import Folder from './Folder'
export default {
  name: 'Share',
  components: {Shareadd, ShareEdit, ShareTree},
  data () {
    return {
      NoticeaddVisiable: false,
      NoticeEditVisiable: false,
      dataSource: [],
      count: 2,
      columns: [
        {
          title: '标题',
          dataIndex: 'Aicon',
          scopedSlots: { customRender: 'Aicon' }
        },
        {
          title: '发布人',
          dataIndex: 'sharefile',
          width: '20%'
        },
        {
          title: '发布时间',
          dataIndex: 'name'
        },
        {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  mounted () {
    this.fach()
  },
  methods: {
    // 渲染
    fach () {
      // this.$get('share').then(res => {
      //   this.dataSource = res.data
      // })
    },
    // 表格
    // 删除
    onDelete (key) {
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      // this.$delete('shate' + this.key).then(() => {
      //   this.$message.success('删除成功')
      //   this.fach()
      // })
    },
    // 添加功能
    handleAdd () {
      this.NoticeaddVisiable = true
    },
    handleShareAdd () {
      this.NoticeaddVisiable = false
      this.fach()
      this.$message.success('上传成功')
    },
    ShareClose () {
      this.NoticeaddVisiable = false
    },
    // 下载
    open (record) {
      if (record.url) {
        // 判断是否是本部门或者管理员
        // if (this.$db.get('ROLES')[0] === '管理员' || this.$db.get('ROLES')[0] === record.deptName) {
        //   // window.open(record.url)
        //   this.$get('/file/downLoad', record.key).then(res => {
        //     console.log(res)
        //   })
        // } else {
        //   this.$message.error('您没有相关权限')
        // }
      } else {
        this.$message.error('请选择具体文件')
      }
    },
    // 新建文件夹
    addFolder () {
      var a = prompt('输入你要添加的文件名')
      // eslint-disable-next-line standard/object-curly-even-spacing
      this.dataSource.push({ key: '5', folder: a})
    },
    // 修改
    handleEdit () {
      this.NoticeEditVisiable = false
      this.fach()
      this.$message.success('修改成功')
    },
    edit (record) {
      this.NoticeEditVisiable = true
      this.$refs.oldedit.setFormValues(record)
    },
    ShareeditClose () {
      this.NoticeEditVisiable = false
    },
    // 根据点击的部门节点，展示对应部门下的员工
    findDeptStaff (res) {
      this.deptStaff = res
      // this.fetch()
    },
    // 清空selcetrowkeys.
    cleardRowKeys () {
      // this.selectedRowKeys = [];
    },
    // 新增人员时的可选择部门
    getMannerableDept (data) {
      this.mannerableDepts = data.newTreeNodes
      this.deptOptions = data.allDepts
    }
  }
}
</script>
<style lang="less" scoped>
  @import "../../../../static/less/Common";
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
  margin: 8px;
}
  /*左边树*/
  div .container {
    width: 200px;
    float: left;
    position: relative;
    /*border:#f0f2f5 solid 1px;*/
    background-color: #FAFAFA;
  }
 div .sildtable {
    float: left;
    margin-left: 20px;
    width: calc(100% - 400px);
    height: auto;
  }
 .leftul li{
   list-style: none;
   line-height: 50px;
   font-size: 15px;
   margin-left: -35px;
   border-bottom: 1px solid #e8e8e8
   }
</style>
