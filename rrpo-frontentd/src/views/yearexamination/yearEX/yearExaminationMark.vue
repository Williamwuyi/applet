<template>
  <a-modal
    :visible="yearMarkVisible"
    :width= "1600"
    @cancel="() => { onClose() }"
    :footer="null"
    style="text-align: center; height: 800px; overflow: auto"
  >
    <a-form :form="form" v-if="arr">
    <h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
    <table  width="100%" v-if="wachws.list[0].length > 0"  border="1" v-for="(wachws, index) in arr"  :key="wachws.name" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px">
      <tr v-if="index === 0">
        <th style="width: 130px;">一级指标</th>
        <th>评估内容</th>
        <th style="width: 50px;">佐证材料</th>
        <th style="width: 40px;">自评得分</th>
        <th style="width: 250px;">自评描述</th>
        <th style="width: 40px;">终评得分</th>
        <th style="width: 250px;">终评描述</th>
        <th style="width: 70px;">状态</th>
        <th style="width: 80px;">复评得分</th>
        <th style="width: 300px;">复评描述</th>
      </tr>
      <tr>
        <td :rowspan="wachws.list[0].length + 1" style="width: 130px;">{{wachws.name}}(60分)</td>
      </tr>
      <tr v-for="item in wachws.list[0]"  :key="item.gradeId" v-if="item.menusYear">
        <td  style="text-align: left">{{item.menusYear.content}}</td>
        <td style="width: 50px;">
          <a-button  @click="condProof(item)">查看</a-button>
        </td>
        <td style="width: 40px;"><span>{{item.num}}</span></td>
        <td style="width: 250px;"><span>{{item.message}}</span></td>
        <td style="width: 40px;"><span>{{item.num2}}</span></td>
        <td style="width: 250px;"><span>{{item.message2}}</span></td>
        <td style="width: 70px;">
          <a-tag v-if="item.status === 0" color="#87d068" @click="YdClick(item, 1)">无异议</a-tag>
          <a-tag v-else-if="item.status === 1" color="#f50" @click="YdClick(item, 0)">有异议</a-tag>
        </td>
        <td style="width: 80px;">
          <a-input-number :id="item.menusYear.menusYearId"
                            v-decorator="[{rules: [{ required: true, message: '不能为空'}]}]"
                            :default-value="item.fpNum"  @blur="onChange(item.gradeId, $event)"/>
        </td>
        <td style="width: 300px;"><a-input :default-value="item.fpMessage" @blur="onChangeMsg(item.gradeId, $event)"></a-input></td>
      </tr>
    </table>
    <!--  佐证材料-->
    <year-exlook-proof
      :prooflookVisibt="prooflookVisibt"
      @close = 'proofClose'
      @success="proofSuccess"
      ref="looKclick"
    />
    </a-form>
    <div class="footer">
        <a-button @click="onClose">取消</a-button>
      <a-button @click="markTimeClick" style="background-color: #ED5239; color: #fff">返回</a-button>
        <a-button style="background-color: #00C853; color: #fff" @click="handleSubmit(3)">终审</a-button>
    </div>
<!-- 必须回复时间   -->
    <mark-time
    :markIsshow="markIsshow"
    @cancel="markcancel"
    @check="markCheck"
    ref="markT"
    />
  </a-modal>
</template>

<script>
import MarkTime from './MarkTime'
import yearExlookProof from './yearExlookProof'
export default {
  name: 'yearExaminationMark',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  components: {yearExlookProof, MarkTime},
  props: ['yearMarkVisible'],
  data () {
    return {
      arr: [],
      yearId: '',
      deptId: '',
      prames: {},
      markIsshow: false,
      prooflookVisibt: false,
      oneClick: true
    }
  },
  methods: {
    // 重置
    rest () {
      // 清空表单
    },
    fach (key) {
      this.numId = key.numId
      this.prames.deptId = key.dept.deptId
      this.prames.yearId = key.yearId
      this.deptId = key.dept.deptId
      this.yearId = key.yearId
      this.getFach()
    },
    // 渲染
    getFach () {
      this.$get('/check/grade/selectByUserIdOrDateYear', this.prames).then(res => {
        this.arr = res.data.data
        console.log(this.arr)
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
      this.getFach()
      this.$notification.success({message: '系统提示', description: '操作成功！', duration: 4})
    },
    YdClick (item, key) {
      console.log(item)
      // 疑点点击
      console.log(key)
      switch (key) {
        case 0:
          this.getYD({status: 0, gradeId: item.gradeId})
          break
        case 1:
          this.getYD({status: 1, gradeId: item.gradeId})
          break
      }
    },
    // 疑点标记
    getYD (value) {
      console.log(value)
      this.$post('/check/grade/checkStatus', value).then(res => {
        this.getFach()
      })
    },
    // 复评分
    onChange (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 3
      pramase.number = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 复评描述
    onChangeMsg (id, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 3
      pramase.message = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 必须回复时间
    markTimeClick () {
      this.markIsshow = true
      this.$refs.markT.getnumId(this.numId)
    },
    markCheck () {
      this.markIsshow = false
      this.handleSubmit(1)
    },
    markcancel () {
      this.markIsshow = false
    },
    // 提交
    handleSubmit (key) {
      this.$get('/check/grade/commit', {deptId: this.deptId, yearId: this.yearId, status: key}).then(res => {
        console.log(res)
        this.$emit('success')
        this.rest()
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
