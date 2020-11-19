<template>
  <a-modal
    :visible="dengluVisible"
    :width= "500"
    @cancel="() => { onClose() }"
    :footer="null"
  >
    <div style="width: 100%;max-height: 700px;overflow: auto">
      <a-row>
        <a-col :span="12">
          <a-form-item
            label="登陆限定次数"
            :labelCol="{span: 12}"
            :wrapperCol="{span: 8, offset: 0}">
            <a-input-number type="text" v-model="variable.number"/>
          </a-form-item></a-col>
        <a-col :span="11" offset="1" style="margin-top: 5px"><a-button type="primary" @click="Srench">查询</a-button></a-col>
      </a-row>
      <!-- 表格区域 -->
      <div>
        <a-table
          bordered
          :data-source="dataSource"
          :columns="columns"
          :loading="loading"
          :pagination="false"
          :scroll="{ y: 550 }"
        >
          <span slot="xuhao" slot-scope="text,record, index">{{index + 1}}</span>
        </a-table>
      </div>
    </div>
  </a-modal>
</template>
<script>
export default {
  name: 'dengluModal',
  props: ['dengluVisible'],
  data () {
    return {
      data: [],
      loading: false,
      advanced: false,
      sortedInfo: null,
      DlsModalVisiable: false,
      // 表格
      dataSource: [],
      variable: {
        number: 4
      },
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
        title: '区县',
        dataIndex: 'deptName',
        align: 'center'
      }, {
        dataIndex: 'less',
        title: '未超出限定次数',
        align: 'center'
      }, {
        dataIndex: 'greater',
        title: '超出限定次数',
        align: 'center'
      }]
    }
  },
  methods: {
    // 渲染
    // 请求
    getFaech (index, deptId) {
      this.variable.year = index
      this.variable.DeptId = deptId
      this.Srench()
    },
    Srench () {
      let pramase = {
        ...this.variable
      }
      this.$get('/accident/login/yearLoginCount', pramase).then(res => {
        this.dataSource = res.data.data
      })
    },
    onClose () {
      this.$emit('close')
      this.dataSource = []
      this.variable = {number: 4}
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
