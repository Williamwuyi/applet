<template>
  <a-modal
    :visible="BModalVisiable"
    title="简报"
    :width= "800"
    :footer="null"
    style="height: 800px; overflow: auto"
    @cancel="() => { onClose() }"
  >
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
              <a-button type="primary" @click="fach">查询</a-button>
              <a-button style="margin-left: 8px" @click="reset">重置</a-button>
                <a-button style="margin-left: 8px; background-color: #2f54eb; color: #fff" @click="players">打印</a-button>
        </span>
      </div>
    </div>
    <div class="rewardForm">
      <h2>湖南铁路护路联防</h2>
      <h1>简报</h1>
      <p class="repNumber">
        第 <input type="text" class="periods"> 期
      </p>
      <div class="repcode clearboth">
        <p class="floatL">湖南省铁路护路联防工作办公室编</p>
        <p class="floatR"><input type="text" class="periods2">年<input type="text" class="periods3">月<input type="text" class="periods3">日</p>
      </div>
    </div>
    <editor height="400px" :readonly=false ref="editor" refType="9" ></editor>
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
    ></a-table>
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
      columns: [
        {
          title: '序号',
          dataIndex: 'number',
          align: 'center'
        },
        {
          title: '推荐单位',
          dataIndex: 'place',
          scopedSlots: { customRender: 'places' },
          align: 'center'
        },
        {
          title: '乡镇街',
          dataIndex: 'qwe',
          scopedSlots: { customRender: 'qwe' },
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
    // 时间查询
    onTimeChange () {},
    // 渲染
    fach () {
      this.loading = true
      this.$get('/prize/briefing', this.character).then(res => {
        let newData = res.data.data.rows
        let pagination = {}
        pagination.total = res.data.data.total
        this.dataSource = newData.rows
        this.pagination = pagination
        // 分页;
        this.dataSource = newData
        this.loading = false
      })
    },
    // 打印
    players () {},
    // 重置
    reset () {
      this.loading = false
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
  .rewardForm h2{font-size: 50px;padding-top: 10px}
  .rewardForm h1{font-size: 70px}
  .rewardForm .repNumber{text-align: center;margin-top: 30px}
  .rewardForm .repcode{width:688px;position: absolute;bottom: 0;left: 0}
  .rewardForm .periods{width: 20px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
  .rewardForm .periods2{width: 40px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
  .rewardForm .periods3{width: 20px;border: transparent;border-bottom: 1px dotted #000c17; text-align: center}
</style>
