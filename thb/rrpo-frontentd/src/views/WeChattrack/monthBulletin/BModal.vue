<template>
  <a-modal
    :visible="BModalVisiable"
    title="简报"
    :width= "800"
    :footer="null"
    :zIndex="100"
    @cancel="() => { onClose() }"
  >
    <div style="height: 700px; overflow: auto; overflow-x: hidden">
    <div style="height: 50px">
      <a-form-item
        style="float: left"
        label="月份选择"
        :labelCol="{span: 7}"
        :wrapperCol="{span: 17, offset: 0}">
        <a-date-picker
          format = 'YYYY-MM' v-model="timeValue" @change="onTimeChange" />
      </a-form-item>
      <div class="floatR" style="width:220px">
        <span style="margin-top: 3px;">
              <a-button type="primary" @click="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                <a-button style="margin-left: 8px; background-color: #2f54eb; color: #fff" @click="players">导出</a-button>
        </span>
      </div>
    </div>
    <div class="rewardForm">
      <h2>湖南铁路护路联防</h2>
      <h1>简报</h1>
      <p class="repNumber">
        第 <input type="text" class="periods" v-model="number"> 期
      </p>
      <div class="repcode clearboth">
        <p class="floatL">湖南省铁路护路联防工作办公室编</p>
<!--        <p class="floatR"><input type="text" class="periods2">年<input type="text" class="periods3">月<input type="text" class="periods3">日</p>-->
      </div>
    </div>
    <editor height="400px" :readonly=false ref="editor" refType="10" ></editor>
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :scroll="{ y: 450 }"
      @change="handleTableChange"
    >
      <template slot="content" slot-scope="text, record">
        <div v-html="text"></div>
      </template>
    </a-table>
    </div>
  </a-modal>
</template>
<script>
import Editor from '@/components/editor/Editor' // 导入编辑器组件
export default {
  name: 'BModal',
  components: {Editor},
  data () {
    return {
      loading: false,
      timeValue: undefined,
      number: null,
      month: '',
      columns: [
        {
          title: '序号',
          dataIndex: 'serial',
          align: 'center'
        },
        {
          title: '推荐单位',
          dataIndex: 'szDeptName',
          align: 'center'
        },
        {
          title: '乡镇街',
          dataIndex: 'deptJc.deptName',
          align: 'center'
        },
        {
          title: '微信记录简要内容(详情内容登陆’hnzhb.com.cn-信息公开——月度最佳微信记录‘)',
          dataIndex: 'content',
          width: '80%',
          scopedSlots: { customRender: 'content' }
        }
      ],
      dataSource: [],
      character: {},
      //  分页
      pagination: {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    }
  },
  props: {
    BModalVisiable: {
      default: false
    }
  },
  methods: {
    // 选择时间
    onTimeChange (date, dateTime) {
      this.month = dateTime
    },
    // 纯文本
    ToText (HTML) {
      var input = HTML
      return input.replace(/<(style|script|iframe)[^>]*?>[\s\S]+?<\/\1\s*>/gi, '').replace(/<[^>]+?>/g, '').replace(/\s+/g, ' ').replace(/ /g, ' ').replace(/>/g, ' ')
    },
    // 查询
    search () {
      if (this.timeValue !== undefined) {
        this.fach()
      } else {
        this.$message.error('请先选择月份')
      }
    },
    // 渲染
    fach (parmse = {pageNum: 1, pageSize: 10, month: this.month, status: 3}) {
      this.loading = true
      this.$get('/wx/month/list', parmse).then(res => {
        let newData = res.data.data
        // 分页;
        const pagination = { ...this.pagination }
        pagination.total = newData.total
        this.dataSource = newData.records
        this.pagination = pagination
        this.loading = false
      })
    },
    // 打印
    players () {
      if (this.timeValue !== undefined && this.number !== null) {
        let newadd = {}
        newadd.content = this.ToText(this.$refs.editor.content)
        newadd.number = this.number
        newadd.month = this.month
        this.$DocExport('/wx/month/downDocxGood', newadd).then(res => {
        })
      } else {
        this.$message.error('月份或期数不能为空')
      }
    },
    // 重置
    reset () {
      this.number = null
      this.$refs.editor.content = ''
      this.loading = false
      this.timeValue = undefined
      this.character = {}
      this.dataSource = []
      this.pagination = {
        total: 0,
        pageSize: 10, // 每页中显示10条数据
        showTotal: total => `共有 ${total} 条数据` // 分页中显示总的数据
      }
    },
    // 关闭
    onClose () {
      this.reset()
      this.$emit('close')
    },
    // 分页
    handleTableChange (pagination) {
      // 通知界面
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      // 通知后台
      this.character.pageNum = pagination.current
      this.character.pageSize = pagination.pageSize
      this.fach({
        ...this.character,
        month: this.month
      })
    }
  }
}
</script>
<style scoped>
  .floatL{float: left}
  .floatR{float: right}
  .clearboth .floatL, clearboth .floatR{display: block;content: '';clear: both}
  .rewardForm{width:750px;height: 320px;margin: 0 auto;border-bottom:2px solid red;position: relative;}
  .rewardForm h2,.rewardForm h1{color: red;text-align: center;margin: 0}
  .rewardForm h2{font-size: 50px;padding-top: 10px;font-weight: 300;}
  .rewardForm h1{font-size: 70px}
  .rewardForm .repNumber{text-align: center;margin-top: 30px}
  .rewardForm .repcode{width:688px;position: absolute;bottom: 0;left: 0}
  .rewardForm .periods{width: 20px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
  .rewardForm .periods2{width: 40px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
  .rewardForm .periods3{width: 20px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
</style>
