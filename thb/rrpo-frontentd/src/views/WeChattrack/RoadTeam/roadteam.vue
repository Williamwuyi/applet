<template>
  <div style="width: 100%; min-height: 690px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item label='乡镇街道' :labelCol="{span: 4}"
                           :wrapperCol="{span: 18, offset: 1}">
                <a-tree-select
                  :allowClear="true"
                  tree-data-simple-mode
                  :dropdownStyle="{ maxHeight: '220px', overflow: 'auto' }"
                  :treeData="deptTreeData"
                  :load-data="getDept"
                  @change="onChansgeThree"
                  v-model="character.deptId">
                </a-tree-select>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label='线路选择' :labelCol="{span: 5}"
                           :wrapperCol="{span: 18, offset: 0}">
                <a-select v-model="character.LineId"  @change="onChansgeSelect">
                  <a-select-option v-for="(n,index) in BdLine" :key="index" :value="n.lineId">
                    {{n.lineName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label='状态查询' :labelCol="{span: 8}"
                           :wrapperCol="{span: 14, offset: 1}">
                <a-cascader  change-on-select
                             style="width: 105px;"
                             @change="onChangecascader"
                             :options="options"
                             placeholder="未选择"
                             ref="cascader"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
            </a-col>
          </a-row>
<!--            v-hasPermission="'roadteam:Bdline'"-->
            <a-row>
            <a-col :md="5" :sm="24" v-hasPermission="'roadteam:Bdline'">
              <a-form-item label='可绑定线路' :labelCol="{span: 6}"
                           :wrapperCol="{span: 14, offset: 0}">
                <a-select v-model="setLineMsg" :allowClear="true">
                  <a-select-option v-for="(n,index) in lineData" :key="index" :value="n.dictId">
                    {{n.fieldName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="1" v-hasPermission="'roadteam:Bdline'">
              <a-tag color="#87d068" @click="setLine" style="margin-top: 8px;cursor: pointer">
                绑定线路
              </a-tag>
            </a-col>
            <a-col :md="5" :sm="24" v-hasPermission="'roadteam:Bdline'">
              <a-form-item label='已绑定线路' :labelCol="{span: 8}"
                           :wrapperCol="{span: 14, offset: 0}">
                <a-select v-model="UnsetLineMsg">
                  <a-select-option v-for="(n,index) in UnLine" :key="index" :value="n.deptLineId">
                    {{n.lineName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="1" v-hasPermission="'roadteam:Bdline'">
              <a-tag color="#f50" @click="UnsetLine" style="margin-top: 8px;cursor: pointer">
                解除绑定
              </a-tag>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <!-- 表格区域 -->
    <div>
      <div style="margin-bottom: 5px">
<!--        v-hasPermission="'roadteam:lock'"-->
<!--        <a-button  @click="setStatusOff" style="background-color: rgb(205, 173, 0);color: white;" v-hasPermission="'roadteam:lock'">-->
<!--          锁定-->
<!--        </a-button>-->
<!--        v-hasPermission="'roadteam:unlock'"-->
<!--        <a-button  @click="setStatusON" style="background-color: rgb(205, 133, 63);color: white" v-hasPermission="'roadteam:unlock'">-->
<!--          解锁-->
<!--        </a-button>-->
        <a-button type="primary" @click="handleAdd"  v-if="status">
          添加
        </a-button>
        <a-button class="delet" @click="onDelete"  v-if="status">
          删除
        </a-button>
        <a-button class="exprot" @click="onExprot"  v-if="status">
          导出
        </a-button>
        <a-button style="background-color: #e6a23c;color: white" @click="read"  v-if="status">
          已读
        </a-button>
      </div>
      <!--    状态-->
      <a-alert :type="Sstats" style="margin-bottom: 6px">
        <div slot="message">
          {{currentName}} {{fieldNameMsg}} {{identity}}（线路里程{{revenue}}公里 护路里程<a-input-number v-model="character.km" style="width: 60px;height: 30px;padding: 0;text-align: center;margin: 0 5px" @blur="search" />公里 {{pagination.total}}人）
<!--          <a-tag color="cyan" style="cursor: pointer" @click="search">查询</a-tag>-->
        </div>
      </a-alert>
      <a-table
        style="overflow: auto;width: 100%"
        :data-source="dataSource"
        :rowKey="record => {record.hlId}"
        :columns="columns"
        :loading="loading"
        :scroll="{x:500, y: 450}"
        :pagination="pagination"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <template slot="dept" slot-scope="text, record">
          {{record.shiName}}<span v-if="record.quXianName">/</span>{{record.quXianName}}<span v-if="record.xiangName">/</span>{{record.xiangName}}
        </template>
        <template slot="operation" slot-scope="text, record">
          <a  style="margin-right: 3px;color:#4a9ff5;"     @click="edit(record)" v-if="status">修改</a>
        </template>
      </a-table>
    </div>
    <!-- 修改 -->
    <roadteamEdit
      @success="handleEdit"
      @close="hanleeditclose"
      :NoticeEditVisiable="NoticeEditVisiable"
      ref="oldedit"
    />
    <!-- 添加 -->
    <roadteamadd
      ref="radd"
      @success="handlecomAdd"
      @close="hanlecomclose"
      :roadteamaddVisiable="roadteamaddVisiable"
    />
  </div>
</template>
<script>
import roadteamadd from './roadteamadd'
import roadteamEdit from './roadteamEdit'
import store from '@/store'
export default {
  name: 'roadteam',
  components: {roadteamEdit, roadteamadd},
  data () {
    return {
      deptName: store.state.account.user.deptName,
      currentName: '',
      fieldNameMsg: '',
      revenue: 'XX',
      identity: '',
      deptId: store.state.account.user.deptId,
      lineData: [],
      BdLine: [],
      UnLine: '',
      setLineMsg: '',
      UnsetLineMsg: '',
      character: {
        LineId: null,
        status: '5'
      },
      deptTreeData: [],
      Sstats: 'info',
      data: [],
      loading: false,
      advanced: false,
      status: true,
      selectedRowKeys: [],
      roadteamaddVisiable: false,
      NoticeEditVisiable: false,
      NoticelookVisiable: false,
      sortedInfo: null,
      randomId: '-1',
      // 分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '50', '100'], // 每页中显示的数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      },
      // 表格
      dataSource: [],
      //  政治面貌
      options: [
        {
          value: '5',
          label: '正常'
        },
        {
          value: '1',
          label: '新增'
        },
        {
          value: '2',
          label: '修改'
        },
        {
          value: '3',
          label: '删除'
        }
      ],
      count: 2
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.currentName = this.deptName
    this.getDept()
    this.getBdLine()
    // this.getStatus()
    this.fach()
    this.getLine()
  },
  computed: {
    columns () {
      return [{
        title: '乡镇街道',
        dataIndex: 'dept',
        scopedSlots: { customRender: 'dept' },
        width: '200px'
      }, {
        title: '名字',
        dataIndex: 'name',
        width: '100px'
      }, {
        title: '性别',
        dataIndex: 'sex',
        width: '100px'
      }, {
        title: '年龄',
        dataIndex: 'age',
        width: '100px'
      }, {
        title: '电话号码',
        dataIndex: 'phone',
        width: '120px'
      }, {
        title: '政治面貌',
        dataIndex: 'face',
        width: '100px'
      }, {
        title: '家庭地址',
        dataIndex: 'address',
        width: '300px'
      }, {
        title: '身份证号码',
        dataIndex: 'idCard',
        width: '300px'
      }, {
        title: '责任路段',
        dataIndex: 'luDuan',
        width: '200px'
      }, {
        title: '是否使用微信群',
        dataIndex: 'isWx',
        width: '100px'
      }, {
        title: '管辖区派出所',
        dataIndex: 'police',
        width: '200px'
      }, {
        title: '操作',
        dataIndex: 'operation',
        width: '100px',
        fixed: 'right',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    // 分页获取
    getDept (treeNode) {
      if (treeNode) {
        return new Promise(resolve => {
          setTimeout(() => {
            this.$get('/dept/queryDeptChile', {prentId: treeNode.dataRef.id}).then((r) => {
              let newData = r.data.data
              newData.forEach(t => {
                this.deptTreeData.push(
                  { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
                )
              })
            })
            resolve()
          }, 300)
        })
      } else {
        this.$get('/dept/queryDeptChile', {prentId: this.deptId}).then((r) => {
          let newData = r.data.data
          newData.forEach(t => {
            this.deptTreeData.push(
              { id: t.deptId, pId: t.parentId, value: t.deptId, title: t.deptName }
            )
          })
        })
      }
    },
    // 已绑定
    getBdLine (params = {deptId: this.deptId}) {
      // 如果没选组织机构默认当前登陆人，选了就给对应选择的组织机构
      if (this.character.deptId) {
        params.deptId = this.character.deptId
      }
      this.$get('/wx/dept-line/childGetLine', {...params}).then(res => {
        this.UnLine = res.data.data
      })
    },
    onChansgeSelect (value) {
      this.BdLine.forEach(t => {
        if (t.lineId === value) {
          // 线路名称
          this.fieldNameMsg = t.lineName
          // 公里数
          if (t.revenue) {
            this.revenue = t.revenue
          } else {
            this.$notification.warning({message: '系统提示', description: '当前线路未绑定公里数', duration: 4})
          }
          // 身份
          if (t.nature) {
            this.identity = t.nature
          } else {
            this.$notification.warning({message: '系统提示', description: '当前线路未绑定护路属性', duration: 4})
          }
        }
      })
    },
    // getStatus () {
    //   this.$get('/wx/people/checkLock').then(res => {
    //     switch (res.data.status) {
    //       case 1: this.status = false
    //         this.Sstats = 'error'
    //         break
    //       case 0: this.status = true
    //         this.Sstats = 'success'
    //         break
    //     }
    //     this.messg = res.data.message
    //   })
    // },
    // 锁定
    // setStatusOff () {
    //   this.$get('/wx/people/lock', {status: 1})
    //   this.reload()
    // },
    // // 解锁
    // setStatusON () {
    //   this.$get('/wx/people/lock', {status: 0})
    //   this.reload()
    // },
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10, status: 5}) {
      this.loading = true
      this.$get('/wx/people/list', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 获取路段
    getLine () {
      this.$get('/dict/getListTable', {parentId: '1867989d0aaaf82f79b34070cc77d766', pageSize: 50}).then(res => {
        this.lineData = res.data.data.records
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 查询
    search () {
      // 重置页码
      this.character.pageNum = 1
      this.character.pageSize = 10
      this.fach({
        ...this.character
      })
    },
    // 重置
    reset () {
      this.currentName = this.deptName
      this.identity = this.fieldNameMsg = this.revenue = ''
      // 清空绑定线路
      this.BdLine = []
      // 重置查询参数
      this.character = {
        LineId: null,
        status: '5'
      }
      // 清空表格选择框
      this.selectedRowKeys = []
      this.$refs.cascader.sValue = []
      // 重置列排序规则
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.getBdLine()
      this.fach()
    },
    // 导出
    onExprot () {
      this.$export('/wx/people/listExcel', {...this.character})
    },
    // 分页
    handleTableChange (pagination, filters, sorter) {
      this.sortedInfo = sorter
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fach({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.character,
        ...filters
      })
    },
    // 删除
    onDelete () {
      let arr = []
      this.selectedRowKeys.forEach(t => {
        arr.push(this.dataSource[t].hlId)
      })
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        that.$confirm({
          title: '是否删除，一经删除永远不会恢复?',
          centered: true,
          onOk () {
            that.$delete('/wx/people/delete', {ids: arr}).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.fach(that.character)
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$message.error('选择不能为空')
      }
    },
    // 组织机构查询线路
    getdeptId (deptId) {
      this.$get('/wx/dept-line/childGetLine', {deptId: deptId}).then(res => {
        this.BdLine = res.data.data
        this.getBdLine()
      })
    },
    // 组织机构下拉变化
    onChansgeThree (change) {
      if (change !== undefined) {
        let newData = this.deptTreeData.filter(t => {
          return t.value === change
        })
        this.character.LineId = null
        this.currentName = newData[0].title
        this.getdeptId(change)
      } else {
        this.BdLine = []
        this.currentName = this.deptName
        this.character.deptId = null
        this.character.lineId = null
      }
    },
    // 标识已读
    read () {
      let arr = []
      this.selectedRowKeys.forEach(t => {
        arr.push(this.dataSource[t].hlId)
      })
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0 && this.character.status !== undefined) {
        let that = this
        that.$confirm({
          title: '确定标记已读？',
          centered: true,
          onOk () {
            that.$get('/wx/people/confirm', {ids: arr, status: that.character.status}).then(() => {
              that.$message.success('操做成功')
              that.selectedRowKeys = []
              that.fach(that.character)
            })
          },
          onCancel () {
            that.selectedRowKeys = []
          }
        })
      } else {
        this.$message.error('选择或者状态不能为空')
      }
    },
    // 绑定线路
    setLine () {
      if (!this.character.deptId) {
        this.$message.error('请选择组织机构')
        return
      }
      if (this.setLineMsg !== undefined) {
        this.$post('/wx/dept-line/add', {lineId: this.setLineMsg, deptId: this.character.deptId}).then(res => {
          if (res.data.status === 1) {
            this.setLineMsg = undefined
            // 更新已绑定线路
            this.getBdLine()
            // 如果有选择则更新通过组织机构查询线路
            if (this.character.deptId) {
              this.getdeptId(this.character.deptId)
            }
            this.$notification.success({message: '系统提示', description: res.data.message, duration: 4})
          } else {
            this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
          }
        })
      }
    },
    // 解除绑定
    UnsetLine () {
      if (this.UnsetLineMsg !== undefined) {
        this.$get('/wx/dept-line/delete', {ids: [this.UnsetLineMsg]}).then(res => {
          if (res.data.status === 1) {
            this.UnLine = []
            this.UnsetLineMsg = undefined
            this.getBdLine()
            // 如果有选择则更新通过组织机构查询线路
            if (this.character.deptId) {
              this.getdeptId(this.character.deptId)
            }
            this.$notification.success({message: '系统提示', description: res.data.message, duration: 4})
          } else {
            this.$notification.warning({message: '系统提示', description: res.data.message, duration: 4})
          }
        })
      }
    },
    // 状态
    onChangecascader (key) {
      if (key.length > 0) {
        this.character.status = key[0]
      } else {
        this.character.status = 5
      }
    },
    // 添加功能
    handleAdd () {
      if (this.character.LineId !== null) {
        this.roadteamaddVisiable = true
        this.$refs.radd.newNoticea.line = this.character.LineId
        this.$refs.radd.newNoticea.deptId = this.character.deptId
        this.$refs.radd.getQun(this.character.deptId)
      } else {
        this.$message.error('请先选择增加的线路')
      }
    },
    handlecomAdd () {
      this.roadteamaddVisiable = false
      this.fach(this.character)
      this.$message.success('新增人员成功')
      // this.reload()
    },
    hanlecomclose () {
      this.roadteamaddVisiable = false
    },
    // 修改功能
    edit (key) {
      this.NoticeEditVisiable = true
      this.$refs.oldedit.setFormValues(key)
    },
    handleEdit () {
      this.NoticeEditVisiable = false
      this.fach(this.character)
      this.$message.success('修改成功')
    },
    hanleeditclose () {
      this.NoticeEditVisiable = false
    }
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
  .bre {
    cursor: pointer;
    position: absolute;
    width: 100%;
    height: 50px;
    left: 2px;
    padding-left: 20px;
    padding-top: 15px;
    background-color: rgb(255, 255, 255);
    top: -24px;
    font-size: 14px;
  }
  .delet {
    background-color: #FF4D4F;
    color: white;
    border-radius: 4px!important;
  }
  .delet:hover {
    border-color: #FF4D4F;
    background-color: #FF4D4F;
    color: white;
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.035);
  }
</style>
