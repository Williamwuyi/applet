<template>
  <div style="width: 100%;">
    <!--左侧-->
    <div class="slideTable">
      <div :class="advanced ? 'search' : null" style="height:60px;">
        <a-form layout="horizontal">
          <div :class="advanced ? null: 'fold'" style="float:left;width:150px;margin-top:5px;">
            <a-row>
              <a-col>
                <a-form-item
                  :wrapperCol="{span: 23, offset: 1}">
                  <a-input v-model="queryParamsLeft.tableName" placeholder="请输入字典名称"/>
                </a-form-item>
              </a-col>
            </a-row>
          </div>
          <span style="float:left;margin-top:8px;margin-left:10px;">
            <a-button type="primary" @click="searchLeft">查询</a-button>
          </span>
        </a-form>
      </div>
      <div class="operator" style="height:32px;clear:both;margin-bottom:15px;">
        <a-button type="primary" style="float:left;margin-left:7px;margin-right:13px;" @click="basicAddLeft">添加</a-button>
        <a-button class="orange_btn" @click="basicEditLeft">修改</a-button>
        <!--<a-button class="red_btn" @click="basicDeleteLeft">删除</a-button>-->
      </div>
     <!--左侧表格-->
      <a-table
        ref="slideTable"
        :columns="columnsLeft"
        :dataSource="dataSourceLeft"
        :pagination="false"
        :loading="loadingLeft"
        :rowSelection="{selectedRowKeys: selectedRowKeysLeft, onChange: onSelectChangeLeft}"
        :scroll="{ x: 300, y: 430 }"
      >
        <a slot="name" slot-scope="text,record,index" style="color:#6290ff" @click="look(record,index)">{{ text }}</a>
      </a-table>
    </div>
    <!--右侧-->
    <div class="dataTable">
      <div :class="advanced ? 'search' : null" style="height:60px;">
        <a-form layout="horizontal">
          <div :class="advanced ? null: 'fold'" style="margin-top:5px;">
            <a-row>
              <a-col :md="24" :sm="24">
                <a-form-item
                  label="字典项名称"
                  :labelCol="{span: 3}"
                  :wrapperCol="{span: 18, offset: 1}">
                  <a-input v-model="queryParamsRight.fieldName" placeholder="请输入字典项名称"/>
                </a-form-item>
              </a-col>
             </a-row >
          </div>
          <span style="float:right;margin-top:8px;">
            <a-button type="primary" @click="searchRight">查询</a-button>
            <a-button style="margin-left:8px" @click="resetRight()">重置</a-button>
          </span>
        </a-form>
      </div>
      <div class="operator" style="clear:both;margin-bottom:15px;">
        <a-button type="primary" @click="basicAddRight">添加</a-button>
        <a-button class="red_btn" @click="basicDeleteRight">删除</a-button>
      </div>
      <!--右侧表格-->
      <a-table
          ref="dataTable"
         :columns="columnsRight"
         :dataSource="dataSourceRight"
         :pagination="pagination"
         :loading="loadingRight"
         :rowSelection="{selectedRowKeys: selectedRowKeysRight, onChange: onSelectChangeRight}"
          @change="handleTableChange"
          :scroll="{ x: 800 }"
      >
        <template slot="status" slot-scope="text, record">
          <a-tag v-if="record.ifDisable === 0" color="#ff4d4f">已禁用</a-tag>
          <a-tag v-else-if="record.ifDisable === 1" color="#67c799" >未禁用</a-tag>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a style="color: #ff4d4f" title="禁用字典项" @click="disableRight(record)">禁用</a>
          <a style="color: #67c799" title="解禁字典项" @click="unDisableRight(record)">解禁</a>
          <a style="color: #4a9ff5" title="修改字典项" @click="basicEditRight(record)">修改</a>
        </template>
      </a-table>
    </div>
    <!--左侧添加字典-->
    <basic-add-left
      :basicAddLeftVisible="AddLeftVisible"
      @close="AddLeftClose"
      @success="handleBasicAddLeftSuccess"
    >
    </basic-add-left>
    <!--右侧添加字典项-->
    <basic-add-right
      ref="rightId"
      :basicAddRightVisible="AddRightVisible"
      @close="AddRightClose"
      @success="handleBasicAddRightSuccess"
    >
    </basic-add-right>
    <!--左侧修改字典项-->
    <basic-edit-left
      ref="basicEditLeft"
      :basicEditLeftVisible="EditLeftVisible"
      @close="EditLeftClose"
      @success="handleBasicEditLeftSuccess"
    >
    </basic-edit-left>
    <!--右侧修改字典项-->
    <basic-edit-right
      ref="basicEditRight"
      :basicEditRightVisible="EditRightVisible"
      @close="EditRightClose"
      @success="handleBasicEditRightSuccess"
    >
    </basic-edit-right>
  </div>
