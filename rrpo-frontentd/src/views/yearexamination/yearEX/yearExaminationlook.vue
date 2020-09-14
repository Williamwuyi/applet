<template>
  <a-modal
    :visible="yearExamVisible"
    :width= "1600"
    @cancel="() => { onClose() }"
    :footer="null"
    style="text-align: center; height: 800px; overflow: auto"
  >
    <h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
    <table  width="100%"  v-for="(wachws, index) in arr"  :key="wachws.name"  border="1" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px">
      <tr v-if="index === 0">
        <th style="width: 130px;">一级指标</th>
        <th>评估内容</th>
        <th style="width: 50px;">佐证材料</th>
        <th style="width: 40px;">自评得分</th>
        <th style="width: 250px;">自评描述</th>
        <th style="width: 40px;">终评得分</th>
        <th style="width: 250px;">终评描述</th>
        <th style="width: 70px;">状态</th>
        <th style="width: 40px;">复评得分</th>
        <th style="width: 250px;">复评描述</th>
      </tr>
      <tr>
        <td style="width: 130px;" :rowspan="wachws.list[0].length + 1">{{wachws.name}}(60分)</td>
      </tr>
      <tr v-for="item in wachws.list[0]" :key="item.gradeId" v-if="item.menusYear">
        <td  style="text-align: left">{{item.menusYear.content}}</td>
        <td style="width: 50px;">
          <a-button  @click="condProof(item)">查看</a-button>
        </td>
        <td style="width: 40px;">{{item.num}}</td>
        <td style="width: 250px;">{{item.message}}</td>
        <td style="width: 40px;" ><a-input-number :id="item.gradeId" :disabled="InputShow" :default-value="item.num2"  @blur="onChange(item.gradeId, $event)"/></td>
        <td style="width: 250px;"><a-input :default-value="item.message2" :read-only="InputShow"  @blur="onChangeMsg(item.gradeId, $event)"></a-input></td>
        <td style="width: 70px;">
          <a-tag v-if="item.status === 0" color="#87d068">无异议</a-tag>
          <a-tag v-else-if="item.status === 1" color="#f50">有异议</a-tag>
        </td>
        <td style="width: 40px;"><span>{{item.fpNum}}</span></td>
        <td style="width: 250px;"><span>{{item.fpMessage}}</span></td>
      </tr>
    </table>
    <!--  佐证材料-->
    <year-exlook-proof
      :prooflookVisibt="prooflookVisibt"
      @close = 'proofClose'
      @success="proofSuccess"
      ref="looKclick"
    />
    <div class="footer">
      <a-button @click="onClose">取消</a-button>
      <a-button style="background-color: #00C853; color: #fff" @click="handleSubmit(2)" v-if="TJshow">重新提交</a-button>
    </div>
  </a-modal>
</template>

<script>
import yearExlookProof from './yearExlookProof'
export default {
  name: 'yearExaminationlook',
  components: {yearExlookProof},
  props: ['yearExamVisible'],
  data () {
    return {
      arr: [],
      yearId: '',
      deptId: '',
      TJshow: true,
      InputShow: false,
      prooflookVisibt: false
    }
  },
  methods: {
    fach (key) {
      let TimeStatus = new Date().getTime() > new Date(key.deadDate).getTime()
      // 当状态为终审时和已经重新提交,超过规定提交时间后 不可重新提交  输入框不可输入
      if (key.status === 3 || key.status === 2 || TimeStatus) {
        this.TJshow = false
        this.InputShow = true
      }
      let prames = {}
      prames.deptId = key.dept.deptId
      prames.yearId = key.yearId
      this.deptId = key.dept.deptId
      this.yearId = key.yearId
      this.$get('/check/grade/selectByUserIdOrDateYear', prames).then(res => {
        console.log(res.data.data)
        this.arr = res.data.data
      })
    },
    onClose () {
      this.$emit('close')
    },
    // 查看
    condProof (item) {
      this.prooflookVisibt = true
      item.yearId = this.yearId
      item.deptId = this.deptId
      this.$refs.looKclick.getyearDate(item)
    },
    proofClose () {
      this.prooflookVisibt = false
    },
    proofSuccess () {
      this.prooflookVisibt = false
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    // 重新自评分
    onChange (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 2
      pramase.number = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 新自评描述
    onChangeMsg (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 2
      pramase.message = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 提交
    handleSubmit (key) {
      this.$get('/check/grade/commit', {deptId: this.deptId, yearId: this.yearId, status: key}).then(res => {
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
    color: #444;
    border-top: 1px solid #FFFFFF;
    padding-left: 5px;
    padding-right: 5px;
    word-break: break-all;
  }
  #rcDialogTitle0 {
    font-size: 34px;
  }
  .footer {
    text-align: right;
    margin-top: 5px;
  }
</style>
