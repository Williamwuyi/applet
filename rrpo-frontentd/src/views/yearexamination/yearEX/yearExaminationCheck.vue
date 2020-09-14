<template>
  <a-modal
    :visible="CheckVisible"
    :width= "1400"
    @ok="handleSubmit"
    :maskClosable="false"
    @cancel="() => { onClose() }"
    style="text-align: center;height: 800px; overflow: auto;"
  >
    <h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
      <table v-if="wachws.list[0].length > 0"  width="100%"  border="1" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px" v-for="(wachws, index) in arr" :key="wachws.name">
          <tr v-if="index === 0">
            <th style="width: 130px;">一级指标</th>
            <th>评估内容</th>
            <th style="width: 50px">佐证材料</th>
            <th style="width: 40px">考核得分</th>
            <th style="width: 200px">自评描述</th>
          </tr>
        <tr>
          <td :rowspan="wachws.list[0].length + 1" style="width: 130px;">{{wachws.name}}(60分)</td>
        </tr>
        <tr v-for="item in wachws.list[0]" :key="item.menusYear.menusYearId" v-if="item.menusYear">
          <td  style="text-align: left">{{item.menusYear.content}}</td>
          <td style="width: 50px">
            <a-button  @click="condProof(item)">提交</a-button>
          </td>
          <td style="width: 40px"><a-input-number :id="item.gradeId"  @blur="onChange(item.gradeId, $event)"/></td>
          <td style="width: 200px"><a-input @blur="onChangeMsg(item.gradeId, $event)"></a-input></td>
        </tr>
      </table>
<!--  佐证材料-->
    <year-condition-proof
    :proofVisibt="proofVisibt"
    @close = 'proofClose'
    @success="proofSuccess"
    ref="count"
    />
  </a-modal>
</template>

<script>
import store from '@/store'
import yearConditionProof from './yearExnditionProof'
export default {
  name: 'yearExaminationCheck',
  props: ['CheckVisible', 'deptId'],
  components: {yearConditionProof},
  data () {
    return {
      arr: [],
      performance: {},
      daptId: store.state.account.user.deptId,
      proofVisibt: false,
      yearId: ''
    }
  },
  methods: {
    // 重置
    rest () {
      // 清空表单
    },
    fach (yearId) {
      this.yearId = yearId
      this.$get('/check/grade/selectByUserIdOrDateYear', {yearId: yearId, deptId: this.daptId}).then(res => {
        console.log(res)
        if (res.status === 200) {
          this.arr = res.data.data
          console.log(this.arr)
        } else {
          this.$notification.warning({message: '系统提示', description: '没有绑定规则', duration: 4})
        }
      })
    },
    onClose () {
      this.rest()
      this.$emit('close')
    },
    // 自评分
    onChange (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 1
      pramase.number = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 自评描述
    onChangeMsg (id, event) {
      let pramase = {}
      pramase.type = 1
      pramase.gradeId = id
      pramase.message = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 佐证材料
    condProof (item) {
      this.proofVisibt = true
      item.yearId = this.yearId
      this.$refs.count.getyearId(item)
    },
    proofClose () {
      this.proofVisibt = false
    },
    proofSuccess () {
      this.proofVisibt = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 提交
    handleSubmit () {
      this.$get('/check/grade/commit', {deptId: this.deptId, yearId: this.yearId, status: 0}).then(res => {
        this.rest()
        this.$emit('success')
      })
    }
  }
}
</script>

<style scoped>
  .t1
  {
    clear: both;
    border: 1px solid #c9dae4;
  }
  .t1 tr th
  {
    color: #0d487b;
    background: #f2f4f8;
    line-height: 28px;
    border-bottom: 1px solid #9cb6cf;
    border-top: 1px solid #e9edf3;
    font-weight: normal;
    text-shadow: #e6ecf3 1px 1px 0px;
    padding-left: 5px;
    padding-right: 5px;
  }
  .t1 tr td
  {
    border-bottom: 1px solid #e9e9e9;
    padding-bottom: 5px;
    padding-top: 5px;
    border-top: 1px solid #FFFFFF;
    padding-left: 5px;
    padding-right: 5px;
    word-break: break-all;
  }
</style>
