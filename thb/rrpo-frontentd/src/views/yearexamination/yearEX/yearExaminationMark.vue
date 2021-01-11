<template>
  <a-modal
    :visible="yearMarkVisible"
    width= "100%"
    :dialogStyle="{top: 0}"
    @cancel="() => { onClose() }"
    :footer="null"
  >
    <a-affix :offset-top="0">
      <fuchuang :numArr="numArr"/>
    </a-affix>
    <div style="text-align: center; max-height: calc(100vh - 35px);overflow: auto" ref="container">
    <a-form :form="form" v-if="arr">
      <a-affix :offset-top="top" :target="() => this.$refs.container">
    <div style="background-color: white; position: relative"><h2 style="text-align: center; font-size: 31px; line-height: 31px">年度考核详细信息</h2>
      <a-button class="editable-add-btn" style="position: absolute; right: 110px;top: 0"  @click="dengluClick">
        登陆统计
      </a-button>
      <a-button class="editable-add-btn" style="position: absolute; right: 10px;top: 0"  @click="MonthClick">
        优秀月评
      </a-button>
      <hr />
    </div>
      </a-affix>
      <a-spin :spinning="spinning">
    <table  width="100%" v-if="wachws.list.length > 0"  border="1" v-for="(wachws, index) in arr"  :key="wachws.name" class="t1" align="center" cellpadding="0" cellspacing="0" style="font-size: 14px">
      <tr v-if="index === 0">
        <th style="width: 130px;">一级指标</th>
        <th style="width: 70px;">序号</th>
        <th>评估内容</th>
        <th style="width: 70px;">加减分</th>
        <th style="width: 50px;">佐证材料</th>
        <th style="width: 40px;">自评得分</th>
        <th style="width: 250px;">自评描述</th>
        <th style="width: 40px;">复评得分</th>
        <th style="width: 250px;">复评描述</th>
        <th style="width: 70px;">状态</th>
        <th style="width: 80px;">终评得分</th>
        <th style="width: 300px;">终评描述</th>
      </tr>
      <tr>
        <td :rowspan="wachws.list.length + 1" style="width: 130px;">{{wachws.name}}</td>
      </tr>
      <tr v-for="(item, num) in wachws.list"  :key="item.gradeId" v-if="item.menusYear">
        <td style="width: 70px;">{{index + 1}}-{{num+1}}</td>
        <td  style="text-align: left">{{item.menusYear.content}}</td>
        <td style="width: 70px;">
          <a-icon type="plus-square" v-if="item.menusYear.saveOrDelete === '加分项'" style="color: #52c41a"/>
          <a-icon type="minus-square" v-if="item.menusYear.saveOrDelete === '减分项'" style="color: red"/>
        </td>
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
                            v-model="item.fpNum"
                            :default-value="item.fpNum"  @blur="onChange(item.gradeId, $event)"/>
        </td>
        <td style="width: 300px;"><a-textarea
          :autoSize=true
          :default-value="item.fpMessage" @blur="onChangeMsg(item.gradeId, item.fpNum, $event)"
          placeholder="请输入简要情况" allow-clear /></td>
<!--        <a-input :default-value="item.fpMessage" @blur="onChangeMsg(item.gradeId, item.fpNum, $event)" />-->
      </tr>
    </table>
      </a-spin>
    <!--  佐证材料-->
    <year-exlook-proof
      :prooflookVisibt="prooflookVisibt"
      @close = 'proofClose'
      @success="proofSuccess"
      ref="looKclick"
    />
    </a-form>
      <a-affix :offset-bottom="bottom" :target="() => this.$refs.container">
    <div class="footer" style="background-color: white">
      <hr />
        <a-button @click="onClose">取消</a-button>
      <a-button @click="markTimeClick" style="background-color: #ED5239; color: #fff">返回</a-button>
        <a-button style="background-color: #00C853; color: #fff" @click="handleSubmit(3)">终审</a-button>
    </div>
      </a-affix>
      <!-- 必须回复时间   -->
    <mark-time
    :markIsshow="markIsshow"
    @cancel="markcancel"
    @check="markCheck"
    ref="markT"
    />
<!--  月评    -->
      <month-modal
      :monthVisible="monthVisible"
      @close="monthClose"
      ref="month"
      />
      <!--  登陆    -->
      <denglu-modal
        :dengluVisible="dengluVisible"
        @close="dengluClose"
        ref="denglu"
      />
    </div>
  </a-modal>
</template>

<script>
import fuchuang from './fuchuang'
import dengluModal from './dengluModal'
import monthModal from './monthModal'
import MarkTime from './MarkTime'
import yearExlookProof from './yearExlookProof'
export default {
  name: 'yearExaminationMark',
  beforeCreate () {
    this.form = this.$form.createForm(this, { name: 'form_in_modal' })
  },
  components: {yearExlookProof, MarkTime, monthModal, dengluModal, fuchuang},
  props: ['yearMarkVisible'],
  data () {
    return {
      top: 0,
      bottom: 0,
      visible: false,
      monthVisible: false,
      dengluVisible: false,
      spinning: false,
      arr: [],
      numArr: {
        gzxg: 0,
        jcgzX: 0,
        jcgzY: 0
      },
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
      this.numArr = {
        gzxg: 0,
        jcgzX: 0,
        jcgzY: 0
      }
    },
    fach (key) {
      // key.yearDate * 1
      this.indexR = key.yearDate * 1
      this.numId = key.numId
      this.prames.deptId = key.dept.deptId
      this.prames.yearId = key.yearId
      this.deptId = key.dept.deptId
      this.yearId = key.yearId
      this.getFach()
    },
    // 渲染
    getFach () {
      this.spinning = true
      this.$get('/check/grade/selectByUserIdOrDateYear', this.prames).then(res => {
        this.arr = res.data.data
        this.spinning = false
        this.statistical()
      })
    },
    onClose () {
      this.$emit('close')
    },
    // 描述开启
    // 描述取消
    msgClose () {
      this.visible = false
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
      // 疑点点击
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
      this.$post('/check/grade/checkStatus', value).then(res => {
        this.getFach()
      })
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
                this.numArr.jcgzX += i.fpNum
              }
              if (i.menusYear.saveOrDelete === '减分项') {
                this.numArr.jcgzY += i.fpNum
              }
            })
          }
          // 判断工作效果
          if (t.id === '93d0a833e9fb2e7cd7d6a6d82ec16b86') {
            t.list.forEach(i => {
              this.numArr.gzxg += i.fpNum
            })
          }
        }
      })
    },
    // 复评分
    onChange (id, event) {
      this.numArr.gzxg = 0
      this.numArr.jcgzY = 0
      this.numArr.jcgzX = 0
      this.statistical()
      let pramase = {}
      pramase.gradeId = id
      pramase.type = 3
      pramase.number = event.target.value
      this.$post('/check/grade/addGrade', pramase).then(res => {
      })
    },
    // 复评描述
    onChangeMsg (id, number, event) {
      let pramase = {}
      pramase.gradeId = id
      pramase.number = number
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
        this.$emit('success')
        this.rest()
      })
    },
    // 月评
    MonthClick () {
      this.monthVisible = true
      this.$refs.month.getFaech(this.indexR)
    },
    monthClose () {
      this.monthVisible = false
    },
    // 登陆统计
    dengluClick () {
      this.dengluVisible = true
      this.$refs.denglu.getFaech(this.indexR, this.deptId)
    },
    dengluClose () {
      this.dengluVisible = false
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
