<template>
  <div style="width: 100%">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row >
            <a-col :md="7" :sm="24" >
              <a-form-item
                label="选择年月"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 20, offset: 0}">
                <a-month-picker @change="handleYearChange" :allowClear="false" v-model="Timer">
                </a-month-picker>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
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
      <a-button @click="returnDls" style="margin-bottom: 10px">返回上一级</a-button>
      <a-table
        bordered
        :data-source="dataSource"
        :columns="columns"
        :loading="loading"
        :rowKey='(record) => record.deptId'
        :pagination="false"
        :scroll="{ y: 550 }"
      >
        <span slot="xuhao" slot-scope="text,record, index">{{index + 1}}</span>
        <a  slot="number" slot-scope="text,record" @click="getNum(record)">{{text}}</a>
        <a slot="dizhoushi" slot-scope="text,record" style="color:#6290FF" @click="anClick(record.deptId)">{{ text }}</a>
        <template slot="operation" slot-scope="text, record">
          <a  style="margin-right: 3px"     @click="lookLogin(record.deptId)"  >查看本级人员登陆信息</a>
        </template>
        <template slot="footer" slot-scope="currentPageData">
          <a-row style="text-align: center">
            <a-col :md="6">登陆合计</a-col>
            <a-col :md="6"></a-col>
            <a-col :md="6">{{num}}</a-col>
            <a-col :md="6"></a-col>
          </a-row>
        </template>
      </a-table>
    </div>
<!--  登陆信息  -->
    <dls-modal
    :DlsModalVisiable="DlsModalVisiable"
    @onClose="DlsOnClose"
    ref="Dls"
    />
<!--  登陆次数  -->
    <denglu-num
      :dengluNumVisible="dengluNumVisible"
      @close="OnNumClose"
      ref="dengluNum"
    />
  </div>
</template>
<script>
import dengluNum from './dengluNum'
import DlsModal from './DlsModal'
export default {
  name: 'DlStatistics',
  components: {DlsModal, dengluNum},
  data () {
    return {
      character: {},
      data: [],
      Timer: undefined,
      dengluNumVisible: false,
      loading: false,
      advanced: false,
      sortedInfo: null,
      // 记录当前到哪一层
      numBer: 0,
      parentId: '',
      DlsModalVisiable: false,
      // 表格
      dataSource: [],
      count: 2,
      year: '',
      month: ''
    }
  },
  // 引用页面刷新方法
  inject: [ 'reload' ],
  mounted () {
    this.newDate()
    this.getFaech(this.character)
  },
  computed: {
    num () {
      let limit = 0
      if (this.dataSource.length > 0) {
        this.dataSource.forEach(t => {
          limit += t.number
        })
      }
      return limit
    },
    columns () {
      return [{
        title: '序号',
        scopedSlots: { customRender: 'xuhao' },
        align: 'center'
      }, {
        title: '地州市',
        dataIndex: 'deptName',
        scopedSlots: { customRender: 'dizhoushi' },
        align: 'center'
      }, {
        dataIndex: 'number',
        title: '登陆次数',
        scopedSlots: { customRender: 'number' },
        align: 'center'
      }, {
        title: '操作',
        dataIndex: 'operation',
        align: 'center',
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  methods: {
    search () {
      this.getFaech({
        ...this.character
      })
    },
    reset () {
      // 清空时间选择
      // 清空年月
      this.Timer = undefined
      this.character = {}
      this.newDate()
      this.getFaech(this.character)
    },
    // 当前系统时间
    newDate () {
      var date = new Date()
      this.year = date.toLocaleDateString().split('/')[0]
      this.month = date.toLocaleDateString().split('/')[1]
      this.Timer = this.year + '-' + this.month
      this.character.deptParentId = 0
      this.character.year = this.year
      this.character.month = this.month
    },
    getNum (record) {
      this.dengluNumVisible = true
      this.$refs.dengluNum.getFaech(record.deptId, this.year, this.month)
    },
    OnNumClose () {
      this.dengluNumVisible = false
    },
    // 时间查询
    handleYearChange (date, dateString) {
      if (date !== null) {
        let newData = {
          year: dateString.split('-')[0],
          month: dateString.split('-')[1]
        }
        this.character.year = this.year = newData.year
        this.character.month = this.month = newData.month
      } else {
        this.arr = []
        this.msgweek = undefined
      }
    },
    // 点击表格
    anClick (key) {
      this.numBer = key
      this.character.deptParentId = key
      this.getFaech(this.character)
    },
    // 点击返回
    returnDls () {
      this.numBer = this.parentId
      this.character.deptParentId = this.parentId
      this.getFaech(this.character)
    },
    // 渲染
    // 请求
    getFaech (index) {
      let parems = {
        ...index
      }
      this.$get('/accident/login/list', parems).then(res => {
        if (res.data.status !== 0) {
          this.parentId = res.data.prentId
          let newArr = res.data.data
          this.dataSource = newArr
        } else {
          this.$notification.warning({message: '系统提示', description: '已经到底了', duration: 4})
        }
      })
    },
    // 查看
    lookLogin (key) {
      this.DlsModalVisiable = true
      this.$refs.Dls.getById(key)
    },
    DlsOnClose () {
      this.DlsModalVisiable = false
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
</style>
