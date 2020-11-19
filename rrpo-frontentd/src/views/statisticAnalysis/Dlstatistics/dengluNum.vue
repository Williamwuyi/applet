<template>
  <a-modal
    :visible="dengluNumVisible"
    :width= "600"
    title="组织机构登陆统计"
    @cancel="() => { onClose() }"
    :footer="null"
  >
    <div style="width: 100%;max-height: 700px;overflow: auto">
      <!-- 表格区域 -->
      <div>
        <a-table
          bordered
          :data-source="dataSource"
          :columns="columns"
          :loading="loading"
          :pagination="false"
        >
          <span slot="xuhao" slot-scope="text,record, index">{{index + 1}}</span>
        </a-table>
      </div>
    </div>
  </a-modal>
</template>
<script>
export default {
  name: 'dengluNum',
  props: ['dengluNumVisible'],
  data () {
    return {
      data: [],
      loading: false,
      advanced: false,
      sortedInfo: null,
      DlsModalVisiable: false,
      // 表格
      dataSource: [],
      variable: {},
      count: 2
    }
  },
  computed: {
    columns () {
      return [{
        title: '序号',
        scopedSlots: { customRender: 'xuhao' },
        align: 'center'
      }, {
        title: '登陆时间',
        dataIndex: 'date',
        align: 'center'
      }, {
        dataIndex: 'count',
        title: '登陆次数',
        align: 'center'
      }]
    }
  },
  methods: {
    // 渲染
    // 请求
    getFaech (deptId, year, month) {
      this.variable.year = year
      this.variable.deptId = deptId
      this.variable.month = month
      this.Srench()
    },
    Srench () {
      let pramase = {
        ...this.variable
      }
      this.loading = true
      this.$get('/accident/login/weekList', pramase).then(res => {
        this.dataSource = res.data.data
        this.loading = false
      })
    },
    onClose () {
      this.$emit('close')
      this.dataSource = []
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