</template>
<script>
import BasicAddLeft from './BasicAddLeft'
import BasicAddRight from './BasicAddRight'
import BasicEditLeft from './BasicEditLeft'
import BasicEditRight from './BasicEditRight'
export default {
  name: 'basic',
  components: {BasicAddLeft, BasicAddRight, BasicEditLeft, BasicEditRight},
  data () {
    return {
      rightINum: null,
      loadingLeft: false,
      loadingRight: false,
      advanced: false,
      queryParamsLeft: {
        parentId: '0'
      },
      queryParamsRight: {},
      sortedInfo: null,
      AddLeftVisible: false,
      AddRightVisible: false,
      EditLeftVisible: false,
      EditRightVisible: false,
      basicAddLeftVisible: false,
      basicAddRightVisible: false,
      dataSourceLeft: [],
      dataSourceRight: [],
      selectedRowKeysLeft: [],
      selectedRowKeysRight: [],
      character: {},
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  computed: {
    columnsLeft () {
      return [{
        title: '字典名称',
        dataIndex: 'tableName',
        scopedSlots: { customRender: 'name' },
        align: 'center'
      }]
    },
    columnsRight () {
      return [{
        title: '字典项名称',
        dataIndex: 'fieldName'
      }, {
        title: '状态',
        dataIndex: 'ifDisable',
        scopedSlots: { customRender: 'status' },
        width: 100
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: { customRender: 'operation' },
        fixed: 'right',
        width: 150
      }]
    }
  },
  mounted () {
    this.fetchLeft({...this.queryParamsLeft})
    // this.fetchRight()
  },
  methods: {
    // 左侧添加
    basicAddLeft () {
      this.AddLeftVisible = true
    },
    AddLeftClose () {
      this.AddLeftVisible = false
    },
    handleBasicAddLeftSuccess () {
      this.AddLeftVisible = false
      this.$notification.success({message: '系统提示', description: '添加字典成功！', duration: 4})
      this.fetchLeft()
    },
    // 左侧修改
    basicEditLeft () {
      if (!this.selectedRowKeysLeft.length) {
        this.$message.warning('请选择需要修改的记录')
        // return
      } else if (this.selectedRowKeysLeft.length > 1) {
        this.$message.warning('最多只能修改一项')
      } else {
        // 获取到勾选的这个选项的值
        // console.log(this.dataSourceLeft[this.selectedRowKeysLeft])
        // console.log(1)
        let record = this.dataSourceLeft[this.selectedRowKeysLeft]
        this.$refs.basicEditLeft.setFormValues(record)
        this.EditLeftVisible = true
      }
    },
    EditLeftClose () {
      this.EditLeftVisible = false
    },
    handleBasicEditLeftSuccess () {
      this.EditLeftVisible = false
      this.$notification.success({message: '系统提示', description: '修改字典成功！', duration: 4})
      this.fetchLeft()
    },
    // 左侧删除
    basicDeleteLeft () {
      if (!this.selectedRowKeysLeft.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let dictIds = []
          for (let key of that.selectedRowKeysLeft) {
            dictIds.push(that.dataSourceLeft[key].dictId)
          }
          // console.log(dictIds)
          that.$post('/dict/deleteTable', {dictIds: dictIds}).then((res) => {
            // console.log('!!', res)
            if (res.data.status === 1) {
              that.$message.success('删除成功')
              that.selectedRowKeysLeft = []
              that.dataSourceLeft = []
              that.fetchLeft()
            } else {
              that.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
            }
          })
        },
        onCancel () {
          that.selectedRowKeysLeft = []
        }
      })
    },
    // 右侧添加
    basicAddRight () {
      this.$refs.rightId.searchId(this.rightINum)
      this.AddRightVisible = true
    },
    AddRightClose () {
      this.AddRightVisible = false
    },
    handleBasicAddRightSuccess () {
      this.AddRightVisible = false
      this.$notification.success({message: '系统提示', description: '添加字典项成功！', duration: 4})
      this.fetchRight(this.queryParamsRight)
    },
    // 右侧修改
    basicEditRight (record) {
      // console.log(record)
      this.$refs.basicEditRight.setFormValues(record)
      this.EditRightVisible = true
    },
    EditRightClose () {
      this.EditRightVisible = false
    },
    handleBasicEditRightSuccess () {
      this.EditRightVisible = false
      this.$notification.success({message: '系统提示', description: '修改字典项成功！', duration: 4})
      this.fetchRight(this.queryParamsRight)
    },
    // 禁用字典项
    disableRight (record) {
      if (record.ifDisable !== 0) {
        let that = this
        that.$confirm({
          title: '确定禁用《' + record.fieldName + '》?',
          centered: true,
          onOk () {
            that.$get('/dict/disable', {dictId: record.dictId}).then(res => {
              that.fetchRight(that.queryParamsRight)
              that.$message.success('禁用成功')
            }).catch(() => {
              that.fetchRight(that.queryParamsRight)
              that.$message.error('禁用失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于禁用状态了')
      }
    },
    // 解禁字典项
    unDisableRight (record) {
      if (record.ifDisable !== 1) {
        let that = this
        that.$confirm({
          title: '确定解禁《' + record.fieldName + '》?',
          centered: true,
          onOk () {
            that.$get('/dict/noDisable', {dictId: record.dictId}).then(res => {
              that.fetchRight(that.queryParamsRight)
              that.$message.success('解禁成功')
            }).catch(() => {
              that.fetchRight(that.queryParamsRight)
              that.$message.success('解禁失败')
            })
          },
          onCancel () {
          }
        })
      } else {
        this.$message.error('已经处于解禁状态了')
      }
    },
    // 右侧删除
    basicDeleteRight () {
      if (!this.selectedRowKeysRight.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let dictIds = []
          for (let key of that.selectedRowKeysRight) {
            dictIds.push(that.dataSourceRight[key].dictId)
          }
          // console.log(dictIds)
          that.$post('/dict/deleteTable', {dictIds: dictIds}).then((res) => {
            if (res.data.status === 1) {
              that.$message.success('删除成功')
              that.selectedRowKeysRight = []
              that.dataSourceRight = []
              that.fetchRight(that.queryParamsRight)
            } else {
              that.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
            }
          })
        },
        onCancel () {
          that.selectedRowKeysRight = []
        }
      })
    },
    // 搜索
    searchLeft () {
      this.fetchLeft({
        ...this.queryParamsLeft
      })
    },
    searchRight () {
      // console.log(this.queryParamsRight)
      this.fetchRight({
        ...this.queryParamsRight
      })
    },
    // 重置
    resetRight () {
      // 取消选中
      this.selectedRowKeysRight = []
      // 重置列排序规则
      this.sortedInfo = null
      // 重置分页
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.queryParamsRight.fieldName = ''
      this.character.parentId = this.queryParamsRight.parentId
      // 重置查询参数
      // this.queryParamsRight = {}
      this.fetchRight(this.character)
    },
    handleTableChange (pagination, filters, sorter) {
      // this.paginationInfo = pagination
      // this.fetchRight({
      //   ...this.queryParamsRight
      // })
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.character.parentId = this.queryParamsRight.parentId
      this.fetchRight(this.character)
    },
    // 请求右侧数据
    look (e, index) {
      // console.log(e.dictId)
      this.rightINum = e.dictId
      this.loadingRight = true
      this.queryParamsRight.parentId = e.dictId
      this.$get('/dict/getListTableTwo', {
        parentId: e.dictId
      }).then((res) => {
        this.loadingRight = false
        let data = res.data.data
        // console.log('+++', data)
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.dataSourceRight = data.records
        this.pagination = pagination
      })
    },
    // 多选框
    onSelectChangeLeft (selectedRowKeys) {
      this.selectedRowKeysLeft = selectedRowKeys
    },
    onSelectChangeRight (selectedRowKeys) {
      this.selectedRowKeysRight = selectedRowKeys
    },
    // 请求左侧数据
    fetchLeft () {
      this.loadingLeft = true
      this.$get('/dict/getListTableTwo', {
        pageSize: 50,
        pageNum: 1,
        ...this.queryParamsLeft
      }).then((res) => {
        this.dataSourceLeft = res.data.data.records
        // console.log(this.dataSourceLeft[0].dictId)
        // console.log(res)
        this.look({dictId: this.dataSourceLeft[0].dictId}, 0)
        this.loadingLeft = false
      })
    },
    fetchRight (params = {pageNum: 1, pageSize: 10}) {
      this.loadingRight = true
      this.$get('dict/getListTableTwo', {
        ...params
      }).then((res) => {
        this.loadingRight = false
        let data = res.data.data
        // console.log('+++', data)
        const pagination = { ...this.pagination }
        pagination.total = data.total
        this.dataSourceRight = data.records
        this.pagination = pagination
      })
    }
  }
}
</script>
<style lang="less" scoped>
@import "BasicInfo";
</style>
