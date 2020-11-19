<template>
  <div style="width: 100%; min-height: 690px">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="单位"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-select @change="citySChange" v-model="unitG" ref="ctyS">
                  <a-select-option v-for="(n) in cityS" :key="n.dept.deptName" :value="n.id" >
                    {{n.dept.deptName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="区县单位 :"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select @change="cityQChange" v-model="unitQ" ref="ctyQ">
                  <a-select-option v-for="(n) in cityQ" :key="n.dept.deptName" :value="n.id" >
                    {{n.dept.deptName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <a-form-item
                label="工务段"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 15, offset: 1}">
                <a-select v-model="gwd">
                  <a-select-option v-for="(n,index) in trackData" :key="index" :value="n.fieldName">
                    {{n.fieldName}}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24" >
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
        </span>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    <!-- 表格区域 -->
    <div>
      <div style="margin-bottom: 5px">
        <a-button type="primary" @click="handleAdd" v-hasPermission="'notice:add'">
          添加
        </a-button>
        <a-button class="delet" @click="onDelete" v-hasPermission="'notice:delete'">
          删除
        </a-button>
        <a-button  @click="onExport" v-hasPermission="'notice:delete'">
          导出
        </a-button>
      </div>
      <a-table
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :scroll="{x:500, y: 450}"
        :pagination="pagination"
        :rowKey="(record)=> record.id"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
      >
        <span slot="index" slot-scope="index">{{index}}</span>
        <a slot="bm" slot-scope="text,record, index" style="color:#6290FF" @click="look(record)">{{ text }}</a>
        <template slot="operation" slot-scope="text, record">
          <a  style="margin-right: 3px;color:#4a9ff5;"     @click="edit(record)" v-hasPermission="'notice:edit'" >修改</a>
        </template>
        <template slot="tpDept" slot-scope="text, record">
          <img v-if="record.tpFile.length > 0" @click="ClickImg(record)" style="width: 100%;height: 20px;display: table-cell; cursor: pointer" :src="record.tpFile[0].accessUrl" :alt="record.tpFile[0].oldName">
        </template>
      </a-table>
    </div>
    <!-- 修改 -->
    <notice-edit
      @success="handleEdit"
      @close="hanleeditclose"
      :NoticeEditVisiable="NoticeEditVisiable"
      ref="oldedit"
    />
    <!-- 添加 -->
    <noticeadd
      @success="handlecomAdd"
      @close="hanlecomclose"
      :NoticeaddVisiable="NoticeaddVisiable"
    />
<!--  查看  -->
    <look-model
      ref="lookMo"
    :visible="visible"
    @cancel="lookClose"
    />
<!--   查看照片 -->
    <img-model
      ref="lookImg"
    :imgVisible="imgVisible"
    @cancel="imgClose"
    />
  </div>
</template>
<script>
import lookModel from './lookModel'
import Noticeadd from './Briggesadd'
import NoticeEdit from './BriggesEdit'
import imgModel from './imgModel'
export default {
  name: 'Bridges',
  components: {NoticeEdit, Noticeadd, lookModel, imgModel},
  data () {
    return {
      character: {},
      data: [],
      loading: false,
      advanced: false,
      trackData: [], // 工务段
      unitG: null,
      unitQ: null,
      cityS: [],
      cityQ: [],
      gwd: null,
      selectedRowKeys: [],
      imgVisible: false,
      NoticeaddVisiable: false,
      NoticeEditVisiable: false,
      visible: false,
      sortedInfo: null,
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
      count: 2
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.fach()
    this.$get('/accident/dept/list').then(res => {
      this.cityS = res.data.data
    })
    // 公务的
    this.$get('/dict/getListTable', {parentId: '521c7e776dbad0ee1fe118e9aee63121', pageSize: 50}).then(res => {
      this.trackData = res.data.data.records
    })
  },
  computed: {
    columns () {
      return [
        {
          title: '序号',
          align: 'center',
          width: 80,
          customRender: (text, record, index) => `${index + 1}`
        }, {
          title: '编号',
          dataIndex: 'bm',
          width: 80,
          ellipsis: true,
          scopedSlots: { customRender: 'bm' }
        }, {
          title: '地州市',
          width: 100,
          dataIndex: 'dzs'
        }, {
          title: '县市区',
          width: 100,
          dataIndex: 'xsq'
        }, {
          title: '乡镇街道',
          width: 120,
          dataIndex: 'xz'
        }, {
          title: '座落村组',
          width: 100,
          ellipsis: true,
          dataIndex: 'zl'
        }, {
          title: '公安处',
          width: 130,
          ellipsis: true,
          dataIndex: 'ga'
        }, {
          title: '派出所',
          width: 100,
          ellipsis: true,
          dataIndex: 'pcs'
        }, {
          title: '警务区',
          width: 100,
          ellipsis: true,
          dataIndex: 'jwq'
        }, {
          title: '工务段',
          width: 120,
          dataIndex: 'gwd'
        }, {
          title: '公里数',
          width: 200,
          ellipsis: true,
          dataIndex: 'gls'
        }, {
          title: '修建方位',
          width: 100,
          ellipsis: true,
          dataIndex: 'fw'
        }, {
          title: '修建长度',
          width: 200,
          ellipsis: true,
          dataIndex: 'cd'
        }, {
          title: '交通事故情况',
          width: 300,
          ellipsis: true,
          dataIndex: 'jtsgqk'
        }, {
          title: '修建原因及周边社情',
          width: 200,
          ellipsis: true,
          dataIndex: 'yy'
        }, {
          title: '路地已开展工作',
          width: 300,
          ellipsis: true,
          dataIndex: 'kzqk'
        }, {
          title: '现场图',
          width: 100,
          ellipsis: true,
          dataIndex: 'tpDept',
          scopedSlots: { customRender: 'tpDept' }
        }, {
          title: '操作',
          fixed: 'right',
          width: '100px',
          dataIndex: 'operation',
          scopedSlots: { customRender: 'operation' }
        }]
    }
  },
  methods: {
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10}) {
      this.loading = true
      this.$get('/accident/ql/list', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 查询
    search () {
      if (this.unitG !== null) {
        this.character.policeName = this.$refs.ctyS.$el.innerText
      }
      if (this.unitQ !== null) {
        this.character.deptName = this.$refs.ctyQ.$el.innerText
      }
      if (this.gwd !== null) {
        this.character.gwdName = this.gwd
      }
      this.fach({
        ...this.character
      })
    },
    // 重置
    reset () {
      // 重置查询参数
      this.character = {}
      // 清空表格选择框
      this.selectedRowKeys = []
      // 清空显示
      this.unitQ = null
      this.unitG = null
      this.gwd = null
      // 重置列排序规则
      this.sortedInfo = null
      this.pagination.current = 1
      this.pagination.pageSize = 10
      this.fach()
    },
    // 选择地级市是，查找县级市
    citySChange (key) {
      this.unitQ = null
      this.character.deptName = null
      this.$get('/accident/dept/list', {prentId: key}).then(res => {
        this.cityQ = res.data.data
      })
      this.cityCsId = key
    },
    cityQChange (key) {
      this.cityQsId = key
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
      // 先通过key之传递到数据库，表明删除这个，然后再进行渲染
      if (this.selectedRowKeys.length > 0) {
        let that = this
        that.$confirm({
          title: '是否删除，一经删除永远不会恢复?',
          centered: true,
          onOk () {
            that.$delete('/accident/ql/delete', {idList: that.selectedRowKeys}).then(res => {
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
    // 导出
    onExport () {
      let params = {
        policeName: this.unitG,
        deptName: this.unitQ,
        gwdName: this.gwd
      }
      this.$export('/accident/ql/excel', params)
    },
    // 添加功能
    handleAdd () {
      this.NoticeaddVisiable = true
    },
    handlecomAdd () {
      this.NoticeaddVisiable = false
      this.fach(this.character)
      this.$message.success('新增公告成功')
      // this.$store.commit('file/initAppendixList')
    },
    hanlecomclose () {
      this.NoticeaddVisiable = false
    },
    // 查看
    look (record) {
      this.visible = true
      this.$refs.lookMo.setLook(record)
    },
    lookClose () {
      this.visible = false
    },
    // 查看照片
    ClickImg (record) {
      this.imgVisible = true
      this.$refs.lookImg.getImgUrl(record.tpFile)
    },
    imgClose () {
      this.imgVisible = false
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
      this.fach(this.character)
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
