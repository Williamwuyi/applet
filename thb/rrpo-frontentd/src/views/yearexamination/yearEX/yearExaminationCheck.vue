<template>
  <a-modal
    :visible="CheckVisible"
    width= "100%"
    @ok="handleSubmit"
    :dialogStyle="{top: 0}"
    :maskClosable="false"
    @cancel="() => { onClose() }"
  >
    <a-affix :offset-top="0">
      <fuchuang :numArr="numArr"/>
    </a-affix>
    <div style="text-align: center;max-height: calc(100vh - 35px); overflow: auto;" ref="container">
      <a-affix :offset-top="top" :target="() => this.$refs.container">
        <div style="background-color: white">
          <h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
          <hr />
        </div>
      </a-affix>
      <a-spin :spinning="spinning">
      <table v-if="wachws.list.length > 0"  width="100%"  border="1" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px" v-for="(wachws, index) in arr" :key="wachws.name">
          <tr v-if="index === 0">
            <th style="width: 130px;">一级指标</th>
            <th style="width: 70px;">序号</th>
            <th>评估内容</th>
            <th style="width: 70px;">加减分</th>
            <th style="width: 50px">佐证材料</th>
            <th style="width: 40px">考核得分</th>
            <th style="width: 200px">自评描述</th>
          </tr>
        <tr>
          <td :rowspan="wachws.list.length + 1" style="width: 130px;">{{wachws.name}}</td>
        </tr>
        <tr v-for="(item, num) in wachws.list" :key="item.menusYear.menusYearId" v-if="item.menusYear">
          <td style="width: 70px;">{{index + 1}}-{{num + 1}}</td>
          <td  style="text-align: left">{{item.menusYear.content}}</td>
          <td style="width: 70px;">
            <a-icon type="plus-square" title="加分项" v-if="item.menusYear.saveOrDelete === '加分项'" style="color: #52c41a"/>
            <a-icon type="minus-square" title="减分项" v-if="item.menusYear.saveOrDelete === '减分项'" style="color: red"/>
          </td>
          <td style="width: 50px">
            <a-button  @click="condProof(item)">提交</a-button>
          </td>
          <td style="width: 40px"><a-input-number :id="item.gradeId" v-model="item.num" @blur="onChange(item.gradeId, $event)"/></td>
          <td style="width: 200px"><a-textarea
            :autoSize=true
            @blur="onChangeMsg(item.gradeId, $event)"
            placeholder="请输入简要情况" allow-clear /></td>
<!--          <a-input @blur="onChangeMsg(item.gradeId, $event)"></a-input>-->
        </tr>
      </table>
      </a-spin>
<!--  佐证材料-->
    <year-condition-proof
    :proofVisibt="proofVisibt"
    @close = 'proofClose'
    @success="proofSuccess"
    ref="count"
    />
    </div>
  </a-modal>
</template>

<script>
import fuchuang from './fuchuang'
import store from '@/store'
import yearConditionProof from './yearExnditionProof'
export default {
  name: 'yearExaminationCheck',
  props: ['CheckVisible', 'deptId'],
  components: {yearConditionProof, fuchuang},
  data () {
    return {
      arr: [],
      numArr: {
        gzxg: 0,
        jcgzX: 0,
        jcgzY: 0
      },
      top: 0,
      bottom: 0,
      performance: {},
      daptId: store.state.account.user.deptId,
      proofVisibt: false,
      spinning: false,
      yearId: ''
    }
  },
  methods: {
    // 重置
    rest () {
      // 清空表单
      this.arr = []
      this.numArr.gzxg = 0
      this.numArr.jcgzY = 0
      this.numArr.jcgzX = 0
    },
    fach (yearId) {
      this.yearId = yearId
      this.spinning = true
      this.$get('/check/grade/selectByUserIdOrDateYear', {yearId: yearId, deptId: this.daptId}).then(res => {
        if (res.status === 200) {
          this.arr = res.data.data
          this.spinning = false
          this.statistical()
        } else {
          this.$notification.warning({message: '系统提示', description: '没有绑定规则', duration: 4})
        }
      })
    },
    onClose () {
      this.rest()
      this.$emit('close')
    },
    // 统计分数
    statistical () {
      this.arr.forEach(t => {
        // 判断当前有题
        if (t.list.length > 0) {
          // 判断基础工作
          if (t.id === '76b942fb606de4b0ba62e68cc4ecf69c') {
            t.list.forEach(i => {
              if (i.menusYear.saveOrDelete === '加分项') {
                this.numArr.jcgzX += i.num
              }
              if (i.menusYear.saveOrDelete === '减分项') {
                this.numArr.jcgzY += i.num
              }
            })
          }
          // 判断工作效果
          if (t.id === '93d0a833e9fb2e7cd7d6a6d82ec16b86') {
            t.list.forEach(i => {
              this.numArr.gzxg += i.num
            })
          }
        }
      })
    },
    // 自评分
    onChange (id, event) {
      this.numArr.gzxg = 0
      this.numArr.jcgzY = 0
      this.numArr.jcgzX = 0
      this.statistical()
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
