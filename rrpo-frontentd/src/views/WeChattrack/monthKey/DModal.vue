<template>
  <a-modal
    :visible="DModalVisiable"
    title='预览'
    :width= "850"
    :footer="null"
    @cancel="() => { onClose() }"
    loading
  >
    <a-row>
      <a-col :md="12" :sm="24" >
        <a-form-item
          label="月份查询"
          :labelCol="{span: 5}"
          :wrapperCol="{span: 18, offset: 1}">
          <a-date-picker
            format = 'YYYY-MM' v-model="timeValue" @change="onTimeChange" />
        </a-form-item>
        </a-col>
      <a-col :md="12" :sm="24" >
              <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                <a-button style="margin-left: 8px; background-color: #2f54eb; color: #fff" @click="players">打印</a-button>
        </span>
      </a-col>
    </a-row>
    <hr />
<!--  表格区域  -->
    <a-table
      :data-source="dataSource"
      :columns="columns"
      :loading="loading"
      :pagination="false"
      :rowKey="record => {record.wxMonthId}"
    >
    </a-table>
  </a-modal>
</template>
<script>
export default {
  name: 'DModal',
  props: ['DModalVisiable'],
  data () {
    return {
      timeValue: '',
      arr: [],
      loading: false,
      dataSource: []
    }
  },
  computed: {
    columns () {
      return [{
        title: '序号',
        dataIndex: 'deptJc1'
      }, {
        title: '推荐单位',
        dataIndex: 'deptJc2'
      }, {
        title: '乡镇街',
        dataIndex: 'deptJc'
      }, {
        title: '微信记录简要内容(详情内容登陆’hnzhb.com.cn-信息公开——月度最佳微信记录‘)',
        width: '80%',
        dataIndex: 'wxName'
      }, {
        title: '初步意见',
        dataIndex: 'date'
      }]
    }
  },
  methods: {
    onClose () {
      this.$emit('onClose')
    },
    search () {},
    reset () {},
    // 打印
    players () {},
    getJd (key) {
      console.log(key)
      this.jiedao = key.deptJc.deptName
      this.$get('/wx/sh/list', {qunid: key.wxId}).then(res => {
        let newArr = res.data.data.records
        this.arr = newArr
        this.num = newArr.length
        this.state = this.arr[this.num - 1].status
      })
    },
    // 选择时间
    onTimeChange (date, dateTime) {
      console.log(dateTime)
    }
  }
}
</script>

<style scoped>
</style>
